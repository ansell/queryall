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
public class RegexInputQueryTypeImplEnum extends QueryTypeEnum
{
    public static final QueryTypeEnum REGEX_INPUT_QUERY_TYPE_IMPL_ENUM = new RegexInputQueryTypeImplEnum();
    
    public RegexInputQueryTypeImplEnum()
    {
        this(RegexInputQueryTypeImpl.class.getName(), RegexInputQueryTypeImpl.myTypes());
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public RegexInputQueryTypeImplEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
    
}
