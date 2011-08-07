/**
 * 
 */
package org.queryall.helpers;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openrdf.model.URI;
import org.queryall.api.Profile;
import org.queryall.api.QueryType;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public final class QueryTypeUtils
{
    public static final Logger log = Logger.getLogger(QueryTypeUtils.class.getName());
    public static final boolean _TRACE = QueryTypeUtils.log.isTraceEnabled();
    public static final boolean _DEBUG = QueryTypeUtils.log.isDebugEnabled();
    public static final boolean _INFO = QueryTypeUtils.log.isInfoEnabled();
    
    /**
	 * 
	 */
    public QueryTypeUtils()
    {
        // TODO Auto-generated constructor stub
    }
    
    public static Collection<QueryType> getQueryTypesMatchingQueryString(String queryString, List<Profile> profileList,
            Map<URI, QueryType> allQueryTypes, boolean recogniseImplicitQueryInclusions,
            boolean includeNonProfileMatchedQueries)
    {
        if(QueryTypeUtils._DEBUG)
        {
            QueryTypeUtils.log.debug("getQueryTypesMatchingQueryString: profileList.size()=" + profileList.size());
            
            if(QueryTypeUtils._TRACE)
            {
                for(Profile nextProfile : profileList)
                {
                    QueryTypeUtils.log.trace("getQueryTypesMatchingQueryString: nextProfile.getKey()="
                            + nextProfile.getKey().stringValue());
                }
            }
        }
        
        final Collection<QueryType> results = new HashSet<QueryType>();
        
        for(QueryType nextQuery : allQueryTypes.values())
        {
            if(nextQuery.matchesQueryString(queryString))
            {
                if(QueryTypeUtils._TRACE)
                {
                    QueryTypeUtils.log
                            .trace("getQueryTypesMatchingQueryString: tentative, pre-profile-check match for"
                                    + " nextQuery.getKey()=" + nextQuery.getKey().stringValue() + " queryString="
                                    + queryString);
                }
                if(nextQuery.isUsedWithProfileList(profileList, recogniseImplicitQueryInclusions,
                        includeNonProfileMatchedQueries))
                {
                    if(QueryTypeUtils._DEBUG)
                    {
                        QueryTypeUtils.log.debug("getQueryTypesMatchingQueryString: profileList suitable for"
                                + " nextQuery.getKey()=" + nextQuery.getKey().stringValue() + " queryString="
                                + queryString);
                    }
                    results.add(nextQuery);
                }
                else if(QueryTypeUtils._TRACE)
                {
                    QueryTypeUtils.log
                            .trace("getQueryTypesMatchingQueryString: profileList not suitable for"
                                    + " nextQuery.getKey()=" + nextQuery.getKey().stringValue() + " queryString="
                                    + queryString);
                }
            }
        }
        return results;
    }
    
    public static Collection<QueryType> getQueryTypesByUri(Map<URI, QueryType> allQueryTypes, URI queryTypeUri)
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
    
}
