/**
 * 
 */
package org.queryall.impl.provider.test;

import org.queryall.api.base.ProfilableInterface;
import org.queryall.api.profile.Profile;
import org.queryall.api.test.AbstractProfilableProviderTest;
import org.queryall.impl.profile.ProfileImpl;
import org.queryall.impl.provider.HttpOnlyProviderImpl;
import org.queryall.impl.provider.HttpRdfProviderImpl;

/**
 * Provides the implementation of the Provider class for the Abstract test class provided with
 * queryall-api-tests.jar
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProfilableHttpRdfProviderImplTest extends AbstractProfilableProviderTest
{
    @Override
    public ProfilableInterface getNewTestProfilable()
    {
        return new HttpRdfProviderImpl();
    }
    
    @Override
    public Profile getNewTestProfile()
    {
        return new ProfileImpl();
    }
}
