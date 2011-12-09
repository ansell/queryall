package org.queryall.api.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.Provider;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.ruletest.RuleTest;

public abstract class AbstractQueryAllConfigurationTest
{
    private QueryAllConfiguration testConfiguration;
    private ValueFactory testValueFactory;
    
    /**
     * Override this in subtests to modify the expected default host address
     * 
     * @return http://bio2rdf.org/ by default
     */
    protected String getExpectedDefaultHostAddress()
    {
        return "http://bio2rdf.org/";
    }
    
    /**
     * Override this to return something other than ":" which is the default separator in the
     * absence of any configuration files
     * 
     * @return ":" by default, this can be overriden in subtests to return a different expected
     *         value
     */
    protected String getExpectedSeparator()
    {
        return ":";
    }
    
    protected abstract NamespaceEntry getNewNamespaceEntry();
    
    protected abstract NormalisationRule getNewNormalisationRule();
    
    protected abstract Profile getNewProfile();
    
    protected abstract Provider getNewProvider();
    
    /**
     * This method is used to create new instances of the QueryAllConfiguration implementation for
     * each test, to enable the test to be abstract and separate from the implementations of this
     * class
     * 
     * @return A new instance of the QueryAllConfiguration implementation for this class
     */
    protected abstract QueryAllConfiguration getNewQueryAllConfiguration();
    
    protected abstract QueryType getNewQueryType();
    
    protected abstract RuleTest getNewRuleTest();
    
    @Before
    public void setUp() throws Exception
    {
        this.testConfiguration = this.getNewQueryAllConfiguration();
        this.testValueFactory = new ValueFactoryImpl();
    }
    
    @After
    public void tearDown() throws Exception
    {
        this.testConfiguration = null;
        this.testValueFactory = null;
    }
    
    @Test
    public void testAddNamespaceEntry()
    {
        final NamespaceEntry nextNamespaceEntry = this.getNewNamespaceEntry();
        nextNamespaceEntry.setKey("http://example.org/test/queryallconfiguration/namespaceentry/add/1");
        
        Assert.assertNotNull(nextNamespaceEntry.getKey());
        
        Assert.assertEquals("http://example.org/test/queryallconfiguration/namespaceentry/add/1", nextNamespaceEntry
                .getKey().stringValue());
        
        this.testConfiguration.addNamespaceEntry(nextNamespaceEntry);
        
        Assert.assertNotNull(this.testConfiguration.getNamespaceEntry(nextNamespaceEntry.getKey()));
        
        Assert.assertEquals(1, this.testConfiguration.getAllNamespaceEntries().size());
        
        Assert.assertTrue(this.testConfiguration.getAllNamespaceEntries().containsKey(nextNamespaceEntry.getKey()));
    }
    
    @Test
    public void testAddNormalisationRule()
    {
        final NormalisationRule nextNormalisationRule = this.getNewNormalisationRule();
        nextNormalisationRule.setKey("http://example.org/test/queryallconfiguration/NormalisationRule/add/1");
        
        Assert.assertNotNull(nextNormalisationRule.getKey());
        
        Assert.assertEquals("http://example.org/test/queryallconfiguration/NormalisationRule/add/1",
                nextNormalisationRule.getKey().stringValue());
        
        this.testConfiguration.addNormalisationRule(nextNormalisationRule);
        
        Assert.assertNotNull(this.testConfiguration.getNormalisationRule(nextNormalisationRule.getKey()));
        
        Assert.assertEquals(1, this.testConfiguration.getAllNormalisationRules().size());
        
        Assert.assertTrue(this.testConfiguration.getAllNormalisationRules().containsKey(nextNormalisationRule.getKey()));
    }
    
    @Test
    public void testAddProfile()
    {
        final Profile nextProfile = this.getNewProfile();
        nextProfile.setKey("http://example.org/test/queryallconfiguration/Profile/add/1");
        
        Assert.assertNotNull(nextProfile.getKey());
        
        Assert.assertEquals("http://example.org/test/queryallconfiguration/Profile/add/1", nextProfile.getKey()
                .stringValue());
        
        this.testConfiguration.addProfile(nextProfile);
        
        Assert.assertNotNull(this.testConfiguration.getProfile(nextProfile.getKey()));
        
        Assert.assertEquals(1, this.testConfiguration.getAllProfiles().size());
        
        Assert.assertTrue(this.testConfiguration.getAllProfiles().containsKey(nextProfile.getKey()));
    }
    
    @Test
    public void testAddProvider()
    {
        final Provider nextProvider = this.getNewProvider();
        nextProvider.setKey("http://example.org/test/queryallconfiguration/Provider/add/1");
        
        Assert.assertNotNull(nextProvider.getKey());
        
        Assert.assertEquals("http://example.org/test/queryallconfiguration/Provider/add/1", nextProvider.getKey()
                .stringValue());
        
        this.testConfiguration.addProvider(nextProvider);
        
        Assert.assertNotNull(this.testConfiguration.getProvider(nextProvider.getKey()));
        
        Assert.assertEquals(1, this.testConfiguration.getAllProviders().size());
        
        Assert.assertTrue(this.testConfiguration.getAllProviders().containsKey(nextProvider.getKey()));
    }
    
