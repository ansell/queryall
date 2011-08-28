/**
 * 
 */
package org.queryall.impl.provider;

import java.util.Collection;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.queryall.api.provider.NoCommunicationProvider;

/**
 *
 */
public class NoCommunicationProviderImpl extends ProviderImpl implements NoCommunicationProvider
{
    static
    {
        NoCommunicationProviderImplEnum.register(NoCommunicationProviderImpl.class.getName(), ProviderImpl.providerTypes());
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
    public NoCommunicationProviderImpl(Collection<Statement> inputStatements, URI keyToUse, int modelVersion)
        throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
    }
    
}
