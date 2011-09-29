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
import org.openrdf.model.Literal;
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
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.rdfrule.SparqlNormalisationRule;
import org.queryall.api.rdfrule.SparqlNormalisationRuleSchema;

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
                this.testValueFactory.createURI("http://example.org/test/multipleWherePatternsSparqlNormalisationRule");
        this.testStageEmptyConstructQuerySetSparqlNormalisationRuleUri =
                this.testValueFactory
                        .createURI("http://example.org/test/emptyConstructQuerySetSparqlNormalisationRule");
        
        this.testStartingUriAEOBase = "http://purl.obolibrary.org/obo/AEO_";
        this.testFinalUriAEOBase = "http://bio2rdf.org/obo_aeo:";
        this.testStartingUriPOBase = "http://purl.obolibrary.org/obo/PO_";
        this.testFinalUriPOBase = "http://bio2rdf.org/obo_po:";
        
        this.invalidStages = new ArrayList<URI>(5);
        
        this.invalidStages.add(NormalisationRuleSchema.getRdfruleStageQueryVariables());
        this.invalidStages.add(NormalisationRuleSchema.getRdfruleStageAfterQueryCreation());
        this.invalidStages.add(NormalisationRuleSchema.getRdfruleStageAfterQueryParsing());
        this.invalidStages.add(NormalisationRuleSchema.getRdfruleStageBeforeResultsImport());
        this.invalidStages.add(NormalisationRuleSchema.getRdfruleStageAfterResultsToDocument());
        
        this.validStages = new ArrayList<URI>(2);
        
        this.validStages.add(NormalisationRuleSchema.getRdfruleStageAfterResultsImport());
        this.validStages.add(NormalisationRuleSchema.getRdfruleStageAfterResultsToPool());
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
        final URI subjectUri = this.testValueFactory.createURI("http://example.org/po:0000198");
        
        final URI predicateUri = this.testValueFactory.createURI("http://bio2rdf.org/ns/obo#is_a");
        
        final URI objectUri = this.testValueFactory.createURI("http://example.org/po:0009089");
        
        final Statement testStatement = this.testValueFactory.createStatement(subjectUri, predicateUri, objectUri);
        
        this.testRepositoryConnection.add(testStatement);
        
        this.testRepositoryConnection.commit();
        
        Assert.assertEquals("The test statement was not added to the repository", 1,
                this.testRepositoryConnection.size());
        
        final SparqlNormalisationRule sparqlRule = this.getNewTestSparqlRule();
        
        sparqlRule.setMode(SparqlNormalisationRuleSchema.getSparqlRuleModeAddAllMatchingTriples());
        
        final String sparqlConstructQueryTarget =
                " ?subjectUri ?normalisedPropertyUri ?objectUri . ?normalisedPropertyUri <http://www.w3.org/2002/07/owl#sameAs> ?propertyUri . ";
        final String sparqlWherePattern =
                " ?subjectUri ?propertyUri ?objectUri . "
                        + "filter(strStarts(str(?propertyUri) , \"http://bio2rdf.org/ns/obo#\")) . " + "bind(" + "iri("
                        + "concat(\"http://oas.example.org/obo_resource:\", " + "encode_for_uri(" + "lcase("
                        + "substr(str(?propertyUri), 26)" + ")" + ")" + ")" + ") " + "AS ?normalisedPropertyUri) . ";
        
        final String nextConstructQuery =
                "CONSTRUCT { " + sparqlConstructQueryTarget + " } WHERE { " + sparqlWherePattern + " }";
        
        final GraphQueryResult graphResult =
                this.testRepositoryConnection.prepareGraphQuery(QueryLanguage.SPARQL, nextConstructQuery).evaluate();
        
        int selectedStatements = 0;
        
        while(graphResult.hasNext())
        {
            selectedStatements++;
            graphResult.next();
        }
        
        Assert.assertTrue("Query was not executed properly by Sesame", (selectedStatements > 0));
        
        sparqlRule.setSparqlConstructQueryTarget(sparqlConstructQueryTarget);
        sparqlRule.addSparqlWherePattern(sparqlWherePattern);
        
        Assert.assertEquals("The construct pattern was not parsed correctly", 1, sparqlRule.getSparqlConstructQueries()
                .size());
        
        sparqlRule.addStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport());
        
        Assert.assertTrue("Stage was not added correctly",
                sparqlRule.validInStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport()));
        
        sparqlRule.normaliseByStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport(), this.testRepository);
        
        Assert.assertEquals("The test statements were not added to the repository", 3,
                this.testRepositoryConnection.size());
        
    }
    
    @Test
    public void testAddMatchingTriplesForGeneSymbol() throws RepositoryException, QueryEvaluationException,
        MalformedQueryException
    {
        final URI subjectUri = this.testValueFactory.createURI("http://bio2rdf.org/geneid:12334");
        
        final URI predicateUri =
                this.testValueFactory.createURI("http://purl.org/science/owl/sciencecommons/ggp_has_primary_symbol");
        
        final Literal objectLiteral = this.testValueFactory.createLiteral("Capn2");
        
        final Statement testStatement = this.testValueFactory.createStatement(subjectUri, predicateUri, objectLiteral);
        
        this.testRepositoryConnection.add(testStatement);
        
        this.testRepositoryConnection.commit();
        
        Assert.assertEquals("The test statement was not added to the repository", 1,
                this.testRepositoryConnection.size());
        
        final SparqlNormalisationRule sparqlRule = this.getNewTestSparqlRule();
        
        sparqlRule.setMode(SparqlNormalisationRuleSchema.getSparqlRuleModeAddAllMatchingTriples());
        
        final String sparqlConstructQueryTarget =
                " ?subjectUri <http://bio2rdf.org/bio2rdf_resource:dbxref> ?symbolUri .  ?symbolPredicate <http://bio2rdf.org/bio2rdf_resource:propertyMappedTo> <http://bio2rdf.org/bio2rdf_resource:dbxref> . ";
        final String sparqlWherePattern =
                " ?subjectUri ?symbolPredicate ?primarySymbol . " + "filter(sameTerm(?symbolPredicate , iri(\""
                        + predicateUri.stringValue() + "\"))) . " + "bind(" + "iri("
                        + "concat(\"http://bio2rdf.org/symbol:\", " + "encode_for_uri(" + "lcase("
                        + "str(?primarySymbol)" + ")" + ")" + ")" + ") " + "AS ?symbolUri) . ";
        
        final String nextConstructQuery =
                "CONSTRUCT { " + sparqlConstructQueryTarget + " } WHERE { " + sparqlWherePattern + " }";
        
        final GraphQueryResult graphResult =
                this.testRepositoryConnection.prepareGraphQuery(QueryLanguage.SPARQL, nextConstructQuery).evaluate();
        
        int selectedStatements = 0;
        
        while(graphResult.hasNext())
        {
            selectedStatements++;
            graphResult.next();
        }
        
        Assert.assertTrue("Query was not executed properly by Sesame", (selectedStatements > 0));
        
        sparqlRule.setSparqlConstructQueryTarget(sparqlConstructQueryTarget);
        sparqlRule.addSparqlWherePattern(sparqlWherePattern);
        
        Assert.assertEquals("The construct pattern was not parsed correctly", 1, sparqlRule.getSparqlConstructQueries()
                .size());
        
        sparqlRule.addStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport());
        
        Assert.assertTrue("Stage was not added correctly",
                sparqlRule.validInStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport()));
        
        sparqlRule.normaliseByStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport(), this.testRepository);
        
        Assert.assertEquals("The test statements were not added to the repository", 3,
                this.testRepositoryConnection.size());
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
        queryallRule.setMode(SparqlNormalisationRuleSchema.getSparqlRuleModeAddAllMatchingTriples());
        Assert.assertTrue(queryallRule.getMode().equals(
                SparqlNormalisationRuleSchema.getSparqlRuleModeAddAllMatchingTriples()));
        
        queryallRule = this.getNewTestSparqlRule();
        queryallRule.setMode(SparqlNormalisationRuleSchema.getSparqlRuleModeOnlyDeleteMatches());
        Assert.assertTrue(queryallRule.getMode().equals(
                SparqlNormalisationRuleSchema.getSparqlRuleModeOnlyDeleteMatches()));
        
        queryallRule = this.getNewTestSparqlRule();
        queryallRule.setMode(SparqlNormalisationRuleSchema.getSparqlRuleModeOnlyIncludeMatches());
        Assert.assertTrue(queryallRule.getMode().equals(
                SparqlNormalisationRuleSchema.getSparqlRuleModeOnlyIncludeMatches()));
    }
    
}