    @Test
    public void testAddQueryType()
    {
        final QueryType nextQueryType = this.getNewQueryType();
        nextQueryType.setKey("http://example.org/test/queryallconfiguration/QueryType/add/1");
        
        Assert.assertNotNull(nextQueryType.getKey());
        
        Assert.assertEquals("http://example.org/test/queryallconfiguration/QueryType/add/1", nextQueryType.getKey()
                .stringValue());
        
        this.testConfiguration.addQueryType(nextQueryType);
        
        Assert.assertNotNull(this.testConfiguration.getQueryType(nextQueryType.getKey()));
        
        Assert.assertEquals(1, this.testConfiguration.getAllQueryTypes().size());
        
        Assert.assertTrue(this.testConfiguration.getAllQueryTypes().containsKey(nextQueryType.getKey()));
    }
    
    @Test
    public void testAddRuleTest()
    {
        final RuleTest nextRuleTest = this.getNewRuleTest();
        nextRuleTest.setKey("http://example.org/test/queryallconfiguration/RuleTest/add/1");
        
        Assert.assertNotNull(nextRuleTest.getKey());
        
        Assert.assertEquals("http://example.org/test/queryallconfiguration/RuleTest/add/1", nextRuleTest.getKey()
                .stringValue());
        
        this.testConfiguration.addRuleTest(nextRuleTest);
        
        Assert.assertNotNull(this.testConfiguration.getRuleTest(nextRuleTest.getKey()));
        
        Assert.assertEquals(1, this.testConfiguration.getAllRuleTests().size());
        
        Assert.assertTrue(this.testConfiguration.getAllRuleTests().containsKey(nextRuleTest.getKey()));
    }
    
    @Test
    public void testGetAllNamespaceEntries()
    {
        for(int i = 0; i < 1000; i++)
        {
            final NamespaceEntry nextNamespaceEntry = this.getNewNamespaceEntry();
            nextNamespaceEntry.setKey("http://example.org/test/queryallconfiguration/namespaceentry/add/" + i);
            
            Assert.assertNotNull(nextNamespaceEntry.getKey());
            
            Assert.assertEquals("http://example.org/test/queryallconfiguration/namespaceentry/add/" + i,
                    nextNamespaceEntry.getKey().stringValue());
            
            this.testConfiguration.addNamespaceEntry(nextNamespaceEntry);
            
            Assert.assertNotNull(this.testConfiguration.getNamespaceEntry(nextNamespaceEntry.getKey()));
            
            Assert.assertEquals((i + 1), this.testConfiguration.getAllNamespaceEntries().size());
            
            Assert.assertTrue(this.testConfiguration.getAllNamespaceEntries().containsKey(nextNamespaceEntry.getKey()));
        }
    }
    
    @Test
    public void testGetAllNormalisationRules()
    {
        for(int i = 0; i < 1000; i++)
        {
            final NormalisationRule nextNormalisationRule = this.getNewNormalisationRule();
            nextNormalisationRule.setKey("http://example.org/test/queryallconfiguration/NormalisationRule/add/" + i);
            
            Assert.assertNotNull(nextNormalisationRule.getKey());
            
            Assert.assertEquals("http://example.org/test/queryallconfiguration/NormalisationRule/add/" + i,
                    nextNormalisationRule.getKey().stringValue());
            
            this.testConfiguration.addNormalisationRule(nextNormalisationRule);
            
            Assert.assertNotNull(this.testConfiguration.getNormalisationRule(nextNormalisationRule.getKey()));
            
            Assert.assertEquals((i + 1), this.testConfiguration.getAllNormalisationRules().size());
            
            Assert.assertTrue(this.testConfiguration.getAllNormalisationRules().containsKey(
                    nextNormalisationRule.getKey()));
        }
    }
    
    @Test
    public void testGetAllProfiles()
    {
        for(int i = 0; i < 1000; i++)
        {
            final Profile nextProfile = this.getNewProfile();
            nextProfile.setKey("http://example.org/test/queryallconfiguration/Profile/add/" + i);
            
            Assert.assertNotNull(nextProfile.getKey());
            
            Assert.assertEquals("http://example.org/test/queryallconfiguration/Profile/add/" + i, nextProfile.getKey()
                    .stringValue());
            
            this.testConfiguration.addProfile(nextProfile);
            
            Assert.assertNotNull(this.testConfiguration.getProfile(nextProfile.getKey()));
            
            Assert.assertEquals((i + 1), this.testConfiguration.getAllProfiles().size());
            
            Assert.assertTrue(this.testConfiguration.getAllProfiles().containsKey(nextProfile.getKey()));
        }
    }
    
    @Test
    public void testGetAllProviders()
    {
        for(int i = 0; i < 1000; i++)
        {
            final Provider nextProvider = this.getNewProvider();
            nextProvider.setKey("http://example.org/test/queryallconfiguration/Provider/add/" + i);
            
            Assert.assertNotNull(nextProvider.getKey());
            
            Assert.assertEquals("http://example.org/test/queryallconfiguration/Provider/add/" + i, nextProvider
                    .getKey().stringValue());
            
            this.testConfiguration.addProvider(nextProvider);
            
            Assert.assertNotNull(this.testConfiguration.getProvider(nextProvider.getKey()));
            
            Assert.assertEquals((i + 1), this.testConfiguration.getAllProviders().size());
            
            Assert.assertTrue(this.testConfiguration.getAllProviders().containsKey(nextProvider.getKey()));
        }
    }
    
