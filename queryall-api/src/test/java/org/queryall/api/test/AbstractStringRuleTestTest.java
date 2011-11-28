/**
 * 
 */
package org.queryall.api.test;

import org.junit.After;
import org.junit.Before;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.api.ruletest.StringRuleTest;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractStringRuleTestTest extends AbstractRuleTestTest
{
    
    /**
     * Returns a new instance of the RuleTest Implementation for each call
     * 
     * @return
     */
    @Override
    public final RuleTest getNewTestRuleTest()
    {
        return this.getNewTestStringRuleTest();
    }
    
    /**
     * Returns a new instance of the StringRuleTest Implementation for each call
     * 
     * @return
     */
    public abstract StringRuleTest getNewTestStringRuleTest();
    
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
