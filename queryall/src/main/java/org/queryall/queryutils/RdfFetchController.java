package org.queryall.queryutils;

import org.queryall.api.HttpProvider;
import org.queryall.api.Profile;
import org.queryall.api.Provider;
import org.queryall.api.QueryType;
import org.queryall.api.SparqlProvider;
import org.queryall.blacklist.*;
import org.queryall.impl.*;
import org.queryall.helpers.*;

import org.apache.log4j.Logger;

import org.openrdf.model.URI;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RdfFetchController
{
    private static final Logger log = Logger.getLogger( RdfFetchController.class.getName() );
    private static final boolean _TRACE = log.isTraceEnabled();
    private static final boolean _DEBUG = log.isDebugEnabled();
    private static final boolean _INFO = log.isInfoEnabled();
    
    private Collection<RdfFetcherQueryRunnable> errorResults = new HashSet<RdfFetcherQueryRunnable>( 10 );
    private Collection<RdfFetcherQueryRunnable> successfulResults = new HashSet<RdfFetcherQueryRunnable>( 10 );
    private Collection<RdfFetcherQueryRunnable> uncalledThreads = new HashSet<RdfFetcherQueryRunnable>( 4 );
    private Collection<RdfFetcherQueryRunnable> fetchThreadGroup = new HashSet<RdfFetcherQueryRunnable>( 20 );
    
    private Collection<QueryBundle> queryBundles = null;
    
    private String queryString;
    private List<Profile> sortedIncludedProfiles;
    private boolean useDefaultProviders = true;
    private String realHostName;
    private int pageOffset;
    private String returnFileFormat;
    private boolean includeNonPagedQueries = true;
    private Settings localSettings;
    private BlacklistController localBlacklistController;
	private boolean namespaceNotRecognised = false;
    
    public RdfFetchController(Settings settingsClass, BlacklistController localBlacklistController, Collection<QueryBundle> nextQueryBundles)
    {
        localSettings = settingsClass;
        this.localBlacklistController = localBlacklistController;
        queryBundles = nextQueryBundles;
        
        initialise();
    }
    
    public RdfFetchController( Settings settingsClass, BlacklistController localBlacklistController, String nextQueryString, List<Profile> nextIncludedSortedProfiles, boolean nextUseDefaultProviders, String nextRealHostName, int nextPageOffset, String nextReturnFileFormat )
    {
        localSettings  = settingsClass;
        this.localBlacklistController = localBlacklistController;        
        queryString = nextQueryString;
        sortedIncludedProfiles = nextIncludedSortedProfiles;
        useDefaultProviders = nextUseDefaultProviders;
        realHostName = nextRealHostName;

        if( nextPageOffset < 1 )
        {
            log.warn( "RdfFetchController.initialise: correcting pageoffset to 1, previous pageOffset="+nextPageOffset );
            
            pageOffset = 1;
        }
        else
        {
            pageOffset = nextPageOffset;
        }
        
        includeNonPagedQueries = (pageOffset == 1);
        returnFileFormat = nextReturnFileFormat;
        
        initialise();
    }
    
    public Collection<Provider> getAllUsedProviders()
    {
        Collection<Provider> results = new LinkedList<Provider>();
        
        for(QueryBundle nextQueryBundle : queryBundles)
        {
            results.add(nextQueryBundle.getOriginalProvider());
        }
        
        return results;
    }
    
    private void addQueryBundles(Collection<QueryBundle> queryBundles)
    {
        this.queryBundles.addAll(queryBundles);
    }
    
    public Collection<QueryBundle> getQueryBundles()
    {
        return queryBundles;
    }
    
    public boolean queryKnown()
    {
        if(queryBundles.size() == 0)
        {
            return false;
        }

        for(QueryBundle nextQueryBundle : queryBundles)
        {
        	// if the query type for this query bundle is not a dummy query, return true
            if(!nextQueryBundle.getQueryType().getIsDummyQueryType())
            {
            	if(_DEBUG)
            		log.debug("RdfFetchController.queryKnown: returning true after looking at nextQueryBundle.getQueryType()="+nextQueryBundle.getQueryType().getKey().stringValue());

            	return true;
            }
        }
        
        if(_DEBUG)
        	log.debug("RdfFetchController.queryKnown: returning false at end of method");
        
        return false;
    }
    
    public boolean anyNamespaceNotRecognised()
    {
    	return namespaceNotRecognised;
    }
    
    private void initialise()
    {
        final long start = System.currentTimeMillis();
        
        if(queryBundles == null)
        {
            queryBundles = new LinkedList<QueryBundle>();
            
            Collection<QueryType> allCustomQueries = localSettings.getQueryTypesMatchingQueryString( queryString, sortedIncludedProfiles );
            
            // TODO: figure out how to also get back the NamespaceEntry objects that matched so we can log this information with the statistics for this query
            if( _DEBUG )
            {
                log.debug("RdfFetchController.initialise: found "+allCustomQueries.size()+" matching queries");
            }
            
            // TODO: should we do classification in the results based on the QueryType that generated the particular subset of QueryBundles to make it easier to distinguish them
            for( QueryType nextQueryType : allCustomQueries )
            {
                // Non-paged queries are a special case. The caller decides whether 
                // they want to use non-paged queries, for example, they may say no
                // if they have decided that they need only extra results from paged 
                // queries
                if(!includeNonPagedQueries && !nextQueryType.getIsPageable())
                {
                    if(_INFO)
                    {
                        log.info("RdfFetchController: not using nonPagedQuery="+nextQueryType.getKey());
                    }
                    
                    continue;
                }
                
                Collection<Provider> chosenProviders = new HashSet<Provider>();
                
                if( !nextQueryType.getIsNamespaceSpecific() )
                {
                    chosenProviders.addAll(getProvidersForQueryNonNamespaceSpecific(nextQueryType));
                }
                else
                {
                    chosenProviders.addAll(getProvidersForQueryNamespaceSpecific(nextQueryType));
                }
                
                if( nextQueryType.getIncludeDefaults() && useDefaultProviders )
                {
                    if( _DEBUG )
                    {
                        log.debug( "RdfFetchController.initialise: including defaults for nextQueryType.title="+nextQueryType.getTitle()+" nextQueryType.getKey()="+nextQueryType.getKey() );
                    }
                    
                    chosenProviders.addAll(localSettings.getDefaultProviders(nextQueryType));
                }
                
                if( _DEBUG )
                {
                    int QueryTypeProvidersSize = chosenProviders.size();
                    
                    if( QueryTypeProvidersSize  > 0 )
                    {
                        log.debug( "RdfFetchController.initialise: found "+QueryTypeProvidersSize +" providers for nextQueryType.getKey()="+nextQueryType.getKey() );
                        
                        if( _TRACE )
                        {
                            for( Provider nextQueryTypeProvider : chosenProviders )
                            {
                                log.trace( "RdfFetchController.initialise: nextQueryTypeProvider="+nextQueryTypeProvider.getKey() );
                            }
                        }
                    }
                    else if( _TRACE && QueryTypeProvidersSize == 0 )
                    {
                        log.trace( "RdfFetchController.initialise: found NO suitable providers for custom type="+nextQueryType.getKey() );
                    }
                } // end if(_DEBUG}
                
                if(_TRACE)
                {
                    log.trace( "RdfFetchController.initialise: about to generate query bundles for query type and providers");
                }
                
                Collection<QueryBundle> queryBundlesForQueryType = this.generateQueryBundlesForQueryTypeAndProviders(nextQueryType,
                        chosenProviders, localSettings.getBooleanPropertyFromConfig("useAllEndpointsForEachProvider", true), localSettings);
                
                if(_DEBUG)
                {
                    log.debug( "RdfFetchController.initialise: queryBundlesForQueryType.size()="+queryBundlesForQueryType.size());
                }
                
                if(_TRACE)
                {
                    for(QueryBundle nextQueryBundleForQueryType : queryBundlesForQueryType)
                    {
                        log.trace( "RdfFetchController.initialise: nextQueryBndleForQueryType="+nextQueryBundleForQueryType.toString());
                    }
                }
                
                this.addQueryBundles(queryBundlesForQueryType);

                // if there are still no query bundles check for the non-namespace specific version of the query type to flag any instances of the namespace not being recognised
                if(queryBundlesForQueryType.size() == 0)
                {
                	if(nextQueryType.getIsNamespaceSpecific() && getProvidersForQueryNonNamespaceSpecific(nextQueryType).size() > 0)
                	{
                		namespaceNotRecognised = true;
                	}
                }
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
        }
        if(_INFO)
        {
            if(queryBundles.size()== 0)
            {
                log.info("RdfFetchController.initialise: no query bundles given or created");
            }
        }
        
        // queryBundles = multiProviderQueryBundles;
        // return multiProviderQueryBundles;
        
        this.setFetchThreadGroup(this.generateFetchThreadsFromQueryBundles(this.queryBundles, localSettings.getIntPropertyFromConfig("pageoffsetIndividualQueryLimit", 0)));
        
        if( _DEBUG )
        {
            final long end = System.currentTimeMillis();
            
            log.debug( "RdfFetchController.initialise: numberOfThreads="+getFetchThreadGroup().size() );
            
            log.debug( String.format( "%s: timing=%10d", "RdfFetchController.initialise", ( end - start ) ) );
        }
    }

    /**
     * @param nextQueryType
     * @param chosenProviders
     */
    private Collection<QueryBundle> generateQueryBundlesForQueryTypeAndProviders(
            QueryType nextQueryType, Collection<Provider> chosenProviders, boolean useAllEndpointsForEachProvider, Settings localSettings)
    {
        Collection<QueryBundle> results = new HashSet<QueryBundle>();
        
        if(_DEBUG)
        	log.debug("RdfFetchController.generateQueryBundlesForQueryTypeAndProviders: nextQueryType="+nextQueryType.getKey().stringValue()+" chosenProviders.size="+chosenProviders.size());

        for( Provider nextProvider : chosenProviders )
        {
        	boolean isHttpWithNoEndpoint = nextProvider instanceof HttpProvider && !((HttpProvider)nextProvider).hasEndpointUrl();
        	
        	if( nextProvider.getEndpointMethod().equals( ProviderImpl.getProviderNoCommunication() ) && isHttpWithNoEndpoint)
            {
                String nextStaticRdfXmlString = "";
                
                for( URI nextCustomInclude : nextQueryType.getSemanticallyLinkedQueryTypes() )
                {
                    // pick out all of the QueryType's which have been delegated for this particular query as static includes
                    Collection<QueryType> allCustomRdfXmlIncludeTypes = localSettings.getQueryTypesByUri( nextCustomInclude );
                    
                    for( QueryType nextCustomIncludeType : allCustomRdfXmlIncludeTypes )
                    {
                        Map<String, String> attributeList = QueryCreator.getAttributeListFor( nextCustomIncludeType, nextProvider, queryString, "", realHostName , pageOffset, localSettings);
                        
                        // then also create the statically defined rdf/xml string to go with this query based on the current attributes, we assume that both queries have been intelligently put into the configuration file so that they have an equivalent number of arguments as ${input_1} etc, in them.
                        // There is no general solution for determining how these should work other than naming them as ${namespace} and ${identifier} and ${searchTerm}, but these can be worked around by only offering compatible services as alternatives with the static rdf/xml portions
                        nextStaticRdfXmlString += QueryCreator.createStaticRdfXmlString( nextQueryType, nextCustomIncludeType, nextProvider, attributeList, sortedIncludedProfiles , localSettings.getBooleanPropertyFromConfig("recogniseImplicitRdfRuleInclusions", true) , localSettings.getBooleanPropertyFromConfig("includeNonProfileMatchedRdfRules", true), localSettings);
                    }
                }
                
                QueryBundle nextProviderQueryBundle = new QueryBundle();
                
                nextProviderQueryBundle.setStaticRdfXmlString(nextStaticRdfXmlString);
                nextProviderQueryBundle.setProvider(nextProvider);
                nextProviderQueryBundle.setQueryType(nextQueryType);
                nextProviderQueryBundle.setRelevantProfiles(sortedIncludedProfiles);
                
                results.add( nextProviderQueryBundle );
            }
//            // check if there is an endpoint, as we allow for providers which are really placeholders for static RDF/XML additions, and they are configured without endpoint URL's and with NO_COMMUNICATION
//            //else if( nextProvider.hasEndpointUrl() )
//        	else if(nextProvider instanceof HttpProvider)
        	else
            {
        		HttpProvider nextHttpProvider = (HttpProvider)nextProvider;
                Map<String, String> attributeList = new HashMap<String, String>();
                
                Collection<String> nextEndpointUrls = ListUtils.randomiseListLayout(nextHttpProvider.getEndpointUrls());
                
                Map<String, Map<String, String>> replacedEndpoints = new HashMap<String, Map<String, String>>();
                
                for( String nextEndpoint : nextEndpointUrls )
                {
                    String replacedEndpoint = nextEndpoint
                                        .replace( Constants.TEMPLATE_REAL_HOST_NAME, realHostName )
                                        .replace( Constants.TEMPLATE_DEFAULT_SEPARATOR, localSettings.getStringPropertyFromConfig("separator", ":") )
                                        .replace( Constants.TEMPLATE_OFFSET, String.valueOf(pageOffset) );
                    
                    // perform the ${input_1} ${urlEncoded_input_1} ${xmlEncoded_input_1} etc replacements on nextEndpoint before using it in the attribute list
                    replacedEndpoint = QueryCreator.matchAndReplaceInputVariablesForQueryType( nextQueryType, queryString, replacedEndpoint, new ArrayList<String>() );
                    
                    attributeList = QueryCreator.getAttributeListFor( nextQueryType, nextProvider, queryString, replacedEndpoint, realHostName, pageOffset, localSettings);
                    
                    // This step is needed in order to replace endpointSpecific related template elements on the provider URL
                    replacedEndpoint = QueryCreator.replaceAttributesOnEndpointUrl( replacedEndpoint, nextQueryType, nextProvider, attributeList, sortedIncludedProfiles , localSettings.getBooleanPropertyFromConfig("recogniseImplicitRdfRuleInclusions", true) , localSettings.getBooleanPropertyFromConfig("includeNonProfileMatchedRdfRules", true), localSettings);
                    
                    String nextEndpointQuery = QueryCreator.createQuery( nextQueryType, nextProvider, attributeList, sortedIncludedProfiles , localSettings.getBooleanPropertyFromConfig("recogniseImplicitRdfRuleInclusions", true) , localSettings.getBooleanPropertyFromConfig("includeNonProfileMatchedRdfRules", true), localSettings);
                    
                    // replace the query on the endpoint URL if necessary
                    replacedEndpoint = replacedEndpoint.replace(Constants.TEMPLATE_PERCENT_ENCODED_ENDPOINT_QUERY, StringUtils.percentEncode(nextEndpointQuery));
                
                	Map<String, String> newList = new HashMap<String,String>();
                	newList.put(replacedEndpoint, nextEndpointQuery);

                	if(replacedEndpoints.containsKey(nextEndpoint))
                    {
                    	replacedEndpoints.get(nextEndpoint).put(replacedEndpoint, nextEndpointQuery);
                    }
                    else
                    {
                    	replacedEndpoints.put(nextEndpoint, newList);
                    }
                }
                
                String nextStaticRdfXmlString = "";
                
                for( URI nextCustomInclude : nextQueryType.getSemanticallyLinkedQueryTypes() )
                {
                    // pick out all of the QueryType's which have been delegated for this particular query as static includes
                    Collection<QueryType> allCustomRdfXmlIncludeTypes = localSettings.getQueryTypesByUri( nextCustomInclude );
                    
                    if( allCustomRdfXmlIncludeTypes.size()  == 0 )
                    {
                        log.warn( "RdfFetchController: no included queries found for nextCustomInclude="+nextCustomInclude );
                    }
                    
                    for( QueryType nextCustomIncludeType : allCustomRdfXmlIncludeTypes )
                    {
                        // then also create the statically defined rdf/xml string to go with this query based on the current attributes, we assume that both queries have been intelligently put into the configuration file so that they have an equivalent number of arguments as ${input_1} etc, in them.
                        // There is no general solution for determining how these should work other than naming them as ${namespace} and ${identifier} and ${searchTerm}, but these can be worked around by only offering compatible services as alternatives with the static rdf/xml portions
                        nextStaticRdfXmlString += QueryCreator.createStaticRdfXmlString( nextQueryType, nextCustomIncludeType, nextProvider, attributeList, sortedIncludedProfiles , localSettings.getBooleanPropertyFromConfig("recogniseImplicitRdfRuleInclusions", true) , localSettings.getBooleanPropertyFromConfig("includeNonProfileMatchedRdfRules", true), localSettings);
                    }
                }
                
                if(replacedEndpoints.size()>0)
                {
                	for(String nextEndpoint : replacedEndpoints.keySet())
                	{
                		Map<String, String> endpointEntries = replacedEndpoints.get(nextEndpoint);
                		
                		for(String replacedEndpoint : endpointEntries.keySet())
                		{
			                QueryBundle nextProviderQueryBundle = new QueryBundle();
			                
			                nextProviderQueryBundle.setQuery(endpointEntries.get(replacedEndpoint));
			                nextProviderQueryBundle.setStaticRdfXmlString(nextStaticRdfXmlString);
			                nextProviderQueryBundle.setQueryEndpoint(replacedEndpoint);
			                nextProviderQueryBundle.setOriginalEndpointString(nextEndpoint);
			                nextProviderQueryBundle.setOriginalProvider(nextProvider);
			                nextProviderQueryBundle.setQueryType(nextQueryType);
			                nextProviderQueryBundle.setRelevantProfiles(sortedIncludedProfiles);
			                
			                // Then test whether the endpoint is blacklisted
			                if(nextProvider.getEndpointMethod().equals(ProviderImpl.getProviderNoCommunication()) || !localBlacklistController.isUrlBlacklisted(replacedEndpoint))
			                {
			                	results.add(nextProviderQueryBundle);
			                }
			                else
			                {
			                	log.warn("Not including provider because it is not no-communication and is a blacklisted url nextProvider.getKey()="+nextProvider.getKey());
			                }
                		}
		                // go to next provider if we are not told to use all of the endpoints for the provider
		                if( endpointEntries.size() > 0 && !useAllEndpointsForEachProvider)
		                {
		                    break;
		                }
                	}
                }
                else
                {
					QueryBundle nextProviderQueryBundle = new QueryBundle();
					  
					nextProviderQueryBundle.setStaticRdfXmlString(nextStaticRdfXmlString);
					nextProviderQueryBundle.setProvider(nextProvider);
					nextProviderQueryBundle.setQueryType(nextQueryType);
					nextProviderQueryBundle.setRelevantProfiles(sortedIncludedProfiles);
					  
					results.add( nextProviderQueryBundle );

                }
            }
                // end for(String nextEndpoint : nextProvider.endpointUrls)
//            } // end if(nextProvider.hasEndpointUrl())
//            else
//            {
//                log.warn("RdfFetchController.generateQueryBundlesForQueryTypeAndProviders: no endpoint URL's found for non-nocommunication provider.getKey()="+nextProvider.getKey());
//            }
        } // end for(Provider nextProvider : QueryTypeProviders)
        
        if(_DEBUG)
        	log.debug("RdfFetchController.generateQueryBundlesForQueryTypeAndProviders: results.size()="+results.size());

        return results;
    }

    private Collection<RdfFetcherQueryRunnable> generateFetchThreadsFromQueryBundles(Collection<QueryBundle> nextQueryBundles, int pageoffsetIndividualQueryLimit)
    {
        Collection<RdfFetcherQueryRunnable> results = new LinkedList<RdfFetcherQueryRunnable>();
        
        for( QueryBundle nextBundle : nextQueryBundles )
        {
            String nextEndpoint = nextBundle.getQueryEndpoint();
            String nextQuery = nextBundle.getQuery();
            
            if( _DEBUG )
            {
                log.debug( "RdfFetchController.generateFetchThreadsFromQueryBundles: About to create a thread for query on endpoint="+nextEndpoint+" query=" + nextQuery + " provider="+nextBundle.getOriginalProvider().getKey());
            }
            
            RdfFetcherQueryRunnable nextThread = null;
            
            boolean addToFetchQueue = false;
            
            if( nextBundle.getOriginalProvider().getEndpointMethod().equals( HttpProviderImpl.getProviderHttpPostSparql() ) )
            {
                nextThread = new RdfFetcherSparqlQueryRunnable( nextEndpoint,
                			 ((SparqlProvider)nextBundle.getOriginalProvider()).getSparqlGraphUri(),
                             returnFileFormat,
                             nextQuery,
                             "off",
                             ((HttpProvider)nextBundle.getOriginalProvider()).getAcceptHeaderString(),
                             pageoffsetIndividualQueryLimit,
                             localSettings,
                             localBlacklistController,
                             nextBundle );
                             
                addToFetchQueue = true;
                
                if(_TRACE)
                {
                    log.trace("RdfFetchController.generateFetchThreadsFromQueryBundles: created HTTP POST SPARQL query thread on nextEndpoint="+nextEndpoint+" provider.getKey()="+nextBundle.getOriginalProvider().getKey());
                }
            }
            else if( nextBundle.getOriginalProvider().getEndpointMethod().equals( HttpProviderImpl.getProviderHttpGetUrl() ) )
            {
                nextThread = new RdfFetcherUriQueryRunnable( nextEndpoint,
                             returnFileFormat,
                             nextQuery,
                             "off",
                             ((HttpProvider)nextBundle.getOriginalProvider()).getAcceptHeaderString(),
                             localSettings,
                             localBlacklistController,
                             nextBundle );
                             
                addToFetchQueue = true;
                
                if(_TRACE)
                {
                    log.trace("RdfFetchController.generateFetchThreadsFromQueryBundles: created HTTP GET query thread on nextEndpoint="+nextEndpoint+" provider.getKey()="+nextBundle.getOriginalProvider().getKey());
                }
            }
            else if( nextBundle.getOriginalProvider().getEndpointMethod().equals( ProviderImpl.getProviderNoCommunication() ) )
            {
                if(_TRACE)
                {
                    log.trace("RdfFetchController.generateFetchThreadsFromQueryBundles: not including no communication provider in fetch queue or creating thread");
                }
                
                addToFetchQueue = false;
            }
            else
            {
                addToFetchQueue = false;
                
                log.warn( "RdfFetchController.generateFetchThreadsFromQueryBundles: endpointMethod did not match any known values. Not adding endpointMethod="+nextBundle.getOriginalProvider().getEndpointMethod().stringValue() + " providerConfig="+nextBundle.getOriginalProvider().getKey().stringValue() );
            }
            
            
            if( addToFetchQueue )
            {
                results.add( nextThread );
            }
            else
            {
//                if( nextThread != null )
//                {
//                    getUncalledThreads().add( nextThread );
//                }
                
                if( _DEBUG )
                {
                    log.debug( "RdfFetchController.generateFetchThreadsFromQueryBundles: not adding bundle/provider to the fetch group for some reason" );
                }
            }
        }
        
        return results;
    }

    private Collection<Provider> getProvidersForQueryNamespaceSpecific(QueryType nextQueryType)
    {
        Collection<Provider> results = new LinkedList<Provider>();
        
        List<String> queryStringMatches = nextQueryType.matchesForQueryString( queryString );
        
        int queryStringMatchesSize = queryStringMatches.size();
        
        // Collection<String> nextQueryNamespacePrefixes = new HashSet<String>();
        Collection<Collection<URI>> nextQueryNamespaceUris = new HashSet<Collection<URI>>();
        
        for( int nextNamespaceInputIndex : nextQueryType.getNamespaceInputIndexes() )
        {
            if( queryStringMatchesSize  >= nextNamespaceInputIndex && nextNamespaceInputIndex > 0 )
            {
                String nextTitle = queryStringMatches.get( nextNamespaceInputIndex-1 );
                
                Collection<URI> nextUriFromTitleNamespaceList = localSettings.getNamespaceUrisForTitle( nextTitle );
                
                if( nextUriFromTitleNamespaceList != null )
                {
                    nextQueryNamespaceUris.add( nextUriFromTitleNamespaceList );
                }
                else
                {
                    log.warn( "RdfFetchController.getProvidersForQueryNamespaceSpecific: did not find any namespace URIs for nextTitle="+nextTitle + " nextQueryType.getKey()="+nextQueryType.getKey());
                }
            }
            else
            {
                log.error( "RdfFetchController.getProvidersForQueryNamespaceSpecific: Could not match the namespace because the input index was invalid nextNamespaceInputIndex="+nextNamespaceInputIndex+" queryStringMatches.size()="+queryStringMatches.size() );
                
                throw new RuntimeException( "Could not match the namespace because the input index was invalid nextNamespaceInputIndex="+nextNamespaceInputIndex+" queryStringMatches.size()="+queryStringMatches.size() );
            }
        }
        
        if( _DEBUG )
        {
            // log.debug( "RdfFetchController.getProvidersForQueryNamespaceSpecific: nextQueryNamespacePrefixes="+nextQueryNamespacePrefixes );
            log.debug( "RdfFetchController.getProvidersForQueryNamespaceSpecific: nextQueryNamespaceUris="+nextQueryNamespaceUris );
        }
        
        if( nextQueryType.handlesNamespaceUris( nextQueryNamespaceUris ) )
        {
            if( _DEBUG )
            {
                log.debug( "RdfFetchController.getProvidersForQueryNamespaceSpecific: confirmed to handle namespaces nextQueryType.getKey()="+nextQueryType.getKey() +" nextQueryNamespaceUris="+nextQueryNamespaceUris );
            }
            
            Collection<Provider> namespaceSpecificProviders = localSettings.getProvidersForQueryTypeForNamespaceUris( nextQueryType.getKey(), nextQueryNamespaceUris, nextQueryType.getNamespaceMatchMethod() );
            
            for( Provider nextNamespaceSpecificProvider : namespaceSpecificProviders )
            {
                if( _TRACE )
                {
                    log.trace( "RdfFetchController.getProvidersForQueryNamespaceSpecific: nextQueryType.isNamespaceSpecific nextNamespaceSpecificProvider="+nextNamespaceSpecificProvider.getKey() );
                }
                
                if( nextNamespaceSpecificProvider.isUsedWithProfileList( sortedIncludedProfiles, localSettings.getBooleanPropertyFromConfig("recogniseImplicitProviderInclusions", true), localSettings.getBooleanPropertyFromConfig("includeNonProfileMatchedProviders", true) ) )
                {
                    if( _DEBUG )
                    {
                        log.debug( "RdfFetchController.getProvidersForQueryNamespaceSpecific: profileList suitable for nextNamespaceSpecificProvider.getKey()="+nextNamespaceSpecificProvider.getKey()+" queryString="+queryString );
                    }
                    
                    results.add( nextNamespaceSpecificProvider );
                }
            }
        }
        
        return results;
    }

    private Collection<Provider> getProvidersForQueryNonNamespaceSpecific(
            QueryType nextQueryType)
    {
        Collection<Provider> results = new LinkedList<Provider>();
        
        // if we aren't specific to namespace we simply find all providers for this type of custom query
        Collection<Provider> allProviders = localSettings.getProvidersForQueryType( nextQueryType.getKey() );
        
        for( Provider nextAllProvider : allProviders )
        {
            if( _DEBUG )
            {
                log.debug( "RdfFetchController.getProvidersForQueryNonNamespaceSpecific: !nextQueryType.isNamespaceSpecific nextAllProvider="+nextAllProvider.toString() );
            }
            
            if( nextAllProvider.isUsedWithProfileList( sortedIncludedProfiles, localSettings.getBooleanPropertyFromConfig("recogniseImplicitProviderInclusions", true), localSettings.getBooleanPropertyFromConfig("includeNonProfileMatchedProviders", true) ) )
            {
                if( _DEBUG )
                {
                    log.debug( "RdfFetchController.getProvidersForQueryNonNamespaceSpecific: profileList suitable for nextAllProvider.getKey()="+nextAllProvider.getKey()+" queryString="+queryString );
                }
                
                results.add( nextAllProvider );
            }
        }
        
        return results;
    }
    
    public void fetchRdfForQueries() throws InterruptedException
    {
        fetchRdfForQueries(getFetchThreadGroup());
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
                log.debug( "RdfFetchController.fetchRdfForQueries: about to start thread name="+nextThread.getName() );
            }
            
            nextThread.start();
        }
        
        if( _DEBUG )
        {
            log.debug( "RdfFetchController.fetchRdfForQueries: about to sleep to let other threads do some work" );
        }
        
        // do some very minor waiting to let the other threads start to do some work
        try
        {
            Thread.sleep( 2 );
        }
        catch ( InterruptedException ie )
        {
            log.fatal( "RdfFetchController.fetchRdfForQueries: Thread interruption occurred" );
            throw ie;
        }
        
        for( RdfFetcherQueryRunnable nextThread : getFetchThreadGroup() )
        {
            try
            {
                // effectively attempt to join each of the threads, this loop will complete when they are all completed
                nextThread.join();
            }
            catch( InterruptedException ie )
            {
                log.error( "RdfFetchController.fetchRdfForQueries: caught interrupted exception message="+ie.getMessage() );
                throw ie;
            }
        }
        
        // This loop is a safety check, although it doesn't actually fallover if something is wrong
        for( RdfFetcherQueryRunnable nextThread : getFetchThreadGroup() )
        {
            if( !nextThread.getCompleted() )
            {
                log.fatal( "RdfFetchController.fetchRdfForQueries: Thread not completed properly name="+nextThread.getName() );
            }
        }
        
        for( RdfFetcherQueryRunnable nextThread : getFetchThreadGroup() )
        {
            if( !nextThread.getWasSuccessful() )
            {
                if( nextThread.getLastException() != null )
                {
                    log.error( "RdfFetchController.fetchRdfForQueries: endpoint="+nextThread.getEndpointUrl()+ " message=" + nextThread.getLastException().getMessage() );
                    
                    URI queryKey = null;
                    
                    if(nextThread.getOriginalQueryBundle() != null && nextThread.getOriginalQueryBundle().getQueryType() != null)
                    {
                        queryKey = nextThread.getOriginalQueryBundle().getQueryType().getKey();
                    }
                    
                    nextThread.setResultDebugString("Error occured with querykey="+queryKey+" on endpoint="+nextThread.getEndpointUrl()+" query=" +nextThread.getQuery());
                    
                    getErrorResults().add( nextThread );
                }
            }
            else
            {
                String nextResult = nextThread.getRawResult();
                
                String convertedResult = (String)QueryCreator.normaliseByStage(
                    NormalisationRuleImpl.getRdfruleStageBeforeResultsImport(),
                    nextResult, 
                    localSettings.getNormalisationRulesForUris( 
                        nextThread.getOriginalQueryBundle().getProvider().getNormalisationUris(), 
                        Constants.HIGHEST_ORDER_FIRST ), 
                    sortedIncludedProfiles, localSettings.getBooleanPropertyFromConfig("recogniseImplicitRdfRuleInclusions", true), localSettings.getBooleanPropertyFromConfig("includeNonProfileMatchedRdfRules", true) );
                
                nextThread.setNormalisedResult(convertedResult);
                
                if( _DEBUG )
                {
                    log.debug( "RdfFetchController.fetchRdfForQueries: Query successful endpoint="+nextThread.getOriginalQueryBundle().getQueryEndpoint() );
                    
                    if( _TRACE )
                    {
                        log.trace( "RdfFetchController.fetchRdfForQueries: Query successful nextResult="+nextResult +" convertedResult="+convertedResult);
                    }
                }
                
                URI queryKey = null;
                
                if(nextThread.getOriginalQueryBundle() != null && nextThread.getOriginalQueryBundle().getQueryType() != null)
                {
                    queryKey = nextThread.getOriginalQueryBundle().getQueryType().getKey();
                }
                
                nextThread.setResultDebugString("Query queryKey="+queryKey+" successful on endpoint="+nextThread.getOriginalQueryBundle().getQueryEndpoint()+" query=" + nextThread.getOriginalQueryBundle().getQuery());
                
                getSuccessfulResults().add( nextThread );
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
        
        results.addAll( getSuccessfulResults() );
        results.addAll( getErrorResults() );
        
        return results;
    }

    /**
     * @param errorResults the errorResults to set
     */
    public void setErrorResults(Collection<RdfFetcherQueryRunnable> errorResults)
    {
        this.errorResults = errorResults;
    }

    /**
     * @return the errorResults
     */
    public Collection<RdfFetcherQueryRunnable> getErrorResults()
    {
        return errorResults;
    }

    /**
     * @param successfulResults the successfulResults to set
     */
    public void setSuccessfulResults(Collection<RdfFetcherQueryRunnable> successfulResults)
    {
        this.successfulResults = successfulResults;
    }

    /**
     * @return the successfulResults
     */
    public Collection<RdfFetcherQueryRunnable> getSuccessfulResults()
    {
        return successfulResults;
    }

    /**
     * @param uncalledThreads the uncalledThreads to set
     */
    public void setUncalledThreads(Collection<RdfFetcherQueryRunnable> uncalledThreads)
    {
        this.uncalledThreads = uncalledThreads;
    }

    /**
     * @return the uncalledThreads
     */
    public Collection<RdfFetcherQueryRunnable> getUncalledThreads()
    {
        return uncalledThreads;
    }

    /**
     * @param fetchThreadGroup the fetchThreadGroup to set
     */
    public void setFetchThreadGroup(Collection<RdfFetcherQueryRunnable> fetchThreadGroup)
    {
        this.fetchThreadGroup = fetchThreadGroup;
    }

    /**
     * @return the fetchThreadGroup
     */
    public Collection<RdfFetcherQueryRunnable> getFetchThreadGroup()
    {
        return fetchThreadGroup;
    }
}
