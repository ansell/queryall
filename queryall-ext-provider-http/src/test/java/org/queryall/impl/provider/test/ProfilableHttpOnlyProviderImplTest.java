/**
 * 
 */
package org.queryall.impl.provider.test;

import org.queryall.api.base.ProfilableInterface;
import org.queryall.api.profile.Profile;
import org.queryall.api.test.AbstractProfilableProviderTest;
import org.queryall.impl.profile.ProfileImpl;
import org.queryall.impl.provider.HttpOnlyProviderImpl;

/**
 * Provides the implementation of the Provider class for the Abstract test class provided with
 * queryall-api-tests.jar
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProfilableHttpOnlyProviderImplTest extends AbstractProfilableProviderTest
{
    @Override
    public Profile getNewTestProfile()
    {
        return new ProfileImpl();
    }

    @Override
    public ProfilableInterface getNewTestProfilable()
    {
        return new HttpOnlyProviderImpl();
    }
}
