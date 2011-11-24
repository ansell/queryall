/**
 * 
 */
package org.queryall.exception;

import org.queryall.api.ruletest.RuleTestEnum;

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
    private RuleTestEnum ruleTestCause;
    
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
    public UnsupportedRuleTestException(final String message, final RuleTestEnum nextRuleTest)
    {
        super(message);
        this.setRuleTestCause(nextRuleTest);
    }
    
    /**
     * @param message
     * @param cause
     */
    public UnsupportedRuleTestException(final String message, final RuleTestEnum nextRuleTest, final Throwable cause)
    {
        super(message, cause);
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
     * @param cause
     */
    public UnsupportedRuleTestException(final Throwable cause)
    {
        super(cause);
    }
    
    /**
     * @return the ruleTestCause
     */
    public RuleTestEnum getRuleTestCause()
    {
        return this.ruleTestCause;
    }
    
    /**
     * @param ruleTestCause
     *            the ruleTestCause to set
     */
    public void setRuleTestCause(final RuleTestEnum ruleTestCause)
    {
        this.ruleTestCause = ruleTestCause;
    }
    
}
