/**
 * 
 */
package org.queryall.exception;

/**
 * An exception that is thrown when an unknown NamespaceEntry is given to the NamespaceEntryRegistry
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class UnsupportedNamespaceEntryException extends QueryAllException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 9132659393857953163L;
    
    /**
     * 
     */
    public UnsupportedNamespaceEntryException()
    {
        super();
    }
    
    /**
     * @param message
     */
    public UnsupportedNamespaceEntryException(final String message)
    {
        super(message);
    }
    
    /**
     * @param message
     * @param cause
     */
    public UnsupportedNamespaceEntryException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
    
    /**
     * @param cause
     */
    public UnsupportedNamespaceEntryException(final Throwable cause)
    {
        super(cause);
    }
    
}
