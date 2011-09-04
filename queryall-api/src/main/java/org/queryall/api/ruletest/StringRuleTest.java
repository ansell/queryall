/**
 * 
 */
package org.queryall.api.ruletest;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface StringRuleTest extends RuleTest
{
    String getTestInputString();
    
    String getTestOutputString();
    
    void setTestInputString(String testInputString);
    
    void setTestOutputString(String testOutputString);
    
}
