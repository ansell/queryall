package org.queryall.queryutils;

import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.URI;
import org.openrdf.model.Literal;
import org.openrdf.model.ValueFactory;

import org.openrdf.OpenRDFException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.sail.memory.model.MemValueFactory;

import java.util.LinkedList;
import java.util.Collection;
import java.util.HashSet;

import org.queryall.NormalisationRule;
import org.queryall.Provider;
import org.queryall.QueryType;
import org.queryall.Profile;

import org.queryall.helpers.Constants;
import org.queryall.helpers.Settings;

import org.apache.log4j.Logger;

public class QueryBundle 
{
    private static final Logger log = Logger.getLogger(QueryBundle.class.getName());
    @SuppressWarnings("unused")
	private static final boolean _TRACE = log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = log.isDebugEnabled();
    private static final boolean _INFO = log.isInfoEnabled();
	
	// This query is specifically tailored for the provider with respect to the URI (de)normalisation rules
	private String query = "";
	private String staticRdfXmlString = "";
	// Note: although the endpoint URL's are available in the Provider,
	// this is the one that has actually been chosen and had variables replaced for this bundle out of the available provider endpoints
	private String queryEndpoint = "";
	// The following is the unreplaced endpoint String
	private String originalEndpointString = "";
	private Provider originalProvider;
	private QueryType customQueryType;
	private boolean redirectRequired = false;
	
	private Collection<Profile> relevantProfiles = new HashSet<Profile>();
	
    public static String queryBundleNamespace;
    
    public static URI queryBundleTypeUri;
    public static URI queryLiteralUri;
    public static URI queryBundleEndpointUriTerm;
    public static URI queryBundleOriginalEndpointStringTerm;
    public static URI queryBundleAlternativeEndpointUriTerm;
    public static URI queryBundleKeyUri;
    public static URI queryBundleQueryTypeUri;
    public static URI queryBundleProviderUri;
    public static URI queryBundleProfileUri;
    public static URI queryBundleConfigurationApiVersion;
    
    static
    {
        ValueFactory f = new MemValueFactory();
        
        queryBundleNamespace = Settings.getSettings().getOntologyTermUriPrefix()
			+Settings.getSettings().getNamespaceForQueryBundle()
			+Settings.getSettings().getOntologyTermUriSuffix();
		
        queryBundleTypeUri = f.createURI(queryBundleNamespace+"QueryBundle");
        queryLiteralUri = f.createURI(queryBundleNamespace+"hasQueryLiteral");
        queryBundleEndpointUriTerm = f.createURI(queryBundleNamespace+"hasQueryBundleEndpoint");
        queryBundleOriginalEndpointStringTerm = f.createURI(queryBundleNamespace+"hasOriginalEndpointString");
        queryBundleAlternativeEndpointUriTerm = f.createURI(queryBundleNamespace+"hasAlternativeQueryBundleEndpoint");
        queryBundleKeyUri = f.createURI(queryBundleNamespace+"hasQueryBundleKey");
        queryBundleQueryTypeUri = f.createURI(queryBundleNamespace+"hasQueryTypeUri");
        queryBundleProviderUri = f.createURI(queryBundleNamespace+"hasProviderUri");
        queryBundleConfigurationApiVersion = f.createURI(queryBundleNamespace+"hasConfigurationApiVersion");
        queryBundleProfileUri = f.createURI(queryBundleNamespace+"hasProfileUri");
    }
    
    public String getQueryEndpoint()
    {
        return queryEndpoint;
    }
    
