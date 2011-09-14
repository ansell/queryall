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
public class SparqlRuleTestSchema
{
    private static final Logger log = LoggerFactory.getLogger(SparqlRuleTestSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = SparqlRuleTestSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = SparqlRuleTestSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = SparqlRuleTestSchema.log.isInfoEnabled();
    
    private static URI sparqlRuletestSparqlAskPattern;
    
    private static URI sparqlRuletestExpectedResult;
    
    private static URI sparqlRuletestTypeUri;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.RULETEST.getBaseURI();
        
        SparqlRuleTestSchema.setSparqlRuleTestTypeUri(f.createURI(baseUri, "SparqlRuleTest"));
        SparqlRuleTestSchema.setSparqlRuletestSparqlAskPattern(f.createURI(baseUri, "sparqlAskPattern"));
        SparqlRuleTestSchema.setSparqlRuletestExpectedResult(f.createURI(baseUri, "expectedResult"));
    }
    
    /**
     * @return the ruletestOutputTestString
     */
    public static URI getSparqlRuletestExpectedResult()
    {
        return SparqlRuleTestSchema.sparqlRuletestExpectedResult;
    }
    
    /**
     * @return the ruletestInputTestString
     */
    public static URI getSparqlRuletestSparqlAskPattern()
    {
        return SparqlRuleTestSchema.sparqlRuletestSparqlAskPattern;
    }
    
    /**
     * @return the ruletestTypeUri
     */
    public static URI getSparqlRuleTestTypeUri()
    {
        return SparqlRuleTestSchema.sparqlRuletestTypeUri;
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
            
            con.add(SparqlRuleTestSchema.getSparqlRuleTestTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);
            con.add(SparqlRuleTestSchema.getSparqlRuleTestTypeUri(), RDFS.LABEL,
                    f.createLiteral("A test case for RDF triples, based on a SPARQL select pattern."), contextKeyUri);
            
            con.add(SparqlRuleTestSchema.getSparqlRuletestSparqlAskPattern(), RDF.TYPE, OWL.DATATYPEPROPERTY,
                    contextKeyUri);
            con.add(SparqlRuleTestSchema.getSparqlRuletestSparqlAskPattern(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(SparqlRuleTestSchema.getSparqlRuletestSparqlAskPattern(), RDFS.DOMAIN,
                    SparqlRuleTestSchema.getSparqlRuleTestTypeUri(), contextKeyUri);
            con.add(SparqlRuleTestSchema.getSparqlRuletestSparqlAskPattern(), RDFS.LABEL, f.createLiteral("The body of a SPARQL ASK query that will generate either true or false, as defined in rdfrule:expectedResult."),
                    contextKeyUri);
            
            con.add(SparqlRuleTestSchema.getSparqlRuletestExpectedResult(), RDF.TYPE, OWL.DATATYPEPROPERTY,
                    contextKeyUri);
            con.add(SparqlRuleTestSchema.getSparqlRuletestExpectedResult(), RDFS.RANGE, RDFS.LITERAL,
                    contextKeyUri);
            con.add(SparqlRuleTestSchema.getSparqlRuletestExpectedResult(), RDFS.DOMAIN,
                    SparqlRuleTestSchema.getSparqlRuleTestTypeUri(), contextKeyUri);
            con.add(SparqlRuleTestSchema.getSparqlRuletestExpectedResult(), RDFS.LABEL, f.createLiteral("The expected result of the SPARQL ASK query, ie, true or false."),
                    contextKeyUri);
            
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
            
            SparqlRuleTestSchema.log.error("RepositoryException: " + re.getMessage());
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
     * @param ruletestOutputTestString
     *            the ruletestOutputTestString to set
     */
    public static void setSparqlRuletestExpectedResult(final URI ruletestOutputTestString)
    {
        SparqlRuleTestSchema.sparqlRuletestExpectedResult = ruletestOutputTestString;
    }
    
    /**
     * @param ruletestInputTestString
     *            the ruletestInputTestString to set
     */
    public static void setSparqlRuletestSparqlAskPattern(final URI ruletestInputTestString)
    {
        SparqlRuleTestSchema.sparqlRuletestSparqlAskPattern = ruletestInputTestString;
    }
    
    /**
     * @param ruletestTypeUri
     *            the ruletestTypeUri to set
     */
    public static void setSparqlRuleTestTypeUri(final URI ruletestTypeUri)
    {
        SparqlRuleTestSchema.sparqlRuletestTypeUri = ruletestTypeUri;
    }
    
}
