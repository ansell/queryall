/**
 * 
 */
package org.queryall.helpers;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.openrdf.model.URI;
import org.queryall.api.Profile;
import org.queryall.api.QueryType;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public final class QueryTypeUtils
{

	/**
	 * 
	 */
	public QueryTypeUtils()
	{
		// TODO Auto-generated constructor stub
	}

	public static Collection<QueryType> getQueryTypesMatchingQueryString(String queryString, List<Profile> profileList, Map<URI, QueryType> allQueryTypes, boolean recogniseImplicitQueryInclusions, boolean includeNonProfileMatchedQueries)
	{
		if(Settings._DEBUG)
		{
			Settings.log.debug("getQueryTypesMatchingQueryString: profileList.size()="+profileList.size());
			
			if(Settings._TRACE)
			{
				for(Profile nextProfile : profileList)
		        {
		            Settings.log.trace("getQueryTypesMatchingQueryString: nextProfile.getKey()="+nextProfile.getKey().stringValue());
		        }
			}
		}
		
	    final Collection<QueryType> results = new HashSet<QueryType>();
	    
	    for(QueryType nextQuery : allQueryTypes.values())
	    {
	        if(nextQuery.matchesQueryString(queryString))
	        {
	            if(Settings._TRACE)
	            {
	                Settings.log
	                        .trace("getQueryTypesMatchingQueryString: tentative, pre-profile-check match for"
	                                + " nextQuery.getKey()="
	                                + nextQuery.getKey().stringValue()
	                                + " queryString="
	                                + queryString);
	            }
	            if(nextQuery.isUsedWithProfileList(profileList, recogniseImplicitQueryInclusions, includeNonProfileMatchedQueries))
	            {
	                if(Settings._DEBUG)
	                {
	                    Settings.log
	                            .debug("getQueryTypesMatchingQueryString: profileList suitable for"
	                                    + " nextQuery.getKey()="
	                                    + nextQuery.getKey().stringValue()
	                                    + " queryString="
	                                    + queryString);
	                }
	                results.add(nextQuery);
	            }
	            else if(Settings._TRACE)
	            {
	                Settings.log
	                        .trace("getQueryTypesMatchingQueryString: profileList not suitable for"
	                                + " nextQuery.getKey()="
	                                + nextQuery.getKey().stringValue()
	                                + " queryString="
	                                + queryString);
	            }
	        }
	    }
	    return results;
	}
	
	
	
}
