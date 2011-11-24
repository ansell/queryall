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
import org.queryall.api.provider.NoCommunicationProviderSchema;
import org.queryall.api.provider.ProviderSchema;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class HttpNoCommunicationProviderImpl extends HttpProviderImpl implements NoCommunicationProvider
{
    private static final Set<URI> HTTP_NO_COMMUNICATION_PROVIDER_IMPL_TYPES = new HashSet<URI>();
    
    static
    {
        HttpNoCommunicationProviderImpl.HTTP_NO_COMMUNICATION_PROVIDER_IMPL_TYPES.add(ProviderSchema
                .getProviderTypeUri());
        HttpNoCommunicationProviderImpl.HTTP_NO_COMMUNICATION_PROVIDER_IMPL_TYPES.add(HttpProviderSchema
                .getProviderHttpTypeUri());
        HttpNoCommunicationProviderImpl.HTTP_NO_COMMUNICATION_PROVIDER_IMPL_TYPES.add(NoCommunicationProviderSchema
                .getProviderNoCommunicationTypeUri());
    }
    
    public static Set<URI> httpNoCommunicationTypes()
    {
        return HttpNoCommunicationProviderImpl.HTTP_NO_COMMUNICATION_PROVIDER_IMPL_TYPES;
    }
    
    private boolean useSparqlGraph = false;
    private String sparqlGraphUri = "";
    
    /**
     * 
     */
    public HttpNoCommunicationProviderImpl()
    {
    }
    
    /**
     * @param inputStatements
     * @param keyToUse
     * @param modelVersion
     * @throws OpenRDFException
     */
    public HttpNoCommunicationProviderImpl(final Collection<Statement> inputStatements, final URI keyToUse,
            final int modelVersion) throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
    }
    
    @Override
    public Set<URI> getElementTypes()
    {
        return HttpNoCommunicationProviderImpl.HTTP_NO_COMMUNICATION_PROVIDER_IMPL_TYPES;
    }
    
}
