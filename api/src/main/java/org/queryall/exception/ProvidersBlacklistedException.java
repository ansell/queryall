/**
 * 
 */
package org.queryall.exception;

/**
 * This exception is thrown when at least one of the suitable providers for a query was blacklisted,
 * and this blacklisting may have been the cause for an empty set of planned queries.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class ProvidersBlacklistedException extends QueryAllException
{
    /**
     * 
     */
    private static final long serialVersionUID = 5159239822862462725L;
    
    /**
     * 
     */
    public ProvidersBlacklistedException()
    {
        super();
    }
    
    /**
     * @param message
     */
    public ProvidersBlacklistedException(final String message)
    {
        super(message);
    }
    
    /**
     * @param message
     * @param cause
     */
    public ProvidersBlacklistedException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
    
    /**
     * @param cause
     */
    public ProvidersBlacklistedException(final Throwable cause)
    {
        super(cause);
    }
    
}