    @Test
    public void testGetAllQueryTypes()
    {
        for(int i = 0; i < 1000; i++)
        {
            final QueryType nextQueryType = this.getNewQueryType();
            nextQueryType.setKey("http://example.org/test/queryallconfiguration/QueryType/add/" + i);
            
            Assert.assertNotNull(nextQueryType.getKey());
            
            Assert.assertEquals("http://example.org/test/queryallconfiguration/QueryType/add/" + i, nextQueryType
                    .getKey().stringValue());
            
            this.testConfiguration.addQueryType(nextQueryType);
            
            Assert.assertNotNull(this.testConfiguration.getQueryType(nextQueryType.getKey()));
            
            Assert.assertEquals((i + 1), this.testConfiguration.getAllQueryTypes().size());
            
            Assert.assertTrue(this.testConfiguration.getAllQueryTypes().containsKey(nextQueryType.getKey()));
        }
    }
    
    @Test
    public void testGetAllRuleTests()
    {
        for(int i = 0; i < 1000; i++)
        {
            final RuleTest nextRuleTest = this.getNewRuleTest();
            nextRuleTest.setKey("http://example.org/test/queryallconfiguration/RuleTest/add/" + i);
            
            Assert.assertNotNull(nextRuleTest.getKey());
            
            Assert.assertEquals("http://example.org/test/queryallconfiguration/RuleTest/add/" + i, nextRuleTest
                    .getKey().stringValue());
            
            this.testConfiguration.addRuleTest(nextRuleTest);
            
            Assert.assertNotNull(this.testConfiguration.getRuleTest(nextRuleTest.getKey()));
            
            Assert.assertEquals((i + 1), this.testConfiguration.getAllRuleTests().size());
            
            Assert.assertTrue(this.testConfiguration.getAllRuleTests().containsKey(nextRuleTest.getKey()));
        }
    }
    
    @Test
    public void testGetBooleanProperty()
    {
        this.testConfiguration.setProperty("testProperty", true);
        
        Assert.assertTrue(this.testConfiguration.getBooleanProperty("testProperty", false));
        
        this.testConfiguration.setProperty("testProperty", false);
        
        Assert.assertFalse(this.testConfiguration.getBooleanProperty("testProperty", true));
    }
    
    /**
     * Test that the default host address is set in the absence of any values being set in a
     * configuration file
     * 
     * By default it tests for http://bio2rdf.org/ but this can be overridden in subtests if needed
     */
    @Test
    public void testGetDefaultHostAddress()
    {
        Assert.assertEquals(this.getExpectedDefaultHostAddress(), this.testConfiguration.getDefaultHostAddress());
    }
    
    @Test
    public void testGetFloatProperty()
    {
        this.testConfiguration.setProperty("testProperty", 0.5f);
        
        // assert that they are equal to within 0.001 of each other, which is enough to distinguish
        // between the real and false cases
        Assert.assertEquals(0.5f, this.testConfiguration.getFloatProperty("testProperty", -1.0f), 0.001f);
        
        this.testConfiguration.setProperty("testProperty", -0.5f);
        
        // assert that they are equal to within 0.001 of each other, which is enough to distinguish
        // between the real and false cases
        Assert.assertEquals(-0.5f, this.testConfiguration.getFloatProperty("testProperty", 1.0f), 0.001f);
    }
    
    @Test
    public void testGetIntProperty()
    {
        this.testConfiguration.setProperty("testProperty", 1);
        
        Assert.assertEquals(1, this.testConfiguration.getIntProperty("testProperty", -1));
        
        this.testConfiguration.setProperty("testProperty", -1);
        
        Assert.assertEquals(-1, this.testConfiguration.getIntProperty("testProperty", 1));
    }
    
    @Test
    public void testGetLongProperty()
    {
        this.testConfiguration.setProperty("testProperty", 4321L);
        
        Assert.assertEquals(4321L, this.testConfiguration.getLongProperty("testProperty", -1L));
        
        this.testConfiguration.setProperty("testProperty", -4321L);
        
        Assert.assertEquals(-4321L, this.testConfiguration.getLongProperty("testProperty", 1L));
        
    }
    
    @Test
    public void testGetNamespaceEntry()
    {
        for(int i = 0; i < 1000; i++)
        {
            final NamespaceEntry nextNamespaceEntry = this.getNewNamespaceEntry();
            nextNamespaceEntry.setKey("http://example.org/test/queryallconfiguration/namespaceentry/add/" + i);
            
            Assert.assertNotNull(nextNamespaceEntry.getKey());
            
            Assert.assertEquals("http://example.org/test/queryallconfiguration/namespaceentry/add/" + i,
                    nextNamespaceEntry.getKey().stringValue());
            
            this.testConfiguration.addNamespaceEntry(nextNamespaceEntry);
            
            Assert.assertNotNull(this.testConfiguration.getNamespaceEntry(nextNamespaceEntry.getKey()));
            
            Assert.assertEquals((i + 1), this.testConfiguration.getAllNamespaceEntries().size());
            
            Assert.assertTrue(this.testConfiguration.getAllNamespaceEntries().containsKey(nextNamespaceEntry.getKey()));
        }
    }
    
