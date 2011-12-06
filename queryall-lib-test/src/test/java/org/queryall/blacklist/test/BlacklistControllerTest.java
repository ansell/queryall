/**
 * 
 */
package org.queryall.blacklist.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.blacklist.BlacklistController;
import org.queryall.blacklist.BlacklistEntry;
import org.queryall.query.RdfFetcherQueryRunnable;
import org.queryall.query.RdfFetcherUriQueryRunnable;
import org.queryall.utils.test.DummySettings;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class BlacklistControllerTest
{
    
    private QueryAllConfiguration testSettings;
    private BlacklistController testBlacklistController;
    private Collection<RdfFetcherQueryRunnable> testTemporaryEndpointBlacklist;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        this.testSettings = new DummySettings();
        this.testBlacklistController = new BlacklistController(this.testSettings);
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        this.testSettings = null;
        this.testBlacklistController = null;
        this.testTemporaryEndpointBlacklist = null;
    }
    
    /**
     * Test method for {@link org.queryall.blacklist.BlacklistController#getDefaultController()}.
     */
    @Test
    public void testGetDefaultController()
    {
        Assert.assertNotNull(BlacklistController.getDefaultController());
    }
    
    /**
     * Test method for {@link org.queryall.blacklist.BlacklistController#accumulateBlacklist(java.util.Collection, long, boolean)}.
     */
    @Test
    public void testAccumulateBlacklist()
    {
        this.testTemporaryEndpointBlacklist = new ArrayList<RdfFetcherQueryRunnable>();
        
        RdfFetcherQueryRunnable fetcherQueryRunnable = new RdfFetcherUriQueryRunnable("http://test.example.org/endpoint/bad/1", "", "", "", testSettings, testBlacklistController, null);
        fetcherQueryRunnable.setLastException(new Exception());
        fetcherQueryRunnable.setCompleted(true);
        
        // check that setting an exception and the completed flag identifies this runnable as being in error
        Assert.assertTrue(fetcherQueryRunnable.wasError());
        
        this.testTemporaryEndpointBlacklist.add(fetcherQueryRunnable);
        
        // then perform the actual accumulateBlacklist operation that we are testing here
        this.testBlacklistController.accumulateBlacklist(testTemporaryEndpointBlacklist);
        
        Map<String, BlacklistEntry> statistics = this.testBlacklistController.getAccumulatedBlacklistStatistics();
        
        Assert.assertNotNull(statistics);
        
        Assert.assertEquals(1, statistics.size());
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", statistics.keySet().toArray()[0]);
        
        BlacklistEntry blacklistEntry = statistics.get("http://test.example.org/endpoint/bad/1");
        
        Assert.assertNotNull(blacklistEntry);
        
        Assert.assertEquals(1, blacklistEntry.numberOfFailures);
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", blacklistEntry.endpointUrl);
    }
    
    /**
     * Test method for {@link org.queryall.blacklist.BlacklistController#accumulateHttpResponseError(java.lang.String, int)}.
     */
    @Test
    public void testAccumulateHttpResponseError()
    {
        Assert.assertEquals(0, this.testBlacklistController.getAllHttpErrorResponseCodesByServer().size());
        
        this.testBlacklistController.accumulateHttpResponseError("http://example.org/test/endpoint/bad/2", 403);
        
        Assert.assertEquals(1, this.testBlacklistController.getAllHttpErrorResponseCodesByServer().size());

        Assert.assertTrue(this.testBlacklistController.getAllHttpErrorResponseCodesByServer().containsKey("http://example.org/test/endpoint/bad/2"));
    
        Map<Integer, Integer> map = this.testBlacklistController.getAllHttpErrorResponseCodesByServer().get("http://example.org/test/endpoint/bad/2");
        
        Assert.assertNotNull(map);
        
        Assert.assertEquals(1, map.size());
        
        Assert.assertTrue(map.containsKey(new Integer(403)));
        
        Assert.assertEquals(new Integer(1), map.get(new Integer(403)));
    }
    
    /**
     * Test method for {@link org.queryall.blacklist.BlacklistController#accumulateQueryDebug(org.queryall.query.QueryDebug, org.queryall.api.base.QueryAllConfiguration, long, boolean, boolean, int, int)}.
     */
    @Ignore
    @Test
    public void testAccumulateQueryDebug()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.blacklist.BlacklistController#accumulateQueryTotal(java.lang.String)}.
     */
    @Ignore
    @Test
    public void testAccumulateQueryTotal()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.blacklist.BlacklistController#clearStatisticsUploadList()}.
     */
    @Ignore
    @Test
    public void testClearStatisticsUploadList()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.blacklist.BlacklistController#doBlacklistExpiry()}.
     */
    @Ignore
    @Test
    public void testDoBlacklistExpiry()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.blacklist.BlacklistController#evaluateClientBlacklist(org.queryall.api.base.QueryAllConfiguration, boolean, int, float, int)}.
     */
    @Ignore
    @Test
    public void testEvaluateClientBlacklist()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.blacklist.BlacklistController#getAlternativeUrl(java.lang.String, java.util.List)}.
     */
    @Ignore
    @Test
    public void testGetAlternativeUrl()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.blacklist.BlacklistController#getCurrentDebugInformationFor(java.lang.String)}.
     */
    @Ignore
    @Test
    public void testGetCurrentDebugInformationFor()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.blacklist.BlacklistController#getCurrentIPBlacklist()}.
     */
    @Ignore
    @Test
    public void testGetCurrentIPBlacklist()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.blacklist.BlacklistController#getCurrentIPWhitelist()}.
     */
    @Ignore
    @Test
    public void testGetCurrentIPWhitelist()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.blacklist.BlacklistController#getEndpointUrlsInBlacklist(long, boolean)}.
     */
    @Ignore
    @Test
    public void testGetEndpointUrlsInBlacklist()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.blacklist.BlacklistController#getPermanentIPBlacklist()}.
     */
    @Ignore
    @Test
    public void testGetPermanentIPBlacklist()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.blacklist.BlacklistController#isClientBlacklisted(java.lang.String)}.
     */
    @Ignore
    @Test
    public void testIsClientBlacklisted()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.blacklist.BlacklistController#isClientPermanentlyBlacklisted(java.lang.String)}.
     */
    @Ignore
    @Test
    public void testIsClientPermanentlyBlacklisted()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.blacklist.BlacklistController#isClientWhitelisted(java.lang.String)}.
     */
    @Ignore
    @Test
    public void testIsClientWhitelisted()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.blacklist.BlacklistController#isEndpointBlacklisted(java.lang.String)}.
     */
    @Ignore
    @Test
    public void testIsEndpointBlacklisted()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.blacklist.BlacklistController#isUrlBlacklisted(java.lang.String)}.
     */
    @Ignore
    @Test
    public void testIsUrlBlacklisted()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.blacklist.BlacklistController#persistStatistics(java.util.Collection, int)}.
     */
    @Ignore
    @Test
    public void testPersistStatistics()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.blacklist.BlacklistController#removeEndpointsFromBlacklist(java.util.Collection, long, boolean)}.
     */
    @Ignore
    @Test
    public void testRemoveEndpointsFromBlacklist()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
}
