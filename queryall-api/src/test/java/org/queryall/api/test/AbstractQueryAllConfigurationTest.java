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
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.Provider;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.api.utils.WebappConfig;

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
        // check both the explicit and implicit default versions using both true and false
        this.testConfiguration.setProperty(WebappConfig._TEST_BOOLEAN_PROPERTY, true);
        
        Assert.assertTrue(this.testConfiguration.getBooleanProperty(WebappConfig._TEST_BOOLEAN_PROPERTY));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_BOOLEAN_PROPERTY, false);
        
        Assert.assertFalse(this.testConfiguration.getBooleanProperty(WebappConfig._TEST_BOOLEAN_PROPERTY));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_BOOLEAN_PROPERTY, true);
        
        Assert.assertTrue(this.testConfiguration.getBooleanProperty(WebappConfig._TEST_BOOLEAN_PROPERTY, false));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_BOOLEAN_PROPERTY, false);
        
        Assert.assertFalse(this.testConfiguration.getBooleanProperty(WebappConfig._TEST_BOOLEAN_PROPERTY, true));
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
        this.testConfiguration.setProperty(WebappConfig._TEST_FLOAT_PROPERTY, -1.5f);
        
        // test without the default value
        Assert.assertEquals(-1.5f, this.testConfiguration.getFloatProperty(WebappConfig._TEST_FLOAT_PROPERTY), 0.001f);
        
        this.testConfiguration.setProperty(WebappConfig._TEST_FLOAT_PROPERTY, 0.5f);
        
        // assert that they are equal to within 0.001 of each other, which is enough to distinguish
        // between the real and false cases
        Assert.assertEquals(0.5f, this.testConfiguration.getFloatProperty(WebappConfig._TEST_FLOAT_PROPERTY, -1.0f),
                0.001f);
        
        this.testConfiguration.setProperty(WebappConfig._TEST_FLOAT_PROPERTY, -0.5f);
        
        // assert that they are equal to within 0.001 of each other, which is enough to distinguish
        // between the real and false cases
        Assert.assertEquals(-0.5f, this.testConfiguration.getFloatProperty(WebappConfig._TEST_FLOAT_PROPERTY, 1.0f),
                0.001f);
    }
    
    @Test
    public void testGetIntProperty()
    {
        this.testConfiguration.setProperty(WebappConfig._TEST_INT_PROPERTY, -2);
        
        Assert.assertEquals(-2, this.testConfiguration.getIntProperty(WebappConfig._TEST_INT_PROPERTY));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_INT_PROPERTY, 1);
        
        Assert.assertEquals(1, this.testConfiguration.getIntProperty(WebappConfig._TEST_INT_PROPERTY, -1));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_INT_PROPERTY, -1);
        
        Assert.assertEquals(-1, this.testConfiguration.getIntProperty(WebappConfig._TEST_INT_PROPERTY, 1));
    }
    
    @Test
    public void testGetLongProperty()
    {
        this.testConfiguration.setProperty(WebappConfig._TEST_LONG_PROPERTY, 6543L);
        
        Assert.assertEquals(6543L, this.testConfiguration.getLongProperty(WebappConfig._TEST_LONG_PROPERTY));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_LONG_PROPERTY, 4321L);
        
        Assert.assertEquals(4321L, this.testConfiguration.getLongProperty(WebappConfig._TEST_LONG_PROPERTY, -1L));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_LONG_PROPERTY, -4321L);
        
        Assert.assertEquals(-4321L, this.testConfiguration.getLongProperty(WebappConfig._TEST_LONG_PROPERTY, 1L));
        
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
        this.testConfiguration.setStringCollectionProperty(WebappConfig._TEST_STRING_COLLECTION_PROPERTY,
                Arrays.asList("my test string", "other different string"));
        
        // test that with multiple properties, random choices are not made, and the default is
        // returned instead
        Assert.assertEquals("default value", this.testConfiguration.getStringProperty(
                WebappConfig._TEST_STRING_COLLECTION_PROPERTY, "default value"));
        
        final Collection<String> stringProperties =
                this.testConfiguration.getStringProperties(WebappConfig._TEST_STRING_COLLECTION_PROPERTY);
        
        Assert.assertNotNull(stringProperties);
        
        Assert.assertEquals(2, stringProperties.size());
        
        Assert.assertTrue(stringProperties.contains("my test string"));
        
        Assert.assertTrue(stringProperties.contains("other different string"));
    }
    
    @Test
    public void testGetStringPropertiesNoItems()
    {
        this.testConfiguration.setStringCollectionProperty(WebappConfig._TEST_STRING_COLLECTION_PROPERTY,
                new ArrayList<String>(0));
        
        Assert.assertEquals("default value", this.testConfiguration.getStringProperty(
                WebappConfig._TEST_STRING_COLLECTION_PROPERTY, "default value"));
        
        final Collection<String> stringProperties =
                this.testConfiguration.getStringProperties(WebappConfig._TEST_STRING_COLLECTION_PROPERTY);
        
        Assert.assertNotNull(stringProperties);
        
        Assert.assertEquals(0, stringProperties.size());
    }
    
    @Test
    public void testGetStringPropertiesSingleItem()
    {
        this.testConfiguration.setStringCollectionProperty(WebappConfig._TEST_STRING_COLLECTION_PROPERTY,
                Arrays.asList("my test string"));
        
        Assert.assertEquals("my test string", this.testConfiguration.getStringProperty(
                WebappConfig._TEST_STRING_COLLECTION_PROPERTY, "default value"));
        
        final Collection<String> stringProperties =
                this.testConfiguration.getStringProperties(WebappConfig._TEST_STRING_COLLECTION_PROPERTY);
        
        Assert.assertNotNull(stringProperties);
        
        Assert.assertEquals(1, stringProperties.size());
        
        Assert.assertEquals("my test string", stringProperties.iterator().next());
    }
    
    @Test
    public void testGetStringProperty()
    {
        this.testConfiguration.setProperty(WebappConfig._TEST_STRING_PROPERTY, "my no default string");
        
        Assert.assertEquals("my no default string",
                this.testConfiguration.getStringProperty(WebappConfig._TEST_STRING_PROPERTY));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_STRING_PROPERTY, "my test string");
        
        Assert.assertEquals("my test string",
                this.testConfiguration.getStringProperty(WebappConfig._TEST_STRING_PROPERTY, "default value"));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_STRING_PROPERTY, "different test string");
        
        Assert.assertEquals("different test string",
                this.testConfiguration.getStringProperty(WebappConfig._TEST_STRING_PROPERTY, "default value"));
        
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
        
        this.testConfiguration.setURICollectionProperty(WebappConfig._TEST_URI_COLLECTION_PROPERTY, testUris);
        
        // ensure that the default is returned from getURIProperty if more than one property is set
        Assert.assertEquals(
                this.testValueFactory.createURI("http://example.org/test/default"),
                this.testConfiguration.getURIProperty(WebappConfig._TEST_URI_COLLECTION_PROPERTY,
                        this.testValueFactory.createURI("http://example.org/test/default")));
        
        final Collection<URI> uriProperties =
                this.testConfiguration.getURIProperties(WebappConfig._TEST_URI_COLLECTION_PROPERTY);
        
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
        
        this.testConfiguration.setURICollectionProperty(WebappConfig._TEST_URI_COLLECTION_PROPERTY, testUris);
        
        // ensure that the default is returned from getURIProperty if no properties are set
        Assert.assertEquals(
                this.testValueFactory.createURI("http://example.org/test/default"),
                this.testConfiguration.getURIProperty(WebappConfig._TEST_URI_COLLECTION_PROPERTY,
                        this.testValueFactory.createURI("http://example.org/test/default")));
        
        final Collection<URI> uriProperties =
                this.testConfiguration.getURIProperties(WebappConfig._TEST_URI_COLLECTION_PROPERTY);
        
        Assert.assertNotNull(uriProperties);
        
        Assert.assertEquals(0, uriProperties.size());
    }
    
    @Test
    public void testGetURIPropertiesSingle()
    {
        final Collection<URI> testUris = new ArrayList<URI>(3);
        
        testUris.add(this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/1"));
        
        this.testConfiguration.setURICollectionProperty(WebappConfig._TEST_URI_COLLECTION_PROPERTY, testUris);
        
        // ensure that the default is returned from getURIProperty if more than one property is set
        Assert.assertEquals(
                this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/1"),
                this.testConfiguration.getURIProperty(WebappConfig._TEST_URI_COLLECTION_PROPERTY,
                        this.testValueFactory.createURI("http://example.org/test/default")));
        
        final Collection<URI> uriProperties =
                this.testConfiguration.getURIProperties(WebappConfig._TEST_URI_COLLECTION_PROPERTY);
        
        Assert.assertNotNull(uriProperties);
        
        Assert.assertEquals(1, uriProperties.size());
        
        Assert.assertTrue(uriProperties.contains(this.testValueFactory
                .createURI("http://example.org/test/setproperty/string/uri/1")));
    }
    
    @Test
    public void testGetURIProperty()
    {
        this.testConfiguration.setProperty(WebappConfig._TEST_URI_PROPERTY,
                this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/1"));
        
        Assert.assertEquals(this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/1"),
                this.testConfiguration.getURIProperty(WebappConfig._TEST_URI_PROPERTY));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_URI_PROPERTY,
                this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/2"));
        
        Assert.assertEquals(
                this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/2"),
                this.testConfiguration.getURIProperty(WebappConfig._TEST_URI_PROPERTY,
                        this.testValueFactory.createURI("http://example.org/test/default")));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_URI_PROPERTY,
                this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/3"));
        
        Assert.assertEquals(
                this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/3"),
                this.testConfiguration.getURIProperty(WebappConfig._TEST_URI_PROPERTY,
                        this.testValueFactory.createURI("http://example.org/test/default")));
        
    }
    
    @Test
    public void testResetNamespaceEntries()
    {
        Assert.assertNotNull(this.testConfiguration.getAllNamespaceEntries());
        Assert.assertEquals(0, this.testConfiguration.getAllNamespaceEntries().size());
        
        final NamespaceEntry testNamespaceEntry1 = this.getNewNamespaceEntry();
        testNamespaceEntry1.setKey("http://test.example.org/namespaceentry/1");
        
        final NamespaceEntry testNamespaceEntry2 = this.getNewNamespaceEntry();
        testNamespaceEntry2.setKey("http://test.example.org/namespaceentry/2");
        
        this.testConfiguration.addNamespaceEntry(testNamespaceEntry1);
        Assert.assertEquals(1, this.testConfiguration.getAllNamespaceEntries().size());
        this.testConfiguration.addNamespaceEntry(testNamespaceEntry2);
        Assert.assertEquals(2, this.testConfiguration.getAllNamespaceEntries().size());
        
        Assert.assertTrue(this.testConfiguration.resetNamespaceEntries());
        
        Assert.assertNotNull(this.testConfiguration.getAllNamespaceEntries());
        Assert.assertEquals(0, this.testConfiguration.getAllNamespaceEntries().size());
    }
    
    @Test
    public void testResetNormalisationRules()
    {
        Assert.assertNotNull(this.testConfiguration.getAllNormalisationRules());
        Assert.assertEquals(0, this.testConfiguration.getAllNormalisationRules().size());
        
        final NormalisationRule testNormalisationRule1 = this.getNewNormalisationRule();
        testNormalisationRule1.setKey("http://test.example.org/normalisationrule/1");
        
        final NormalisationRule testNormalisationRule2 = this.getNewNormalisationRule();
        testNormalisationRule2.setKey("http://test.example.org/normalisationrule/2");
        
        this.testConfiguration.addNormalisationRule(testNormalisationRule1);
        Assert.assertEquals(1, this.testConfiguration.getAllNormalisationRules().size());
        this.testConfiguration.addNormalisationRule(testNormalisationRule2);
        Assert.assertEquals(2, this.testConfiguration.getAllNormalisationRules().size());
        
        Assert.assertTrue(this.testConfiguration.resetNormalisationRules());
        
        Assert.assertNotNull(this.testConfiguration.getAllNormalisationRules());
        Assert.assertEquals(0, this.testConfiguration.getAllNormalisationRules().size());
    }
    
    @Test
    public void testResetProfiles()
    {
        Assert.assertNotNull(this.testConfiguration.getAllProfiles());
        Assert.assertEquals(0, this.testConfiguration.getAllProfiles().size());
        
        final Profile testProfile1 = this.getNewProfile();
        testProfile1.setKey("http://test.example.org/profile/1");
        
        final Profile testProfile2 = this.getNewProfile();
        testProfile2.setKey("http://test.example.org/profile/2");
        
        this.testConfiguration.addProfile(testProfile1);
        Assert.assertEquals(1, this.testConfiguration.getAllProfiles().size());
        this.testConfiguration.addProfile(testProfile2);
        Assert.assertEquals(2, this.testConfiguration.getAllProfiles().size());
        
        Assert.assertTrue(this.testConfiguration.resetProfiles());
        
        Assert.assertNotNull(this.testConfiguration.getAllProfiles());
        Assert.assertEquals(0, this.testConfiguration.getAllProfiles().size());
    }
    
    @Test
    public void testResetProperties()
    {
        final Collection<URI> testUris = new ArrayList<URI>(3);
        
        testUris.add(this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/1"));
        testUris.add(this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/2"));
        testUris.add(this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/3"));
        
        this.testConfiguration.setURICollectionProperty(WebappConfig._TEST_URI_COLLECTION_PROPERTY, testUris);
        
        final Collection<URI> uriProperties =
                this.testConfiguration.getURIProperties(WebappConfig._TEST_URI_COLLECTION_PROPERTY);
        
        Assert.assertNotNull(uriProperties);
        Assert.assertEquals(3, uriProperties.size());
        
        Assert.assertTrue(this.testConfiguration.resetProperties());
        
        final Collection<URI> uriProperties2 =
                this.testConfiguration.getURIProperties(WebappConfig._TEST_URI_COLLECTION_PROPERTY);
        
        Assert.assertNotNull(uriProperties2);
        Assert.assertEquals(0, uriProperties2.size());
    }
    
    @Test
    public void testResetProviders()
    {
        Assert.assertNotNull(this.testConfiguration.getAllProviders());
        Assert.assertEquals(0, this.testConfiguration.getAllProviders().size());
        
        final Provider testProvider1 = this.getNewProvider();
        testProvider1.setKey("http://test.example.org/provider/1");
        
        final Provider testProvider2 = this.getNewProvider();
        testProvider2.setKey("http://test.example.org/provider/2");
        
        this.testConfiguration.addProvider(testProvider1);
        Assert.assertEquals(1, this.testConfiguration.getAllProviders().size());
        this.testConfiguration.addProvider(testProvider2);
        Assert.assertEquals(2, this.testConfiguration.getAllProviders().size());
        
        Assert.assertTrue(this.testConfiguration.resetProviders());
        
        Assert.assertNotNull(this.testConfiguration.getAllProviders());
        Assert.assertEquals(0, this.testConfiguration.getAllProviders().size());
    }
    
    @Test
    public void testResetQueryTypes()
    {
        Assert.assertNotNull(this.testConfiguration.getAllQueryTypes());
        Assert.assertEquals(0, this.testConfiguration.getAllQueryTypes().size());
        
        final QueryType testQueryType1 = this.getNewQueryType();
        testQueryType1.setKey("http://test.example.org/querytype/1");
        
        final QueryType testQueryType2 = this.getNewQueryType();
        testQueryType2.setKey("http://test.example.org/querytype/2");
        
        this.testConfiguration.addQueryType(testQueryType1);
        Assert.assertEquals(1, this.testConfiguration.getAllQueryTypes().size());
        this.testConfiguration.addQueryType(testQueryType2);
        Assert.assertEquals(2, this.testConfiguration.getAllQueryTypes().size());
        
        Assert.assertTrue(this.testConfiguration.resetQueryTypes());
        
        Assert.assertNotNull(this.testConfiguration.getAllQueryTypes());
        Assert.assertEquals(0, this.testConfiguration.getAllQueryTypes().size());
    }
    
    @Test
    public void testResetRuleTests()
    {
        Assert.assertNotNull(this.testConfiguration.getAllRuleTests());
        Assert.assertEquals(0, this.testConfiguration.getAllRuleTests().size());
        
        final RuleTest testRuleTest1 = this.getNewRuleTest();
        testRuleTest1.setKey("http://test.example.org/ruletest/1");
        
        final RuleTest testRuleTest2 = this.getNewRuleTest();
        testRuleTest2.setKey("http://test.example.org/ruletest/2");
        
        this.testConfiguration.addRuleTest(testRuleTest1);
        Assert.assertEquals(1, this.testConfiguration.getAllRuleTests().size());
        this.testConfiguration.addRuleTest(testRuleTest2);
        Assert.assertEquals(2, this.testConfiguration.getAllRuleTests().size());
        
        Assert.assertTrue(this.testConfiguration.resetRuleTests());
        
        Assert.assertNotNull(this.testConfiguration.getAllRuleTests());
        Assert.assertEquals(0, this.testConfiguration.getAllRuleTests().size());
    }
    
    @Test
    public void testSetPropertyStringBoolean()
    {
        // check both the explicit and implicit default versions using both true and false
        this.testConfiguration.setProperty(WebappConfig._TEST_BOOLEAN_PROPERTY, true);
        
        Assert.assertTrue(this.testConfiguration.getBooleanProperty(WebappConfig._TEST_BOOLEAN_PROPERTY));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_BOOLEAN_PROPERTY, false);
        
        Assert.assertFalse(this.testConfiguration.getBooleanProperty(WebappConfig._TEST_BOOLEAN_PROPERTY));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_BOOLEAN_PROPERTY, true);
        
        Assert.assertTrue(this.testConfiguration.getBooleanProperty(WebappConfig._TEST_BOOLEAN_PROPERTY, false));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_BOOLEAN_PROPERTY, false);
        
        Assert.assertFalse(this.testConfiguration.getBooleanProperty(WebappConfig._TEST_BOOLEAN_PROPERTY, true));
    }
    
    @Test
    public void testSetPropertyStringFloat()
    {
        this.testConfiguration.setProperty(WebappConfig._TEST_FLOAT_PROPERTY, -1.5f);
        
        // test without the default value
        Assert.assertEquals(-1.5f, this.testConfiguration.getFloatProperty(WebappConfig._TEST_FLOAT_PROPERTY), 0.001f);
        
        this.testConfiguration.setProperty(WebappConfig._TEST_FLOAT_PROPERTY, 0.5f);
        
        // assert that they are equal to within 0.001 of each other, which is enough to distinguish
        // between the real and false cases
        Assert.assertEquals(0.5f, this.testConfiguration.getFloatProperty(WebappConfig._TEST_FLOAT_PROPERTY, -1.0f),
                0.001f);
        
        this.testConfiguration.setProperty(WebappConfig._TEST_FLOAT_PROPERTY, -0.5f);
        
        // assert that they are equal to within 0.001 of each other, which is enough to distinguish
        // between the real and false cases
        Assert.assertEquals(-0.5f, this.testConfiguration.getFloatProperty(WebappConfig._TEST_FLOAT_PROPERTY, 1.0f),
                0.001f);
    }
    
    @Test
    public void testSetPropertyStringInt()
    {
        this.testConfiguration.setProperty(WebappConfig._TEST_INT_PROPERTY, -2);
        
        Assert.assertEquals(-2, this.testConfiguration.getIntProperty(WebappConfig._TEST_INT_PROPERTY));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_INT_PROPERTY, 1);
        
        Assert.assertEquals(1, this.testConfiguration.getIntProperty(WebappConfig._TEST_INT_PROPERTY, -1));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_INT_PROPERTY, -1);
        
        Assert.assertEquals(-1, this.testConfiguration.getIntProperty(WebappConfig._TEST_INT_PROPERTY, 1));
    }
    
    @Test
    public void testSetPropertyStringLong()
    {
        this.testConfiguration.setProperty(WebappConfig._TEST_LONG_PROPERTY, 6543L);
        
        Assert.assertEquals(6543L, this.testConfiguration.getLongProperty(WebappConfig._TEST_LONG_PROPERTY));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_LONG_PROPERTY, 4321L);
        
        Assert.assertEquals(4321L, this.testConfiguration.getLongProperty(WebappConfig._TEST_LONG_PROPERTY, -1L));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_LONG_PROPERTY, -4321L);
        
        Assert.assertEquals(-4321L, this.testConfiguration.getLongProperty(WebappConfig._TEST_LONG_PROPERTY, 1L));
        
    }
    
    @Test
    public void testSetPropertyStringString()
    {
        this.testConfiguration.setProperty(WebappConfig._TEST_STRING_PROPERTY, "my non-default string");
        
        Assert.assertEquals("my non-default string",
                this.testConfiguration.getStringProperty(WebappConfig._TEST_STRING_PROPERTY));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_STRING_PROPERTY, "my test string");
        
        Assert.assertEquals("my test string",
                this.testConfiguration.getStringProperty(WebappConfig._TEST_STRING_PROPERTY, "default value"));
    }
    
    @Test
    public void testSetPropertyStringStringCollection()
    {
        final Collection<String> testEmptyProperty =
                this.testConfiguration.getStringProperties(WebappConfig._TEST_STRING_COLLECTION_PROPERTY);
        
        Assert.assertNotNull(testEmptyProperty);
        Assert.assertEquals(0, testEmptyProperty.size());
        
        final String testStringDefault = "default value";
        
        final String testStringNonDefault = "my non-default string";
        this.testConfiguration.setProperty(WebappConfig._TEST_STRING_COLLECTION_PROPERTY, testStringNonDefault);
        
        Assert.assertEquals(testStringNonDefault, this.testConfiguration.getStringProperty(
                WebappConfig._TEST_STRING_COLLECTION_PROPERTY, testStringDefault));
        
        final String testString1 = "my test string";
        this.testConfiguration.setProperty(WebappConfig._TEST_STRING_COLLECTION_PROPERTY, testString1);
        
        // verify that the default value is returned now that there are two properties with the same
        // name, as it should not attempt to choose one of them
        Assert.assertEquals(testStringDefault, this.testConfiguration.getStringProperty(
                WebappConfig._TEST_STRING_COLLECTION_PROPERTY, testStringDefault));
        
        final Collection<String> stringProperties =
                this.testConfiguration.getStringProperties(WebappConfig._TEST_STRING_COLLECTION_PROPERTY);
        
        Assert.assertNotNull(stringProperties);
        Assert.assertEquals(2, stringProperties.size());
        Assert.assertTrue(stringProperties.contains(testStringNonDefault));
        Assert.assertTrue(stringProperties.contains(testString1));
        
        // check that the default test string was not accidentally inserted at any point
        Assert.assertFalse(stringProperties.contains(testStringDefault));
        
    }
    
    @Test
    public void testSetPropertyStringURI()
    {
        this.testConfiguration.setProperty(WebappConfig._TEST_URI_PROPERTY,
                this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/1"));
        
        Assert.assertEquals(
                this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/1"),
                this.testConfiguration.getURIProperty(WebappConfig._TEST_URI_PROPERTY,
                        this.testValueFactory.createURI("http://example.org/test/default")));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_URI_PROPERTY,
                this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/2"));
        
        Assert.assertEquals(
                this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/2"),
                this.testConfiguration.getURIProperty(WebappConfig._TEST_URI_PROPERTY,
                        this.testValueFactory.createURI("http://example.org/test/default")));
        
    }
    
    @Test
    public void testSetPropertyStringURICollection()
    {
        final Collection<URI> testEmptyProperty =
                this.testConfiguration.getURIProperties(WebappConfig._TEST_URI_COLLECTION_PROPERTY);
        
        Assert.assertNotNull(testEmptyProperty);
        Assert.assertEquals(0, testEmptyProperty.size());
        
        final URI testDefault = this.testValueFactory.createURI("http://example.org/test/default");
        
        final URI testUri1 = this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/1");
        this.testConfiguration.setProperty(WebappConfig._TEST_URI_COLLECTION_PROPERTY, testUri1);
        
        Assert.assertEquals(testUri1,
                this.testConfiguration.getURIProperty(WebappConfig._TEST_URI_COLLECTION_PROPERTY, testDefault));
        
        final URI testUri2 = this.testValueFactory.createURI("http://example.org/test/setproperty/string/uri/2");
        this.testConfiguration.setProperty(WebappConfig._TEST_URI_COLLECTION_PROPERTY, testUri2);
        
        // expect the default to be returned as there should now be two values for this property, so
        // getURIProperty should always use the default in these circumstances
        Assert.assertEquals(testDefault,
                this.testConfiguration.getURIProperty(WebappConfig._TEST_URI_COLLECTION_PROPERTY, testDefault));
        
        final Collection<URI> uriProperties =
                this.testConfiguration.getURIProperties(WebappConfig._TEST_URI_COLLECTION_PROPERTY);
        
        Assert.assertEquals(2, uriProperties.size());
        
        // check that both of the URIs exist
        Assert.assertTrue(uriProperties.contains(testUri1));
        Assert.assertTrue(uriProperties.contains(testUri2));
        
        // check that the default URI was never accidentally inserted into the collection
        Assert.assertFalse(uriProperties.contains(testDefault));
        
    }
    
    @Test
    public void testSetPropertyStringValueBoolean()
    {
        this.testConfiguration.setProperty(WebappConfig._TEST_BOOLEAN_PROPERTY,
                this.testValueFactory.createLiteral(false));
        
        Assert.assertFalse(this.testConfiguration.getBooleanProperty(WebappConfig._TEST_BOOLEAN_PROPERTY));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_BOOLEAN_PROPERTY,
                this.testValueFactory.createLiteral(true));
        
        Assert.assertTrue(this.testConfiguration.getBooleanProperty(WebappConfig._TEST_BOOLEAN_PROPERTY));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_BOOLEAN_PROPERTY,
                this.testValueFactory.createLiteral(true));
        
        Assert.assertTrue(this.testConfiguration.getBooleanProperty(WebappConfig._TEST_BOOLEAN_PROPERTY, false));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_BOOLEAN_PROPERTY,
                this.testValueFactory.createLiteral(false));
        
        Assert.assertFalse(this.testConfiguration.getBooleanProperty(WebappConfig._TEST_BOOLEAN_PROPERTY, true));
        
    }
    
    @Test
    public void testSetPropertyStringValueFloat()
    {
        this.testConfiguration
                .setProperty(WebappConfig._TEST_FLOAT_PROPERTY, this.testValueFactory.createLiteral(0.5f));
        
        // assert that they are equal to within 0.001 of each other, which is enough to distinguish
        // between the real and false cases
        Assert.assertEquals(0.5f, this.testConfiguration.getFloatProperty(WebappConfig._TEST_FLOAT_PROPERTY, -1.0f),
                0.001f);
        
        this.testConfiguration.setProperty(WebappConfig._TEST_FLOAT_PROPERTY,
                this.testValueFactory.createLiteral(-0.5f));
        
        // assert that they are equal to within 0.001 of each other, which is enough to distinguish
        // between the real and false cases
        Assert.assertEquals(-0.5f, this.testConfiguration.getFloatProperty(WebappConfig._TEST_FLOAT_PROPERTY, 1.0f),
                0.001f);
        
    }
    
    @Test
    public void testSetPropertyStringValueInt()
    {
        this.testConfiguration.setProperty(WebappConfig._TEST_INT_PROPERTY, this.testValueFactory.createLiteral(1));
        
        Assert.assertEquals(1, this.testConfiguration.getIntProperty(WebappConfig._TEST_INT_PROPERTY, -1));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_INT_PROPERTY, this.testValueFactory.createLiteral(-123));
        
        Assert.assertEquals(-123, this.testConfiguration.getIntProperty(WebappConfig._TEST_INT_PROPERTY, 1));
        
    }
    
    @Test
    public void testSetPropertyStringValueLong()
    {
        this.testConfiguration
                .setProperty(WebappConfig._TEST_LONG_PROPERTY, this.testValueFactory.createLiteral(4321L));
        
        Assert.assertEquals(4321L, this.testConfiguration.getLongProperty(WebappConfig._TEST_LONG_PROPERTY, -1L));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_LONG_PROPERTY,
                this.testValueFactory.createLiteral(-4321L));
        
        Assert.assertEquals(-4321L, this.testConfiguration.getLongProperty(WebappConfig._TEST_LONG_PROPERTY, 1L));
        
    }
    
    @Test
    public void testSetPropertyStringValueString()
    {
        // NOTE: These should be cast to Value to enable testing of the
        // setProperty(WebappConfig,Value) method as opposed to the setProperty(WebappConfig,URI)
        // method
        final String defaultString = "default value";
        final String testString1 = "first string value";
        final String testString2 = "second string value";
        
        final Value testValue1 = this.testValueFactory.createLiteral(testString1);
        final Value testValue2 = this.testValueFactory.createLiteral(testString2);
        
        this.testConfiguration.setProperty(WebappConfig._TEST_STRING_PROPERTY, testValue1);
        
        Assert.assertEquals(testString1,
                this.testConfiguration.getStringProperty(WebappConfig._TEST_STRING_PROPERTY, defaultString));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_STRING_PROPERTY, testValue2);
        
        Assert.assertEquals(testString2,
                this.testConfiguration.getStringProperty(WebappConfig._TEST_STRING_PROPERTY, defaultString));
        
    }
    
    @Test
    public void testSetPropertyStringValueStringCollection()
    {
        // NOTE: These should be cast to Value to enable testing of the
        // setProperty(WebappConfig,Value) method as opposed to the setProperty(WebappConfig,URI)
        // method
        final String defaultString = "default value";
        final String testString1 = "first string value";
        final String testString2 = "second string value";
        
        final Value testValue1 = this.testValueFactory.createLiteral(testString1);
        final Value testValue2 = this.testValueFactory.createLiteral(testString2);
        
        Assert.assertEquals(defaultString,
                this.testConfiguration.getStringProperty(WebappConfig._TEST_STRING_COLLECTION_PROPERTY, defaultString));
        
        final Collection<String> emptyProperties =
                this.testConfiguration.getStringProperties(WebappConfig._TEST_STRING_COLLECTION_PROPERTY);
        Assert.assertNotNull(emptyProperties);
        Assert.assertEquals(0, emptyProperties.size());
        
        this.testConfiguration.setProperty(WebappConfig._TEST_STRING_COLLECTION_PROPERTY, testValue1);
        
        Assert.assertEquals(testString1,
                this.testConfiguration.getStringProperty(WebappConfig._TEST_STRING_COLLECTION_PROPERTY, defaultString));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_STRING_COLLECTION_PROPERTY, testValue2);
        
        // verify that the default is now returned as there is no other unique value
        Assert.assertEquals(defaultString,
                this.testConfiguration.getStringProperty(WebappConfig._TEST_STRING_COLLECTION_PROPERTY, defaultString));
        
        final Collection<String> stringProperties =
                this.testConfiguration.getStringProperties(WebappConfig._TEST_STRING_COLLECTION_PROPERTY);
        
        Assert.assertNotNull(stringProperties);
        Assert.assertEquals(2, stringProperties.size());
        
        Assert.assertTrue(stringProperties.contains(testString1));
        Assert.assertTrue(stringProperties.contains(testString2));
        
        Assert.assertFalse(stringProperties.contains(defaultString));
    }
    
    @Test
    public void testSetPropertyStringValueURI()
    {
        // NOTE: These should be cast to Value to enable testing of the
        // setProperty(WebappConfig,Value) method as opposed to the setProperty(WebappConfig,URI)
        // method
        final Value testUriDefault =
                this.testValueFactory.createURI("http://test.example.org/test/setproperty/string/uri/default");
        final Value testUri1 = this.testValueFactory.createURI("http://test.example.org/test/setproperty/string/uri/1");
        final Value testUri2 = this.testValueFactory.createURI("http://test.example.org/test/setproperty/string/uri/2");
        
        this.testConfiguration.setProperty(WebappConfig._TEST_URI_PROPERTY, testUri1);
        
        Assert.assertEquals(testUri1,
                this.testConfiguration.getURIProperty(WebappConfig._TEST_URI_PROPERTY, (URI)testUriDefault));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_URI_PROPERTY, testUri2);
        
        Assert.assertEquals(testUri2,
                this.testConfiguration.getURIProperty(WebappConfig._TEST_URI_PROPERTY, (URI)testUriDefault));
        
    }
    
    @Test
    public void testSetPropertyStringValueURICollection()
    {
        // NOTE: These should be cast to Value to enable testing of the
        // setProperty(WebappConfig,Value) method as opposed to the setProperty(WebappConfig,URI)
        // method
        final Value testUriDefault =
                this.testValueFactory.createURI("http://test.example.org/test/setproperty/string/uri/default");
        final Value testUri1 = this.testValueFactory.createURI("http://test.example.org/test/setproperty/string/uri/1");
        final Value testUri2 = this.testValueFactory.createURI("http://test.example.org/test/setproperty/string/uri/2");
        
        Assert.assertEquals(testUriDefault,
                this.testConfiguration.getURIProperty(WebappConfig._TEST_URI_COLLECTION_PROPERTY, (URI)testUriDefault));
        
        final Collection<URI> emptyProperties =
                this.testConfiguration.getURIProperties(WebappConfig._TEST_URI_COLLECTION_PROPERTY);
        Assert.assertNotNull(emptyProperties);
        Assert.assertEquals(0, emptyProperties.size());
        
        this.testConfiguration.setProperty(WebappConfig._TEST_URI_COLLECTION_PROPERTY, testUri1);
        
        Assert.assertEquals(testUri1,
                this.testConfiguration.getURIProperty(WebappConfig._TEST_URI_COLLECTION_PROPERTY, (URI)testUriDefault));
        
        this.testConfiguration.setProperty(WebappConfig._TEST_URI_COLLECTION_PROPERTY, testUri2);
        
        // check that the default is now returned as there is no single unique value for this
        // property
        Assert.assertEquals(testUriDefault,
                this.testConfiguration.getURIProperty(WebappConfig._TEST_URI_COLLECTION_PROPERTY, (URI)testUriDefault));
        
        final Collection<URI> uriProperties =
                this.testConfiguration.getURIProperties(WebappConfig._TEST_URI_COLLECTION_PROPERTY);
        
        Assert.assertNotNull(uriProperties);
        Assert.assertEquals(2, uriProperties.size());
        
        Assert.assertTrue(uriProperties.contains(testUri1));
        Assert.assertTrue(uriProperties.contains(testUri2));
        
        Assert.assertFalse(uriProperties.contains(testUriDefault));
    }
    
    @Test
    public void testSetStringCollectionProperty()
    {
        this.testConfiguration.setStringCollectionProperty(WebappConfig._TEST_STRING_COLLECTION_PROPERTY,
                Arrays.asList("my test string", "other different string"));
        
        // test that with multiple properties, random choices are not made, and the default is
        // returned instead
        Assert.assertEquals("default value", this.testConfiguration.getStringProperty(
                WebappConfig._TEST_STRING_COLLECTION_PROPERTY, "default value"));
        
        final Collection<String> stringProperties =
                this.testConfiguration.getStringProperties(WebappConfig._TEST_STRING_COLLECTION_PROPERTY);
        
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
        
        this.testConfiguration.setURICollectionProperty(WebappConfig._TEST_URI_COLLECTION_PROPERTY, testUris);
        
        // ensure that the default is returned from getURIProperty if more than one property is set
        Assert.assertEquals(
                this.testValueFactory.createURI("http://example.org/test/default"),
                this.testConfiguration.getURIProperty(WebappConfig._TEST_URI_COLLECTION_PROPERTY,
                        this.testValueFactory.createURI("http://example.org/test/default")));
        
        final Collection<URI> uriProperties =
                this.testConfiguration.getURIProperties(WebappConfig._TEST_URI_COLLECTION_PROPERTY);
        
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
