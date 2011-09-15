/**
 * 
 */
package org.queryall.impl.test;

import org.queryall.api.ruletest.RuleTest;
import org.queryall.api.test.AbstractRuleTestTest;
import org.queryall.impl.ruletest.StringRuleTestImpl;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class StringRuleTestImplTest extends AbstractRuleTestTest
{
    
    @Override
    public RuleTest getNewTestRuleTest()
    {
        return new StringRuleTestImpl();
    }
    
}
