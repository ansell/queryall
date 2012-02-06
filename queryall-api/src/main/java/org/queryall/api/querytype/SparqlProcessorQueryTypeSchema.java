/**
 * 
 */
package org.queryall.api.querytype;

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
public class SparqlProcessorQueryTypeSchema extends QueryAllSchema
{
    private static final Logger LOG = LoggerFactory.getLogger(SparqlProcessorQueryTypeSchema.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = SparqlProcessorQueryTypeSchema.LOG.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = SparqlProcessorQueryTypeSchema.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = SparqlProcessorQueryTypeSchema.LOG.isInfoEnabled();
    
    private static URI sparqlProcessorQueryTypeUri;
    
    static
    {
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        final String baseUri = QueryAllNamespaces.QUERY.getBaseURI();
        
        SparqlProcessorQueryTypeSchema.setSparqlProcessorQueryTypeUri(f.createURI(baseUri, "SparqlProcessorQuery"));
    }
    
    /**
     * A pre-instantiated schema object for SparqlProcessorQueryTypeSchema.
     */
    public static final QueryAllSchema SPARQL_PROCESSOR_QUERY_TYPE_SCHEMA = new SparqlProcessorQueryTypeSchema();
    
    /**
     * @return the queryTypeUri
     */
    public static URI getSparqlProcessorQueryTypeUri()
    {
        return SparqlProcessorQueryTypeSchema.sparqlProcessorQueryTypeUri;
    }
    
    /**
     * @param queryTypeUri
     *            the queryTypeUri to set
     */
    public static void setSparqlProcessorQueryTypeUri(final URI queryTypeUri)
    {
        SparqlProcessorQueryTypeSchema.sparqlProcessorQueryTypeUri = queryTypeUri;
    }
    
    /**
     * Default constructor, uses the name of this class as the name.
     */
    public SparqlProcessorQueryTypeSchema()
    {
        this(SparqlProcessorQueryTypeSchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public SparqlProcessorQueryTypeSchema(final String nextName)
    {
        super(nextName);
    }
    
    @Override
    public boolean schemaToRdf(final Repository myRepository, final int modelVersion, final URI... contexts)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        // final ValueFactory f = Constants.valueFactory;
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(SparqlProcessorQueryTypeSchema.getSparqlProcessorQueryTypeUri(), RDF.TYPE, OWL.CLASS, contexts);
            con.add(SparqlProcessorQueryTypeSchema.getSparqlProcessorQueryTypeUri(), RDFS.SUBCLASSOF,
                    QueryTypeSchema.getQueryTypeUri(), contexts);
            
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
            
            SparqlProcessorQueryTypeSchema.LOG.error("RepositoryException: " + re.getMessage());
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
