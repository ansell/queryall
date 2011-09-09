/**
 * 
 */
package org.queryall.api.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.queryall.api.querytype.RegexInputQueryType;

/**
 * Abstract unit test for InputQueryType API
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractRegexInputQueryTypeTest
{
    private RegexInputQueryType testRegexInputQueryType1;
    private String testRegex;
    private Map<String, String> testQueryParameters;
    
    /**
     * This method must be overridden to return a new instance of the implemented QueryType class
     * for each successive invocation
     * 
     * @return A new instance of the RegexInputQueryType implementation
     */
    public abstract RegexInputQueryType getNewTestRegexInputQueryType();
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
//        final ValueFactory f = new MemValueFactory();
        
        this.testRegexInputQueryType1 = getNewTestRegexInputQueryType();
        
        this.testRegex = "^([\\w-]+):(.+)";

        this.testRegexInputQueryType1.setInputRegex(this.testRegex);
        
        testQueryParameters = new HashMap<String, String>();
        testQueryParameters.put("queryString", "geneid-uniprot:AX99cc");
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        this.testRegexInputQueryType1 = null;
        this.testRegex = null;
        this.testQueryParameters = null;
    }
    
    /**
     * Test method for {@link org.queryall.api.querytype.RegexInputQueryType.getExpectedInputParameters()}.
     */
    @Test
    public void testGet()
    {
        Assert.assertEquals("Regex was not set properly", this.testRegex, this.testRegexInputQueryType1.getInputRegex());
        
        Assert.assertTrue("Regex matches for query string", this.testRegexInputQueryType1.matchesQueryParameters(testQueryParameters));
    }
    
}
