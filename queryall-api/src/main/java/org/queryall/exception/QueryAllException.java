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
    public QueryAllException(String message)
    {
        super(message);
    }
    
    /**
     * @param cause
     */
    public QueryAllException(Throwable cause)
    {
        super(cause);
    }
    
    /**
     * @param message
     * @param cause
     */
    public QueryAllException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    
}