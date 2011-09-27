/**
 * 
 */
package org.queryall.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.Provider;
import org.queryall.api.querytype.RegexInputQueryType;
import org.queryall.api.utils.Constants;
import org.queryall.impl.namespace.NamespaceEntryImpl;
import org.queryall.impl.provider.HttpSparqlProviderImpl;
import org.queryall.impl.querytype.RegexInputQueryTypeImpl;
import org.queryall.query.QueryCreator;
import org.queryall.query.Settings;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class QueryCreatorTest
{
    
    private RegexInputQueryType testRegexInputQueryType1;
    private Provider testProvider1;
    private Map<String, String> testAttributeList1;
    private List<Profile> testIncludedProfiles;
    private boolean testRecogniseImplicitRdfRuleInclusions;
    private boolean testIncludeNonProfileMatchedRdfRules;
    private boolean testConvertAlternateToPreferredPrefix;
    private QueryAllConfiguration testLocalSettings1;
    private Map<String, Collection<NamespaceEntry>> testNamespaceInputVariables1;
    private NamespaceEntry testNamespaceEntry1;
    private Collection<NamespaceEntry> testNamespaceEntries1;
    private RegexInputQueryType testRegexInputQueryType2;
    private Provider testProvider2;
    private NamespaceEntry testNamespaceEntry2;
    private Map<String, String> testAttributeList2;
    private Map<String, Collection<NamespaceEntry>> testNamespaceInputVariables2;
    private Collection<NamespaceEntry> testNamespaceEntries2;
    private QueryAllConfiguration testLocalSettings2;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        testNamespaceEntry1 = new NamespaceEntryImpl();
        testNamespaceEntry1.setKey("http://example.org/test/namespace/1");
        testNamespaceEntry1.setPreferredPrefix("myPreferredNamespace");
        testNamespaceEntry1.addAlternativePrefix("alternateNs");
        
        testNamespaceEntry2 = new NamespaceEntryImpl();
        testNamespaceEntry2.setKey("http://example.org/test/namespace/2");
        testNamespaceEntry2.setPreferredPrefix("myOtherNamespace");
        testNamespaceEntry2.addAlternativePrefix("otherNs");
        testNamespaceEntry2.setSeparator("/");

        testRegexInputQueryType1 = new RegexInputQueryTypeImpl();
        testRegexInputQueryType1.setKey("http://example.org/test/query/1");
        testRegexInputQueryType1.addNamespaceInputTag("input_1");
        testRegexInputQueryType1.setIsNamespaceSpecific(true);
        testRegexInputQueryType1.addNamespaceToHandle(testNamespaceEntry1.getKey());
        testRegexInputQueryType1.setInputRegex("^([\\w-]+):(.+)$");
        testRegexInputQueryType1.setTemplateString("Select * Where { <http://example.org/ns/${input_1}> dc:identifier \"${input_2}\" . }");
        
        testRegexInputQueryType2 = new RegexInputQueryTypeImpl();
        testRegexInputQueryType2.setKey("http://example.org/test/query/2");
        testRegexInputQueryType2.addNamespaceInputTag("input_1");
        testRegexInputQueryType2.setStandardUriTemplateString("${defaultHostAddress}${input_1}${separator}${input_2}");
        testRegexInputQueryType2.setIsNamespaceSpecific(true);
        testRegexInputQueryType2.addNamespaceToHandle(testNamespaceEntry2.getKey());
        testRegexInputQueryType2.setInputRegex("^([\\w-]+):(.+)$");
        testRegexInputQueryType2.setTemplateString("Select * Where { <${normalisedStandardUri}> dc:identifier \"${endpointSpecificUri}\" . }");
        
        testProvider1 = new HttpSparqlProviderImpl();
        testProvider1.setKey("http://example.org/test/provider/1");
        testProvider1.addIncludedInQueryType(testRegexInputQueryType1.getKey());
        testProvider1.addNamespace(testNamespaceEntry1.getKey());
        
        testProvider2 = new HttpSparqlProviderImpl();
        testProvider2.setKey("http://example.org/test/provider/2");
        testProvider2.addIncludedInQueryType(testRegexInputQueryType2.getKey());
        testProvider2.addNamespace(testNamespaceEntry2.getKey());
        
        testAttributeList1 = new HashMap<String, String>();
        testAttributeList1.put(Constants.QUERY, "alternateNs:alternateNsUniqueId");
        
        testAttributeList2 = new HashMap<String, String>();
        testAttributeList2.put(Constants.QUERY, "otherNs:otherNsUniqueId");
        testAttributeList2.put(Constants.TEMPLATE_KEY_DEFAULT_HOST_ADDRESS, "http://my.example.org/");
        testIncludedProfiles = new ArrayList<Profile>(1);
        
        testRecogniseImplicitRdfRuleInclusions = true;
        testIncludeNonProfileMatchedRdfRules = true;
        testConvertAlternateToPreferredPrefix = true;
        
        testLocalSettings1 = new Settings("/testconfigs/querycreatortestconfig-base.n3", "text/rdf+n3", "http://example.org/test/config/querycreator-1");
        
        testLocalSettings1.addQueryType(testRegexInputQueryType1);
        testLocalSettings1.addProvider(testProvider1);
        testLocalSettings1.addNamespaceEntry(testNamespaceEntry1);
        
        testLocalSettings2 = new Settings("/testconfigs/querycreatortestconfig-base.n3", "text/rdf+n3", "http://example.org/test/config/querycreator-1");
        
        testLocalSettings2.addQueryType(testRegexInputQueryType2);
        testLocalSettings2.addProvider(testProvider2);
        testLocalSettings2.addNamespaceEntry(testNamespaceEntry2);
        
        testNamespaceInputVariables1 = new HashMap<String, Collection<NamespaceEntry>>();
        testNamespaceEntries1 = new ArrayList<NamespaceEntry>();
        testNamespaceEntries1.add(testNamespaceEntry1);
        testNamespaceInputVariables1.put("input_1", testNamespaceEntries1);

        testNamespaceInputVariables2 = new HashMap<String, Collection<NamespaceEntry>>();
        testNamespaceEntries2 = new ArrayList<NamespaceEntry>();
        testNamespaceEntries2.add(testNamespaceEntry2);
        testNamespaceInputVariables2.put("input_1", testNamespaceEntries2);
    
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
    }
    
    /**
     * Test method for {@link org.queryall.query.QueryCreator#createQuery(org.queryall.api.querytype.QueryType, org.queryall.api.provider.Provider, java.util.Map, java.util.List, boolean, boolean, boolean, org.queryall.api.base.QueryAllConfiguration, java.util.Map)}.
     */
    @Test
    public void testCreateQuery()
    {
        String result1 = QueryCreator.createQuery(
                testRegexInputQueryType1, 
                testProvider1, 
                testAttributeList1, 
                testIncludedProfiles, 
                testRecogniseImplicitRdfRuleInclusions, 
                testIncludeNonProfileMatchedRdfRules, 
                testConvertAlternateToPreferredPrefix, 
                testLocalSettings1, 
                testNamespaceInputVariables1);
        
        Assert.assertEquals("query 1 was not as expected", "Select * Where { <http://example.org/ns/myPreferredNamespace> dc:identifier \"alternateNsUniqueId\" . }", result1);

        String result2 = QueryCreator.createQuery(
                testRegexInputQueryType2, 
                testProvider2, 
                testAttributeList2, 
                testIncludedProfiles, 
                testRecogniseImplicitRdfRuleInclusions, 
                testIncludeNonProfileMatchedRdfRules, 
                testConvertAlternateToPreferredPrefix, 
                testLocalSettings2, 
                testNamespaceInputVariables2);
        
        Assert.assertEquals("query 2 was not as expected", "Select * Where { <http://my.example.org/myOtherNamespace/otherNsUniqueId> dc:identifier \"http://my.example.org/myOtherNamespace/otherNsUniqueId\" . }", result2);
    }
    
    /**
     * Test method for {@link org.queryall.query.QueryCreator#createStaticRdfXmlString(org.queryall.api.querytype.QueryType, org.queryall.api.querytype.OutputQueryType, org.queryall.api.provider.Provider, java.util.Map, java.util.Map, java.util.List, boolean, boolean, boolean, org.queryall.api.base.QueryAllConfiguration)}.
     */
    @Test
    @Ignore
    public void testCreateStaticRdfXmlString()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.query.QueryCreator#doReplacementsOnString(java.util.Map, java.lang.String, org.queryall.api.querytype.QueryType, org.queryall.api.querytype.QueryType, org.queryall.api.provider.Provider, java.util.Map, java.util.Map, java.util.List, boolean, boolean, boolean, org.queryall.api.base.QueryAllConfiguration)}.
     */
    @Test
    @Ignore
    public void testDoReplacementsOnString()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.query.QueryCreator#getAttributeListFor(org.queryall.api.querytype.QueryType, org.queryall.api.provider.Provider, java.util.Map, java.lang.String, java.lang.String, int, org.queryall.api.base.QueryAllConfiguration)}.
     */
    @Test
    @Ignore
    public void testGetAttributeListFor()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.query.QueryCreator#matchAndReplaceInputVariablesForQueryType(org.queryall.api.querytype.QueryType, java.util.Map, java.lang.String, java.util.List, boolean, java.util.Map, org.queryall.api.provider.Provider)}.
     */
    @Test
    @Ignore
    public void testMatchAndReplaceInputVariablesForQueryType()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.query.QueryCreator#replaceAttributesOnEndpointUrl(java.lang.String, org.queryall.api.querytype.QueryType, org.queryall.api.provider.Provider, java.util.Map, java.util.List, boolean, boolean, boolean, org.queryall.api.base.QueryAllConfiguration, java.util.Map)}.
     */
    @Test
    @Ignore
    public void testReplaceAttributesOnEndpointUrl()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for {@link org.queryall.query.QueryCreator#replaceTags(java.lang.String, java.util.Map, java.util.regex.Pattern)}.
     */
    @Test
    @Ignore
    public void testReplaceTags()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
}
