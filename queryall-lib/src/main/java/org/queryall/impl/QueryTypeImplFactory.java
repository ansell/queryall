/**
 * 
 */
package org.queryall.impl;

import org.queryall.api.utils.QueryTypeEnum;
import org.queryall.api.utils.QueryTypeFactory;
import org.queryall.api.utils.QueryTypeParser;

/**
 * @author uqpanse1
 *
 */
public class QueryTypeImplFactory implements QueryTypeFactory
{
    
    /**
     * 
     */
    public QueryTypeImplFactory()
    {
        // TODO Auto-generated constructor stub
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.utils.QueryTypeFactory#getQueryType()
     */
    @Override
    public QueryTypeEnum getQueryType()
    {
        return QueryTypeEnum.valueOf(QueryTypeImpl.class.getName());
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.utils.QueryTypeFactory#getParser()
     */
    @Override
    public QueryTypeParser getParser()
    {
        return new QueryTypeImplParser();
    }
    
}
