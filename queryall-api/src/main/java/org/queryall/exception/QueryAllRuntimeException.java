package org.queryall.exception;

public class QueryAllRuntimeException extends RuntimeException
{
    /**
     * 
     */
    private static final long serialVersionUID = -2658083067730722287L;
    
    /**
     * 
     */
    public QueryAllRuntimeException()
    {
        super();
    }
    
    /**
     * @param message
     */
    public QueryAllRuntimeException(final String message)
    {
        super(message);
    }
    
    /**
     * @param message
     * @param cause
     */
    public QueryAllRuntimeException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
    
    /**
     * @param cause
     */
    public QueryAllRuntimeException(final Throwable cause)
    {
        super(cause);
    }
    
}