/**
 * 
 */
package org.queryall.api.querytype;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class InputQueryTypeSchema
{
    private static final Logger log = LoggerFactory.getLogger(InputQueryTypeSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = InputQueryTypeSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = InputQueryTypeSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = InputQueryTypeSchema.log.isInfoEnabled();
    
    private static URI queryExpectedInputParameters;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.QUERY.getBaseURI();
        
        InputQueryTypeSchema.setQueryExpectedInputParameters(f.createURI(baseUri, "expectedInputParameters"));
    }
    
    public static URI getQueryExpectedInputParameters()
    {
        return InputQueryTypeSchema.queryExpectedInputParameters;
    }
    
    public static boolean schemaToRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            final URI contextKeyUri = keyToUse;
            con.setAutoCommit(false);
            
            con.add(InputQueryTypeSchema.getQueryExpectedInputParameters(), RDF.TYPE, OWL.DATATYPEPROPERTY,
                    contextKeyUri);
            con.add(InputQueryTypeSchema.getQueryExpectedInputParameters(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(InputQueryTypeSchema.getQueryExpectedInputParameters(), RDFS.DOMAIN,
                    QueryTypeSchema.getQueryTypeUri(), contextKeyUri);
            con.add(InputQueryTypeSchema.getQueryExpectedInputParameters(), RDFS.LABEL,
                    f.createLiteral("The list of input parameters to expect for this query."), contextKeyUri);
            con.add(InputQueryTypeSchema.getQueryExpectedInputParameters(),
                    RDFS.COMMENT,
                    f.createLiteral("The list of parameters must contain each and every valid parameter for this query. If a parameter is not in the query, and it is not a globally recognised parameter, it will be ignored completely."),
                    contextKeyUri);
            
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
            
            InputQueryTypeSchema.log.error("RepositoryException: " + re.getMessage());
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
    
    public static void setQueryExpectedInputParameters(final URI expectedInputParameters)
    {
        InputQueryTypeSchema.queryExpectedInputParameters = expectedInputParameters;
    }
    
}
