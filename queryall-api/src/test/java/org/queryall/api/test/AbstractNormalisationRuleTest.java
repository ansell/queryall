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
public abstract class AbstractNormalisationRuleTest extends AbstractProfilableNormalisationRuleTest
{
    private URI testStageInvalidInclusionRuleUri;
    private URI testStageAllValidAndInvalidRuleUri;
    protected Set<URI> validStages;
    protected Set<URI> invalidStages;
    private URI testRelatedNamespace1;
    
    /**
     * 
     * @return The set of URIs that are expected to be valid stages for this type of rule.
     */
    public abstract Set<URI> getExpectedValidStages();
    
    @Override
    public final NormalisationRule getNewTestProfilable()
    {
        return this.getNewTestRule();
    }
    
    /**
     * Create a new profile instance with default properties
     * 
     * @return A new profile instance with default properties
     */
    @Override
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
    @Override
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        
        final ValueFactory f = new MemValueFactory();
        
        // this.testTrueSparqlNormalisationRuleUri =
        // f.createURI("http://example.org/test/includedNormalisationRule");
        // this.testFalseSparqlNormalisationRuleUri =
        // f.createURI("http://example.org/test/excludedNormalisationRule");
        this.testStageInvalidInclusionRuleUri = f.createURI("http://example.org/test/stageInclusionRule");
        this.testStageAllValidAndInvalidRuleUri = f.createURI("http://example.org/test/stageExclusionRule");
        this.testRelatedNamespace1 = f.createURI("http://example.org/test/relatednamespace/1");
        
        this.validStages = this.getExpectedValidStages();
        
        Assert.assertNotNull("Expected valid stages was null", this.validStages);
        
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
    @Override
    @After
    public void tearDown() throws Exception
    {
        super.tearDown();
        
        // this.testTrueSparqlNormalisationRuleUri = null;
        // this.testFalseSparqlNormalisationRuleUri = null;
        this.testStageInvalidInclusionRuleUri = null;
        this.testStageAllValidAndInvalidRuleUri = null;
        
        this.testRelatedNamespace1 = null;
        
        this.invalidStages = null;
        this.validStages = null;
    }
    
    @Test
    public void testAllValidAndInvalidStages() throws InvalidStageException
    {
        final NormalisationRule normalisationRule = this.getNewTestRule();
        
        normalisationRule.setKey(this.testStageAllValidAndInvalidRuleUri);
        
        final Collection<URI> includedStages = this.validStages;
        
        Assert.assertNotNull(
                "Unexpected null stages. Check if super.setUp() and super.tearDown() are called in the relevant places",
                this.validStages);
        
        final Collection<URI> excludedStages = this.invalidStages;
        
        Assert.assertNotNull(
                "Unexpected null stages. Check if super.setUp() and super.tearDown() are called in the relevant places",
                this.invalidStages);
        
        for(final URI nextIncludedStage : includedStages)
        {
            try
            {
                normalisationRule.addStage(nextIncludedStage);
            }
            catch(final InvalidStageException ise)
            {
                // do sanity checking on the exception before failing
                Assert.assertEquals(nextIncludedStage, ise.getInvalidStageCause());
                Assert.assertEquals(normalisationRule, ise.getRuleCause());
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
        
        Assert.assertNotNull(
                "Unexpected null stages. Check if super.setUp() and super.tearDown() are called in the relevant places",
                this.validStages);
        Assert.assertNotNull(
                "Unexpected null stages. Check if super.setUp() and super.tearDown() are called in the relevant places",
                this.invalidStages);
        
        for(final URI nextInvalidStage : this.invalidStages)
        {
            try
            {
                normalisationRule.addStage(nextInvalidStage);
                Assert.fail("Did not find expected invalid stage exception for setStages nextInvalidStage="
                        + nextInvalidStage);
            }
            catch(final InvalidStageException ise)
            {
                // do sanity checking on the exception
                Assert.assertEquals(nextInvalidStage, ise.getInvalidStageCause());
                Assert.assertEquals(normalisationRule, ise.getRuleCause());
            }
        }
        
        Assert.assertEquals(normalisationRule.getStages().size(), 0);
        
        NormalisationRuleTestUtil.testIsValidInStages(normalisationRule, this.validStages, true);
        NormalisationRuleTestUtil.testIsValidInStages(normalisationRule, this.invalidStages, false);
        
        NormalisationRuleTestUtil.testIsUsedInStages(normalisationRule, includedStages, true);
        NormalisationRuleTestUtil.testIsUsedInStages(normalisationRule, excludedStages, false);
    }
    
    @Test
    public void testResetRelatedNamespaces()
    {
        final NormalisationRule normalisationRule = this.getNewTestRule();
        
        normalisationRule.addRelatedNamespace(this.testRelatedNamespace1);
        
        Assert.assertEquals(1, normalisationRule.getRelatedNamespaces().size());

        Assert.assertTrue(normalisationRule.resetRelatedNamespaces());
        
        Assert.assertEquals(0, normalisationRule.getRelatedNamespaces().size());
    }
    
    @Test
    public void testResetStages() throws InvalidStageException
    {
        final NormalisationRule normalisationRule = this.getNewTestRule();
        
        Assert.assertTrue("Normalisation Rule should not have an empty valid stages list", this.validStages.size() > 0);
        
        for(URI nextValidStage : this.validStages)
        {
            normalisationRule.addStage(nextValidStage);
        }
        
        Assert.assertEquals(this.validStages.size(), normalisationRule.getStages().size());

        Assert.assertTrue(normalisationRule.resetStages());
        
        Assert.assertEquals(0, normalisationRule.getStages().size());
    }
}
