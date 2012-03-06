/**
 * 
 */
package org.queryall.blacklist.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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
import org.queryall.query.RdfFetcherQueryRunnableImpl;
import org.queryall.query.RdfFetcherUriQueryRunnableImpl;
import org.queryall.utils.Settings;
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
        this.testSettings = new Settings();
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
        final RdfFetcherQueryRunnableImpl fetcherQueryRunnable =
                new RdfFetcherUriQueryRunnableImpl("http://test.example.org/endpoint/bad/1", "", "", "", this.testSettings,
                        this.testBlacklistController, null);
        
        fetcherQueryRunnable.setActualEndpointUrl("http://test.example.org/endpoint/bad/1");
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
        // this in Settings for the "automaticallyBlacklistClients" property so it should be
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
        
        Assert.assertTrue(this.testBlacklistController.getAllServerQueryTotals().containsKey(
                "http://example.org/test/query/total/endpoint/1"));
        
        Assert.assertNotNull(this.testBlacklistController.getAllServerQueryTotals().get(
                "http://example.org/test/query/total/endpoint/1"));
        
        Assert.assertEquals(
                new Integer(1),
                this.testBlacklistController.getAllServerQueryTotals().get(
                        "http://example.org/test/query/total/endpoint/1"));
        
        this.testBlacklistController.accumulateQueryTotal("http://example.org/test/query/total/endpoint/1");
        
        Assert.assertEquals(1, this.testBlacklistController.getAllServerQueryTotals().size());
        
        Assert.assertTrue(this.testBlacklistController.getAllServerQueryTotals().containsKey(
                "http://example.org/test/query/total/endpoint/1"));
        
        Assert.assertNotNull(this.testBlacklistController.getAllServerQueryTotals().get(
                "http://example.org/test/query/total/endpoint/1"));
        
        Assert.assertEquals(
                new Integer(2),
                this.testBlacklistController.getAllServerQueryTotals().get(
                        "http://example.org/test/query/total/endpoint/1"));
        
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
     * 
     * @throws InterruptedException
     *             If the Thread.sleep call that simulates time between blacklist period start and
     *             expiry time is interrupted
     */
    @Ignore
    @Test
    public void testDoBlacklistExpiryDefaultParameters() throws InterruptedException
    {
        final RdfFetcherQueryRunnableImpl fetcherQueryRunnable =
                new RdfFetcherUriQueryRunnableImpl("http://test.example.org/endpoint/bad/1", "", "", "", this.testSettings,
                        this.testBlacklistController, null);
        fetcherQueryRunnable.setLastException(new Exception());
        fetcherQueryRunnable.setCompleted(true);
        
        // check that setting an exception and the completed flag identifies this runnable as being
        // in error
        Assert.assertTrue(fetcherQueryRunnable.wasError());
        
        this.testTemporaryEndpointBlacklist.add(fetcherQueryRunnable);
        
        // then perform the accumulateBlacklist operation to make the controller ready to test
        // doBlacklistExpiry
        this.testBlacklistController.accumulateBlacklist(this.testTemporaryEndpointBlacklist);
        
        final Map<String, BlacklistEntry> statistics = this.testBlacklistController.getAccumulatedBlacklistStatistics();
        
        Assert.assertNotNull(statistics);
        
        Assert.assertEquals(1, statistics.size());
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", statistics.keySet().toArray()[0]);
        
        final BlacklistEntry blacklistEntry = statistics.get("http://test.example.org/endpoint/bad/1");
        
        Assert.assertNotNull(blacklistEntry);
        
        Assert.assertEquals(1, blacklistEntry.numberOfFailures);
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", blacklistEntry.endpointUrl);
        
        // Blacklist shouldn't expire immediately, so test that it hasn't before waiting for the
        // time period where it should expire
        Assert.assertFalse(this.testBlacklistController.doBlacklistExpiry());
        
        // Test that the accumulated blacklist statistics were preserved
        final Map<String, BlacklistEntry> noExpiryStatistics =
                this.testBlacklistController.getAccumulatedBlacklistStatistics();
        
        Assert.assertNotNull(noExpiryStatistics);
        
        Assert.assertEquals(1, noExpiryStatistics.size());
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", noExpiryStatistics.keySet().toArray()[0]);
        
        final BlacklistEntry nonExpiredBlacklistEntry =
                noExpiryStatistics.get("http://test.example.org/endpoint/bad/1");
        
        Assert.assertNotNull(nonExpiredBlacklistEntry);
        
        Assert.assertEquals(1, nonExpiredBlacklistEntry.numberOfFailures);
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", nonExpiredBlacklistEntry.endpointUrl);
        
        BlacklistControllerTest.log.info("About to sleep to wait out the default blacklist reset period");
        
        // By default, the period is 60000 milliseconds, so sleep for a little more than that to
        // make sure
        Thread.sleep(70000);
        
        BlacklistControllerTest.log.info("Finished sleeping, default blacklist reset period should be complete now");
        
        // Blacklist should expire now
        Assert.assertTrue(this.testBlacklistController.doBlacklistExpiry());
        
        // Test that the accumulated blacklist statistics were wiped out this time
        final Map<String, BlacklistEntry> afterExpiryStatistics =
                this.testBlacklistController.getAccumulatedBlacklistStatistics();
        
        Assert.assertNotNull(afterExpiryStatistics);
        
        Assert.assertEquals(0, afterExpiryStatistics.size());
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#doBlacklistExpiry(long, boolean)}.
     * 
     * @throws InterruptedException
     *             If the Thread.sleep call that simulates time between blacklist period start and
     *             expiry time is interrupted
     */
    @Ignore
    @Test
    public void testDoBlacklistExpiryWithParameters() throws InterruptedException
    {
        final RdfFetcherQueryRunnableImpl fetcherQueryRunnable =
                new RdfFetcherUriQueryRunnableImpl("http://test.example.org/endpoint/bad/1", "", "", "", this.testSettings,
                        this.testBlacklistController, null);
        fetcherQueryRunnable.setLastException(new Exception());
        fetcherQueryRunnable.setCompleted(true);
        
        // check that setting an exception and the completed flag identifies this runnable as being
        // in error
        Assert.assertTrue(fetcherQueryRunnable.wasError());
        
        this.testTemporaryEndpointBlacklist.add(fetcherQueryRunnable);
        
        // then perform the accumulateBlacklist operation to make the controller ready to test
        // doBlacklistExpiry
        this.testBlacklistController.accumulateBlacklist(this.testTemporaryEndpointBlacklist);
        
        final Map<String, BlacklistEntry> statistics = this.testBlacklistController.getAccumulatedBlacklistStatistics();
        
        Assert.assertNotNull(statistics);
        
        Assert.assertEquals(1, statistics.size());
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", statistics.keySet().toArray()[0]);
        
        final BlacklistEntry blacklistEntry = statistics.get("http://test.example.org/endpoint/bad/1");
        
        Assert.assertNotNull(blacklistEntry);
        
        Assert.assertEquals(1, blacklistEntry.numberOfFailures);
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", blacklistEntry.endpointUrl);
        
        // Blacklist shouldn't expire immediately with these parameters, so test that it hasn't
        // before waiting for the time period where it should expire
        Assert.assertFalse(this.testBlacklistController.doBlacklistExpiry(120000L, true));
        
        // Test that the accumulated blacklist statistics were preserved
        final Map<String, BlacklistEntry> noExpiryStatistics =
                this.testBlacklistController.getAccumulatedBlacklistStatistics();
        
        Assert.assertNotNull(noExpiryStatistics);
        
        Assert.assertEquals(1, noExpiryStatistics.size());
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", noExpiryStatistics.keySet().toArray()[0]);
        
        final BlacklistEntry nonExpiredBlacklistEntry =
                noExpiryStatistics.get("http://test.example.org/endpoint/bad/1");
        
        Assert.assertNotNull(nonExpiredBlacklistEntry);
        
        Assert.assertEquals(1, nonExpiredBlacklistEntry.numberOfFailures);
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", nonExpiredBlacklistEntry.endpointUrl);
        
        BlacklistControllerTest.log.info("About to sleep to wait out the desired test blacklist reset period");
        
        // The desired period is 6000 milliseconds, so sleep for a little more than that to make
        // sure
        Thread.sleep(7000);
        
        BlacklistControllerTest.log
                .info("Finished sleeping, desired test blacklist reset period should be complete now");
        
        // Blacklist should expire now
        Assert.assertTrue(this.testBlacklistController.doBlacklistExpiry(6000L, true));
        
        // Test that the accumulated blacklist statistics were wiped out this time
        final Map<String, BlacklistEntry> afterExpiryStatistics =
                this.testBlacklistController.getAccumulatedBlacklistStatistics();
        
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
        
        this.testBlacklistController.accumulateQueryDebug(nextQueryObject, 0, false, false, 50, 100);
        
        final Map<String, Collection<QueryDebug>> debugInformation =
                this.testBlacklistController.getCurrentQueryDebugInformation();
        
        // the boolean automaticallyBlacklistClients parameter was set to false above, so we don't
        // expect to see the debug information retained at this point
        Assert.assertEquals(0, debugInformation.size());
        
        // Now test that evaluateClientBlacklist with false as the automaticallyBlacklistClients
        // parameter does nothing to the query debug information
        this.testBlacklistController.evaluateClientBlacklist(false, 0, 0, 0);
        
        final Map<String, Collection<QueryDebug>> afterEvaluateDebugInformation =
                this.testBlacklistController.getCurrentQueryDebugInformation();
        
        // the boolean automaticallyBlacklistClients parameter was set to false above, so we don't
        // expect to see the debug information retained at this point
        Assert.assertEquals(0, afterEvaluateDebugInformation.size());
        
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
        
        // aggressively put the client on the blacklist for making a single query (ie, 1 as the
        // blacklistClientMaxQueriesPerPeriod parameter)
        this.testBlacklistController.accumulateQueryDebug(nextQueryObject, 0, false, true, 0, 1);
        
        final Map<String, Collection<QueryDebug>> debugInformation =
                this.testBlacklistController.getCurrentQueryDebugInformation();
        
        // the boolean automaticallyBlacklistClients parameter was set to true above, so we expect
        // to see the query debug object in the results
        Assert.assertEquals(1, debugInformation.size());
        
        // Now test that evaluateClientBlacklist with true as the automaticallyBlacklistClients
        // parameter adds the client to the blacklist
        this.testBlacklistController.evaluateClientBlacklist(true, 0, 0, 1);
        
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
    @Test
    public void testGetAlternativeUrlNoBlacklistedUrlsMultiple()
    {
        final List<String> testList = new ArrayList<String>(2);
        testList.add("http://test.example.org/endpoint/good/1");
        testList.add("http://test.example.org/endpoint/bad/1");
        
        for(int i = 0; i < 1000; i++)
        {
            Assert.assertEquals("Failure on attempt i=" + i, "http://test.example.org/endpoint/good/1",
                    this.testBlacklistController.getAlternativeUrl("http://test.example.org/endpoint/bad/1", testList));
        }
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#getAlternativeUrl(java.lang.String, java.util.List)}
     * .
     */
    @Test
    public void testGetAlternativeUrlNoBlacklistedUrlsSingle()
    {
        final List<String> testList = new ArrayList<String>(1);
        testList.add("http://test.example.org/endpoint/good/1");
        
        for(int i = 0; i < 1000; i++)
        {
            Assert.assertEquals("http://test.example.org/endpoint/good/1",
                    this.testBlacklistController.getAlternativeUrl("http://test.example.org/endpoint/bad/1", testList));
        }
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#getAlternativeUrl(java.lang.String, java.util.List)}
     * .
     */
    @Test
    public void testGetAlternativeUrlWithMultipleBlacklistedUrls()
    {
        final RdfFetcherQueryRunnableImpl fetcherQueryRunnable =
                new RdfFetcherUriQueryRunnableImpl("http://test.example.org/endpoint/bad/1", "", "", "", this.testSettings,
                        this.testBlacklistController, null);
        
        fetcherQueryRunnable.setActualEndpointUrl("http://test.example.org/endpoint/bad/1");
        fetcherQueryRunnable.setLastException(new Exception());
        fetcherQueryRunnable.setCompleted(true);
        
        // check that setting an exception and the completed flag identifies this runnable as being
        // in error
        Assert.assertTrue(fetcherQueryRunnable.wasError());
        
        this.testTemporaryEndpointBlacklist.add(fetcherQueryRunnable);
        
        // then perform the accumulateBlacklist operation to make the controller ready to test
        // doBlacklistExpiry
        this.testBlacklistController.accumulateBlacklist(this.testTemporaryEndpointBlacklist);
        
        final Map<String, BlacklistEntry> statistics = this.testBlacklistController.getAccumulatedBlacklistStatistics();
        
        Assert.assertNotNull(statistics);
        
        Assert.assertEquals(1, statistics.size());
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", statistics.keySet().toArray()[0]);
        
        final BlacklistEntry blacklistEntry = statistics.get("http://test.example.org/endpoint/bad/1");
        
        Assert.assertNotNull(blacklistEntry);
        
        Assert.assertEquals(1, blacklistEntry.numberOfFailures);
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", blacklistEntry.endpointUrl);
        
        Assert.assertNotNull(this.testBlacklistController.getAlternativeUrl("http://test.example.org/endpoint/bad/1",
                Arrays.asList("http://test.example.org/endpoint/bad/1", "http://test.example.org/endpoint/good/1")));
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#getAlternativeUrl(java.lang.String, java.util.List)}
     * .
     */
    @Test
    public void testGetAlternativeUrlWithSingleBlacklistedUrl()
    {
        final RdfFetcherQueryRunnableImpl fetcherQueryRunnable =
                new RdfFetcherUriQueryRunnableImpl("http://test.example.org/endpoint/bad/1", "", "", "", this.testSettings,
                        this.testBlacklistController, null);
        
        fetcherQueryRunnable.setActualEndpointUrl("http://test.example.org/endpoint/bad/1");
        fetcherQueryRunnable.setLastException(new Exception());
        fetcherQueryRunnable.setCompleted(true);
        
        // check that setting an exception and the completed flag identifies this runnable as being
        // in error
        Assert.assertTrue(fetcherQueryRunnable.wasError());
        
        this.testTemporaryEndpointBlacklist.add(fetcherQueryRunnable);
        
        // then perform the accumulateBlacklist operation to make the controller ready to test
        // doBlacklistExpiry
        this.testBlacklistController.accumulateBlacklist(this.testTemporaryEndpointBlacklist);
        
        final Map<String, BlacklistEntry> statistics = this.testBlacklistController.getAccumulatedBlacklistStatistics();
        
        Assert.assertNotNull(statistics);
        
        Assert.assertEquals(1, statistics.size());
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", statistics.keySet().toArray()[0]);
        
        final BlacklistEntry blacklistEntry = statistics.get("http://test.example.org/endpoint/bad/1");
        
        Assert.assertNotNull(blacklistEntry);
        
        Assert.assertEquals(1, blacklistEntry.numberOfFailures);
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", blacklistEntry.endpointUrl);
        
        Assert.assertNull(this.testBlacklistController.getAlternativeUrl("http://test.example.org/endpoint/bad/1",
                Arrays.asList("http://test.example.org/endpoint/bad/1")));
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#getCurrentDebugInformationFor(java.lang.String)}
     * .
     */
    @Test
    public void testGetCurrentDebugInformationFor()
    {
        // add some query debug information in
        final QueryDebug nextQueryObject = new QueryDebug();
        
        nextQueryObject.setClientIPAddress("127.0.0.1");
        
        // aggressively put the client on the blacklist for making a single query (ie, 1 as the
        // blacklistClientMaxQueriesPerPeriod parameter)
        this.testBlacklistController.accumulateQueryDebug(nextQueryObject, 0, false, true, 0, 1);
        
        final Map<String, Collection<QueryDebug>> debugInformation =
                this.testBlacklistController.getCurrentQueryDebugInformation();
        
        // the boolean automaticallyBlacklistClients parameter was set to true above, so we expect
        // to see the query debug object in the results
        Assert.assertEquals(1, debugInformation.size());
        
        Assert.assertTrue(debugInformation.containsKey("127.0.0.1"));
        
        final Collection<QueryDebug> specificDebugInfo =
                this.testBlacklistController.getCurrentDebugInformationFor("127.0.0.1");
        
        Assert.assertNotNull(specificDebugInfo);
        
        Assert.assertEquals(1, specificDebugInfo.size());
        
        final QueryDebug next = specificDebugInfo.iterator().next();
        
        Assert.assertNotNull(next);
        
        Assert.assertEquals("127.0.0.1", next.getClientIPAddress());
    }
    
    /**
     * Test method for {@link org.queryall.blacklist.BlacklistController#getCurrentIPBlacklist()}.
     * 
     * TODO: add another test using a modified Settings that returns a non-empty blacklist
     */
    @Test
    public void testGetCurrentIPBlacklist()
    {
        final Collection<String> currentIPBlacklist = this.testBlacklistController.getCurrentIPBlacklist();
        
        Assert.assertNotNull(currentIPBlacklist);
        
        Assert.assertEquals(0, currentIPBlacklist.size());
    }
    
    /**
     * Test method for {@link org.queryall.blacklist.BlacklistController#getCurrentIPWhitelist()}.
     * 
     * TODO: add another test using a modified Settings that returns a non-empty whitelist
     */
    @Test
    public void testGetCurrentIPWhitelist()
    {
        final Collection<String> currentIPWhitelist = this.testBlacklistController.getCurrentIPWhitelist();
        
        Assert.assertNotNull(currentIPWhitelist);
        
        Assert.assertEquals(0, currentIPWhitelist.size());
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
     * {@link org.queryall.blacklist.BlacklistController#getEndpointUrlsInBlacklist()}.
     */
    @Test
    public void testGetEndpointUrlsInBlacklistDefaultParameters()
    {
        final RdfFetcherQueryRunnableImpl fetcherQueryRunnable =
                new RdfFetcherUriQueryRunnableImpl("http://test.example.org/endpoint/bad/1", "", "", "", this.testSettings,
                        this.testBlacklistController, null);
        
        fetcherQueryRunnable.setActualEndpointUrl("http://test.example.org/endpoint/bad/1");
        fetcherQueryRunnable.setLastException(new Exception());
        fetcherQueryRunnable.setCompleted(true);
        
        // check that setting an exception and the completed flag identifies this runnable as being
        // in error
        Assert.assertTrue(fetcherQueryRunnable.wasError());
        
        this.testTemporaryEndpointBlacklist.add(fetcherQueryRunnable);
        
        // then perform the accumulateBlacklist operation to make the controller ready to test
        // doBlacklistExpiry
        this.testBlacklistController.accumulateBlacklist(this.testTemporaryEndpointBlacklist);
        
        final Map<String, BlacklistEntry> statistics = this.testBlacklistController.getAccumulatedBlacklistStatistics();
        
        Assert.assertNotNull(statistics);
        
        Assert.assertEquals(1, statistics.size());
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", statistics.keySet().toArray()[0]);
        
        final BlacklistEntry blacklistEntry = statistics.get("http://test.example.org/endpoint/bad/1");
        
        Assert.assertNotNull(blacklistEntry);
        
        Assert.assertEquals(1, blacklistEntry.numberOfFailures);
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", blacklistEntry.endpointUrl);
        
        // Test the default parameter version of this method
        final Collection<String> blacklist = this.testBlacklistController.getEndpointUrlsInBlacklist();
        
        Assert.assertNotNull(blacklist);
        
        Assert.assertEquals(1, blacklist.size());
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", blacklist.iterator().next());
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#getEndpointUrlsInBlacklist(long, boolean)}.
     */
    @Test
    public void testGetEndpointUrlsInBlacklistSpecificParameters()
    {
        final RdfFetcherQueryRunnableImpl fetcherQueryRunnable =
                new RdfFetcherUriQueryRunnableImpl("http://test.example.org/endpoint/bad/1", "", "", "", this.testSettings,
                        this.testBlacklistController, null);
        
        fetcherQueryRunnable.setActualEndpointUrl("http://test.example.org/endpoint/bad/1");
        fetcherQueryRunnable.setLastException(new Exception());
        fetcherQueryRunnable.setCompleted(true);
        
        // check that setting an exception and the completed flag identifies this runnable as being
        // in error
        Assert.assertTrue(fetcherQueryRunnable.wasError());
        
        this.testTemporaryEndpointBlacklist.add(fetcherQueryRunnable);
        
        // then perform the accumulateBlacklist operation to make the controller ready to test
        // doBlacklistExpiry
        this.testBlacklistController.accumulateBlacklist(this.testTemporaryEndpointBlacklist);
        
        final Map<String, BlacklistEntry> statistics = this.testBlacklistController.getAccumulatedBlacklistStatistics();
        
        Assert.assertNotNull(statistics);
        
        Assert.assertEquals(1, statistics.size());
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", statistics.keySet().toArray()[0]);
        
        final BlacklistEntry blacklistEntry = statistics.get("http://test.example.org/endpoint/bad/1");
        
        Assert.assertNotNull(blacklistEntry);
        
        Assert.assertEquals(1, blacklistEntry.numberOfFailures);
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", blacklistEntry.endpointUrl);
        
        // Test the explicit parameter version of this method
        final Collection<String> blacklist = this.testBlacklistController.getEndpointUrlsInBlacklist(6000L, true);
        
        Assert.assertNotNull(blacklist);
        
        Assert.assertEquals(1, blacklist.size());
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", blacklist.iterator().next());
    }
    
    /**
     * Test method for {@link org.queryall.blacklist.BlacklistController#getPermanentIPBlacklist()}.
     */
    @Test
    public void testGetPermanentIPBlacklistEmpty()
    {
        final Collection<String> blacklist = this.testBlacklistController.getPermanentIPBlacklist();
        
        Assert.assertNotNull(blacklist);
        
        Assert.assertEquals(0, blacklist.size());
        
    }
    
    /**
     * Test method for {@link org.queryall.blacklist.BlacklistController#getPermanentIPBlacklist()}.
     */
    @Test
    public void testGetPermanentIPBlacklistNonEmpty()
    {
        final Collection<String> blacklist = this.testBlacklistController.getPermanentIPBlacklist();
        
        Assert.assertNotNull(blacklist);
        
        Assert.assertEquals(0, blacklist.size());
        
        // add some query debug information in
        final QueryDebug nextQueryObject = new QueryDebug();
        
        nextQueryObject.setClientIPAddress("127.0.0.1");
        
        // aggressively put the client on the blacklist for making a single query (ie, 1 as the
        // blacklistClientMaxQueriesPerPeriod parameter)
        this.testBlacklistController.accumulateQueryDebug(nextQueryObject, 0, false, true, 0, 1);
        
        final Map<String, Collection<QueryDebug>> debugInformation =
                this.testBlacklistController.getCurrentQueryDebugInformation();
        
        // the boolean automaticallyBlacklistClients parameter was set to true above, so we expect
        // to see the query debug object in the results
        Assert.assertEquals(1, debugInformation.size());
        
        // Now test that evaluateClientBlacklist with true as the automaticallyBlacklistClients
        // parameter adds the client to the permanent blacklist
        this.testBlacklistController.evaluateClientBlacklist(true, 0, 0, 0);
        
        final Map<String, Collection<QueryDebug>> afterEvaluateDebugInformation =
                this.testBlacklistController.getCurrentQueryDebugInformation();
        
        // the boolean automaticallyBlacklistClients parameter was set to true above, so we expect
        // to see the query debug object in the results
        Assert.assertEquals(1, afterEvaluateDebugInformation.size());
        
        Assert.assertTrue(this.testBlacklistController.isClientBlacklisted("127.0.0.1"));
        
        Assert.assertTrue(this.testBlacklistController.isClientPermanentlyBlacklisted("127.0.0.1"));
        
        // Now verify that the permanent IP blacklist contains this item
        final Collection<String> permanentBlacklist = this.testBlacklistController.getPermanentIPBlacklist();
        
        Assert.assertNotNull(permanentBlacklist);
        
        Assert.assertEquals(1, permanentBlacklist.size());
        
        Assert.assertEquals("127.0.0.1", permanentBlacklist.iterator().next());
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#isClientBlacklisted(java.lang.String)}.
     */
    @Test
    public void testIsClientBlacklisted()
    {
        final Collection<String> blacklist = this.testBlacklistController.getPermanentIPBlacklist();
        
        Assert.assertNotNull(blacklist);
        
        Assert.assertEquals(0, blacklist.size());
        
        // add some query debug information in
        final QueryDebug nextQueryObject = new QueryDebug();
        
        nextQueryObject.setClientIPAddress("127.0.0.1");
        
        // aggressively put the client on the blacklist for making a single query (ie, 1 as the
        // blacklistClientMaxQueriesPerPeriod parameter)
        this.testBlacklistController.accumulateQueryDebug(nextQueryObject, 0, false, true, 0, 1);
        
        final Map<String, Collection<QueryDebug>> debugInformation =
                this.testBlacklistController.getCurrentQueryDebugInformation();
        
        // the boolean automaticallyBlacklistClients parameter was set to true above, so we expect
        // to see the query debug object in the results
        Assert.assertEquals(1, debugInformation.size());
        
        // Now test that evaluateClientBlacklist with true as the automaticallyBlacklistClients
        // parameter adds the client to the permanent blacklist
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
     * {@link org.queryall.blacklist.BlacklistController#isClientPermanentlyBlacklisted(java.lang.String)}
     * .
     */
    @Test
    public void testIsClientPermanentlyBlacklisted()
    {
        final Collection<String> blacklist = this.testBlacklistController.getPermanentIPBlacklist();
        
        Assert.assertNotNull(blacklist);
        
        Assert.assertEquals(0, blacklist.size());
        
        // add some query debug information in
        final QueryDebug nextQueryObject = new QueryDebug();
        
        nextQueryObject.setClientIPAddress("127.0.0.1");
        
        // aggressively put the client on the blacklist for making a single query (ie, 1 as the
        // blacklistClientMaxQueriesPerPeriod parameter)
        this.testBlacklistController.accumulateQueryDebug(nextQueryObject, 0, false, true, 0, 1);
        
        final Map<String, Collection<QueryDebug>> debugInformation =
                this.testBlacklistController.getCurrentQueryDebugInformation();
        
        // the boolean automaticallyBlacklistClients parameter was set to true above, so we expect
        // to see the query debug object in the results
        Assert.assertEquals(1, debugInformation.size());
        
        // Now test that evaluateClientBlacklist with true as the automaticallyBlacklistClients
        // parameter adds the client to the permanent blacklist
        this.testBlacklistController.evaluateClientBlacklist(true, 0, 0, 0);
        
        final Map<String, Collection<QueryDebug>> afterEvaluateDebugInformation =
                this.testBlacklistController.getCurrentQueryDebugInformation();
        
        // the boolean automaticallyBlacklistClients parameter was set to true above, so we expect
        // to see the query debug object in the results
        Assert.assertEquals(1, afterEvaluateDebugInformation.size());
        
        Assert.assertTrue(this.testBlacklistController.isClientBlacklisted("127.0.0.1"));
        
        Assert.assertTrue(this.testBlacklistController.isClientPermanentlyBlacklisted("127.0.0.1"));
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#isClientWhitelisted(java.lang.String)}.
     * 
     * TODO: make up a test using a modified Settings that creates a non-empty client whitelist
     */
    @Test
    public void testIsClientWhitelisted()
    {
        Assert.assertFalse(this.testBlacklistController.isClientWhitelisted("127.0.0.1"));
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#isEndpointBlacklisted(java.lang.String)}.
     */
    @Test
    public void testIsEndpointBlacklistedDefaultParametersFalse()
    {
        // add 4 runnables to the list to make sure that it is under the default limit of 5
        for(int i = 0; i < 4; i++)
        {
            final RdfFetcherQueryRunnableImpl fetcherQueryRunnable =
                    new RdfFetcherUriQueryRunnableImpl("http://test.example.org/endpoint/bad/1", "", "", "",
                            this.testSettings, this.testBlacklistController, null);
            
            fetcherQueryRunnable.setActualEndpointUrl("http://test.example.org/endpoint/bad/1");
            fetcherQueryRunnable.setLastException(new Exception());
            fetcherQueryRunnable.setCompleted(true);
            
            // check that setting an exception and the completed flag identifies this runnable as
            // being
            // in error
            Assert.assertTrue(fetcherQueryRunnable.wasError());
            
            this.testTemporaryEndpointBlacklist.add(fetcherQueryRunnable);
        }
        
        // then perform the accumulateBlacklist operation to make the controller ready to test
        // doBlacklistExpiry
        this.testBlacklistController.accumulateBlacklist(this.testTemporaryEndpointBlacklist);
        
        // Test the default parameter version of this method, which assumes 5 request failures
        // needed to blacklist an endpoint
        Assert.assertFalse(this.testBlacklistController.isEndpointBlacklisted("http://test.example.org/endpoint/bad/1"));
        
        final Map<String, BlacklistEntry> statistics = this.testBlacklistController.getAccumulatedBlacklistStatistics();
        
        Assert.assertNotNull(statistics);
        
        Assert.assertEquals(1, statistics.size());
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", statistics.keySet().toArray()[0]);
        
        final BlacklistEntry blacklistEntry = statistics.get("http://test.example.org/endpoint/bad/1");
        
        Assert.assertNotNull(blacklistEntry);
        
        Assert.assertEquals(4, blacklistEntry.numberOfFailures);
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", blacklistEntry.endpointUrl);
        
        final Collection<String> blacklist = this.testBlacklistController.getEndpointUrlsInBlacklist();
        
        Assert.assertNotNull(blacklist);
        
        Assert.assertEquals(1, blacklist.size());
        
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#isEndpointBlacklisted(java.lang.String)}.
     */
    @Test
    public void testIsEndpointBlacklistedDefaultParametersTrue()
    {
        // add 10 runnables to the list to make sure that it goes past the default limit of 5
        for(int i = 0; i < 10; i++)
        {
            final RdfFetcherQueryRunnableImpl fetcherQueryRunnable =
                    new RdfFetcherUriQueryRunnableImpl("http://test.example.org/endpoint/bad/1", "", "", "",
                            this.testSettings, this.testBlacklistController, null);

            fetcherQueryRunnable.setActualEndpointUrl("http://test.example.org/endpoint/bad/1");
            fetcherQueryRunnable.setLastException(new Exception());
            fetcherQueryRunnable.setCompleted(true);
            
            // check that setting an exception and the completed flag identifies this runnable as
            // being
            // in error
            Assert.assertTrue(fetcherQueryRunnable.wasError());
            
            this.testTemporaryEndpointBlacklist.add(fetcherQueryRunnable);
        }
        
        // then perform the accumulateBlacklist operation to make the controller ready to test
        // doBlacklistExpiry
        this.testBlacklistController.accumulateBlacklist(this.testTemporaryEndpointBlacklist);
        
        // Test the default parameter version of this method
        Assert.assertTrue(this.testBlacklistController.isEndpointBlacklisted("http://test.example.org/endpoint/bad/1"));
        
        final Map<String, BlacklistEntry> statistics = this.testBlacklistController.getAccumulatedBlacklistStatistics();
        
        Assert.assertNotNull(statistics);
        
        Assert.assertEquals(1, statistics.size());
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", statistics.keySet().toArray()[0]);
        
        final BlacklistEntry blacklistEntry = statistics.get("http://test.example.org/endpoint/bad/1");
        
        Assert.assertNotNull(blacklistEntry);
        
        Assert.assertEquals(10, blacklistEntry.numberOfFailures);
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", blacklistEntry.endpointUrl);
        
        final Collection<String> blacklist = this.testBlacklistController.getEndpointUrlsInBlacklist();
        
        Assert.assertNotNull(blacklist);
        
        Assert.assertEquals(1, blacklist.size());
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", blacklist.iterator().next());
        
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#isEndpointBlacklisted(java.lang.String,int,long,boolean)}
     * .
     */
    @Test
    public void testIsEndpointBlacklistedSpecificParameters()
    {
        final int blacklistMaxAccumulatedFailures = 1;
        final long blacklistResetPeriodMilliseconds = 6000L;
        final boolean blacklistResetClientBlacklistWithEndpoints = true;
        
        final RdfFetcherQueryRunnableImpl fetcherQueryRunnable =
                new RdfFetcherUriQueryRunnableImpl("http://test.example.org/endpoint/bad/1", "", "", "", this.testSettings,
                        this.testBlacklistController, null);

        fetcherQueryRunnable.setActualEndpointUrl("http://test.example.org/endpoint/bad/1");
        fetcherQueryRunnable.setLastException(new Exception());
        fetcherQueryRunnable.setCompleted(true);
        
        // check that setting an exception and the completed flag identifies this runnable as being
        // in error
        Assert.assertTrue(fetcherQueryRunnable.wasError());
        
        this.testTemporaryEndpointBlacklist.add(fetcherQueryRunnable);
        
        // then perform the accumulateBlacklist operation to make the controller ready to test
        // doBlacklistExpiry
        this.testBlacklistController.accumulateBlacklist(this.testTemporaryEndpointBlacklist);
        
        final Map<String, BlacklistEntry> statistics = this.testBlacklistController.getAccumulatedBlacklistStatistics();
        
        Assert.assertNotNull(statistics);
        
        Assert.assertEquals(1, statistics.size());
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", statistics.keySet().toArray()[0]);
        
        final BlacklistEntry blacklistEntry = statistics.get("http://test.example.org/endpoint/bad/1");
        
        Assert.assertNotNull(blacklistEntry);
        
        Assert.assertEquals(1, blacklistEntry.numberOfFailures);
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", blacklistEntry.endpointUrl);
        
        // Test the default parameter version of this method
        final Collection<String> blacklist =
                this.testBlacklistController.getEndpointUrlsInBlacklist(blacklistResetPeriodMilliseconds,
                        blacklistResetClientBlacklistWithEndpoints);
        
        Assert.assertNotNull(blacklist);
        
        Assert.assertEquals(1, blacklist.size());
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", blacklist.iterator().next());
        
        Assert.assertTrue(this.testBlacklistController.isEndpointBlacklisted("http://test.example.org/endpoint/bad/1",
                blacklistMaxAccumulatedFailures, blacklistResetPeriodMilliseconds,
                blacklistResetClientBlacklistWithEndpoints));
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#isUrlBlacklisted(java.lang.String)}.
     */
    @Test
    public void testIsUrlBlacklistedFullSpecificParameters()
    {
        final int blacklistMaxAccumulatedFailures = 1;
        final long blacklistResetPeriodMilliseconds = 6000L;
        final boolean blacklistResetClientBlacklistWithEndpoints = true;
        
        final RdfFetcherQueryRunnableImpl fetcherQueryRunnable =
                new RdfFetcherUriQueryRunnableImpl("http://test.example.org/endpoint/bad/1", "", "", "", this.testSettings,
                        this.testBlacklistController, null);

        fetcherQueryRunnable.setActualEndpointUrl("http://test.example.org/endpoint/bad/1");
        fetcherQueryRunnable.setLastException(new Exception());
        fetcherQueryRunnable.setCompleted(true);
        
        // check that setting an exception and the completed flag identifies this runnable as being
        // in error
        Assert.assertTrue(fetcherQueryRunnable.wasError());
        
        this.testTemporaryEndpointBlacklist.add(fetcherQueryRunnable);
        
        // then perform the accumulateBlacklist operation to make the controller ready to test
        // doBlacklistExpiry
        this.testBlacklistController.accumulateBlacklist(this.testTemporaryEndpointBlacklist);
        
        final Map<String, BlacklistEntry> statistics = this.testBlacklistController.getAccumulatedBlacklistStatistics();
        
        Assert.assertNotNull(statistics);
        
        Assert.assertEquals(1, statistics.size());
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", statistics.keySet().toArray()[0]);
        
        final BlacklistEntry blacklistEntry = statistics.get("http://test.example.org/endpoint/bad/1");
        
        Assert.assertNotNull(blacklistEntry);
        
        Assert.assertEquals(1, blacklistEntry.numberOfFailures);
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", blacklistEntry.endpointUrl);
        
        // Test the default parameter version of this method
        final Collection<String> blacklist =
                this.testBlacklistController.getEndpointUrlsInBlacklist(blacklistResetPeriodMilliseconds,
                        blacklistResetClientBlacklistWithEndpoints);
        
        Assert.assertNotNull(blacklist);
        
        Assert.assertEquals(1, blacklist.size());
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", blacklist.iterator().next());
        
        Assert.assertTrue(this.testBlacklistController.isUrlBlacklisted("http://test.example.org/endpoint/bad/1",
                blacklistMaxAccumulatedFailures, blacklistResetPeriodMilliseconds,
                blacklistResetClientBlacklistWithEndpoints));
    }
    
    /**
     * Test method for
     * {@link org.queryall.blacklist.BlacklistController#isUrlBlacklisted(java.lang.String)}.
     */
    @Test
    public void testIsUrlBlacklistedPartialSpecificParameters()
    {
        final int blacklistMaxAccumulatedFailures = 1;
        final long blacklistResetPeriodMilliseconds = 6000L;
        final boolean blacklistResetClientBlacklistWithEndpoints = true;
        
        final RdfFetcherQueryRunnableImpl fetcherQueryRunnable =
                new RdfFetcherUriQueryRunnableImpl("http://test.example.org", "", "", "", this.testSettings,
                        this.testBlacklistController, null);

        fetcherQueryRunnable.setActualEndpointUrl("http://test.example.org");
        fetcherQueryRunnable.setLastException(new Exception());
        fetcherQueryRunnable.setCompleted(true);
        
        // check that setting an exception and the completed flag identifies this runnable as being
        // in error
        Assert.assertTrue(fetcherQueryRunnable.wasError());
        
        this.testTemporaryEndpointBlacklist.add(fetcherQueryRunnable);
        
        // then perform the accumulateBlacklist operation to make the controller ready to test
        // doBlacklistExpiry
        this.testBlacklistController.accumulateBlacklist(this.testTemporaryEndpointBlacklist);
        
        final Map<String, BlacklistEntry> statistics = this.testBlacklistController.getAccumulatedBlacklistStatistics();
        
        Assert.assertNotNull(statistics);
        
        Assert.assertEquals(1, statistics.size());
        
        Assert.assertEquals("http://test.example.org", statistics.keySet().toArray()[0]);
        
        final BlacklistEntry blacklistEntry = statistics.get("http://test.example.org");
        
        Assert.assertNotNull(blacklistEntry);
        
        Assert.assertEquals(1, blacklistEntry.numberOfFailures);
        
        Assert.assertEquals("http://test.example.org", blacklistEntry.endpointUrl);
        
        // Test the default parameter version of this method
        final Collection<String> blacklist =
                this.testBlacklistController.getEndpointUrlsInBlacklist(blacklistResetPeriodMilliseconds,
                        blacklistResetClientBlacklistWithEndpoints);
        
        Assert.assertNotNull(blacklist);
        
        Assert.assertEquals(1, blacklist.size());
        
        Assert.assertEquals("http://test.example.org", blacklist.iterator().next());
        
        // test a full URL after having blacklisted the host
        Assert.assertTrue(this.testBlacklistController.isUrlBlacklisted("http://test.example.org/endpoint/bad/1",
                blacklistMaxAccumulatedFailures, blacklistResetPeriodMilliseconds,
                blacklistResetClientBlacklistWithEndpoints));
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
    @Test
    public void testRemoveEndpointsFromBlacklist()
    {
        final RdfFetcherQueryRunnableImpl fetcherQueryRunnable =
                new RdfFetcherUriQueryRunnableImpl("http://test.example.org/endpoint/bad/1", "", "", "", this.testSettings,
                        this.testBlacklistController, null);
        
        fetcherQueryRunnable.setActualEndpointUrl("http://test.example.org/endpoint/bad/1");
        fetcherQueryRunnable.setLastException(new Exception());
        fetcherQueryRunnable.setCompleted(true);
        
        // check that setting an exception and the completed flag identifies this runnable as being
        // in error
        Assert.assertTrue(fetcherQueryRunnable.wasError());
        
        this.testTemporaryEndpointBlacklist.add(fetcherQueryRunnable);
        
        // then perform the accumulateBlacklist operation to make the controller ready to test
        // doBlacklistExpiry
        this.testBlacklistController.accumulateBlacklist(this.testTemporaryEndpointBlacklist);
        
        final Map<String, BlacklistEntry> statistics = this.testBlacklistController.getAccumulatedBlacklistStatistics();
        
        Assert.assertNotNull(statistics);
        
        Assert.assertEquals(1, statistics.size());
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", statistics.keySet().toArray()[0]);
        
        final BlacklistEntry blacklistEntry = statistics.get("http://test.example.org/endpoint/bad/1");
        
        Assert.assertNotNull(blacklistEntry);
        
        Assert.assertEquals(1, blacklistEntry.numberOfFailures);
        
        Assert.assertEquals("http://test.example.org/endpoint/bad/1", blacklistEntry.endpointUrl);
        
        // after verifying it is on the blacklist, remove it
        this.testBlacklistController.removeEndpointsFromBlacklist(this.testTemporaryEndpointBlacklist);
        
        // then run two checks to make sure it was removed
        final Map<String, BlacklistEntry> afterRemovalStatistics =
                this.testBlacklistController.getAccumulatedBlacklistStatistics();
        
        Assert.assertNotNull(afterRemovalStatistics);
        
        Assert.assertEquals(0, afterRemovalStatistics.size());
        
        Assert.assertFalse(this.testBlacklistController.isUrlBlacklisted("http://test.example.org/endpoint/bad/1"));
    }
    
}
