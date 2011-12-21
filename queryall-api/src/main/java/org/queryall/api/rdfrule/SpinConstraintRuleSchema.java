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
public class SpinConstraintRuleSchema extends QueryAllSchema
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
    
    public static final QueryAllSchema SPIN_CONSTRAINT_RULE_SCHEMA = new SpinConstraintRuleSchema();
    
    /**
     * @return the spinruleTypeUri
     */
    public static URI getSpinConstraintRuleTypeUri()
    {
        return SpinConstraintRuleSchema.spinConstraintRuleTypeUri;
    }
    
    /**
     * @param spinConstraintRuleTypeUri
     *            the spinruleTypeUri to set
     */
    public static void setSpinConstraintRuleTypeUri(final URI spinConstraintRuleTypeUri)
    {
        SpinConstraintRuleSchema.spinConstraintRuleTypeUri = spinConstraintRuleTypeUri;
    }
    
    /**
     * Default constructor, uses the name of this class as the name
     */
    public SpinConstraintRuleSchema()
    {
        this(SpinConstraintRuleSchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public SpinConstraintRuleSchema(final String nextName)
    {
        super(nextName);
    }
    
    @Override
    public boolean schemaToRdf(final Repository myRepository, final int modelVersion, final URI... contextUri)
        throws OpenRDFException
    {
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
            con.add(SpinConstraintRuleSchema.getSpinConstraintRuleTypeUri(), RDFS.SUBCLASSOF,
                    SpinNormalisationRuleSchema.getSpinNormalisationRuleTypeUri(), contextUri);
            
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
    
}
