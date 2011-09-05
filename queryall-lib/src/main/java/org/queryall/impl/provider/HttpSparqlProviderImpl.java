package org.queryall.impl.provider;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.queryall.api.provider.HttpProviderSchema;
import org.queryall.api.provider.HttpSparqlProvider;
import org.queryall.api.provider.ProviderSchema;
import org.queryall.api.provider.SparqlProviderSchema;

public class HttpSparqlProviderImpl extends HttpProviderImpl implements HttpSparqlProvider
{
    private static final Set<URI> HTTP_SPARQL_PROVIDER_IMPL_TYPES = new HashSet<URI>();
    
    static
    {
        HttpSparqlProviderImpl.HTTP_SPARQL_PROVIDER_IMPL_TYPES.add(ProviderSchema.getProviderTypeUri());
        HttpSparqlProviderImpl.HTTP_SPARQL_PROVIDER_IMPL_TYPES.add(HttpProviderSchema.getProviderHttpTypeUri());
        HttpSparqlProviderImpl.HTTP_SPARQL_PROVIDER_IMPL_TYPES.add(SparqlProviderSchema.getProviderSparqlTypeUri());
    }
    
    public static Set<URI> httpAndSparqlTypes()
    {
        return HttpSparqlProviderImpl.HTTP_SPARQL_PROVIDER_IMPL_TYPES;
    }
    
    public HttpSparqlProviderImpl()
    {
        super();
    }
    
    public HttpSparqlProviderImpl(final Collection<Statement> inputStatements, final URI keyToUse,
            final int modelVersion) throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
    }
    
}
