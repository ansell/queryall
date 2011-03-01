package org.queryall.queryutils;

import org.queryall.*;
import org.queryall.blacklist.*;
import org.queryall.impl.*;
import org.queryall.helpers.*;

import org.apache.log4j.Logger;

import org.openrdf.model.URI;

import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collection;
import java.util.HashSet;

public class RdfFetchController
{
    private static final Logger log = Logger.getLogger( RdfFetchController.class.getName() );
    private static final boolean _TRACE = log.isTraceEnabled();
    private static final boolean _DEBUG = log.isDebugEnabled();
    private static final boolean _INFO = log.isInfoEnabled();
    
    public Collection<RdfFetcherQueryRunnable> errorResults = new HashSet<RdfFetcherQueryRunnable>( 10 );
    public Collection<RdfFetcherQueryRunnable> successfulResults = new HashSet<RdfFetcherQueryRunnable>( 10 );
    public Collection<RdfFetcherQueryRunnable> uncalledThreads = new HashSet<RdfFetcherQueryRunnable>( 4 );
    public Collection<RdfFetcherQueryRunnable> fetchThreadGroup = new HashSet<RdfFetcherQueryRunnable>( 20 );
    
    private Collection<QueryBundle> queryBundles = null;
    
    private String queryString;
    private List<Profile> sortedIncludedProfiles;
    private boolean useDefaultProviders = true;
    private String realHostName;
    private int pageOffset;
    private String returnFileFormat;
    private boolean includeNonPagedQueries = true;
    
    public RdfFetchController(Collection<QueryBundle> nextQueryBundles)
    {
        queryBundles = nextQueryBundles;
        
        initialise();
    }
    
    public RdfFetchController( String nextQueryString, List<Profile> nextIncludedSortedProfiles, boolean nextUseDefaultProviders, String nextRealHostName, int nextPageOffset, String nextReturnFileFormat )
    {
        queryString = nextQueryString;
        sortedIncludedProfiles = nextIncludedSortedProfiles;
        useDefaultProviders = nextUseDefaultProviders;
        realHostName = nextRealHostName;
        pageOffset = nextPageOffset;
        includeNonPagedQueries = (pageOffset == 1);
        returnFileFormat = nextReturnFileFormat;
        
        initialise();
    }
    
    public Collection<Provider> getAllUsedProviders()
    {
        Collection<Provider> results = new HashSet<Provider>();
        
        for(QueryBundle nextQueryBundle : queryBundles)
        {
            results.add(nextQueryBundle.originalProvider);
        }
        
        return results;
    }
    
    public Collection<QueryBundle> getQueryBundles()
    {
        return queryBundles;
    }
    
    public boolean queryKnown()
    {
        return queryBundles.size()  > 0;
    }
    
