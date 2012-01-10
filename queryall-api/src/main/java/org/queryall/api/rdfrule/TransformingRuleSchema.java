/**
 * 
 */
package org.queryall.api.rdfrule;

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
public class TransformingRuleSchema extends QueryAllSchema
{
    private static final Logger LOG = LoggerFactory.getLogger(TransformingRuleSchema.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = TransformingRuleSchema.LOG.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = TransformingRuleSchema.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = TransformingRuleSchema.LOG.isInfoEnabled();
    
    private static URI transformingRuleTypeUri;
    
    static
    {
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        final String baseUri = QueryAllNamespaces.RDFRULE.getBaseURI();
        
        TransformingRuleSchema.setTransformingRuleTypeUri(f.createURI(baseUri, "TransformationRule"));
        
    }
    
    /**
     * A pre-instantiated schema object for TransformingRuleSchema.
     */
    public static final QueryAllSchema TRANSFORMING_RULE_SCHEMA = new TransformingRuleSchema();
    
    /**
     * @return the normalisationRuleTypeUri
     */
    public static URI getTransformingRuleTypeUri()
    {
        return TransformingRuleSchema.transformingRuleTypeUri;
    }
    
    /**
     * @param nextTransformingRuleTypeUri
     *            the normalisationRuleTypeUri to set
     */
    public static void setTransformingRuleTypeUri(final URI nextTransformingRuleTypeUri)
    {
        TransformingRuleSchema.transformingRuleTypeUri = nextTransformingRuleTypeUri;
    }
    
    /**
     * Default constructor, uses the name of this class as the name.
     */
    public TransformingRuleSchema()
    {
        this(TransformingRuleSchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public TransformingRuleSchema(final String nextName)
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
            con.setAutoCommit(false);
            
            con.add(TransformingRuleSchema.getTransformingRuleTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            con.add(TransformingRuleSchema.getTransformingRuleTypeUri(), RDFS.SUBCLASSOF,
                    NormalisationRuleSchema.getNormalisationRuleTypeUri(), contextUri);
            
            con.add(TransformingRuleSchema.getTransformingRuleTypeUri(),
                    RDFS.LABEL,
                    f.createLiteral("A normalisation rule setup to transform input, as opposed to a validation rule which decides on the validity of the input in the context of this rule."),
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
            
            TransformingRuleSchema.LOG.error("RepositoryException: " + re.getMessage());
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
