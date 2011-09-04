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
public class StringRuleTestImplFactory implements RuleTestFactory
{
    /**
     * Returns the enumeration from the enumeration that matches this factory
     */
    @Override
    public RuleTestEnum getEnum()
    {
        return StringRuleTestImplEnum.STRING_RULE_TEST_IMPL_ENUM;
    }
    
    /**
     * Returns the parser from the enumeration that matches this factory
     */
    @Override
    public RuleTestParser getParser()
    {
        return new StringRuleTestImplParser();
    }
    
}
