package org.queryall.api.test;

import java.util.List;

import org.junit.Assert;
import org.queryall.api.base.ProfilableInterface;
import org.queryall.api.profile.Profile;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public final class ProfilableTestUtil
{
    protected static void testIsUsedWithProfileList(final ProfilableInterface profilableObject,
            final List<Profile> profileList, final boolean test1Result, final boolean test2Result,
            final boolean test3Result, final boolean test4Result)
    {
        Assert.assertEquals(test1Result, profilableObject.isUsedWithProfileList(profileList, true, true));
        Assert.assertEquals(test2Result, profilableObject.isUsedWithProfileList(profileList, false, true));
        Assert.assertEquals(test3Result, profilableObject.isUsedWithProfileList(profileList, true, false));
        Assert.assertEquals(test4Result, profilableObject.isUsedWithProfileList(profileList, false, false));
    }
    
}
