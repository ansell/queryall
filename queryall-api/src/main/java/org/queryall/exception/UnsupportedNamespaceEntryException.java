/**
 * 
 */
package org.queryall.exception;

import org.queryall.api.namespace.NamespaceEntryEnum;

/**
 * An exception that is thrown when an unknown NamespaceEntry is encountered.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class UnsupportedNamespaceEntryException extends QueryAllException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 9132659393857953163L;
    private NamespaceEntryEnum namespaceEntryCause;
    
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
     */
    public UnsupportedNamespaceEntryException(final String message, final NamespaceEntryEnum nextNamespaceEntry)
    {
        super(message);
        this.setNamespaceEntryCause(nextNamespaceEntry);
    }
    
    /**
     * @param message
     * @param cause
     */
    public UnsupportedNamespaceEntryException(final String message, final NamespaceEntryEnum nextNamespaceEntry,
            final Throwable cause)
    {
        super(message, cause);
        this.setNamespaceEntryCause(nextNamespaceEntry);
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
    
    /**
     * @return the namespaceEntryCause
     */
    public NamespaceEntryEnum getNamespaceEntryCause()
    {
        return this.namespaceEntryCause;
    }
    
    /**
     * @param nextNamespaceEntryCause
     *            the namespaceEntryCause to set
     */
    public void setNamespaceEntryCause(final NamespaceEntryEnum nextNamespaceEntryCause)
    {
        this.namespaceEntryCause = nextNamespaceEntryCause;
    }
    
}
