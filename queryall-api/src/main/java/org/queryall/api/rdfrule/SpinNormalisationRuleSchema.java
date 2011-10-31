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
public class SpinNormalisationRuleSchema
{
    private static final Logger log = LoggerFactory.getLogger(SpinNormalisationRuleSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = SpinNormalisationRuleSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = SpinNormalisationRuleSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = SpinNormalisationRuleSchema.log.isInfoEnabled();
    
    private static URI spinruleTypeUri;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.RDFRULE.getBaseURI();
        
        SpinNormalisationRuleSchema.setSpinRuleTypeUri(f.createURI(baseUri, "SpinNormalisationRule"));
    }
    
    /**
     * @return the spinruleTypeUri
     */
    public static URI getSpinRuleTypeUri()
    {
        return SpinNormalisationRuleSchema.spinruleTypeUri;
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
            
            con.add(SpinNormalisationRuleSchema.getSpinRuleTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            con.add(SpinNormalisationRuleSchema.getSpinRuleTypeUri(), RDFS.LABEL,
                    f.createLiteral("A SPARQL based normalisation rule intended to normalise in-memory RDF triples and SPARQL queries using the SPIN notation."),
                    contextUri);
            con.add(SpinNormalisationRuleSchema.getSpinRuleTypeUri(), RDFS.SUBCLASSOF,
                    NormalisationRuleSchema.getNormalisationRuleTypeUri(), contextUri);
            
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
            
            SpinNormalisationRuleSchema.log.error("RepositoryException: " + re.getMessage());
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
     * @param spinruleTypeUri
     *            the spinruleTypeUri to set
     */
    public static void setSpinRuleTypeUri(final URI spinruleTypeUri)
    {
        SpinNormalisationRuleSchema.spinruleTypeUri = spinruleTypeUri;
    }
    
}
