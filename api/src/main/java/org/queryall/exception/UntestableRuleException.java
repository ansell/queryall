/**
 * 
 */
package org.queryall.exception;

import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.ruletest.RuleTest;

/**
 * An exception that is thrown when an unknown RuleTest is encountered.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class UntestableRuleException extends QueryAllException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 9132659393857953163L;
    private RuleTest ruleTestCause;
    private NormalisationRule ruleCause;
    
    /**
     * 
     */
    public UntestableRuleException()
    {
        super();
    }
    
    /**
     * @param message
     */
    public UntestableRuleException(final String message)
    {
        super(message);
    }
    
    /**
     * @param message
     */
    public UntestableRuleException(final String message, final NormalisationRule nextRule, final RuleTest nextRuleTest)
    {
        super(message);
        this.setRuleCause(nextRule);
        this.setRuleTestCause(nextRuleTest);
    }
    
    /**
     * @param message
     * @param cause
     */
    public UntestableRuleException(final String message, final NormalisationRule nextRule, final RuleTest nextRuleTest,
            final Throwable cause)
    {
        super(message, cause);
        this.setRuleCause(nextRule);
        this.setRuleTestCause(nextRuleTest);
    }
    
    /**
     * @param message
     * @param cause
     */
    public UntestableRuleException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
    
    /**
     * @param cause
     */
    public UntestableRuleException(final Throwable cause)
    {
        super(cause);
    }
    
    /**
     * @return the ruleCause
     */
    public NormalisationRule getRuleCause()
    {
        return this.ruleCause;
    }
    
    /**
     * @return the ruleTestCause
     */
    public RuleTest getRuleTestCause()
    {
        return this.ruleTestCause;
    }
    
    /**
     * @param nextRuleCause
     *            the ruleCause to set
     */
    public void setRuleCause(final NormalisationRule nextRuleCause)
    {
        this.ruleCause = nextRuleCause;
    }
    
    /**
     * @param nextRuleTestCause
     *            the ruleTestCause to set
     */
    public void setRuleTestCause(final RuleTest nextRuleTestCause)
    {
        this.ruleTestCause = nextRuleTestCause;
    }
    
}
