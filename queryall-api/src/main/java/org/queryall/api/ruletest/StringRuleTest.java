/**
 * 
 */
package org.queryall.api.ruletest;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface StringRuleTest extends RuleTest
{
    /**
     * 
     * @return The test input string that is to be used as input for denormalisation test stages and output for renormalisation test stages
     */
    String getTestInputString();
    
    /**
     * 
     * @return The test output string that is to be used as output for denormalisation test stages and input for renormalisation test stages
     */
    String getTestOutputString();
    
    /**
     * 
     * @param testInputString The test input string that is to be used as input for denormalisation test stages and output for renormalisation test stages
     */
    void setTestInputString(String testInputString);
    
    /**
     * 
     * @param testOutputString The test output string that is to be used as output for denormalisation test stages and input for renormalisation test stages
     */
    void setTestOutputString(String testOutputString);
    
}
