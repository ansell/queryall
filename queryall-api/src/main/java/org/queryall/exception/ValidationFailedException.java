/**
 * 
 */
package org.queryall.exception;

import org.queryall.api.rdfrule.ValidatingRule;

/**
 * Validation exception thrown when a normalisation rule failed.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ValidationFailedException extends QueryAllException
{
    /**
     * 
     */
    private static final long serialVersionUID = 6803456348739020133L;
    private ValidatingRule validationRuleCause;
    
    /**
     * 
     */
    public ValidationFailedException()
    {
    }
    
    /**
     * @param message
     */
    public ValidationFailedException(final String message)
    {
        super(message);
    }
    
    /**
     * @param message
     * @param cause
     */
    public ValidationFailedException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
    
    /**
     * @param message
     */
    public ValidationFailedException(final String message, final ValidatingRule nextValidationRule)
    {
        super(message);
        this.setValidationRuleCause(nextValidationRule);
    }
    
    /**
     * @param message
     * @param cause
     */
    public ValidationFailedException(final String message, final ValidatingRule nextValidationRule,
            final Throwable cause)
    {
        super(message, cause);
        this.setValidationRuleCause(nextValidationRule);
    }
    
    /**
     * @param cause
     */
    public ValidationFailedException(final Throwable cause)
    {
        super(cause);
    }
    
    /**
     * @return the validationRuleCause
     */
    public ValidatingRule getValidationRuleCause()
    {
        return this.validationRuleCause;
    }
    
    /**
     * @param nextValidationRuleCause
     *            the validationRuleCause to set
     */
    public void setValidationRuleCause(final ValidatingRule nextValidationRuleCause)
    {
        this.validationRuleCause = nextValidationRuleCause;
    }
    
}
