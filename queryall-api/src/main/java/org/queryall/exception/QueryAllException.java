package org.queryall.exception;

public class QueryAllException extends RuntimeException
{
    /**
     * 
     */
    private static final long serialVersionUID = -2658083067730722287L;
    
    /**
     * 
     */
    public QueryAllException()
    {
        super();
    }
    
    /**
     * @param message
     */
    public QueryAllException(final String message)
    {
        super(message);
    }
    
    /**
     * @param message
     * @param cause
     */
    public QueryAllException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
    
    /**
     * @param cause
     */
    public QueryAllException(final Throwable cause)
    {
        super(cause);
    }
    
}