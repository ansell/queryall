/**
 * 
 */
package org.queryall.impl.rule.test;

import org.queryall.api.base.ProfilableInterface;
import org.queryall.api.profile.Profile;
import org.queryall.api.test.AbstractProfilableNormalisationRuleTest;
import org.queryall.impl.profile.ProfileImpl;
import org.queryall.impl.rdfrule.SparqlConstructRuleImpl;

/**
 * 
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProfilableSparqlConstructRuleImplTest extends AbstractProfilableNormalisationRuleTest
{
    @Override
    public Profile getNewTestProfile()
    {
        return new ProfileImpl();
    }

    @Override
    public ProfilableInterface getNewTestProfilable()
    {
        return new SparqlConstructRuleImpl();
    }
}