    @Test
    public void testGetNamespacePrefixesToUrisPreferredAndAlternateOverlapping()
    {
        final List<String> testPrefixes = new ArrayList<String>(5);
        testPrefixes.add(0, "testoverlappingprefix-0");
        testPrefixes.add(1, "testoverlappingprefix-1");
        testPrefixes.add(2, "testoverlappingprefix-2");
        testPrefixes.add(3, "testoverlappingprefix-3");
        testPrefixes.add(4, "testoverlappingprefix-4");
        
        final List<String> testAlternatePrefixes = new ArrayList<String>(5);
        testAlternatePrefixes.add(0, "testoverlappingalternateprefix-0");
        testAlternatePrefixes.add(1, "testoverlappingalternateprefix-1");
        testAlternatePrefixes.add(2, "testoverlappingalternateprefix-2");
        testAlternatePrefixes.add(3, "testoverlappingalternateprefix-3");
        testAlternatePrefixes.add(4, "testoverlappingalternateprefix-4");
        
        for(int i = 0; i < 1000; i++)
        {
            // cycle through the 5 test prefixes to create overlapping namespaces to test the
            // namespace prefix to URIs map
            final String nextPreferredPrefix = testPrefixes.get(i % 5);
            final String nextAlternatePrefix = testAlternatePrefixes.get(i % 5);
            
            final NamespaceEntry nextNamespaceEntry = this.getNewNamespaceEntry();
            nextNamespaceEntry.setKey("http://example.org/test/queryallconfiguration/namespaceentry/add/" + i);
            nextNamespaceEntry.setPreferredPrefix(nextPreferredPrefix);
            nextNamespaceEntry.addAlternativePrefix(nextAlternatePrefix);
            
            Assert.assertNotNull(nextNamespaceEntry.getKey());
            Assert.assertEquals("http://example.org/test/queryallconfiguration/namespaceentry/add/" + i,
                    nextNamespaceEntry.getKey().stringValue());
            
            // different method of constructing the prefix
            Assert.assertEquals("testoverlappingprefix-" + (i % 5), nextNamespaceEntry.getPreferredPrefix());
            
            Assert.assertNotNull(nextNamespaceEntry.getAlternativePrefixes());
            Assert.assertEquals(1, nextNamespaceEntry.getAlternativePrefixes().size());
            Assert.assertTrue(nextNamespaceEntry.getAlternativePrefixes().contains(nextAlternatePrefix));
            
            this.testConfiguration.addNamespaceEntry(nextNamespaceEntry);
            
            Assert.assertNotNull(this.testConfiguration.getNamespaceEntry(nextNamespaceEntry.getKey()));
            
            Assert.assertEquals((i + 1), this.testConfiguration.getAllNamespaceEntries().size());
            
            Assert.assertTrue(this.testConfiguration.getAllNamespaceEntries().containsKey(nextNamespaceEntry.getKey()));
            
            Assert.assertTrue(this.testConfiguration.getNamespacePrefixesToUris().containsKey(nextPreferredPrefix));
            
            Assert.assertTrue(this.testConfiguration.getNamespacePrefixesToUris().containsKey(nextAlternatePrefix));
            
            // before the first 5 are processed, we expect (i+1)*2 (one preferred and one alternate
            // for each of the first 5) elements, and after that, it should stay at size 10 (all
            // preferred and alternates)
            final int expectedPrefixMapSize = i < 5 ? (i + 1) * 2 : 10;
            
            Assert.assertEquals(expectedPrefixMapSize, this.testConfiguration.getNamespacePrefixesToUris().size());
        }
    }
    
    @Test
    public void testGetNamespacePrefixesToUrisPreferredOnlyOverlapping()
    {
        final List<String> testPrefixes = new ArrayList<String>(5);
        testPrefixes.add(0, "testoverlappingprefix-0");
        testPrefixes.add(1, "testoverlappingprefix-1");
        testPrefixes.add(2, "testoverlappingprefix-2");
        testPrefixes.add(3, "testoverlappingprefix-3");
        testPrefixes.add(4, "testoverlappingprefix-4");
        
        for(int i = 0; i < 1000; i++)
        {
            // cycle through the 5 test prefixes to create overlapping namespaces to test the
            // namespace prefix to URIs map
            final String nextPreferredPrefix = testPrefixes.get(i % 5);
            
            final NamespaceEntry nextNamespaceEntry = this.getNewNamespaceEntry();
            nextNamespaceEntry.setKey("http://example.org/test/queryallconfiguration/namespaceentry/add/" + i);
            nextNamespaceEntry.setPreferredPrefix(nextPreferredPrefix);
            
            Assert.assertNotNull(nextNamespaceEntry.getKey());
            Assert.assertEquals("http://example.org/test/queryallconfiguration/namespaceentry/add/" + i,
                    nextNamespaceEntry.getKey().stringValue());
            
            // different method of constructing the prefix
            Assert.assertEquals("testoverlappingprefix-" + (i % 5), nextNamespaceEntry.getPreferredPrefix());
            
            this.testConfiguration.addNamespaceEntry(nextNamespaceEntry);
            
            Assert.assertNotNull(this.testConfiguration.getNamespaceEntry(nextNamespaceEntry.getKey()));
            
            Assert.assertEquals((i + 1), this.testConfiguration.getAllNamespaceEntries().size());
            
            Assert.assertTrue(this.testConfiguration.getAllNamespaceEntries().containsKey(nextNamespaceEntry.getKey()));
            
            Assert.assertTrue(this.testConfiguration.getNamespacePrefixesToUris().containsKey(nextPreferredPrefix));
            
            // before the first 5 are processed, we expect i+1 elements, and after that, it should
            // stay at size 5
            final int expectedPrefixMapSize = i < 5 ? i + 1 : 5;
            
            Assert.assertEquals(expectedPrefixMapSize, this.testConfiguration.getNamespacePrefixesToUris().size());
        }
    }
    
