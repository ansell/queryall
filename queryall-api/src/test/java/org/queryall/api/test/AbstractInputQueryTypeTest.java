/**
 * 
 */
package org.queryall.api.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.queryall.api.querytype.InputQueryType;
import org.queryall.api.querytype.QueryType;

/**
 * Abstract unit test for InputQueryType API
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractInputQueryTypeTest extends AbstractQueryTypeTest
{
    private InputQueryType testQueryType1;
    
    /**
     * This method must be overridden to return a new instance of the implemented QueryType class
     * for each successive invocation
     * 
     * @return A new instance of the QueryType implementation
     */
    public abstract InputQueryType getNewTestInputQueryType();
    
    @Override
    public final QueryType getNewTestQueryType()
    {
        return this.getNewTestInputQueryType();
    }
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        
        // final ValueFactory f = new MemValueFactory();
        
        this.testQueryType1 = this.getNewTestInputQueryType();
        
        this.testQueryType1.addExpectedInputParameter("input_1");
        this.testQueryType1.addExpectedInputParameter("input_2");
        this.testQueryType1.addExpectedInputParameter("namespace");
        this.testQueryType1.addExpectedInputParameter("identifier");
    }
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    @After
    public void tearDown() throws Exception
    {
        this.testQueryType1 = null;
    }
    
    /**
     * Test method for {@link
     * org.queryall.api.querytype.InputQueryType.getExpectedInputParameters()}.
     */
    @Test
    public void testGetExpectedInputParameters()
    {
        Assert.assertEquals("Did not find all of the expected input parameters", 4, this.testQueryType1
                .getExpectedInputParameters().size());
    }
    
}
