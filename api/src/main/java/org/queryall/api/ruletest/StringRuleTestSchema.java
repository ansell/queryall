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
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices(QueryAllSchema.class)
public class StringRuleTestSchema extends QueryAllSchema
{
    private static final Logger LOG = LoggerFactory.getLogger(StringRuleTestSchema.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = StringRuleTestSchema.LOG.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = StringRuleTestSchema.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = StringRuleTestSchema.LOG.isInfoEnabled();
    
    private static URI ruletestInputTestString;
    
    private static URI ruletestOutputTestString;
    
    private static URI stringRuletestTypeUri;
    
    static
    {
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        final String baseUri = QueryAllNamespaces.RULETEST.getBaseURI();
        
        StringRuleTestSchema.setStringRuleTestTypeUri(f.createURI(baseUri, "StringRuleTest"));
        StringRuleTestSchema.setRuletestInputTestString(f.createURI(baseUri, "inputTestString"));
        StringRuleTestSchema.setRuletestOutputTestString(f.createURI(baseUri, "outputTestString"));
    }
    
    /**
     * The pre-instantiated schema object for StringRuleTestSchema.
     */
    public static final QueryAllSchema STRING_RULE_TEST_SCHEMA = new StringRuleTestSchema();
    
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
    
    /**
     * @param nextRuletestInputTestString
     *            the ruletestInputTestString to set
     */
    public static void setRuletestInputTestString(final URI nextRuletestInputTestString)
    {
        StringRuleTestSchema.ruletestInputTestString = nextRuletestInputTestString;
    }
    
    /**
     * @param nextRuletestOutputTestString
     *            the ruletestOutputTestString to set
     */
    public static void setRuletestOutputTestString(final URI nextRuletestOutputTestString)
    {
        StringRuleTestSchema.ruletestOutputTestString = nextRuletestOutputTestString;
    }
    
    /**
     * @param nextStringRuletestTypeUri
     *            the ruletestTypeUri to set
     */
    public static void setStringRuleTestTypeUri(final URI nextStringRuletestTypeUri)
    {
        StringRuleTestSchema.stringRuletestTypeUri = nextStringRuletestTypeUri;
    }
    
    /**
     * Default constructor, uses the name of this class as the name.
     */
    public StringRuleTestSchema()
    {
        this(StringRuleTestSchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public StringRuleTestSchema(final String nextName)
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
            
            con.add(StringRuleTestSchema.getStringRuleTestTypeUri(), RDF.TYPE, OWL.CLASS, contexts);
            con.add(StringRuleTestSchema.getStringRuleTestTypeUri(), RDFS.LABEL,
                    f.createLiteral("A test case for regular expression normalisation rules."), contexts);
            
            con.add(StringRuleTestSchema.getRuletestInputTestString(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
            con.add(StringRuleTestSchema.getRuletestInputTestString(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(StringRuleTestSchema.getRuletestInputTestString(), RDFS.DOMAIN,
                    StringRuleTestSchema.getStringRuleTestTypeUri(), contexts);
            con.add(StringRuleTestSchema.getRuletestInputTestString(), RDFS.LABEL, f.createLiteral("."), contexts);
            
            con.add(StringRuleTestSchema.getRuletestOutputTestString(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
            con.add(StringRuleTestSchema.getRuletestOutputTestString(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(StringRuleTestSchema.getRuletestOutputTestString(), RDFS.DOMAIN,
                    StringRuleTestSchema.getStringRuleTestTypeUri(), contexts);
            con.add(StringRuleTestSchema.getRuletestOutputTestString(), RDFS.LABEL, f.createLiteral("."), contexts);
            
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
            
            StringRuleTestSchema.LOG.error("RepositoryException: " + re.getMessage());
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