    @Test
    public void testGetNamespacePrefixesToUrisPreferredOnlyUnique()
    {
        for(int i = 0; i < 1000; i++)
        {
            final NamespaceEntry nextNamespaceEntry = this.getNewNamespaceEntry();
            nextNamespaceEntry.setKey("http://example.org/test/queryallconfiguration/namespaceentry/add/" + i);
            nextNamespaceEntry.setPreferredPrefix("testprefix-" + i);
            
            Assert.assertNotNull(nextNamespaceEntry.getKey());
            Assert.assertEquals("http://example.org/test/queryallconfiguration/namespaceentry/add/" + i,
                    nextNamespaceEntry.getKey().stringValue());
            
            Assert.assertEquals("testprefix-" + i, nextNamespaceEntry.getPreferredPrefix());
            
            this.testConfiguration.addNamespaceEntry(nextNamespaceEntry);
            
            Assert.assertNotNull(this.testConfiguration.getNamespaceEntry(nextNamespaceEntry.getKey()));
            
            Assert.assertEquals((i + 1), this.testConfiguration.getAllNamespaceEntries().size());
            
            Assert.assertTrue(this.testConfiguration.getAllNamespaceEntries().containsKey(nextNamespaceEntry.getKey()));
            
            Assert.assertTrue(this.testConfiguration.getNamespacePrefixesToUris().containsKey("testprefix-" + i));
        }
    }
    
    @Test
    public void testGetNormalisationRule()
    {
        for(int i = 0; i < 1000; i++)
        {
            final NormalisationRule nextNormalisationRule = this.getNewNormalisationRule();
            nextNormalisationRule.setKey("http://example.org/test/queryallconfiguration/NormalisationRule/add/" + i);
            
            Assert.assertNotNull(nextNormalisationRule.getKey());
            
            Assert.assertEquals("http://example.org/test/queryallconfiguration/NormalisationRule/add/" + i,
                    nextNormalisationRule.getKey().stringValue());
            
            this.testConfiguration.addNormalisationRule(nextNormalisationRule);
            
            Assert.assertNotNull(this.testConfiguration.getNormalisationRule(nextNormalisationRule.getKey()));
            
            Assert.assertEquals((i + 1), this.testConfiguration.getAllNormalisationRules().size());
            
            Assert.assertTrue(this.testConfiguration.getAllNormalisationRules().containsKey(
                    nextNormalisationRule.getKey()));
        }
    }
    
    @Test
    public void testGetPlainNamespaceAndIdentifierPattern()
    {
        Assert.assertNotNull(this.testConfiguration.getPlainNamespaceAndIdentifierPattern());
        
        Assert.assertTrue(this.testConfiguration.getPlainNamespaceAndIdentifierPattern().matcher("abc:zyx").matches());
        Assert.assertTrue(this.testConfiguration.getPlainNamespaceAndIdentifierPattern().matcher("123abc:zyx")
                .matches());
        Assert.assertTrue(this.testConfiguration.getPlainNamespaceAndIdentifierPattern().matcher("123-abc:yzx_$")
                .matches());
        Assert.assertTrue(this.testConfiguration.getPlainNamespaceAndIdentifierPattern().matcher("123_abc:xuz:9")
                .matches());
        Assert.assertTrue(this.testConfiguration.getPlainNamespaceAndIdentifierPattern().matcher("123_abc:putmeaway")
                .matches());
        Assert.assertTrue(this.testConfiguration.getPlainNamespaceAndIdentifierPattern()
                .matcher("somethingrandom-plus_one_123:test").matches());
        Assert.assertTrue(this.testConfiguration.getPlainNamespaceAndIdentifierPattern()
                .matcher("somethingrandom-plus_one_123:1244").matches());
        Assert.assertTrue(this.testConfiguration.getPlainNamespaceAndIdentifierPattern()
                .matcher("somethingrandom-plus_one_123:./shelltest").matches());
    }
    
    @Test
    public void testGetPlainNamespacePattern()
    {
        Assert.assertNotNull(this.testConfiguration.getPlainNamespacePattern());
        
        Assert.assertTrue(this.testConfiguration.getPlainNamespacePattern().matcher("abc").matches());
        Assert.assertTrue(this.testConfiguration.getPlainNamespacePattern().matcher("123abc").matches());
        Assert.assertTrue(this.testConfiguration.getPlainNamespacePattern().matcher("123-abc").matches());
        Assert.assertTrue(this.testConfiguration.getPlainNamespacePattern().matcher("123_abc").matches());
        Assert.assertTrue(this.testConfiguration.getPlainNamespacePattern().matcher("123_abc").matches());
        Assert.assertTrue(this.testConfiguration.getPlainNamespacePattern().matcher("somethingrandom-plus_one_123")
                .matches());
        Assert.assertTrue(this.testConfiguration.getPlainNamespacePattern().matcher("somethingrandom-plus_one_123")
                .matches());
        Assert.assertTrue(this.testConfiguration.getPlainNamespacePattern().matcher("somethingrandom-plus_one_123")
                .matches());
    }
    
