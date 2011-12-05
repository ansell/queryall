/**
 * 
 */
package org.queryall.api.test;

import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.queryall.api.querytype.ProcessorQueryType;
import org.queryall.api.querytype.QueryType;

/**
 * Abstract unit test for ProcessorQueryType API
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractProcessorQueryTypeTest extends AbstractQueryTypeTest
{
    private ProcessorQueryType testQueryType1;
    private Map<String, Object> testQueryVariables;
    
    /**
     * This method must be overridden to return a new instance of the implemented QueryType class
     * for each successive invocation
     * 
     * @return A new instance of the QueryType implementation
     */
    public abstract ProcessorQueryType getNewTestProcessorQueryType();
    
    @Override
    public final QueryType getNewTestQueryType()
    {
        return this.getNewTestProcessorQueryType();
    }
    
    /**
     * This method must be overridden to return a new String for each invocation representing a valid processing template string for this processor query type
     * 
     * @return A new valid processing template string to match this processor query type
     */
    public abstract String getNewTestProcessingTemplateString();
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        
        // final ValueFactory f = new MemValueFactory();
        
        this.testQueryType1 = this.getNewTestProcessorQueryType();
        
    }
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    @After
    public void tearDown() throws Exception
    {
        super.tearDown();
        
        this.testQueryType1 = null;
    }
    
    @Test
    public void testProcessorGetProcessingTemplateString()
    {
        String templateString = getNewTestProcessingTemplateString();
        
        Assert.assertNotNull(templateString);
        
        this.testQueryType1.setProcessingTemplateString(templateString);
        
        Assert.assertNotNull(this.testQueryType1.getProcessingTemplateString());
        
        Assert.assertEquals(templateString, this.testQueryType1.getProcessingTemplateString());
    }
    
    @Test
    public void testProcessorProcessQueryVariables()
    {
        Map<String, Object> processedQueryVariables = this.testQueryType1.processQueryVariables(testQueryVariables);
        
        Assert.assertNotNull(processedQueryVariables);
        
    }

    @Test
    public void testSubstituteQueryVariables()
    {
        String substitutedQuery = this.testQueryType1.substituteQueryVariables(testQueryVariables);
        
        Assert.assertNotNull(substitutedQuery);
        
        Assert.assertTrue(substitutedQuery.trim().length() > 0);
    }

    @Test
    public void testParseProcessorQuery()
    {
        Object parsedQuery = this.testQueryType1.parseProcessorQuery("");
        
        Assert.assertNotNull(parsedQuery);
        
    }


}
