/**
 * 
 */
package org.queryall.impl.provider;

import java.util.Set;

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
    private static final boolean TRACE = NoCommunicationProviderImplEnum.log.isTraceEnabled();
    private static final boolean DEBUG = NoCommunicationProviderImplEnum.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = NoCommunicationProviderImplEnum.log.isInfoEnabled();
    
    public static final ProviderEnum NO_COMMUNICATION_PROVIDER_IMPL_ENUM = new NoCommunicationProviderImplEnum();
    
    // static
    // {
    // NormalisationRuleEnum.register(REGEX_NORMALISATION_RULE_IMPL_ENUM);
    // }
    
    public NoCommunicationProviderImplEnum()
    {
        this(NoCommunicationProviderImpl.class.getName(), NoCommunicationProviderImpl.noCommunicationTypes());
        
        if(NoCommunicationProviderImplEnum.DEBUG)
        {
            NoCommunicationProviderImplEnum.log.debug("NoCommunicationProviderImplEnum() registered");
        }
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public NoCommunicationProviderImplEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
        
        if(NoCommunicationProviderImplEnum.DEBUG)
        {
            NoCommunicationProviderImplEnum.log.debug("NoCommunicationProviderImplEnum(String, List<URI>) registered");
        }
    }
    
}
