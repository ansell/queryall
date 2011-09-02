/**
 * 
 */
package org.queryall.impl.provider;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.provider.HttpProvider;
import org.queryall.api.provider.HttpProviderSchema;
import org.queryall.api.provider.ProviderSchema;
import org.queryall.api.provider.SparqlProvider;
import org.queryall.api.utils.Constants;
import org.queryall.utils.RdfUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class HttpProviderImpl extends ProviderImpl implements HttpProvider, SparqlProvider
{
    static final Logger log = LoggerFactory.getLogger(HttpProviderImpl.class);
    private static final boolean _TRACE = HttpProviderImpl.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = HttpProviderImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = HttpProviderImpl.log.isInfoEnabled();
    
    private Collection<String> endpointUrls = new HashSet<String>();
    // Use these to include information based on whether or not the provider was actually used to
    // provide information for particular user queries
    // public Collection<String> providerQueryInclusions = new HashSet<String>();
    // public boolean onlyIncludeProviderQueryIfInformationReturned = true;
    
    private String acceptHeaderString = "";
    
    private boolean useSparqlGraph = false;
    
    private String sparqlGraphUri = "";
    
    static
    {
        ;
    }
    
    public HttpProviderImpl()
    {
        super();
    }
    
    public HttpProviderImpl(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
        final Collection<Statement> currentUnrecognisedStatements = new HashSet<Statement>();
        
        currentUnrecognisedStatements.addAll(this.getUnrecognisedStatements());
        
        this.unrecognisedStatements = new HashSet<Statement>();
        
        for(final Statement nextStatement : currentUnrecognisedStatements)
        {
            if(HttpProviderImpl._TRACE)
            {
                HttpProviderImpl.log.trace("HttpProvider: nextStatement: " + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(HttpProviderSchema.getProviderHttpProviderUri()))
            {
                if(HttpProviderImpl._TRACE)
                {
                    HttpProviderImpl.log.trace("HttpProvider: found valid type predicate for URI: " + keyToUse);
                }
                
                // resultIsValid = true;
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(HttpProviderSchema.getProviderAcceptHeader()))
            {
                this.setAcceptHeaderString(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(HttpProviderSchema.getProviderEndpointUrl()))
            {
                this.addEndpointUrl(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(ProviderSchema.getProviderSparqlRequiresGraphURI()))
            {
                this.setUseSparqlGraph(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(ProviderSchema.getProviderSparqlGraphUri()))
            {
                this.setSparqlGraphUri(nextStatement.getObject().stringValue());
            }
            else
            {
                if(HttpProviderImpl._TRACE)
                {
                    HttpProviderImpl.log.trace("HttpProvider: unrecognisedStatement nextStatement: "
                            + nextStatement.toString());
                }
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
    }
    
    @Override
    public void addEndpointUrl(final String endpointUrl)
    {
        if(this.endpointUrls == null)
        {
            this.endpointUrls = Collections.synchronizedSet(new HashSet<String>());
        }
        
        this.endpointUrls.add(endpointUrl);
    }
    
    @Override
    public String getAcceptHeaderString(final String defaultAcceptHeader)
    {
        if(this.acceptHeaderString != null && !this.acceptHeaderString.trim().equals(""))
        {
            return this.acceptHeaderString;
        }
        else
        {
            return defaultAcceptHeader;
        }
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class,
     *         including abstract implementations
     */
    @Override
    public Collection<URI> getElementTypes()
    {
        final Collection<URI> results = super.getElementTypes();
        
        results.add(HttpProviderSchema.getProviderHttpProviderUri());
        results.add(HttpProviderSchema.getProviderSparqlProviderUri());
        
        return results;
    }
    
    @Override
    public Collection<String> getEndpointUrls()
    {
        return this.endpointUrls;
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
    public boolean hasEndpointUrl()
    {
        return (this.getEndpointUrls() != null && this.getEndpointUrls().size() > 0);
    }
    
    @Override
    public boolean isHttpGetUrl()
    {
        return this.getEndpointMethod().equals(HttpProviderSchema.getProviderHttpGetUrl());
    }
    
    @Override
    public boolean isHttpPostSparql()
    {
        return this.getEndpointMethod().equals(HttpProviderSchema.getProviderHttpPostSparql());
    }
    
    @Override
    public void setAcceptHeaderString(final String acceptHeaderString)
    {
        this.acceptHeaderString = acceptHeaderString;
    }
    
    @Override
    public void setEndpointUrls(final Collection<String> endpointUrls)
    {
        this.endpointUrls = endpointUrls;
    }
    
    @Override
    public void setSparqlGraphUri(final String sparqlGraphUri)
    {
        this.sparqlGraphUri = sparqlGraphUri;
    }
    
    @Override
    public void setUseSparqlGraph(final boolean useSparqlGraph)
    {
        this.useSparqlGraph = useSparqlGraph;
    }
    
    @Override
    public boolean toRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        super.toRdf(myRepository, keyToUse, modelVersion);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            con.setAutoCommit(false);
            
            if(HttpProviderImpl._TRACE)
            {
                HttpProviderImpl.log.trace("Provider.toRdf: keyToUse=" + keyToUse);
            }
            
            // create some resources and literals to make statements out of
            final URI providerInstanceUri = this.getKey();
            
            Literal acceptHeaderLiteral = null;
            
            if(this.acceptHeaderString != null && this.acceptHeaderString.trim().length() > 0)
            {
                acceptHeaderLiteral = f.createLiteral(this.acceptHeaderString);
            }
            
            final Literal useSparqlGraphLiteral = f.createLiteral(this.getUseSparqlGraph());
            final Literal sparqlGraphUriLiteral = f.createLiteral(this.getSparqlGraphUri());
            
            con.setAutoCommit(false);
            
            con.add(providerInstanceUri, RDF.TYPE, ProviderSchema.getProviderTypeUri(), keyToUse);
            
            if(acceptHeaderLiteral != null)
            {
                con.add(providerInstanceUri, HttpProviderSchema.getProviderAcceptHeader(), acceptHeaderLiteral,
                        keyToUse);
            }
            
            con.add(providerInstanceUri, ProviderSchema.getProviderSparqlRequiresGraphURI(), useSparqlGraphLiteral,
                    keyToUse);
            
            con.add(providerInstanceUri, ProviderSchema.getProviderSparqlGraphUri(), sparqlGraphUriLiteral, keyToUse);
            
            if(this.getEndpointUrls() != null)
            {
                for(final String nextEndpointUrl : this.getEndpointUrls())
                {
                    if(nextEndpointUrl != null)
                    {
                        con.add(providerInstanceUri, HttpProviderSchema.getProviderEndpointUrl(),
                                f.createLiteral(nextEndpointUrl), keyToUse);
                    }
                }
            }
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            HttpProviderImpl.log.error("RepositoryException: " + re.getMessage());
        }
        catch(final Exception ex)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            HttpProviderImpl.log.error("Exception.. this.getKey()=" + this.getKey().stringValue(), ex);
        }
        finally
        {
            con.close();
        }
        
        return false;
    }
    
}
