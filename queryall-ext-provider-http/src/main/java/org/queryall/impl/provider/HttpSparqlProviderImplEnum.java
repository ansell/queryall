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
public class HttpSparqlProviderImplEnum extends ProviderEnum
{
    public static final ProviderEnum HTTP_SPARQL_PROVIDER_IMPL_ENUM = new HttpSparqlProviderImplEnum();
    
    // static
    // {
    // NormalisationRuleEnum.register(REGEX_NORMALISATION_RULE_IMPL_ENUM);
    // }
    
    public HttpSparqlProviderImplEnum()
    {
        this(HttpSparqlProviderImpl.class.getName(), HttpSparqlProviderImpl.httpAndSparqlTypes());
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public HttpSparqlProviderImplEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
    
}
