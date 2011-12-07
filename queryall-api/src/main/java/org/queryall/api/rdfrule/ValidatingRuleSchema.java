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
public class ValidatingRuleSchema extends QueryAllSchema
{
    private static final Logger log = LoggerFactory.getLogger(ValidatingRuleSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = ValidatingRuleSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = ValidatingRuleSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = ValidatingRuleSchema.log.isInfoEnabled();
    
    private static URI validatingRuleTypeUri;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.RDFRULE.getBaseURI();
        
        ValidatingRuleSchema.setValidatingRuleTypeUri(f.createURI(baseUri, "ValidationRule"));
        
    }
    
    public static final QueryAllSchema VALIDATING_RULE_SCHEMA = new ValidatingRuleSchema();
    
    /**
     * @return the normalisationRuleTypeUri
     */
    public static URI getValidatingRuleTypeUri()
    {
        return ValidatingRuleSchema.validatingRuleTypeUri;
    }
    
    /**
     * @param validatingRuleTypeUri
     *            the normalisationRuleTypeUri to set
     */
    public static void setValidatingRuleTypeUri(final URI validatingRuleTypeUri)
    {
        ValidatingRuleSchema.validatingRuleTypeUri = validatingRuleTypeUri;
    }
    
    /**
     * Default constructor, uses the name of this class as the name
     */
    public ValidatingRuleSchema()
    {
        this(ValidatingRuleSchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public ValidatingRuleSchema(final String nextName)
    {
        super(nextName);
    }
    
    @Override
    public boolean schemaToRdf(final Repository myRepository, final int modelVersion, final URI... contextUri)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(ValidatingRuleSchema.getValidatingRuleTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            con.add(ValidatingRuleSchema.getValidatingRuleTypeUri(), RDFS.SUBCLASSOF,
                    NormalisationRuleSchema.getNormalisationRuleTypeUri(), contextUri);
            
            con.add(ValidatingRuleSchema.getValidatingRuleTypeUri(),
                    RDFS.LABEL,
                    f.createLiteral("A normalisation rule setup to decides on the validity of the input in the context of this rule, as opposed to transforming rules that transform the input into an output."),
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
            
            ValidatingRuleSchema.log.error("RepositoryException: " + re.getMessage());
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
