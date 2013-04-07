/**
 * 
 */
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
import org.queryall.api.base.QueryAllSchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices(QueryAllSchema.class)
public class SparqlProviderSchema extends QueryAllSchema
{
    private static final Logger LOG = LoggerFactory.getLogger(SparqlProviderSchema.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = SparqlProviderSchema.LOG.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = SparqlProviderSchema.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = SparqlProviderSchema.LOG.isInfoEnabled();
    
    private static URI providerSparqlTypeUri;
    private static URI providerSparqlRequiresGraphURI;
    private static URI providerHttpPostSparql;
    private static URI providerSparqlGraphUri;
    private static URI providerHttpGetSparql;
    
    static
    {
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        final String baseUri = QueryAllNamespaces.PROVIDER.getBaseURI();
        
        SparqlProviderSchema.setProviderSparqlTypeUri(f.createURI(baseUri, "SparqlProvider"));
        
        SparqlProviderSchema.setProviderSparqlRequiresGraphURI(f.createURI(baseUri, "requiresGraphUri"));
        SparqlProviderSchema.setProviderSparqlGraphUri(f.createURI(baseUri, "graphUri"));
        SparqlProviderSchema.setProviderHttpPostSparql(f.createURI(baseUri, "httppostsparql"));
        SparqlProviderSchema.setProviderHttpGetSparql(f.createURI(baseUri, "httpgetsparql"));
        
    }
    
    /**
     * The preinitialized schema object for SparqlProviderSchema.
     */
    public static final QueryAllSchema SPARQL_PROVIDER_SCHEMA = new SparqlProviderSchema();
    
    /**
     * @return the providerHttpGetSparql
     */
    public static URI getProviderHttpGetSparql()
    {
        return SparqlProviderSchema.providerHttpGetSparql;
    }
    
    /**
     * @return the providerHttpPostSparql
     */
    public static URI getProviderHttpPostSparql()
    {
        return SparqlProviderSchema.providerHttpPostSparql;
    }
    
    /**
     * @return the providerSparqlGraphUri
     */
    public static URI getProviderSparqlGraphUri()
    {
        return SparqlProviderSchema.providerSparqlGraphUri;
    }
    
    /**
     * @return the providerSparqlRequiresGraphURI
     */
    public static URI getProviderSparqlRequiresGraphURI()
    {
        return SparqlProviderSchema.providerSparqlRequiresGraphURI;
    }
    
    /**
     * @return the providerSparqlTypeUri
     */
    public static URI getProviderSparqlTypeUri()
    {
        return SparqlProviderSchema.providerSparqlTypeUri;
    }
    
    /**
     * @param nextProviderHttpGetSparql
     *            the providerHttpGetSparql to set
     */
    public static void setProviderHttpGetSparql(final URI nextProviderHttpGetSparql)
    {
        SparqlProviderSchema.providerHttpGetSparql = nextProviderHttpGetSparql;
    }
    
    /**
     * @param nextProviderHttpPostSparql
     *            the providerHttpPostSparql to set
     */
    public static void setProviderHttpPostSparql(final URI nextProviderHttpPostSparql)
    {
        SparqlProviderSchema.providerHttpPostSparql = nextProviderHttpPostSparql;
    }
    
    /**
     * @param nextProviderSparqlGraphUri
     *            the providerSparqlGraphUri to set
     */
    public static void setProviderSparqlGraphUri(final URI nextProviderSparqlGraphUri)
    {
        SparqlProviderSchema.providerSparqlGraphUri = nextProviderSparqlGraphUri;
    }
    
    /**
     * @param nextProviderSparqlRequiresGraphURI
     *            the providerSparqlRequiresGraphURI to set
     */
    public static void setProviderSparqlRequiresGraphURI(final URI nextProviderSparqlRequiresGraphURI)
    {
        SparqlProviderSchema.providerSparqlRequiresGraphURI = nextProviderSparqlRequiresGraphURI;
    }
    
    /**
     * @param nextProviderSparqlTypeUri
     *            the providerSparqlTypeUri to set
     */
    public static void setProviderSparqlTypeUri(final URI nextProviderSparqlTypeUri)
    {
        SparqlProviderSchema.providerSparqlTypeUri = nextProviderSparqlTypeUri;
    }
    
    /**
     * Default constructor, uses the name of this class as the name.
     */
    public SparqlProviderSchema()
    {
        this(SparqlProviderSchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public SparqlProviderSchema(final String nextName)
    {
        super(nextName);
    }
    
    @Override
    public boolean schemaToRdf(final Repository myRepository, final int modelVersion, final URI... contextUri)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        try
        {
            con.begin();
            
            con.add(SparqlProviderSchema.getProviderSparqlTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            con.add(SparqlProviderSchema.getProviderSparqlTypeUri(), RDFS.LABEL,
                    f.createLiteral("The class of SPARQL data providers."), contextUri);
            
            con.add(SparqlProviderSchema.getProviderSparqlRequiresGraphURI(), RDF.TYPE, OWL.DATATYPEPROPERTY,
                    contextUri);
            con.add(SparqlProviderSchema.getProviderSparqlRequiresGraphURI(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(SparqlProviderSchema.getProviderSparqlRequiresGraphURI(), RDFS.DOMAIN,
                    SparqlProviderSchema.getProviderSparqlTypeUri(), contextUri);
            con.add(SparqlProviderSchema.getProviderSparqlRequiresGraphURI(),
                    RDFS.LABEL,
                    f.createLiteral("This SPARQL provider declares that it needs to have a specific sparql graph URI configured before it can operate."),
                    contextUri);
            
            con.add(SparqlProviderSchema.getProviderSparqlGraphUri(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextUri);
            con.add(SparqlProviderSchema.getProviderSparqlGraphUri(), RDFS.RANGE, RDFS.LITERAL, contextUri);
            con.add(SparqlProviderSchema.getProviderSparqlGraphUri(), RDFS.DOMAIN,
                    SparqlProviderSchema.getProviderSparqlTypeUri(), contextUri);
            con.add(SparqlProviderSchema.getProviderSparqlGraphUri(), RDFS.LABEL,
                    f.createLiteral("This SPARQL provider has this SPARQL graph URI as its target."), contextUri);
            
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
            
            SparqlProviderSchema.LOG.error("RepositoryException: " + re.getMessage());
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
