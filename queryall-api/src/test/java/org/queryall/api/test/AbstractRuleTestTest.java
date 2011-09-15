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
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractRuleTestTest
{
    private RuleTest testRuleTest1;
    private URI testRdfRuleUri1;
    private URI testRdfRuleUri2;

    /**
     * Returns a new instance of the RuleTest Implementation for each call
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

        testRuleTest1 = getNewTestRuleTest();
        
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        testRuleTest1 = null;
        testRdfRuleUri1 = null;
        testRdfRuleUri2 = null;
    }
    
    /**
     * Test method for {@link org.queryall.api.ruletest.RuleTest#getRuleUris()}.
     */
    @Test
    public void testGetRuleUris()
    {
        testRuleTest1.addRuleUri(this.testRdfRuleUri1);
        testRuleTest1.addRuleUri(this.testRdfRuleUri2);

        Assert.assertEquals("Rule URIs were not added correctly", 2, testRuleTest1.getRuleUris().size());
    }
    
    /**
     * Test method for {@link org.queryall.api.ruletest.RuleTest#getStages()}.
     */
    @Test
    public void testGetStages()
    {
        testRuleTest1.addStage(NormalisationRuleSchema.getRdfruleStageAfterQueryCreation());
        testRuleTest1.addStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport());
        testRuleTest1.addStage(NormalisationRuleSchema.getRdfruleStageAfterResultsToPool());
        testRuleTest1.addStage(NormalisationRuleSchema.getRdfruleStageQueryVariables());
        
        Assert.assertEquals("getStages test failed", 4, testRuleTest1.getStages().size());
    }
    
    /**
     * Test method for {@link org.queryall.api.ruletest.RuleTest#addRuleUri(org.openrdf.model.URI)}.
     */
    @Test
    public void testAddRuleUri()
    {
        testRuleTest1.addRuleUri(this.testRdfRuleUri1);

        Assert.assertEquals("Rule URIs were not added correctly", 1, testRuleTest1.getRuleUris().size());
    }
    
    /**
     * Test method for {@link org.queryall.api.ruletest.RuleTest#addStage(org.openrdf.model.URI)}.
     */
    @Test
    public void testAddStage()
    {
        testRuleTest1.addStage(NormalisationRuleSchema.getRdfruleStageAfterQueryCreation());
        testRuleTest1.addStage(NormalisationRuleSchema.getRdfruleStageAfterQueryParsing());
        testRuleTest1.addStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport());
        testRuleTest1.addStage(NormalisationRuleSchema.getRdfruleStageAfterResultsToDocument());
        testRuleTest1.addStage(NormalisationRuleSchema.getRdfruleStageAfterResultsToPool());
        testRuleTest1.addStage(NormalisationRuleSchema.getRdfruleStageBeforeResultsImport());
        testRuleTest1.addStage(NormalisationRuleSchema.getRdfruleStageQueryVariables());
        
        Assert.assertEquals("addStage test failed", 7, testRuleTest1.getStages().size());
    }
    
}
