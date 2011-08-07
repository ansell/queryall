/**
 * 
 */
package org.queryall.api.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.sail.memory.model.MemValueFactory;

import org.queryall.api.NormalisationRule;
import org.queryall.api.SparqlNormalisationRule;

/**
 * Abstract unit test for SparqlNormalisationRule API.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractSparqlNormalisationRuleTest extends AbstractNormalisationRuleTest
{
    private URI testMultipleWherePatternsSparqlNormalisationRuleUri;
    private URI testStageEmptyConstructQuerySetSparqlNormalisationRuleUri;
    private String testStartingUriAEOBase;
    private String testFinalUriAEOBase;
    private String testStartingUriPOBase;
    private String testFinalUriPOBase;
    
    /**
     * @throws java.lang.Exception
     */
    @Override
	@Before
    public void setUp() throws Exception
    {
        super.setUp();
        
        ValueFactory f = new MemValueFactory();
        
        testMultipleWherePatternsSparqlNormalisationRuleUri =
                f.createURI("http://example.org/test/multipleWherePatternsSparqlNormalisationRule");
        testStageEmptyConstructQuerySetSparqlNormalisationRuleUri =
                f.createURI("http://example.org/test/emptyConstructQuerySetSparqlNormalisationRule");
        
        testStartingUriAEOBase = "http://purl.obolibrary.org/obo/AEO_";
        testFinalUriAEOBase = "http://bio2rdf.org/obo_aeo:";
        testStartingUriPOBase = "http://purl.obolibrary.org/obo/PO_";
        testFinalUriPOBase = "http://bio2rdf.org/obo_po:";
        
        invalidStages = new ArrayList<URI>(5);
        
        invalidStages.add(getRdfruleStageQueryVariablesURI());
        invalidStages.add(getRdfruleStageAfterQueryCreationURI());
        invalidStages.add(getRdfruleStageAfterQueryParsingURI());
        invalidStages.add(getRdfruleStageBeforeResultsImportURI());
        invalidStages.add(getRdfruleStageAfterResultsToDocumentURI());
        
        validStages = new ArrayList<URI>(2);
        
        validStages.add(getRdfruleStageAfterResultsImportURI());
        validStages.add(getRdfruleStageAfterResultsToPoolURI());
    }
    
    /**
     * @throws java.lang.Exception
     */
    @Override
	@After
    public void tearDown() throws Exception
    {
        super.tearDown();
        
        testMultipleWherePatternsSparqlNormalisationRuleUri = null;
        
        testStartingUriAEOBase = null;
        testFinalUriAEOBase = null;
        testStartingUriPOBase = null;
        testFinalUriPOBase = null;
    }
    
    /**
     * Final method, so that implementing test cases must supply a SparqlNormalisationRule instead,
     * through getNewTestSparqlRule.
     * 
     * @return A NormalisationRule that is also a SparqlNormalisationRule
     */
    @Override
    public final NormalisationRule getNewTestRule()
    {
        return getNewTestSparqlRule();
    }
    
    /**
     * Create a new instance of the SparqlNormalisationRule implementation being tested.
     * 
     * @return a new instance of the implemented SparqlNormalisationRule
     */
    public abstract SparqlNormalisationRule getNewTestSparqlRule();
    
    public abstract URI getSparqlRuleModeAddAllMatchingTriplesURI();
    
    public abstract URI getSparqlRuleModeOnlyDeleteMatchesURI();
    
    public abstract URI getSparqlRuleModeOnlyIncludeMatchesURI();
    
    @Test
    public void testConstructQueryMultipleWherePatterns()
    {
        String testQueryConstructGraph =
                "?myUri ?property ?convertedUri . ?convertedUri <http://www.w3.org/2002/07/owl#sameAs> ?object . ";
        
        NormalisationRule queryallRule = getNewTestRule();
        
        assertTrue(queryallRule instanceof NormalisationRule);
        assertTrue(queryallRule instanceof SparqlNormalisationRule);
        
        SparqlNormalisationRule sparqlRule = (SparqlNormalisationRule)queryallRule;
        
        queryallRule.setKey(testMultipleWherePatternsSparqlNormalisationRuleUri);
        sparqlRule.setSparqlConstructQueryTarget(testQueryConstructGraph);
        sparqlRule.addSparqlWherePattern(generateConversionPattern(testStartingUriAEOBase, testFinalUriAEOBase));
        sparqlRule.addSparqlWherePattern(generateConversionPattern(testStartingUriPOBase, testFinalUriPOBase));
        
        List<String> constructQueries = sparqlRule.getSparqlConstructQueries();
        
        assertEquals(constructQueries.size(), 2);
        
        // TODO: this should be insensitive to spaces, possibly by using regular expression
        // matchers, or by removing spaces
        // Matcher matcher = new Matcher();
        // assertThat(actual, matcher)
        // TODO: add prefix testing
        assertTrue(constructQueries.contains(mergeQuery("", testQueryConstructGraph,
                generateConversionPattern(testStartingUriAEOBase, testFinalUriAEOBase))));
        assertTrue(constructQueries.contains(mergeQuery("", testQueryConstructGraph,
                generateConversionPattern(testStartingUriPOBase, testFinalUriPOBase))));
    }
    
    @Test
    public void testModes()
    {
        SparqlNormalisationRule queryallRule = getNewTestSparqlRule();
        queryallRule.setMode(getSparqlRuleModeAddAllMatchingTriplesURI());
        assertTrue(queryallRule.getMode().equals(getSparqlRuleModeAddAllMatchingTriplesURI()));
        
        queryallRule = getNewTestSparqlRule();
        queryallRule.setMode(getSparqlRuleModeOnlyDeleteMatchesURI());
        assertTrue(queryallRule.getMode().equals(getSparqlRuleModeOnlyDeleteMatchesURI()));
        
        queryallRule = getNewTestSparqlRule();
        queryallRule.setMode(getSparqlRuleModeOnlyIncludeMatchesURI());
        assertTrue(queryallRule.getMode().equals(getSparqlRuleModeOnlyIncludeMatchesURI()));
    }
    
    @Test
    public void testEmptyConstructQuerySet()
    {
        NormalisationRule queryallRule = getNewTestRule();
        
        assertTrue(queryallRule instanceof NormalisationRule);
        
        SparqlNormalisationRule sparqlRule = (SparqlNormalisationRule)queryallRule;
        
        queryallRule.setKey(testStageEmptyConstructQuerySetSparqlNormalisationRuleUri);
        
        List<String> constructQueries = sparqlRule.getSparqlConstructQueries();
        
        assertEquals(constructQueries.size(), 0);
        
    }
    
    /**
     * @param testStartingUri
     * @param testFinalUri
     * @return
     */
    private static String generateConversionPattern(String testStartingUri, String testFinalUri)
    {
        return "?myUri ?property ?object . " + " " + "FILTER(isUri(?object) && regex(str(?object), \""
                + testStartingUri + "\")) . " + " " + "bind(" + "iri(" + "concat(" + "\"" + testFinalUri + "\"," + " "
                + "encode_for_uri(" + "lcase(" + "substr(" + "str(?object), " + (testStartingUri.length() + 1) + ")))"
                + " " + ") " + ") " + "AS ?convertedUri) . ";
    }
    
    /**
     * 
     * @param sparqlPrefixes
     * @param constructGraphPattern
     * @param wherePattern
     * @return
     */
    private static String mergeQuery(String sparqlPrefixes, String constructGraphPattern, String wherePattern)
    {
        return new StringBuilder(sparqlPrefixes).append(" CONSTRUCT { ").append(constructGraphPattern)
                .append(" } WHERE { ").append(wherePattern).append(" }").toString();
    }
    
}
