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
public class OwlNormalisationRuleSchema extends QueryAllSchema
{
    private static final Logger log = LoggerFactory.getLogger(OwlNormalisationRuleSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = OwlNormalisationRuleSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = OwlNormalisationRuleSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = OwlNormalisationRuleSchema.log.isInfoEnabled();
    
    private static URI owlruleTypeUri;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.RDFRULE.getBaseURI();
        
        OwlNormalisationRuleSchema.setOwlRuleTypeUri(f.createURI(baseUri, "OwlValidatingRule"));
    }
    
    /**
     * @return the owlruleTypeUri
     */
    public static URI getOwlRuleTypeUri()
    {
        return OwlNormalisationRuleSchema.owlruleTypeUri;
    }
    
    /**
     * @param owlruleTypeUri
     *            the owlruleTypeUri to set
     */
    public static void setOwlRuleTypeUri(final URI owlruleTypeUri)
    {
        OwlNormalisationRuleSchema.owlruleTypeUri = owlruleTypeUri;
    }
    
    public static final QueryAllSchema OWL_NORMALISATION_RULE_SCHEMA = new OwlNormalisationRuleSchema();
    
    /**
     * Default constructor, uses the name of this class as the name
     */
    public OwlNormalisationRuleSchema()
    {
        this(OwlNormalisationRuleSchema.class.getName());
    }
    
    /**
     * @param nextName The name for this schema object
     */
    public OwlNormalisationRuleSchema(final String nextName)
    {
        super(nextName);
    }
    
    @Override
    public boolean schemaToRdf(final Repository myRepository, final URI contextUri, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(OwlNormalisationRuleSchema.getOwlRuleTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            con.add(OwlNormalisationRuleSchema.getOwlRuleTypeUri(), RDFS.SUBCLASSOF,
                    ValidatingRuleSchema.getValidatingRuleTypeUri(), contextUri);
            con.add(OwlNormalisationRuleSchema.getOwlRuleTypeUri(),
                    RDFS.LABEL,
                    f.createLiteral("An OWL normalisation rule intended to validate triples based on an OWL ontology."),
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
            
            OwlNormalisationRuleSchema.log.error("RepositoryException: " + re.getMessage());
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
