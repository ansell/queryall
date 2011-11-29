/**
 * 
 */
package org.queryall.impl.rule.test;

import org.queryall.api.profile.Profile;
import org.queryall.api.rdfrule.XsltNormalisationRule;
import org.queryall.api.test.AbstractXsltNormalisationRuleTest;
import org.queryall.impl.profile.ProfileImpl;
import org.queryall.impl.rdfrule.XsltTransformingRuleImpl;

/**
 * 
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class XsltNormalisationRuleImplTest extends AbstractXsltNormalisationRuleTest
{
    @Override
    public Profile getNewTestProfile()
    {
        return new ProfileImpl();
    }
    
    @Override
    public XsltNormalisationRule getNewTestXsltNormalisationRule()
    {
        return new XsltTransformingRuleImpl();
    }
}
