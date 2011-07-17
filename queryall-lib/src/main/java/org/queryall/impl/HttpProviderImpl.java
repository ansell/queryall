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
 * @author karina
 *
 */
public class HttpProviderImpl extends ProviderImpl implements
		HttpProvider , SparqlProvider
{
    private static final Logger log = Logger.getLogger(HttpProviderImpl.class.getName());
    private static final boolean _TRACE = log.isTraceEnabled();
    @SuppressWarnings("unused")
	private static final boolean _DEBUG = log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = log.isInfoEnabled();
    

	private static URI providerHttpProviderUri;
	private static URI providerHttpPostSparql;
	private static URI providerHttpGetUrl;
	private static URI providerHttpPostUrl;
	private static URI providerAcceptHeader;
	private static URI providerEndpointUrl;
	private static URI providerSparqlProviderUri;
	private Collection<String> endpointUrls = new HashSet<String>();
	// Use these to include information based on whether or not the provider was actually used to provide information for particular user queries
	//    public Collection<String> providerQueryInclusions = new HashSet<String>();
	//    public boolean onlyIncludeProviderQueryIfInformationReturned = true;
	    
    private String acceptHeaderString = "";
	private boolean useSparqlGraph = false;
	private String sparqlGraphUri = "";

    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        HttpProviderImpl.setProviderHttpProviderUri(f.createURI(providerNamespace, "HttpProvider"));
        HttpProviderImpl.setProviderSparqlProviderUri(f.createURI(providerNamespace, "SparqlProvider"));
        HttpProviderImpl.setProviderEndpointUrl(f.createURI(providerNamespace,"endpointUrl"));
        HttpProviderImpl.setProviderAcceptHeader(f.createURI(providerNamespace,"acceptHeader"));
        HttpProviderImpl.setProviderHttpPostSparql(f.createURI(providerNamespace,"httppostsparql"));
        HttpProviderImpl.setProviderHttpGetUrl(f.createURI(providerNamespace,"httpgeturl"));
        HttpProviderImpl.setProviderHttpPostUrl(f.createURI(providerNamespace,"httpposturl"));
    }
    
    public HttpProviderImpl()
    {
    	super();
    }
    

	public HttpProviderImpl(Collection<Statement> inputStatements, URI keyToUse, int modelVersion) throws OpenRDFException
    {
    	super(inputStatements, keyToUse, modelVersion);
    	
    	Collection<Statement> currentUnrecognisedStatements = new HashSet<Statement>();
    	
    	currentUnrecognisedStatements.addAll(this.getUnrecognisedStatements());
    	
    	this.unrecognisedStatements = new HashSet<Statement>();
    	
        for(Statement nextStatement : currentUnrecognisedStatements)
        {
            if(_TRACE)
            {
                log.trace("HttpProvider: nextStatement: "+nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE) && nextStatement.getObject().equals(getProviderHttpProviderUri()))
            {
                if(_TRACE)
                {
                    log.trace("HttpProvider: found valid type predicate for URI: "+keyToUse);
                }
                
                //resultIsValid = true;
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(getProviderAcceptHeader()))
            {
                this.setAcceptHeaderString(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(getProviderEndpointUrl()))
            {
            	this.addEndpointUrl(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(getProviderRequiresSparqlGraphURI()))
            {
            	this.setUseSparqlGraph(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(getProviderGraphUri()))
            {
            	this.setSparqlGraphUri(nextStatement.getObject().stringValue());
            }
            else
            {
                if(_TRACE)
                {
                    log.trace("HttpProvider: unrecognisedStatement nextStatement: "+nextStatement.toString());
                }
            	this.addUnrecognisedStatement(nextStatement);
            }
        }
    	
    }

    public static boolean schemaToRdf(Repository myRepository, URI contextUri, int modelVersion) throws OpenRDFException
    {
    	ProviderImpl.schemaToRdf(myRepository, contextUri, modelVersion);
    	
        RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;

        try
        {
            con.setAutoCommit(false);
            
            con.add(HttpProviderImpl.getProviderHttpProviderUri(), RDF.TYPE, OWL.CLASS, contextUri);
            con.add(HttpProviderImpl.getProviderHttpProviderUri(), RDFS.SUBCLASSOF, ProviderImpl.getProviderTypeUri(), contextUri);
            
            con.add(HttpProviderImpl.getProviderAcceptHeader(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(HttpProviderImpl.getProviderAcceptHeader(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(HttpProviderImpl.getProviderAcceptHeader(), RDFS.DOMAIN, HttpProviderImpl.getProviderHttpProviderUri(), contextUri);
            con.add(HttpProviderImpl.getProviderAcceptHeader(), RDFS.LABEL, f.createLiteral("The HTTP Accept header to send to this provider."), contextUri);

            con.add(HttpProviderImpl.getProviderEndpointUrl(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(HttpProviderImpl.getProviderEndpointUrl(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(HttpProviderImpl.getProviderEndpointUrl(), RDFS.DOMAIN, HttpProviderImpl.getProviderHttpProviderUri(), contextUri);
            con.add(HttpProviderImpl.getProviderEndpointUrl(), RDFS.LABEL, f.createLiteral("The URL template for this provider. If it contains variables, these may be replaced when executing a query."), contextUri);

	        // If everything went as planned, we can commit the result
	        con.commit();
	        
	        return true;
	    }
	    catch (RepositoryException re)
	    {
	        // Something went wrong during the transaction, so we roll it back
	        
	        if(con != null)
	            con.rollback();
	            
	        log.error("RepositoryException: "+re.getMessage());
	    }
	    finally
	    {
	        if(con != null)
	            con.close();
	    }
	    
	    return false;
	}

    public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
    	super.toRdf(myRepository, keyToUse, modelVersion);

        RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
        	con.setAutoCommit(false);
        	
            if(_TRACE)
            {
                log.trace("Provider.toRdf: keyToUse="+keyToUse);
            }

            // create some resources and literals to make statements out of
            URI providerInstanceUri = this.getKey();
            
		    // backwards compatibility check, and use default if nothing was previously specified
		    // NOTE: we assume empty accept header is non-intentional as it doesn't have a non-trivial purpose
		    Literal acceptHeaderLiteral = null;
		    
		    if(getAcceptHeaderString() == null || getAcceptHeaderString().trim().equals(""))
		    {
		        acceptHeaderLiteral = f.createLiteral(Settings.getSettings().getStringPropertyFromConfig("defaultAcceptHeader", ""));
		    }
		    else
		    {
		        acceptHeaderLiteral = f.createLiteral(getAcceptHeaderString());
		    }
		    
            Literal useSparqlGraphLiteral = f.createLiteral(getUseSparqlGraph());
            Literal sparqlGraphUriLiteral = f.createLiteral(getSparqlGraphUri());

            con.setAutoCommit(false);
		    
		    con.add(providerInstanceUri, RDF.TYPE, HttpProviderImpl.getProviderTypeUri(), keyToUse);

		    con.add(providerInstanceUri, HttpProviderImpl.getProviderAcceptHeader(), acceptHeaderLiteral, keyToUse);
		    
            con.add(providerInstanceUri, getProviderRequiresSparqlGraphURI(), useSparqlGraphLiteral, keyToUse);
            
            con.add(providerInstanceUri, getProviderGraphUri(), sparqlGraphUriLiteral, keyToUse);
            
		
		    if(getEndpointUrls() != null)
		    {
		        for(String nextEndpointUrl : getEndpointUrls())
		        {
		            if(nextEndpointUrl != null)
		            {
		                con.add(providerInstanceUri, HttpProviderImpl.getProviderEndpointUrl(), f.createLiteral(nextEndpointUrl), keyToUse);
		            }
		        }
		    }
		    
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch (RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();
            
            log.error("RepositoryException: "+re.getMessage());
        }
        catch(Exception ex)
        {
            // Something went wrong during the transaction, so we roll it back
            con.rollback();

            log.error("Exception.. this.getKey()="+this.getKey().stringValue(), ex);
        }
        finally
        {
            con.close();
        }
        
        return false;
    }
	
    /**
     * @return a collection of the relevant element types that are implemented by this class, including abstract implementations
     */
    @Override
	public Collection<URI> getElementTypes()
    {
    	Collection<URI> results = super.getElementTypes();
        
    	results.add(getProviderHttpProviderUri());
    	results.add(getProviderSparqlProviderUri());
    	
        return results;
    }

    @Override
    public Collection<String> getEndpointUrls()
    {
        return endpointUrls;
    }
    
    @Override
    public void setEndpointUrls(Collection<String> endpointUrls)
    {
        this.endpointUrls = endpointUrls;
    }

    @Override
	public boolean isHttpPostSparql()
    {
        return this.getEndpointMethod().equals(HttpProviderImpl.getProviderHttpPostSparql());
    }
    
    @Override
    public boolean isHttpGetUrl()
    {
        return this.getEndpointMethod().equals(HttpProviderImpl.getProviderHttpGetUrl());
    }
    
    @Override
    public boolean hasEndpointUrl()
    {
        return (this.getEndpointUrls() != null && this.getEndpointUrls().size() > 0);
    }

	@Override
	public void addEndpointUrl(String endpointUrl)
	{
		if(this.endpointUrls == null)
			this.endpointUrls = new HashSet<String>();
		
		this.endpointUrls.add(endpointUrl);
	
	}

	public String getAcceptHeaderString()
    {
        if(acceptHeaderString != null && !acceptHeaderString.trim().equals(""))
        {
            return acceptHeaderString;
        }
        else
        {
            return Settings.getSettings().getStringPropertyFromConfig("defaultAcceptHeader", "");
        }
    }
    
    public void setAcceptHeaderString(String acceptHeaderString)
    {
        this.acceptHeaderString = acceptHeaderString;
    }
    
	public boolean getUseSparqlGraph()
	{
	    return useSparqlGraph;
	}

	public void setUseSparqlGraph(boolean useSparqlGraph)
	{
	    this.useSparqlGraph = useSparqlGraph;
	}

	public String getSparqlGraphUri()
	{
		if(this.getUseSparqlGraph())
			return sparqlGraphUri;
		else
			return "";
	}

	public void setSparqlGraphUri(String sparqlGraphUri)
	{
	    this.sparqlGraphUri = sparqlGraphUri;
	}

	/**
	 * @param providerEndpointUrl the providerEndpointUrl to set
	 */
	public static void setProviderEndpointUrl(URI providerEndpointUrl) {
		HttpProviderImpl.providerEndpointUrl = providerEndpointUrl;
	}

	/**
	 * @return the providerEndpointUrl
	 */
	public static URI getProviderEndpointUrl() {
		return providerEndpointUrl;
	}

	/**
	 * @return the providerAcceptHeader
	 */
	public static URI getProviderAcceptHeader() {
		return providerAcceptHeader;
	}

	/**
	 * @param providerAcceptHeader the providerAcceptHeader to set
	 */
	public static void setProviderAcceptHeader(URI providerAcceptHeader) {
		HttpProviderImpl.providerAcceptHeader = providerAcceptHeader;
	}

	/**
	 * @return the providerHttpPostUrl
	 */
	public static URI getProviderHttpPostUrl() {
		return HttpProviderImpl.providerHttpPostUrl;
	}

	/**
	 * @param providerHttpPostSparql the providerHttpPostSparql to set
	 */
	public static void setProviderHttpPostSparql(URI providerHttpPostSparql) {
		HttpProviderImpl.providerHttpPostSparql = providerHttpPostSparql;
	}

	/**
	 * @return the providerHttpPostSparql
	 */
	public static URI getProviderHttpPostSparql() {
		return HttpProviderImpl.providerHttpPostSparql;
	}

	/**
	 * @param providerHttpPostUrl the providerHttpPostUrl to set
	 */
	public static void setProviderHttpPostUrl(URI providerHttpPostUrl) {
		HttpProviderImpl.providerHttpPostUrl = providerHttpPostUrl;
	}

	/**
	 * @param providerHttpGetUrl the providerHttpGetUrl to set
	 */
	public static void setProviderHttpGetUrl(URI providerHttpGetUrl) {
		HttpProviderImpl.providerHttpGetUrl = providerHttpGetUrl;
	}

	/**
	 * @return the providerHttpGetUrl
	 */
	public static URI getProviderHttpGetUrl() {
		return HttpProviderImpl.providerHttpGetUrl;
	}

	public static URI getProviderHttpGetUrlUri()
	{
	    return HttpProviderImpl.getProviderHttpGetUrl();
	}

	public static URI getProviderHttpPostUrlUri()
	{
	    return HttpProviderImpl.getProviderHttpPostUrl();
	}

	public static URI getProviderHttpPostSparqlUri()
	{
	    return HttpProviderImpl.getProviderHttpPostSparql();
	}

	/**
	 * @param providerHttpProviderUri the providerHttpProviderUri to set
	 */
	public static void setProviderHttpProviderUri(
			URI providerHttpProviderUri)
	{
		HttpProviderImpl.providerHttpProviderUri = providerHttpProviderUri;
	}

	/**
	 * @return the providerHttpProviderUri
	 */
	public static URI getProviderHttpProviderUri()
	{
		return providerHttpProviderUri;
	}


	/**
	 * @return the providerSparqlProviderUri
	 */
	public static URI getProviderSparqlProviderUri()
	{
		return providerSparqlProviderUri;
	}


	/**
	 * @param providerSparqlProviderUri the providerSparqlProviderUri to set
	 */
	public static void setProviderSparqlProviderUri(
			URI providerSparqlProviderUri)
	{
		HttpProviderImpl.providerSparqlProviderUri = providerSparqlProviderUri;
	}

    
}
