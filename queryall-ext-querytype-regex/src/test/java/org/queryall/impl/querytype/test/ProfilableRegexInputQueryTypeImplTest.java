/**
 * 
 */
package org.queryall.impl.querytype.test;

import org.openrdf.model.URI;
import org.queryall.api.base.ProfilableInterface;
import org.queryall.api.profile.Profile;
import org.queryall.api.test.AbstractProfilableTest;
import org.queryall.impl.profile.ProfileImpl;
import org.queryall.impl.querytype.RegexInputQueryTypeImpl;

/**
 * Provides the implementation of the Provider class for the Abstract test class provided with
 * queryall-api-tests.jar
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProfilableRegexInputQueryTypeImplTest extends AbstractProfilableTest
{
    @Override
    public Profile getNewTestProfile()
    {
        return new ProfileImpl();
    }

    @Override
    public ProfilableInterface getNewTestProfilable()
    {
        return new RegexInputQueryTypeImpl();
    }
    
    @Override
    public void includeFalseUri(final Profile profilable, final URI uriToExclude)
    {
        profilable.addExcludeQueryType(uriToExclude);
    }
    
    @Override
    public void includeTrueUri(final Profile profilable, final URI uriToInclude)
    {
        profilable.addIncludeQueryType(uriToInclude);
    }
}
