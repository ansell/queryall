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
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.rdfrule.SparqlAskRule;
import org.queryall.api.rdfrule.SparqlNormalisationRule;
import org.queryall.exception.InvalidStageException;
import org.queryall.exception.ValidationFailedException;

/**
 * Abstract unit test for SparqlNormalisationRule API.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractSparqlAskRuleTest extends AbstractSparqlNormalisationRuleTest
{
    /**
     * 
     * @param sparqlPrefixes
     * @param constructGraphPattern
     * @param wherePattern
     * @return
     */
    private static String mergeQuery(final String sparqlPrefixes, final String wherePattern)
    {
        return new StringBuilder(sparqlPrefixes).append(" ASK WHERE { ").append(wherePattern).append(" }").toString();
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
     * Create a new instance of the SparqlAskRule implementation being tested.
     * 
     * @return a new instance of the implemented SparqlAskRule
     */
    public abstract SparqlAskRule getNewTestSparqlAskRule();
    
    /**
     * Final method, so that implementing test cases must supply a SparqlAskRule instead, through
     * getNewTestSparqlAskRule.
     * 
     * @return A NormalisationRule that is also a SparqlNormalisationRule
     */
    @Override
    public final SparqlNormalisationRule getNewTestSparqlNormalisationRule()
    {
        return this.getNewTestSparqlAskRule();
    }
    
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
    
    @Test
    public void testConstructQueryMultipleWherePatterns()
    {
        final String testQueryConstructGraph =
                "?myUri ?property ?convertedUri . ?convertedUri <http://www.w3.org/2002/07/owl#sameAs> ?object . ";
        
        final SparqlAskRule sparqlRule = this.getNewTestSparqlAskRule();
        
        sparqlRule.setKey(this.testMultipleWherePatternsSparqlNormalisationRuleUri);
        
        sparqlRule.addSparqlWherePattern("?s ?p ?o . ?s a ?type . ");
        
        sparqlRule.addSparqlWherePattern("?s a ?o . ?other a ?o . FILTER( ?other != ?s) . ");
        
        final List<String> askQueries = sparqlRule.getSparqlAskQueries();
        
        Assert.assertEquals(2, askQueries.size());
        
    }
    
    @Test
    public void testEmptyWhereQuerySet()
    {
        final SparqlAskRule sparqlRule = this.getNewTestSparqlAskRule();
        
        sparqlRule.setKey(this.testStageEmptyConstructQuerySetSparqlNormalisationRuleUri);
        
        final List<String> askQueries = sparqlRule.getSparqlAskQueries();
        
        Assert.assertEquals(0, askQueries.size());
        
    }
    
    /**
     * Tests the SparqlAskRule interface using a match that should be positive
     * 
     * @throws RepositoryException
     * @throws QueryEvaluationException
     * @throws MalformedQueryException
     * @throws InvalidStageException 
     * @throws ValidationFailedException 
     */
    @Test
    public void testNegativeMatchTriples() throws RepositoryException, QueryEvaluationException,
        MalformedQueryException, InvalidStageException, ValidationFailedException
    {
        final URI subjectUri = this.testValueFactory.createURI("http://example.org/po:0000198");
        
        final URI predicateUri = this.testValueFactory.createURI("http://bio2rdf.org/ns/obo#is_a");
        
        final URI objectUri = this.testValueFactory.createURI("http://example.org/po:0009089");
        
        final Statement testStatement = this.testValueFactory.createStatement(subjectUri, predicateUri, objectUri);
        
        this.testRepositoryConnection.add(testStatement);
        
        this.testRepositoryConnection.commit();
        
        Assert.assertEquals("The test statement was not added to the repository", 1,
                this.testRepositoryConnection.size());
        
        final SparqlAskRule sparqlRule = this.getNewTestSparqlAskRule();
        
        final String sparqlWherePattern = " ?s a ?o . ";
        
        sparqlRule.addSparqlWherePattern(sparqlWherePattern);
        
        Assert.assertEquals("The ask pattern was not parsed correctly", 1, sparqlRule.getSparqlAskQueries().size());
        
        sparqlRule.addStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport());
        
        Assert.assertTrue("Stage was not added correctly",
                sparqlRule.validInStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport()));
        
        final boolean result =
                sparqlRule.normaliseByStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport(),
                        this.testRepository);
        
        Assert.assertFalse("Ask queries did not execute properly to return false", result);
    }
    
    /**
     * Tests the SparqlAskRule interface using a match that should be positive
     * 
     * @throws RepositoryException
     * @throws QueryEvaluationException
     * @throws MalformedQueryException
     * @throws ValidationFailedException 
     * @throws InvalidStageException 
     */
    @Test
    public void testPositiveMatchTriples() throws RepositoryException, QueryEvaluationException,
        MalformedQueryException, InvalidStageException, ValidationFailedException
    {
        final URI subjectUri = this.testValueFactory.createURI("http://example.org/po:0000198");
        
        final URI predicateUri = this.testValueFactory.createURI("http://bio2rdf.org/ns/obo#is_a");
        
        final URI objectUri = this.testValueFactory.createURI("http://example.org/po:0009089");
        
        final Statement testStatement = this.testValueFactory.createStatement(subjectUri, predicateUri, objectUri);
        
        this.testRepositoryConnection.add(testStatement);
        
        this.testRepositoryConnection.commit();
        
        Assert.assertEquals("The test statement was not added to the repository", 1,
                this.testRepositoryConnection.size());
        
        final SparqlAskRule sparqlRule = this.getNewTestSparqlAskRule();
        
        final String sparqlWherePattern = " ?s ?p ?o . ";
        
        sparqlRule.addSparqlWherePattern(sparqlWherePattern);
        
        Assert.assertEquals("The ask pattern was not parsed correctly", 1, sparqlRule.getSparqlAskQueries().size());
        
        sparqlRule.addStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport());
        
        Assert.assertTrue("Stage was not added correctly",
                sparqlRule.validInStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport()));
        
        final boolean result =
                sparqlRule.normaliseByStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport(),
                        this.testRepository);
        
        Assert.assertTrue("Ask queries did not execute properly to return true", result);
    }
}
