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
    private static final Logger log = LoggerFactory.getLogger(SparqlProcessorQueryTypeSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = SparqlProcessorQueryTypeSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = SparqlProcessorQueryTypeSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = SparqlProcessorQueryTypeSchema.log.isInfoEnabled();
    
    private static URI sparqlProcessorQueryTypeUri;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.QUERY.getBaseURI();
        
        SparqlProcessorQueryTypeSchema.setSparqlProcessorQueryTypeUri(f.createURI(baseUri, "SparqlProcessorQuery"));
    }
    
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
     * Default constructor, uses the name of this class as the name
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
    public boolean schemaToRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        // final ValueFactory f = Constants.valueFactory;
        
        try
        {
            final URI contextKeyUri = keyToUse;
            con.setAutoCommit(false);
            
            con.add(SparqlProcessorQueryTypeSchema.getSparqlProcessorQueryTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);
            
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
            
            SparqlProcessorQueryTypeSchema.log.error("RepositoryException: " + re.getMessage());
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
