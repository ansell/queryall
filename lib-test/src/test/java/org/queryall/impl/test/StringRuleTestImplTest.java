/**
 * 
 */
package org.queryall.impl.test;

import org.queryall.api.ruletest.StringRuleTest;
import org.queryall.api.test.AbstractStringRuleTestTest;
import org.queryall.impl.ruletest.StringRuleTestImpl;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class StringRuleTestImplTest extends AbstractStringRuleTestTest
{
    
    @Override
    public StringRuleTest getNewTestStringRuleTest()
    {
        return new StringRuleTestImpl();
    }
    
}
