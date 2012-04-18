/**
 * 
 */
package org.queryall.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.model.URI;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.namespace.ValidatingNamespaceEntry;
import org.queryall.api.profile.Profile;
import org.queryall.api.querytype.InputQueryType;
import org.queryall.api.querytype.QueryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public final class QueryTypeUtils
{
    private static final Logger log = LoggerFactory.getLogger(QueryTypeUtils.class);
    private static final boolean TRACE = QueryTypeUtils.log.isTraceEnabled();
    private static final boolean DEBUG = QueryTypeUtils.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = QueryTypeUtils.log.isInfoEnabled();
    
    public static Map<QueryType, Map<String, Collection<NamespaceEntry>>> getQueryTypesMatchingQuery(
            final Map<String, String> queryParameters, final List<Profile> profileList,
            final Map<URI, QueryType> allQueryTypes, final Map<String, Collection<URI>> namespacePrefixesToUris,
            final Map<URI, NamespaceEntry> allNamespaceEntries, final boolean recogniseImplicitQueryInclusions,
            final boolean includeNonProfileMatchedQueries)
    {
        if(QueryTypeUtils.DEBUG)
        {
            QueryTypeUtils.log.debug("profileList.size()=" + profileList.size());
            
            if(QueryTypeUtils.TRACE)
            {
                for(final Profile nextProfile : profileList)
                {
                    QueryTypeUtils.log.trace("getQueryTypesMatchingQueryString: nextProfile.getKey()="
                            + nextProfile.getKey().stringValue());
                }
            }
        }
        
        final Map<QueryType, Map<String, Collection<NamespaceEntry>>> results =
                new HashMap<QueryType, Map<String, Collection<NamespaceEntry>>>();
        
        for(final QueryType nextQuery : allQueryTypes.values())
        {
            if(!(nextQuery instanceof InputQueryType))
            {
                QueryTypeUtils.log.info("Found a query type that was not an input query type, ignoring it. key="
                        + nextQuery.getKey().stringValue());
                continue;
            }
            
            final InputQueryType nextInputQuery = (InputQueryType)nextQuery;
            
            if(nextInputQuery.matchesQueryParameters(queryParameters))
            {
                if(QueryTypeUtils.TRACE)
                {
                    QueryTypeUtils.log.trace("tentative, pre-profile-check match for" + " nextQuery.getKey()="
                            + nextQuery.getKey().stringValue() + " queryParameters=" + queryParameters);
                }
                if(nextQuery.isUsedWithProfileList(profileList, recogniseImplicitQueryInclusions,
                        includeNonProfileMatchedQueries))
                {
                    if(QueryTypeUtils.DEBUG)
                    {
                        QueryTypeUtils.log.debug("profileList suitable for" + " nextQuery.getKey()="
                                + nextQuery.getKey().stringValue() + " queryParameters=" + queryParameters);
                    }
                    
                    // Only try to populate actualNamespaceEntries if the query is namespace
                    // specific
                    if(nextQuery.getIsNamespaceSpecific())
                    {
                        final Map<String, Collection<URI>> namespaceMatches =
                                QueryTypeUtils.namespacesMatchesForQueryParameters(nextInputQuery, queryParameters,
                                        namespacePrefixesToUris);
                        
                        final Map<String, Collection<NamespaceEntry>> actualNamespaceEntries =
                                new HashMap<String, Collection<NamespaceEntry>>(namespaceMatches.size() * 2);
                        
                        for(final String nextParameter : namespaceMatches.keySet())
                        {
                            final Collection<NamespaceEntry> namespaceParameterMatches =
                                    new ArrayList<NamespaceEntry>(2);
                            
                            for(final URI nextNamespaceUri : namespaceMatches.get(nextParameter))
                            {
                                final NamespaceEntry nextNamespaceEntry = allNamespaceEntries.get(nextNamespaceUri);
                                
                                if(nextNamespaceEntry instanceof ValidatingNamespaceEntry)
                                {
                                    // TODO: implement validation code here on the queryParameter
                                    // that is identified as the "identifier" for this namespace in
                                    // the context of this query
                                }
                                
                                namespaceParameterMatches.add(nextNamespaceEntry);
                            }
                            
                            actualNamespaceEntries.put(nextParameter, namespaceParameterMatches);
                        }
                        
                        if(actualNamespaceEntries.size() > 0)
                        {
                            results.put(nextQuery, actualNamespaceEntries);
                        }
                        else if(QueryTypeUtils.INFO)
                        {
                            QueryTypeUtils.log
                                    .info("No namespace parameters matched for a namespace specific query, so not including this query type nextQuery={}",
                                            nextQuery.getKey().stringValue());
                        }
                    }
                    else
                    {
                        if(QueryTypeUtils.DEBUG)
                        {
                            QueryTypeUtils.log
                                    .debug("Query type is not namespace specific, creating an empty namespace parameter map");
                        }
                        // In other cases use an unmodifiable empty map
                        final Map<String, Collection<NamespaceEntry>> emptyMap = Collections.emptyMap();
                        
                        results.put(nextQuery, emptyMap);
                    }
                    
                }
                else if(QueryTypeUtils.TRACE)
                {
                    QueryTypeUtils.log.trace("profileList not suitable for" + " nextQuery.getKey()="
                            + nextQuery.getKey().stringValue() + " queryParameters=" + queryParameters);
                }
            }
        }
        return results;
    }
    
    public static Map<String, Collection<URI>> namespacesMatchesForQueryParameters(final InputQueryType nextQueryType,
            final Map<String, String> nextQueryParameters, final Map<String, Collection<URI>> namespacePrefixMap)
    {
        final Map<String, Collection<URI>> results = new HashMap<String, Collection<URI>>();
        
        final Map<String, List<String>> namespaceParameterMatches =
                nextQueryType.matchesForQueryParameters(nextQueryParameters);
        
        for(final String nextNamespaceParameter : namespaceParameterMatches.keySet())
        {
            if(nextQueryType.isInputVariableNamespace(nextNamespaceParameter))
            {
                final List<String> nextNamespaceParameterMatches =
                        namespaceParameterMatches.get(nextNamespaceParameter);
                
                for(final String nextNamespaceParameterMatch : nextNamespaceParameterMatches)
                {
                    if(namespacePrefixMap.containsKey(nextNamespaceParameterMatch))
                    {
                        if(QueryTypeUtils.TRACE)
                        {
                            QueryTypeUtils.log.trace("Found a namespace for nextNamespaceParameter="
                                    + nextNamespaceParameter + " nextNamespaceParameterMatch="
                                    + nextNamespaceParameterMatch);
                        }
                        
                        results.put(nextNamespaceParameter, namespacePrefixMap.get(nextNamespaceParameterMatch));
                    }
                    else
                    {
                        QueryTypeUtils.log.error("Could not find a matching namespace for nextNamespaceParameter="
                                + nextNamespaceParameter + " nextNamespaceParameterMatch="
                                + nextNamespaceParameterMatch);
                    }
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
