package org.queryall.api.provider;

import org.kohsuke.MetaInfServices;
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
import org.queryall.api.base.QueryAllSchema;
import org.queryall.api.utils.QueryAllNamespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices(QueryAllSchema.class)
public class HttpProviderSchema extends QueryAllSchema
{
    static final Logger LOG = LoggerFactory.getLogger(HttpProviderSchema.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = HttpProviderSchema.LOG.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = HttpProviderSchema.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = HttpProviderSchema.LOG.isInfoEnabled();
    
    private static URI providerHttpProviderUri;
    private static URI providerHttpGetUrl;
    private static URI providerHttpPostUrl;
    private static URI providerAcceptHeader;
    private static URI providerEndpointUrl;
    
    static
    {
        final ValueFactory f = new MemValueFactory();
        
        final String baseUri = QueryAllNamespaces.PROVIDER.getBaseURI();
        
        HttpProviderSchema.setProviderHttpProviderUri(f.createURI(baseUri, "HttpProvider"));
        HttpProviderSchema.setProviderEndpointUrl(f.createURI(baseUri, "endpointUrl"));
        HttpProviderSchema.setProviderAcceptHeader(f.createURI(baseUri, "acceptHeader"));
        HttpProviderSchema.setProviderHttpGetUrl(f.createURI(baseUri, "httpgeturl"));
        HttpProviderSchema.setProviderHttpPostUrl(f.createURI(baseUri, "httpposturl"));
    }
    
    public static final QueryAllSchema HTTP_PROVIDER_SCHEMA = new HttpProviderSchema();
    
    /**
     * @return the providerAcceptHeader
     */
    public static URI getProviderHttpAcceptHeader()
    {
        return HttpProviderSchema.providerAcceptHeader;
    }
    
    /**
     * @return the providerEndpointUrl
     */
    public static URI getProviderHttpEndpointUrl()
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
    
    /**
     * @return the providerHttpPostUrl
     */
    public static URI getProviderHttpPostUrl()
    {
        return HttpProviderSchema.providerHttpPostUrl;
    }
    
    /**
     * @return the providerHttpProviderUri
     */
    public static URI getProviderHttpTypeUri()
    {
        return HttpProviderSchema.providerHttpProviderUri;
    }
    
    /**
     * @param nextProviderAcceptHeader
     *            the providerAcceptHeader to set
     */
    public static void setProviderAcceptHeader(final URI nextProviderAcceptHeader)
    {
        HttpProviderSchema.providerAcceptHeader = nextProviderAcceptHeader;
    }
    
    /**
     * @param nextProviderEndpointUrl
     *            the providerEndpointUrl to set
     */
    public static void setProviderEndpointUrl(final URI nextProviderEndpointUrl)
    {
        HttpProviderSchema.providerEndpointUrl = nextProviderEndpointUrl;
    }
    
    /**
     * @param nextProviderHttpGetUrl
     *            the providerHttpGetUrl to set
     */
    public static void setProviderHttpGetUrl(final URI nextProviderHttpGetUrl)
    {
        HttpProviderSchema.providerHttpGetUrl = nextProviderHttpGetUrl;
    }
    
    /**
     * @param nextProviderHttpPostUrl
     *            the providerHttpPostUrl to set
     */
    public static void setProviderHttpPostUrl(final URI nextProviderHttpPostUrl)
    {
        HttpProviderSchema.providerHttpPostUrl = nextProviderHttpPostUrl;
    }
    
    /**
     * @param nextProviderHttpProviderUri
     *            the providerHttpProviderUri to set
     */
    public static void setProviderHttpProviderUri(final URI nextProviderHttpProviderUri)
    {
        HttpProviderSchema.providerHttpProviderUri = nextProviderHttpProviderUri;
    }
    
    /**
     * Default constructor, uses the name of this class as the name.
     */
    public HttpProviderSchema()
    {
        this(HttpProviderSchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public HttpProviderSchema(final String nextName)
    {
        super(nextName);
    }
    
    @Override
    public boolean schemaToRdf(final Repository myRepository, final int modelVersion, final URI... contextUri)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = new MemValueFactory();
        
        try
        {
            con.begin();
            
            con.add(HttpProviderSchema.getProviderHttpTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            con.add(HttpProviderSchema.getProviderHttpTypeUri(), RDFS.LABEL,
                    f.createLiteral("The class of HTTP based Data Providers."), contextUri);
            con.add(HttpProviderSchema.getProviderHttpTypeUri(), RDFS.SUBCLASSOF, ProviderSchema.getProviderTypeUri(),
                    contextUri);
            
            con.add(HttpProviderSchema.getProviderHttpAcceptHeader(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(HttpProviderSchema.getProviderHttpAcceptHeader(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(HttpProviderSchema.getProviderHttpAcceptHeader(), RDFS.DOMAIN,
                    HttpProviderSchema.getProviderHttpTypeUri(), contextUri);
            con.add(HttpProviderSchema.getProviderHttpAcceptHeader(), RDFS.LABEL,
                    f.createLiteral("The HTTP Accept header to send to this provider."), contextUri);
            
            con.add(HttpProviderSchema.getProviderHttpEndpointUrl(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(HttpProviderSchema.getProviderHttpEndpointUrl(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(HttpProviderSchema.getProviderHttpEndpointUrl(), RDFS.DOMAIN,
                    HttpProviderSchema.getProviderHttpTypeUri(), contextUri);
            con.add(HttpProviderSchema.getProviderHttpEndpointUrl(),
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
            
            HttpProviderSchema.LOG.error("RepositoryException: " + re.getMessage());
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
}