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
	public String query = "";
	public String staticRdfXmlString = "";
	// Note: although the endpoint URL's are available in the Provider,
	// this is the one that has actually been chosen and had variables replaced for this bundle out of the available provider endpoints
	public String queryEndpoint = "";
	// The following is the unreplaced endpoint String
	public String originalEndpointString = "";
	public Provider originalProvider;
	private QueryType customQueryType;
	public boolean redirectRequired = false;
	
	public Collection<Profile> relevantProfiles = new HashSet<Profile>();
	
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
        
        queryBundleNamespace = Settings.DEFAULT_ONTOLOGYTERMURI_PREFIX
			+Settings.DEFAULT_RDF_QUERYBUNDLE_NAMESPACE
			+Settings.DEFAULT_ONTOLOGYTERMURI_SUFFIX;
		
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
        Collection<String> allEndpoints = originalProvider.getEndpointUrls();
        
        Collection<String> results = new LinkedList<String>();
        
        if(allEndpoints == null || allEndpoints.size() <=1)
            return results;
        
        for(String nextEndpointUrl : allEndpoints)
        {
            if(!nextEndpointUrl.equals(queryEndpoint))
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
        return originalProvider;
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
        this.originalProvider = originalProvider;
    }
    

	public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
	{
		// log.info("QueryBundle: About to generate Rdf");
		
		RepositoryConnection con = myRepository.getConnection();
		
		ValueFactory f = myRepository.getValueFactory();
		
		try
		{
			String keyPrefix = Settings.getDefaultHostAddress()
				+ Settings.DEFAULT_RDF_QUERYBUNDLE_NAMESPACE
				+ Settings.getStringPropertyFromConfig("separator");
			
			// create some resources and literals to make statements out of
			URI queryBundleInstanceUri = f.createURI(keyPrefix + keyToUse);
			
			Literal keyLiteral = f.createLiteral(keyToUse.stringValue());
			Literal queryLiteral = f.createLiteral(query);
			Literal queryBundleEndpointUri = f.createLiteral(queryEndpoint);
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
			
			URI originalProviderUri = originalProvider.getKey();
			
			con.add(queryBundleInstanceUri, queryBundleProviderUri, originalProviderUri, queryBundleInstanceUri);
			
			if(relevantProfiles != null)
			{
			    for(Profile nextRelevantProfile : relevantProfiles)
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
			
			originalProvider.toRdf(myRepository, originalProvider.getKey(), modelVersion);
			
			for(NormalisationRule nextRelevantRdfRule : Settings.getNormalisationRulesForUris(originalProvider.getNormalisationsNeeded(), Settings.LOWEST_ORDER_FIRST))
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
		
		sb.append("queryEndpoint=" + queryEndpoint + "\n");
		sb.append("query=" + query + "\n");
		sb.append("staticRdfXmlString=" + staticRdfXmlString + "\n");
		
		if(originalProvider == null)
		{
			sb.append("originalProvider=null\n");
		}
		else
		{
			sb.append("originalProvider.getKey()="+originalProvider.getKey() + "\n");
		}
		
		return sb.toString();
	}
	
	public void setQueryEndpoint(String endpointUrl)
	{
	    queryEndpoint = endpointUrl;
	}
}
