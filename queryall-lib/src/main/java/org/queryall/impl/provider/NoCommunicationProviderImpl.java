/**
 * 
 */
package org.queryall.impl.provider;

import java.util.Collection;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.queryall.api.provider.NoCommunicationProvider;
import org.queryall.api.provider.ProviderEnum;

/**
 *
 */
public class NoCommunicationProviderImpl extends ProviderImpl implements NoCommunicationProvider
{
    static
    {
        ProviderEnum.register(NoCommunicationProviderImpl.class.getName(), ProviderImpl.providerTypes());
    }
    
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
