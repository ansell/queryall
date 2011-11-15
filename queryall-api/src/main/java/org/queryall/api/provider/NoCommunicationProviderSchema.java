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
public class NoCommunicationProviderSchema extends QueryAllSchema
{
    static final Logger log = LoggerFactory.getLogger(NoCommunicationProviderSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = NoCommunicationProviderSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = NoCommunicationProviderSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = NoCommunicationProviderSchema.log.isInfoEnabled();
    
    private static URI providerNoCommunicationProviderUri;
    
    static
    {
        final ValueFactory f = new MemValueFactory();
        
        final String baseUri = QueryAllNamespaces.PROVIDER.getBaseURI();
        
        NoCommunicationProviderSchema.setProviderNoCommunicationProviderUri(f.createURI(baseUri,
                "NoCommunicationProvider"));
    }
    
    /**
     * @return the providerNoCommunicationProviderUri
     */
    public static URI getProviderNoCommunicationTypeUri()
    {
        return NoCommunicationProviderSchema.providerNoCommunicationProviderUri;
    }
    
    /**
     * @param providerNoCommunicationProviderUri
     *            the providerNoCommunicationProviderUri to set
     */
    public static void setProviderNoCommunicationProviderUri(final URI providerHttpProviderUri)
    {
        NoCommunicationProviderSchema.providerNoCommunicationProviderUri = providerHttpProviderUri;
    }
    
    public static final QueryAllSchema NO_COMMUNICATION_PROVIDER_SCHEMA = new NoCommunicationProviderSchema();
    
    /**
     * Default constructor, uses the name of this class as the name
     */
    public NoCommunicationProviderSchema()
    {
        this(NoCommunicationProviderSchema.class.getName());
    }
    
    /**
     * @param nextName The name for this schema object
     */
    public NoCommunicationProviderSchema(final String nextName)
    {
        super(nextName);
    }
    
    
    @Override
    public boolean schemaToRdf(final Repository myRepository, final URI contextUri, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = new MemValueFactory();
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(NoCommunicationProviderSchema.getProviderNoCommunicationTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            con.add(NoCommunicationProviderSchema.getProviderNoCommunicationTypeUri(), RDFS.LABEL,
                    f.createLiteral("The class of Data Providers who do not require network communication."),
                    contextUri);
            con.add(NoCommunicationProviderSchema.getProviderNoCommunicationTypeUri(), RDFS.SUBCLASSOF,
                    ProviderSchema.getProviderTypeUri(), contextUri);
            
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
            
            NoCommunicationProviderSchema.log.error("RepositoryException: " + re.getMessage());
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