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
public class SparqlRuleTestSchema extends QueryAllSchema
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
    private static URI sparqlRuletestInputTriples;
    private static URI sparqlRuletestInputMimeType;
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.RULETEST.getBaseURI();
        
        SparqlRuleTestSchema.setSparqlRuleTestTypeUri(f.createURI(baseUri, "SparqlRuleTest"));
        SparqlRuleTestSchema.setSparqlRuletestSparqlAskPattern(f.createURI(baseUri, "sparqlAskPattern"));
        SparqlRuleTestSchema.setSparqlRuletestExpectedResult(f.createURI(baseUri, "expectedResult"));
        SparqlRuleTestSchema.setSparqlRuletestInputTriples(f.createURI(baseUri, "inputTriples"));
        SparqlRuleTestSchema.setSparqlRuletestInputMimeType(f.createURI(baseUri, "inputMimeType"));
    }
    
    public static final QueryAllSchema SPARQL_RULE_TEST_SCHEMA = new SparqlRuleTestSchema();
    
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
    public static URI getSparqlRuletestInputMimeType()
    {
        return SparqlRuleTestSchema.sparqlRuletestInputMimeType;
    }
    
    /**
     * @return the ruletestOutputTestString
     */
    public static URI getSparqlRuletestInputTriples()
    {
        return SparqlRuleTestSchema.sparqlRuletestInputTriples;
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
    public static void setSparqlRuletestInputMimeType(final URI ruletestInputTestString)
    {
        SparqlRuleTestSchema.sparqlRuletestInputMimeType = ruletestInputTestString;
    }
    
    /**
     * @param ruletestOutputTestString
     *            the ruletestOutputTestString to set
     */
    public static void setSparqlRuletestInputTriples(final URI ruletestOutputTestString)
    {
        SparqlRuleTestSchema.sparqlRuletestInputTriples = ruletestOutputTestString;
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
    
    /**
     * Default constructor, uses the name of this class as the name
     */
    public SparqlRuleTestSchema()
    {
        this(SparqlRuleTestSchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public SparqlRuleTestSchema(final String nextName)
    {
        super(nextName);
    }
    
    @Override
    public boolean schemaToRdf(final Repository myRepository, final int modelVersion, final URI... contexts)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(SparqlRuleTestSchema.getSparqlRuleTestTypeUri(), RDF.TYPE, OWL.CLASS, contexts);
            con.add(SparqlRuleTestSchema.getSparqlRuleTestTypeUri(), RDFS.LABEL,
                    f.createLiteral("A test case for RDF triples, based on a SPARQL select pattern."), contexts);
            
            con.add(SparqlRuleTestSchema.getSparqlRuletestSparqlAskPattern(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
            con.add(SparqlRuleTestSchema.getSparqlRuletestSparqlAskPattern(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(SparqlRuleTestSchema.getSparqlRuletestSparqlAskPattern(), RDFS.DOMAIN,
                    SparqlRuleTestSchema.getSparqlRuleTestTypeUri(), contexts);
            con.add(SparqlRuleTestSchema.getSparqlRuletestSparqlAskPattern(),
                    RDFS.LABEL,
                    f.createLiteral("The body of a SPARQL ASK query that will generate either true or false, as defined in rdfrule:expectedResult."),
                    contexts);
            
            con.add(SparqlRuleTestSchema.getSparqlRuletestExpectedResult(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
            con.add(SparqlRuleTestSchema.getSparqlRuletestExpectedResult(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(SparqlRuleTestSchema.getSparqlRuletestExpectedResult(), RDFS.DOMAIN,
                    SparqlRuleTestSchema.getSparqlRuleTestTypeUri(), contexts);
            con.add(SparqlRuleTestSchema.getSparqlRuletestExpectedResult(), RDFS.LABEL,
                    f.createLiteral("The expected result of the SPARQL ASK query, ie, true or false."), contexts);
            
            con.add(SparqlRuleTestSchema.getSparqlRuletestInputTriples(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
            con.add(SparqlRuleTestSchema.getSparqlRuletestInputTriples(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(SparqlRuleTestSchema.getSparqlRuletestInputTriples(), RDFS.DOMAIN,
                    SparqlRuleTestSchema.getSparqlRuleTestTypeUri(), contexts);
            con.add(SparqlRuleTestSchema.getSparqlRuletestInputTriples(),
                    RDFS.LABEL,
                    f.createLiteral("The RDF triples to normalise using the linked rules and stages before evaluating the ASK query."),
                    contexts);
            
            con.add(SparqlRuleTestSchema.getSparqlRuletestInputMimeType(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
            con.add(SparqlRuleTestSchema.getSparqlRuletestInputMimeType(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(SparqlRuleTestSchema.getSparqlRuletestInputMimeType(), RDFS.DOMAIN,
                    SparqlRuleTestSchema.getSparqlRuleTestTypeUri(), contexts);
            con.add(SparqlRuleTestSchema.getSparqlRuletestInputMimeType(), RDFS.LABEL,
                    f.createLiteral("The mime type of the input triples."), contexts);
            
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
    
}
