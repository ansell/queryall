/**
 * 
 */
package org.queryall.api.provider;

import java.util.Collection;
import java.util.Set;

import org.openrdf.model.URI;
import org.queryall.api.services.QueryAllEnum;
import org.queryall.api.services.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider implementations register themselves with this enumeration when they are loaded.
 * 
 * NOTE: This is not an enumeration as java does not support extensible enumerations, but it should
 * act like one for all purposes other than java's underpowered switch case statement.
 * 
 */
public class ProviderEnum extends QueryAllEnum
{
    private static final Logger LOG = LoggerFactory.getLogger(ProviderEnum.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = ProviderEnum.LOG.isTraceEnabled();
    private static final boolean DEBUG = ProviderEnum.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = ProviderEnum.LOG.isInfoEnabled();
    
    /**
     * @deprecated Use {@link ServiceUtils#getProviderEnumsByTypeUris(Set<URI>)} instead
     */
    @Deprecated
    public static Collection<ProviderEnum> byTypeUris(final Set<URI> nextTypeUris)
    {
        return ServiceUtils.getProviderEnumsByTypeUris(nextTypeUris);
    }
    
    /**
     * Create a new Provider enum using the given name, which must be unique.
     * 
     * @param nextName
     * @param nextTypeURIs
     */
    public ProviderEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
    
    /**
     * For Providers, we require that all of the given URIs exactly match the declared type URIs, to
     * prevent basic HTTP Providers from being parsed as any of the specialised HTTP Providers.
     */
    @Override
    protected boolean matchForTypeUris(final Set<URI> nextTypeURIs)
    {
        boolean matching = true;
        
        if(this.getTypeURIs().size() != nextTypeURIs.size())
        {
            return false;
        }
        
        for(final URI nextURI : nextTypeURIs)
        {
            // Default implementation of this method is to check whether the given URIs are all in
            // the type URIs for this Enum
            // This behaviour can be overriden to provide different behaviour in implementations
            if(!this.getTypeURIs().contains(nextURI))
            {
                matching = false;
            }
        }
        
        return matching;
    }
}
