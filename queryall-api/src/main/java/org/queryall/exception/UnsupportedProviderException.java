/**
 * 
 */
package org.queryall.exception;

import org.queryall.api.provider.ProviderEnum;

/**
 * An exception that is thrown when an unknown Provider is encountered.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class UnsupportedProviderException extends QueryAllException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 6962509127774602019L;
    private ProviderEnum providerCause;
    
    /**
     * 
     */
    public UnsupportedProviderException()
    {
        super();
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
     */
    public UnsupportedProviderException(final String message, final ProviderEnum nextProvider)
    {
        super(message);
        this.setProviderCause(nextProvider);
    }
    
    /**
     * @param message
     * @param cause
     */
    public UnsupportedProviderException(final String message, final ProviderEnum nextProvider, final Throwable cause)
    {
        super(message, cause);
        this.setProviderCause(nextProvider);
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
    
    /**
     * @return the providerCause
     */
    public ProviderEnum getProviderCause()
    {
        return this.providerCause;
    }
    
    /**
     * @param nextProviderCause
     *            the providerCause to set
     */
    public void setProviderCause(final ProviderEnum nextProviderCause)
    {
        this.providerCause = nextProviderCause;
    }
    
}