    public static boolean schemaToRdf(Repository myRepository, String keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            URI contextKeyUri = f.createURI(keyToUse);
            
            con.setAutoCommit(false);
            
            con.add(queryBundleTypeUri, RDF.TYPE, OWL.CLASS, contextKeyUri);
            
            con.add(queryLiteralUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(queryBundleEndpointUriTerm, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(queryBundleKeyUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(queryBundleQueryTypeUri, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            
            con.add(queryBundleProviderUri, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            
            con.add(queryBundleProfileUri, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);

            con.add(queryBundleConfigurationApiVersion, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
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
    
    // this will remove the current queryEndpoint from the list of endpoint URL's in the originalProvider and return the new list
    // NOTE: this will not work if the URL has any templates replaced against it
    public Collection<String> getAlternativeEndpoints()
    {
        Collection<String> allEndpoints = getOriginalProvider().getEndpointUrls();
        
        Collection<String> results = new LinkedList<String>();
        
        if(allEndpoints == null || allEndpoints.size() <=1)
            return results;
        
        for(String nextEndpointUrl : allEndpoints)
        {
            if(!nextEndpointUrl.equals(getQueryEndpoint()))
                results.add(nextEndpointUrl);
        }
        
        return results;
    }
    
    public String getQuery()
    {
        return query;
    }
    
    public QueryType getQueryType()
    {
        return customQueryType;
    }
    
    public Provider getProvider()
    {
        return getOriginalProvider();
    }
    

    public void setQuery(String query)
    {
        this.query = query;
    }
    
    public void setQueryType(QueryType customQueryType)
    {
        this.customQueryType = customQueryType;
    }
    
    public void setProvider(Provider originalProvider)
    {
        this.setOriginalProvider(originalProvider);
    }
    

	public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
	{
		// log.info("QueryBundle: About to generate Rdf");
		
		RepositoryConnection con = myRepository.getConnection();
		
		ValueFactory f = myRepository.getValueFactory();
		
		try
		{
			String keyPrefix = Settings.getSettings().getDefaultHostAddress()
				+ Settings.getSettings().getNamespaceForQueryBundle()
				+ Settings.getSettings().getStringPropertyFromConfig("separator");
			
			// create some resources and literals to make statements out of
			URI queryBundleInstanceUri = f.createURI(keyPrefix + keyToUse);
			
			Literal keyLiteral = f.createLiteral(keyToUse.stringValue());
			Literal queryLiteral = f.createLiteral(getQuery());
			Literal queryBundleEndpointUri = f.createLiteral(getQueryEndpoint());
			Literal modelVersionLiteral = f.createLiteral(modelVersion);
			
			Collection<String> alternativeEndpoints = getAlternativeEndpoints();
			
			
			// make sure we can commit them all as far as this query bundle itself is concerned before we actually put statements in
			con.setAutoCommit(false);
			
			con.add(queryBundleInstanceUri, RDF.TYPE, queryBundleTypeUri, queryBundleInstanceUri);
			con.add(queryBundleInstanceUri, queryLiteralUri, queryLiteral, queryBundleInstanceUri);
			con.add(queryBundleInstanceUri, queryBundleEndpointUriTerm, queryBundleEndpointUri, queryBundleInstanceUri);
			con.add(queryBundleInstanceUri, queryBundleKeyUri, keyLiteral, queryBundleInstanceUri);
			con.add(queryBundleInstanceUri, queryBundleConfigurationApiVersion, modelVersionLiteral, queryBundleInstanceUri);
			
			URI QueryTypeUri = getQueryType().getKey();
			
			con.add(queryBundleInstanceUri, queryBundleQueryTypeUri, QueryTypeUri, queryBundleInstanceUri);
			
			URI originalProviderUri = getOriginalProvider().getKey();
			
			con.add(queryBundleInstanceUri, queryBundleProviderUri, originalProviderUri, queryBundleInstanceUri);
			
			if(getRelevantProfiles() != null)
			{
			    for(Profile nextRelevantProfile : getRelevantProfiles())
			    {
			        if(nextRelevantProfile != null)
			            con.add(queryBundleInstanceUri, queryBundleProfileUri, nextRelevantProfile.getKey());
			    }
			}
			
			if(alternativeEndpoints != null)
			{
			    for(String nextAlternativeEndpoint : alternativeEndpoints)
			    {
			        if(nextAlternativeEndpoint != null && !nextAlternativeEndpoint.trim().equals(""))
			            con.add(queryBundleInstanceUri, queryBundleAlternativeEndpointUriTerm, f.createLiteral(nextAlternativeEndpoint));
			    }
			}


			// We commit here to avoid a commit deadlock that seems to happen when this is at the end of this sequence, 
			// as the custom query type and provider configuration and rdf rules all start transactions themselves
			con.commit();
			
			// log.info("QueryBundle: About to add custom query RDF to the repository");
			
			getQueryType().toRdf(myRepository, getQueryType().getKey(), modelVersion);
			
			
			// log.info("QueryBundle: About to add provider configuration RDF to the repository");
			
			getOriginalProvider().toRdf(myRepository, getOriginalProvider().getKey(), modelVersion);
			
			for(NormalisationRule nextRelevantRdfRule : Settings.getSettings().getNormalisationRulesForUris(getOriginalProvider().getNormalisationUris(), Constants.LOWEST_ORDER_FIRST))
			{
				nextRelevantRdfRule.toRdf(myRepository, nextRelevantRdfRule.getKey(), modelVersion);
			}
			
			// log.info("QueryBundle: toRdf returning true");
			
			return true;
		}
		catch (RepositoryException re) 
		{
			log.error("RepositoryException: "+re.getMessage());
			
			if(con != null)
			{
				// Something went wrong during the transaction, so we roll it back
				con.rollback();
			}
		}
		catch (OpenRDFException ordfe) 
		{
			log.error(ordfe);
			
			if(con != null)
			{
				// Something went wrong during the transaction, so we roll it back
				con.rollback();
			}
			
			throw ordfe;
		}
		finally
		{
			if(con != null)
			{
				con.close();
			}
		}
		
        if(_INFO)
        {
            log.info("QueryBundle: toRdf returning false");
        }
		
		return false;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		if(getQueryType() == null)
		{
			sb.append("getQueryType()=null\n");
		}
		else
		{
			sb.append("getQueryType().getKey()=" + getQueryType().getKey() + "\n");
		}
		
		sb.append("queryEndpoint=" + getQueryEndpoint() + "\n");
		sb.append("query=" + getQuery() + "\n");
		sb.append("staticRdfXmlString=" + getStaticRdfXmlString() + "\n");
		
		if(getOriginalProvider() == null)
		{
			sb.append("originalProvider=null\n");
		}
		else
		{
			sb.append("originalProvider.getKey()="+getOriginalProvider().getKey() + "\n");
		}
		
		return sb.toString();
	}
	
	public void setQueryEndpoint(String endpointUrl)
	{
	    queryEndpoint = endpointUrl;
	}

    /**
     * @param staticRdfXmlString the staticRdfXmlString to set
     */
    public void setStaticRdfXmlString(String staticRdfXmlString)
    {
        this.staticRdfXmlString = staticRdfXmlString;
    }

    /**
     * @return the staticRdfXmlString
     */
    public String getStaticRdfXmlString()
    {
        return staticRdfXmlString;
    }

    /**
     * @param originalEndpointString the originalEndpointString to set
     */
    public void setOriginalEndpointString(String originalEndpointString)
    {
        this.originalEndpointString = originalEndpointString;
    }

    /**
     * @return the originalEndpointString
     */
    public String getOriginalEndpointString()
    {
        return originalEndpointString;
    }

    /**
     * @param originalProvider the originalProvider to set
     */
    public void setOriginalProvider(Provider originalProvider)
    {
        this.originalProvider = originalProvider;
    }

    /**
     * @return the originalProvider
     */
    public Provider getOriginalProvider()
    {
        return originalProvider;
    }

    /**
     * @param redirectRequired the redirectRequired to set
     */
    public void setRedirectRequired(boolean redirectRequired)
    {
        this.redirectRequired = redirectRequired;
    }

    /**
     * @return the redirectRequired
     */
    public boolean getRedirectRequired()
    {
        return redirectRequired;
    }

    /**
     * @param relevantProfiles the relevantProfiles to set
     */
    public void setRelevantProfiles(Collection<Profile> relevantProfiles)
    {
        this.relevantProfiles = relevantProfiles;
    }

    /**
     * @return the relevantProfiles
     */
    public Collection<Profile> getRelevantProfiles()
    {
        return relevantProfiles;
    }
}
