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
    
    public static boolean schemaToRdf(final Repository myRepository, final URI contextUri, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
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
        catch(final RepositoryException re)
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
        final Collection<String> results = new LinkedList<String>();
        
        if(this.getProvider() instanceof HttpProvider)
        {
            final Collection<String> allEndpoints = ((HttpProvider)this.getProvider()).getEndpointUrls();
            
            if(allEndpoints == null || allEndpoints.size() <= 1)
            {
                return results;
            }
            
            for(final String nextEndpointUrl : allEndpoints)
            {
                if(!nextEndpointUrl.equals(this.getQueryEndpoint()))
                {
                    results.add(nextEndpointUrl);
                }
            }
        }
        
        return results;
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
        return Collections.unmodifiableMap(this.alternativeEndpointsAndQueries);
    }
    
    /**
     * @return the originalEndpointString
     */
    public String getOriginalEndpointString()
    {
        return this.originalEndpointString;
    }
    
    /**
     * @return the originalProvider
     */
    public Provider getOriginalProvider()
    {
        return this.originalProvider;
    }
    
    public Provider getProvider()
    {
        return this.getOriginalProvider();
    }
    
    public String getQuery()
    {
        return this.query;
    }
    
    public String getQueryEndpoint()
    {
        return this.queryEndpoint;
    }
    
    public QueryType getQueryType()
    {
        return this.customQueryType;
    }
    
    /**
     * @return the redirectRequired
     */
    public boolean getRedirectRequired()
    {
        return this.redirectRequired;
    }
    
    /**
     * @return the relevantProfiles
     */
    public Collection<Profile> getRelevantProfiles()
    {
        return this.relevantProfiles;
    }
    
    /**
     * @return the staticRdfXmlString
     */
    public String getStaticRdfXmlString()
    {
        return this.staticRdfXmlString;
    }
    
    /**
     * Setup a list of alternative endpoints, along with the queries to use for each of the
     * alternative endpoints
     * 
     * @param endpointEntries
     *            A map with alternative endpoints as keys and the queries to use as the entries on
     *            the map
     */
    public void setAlternativeEndpointsAndQueries(final Map<String, String> endpointEntries)
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
     * @param originalEndpointString
     *            the originalEndpointString to set
     */
    public void setOriginalEndpointString(final String originalEndpointString)
    {
        this.originalEndpointString = originalEndpointString;
    }
    
    /**
     * @param originalProvider
     *            the originalProvider to set
     */
    public void setOriginalProvider(final Provider originalProvider)
    {
        this.originalProvider = originalProvider;
    }
    
    public void setProvider(final Provider originalProvider)
    {
        this.setOriginalProvider(originalProvider);
    }
    
    public void setQuery(final String query)
    {
        this.query = query;
    }
    
    public void setQueryEndpoint(final String endpointUrl)
    {
        this.queryEndpoint = endpointUrl;
    }
    
    public void setQueryType(final QueryType customQueryType)
    {
        this.customQueryType = customQueryType;
    }
    
    /**
     * @param redirectRequired
     *            the redirectRequired to set
     */
    public void setRedirectRequired(final boolean redirectRequired)
    {
        this.redirectRequired = redirectRequired;
    }
    
    /**
     * @param relevantProfiles
     *            the relevantProfiles to set
     */
    public void setRelevantProfiles(final Collection<Profile> relevantProfiles)
    {
        this.relevantProfiles = relevantProfiles;
    }
    
    /**
     * @param staticRdfXmlString
     *            the staticRdfXmlString to set
     */
    public void setStaticRdfXmlString(final String staticRdfXmlString)
    {
        this.staticRdfXmlString = staticRdfXmlString;
    }
    
    public boolean toRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        // log.info("QueryBundle: About to generate Rdf");
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            final String keyPrefix =
                    Settings.getSettings().getDefaultHostAddress()
                            + Settings.getSettings().getNamespaceForQueryBundle()
                            + Settings.getSettings().getStringProperty("separator", ":");
            
            // create some resources and literals to make statements out of
            final URI queryBundleInstanceUri = f.createURI(keyPrefix + keyToUse.stringValue());
            
            final Literal keyLiteral = f.createLiteral(keyToUse.stringValue());
            final Literal queryLiteral = f.createLiteral(this.getQuery());
            final Literal queryBundleEndpointUri = f.createLiteral(this.getQueryEndpoint());
            final Literal modelVersionLiteral = f.createLiteral(modelVersion);
            
            final Collection<String> alternativeEndpoints = this.getAlternativeEndpoints();
            
            // make sure we can commit them all as far as this query bundle itself is concerned
            // before we actually put statements in
            con.setAutoCommit(false);
            
            con.add(queryBundleInstanceUri, RDF.TYPE, QueryBundle.queryBundleTypeUri, keyToUse);
            con.add(queryBundleInstanceUri, QueryBundle.queryLiteralUri, queryLiteral, keyToUse);
            con.add(queryBundleInstanceUri, QueryBundle.queryBundleEndpointUriTerm, queryBundleEndpointUri, keyToUse);
            con.add(queryBundleInstanceUri, QueryBundle.queryBundleKeyUri, keyLiteral, keyToUse);
            con.add(queryBundleInstanceUri, QueryBundle.queryBundleConfigurationApiVersion, modelVersionLiteral,
                    keyToUse);
            
            final URI queryTypeUri = this.getQueryType().getKey();
            
            con.add(queryBundleInstanceUri, QueryBundle.queryBundleQueryTypeUri, queryTypeUri, keyToUse);
            
            final URI originalProviderUri = this.getOriginalProvider().getKey();
            
            con.add(queryBundleInstanceUri, QueryBundle.queryBundleProviderUri, originalProviderUri, keyToUse);
            
            if(this.getRelevantProfiles() != null)
            {
                for(final Profile nextRelevantProfile : this.getRelevantProfiles())
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
                for(final String nextAlternativeEndpoint : alternativeEndpoints)
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
            
            this.getQueryType().toRdf(myRepository, keyToUse, modelVersion);
            
            // log.info("QueryBundle: About to add provider configuration RDF to the repository");
            
            this.getOriginalProvider().toRdf(myRepository, keyToUse, modelVersion);
            
            for(final NormalisationRule nextRelevantRdfRule : RuleUtils.getSortedRulesByUris(Settings.getSettings()
                    .getAllNormalisationRules(), this.getOriginalProvider().getNormalisationUris(),
                    SortOrder.LOWEST_ORDER_FIRST))
            {
                nextRelevantRdfRule.toRdf(myRepository, keyToUse, modelVersion);
            }
            
            // log.info("QueryBundle: toRdf returning true");
            
            return true;
        }
        catch(final RepositoryException re)
        {
            QueryBundle.log.error("RepositoryException: " + re.getMessage());
            
            if(con != null)
            {
                // Something went wrong during the transaction, so we roll it back
                con.rollback();
            }
        }
        catch(final OpenRDFException ordfe)
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
        final StringBuilder sb = new StringBuilder();
        
        if(this.getQueryType() == null)
        {
            sb.append("getQueryType()=null\n");
        }
        else
        {
            sb.append("getQueryType().getKey()=" + this.getQueryType().getKey() + "\n");
        }
        
        sb.append("queryEndpoint=" + this.getQueryEndpoint() + "\n");
        sb.append("query=" + this.getQuery() + "\n");
        sb.append("staticRdfXmlString=" + this.getStaticRdfXmlString() + "\n");
        
        if(this.getOriginalProvider() == null)
        {
            sb.append("originalProvider=null\n");
        }
        else
        {
            sb.append("originalProvider.getKey()=" + this.getOriginalProvider().getKey() + "\n");
        }
        
        return sb.toString();
    }
}
