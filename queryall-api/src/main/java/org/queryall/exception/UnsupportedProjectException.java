/**
 * 
 */
package org.queryall.exception;

/**
 * An exception that is thrown when an unknown Project is given to the ProjectRegistry
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class UnsupportedProjectException extends QueryAllException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 9132659393857953163L;
    
    /**
     * 
     */
    public UnsupportedProjectException()
    {
        super();
    }
    
    /**
     * @param message
     */
    public UnsupportedProjectException(final String message)
    {
        super(message);
    }
    
    /**
     * @param message
     * @param cause
     */
    public UnsupportedProjectException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
    
    /**
     * @param cause
     */
    public UnsupportedProjectException(final Throwable cause)
    {
        super(cause);
    }
    
}
