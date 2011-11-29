/**
 * 
 */
package org.queryall.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.model.URI;
import org.queryall.api.provider.Provider;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.api.ruletest.StringRuleTest;
import org.queryall.api.test.DummyNormalisationRule;
import org.queryall.api.test.DummyProvider;
import org.queryall.api.utils.SortOrder;
import org.queryall.exception.QueryAllException;
import org.queryall.impl.ruletest.StringRuleTestImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class RuleUtilsTest
{
    private static final Logger log = LoggerFactory.getLogger(RuleUtilsTest.class);
    
    private NormalisationRule testRuleHighestOrder600;
    private NormalisationRule testRuleHighOrder200;
    private NormalisationRule testRuleMidOrder50;
    private NormalisationRule testRuleLowOrder15;
    private NormalisationRule testRuleLowestOrder3;
    
    private Map<URI, NormalisationRule> testAllNormalisationRules;
    private Collection<URI> testRulesNeededAll;
    
    private Provider testProviderNoRules;
    private Collection<Provider> testProvidersNoRules;
    
    private Provider testProviderAllRulesRandomInsertion;
    private Collection<Provider> testProvidersAllRulesRandomInsertion;
    
    private Collection<RuleTest> testRuleTests;
    
    private StringRuleTest testStringRuleTestQueryVariables;
    
    private StringRuleTest testStringRuleTestBeforeResultsImport;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        this.testRuleLowestOrder3 = new DummyNormalisationRule();
        this.testRuleLowestOrder3.setKey("http://test.ruleutils.example.com/test/rule/order/1");
        this.testRuleLowestOrder3.setOrder(3);
        
        this.testRuleLowOrder15 = new DummyNormalisationRule();
        this.testRuleLowOrder15.setKey("http://test.ruleutils.example.com/test/rule/order/10");
        this.testRuleLowOrder15.setOrder(15);
        
        this.testRuleMidOrder50 = new DummyNormalisationRule();
        this.testRuleMidOrder50.setKey("http://test.ruleutils.example.com/test/rule/order/50");
        this.testRuleMidOrder50.setOrder(50);
        
        this.testRuleHighOrder200 = new DummyNormalisationRule();
        this.testRuleHighOrder200.setKey("http://test.ruleutils.example.com/test/rule/order/200");
        this.testRuleHighOrder200.setOrder(200);
        
        this.testRuleHighestOrder600 = new DummyNormalisationRule();
        this.testRuleHighestOrder600.setKey("http://test.ruleutils.example.com/test/rule/order/500");
        this.testRuleHighestOrder600.setOrder(600);
        
        this.testAllNormalisationRules = new ConcurrentHashMap<URI, NormalisationRule>();
        this.testAllNormalisationRules.put(this.testRuleLowestOrder3.getKey(), this.testRuleLowestOrder3);
        this.testAllNormalisationRules.put(this.testRuleLowOrder15.getKey(), this.testRuleLowOrder15);
        this.testAllNormalisationRules.put(this.testRuleMidOrder50.getKey(), this.testRuleMidOrder50);
        this.testAllNormalisationRules.put(this.testRuleHighOrder200.getKey(), this.testRuleHighOrder200);
        this.testAllNormalisationRules.put(this.testRuleHighestOrder600.getKey(), this.testRuleHighestOrder600);
        
        this.testRulesNeededAll = new ArrayList<URI>();
        this.testRulesNeededAll.add(this.testRuleLowestOrder3.getKey());
        this.testRulesNeededAll.add(this.testRuleLowOrder15.getKey());
        this.testRulesNeededAll.add(this.testRuleMidOrder50.getKey());
        this.testRulesNeededAll.add(this.testRuleHighOrder200.getKey());
        this.testRulesNeededAll.add(this.testRuleHighestOrder600.getKey());
        
        this.testProviderNoRules = new DummyProvider();
        this.testProviderNoRules.setKey("http://test.ruleutils.example.com/test/provider/norules");
        
        this.testProvidersNoRules = new ArrayList<Provider>();
        this.testProvidersNoRules.add(this.testProviderNoRules);
        
        this.testProviderAllRulesRandomInsertion = new DummyProvider();
        this.testProviderAllRulesRandomInsertion
                .setKey("http://test.ruleutils.example.com/test/provider/allrules/randominsertion");
        this.testProviderAllRulesRandomInsertion.addNormalisationUri(this.testRuleHighOrder200.getKey());
        this.testProviderAllRulesRandomInsertion.addNormalisationUri(this.testRuleLowOrder15.getKey());
        this.testProviderAllRulesRandomInsertion.addNormalisationUri(this.testRuleLowestOrder3.getKey());
        this.testProviderAllRulesRandomInsertion.addNormalisationUri(this.testRuleMidOrder50.getKey());
        this.testProviderAllRulesRandomInsertion.addNormalisationUri(this.testRuleHighestOrder600.getKey());
        
        this.testProvidersAllRulesRandomInsertion = new ArrayList<Provider>();
        this.testProvidersAllRulesRandomInsertion.add(this.testProviderAllRulesRandomInsertion);
        
        this.testRuleTests = new ArrayList<RuleTest>();
        
        this.testStringRuleTestQueryVariables = new StringRuleTestImpl();
        this.testStringRuleTestQueryVariables.addStage(NormalisationRuleSchema.getRdfruleStageQueryVariables());
        
        this.testStringRuleTestBeforeResultsImport = new StringRuleTestImpl();
        this.testStringRuleTestBeforeResultsImport.addStage(NormalisationRuleSchema
                .getRdfruleStageBeforeResultsImport());
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        this.testRuleLowestOrder3 = null;
        this.testRuleLowOrder15 = null;
        this.testRuleMidOrder50 = null;
        this.testRuleHighOrder200 = null;
        this.testRuleHighestOrder600 = null;
        
        this.testAllNormalisationRules = null;
        
        this.testRulesNeededAll = null;
        
        this.testProviderNoRules = null;
        this.testProvidersNoRules = null;
        
        this.testProviderAllRulesRandomInsertion = null;
        this.testProvidersAllRulesRandomInsertion = null;
        
        this.testStringRuleTestQueryVariables = null;
        this.testStringRuleTestBeforeResultsImport = null;
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RuleUtils#getSortedRulesByUris(java.util.Map, java.util.Collection, org.queryall.api.utils.SortOrder)}
     * .
     */
    @Test
    public void testGetSortedRulesByUrisHighestOrderFirst()
    {
        final List<NormalisationRule> sortedRulesByUris =
                RuleUtils.getSortedRulesByUris(this.testAllNormalisationRules, this.testRulesNeededAll,
                        SortOrder.HIGHEST_ORDER_FIRST);
        
        Assert.assertEquals(5, sortedRulesByUris.size());
        
        Assert.assertEquals(this.testRuleHighestOrder600, sortedRulesByUris.get(0));
        Assert.assertEquals(this.testRuleHighOrder200, sortedRulesByUris.get(1));
        Assert.assertEquals(this.testRuleMidOrder50, sortedRulesByUris.get(2));
        Assert.assertEquals(this.testRuleLowOrder15, sortedRulesByUris.get(3));
        Assert.assertEquals(this.testRuleLowestOrder3, sortedRulesByUris.get(4));
        
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RuleUtils#getSortedRulesByUris(java.util.Map, java.util.Collection, org.queryall.api.utils.SortOrder)}
     * .
     */
    @Test
    public void testGetSortedRulesByUrisLowestOrderFirst()
    {
        final List<NormalisationRule> sortedRulesByUris =
                RuleUtils.getSortedRulesByUris(this.testAllNormalisationRules, this.testRulesNeededAll,
                        SortOrder.LOWEST_ORDER_FIRST);
        
        Assert.assertEquals(5, sortedRulesByUris.size());
        
        Assert.assertEquals(this.testRuleLowestOrder3, sortedRulesByUris.get(0));
        Assert.assertEquals(this.testRuleLowOrder15, sortedRulesByUris.get(1));
        Assert.assertEquals(this.testRuleMidOrder50, sortedRulesByUris.get(2));
        Assert.assertEquals(this.testRuleHighOrder200, sortedRulesByUris.get(3));
        Assert.assertEquals(this.testRuleHighestOrder600, sortedRulesByUris.get(4));
        
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RuleUtils#getSortedRulesForProviders(java.util.Collection, java.util.Map, org.queryall.api.utils.SortOrder)}
     * .
     */
    @Test
    public void testGetSortedRulesForProvidersEmpty()
    {
        final List<NormalisationRule> sortedRulesForProvidersHighestFirst =
                RuleUtils.getSortedRulesForProviders(this.testProvidersNoRules, this.testAllNormalisationRules,
                        SortOrder.HIGHEST_ORDER_FIRST);
        
        Assert.assertEquals(0, sortedRulesForProvidersHighestFirst.size());
        
        final List<NormalisationRule> sortedRulesForProvidersLowestFirst =
                RuleUtils.getSortedRulesForProviders(this.testProvidersNoRules, this.testAllNormalisationRules,
                        SortOrder.LOWEST_ORDER_FIRST);
        
        Assert.assertEquals(0, sortedRulesForProvidersLowestFirst.size());
        
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RuleUtils#getSortedRulesForProviders(java.util.Collection, java.util.Map, org.queryall.api.utils.SortOrder)}
     * .
     */
    @Test
    public void testGetSortedRulesForProvidersRandomInsertionHighestOrderFirst()
    {
        final List<NormalisationRule> sortedRulesForProvidersHighestFirst =
                RuleUtils.getSortedRulesForProviders(this.testProvidersAllRulesRandomInsertion,
                        this.testAllNormalisationRules, SortOrder.HIGHEST_ORDER_FIRST);
        
        Assert.assertEquals(5, sortedRulesForProvidersHighestFirst.size());
        
        Assert.assertEquals(this.testRuleHighestOrder600, sortedRulesForProvidersHighestFirst.get(0));
        Assert.assertEquals(this.testRuleHighOrder200, sortedRulesForProvidersHighestFirst.get(1));
        Assert.assertEquals(this.testRuleMidOrder50, sortedRulesForProvidersHighestFirst.get(2));
        Assert.assertEquals(this.testRuleLowOrder15, sortedRulesForProvidersHighestFirst.get(3));
        Assert.assertEquals(this.testRuleLowestOrder3, sortedRulesForProvidersHighestFirst.get(4));
        
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RuleUtils#getSortedRulesForProviders(java.util.Collection, java.util.Map, org.queryall.api.utils.SortOrder)}
     * .
     */
    @Test
    public void testGetSortedRulesForProvidersRandomInsertionLowestOrderFirst()
    {
        final List<NormalisationRule> sortedRulesForProvidersLowestFirst =
                RuleUtils.getSortedRulesForProviders(this.testProvidersAllRulesRandomInsertion,
                        this.testAllNormalisationRules, SortOrder.LOWEST_ORDER_FIRST);
        
        Assert.assertEquals(5, sortedRulesForProvidersLowestFirst.size());
        
        Assert.assertEquals(this.testRuleLowestOrder3, sortedRulesForProvidersLowestFirst.get(0));
        Assert.assertEquals(this.testRuleLowOrder15, sortedRulesForProvidersLowestFirst.get(1));
        Assert.assertEquals(this.testRuleMidOrder50, sortedRulesForProvidersLowestFirst.get(2));
        Assert.assertEquals(this.testRuleHighOrder200, sortedRulesForProvidersLowestFirst.get(3));
        Assert.assertEquals(this.testRuleHighestOrder600, sortedRulesForProvidersLowestFirst.get(4));
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RuleUtils#normaliseByStage(org.openrdf.model.URI, java.lang.Object, java.util.List, java.util.List, boolean, boolean)}
     * .
     */
    @Ignore
    @Test
    public void testNormaliseByStage()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.RuleUtils#runRuleTests(java.util.Collection, java.util.Map)}.
     */
    @Test
    public void testRunRuleTestsEmpty()
    {
        try
        {
            final boolean runRuleTests = RuleUtils.runRuleTests(this.testRuleTests, this.testAllNormalisationRules);
            
            Assert.assertTrue("Empty rule test running failed", runRuleTests);
        }
        catch(final QueryAllException e)
        {
            Assert.fail("Empty rule test running threw an exception");
        }
        
    }
    
}
