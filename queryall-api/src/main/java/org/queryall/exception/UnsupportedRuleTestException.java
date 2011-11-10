/**
 * 
 */
package org.queryall.exception;

/**
 * An exception that is thrown when an unknown RuleTest is given to the RuleTestRegistry
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class UnsupportedRuleTestException extends QueryAllException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 9132659393857953163L;
    
    /**
     * 
     */
    public UnsupportedRuleTestException()
    {
        super();
    }
    
    /**
     * @param message
     */
    public UnsupportedRuleTestException(final String message)
    {
        super(message);
    }
    
    /**
     * @param message
     * @param cause
     */
    public UnsupportedRuleTestException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
    
    /**
     * @param cause
     */
    public UnsupportedRuleTestException(final Throwable cause)
    {
        super(cause);
    }
    
}
