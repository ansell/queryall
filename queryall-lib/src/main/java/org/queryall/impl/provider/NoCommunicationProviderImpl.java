/**
 * 
 */
package org.queryall.impl.provider;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.queryall.api.provider.HttpProviderSchema;
import org.queryall.api.provider.NoCommunicationProvider;
import org.queryall.api.provider.ProviderEnum;
import org.queryall.api.provider.ProviderSchema;

/**
 *
 */
public class NoCommunicationProviderImpl extends ProviderImpl implements NoCommunicationProvider
{
    private static final Set<URI> NO_COMMUNICATION_PROVIDER_IMPL_TYPES = new HashSet<URI>();

    static
    {
        NO_COMMUNICATION_PROVIDER_IMPL_TYPES.add(ProviderSchema.getProviderTypeUri());
    }
    
    public static Set<URI> noCommunicationTypes()
    {
        return NO_COMMUNICATION_PROVIDER_IMPL_TYPES;
    }
//    static
//    {
//        ProviderEnum.register(NoCommunicationProviderImpl.class.getName(), ProviderImpl.providerTypes());
//    }
    
    /**
     * 
     */
    public NoCommunicationProviderImpl()
    {
    }
    
    /**
     * @param inputStatements
     * @param keyToUse
     * @param modelVersion
     * @throws OpenRDFException
     */
    public NoCommunicationProviderImpl(final Collection<Statement> inputStatements, final URI keyToUse,
            final int modelVersion) throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
    }
    
}
