/**
 * 
 */
package org.queryall.impl.test;

import org.openrdf.model.URI;
import org.queryall.api.Profile;

/**
 * Provides the implementation of the Provider class for the Abstract test class provided with
 * queryall-api-tests.jar
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class BaseProfilableNormalisationRuleTest extends BaseProfilableTest
{
    @Override
    public void includeTrueUri(Profile profilable, URI uriToInclude)
    {
        profilable.addIncludeRdfRule(uriToInclude);
    }
    
    @Override
    public void includeFalseUri(Profile profilable, URI uriToExclude)
    {
        profilable.addExcludeRdfRule(uriToExclude);
    }
}
