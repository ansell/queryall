/**
 * 
 */
package org.queryall.impl.rule.test;

import org.queryall.api.base.ProfilableInterface;
import org.queryall.api.profile.Profile;
import org.queryall.api.test.AbstractProfilableNormalisationRuleTest;
import org.queryall.impl.profile.ProfileImpl;
import org.queryall.impl.rdfrule.XsltTransformingRuleImpl;

/**
 * 
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProfilableXsltNormalisationRuleImplTest extends AbstractProfilableNormalisationRuleTest
{
    @Override
    public ProfilableInterface getNewTestProfilable()
    {
        return new XsltTransformingRuleImpl();
    }
    
    @Override
    public Profile getNewTestProfile()
    {
        return new ProfileImpl();
    }
}
