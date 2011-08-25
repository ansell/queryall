/**
 * 
 */
package org.queryall.api.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.sail.memory.model.MemValueFactory;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.rdfrule.SparqlNormalisationRule;

/**
 * Abstract unit test for SparqlNormalisationRule API.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractSparqlNormalisationRuleTest extends AbstractNormalisationRuleTest
{
    /**
     * @param testStartingUri
     * @param testFinalUri
     * @return
     */
    private static String generateConversionPattern(final String testStartingUri, final String testFinalUri)
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
    private static String mergeQuery(final String sparqlPrefixes, final String constructGraphPattern,
            final String wherePattern)
    {
        return new StringBuilder(sparqlPrefixes).append(" CONSTRUCT { ").append(constructGraphPattern)
                .append(" } WHERE { ").append(wherePattern).append(" }").toString();
    }
    
    private URI testMultipleWherePatternsSparqlNormalisationRuleUri;
    private URI testStageEmptyConstructQuerySetSparqlNormalisationRuleUri;
    private String testStartingUriAEOBase;
    private String testFinalUriAEOBase;
    
    private String testStartingUriPOBase;
    
    private String testFinalUriPOBase;
    
    /**
     * Final method, so that implementing test cases must supply a SparqlNormalisationRule instead,
     * through getNewTestSparqlRule.
     * 
     * @return A NormalisationRule that is also a SparqlNormalisationRule
     */
    @Override
    public final NormalisationRule getNewTestRule()
    {
        return this.getNewTestSparqlRule();
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
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        
        final ValueFactory f = new MemValueFactory();
        
        this.testMultipleWherePatternsSparqlNormalisationRuleUri =
                f.createURI("http://example.org/test/multipleWherePatternsSparqlNormalisationRule");
        this.testStageEmptyConstructQuerySetSparqlNormalisationRuleUri =
                f.createURI("http://example.org/test/emptyConstructQuerySetSparqlNormalisationRule");
        
        this.testStartingUriAEOBase = "http://purl.obolibrary.org/obo/AEO_";
        this.testFinalUriAEOBase = "http://bio2rdf.org/obo_aeo:";
        this.testStartingUriPOBase = "http://purl.obolibrary.org/obo/PO_";
        this.testFinalUriPOBase = "http://bio2rdf.org/obo_po:";
        
        this.invalidStages = new ArrayList<URI>(5);
        
        this.invalidStages.add(this.getRdfruleStageQueryVariablesURI());
        this.invalidStages.add(this.getRdfruleStageAfterQueryCreationURI());
        this.invalidStages.add(this.getRdfruleStageAfterQueryParsingURI());
        this.invalidStages.add(this.getRdfruleStageBeforeResultsImportURI());
        this.invalidStages.add(this.getRdfruleStageAfterResultsToDocumentURI());
        
        this.validStages = new ArrayList<URI>(2);
        
        this.validStages.add(this.getRdfruleStageAfterResultsImportURI());
        this.validStages.add(this.getRdfruleStageAfterResultsToPoolURI());
    }
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    @After
    public void tearDown() throws Exception
    {
        super.tearDown();
        
        this.testMultipleWherePatternsSparqlNormalisationRuleUri = null;
        
        this.testStartingUriAEOBase = null;
        this.testFinalUriAEOBase = null;
        this.testStartingUriPOBase = null;
        this.testFinalUriPOBase = null;
    }
    
    @Test
    public void testConstructQueryMultipleWherePatterns()
    {
        final String testQueryConstructGraph =
                "?myUri ?property ?convertedUri . ?convertedUri <http://www.w3.org/2002/07/owl#sameAs> ?object . ";
        
        final NormalisationRule queryallRule = this.getNewTestRule();
        
        Assert.assertTrue(queryallRule instanceof NormalisationRule);
        Assert.assertTrue(queryallRule instanceof SparqlNormalisationRule);
        
        final SparqlNormalisationRule sparqlRule = (SparqlNormalisationRule)queryallRule;
        
        queryallRule.setKey(this.testMultipleWherePatternsSparqlNormalisationRuleUri);
        sparqlRule.setSparqlConstructQueryTarget(testQueryConstructGraph);
        sparqlRule.addSparqlWherePattern(AbstractSparqlNormalisationRuleTest.generateConversionPattern(
                this.testStartingUriAEOBase, this.testFinalUriAEOBase));
        sparqlRule.addSparqlWherePattern(AbstractSparqlNormalisationRuleTest.generateConversionPattern(
                this.testStartingUriPOBase, this.testFinalUriPOBase));
        
        final List<String> constructQueries = sparqlRule.getSparqlConstructQueries();
        
        Assert.assertEquals(constructQueries.size(), 2);
        
        // TODO: this should be insensitive to spaces, possibly by using regular
        // expression
        // matchers, or by removing spaces
        // Matcher matcher = new Matcher();
        // assertThat(actual, matcher)
        // TODO: add prefix testing
        Assert.assertTrue(constructQueries.contains(AbstractSparqlNormalisationRuleTest.mergeQuery("",
                testQueryConstructGraph, AbstractSparqlNormalisationRuleTest.generateConversionPattern(
                        this.testStartingUriAEOBase, this.testFinalUriAEOBase))));
        Assert.assertTrue(constructQueries.contains(AbstractSparqlNormalisationRuleTest.mergeQuery("",
                testQueryConstructGraph, AbstractSparqlNormalisationRuleTest.generateConversionPattern(
                        this.testStartingUriPOBase, this.testFinalUriPOBase))));
    }
    
    @Test
    public void testEmptyConstructQuerySet()
    {
        final NormalisationRule queryallRule = this.getNewTestRule();
        
        Assert.assertTrue(queryallRule instanceof NormalisationRule);
        
        final SparqlNormalisationRule sparqlRule = (SparqlNormalisationRule)queryallRule;
        
        queryallRule.setKey(this.testStageEmptyConstructQuerySetSparqlNormalisationRuleUri);
        
        final List<String> constructQueries = sparqlRule.getSparqlConstructQueries();
        
        Assert.assertEquals(constructQueries.size(), 0);
        
    }
    
    @Test
    public void testModes()
    {
        SparqlNormalisationRule queryallRule = this.getNewTestSparqlRule();
        queryallRule.setMode(this.getSparqlRuleModeAddAllMatchingTriplesURI());
        Assert.assertTrue(queryallRule.getMode().equals(this.getSparqlRuleModeAddAllMatchingTriplesURI()));
        
        queryallRule = this.getNewTestSparqlRule();
        queryallRule.setMode(this.getSparqlRuleModeOnlyDeleteMatchesURI());
        Assert.assertTrue(queryallRule.getMode().equals(this.getSparqlRuleModeOnlyDeleteMatchesURI()));
        
        queryallRule = this.getNewTestSparqlRule();
        queryallRule.setMode(this.getSparqlRuleModeOnlyIncludeMatchesURI());
        Assert.assertTrue(queryallRule.getMode().equals(this.getSparqlRuleModeOnlyIncludeMatchesURI()));
    }
    
}
