/**
 * 
 */
package org.queryall.impl.querytype;

import java.util.Set;

import org.kohsuke.MetaInfServices;
import org.openrdf.model.URI;
import org.queryall.api.querytype.QueryTypeEnum;
import org.queryall.api.services.QueryAllEnum;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices(QueryAllEnum.class)
public class NoInputQueryTypeImplEnum extends QueryTypeEnum
{
    public static final QueryTypeEnum NO_INPUT_QUERY_TYPE_IMPL_ENUM = new NoInputQueryTypeImplEnum();
    
    // static
    // {
    // QueryTypeEnum.register(RDF_INPUT_QUERY_TYPE_IMPL_ENUM);
    // }
    
    public NoInputQueryTypeImplEnum()
    {
        this(NoInputQueryTypeImpl.class.getName(), NoInputQueryTypeImpl.myTypes());
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public NoInputQueryTypeImplEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
    
}
