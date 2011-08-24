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
import org.queryall.exception.InvalidStageException;

/**
 * Abstract unit test for NormalisationRule API
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractNormalisationRuleTest
{
    private URI testTrueSparqlNormalisationRuleUri;
    private URI testFalseSparqlNormalisationRuleUri;
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
    
    public abstract URI getProfileExcludeThenIncludeURI();
    
    public abstract URI getProfileIncludeExcludeOrderUndefinedUri();
    
    public abstract URI getProfileIncludeThenExcludeURI();
    
    public abstract URI getRdfruleStageAfterQueryCreationURI();
    
    public abstract URI getRdfruleStageAfterQueryParsingURI();
    
    public abstract URI getRdfruleStageAfterResultsImportURI();
    
    public abstract URI getRdfruleStageAfterResultsToDocumentURI();
    
    public abstract URI getRdfruleStageAfterResultsToPoolURI();
    
    public abstract URI getRdfruleStageBeforeResultsImportURI();
    
    public abstract URI getRdfruleStageQueryVariablesURI();
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        final ValueFactory f = new MemValueFactory();
        
        this.testTrueSparqlNormalisationRuleUri = f.createURI("http://example.org/test/includedNormalisationRule");
        this.testFalseSparqlNormalisationRuleUri = f.createURI("http://example.org/test/excludedNormalisationRule");
        this.testStageInvalidInclusionSparqlNormalisationRuleUri =
                f.createURI("http://example.org/test/stageInclusionSparqlNormalisationRule");
        this.testStageAllValidAndInvalidSparqlNormalisationRuleUri =
                f.createURI("http://example.org/test/stageExclusionSparqlNormalisationRule");
        
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
    @After
    public void tearDown() throws Exception
    {
        this.testTrueSparqlNormalisationRuleUri = null;
        this.testFalseSparqlNormalisationRuleUri = null;
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
                Assert.assertTrue("InvalidStageException thrown for valid stage", false);
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
