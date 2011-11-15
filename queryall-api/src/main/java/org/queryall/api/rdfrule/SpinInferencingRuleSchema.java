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
    private static final Logger log = LoggerFactory.getLogger(SpinInferencingRuleSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = SpinInferencingRuleSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = SpinInferencingRuleSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = SpinInferencingRuleSchema.log.isInfoEnabled();
    
    private static URI spinInferencingRuleTypeUri;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.RDFRULE.getBaseURI();
        
        SpinInferencingRuleSchema.setSpinInferencingRuleTypeUri(f.createURI(baseUri, "SpinInferencingRule"));
    }
    
    /**
     * @return the spinruleTypeUri
     */
    public static URI getSpinInferencingRuleTypeUri()
    {
        return SpinInferencingRuleSchema.spinInferencingRuleTypeUri;
    }
    
    /**
     * @param spinInferencingRuleTypeUri
     *            the spinruleTypeUri to set
     */
    public static void setSpinInferencingRuleTypeUri(final URI spinInferencingRuleTypeUri)
    {
        SpinInferencingRuleSchema.spinInferencingRuleTypeUri = spinInferencingRuleTypeUri;
    }
    
    @Override
    public String getName()
    {
        return SpinInferencingRuleSchema.class.getName();
    }
    
    @Override
    public boolean schemaToRdf(final Repository myRepository, final URI contextUri, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(SpinInferencingRuleSchema.getSpinInferencingRuleTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            con.add(SpinInferencingRuleSchema.getSpinInferencingRuleTypeUri(),
                    RDFS.LABEL,
                    f.createLiteral("A SPIN based normalisation rule intended to infer extra triples based on SPIN rules."),
                    contextUri);
            con.add(SpinInferencingRuleSchema.getSpinInferencingRuleTypeUri(), RDFS.SUBCLASSOF,
                    TransformingRuleSchema.getTransformingRuleTypeUri(), contextUri);
            
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
            
            SpinInferencingRuleSchema.log.error("RepositoryException: " + re.getMessage());
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
