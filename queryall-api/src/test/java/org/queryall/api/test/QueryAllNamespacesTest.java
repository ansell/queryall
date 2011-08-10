/**
 * 
 */
package org.queryall.api.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.queryall.api.utils.QueryAllNamespaces;

/**
 * @author uqpanse1
 *
 */
public class QueryAllNamespacesTest
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
     * Test method for {@link org.queryall.api.utils.QueryAllNamespaces#QueryAllNamespaces(java.lang.String, java.lang.String, java.lang.String)}.
     */
    @Test
    public final void testQueryAllNamespaces()
    {
        Assert.assertEquals(12, QueryAllNamespaces.values().length);
    }
    
    /**
     * Test method for {@link org.queryall.api.utils.QueryAllNamespaces#getBaseURI()}.
     */
    @Test
    public final void testGetBaseURI()
    {
        for(QueryAllNamespaces nextNamespace : QueryAllNamespaces.values())
        {
            // the testing queryall.properties file should always contain these definitions, as they are stable and long term
            String expectedNamespace = "http://purl.org/queryall/"+nextNamespace.getNamespace()+":";
            Assert.assertEquals("Base URI was not as expected", expectedNamespace, nextNamespace.getBaseURI());
        }
    }
    
    /**
     * Test method for {@link org.queryall.api.utils.QueryAllNamespaces#getDefaultValue()}.
     */
    @Test
    public final void testGetDefaultValue()
    {
        for(QueryAllNamespaces nextNamespace : QueryAllNamespaces.values())
        {
            // verify that the default values match the testing queryall.properties file that is used via PropertyUtils to generate the actual namespace
            Assert.assertEquals("Namespace did not match the default, as it should for testing purposes", nextNamespace.getNamespace(), nextNamespace.getDefaultValue());
        }
    }
    
    /**
     * Test method for {@link org.queryall.api.utils.QueryAllNamespaces#getDescription()}.
     */
    @Test
    public final void testGetDescription()
    {
        for(QueryAllNamespaces nextNamespace : QueryAllNamespaces.values())
        {
            // verify that the description is not a small trivial string
            assertTrue(nextNamespace.getDescription().trim().length()> 5);
        }
        
    }
    
    /**
     * Test method for {@link org.queryall.api.utils.QueryAllNamespaces#getNamespace()}.
     */
    @Test
    public final void testGetNamespace()
    {
        for(QueryAllNamespaces nextNamespace : QueryAllNamespaces.values())
        {
            // verify that the default values match the testing queryall.properties file that is used via PropertyUtils to generate the actual namespace
            Assert.assertEquals("Default value did not match the actual namespace, as it should for testing purposes", nextNamespace.getDefaultValue(), nextNamespace.getNamespace());
        }
    }
    
}
