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
import org.queryall.api.base.QueryAllSchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class StringRuleTestSchema implements QueryAllSchema
{
    private static final Logger log = LoggerFactory.getLogger(StringRuleTestSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = StringRuleTestSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = StringRuleTestSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = StringRuleTestSchema.log.isInfoEnabled();
    
    private static URI ruletestInputTestString;
    
    private static URI ruletestOutputTestString;
    
    private static URI stringRuletestTypeUri;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.RULETEST.getBaseURI();
        
        StringRuleTestSchema.setStringRuleTestTypeUri(f.createURI(baseUri, "StringRuleTest"));
        StringRuleTestSchema.setRuletestInputTestString(f.createURI(baseUri, "inputTestString"));
        StringRuleTestSchema.setRuletestOutputTestString(f.createURI(baseUri, "outputTestString"));
    }
    
    /**
     * @return the ruletestInputTestString
     */
    public static URI getRuletestInputTestString()
    {
        return StringRuleTestSchema.ruletestInputTestString;
    }
    
    /**
     * @return the ruletestOutputTestString
     */
    public static URI getRuletestOutputTestString()
    {
        return StringRuleTestSchema.ruletestOutputTestString;
    }
    
    /**
     * @return the ruletestTypeUri
     */
    public static URI getStringRuleTestTypeUri()
    {
        return StringRuleTestSchema.stringRuletestTypeUri;
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
            
            con.add(StringRuleTestSchema.getStringRuleTestTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);
            con.add(StringRuleTestSchema.getStringRuleTestTypeUri(), RDFS.LABEL,
                    f.createLiteral("A test case for regular expression normalisation rules."), contextKeyUri);
            
            con.add(StringRuleTestSchema.getRuletestInputTestString(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(StringRuleTestSchema.getRuletestInputTestString(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(StringRuleTestSchema.getRuletestInputTestString(), RDFS.DOMAIN,
                    StringRuleTestSchema.getStringRuleTestTypeUri(), contextKeyUri);
            con.add(StringRuleTestSchema.getRuletestInputTestString(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(StringRuleTestSchema.getRuletestOutputTestString(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(StringRuleTestSchema.getRuletestOutputTestString(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(StringRuleTestSchema.getRuletestOutputTestString(), RDFS.DOMAIN,
                    StringRuleTestSchema.getStringRuleTestTypeUri(), contextKeyUri);
            con.add(StringRuleTestSchema.getRuletestOutputTestString(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
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
            
            StringRuleTestSchema.log.error("RepositoryException: " + re.getMessage());
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
     * @param ruletestInputTestString
     *            the ruletestInputTestString to set
     */
    public static void setRuletestInputTestString(final URI ruletestInputTestString)
    {
        StringRuleTestSchema.ruletestInputTestString = ruletestInputTestString;
    }
    
    /**
     * @param ruletestOutputTestString
     *            the ruletestOutputTestString to set
     */
    public static void setRuletestOutputTestString(final URI ruletestOutputTestString)
    {
        StringRuleTestSchema.ruletestOutputTestString = ruletestOutputTestString;
    }
    
    /**
     * @param ruletestTypeUri
     *            the ruletestTypeUri to set
     */
    public static void setStringRuleTestTypeUri(final URI ruletestTypeUri)
    {
        StringRuleTestSchema.stringRuletestTypeUri = ruletestTypeUri;
    }
    
}
