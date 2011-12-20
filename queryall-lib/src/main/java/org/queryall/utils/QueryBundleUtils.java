package org.queryall.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.openrdf.model.URI;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.HttpProvider;
import org.queryall.api.provider.Provider;
import org.queryall.api.provider.ProviderSchema;
import org.queryall.api.querytype.InputQueryType;
import org.queryall.api.querytype.OutputQueryType;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.WebappConfig;
import org.queryall.blacklist.BlacklistController;
import org.queryall.exception.QueryAllException;
import org.queryall.query.QueryBundle;
import org.queryall.query.QueryCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class QueryBundleUtils
{
    private static final Logger log = LoggerFactory.getLogger(QueryBundleUtils.class);
    private static final boolean _TRACE = QueryBundleUtils.log.isTraceEnabled();
    private static final boolean _DEBUG = QueryBundleUtils.log.isDebugEnabled();
    private static final boolean _INFO = QueryBundleUtils.log.isInfoEnabled();
    
    /**
     * TODO: Simplify this method and abstract it out into Provider classes
     * 
     * @param nextQueryType
     * @param chosenProviders
     * @param realHostName
     *            TODO
     * @param pageOffset
     *            TODO
     * @param queryParameters
     *            TODO
     * @param sortedIncludedProfiles
     *            TODO
     * @param localBlacklistController
     *            TODO
     * @throws QueryAllException
     */
    public static Collection<QueryBundle> generateQueryBundlesForQueryTypeAndProviders(
            final InputQueryType nextQueryType, final Map<String, Collection<NamespaceEntry>> namespaceInputVariables,
            final Collection<Provider> chosenProviders, final boolean useAllEndpointsForEachProvider,
            final String realHostName, final int pageOffset, final Map<String, String> queryParameters,
            final List<Profile> sortedIncludedProfiles, final QueryAllConfiguration localSettings,
            final BlacklistController localBlacklistController) throws QueryAllException
    {
        final Collection<QueryBundle> results = new HashSet<QueryBundle>();
        
        // Note: We default to converting alternate namespaces to preferred unless it is turned off
        // in the configuration. It can always be turned off for each namespace entry individually
        // FIXME: The current processing code ignores the preferences given by namespace entries,
        // and just uses this setting
        final boolean overallConvertAlternateToPreferredPrefix =
                localSettings.getBooleanProperty(WebappConfig.CONVERT_ALTERNATE_NAMESPACE_PREFIXES_TO_PREFERRED);
        
        if(QueryBundleUtils._DEBUG)
        {
            QueryBundleUtils.log
                    .debug("RdfFetchController.generateQueryBundlesForQueryTypeAndProviders: nextQueryType="
                            + nextQueryType.getKey().stringValue() + " chosenProviders.size=" + chosenProviders.size());
        }
        
        for(final Provider nextProvider : chosenProviders)
        {
            final boolean noCommunicationProvider =
                    nextProvider.getEndpointMethod().equals(ProviderSchema.getProviderNoCommunication());
            
            if(nextProvider instanceof HttpProvider)
            {
                if(QueryBundleUtils._DEBUG)
                {
                    QueryBundleUtils.log.debug("instanceof HttpProvider key=" + nextProvider.getKey());
                }
                final HttpProvider nextHttpProvider = (HttpProvider)nextProvider;
                Map<String, String> attributeList = new HashMap<String, String>();
                
                final List<String> nextEndpointUrls =
                        ListUtils.randomiseCollectionLayout(nextHttpProvider.getEndpointUrls());
                
                final Map<String, Map<String, String>> replacedEndpoints = new HashMap<String, Map<String, String>>();
                
                for(final String nextEndpoint : nextEndpointUrls)
                {
                    String replacedEndpoint =
                            nextEndpoint.replace(Constants.TEMPLATE_REAL_HOST_NAME, realHostName)
                                    .replace(Constants.TEMPLATE_DEFAULT_SEPARATOR, localSettings.getSeparator())
                                    .replace(Constants.TEMPLATE_OFFSET, String.valueOf(pageOffset));
                    
                    // perform the ${input_1} ${urlEncoded_input_1} ${xmlEncoded_input_1} etc
                    // replacements on nextEndpoint before using it in the attribute list
                    replacedEndpoint =
                            QueryCreator.matchAndReplaceInputVariablesForQueryType(nextQueryType, queryParameters,
                                    replacedEndpoint, Constants.EMPTY_STRING_LIST,
                                    overallConvertAlternateToPreferredPrefix, namespaceInputVariables, nextProvider);
                    
                    attributeList =
                            QueryCreator.getAttributeListFor(nextQueryType, nextProvider, queryParameters,
                                    replacedEndpoint, realHostName, pageOffset, localSettings);
                    
                    // This step is needed in order to replace endpointSpecific related template
                    // elements on the provider URL
                    replacedEndpoint =
                            QueryCreator
                                    .replaceAttributesOnEndpointUrl(
                                            replacedEndpoint,
                                            nextQueryType,
                                            nextProvider,
                                            attributeList,
                                            sortedIncludedProfiles,
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
                                            sortedIncludedProfiles,
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
                        QueryBundleUtils.log
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
                                                    sortedIncludedProfiles,
                                                    localSettings
                                                            .getBooleanProperty(WebappConfig.RECOGNISE_IMPLICIT_RDFRULE_INCLUSIONS),
                                                    localSettings
                                                            .getBooleanProperty(WebappConfig.INCLUDE_NON_PROFILE_MATCHED_RDFRULES),
                                                    overallConvertAlternateToPreferredPrefix, localSettings);
                        }
                        else
                        {
                            QueryBundleUtils.log
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
                nextProviderQueryBundle.setRelevantProfiles(sortedIncludedProfiles);
                nextProviderQueryBundle.setQueryallSettings(localSettings);
                
                if(QueryBundleUtils._DEBUG)
                {
                    QueryBundleUtils.log.debug("nextQueryType=" + nextQueryType.getKey().stringValue());
                }
                
                for(final String nextEndpoint : ListUtils.randomiseCollectionLayout(replacedEndpoints.keySet()))
                {
                    final Map<String, String> originalEndpointEntries = replacedEndpoints.get(nextEndpoint);
                    
                    if(QueryBundleUtils._DEBUG)
                    {
                        QueryBundleUtils.log.debug("nextEndpoint=" + nextEndpoint);
                    }
                    
                    for(final String nextReplacedEndpoint : originalEndpointEntries.keySet())
                    {
                        if(QueryBundleUtils._DEBUG)
                        {
                            QueryBundleUtils.log.debug("nextReplacedEndpoint=" + nextReplacedEndpoint);
                        }
                        
                        if(nextReplacedEndpoint == null)
                        {
                            QueryBundleUtils.log.error("nextReplacedEndpoint was null nextEndpoint=" + nextEndpoint
                                    + " nextQueryType=" + nextQueryType + " nextProvider=" + nextProvider);
                            continue;
                        }
                        
                        // Then test whether the endpoint is blacklisted before accepting it
                        if(noCommunicationProvider || !localBlacklistController.isUrlBlacklisted(nextReplacedEndpoint))
                        {
                            if(QueryBundleUtils._DEBUG)
                            {
                                QueryBundleUtils.log.debug("not blacklisted");
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
                                QueryBundleUtils.log
                                        .warn("Not adding an endpoint because we are not told to attempt to use all endpoints, and we have already chosen one");
                            }
                        }
                        else
                        {
                            QueryBundleUtils.log
                                    .warn("Not including provider because it is not no-communication and is a blacklisted url nextProvider.getKey()="
                                            + nextProvider.getKey());
                        }
                    }
                    
                    results.add(nextProviderQueryBundle);
                }
            } // end if(nextProvider instanceof HttpProvider)
            else if(noCommunicationProvider)
            {
                if(QueryBundleUtils._DEBUG)
                {
                    QueryBundleUtils.log.debug("endpoint method = noCommunication key=" + nextProvider.getKey());
                }
                
                String nextStaticRdfXmlString = "";
                
                for(final URI nextCustomInclude : nextQueryType.getLinkedQueryTypes())
                {
                    // pick out all of the QueryType's which have been delegated for this particular
                    // query as static includes
                    final QueryType nextCustomIncludeType = localSettings.getAllQueryTypes().get(nextCustomInclude);
                    
                    if(nextCustomIncludeType == null)
                    {
                        QueryBundleUtils.log
                                .warn("Attempted to include an unknown include type using the URI nextCustomInclude="
                                        + nextCustomInclude.stringValue());
                    }
                    else
                    {
                        final Map<String, String> attributeList =
                                QueryCreator.getAttributeListFor(nextCustomIncludeType, nextProvider, queryParameters,
                                        "", realHostName, pageOffset, localSettings);
                        
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
                                                    sortedIncludedProfiles,
                                                    localSettings
                                                            .getBooleanProperty(WebappConfig.RECOGNISE_IMPLICIT_RDFRULE_INCLUSIONS),
                                                    localSettings
                                                            .getBooleanProperty(WebappConfig.INCLUDE_NON_PROFILE_MATCHED_RDFRULES),
                                                    overallConvertAlternateToPreferredPrefix, localSettings);
                        }
                        else
                        {
                            QueryBundleUtils.log
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
                nextProviderQueryBundle.setRelevantProfiles(sortedIncludedProfiles);
                nextProviderQueryBundle.setQueryallSettings(localSettings);
                
                results.add(nextProviderQueryBundle);
            }
            else
            {
                QueryBundleUtils.log.warn("Unrecognised provider endpoint method type nextProvider.getKey()="
                        + nextProvider.getKey() + " nextProvider.getClass().getName()="
                        + nextProvider.getClass().getName() + " endpointMethod=" + nextProvider.getEndpointMethod());
            }
        } // end for(Provider nextProvider : QueryTypeProviders)
        
        if(QueryBundleUtils._DEBUG)
        {
            QueryBundleUtils.log
                    .debug("RdfFetchController.generateQueryBundlesForQueryTypeAndProviders: results.size()="
                            + results.size());
        }
        
        return results;
    }
    
}
