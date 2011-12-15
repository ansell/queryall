package org.queryall.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
import org.queryall.api.querytype.OutputQueryType;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.SortOrder;
import org.queryall.api.utils.WebappConfig;
import org.queryall.blacklist.BlacklistController;
import org.queryall.exception.QueryAllException;
import org.queryall.exception.UnnormalisableRuleException;
import org.queryall.utils.ListUtils;
import org.queryall.utils.ProviderUtils;
import org.queryall.utils.QueryTypeUtils;
import org.queryall.utils.RuleUtils;
import org.queryall.utils.StringUtils;
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
    
    private volatile Collection<QueryBundle> queryBundles = new LinkedList<QueryBundle>();
    
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
        
        this.initialise(false);
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
        this.fetchRdfForQueries(this.getFetchThreadGroup());
        
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
                            (String)RuleUtils.normaliseByStage(NormalisationRuleSchema
                                    .getRdfruleStageBeforeResultsImport(), nextResult, RuleUtils.getSortedRulesByUris(
                                    this.getSettings().getAllNormalisationRules(), nextThread.getOriginalQueryBundle()
                                            .getProvider().getNormalisationUris(), SortOrder.HIGHEST_ORDER_FIRST),
                                    this.sortedIncludedProfiles, this.getSettings()
                                            .getBooleanProperty(WebappConfig.RECOGNISE_IMPLICIT_RDFRULE_INCLUSIONS),
                                    this.getSettings()
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
    
    public void fetchRdfForQueries(final Collection<RdfFetcherQueryRunnable> fetchThreads) throws InterruptedException
    {
        final long start = System.currentTimeMillis();
        
        // TODO: FIXME: Should be using this to recover from errors if possible when there is an
        // alternative endpoint available
        final Collection<RdfFetcherQueryRunnable> temporaryEndpointBlacklist = new HashSet<RdfFetcherQueryRunnable>();
        
        final List<Future<String>> futures = this.executor.invokeAll(fetchThreads, 30, TimeUnit.SECONDS);
        
        // for(final RdfFetcherQueryRunnable nextThread : fetchThreads)
        // {
        // if(RdfFetchController._DEBUG)
        // {
        // RdfFetchController.log.debug("RdfFetchController.fetchRdfForQueries: about to start thread name="
        // + nextThread.getName());
        // }
        // executor.in
        // futures.add(executor.submit(nextThread));
        // //nextThread.start();
        // }
        
        // if(RdfFetchController._DEBUG)
        // {
        // RdfFetchController.log
        // .debug("RdfFetchController.fetchRdfForQueries: about to sleep to let other threads do some work");
        // }
        
        // do some very minor waiting to let the other threads start to do some work
        // try
        // {
        // Thread.sleep(5);
        // }
        // catch(final InterruptedException ie)
        // {
        // RdfFetchController.log.error("RdfFetchController.fetchRdfForQueries: Thread interruption occurred");
        // throw ie;
        // }
        
        // for(Future<?> nextFuture : futures)
        // {
        // try
        // {
        // nextFuture.get(30000, TimeUnit.MILLISECONDS);
        // }
        // catch(ExecutionException e)
        // {
        // RdfFetchController.log.error("RdfFetchController.fetchRdfForQueries: Thread execution failed due to an exception");
        // }
        // catch(TimeoutException e)
        // {
        // RdfFetchController.log.error("RdfFetchController.fetchRdfForQueries: Thread execution timed out");
        // }
        // }
        
        // for(final RdfFetcherQueryRunnable nextThread : fetchThreads)
        // {
        // try
        // {
        // // effectively attempt to join each of the threads, this loop will complete when
        // // they are all completed
        // nextThread.join();
        // }
        // catch(final InterruptedException ie)
        // {
        // RdfFetchController.log
        // .error("RdfFetchController.fetchRdfForQueries: caught interrupted exception message="
        // + ie.getMessage());
        // throw ie;
        // }
        // }
        
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
        final Collection<RdfFetcherQueryRunnable> results = new LinkedList<RdfFetcherQueryRunnable>();
        
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
            
            // TODO: Make this section extensible, preferably defined by the provider itself
            if(nextBundle.getOriginalProvider() instanceof HttpSparqlProvider
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
                                "off",
                                ((HttpProvider)nextBundle.getOriginalProvider())
                                        .getAcceptHeaderString(this.getSettings()
                                                .getStringProperty(WebappConfig.DEFAULT_ACCEPT_HEADER)),
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
                                ((HttpProvider)nextBundle.getOriginalProvider())
                                        .getAcceptHeaderString(this.getSettings()
                                                .getStringProperty(WebappConfig.DEFAULT_ACCEPT_HEADER)),
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
    
    /**
     * @param nextQueryType
     * @param chosenProviders
     * @throws QueryAllException
     */
    private Collection<QueryBundle> generateQueryBundlesForQueryTypeAndProviders(
            final QueryAllConfiguration localSettings, final QueryType nextQueryType,
            final Map<String, Collection<NamespaceEntry>> namespaceInputVariables,
            final Collection<Provider> chosenProviders, final boolean useAllEndpointsForEachProvider)
        throws QueryAllException
    {
        final Collection<QueryBundle> results = new HashSet<QueryBundle>();
        
        // Note: We default to converting alternate namespaces to preferred unless it is turned off
        // in the configuration. It can always be turned off for each namespace entry individually
        // FIXME: The current processing code ignores the preferences given by namespace entries, and just uses this setting
        final boolean overallConvertAlternateToPreferredPrefix =
                localSettings.getBooleanProperty(WebappConfig.CONVERT_ALTERNATE_NAMESPACE_PREFIXES_TO_PREFERRED);
        
        if(RdfFetchController._DEBUG)
        {
            RdfFetchController.log
                    .debug("RdfFetchController.generateQueryBundlesForQueryTypeAndProviders: nextQueryType="
                            + nextQueryType.getKey().stringValue() + " chosenProviders.size=" + chosenProviders.size());
        }
        
        for(final Provider nextProvider : chosenProviders)
        {
            final boolean noCommunicationProvider =
                    nextProvider.getEndpointMethod().equals(ProviderSchema.getProviderNoCommunication());
            
            if(nextProvider instanceof HttpProvider)
            {
                if(RdfFetchController._DEBUG)
                {
                    RdfFetchController.log.debug("instanceof HttpProvider key=" + nextProvider.getKey());
                }
                final HttpProvider nextHttpProvider = (HttpProvider)nextProvider;
                Map<String, String> attributeList = new HashMap<String, String>();
                
                final List<String> nextEndpointUrls = ListUtils.randomiseListLayout(nextHttpProvider.getEndpointUrls());
                
                final Map<String, Map<String, String>> replacedEndpoints = new HashMap<String, Map<String, String>>();
                
                for(final String nextEndpoint : nextEndpointUrls)
                {
                    String replacedEndpoint =
                            nextEndpoint.replace(Constants.TEMPLATE_REAL_HOST_NAME, this.realHostName)
                                    .replace(Constants.TEMPLATE_DEFAULT_SEPARATOR, localSettings.getSeparator())
                                    .replace(Constants.TEMPLATE_OFFSET, String.valueOf(this.pageOffset));
                    
                    // perform the ${input_1} ${urlEncoded_input_1} ${xmlEncoded_input_1} etc
                    // replacements on nextEndpoint before using it in the attribute list
                    replacedEndpoint =
                            QueryCreator.matchAndReplaceInputVariablesForQueryType(nextQueryType, this.queryParameters,
                                    replacedEndpoint, Constants.EMPTY_STRING_LIST,
                                    overallConvertAlternateToPreferredPrefix, namespaceInputVariables, nextProvider);
                    
                    attributeList =
                            QueryCreator.getAttributeListFor(nextQueryType, nextProvider, this.queryParameters,
                                    replacedEndpoint, this.realHostName, this.pageOffset, localSettings);
                    
                    // This step is needed in order to replace endpointSpecific related template
                    // elements on the provider URL
                    replacedEndpoint =
                            QueryCreator
                                    .replaceAttributesOnEndpointUrl(
                                            replacedEndpoint,
                                            nextQueryType,
                                            nextProvider,
                                            attributeList,
                                            this.sortedIncludedProfiles,
                                            localSettings
                                                    .getBooleanProperty(WebappConfig.RECOGNISE_IMPLICIT_RDFRULE_INCLUSIONS),
                                            localSettings
                                                    .getBooleanProperty(WebappConfig.INCLUDE_NON_PROFILE_MATCHED_RDFRULES),
                                            overallConvertAlternateToPreferredPrefix, localSettings,
                                            namespaceInputVariables);
                    
                    final String nextEndpointQuery =
                            QueryCreator
                                    .createQuery(
                                            nextQueryType,
                                            nextProvider,
                                            attributeList,
                                            this.sortedIncludedProfiles,
                                            localSettings
                                                    .getBooleanProperty(WebappConfig.RECOGNISE_IMPLICIT_RDFRULE_INCLUSIONS),
                                            localSettings
                                                    .getBooleanProperty(WebappConfig.INCLUDE_NON_PROFILE_MATCHED_RDFRULES),
                                            overallConvertAlternateToPreferredPrefix, localSettings,
                                            namespaceInputVariables);
                    
                    // replace the query on the endpoint URL if necessary
                    replacedEndpoint =
                            replacedEndpoint.replace(Constants.TEMPLATE_PERCENT_ENCODED_ENDPOINT_QUERY,
                                    StringUtils.percentEncode(nextEndpointQuery));
                    
                    if(replacedEndpoints.containsKey(nextEndpoint))
                    {
                        replacedEndpoints.get(nextEndpoint).put(replacedEndpoint, nextEndpointQuery);
                    }
                    else
                    {
                        final Map<String, String> newList = new HashMap<String, String>();
                        newList.put(replacedEndpoint, nextEndpointQuery);
                        
                        replacedEndpoints.put(nextEndpoint, newList);
                    }
                }
                
                String nextStaticRdfXmlString = "";
                
                for(final URI nextCustomInclude : nextQueryType.getLinkedQueryTypes())
                {
                    // pick out all of the QueryType's which have been delegated for this
                    // particular
                    // query as static includes
                    final QueryType nextCustomIncludeType = localSettings.getAllQueryTypes().get(nextCustomInclude);
                    
                    if(nextCustomIncludeType == null)
                    {
                        RdfFetchController.log
                                .warn("RdfFetchController: no included queries found for nextCustomInclude="
                                        + nextCustomInclude);
                    }
                    else
                    {
                        // then also create the statically defined rdf/xml string to go with this
                        // query based on the current attributes, we assume that both queries have
                        // been intelligently put into the configuration file so that they have an
                        // equivalent number of arguments as ${input_1} etc, in them. There is no
                        // general solution for determining how these should work other than naming
                        // them as ${namespace} and ${identifier} and ${searchTerm}, but these can
                        // be worked around by only offering compatible services as alternatives
                        // with the static rdf/xml portions
                        if(nextCustomIncludeType instanceof OutputQueryType)
                        {
                            nextStaticRdfXmlString +=
                                    QueryCreator
                                            .createStaticRdfXmlString(
                                                    nextQueryType,
                                                    (OutputQueryType)nextCustomIncludeType,
                                                    nextProvider,
                                                    attributeList,
                                                    namespaceInputVariables,
                                                    this.sortedIncludedProfiles,
                                                    localSettings
                                                            .getBooleanProperty(WebappConfig.RECOGNISE_IMPLICIT_RDFRULE_INCLUSIONS),
                                                    localSettings
                                                            .getBooleanProperty(WebappConfig.INCLUDE_NON_PROFILE_MATCHED_RDFRULES),
                                                    overallConvertAlternateToPreferredPrefix, localSettings);
                        }
                        else
                        {
                            RdfFetchController.log
                                    .warn("Attempted to include a query type that was not parsed as an output query type key="
                                            + nextCustomIncludeType.getKey()
                                            + " types="
                                            + nextCustomIncludeType.getElementTypes());
                        }
                    }
                }
                
                final QueryBundle nextProviderQueryBundle = new QueryBundle();
                
                nextProviderQueryBundle.setOutputString(nextStaticRdfXmlString);
                nextProviderQueryBundle.setOriginalProvider(nextProvider);
                nextProviderQueryBundle.setQueryType(nextQueryType);
                nextProviderQueryBundle.setRelevantProfiles(this.sortedIncludedProfiles);
                nextProviderQueryBundle.setQueryallSettings(localSettings);
                
                if(RdfFetchController._DEBUG)
                {
                    RdfFetchController.log.debug("nextQueryType=" + nextQueryType.getKey().stringValue());
                }
                
                for(final String nextEndpoint : ListUtils.randomiseListLayout(replacedEndpoints.keySet()))
                {
                    final Map<String, String> originalEndpointEntries = replacedEndpoints.get(nextEndpoint);
                    
                    if(RdfFetchController._DEBUG)
                    {
                        RdfFetchController.log.debug("nextEndpoint=" + nextEndpoint);
                    }
                    
                    for(final String nextReplacedEndpoint : originalEndpointEntries.keySet())
                    {
                        if(RdfFetchController._DEBUG)
                        {
                            RdfFetchController.log.debug("nextReplacedEndpoint=" + nextReplacedEndpoint);
                        }
                        
                        if(nextReplacedEndpoint == null)
                        {
                            RdfFetchController.log.error("nextReplacedEndpoint was null nextEndpoint=" + nextEndpoint
                                    + " nextQueryType=" + nextQueryType + " nextProvider=" + nextProvider);
                            continue;
                        }
                        
                        // Then test whether the endpoint is blacklisted before accepting it
                        if(noCommunicationProvider
                                || !this.getBlacklistController().isUrlBlacklisted(nextReplacedEndpoint))
                        {
                            if(RdfFetchController._DEBUG)
                            {
                                RdfFetchController.log.debug("not blacklisted");
                            }
                            
                            // no need to worry about redundant endpoint alternates if we are going
                            // to try to query all of the endpoints for each provider
                            if(nextProviderQueryBundle.getAlternativeEndpointsAndQueries().size() == 0
                                    || useAllEndpointsForEachProvider)
                            {
                                // FIXME: Check to make sure that this does not generate nulls
                                nextProviderQueryBundle.addAlternativeEndpointAndQuery(nextReplacedEndpoint,
                                        originalEndpointEntries.get(nextReplacedEndpoint));
                            }
                            else
                            {
                                RdfFetchController.log
                                        .warn("Not adding an endpoint because we are not told to attempt to use all endpoints, and we have already chosen one");
                            }
                        }
                        else
                        {
                            RdfFetchController.log
                                    .warn("Not including provider because it is not no-communication and is a blacklisted url nextProvider.getKey()="
                                            + nextProvider.getKey());
                        }
                    }
                    
                    results.add(nextProviderQueryBundle);
                }
            } // end if(nextProvider instanceof HttpProvider)
            else if(noCommunicationProvider)
            {
                if(RdfFetchController._DEBUG)
                {
                    RdfFetchController.log.debug("endpoint method = noCommunication key=" + nextProvider.getKey());
                }
                
                String nextStaticRdfXmlString = "";
                
                for(final URI nextCustomInclude : nextQueryType.getLinkedQueryTypes())
                {
                    // pick out all of the QueryType's which have been delegated for this particular
                    // query as static includes
                    final QueryType nextCustomIncludeType = localSettings.getAllQueryTypes().get(nextCustomInclude);
                    
                    if(nextCustomIncludeType == null)
                    {
                        RdfFetchController.log
                                .warn("Attempted to include an unknown include type using the URI nextCustomInclude="
                                        + nextCustomInclude.stringValue());
                    }
                    else
                    {
                        final Map<String, String> attributeList =
                                QueryCreator.getAttributeListFor(nextCustomIncludeType, nextProvider,
                                        this.queryParameters, "", this.realHostName, this.pageOffset, localSettings);
                        
                        if(nextCustomIncludeType instanceof OutputQueryType)
                        {
                            nextStaticRdfXmlString +=
                                    QueryCreator
                                            .createStaticRdfXmlString(
                                                    nextQueryType,
                                                    (OutputQueryType)nextCustomIncludeType,
                                                    nextProvider,
                                                    attributeList,
                                                    namespaceInputVariables,
                                                    this.sortedIncludedProfiles,
                                                    localSettings
                                                            .getBooleanProperty(WebappConfig.RECOGNISE_IMPLICIT_RDFRULE_INCLUSIONS),
                                                    localSettings
                                                            .getBooleanProperty(WebappConfig.INCLUDE_NON_PROFILE_MATCHED_RDFRULES),
                                                    overallConvertAlternateToPreferredPrefix, localSettings);
                        }
                        else
                        {
                            RdfFetchController.log
                                    .warn("Attempted to include a query type that was not parsed as an output query type key="
                                            + nextCustomIncludeType.getKey()
                                            + " types="
                                            + nextCustomIncludeType.getElementTypes());
                        }
                    }
                }
                
                final QueryBundle nextProviderQueryBundle = new QueryBundle();
                
                nextProviderQueryBundle.setOutputString(nextStaticRdfXmlString);
                nextProviderQueryBundle.setProvider(nextProvider);
                nextProviderQueryBundle.setQueryType(nextQueryType);
                nextProviderQueryBundle.setRelevantProfiles(this.sortedIncludedProfiles);
                nextProviderQueryBundle.setQueryallSettings(localSettings);
                
                results.add(nextProviderQueryBundle);
            }
            else
            {
                RdfFetchController.log.warn("Unrecognised provider endpoint method type nextProvider.getKey()="
                        + nextProvider.getKey() + " nextProvider.getClass().getName()="
                        + nextProvider.getClass().getName() + " endpointMethod=" + nextProvider.getEndpointMethod());
            }
        } // end for(Provider nextProvider : QueryTypeProviders)
        
        if(RdfFetchController._DEBUG)
        {
            RdfFetchController.log
                    .debug("RdfFetchController.generateQueryBundlesForQueryTypeAndProviders: results.size()="
                            + results.size());
        }
        
        return results;
    }
    
    public Collection<Provider> getAllUsedProviders()
    {
        final Collection<Provider> results = new LinkedList<Provider>();
        
        for(final QueryBundle nextQueryBundle : this.getQueryBundles())
        {
            results.add(nextQueryBundle.getOriginalProvider());
        }
        
        return results;
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
    
    private synchronized void initialise(boolean generateQueryBundles) throws QueryAllException
    {
        final long start = System.currentTimeMillis();
        
        if(generateQueryBundles)
        {
            // overwrite any query bundles that may have been inserted previously as we were told to generate new query bundles
            this.queryBundles = new LinkedList<QueryBundle>();
            
            // Note: this set contains queries that matched without taking into account the
            // namespaces assigned to each query type
            // The calculation of the namespace matching is done later
            final Map<QueryType, Map<String, Collection<NamespaceEntry>>> allCustomQueries =
                    QueryTypeUtils.getQueryTypesMatchingQuery(this.queryParameters, this.sortedIncludedProfiles,
                            this.getSettings().getAllQueryTypes(), this.getSettings().getNamespacePrefixesToUris(),
                            this.getSettings().getAllNamespaceEntries(),
                            this.getSettings().getBooleanProperty(WebappConfig.RECOGNISE_IMPLICIT_QUERY_INCLUSIONS),
                            this.getSettings().getBooleanProperty(WebappConfig.INCLUDE_NON_PROFILE_MATCHED_QUERIES));
            
            if(RdfFetchController._DEBUG)
            {
                RdfFetchController.log.debug("RdfFetchController.initialise: found " + allCustomQueries.size()
                        + " matching queries");
            }
            
            // TODO: should we do classification in the results based on the QueryType that
            // generated the particular subset of QueryBundles to make it easier to distinguish them
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
                
                final Collection<Provider> chosenProviders = new HashSet<Provider>();
                
                if(!nextQueryType.getIsNamespaceSpecific())
                {
                    chosenProviders.addAll(ProviderUtils.getProvidersForQueryNonNamespaceSpecific(
                            this.getSettings().getAllProviders(), nextQueryType.getKey(), this.sortedIncludedProfiles,
                            this.getSettings().getBooleanProperty(WebappConfig.RECOGNISE_IMPLICIT_PROVIDER_INCLUSIONS),
                            this.getSettings().getBooleanProperty(WebappConfig.INCLUDE_NON_PROFILE_MATCHED_PROVIDERS)));
                }
                else
                {
                    chosenProviders.addAll(ProviderUtils.getProvidersForQueryNamespaceSpecific(
                            this.getSettings().getAllProviders(), this.sortedIncludedProfiles, nextQueryType,
                            this.getSettings().getNamespacePrefixesToUris(), this.queryParameters,
                            this.getSettings().getBooleanProperty(WebappConfig.RECOGNISE_IMPLICIT_PROVIDER_INCLUSIONS),
                            this.getSettings().getBooleanProperty(WebappConfig.INCLUDE_NON_PROFILE_MATCHED_PROVIDERS)));
                }
                
                if(nextQueryType.getIncludeDefaults() && this.useDefaultProviders)
                {
                    if(RdfFetchController._DEBUG)
                    {
                        RdfFetchController.log
                                .debug("RdfFetchController.initialise: including defaults for nextQueryType.title="
                                        + nextQueryType.getTitle() + " nextQueryType.getKey()="
                                        + nextQueryType.getKey());
                    }
                    
                    chosenProviders.addAll(ProviderUtils.getDefaultProviders(this.getSettings().getAllProviders(),
                            nextQueryType, this.sortedIncludedProfiles,
                            this.getSettings().getBooleanProperty(WebappConfig.RECOGNISE_IMPLICIT_PROVIDER_INCLUSIONS),
                            this.getSettings().getBooleanProperty(WebappConfig.INCLUDE_NON_PROFILE_MATCHED_PROVIDERS)));
                }
                
                if(RdfFetchController._DEBUG)
                {
                    final int QueryTypeProvidersSize = chosenProviders.size();
                    
                    if(QueryTypeProvidersSize > 0)
                    {
                        RdfFetchController.log.debug("RdfFetchController.initialise: found " + QueryTypeProvidersSize
                                + " providers for nextQueryType.getKey()=" + nextQueryType.getKey());
                        
                        if(RdfFetchController._TRACE)
                        {
                            for(final Provider nextQueryTypeProvider : chosenProviders)
                            {
                                RdfFetchController.log.trace("RdfFetchController.initialise: nextQueryTypeProvider="
                                        + nextQueryTypeProvider.getKey());
                            }
                        }
                    }
                    else if(RdfFetchController._TRACE && QueryTypeProvidersSize == 0)
                    {
                        RdfFetchController.log
                                .trace("RdfFetchController.initialise: found NO suitable providers for custom type="
                                        + nextQueryType.getKey());
                    }
                } // end if(_DEBUG}
                
                // Default to safe setting of useAllEndpointsForEachProvider=true here
                final Collection<QueryBundle> queryBundlesForQueryType =
                        this.generateQueryBundlesForQueryTypeAndProviders(this.getSettings(), nextQueryType,
                                allCustomQueries.get(nextQueryType), chosenProviders,
                                this.getSettings().getBooleanProperty(WebappConfig.TRY_ALL_ENDPOINTS_FOR_EACH_PROVIDER));
                
                if(RdfFetchController._DEBUG)
                {
                    RdfFetchController.log.debug("RdfFetchController.initialise: queryBundlesForQueryType.size()="
                            + queryBundlesForQueryType.size());
                    
                    if(RdfFetchController._TRACE)
                    {
                        for(final QueryBundle nextQueryBundleForQueryType : queryBundlesForQueryType)
                        {
                            RdfFetchController.log.trace("RdfFetchController.initialise: nextQueryBundleForQueryType="
                                    + nextQueryBundleForQueryType.toString());
                        }
                    }
                }
                
                this.getQueryBundles().addAll(queryBundlesForQueryType);
                
                // if there are still no query bundles check for the non-namespace specific version
                // of the query type to flag any instances of the namespace not being recognised
                if(queryBundlesForQueryType.size() == 0)
                {
                    if(nextQueryType.getIsNamespaceSpecific()
                            && ProviderUtils.getProvidersForQueryNonNamespaceSpecific(
                                    this.getSettings().getAllProviders(),
                                    nextQueryType.getKey(),
                                    this.sortedIncludedProfiles,
                                    this.getSettings()
                                            .getBooleanProperty(WebappConfig.RECOGNISE_IMPLICIT_PROVIDER_INCLUSIONS),
                                    this.getSettings()
                                            .getBooleanProperty(WebappConfig.INCLUDE_NON_PROFILE_MATCHED_PROVIDERS))
                                    .size() > 0)
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
        } // end if(queryBundles == null)
        
        if(this.fetchThreadGroup.size() == 0)
        {
            this.setFetchThreadGroup(this.generateFetchThreadsFromQueryBundles(this.getQueryBundles(),
                    this.getSettings().getIntProperty(WebappConfig.PAGEOFFSET_INDIVIDUAL_QUERY_LIMIT)));
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
            if(!nextQueryBundle.getQueryType().getIsDummyQueryType())
            {
                if(RdfFetchController._DEBUG)
                {
                    RdfFetchController.log
                            .debug("RdfFetchController.queryKnown: returning true after looking at nextQueryBundle.getQueryType()="
                                    + nextQueryBundle.getQueryType().getKey().stringValue());
                }
                
                return true;
            }
        }
        
        if(RdfFetchController._DEBUG)
        {
            RdfFetchController.log.debug("RdfFetchController.queryKnown: returning false at end of method");
        }
        
        return false;
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

    public QueryAllConfiguration getSettings()
    {
        return localSettings;
    }

    public void setSettings(QueryAllConfiguration localSettings)
    {
        this.localSettings = localSettings;
    }

    public BlacklistController getBlacklistController()
    {
        return localBlacklistController;
    }

    public void setBlacklistController(BlacklistController localBlacklistController)
    {
        this.localBlacklistController = localBlacklistController;
    }

    public void setQueryBundles(Collection<QueryBundle> queryBundles)
    {
        this.queryBundles = queryBundles;
    }
}
