/**
 * 
 */
package org.queryall.impl.provider.test;

import org.queryall.api.profile.Profile;
import org.queryall.api.provider.Provider;
import org.queryall.api.test.AbstractProviderTest;
import org.queryall.api.test.DummyProfile;
import org.queryall.impl.provider.HttpOnlyProviderImpl;

/**
 * Provides the implementation of the Provider class for the Abstract test class provided with
 * queryall-api-tests.jar.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class HttpOnlyProviderImplTest extends AbstractProviderTest
{
    @Override
    public Profile getNewTestProfile()
    {
        return new DummyProfile();
    }
    
    @Override
    public Provider getNewTestProvider()
    {
        return new HttpOnlyProviderImpl();
    }
}
