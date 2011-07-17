package org.queryall.api.test;

import java.util.List;

import org.queryall.api.ProfilableInterface;
import org.queryall.api.Profile;

import static org.junit.Assert.*;

public final class ProfilableTestUtil
{
    protected final static void testIsUsedWithProfileList(ProfilableInterface profilableObject, List<Profile> profileList, boolean test1Result, boolean test2Result, boolean test3Result, boolean test4Result)
    {
        assertEquals(test1Result, profilableObject.isUsedWithProfileList(profileList, true, true));
        assertEquals(test2Result, profilableObject.isUsedWithProfileList(profileList, false, true));
        assertEquals(test3Result, profilableObject.isUsedWithProfileList(profileList, true, false));
        assertEquals(test4Result, profilableObject.isUsedWithProfileList(profileList, false, false));
    }

}
