/**
 * 
 */
package org.queryall.impl.querytype;

import org.queryall.api.querytype.QueryTypeEnum;
import org.queryall.api.querytype.QueryTypeFactory;
import org.queryall.api.querytype.QueryTypeParser;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
// @MetaInfServices
public class QueryTypeImplFactory implements QueryTypeFactory
{
    // static
    // {
    // QueryTypeEnum.register(QueryTypeImplEnum.QUERY_TYPE_IMPL_ENUM);
    // }
    
    /**
     * Returns the enumeration from the enumeration that matches this factory
     */
    @Override
    public QueryTypeEnum getEnum()
    {
        return QueryTypeImplEnum.QUERY_TYPE_IMPL_ENUM;
    }
    
    /**
     * Returns the parser from the enumeration that matches this factory
     */
    @Override
    public QueryTypeParser getParser()
    {
        return new QueryTypeImplParser();
    }
    
}
