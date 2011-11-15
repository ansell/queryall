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
    private static final Logger log = LoggerFactory.getLogger(RdfProviderSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = RdfProviderSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = RdfProviderSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = RdfProviderSchema.log.isInfoEnabled();
    
    private static URI providerRdfProviderUri;
    
    static
    {
        final ValueFactory f = new MemValueFactory();
        
        final String baseUri = QueryAllNamespaces.PROVIDER.getBaseURI();
        
        RdfProviderSchema.setProviderRdfProviderUri(f.createURI(baseUri, "RdfProvider"));
    }
    
    /**
     * @return the providerRdfProviderUri
     */
    public static URI getProviderRdfTypeUri()
    {
        return RdfProviderSchema.providerRdfProviderUri;
    }
    
    /**
     * @param providerRdfProviderUri
     *            the providerRdfProviderUri to set
     */
    public static void setProviderRdfProviderUri(final URI providerRdfProviderUri)
    {
        RdfProviderSchema.providerRdfProviderUri = providerRdfProviderUri;
    }
    
    @Override
    public String getName()
    {
        return RdfProviderSchema.class.getName();
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
            
            RdfProviderSchema.log.error("RepositoryException: " + re.getMessage());
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