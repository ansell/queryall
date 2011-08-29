/**
 * 
 */
package org.queryall.api.ruletest;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RuleTestSchema
{
    private static final Logger log = LoggerFactory.getLogger(RuleTestSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = RuleTestSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = RuleTestSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = RuleTestSchema.log.isInfoEnabled();
    
    /**
     * @return the ruletestHasRuleUri
     */
    public static URI getRuletestHasRuleUri()
    {
        return RuleTestSchema.ruletestHasRuleUri;
    }
    
    /**
     * @return the ruletestInputTestString
     */
    public static URI getRuletestInputTestString()
    {
        return RuleTestSchema.ruletestInputTestString;
    }
    
    /**
     * @return the ruletestOutputTestString
     */
    public static URI getRuletestOutputTestString()
    {
        return RuleTestSchema.ruletestOutputTestString;
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
     * @param ruletestHasRuleUri
     *            the ruletestHasRuleUri to set
     */
    public static void setRuletestHasRuleUri(final URI ruletestHasRuleUri)
    {
        RuleTestSchema.ruletestHasRuleUri = ruletestHasRuleUri;
    }
    
    /**
     * @param ruletestInputTestString
     *            the ruletestInputTestString to set
     */
    public static void setRuletestInputTestString(final URI ruletestInputTestString)
    {
        RuleTestSchema.ruletestInputTestString = ruletestInputTestString;
    }
    
    /**
     * @param ruletestOutputTestString
     *            the ruletestOutputTestString to set
     */
    public static void setRuletestOutputTestString(final URI ruletestOutputTestString)
    {
        RuleTestSchema.ruletestOutputTestString = ruletestOutputTestString;
    }
    
    /**
     * @param ruletestTestsStage
     *            the ruletestTestsStage to set
     */
    public static void setRuletestTestsStage(final URI ruletestTestsStage)
    {
        RuleTestSchema.ruletestTestsStage = ruletestTestsStage;
    }
    
    /**
     * @param ruletestTypeUri
     *            the ruletestTypeUri to set
     */
    public static void setRuletestTypeUri(final URI ruletestTypeUri)
    {
        RuleTestSchema.ruletestTypeUri = ruletestTypeUri;
    }
        
    private static URI ruletestTypeUri;
    
    private static URI ruletestHasRuleUri;
    
    private static URI ruletestTestsStage;
    
    private static URI ruletestInputTestString;
    
    private static URI ruletestOutputTestString;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.RULETEST.getBaseURI();
        
        RuleTestSchema.setRuletestTypeUri(f.createURI(baseUri, "RuleTest"));
        RuleTestSchema.setRuletestHasRuleUri(f.createURI(baseUri, "testsRules"));
        RuleTestSchema.setRuletestTestsStage(f.createURI(baseUri, "testsStages"));
        
        RuleTestSchema.setRuletestInputTestString(f.createURI(baseUri, "inputTestString"));
        RuleTestSchema.setRuletestOutputTestString(f.createURI(baseUri, "outputTestString"));
    }
    
    public static boolean schemaToRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            final URI contextKeyUri = keyToUse;
            con.setAutoCommit(false);
            
            con.add(RuleTestSchema.getRuletestTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);
            con.add(RuleTestSchema.getRuletestTypeUri(), RDFS.LABEL,
                    f.createLiteral("A test case for normalisation rules."), contextKeyUri);
            
            con.add(RuleTestSchema.getRuletestHasRuleUri(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(RuleTestSchema.getRuletestHasRuleUri(), RDFS.RANGE,
                    NormalisationRuleSchema.getNormalisationRuleTypeUri(), contextKeyUri);
            con.add(RuleTestSchema.getRuletestHasRuleUri(), RDFS.DOMAIN, RuleTestSchema.getRuletestTypeUri(), contextKeyUri);
            con.add(RuleTestSchema.getRuletestHasRuleUri(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(RuleTestSchema.getRuletestTestsStage(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(RuleTestSchema.getRuletestTestsStage(), RDFS.RANGE, RDFS.RESOURCE, contextKeyUri);
            con.add(RuleTestSchema.getRuletestTestsStage(), RDFS.DOMAIN, RuleTestSchema.getRuletestTypeUri(), contextKeyUri);
            con.add(RuleTestSchema.getRuletestTestsStage(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(RuleTestSchema.getRuletestInputTestString(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(RuleTestSchema.getRuletestInputTestString(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(RuleTestSchema.getRuletestInputTestString(), RDFS.DOMAIN, RuleTestSchema.getRuletestTypeUri(),
                    contextKeyUri);
            con.add(RuleTestSchema.getRuletestInputTestString(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(RuleTestSchema.getRuletestOutputTestString(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(RuleTestSchema.getRuletestOutputTestString(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(RuleTestSchema.getRuletestOutputTestString(), RDFS.DOMAIN, RuleTestSchema.getRuletestTypeUri(),
                    contextKeyUri);
            con.add(RuleTestSchema.getRuletestOutputTestString(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
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
            
            RuleTestSchema.log.error("RepositoryException: " + re.getMessage());
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
