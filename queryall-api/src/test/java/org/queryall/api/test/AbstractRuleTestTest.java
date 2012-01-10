/**
 * 
 */
package org.queryall.api.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.ruletest.RuleTest;

/**
 * Abstract unit test for the RuleTest API.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractRuleTestTest
{
    private RuleTest testRuleTest1;
    private URI testRdfRuleUri1;
    private URI testRdfRuleUri2;
    
    /**
     * Returns a new instance of the RuleTest Implementation for each call.
     * 
     * @return
     */
    public abstract RuleTest getNewTestRuleTest();
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        final ValueFactory valueFactory = new ValueFactoryImpl();
        
        this.testRdfRuleUri1 = valueFactory.createURI("http://example.org/test/rule/1");
        this.testRdfRuleUri2 = valueFactory.createURI("http://example.org/test/rule/2");
        
        this.testRuleTest1 = this.getNewTestRuleTest();
        
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        this.testRuleTest1 = null;
        this.testRdfRuleUri1 = null;
        this.testRdfRuleUri2 = null;
    }
    
    /**
     * Test method for {@link org.queryall.api.ruletest.RuleTest#addRuleUri(org.openrdf.model.URI)}.
     */
    @Test
    public void testAddRuleUri()
    {
        this.testRuleTest1.addRuleUri(this.testRdfRuleUri1);
        
        Assert.assertEquals("Rule URIs were not added correctly", 1, this.testRuleTest1.getRuleUris().size());
    }
    
    /**
     * Test method for {@link org.queryall.api.ruletest.RuleTest#addStage(org.openrdf.model.URI)}.
     */
    @Test
    public void testAddStage()
    {
        this.testRuleTest1.addStage(NormalisationRuleSchema.getRdfruleStageAfterQueryCreation());
        this.testRuleTest1.addStage(NormalisationRuleSchema.getRdfruleStageAfterQueryParsing());
        this.testRuleTest1.addStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport());
        this.testRuleTest1.addStage(NormalisationRuleSchema.getRdfruleStageAfterResultsToDocument());
        this.testRuleTest1.addStage(NormalisationRuleSchema.getRdfruleStageAfterResultsToPool());
        this.testRuleTest1.addStage(NormalisationRuleSchema.getRdfruleStageBeforeResultsImport());
        this.testRuleTest1.addStage(NormalisationRuleSchema.getRdfruleStageQueryVariables());
        
        Assert.assertEquals("addStage test failed", 7, this.testRuleTest1.getStages().size());
    }
    
    /**
     * Test method for {@link org.queryall.api.ruletest.RuleTest#getRuleUris()}.
     */
    @Test
    public void testGetRuleUris()
    {
        this.testRuleTest1.addRuleUri(this.testRdfRuleUri1);
        this.testRuleTest1.addRuleUri(this.testRdfRuleUri2);
        
        Assert.assertEquals("Rule URIs were not added correctly", 2, this.testRuleTest1.getRuleUris().size());
    }
    
    /**
     * Test method for {@link org.queryall.api.ruletest.RuleTest#getStages()}.
     */
    @Test
    public void testGetStages()
    {
        this.testRuleTest1.addStage(NormalisationRuleSchema.getRdfruleStageAfterQueryCreation());
        this.testRuleTest1.addStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport());
        this.testRuleTest1.addStage(NormalisationRuleSchema.getRdfruleStageAfterResultsToPool());
        this.testRuleTest1.addStage(NormalisationRuleSchema.getRdfruleStageQueryVariables());
        
        Assert.assertEquals("getStages test failed", 4, this.testRuleTest1.getStages().size());
    }
    
    @Test
    public void testResetRuleUris()
    {
        Assert.assertEquals(0, this.testRuleTest1.getRuleUris().size());
        
        this.testRuleTest1.addRuleUri(this.testRdfRuleUri2);
        
        Assert.assertEquals(1, this.testRuleTest1.getRuleUris().size());
        
        Assert.assertTrue(this.testRuleTest1.resetRuleUris());
        
        Assert.assertEquals(0, this.testRuleTest1.getRuleUris().size());
    }
    
    @Test
    public void testResetStages()
    {
        Assert.assertEquals(0, this.testRuleTest1.getStages().size());
        
        this.testRuleTest1.addStage(NormalisationRuleSchema.getRdfruleStageQueryVariables());
        
        Assert.assertEquals(1, this.testRuleTest1.getStages().size());
        
        Assert.assertTrue(this.testRuleTest1.resetStages());
        
        Assert.assertEquals(0, this.testRuleTest1.getStages().size());
    }
}