    @Test
    public void testGetProfile()
    {
        for(int i = 0; i < 1000; i++)
        {
            final Profile nextProfile = this.getNewProfile();
            nextProfile.setKey("http://example.org/test/queryallconfiguration/Profile/add/" + i);
            
            Assert.assertNotNull(nextProfile.getKey());
            
            Assert.assertEquals("http://example.org/test/queryallconfiguration/Profile/add/" + i, nextProfile.getKey()
                    .stringValue());
            
            this.testConfiguration.addProfile(nextProfile);
            
            Assert.assertNotNull(this.testConfiguration.getProfile(nextProfile.getKey()));
            
            Assert.assertEquals((i + 1), this.testConfiguration.getAllProfiles().size());
            
            Assert.assertTrue(this.testConfiguration.getAllProfiles().containsKey(nextProfile.getKey()));
        }
    }
    
    @Test
    public void testGetProvider()
    {
        for(int i = 0; i < 1000; i++)
        {
            final Provider nextProvider = this.getNewProvider();
            nextProvider.setKey("http://example.org/test/queryallconfiguration/Provider/add/" + i);
            
            Assert.assertNotNull(nextProvider.getKey());
            
            Assert.assertEquals("http://example.org/test/queryallconfiguration/Provider/add/" + i, nextProvider
                    .getKey().stringValue());
            
            this.testConfiguration.addProvider(nextProvider);
            
            Assert.assertNotNull(this.testConfiguration.getProvider(nextProvider.getKey()));
            
            Assert.assertEquals((i + 1), this.testConfiguration.getAllProviders().size());
            
            Assert.assertTrue(this.testConfiguration.getAllProviders().containsKey(nextProvider.getKey()));
        }
    }
    
    @Test
    public void testGetQueryType()
    {
        for(int i = 0; i < 1000; i++)
        {
            final QueryType nextQueryType = this.getNewQueryType();
            nextQueryType.setKey("http://example.org/test/queryallconfiguration/QueryType/add/" + i);
            
            Assert.assertNotNull(nextQueryType.getKey());
            
            Assert.assertEquals("http://example.org/test/queryallconfiguration/QueryType/add/" + i, nextQueryType
                    .getKey().stringValue());
            
            this.testConfiguration.addQueryType(nextQueryType);
            
            Assert.assertNotNull(this.testConfiguration.getQueryType(nextQueryType.getKey()));
            
            Assert.assertEquals((i + 1), this.testConfiguration.getAllQueryTypes().size());
            
            Assert.assertTrue(this.testConfiguration.getAllQueryTypes().containsKey(nextQueryType.getKey()));
        }
    }
    
    @Test
    public void testGetRuleTest()
    {
        for(int i = 0; i < 1000; i++)
        {
            final RuleTest nextRuleTest = this.getNewRuleTest();
            nextRuleTest.setKey("http://example.org/test/queryallconfiguration/RuleTest/add/" + i);
            
            Assert.assertNotNull(nextRuleTest.getKey());
            
            Assert.assertEquals("http://example.org/test/queryallconfiguration/RuleTest/add/" + i, nextRuleTest
                    .getKey().stringValue());
            
            this.testConfiguration.addRuleTest(nextRuleTest);
            
            Assert.assertNotNull(this.testConfiguration.getRuleTest(nextRuleTest.getKey()));
            
            Assert.assertEquals((i + 1), this.testConfiguration.getAllRuleTests().size());
            
            Assert.assertTrue(this.testConfiguration.getAllRuleTests().containsKey(nextRuleTest.getKey()));
        }
    }
    
    @Test
    public void testGetSeparator()
    {
        Assert.assertEquals(this.getExpectedSeparator(), this.testConfiguration.getSeparator());
    }
    
    @Test
    public void testGetStringPropertiesMultipleItems()
    {
        this.testConfiguration.setStringCollectionProperty("testProperty",
                Arrays.asList("my test string", "other different string"));
        
        // test that with multiple properties, random choices are not made, and the default is
        // returned instead
        Assert.assertEquals("default value", this.testConfiguration.getStringProperty("testProperty", "default value"));
        
        final Collection<String> stringProperties = this.testConfiguration.getStringProperties("testProperty");
        
        Assert.assertNotNull(stringProperties);
        
        Assert.assertEquals(2, stringProperties.size());
        
        Assert.assertTrue(stringProperties.contains("my test string"));
        
        Assert.assertTrue(stringProperties.contains("other different string"));
    }
    
    @Test
    public void testGetStringPropertiesNoItems()
    {
        this.testConfiguration.setStringCollectionProperty("testProperty", new ArrayList<String>(0));
        
        Assert.assertEquals("default value", this.testConfiguration.getStringProperty("testProperty", "default value"));
        
        final Collection<String> stringProperties = this.testConfiguration.getStringProperties("testProperty");
        
        Assert.assertNotNull(stringProperties);
        
        Assert.assertEquals(0, stringProperties.size());
    }
    
    @Test
    public void testGetStringPropertiesSingleItem()
    {
        this.testConfiguration.setStringCollectionProperty("testProperty", Arrays.asList("my test string"));
        
        Assert.assertEquals("my test string", this.testConfiguration.getStringProperty("testProperty", "default value"));
        
        final Collection<String> stringProperties = this.testConfiguration.getStringProperties("testProperty");
        
        Assert.assertNotNull(stringProperties);
        
        Assert.assertEquals(1, stringProperties.size());
        
        Assert.assertEquals("my test string", stringProperties.iterator().next());
    }
    
