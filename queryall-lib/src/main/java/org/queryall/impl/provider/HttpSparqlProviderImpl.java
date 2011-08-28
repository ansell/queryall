package org.queryall.impl.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.queryall.api.provider.HttpProviderSchema;
import org.queryall.api.provider.HttpSparqlProvider;
import org.queryall.api.provider.ProviderSchema;

public class HttpSparqlProviderImpl extends HttpProviderImpl implements HttpSparqlProvider
{

    public HttpSparqlProviderImpl()
    {
        super();
    }

    public HttpSparqlProviderImpl(Collection<Statement> inputStatements, URI keyToUse, int modelVersion)
        throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
    }

    public static List<URI> httpAndSparqlTypes()
    {
        List<URI> results = new ArrayList<URI>(3);
        
        results.add(ProviderSchema.getProviderTypeUri());
        results.add(HttpProviderSchema.getProviderHttpProviderUri());
        results.add(HttpProviderSchema.getProviderSparqlProviderUri());
        
        return results;
    }
    
}
