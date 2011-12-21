/**
 * 
 */
package org.queryall.api.test;

import org.queryall.api.profile.Profile;

/**
 * Test the DummyProfile interface is implemented according to the Profile contract using
 * AbstractProfileTest
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class DummyProfileTest extends AbstractProfileTest
{
    
    @Override
    public Profile getNewTestProfile()
    {
        return new DummyProfile();
    }
    
}