    @Test
    public void testGetStringProperty()
    {
        this.testConfiguration.setProperty("testProperty", "my test string");
        
        Assert.assertEquals("my test string", this.testConfiguration.getStringProperty("testProperty", "default value"));
        
        this.testConfiguration.setProperty("testProperty", "different test string");
        
        Assert.assertEquals("different test string",
                this.testConfiguration.getStringProperty("testProperty", "default value"));
        
    }
    
    /**
     * Tests the default tag pattern to make sure it works for our desired purposes
     */
    @Test
    public void testGetTagPattern()
    {
        Assert.assertNotNull(this.testConfiguration.getTagPattern());
        
        Assert.assertTrue(this.testConfiguration.getTagPattern().matcher("${input_1}").matches());
        
        Assert.assertTrue(this.testConfiguration.getTagPattern().matcher("${queryString}").matches());
        
        Assert.assertTrue(this.testConfiguration.getTagPattern().matcher("${xmlEncoded_urlEncoded_input_1}").matches());
    }
    
    @Test
    public void testGetURIPropertiesMultiple()
    {
        final Collection<URI> testUris = new ArrayList<URI>(3);
        
        testUris.add(this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/1"));
        testUris.add(this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/2"));
        testUris.add(this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/3"));
        
        this.testConfiguration.setURICollectionProperty("testProperty", testUris);
        
        // ensure that the default is returned from getURIProperty if more than one property is set
        Assert.assertEquals(
                this.testValueFactory.createURI("http://example.org/test/default"),
                this.testConfiguration.getURIProperty("testProperty",
                        this.testValueFactory.createURI("http://example.org/test/default")));
        
        final Collection<URI> uriProperties = this.testConfiguration.getURIProperties("testProperty");
        
        Assert.assertNotNull(uriProperties);
        
        Assert.assertEquals(3, uriProperties.size());
        
        Assert.assertTrue(uriProperties.contains(this.testValueFactory
                .createURI("http://example.org/test/setproperty/string/uri/1")));
        Assert.assertTrue(uriProperties.contains(this.testValueFactory
                .createURI("http://example.org/test/setproperty/string/uri/2")));
        Assert.assertTrue(uriProperties.contains(this.testValueFactory
                .createURI("http://example.org/test/setproperty/string/uri/3")));
    }
    
    @Test
    public void testGetURIPropertiesNoItems()
    {
        final Collection<URI> testUris = new ArrayList<URI>(0);
        
        this.testConfiguration.setURICollectionProperty("testProperty", testUris);
        
        // ensure that the default is returned from getURIProperty if no properties are set
        Assert.assertEquals(
                this.testValueFactory.createURI("http://example.org/test/default"),
                this.testConfiguration.getURIProperty("testProperty",
                        this.testValueFactory.createURI("http://example.org/test/default")));
        
        final Collection<URI> uriProperties = this.testConfiguration.getURIProperties("testProperty");
        
        Assert.assertNotNull(uriProperties);
        
        Assert.assertEquals(0, uriProperties.size());
    }
    
    @Test
    public void testGetURIPropertiesSingle()
    {
        final Collection<URI> testUris = new ArrayList<URI>(3);
        
        testUris.add(this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/1"));
        
        this.testConfiguration.setURICollectionProperty("testProperty", testUris);
        
        // ensure that the default is returned from getURIProperty if more than one property is set
        Assert.assertEquals(
                this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/1"),
                this.testConfiguration.getURIProperty("testProperty",
                        this.testValueFactory.createURI("http://example.org/test/default")));
        
        final Collection<URI> uriProperties = this.testConfiguration.getURIProperties("testProperty");
        
        Assert.assertNotNull(uriProperties);
        
        Assert.assertEquals(1, uriProperties.size());
        
        Assert.assertTrue(uriProperties.contains(this.testValueFactory
                .createURI("http://example.org/test/setproperty/string/uri/1")));
    }
    
    @Test
    public void testGetURIProperty()
    {
        this.testConfiguration.setProperty("testProperty",
                this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/1"));
        
        Assert.assertEquals(
                this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/1"),
                this.testConfiguration.getURIProperty("testProperty",
                        this.testValueFactory.createURI("http://example.org/test/default")));
        
        this.testConfiguration.setProperty("testProperty",
                this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/2"));
        
        Assert.assertEquals(
                this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/2"),
                this.testConfiguration.getURIProperty("testProperty",
                        this.testValueFactory.createURI("http://example.org/test/default")));
        
    }
    
    @Test
    public void testSetPropertyStringBoolean()
    {
        this.testConfiguration.setProperty("testProperty", true);
        
        Assert.assertTrue(this.testConfiguration.getBooleanProperty("testProperty", false));
    }
    
    @Test
    public void testSetPropertyStringFloat()
    {
        this.testConfiguration.setProperty("testProperty", 0.5f);
        
        // assert that they are equal to within 0.001 of each other, which is enough to distinguish
        // between the real and false cases
        Assert.assertEquals(0.5f, this.testConfiguration.getFloatProperty("testProperty", -1.0f), 0.001f);
    }
    
    @Test
    public void testSetPropertyStringInt()
    {
        this.testConfiguration.setProperty("testProperty", 1);
        
        Assert.assertEquals(1, this.testConfiguration.getIntProperty("testProperty", -1));
    }
    
    @Test
    public void testSetPropertyStringLong()
    {
        this.testConfiguration.setProperty("testProperty", 4321L);
        
        Assert.assertEquals(4321L, this.testConfiguration.getLongProperty("testProperty", -1L));
    }
    
    @Test
    public void testSetPropertyStringString()
    {
        this.testConfiguration.setProperty("testProperty", "my test string");
        
        Assert.assertEquals("my test string", this.testConfiguration.getStringProperty("testProperty", "default value"));
    }
    
    @Test
    public void testSetPropertyStringURI()
    {
        this.testConfiguration.setProperty("testProperty",
                this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/1"));
        
        Assert.assertEquals(
                this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/1"),
                this.testConfiguration.getURIProperty("testProperty",
                        this.testValueFactory.createURI("http://example.org/test/default")));
        
        this.testConfiguration.setProperty("testProperty",
                this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/2"));
        
        Assert.assertEquals(
                this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/2"),
                this.testConfiguration.getURIProperty("testProperty",
                        this.testValueFactory.createURI("http://example.org/test/default")));
        
    }
    
    @Test
    public void testSetPropertyStringValueBoolean()
    {
        this.testConfiguration.setProperty("testProperty", this.testValueFactory.createLiteral(true));
        
        Assert.assertTrue(this.testConfiguration.getBooleanProperty("testProperty", false));
        
        this.testConfiguration.setProperty("testProperty", this.testValueFactory.createLiteral(false));
        
        Assert.assertFalse(this.testConfiguration.getBooleanProperty("testProperty", true));
        
    }
    
    @Test
    public void testSetPropertyStringValueFloat()
    {
        this.testConfiguration.setProperty("testProperty", this.testValueFactory.createLiteral(0.5f));
        
        // assert that they are equal to within 0.001 of each other, which is enough to distinguish
        // between the real and false cases
        Assert.assertEquals(0.5f, this.testConfiguration.getFloatProperty("testProperty", -1.0f), 0.001f);
        
        this.testConfiguration.setProperty("testProperty", this.testValueFactory.createLiteral(-0.5f));
        
        // assert that they are equal to within 0.001 of each other, which is enough to distinguish
        // between the real and false cases
        Assert.assertEquals(-0.5f, this.testConfiguration.getFloatProperty("testProperty", 1.0f), 0.001f);
        
    }
    
    @Test
    public void testSetPropertyStringValueInt()
    {
        this.testConfiguration.setProperty("testProperty", this.testValueFactory.createLiteral(1));
        
        Assert.assertEquals(1, this.testConfiguration.getIntProperty("testProperty", -1));
        
        this.testConfiguration.setProperty("testProperty", this.testValueFactory.createLiteral(-123));
        
        Assert.assertEquals(-123, this.testConfiguration.getIntProperty("testProperty", 1));
        
    }
    
    @Test
    public void testSetPropertyStringValueLong()
    {
        this.testConfiguration.setProperty("testProperty", this.testValueFactory.createLiteral(4321L));
        
        Assert.assertEquals(4321L, this.testConfiguration.getLongProperty("testProperty", -1L));
        
        this.testConfiguration.setProperty("testProperty", this.testValueFactory.createLiteral(-4321L));
        
        Assert.assertEquals(-4321L, this.testConfiguration.getLongProperty("testProperty", 1L));
        
    }
    
    @Test
    public void testSetStringCollectionProperty()
    {
        this.testConfiguration.setStringCollectionProperty("testProperty",
                Arrays.asList("my test string", "other different string"));
        
        // test that with multiple properties, random choices are not made, and the default is
        // returned instead
        Assert.assertEquals("default value", this.testConfiguration.getStringProperty("testProperty", "default value"));
        
        final Collection<String> stringProperties = this.testConfiguration.getStringProperties("testProperty");
        
        Assert.assertNotNull(stringProperties);
        
        Assert.assertEquals(2, stringProperties.size());
        
        Assert.assertTrue(stringProperties.contains("my test string"));
        
        Assert.assertTrue(stringProperties.contains("other different string"));
    }
    
    @Test
    public void testSetURICollectionProperty()
    {
        final Collection<URI> testUris = new ArrayList<URI>(3);
        
        testUris.add(this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/1"));
        testUris.add(this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/2"));
        testUris.add(this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/3"));
        
        this.testConfiguration.setURICollectionProperty("testProperty", testUris);
        
        // ensure that the default is returned from getURIProperty if more than one property is set
        Assert.assertEquals(
                this.testValueFactory.createURI("http://example.org/test/default"),
                this.testConfiguration.getURIProperty("testProperty",
                        this.testValueFactory.createURI("http://example.org/test/default")));
        
        final Collection<URI> uriProperties = this.testConfiguration.getURIProperties("testProperty");
        
        Assert.assertNotNull(uriProperties);
        
        Assert.assertEquals(3, uriProperties.size());
        
        Assert.assertTrue(uriProperties.contains(this.testValueFactory
                .createURI("http://example.org/test/setproperty/string/uri/1")));
        Assert.assertTrue(uriProperties.contains(this.testValueFactory
                .createURI("http://example.org/test/setproperty/string/uri/2")));
        Assert.assertTrue(uriProperties.contains(this.testValueFactory
                .createURI("http://example.org/test/setproperty/string/uri/3")));
    }
    
}