    private void initialise()
    {
        final long start = System.currentTimeMillis();
        
        if( pageOffset < 1 )
        {
            log.warn( "RdfFetchController.initialise: correcting pageoffset to 1, previous pageOffset="+pageOffset );
            
            pageOffset = 1;
        }
        
        if(queryBundles == null)
        {
            queryBundles = new HashSet<QueryBundle>( 20 );
            
            // TODO: figure out how to also get back the NamespaceEntry objects that matched so we can log this information with the statistics for this query
            Collection<QueryType> allCustomQueries = Settings.getCustomQueriesMatchingQueryString( queryString, sortedIncludedProfiles );
            
            if( _DEBUG )
            {
                log.debug("RdfFetchController.initialise: found "+allCustomQueries.size()+" matching queries");
            }
            
            // TODO: should we do classification in the results based on the QueryType that generated the particular subset of QueryBundles to make it easier to distinguish them
            for( QueryType nextQueryType : allCustomQueries )
            {
                if(!includeNonPagedQueries && !nextQueryType.getIsPageable())
                {
                    if(_INFO)
                    {
                        log.info("RdfFetchController: not using nonPagedQuery="+nextQueryType.getKey());
                    }
                    
                    continue;
                }
                
                Collection<Provider> QueryTypeProviders = new HashSet<Provider>();
                
                if( !nextQueryType.getIsNamespaceSpecific() )
                {
                    // if we aren't specific to namespace we simply find all providers for this type of custom query
                    Collection<Provider> allProviders = Settings.getProvidersForQueryType( nextQueryType.getKey() );
                    
                    for( Provider nextAllProvider : allProviders )
                    {
                        if( _DEBUG )
                        {
                            log.debug( "RdfFetchController.initialise: !nextQueryType.isNamespaceSpecific nextAllProvider="+nextAllProvider.toString() );
                        }
                        
                        if( Settings.isProviderUsedWithProfileList( nextAllProvider.getKey(), nextAllProvider.getProfileIncludeExcludeOrder(), sortedIncludedProfiles ) )
                        {
                            if( _DEBUG )
                            {
                                log.debug( "RdfFetchController.initialise: profileList suitable for nextAllProvider.getKey()="+nextAllProvider.getKey()+" queryString="+queryString );
                            }
                            
                            QueryTypeProviders.add( nextAllProvider );
                        }
                    }
                }
                else
                {
                    List<String> queryStringMatches = nextQueryType.matchesForQueryString( queryString );
                    
                    int queryStringMatchesSize = queryStringMatches.size();
                    
                    // Collection<String> nextQueryNamespacePrefixes = new HashSet<String>();
                    Collection<Collection<URI>> nextQueryNamespaceUris = new HashSet<Collection<URI>>();
                    Hashtable<String, Hashtable<String, Collection<String>>> titleToPreferredPrefixToUriMapping = new Hashtable<String, Hashtable<String, Collection<String>>>();
                    
                    for( int nextNamespaceInputIndex : nextQueryType.getNamespaceInputIndexes() )
                    {
                        if( queryStringMatchesSize  >= nextNamespaceInputIndex && nextNamespaceInputIndex > 0 )
                        {
                            String nextTitle = queryStringMatches.get( nextNamespaceInputIndex-1 );
                            
                            Collection<URI> nextUriFromTitleNamespaceList = Settings.getNamespaceUrisForTitle( nextTitle );
                            
                            if( nextUriFromTitleNamespaceList != null )
                            {
                                nextQueryNamespaceUris.add( nextUriFromTitleNamespaceList );
                            }
                            else
                            {
                                log.warn( "RdfFetchController.initialise did not find any namespace URIs for nextTitle="+nextTitle + " nextQueryType.getKey()="+nextQueryType.getKey());
                            }
                        }
                        else
                        {
                            log.error( "RdfFetchController.initialise: Could not match the namespace because the input index was invalid nextNamespaceInputIndex="+nextNamespaceInputIndex+" queryStringMatches.size()="+queryStringMatches.size() );
                            
                            throw new RuntimeException( "Could not match the namespace because the input index was invalid nextNamespaceInputIndex="+nextNamespaceInputIndex+" queryStringMatches.size()="+queryStringMatches.size() );
                        }
                    }
                    
                    if( _DEBUG )
                    {
                        // log.debug( "RdfFetchController.initialise: nextQueryNamespacePrefixes="+nextQueryNamespacePrefixes );
                        log.debug( "RdfFetchController.initialise: nextQueryNamespaceUris="+nextQueryNamespaceUris );
                    }
                    
                    if( nextQueryType.handlesNamespaceUris( nextQueryNamespaceUris ) )
                    {
                        if( _DEBUG )
                        {
                            log.debug( "RdfFetchController.initialise: confirmed to handle namespaces nextQueryType.getKey()="+nextQueryType.getKey() +" nextQueryNamespaceUris="+nextQueryNamespaceUris );
                        }
                        
                        Collection<Provider> namespaceSpecificProviders = Settings.getProvidersForQueryTypeForNamespaceUris( nextQueryType.getKey(), nextQueryNamespaceUris, nextQueryType.getNamespaceMatchMethod() );
                        
                        for( Provider nextNamespaceSpecificProvider : namespaceSpecificProviders )
                        {
                            if( _TRACE )
                            {
                                log.trace( "RdfFetchController.initialise: nextQueryType.isNamespaceSpecific nextNamespaceSpecificProvider="+nextNamespaceSpecificProvider.getKey() );
                            }
                            
                            if( Settings.isProviderUsedWithProfileList( nextNamespaceSpecificProvider.getKey(), nextNamespaceSpecificProvider.getProfileIncludeExcludeOrder(), sortedIncludedProfiles ) )
                            {
                                if( _DEBUG )
                                {
                                    log.debug( "RdfFetchController.initialise: profileList suitable for nextNamespaceSpecificProvider.getKey()="+nextNamespaceSpecificProvider.getKey()+" queryString="+queryString );
                                }
                                
                                QueryTypeProviders.add( nextNamespaceSpecificProvider );
                            }
                        }
                    }
                }
                
                if( nextQueryType.getIncludeDefaults() && useDefaultProviders )
                {
                    if( _DEBUG )
                    {
                        log.debug( "RdfFetchController.initialise: including defaults for nextQueryType.title="+nextQueryType.getTitle()+" nextQueryType.getKey()="+nextQueryType.getKey() );
                    }
                    
                    Collection<Provider> defaultProviders = Settings.getDefaultProviders(nextQueryType.getKey());
                    
                    Collection<Provider> usefulDefaultProviders = new HashSet<Provider>();
                    
                    for( Provider nextDefaultProvider : defaultProviders )
                    {
                        // Note: We avoid duplicate calls based on the functional specification in Provider.functionallyDifferentTo
                        boolean providerAlreadyUsed = false;
                        
                        for( Provider nextCollectionProvider : QueryTypeProviders )
                        {
                            if( nextCollectionProvider.equals( nextDefaultProvider ) )
                            {
                                if( _TRACE )
                                {
                                    log.trace( "Atlas2Rdf.jsp: Default Provider not functionally different to another provider nextDefaultProvider.getKey()="+nextDefaultProvider.getKey()+" nextCollectionProvider.getKey()="+nextCollectionProvider.getKey() );
                                }
                                
                                providerAlreadyUsed = true;
                                break;
                            }
                        }
                        
                        
                        if( !providerAlreadyUsed )
                        {
                            if( Settings.isProviderUsedWithProfileList( nextDefaultProvider.getKey(), nextDefaultProvider.getProfileIncludeExcludeOrder(), sortedIncludedProfiles ) )
                            {
                                if( _DEBUG )
                                {
                                    log.debug( "RdfFetchController.initialise: profileList suitable for nextDefaultProvider.getKey()="+nextDefaultProvider.getKey()+" queryString="+queryString );
                                }
                                
                                usefulDefaultProviders.add( nextDefaultProvider );
                            }
                        }
                    }
                    
                    QueryTypeProviders.addAll( usefulDefaultProviders );
                }
                
                if( _DEBUG )
                {
                    int QueryTypeProvidersSize = QueryTypeProviders.size();
                    
                    if( QueryTypeProvidersSize  > 0 )
                    {
                        log.debug( "RdfFetchController.initialise: found "+QueryTypeProvidersSize +" providers for nextQueryType.getKey()="+nextQueryType.getKey() );
                        
                        if( _TRACE )
                        {
                            for( Provider nextQueryTypeProvider : QueryTypeProviders )
                            {
                                log.trace( "RdfFetchController.initialise: nextQueryTypeProvider="+nextQueryTypeProvider.getKey() );
                            }
                        }
                    }
                    else if( _TRACE && QueryTypeProvidersSize == 0 )
                    {
                        log.trace( "RdfFetchController.initialise: found NO suitable providers for custom type="+nextQueryType.getKey() );
                    }
                }
                
                for( Provider nextProvider : QueryTypeProviders )
                {
                    
                    // check if there is an endpoint, as we allow for providers which are really placeholders for static RDF/XML additions, and they are configured without endpoint URL's and with NO_COMMUNICATION
                    if( nextProvider.hasEndpointUrl() )
                    {
                        Map<String, String> attributeList = null;
                        
                        Collection<String> nextEndpointUrls = Utilities.randomiseListLayout(nextProvider.getEndpointUrls());
                        
                        for( String nextEndpoint : nextEndpointUrls )
                        {
                            String replacedEndpoint = nextEndpoint
                                                .replace( "${realHostName}",realHostName )
                                                .replace( "${defaultSeparator}",Settings.getStringPropertyFromConfig("separator") )
                                                .replace( "${offset}",pageOffset+"" );
                            
                            // perform the ${input_1} ${urlEncoded_input_1} ${xmlEncoded_input_1} etc replacements before using it in the attribute list
                            replacedEndpoint = SparqlQueryCreator.matchAndReplaceInputVariablesForQueryType( nextQueryType, queryString, replacedEndpoint, new ArrayList<String>() );
                            
                            attributeList = SparqlQueryCreator.getAttributeListFor( nextProvider, queryString, replacedEndpoint, realHostName, pageOffset );
                            
                            // This step is needed in order to replace endpointSpecific related template elements on the provider URL
                            replacedEndpoint = SparqlQueryCreator.replaceAttributesOnEndpointUrl( replacedEndpoint, nextQueryType, nextProvider, attributeList, sortedIncludedProfiles );
                            
                            // Then test whether the endpoint is blacklisted
                            if(!BlacklistController.isUrlBlacklisted(replacedEndpoint))
                            {
                                String nextEndpointQuery = SparqlQueryCreator.createQuery( nextQueryType, nextProvider, attributeList, sortedIncludedProfiles );
                                
                                String nextStaticRdfXmlString = "";
                                
                                for( URI nextCustomInclude : nextQueryType.getSemanticallyLinkedQueryTypes() )
                                {
                                    // pick out all of the QueryType's which have been delegated for this particular query as static includes
                                    Collection<QueryType> allCustomRdfXmlIncludeTypes = Settings.getCustomQueriesByUri( nextCustomInclude );
                                    
                                    if( allCustomRdfXmlIncludeTypes.size()  == 0 )
                                    {
                                        log.warn( "RdfFetchController: no included queries found for nextCustomInclude="+nextCustomInclude );
                                    }
                                    
                                    for( QueryType nextCustomIncludeType : allCustomRdfXmlIncludeTypes )
                                    {
                                        // then also create the statically defined rdf/xml string to go with this query based on the current attributes, we assume that both queries have been intelligently put into the configuration file so that they have an equivalent number of arguments as ${input_1} etc, in them.
                                        // There is no general solution for determining how these should work other than naming them as ${namespace} and ${identifier} and ${searchTerm}, but these can be worked around by only offering compatible services as alternatives with the static rdf/xml portions
                                        nextStaticRdfXmlString += SparqlQueryCreator.createStaticRdfXmlString( nextQueryType, nextCustomIncludeType, nextProvider, attributeList, sortedIncludedProfiles );
                                    }
                                }
                                
                                QueryBundle nextProviderQueryBundle = new QueryBundle();
                                
                                nextProviderQueryBundle.query = nextEndpointQuery;
                                nextProviderQueryBundle.staticRdfXmlString = nextStaticRdfXmlString;
                                nextProviderQueryBundle.queryEndpoint = replacedEndpoint;
                                nextProviderQueryBundle.originalEndpointString = nextEndpoint;
                                nextProviderQueryBundle.originalProvider = nextProvider;
                                nextProviderQueryBundle.setQueryType(nextQueryType);
                                nextProviderQueryBundle.relevantProfiles = sortedIncludedProfiles;
                                
                                queryBundles.add( nextProviderQueryBundle );
                                
                                // go to next provider if we are not told to use all of the providers possible
                                if( !Settings.getBooleanPropertyFromConfig("useAllEndpointsForEachProvider"))
                                {
                                    break;
                                }
                            } // end if(!BlacklistController.isUrlBlacklisted(nextEndpoint))
                        } // end for(String nextEndpoint : nextProvider.endpointUrls)
                    } // end if(nextProvider.hasEndpointUrl())
                    else if( nextProvider.getEndpointMethod().equals( ProviderImpl.getProviderNoCommunication() ) )
                    {
                        Map<String, String> attributeList = SparqlQueryCreator.getAttributeListFor( nextProvider, queryString, "", realHostName, pageOffset );
                        
                        String nextStaticRdfXmlString = "";
                        
                        for( URI nextCustomInclude : nextQueryType.getSemanticallyLinkedQueryTypes() )
                        {
                            // pick out all of the QueryType's which have been delegated for this particular query as static includes
                            Collection<QueryType> allCustomRdfXmlIncludeTypes = Settings.getCustomQueriesByUri( nextCustomInclude );
                            
                            for( QueryType nextCustomIncludeType : allCustomRdfXmlIncludeTypes )
                            {
                                // then also create the statically defined rdf/xml string to go with this query based on the current attributes, we assume that both queries have been intelligently put into the configuration file so that they have an equivalent number of arguments as ${input_1} etc, in them.
                                // There is no general solution for determining how these should work other than naming them as ${namespace} and ${identifier} and ${searchTerm}, but these can be worked around by only offering compatible services as alternatives with the static rdf/xml portions
                                nextStaticRdfXmlString += SparqlQueryCreator.createStaticRdfXmlString( nextQueryType, nextCustomIncludeType, nextProvider, attributeList, sortedIncludedProfiles );
                            }
                        }
                        
                        QueryBundle nextProviderQueryBundle = new QueryBundle();
                        
                        nextProviderQueryBundle.staticRdfXmlString = nextStaticRdfXmlString;
                        nextProviderQueryBundle.setProvider(nextProvider);
                        nextProviderQueryBundle.setQueryType(nextQueryType);
                        nextProviderQueryBundle.relevantProfiles = sortedIncludedProfiles;
                        
                        queryBundles.add( nextProviderQueryBundle );
                    }
                    else
                    {
                        log.warn("RdfFetchController: no endpoint URL's found for provider.getKey()="+nextProvider.getKey());
                    }
                } // end for(Provider nextProvider : QueryTypeProviders)
            } // end for(QueryType nextQueryType : allCustomQueries)
        } // end if(queryBundles == null)
        
        if( _DEBUG )
        {
            if(queryBundles.size() > 0)
            {
                for( QueryBundle nextQueryBundleDebug : queryBundles )
                {
                    log.debug( "RdfFetchController.initialise: nextQueryBundleDebug="+nextQueryBundleDebug.toString() );
                }
            }
            else
            {
                log.debug("RdfFetchController.initialise: no query bundles given or created");
            }
        }
        
        // queryBundles = multiProviderQueryBundles;
        // return multiProviderQueryBundles;
        
        for( QueryBundle nextBundle : queryBundles )
        {
            String nextEndpoint = nextBundle.queryEndpoint;
            String nextQuery = nextBundle.query;
            
            if( _DEBUG )
            {
                log.debug( "RdfFetchController.initialise:: About to create a thread for query on endpoint="+nextEndpoint+" query=" + nextQuery + " provider="+nextBundle.originalProvider.getKey());
            }
            
            RdfFetcherQueryRunnable nextThread = null;
            
            boolean addToFetchQueue = false;
            
            if( nextBundle.originalProvider.getEndpointMethod().equals( ProviderImpl.getProviderHttpPostSparql() ) )
            {
                nextThread = new RdfFetcherSparqlQueryRunnable( nextEndpoint,
                             nextBundle.originalProvider.getSparqlGraphUri(),
                             returnFileFormat,
                             nextQuery,
                             "off",
                             nextBundle.originalProvider.getAcceptHeaderString(),
                             Settings.getIntPropertyFromConfig("pageoffsetIndividualQueryLimit"),
                             nextBundle );
                             
                addToFetchQueue = true;
                
                if(_TRACE)
                {
                    log.trace("RdfFetchController.initialise: created HTTP POST SPARQL query thread on nextEndpoint="+nextEndpoint+" provider.getKey()="+nextBundle.originalProvider.getKey());
                }
            }
            else if( nextBundle.originalProvider.getEndpointMethod().equals( ProviderImpl.getProviderHttpGetUrl() ) )
            {
                nextThread = new RdfFetcherUriQueryRunnable( nextEndpoint,
                             returnFileFormat,
                             nextQuery,
                             "off",
                             nextBundle.originalProvider.getAcceptHeaderString(),
                             nextBundle );
                             
                addToFetchQueue = true;
                
                if(_TRACE)
                {
                    log.trace("RdfFetchController.initialise: created HTTP GET query thread on nextEndpoint="+nextEndpoint+" provider.getKey()="+nextBundle.originalProvider.getKey());
                }
            }
            else if( nextBundle.originalProvider.getEndpointMethod().equals( ProviderImpl.getProviderNoCommunication() ) )
            {
                if(_TRACE)
                {
                    log.trace("RdfFetchController.initialise: not including no communication provider in fetch queue or creating thread");
                }
                
                addToFetchQueue = false;
            }
            else
            {
                addToFetchQueue = false;
                
                log.warn( "RdfFetchController.initialise: endpointMethod did not match any known values. Not adding endpointMethod="+nextBundle.originalProvider.getEndpointMethod().stringValue() + " providerConfig="+nextBundle.originalProvider.getKey().stringValue() );
            }
            
            
            if( addToFetchQueue )
            {
                fetchThreadGroup.add( nextThread );
            }
            else
            {
                if( nextThread != null )
                {
                    uncalledThreads.add( nextThread );
                }
                
                if( _DEBUG )
                {
                    log.debug( "RdfFetchController.initialise: not adding bundle/provider to the fetch group for some reason" );
                }
            }
        }
        
        if( _DEBUG )
        {
            final long end = System.currentTimeMillis();
            
            log.debug( "RdfFetchController.initialise: numberOfThreads="+fetchThreadGroup.size() );
            
            log.debug( String.format( "%s: timing=%10d", "RdfFetchController.initialise", ( end - start ) ) );
        }
    }
    
