/**
 * 
 */
package org.queryall.exception;

import org.queryall.api.ruletest.RuleTest;

/**
 * An exception that is thrown when an unknown RuleTest is encountered
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class UnsupportedRuleTestException extends QueryAllException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 9132659393857953163L;
    private RuleTest ruleTestCause;
    
    /**
     * 
     */
    public UnsupportedRuleTestException()
    {
        super();
    }
    
    /**
     * @param message
     */
    public UnsupportedRuleTestException(final String message)
    {
        super(message);
    }
    
    /**
     * @param message
     */
    public UnsupportedRuleTestException(final String message, RuleTest nextRuleTest)
    {
        super(message);
        this.setRuleTestCause(nextRuleTest);
    }
    
    /**
     * @param message
     * @param cause
     */
    public UnsupportedRuleTestException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
    
    /**
     * @param message
     * @param cause
     */
    public UnsupportedRuleTestException(final String message, RuleTest nextRuleTest, final Throwable cause)
    {
        super(message, cause);
        this.setRuleTestCause(nextRuleTest);
    }
    
    /**
     * @param cause
     */
    public UnsupportedRuleTestException(final Throwable cause)
    {
        super(cause);
    }

    /**
     * @return the ruleTestCause
     */
    public RuleTest getRuleTestCause()
    {
        return ruleTestCause;
    }

    /**
     * @param ruleTestCause the ruleTestCause to set
     */
    public void setRuleTestCause(RuleTest ruleTestCause)
    {
        this.ruleTestCause = ruleTestCause;
    }
    
}
