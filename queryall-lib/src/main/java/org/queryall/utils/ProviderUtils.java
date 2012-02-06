/**
 * 
 */
package org.queryall.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.openrdf.model.URI;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.Provider;
import org.queryall.api.querytype.InputQueryType;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.utils.NamespaceMatch;
import org.queryall.api.utils.WebappConfig;
import org.queryall.comparators.ValueComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public final class ProviderUtils
{
    private static final Logger log = LoggerFactory.getLogger(ProviderUtils.class);
    private static final boolean TRACE = ProviderUtils.log.isTraceEnabled();
    private static final boolean DEBUG = ProviderUtils.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = ProviderUtils.log.isInfoEnabled();
    
    /**
     * If the given QueryType includes defaults, this method returns a collection of Providers from
     * allProviders that are default providers and which are relevant to this query type, and are
     * used with the given profile instructions.
     * 
     * @param allProviders
     * @param queryType
     * @param profileList
     * @param recogniseImplicitProviderInclusions
     * @param includeNonProfileMatchedProviders
     * @return
     */
    public static Collection<Provider> getDefaultProviders(final Map<URI, Provider> allProviders,
            final QueryType queryType, final List<Profile> profileList,
            final boolean recogniseImplicitProviderInclusions, final boolean includeNonProfileMatchedProviders)
    {
        // Check if this query type includes defaults
        if(queryType.getIncludeDefaults())
        {
            final Collection<Provider> results = new HashSet<Provider>();
            
            for(final Provider nextProvider : allProviders.values())
            {
                if(nextProvider.getIsDefaultSource() && nextProvider.containsQueryTypeUri(queryType.getKey()))
                {
                    if(nextProvider.isUsedWithProfileList(profileList, recogniseImplicitProviderInclusions,
                            includeNonProfileMatchedProviders))
                    {
                        results.add(nextProvider);
                    }
                }
            }
            
            return results;
        }
        else
        {
            return Collections.emptyList();
        }
    }
    
    /**
     * 
     * 
     * @param allProviders
     * @param namespaceUris
     * @param namespaceMatchMethod
     * @return
     */
    public static Map<URI, Provider> getProvidersForNamespaceUris(final Map<URI, Provider> allProviders,
            final Map<String, Collection<URI>> namespaceUris, final NamespaceMatch namespaceMatchMethod)
    {
        if((namespaceUris == null) || (namespaceUris.size() == 0))
        {
            return Collections.emptyMap();
        }
        
        final Map<URI, Provider> results = new HashMap<URI, Provider>();
        
        for(final Provider nextProvider : allProviders.values())
        {
            // Assume nothing found for anyFound so we can switch it if anything is found
            boolean anyFound = false;
            // Assume everything found for allFound so we can switch it if anything is not found
            boolean allFound = true;
            
            for(final String nextInputParameter : namespaceUris.keySet())
            {
                final Collection<URI> nextNamespaceUriList = namespaceUris.get(nextInputParameter);
                
                if(nextNamespaceUriList == null)
                {
                    ProviderUtils.log
                            .warn("getProvidersForNamespaceUris: nextNamespaceUriList was null nextInputParameter="
                                    + nextInputParameter);
                    
                    continue;
                }
                
                boolean somethingFound = false;
                
                for(final URI nextNamespaceUri : nextNamespaceUriList)
                {
                    if(nextProvider.containsNamespaceUri(nextNamespaceUri))
                    {
                        somethingFound = true;
                    }
                }
                
                if(somethingFound)
                {
                    anyFound = true;
                }
                else
                {
                    allFound = false;
                }
            }
            
            if(anyFound && namespaceMatchMethod.equals(NamespaceMatch.ANY_MATCHED))
            {
                results.put(nextProvider.getKey(), nextProvider);
            }
            else if(allFound && namespaceMatchMethod.equals(NamespaceMatch.ALL_MATCHED))
            {
                results.put(nextProvider.getKey(), nextProvider);
            }
        }
        return results;
    }
    
    /**
     * 
     * 
     * @param sortedIncludedProfiles
     * @param allProviders
     * @param namespacePrefixToUriMap
     * @param recogniseImplicitProviderInclusions
     * @param includeNonProfileMatchedProviders
     * @param useDefaultProviders
     * @param nextQueryType
     * @param queryString
     * 
     * @return
     */
    public static Collection<Provider> getProvidersForQuery(final InputQueryType nextInputQueryType,
            final Map<String, String> queryParameters, final List<Profile> sortedIncludedProfiles,
            final Map<URI, Provider> allProviders, final Map<String, Collection<URI>> namespacePrefixToUriMap,
            final boolean recogniseImplicitProviderInclusions, final boolean includeNonProfileMatchedProviders)
    {
        final Collection<Provider> chosenProviders = new HashSet<Provider>();
        
        if(!nextInputQueryType.getIsNamespaceSpecific())
        {
            chosenProviders.addAll(ProviderUtils.getProvidersForQueryNonNamespaceSpecific(allProviders,
                    nextInputQueryType, sortedIncludedProfiles, recogniseImplicitProviderInclusions,
                    includeNonProfileMatchedProviders));
        }
        else
        {
            chosenProviders.addAll(ProviderUtils.getProvidersForQueryNamespaceSpecific(allProviders,
                    sortedIncludedProfiles, nextInputQueryType, namespacePrefixToUriMap, queryParameters,
                    recogniseImplicitProviderInclusions, includeNonProfileMatchedProviders));
        }
        
        if(nextInputQueryType.getIncludeDefaults())
        {
            if(ProviderUtils.DEBUG)
            {
                ProviderUtils.log.debug("including defaults for nextQueryType.title=" + nextInputQueryType.getTitle()
                        + " nextQueryType.getKey()=" + nextInputQueryType.getKey());
            }
            
            chosenProviders.addAll(ProviderUtils.getDefaultProviders(allProviders, nextInputQueryType,
                    sortedIncludedProfiles, recogniseImplicitProviderInclusions, includeNonProfileMatchedProviders));
        }
        
        return chosenProviders;
    }
    
    /**
     * Wrapper for the full parameter version of getProvidersForQuery using the given
     * QueryAllConfiguration to derive the extra parameters
     * 
     * @param nextInputQueryType
     * @param queryParameters
     * @param sortedIncludedProfiles
     * @param nextSettings
     * @return A collection of providers that are relevant to the given query type with the given
     *         query parameters and the given profiles
     */
    public static Collection<Provider> getProvidersForQuery(final InputQueryType nextInputQueryType,
            final Map<String, String> queryParameters, final List<Profile> sortedIncludedProfiles,
            final QueryAllConfiguration nextSettings)
    {
        return ProviderUtils.getProvidersForQuery(nextInputQueryType, queryParameters, sortedIncludedProfiles,
                nextSettings.getAllProviders(), nextSettings.getNamespacePrefixesToUris(),
                nextSettings.getBooleanProperty(WebappConfig.RECOGNISE_IMPLICIT_PROVIDER_INCLUSIONS),
                nextSettings.getBooleanProperty(WebappConfig.INCLUDE_NON_PROFILE_MATCHED_PROVIDERS));
    }
    
    /**
     * 
     * 
     * NOTE: this method relies on the regular expression matching behaviour of QueryType
     * 
     * @param allProviders
     * @param sortedIncludedProfiles
     * @param nextQueryType
     * @param namespacePrefixToUriMap
     * @param queryString
     * @param recogniseImplicitProviderInclusions
     * @param includeNonProfileMatchedProviders
     * @return
     */
    public static Collection<Provider> getProvidersForQueryNamespaceSpecific(final Map<URI, Provider> allProviders,
            final List<Profile> sortedIncludedProfiles, final InputQueryType nextQueryType,
            final Map<String, Collection<URI>> namespacePrefixToUriMap, final Map<String, String> queryParameters,
            final boolean recogniseImplicitProviderInclusions, final boolean includeNonProfileMatchedProviders)
    {
        final Collection<Provider> results = new ArrayList<Provider>();
        
        final Map<String, List<String>> queryStringMatches = nextQueryType.matchesForQueryParameters(queryParameters);
        
        final Map<String, Collection<URI>> nextQueryNamespaceUris = new HashMap<String, Collection<URI>>();
        
        // fill nextQueryNamespaceUris with the relevant namespaces
        for(final String nextNamespaceInputTag : nextQueryType.getNamespaceInputTags())
        {
            if(queryStringMatches.containsKey(nextNamespaceInputTag))
            {
                final Collection<String> nextInputNamespaces = queryStringMatches.get(nextNamespaceInputTag);
                
                for(final String nextInputNamespace : nextInputNamespaces)
                {
                    final Collection<URI> nextUriFromTitleNamespaceList =
                            NamespaceUtils.getNamespaceUrisForPrefix(namespacePrefixToUriMap, nextInputNamespace);
                    
                    if(nextUriFromTitleNamespaceList.size() > 0)
                    {
                        nextQueryNamespaceUris.put(nextInputNamespace, nextUriFromTitleNamespaceList);
                    }
                    else if(ProviderUtils.DEBUG)
                    {
                        ProviderUtils.log
                                .debug("getProvidersForQueryNamespaceSpecific: did not find any namespace URIs for nextTitle="
                                        + nextInputNamespaces + " nextQueryType.getKey()=" + nextQueryType.getKey());
                    }
                }
            }
            else
            {
                ProviderUtils.log
                        .error("getProvidersForQueryNamespaceSpecific: Could not match the namespace because the input tag was invalid nextNamespaceInputTag="
                                + nextNamespaceInputTag + " queryStringMatches.size()=" + queryStringMatches.size());
                
                throw new RuntimeException(
                        "Could not match the namespace because the input tag was invalid nextNamespaceInputTag="
                                + nextNamespaceInputTag + " queryStringMatches.size()=" + queryStringMatches.size());
            }
        }
        
        // then check if the query type handles these namespace Uris and if it does, attempt to get
        // providers
        if(nextQueryType.handlesNamespaceUris(nextQueryNamespaceUris))
        {
            final Map<URI, Provider> namespaceSpecificProviders =
                    ProviderUtils.getProvidersForQueryTypeForNamespaceUris(allProviders, nextQueryType,
                            nextQueryNamespaceUris, nextQueryType.getNamespaceMatchMethod());
            
            for(final Provider nextNamespaceSpecificProvider : namespaceSpecificProviders.values())
            {
                if(nextNamespaceSpecificProvider.isUsedWithProfileList(sortedIncludedProfiles,
                        recogniseImplicitProviderInclusions, includeNonProfileMatchedProviders))
                {
                    results.add(nextNamespaceSpecificProvider);
                }
            }
        }
        
        return results;
    }
    
    /**
     * Finds all providers for the given query type URI, taking into account profile instructions,
     * but without taking into account namespace conditions
     * 
     * @param allProviders
     * @param nextQueryType
     * @param sortedIncludedProfiles
     * @param recogniseImplicitProviderInclusions
     * @param includeNonProfileMatchedProviders
     * @return
     */
    public static Collection<Provider> getProvidersForQueryNonNamespaceSpecific(final Map<URI, Provider> allProviders,
            final InputQueryType nextQueryType, final List<Profile> sortedIncludedProfiles,
            final boolean recogniseImplicitProviderInclusions, final boolean includeNonProfileMatchedProviders)
    {
        // if we aren't specific to namespace we simply find all providers for this type of custom
        // query
        final Map<URI, Provider> relevantProviders =
                ProviderUtils.getProvidersForQueryType(allProviders, nextQueryType.getKey());
        
        final Collection<Provider> results = new ArrayList<Provider>(relevantProviders.size());
        
        for(final Provider nextAllProvider : relevantProviders.values())
        {
            if(ProviderUtils.TRACE)
            {
                ProviderUtils.log
                        .trace("getProvidersForQueryNonNamespaceSpecific: !nextQueryType.isNamespaceSpecific nextAllProvider="
                                + nextAllProvider.toString());
            }
            
            if(nextAllProvider.isUsedWithProfileList(sortedIncludedProfiles, recogniseImplicitProviderInclusions,
                    includeNonProfileMatchedProviders))
            {
                if(ProviderUtils.DEBUG)
                {
                    ProviderUtils.log
                            .debug("getProvidersForQueryNonNamespaceSpecific: profileList suitable for nextAllProvider.getKey()="
                                    + nextAllProvider.getKey());
                }
                
                results.add(nextAllProvider);
            }
        }
        
        return results;
    }
    
    /**
     * 
     * @param allProviders
     * @param nextQueryType
     * @return
     */
    public static Map<URI, Provider> getProvidersForQueryType(final Map<URI, Provider> allProviders,
            final URI nextQueryType)
    {
        final Map<URI, Provider> results = new TreeMap<URI, Provider>(new ValueComparator());
        
        for(final Provider nextProvider : allProviders.values())
        {
            if(nextProvider.containsQueryTypeUri(nextQueryType))
            {
                results.put(nextProvider.getKey(), nextProvider);
            }
        }
        
        if(ProviderUtils.DEBUG)
        {
            ProviderUtils.log.debug("getProvidersForQueryType: Found " + results.size() + " providers for querytype="
                    + nextQueryType.stringValue());
            
            if(ProviderUtils.TRACE)
            {
                for(final Provider nextResult : results.values())
                {
                    ProviderUtils.log.trace("getProvidersForQueryType: nextResult=" + nextResult.toString());
                }
            }
        }
        return results;
    }
    
    /**
     * 
     * 
     * @param allProviders
     * @param queryType
     * @param namespaceUris
     *            A Map of collections of URIs, where the inner collections all matched to a single
     *            input parameter which is given as the String key for the map, so that the
     *            algorithm can distinguish cases where more than one parameter was matched
     * @param namespaceMatchMethod
     *            The URI defining the method of matching namespaces. This can override the setting
     *            in the query type to examine other possible match scenarios, or it can be derived
     *            from the query type to match the query type creators intention.
     * @return
     */
    public static Map<URI, Provider> getProvidersForQueryTypeForNamespaceUris(final Map<URI, Provider> allProviders,
            final QueryType queryType, final Map<String, Collection<URI>> namespaceUris,
            final NamespaceMatch namespaceMatchMethod)
    {
        final Map<URI, Provider> namespaceProviders =
                ProviderUtils.getProvidersForNamespaceUris(allProviders, namespaceUris, namespaceMatchMethod);
        
        final Map<URI, Provider> results =
                ProviderUtils.getProvidersForQueryType(namespaceProviders, queryType.getKey());
        
        return results;
    }
    
    /**
	 * 
	 */
    private ProviderUtils()
    {
        // TODO Auto-generated constructor stub
    }
    
}
