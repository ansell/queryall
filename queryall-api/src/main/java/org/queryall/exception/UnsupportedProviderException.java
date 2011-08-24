/**
 * 
 */
package org.queryall.exception;

/**
 * An exception that is thrown when an unknown Provider is given to the ProviderRegistry
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class UnsupportedProviderException extends RuntimeException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 6962509127774602019L;
    
    /**
     * 
     */
    public UnsupportedProviderException()
    {
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @param message
     */
    public UnsupportedProviderException(final String message)
    {
        super(message);
    }
    
    /**
     * @param message
     * @param cause
     */
    public UnsupportedProviderException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
    
    /**
     * @param cause
     */
    public UnsupportedProviderException(final Throwable cause)
    {
        super(cause);
    }
    
}
