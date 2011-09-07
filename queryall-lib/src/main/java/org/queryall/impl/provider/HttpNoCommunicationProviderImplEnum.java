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
public class HttpNoCommunicationProviderImplEnum extends ProviderEnum
{
    public static final ProviderEnum HTTP_NO_COMMUNICATION_PROVIDER_IMPL_ENUM = new HttpNoCommunicationProviderImplEnum();
    
    public HttpNoCommunicationProviderImplEnum()
    {
        this(HttpNoCommunicationProviderImpl.class.getName(), HttpNoCommunicationProviderImpl.httpNoCommunicationTypes());
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public HttpNoCommunicationProviderImplEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
    
}
