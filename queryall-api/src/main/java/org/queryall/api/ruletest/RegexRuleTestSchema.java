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
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RegexRuleTestSchema
{
    private static final Logger log = LoggerFactory.getLogger(RegexRuleTestSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = RegexRuleTestSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = RegexRuleTestSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = RegexRuleTestSchema.log.isInfoEnabled();
    
    private static URI ruletestInputTestString;
    
    private static URI ruletestOutputTestString;
    
    private static URI regexRuletestTypeUri;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.RULETEST.getBaseURI();
        
        RegexRuleTestSchema.setRegexRuletestTypeUri(f.createURI(baseUri, "RegexRuleTest"));
        RegexRuleTestSchema.setRuletestInputTestString(f.createURI(baseUri, "inputTestString"));
        RegexRuleTestSchema.setRuletestOutputTestString(f.createURI(baseUri, "outputTestString"));
    }
    
    /**
     * @return the ruletestTypeUri
     */
    public static URI getRegexRuletestTypeUri()
    {
        return RegexRuleTestSchema.regexRuletestTypeUri;
    }
    
    /**
     * @return the ruletestInputTestString
     */
    public static URI getRuletestInputTestString()
    {
        return RegexRuleTestSchema.ruletestInputTestString;
    }
    
    /**
     * @return the ruletestOutputTestString
     */
    public static URI getRuletestOutputTestString()
    {
        return RegexRuleTestSchema.ruletestOutputTestString;
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
            
            con.add(RegexRuleTestSchema.getRegexRuletestTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);
            con.add(RegexRuleTestSchema.getRegexRuletestTypeUri(), RDFS.LABEL,
                    f.createLiteral("A test case for regular expression normalisation rules."), contextKeyUri);
            
            con.add(RegexRuleTestSchema.getRuletestInputTestString(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(RegexRuleTestSchema.getRuletestInputTestString(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(RegexRuleTestSchema.getRuletestInputTestString(), RDFS.DOMAIN, RuleTestSchema.getRuletestTypeUri(),
                    contextKeyUri);
            con.add(RegexRuleTestSchema.getRuletestInputTestString(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(RegexRuleTestSchema.getRuletestOutputTestString(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(RegexRuleTestSchema.getRuletestOutputTestString(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(RegexRuleTestSchema.getRuletestOutputTestString(), RDFS.DOMAIN,
                    RuleTestSchema.getRuletestTypeUri(), contextKeyUri);
            con.add(RegexRuleTestSchema.getRuletestOutputTestString(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
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
            
            RegexRuleTestSchema.log.error("RepositoryException: " + re.getMessage());
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
     * @param ruletestTypeUri
     *            the ruletestTypeUri to set
     */
    public static void setRegexRuletestTypeUri(final URI ruletestTypeUri)
    {
        RegexRuleTestSchema.regexRuletestTypeUri = ruletestTypeUri;
    }
    
    /**
     * @param ruletestInputTestString
     *            the ruletestInputTestString to set
     */
    public static void setRuletestInputTestString(final URI ruletestInputTestString)
    {
        RegexRuleTestSchema.ruletestInputTestString = ruletestInputTestString;
    }
    
    /**
     * @param ruletestOutputTestString
     *            the ruletestOutputTestString to set
     */
    public static void setRuletestOutputTestString(final URI ruletestOutputTestString)
    {
        RegexRuleTestSchema.ruletestOutputTestString = ruletestOutputTestString;
    }
    
}
