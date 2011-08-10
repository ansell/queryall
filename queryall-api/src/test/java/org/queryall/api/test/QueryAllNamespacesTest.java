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
        fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.api.utils.QueryAllNamespaces#getDescription()}.
     */
    @Test
    public final void testGetDescription()
    {
        fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.api.utils.QueryAllNamespaces#getNamespace()}.
     */
    @Test
    public final void testGetNamespace()
    {
        fail("Not yet implemented"); // TODO
    }
    
}
