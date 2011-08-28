/**
 * 
 */
package org.queryall.impl.provider;

import java.util.List;

import org.kohsuke.MetaInfServices;
import org.openrdf.model.URI;
import org.queryall.api.provider.ProviderEnum;
import org.queryall.api.services.QueryAllEnum;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices(QueryAllEnum.class)
public class NoCommunicationProviderImplEnum extends ProviderEnum
{
    public static final ProviderEnum NO_COMMUNICATION_PROVIDER_IMPL_ENUM = new NoCommunicationProviderImplEnum();
    
    // static
    // {
    // NormalisationRuleEnum.register(REGEX_NORMALISATION_RULE_IMPL_ENUM);
    // }
    
    public NoCommunicationProviderImplEnum()
    {
        this(NoCommunicationProviderImpl.class.getName(), ProviderImpl.providerTypes());
        
        QueryAllEnum.log.info("NoCommunicationProviderImplEnum() registered");
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public NoCommunicationProviderImplEnum(final String nextName, final List<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
        
        QueryAllEnum.log.info("NoCommunicationProviderImplEnum(String, List<URI>) registered");
    }
    
}
