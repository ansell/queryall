/**
 * 
 */
package org.queryall.api.test;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.rdfrule.SpinConstraintRule;
import org.queryall.api.rdfrule.SpinNormalisationRule;

/**
 * Abstract unit test for SparqlNormalisationRule API.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractSpinConstraintRuleTest extends AbstractSpinNormalisationRuleTest
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
    public abstract SpinConstraintRule getNewTestSpinConstraintRule();
    
    /**
     * Final method, so that implementing test cases must supply a SparqlAskRule instead, through
     * getNewTestSparqlAskRule.
     * 
     * @return A NormalisationRule that is also a SparqlNormalisationRule
     */
    @Override
    public final SpinNormalisationRule getNewTestSpinNormalisationRule()
    {
        return this.getNewTestSpinConstraintRule();
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
    
}
