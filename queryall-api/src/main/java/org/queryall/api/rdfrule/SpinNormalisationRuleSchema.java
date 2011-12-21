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
public class SpinNormalisationRuleSchema extends QueryAllSchema
{
    private static final Logger log = LoggerFactory.getLogger(SpinNormalisationRuleSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = SpinNormalisationRuleSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = SpinNormalisationRuleSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = SpinNormalisationRuleSchema.log.isInfoEnabled();
    
    private static URI spinNormalisationRuleTypeUri;
    private static URI spinNormalisationRuleUrlImport;
    private static URI spinNormalisationRuleLocalImport;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.RDFRULE.getBaseURI();
        
        SpinNormalisationRuleSchema.setSpinNormalisationRuleTypeUri(f.createURI(baseUri, "SpinNormalisationRule"));
        
        SpinNormalisationRuleSchema.setSpinNormalisationRuleUrlImport(f.createURI(baseUri, "spinUrlImport"));
        
        SpinNormalisationRuleSchema.setSpinNormalisationRuleLocalImport(f.createURI(baseUri, "spinLocalImport"));
    }
    
    public static final QueryAllSchema SPIN_NORMALISATION_RULE_SCHEMA = new SpinNormalisationRuleSchema();
    
    /**
     * @return the spinNormalisationRuleLocalImport
     */
    public static URI getSpinNormalisationRuleLocalImport()
    {
        return SpinNormalisationRuleSchema.spinNormalisationRuleLocalImport;
    }
    
    /**
     * @return the spinruleTypeUri
     */
    public static URI getSpinNormalisationRuleTypeUri()
    {
        return SpinNormalisationRuleSchema.spinNormalisationRuleTypeUri;
    }
    
    /**
     * @return the spinNormalisationRuleUrlImport
     */
    public static URI getSpinNormalisationRuleUrlImport()
    {
        return SpinNormalisationRuleSchema.spinNormalisationRuleUrlImport;
    }
    
    /**
     * @param spinNormalisationRuleLocalImport
     *            the spinNormalisationRuleLocalImport to set
     */
    public static void setSpinNormalisationRuleLocalImport(final URI spinNormalisationRuleLocalImport)
    {
        SpinNormalisationRuleSchema.spinNormalisationRuleLocalImport = spinNormalisationRuleLocalImport;
    }
    
    /**
     * @param spinNormalisationRuleTypeUri
     *            the spinruleTypeUri to set
     */
    public static void setSpinNormalisationRuleTypeUri(final URI spinNormalisationRuleTypeUri)
    {
        SpinNormalisationRuleSchema.spinNormalisationRuleTypeUri = spinNormalisationRuleTypeUri;
    }
    
    /**
     * @param spinNormalisationRuleUrlImport
     *            the spinNormalisationRuleUrlImport to set
     */
    public static void setSpinNormalisationRuleUrlImport(final URI spinNormalisationRuleUrlImport)
    {
        SpinNormalisationRuleSchema.spinNormalisationRuleUrlImport = spinNormalisationRuleUrlImport;
    }
    
    /**
     * Default constructor, uses the name of this class as the name
     */
    public SpinNormalisationRuleSchema()
    {
        this(SpinNormalisationRuleSchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public SpinNormalisationRuleSchema(final String nextName)
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
            
            con.add(SpinNormalisationRuleSchema.getSpinNormalisationRuleTypeUri(), RDF.TYPE, OWL.CLASS, contextUri);
            con.add(SpinNormalisationRuleSchema.getSpinNormalisationRuleTypeUri(), RDFS.LABEL,
                    f.createLiteral("A SPIN based normalisation rule."), contextUri);
            con.add(SpinNormalisationRuleSchema.getSpinNormalisationRuleTypeUri(), RDFS.SUBCLASSOF,
                    NormalisationRuleSchema.getNormalisationRuleTypeUri(), contextUri);
            
            con.add(SpinNormalisationRuleSchema.getSpinNormalisationRuleUrlImport(), RDF.TYPE, OWL.OBJECTPROPERTY,
                    contextUri);
            con.add(SpinNormalisationRuleSchema.getSpinNormalisationRuleUrlImport(), RDFS.RANGE, RDFS.RESOURCE,
                    contextUri);
            con.add(SpinNormalisationRuleSchema.getSpinNormalisationRuleUrlImport(), RDFS.DOMAIN,
                    SpinNormalisationRuleSchema.getSpinNormalisationRuleTypeUri(), contextUri);
            con.add(SpinNormalisationRuleSchema.getSpinNormalisationRuleUrlImport(), RDFS.LABEL,
                    f.createLiteral("Defines URLs to import for SPIN Normalisation rules."), contextUri);
            
            con.add(SpinNormalisationRuleSchema.getSpinNormalisationRuleLocalImport(), RDF.TYPE, OWL.OBJECTPROPERTY,
                    contextUri);
            con.add(SpinNormalisationRuleSchema.getSpinNormalisationRuleLocalImport(), RDFS.RANGE, RDFS.RESOURCE,
                    contextUri);
            con.add(SpinNormalisationRuleSchema.getSpinNormalisationRuleLocalImport(), RDFS.DOMAIN,
                    SpinNormalisationRuleSchema.getSpinNormalisationRuleTypeUri(), contextUri);
            con.add(SpinNormalisationRuleSchema.getSpinNormalisationRuleLocalImport(), RDFS.LABEL,
                    f.createLiteral("Defines local Java classpath references to import for SPIN Normalisation rules."),
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
}
