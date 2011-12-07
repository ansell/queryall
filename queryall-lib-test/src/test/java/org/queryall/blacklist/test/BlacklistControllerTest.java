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
import org.queryall.query.QueryDebug;
import org.queryall.query.RdfFetcherQueryRunnable;
import org.queryall.query.RdfFetcherUriQueryRunnable;
import org.queryall.utils.test.DummySettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class BlacklistControllerTest
{
    private static final Logger log = LoggerFactory.getLogger(BlacklistControllerTest.class);
    
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
        this.testTemporaryEndpointBlacklist = new ArrayList<RdfFetcherQueryRunnable>();
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
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#accumulateBlacklist(java.util.Collection, long, boolean)}
     * .
     */
    @Test
    public void testAccumulateBlacklist()
    {
        final RdfFetcherQueryRunnable fetcherQueryRunnable =
                new RdfFetcherUriQueryRunnable("http://test.example.org/endpoint/bad/1", "", "", "", this.testSettings,
                        this.testBlacklistController, null);
        fetcherQueryRunnable.setLastException(new Exception());
        fetcherQueryRunnable.setCompleted(true);
        
        // check that setting an exception and the completed flag identifies this runnable as being
        // in error
        Assert.assertTrue(fetcherQueryRunnable.wasError());
        
        this.testTemporaryEndpointBlacklist.add(fetcherQueryRunnable);
        
        // then perform the actual accumulateBlacklist operation that we are testing here
        this.testBlacklistController.accumulateBlacklist(this.testTemporaryEndpointBlacklist);
        
        final Map<String, BlacklistEntry> statistics = this.testBlacklistController.getAccumulatedBlacklistStatistics();
        
        Assert.assertNotNull(statistics);
        
        Assert.assertEquals(1, statistics.size());
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", statistics.keySet().toArray()[0]);
        
        final BlacklistEntry blacklistEntry = statistics.get("http://test.example.org/endpoint/bad/1");
        
        Assert.assertNotNull(blacklistEntry);
        
        Assert.assertEquals(1, blacklistEntry.numberOfFailures);
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", blacklistEntry.endpointUrl);
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#accumulateHttpResponseError(java.lang.String, int)}
     * .
     */
    @Test
    public void testAccumulateHttpResponseError()
    {
        Assert.assertEquals(0, this.testBlacklistController.getAllHttpErrorResponseCodesByServer().size());
        
        this.testBlacklistController.accumulateHttpResponseError("http://example.org/test/endpoint/bad/2", 403);
        
        Assert.assertEquals(1, this.testBlacklistController.getAllHttpErrorResponseCodesByServer().size());
        
        Assert.assertTrue(this.testBlacklistController.getAllHttpErrorResponseCodesByServer().containsKey(
                "http://example.org/test/endpoint/bad/2"));
        
        final Map<Integer, Integer> map =
                this.testBlacklistController.getAllHttpErrorResponseCodesByServer().get(
                        "http://example.org/test/endpoint/bad/2");
        
        Assert.assertNotNull(map);
        
        Assert.assertEquals(1, map.size());
        
        Assert.assertTrue(map.containsKey(new Integer(403)));
        
        Assert.assertEquals(new Integer(1), map.get(new Integer(403)));
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#accumulateQueryDebug(org.queryall.query.QueryDebug)}
     * .
     */
    @Test
    public void testAccumulateQueryDebugDefaultParameters()
    {
        final QueryDebug nextQueryObject = new QueryDebug();
        
        nextQueryObject.setClientIPAddress("127.0.0.1");
        
        this.testBlacklistController.accumulateQueryDebug(nextQueryObject);
        
        final Map<String, Collection<QueryDebug>> debugInformation =
                this.testBlacklistController.getCurrentQueryDebugInformation();
        
        // By default, we assume that client blacklisting is not required, and we don't override
        // this in DummySettings for the "automaticallyBlacklistClients" property so it should be
        // false and hence there should be no accumulation
        Assert.assertEquals(0, debugInformation.size());
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#accumulateQueryDebug(org.queryall.query.QueryDebug, long, boolean, boolean, int, int)}
     * .
     */
    @Test
    public void testAccumulateQueryDebugExplicitParametersFalse()
    {
        final QueryDebug nextQueryObject = new QueryDebug();
        
        this.testBlacklistController.accumulateQueryDebug(nextQueryObject, 0, false, false, 0, 0);
        
        final Map<String, Collection<QueryDebug>> debugInformation =
                this.testBlacklistController.getCurrentQueryDebugInformation();
        
        // the boolean automaticallyBlacklistClients parameter was set to false above, so we expect
        // not to see any debug information
        Assert.assertEquals(0, debugInformation.size());
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#accumulateQueryDebug(org.queryall.query.QueryDebug, long, boolean, boolean, int, int)}
     * .
     */
    @Test
    public void testAccumulateQueryDebugExplicitParametersTrue()
    {
        final QueryDebug nextQueryObject = new QueryDebug();
        
        nextQueryObject.setClientIPAddress("127.0.0.1");
        
        this.testBlacklistController.accumulateQueryDebug(nextQueryObject, 0, false, true, 0, 0);
        
        final Map<String, Collection<QueryDebug>> debugInformation =
                this.testBlacklistController.getCurrentQueryDebugInformation();
        
        // the boolean automaticallyBlacklistClients parameter was set to true above, so we expect
        // to see the query debug object in the results
        Assert.assertEquals(1, debugInformation.size());
        
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#accumulateQueryTotal(java.lang.String)}.
     */
    @Test
    public void testAccumulateQueryTotal()
    {
        Assert.assertEquals(0, this.testBlacklistController.getAllServerQueryTotals().size());
        
        this.testBlacklistController.accumulateQueryTotal("http://example.org/test/query/total/endpoint/1");

        Assert.assertEquals(1, this.testBlacklistController.getAllServerQueryTotals().size());
        
        Assert.assertTrue(this.testBlacklistController.getAllServerQueryTotals().containsKey("http://example.org/test/query/total/endpoint/1"));
        
        Assert.assertNotNull(this.testBlacklistController.getAllServerQueryTotals().get("http://example.org/test/query/total/endpoint/1"));
        
        Assert.assertEquals(new Integer(1), this.testBlacklistController.getAllServerQueryTotals().get("http://example.org/test/query/total/endpoint/1"));
        
        this.testBlacklistController.accumulateQueryTotal("http://example.org/test/query/total/endpoint/1");

        Assert.assertEquals(1, this.testBlacklistController.getAllServerQueryTotals().size());
        
        Assert.assertTrue(this.testBlacklistController.getAllServerQueryTotals().containsKey("http://example.org/test/query/total/endpoint/1"));
        
        Assert.assertNotNull(this.testBlacklistController.getAllServerQueryTotals().get("http://example.org/test/query/total/endpoint/1"));
        
        Assert.assertEquals(new Integer(2), this.testBlacklistController.getAllServerQueryTotals().get("http://example.org/test/query/total/endpoint/1"));
        
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#clearStatisticsUploadList()}.
     */
    @Ignore
    @Test
    public void testClearStatisticsUploadList()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.blacklist.BlacklistController#doBlacklistExpiry()}.
     * @throws InterruptedException If the Thread.sleep call that simulates time between blacklist period start and expiry time is interrupted
     */
    @Test
    public void testDoBlacklistExpiryDefaultParameters() throws InterruptedException
    {
        final RdfFetcherQueryRunnable fetcherQueryRunnable =
                new RdfFetcherUriQueryRunnable("http://test.example.org/endpoint/bad/1", "", "", "", this.testSettings,
                        this.testBlacklistController, null);
        fetcherQueryRunnable.setLastException(new Exception());
        fetcherQueryRunnable.setCompleted(true);
        
        // check that setting an exception and the completed flag identifies this runnable as being
        // in error
        Assert.assertTrue(fetcherQueryRunnable.wasError());
        
        this.testTemporaryEndpointBlacklist.add(fetcherQueryRunnable);
        
        
        // then perform the accumulateBlacklist operation to make the controller ready to test doBlacklistExpiry
        this.testBlacklistController.accumulateBlacklist(this.testTemporaryEndpointBlacklist);
        
        final Map<String, BlacklistEntry> statistics = this.testBlacklistController.getAccumulatedBlacklistStatistics();
        
        Assert.assertNotNull(statistics);
        
        Assert.assertEquals(1, statistics.size());
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", statistics.keySet().toArray()[0]);
        
        final BlacklistEntry blacklistEntry = statistics.get("http://test.example.org/endpoint/bad/1");
        
        Assert.assertNotNull(blacklistEntry);
        
        Assert.assertEquals(1, blacklistEntry.numberOfFailures);
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", blacklistEntry.endpointUrl);
        

        // Blacklist shouldn't expire immediately, so test that it hasn't before waiting for the time period where it should expire
        Assert.assertFalse(this.testBlacklistController.doBlacklistExpiry());


        // Test that the accumulated blacklist statistics were preserved
        final Map<String, BlacklistEntry> noExpiryStatistics = this.testBlacklistController.getAccumulatedBlacklistStatistics();
        
        Assert.assertNotNull(noExpiryStatistics);
        
        Assert.assertEquals(1, noExpiryStatistics.size());

        Assert.assertEquals("http://test.example.org/endpoint/bad/1", noExpiryStatistics.keySet().toArray()[0]);
        
        final BlacklistEntry nonExpiredBlacklistEntry = noExpiryStatistics.get("http://test.example.org/endpoint/bad/1");
        
        Assert.assertNotNull(nonExpiredBlacklistEntry);
        
        Assert.assertEquals(1, nonExpiredBlacklistEntry.numberOfFailures);
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", nonExpiredBlacklistEntry.endpointUrl);
        
        log.info("About to sleep to wait out the default blacklist reset period");
        
        // By default, the period is 60000 milliseconds, so sleep for a little more than that to make sure
        Thread.sleep(70000);
        
        log.info("Finished sleeping, default blacklist reset period should be complete now");

        // Blacklist should expire now
        Assert.assertTrue(this.testBlacklistController.doBlacklistExpiry());

        // Test that the accumulated blacklist statistics were wiped out this time
        final Map<String, BlacklistEntry> afterExpiryStatistics = this.testBlacklistController.getAccumulatedBlacklistStatistics();
        
        Assert.assertNotNull(afterExpiryStatistics);
        
        Assert.assertEquals(0, afterExpiryStatistics.size());
    }
    
    /**
     * Test method for {@link org.queryall.blacklist.BlacklistController#doBlacklistExpiry(long, boolean)}.
     * @throws InterruptedException If the Thread.sleep call that simulates time between blacklist period start and expiry time is interrupted
     */
    @Test
    public void testDoBlacklistExpiryWithParameters() throws InterruptedException
    {
        final RdfFetcherQueryRunnable fetcherQueryRunnable =
                new RdfFetcherUriQueryRunnable("http://test.example.org/endpoint/bad/1", "", "", "", this.testSettings,
                        this.testBlacklistController, null);
        fetcherQueryRunnable.setLastException(new Exception());
        fetcherQueryRunnable.setCompleted(true);
        
        // check that setting an exception and the completed flag identifies this runnable as being
        // in error
        Assert.assertTrue(fetcherQueryRunnable.wasError());
        
        this.testTemporaryEndpointBlacklist.add(fetcherQueryRunnable);
        
        
        // then perform the accumulateBlacklist operation to make the controller ready to test doBlacklistExpiry
        this.testBlacklistController.accumulateBlacklist(this.testTemporaryEndpointBlacklist);
        
        final Map<String, BlacklistEntry> statistics = this.testBlacklistController.getAccumulatedBlacklistStatistics();
        
        Assert.assertNotNull(statistics);
        
        Assert.assertEquals(1, statistics.size());
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", statistics.keySet().toArray()[0]);
        
        final BlacklistEntry blacklistEntry = statistics.get("http://test.example.org/endpoint/bad/1");
        
        Assert.assertNotNull(blacklistEntry);
        
        Assert.assertEquals(1, blacklistEntry.numberOfFailures);
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", blacklistEntry.endpointUrl);
        

        // Blacklist shouldn't expire immediately with these parameters, so test that it hasn't before waiting for the time period where it should expire
        Assert.assertFalse(this.testBlacklistController.doBlacklistExpiry(120000L, true));


        // Test that the accumulated blacklist statistics were preserved
        final Map<String, BlacklistEntry> noExpiryStatistics = this.testBlacklistController.getAccumulatedBlacklistStatistics();
        
        Assert.assertNotNull(noExpiryStatistics);
        
        Assert.assertEquals(1, noExpiryStatistics.size());

        Assert.assertEquals("http://test.example.org/endpoint/bad/1", noExpiryStatistics.keySet().toArray()[0]);
        
        final BlacklistEntry nonExpiredBlacklistEntry = noExpiryStatistics.get("http://test.example.org/endpoint/bad/1");
        
        Assert.assertNotNull(nonExpiredBlacklistEntry);
        
        Assert.assertEquals(1, nonExpiredBlacklistEntry.numberOfFailures);
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", nonExpiredBlacklistEntry.endpointUrl);
        
        log.info("About to sleep to wait out the desired test blacklist reset period");
        
        // The desired period is 6000 milliseconds, so sleep for a little more than that to make sure
        Thread.sleep(7000);
        
        log.info("Finished sleeping, desired test blacklist reset period should be complete now");

        // Blacklist should expire now
        Assert.assertTrue(this.testBlacklistController.doBlacklistExpiry(6000L, true));

        // Test that the accumulated blacklist statistics were wiped out this time
        final Map<String, BlacklistEntry> afterExpiryStatistics = this.testBlacklistController.getAccumulatedBlacklistStatistics();
        
        Assert.assertNotNull(afterExpiryStatistics);
        
        Assert.assertEquals(0, afterExpiryStatistics.size());
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#evaluateClientBlacklist(boolean, int, float, int)}
     * .
     */
    @Test
    public void testEvaluateClientBlacklistFalse()
    {
        // add some query debug information in
        final QueryDebug nextQueryObject = new QueryDebug();
        
        nextQueryObject.setClientIPAddress("127.0.0.1");
        
        this.testBlacklistController.accumulateQueryDebug(nextQueryObject, 0, false, true, 0, 0);
        
        final Map<String, Collection<QueryDebug>> debugInformation =
                this.testBlacklistController.getCurrentQueryDebugInformation();
        
        // the boolean automaticallyBlacklistClients parameter was set to true above, so we expect
        // to see the query debug object in the results
        Assert.assertEquals(1, debugInformation.size());
        
        // Now test that evaluateClientBlacklist with false as the automaticallyBlacklistClients parameter does nothing to the query debug information
        this.testBlacklistController.evaluateClientBlacklist(false, 0, 0, 0);

        final Map<String, Collection<QueryDebug>> afterEvaluateDebugInformation =
                this.testBlacklistController.getCurrentQueryDebugInformation();
        
        // the boolean automaticallyBlacklistClients parameter was set to true above, so we expect
        // to see the query debug object in the results
        Assert.assertEquals(1, afterEvaluateDebugInformation.size());
        
        Assert.assertFalse(this.testBlacklistController.isClientBlacklisted("127.0.0.1"));
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#evaluateClientBlacklist(boolean, int, float, int)}
     * .
     */
    @Test
    public void testEvaluateClientBlacklistTrue()
    {
        // add some query debug information in
        final QueryDebug nextQueryObject = new QueryDebug();
        
        nextQueryObject.setClientIPAddress("127.0.0.1");
        
        this.testBlacklistController.accumulateQueryDebug(nextQueryObject, 0, false, true, 0, 0);
        
        final Map<String, Collection<QueryDebug>> debugInformation =
                this.testBlacklistController.getCurrentQueryDebugInformation();
        
        // the boolean automaticallyBlacklistClients parameter was set to true above, so we expect
        // to see the query debug object in the results
        Assert.assertEquals(1, debugInformation.size());
        
        // Now test that evaluateClientBlacklist with true as the automaticallyBlacklistClients parameter adds the client to the blacklist
        this.testBlacklistController.evaluateClientBlacklist(true, 0, 0, 0);

        final Map<String, Collection<QueryDebug>> afterEvaluateDebugInformation =
                this.testBlacklistController.getCurrentQueryDebugInformation();
        
        // the boolean automaticallyBlacklistClients parameter was set to true above, so we expect
        // to see the query debug object in the results
        Assert.assertEquals(1, afterEvaluateDebugInformation.size());

        Assert.assertTrue(this.testBlacklistController.isClientBlacklisted("127.0.0.1"));
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#getAlternativeUrl(java.lang.String, java.util.List)}
     * .
     */
    @Ignore
    @Test
    public void testGetAlternativeUrl()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#getCurrentDebugInformationFor(java.lang.String)}
     * .
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
     * Test method for {@link org.queryall.blacklist.BlacklistController#getDefaultController()}.
     */
    @Test
    public void testGetDefaultController()
    {
        Assert.assertNotNull(BlacklistController.getDefaultController());
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#getEndpointUrlsInBlacklist(long, boolean)}.
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
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#isClientBlacklisted(java.lang.String)}.
     */
    @Ignore
    @Test
    public void testIsClientBlacklisted()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#isClientPermanentlyBlacklisted(java.lang.String)}
     * .
     */
    @Ignore
    @Test
    public void testIsClientPermanentlyBlacklisted()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#isClientWhitelisted(java.lang.String)}.
     */
    @Ignore
    @Test
    public void testIsClientWhitelisted()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#isEndpointBlacklisted(java.lang.String)}.
     */
    @Ignore
    @Test
    public void testIsEndpointBlacklisted()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#isUrlBlacklisted(java.lang.String)}.
     */
    @Ignore
    @Test
    public void testIsUrlBlacklisted()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#persistStatistics(java.util.Collection, int)}
     * .
     */
    @Ignore
    @Test
    public void testPersistStatistics()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#removeEndpointsFromBlacklist(java.util.Collection, long, boolean)}
     * .
     */
    @Ignore
    @Test
    public void testRemoveEndpointsFromBlacklist()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
}
