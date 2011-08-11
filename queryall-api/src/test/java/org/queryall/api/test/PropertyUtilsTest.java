/**
 * 
 */
package org.queryall.api.test;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.queryall.api.utils.PropertyUtils;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class PropertyUtilsTest
{
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
    }
    
    /**
     * Tests the getSystemOrPropertyString using the basic ontology URI properties that are very
     * stable and should not change inside queryall tests without notice here
     * 
     * Test method for
     * {@link org.queryall.api.utils.PropertyUtils#getSystemOrPropertyString(java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public final void testGetSystemOrPropertyString()
    {
        Assert.assertEquals("Ontology URI Prefix was not as expected", "http://purl.org/queryall/",
                PropertyUtils.getSystemOrPropertyString("queryall.ontologyPrefix", ""));
        Assert.assertEquals("Ontology URI Prefix was not as expected, even with default", "http://purl.org/queryall/",
                PropertyUtils.getSystemOrPropertyString("queryall.ontologyPrefix", "http://purl.org/queryall/"));
        
        Assert.assertEquals("Ontology URI Suffix was not as expected", ":",
                PropertyUtils.getSystemOrPropertyString("queryall.ontologySuffix", ":"));
        Assert.assertEquals("Ontology URI Suffix was not as expected, even with default", ":",
                PropertyUtils.getSystemOrPropertyString("queryall.ontologySuffix", ":"));
    }
    
    /**
     * Checks that the Property Utils bundle has not been changed without an explicit update here
     */
    @Test
    public final void testPropertyUtilsBundle()
    {
        Assert.assertEquals("Property Utils bundle was not as expected", "queryall",
                PropertyUtils.DEFAULT_PROPERTIES_BUNDLE_NAME);
    }
    
}
