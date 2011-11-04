/**
 * 
 */
package org.queryall.exception;

/**
 * An exception that is thrown when an unknown QueryType is given to the QueryTypeRegistry
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class UnsupportedQueryTypeException extends QueryAllException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 6962509127774602019L;
    
    /**
     * 
     */
    public UnsupportedQueryTypeException()
    {
        super();
    }
    
    /**
     * @param message
     */
    public UnsupportedQueryTypeException(final String message)
    {
        super(message);
    }
    
    /**
     * @param message
     * @param cause
     */
    public UnsupportedQueryTypeException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
    
    /**
     * @param cause
     */
    public UnsupportedQueryTypeException(final Throwable cause)
    {
        super(cause);
    }
    
}
