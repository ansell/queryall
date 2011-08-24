package org.queryall.api.provider;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.sail.memory.model.MemValueFactory;
import org.queryall.api.utils.QueryAllNamespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class HttpProviderSchema
{
    static final Logger log = LoggerFactory.getLogger(HttpProviderSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = HttpProviderSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = HttpProviderSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = HttpProviderSchema.log.isInfoEnabled();
    
    private static URI providerHttpProviderUri;
    private static URI providerHttpPostSparql;
    private static URI providerHttpGetUrl;
    private static URI providerHttpPostUrl;
    private static URI providerAcceptHeader;
    private static URI providerEndpointUrl;
    private static URI providerSparqlProviderUri;
    
    static
    {
        final ValueFactory f = new MemValueFactory();
        
        final String baseUri = QueryAllNamespaces.PROVIDER.getBaseURI();
        
        HttpProviderSchema.setProviderHttpProviderUri(f.createURI(baseUri, "HttpProvider"));
        HttpProviderSchema.setProviderSparqlProviderUri(f.createURI(baseUri, "SparqlProvider"));
        HttpProviderSchema.setProviderEndpointUrl(f.createURI(baseUri, "endpointUrl"));
        HttpProviderSchema.setProviderAcceptHeader(f.createURI(baseUri, "acceptHeader"));
        HttpProviderSchema.setProviderHttpPostSparql(f.createURI(baseUri, "httppostsparql"));
        HttpProviderSchema.setProviderHttpGetUrl(f.createURI(baseUri, "httpgeturl"));
        HttpProviderSchema.setProviderHttpPostUrl(f.createURI(baseUri, "httpposturl"));
    }
    
    /**
     * @return the providerAcceptHeader
     */
    public static URI getProviderAcceptHeader()
    {
        return HttpProviderSchema.providerAcceptHeader;
    }
    
    /**
     * @return the providerEndpointUrl
     */
    public static URI getProviderEndpointUrl()
    {
        return HttpProviderSchema.providerEndpointUrl;
    }
    
    /**
     * @return the providerHttpGetUrl
     */
    public static URI getProviderHttpGetUrl()
    {
        return HttpProviderSchema.providerHttpGetUrl;
    }
    
    public static URI getProviderHttpGetUrlUri()
    {
        return HttpProviderSchema.getProviderHttpGetUrl();
    }
    
    /**
     * @return the providerHttpPostSparql
     */
    public static URI getProviderHttpPostSparql()
    {
        return HttpProviderSchema.providerHttpPostSparql;
    }
    
    public static URI getProviderHttpPostSparqlUri()
    {
        return HttpProviderSchema.getProviderHttpPostSparql();
    }
    
    /**
     * @return the providerHttpPostUrl
     */
    public static URI getProviderHttpPostUrl()
    {
        return HttpProviderSchema.providerHttpPostUrl;
    }
    
    public static URI getProviderHttpPostUrlUri()
    {
        return HttpProviderSchema.getProviderHttpPostUrl();
    }
    
    /**
     * @return the providerHttpProviderUri
     */
    public static URI getProviderHttpProviderUri()
    {
        return HttpProviderSchema.providerHttpProviderUri;
    }
    
    /**
     * @return the providerSparqlProviderUri
     */
    public static URI getProviderSparqlProviderUri()
    {
        return HttpProviderSchema.providerSparqlProviderUri;
    }
    
    public static boolean schemaToRdf(final Repository myRepository, final URI contextUri, final int modelVersion)
        throws OpenRDFException
    {
        ProviderSchema.schemaToRdf(myRepository, contextUri, modelVersion);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = new MemValueFactory();
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(HttpProviderSchema.getProviderHttpProviderUri(), RDF.TYPE, OWL.CLASS, contextUri);
            con.add(HttpProviderSchema.getProviderHttpProviderUri(), RDFS.SUBCLASSOF,
                    ProviderSchema.getProviderTypeUri(), contextUri);
            
            con.add(HttpProviderSchema.getProviderAcceptHeader(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(HttpProviderSchema.getProviderAcceptHeader(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(HttpProviderSchema.getProviderAcceptHeader(), RDFS.DOMAIN,
                    HttpProviderSchema.getProviderHttpProviderUri(), contextUri);
            con.add(HttpProviderSchema.getProviderAcceptHeader(), RDFS.LABEL,
                    f.createLiteral("The HTTP Accept header to send to this provider."), contextUri);
            
            con.add(HttpProviderSchema.getProviderEndpointUrl(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(HttpProviderSchema.getProviderEndpointUrl(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(HttpProviderSchema.getProviderEndpointUrl(), RDFS.DOMAIN,
                    HttpProviderSchema.getProviderHttpProviderUri(), contextUri);
            con.add(HttpProviderSchema.getProviderEndpointUrl(),
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
            
            HttpProviderSchema.log.error("RepositoryException: " + re.getMessage());
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
        HttpProviderSchema.providerAcceptHeader = providerAcceptHeader;
    }
    
    /**
     * @param providerEndpointUrl
     *            the providerEndpointUrl to set
     */
    public static void setProviderEndpointUrl(final URI providerEndpointUrl)
    {
        HttpProviderSchema.providerEndpointUrl = providerEndpointUrl;
    }
    
    /**
     * @param providerHttpGetUrl
     *            the providerHttpGetUrl to set
     */
    public static void setProviderHttpGetUrl(final URI providerHttpGetUrl)
    {
        HttpProviderSchema.providerHttpGetUrl = providerHttpGetUrl;
    }
    
    /**
     * @param providerHttpPostSparql
     *            the providerHttpPostSparql to set
     */
    public static void setProviderHttpPostSparql(final URI providerHttpPostSparql)
    {
        HttpProviderSchema.providerHttpPostSparql = providerHttpPostSparql;
    }
    
    /**
     * @param providerHttpPostUrl
     *            the providerHttpPostUrl to set
     */
    public static void setProviderHttpPostUrl(final URI providerHttpPostUrl)
    {
        HttpProviderSchema.providerHttpPostUrl = providerHttpPostUrl;
    }
    
    /**
     * @param providerHttpProviderUri
     *            the providerHttpProviderUri to set
     */
    public static void setProviderHttpProviderUri(final URI providerHttpProviderUri)
    {
        HttpProviderSchema.providerHttpProviderUri = providerHttpProviderUri;
    }
    
    /**
     * @param providerSparqlProviderUri
     *            the providerSparqlProviderUri to set
     */
    public static void setProviderSparqlProviderUri(final URI providerSparqlProviderUri)
    {
        HttpProviderSchema.providerSparqlProviderUri = providerSparqlProviderUri;
    }
}