/**
 * 
 */
package org.queryall.query.test;

import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.blacklist.BlacklistController;
import org.queryall.exception.QueryAllException;
import org.queryall.query.QueryBundle;
import org.queryall.query.RdfFetchController;
import org.queryall.utils.Settings;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RdfFetchControllerTest
{
    
    private RdfFetchController testController;
    private QueryAllConfiguration testSettings;
    private BlacklistController testBlacklistController;
    private LinkedList<QueryBundle> testQueryBundles1;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        this.testController = new RdfFetchController();
        this.testSettings = new Settings();
        this.testBlacklistController = new BlacklistController(this.testSettings);
        
        this.testQueryBundles1 = new LinkedList<QueryBundle>();
        this.testQueryBundles1.add(new QueryBundle());
        this.testQueryBundles1.add(new QueryBundle());
        this.testQueryBundles1.add(new QueryBundle());
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        this.testController = null;
        this.testSettings = null;
        this.testBlacklistController = null;
        this.testQueryBundles1 = null;
    }
    
    /**
     * Test method for {@link org.queryall.query.RdfFetchController#anyNamespaceNotRecognised()}.
     */
    @Ignore
    @Test
    public final void testAnyNamespaceNotRecognised()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.query.RdfFetchController#fetchRdfForQueries()}.
     */
    @Ignore
    @Test
    public final void testFetchRdfForQueries()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.RdfFetchController#fetchRdfForQueries(java.util.Collection)}.
     */
    @Ignore
    @Test
    public final void testFetchRdfForQueriesCollectionOfRdfFetcherQueryRunnable()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.query.RdfFetchController#getAllUsedProviders()}.
     */
    @Ignore
    @Test
    public final void testGetAllUsedProviders()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.query.RdfFetchController#getErrorResults()}.
     */
    @Ignore
    @Test
    public final void testGetErrorResults()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.query.RdfFetchController#getFetchThreadGroup()}.
     */
    @Ignore
    @Test
    public final void testGetFetchThreadGroup()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.query.RdfFetchController#getQueryBundles()}.
     */
    @Ignore
    @Test
    public final void testGetQueryBundles()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.query.RdfFetchController#getResults()}.
     */
    @Ignore
    @Test
    public final void testGetResults()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.query.RdfFetchController#getSuccessfulResults()}.
     */
    @Ignore
    @Test
    public final void testGetSuccessfulResults()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.query.RdfFetchController#getUncalledThreads()}.
     */
    @Ignore
    @Test
    public final void testGetUncalledThreads()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.query.RdfFetchController#queryKnown()}.
     */
    @Ignore
    @Test
    public final void testQueryKnown()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.query.RdfFetchController#RdfFetchController()}.
     * 
     * Tests the initial setup from the default constructor
     */
    @Test
    public final void testRdfFetchController()
    {
        Assert.assertNotNull(this.testController);
        
        Assert.assertNull(this.testController.getSettings());
        Assert.assertNull(this.testController.getBlacklistController());
        
        this.testController.setSettings(this.testSettings);
        Assert.assertNotNull(this.testController.getSettings());
        
        this.testController.setBlacklistController(this.testBlacklistController);
        Assert.assertNotNull(this.testController.getBlacklistController());
        
        Assert.assertEquals(this.testSettings, this.testController.getSettings());
        Assert.assertEquals(this.testBlacklistController, this.testController.getBlacklistController());
        
        Assert.assertNotNull(this.testController.getQueryBundles());
        Assert.assertEquals(0, this.testController.getQueryBundles().size());
        
        Assert.assertFalse(this.testController.queryKnown());
        Assert.assertFalse(this.testController.anyNamespaceNotRecognised());
        Assert.assertNotNull(this.testController.getAllUsedProviders());
        Assert.assertEquals(0, this.testController.getAllUsedProviders().size());
        Assert.assertEquals(0, this.testController.getErrorResults().size());
        Assert.assertEquals(0, this.testController.getFetchThreadGroup().size());
        Assert.assertEquals(0, this.testController.getResults().size());
        Assert.assertEquals(0, this.testController.getSuccessfulResults().size());
        Assert.assertEquals(0, this.testController.getUncalledThreads().size());
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.RdfFetchController#RdfFetchController(org.queryall.api.base.QueryAllConfiguration, org.queryall.blacklist.BlacklistController, java.util.Map, java.util.List, boolean, java.lang.String, int)}
     * .
     * 
     * @throws QueryAllException
     */
    @Test
    public final void testRdfFetchControllerByParameters() throws QueryAllException
    {
        this.testController =
                new RdfFetchController(this.testSettings, this.testBlacklistController, new ArrayList<QueryBundle>());
        
        Assert.assertNotNull(this.testController.getSettings());
        Assert.assertNotNull(this.testController.getBlacklistController());
        
        Assert.assertEquals(this.testSettings, this.testController.getSettings());
        Assert.assertEquals(this.testBlacklistController, this.testController.getBlacklistController());
        
        Assert.assertNotNull(this.testController.getQueryBundles());
        Assert.assertEquals(0, this.testController.getQueryBundles().size());
        
        Assert.assertFalse(this.testController.queryKnown());
        Assert.assertFalse(this.testController.anyNamespaceNotRecognised());
        Assert.assertNotNull(this.testController.getAllUsedProviders());
        Assert.assertEquals(0, this.testController.getAllUsedProviders().size());
        Assert.assertNotNull(this.testController.getErrorResults());
        Assert.assertEquals(0, this.testController.getErrorResults().size());
        Assert.assertNotNull(this.testController.getFetchThreadGroup());
        Assert.assertEquals(0, this.testController.getFetchThreadGroup().size());
        Assert.assertNotNull(this.testController.getResults());
        Assert.assertEquals(0, this.testController.getResults().size());
        Assert.assertNotNull(this.testController.getSuccessfulResults());
        Assert.assertEquals(0, this.testController.getSuccessfulResults().size());
        Assert.assertNotNull(this.testController.getUncalledThreads());
        Assert.assertEquals(0, this.testController.getUncalledThreads().size());
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.RdfFetchController#RdfFetchController(org.queryall.api.base.QueryAllConfiguration, org.queryall.blacklist.BlacklistController, java.util.Collection)}
     * .
     * 
     * Tests the initial setup from the constructor with an empty collection of query bundles
     * 
     * @throws QueryAllException
     *             If the constructor failed to initialise correctly using the given settings,
     *             blacklist controller and query bundles
     */
    @Test
    public final void testRdfFetchControllerEmptyQueryBundles() throws QueryAllException
    {
        this.testController =
                new RdfFetchController(this.testSettings, this.testBlacklistController, new ArrayList<QueryBundle>());
        
        Assert.assertNotNull(this.testController.getSettings());
        Assert.assertNotNull(this.testController.getBlacklistController());
        
        Assert.assertEquals(this.testSettings, this.testController.getSettings());
        Assert.assertEquals(this.testBlacklistController, this.testController.getBlacklistController());
        
        Assert.assertNotNull(this.testController.getQueryBundles());
        Assert.assertEquals(0, this.testController.getQueryBundles().size());
        
        Assert.assertFalse(this.testController.queryKnown());
        Assert.assertFalse(this.testController.anyNamespaceNotRecognised());
        Assert.assertNotNull(this.testController.getAllUsedProviders());
        Assert.assertEquals(0, this.testController.getAllUsedProviders().size());
        Assert.assertNotNull(this.testController.getErrorResults());
        Assert.assertEquals(0, this.testController.getErrorResults().size());
        Assert.assertNotNull(this.testController.getFetchThreadGroup());
        Assert.assertEquals(0, this.testController.getFetchThreadGroup().size());
        Assert.assertNotNull(this.testController.getResults());
        Assert.assertEquals(0, this.testController.getResults().size());
        Assert.assertNotNull(this.testController.getSuccessfulResults());
        Assert.assertEquals(0, this.testController.getSuccessfulResults().size());
        Assert.assertNotNull(this.testController.getUncalledThreads());
        Assert.assertEquals(0, this.testController.getUncalledThreads().size());
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.RdfFetchController#RdfFetchController(org.queryall.api.base.QueryAllConfiguration, org.queryall.blacklist.BlacklistController, java.util.Collection)}
     * .
     * 
     * Tests the initial setup from the constructor with an empty collection of query bundles
     * 
     * @throws QueryAllException
     *             If the constructor failed to initialise correctly using the given settings,
     *             blacklist controller and query bundles
     */
    @Test
    public final void testRdfFetchControllerMultipleNullQueryBundles() throws QueryAllException
    {
        this.testController =
                new RdfFetchController(this.testSettings, this.testBlacklistController, this.testQueryBundles1);
        
        Assert.assertNotNull(this.testController.getSettings());
        Assert.assertNotNull(this.testController.getBlacklistController());
        
        Assert.assertEquals(this.testSettings, this.testController.getSettings());
        Assert.assertEquals(this.testBlacklistController, this.testController.getBlacklistController());
        
        Assert.assertNotNull(this.testController.getQueryBundles());
        Assert.assertEquals(3, this.testController.getQueryBundles().size());
        
        Assert.assertFalse(this.testController.queryKnown());
        Assert.assertFalse(this.testController.anyNamespaceNotRecognised());
        Assert.assertNotNull(this.testController.getAllUsedProviders());
        Assert.assertEquals(0, this.testController.getAllUsedProviders().size());
        Assert.assertNotNull(this.testController.getErrorResults());
        Assert.assertEquals(0, this.testController.getErrorResults().size());
        Assert.assertNotNull(this.testController.getFetchThreadGroup());
        Assert.assertEquals(0, this.testController.getFetchThreadGroup().size());
        Assert.assertNotNull(this.testController.getResults());
        Assert.assertEquals(0, this.testController.getResults().size());
        Assert.assertNotNull(this.testController.getSuccessfulResults());
        Assert.assertEquals(0, this.testController.getSuccessfulResults().size());
        Assert.assertNotNull(this.testController.getUncalledThreads());
        Assert.assertEquals(0, this.testController.getUncalledThreads().size());
        
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.RdfFetchController#setErrorResults(java.util.Collection)}.
     */
    @Ignore
    @Test
    public final void testSetErrorResults()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.RdfFetchController#setFetchThreadGroup(java.util.Collection)}.
     */
    @Ignore
    @Test
    public final void testSetFetchThreadGroup()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.RdfFetchController#setSuccessfulResults(java.util.Collection)}.
     */
    @Ignore
    @Test
    public final void testSetSuccessfulResults()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.RdfFetchController#setUncalledThreads(java.util.Collection)}.
     */
    @Ignore
    @Test
    public final void testSetUncalledThreads()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
}
