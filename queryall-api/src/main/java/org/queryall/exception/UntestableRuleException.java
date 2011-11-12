/**
 * 
 */
package org.queryall.exception;

import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.api.ruletest.RuleTestEnum;

/**
 * An exception that is thrown when an unknown RuleTest is encountered
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
    public UntestableRuleException(final String message, NormalisationRule nextRule, RuleTest nextRuleTest)
    {
        super(message);
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
     * @param message
     * @param cause
     */
    public UntestableRuleException(final String message, NormalisationRule nextRule, RuleTest nextRuleTest, final Throwable cause)
    {
        super(message, cause);
        this.setRuleCause(nextRule);
        this.setRuleTestCause(nextRuleTest);
    }
    
    /**
     * @param cause
     */
    public UntestableRuleException(final Throwable cause)
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

    /**
     * @return the ruleCause
     */
    public NormalisationRule getRuleCause()
    {
        return ruleCause;
    }

    /**
     * @param ruleCause the ruleCause to set
     */
    public void setRuleCause(NormalisationRule ruleCause)
    {
        this.ruleCause = ruleCause;
    }
    
}
