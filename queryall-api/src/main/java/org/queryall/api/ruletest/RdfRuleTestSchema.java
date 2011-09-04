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
public class RdfRuleTestSchema
{
    private static final Logger log = LoggerFactory.getLogger(RdfRuleTestSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = RdfRuleTestSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = RdfRuleTestSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = RdfRuleTestSchema.log.isInfoEnabled();
    
    private static URI rdfRuletestSparqlSelectPattern;
    
    private static URI rdfRuletestExpectedResultVariables;
    
    private static URI rdfRuletestTypeUri;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.RULETEST.getBaseURI();
        
        RdfRuleTestSchema.setRdfRuleTestTypeUri(f.createURI(baseUri, "RdfRuleTest"));
        RdfRuleTestSchema.setRdfRuletestSparqlSelectPattern(f.createURI(baseUri, "sparqlSelectPattern"));
        RdfRuleTestSchema.setRdfRuletestExpectedResultVariables(f.createURI(baseUri, "expectedResultVariables"));
    }
    
    /**
     * @return the ruletestOutputTestString
     */
    public static URI getRdfRuletestExpectedResultVariables()
    {
        return RdfRuleTestSchema.rdfRuletestExpectedResultVariables;
    }
    
    /**
     * @return the ruletestInputTestString
     */
    public static URI getRdfRuletestSparqlSelectPattern()
    {
        return RdfRuleTestSchema.rdfRuletestSparqlSelectPattern;
    }
    
    /**
     * @return the ruletestTypeUri
     */
    public static URI getRdfRuleTestTypeUri()
    {
        return RdfRuleTestSchema.rdfRuletestTypeUri;
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
            
            con.add(RdfRuleTestSchema.getRdfRuleTestTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);
            con.add(RdfRuleTestSchema.getRdfRuleTestTypeUri(), RDFS.LABEL,
                    f.createLiteral("A test case for RDF triples, based on a SPARQL select pattern."), contextKeyUri);
            
            con.add(RdfRuleTestSchema.getRdfRuletestSparqlSelectPattern(), RDF.TYPE, OWL.DATATYPEPROPERTY,
                    contextKeyUri);
            con.add(RdfRuleTestSchema.getRdfRuletestSparqlSelectPattern(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(RdfRuleTestSchema.getRdfRuletestSparqlSelectPattern(), RDFS.DOMAIN,
                    RdfRuleTestSchema.getRdfRuleTestTypeUri(), contextKeyUri);
            con.add(RdfRuleTestSchema.getRdfRuletestSparqlSelectPattern(), RDFS.LABEL, f.createLiteral("."),
                    contextKeyUri);
            
            con.add(RdfRuleTestSchema.getRdfRuletestExpectedResultVariables(), RDF.TYPE, OWL.DATATYPEPROPERTY,
                    contextKeyUri);
            con.add(RdfRuleTestSchema.getRdfRuletestExpectedResultVariables(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(RdfRuleTestSchema.getRdfRuletestExpectedResultVariables(), RDFS.DOMAIN,
                    RdfRuleTestSchema.getRdfRuleTestTypeUri(), contextKeyUri);
            con.add(RdfRuleTestSchema.getRdfRuletestExpectedResultVariables(), RDFS.LABEL, f.createLiteral("."),
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
            
            RdfRuleTestSchema.log.error("RepositoryException: " + re.getMessage());
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
    public static void setRdfRuletestExpectedResultVariables(final URI ruletestOutputTestString)
    {
        RdfRuleTestSchema.rdfRuletestExpectedResultVariables = ruletestOutputTestString;
    }
    
    /**
     * @param ruletestInputTestString
     *            the ruletestInputTestString to set
     */
    public static void setRdfRuletestSparqlSelectPattern(final URI ruletestInputTestString)
    {
        RdfRuleTestSchema.rdfRuletestSparqlSelectPattern = ruletestInputTestString;
    }
    
    /**
     * @param ruletestTypeUri
     *            the ruletestTypeUri to set
     */
    public static void setRdfRuleTestTypeUri(final URI ruletestTypeUri)
    {
        RdfRuleTestSchema.rdfRuletestTypeUri = ruletestTypeUri;
    }
    
}
