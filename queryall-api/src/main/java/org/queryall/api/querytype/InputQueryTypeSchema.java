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
public class InputQueryTypeSchema extends QueryAllSchema
{
    private static final Logger LOG = LoggerFactory.getLogger(InputQueryTypeSchema.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = InputQueryTypeSchema.LOG.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = InputQueryTypeSchema.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = InputQueryTypeSchema.LOG.isInfoEnabled();
    
    private static URI queryExpectedInputParameters;
    
    static
    {
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        final String baseUri = QueryAllNamespaces.QUERY.getBaseURI();
        
        InputQueryTypeSchema.setQueryExpectedInputParameters(f.createURI(baseUri, "expectedInputParameters"));
    }
    
    /**
     * The pre-instantiated schema object for InputQueryTypeSchema.
     */
    public static final QueryAllSchema INPUT_QUERY_TYPE_SCHEMA = new InputQueryTypeSchema();
    
    public static URI getQueryExpectedInputParameters()
    {
        return InputQueryTypeSchema.queryExpectedInputParameters;
    }
    
    public static void setQueryExpectedInputParameters(final URI expectedInputParameters)
    {
        InputQueryTypeSchema.queryExpectedInputParameters = expectedInputParameters;
    }
    
    /**
     * Default constructor, uses the name of this class as the name.
     */
    public InputQueryTypeSchema()
    {
        this(InputQueryTypeSchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public InputQueryTypeSchema(final String nextName)
    {
        super(nextName);
    }
    
    @Override
    public boolean schemaToRdf(final Repository myRepository, final int modelVersion, final URI... contexts)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(InputQueryTypeSchema.getQueryExpectedInputParameters(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
            con.add(InputQueryTypeSchema.getQueryExpectedInputParameters(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(InputQueryTypeSchema.getQueryExpectedInputParameters(), RDFS.DOMAIN,
                    QueryTypeSchema.getQueryTypeUri(), contexts);
            con.add(InputQueryTypeSchema.getQueryExpectedInputParameters(), RDFS.LABEL,
                    f.createLiteral("The list of input parameters to expect for this query."), contexts);
            con.add(InputQueryTypeSchema.getQueryExpectedInputParameters(),
                    RDFS.COMMENT,
                    f.createLiteral("The list of parameters must contain each and every valid parameter for this query. If a parameter is not in the query, and it is not a globally recognised parameter, it will be ignored completely."),
                    contexts);
            
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
            
            InputQueryTypeSchema.LOG.error("RepositoryException: " + re.getMessage());
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
