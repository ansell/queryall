package org.queryall.impl.provider;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.queryall.api.provider.HttpProviderSchema;
import org.queryall.api.provider.ProviderSchema;
import org.queryall.api.provider.RdfProvider;
import org.queryall.api.provider.RdfProviderSchema;

public class HttpRdfProviderImpl extends HttpProviderImpl implements RdfProvider
{
    private static final Set<URI> HTTP_RDF_PROVIDER_IMPL_TYPES = new HashSet<URI>();
    
    static
    {
        HttpRdfProviderImpl.HTTP_RDF_PROVIDER_IMPL_TYPES.add(ProviderSchema.getProviderTypeUri());
        HttpRdfProviderImpl.HTTP_RDF_PROVIDER_IMPL_TYPES.add(HttpProviderSchema.getProviderHttpTypeUri());
        HttpRdfProviderImpl.HTTP_RDF_PROVIDER_IMPL_TYPES.add(RdfProviderSchema.getProviderRdfTypeUri());
    }
    
    public static Set<URI> httpAndRdfTypes()
    {
        return HttpRdfProviderImpl.HTTP_RDF_PROVIDER_IMPL_TYPES;
    }
    
    public HttpRdfProviderImpl()
    {
        super();
    }
    
    public HttpRdfProviderImpl(final Collection<Statement> inputStatements, final URI keyToUse,
            final int modelVersion) throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
    }
    
    @Override
    public Set<URI> getElementTypes()
    {
        return HttpRdfProviderImpl.HTTP_RDF_PROVIDER_IMPL_TYPES;
    }
    
}
