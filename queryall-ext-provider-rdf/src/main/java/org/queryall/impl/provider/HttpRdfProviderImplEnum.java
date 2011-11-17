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
public class HttpRdfProviderImplEnum extends ProviderEnum
{
    public static final ProviderEnum HTTP_RDF_PROVIDER_IMPL_ENUM = new HttpRdfProviderImplEnum();
    
    // static
    // {
    // NormalisationRuleEnum.register(REGEX_NORMALISATION_RULE_IMPL_ENUM);
    // }
    
    public HttpRdfProviderImplEnum()
    {
        this(HttpRdfProviderImpl.class.getName(), HttpRdfProviderImpl.httpAndRdfTypes());
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public HttpRdfProviderImplEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
    
}
