/**
 * 
 */
package org.queryall.impl.rule.test;

import org.queryall.api.profile.Profile;
import org.queryall.api.rdfrule.RegexNormalisationRule;
import org.queryall.api.test.AbstractRegexNormalisationRuleTest;
import org.queryall.api.test.DummyProfile;
import org.queryall.impl.rdfrule.RegexTransformingRuleImpl;

/**
 * 
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RegexTransformingNormalisationRuleImplTest extends AbstractRegexNormalisationRuleTest
{
    
    @Override
    public Profile getNewTestProfile()
    {
        return new DummyProfile();
    }
    
    @Override
    public RegexNormalisationRule getNewTestRegexNormalisationRule()
    {
        return new RegexTransformingRuleImpl();
    }
}
