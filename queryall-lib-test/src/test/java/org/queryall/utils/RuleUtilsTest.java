/**
 * 
 */
package org.queryall.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.test.DummyNormalisationRule;
import org.queryall.api.utils.SortOrder;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 *
 */
public class RuleUtilsTest
{
    
    private NormalisationRule testRuleHighestOrder600;
    private NormalisationRule testRuleHighOrder200;
    private NormalisationRule testRuleMidOrder50;
    private NormalisationRule testRuleLowOrder15;
    private NormalisationRule testRuleLowestOrder3;
    private Collection<URI> testRulesNeededAll;
    private Map<URI, NormalisationRule> testAllNormalisationRules;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        testRuleLowestOrder3 = new DummyNormalisationRule();
        testRuleLowestOrder3.setKey("http://test.ruleutils.example.com/test/rule/order/1");
        testRuleLowestOrder3.setOrder(3);

        testRuleLowOrder15 = new DummyNormalisationRule();
        testRuleLowOrder15.setKey("http://test.ruleutils.example.com/test/rule/order/10");
        testRuleLowOrder15.setOrder(15);
    
        testRuleMidOrder50 = new DummyNormalisationRule();
        testRuleMidOrder50.setKey("http://test.ruleutils.example.com/test/rule/order/50");
        testRuleMidOrder50.setOrder(50);
        
        testRuleHighOrder200 = new DummyNormalisationRule();
        testRuleHighOrder200.setKey("http://test.ruleutils.example.com/test/rule/order/200");
        testRuleHighOrder200.setOrder(200);
        
        testRuleHighestOrder600 = new DummyNormalisationRule();
        testRuleHighestOrder600.setKey("http://test.ruleutils.example.com/test/rule/order/500");
        testRuleHighestOrder600.setOrder(600);
        
        testAllNormalisationRules = new ConcurrentHashMap<URI, NormalisationRule>();
        testAllNormalisationRules.put(testRuleLowestOrder3.getKey(), testRuleLowestOrder3);
        testAllNormalisationRules.put(testRuleLowOrder15.getKey(), testRuleLowOrder15);
        testAllNormalisationRules.put(testRuleMidOrder50.getKey(), testRuleMidOrder50);
        testAllNormalisationRules.put(testRuleHighOrder200.getKey(), testRuleHighOrder200);
        testAllNormalisationRules.put(testRuleHighestOrder600.getKey(), testRuleHighestOrder600);
        
        testRulesNeededAll = new ArrayList<URI>();
        testRulesNeededAll.add(testRuleLowestOrder3.getKey());
        testRulesNeededAll.add(testRuleLowOrder15.getKey());
        testRulesNeededAll.add(testRuleMidOrder50.getKey());
        testRulesNeededAll.add(testRuleHighOrder200.getKey());
        testRulesNeededAll.add(testRuleHighestOrder600.getKey());
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        testRuleLowestOrder3 = null;
        testRuleLowOrder15 = null;
        testRuleMidOrder50 = null;
        testRuleHighOrder200 = null;
        testRuleHighestOrder600 = null;
        
    }
    
    /**
     * Test method for {@link org.queryall.utils.RuleUtils#getSortedRulesByUris(java.util.Map, java.util.Collection, org.queryall.api.utils.SortOrder)}.
     */
    @Test
    public void testGetSortedRulesByUrisLowestOrderFirst()
    {
        List<NormalisationRule> sortedRulesByUris = RuleUtils.getSortedRulesByUris(testAllNormalisationRules, testRulesNeededAll, SortOrder.LOWEST_ORDER_FIRST);
        
        Assert.assertEquals(5, sortedRulesByUris.size());
        
        Assert.assertEquals(testRuleLowestOrder3, sortedRulesByUris.get(0));
        Assert.assertEquals(testRuleLowOrder15, sortedRulesByUris.get(1));
        Assert.assertEquals(testRuleMidOrder50, sortedRulesByUris.get(2));
        Assert.assertEquals(testRuleHighOrder200, sortedRulesByUris.get(3));
        Assert.assertEquals(testRuleHighestOrder600, sortedRulesByUris.get(4));
        
    }
    
    /**
     * Test method for {@link org.queryall.utils.RuleUtils#getSortedRulesByUris(java.util.Map, java.util.Collection, org.queryall.api.utils.SortOrder)}.
     */
    @Test
    public void testGetSortedRulesByUrisHighestOrderFirst()
    {
        List<NormalisationRule> sortedRulesByUris = RuleUtils.getSortedRulesByUris(testAllNormalisationRules, testRulesNeededAll, SortOrder.HIGHEST_ORDER_FIRST);
        
        Assert.assertEquals(5, sortedRulesByUris.size());
        
        Assert.assertEquals(testRuleHighestOrder600, sortedRulesByUris.get(0));
        Assert.assertEquals(testRuleHighOrder200, sortedRulesByUris.get(1));
        Assert.assertEquals(testRuleMidOrder50, sortedRulesByUris.get(2));
        Assert.assertEquals(testRuleLowOrder15, sortedRulesByUris.get(3));
        Assert.assertEquals(testRuleLowestOrder3, sortedRulesByUris.get(4));
        
    }
    
    /**
     * Test method for {@link org.queryall.utils.RuleUtils#getSortedRulesForProviders(java.util.Collection, java.util.Map, org.queryall.api.utils.SortOrder)}.
     */
    @Test
    public void testGetSortedRulesForProviders()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.utils.RuleUtils#normaliseByStage(org.openrdf.model.URI, java.lang.Object, java.util.List, java.util.List, boolean, boolean)}.
     */
    @Test
    public void testNormaliseByStage()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.utils.RuleUtils#runRuleTests(java.util.Collection, java.util.Map)}.
     */
    @Test
    public void testRunRuleTests()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
}
