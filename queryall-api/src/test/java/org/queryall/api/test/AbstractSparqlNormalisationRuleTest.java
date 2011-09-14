/**
 * 
 */
package org.queryall.api.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
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
    private Repository testRepository;
    private RepositoryConnection testRepositoryConnection;
    private ValueFactory testValueFactory;
    
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
        
        this.testRepository = new SailRepository(new MemoryStore());
        this.testRepository.initialize();
        this.testRepositoryConnection = this.testRepository.getConnection();
        this.testValueFactory = new ValueFactoryImpl();
        
        this.testMultipleWherePatternsSparqlNormalisationRuleUri =
                testValueFactory.createURI("http://example.org/test/multipleWherePatternsSparqlNormalisationRule");
        this.testStageEmptyConstructQuerySetSparqlNormalisationRuleUri =
                testValueFactory.createURI("http://example.org/test/emptyConstructQuerySetSparqlNormalisationRule");
        
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
        
        if(this.testRepositoryConnection != null)
        {
            try
            {
                this.testRepositoryConnection.close();
            }
            catch(final RepositoryException ex)
            {
                ex.printStackTrace();
            }
            finally
            {
                this.testRepositoryConnection = null;
            }
        }
        
        this.testRepository = null;
        this.testValueFactory = null;
        
        this.testMultipleWherePatternsSparqlNormalisationRuleUri = null;
        
        this.testStartingUriAEOBase = null;
        this.testFinalUriAEOBase = null;
        this.testStartingUriPOBase = null;
        this.testFinalUriPOBase = null;
    }
    
    /**
     * Tests the addMatchingTriples mode of the SparqlNormalisationRule interface
     * 
     * @throws RepositoryException
     * @throws QueryEvaluationException 
     * @throws MalformedQueryException 
     */
    @Test 
    public void testAddMatchingTriples() throws RepositoryException, QueryEvaluationException, MalformedQueryException
    {
        URI subjectUri = testValueFactory.createURI("http://example.org/po:0000198");
        
        URI predicateUri = testValueFactory.createURI("http://bio2rdf.org/ns/obo#is_a");
        
        URI objectUri = testValueFactory.createURI("http://example.org/po:0009089");
        
        Statement testStatement = testValueFactory.createStatement(subjectUri, predicateUri, objectUri);
        
        testRepositoryConnection.add(testStatement);
        
        testRepositoryConnection.commit();
        
        Assert.assertEquals("The test statement was not added to the repository", 1, testRepositoryConnection.size());
        
        final SparqlNormalisationRule sparqlRule = this.getNewTestSparqlRule();
        
        sparqlRule.setMode(getSparqlRuleModeAddAllMatchingTriplesURI());
        
        String sparqlConstructQueryTarget = " ?subjectUri ?normalisedPropertyUri ?objectUri . ?normalisedPropertyUri <http://www.w3.org/2002/07/owl#sameAs> ?propertyUri . ";
        String sparqlWherePattern = " ?subjectUri ?propertyUri ?objectUri . " +
        		"filter(strStarts(str(?propertyUri) , \"http://bio2rdf.org/ns/obo#\")) . " +
        		"bind(" +
        		"iri(" +
        		"concat(\"http://oas.example.org/obo_resource:\", " +
        		"encode_for_uri(lcase(substr(str(?propertyUri), 26))))) " +
        		"AS ?normalisedPropertyUri) . ";

        String nextConstructQuery = "CONSTRUCT { "+sparqlConstructQueryTarget+" } WHERE { "+sparqlWherePattern+" }";
        
        final GraphQueryResult graphResult =
                testRepositoryConnection.prepareGraphQuery(QueryLanguage.SPARQL, nextConstructQuery).evaluate();
        
        int selectedStatements = 0;
        
        while(graphResult.hasNext())
        {
            selectedStatements++;
        }

        Assert.assertTrue("Query was not executed properly by Sesame", (selectedStatements > 0));
        
        sparqlRule.setSparqlConstructQueryTarget(sparqlConstructQueryTarget);
        sparqlRule.addSparqlWherePattern(sparqlWherePattern);
        
        Assert.assertEquals("The construct pattern was not parsed correctly", 1, sparqlRule.getSparqlConstructQueries().size());
        
        sparqlRule.addStage(getRdfruleStageAfterResultsImportURI());
        
        Assert.assertTrue("Stage was not added correctly", sparqlRule.validInStage(getRdfruleStageAfterResultsImportURI()));
        
        sparqlRule.normaliseByStage(getRdfruleStageAfterResultsImportURI(), testRepository);

        Assert.assertEquals("The test statement was not added to the repository", 2, testRepositoryConnection.size());
    
    }
    
    @Test
    public void testConstructQueryMultipleWherePatterns()
    {
        final String testQueryConstructGraph =
                "?myUri ?property ?convertedUri . ?convertedUri <http://www.w3.org/2002/07/owl#sameAs> ?object . ";
        
        final SparqlNormalisationRule sparqlRule = this.getNewTestSparqlRule();
        
        sparqlRule.setKey(this.testMultipleWherePatternsSparqlNormalisationRuleUri);
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
