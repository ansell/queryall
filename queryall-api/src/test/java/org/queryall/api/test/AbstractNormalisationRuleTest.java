/**
 * 
 */
package org.queryall.api.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.sail.memory.model.MemValueFactory;
import org.queryall.api.profile.Profile;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.exception.InvalidStageException;

/**
 * Abstract unit test for NormalisationRule API
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractNormalisationRuleTest
{
    // private URI testTrueSparqlNormalisationRuleUri;
    // private URI testFalseSparqlNormalisationRuleUri;
    private URI testStageInvalidInclusionSparqlNormalisationRuleUri;
    private URI testStageAllValidAndInvalidSparqlNormalisationRuleUri;
    protected List<URI> validStages;
    protected List<URI> invalidStages;
    
    /**
     * Create a new profile instance with default properties
     * 
     * @return A new profile instance with default properties
     */
    public abstract Profile getNewTestProfile();
    
    /**
     * Create a new instance of the SparqlNormalisationRule implementation being tested
     * 
     * @return a new instance of the implemented SparqlNormalisationRule
     */
    public abstract NormalisationRule getNewTestRule();
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        final ValueFactory f = new MemValueFactory();
        
        // this.testTrueSparqlNormalisationRuleUri =
        // f.createURI("http://example.org/test/includedNormalisationRule");
        // this.testFalseSparqlNormalisationRuleUri =
        // f.createURI("http://example.org/test/excludedNormalisationRule");
        this.testStageInvalidInclusionSparqlNormalisationRuleUri =
                f.createURI("http://example.org/test/stageInclusionSparqlNormalisationRule");
        this.testStageAllValidAndInvalidSparqlNormalisationRuleUri =
                f.createURI("http://example.org/test/stageExclusionSparqlNormalisationRule");
        
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
    @After
    public void tearDown() throws Exception
    {
        // this.testTrueSparqlNormalisationRuleUri = null;
        // this.testFalseSparqlNormalisationRuleUri = null;
        this.testStageInvalidInclusionSparqlNormalisationRuleUri = null;
        this.testStageAllValidAndInvalidSparqlNormalisationRuleUri = null;
        
        this.invalidStages = null;
        this.validStages = null;
    }
    
    @Test
    public void testAllValidAndInvalidStages()
    {
        final NormalisationRule queryallRule = this.getNewTestRule();
        
        Assert.assertTrue(queryallRule instanceof NormalisationRule);
        
        final NormalisationRule normalisationRule = queryallRule;
        
        normalisationRule.setKey(this.testStageAllValidAndInvalidSparqlNormalisationRuleUri);
        
        final Collection<URI> includedStages = this.validStages;
        
        final Collection<URI> excludedStages = this.invalidStages;
        
        for(final URI nextIncludedStage : includedStages)
        {
            try
            {
                normalisationRule.addStage(nextIncludedStage);
            }
            catch(final InvalidStageException ise)
            {
                Assert.fail("InvalidStageException thrown for valid stage nextIncludedStage=" + nextIncludedStage);
            }
        }
        
        NormalisationRuleTestUtil.testIsValidInStages(normalisationRule, this.validStages, true);
        NormalisationRuleTestUtil.testIsValidInStages(normalisationRule, this.invalidStages, false);
        
        NormalisationRuleTestUtil.testIsUsedInStages(normalisationRule, includedStages, true);
        NormalisationRuleTestUtil.testIsUsedInStages(normalisationRule, excludedStages, false);
    }
    
    @Test
    public void testInvalidStageInclusion()
    {
        final NormalisationRule queryallRule = this.getNewTestRule();
        
        Assert.assertTrue(queryallRule instanceof NormalisationRule);
        
        final NormalisationRule normalisationRule = queryallRule;
        
        normalisationRule.setKey(this.testStageInvalidInclusionSparqlNormalisationRuleUri);
        
        final Collection<URI> includedStages = new ArrayList<URI>(0);
        
        final Collection<URI> excludedStages = this.invalidStages;
        
        for(final URI nextInvalidStage : this.invalidStages)
        {
            try
            {
                normalisationRule.addStage(nextInvalidStage);
                Assert.fail("Did not find expected invalid stage exception for setStages");
            }
            catch(final InvalidStageException e)
            {
                // expected exception
            }
        }
        
        Assert.assertEquals(normalisationRule.getStages().size(), 0);
        
        NormalisationRuleTestUtil.testIsValidInStages(normalisationRule, this.validStages, true);
        NormalisationRuleTestUtil.testIsValidInStages(normalisationRule, this.invalidStages, false);
        
        NormalisationRuleTestUtil.testIsUsedInStages(normalisationRule, includedStages, true);
        NormalisationRuleTestUtil.testIsUsedInStages(normalisationRule, excludedStages, false);
    }
}
