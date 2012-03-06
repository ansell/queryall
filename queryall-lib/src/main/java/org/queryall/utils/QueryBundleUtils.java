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
    private static final boolean TRACE = QueryBundleUtils.log.isTraceEnabled();
    private static final boolean DEBUG = QueryBundleUtils.log.isDebugEnabled();
    private static final boolean INFO = QueryBundleUtils.log.isInfoEnabled();
    
    /**
     * TODO: Simplify this method and abstract it out into Provider classes
     * 
     * @param nextQueryType
     * @param chosenProviders
     * @param queryParameters
     * @param namespaceInputVariables
     * @param sortedIncludedProfiles
     * @param localSettings
     * @param localBlacklistController
     * @param realHostName
     * @param useAllEndpointsForEachProvider
     * @param pageOffset
     * @return
     * @throws QueryAllException
     */
    public static Collection<QueryBundle> generateQueryBundlesForQueryTypeAndProviders(
            final InputQueryType nextQueryType, final Collection<Provider> chosenProviders,
            final Map<String, String> queryParameters,
            final Map<String, Collection<NamespaceEntry>> namespaceInputVariables,
            final List<Profile> sortedIncludedProfiles, final QueryAllConfiguration localSettings,
            final BlacklistController localBlacklistController, final String realHostName,
            final boolean useAllEndpointsForEachProvider, final int pageOffset) throws QueryAllException
    {
        final Collection<QueryBundle> results = new HashSet<QueryBundle>();
        
        // Note: We default to converting alternate namespaces to preferred unless it is turned off
        // in the configuration. It can always be turned off for each namespace entry individually
        // FIXME: The current processing code ignores the preferences given by namespace entries,
        // and just uses this setting
        final boolean overallConvertAlternateToPreferredPrefix =
                localSettings.getBooleanProperty(WebappConfig.CONVERT_ALTERNATE_NAMESPACE_PREFIXES_TO_PREFERRED);
        
        for(final Provider nextProvider : chosenProviders)
        {
            QueryBundleUtils.log.info("start of loop body for nextProvider="+nextProvider.getKey().stringValue());
            final boolean noCommunicationProvider =
                    nextProvider.getEndpointMethod().equals(ProviderSchema.getProviderNoCommunication());
            
            if(nextProvider instanceof HttpProvider)
            {
                QueryBundleUtils.log.info("nextProvider instanceof HttpProvider");
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
                    
                    if(nextCustomIncludeType != null)
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
                    }
                }
                
                final QueryBundle nextProviderQueryBundle = new QueryBundle();
                
                nextProviderQueryBundle.setOutputString(nextStaticRdfXmlString);
                nextProviderQueryBundle.setOriginalProvider(nextProvider);
                nextProviderQueryBundle.setQueryType(nextQueryType);
                nextProviderQueryBundle.setRelevantProfiles(sortedIncludedProfiles);
                nextProviderQueryBundle.setQueryallSettings(localSettings);
                
                for(final String nextEndpoint : ListUtils.randomiseCollectionLayout(replacedEndpoints.keySet()))
                {
                    final Map<String, String> originalEndpointEntries = replacedEndpoints.get(nextEndpoint);
                    
                    for(final String nextReplacedEndpoint : originalEndpointEntries.keySet())
                    {
                        if(nextReplacedEndpoint == null)
                        {
                            QueryBundleUtils.log.error("nextReplacedEndpoint was null nextEndpoint=" + nextEndpoint
                                    + " nextQueryType=" + nextQueryType + " nextProvider=" + nextProvider);
                            continue;
                        }
                        
                        // Then test whether the endpoint is blacklisted before accepting it
                        if(noCommunicationProvider || !localBlacklistController.isUrlBlacklisted(nextReplacedEndpoint))
                        {
                            // no need to worry about redundant endpoint alternates if we are going
                            // to try to query all of the endpoints for each provider
                            if(nextProviderQueryBundle.getAlternativeEndpointsAndQueries().size() == 0
                                    || useAllEndpointsForEachProvider)
                            {
                                // FIXME: Check to make sure that this does not generate nulls
                                nextProviderQueryBundle.addAlternativeEndpointAndQuery(nextReplacedEndpoint,
                                        originalEndpointEntries.get(nextReplacedEndpoint));
                            }
                        }
                    }
                    
                    results.add(nextProviderQueryBundle);
                }
            } // end if(nextProvider instanceof HttpProvider)
            else if(noCommunicationProvider)
            {
                QueryBundleUtils.log.info("noCommunicationProvider == true");
                String nextStaticRdfXmlString = "";
                
                for(final URI nextCustomInclude : nextQueryType.getLinkedQueryTypes())
                {
                    QueryBundleUtils.log.info("d");
                    // pick out all of the QueryType's which have been delegated for this particular
                    // query as static includes
                    final QueryType nextCustomIncludeType = localSettings.getAllQueryTypes().get(nextCustomInclude);
                    
                    if(nextCustomIncludeType != null)
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
            
            QueryBundleUtils.log.info("end of loop body for nextProvider="+nextProvider.getKey().stringValue());
        } // end for(Provider nextProvider : QueryTypeProviders)
        
        return results;
    }
    
}
