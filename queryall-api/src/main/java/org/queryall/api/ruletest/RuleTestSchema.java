/**
 * 
 */
package org.queryall.api.ruletest;

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
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices(QueryAllSchema.class)
public class RuleTestSchema extends QueryAllSchema
{
    private static final Logger LOG = LoggerFactory.getLogger(RuleTestSchema.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = RuleTestSchema.LOG.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = RuleTestSchema.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = RuleTestSchema.LOG.isInfoEnabled();
    
    private static URI ruletestTypeUri;
    
    private static URI ruletestHasRuleUri;
    
    private static URI ruletestTestsStage;
    
    static
    {
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        final String baseUri = QueryAllNamespaces.RULETEST.getBaseURI();
        
        RuleTestSchema.setRuletestTypeUri(f.createURI(baseUri, "RuleTest"));
        RuleTestSchema.setRuletestHasRuleUri(f.createURI(baseUri, "testsRules"));
        RuleTestSchema.setRuletestTestsStage(f.createURI(baseUri, "testsStages"));
    }
    
    /**
     * The pre-instantiated schema object for RuleTestSchema.
     */
    public static final QueryAllSchema RULE_TEST_SCHEMA = new RuleTestSchema();
    
    /**
     * @return the ruletestHasRuleUri
     */
    public static URI getRuletestHasRuleUri()
    {
        return RuleTestSchema.ruletestHasRuleUri;
    }
    
    /**
     * @return the ruletestTestsStage
     */
    public static URI getRuletestTestsStage()
    {
        return RuleTestSchema.ruletestTestsStage;
    }
    
    /**
     * @return the ruletestTypeUri
     */
    public static URI getRuletestTypeUri()
    {
        return RuleTestSchema.ruletestTypeUri;
    }
    
    /**
     * @param nextRuletestHasRuleUri
     *            the ruletestHasRuleUri to set
     */
    public static void setRuletestHasRuleUri(final URI nextRuletestHasRuleUri)
    {
        RuleTestSchema.ruletestHasRuleUri = nextRuletestHasRuleUri;
    }
    
    /**
     * @param nextRuletestTestsStage
     *            the ruletestTestsStage to set
     */
    public static void setRuletestTestsStage(final URI nextRuletestTestsStage)
    {
        RuleTestSchema.ruletestTestsStage = nextRuletestTestsStage;
    }
    
    /**
     * @param nextRuletestTypeUri
     *            the ruletestTypeUri to set
     */
    public static void setRuletestTypeUri(final URI nextRuletestTypeUri)
    {
        RuleTestSchema.ruletestTypeUri = nextRuletestTypeUri;
    }
    
    /**
     * Default constructor, uses the name of this class as the name.
     */
    public RuleTestSchema()
    {
        this(RuleTestSchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public RuleTestSchema(final String nextName)
    {
        super(nextName);
    }
    
    @Override
    public boolean schemaToRdf(final Repository myRepository, final int modelVersion, final URI... contexts)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        try
        {
            con.begin();
            
            con.add(RuleTestSchema.getRuletestTypeUri(), RDF.TYPE, OWL.CLASS, contexts);
            con.add(RuleTestSchema.getRuletestTypeUri(), RDFS.LABEL,
                    f.createLiteral("A test case for normalisation rules."), contexts);
            
            con.add(RuleTestSchema.getRuletestHasRuleUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contexts);
            con.add(RuleTestSchema.getRuletestHasRuleUri(), RDFS.RANGE,
                    NormalisationRuleSchema.getNormalisationRuleTypeUri(), contexts);
            con.add(RuleTestSchema.getRuletestHasRuleUri(), RDFS.DOMAIN, RuleTestSchema.getRuletestTypeUri(), contexts);
            con.add(RuleTestSchema.getRuletestHasRuleUri(), RDFS.LABEL, f.createLiteral("."), contexts);
            
            con.add(RuleTestSchema.getRuletestTestsStage(), RDF.TYPE, OWL.OBJECTPROPERTY, contexts);
            con.add(RuleTestSchema.getRuletestTestsStage(), RDFS.RANGE, RDFS.RESOURCE, contexts);
            con.add(RuleTestSchema.getRuletestTestsStage(), RDFS.DOMAIN, RuleTestSchema.getRuletestTypeUri(), contexts);
            con.add(RuleTestSchema.getRuletestTestsStage(), RDFS.LABEL, f.createLiteral("."), contexts);
            
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
            
            RuleTestSchema.LOG.error("RepositoryException: " + re.getMessage());
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
