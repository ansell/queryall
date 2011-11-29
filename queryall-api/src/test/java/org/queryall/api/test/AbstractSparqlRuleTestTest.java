/**
 * 
 */
package org.queryall.api.test;

import org.junit.After;
import org.junit.Before;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.api.ruletest.SparqlRuleTest;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractSparqlRuleTestTest extends AbstractRuleTestTest
{
    
    /**
     * Returns a new instance of the RuleTest Implementation for each call
     * 
     * @return
     */
    @Override
    public final RuleTest getNewTestRuleTest()
    {
        return this.getNewTestSparqlRuleTest();
    }
    
    /**
     * Returns a new instance of the SparqlRuleTest Implementation for each call
     * 
     * @return
     */
    public abstract SparqlRuleTest getNewTestSparqlRuleTest();
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        
    }
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    @After
    public void tearDown() throws Exception
    {
        super.tearDown();
        
    }
    
}
