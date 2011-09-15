/**
 * 
 */
package org.queryall.impl.test;

import org.queryall.api.ruletest.RuleTest;
import org.queryall.api.test.AbstractRuleTestTest;
import org.queryall.impl.ruletest.SparqlRuleTestImpl;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class SparqlRuleTestImplTest extends AbstractRuleTestTest
{
    @Override
    public RuleTest getNewTestRuleTest()
    {
        return new SparqlRuleTestImpl();
    }
    
}
