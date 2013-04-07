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
public class SpinInferencingRuleSchema extends QueryAllSchema
{
    private static final Logger LOG = LoggerFactory.getLogger(SpinInferencingRuleSchema.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = SpinInferencingRuleSchema.LOG.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = SpinInferencingRuleSchema.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = SpinInferencingRuleSchema.LOG.isInfoEnabled();
    
    private static URI spinInferencingRuleTypeUri;
    
    static
    {
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        final String baseUri = QueryAllNamespaces.RDFRULE.getBaseURI();
        
        SpinInferencingRuleSchema.setSpinInferencingRuleTypeUri(f.createURI(baseUri, "SpinInferencingRule"));
    }
    
    /**
     * A pre-instantiated schema object for SpinInferencingRuleSchema.
     */
    public static final QueryAllSchema SPIN_INFERENCING_RULE_SCHEMA = new SpinInferencingRuleSchema();
    
    /**
     * @return the spinruleTypeUri
     */
    public static URI getSpinInferencingRuleTypeUri()
    {
        return SpinInferencingRuleSchema.spinInferencingRuleTypeUri;
    }
    
    /**
     * @param nextSpinInferencingRuleTypeUri
     *            the spinruleTypeUri to set
     */
    public static void setSpinInferencingRuleTypeUri(final URI nextSpinInferencingRuleTypeUri)
    {
        SpinInferencingRuleSchema.spinInferencingRuleTypeUri = nextSpinInferencingRuleTypeUri;
    }
    
    /**
     * Default constructor, uses the name of this class as the name.
     */
    public SpinInferencingRuleSchema()
    {
        this(SpinInferencingRuleSchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public SpinInferencingRuleSchema(final String nextName)
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
            con.begin();
            
            con.add(SpinInferencingRuleSchema.getSpinInferencingRuleTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            con.add(SpinInferencingRuleSchema.getSpinInferencingRuleTypeUri(),
                    RDFS.LABEL,
                    f.createLiteral("A SPIN based normalisation rule intended to infer extra triples based on SPIN rules."),
                    contextUri);
            con.add(SpinInferencingRuleSchema.getSpinInferencingRuleTypeUri(), RDFS.SUBCLASSOF,
                    TransformingRuleSchema.getTransformingRuleTypeUri(), contextUri);
            con.add(SpinInferencingRuleSchema.getSpinInferencingRuleTypeUri(), RDFS.SUBCLASSOF,
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
            
            SpinInferencingRuleSchema.LOG.error("RepositoryException: " + re.getMessage());
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
