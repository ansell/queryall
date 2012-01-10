/**
 * 
 */
package org.queryall.api.test;

import org.queryall.api.profile.Profile;
import org.queryall.api.provider.Provider;

/**
 * Test the DummyProvider interface is implemented according to the Provider contract using
 * AbstractProviderTest.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class DummyProviderTest extends AbstractProviderTest
{
    
    @Override
    public Profile getNewTestProfile()
    {
        return new DummyProfile();
    }
    
    @Override
    public Provider getNewTestProvider()
    {
        return new DummyProvider();
    }
    
}
