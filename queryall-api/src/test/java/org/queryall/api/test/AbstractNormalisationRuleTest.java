/**
 * 
 */
package org.queryall.api.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
    private URI testStageInvalidInclusionRuleUri;
    private URI testStageAllValidAndInvalidRuleUri;
    protected Set<URI> validStages;
    protected Set<URI> invalidStages;
    
    /**
     * 
     * @return The set of URIs that are expected to be valid stages for this type of rule.
     */
    public abstract Set<URI> getExpectedValidStages();
    
    /**
     * Create a new profile instance with default properties
     * 
     * @return A new profile instance with default properties
     */
    public abstract Profile getNewTestProfile();
    
    /**
     * Create a new instance of the NormalisationRule implementation being tested
     * 
     * @return a new instance of the implemented NormalisationRule
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
        this.testStageInvalidInclusionRuleUri = f.createURI("http://example.org/test/stageInclusionRule");
        this.testStageAllValidAndInvalidRuleUri = f.createURI("http://example.org/test/stageExclusionRule");
        
        this.validStages = this.getExpectedValidStages();
        
        // make sure that we have reasonable sizes for the relevant sets
        Assert.assertTrue(this.validStages.size() > 0);
        Assert.assertTrue(this.validStages.size() <= 7);
        Assert.assertEquals(7, NormalisationRuleSchema.getAllStages().size());
        
        this.invalidStages = new HashSet<URI>(7);
        
        for(final URI nextStage : NormalisationRuleSchema.getAllStages())
        {
            if(!this.validStages.contains(nextStage))
            {
                this.invalidStages.add(nextStage);
            }
        }
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        // this.testTrueSparqlNormalisationRuleUri = null;
        // this.testFalseSparqlNormalisationRuleUri = null;
        this.testStageInvalidInclusionRuleUri = null;
        this.testStageAllValidAndInvalidRuleUri = null;
        
        this.invalidStages = null;
        this.validStages = null;
    }
    
    @Test
    public void testAllValidAndInvalidStages() throws InvalidStageException
    {
        final NormalisationRule normalisationRule = this.getNewTestRule();
        
        normalisationRule.setKey(this.testStageAllValidAndInvalidRuleUri);
        
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
    public void testInvalidStageInclusion() throws InvalidStageException
    {
        final NormalisationRule normalisationRule = this.getNewTestRule();
        
        normalisationRule.setKey(this.testStageInvalidInclusionRuleUri);
        
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
