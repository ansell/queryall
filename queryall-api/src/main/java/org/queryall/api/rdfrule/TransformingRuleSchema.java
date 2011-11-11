/**
 * 
 */
package org.queryall.api.rdfrule;

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
public class TransformingRuleSchema
{
    private static final Logger log = LoggerFactory.getLogger(TransformingRuleSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = TransformingRuleSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = TransformingRuleSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = TransformingRuleSchema.log.isInfoEnabled();

    private static URI transformingRuleTypeUri;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.RDFRULE.getBaseURI();
        
        TransformingRuleSchema.setTransformingRuleTypeUri(f.createURI(baseUri, "TransformingNormalisationRule"));
        
    }
    /**
     * @return the normalisationRuleTypeUri
     */
    public static URI getTransformingRuleTypeUri()
    {
        return TransformingRuleSchema.transformingRuleTypeUri;
    }
    
    public static boolean schemaToRdf(final Repository myRepository, final URI contextUri, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
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
            
            TransformingRuleSchema.log.error("RepositoryException: " + re.getMessage());
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
    
    /**
     * @param transformingRuleTypeUri
     *            the normalisationRuleTypeUri to set
     */
    public static void setTransformingRuleTypeUri(final URI transformingRuleTypeUri)
    {
        TransformingRuleSchema.transformingRuleTypeUri = transformingRuleTypeUri;
    }
    

}
