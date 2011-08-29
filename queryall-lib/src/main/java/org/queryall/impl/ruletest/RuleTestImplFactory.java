/**
 * 
 */
package org.queryall.impl.ruletest;

import org.kohsuke.MetaInfServices;
import org.queryall.api.ruletest.RuleTestEnum;
import org.queryall.api.ruletest.RuleTestFactory;
import org.queryall.api.ruletest.RuleTestParser;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices
public class RuleTestImplFactory implements RuleTestFactory
{
    /**
     * Returns the enumeration from the enumeration that matches this factory
     */
    @Override
    public RuleTestEnum getEnum()
    {
        return RuleTestImplEnum.RULE_TEST_IMPL_ENUM;
        // return NormalisationRuleEnum.valueOf(RegexNormalisationRuleImpl.class.getName());
    }
    
    /**
     * Returns the parser from the enumeration that matches this factory
     */
    @Override
    public RuleTestParser getParser()
    {
        return new RuleTestImplParser();
    }
    
}
