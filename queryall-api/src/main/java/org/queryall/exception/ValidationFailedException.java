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
    public ValidationFailedException(String message)
    {
        super(message);
    }
    
    /**
     * @param cause
     */
    public ValidationFailedException(Throwable cause)
    {
        super(cause);
    }
    
    /**
     * @param message
     * @param cause
     */
    public ValidationFailedException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
}
