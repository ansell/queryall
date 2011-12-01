/**
 * 
 */
package org.queryall.api.test;

import org.junit.Test;
import org.openrdf.model.URI;
import org.queryall.api.profile.Profile;
import org.queryall.api.rdfrule.NormalisationRule;

/**
 * 
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractProfilableNormalisationRuleTest extends AbstractProfilableTest
{
    @Override
    public abstract NormalisationRule getNewTestProfilable();
    
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
