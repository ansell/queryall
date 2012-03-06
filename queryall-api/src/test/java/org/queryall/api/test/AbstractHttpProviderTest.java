/**
 * 
 */
package org.queryall.api.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.queryall.api.provider.HttpProvider;
import org.queryall.api.provider.HttpProviderSchema;
import org.queryall.api.provider.Provider;
import org.queryall.api.provider.ProviderSchema;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public abstract class AbstractHttpProviderTest extends AbstractProviderTest
{
    /**
     * This method must be overridden to return a new instance of the implemented HttpProvider class
     * for each successive invocation.
     */
    public abstract HttpProvider getNewTestHttpProvider();
    
    @Override
    public final Provider getNewTestProvider()
    {
        return this.getNewTestHttpProvider();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.test.AbstractProviderTest#setUp()
     */
    @Override
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.test.AbstractProviderTest#tearDown()
     */
    @Override
    @After
    public void tearDown() throws Exception
    {
        super.tearDown();
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.provider.HttpProvider#addEndpointUrl(java.lang.String)}.
     */
    @Test
    public void testAddEndpointUrl()
    {
        HttpProvider testProvider = getNewTestHttpProvider();
        
        testProvider.addEndpointUrl("http://test.example.org/${input_1}:${input_2}");
        
        Assert.assertNotNull(testProvider.getEndpointUrls());
        Assert.assertFalse(testProvider.getEndpointUrls().isEmpty());
        
        Assert.assertEquals("http://test.example.org/${input_1}:${input_2}", testProvider.getEndpointUrls().iterator()
                .next());
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.provider.HttpProvider#getAcceptHeaderString(java.lang.String)}.
     */
    @Test
    public void testGetAcceptHeaderStringNoSetupDefaultNull()
    {
        HttpProvider testProvider = getNewTestHttpProvider();
        
        String acceptHeaderString = testProvider.getAcceptHeaderString(null);
        Assert.assertNull(acceptHeaderString);
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.provider.HttpProvider#getAcceptHeaderString(java.lang.String)}.
     */
    @Test
    public void testGetAcceptHeaderStringSetupDefaultNull()
    {
        HttpProvider testProvider = getNewTestHttpProvider();
        
        testProvider.setAcceptHeaderString("text/nquads, text/turtle, text/plain");
        
        String acceptHeaderString = testProvider.getAcceptHeaderString(null);
        Assert.assertNotNull(acceptHeaderString);
        Assert.assertEquals("text/nquads, text/turtle, text/plain", acceptHeaderString);
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.provider.HttpProvider#getAcceptHeaderString(java.lang.String)}.
     */
    @Test
    public void testGetAcceptHeaderStringNoSetupDefaultNotNull()
    {
        HttpProvider testProvider = getNewTestHttpProvider();
        
        String acceptHeaderString = testProvider.getAcceptHeaderString("text/nquads, text/turtle");
        Assert.assertNotNull(acceptHeaderString);
        Assert.assertEquals("text/nquads, text/turtle", acceptHeaderString);
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.provider.HttpProvider#getAcceptHeaderString(java.lang.String)}.
     */
    @Test
    public void testGetAcceptHeaderStringSetupDefaultNotNull()
    {
        HttpProvider testProvider = getNewTestHttpProvider();
        
        testProvider.setAcceptHeaderString("text/nquads");
        
        String acceptHeaderString = testProvider.getAcceptHeaderString("text/nquads, text/turtle");
        Assert.assertNotNull(acceptHeaderString);
        Assert.assertEquals("text/nquads", acceptHeaderString);
    }
    
    /**
     * Test method for {@link org.queryall.api.provider.HttpProvider#getEndpointUrls()}.
     */
    @Test
    public void testGetEndpointUrlsSingle()
    {
        HttpProvider testProvider = getNewTestHttpProvider();
        
        testProvider.addEndpointUrl("http://test.example.org/${input_1}:${input_2}");
        
        Assert.assertNotNull(testProvider.getEndpointUrls());
        Assert.assertFalse(testProvider.getEndpointUrls().isEmpty());
        
        Assert.assertEquals("http://test.example.org/${input_1}:${input_2}", testProvider.getEndpointUrls().iterator()
                .next());
    }
    
    /**
     * Test method for {@link org.queryall.api.provider.HttpProvider#getEndpointUrls()}.
     */
    @Test
    public void testGetEndpointUrlsMultiple()
    {
        HttpProvider testProvider = getNewTestHttpProvider();
        
        testProvider.addEndpointUrl("http://test.example.org/${input_1}:${input_2}");
        
        Assert.assertNotNull(testProvider.getEndpointUrls());
        Assert.assertFalse(testProvider.getEndpointUrls().isEmpty());
        Assert.assertEquals(1, testProvider.getEndpointUrls().size());
        Assert.assertTrue(testProvider.getEndpointUrls().contains("http://test.example.org/${input_1}:${input_2}"));
        
        testProvider.addEndpointUrl("http://other.example.org/${input_1}:${input_2}");
        
        Assert.assertNotNull(testProvider.getEndpointUrls());
        Assert.assertFalse(testProvider.getEndpointUrls().isEmpty());
        Assert.assertEquals(2, testProvider.getEndpointUrls().size());
        Assert.assertTrue(testProvider.getEndpointUrls().contains("http://test.example.org/${input_1}:${input_2}"));
        Assert.assertTrue(testProvider.getEndpointUrls().contains("http://other.example.org/${input_1}:${input_2}"));
    }
    
    /**
     * Test method for {@link org.queryall.api.provider.HttpProvider#hasEndpointUrl()}.
     */
    @Test
    public void testHasEndpointUrlNoEndpoints()
    {
        HttpProvider testProvider = getNewTestHttpProvider();
        
        Assert.assertFalse(testProvider.hasEndpointUrl());
    }
    
    /**
     * Test method for {@link org.queryall.api.provider.HttpProvider#hasEndpointUrl()}.
     */
    @Test
    public void testHasEndpointUrlOneEndpoint()
    {
        HttpProvider testProvider = getNewTestHttpProvider();
        
        Assert.assertFalse(testProvider.hasEndpointUrl());
        
        testProvider.addEndpointUrl("http://test.example.org/${input_1}:${input_2}");
        
        Assert.assertTrue(testProvider.hasEndpointUrl());
    }
    
    /**
     * Test method for {@link org.queryall.api.provider.HttpProvider#hasEndpointUrl()}.
     */
    @Test
    public void testHasEndpointUrlMultipleEndpoints()
    {
        HttpProvider testProvider = getNewTestHttpProvider();
        
        Assert.assertFalse(testProvider.hasEndpointUrl());
        
        testProvider.addEndpointUrl("http://test.example.org/${input_1}:${input_2}");
        testProvider.addEndpointUrl("http://other.example.org/${input_1}:${input_2}");
        
        Assert.assertTrue(testProvider.hasEndpointUrl());
    }
    
    /**
     * Test method for {@link org.queryall.api.provider.HttpProvider#isHttpGetUrl()}.
     */
    @Test
    public void testIsHttpGetUrl()
    {
        HttpProvider testProvider = getNewTestHttpProvider();
        
        // verify that the default of no communication is present
        Assert.assertEquals(ProviderSchema.getProviderNoCommunication(), testProvider.getEndpointMethod());
        
        testProvider.setEndpointMethod(HttpProviderSchema.getProviderHttpGetUrl());
        
        Assert.assertEquals(HttpProviderSchema.getProviderHttpGetUrl(), testProvider.getEndpointMethod());
        
        // then perform the test
        Assert.assertTrue(testProvider.isHttpGetUrl());
    }
    
    /**
     * Test method for {@link org.queryall.api.provider.HttpProvider#resetEndpointUrls()}.
     * 
     * Tests that resetEndpointUrls returns true even if the list was already empty.
     */
    @Test
    public void testResetEndpointUrlsEmpty()
    {
        HttpProvider testProvider = getNewTestHttpProvider();
        
        Assert.assertFalse(testProvider.hasEndpointUrl());
        Assert.assertNotNull(testProvider.getEndpointUrls());
        Assert.assertTrue(testProvider.getEndpointUrls().isEmpty());
        
        Assert.assertTrue(testProvider.resetEndpointUrls());

        // Do checks after reset to verify the status quo
        Assert.assertFalse(testProvider.hasEndpointUrl());
        Assert.assertNotNull(testProvider.getEndpointUrls());
        Assert.assertTrue(testProvider.getEndpointUrls().isEmpty());
    }
    
    /**
     * Test method for {@link org.queryall.api.provider.HttpProvider#resetEndpointUrls()}.
     * 
     * Tests that resetEndpointUrls returns true if the list was not empty.
     * 
     * There should be no typical situations where the endpoint URLs are not able to be reset, but
     * it is possible that a class may veto it in the future, in which case will have to add
     * complexity here for that purpose.
     */
    @Test
    public void testResetEndpointUrlsNotEmpty()
    {
        HttpProvider testProvider = getNewTestHttpProvider();
        
        Assert.assertFalse(testProvider.hasEndpointUrl());
        Assert.assertNotNull(testProvider.getEndpointUrls());
        Assert.assertTrue(testProvider.getEndpointUrls().isEmpty());
        
        testProvider.addEndpointUrl("http://test.example.org/${input_1}:${input_2}");
        testProvider.addEndpointUrl("http://other.example.org/${input_1}:${input_2}");
        
        Assert.assertTrue(testProvider.hasEndpointUrl());
        Assert.assertNotNull(testProvider.getEndpointUrls());
        Assert.assertFalse(testProvider.getEndpointUrls().isEmpty());
        Assert.assertEquals(2, testProvider.getEndpointUrls().size());
        
        Assert.assertTrue(testProvider.resetEndpointUrls());

        // Do checks after reset to verify the successful reset
        Assert.assertFalse(testProvider.hasEndpointUrl());
        Assert.assertNotNull(testProvider.getEndpointUrls());
        Assert.assertTrue(testProvider.getEndpointUrls().isEmpty());
    }
}
