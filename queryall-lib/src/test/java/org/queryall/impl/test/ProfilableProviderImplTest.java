/**
 * 
 */
package org.queryall.impl.test;

import org.openrdf.model.URI;
import org.queryall.api.ProfilableInterface;
import org.queryall.api.Profile;
import org.queryall.impl.HttpProviderImpl;

/**
 * Provides the implementation of the Provider class for the Abstract test class provided with
 * queryall-api-tests.jar
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProfilableProviderImplTest extends BaseProfilableTest
{
    @Override
    public ProfilableInterface getNewTestProfilable()
    {
        return new HttpProviderImpl();
    }
    
    @Override
    public void includeTrueUri(Profile profilable, URI uriToInclude)
    {
        profilable.addIncludeProvider(uriToInclude);
    }
    
    @Override
    public void includeFalseUri(Profile profilable, URI uriToExclude)
    {
        profilable.addExcludeProvider(uriToExclude);
    }
}
