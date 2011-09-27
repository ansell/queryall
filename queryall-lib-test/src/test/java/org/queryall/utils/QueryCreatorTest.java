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
    
    private RegexInputQueryType testRegexInputQueryType;
    private Provider testProvider;
    private Map<String, String> testAttributeList;
    private List<Profile> testIncludedProfiles;
    private boolean testRecogniseImplicitRdfRuleInclusions;
    private boolean testIncludeNonProfileMatchedRdfRules;
    private boolean testConvertAlternateToPreferredPrefix;
    private QueryAllConfiguration testLocalSettings;
    private Map<String, Collection<NamespaceEntry>> testNamespaceInputVariables;
    private NamespaceEntryImpl testNamespaceEntry;
    private Collection<NamespaceEntry> testNamespaceEntries;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        testNamespaceEntry = new NamespaceEntryImpl();
        testNamespaceEntry.setKey("http://example.org/test/namespace/1");
        testNamespaceEntry.setPreferredPrefix("myPreferredNamespace");
        testNamespaceEntry.addAlternativePrefix("alternateNs");
        
        testRegexInputQueryType = new RegexInputQueryTypeImpl();
        testRegexInputQueryType.setKey("http://example.org/test/query/1");
        testRegexInputQueryType.addNamespaceInputTag("input_1");
        testRegexInputQueryType.setIsNamespaceSpecific(true);
        testRegexInputQueryType.addNamespaceToHandle(testNamespaceEntry.getKey());
        testRegexInputQueryType.addLinkedQueryType(testRegexInputQueryType.getKey());
        testRegexInputQueryType.setInputRegex("^([\\w-]+):(.+)$");
        testRegexInputQueryType.setTemplateString("Select * Where { <http://example.org/ns/${input_1}> dc:identifier \"${input_2}\" . }");
        
        testProvider = new HttpSparqlProviderImpl();
        testProvider.setKey("http://example.org/test/provider/1");
        testProvider.addIncludedInQueryType(testRegexInputQueryType.getKey());
        testProvider.addNamespace(testNamespaceEntry.getKey());
        
        testAttributeList = new HashMap<String, String>();
        testAttributeList.put(Constants.QUERY, "alternateNs:alternateNsUniqueId");
        
        testIncludedProfiles = new ArrayList<Profile>(1);
        
        testRecogniseImplicitRdfRuleInclusions = true;
        testIncludeNonProfileMatchedRdfRules = true;
        testConvertAlternateToPreferredPrefix = true;
        
        testLocalSettings = new Settings("/testconfigs/querycreatortestconfig-base.n3", "text/rdf+n3", "http://example.org/test/config/querycreator-1");
        
        testLocalSettings.addQueryType(testRegexInputQueryType);
        testLocalSettings.addProvider(testProvider);
        testLocalSettings.addNamespaceEntry(testNamespaceEntry);
        
        testNamespaceInputVariables = new HashMap<String, Collection<NamespaceEntry>>();
        testNamespaceEntries = new ArrayList<NamespaceEntry>();
        testNamespaceEntries.add(testNamespaceEntry);
        testNamespaceInputVariables.put("input_1", testNamespaceEntries);
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
        String result = QueryCreator.createQuery(
                testRegexInputQueryType, 
                testProvider, 
                testAttributeList, 
                testIncludedProfiles, 
                testRecogniseImplicitRdfRuleInclusions, 
                testIncludeNonProfileMatchedRdfRules, 
                testConvertAlternateToPreferredPrefix, 
                testLocalSettings, 
                testNamespaceInputVariables);
        
        Assert.assertEquals("query was not as expected", "Select * Where { <http://example.org/ns/myPreferredNamespace> dc:identifier \"alternateNsUniqueId\" . }", result);
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
