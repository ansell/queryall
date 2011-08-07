/**
 * 
 */
package org.queryall.helpers;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openrdf.model.URI;
import org.queryall.api.NormalisationRule;
import org.queryall.api.ProfilableInterface;
import org.queryall.api.Profile;
import org.queryall.api.Provider;
import org.queryall.api.QueryType;
import org.queryall.impl.ProfileImpl;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProfileUtils
{
    private static final Logger log = Logger.getLogger(ProfileUtils.class.getName());
    @SuppressWarnings("unused")
    private static final boolean _TRACE = ProfileUtils.log.isTraceEnabled();
    private static final boolean _DEBUG = ProfileUtils.log.isDebugEnabled();
    private static final boolean _INFO = ProfileUtils.log.isInfoEnabled();
    
    /**
     * This method implements the main logic with reference to include/exclude decisions based on a
     * given includeExcludeOrder and the default profile include exclude order which overrides the
     * given includeExcludeOrder if it is the undefined URI
     * 
     * The algorithm starts by checking both the include and exclude lists for the URI and records
     * the existence of the URI in either list
     * 
     * If the nextIncludeExcludeOrder is null or the undefined URI, it is replaced with
     * nextDefaultProfileIncludeExclude, which is not allowed to be undefined if it is required.
     * 
     * Then the main part of the algorithm is checked based on whether nextIncludeExcludeOrder is
     * excludeThenInclude or includeThenExclude
     * 
     * If nextIncludeOrder is excludeThenInclude and an exclude was found then SPECIFIC_EXCLUDE is
     * returned. Otherwise if nextIncludeOrder is excludeThenInclude and an include was found, then
     * SPECIFIC_INCLUDE is returned. Otherwise if nextIncludeOrder is excludeThenInclude,
     * IMPLICIT_INCLUDE is returned.
     * 
     * If next IncludeOrder is includeThenExclude and an include was found then SPECIFIC_INCLUDE is
     * returned. Otherwise if nextIncludeOrder is includeThenExclude and an exclude was found then
     * SPECIFIC_EXCLUDE is returned. Otherwise if nextIncludeORder is includeThenExclude, NO_MATCH
     * is returned
     * 
     * @param nextUri
     * @param nextIncludeExcludeOrder
     * @param includeList
     * @param excludeList
     * @param nextDefaultProfileIncludeExcludeOrder
     * @return One of the following constants, ProfileImpl.SPECIFIC_EXCLUDE,
     *         ProfileImpl.SPECIFIC_INCLUDE, ProfileImpl.IMPLICIT_INCLUDE or ProfileImpl.NO_MATCH
     * @throws IllegalArgumentException
     *             if the include or exclude lists are null, or nextIncludeExcludeOrder is not
     *             includeOrExclude or excludeOrInclude and nextDefaultProfileIncludeExcludeOrder
     *             does not help resolve the nextIncludeExcludeOrder
     */
    public static final ProfileMatch usedWithIncludeExcludeList(URI nextUri, URI nextIncludeExcludeOrder,
            Collection<URI> includeList, Collection<URI> excludeList, URI nextDefaultProfileIncludeExcludeOrder)
    {
        if(includeList == null || excludeList == null)
        {
            throw new IllegalArgumentException("Profile.usedWithList: includeList or excludeList was null");
        }
        
        boolean includeFound = includeList.contains(nextUri);
        boolean excludeFound = excludeList.contains(nextUri);
        
        if(nextIncludeExcludeOrder == null
                || nextIncludeExcludeOrder.equals(ProfileImpl.getProfileIncludeExcludeOrderUndefinedUri()))
        {
            nextIncludeExcludeOrder = nextDefaultProfileIncludeExcludeOrder;
        }
        
        if(nextIncludeExcludeOrder.equals(ProfileImpl.getExcludeThenIncludeUri()))
        {
            if(ProfileUtils._DEBUG)
            {
                ProfileUtils.log.debug("Profile.usedWithList: using exclude then include rules");
            }
            
            if(excludeFound)
            {
                if(ProfileUtils._DEBUG)
                {
                    ProfileUtils.log.debug("Profile.usedWithList: excludeFound=true, returning false");
                }
                
                return ProfileMatch.SPECIFIC_EXCLUDE;
            }
            else if(includeFound)
            {
                if(ProfileUtils._DEBUG)
                {
                    ProfileUtils.log.debug("Profile.usedWithList: includeFound=true, returning true");
                }
                
                return ProfileMatch.SPECIFIC_INCLUDE;
            }
            else
            {
                if(ProfileUtils._DEBUG)
                {
                    ProfileUtils.log
                            .debug("Profile.usedWithList: includeFound=false and excludeFound=false, returning true");
                }
                
                return ProfileMatch.IMPLICIT_INCLUDE;
            }
        }
        else if(nextIncludeExcludeOrder.equals(ProfileImpl.getIncludeThenExcludeUri()))
        {
            if(ProfileUtils._DEBUG)
            {
                ProfileUtils.log.debug("Profile.usedWithList: using include then exclude rules");
            }
            
            if(includeFound)
            {
                if(ProfileUtils._DEBUG)
                {
                    ProfileUtils.log.debug("Profile.usedWithList: includeFound=true, returning true");
                }
                
                return ProfileMatch.SPECIFIC_INCLUDE;
            }
            else if(excludeFound)
            {
                if(ProfileUtils._DEBUG)
                {
                    ProfileUtils.log.debug("Profile.usedWithList: excludeFound=true, returning false");
                }
                
                return ProfileMatch.SPECIFIC_EXCLUDE;
            }
            else
            {
                if(ProfileUtils._DEBUG)
                {
                    ProfileUtils.log
                            .debug("Profile.usedWithList: includeFound=false and excludeFound=false, returning false");
                }
                
                return ProfileMatch.NO_MATCH;
            }
        }
        else
        {
            throw new IllegalArgumentException("Profile.usedWithList: nextIncludeExcludeOrder not recognised ("
                    + nextIncludeExcludeOrder + ")");
        }
    }
    
    public static boolean isUsedWithProfileList(ProfilableInterface profilableObject,
            List<Profile> nextSortedProfileList, boolean recogniseImplicitInclusions, boolean includeNonProfileMatched)
    {
        for(final Profile nextProfile : nextSortedProfileList)
        {
            final ProfileMatch trueResult = ProfileUtils.usedWithProfilable(nextProfile, profilableObject);
            if(trueResult == ProfileMatch.IMPLICIT_INCLUDE)
            {
                if(ProfileUtils._DEBUG)
                {
                    ProfileUtils.log.debug("isUsedWithProfileList: found implicit include for profilableObject="
                            + profilableObject.getKey().stringValue() + " profile="
                            + nextProfile.getKey().stringValue());
                }
                
                if(recogniseImplicitInclusions)
                {
                    if(ProfileUtils._DEBUG)
                    {
                        ProfileUtils.log
                                .debug("isUsedWithProfileList: returning implicit include true for profilableObject="
                                        + profilableObject.getKey().stringValue() + " profile="
                                        + nextProfile.getKey().stringValue());
                    }
                    return true;
                }
                else if(ProfileUtils._DEBUG)
                {
                    ProfileUtils.log
                            .debug("isUsedWithProfileList: implicit include not recognised for profilableObject="
                                    + profilableObject.getKey().stringValue() + " profile="
                                    + nextProfile.getKey().stringValue());
                }
            }
            else if(trueResult == ProfileMatch.SPECIFIC_INCLUDE)
            {
                if(ProfileUtils._DEBUG)
                {
                    ProfileUtils.log.debug("isUsedWithProfileList: returning specific true for profilableObject="
                            + profilableObject.getKey().stringValue() + " profile="
                            + nextProfile.getKey().stringValue());
                }
                return true;
            }
            else if(trueResult == ProfileMatch.SPECIFIC_EXCLUDE)
            {
                if(ProfileUtils._DEBUG)
                {
                    ProfileUtils.log.debug("isUsedWithProfileList: returning specific false for profilableObject="
                            + profilableObject.getKey().stringValue() + " profile="
                            + nextProfile.getKey().stringValue());
                }
                return false;
            }
            
        }
        
        boolean returnValue =
                (profilableObject.getProfileIncludeExcludeOrder().equals(ProfileImpl.getExcludeThenIncludeUri()) || profilableObject
                        .getProfileIncludeExcludeOrder()
                        .equals(ProfileImpl.getProfileIncludeExcludeOrderUndefinedUri()))
                        && includeNonProfileMatched;
        
        if(ProfileUtils._DEBUG)
        {
            ProfileUtils.log.debug("ProfileImpl.isUsedWithProfileList: returning no matches found returnValue="
                    + returnValue + " for profilableObject=" + profilableObject.getKey().stringValue());
        }
        
        return returnValue;
    }
    
    public static ProfileMatch usedWithProfilable(Profile profile, ProfilableInterface profilableObject)
    {
        Collection<URI> includeList = null;
        Collection<URI> excludeList = null;
        boolean allowImplicitInclusions = false;
        
        if(profilableObject instanceof Provider)
        {
            includeList = profile.getIncludeProviders();
            excludeList = profile.getExcludeProviders();
            allowImplicitInclusions = profile.getAllowImplicitProviderInclusions();
        }
        else if(profilableObject instanceof QueryType)
        {
            includeList = profile.getIncludeQueryTypes();
            excludeList = profile.getExcludeQueryTypes();
            allowImplicitInclusions = profile.getAllowImplicitQueryTypeInclusions();
        }
        else if(profilableObject instanceof NormalisationRule)
        {
            includeList = profile.getIncludeRdfRules();
            excludeList = profile.getExcludeRdfRules();
            allowImplicitInclusions = profile.getAllowImplicitRdfRuleInclusions();
        }
        else
        {
            throw new RuntimeException(
                    "ProfileImpl.usedWithProfilable: Did not recognise the type for object profilableObject="
                            + profilableObject.toString());
        }
        
        ProfileMatch trueResult =
                ProfileUtils.usedWithIncludeExcludeList(profilableObject.getKey(),
                        profilableObject.getProfileIncludeExcludeOrder(), includeList, excludeList,
                        profile.getDefaultProfileIncludeExcludeOrder());
        
        if(trueResult == ProfileMatch.IMPLICIT_INCLUDE)
        {
            if(ProfileUtils._DEBUG)
            {
                ProfileUtils.log
                        .debug("ProfileImpl.usedWithProfilable: found implicit match profilableObject.getKey()="
                                + profilableObject.getKey() + " nextIncludeExcludeOrder="
                                + profilableObject.getProfileIncludeExcludeOrder() + " allowImplicitInclusions="
                                + allowImplicitInclusions);
            }
            
            if(allowImplicitInclusions)
            {
                return ProfileMatch.IMPLICIT_INCLUDE;
            }
            else
            {
                return ProfileMatch.NO_MATCH;
            }
        }
        
        return trueResult;
    }
    
    public static List<Profile> getAndSortProfileList(Collection<URI> nextProfileUriList, SortOrder nextSortOrder,
            Map<URI, Profile> allProfiles)
    {
        // Map<URI, Profile> allProfiles = this.getAllProfiles();
        final List<Profile> results = new LinkedList<Profile>();
        
        if(nextProfileUriList == null)
        {
            ProfileUtils.log.error("getAndSortProfileList: nextProfileUriList was null!");
            
            throw new RuntimeException("getAndSortProfileList: nextProfileUriList was null!");
        }
        else
        {
            for(final URI nextProfileUri : nextProfileUriList)
            {
                // log.error("Settings.getAndSortProfileList: nextProfileUri="+nextProfileUri);
                if(allProfiles.containsKey(nextProfileUri))
                {
                    final Profile nextProfileObject = allProfiles.get(nextProfileUri);
                    results.add(nextProfileObject);
                }
                else if(ProfileUtils._INFO)
                {
                    ProfileUtils.log.info("getAndSortProfileList: Could not get profile by URI nextProfileUri="
                            + nextProfileUri);
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
