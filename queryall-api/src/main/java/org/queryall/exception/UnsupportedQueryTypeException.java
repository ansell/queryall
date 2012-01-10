/**
 * 
 */
package org.queryall.exception;

import org.queryall.api.querytype.QueryTypeEnum;

/**
 * An exception that is thrown when an unknown QueryType is encountered.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class UnsupportedQueryTypeException extends QueryAllException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 6962509127774602019L;
    private QueryTypeEnum queryTypeCause;
    
    /**
     * 
     */
    public UnsupportedQueryTypeException()
    {
        super();
    }
    
    /**
     * @param message
     */
    public UnsupportedQueryTypeException(final String message)
    {
        super(message);
    }
    
    /**
     * @param message
     */
    public UnsupportedQueryTypeException(final String message, final QueryTypeEnum nextQueryType)
    {
        super(message);
        this.setQueryTypeCause(nextQueryType);
    }
    
    /**
     * @param message
     * @param cause
     */
    public UnsupportedQueryTypeException(final String message, final QueryTypeEnum nextQueryType, final Throwable cause)
    {
        super(message, cause);
        this.setQueryTypeCause(nextQueryType);
    }
    
    /**
     * @param message
     * @param cause
     */
    public UnsupportedQueryTypeException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
    
    /**
     * @param cause
     */
    public UnsupportedQueryTypeException(final Throwable cause)
    {
        super(cause);
    }
    
    /**
     * @return the queryTypeCause
     */
    public QueryTypeEnum getQueryTypeCause()
    {
        return this.queryTypeCause;
    }
    
    /**
     * @param nextQueryTypeCause
     *            the queryTypeCause to set
     */
    public void setQueryTypeCause(final QueryTypeEnum nextQueryTypeCause)
    {
        this.queryTypeCause = nextQueryTypeCause;
    }
    
}
