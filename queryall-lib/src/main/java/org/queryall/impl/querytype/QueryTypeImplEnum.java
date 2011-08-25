/**
 * 
 */
package org.queryall.impl.querytype;

import java.util.List;

import org.kohsuke.MetaInfServices;
import org.openrdf.model.URI;
import org.queryall.api.querytype.QueryTypeEnum;
import org.queryall.api.services.QueryAllEnum;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices(QueryAllEnum.class)
public class QueryTypeImplEnum extends QueryTypeEnum
{
    public static final QueryTypeEnum QUERY_TYPE_IMPL_ENUM = new QueryTypeImplEnum();
    
    // static
    // {
    // QueryTypeEnum.register(QUERY_TYPE_IMPL_ENUM);
    // }
    
    public QueryTypeImplEnum()
    {
        this(QueryTypeImpl.class.getName(), QueryTypeImpl.myTypes());
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public QueryTypeImplEnum(final String nextName, final List<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
    
}
