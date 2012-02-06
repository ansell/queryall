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
public class RdfProviderSchema extends QueryAllSchema
{
    private static final Logger LOG = LoggerFactory.getLogger(RdfProviderSchema.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = RdfProviderSchema.LOG.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = RdfProviderSchema.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = RdfProviderSchema.LOG.isInfoEnabled();
    
    private static URI providerRdfProviderUri;
    
    static
    {
        final ValueFactory f = new MemValueFactory();
        
        final String baseUri = QueryAllNamespaces.PROVIDER.getBaseURI();
        
        RdfProviderSchema.setProviderRdfProviderUri(f.createURI(baseUri, "RdfProvider"));
    }
    
    /**
     * The pre-initialised schema object for RdfProviderSchema.
     */
    public static final QueryAllSchema RDF_PROVIDER_SCHEMA = new RdfProviderSchema();
    
    /**
     * @return the providerRdfProviderUri
     */
    public static URI getProviderRdfTypeUri()
    {
        return RdfProviderSchema.providerRdfProviderUri;
    }
    
    /**
     * @param nextProviderRdfProviderUri
     *            the providerRdfProviderUri to set
     */
    public static void setProviderRdfProviderUri(final URI nextProviderRdfProviderUri)
    {
        RdfProviderSchema.providerRdfProviderUri = nextProviderRdfProviderUri;
    }
    
    /**
     * Default constructor, uses the name of this class as the name.
     */
    public RdfProviderSchema()
    {
        this(RdfProviderSchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public RdfProviderSchema(final String nextName)
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
            con.setAutoCommit(false);
            
            con.add(RdfProviderSchema.getProviderRdfTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            con.add(RdfProviderSchema.getProviderRdfTypeUri(), RDFS.LABEL,
                    f.createLiteral("The class of RDF based Data Providers."), contextUri);
            con.add(RdfProviderSchema.getProviderRdfTypeUri(), RDFS.SUBCLASSOF, ProviderSchema.getProviderTypeUri(),
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
            
            RdfProviderSchema.LOG.error("RepositoryException: " + re.getMessage());
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