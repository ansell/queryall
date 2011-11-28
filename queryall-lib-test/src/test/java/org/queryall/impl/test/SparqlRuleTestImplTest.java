/**
 * 
 */
package org.queryall.impl.test;

import org.queryall.api.ruletest.SparqlRuleTest;
import org.queryall.api.test.AbstractSparqlRuleTestTest;
import org.queryall.impl.ruletest.SparqlRuleTestImpl;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class SparqlRuleTestImplTest extends AbstractSparqlRuleTestTest
{
    @Override
    public SparqlRuleTest getNewTestSparqlRuleTest()
    {
        return new SparqlRuleTestImpl();
    }
    
}
