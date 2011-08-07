package org.queryall.queryutils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.HttpProvider;
import org.queryall.api.NormalisationRule;
import org.queryall.api.Profile;
import org.queryall.api.Provider;
import org.queryall.api.QueryType;
import org.queryall.helpers.Constants;
import org.queryall.helpers.RuleUtils;
import org.queryall.helpers.Settings;
import org.queryall.helpers.SortOrder;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class QueryBundle
{
    private static final Logger log = Logger.getLogger(QueryBundle.class.getName());
    @SuppressWarnings("unused")
    private static final boolean _TRACE = QueryBundle.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = QueryBundle.log.isDebugEnabled();
    private static final boolean _INFO = QueryBundle.log.isInfoEnabled();
    
    // This query is specifically tailored for the provider with respect to the URI
    // (de)normalisation rules
    private String query = "";
    private String staticRdfXmlString = "";
    // Note: although the endpoint URL's are available in the Provider,
    // this is the one that has actually been chosen and had variables replaced for this bundle out
    // of the available provider endpoints
    private String queryEndpoint = "";
    // The following is the unreplaced endpoint String
    private String originalEndpointString = "";
    private Provider originalProvider;
    private QueryType customQueryType;
    private boolean redirectRequired = false;
    
    private Collection<Profile> relevantProfiles = new HashSet<Profile>();
    private Map<String, String> alternativeEndpointsAndQueries;
    
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
        final ValueFactory f = Constants.valueFactory;
        
        QueryBundle.queryBundleNamespace =
                Settings.getSettings().getOntologyTermUriPrefix() + Settings.getSettings().getNamespaceForQueryBundle()
                        + Settings.getSettings().getOntologyTermUriSuffix();
        
        QueryBundle.queryBundleTypeUri = f.createURI(QueryBundle.queryBundleNamespace, "QueryBundle");
        QueryBundle.queryLiteralUri = f.createURI(QueryBundle.queryBundleNamespace, "hasQueryLiteral");
        QueryBundle.queryBundleEndpointUriTerm =
                f.createURI(QueryBundle.queryBundleNamespace, "hasQueryBundleEndpoint");
        QueryBundle.queryBundleOriginalEndpointStringTerm =
                f.createURI(QueryBundle.queryBundleNamespace, "hasOriginalEndpointString");
        QueryBundle.queryBundleAlternativeEndpointUriTerm =
                f.createURI(QueryBundle.queryBundleNamespace, "hasAlternativeQueryBundleEndpoint");
        QueryBundle.queryBundleKeyUri = f.createURI(QueryBundle.queryBundleNamespace, "hasQueryBundleKey");
        QueryBundle.queryBundleQueryTypeUri = f.createURI(QueryBundle.queryBundleNamespace, "hasQueryTypeUri");
        QueryBundle.queryBundleProviderUri = f.createURI(QueryBundle.queryBundleNamespace, "hasProviderUri");
        QueryBundle.queryBundleConfigurationApiVersion =
                f.createURI(QueryBundle.queryBundleNamespace, "hasConfigurationApiVersion");
        QueryBundle.queryBundleProfileUri = f.createURI(QueryBundle.queryBundleNamespace, "hasProfileUri");
    }
    
    public String getQueryEndpoint()
    {
        return queryEndpoint;
    }
    
    public static boolean schemaToRdf(Repository myRepository, URI contextUri, int modelVersion)
        throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(QueryBundle.queryBundleTypeUri, RDF.TYPE, OWL.CLASS, contextUri);
            
            con.add(QueryBundle.queryLiteralUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            
            con.add(QueryBundle.queryBundleEndpointUriTerm, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            
            con.add(QueryBundle.queryBundleKeyUri, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            
            con.add(QueryBundle.queryBundleQueryTypeUri, RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            
            con.add(QueryBundle.queryBundleProviderUri, RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            
            con.add(QueryBundle.queryBundleProfileUri, RDF.TYPE, OWL.OBJECTPROPERTY, contextUri);
            
            con.add(QueryBundle.queryBundleConfigurationApiVersion, RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            
            if(con != null)
            {
                con.rollback();
            }
            
            QueryBundle.log.error("RepositoryException: " + re.getMessage());
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
    
    // this will remove the current queryEndpoint from the list of endpoint URL's in the
    // originalProvider and return the new list
    // NOTE: this will not work if the URL has any templates replaced against it
    public Collection<String> getAlternativeEndpoints()
    {
        Collection<String> results = new LinkedList<String>();
        
        if(getProvider() instanceof HttpProvider)
        {
            Collection<String> allEndpoints = ((HttpProvider)getProvider()).getEndpointUrls();
            
            if(allEndpoints == null || allEndpoints.size() <= 1)
            {
                return results;
            }
            
            for(String nextEndpointUrl : allEndpoints)
            {
                if(!nextEndpointUrl.equals(getQueryEndpoint()))
                {
                    results.add(nextEndpointUrl);
                }
            }
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
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            String keyPrefix =
                    Settings.getSettings().getDefaultHostAddress()
                            + Settings.getSettings().getNamespaceForQueryBundle()
                            + Settings.getSettings().getStringProperty("separator", ":");
            
            // create some resources and literals to make statements out of
            URI queryBundleInstanceUri = f.createURI(keyPrefix + keyToUse.stringValue());
            
            Literal keyLiteral = f.createLiteral(keyToUse.stringValue());
            Literal queryLiteral = f.createLiteral(getQuery());
            Literal queryBundleEndpointUri = f.createLiteral(getQueryEndpoint());
            Literal modelVersionLiteral = f.createLiteral(modelVersion);
            
            Collection<String> alternativeEndpoints = getAlternativeEndpoints();
            
            // make sure we can commit them all as far as this query bundle itself is concerned
            // before we actually put statements in
            con.setAutoCommit(false);
            
            con.add(queryBundleInstanceUri, RDF.TYPE, QueryBundle.queryBundleTypeUri, keyToUse);
            con.add(queryBundleInstanceUri, QueryBundle.queryLiteralUri, queryLiteral, keyToUse);
            con.add(queryBundleInstanceUri, QueryBundle.queryBundleEndpointUriTerm, queryBundleEndpointUri, keyToUse);
            con.add(queryBundleInstanceUri, QueryBundle.queryBundleKeyUri, keyLiteral, keyToUse);
            con.add(queryBundleInstanceUri, QueryBundle.queryBundleConfigurationApiVersion, modelVersionLiteral,
                    keyToUse);
            
            URI queryTypeUri = getQueryType().getKey();
            
            con.add(queryBundleInstanceUri, QueryBundle.queryBundleQueryTypeUri, queryTypeUri, keyToUse);
            
            URI originalProviderUri = getOriginalProvider().getKey();
            
            con.add(queryBundleInstanceUri, QueryBundle.queryBundleProviderUri, originalProviderUri, keyToUse);
            
            if(getRelevantProfiles() != null)
            {
                for(Profile nextRelevantProfile : getRelevantProfiles())
                {
                    if(nextRelevantProfile != null)
                    {
                        con.add(queryBundleInstanceUri, QueryBundle.queryBundleProfileUri,
                                nextRelevantProfile.getKey(), keyToUse);
                    }
                }
            }
            
            if(alternativeEndpoints != null)
            {
                for(String nextAlternativeEndpoint : alternativeEndpoints)
                {
                    if(nextAlternativeEndpoint != null && !nextAlternativeEndpoint.trim().equals(""))
                    {
                        con.add(queryBundleInstanceUri, QueryBundle.queryBundleAlternativeEndpointUriTerm,
                                f.createLiteral(nextAlternativeEndpoint), keyToUse);
                    }
                }
            }
            
            // We commit here to avoid a commit deadlock that seems to happen when this is at the
            // end of this sequence,
            // as the custom query type and provider configuration and rdf rules all start
            // transactions themselves
            con.commit();
            
            // log.info("QueryBundle: About to add custom query RDF to the repository");
            
            getQueryType().toRdf(myRepository, keyToUse, modelVersion);
            
            // log.info("QueryBundle: About to add provider configuration RDF to the repository");
            
            getOriginalProvider().toRdf(myRepository, keyToUse, modelVersion);
            
            for(NormalisationRule nextRelevantRdfRule : RuleUtils.getSortedRulesByUris(Settings.getSettings()
                    .getAllNormalisationRules(), getOriginalProvider().getNormalisationUris(),
                    SortOrder.LOWEST_ORDER_FIRST))
            {
                nextRelevantRdfRule.toRdf(myRepository, keyToUse, modelVersion);
            }
            
            // log.info("QueryBundle: toRdf returning true");
            
            return true;
        }
        catch(RepositoryException re)
        {
            QueryBundle.log.error("RepositoryException: " + re.getMessage());
            
            if(con != null)
            {
                // Something went wrong during the transaction, so we roll it back
                con.rollback();
            }
        }
        catch(OpenRDFException ordfe)
        {
            QueryBundle.log.error(ordfe);
            
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
        
        if(QueryBundle._INFO)
        {
            QueryBundle.log.info("QueryBundle: toRdf returning false");
        }
        
        return false;
    }
    
    @Override
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
            sb.append("originalProvider.getKey()=" + getOriginalProvider().getKey() + "\n");
        }
        
        return sb.toString();
    }
    
    public void setQueryEndpoint(String endpointUrl)
    {
        queryEndpoint = endpointUrl;
    }
    
    /**
     * @param staticRdfXmlString
     *            the staticRdfXmlString to set
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
     * @param originalEndpointString
     *            the originalEndpointString to set
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
     * @param originalProvider
     *            the originalProvider to set
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
     * @param redirectRequired
     *            the redirectRequired to set
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
     * @param relevantProfiles
     *            the relevantProfiles to set
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
    
    /**
     * Setup a list of alternative endpoints, along with the queries to use for each of the
     * alternative endpoints
     * 
     * @param endpointEntries
     *            A map with alternative endpoints as keys and the queries to use as the entries on
     *            the map
     */
    public void setAlternativeEndpointsAndQueries(Map<String, String> endpointEntries)
    {
        this.alternativeEndpointsAndQueries = endpointEntries;
        
        // remove self-references here so we don't accidentally recursively call the same provider
        // endpoint
        if(this.alternativeEndpointsAndQueries.containsKey(this.getQueryEndpoint())
                && this.alternativeEndpointsAndQueries.get(this.queryEndpoint).equals(this.getQuery()))
        {
            this.alternativeEndpointsAndQueries.remove(this.getQueryEndpoint());
        }
        
    }
    
    /**
     * Get an unmodifiable Map containing alternative endpoints as keys and queries as values
     * 
     * @return An unmodifiable map containing the endpoints and queries that are known to be
     *         alternatives to this query bundle and could be used if there was an error with this
     *         query bundle
     */
    public Map<String, String> getAlternativeEndpointsAndQueries()
    {
        return Collections.unmodifiableMap(alternativeEndpointsAndQueries);
    }
}
