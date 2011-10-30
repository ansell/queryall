/**
 * 
 */
package org.queryall.impl.provider;

import java.util.Set;

import org.kohsuke.MetaInfServices;
import org.openrdf.model.URI;
import org.queryall.api.provider.ProviderEnum;
import org.queryall.api.services.QueryAllEnum;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices(QueryAllEnum.class)
public class HttpOnlyProviderImplEnum extends ProviderEnum
{
    public static final ProviderEnum HTTP_ONLY_PROVIDER_IMPL_ENUM = new HttpOnlyProviderImplEnum();
    
    // static
    // {
    // NormalisationRuleEnum.register(REGEX_NORMALISATION_RULE_IMPL_ENUM);
    // }
    
    public HttpOnlyProviderImplEnum()
    {
        this(HttpOnlyProviderImpl.class.getName(), HttpOnlyProviderImpl.httpOnlyTypes());
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public HttpOnlyProviderImplEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
    
}
