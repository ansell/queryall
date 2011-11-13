/**
 * 
 */
package org.queryall.exception;

import org.queryall.api.rdfrule.NormalisationRuleEnum;

/**
 * An exception that is thrown when an unknown NormalisationRule is encountered
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class UnsupportedNormalisationRuleException extends QueryAllException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 9132659393857953163L;
    private NormalisationRuleEnum ruleCause;
    
    /**
     * 
     */
    public UnsupportedNormalisationRuleException()
    {
        super();
    }
    
    /**
     * @param message
     */
    public UnsupportedNormalisationRuleException(final String message)
    {
        super(message);
    }
    
    /**
     * @param message
     */
    public UnsupportedNormalisationRuleException(final String message, final NormalisationRuleEnum nextRule)
    {
        super(message);
        this.setRuleCause(nextRule);
    }
    
    /**
     * @param message
     * @param cause
     */
    public UnsupportedNormalisationRuleException(final String message, final NormalisationRuleEnum nextRule,
            final Throwable cause)
    {
        super(message, cause);
        this.setRuleCause(nextRule);
    }
    
    /**
     * @param message
     * @param cause
     */
    public UnsupportedNormalisationRuleException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
    
    /**
     * @param cause
     */
    public UnsupportedNormalisationRuleException(final Throwable cause)
    {
        super(cause);
    }
    
    /**
     * @return the ruleCause
     */
    public NormalisationRuleEnum getRuleCause()
    {
        return this.ruleCause;
    }
    
    /**
     * @param ruleCause
     *            the ruleCause to set
     */
    public void setRuleCause(final NormalisationRuleEnum ruleCause)
    {
        this.ruleCause = ruleCause;
    }
    
}
