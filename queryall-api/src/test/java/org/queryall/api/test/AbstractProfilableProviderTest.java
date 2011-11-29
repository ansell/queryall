/**
 * 
 */
package org.queryall.api.test;

import org.openrdf.model.URI;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.Provider;

/**
 * 
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractProfilableProviderTest extends AbstractProfilableTest
{
    @Override
    public abstract Provider getNewTestProfilable();
    
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
