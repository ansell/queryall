/**
 * 
 */
package org.queryall.exception;

/**
 * Validation exception thrown when a normalisation rule failed
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ValidationFailedException extends QueryAllException
{
    /**
     * 
     */
    private static final long serialVersionUID = 6803456348739020133L;
    
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
     * @param cause
     */
    public ValidationFailedException(final Throwable cause)
    {
        super(cause);
    }
    
}
