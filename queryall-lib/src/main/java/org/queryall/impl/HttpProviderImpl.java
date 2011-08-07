/**
 * 
 */
package org.queryall.impl;

import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.HttpProvider;
import org.queryall.api.SparqlProvider;
import org.queryall.helpers.Constants;
import org.queryall.helpers.RdfUtils;
import org.queryall.helpers.Settings;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class HttpProviderImpl extends ProviderImpl implements HttpProvider, SparqlProvider
{
    private static final Logger log = Logger.getLogger(HttpProviderImpl.class.getName());
    private static final boolean _TRACE = HttpProviderImpl.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = HttpProviderImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = HttpProviderImpl.log.isInfoEnabled();
    
    private static URI providerHttpProviderUri;
    private static URI providerHttpPostSparql;
    private static URI providerHttpGetUrl;
    private static URI providerHttpPostUrl;
    private static URI providerAcceptHeader;
    private static URI providerEndpointUrl;
    private static URI providerSparqlProviderUri;
    
    /**
     * @return the providerAcceptHeader
     */
    public static URI getProviderAcceptHeader()
    {
        return HttpProviderImpl.providerAcceptHeader;
    }
    
    /**
     * @return the providerEndpointUrl
     */
    public static URI getProviderEndpointUrl()
    {
        return HttpProviderImpl.providerEndpointUrl;
    }
    
    /**
     * @return the providerHttpGetUrl
     */
    public static URI getProviderHttpGetUrl()
    {
        return HttpProviderImpl.providerHttpGetUrl;
    }
    
    public static URI getProviderHttpGetUrlUri()
    {
        return HttpProviderImpl.getProviderHttpGetUrl();
    }
    
    /**
     * @return the providerHttpPostSparql
     */
    public static URI getProviderHttpPostSparql()
    {
        return HttpProviderImpl.providerHttpPostSparql;
    }
    
    public static URI getProviderHttpPostSparqlUri()
    {
        return HttpProviderImpl.getProviderHttpPostSparql();
    }
    
    /**
     * @return the providerHttpPostUrl
     */
    public static URI getProviderHttpPostUrl()
    {
        return HttpProviderImpl.providerHttpPostUrl;
    }
    
    public static URI getProviderHttpPostUrlUri()
    {
        return HttpProviderImpl.getProviderHttpPostUrl();
    }
    
    /**
     * @return the providerHttpProviderUri
     */
    public static URI getProviderHttpProviderUri()
    {
        return HttpProviderImpl.providerHttpProviderUri;
    }
    
    /**
     * @return the providerSparqlProviderUri
     */
    public static URI getProviderSparqlProviderUri()
    {
        return HttpProviderImpl.providerSparqlProviderUri;
    }
    
    public static boolean schemaToRdf(final Repository myRepository, final URI contextUri, final int modelVersion)
        throws OpenRDFException
    {
        ProviderImpl.schemaToRdf(myRepository, contextUri, modelVersion);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(HttpProviderImpl.getProviderHttpProviderUri(), RDF.TYPE, OWL.CLASS, contextUri);
            con.add(HttpProviderImpl.getProviderHttpProviderUri(), RDFS.SUBCLASSOF, ProviderImpl.getProviderTypeUri(),
                    contextUri);
            
            con.add(HttpProviderImpl.getProviderAcceptHeader(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(HttpProviderImpl.getProviderAcceptHeader(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(HttpProviderImpl.getProviderAcceptHeader(), RDFS.DOMAIN,
                    HttpProviderImpl.getProviderHttpProviderUri(), contextUri);
            con.add(HttpProviderImpl.getProviderAcceptHeader(), RDFS.LABEL,
                    f.createLiteral("The HTTP Accept header to send to this provider."), contextUri);
            
            con.add(HttpProviderImpl.getProviderEndpointUrl(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(HttpProviderImpl.getProviderEndpointUrl(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(HttpProviderImpl.getProviderEndpointUrl(), RDFS.DOMAIN,
                    HttpProviderImpl.getProviderHttpProviderUri(), contextUri);
            con.add(HttpProviderImpl.getProviderEndpointUrl(),
                    RDFS.LABEL,
                    f.createLiteral("The URL template for this provider. If it contains variables, these may be replaced when executing a query."),
                    contextUri);
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            
            if(con != null)
            {
                con.rollback();
            }
            
            HttpProviderImpl.log.error("RepositoryException: " + re.getMessage());
        }
        finally
        {
            if(con != null)
            {
                con.close();
            }
        }
        
        return false;
    }
    
    /**
     * @param providerAcceptHeader
     *            the providerAcceptHeader to set
     */
    public static void setProviderAcceptHeader(final URI providerAcceptHeader)
    {
        HttpProviderImpl.providerAcceptHeader = providerAcceptHeader;
    }
    
    /**
     * @param providerEndpointUrl
     *            the providerEndpointUrl to set
     */
    public static void setProviderEndpointUrl(final URI providerEndpointUrl)
    {
        HttpProviderImpl.providerEndpointUrl = providerEndpointUrl;
    }
    
    /**
     * @param providerHttpGetUrl
     *            the providerHttpGetUrl to set
     */
    public static void setProviderHttpGetUrl(final URI providerHttpGetUrl)
    {
        HttpProviderImpl.providerHttpGetUrl = providerHttpGetUrl;
    }
    
    /**
     * @param providerHttpPostSparql
     *            the providerHttpPostSparql to set
     */
    public static void setProviderHttpPostSparql(final URI providerHttpPostSparql)
    {
        HttpProviderImpl.providerHttpPostSparql = providerHttpPostSparql;
    }
    
    /**
     * @param providerHttpPostUrl
     *            the providerHttpPostUrl to set
     */
    public static void setProviderHttpPostUrl(final URI providerHttpPostUrl)
    {
        HttpProviderImpl.providerHttpPostUrl = providerHttpPostUrl;
    }
    
    /**
     * @param providerHttpProviderUri
     *            the providerHttpProviderUri to set
     */
    public static void setProviderHttpProviderUri(final URI providerHttpProviderUri)
    {
        HttpProviderImpl.providerHttpProviderUri = providerHttpProviderUri;
    }
    
    /**
     * @param providerSparqlProviderUri
     *            the providerSparqlProviderUri to set
     */
    public static void setProviderSparqlProviderUri(final URI providerSparqlProviderUri)
    {
        HttpProviderImpl.providerSparqlProviderUri = providerSparqlProviderUri;
    }
    
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
        final ValueFactory f = Constants.valueFactory;
        
        HttpProviderImpl.setProviderHttpProviderUri(f.createURI(ProviderImpl.providerNamespace, "HttpProvider"));
        HttpProviderImpl.setProviderSparqlProviderUri(f.createURI(ProviderImpl.providerNamespace, "SparqlProvider"));
        HttpProviderImpl.setProviderEndpointUrl(f.createURI(ProviderImpl.providerNamespace, "endpointUrl"));
        HttpProviderImpl.setProviderAcceptHeader(f.createURI(ProviderImpl.providerNamespace, "acceptHeader"));
        HttpProviderImpl.setProviderHttpPostSparql(f.createURI(ProviderImpl.providerNamespace, "httppostsparql"));
        HttpProviderImpl.setProviderHttpGetUrl(f.createURI(ProviderImpl.providerNamespace, "httpgeturl"));
        HttpProviderImpl.setProviderHttpPostUrl(f.createURI(ProviderImpl.providerNamespace, "httpposturl"));
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
                    && nextStatement.getObject().equals(HttpProviderImpl.getProviderHttpProviderUri()))
            {
                if(HttpProviderImpl._TRACE)
                {
                    HttpProviderImpl.log.trace("HttpProvider: found valid type predicate for URI: " + keyToUse);
                }
                
                // resultIsValid = true;
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(HttpProviderImpl.getProviderAcceptHeader()))
            {
                this.setAcceptHeaderString(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(HttpProviderImpl.getProviderEndpointUrl()))
            {
                this.addEndpointUrl(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(ProviderImpl.getProviderRequiresSparqlGraphURI()))
            {
                this.setUseSparqlGraph(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(ProviderImpl.getProviderGraphUri()))
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
            this.endpointUrls = new HashSet<String>();
        }
        
        this.endpointUrls.add(endpointUrl);
        
    }
    
    @Override
    public String getAcceptHeaderString()
    {
        if(this.acceptHeaderString != null && !this.acceptHeaderString.trim().equals(""))
        {
            return this.acceptHeaderString;
        }
        else
        {
            return Settings.getSettings().getStringProperty("defaultAcceptHeader", "application/rdf+xml");
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
        
        results.add(HttpProviderImpl.getProviderHttpProviderUri());
        results.add(HttpProviderImpl.getProviderSparqlProviderUri());
        
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
        return this.getEndpointMethod().equals(HttpProviderImpl.getProviderHttpGetUrl());
    }
    
    @Override
    public boolean isHttpPostSparql()
    {
        return this.getEndpointMethod().equals(HttpProviderImpl.getProviderHttpPostSparql());
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
            
            // backwards compatibility check, and use default if nothing was previously specified
            // NOTE: we assume empty accept header is non-intentional as it doesn't have a
            // non-trivial purpose
            Literal acceptHeaderLiteral = null;
            
            if(this.getAcceptHeaderString() == null || this.getAcceptHeaderString().trim().equals(""))
            {
                acceptHeaderLiteral =
                        f.createLiteral(Settings.getSettings().getStringProperty("defaultAcceptHeader", ""));
            }
            else
            {
                acceptHeaderLiteral = f.createLiteral(this.getAcceptHeaderString());
            }
            
            final Literal useSparqlGraphLiteral = f.createLiteral(this.getUseSparqlGraph());
            final Literal sparqlGraphUriLiteral = f.createLiteral(this.getSparqlGraphUri());
            
            con.setAutoCommit(false);
            
            con.add(providerInstanceUri, RDF.TYPE, ProviderImpl.getProviderTypeUri(), keyToUse);
            
            con.add(providerInstanceUri, HttpProviderImpl.getProviderAcceptHeader(), acceptHeaderLiteral, keyToUse);
            
            con.add(providerInstanceUri, ProviderImpl.getProviderRequiresSparqlGraphURI(), useSparqlGraphLiteral,
                    keyToUse);
            
            con.add(providerInstanceUri, ProviderImpl.getProviderGraphUri(), sparqlGraphUriLiteral, keyToUse);
            
            if(this.getEndpointUrls() != null)
            {
                for(final String nextEndpointUrl : this.getEndpointUrls())
                {
                    if(nextEndpointUrl != null)
                    {
                        con.add(providerInstanceUri, HttpProviderImpl.getProviderEndpointUrl(),
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
