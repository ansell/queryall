/**
 * 
 */
package org.queryall.impl.provider;

import java.util.List;

import org.kohsuke.MetaInfServices;
import org.openrdf.model.URI;
import org.queryall.api.provider.ProviderEnum;
import org.queryall.api.services.QueryAllEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices(QueryAllEnum.class)
public class NoCommunicationProviderImplEnum extends ProviderEnum
{
    private static final Logger log = LoggerFactory.getLogger(NoCommunicationProviderImplEnum.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = NoCommunicationProviderImplEnum.log.isTraceEnabled();
    private static final boolean _DEBUG = NoCommunicationProviderImplEnum.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = NoCommunicationProviderImplEnum.log.isInfoEnabled();
    
    public static final ProviderEnum NO_COMMUNICATION_PROVIDER_IMPL_ENUM = new NoCommunicationProviderImplEnum();
    
    // static
    // {
    // NormalisationRuleEnum.register(REGEX_NORMALISATION_RULE_IMPL_ENUM);
    // }
    
    public NoCommunicationProviderImplEnum()
    {
        this(NoCommunicationProviderImpl.class.getName(), ProviderImpl.providerTypes());
        
        if(NoCommunicationProviderImplEnum._DEBUG)
        {
            NoCommunicationProviderImplEnum.log.debug("NoCommunicationProviderImplEnum() registered");
        }
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public NoCommunicationProviderImplEnum(final String nextName, final List<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
        
        if(NoCommunicationProviderImplEnum._DEBUG)
        {
            NoCommunicationProviderImplEnum.log.debug("NoCommunicationProviderImplEnum(String, List<URI>) registered");
        }
    }
    
}
