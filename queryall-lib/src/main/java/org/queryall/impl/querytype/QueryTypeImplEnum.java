/**
 * 
 */
package org.queryall.impl.querytype;

import java.util.List;

import org.openrdf.model.URI;
import org.queryall.api.querytype.QueryTypeEnum;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class QueryTypeImplEnum extends QueryTypeEnum
{
    public static final QueryTypeEnum QUERY_TYPE_IMPL_ENUM = new QueryTypeImplEnum(QueryTypeImpl.class.getName(), QueryTypeImpl.myTypes());
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public QueryTypeImplEnum(String nextName, List<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
    
}
