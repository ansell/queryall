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
public class SpinConstraintRuleSchema
{
    private static final Logger log = LoggerFactory.getLogger(SpinConstraintRuleSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = SpinConstraintRuleSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = SpinConstraintRuleSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = SpinConstraintRuleSchema.log.isInfoEnabled();
    
    private static URI spinConstraintRuleTypeUri;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.RDFRULE.getBaseURI();
        
        SpinConstraintRuleSchema.setSpinConstraintRuleTypeUri(f.createURI(baseUri, "SpinConstraintRule"));
    }
    
    /**
     * @return the spinruleTypeUri
     */
    public static URI getSpinConstraintRuleTypeUri()
    {
        return SpinConstraintRuleSchema.spinConstraintRuleTypeUri;
    }
    
    public static boolean schemaToRdf(final Repository myRepository, final URI contextUri, final int modelVersion)
        throws OpenRDFException
    {
        NormalisationRuleSchema.schemaToRdf(myRepository, contextUri, modelVersion);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(SpinConstraintRuleSchema.getSpinConstraintRuleTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            con.add(SpinConstraintRuleSchema.getSpinConstraintRuleTypeUri(),
                    RDFS.LABEL,
                    f.createLiteral("A SPIN based normalisation rule intended to validate triples based on SPIN constraints."),
                    contextUri);
            con.add(SpinConstraintRuleSchema.getSpinConstraintRuleTypeUri(), RDFS.SUBCLASSOF,
                    ValidatingRuleSchema.getValidatingRuleTypeUri(), contextUri);
            
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
            
            SpinConstraintRuleSchema.log.error("RepositoryException: " + re.getMessage());
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
     * @param spinConstraintRuleTypeUri
     *            the spinruleTypeUri to set
     */
    public static void setSpinConstraintRuleTypeUri(final URI spinConstraintRuleTypeUri)
    {
        SpinConstraintRuleSchema.spinConstraintRuleTypeUri = spinConstraintRuleTypeUri;
    }
    
}
