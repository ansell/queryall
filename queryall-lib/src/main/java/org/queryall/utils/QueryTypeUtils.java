/**
 * 
 */
package org.queryall.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.openrdf.model.URI;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.profile.Profile;
import org.queryall.api.querytype.QueryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public final class QueryTypeUtils
{
    public static final Logger log = LoggerFactory.getLogger(QueryTypeUtils.class);
    public static final boolean _TRACE = QueryTypeUtils.log.isTraceEnabled();
    public static final boolean _DEBUG = QueryTypeUtils.log.isDebugEnabled();
    public static final boolean _INFO = QueryTypeUtils.log.isInfoEnabled();
    
    public static Collection<QueryType> getQueryTypesByUri(final Map<URI, QueryType> allQueryTypes,
            final URI queryTypeUri)
    {
        final Collection<QueryType> results = new HashSet<QueryType>();
        for(final QueryType nextQueryType : allQueryTypes.values())
        {
            if(nextQueryType.getKey().equals(queryTypeUri))
            {
                results.add(nextQueryType);
            }
        }
        return results;
    }
    
    public static Map<String, Collection<URI>> namespacesMatchesForQueryParameters(final QueryType nextQueryType, final Map<String, String> nextQueryParameters, Map<String, Collection<URI>> namespacePrefixMap)
    {
        Map<String, Collection<URI>> results = new HashMap<String, Collection<URI>>();
        
        for(String nextQueryParameter : nextQueryParameters.keySet())
        {
            if(nextQueryType.isInputVariableNamespace(nextQueryParameter))
            {
                if(namespacePrefixMap.containsKey(nextQueryParameters.get(nextQueryParameter)))
                {
                    results.put(nextQueryParameter, namespacePrefixMap.get(nextQueryParameters.get(nextQueryParameter)));
                }
                else
                {
                    log.warn("Could not find a matching namespace for nextQueryParameter="+nextQueryParameter);
                }
            }
        }
        
        return results;
    }

    public static Map<QueryType, Map<String, Collection<NamespaceEntry>>> getQueryTypesMatchingQuery(final Map<String, String> queryParameters,
            final List<Profile> profileList, final Map<URI, QueryType> allQueryTypes,
            final boolean recogniseImplicitQueryInclusions, final boolean includeNonProfileMatchedQueries, QueryAllConfiguration localSettings)
    {
        if(QueryTypeUtils._DEBUG)
        {
            QueryTypeUtils.log.debug("getQueryTypesMatchingQueryString: profileList.size()=" + profileList.size());
            
            if(QueryTypeUtils._TRACE)
            {
                for(final Profile nextProfile : profileList)
                {
                    QueryTypeUtils.log.trace("getQueryTypesMatchingQueryString: nextProfile.getKey()="
                            + nextProfile.getKey().stringValue());
                }
            }
        }
        
        final Map<QueryType, Map<String, Collection<NamespaceEntry>>> results = new HashMap<QueryType, Map<String, Collection<NamespaceEntry>>>();
        
        for(final QueryType nextQuery : allQueryTypes.values())
        {
            if(nextQuery.matchesQueryParameters(queryParameters))
            {
                if(QueryTypeUtils._TRACE)
                {
                    QueryTypeUtils.log.trace("getQueryTypesMatchingQueryString: tentative, pre-profile-check match for"
                            + " nextQuery.getKey()=" + nextQuery.getKey().stringValue() + " queryParameters="
                            + queryParameters);
                }
                if(nextQuery.isUsedWithProfileList(profileList, recogniseImplicitQueryInclusions,
                        includeNonProfileMatchedQueries))
                {
                    if(QueryTypeUtils._DEBUG)
                    {
                        QueryTypeUtils.log.debug("getQueryTypesMatchingQueryString: profileList suitable for"
                                + " nextQuery.getKey()=" + nextQuery.getKey().stringValue() + " queryParameters="
                                + queryParameters);
                    }
                    
                    if(nextQuery.getIsNamespaceSpecific())
                    {
                        Map<String, Collection<URI>> namespaceMatches = QueryTypeUtils.namespacesMatchesForQueryParameters(nextQuery, queryParameters, localSettings.getNamespacePrefixesToUris());
                        
                        Map<String, Collection<NamespaceEntry>> actualNamespaceEntries = new HashMap<String, Collection<NamespaceEntry>>();
                        
                        for(String nextParameter : namespaceMatches.keySet())
                        {
                            Collection<NamespaceEntry> namespaceParameterMatches = new ArrayList<NamespaceEntry>(2);

                            for(URI nextNamespaceUri : namespaceMatches.get(nextParameter))
                            {
                                namespaceParameterMatches.add(localSettings.getAllNamespaceEntries().get(nextNamespaceUri));
                            }

                            actualNamespaceEntries.put(nextParameter, namespaceParameterMatches);
                        }

                        results.put(nextQuery, actualNamespaceEntries);
                    }
                    
                }
                else if(QueryTypeUtils._TRACE)
                {
                    QueryTypeUtils.log.trace("getQueryTypesMatchingQueryString: profileList not suitable for"
                            + " nextQuery.getKey()=" + nextQuery.getKey().stringValue() + " queryParameters="
                            + queryParameters);
                }
            }
        }
        return results;
    }
    
    /**
	 * 
	 */
    private QueryTypeUtils()
    {
        // TODO Auto-generated constructor stub
    }
    
}
