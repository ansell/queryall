/**
 * 
 */
package org.queryall.api.ruletest;

/**
 * A StringRuleTest determines whether a given normalised input string is transformed into the given
 * denormalised output string by the given series of rules. It can also determine whether the given
 * denormalised output string is converted back into the normalised input string for the
 * normalisation rule stages after the results are returned.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface StringRuleTest extends RuleTest
{
    /**
     * 
     * @return The test input string that is to be used as input for denormalisation test stages and
     *         output for renormalisation test stages
     */
    String getTestInputString();
    
    /**
     * 
     * @return The test output string that is to be used as output for denormalisation test stages
     *         and input for renormalisation test stages
     */
    String getTestOutputString();
    
    /**
     * 
     * @param testInputString
     *            The test input string that is to be used as input for denormalisation test stages
     *            and output for renormalisation test stages
     */
    void setTestInputString(String testInputString);
    
    /**
     * 
     * @param testOutputString
     *            The test output string that is to be used as output for denormalisation test
     *            stages and input for renormalisation test stages
     */
    void setTestOutputString(String testOutputString);
    
}
