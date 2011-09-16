/**
 * 
 */
package org.queryall.api.test;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.rdfrule.SimplePrefixMappingNormalisationRule;

/**
 * Abstract unit test for SparqlNormalisationRule API.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractSimplePrefixMappingNormalisationRuleTest extends AbstractNormalisationRuleTest
{
    @SuppressWarnings("unused")
    private URI testPrefixMappingNormalisationRuleUri1;
    @SuppressWarnings("unused")
    private URI testPrefixMappingNormalisationRuleUri2;
    
    @SuppressWarnings("unused")
    private String testStartingUriAEOBase;
    @SuppressWarnings("unused")
    private String testFinalUriAEOBase;
    
    @SuppressWarnings("unused")
    private String testStartingUriPOBase;
    @SuppressWarnings("unused")
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
        return this.getNewTestMappingRule();
    }
    
    /**
     * Create a new instance of the SparqlNormalisationRule implementation being tested.
     * 
     * @return a new instance of the implemented SparqlNormalisationRule
     */
    public abstract SimplePrefixMappingNormalisationRule getNewTestMappingRule();
    
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
        
        this.testPrefixMappingNormalisationRuleUri1 =
                testValueFactory.createURI("http://example.org/test/rule:prefixmapping-1");
        this.testPrefixMappingNormalisationRuleUri2 =
                testValueFactory.createURI("http://example.org/test/rule:prefixmapping-2");
        
        this.testStartingUriAEOBase = "http://purl.obolibrary.org/obo/AEO_";
        this.testFinalUriAEOBase = "http://bio2rdf.org/obo_aeo:";
        this.testStartingUriPOBase = "http://purl.obolibrary.org/obo/PO_";
        this.testFinalUriPOBase = "http://bio2rdf.org/obo_po:";
        
        this.invalidStages = new ArrayList<URI>(1);
        
        this.invalidStages.add(NormalisationRuleSchema.getRdfruleStageAfterQueryParsing());
        
        this.validStages = new ArrayList<URI>(6);
        
        this.validStages.add(NormalisationRuleSchema.getRdfruleStageQueryVariables());
        this.validStages.add(NormalisationRuleSchema.getRdfruleStageAfterQueryCreation());
        this.validStages.add(NormalisationRuleSchema.getRdfruleStageBeforeResultsImport());
        this.validStages.add(NormalisationRuleSchema.getRdfruleStageAfterResultsImport());
        this.validStages.add(NormalisationRuleSchema.getRdfruleStageAfterResultsToPool());
        this.validStages.add(NormalisationRuleSchema.getRdfruleStageAfterResultsToDocument());
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
        
        this.testPrefixMappingNormalisationRuleUri1 = null;
        
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
        
        URI normalisedPredicateUri = testValueFactory.createURI("http://bio2rdf.org/obo_resource:is_a");

        URI objectUri = testValueFactory.createURI("http://example.org/po:0009089");
        
        Statement testInputStatement = testValueFactory.createStatement(subjectUri, predicateUri, objectUri);
        
        Statement testOutputStatement = testValueFactory.createStatement(subjectUri, normalisedPredicateUri, objectUri);

        Statement testOutputMappingStatement = testValueFactory.createStatement(normalisedPredicateUri, OWL.SAMEAS, predicateUri);

        testRepositoryConnection.add(testInputStatement);
        
        testRepositoryConnection.commit();
        
        Assert.assertEquals("The test statement was not added to the repository", 1, testRepositoryConnection.size());
        
        final SimplePrefixMappingNormalisationRule mappingRule = this.getNewTestMappingRule();
        
        mappingRule.addMappingPredicate(OWL.SAMEAS);
        
        mappingRule.setInputUriPrefix("http://bio2rdf.org/ns/obo#");
        mappingRule.setOutputUriPrefix("http://bio2rdf.org/obo_resource:");
        
        mappingRule.addStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport());

        mappingRule.normaliseByStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport(), testRepository);

        Assert.assertTrue("The test output statement was not in the resulting repository", testRepositoryConnection.hasStatement(testOutputStatement, false));

        Assert.assertFalse("The test input statement was still in the resulting repository", testRepositoryConnection.hasStatement(testInputStatement, false));

        Assert.assertTrue("The test output mapping statement was not in the resulting repository", testRepositoryConnection.hasStatement(testOutputMappingStatement, false));

        Assert.assertEquals("The test statements were not added accurately to the repository", 2, testRepositoryConnection.size());
    }    
}
