package org.queryall.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
    private static final boolean _TRACE = RdfFetchController.log.isTraceEnabled();
    private static final boolean _DEBUG = RdfFetchController.log.isDebugEnabled();
    private static final boolean _INFO = RdfFetchController.log.isInfoEnabled();
    
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    private volatile Collection<RdfFetcherQueryRunnable> errorResults = new HashSet<RdfFetcherQueryRunnable>(10);
    
    private volatile Collection<RdfFetcherQueryRunnable> successfulResults = new HashSet<RdfFetcherQueryRunnable>(10);
    private volatile Collection<RdfFetcherQueryRunnable> uncalledThreads = new HashSet<RdfFetcherQueryRunnable>(4);
    private volatile Collection<RdfFetcherQueryRunnable> fetchThreadGroup = new HashSet<RdfFetcherQueryRunnable>(20);
    
    private volatile Collection<QueryBundle> queryBundles = new ArrayList<QueryBundle>();
    
    private Map<String, String> queryParameters;
    private List<Profile> sortedIncludedProfiles;
    private boolean useDefaultProviders = true;
    private String realHostName;
    private int pageOffset;
    private boolean includeNonPagedQueries = true;
    private QueryAllConfiguration localSettings;
    private BlacklistController localBlacklistController;
    private boolean namespaceNotRecognised = false;
    
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
     * @param nextUseDefaultProviders
     * @param nextRealHostName
     * @param nextPageOffset
     * @throws QueryAllException
     */
    public RdfFetchController(final QueryAllConfiguration settingsClass,
            final BlacklistController localBlacklistController, final Map<String, String> nextQueryParameters,
            final List<Profile> nextIncludedSortedProfiles, final boolean nextUseDefaultProviders,
            final String nextRealHostName, final int nextPageOffset) throws QueryAllException
    {
        this.setSettings(settingsClass);
        this.setBlacklistController(localBlacklistController);
        this.queryParameters = nextQueryParameters;
        this.sortedIncludedProfiles = nextIncludedSortedProfiles;
        this.useDefaultProviders = nextUseDefaultProviders;
        this.realHostName = nextRealHostName;
        
        if(nextPageOffset < 1)
        {
            RdfFetchController.log
                    .warn("RdfFetchController.initialise: correcting pageoffset to 1, previous pageOffset="
                            + nextPageOffset);
            
            this.pageOffset = 1;
        }
        else
        {
            this.pageOffset = nextPageOffset;
        }
        
        this.includeNonPagedQueries = (this.pageOffset == 1);
        
        this.initialise(true);
    }
    
    public boolean anyNamespaceNotRecognised()
    {
        return this.namespaceNotRecognised;
    }
    
    public void fetchRdfForQueries() throws InterruptedException, UnnormalisableRuleException, QueryAllException
    {
        this.fetchRdfForQueriesWithoutNormalisation(this.getFetchThreadGroup());
        
        for(final RdfFetcherQueryRunnable nextThread : this.getFetchThreadGroup())
        {
            if(nextThread.getCompleted())
            {
                if(!nextThread.getWasSuccessful())
                {
                    if(nextThread.getLastException() != null)
                    {
                        RdfFetchController.log.error("RdfFetchController.fetchRdfForQueries: endpoint="
                                + nextThread.getEndpointUrl() + " message="
                                + nextThread.getLastException().getMessage());
                        
                        URI queryKey = null;
                        
                        if(nextThread.getOriginalQueryBundle() != null
                                && nextThread.getOriginalQueryBundle().getQueryType() != null)
                        {
                            queryKey = nextThread.getOriginalQueryBundle().getQueryType().getKey();
                        }
                        
                        nextThread.setResultDebugString("FAILURE: endpoint=" + nextThread.getEndpointUrl()
                                + " querykey=" + queryKey + " query=" + nextThread.getQuery() + " message="
                                + nextThread.getLastException().getMessage());
                        
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
                    
                    if(RdfFetchController._DEBUG)
                    {
                        RdfFetchController.log.debug("RdfFetchController.fetchRdfForQueries: Query successful query="
                                + nextThread.getOriginalQueryBundle().getQueryType().getKey());
                        
                        if(RdfFetchController._TRACE)
                        {
                            RdfFetchController.log
                                    .trace("RdfFetchController.fetchRdfForQueries: Query successful nextResult="
                                            + nextResult + " convertedResult=" + convertedResult);
                        }
                    }
                    
                    URI queryKey = null;
                    
                    if(nextThread.getOriginalQueryBundle() != null
                            && nextThread.getOriginalQueryBundle().getQueryType() != null)
                    {
                        queryKey = nextThread.getOriginalQueryBundle().getQueryType().getKey();
                    }
                    
                    // TODO: expand to include details of the actual endpoint
                    nextThread.setResultDebugString("SUCCESS: queryKey=" + queryKey);
                    // nextThread.setResultDebugString("SUCCESS: endpoint="
                    // + nextThread.getOriginalQueryBundle().getQueryEndpoint() + " queryKey=" +
                    // queryKey);
                    // + " query=" + nextThread.getOriginalQueryBundle().getQuery());
                    
                    this.getSuccessfulResults().add(nextThread);
                }
            }
            else
            {
                // this.uncalledThreads.add(nextThread);
                RdfFetchController.log.error("Thread wasn't completed after fetchRdfForQueries completed endpointUrl="
                        + nextThread.getEndpointUrl());
            }
        }
    }
    
    public void fetchRdfForQueriesWithoutNormalisation(final Collection<RdfFetcherQueryRunnable> fetchThreads)
        throws InterruptedException
    {
        final long start = System.currentTimeMillis();
        
        final List<Future<String>> futures = this.executor.invokeAll(fetchThreads, 30, TimeUnit.SECONDS);
        
        // This loop is a safety check, although it doesn't actually fallover if something is wrong
        // it will happen if the executor returns before the thread is completed
        for(final RdfFetcherQueryRunnable nextThread : fetchThreads)
        {
            if(!nextThread.getCompleted())
            {
                RdfFetchController.log
                        .error("RdfFetchController.fetchRdfForQueries: Thread not completed properly name="
                                + nextThread.getName());
            }
        }
        
        for(final Future<String> nextFuture : futures)
        {
            if(nextFuture.isCancelled())
            {
                RdfFetchController.log
                        .error("RdfFetchController.fetchRdfForQueries: Future was cancelled, thread not completed properly");
            }
        }
        
        if(RdfFetchController._INFO)
        {
            final long end = System.currentTimeMillis();
            
            RdfFetchController.log.info(String.format("%s: timing=%10d", "RdfFetchController.fetchRdfForQueries",
                    (end - start)));
        }
    }
    
    private Collection<RdfFetcherQueryRunnable> generateFetchThreadsFromQueryBundles(
            final Collection<QueryBundle> nextQueryBundles, final int pageoffsetIndividualQueryLimit)
    {
        final Collection<RdfFetcherQueryRunnable> results =
                new ArrayList<RdfFetcherQueryRunnable>(nextQueryBundles.size());
        
        for(final QueryBundle nextBundle : nextQueryBundles)
        {
            if(RdfFetchController._DEBUG)
            {
                RdfFetchController.log
                        .debug("RdfFetchController.generateFetchThreadsFromQueryBundles: About to create a thread for query on "
                                // + "endpoint="
                                // + nextEndpoint
                                // + " query="
                                // + nextQuery
                                + " provider=" + nextBundle.getOriginalProvider().getKey());
            }
            
            RdfFetcherQueryRunnable nextThread = null;
            
            boolean addToFetchQueue = false;
            
            if(nextBundle.getOriginalProvider() == null)
            {
                RdfFetchController.log
                        .error("nextBundle.getOriginalProvider() was null. not generating fetch thread for this query bundle");
            }
            // TODO: Make this section extensible, preferably defined by the provider itself
            else if(nextBundle.getOriginalProvider() instanceof HttpSparqlProvider
                    && nextBundle.getOriginalProvider().getEndpointMethod()
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
                            + nextBundle.getOriginalProvider().getKey());
                    continue;
                }
                
                // nextBundle.getQueryEndpoint();
                final String nextQuery = nextAlternativeEndpointsAndQueries.get(nextEndpoint);
                // nextBundle.getQuery();
                
                if(nextQuery == null)
                {
                    RdfFetchController.log.error("nextQuery was retrieved as null nextBundle.getOriginalProvider()="
                            + nextBundle.getOriginalProvider().getKey());
                    continue;
                }
                
                nextThread =
                        new RdfFetcherSparqlQueryRunnable(nextEndpoint,
                                ((SparqlProvider)nextBundle.getOriginalProvider()).getSparqlGraphUri(), nextQuery,
                                "off", ((HttpProvider)nextBundle.getOriginalProvider()).getAcceptHeaderString(this
                                        .getSettings().getStringProperty(WebappConfig.DEFAULT_ACCEPT_HEADER)),
                                pageoffsetIndividualQueryLimit, this.getSettings(), this.getBlacklistController(),
                                nextBundle);
                
                addToFetchQueue = true;
                
                if(RdfFetchController._TRACE)
                {
                    RdfFetchController.log
                            .trace("RdfFetchController.generateFetchThreadsFromQueryBundles: created HTTP POST SPARQL query thread on nextEndpoint="
                                    + nextEndpoint + " provider.getKey()=" + nextBundle.getOriginalProvider().getKey());
                }
            }
            else if(nextBundle.getOriginalProvider() instanceof HttpProvider
                    && nextBundle.getOriginalProvider().getEndpointMethod()
                            .equals(HttpProviderSchema.getProviderHttpGetUrl()))
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
                            + nextBundle.getOriginalProvider().getKey());
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
                        new RdfFetcherUriQueryRunnable(nextEndpoint, nextQuery, "off",
                                ((HttpProvider)nextBundle.getOriginalProvider()).getAcceptHeaderString(this
                                        .getSettings().getStringProperty(WebappConfig.DEFAULT_ACCEPT_HEADER)),
                                this.getSettings(), this.getBlacklistController(), nextBundle);
                
                addToFetchQueue = true;
                
                if(RdfFetchController._TRACE)
                {
                    RdfFetchController.log
                            .trace("RdfFetchController.generateFetchThreadsFromQueryBundles: created HTTP GET query thread on nextEndpoint="
                                    + nextEndpoint + " provider.getKey()=" + nextBundle.getOriginalProvider().getKey());
                }
            }
            else if(nextBundle.getOriginalProvider() instanceof NoCommunicationProvider
                    && nextBundle.getOriginalProvider().getEndpointMethod()
                            .equals(ProviderSchema.getProviderNoCommunication()))
            {
                if(RdfFetchController._TRACE)
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
                                + nextBundle.getOriginalProvider().getEndpointMethod().stringValue()
                                + " providerConfig=" + nextBundle.getOriginalProvider().getKey().stringValue());
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
                
                if(RdfFetchController._DEBUG)
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
            if(nextQueryBundle.getOriginalProvider() != null)
            {
                results.add(nextQueryBundle.getOriginalProvider());
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
     * @return the fetchThreadGroup
     */
    public Collection<RdfFetcherQueryRunnable> getFetchThreadGroup()
    {
        return this.fetchThreadGroup;
    }
    
    public Collection<QueryBundle> getQueryBundles()
    {
        return this.queryBundles;
    }
    
    public Collection<RdfFetcherQueryRunnable> getResults()
    {
        final Collection<RdfFetcherQueryRunnable> results = new HashSet<RdfFetcherQueryRunnable>();
        
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
            
            if(RdfFetchController._DEBUG)
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
                if(!this.includeNonPagedQueries && !nextQueryType.getIsPageable())
                {
                    if(RdfFetchController._INFO)
                    {
                        RdfFetchController.log
                                .info("RdfFetchController: not using query as it is not pageable nonPagedQuery="
                                        + nextQueryType.getKey());
                    }
                    
                    continue;
                }
                
                // Non-paged queries are a special case. The caller decides whether
                // they want to use non-paged queries, for example, they may say no
                // if they have decided that they need only extra results from paged
                // queries
                if(!(nextQueryType instanceof InputQueryType))
                {
                    if(RdfFetchController._INFO)
                    {
                        RdfFetchController.log
                                .info("RdfFetchController: not using query as it is not an InputQueryType"
                                        + nextQueryType.getKey());
                    }
                    
                    continue;
                }
                
                final InputQueryType nextInputQueryType = (InputQueryType)nextQueryType;
                
                final Collection<Provider> chosenProviders = new HashSet<Provider>();
                
                chosenProviders.addAll(ProviderUtils.getProvidersForQuery(this.getSettings().getAllProviders(),
                        this.sortedIncludedProfiles, nextInputQueryType, this.getSettings()
                                .getNamespacePrefixesToUris(), this.queryParameters, this.getSettings()
                                .getBooleanProperty(WebappConfig.RECOGNISE_IMPLICIT_PROVIDER_INCLUSIONS), this
                                .getSettings().getBooleanProperty(WebappConfig.INCLUDE_NON_PROFILE_MATCHED_PROVIDERS),
                        this.useDefaultProviders));
                
                final Collection<QueryBundle> queryBundlesForQueryType =
                        QueryBundleUtils
                                .generateQueryBundlesForQueryTypeAndProviders(
                                        nextInputQueryType,
                                        chosenProviders,
                                        this.queryParameters,
                                        allCustomQueries.get(nextQueryType),
                                        this.sortedIncludedProfiles,
                                        this.getSettings(),
                                        this.getBlacklistController(),
                                        this.realHostName,
                                        this.getSettings().getBooleanProperty(
                                                WebappConfig.TRY_ALL_ENDPOINTS_FOR_EACH_PROVIDER), this.pageOffset);
                
                this.queryBundles.addAll(queryBundlesForQueryType);
                
                // if there are still no query bundles check for the non-namespace specific version
                // of the query type to flag any instances of the namespace not being recognised
                if(queryBundlesForQueryType.size() == 0)
                {
                    // For namespace specific query types, do a check ignoring the namespace
                    // conditions to determine whether namespace matching was the only reason that
                    // the query type did not match this provider
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
            } // end for(QueryType nextQueryType : allCustomQueries)
            
            if(RdfFetchController._INFO)
            {
                if(this.getQueryBundles().size() == 0)
                {
                    RdfFetchController.log.info("RdfFetchController.initialise: no query bundles given or created");
                }
                
                if(RdfFetchController._DEBUG)
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
        }
        
        // replace the fetch thread group if we were instructed to generate the query bundles or the
        // fetch thread group was empty
        if(generateQueryBundles || this.fetchThreadGroup.size() == 0)
        {
            this.setFetchThreadGroup(this.generateFetchThreadsFromQueryBundles(this.getQueryBundles(), this
                    .getSettings().getIntProperty(WebappConfig.PAGEOFFSET_INDIVIDUAL_QUERY_LIMIT)));
        }
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
