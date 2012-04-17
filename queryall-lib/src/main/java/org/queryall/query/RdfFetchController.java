package org.queryall.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.openrdf.model.URI;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.HttpProvider;
import org.queryall.api.provider.HttpProviderSchema;
import org.queryall.api.provider.HttpSparqlProvider;
import org.queryall.api.provider.NoCommunicationProvider;
import org.queryall.api.provider.Provider;
import org.queryall.api.provider.ProviderSchema;
import org.queryall.api.provider.SparqlProvider;
import org.queryall.api.provider.SparqlProviderSchema;
import org.queryall.api.querytype.InputQueryType;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.utils.SortOrder;
import org.queryall.api.utils.WebappConfig;
import org.queryall.blacklist.BlacklistController;
import org.queryall.exception.QueryAllException;
import org.queryall.exception.UnnormalisableRuleException;
import org.queryall.utils.ListUtils;
import org.queryall.utils.ProviderUtils;
import org.queryall.utils.QueryBundleUtils;
import org.queryall.utils.QueryTypeUtils;
import org.queryall.utils.RuleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RdfFetchController
{
    private static final Logger log = LoggerFactory.getLogger(RdfFetchController.class);
    private static final boolean TRACE = RdfFetchController.log.isTraceEnabled();
    private static final boolean DEBUG = RdfFetchController.log.isDebugEnabled();
    private static final boolean INFO = RdfFetchController.log.isInfoEnabled();
    
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    private volatile Collection<RdfFetcherQueryRunnable> fetchThreadGroup = new ArrayList<RdfFetcherQueryRunnable>(20);
    
    private volatile Collection<RdfFetcherQueryRunnable> errorResults = new ArrayList<RdfFetcherQueryRunnable>(10);
    private volatile Collection<RdfFetcherQueryRunnable> successfulResults = new ArrayList<RdfFetcherQueryRunnable>(10);
    private volatile Collection<RdfFetcherQueryRunnable> uncalledThreads = new ArrayList<RdfFetcherQueryRunnable>(4);
    
    private volatile Collection<QueryBundle> queryBundles = new ArrayList<QueryBundle>();
    
    private volatile boolean namespaceNotRecognised = false;
    
    private Map<String, String> queryParameters;
    private List<Profile> sortedIncludedProfiles;
    private String realHostName;
    private int pageOffset;
    private QueryAllConfiguration localSettings;
    private BlacklistController localBlacklistController;
    
    /**
     * Default constructor. In this case, the fetch threads must be set directly before attempting
     * to call fetchRdfForQueries.
     */
    public RdfFetchController()
    {
        
    }
    
    /**
     * Sets the controller up using a collection of predefined query bundles, along with the
     * settings and blacklist controllers
     * 
     * @param settingsClass
     * @param localBlacklistController
     * @param nextQueryBundles
     * @throws QueryAllException
     */
    public RdfFetchController(final QueryAllConfiguration settingsClass,
            final BlacklistController localBlacklistController, final Collection<QueryBundle> nextQueryBundles)
        throws QueryAllException
    {
        this.setSettings(settingsClass);
        this.setBlacklistController(localBlacklistController);
        this.setQueryBundles(nextQueryBundles);
    }
    
    /**
     * Sets the controller up using a map containing query parameters, along with the other
     * necessary information for interpreting the context of the query parameters, including the
     * settings class, a blacklist controller, and some request specific parameters such as the
     * profiles to use, whether to use default providers at all, and the pageoffset and host name
     * for the query.
     * 
     * @param settingsClass
     * @param localBlacklistController
     * @param nextQueryParameters
     * @param nextIncludedSortedProfiles
     * @param nextRealHostName
     * @param nextPageOffset
     * @throws QueryAllException
     */
    public RdfFetchController(final QueryAllConfiguration settingsClass,
            final BlacklistController localBlacklistController, final Map<String, String> nextQueryParameters,
            final List<Profile> nextIncludedSortedProfiles, final String nextRealHostName, final int nextPageOffset)
        throws QueryAllException
    {
        this.setSettings(settingsClass);
        this.setBlacklistController(localBlacklistController);
        this.queryParameters = nextQueryParameters;
        this.sortedIncludedProfiles = nextIncludedSortedProfiles;
        this.setRealHostName(nextRealHostName);
        this.setPageOffset(nextPageOffset);
        
        this.initialise(true);
    }
    
    public boolean anyNamespaceNotRecognised()
    {
        return this.namespaceNotRecognised;
    }
    
    /**
     * Fetches RDF for the currently configured fetch thread groups, including normalisation after
     * the fact.
     * 
     * @throws InterruptedException
     * @throws UnnormalisableRuleException
     * @throws QueryAllException
     */
    public void fetchRdfForQueries() throws InterruptedException, UnnormalisableRuleException, QueryAllException
    {
        this.fetchRdfForQueriesWithoutNormalisation(this.getFetchThreadGroup());
        
        for(final RdfFetcherQueryRunnable nextThread : this.getFetchThreadGroup())
        {
            if(nextThread.getCompleted())
            {
                URI queryKey = null;
                
                if(nextThread.getOriginalQueryBundle() != null
                        && nextThread.getOriginalQueryBundle().getQueryType() != null)
                {
                    queryKey = nextThread.getOriginalQueryBundle().getQueryType().getKey();
                }
                
                if(!nextThread.getWasSuccessful())
                {
                    if(nextThread.getLastException() != null)
                    {
                        RdfFetchController.log.error("RdfFetchController.fetchRdfForQueries: originalendpoint="
                                + nextThread.getOriginalEndpointUrl() + " actualendpoint="
                                + nextThread.getActualEndpointUrl() + " message="
                                + nextThread.getLastException().getMessage());
                        
                        nextThread.setResultDebugString("FAILURE: originalendpoint="
                                + nextThread.getOriginalEndpointUrl() + " actualendpoint="
                                + nextThread.getActualEndpointUrl() + " querykey=" + queryKey + " query="
                                + nextThread.getOriginalQuery() + " message="
                                + nextThread.getLastException().getMessage());
                        
                    }
                    else
                    {
                        RdfFetchController.log.error("Found unknown error for originalendpoint="
                                + nextThread.getOriginalEndpointUrl() + " actualendpoint="
                                + nextThread.getActualEndpointUrl());
                        
                        nextThread.setResultDebugString("FAILURE: originalendpoint="
                                + nextThread.getOriginalEndpointUrl() + " actualendpoint="
                                + nextThread.getActualEndpointUrl() + " querykey=" + queryKey + " query="
                                + nextThread.getOriginalQuery() + " (no message)");
                    }
                    
                    this.errorResults.add(nextThread);
                }
                else
                {
                    final String nextResult = nextThread.getRawResult();
                    
                    final String convertedResult =
                            (String)RuleUtils.normaliseByStage(
                                    NormalisationRuleSchema.getRdfruleStageBeforeResultsImport(),
                                    nextResult,
                                    RuleUtils.getSortedRulesByUris(this.getSettings().getAllNormalisationRules(),
                                            nextThread.getOriginalQueryBundle().getProvider().getNormalisationUris(),
                                            SortOrder.HIGHEST_ORDER_FIRST),
                                    this.sortedIncludedProfiles,
                                    this.getSettings().getBooleanProperty(
                                            WebappConfig.RECOGNISE_IMPLICIT_RDFRULE_INCLUSIONS), this.getSettings()
                                            .getBooleanProperty(WebappConfig.INCLUDE_NON_PROFILE_MATCHED_RDFRULES));
                    
                    nextThread.setNormalisedResult(convertedResult);
                    
                    nextThread.setResultDebugString("SUCCESS: originalendpoint=" + nextThread.getOriginalEndpointUrl()
                            + " actualendpoint=" + nextThread.getActualEndpointUrl() + " querykey=" + queryKey
                            + " query=" + nextThread.getOriginalQuery());
                    
                    if(RdfFetchController.TRACE)
                    {
                        RdfFetchController.log
                                .debug("RdfFetchController.fetchRdfForQueries: Query successful nextThread.getOriginalEndpointUrl()={} query={}",
                                        nextThread.getOriginalEndpointUrl(), nextThread.getOriginalQueryBundle()
                                                .getQueryType().getKey());
                        RdfFetchController.log
                                .trace("RdfFetchController.fetchRdfForQueries: Query successful nextResult={} convertedResult={}",
                                        nextResult, convertedResult);
                    }
                    else if(RdfFetchController.DEBUG)
                    {
                        RdfFetchController.log
                                .debug("RdfFetchController.fetchRdfForQueries: Query successful nextThread.getOriginalEndpointUrl()={} query={}",
                                        nextThread.getOriginalEndpointUrl(), nextThread.getOriginalQueryBundle()
                                                .getQueryType().getKey());
                    }
                    
                    this.getSuccessfulResults().add(nextThread);
                }
            }
            else
            {
                // this.uncalledThreads.add(nextThread);
                RdfFetchController.log.error("Thread wasn't completed after fetchRdfForQueries completed endpointUrl="
                        + nextThread.getOriginalEndpointUrl());
            }
        }
    }
    
    public void fetchRdfForQueriesWithoutNormalisation(final Collection<RdfFetcherQueryRunnable> fetchThreads)
        throws InterruptedException
    {
        final long start = System.currentTimeMillis();
        
        final CountDownLatch isDone = new CountDownLatch(fetchThreads.size());
        
        for(final RdfFetcherQueryRunnable nextThread : fetchThreads)
        {
            nextThread.setCountDownLatch(isDone);
            this.executor.submit(nextThread);
        }
        
        try
        {
            isDone.await();
            this.executor.shutdown();
            for(int count = 0; !this.executor.isTerminated() && count < 30; count++)
            {
                if(RdfFetchController.DEBUG)
                {
                    RdfFetchController.log.debug("awaiting executor termination count={}", count);
                }
                
                this.executor.awaitTermination(1, TimeUnit.SECONDS);
            }
            
            if(!this.executor.isTerminated())
            {
                RdfFetchController.log
                        .error("Fetch executor was not terminated, attempting to force shutdown with shutdownNow");
                this.executor.shutdownNow();
                RdfFetchController.log.error("Force shutdownNow returned");
            }
        }
        catch(final InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
        finally
        {
            if(RdfFetchController.INFO)
            {
                final long end = System.currentTimeMillis();
                
                RdfFetchController.log.info(String.format("%s: timing=%10d", "RdfFetchController.fetchRdfForQueries",
                        (end - start)));
            }
        }
    }
    
    private Collection<RdfFetcherQueryRunnable> generateFetchThreadsFromQueryBundles(
            final Collection<QueryBundle> nextQueryBundles, final int pageoffsetIndividualQueryLimit)
    {
        final Collection<RdfFetcherQueryRunnable> results =
                new ArrayList<RdfFetcherQueryRunnable>(nextQueryBundles.size());
        
        for(final QueryBundle nextBundle : nextQueryBundles)
        {
            if(RdfFetchController.DEBUG)
            {
                RdfFetchController.log
                        .debug("RdfFetchController.generateFetchThreadsFromQueryBundles: About to create a thread for query on "
                                // + "endpoint="
                                // + nextEndpoint
                                // + " query="
                                // + nextQuery
                                + " provider=" + nextBundle.getProvider().getKey());
            }
            
            RdfFetcherQueryRunnableImpl nextThread = null;
            
            boolean addToFetchQueue = false;
            
            if(nextBundle.getProvider() == null)
            {
                RdfFetchController.log
                        .error("nextBundle.getOriginalProvider() was null. not generating fetch thread for this query bundle");
            }
            // TODO: Make this section extensible, preferably defined by the provider itself
            else if(nextBundle.getProvider() instanceof HttpSparqlProvider
                    && nextBundle.getProvider().getEndpointMethod()
                            .equals(SparqlProviderSchema.getProviderHttpPostSparql()))
            {
                // randomly choose one of the alternatives, the others will be resolved if necessary
                // automagically
                final Map<String, String> nextAlternativeEndpointsAndQueries =
                        nextBundle.getAlternativeEndpointsAndQueries();
                final String nextEndpoint =
                        ListUtils.chooseRandomItemFromCollection(nextAlternativeEndpointsAndQueries.keySet());
                
                if(nextEndpoint == null)
                {
                    RdfFetchController.log.error("nextEndpoint was retrieved as null nextBundle.getOriginalProvider()="
                            + nextBundle.getProvider().getKey());
                    continue;
                }
                
                // nextBundle.getQueryEndpoint();
                final String nextQuery = nextAlternativeEndpointsAndQueries.get(nextEndpoint);
                // nextBundle.getQuery();
                
                if(nextQuery == null)
                {
                    RdfFetchController.log.error("nextQuery was retrieved as null nextBundle.getOriginalProvider()="
                            + nextBundle.getProvider().getKey());
                    continue;
                }
                
                nextThread =
                        new RdfFetcherSparqlQueryRunnableImpl(nextEndpoint,
                                ((SparqlProvider)nextBundle.getProvider()).getSparqlGraphUri(), nextQuery, "off",
                                ((HttpProvider)nextBundle.getProvider()).getAcceptHeaderString(this.getSettings()
                                        .getStringProperty(WebappConfig.DEFAULT_ACCEPT_HEADER)),
                                pageoffsetIndividualQueryLimit, this.getSettings(), this.getBlacklistController(),
                                nextBundle);
                
                addToFetchQueue = true;
                
                if(RdfFetchController.TRACE)
                {
                    RdfFetchController.log
                            .trace("RdfFetchController.generateFetchThreadsFromQueryBundles: created HTTP POST SPARQL query thread on nextEndpoint="
                                    + nextEndpoint + " provider.getKey()=" + nextBundle.getProvider().getKey());
                }
            }
            else if(nextBundle.getProvider() instanceof HttpProvider
                    && nextBundle.getProvider().getEndpointMethod().equals(HttpProviderSchema.getProviderHttpGetUrl()))
            {
                // randomly choose one of the alternatives, the others will be resolved if necessary
                // automagically
                final Map<String, String> nextAlternativeEndpointsAndQueries =
                        nextBundle.getAlternativeEndpointsAndQueries();
                final String nextEndpoint =
                        ListUtils.chooseRandomItemFromCollection(nextAlternativeEndpointsAndQueries.keySet());
                
                if(nextEndpoint == null)
                {
                    RdfFetchController.log.error("nextEndpoint was retrieved as null nextBundle.getOriginalProvider()="
                            + nextBundle.getProvider().getKey());
                    continue;
                }
                
                // nextBundle.getQueryEndpoint();
                final String nextQuery = nextAlternativeEndpointsAndQueries.get(nextEndpoint);
                // nextBundle.getQuery();
                
                if(nextQuery == null)
                {
                    RdfFetchController.log.warn("nextQuery was retrieved as null");
                }
                
                nextThread =
                        new RdfFetcherUriQueryRunnableImpl(nextEndpoint, nextQuery, "off",
                                ((HttpProvider)nextBundle.getProvider()).getAcceptHeaderString(this.getSettings()
                                        .getStringProperty(WebappConfig.DEFAULT_ACCEPT_HEADER)), this.getSettings(),
                                this.getBlacklistController(), nextBundle);
                
                addToFetchQueue = true;
                
                if(RdfFetchController.TRACE)
                {
                    RdfFetchController.log
                            .trace("RdfFetchController.generateFetchThreadsFromQueryBundles: created HTTP GET query thread on nextEndpoint="
                                    + nextEndpoint + " provider.getKey()=" + nextBundle.getProvider().getKey());
                }
            }
            else if(nextBundle.getProvider() instanceof NoCommunicationProvider
                    && nextBundle.getProvider().getEndpointMethod().equals(ProviderSchema.getProviderNoCommunication()))
            {
                if(RdfFetchController.TRACE)
                {
                    RdfFetchController.log
                            .trace("RdfFetchController.generateFetchThreadsFromQueryBundles: not including no communication provider in fetch queue or creating thread");
                }
                
                addToFetchQueue = false;
            }
            else
            {
                addToFetchQueue = false;
                
                RdfFetchController.log
                        .warn("RdfFetchController.generateFetchThreadsFromQueryBundles: endpointMethod did not match any known values. Not adding endpointMethod="
                                + nextBundle.getProvider().getEndpointMethod().stringValue()
                                + " providerConfig="
                                + nextBundle.getProvider().getKey().stringValue());
            }
            
            if(addToFetchQueue)
            {
                results.add(nextThread);
            }
            else
            {
                // if( nextThread != null )
                // {
                // getUncalledThreads().add( nextThread );
                // }
                
                if(RdfFetchController.DEBUG)
                {
                    RdfFetchController.log
                            .debug("RdfFetchController.generateFetchThreadsFromQueryBundles: not adding bundle/provider to the fetch group for some reason");
                }
            }
        }
        
        return results;
    }
    
    public Collection<Provider> getAllUsedProviders()
    {
        final Collection<Provider> results = new ArrayList<Provider>(this.getQueryBundles().size());
        
        for(final QueryBundle nextQueryBundle : this.getQueryBundles())
        {
            if(nextQueryBundle.getProvider() != null)
            {
                results.add(nextQueryBundle.getProvider());
            }
        }
        
        return results;
    }
    
    public BlacklistController getBlacklistController()
    {
        return this.localBlacklistController;
    }
    
    /**
     * @return the errorResults
     */
    public Collection<RdfFetcherQueryRunnable> getErrorResults()
    {
        return this.errorResults;
    }
    
    /**
     * Helper method to determine whether this fetch controller would currently exclude non-paged
     * queries. The result is based directly on the current boolean result of the expression
     * (getPageOffset() > 1).
     * 
     * @return the excludeNonPagedQueries
     */
    public boolean getExcludeNonPagedQueries()
    {
        return this.getPageOffset() > 1;
    }
    
    /**
     * @return the fetchThreadGroup
     */
    public Collection<RdfFetcherQueryRunnable> getFetchThreadGroup()
    {
        return this.fetchThreadGroup;
    }
    
    /**
     * @return the pageOffset
     */
    public int getPageOffset()
    {
        return this.pageOffset;
    }
    
    public Collection<QueryBundle> getQueryBundles()
    {
        return this.queryBundles;
    }
    
    /**
     * @return the realHostName
     */
    public String getRealHostName()
    {
        return this.realHostName;
    }
    
    public Collection<RdfFetcherQueryRunnable> getResults()
    {
        final Collection<RdfFetcherQueryRunnable> results = new ArrayList<RdfFetcherQueryRunnable>();
        
        results.addAll(this.getSuccessfulResults());
        results.addAll(this.getErrorResults());
        
        return results;
    }
    
    public QueryAllConfiguration getSettings()
    {
        return this.localSettings;
    }
    
    /**
     * @return the successfulResults
     */
    public Collection<RdfFetcherQueryRunnable> getSuccessfulResults()
    {
        return this.successfulResults;
    }
    
    /**
     * @return the uncalledThreads
     */
    public Collection<RdfFetcherQueryRunnable> getUncalledThreads()
    {
        return this.uncalledThreads;
    }
    
    private synchronized void initialise(final boolean generateQueryBundles) throws QueryAllException
    {
        final long start = System.currentTimeMillis();
        
        if(generateQueryBundles)
        {
            // overwrite any query bundles that may have been inserted previously as we were told to
            // generate new query bundles
            this.queryBundles = new ArrayList<QueryBundle>(20);
            
            // Note: this set contains queries that matched without taking into account the
            // namespaces assigned to each query type
            // The calculation of the namespace matching is done later
            final Map<QueryType, Map<String, Collection<NamespaceEntry>>> allCustomQueries =
                    QueryTypeUtils.getQueryTypesMatchingQuery(this.queryParameters, this.sortedIncludedProfiles, this
                            .getSettings().getAllQueryTypes(), this.getSettings().getNamespacePrefixesToUris(), this
                            .getSettings().getAllNamespaceEntries(),
                            this.getSettings().getBooleanProperty(WebappConfig.RECOGNISE_IMPLICIT_QUERY_INCLUSIONS),
                            this.getSettings().getBooleanProperty(WebappConfig.INCLUDE_NON_PROFILE_MATCHED_QUERIES));
            
            if(RdfFetchController.DEBUG)
            {
                RdfFetchController.log.debug("RdfFetchController.initialise: found " + allCustomQueries.size()
                        + " matching queries");
            }
            
            for(final QueryType nextQueryType : allCustomQueries.keySet())
            {
                // Non-paged queries are a special case. The caller decides whether
                // they want to use non-paged queries, for example, they may say no
                // if they have decided that they need only extra results from paged
                // queries
                if(this.getExcludeNonPagedQueries() && !nextQueryType.getIsPageable())
                {
                    if(RdfFetchController.INFO)
                    {
                        RdfFetchController.log
                                .info("RdfFetchController: not using query as it is not pageable nonPagedQuery="
                                        + nextQueryType.getKey());
                    }
                }
                else if(!(nextQueryType instanceof InputQueryType))
                {
                    if(RdfFetchController.INFO)
                    {
                        RdfFetchController.log
                                .info("RdfFetchController: not using query as it is not an InputQueryType"
                                        + nextQueryType.getKey());
                    }
                }
                else
                {
                    final InputQueryType nextInputQueryType = (InputQueryType)nextQueryType;
                    
                    final Collection<Provider> chosenProviders =
                            ProviderUtils.getProvidersForQuery(nextInputQueryType, this.queryParameters,
                                    this.sortedIncludedProfiles, this.getSettings());
                    
                    final Collection<QueryBundle> queryBundlesForQueryType =
                            QueryBundleUtils
                                    .generateQueryBundlesForQueryTypeAndProviders(
                                            nextInputQueryType,
                                            chosenProviders,
                                            this.queryParameters,
                                            allCustomQueries.get(nextQueryType),
                                            this.sortedIncludedProfiles,
                                            this.localSettings.getAllQueryTypes(),
                                            this.getSettings(),
                                            this.getBlacklistController(),
                                            this.getRealHostName(),
                                            this.getSettings().getBooleanProperty(
                                                    WebappConfig.TRY_ALL_ENDPOINTS_FOR_EACH_PROVIDER),
                                            this.getPageOffset(),
                                            this.localSettings
                                                    .getBooleanProperty(WebappConfig.CONVERT_ALTERNATE_NAMESPACE_PREFIXES_TO_PREFERRED),
                                            this.localSettings
                                                    .getBooleanProperty(WebappConfig.RECOGNISE_IMPLICIT_RDFRULE_INCLUSIONS),
                                            this.localSettings
                                                    .getBooleanProperty(WebappConfig.INCLUDE_NON_PROFILE_MATCHED_RDFRULES));
                    
                    this.queryBundles.addAll(queryBundlesForQueryType);
                    
                    // if there are still no query bundles check for the non-namespace specific
                    // version
                    // of the query type to flag any instances of the namespace not being recognised
                    if(queryBundlesForQueryType.size() == 0)
                    {
                        // For namespace specific query types, do a check ignoring the namespace
                        // conditions to determine whether namespace matching was the only reason
                        // that
                        // the query type did not match this provider
                        
                        // NOTE: We only do this expensive, generally optional, check if there were
                        // no previous unrecognised namespaces
                        if(!this.namespaceNotRecognised)
                        {
                            if(nextQueryType.getIsNamespaceSpecific()
                                    && ProviderUtils.getProvidersForQueryNonNamespaceSpecific(
                                            this.getSettings().getAllProviders(),
                                            nextInputQueryType,
                                            this.sortedIncludedProfiles,
                                            this.getSettings().getBooleanProperty(
                                                    WebappConfig.RECOGNISE_IMPLICIT_PROVIDER_INCLUSIONS),
                                            this.getSettings().getBooleanProperty(
                                                    WebappConfig.INCLUDE_NON_PROFILE_MATCHED_PROVIDERS)).size() > 0)
                            {
                                this.namespaceNotRecognised = true;
                            }
                        }
                    }
                }
            } // end for(QueryType nextQueryType : allCustomQueries)
        }
        
        // replace the fetch thread group if we were instructed to generate the query bundles or the
        // fetch thread group was empty
        if(generateQueryBundles || this.fetchThreadGroup.size() == 0)
        {
            this.setFetchThreadGroup(this.generateFetchThreadsFromQueryBundles(this.getQueryBundles(), this
                    .getSettings().getIntProperty(WebappConfig.PAGEOFFSET_INDIVIDUAL_QUERY_LIMIT)));
        }
        
        // ---------------------------------
        // LOGGING
        if(RdfFetchController.INFO)
        {
            if(this.getQueryBundles().size() == 0)
            {
                RdfFetchController.log.info("RdfFetchController.initialise: no query bundles given or created");
            }
            
            if(RdfFetchController.DEBUG)
            {
                if(this.getQueryBundles().size() > 0)
                {
                    for(final QueryBundle nextQueryBundleDebug : this.getQueryBundles())
                    {
                        RdfFetchController.log.debug("RdfFetchController.initialise: nextQueryBundleDebug="
                                + nextQueryBundleDebug.toString());
                    }
                }
                
                final long end = System.currentTimeMillis();
                
                RdfFetchController.log.debug("RdfFetchController.initialise: numberOfThreads="
                        + this.getFetchThreadGroup().size());
                
                RdfFetchController.log.debug(String.format("%s: timing=%10d", "RdfFetchController.initialise",
                        (end - start)));
            }
        }
        // ---------------------------------
    }
    
    public boolean queryKnown()
    {
        if(this.getQueryBundles() == null || this.getQueryBundles().size() == 0)
        {
            return false;
        }
        
        for(final QueryBundle nextQueryBundle : this.getQueryBundles())
        {
            // if the query type for any query bundle is not a dummy query, return true
            if(nextQueryBundle.getQueryType() != null && !nextQueryBundle.getQueryType().getIsDummyQueryType())
            {
                return true;
            }
        }
        
        return false;
    }
    
    public void setBlacklistController(final BlacklistController localBlacklistController)
    {
        this.localBlacklistController = localBlacklistController;
    }
    
    /**
     * @param errorResults
     *            the errorResults to set
     */
    public void setErrorResults(final Collection<RdfFetcherQueryRunnable> errorResults)
    {
        this.errorResults = errorResults;
    }
    
    /**
     * @param fetchThreadGroup
     *            the fetchThreadGroup to set
     */
    public void setFetchThreadGroup(final Collection<RdfFetcherQueryRunnable> fetchThreadGroup)
    {
        this.fetchThreadGroup = fetchThreadGroup;
    }
    
    public void setPageOffset(final int nextPageOffset)
    {
        if(nextPageOffset < 1)
        {
            RdfFetchController.log
                    .warn("RdfFetchController.setPageOffset: correcting pageoffset to 1, previous pageOffset="
                            + nextPageOffset);
            
            this.pageOffset = 1;
        }
        else
        {
            this.pageOffset = nextPageOffset;
        }
    }
    
    /**
     * Sets the query bundles and initialises the controllers internal settings using these new
     * query bundles
     * 
     * @param queryBundles
     *            The query bundles to use to set
     * @throws QueryAllException
     *             If the query bundles were not valid in any way, or could not be used to
     *             successfully generate fetch threads
     */
    public void setQueryBundles(final Collection<QueryBundle> queryBundles) throws QueryAllException
    {
        this.queryBundles = queryBundles;
        this.initialise(false);
    }
    
    /**
     * @param realHostName
     *            the realHostName to set
     */
    public void setRealHostName(final String realHostName)
    {
        this.realHostName = realHostName;
    }
    
    public void setSettings(final QueryAllConfiguration localSettings)
    {
        this.localSettings = localSettings;
    }
    
    /**
     * @param successfulResults
     *            the successfulResults to set
     */
    public void setSuccessfulResults(final Collection<RdfFetcherQueryRunnable> successfulResults)
    {
        this.successfulResults = successfulResults;
    }
    
    /**
     * @param uncalledThreads
     *            the uncalledThreads to set
     */
    public void setUncalledThreads(final Collection<RdfFetcherQueryRunnable> uncalledThreads)
    {
        this.uncalledThreads = uncalledThreads;
    }
}
