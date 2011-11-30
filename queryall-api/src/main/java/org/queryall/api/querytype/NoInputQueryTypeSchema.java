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
public class NoInputQueryTypeSchema extends QueryAllSchema
{
    private static final Logger log = LoggerFactory.getLogger(NoInputQueryTypeSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = NoInputQueryTypeSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = NoInputQueryTypeSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = NoInputQueryTypeSchema.log.isInfoEnabled();
    
    private static URI noInputQueryTypeUri;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.QUERY.getBaseURI();
        
        NoInputQueryTypeSchema.setNoInputInputQueryTypeUri(f.createURI(baseUri, "NoInputInputQuery"));
    }
    
    public static final QueryAllSchema RDF_INPUT_QUERY_TYPE_SCHEMA = new NoInputQueryTypeSchema();
    
    /**
     * @return the queryTypeUri
     */
    public static URI getNoInputQueryTypeUri()
    {
        return NoInputQueryTypeSchema.noInputQueryTypeUri;
    }
    
    /**
     * @param queryTypeUri
     *            the queryTypeUri to set
     */
    public static void setNoInputInputQueryTypeUri(final URI queryTypeUri)
    {
        NoInputQueryTypeSchema.noInputQueryTypeUri = queryTypeUri;
    }
    
    /**
     * Default constructor, uses the name of this class as the name
     */
    public NoInputQueryTypeSchema()
    {
        this(NoInputQueryTypeSchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public NoInputQueryTypeSchema(final String nextName)
    {
        super(nextName);
    }
    
    @Override
    public boolean schemaToRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            final URI contextKeyUri = keyToUse;
            con.setAutoCommit(false);
            
            con.add(NoInputQueryTypeSchema.getNoInputQueryTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);
            con.add(NoInputQueryTypeSchema.getNoInputQueryTypeUri(), RDFS.SUBCLASSOF, QueryTypeSchema.getQueryTypeUri(), contextKeyUri);
            
            
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
            
            NoInputQueryTypeSchema.log.error("RepositoryException: " + re.getMessage());
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
