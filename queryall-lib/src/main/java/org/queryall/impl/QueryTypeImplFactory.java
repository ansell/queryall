/**
 * 
 */
package org.queryall.impl;

import org.queryall.api.querytype.QueryTypeEnum;
import org.queryall.api.querytype.QueryTypeFactory;
import org.queryall.api.querytype.QueryTypeParser;

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