    public void fetchRdfForQueries() throws InterruptedException
    {
        fetchRdfForQueries(fetchThreadGroup);
    }
    
    public void fetchRdfForQueries(Collection<RdfFetcherQueryRunnable> fetchThreads) throws InterruptedException
    {
        final long start = System.currentTimeMillis();
        
        // TODO: FIXME: Should be using this to recover from errors if possible when there is an alternative endpoint available
        Collection<RdfFetcherQueryRunnable> temporaryEndpointBlacklist = new HashSet<RdfFetcherQueryRunnable>();
        
        for( RdfFetcherQueryRunnable nextThread : fetchThreads )
        {
            if( _DEBUG )
            {
                log.debug( "RdfFetchController.fetchRdfForQuery: about to start thread name="+nextThread.getName() );
            }
            
            nextThread.start();
        }
        
        if( _DEBUG )
        {
            log.debug( "RdfFetchController.fetchRdfForQuery: about to sleep to let other threads do some work" );
        }
        
        // do some very minor waiting to let the other threads start to do some work
        try
        {
            Thread.sleep( 10 );
        }
        catch ( InterruptedException ie )
        {
            log.fatal( "RdfFetchController.fetchRdfForQuery: Thread interruption occurred" );
            throw ie;
        }
        
        for( RdfFetcherQueryRunnable nextThread : fetchThreadGroup )
        {
            try
            {
                // effectively attempt to join each of the threads, this loop will complete when they are all completed
                nextThread.join();
            }
            catch( InterruptedException ie )
            {
                log.error( "RdfFetchController.fetchRdfForQuery: caught interrupted exception message="+ie.getMessage() );
                throw ie;
            }
        }
        
        // This loop is a safety check, although it doesn't actually fallover if something is wrong
        for( RdfFetcherQueryRunnable nextThread : fetchThreadGroup )
        {
            if( !nextThread.completed )
            {
                log.fatal( "RdfFetchController.fetchRdfForQuery: Thread not completed properly name="+nextThread.getName() );
            }
        }
        
        for( RdfFetcherQueryRunnable nextThread : fetchThreadGroup )
        {
            if( !nextThread.wasSuccessful )
            {
                if( nextThread.lastException != null )
                {
                    log.error( "RdfFetchController.fetchRdfForQuery: endpoint="+nextThread.endpointUrl+ " message=" + nextThread.lastException.getMessage() );
                    
                    URI queryKey = null;
                    
                    if(nextThread.originalQueryBundle != null && nextThread.originalQueryBundle.getQueryType() != null)
                    {
                        queryKey = nextThread.originalQueryBundle.getQueryType().getKey();
                    }
                    
                    nextThread.resultDebugString = "Error occured with querykey="+queryKey+" on endpoint="+nextThread.getEndpointUrl()+" query=" +nextThread.getQuery();
                    
                    errorResults.add( nextThread );
                }
            }
            else
            {
                String nextResult = nextThread.rawResult;
                
                String convertedResult = (String)SparqlQueryCreator.normaliseByStage(
                    NormalisationRuleImpl.getRdfruleStageBeforeResultsImport(),
                    nextResult, 
                    Settings.getSortedRulesForProvider( 
                        nextThread.originalQueryBundle.getProvider(), 
                        Settings.HIGHEST_ORDER_FIRST ), 
                    sortedIncludedProfiles );
                
                nextThread.normalisedResult = convertedResult;
                
                if( _DEBUG )
                {
                    log.debug( "RdfFetchController.fetchRdfForQuery: Query successful endpoint="+nextThread.originalQueryBundle.queryEndpoint );
                    
                    if( _TRACE )
                    {
                        log.trace( "RdfFetchController.fetchRdfForQuery: Query successful nextResult="+nextResult +" convertedResult="+convertedResult);
                    }
                }
                
                URI queryKey = null;
                
                if(nextThread.originalQueryBundle != null && nextThread.originalQueryBundle.getQueryType() != null)
                {
                    queryKey = nextThread.originalQueryBundle.getQueryType().getKey();
                }
                
                nextThread.resultDebugString = "Query queryKey="+queryKey+" successful on endpoint="+nextThread.originalQueryBundle.getQueryEndpoint()+" query=" + nextThread.originalQueryBundle.getQuery();
                
                successfulResults.add( nextThread );
            }
        } // end for(RdfFetcherSparqlQueryRunnable nextThread : fetchThreadGroup)
        
        
        if( _INFO )
        {
            final long end = System.currentTimeMillis();
            
            log.info( String.format( "%s: timing=%10d", "RdfFetchController.fetchRdfForQueries", ( end - start ) ) );
        }
    }
    
    public Collection<RdfFetcherQueryRunnable> getResults()
    {
        Collection<RdfFetcherQueryRunnable> results = new HashSet<RdfFetcherQueryRunnable>();
        
        results.addAll( successfulResults );
        results.addAll( errorResults );
        
        return results;
    }
}
