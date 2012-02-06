package org.queryall.impl.provider;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.queryall.api.provider.HttpProviderSchema;
import org.queryall.api.provider.HttpSparqlProvider;
import org.queryall.api.provider.ProviderSchema;
import org.queryall.api.provider.SparqlProviderSchema;
import org.queryall.utils.RdfUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpSparqlProviderImpl extends HttpProviderImpl implements HttpSparqlProvider
{
    private static final Logger LOG = LoggerFactory.getLogger(HttpSparqlProviderImpl.class);
    private static final boolean TRACE = HttpSparqlProviderImpl.LOG.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = HttpSparqlProviderImpl.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = HttpSparqlProviderImpl.LOG.isInfoEnabled();
    
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
    
    private boolean useSparqlGraph = false;
    private String sparqlGraphUri = "";
    
    public HttpSparqlProviderImpl()
    {
        super();
    }
    
    public HttpSparqlProviderImpl(final Collection<Statement> inputStatements, final URI keyToUse,
            final int modelVersion) throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
        final Collection<Statement> currentUnrecognisedStatements = this.resetUnrecognisedStatements();
        
        for(final Statement nextStatement : currentUnrecognisedStatements)
        {
            if(HttpSparqlProviderImpl.TRACE)
            {
                HttpSparqlProviderImpl.LOG.trace("HttpSparqlProviderImpl: nextStatement: " + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(HttpProviderSchema.getProviderHttpTypeUri()))
            {
                if(HttpSparqlProviderImpl.TRACE)
                {
                    HttpSparqlProviderImpl.LOG.trace("HttpSparqlProviderImpl: found valid type predicate for URI: "
                            + keyToUse);
                }
                
                // resultIsValid = true;
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(SparqlProviderSchema.getProviderSparqlRequiresGraphURI()))
            {
                this.setUseSparqlGraph(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(SparqlProviderSchema.getProviderSparqlGraphUri()))
            {
                this.setSparqlGraphUri(nextStatement.getObject().stringValue());
            }
        }
        
    }
    
    @Override
    public Set<URI> getElementTypes()
    {
        return HttpSparqlProviderImpl.HTTP_SPARQL_PROVIDER_IMPL_TYPES;
    }
    
    @Override
    public String getSparqlGraphUri()
    {
        if(this.getUseSparqlGraph())
        {
            return this.sparqlGraphUri;
        }
        else
        {
            return "";
        }
    }
    
    @Override
    public boolean getUseSparqlGraph()
    {
        return this.useSparqlGraph;
    }
    
    @Override
    public boolean isHttpGetSparql()
    {
        return this.getEndpointMethod().equals(SparqlProviderSchema.getProviderHttpGetSparql());
    }
    
    @Override
    public boolean isHttpPostSparql()
    {
        return this.getEndpointMethod().equals(SparqlProviderSchema.getProviderHttpPostSparql());
    }
    
    @Override
    public void setSparqlGraphUri(final String nextSparqlGraphUri)
    {
        this.sparqlGraphUri = nextSparqlGraphUri;
    }
    
    @Override
    public void setUseSparqlGraph(final boolean nextUseSparqlGraph)
    {
        this.useSparqlGraph = nextUseSparqlGraph;
    }
    
}
