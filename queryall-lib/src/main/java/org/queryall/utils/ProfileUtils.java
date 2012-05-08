/**
 * 
 */
package org.queryall.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.openrdf.model.URI;
import org.queryall.api.profile.Profile;
import org.queryall.api.utils.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProfileUtils
{
    private static final Logger log = LoggerFactory.getLogger(ProfileUtils.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = ProfileUtils.log.isTraceEnabled();
    private static final boolean DEBUG = ProfileUtils.log.isDebugEnabled();
    private static final boolean INFO = ProfileUtils.log.isInfoEnabled();
    
    /**
     * Fetches the profiles in the collection specified by nextProfileUriList from the map given as
     * allProfiles and then sorts them based on nextSortOrder.
     * 
     * Sorting is performed based on the fact that all Profile implementations are required to
     * implement Comparable<Profile>.
     * 
     * @param nextProfileUriList
     * @param nextSortOrder
     * @param allProfiles
     * @return
     */
    public static List<Profile> getAndSortProfileList(final Collection<URI> nextProfileUriList,
            final SortOrder nextSortOrder, final Map<URI, Profile> allProfiles)
    {
        final List<Profile> results = new ArrayList<Profile>();
        
        if(nextProfileUriList == null)
        {
            ProfileUtils.log.error("getAndSortProfileList: nextProfileUriList was null!");
            
            throw new RuntimeException("getAndSortProfileList: nextProfileUriList was null!");
        }
        else
        {
            for(final URI nextProfileUri : nextProfileUriList)
            {
                if(allProfiles.containsKey(nextProfileUri))
                {
                    final Profile nextProfileObject = allProfiles.get(nextProfileUri);
                    results.add(nextProfileObject);
                }
            }
        }
        
        if(nextSortOrder == SortOrder.LOWEST_ORDER_FIRST)
        {
            Collections.sort(results);
        }
        else if(nextSortOrder == SortOrder.HIGHEST_ORDER_FIRST)
        {
            Collections.sort(results, Collections.reverseOrder());
        }
        else
        {
            throw new RuntimeException("getAndSortProfileList: sortOrder unrecognised nextSortOrder=" + nextSortOrder);
        }
        
        return results;
    }
}
