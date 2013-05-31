/**
 * 
 */
package org.queryall.exception;

import org.queryall.api.base.BaseQueryAllInterface;

/**
 * Exception that is thrown when a setting already exists, and the given object could not be
 * inserted instead.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class SettingAlreadyExistsException extends QueryAllException
{
    private static final long serialVersionUID = -5216073606677985551L;
    
    private BaseQueryAllInterface object;
    
    /**
     * @param message
     */
    public SettingAlreadyExistsException(final String message, final BaseQueryAllInterface object)
    {
        super(message);
        this.object = object;
    }
    
    /**
     * @param message
     * @param cause
     */
    public SettingAlreadyExistsException(final String message, final Throwable cause, final BaseQueryAllInterface object)
    {
        super(message, cause);
        this.object = object;
    }
    
    /**
     * @param cause
     */
    public SettingAlreadyExistsException(final Throwable cause, final BaseQueryAllInterface object)
    {
        super(cause);
        this.object = object;
    }
    
    public BaseQueryAllInterface getObject()
    {
        return this.object;
    }
    
}
