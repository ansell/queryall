package org.queryall.api.base;

import java.util.List;

import org.queryall.api.profile.Profile;
import org.queryall.api.utils.ProfileIncludeExclude;

/**
 * This interface provides the basis for profiles to be used to include or exclude objects. All
 * profilable objects are also BaseQueryAllInterface objects.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface ProfilableInterface extends BaseQueryAllInterface
{
    /**
     * @return A URI indicating whether this object should have include or exclude instructions
     *         processed first
     */
    ProfileIncludeExclude getProfileIncludeExcludeOrder();
    
    /**
     * This method determines whether this class should be included or excluded, based on the
     * pre-ordered profile list and the parameters which indicate whether implicit inclusions are
     * allowed and whether this method will match if no profiles match at all.
     * 
     * @param orderedProfileList
     *            a pre sorted list of profiles that will be used in sequence to determine whether
     *            this object will be used
     * @param allowImplicitInclusions
     *            true if implicit inclusions are acceptable, and false if implicit inclusions
     *            should be ignored and the profile list should be processed until an explicit match
     *            is found
     * @param includeNonProfileMatched
     *            true if this object should be included even if no profiles matched, and false if
     *            it is not included when there were no matches, including implicit inclusions if
     *            they are acceptable
     * @return true if this object should be used and false if it should not
     */
    boolean isUsedWithProfileList(List<Profile> orderedProfileList, boolean allowImplicitInclusions,
            boolean includeNonProfileMatched);
    
    /**
     * @param profileIncludeExcludeOrder
     *            A URI indicating whether this object should have include or exclude instructions
     *            processed first
     */
    void setProfileIncludeExcludeOrder(ProfileIncludeExclude profileIncludeExcludeOrder);
    
}