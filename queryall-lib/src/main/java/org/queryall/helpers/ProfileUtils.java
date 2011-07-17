/**
 * 
 */
package org.queryall.helpers;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.openrdf.model.URI;
import org.queryall.api.Profile;
import org.queryall.impl.ProfileImpl;

/**
 *
 */
public class ProfileUtils
{
    private static final Logger log = Logger.getLogger(ProfileUtils.class.getName());
    @SuppressWarnings("unused")
    private static final boolean _TRACE = log.isTraceEnabled();
    private static final boolean _DEBUG = log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = log.isInfoEnabled();
    

	/**
	 * This method implements the main logic with reference to include/exclude decisions
	 * based on a given includeExcludeOrder and the default profile include exclude 
	 * order which overrides the given includeExcludeOrder if it is the undefined URI
	 * 
	 * The algorithm starts by checking both the include and exclude lists for the URI and records the existence of the URI in either list
	 * 
	 * If the nextIncludeExcludeOrder is null or the undefined URI, it is replaced with nextDefaultProfileIncludeExclude, which is not allowed to be undefined if it is required.
	 * 
	 * Then the main part of the algorithm is checked based on whether nextIncludeExcludeOrder is excludeThenInclude or includeThenExclude
	 * 
	 * If nextIncludeOrder is excludeThenInclude and an exclude was found then SPECIFIC_EXCLUDE is returned.
	 * Otherwise if nextIncludeOrder is excludeThenInclude and an include was found, then SPECIFIC_INCLUDE is returned.
	 * Otherwise if nextIncludeOrder is excludeThenInclude, IMPLICIT_INCLUDE is returned.
	 * 
	 * If next IncludeOrder is includeThenExclude and an include was found then SPECIFIC_INCLUDE is returned.
	 * Otherwise if nextIncludeOrder is includeThenExclude and an exclude was found then SPECIFIC_EXCLUDE is returned.
	 * Otherwise if nextIncludeORder is includeThenExclude, NO_MATCH is returned
	 * 
	 * @param nextUri
	 * @param nextIncludeExcludeOrder
	 * @param includeList
	 * @param excludeList
	 * @param nextDefaultProfileIncludeExcludeOrder
	 * @return One of the following constants, ProfileImpl.SPECIFIC_EXCLUDE, ProfileImpl.SPECIFIC_INCLUDE, ProfileImpl.IMPLICIT_INCLUDE or ProfileImpl.NO_MATCH
	 */
	public static final int usedWithIncludeExcludeList(URI nextUri, URI nextIncludeExcludeOrder, Collection<URI> includeList, Collection<URI> excludeList, URI nextDefaultProfileIncludeExcludeOrder)
	{
	    if(includeList == null || excludeList == null)
	    {
	        throw new IllegalArgumentException("Profile.usedWithList: includeList or excludeList was null");
	    }
	    
	    boolean includeFound = includeList.contains(nextUri);
	    boolean excludeFound = excludeList.contains(nextUri);
	    
	    if(nextIncludeExcludeOrder == null || nextIncludeExcludeOrder.equals(ProfileImpl.getProfileIncludeExcludeOrderUndefinedUri()))
	    {
	        nextIncludeExcludeOrder = nextDefaultProfileIncludeExcludeOrder;
	    }
	    
	    if(nextIncludeExcludeOrder.equals(ProfileImpl.getExcludeThenIncludeUri()))
	    {
	        if(ProfileUtils._DEBUG)
	        {
	            log.debug("Profile.usedWithList: using exclude then include rules");
	        }
	        
	        if(excludeFound)
	        {
	            if(ProfileUtils._DEBUG)
	            {
	                log.debug("Profile.usedWithList: excludeFound=true, returning false");
	            }
	            
	            return ProfileImpl.SPECIFIC_EXCLUDE;
	        }
	        else if(includeFound)
	        {
	            if(ProfileUtils._DEBUG)
	            {
	                log.debug("Profile.usedWithList: includeFound=true, returning true");
	            }
	            
	            return ProfileImpl.SPECIFIC_INCLUDE;
	        }
	        else
	        {
	            if(ProfileUtils._DEBUG)
	            {
	                log.debug("Profile.usedWithList: includeFound=false and excludeFound=false, returning true");
	            }
	            
	            return ProfileImpl.IMPLICIT_INCLUDE;
	        }
	    }
	    else if(nextIncludeExcludeOrder.equals(ProfileImpl.getIncludeThenExcludeUri()))
	    {
	        if(ProfileUtils._DEBUG)
	        {
	            log.debug("Profile.usedWithList: using include then exclude rules");
	        }
	        
	        if(includeFound)
	        {
	            if(ProfileUtils._DEBUG)
	            {
	                log.debug("Profile.usedWithList: includeFound=true, returning true");
	            }
	            
	            return ProfileImpl.SPECIFIC_INCLUDE;
	        }
	        else if(excludeFound)
	        {
	            if(ProfileUtils._DEBUG)
	            {
	                log.debug("Profile.usedWithList: excludeFound=true, returning false");
	            }
	            
	            return ProfileImpl.SPECIFIC_EXCLUDE;
	        }
	        else
	        {
	            if(ProfileUtils._DEBUG)
	            {
	                log.debug("Profile.usedWithList: includeFound=false and excludeFound=false, returning false");
	            }
	            
	            return ProfileImpl.NO_MATCH;
	        }
	    }
	    else
	    {
	        throw new RuntimeException("Profile.usedWithList: nextIncludeExcludeOrder not recognised ("+nextIncludeExcludeOrder+")");
	    }
	}

}
