/**
 * 
 */
package org.queryall.exception;

import org.queryall.api.querytype.QueryType;

/**
 * An exception that is thrown when an unknown QueryType is encountered
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class UnsupportedQueryTypeException extends QueryAllException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 6962509127774602019L;
    private QueryType queryTypeCause;
    
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
    public UnsupportedQueryTypeException(final String message, QueryType nextQueryType)
    {
        super(message);
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
     * @param message
     * @param cause
     */
    public UnsupportedQueryTypeException(final String message, QueryType nextQueryType, final Throwable cause)
    {
        super(message, cause);
        this.setQueryTypeCause(nextQueryType);
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
    public QueryType getQueryTypeCause()
    {
        return queryTypeCause;
    }

    /**
     * @param queryTypeCause the queryTypeCause to set
     */
    public void setQueryTypeCause(QueryType queryTypeCause)
    {
        this.queryTypeCause = queryTypeCause;
    }
    
}
