/**
 * 
 */
package org.queryall.api.utils;

import java.util.Collection;
import java.util.List;

import org.openrdf.model.URI;
import org.queryall.api.base.ProfilableInterface;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.Provider;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.rdfrule.NormalisationRule;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public enum ProfileMatch
{
    /**
     * The Profile specifically includes this Profilable object.
     */
    SPECIFIC_INCLUDE,
    
    /**
     * The Profile specifically excludes this Profilable object.
     */
    SPECIFIC_EXCLUDE,
    
    /**
     * The Profile could implicitly include this Profilable object.
     */
    IMPLICIT_INCLUDE,
    
    /**
     * The Profile does not specifically match or implicitly include this Profilable object.
     */
    NO_MATCH;
    
    public static boolean isUsedWithProfileList(final ProfilableInterface profilableObject,
            final List<Profile> nextSortedProfileList, final boolean recogniseImplicitInclusions,
            final boolean includeNonProfileMatched)
    {
        for(final Profile nextProfile : nextSortedProfileList)
        {
            final ProfileMatch trueResult = ProfileMatch.usedWithProfilable(nextProfile, profilableObject);
            
            if(trueResult == IMPLICIT_INCLUDE)
            {
                if(recogniseImplicitInclusions)
                {
                    return true;
                }
            }
            else if(trueResult == SPECIFIC_INCLUDE)
            {
                return true;
            }
            else if(trueResult == SPECIFIC_EXCLUDE)
            {
                return false;
            }
        }
        
        final boolean returnValue =
                includeNonProfileMatched
                        && (ProfileIncludeExclude.EXCLUDE_THEN_INCLUDE == profilableObject
                                .getProfileIncludeExcludeOrder() || ProfileIncludeExclude.UNDEFINED == profilableObject
                                .getProfileIncludeExcludeOrder());
        
        return returnValue;
    }
    
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
     * @return One of the following constants, ProfileMatch.SPECIFIC_EXCLUDE,
     *         ProfileMatch.SPECIFIC_INCLUDE, ProfileMatch.IMPLICIT_INCLUDE or ProfileMatch.NO_MATCH
     * @throws IllegalArgumentException
     *             if the include or exclude lists are null, or nextIncludeExcludeOrder is not
     *             includeOrExclude or excludeOrInclude and nextDefaultProfileIncludeExcludeOrder
     *             does not help resolve the nextIncludeExcludeOrder
     */
    public static ProfileMatch usedWithIncludeExcludeList(final URI nextUri,
            final ProfileIncludeExclude nextIncludeExcludeOrder, final Collection<URI> includeList,
            final Collection<URI> excludeList, final ProfileIncludeExclude nextDefaultProfileIncludeExcludeOrder)
    {
        if(includeList == null || excludeList == null)
        {
            throw new IllegalArgumentException("usedWithIncludeExcludeList: includeList or excludeList was null");
        }
        
        final boolean includeFound = includeList.contains(nextUri);
        final boolean excludeFound = excludeList.contains(nextUri);
        
        ProfileMatch result;
        
        ProfileIncludeExclude actualIncludeExcludeOrder = nextIncludeExcludeOrder;
        
        if(ProfileIncludeExclude.UNDEFINED == actualIncludeExcludeOrder)
        {
            actualIncludeExcludeOrder = nextDefaultProfileIncludeExcludeOrder;
        }
        
        if(ProfileIncludeExclude.EXCLUDE_THEN_INCLUDE == actualIncludeExcludeOrder)
        {
            if(excludeFound)
            {
                result = SPECIFIC_EXCLUDE;
            }
            else if(includeFound)
            {
                result = SPECIFIC_INCLUDE;
            }
            else
            {
                result = IMPLICIT_INCLUDE;
            }
        }
        else if(ProfileIncludeExclude.INCLUDE_THEN_EXCLUDE == actualIncludeExcludeOrder)
        {
            if(includeFound)
            {
                result = SPECIFIC_INCLUDE;
            }
            else if(excludeFound)
            {
                result = SPECIFIC_EXCLUDE;
            }
            else
            {
                result = NO_MATCH;
            }
        }
        else
        {
            throw new IllegalArgumentException("usedWithIncludeExcludeList: nextIncludeExcludeOrder not recognised ("
                    + nextIncludeExcludeOrder + ") nextDefaultProfileIncludeExcludeOrder ("
                    + nextDefaultProfileIncludeExcludeOrder + ")");
        }
        
        return result;
    }
    
    public static ProfileMatch usedWithProfilable(final Profile profile, final ProfilableInterface profilableObject)
    {
        Collection<URI> includeList;
        Collection<URI> excludeList;
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
            // Add new profilable types here with their corresponding include and exclude lists
            throw new RuntimeException("usedWithProfilable: Did not recognise the type for object profilableObject="
                    + profilableObject.getClass().getName() + " key=" + profilableObject.getKey().stringValue());
        }
        
        final ProfileMatch trueResult =
                ProfileMatch.usedWithIncludeExcludeList(profilableObject.getKey(),
                        profilableObject.getProfileIncludeExcludeOrder(), includeList, excludeList,
                        profile.getDefaultProfileIncludeExcludeOrder());
        
        if(trueResult == IMPLICIT_INCLUDE)
        {
            if(allowImplicitInclusions)
            {
                return IMPLICIT_INCLUDE;
            }
            else
            {
                return NO_MATCH;
            }
        }
        
        return trueResult;
    }
    
}
