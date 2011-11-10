/**
 * 
 */
package org.queryall.impl.provider.test;

import org.openrdf.model.URI;
import org.queryall.api.base.ProfilableInterface;
import org.queryall.api.profile.Profile;
import org.queryall.api.test.AbstractProfilableTest;
import org.queryall.impl.profile.ProfileImpl;
import org.queryall.impl.provider.HttpOnlyProviderImpl;

/**
 * Provides the implementation of the Provider class for the Abstract test class provided with
 * queryall-api-tests.jar
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProfilableHttpOnlyProviderImplTest extends AbstractProfilableTest
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
    
    @Override
    public void includeFalseUri(final Profile profilable, final URI uriToExclude)
    {
        profilable.addExcludeProvider(uriToExclude);
    }
    
    @Override
    public void includeTrueUri(final Profile profilable, final URI uriToInclude)
    {
        profilable.addIncludeProvider(uriToInclude);
    }
}
