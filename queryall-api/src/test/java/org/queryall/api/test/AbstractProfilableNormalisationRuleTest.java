/**
 * 
 */
package org.queryall.api.test;

import org.openrdf.model.URI;
import org.queryall.api.profile.Profile;

/**
 * Provides the implementation of the Provider class for the Abstract test class provided with
 * queryall-api-tests.jar
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractProfilableNormalisationRuleTest extends AbstractProfilableTest
{
    @Override
    public void includeFalseUri(final Profile profilable, final URI uriToExclude)
    {
        profilable.addExcludeRdfRule(uriToExclude);
    }
    
    @Override
    public void includeTrueUri(final Profile profilable, final URI uriToInclude)
    {
        profilable.addIncludeRdfRule(uriToInclude);
    }
}
