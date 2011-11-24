/**
 * 
 */
package org.queryall.impl.rule.test;

import org.queryall.api.base.ProfilableInterface;
import org.queryall.api.profile.Profile;
import org.queryall.api.test.AbstractProfilableNormalisationRuleTest;
import org.queryall.impl.profile.ProfileImpl;
import org.queryall.impl.rdfrule.SparqlAskRuleImpl;

/**
 * 
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProfilableSparqlAskRuleImplTest extends AbstractProfilableNormalisationRuleTest
{
    @Override
    public ProfilableInterface getNewTestProfilable()
    {
        return new SparqlAskRuleImpl();
    }
    
    @Override
    public Profile getNewTestProfile()
    {
        return new ProfileImpl();
    }
}
