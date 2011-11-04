/**
 * 
 */
package org.queryall.exception;

/**
 * An exception that is thrown when an unknown NormalisationRule is given to the
 * NormalisationRuleRegistry
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class UnsupportedNormalisationRuleException extends QueryAllException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 9132659393857953163L;
    
    /**
     * 
     */
    public UnsupportedNormalisationRuleException()
    {
        super();
    }
    
    /**
     * @param message
     */
    public UnsupportedNormalisationRuleException(final String message)
    {
        super(message);
    }
    
    /**
     * @param message
     * @param cause
     */
    public UnsupportedNormalisationRuleException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
    
    /**
     * @param cause
     */
    public UnsupportedNormalisationRuleException(final Throwable cause)
    {
        super(cause);
    }
    
}
