/**
 * 
 */
package org.queryall.exception;

/**
 * An exception that is thrown when an unknown Profile is given to the ProfileRegistry
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class UnsupportedProfileException extends RuntimeException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 9132659393857953163L;
    
    /**
     * 
     */
    public UnsupportedProfileException()
    {
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     */
    public UnsupportedProfileException(final String message)
    {
        super(message);
    }
    
    /**
     * @param message
     * @param cause
     */
    public UnsupportedProfileException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
    
    /**
     * @param cause
     */
    public UnsupportedProfileException(final Throwable cause)
    {
        super(cause);
    }
    
}
