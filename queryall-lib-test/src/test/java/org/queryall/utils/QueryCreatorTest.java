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
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.Provider;
import org.queryall.api.querytype.RegexInputQueryType;
import org.queryall.api.utils.Constants;
import org.queryall.exception.QueryAllException;
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
    private ValueFactory testValueFactory;
    
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
    
    private RegexInputQueryTypeImpl testRegexInputQueryType3;
    
    private HttpSparqlProviderImpl testProvider3;
    
    private HashMap<String, Collection<NamespaceEntry>> testNamespaceInputVariables3;
    
    private ArrayList<NamespaceEntry> testNamespaceEntries3;
    
    private HashMap<String, String> testAttributeList3;
    
    private QueryAllConfiguration testLocalSettings3;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        this.testValueFactory = new ValueFactoryImpl();
        
        this.testNamespaceEntry1 = new NamespaceEntryImpl();
        this.testNamespaceEntry1.setKey("http://example.org/test/namespace/1");
        this.testNamespaceEntry1.setPreferredPrefix("myPreferredNamespace");
        this.testNamespaceEntry1.addAlternativePrefix("alternateNs");
        this.testNamespaceEntry1.setSeparator(":");
        this.testNamespaceEntry1.setAuthority(this.testValueFactory.createURI("http://my.example.org/"));
        
        this.testNamespaceEntry2 = new NamespaceEntryImpl();
        this.testNamespaceEntry2.setKey("http://example.org/test/namespace/2");
        this.testNamespaceEntry2.setPreferredPrefix("myOtherNamespace");
        this.testNamespaceEntry2.addAlternativePrefix("otherNs");
        this.testNamespaceEntry2.setSeparator("/");
        this.testNamespaceEntry2.setAuthority(this.testValueFactory.createURI("http://other.example.org/"));
        
        this.testRegexInputQueryType1 = new RegexInputQueryTypeImpl();
        this.testRegexInputQueryType1.setKey("http://example.org/test/query/1");
        this.testRegexInputQueryType1.addNamespaceInputTag("input_1");
        this.testRegexInputQueryType1.setIsNamespaceSpecific(true);
        this.testRegexInputQueryType1.addNamespaceToHandle(this.testNamespaceEntry1.getKey());
        this.testRegexInputQueryType1.setInputRegex("^([\\w-]+):(.+)$");
        this.testRegexInputQueryType1
                .setTemplateString("Select * Where { <http://example.org/ns/${input_1}> dc:identifier \"${input_2}\" . }");
        
        this.testRegexInputQueryType2 = new RegexInputQueryTypeImpl();
        this.testRegexInputQueryType2.setKey("http://example.org/test/query/2");
        this.testRegexInputQueryType2.addNamespaceInputTag("input_1");
        this.testRegexInputQueryType2.setStandardUriTemplateString("${authority}${input_1}${separator}${input_2}");
        this.testRegexInputQueryType2.setIsNamespaceSpecific(true);
        this.testRegexInputQueryType2.addNamespaceToHandle(this.testNamespaceEntry2.getKey());
        this.testRegexInputQueryType2.setInputRegex("^([\\w-]+):(.+)$");
        this.testRegexInputQueryType2
                .setTemplateString("Select * Where { <${normalisedStandardUri}> dc:identifier \"${endpointSpecificUri}\" . }");
        
        this.testRegexInputQueryType3 = new RegexInputQueryTypeImpl();
        this.testRegexInputQueryType3.setKey("http://example.org/test/query/3");
        this.testRegexInputQueryType3
                .setOutputString("<rdf:Description rdf:about=\"${xmlEncoded_inputUrlEncoded_normalisedStandardUri}\"><ns0pred:urlFasta xmlns:ns0pred=\"${defaultHostAddress}bio2rdf_resource:\">${xmlEncoded_endpointUrl}</ns0pred:urlFasta></rdf:Description>");
        this.testRegexInputQueryType3.setStandardUriTemplateString("${authority}${input_1}${separator}${input_2}");
        this.testRegexInputQueryType3.setInputRegex("^([\\w-]+):(.+)$");
        this.testRegexInputQueryType3.addNamespaceInputTag("input_1");
        this.testRegexInputQueryType3.setIsNamespaceSpecific(true);
        this.testRegexInputQueryType3.addNamespaceToHandle(this.testNamespaceEntry1.getKey());
        
        this.testProvider1 = new HttpSparqlProviderImpl();
        this.testProvider1.setKey("http://example.org/test/provider/1");
        this.testProvider1.addIncludedInQueryType(this.testRegexInputQueryType1.getKey());
        this.testProvider1.addNamespace(this.testNamespaceEntry1.getKey());
        
        this.testProvider2 = new HttpSparqlProviderImpl();
        this.testProvider2.setKey("http://example.org/test/provider/2");
        this.testProvider2.addIncludedInQueryType(this.testRegexInputQueryType2.getKey());
        this.testProvider2.addNamespace(this.testNamespaceEntry2.getKey());
        
        this.testProvider3 = new HttpSparqlProviderImpl();
        this.testProvider3.setKey("http://example.org/test/provider/2");
        this.testProvider3.addIncludedInQueryType(this.testRegexInputQueryType3.getKey());
        this.testProvider3.addEndpointUrl("http://testendpointurl.net/${input_1}/goingwell/${input_2}");
        this.testProvider2.addNamespace(this.testNamespaceEntry1.getKey());
        
        this.testAttributeList1 = new HashMap<String, String>();
        this.testAttributeList1.put(Constants.QUERY, "alternateNs:alternateNsUniqueId");
        
        this.testAttributeList2 = new HashMap<String, String>();
        this.testAttributeList2.put(Constants.QUERY, "otherNs:otherNsUniqueId");
        this.testAttributeList2.put(Constants.TEMPLATE_KEY_DEFAULT_HOST_ADDRESS, "http://my.example.org/");
        
        this.testAttributeList3 = new HashMap<String, String>();
        this.testAttributeList3.put(Constants.QUERY, "alternateNs:alternateNsUniqueId");
        this.testAttributeList2.put(Constants.TEMPLATE_KEY_DEFAULT_HOST_ADDRESS, "http://my.example.org/");
        
        this.testIncludedProfiles = new ArrayList<Profile>(1);
        this.testRecogniseImplicitRdfRuleInclusions = true;
        this.testIncludeNonProfileMatchedRdfRules = true;
        this.testConvertAlternateToPreferredPrefix = true;
        
        this.testLocalSettings1 =
                new Settings("/testconfigs/querycreatortestconfig-base.n3", "text/rdf+n3",
                        "http://example.org/test/config/querycreator-1");
        
        this.testLocalSettings1.addQueryType(this.testRegexInputQueryType1);
        this.testLocalSettings1.addProvider(this.testProvider1);
        this.testLocalSettings1.addNamespaceEntry(this.testNamespaceEntry1);
        
        this.testLocalSettings2 =
                new Settings("/testconfigs/querycreatortestconfig-base.n3", "text/rdf+n3",
                        "http://example.org/test/config/querycreator-1");
        
        this.testLocalSettings2.addQueryType(this.testRegexInputQueryType2);
        this.testLocalSettings2.addProvider(this.testProvider2);
        this.testLocalSettings2.addNamespaceEntry(this.testNamespaceEntry2);
        
        this.testLocalSettings3 =
                new Settings("/testconfigs/querycreatortestconfig-base.n3", "text/rdf+n3",
                        "http://example.org/test/config/querycreator-1");
        
        this.testLocalSettings3.addQueryType(this.testRegexInputQueryType3);
        this.testLocalSettings3.addProvider(this.testProvider3);
        this.testLocalSettings3.addNamespaceEntry(this.testNamespaceEntry1);
        
        this.testNamespaceInputVariables1 = new HashMap<String, Collection<NamespaceEntry>>();
        this.testNamespaceEntries1 = new ArrayList<NamespaceEntry>();
        this.testNamespaceEntries1.add(this.testNamespaceEntry1);
        this.testNamespaceInputVariables1.put("input_1", this.testNamespaceEntries1);
        
        this.testNamespaceInputVariables2 = new HashMap<String, Collection<NamespaceEntry>>();
        this.testNamespaceEntries2 = new ArrayList<NamespaceEntry>();
        this.testNamespaceEntries2.add(this.testNamespaceEntry2);
        this.testNamespaceInputVariables2.put("input_1", this.testNamespaceEntries2);
        
        this.testNamespaceInputVariables3 = new HashMap<String, Collection<NamespaceEntry>>();
        this.testNamespaceEntries3 = new ArrayList<NamespaceEntry>();
        this.testNamespaceEntries3.add(this.testNamespaceEntry1);
        this.testNamespaceInputVariables3.put("input_1", this.testNamespaceEntries3);
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        this.testValueFactory = null;
        
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.QueryCreator#createQuery(org.queryall.api.querytype.QueryType, org.queryall.api.provider.Provider, java.util.Map, java.util.List, boolean, boolean, boolean, org.queryall.api.base.QueryAllConfiguration, java.util.Map)}
     * .
     * @throws QueryAllException 
     */
    @Test
    public void testCreateQuery() throws QueryAllException
    {
        final String result1 =
                QueryCreator.createQuery(this.testRegexInputQueryType1, this.testProvider1, this.testAttributeList1,
                        this.testIncludedProfiles, this.testRecogniseImplicitRdfRuleInclusions,
                        this.testIncludeNonProfileMatchedRdfRules, this.testConvertAlternateToPreferredPrefix,
                        this.testLocalSettings1, this.testNamespaceInputVariables1);
        
        Assert.assertEquals(
                "query 1 was not as expected",
                "Select * Where { <http://example.org/ns/myPreferredNamespace> dc:identifier \"alternateNsUniqueId\" . }",
                result1);
        
        final String result2 =
                QueryCreator.createQuery(this.testRegexInputQueryType2, this.testProvider2, this.testAttributeList2,
                        this.testIncludedProfiles, this.testRecogniseImplicitRdfRuleInclusions,
                        this.testIncludeNonProfileMatchedRdfRules, this.testConvertAlternateToPreferredPrefix,
                        this.testLocalSettings2, this.testNamespaceInputVariables2);
        
        Assert.assertEquals(
                "query 2 was not as expected",
                "Select * Where { <http://other.example.org/myOtherNamespace/otherNsUniqueId> dc:identifier \"http://other.example.org/myOtherNamespace/otherNsUniqueId\" . }",
                result2);
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.QueryCreator#createStaticRdfXmlString(org.queryall.api.querytype.QueryType, org.queryall.api.querytype.OutputQueryType, org.queryall.api.provider.Provider, java.util.Map, java.util.Map, java.util.List, boolean, boolean, boolean, org.queryall.api.base.QueryAllConfiguration)}
     * .
     * @throws QueryAllException 
     */
    @Test
    @Ignore
    public void testCreateStaticRdfXmlString() throws QueryAllException
    {
        Assert.assertEquals("testsomething", QueryCreator.createStaticRdfXmlString(this.testRegexInputQueryType3,
                this.testRegexInputQueryType3, this.testProvider3, this.testAttributeList3,
                this.testNamespaceInputVariables3, this.testIncludedProfiles,
                this.testRecogniseImplicitRdfRuleInclusions, this.testIncludeNonProfileMatchedRdfRules,
                this.testConvertAlternateToPreferredPrefix, this.testLocalSettings3));
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.QueryCreator#doReplacementsOnString(java.util.Map, java.lang.String, org.queryall.api.querytype.QueryType, org.queryall.api.querytype.QueryType, org.queryall.api.provider.Provider, java.util.Map, java.util.Map, java.util.List, boolean, boolean, boolean, org.queryall.api.base.QueryAllConfiguration)}
     * .
     */
    @Test
    @Ignore
    public void testDoReplacementsOnString()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.QueryCreator#getAttributeListFor(org.queryall.api.querytype.QueryType, org.queryall.api.provider.Provider, java.util.Map, java.lang.String, java.lang.String, int, org.queryall.api.base.QueryAllConfiguration)}
     * .
     */
    @Test
    @Ignore
    public void testGetAttributeListFor()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.QueryCreator#matchAndReplaceInputVariablesForQueryType(org.queryall.api.querytype.QueryType, java.util.Map, java.lang.String, java.util.List, boolean, java.util.Map, org.queryall.api.provider.Provider)}
     * .
     */
    @Test
    @Ignore
    public void testMatchAndReplaceInputVariablesForQueryType()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.QueryCreator#replaceAttributesOnEndpointUrl(java.lang.String, org.queryall.api.querytype.QueryType, org.queryall.api.provider.Provider, java.util.Map, java.util.List, boolean, boolean, boolean, org.queryall.api.base.QueryAllConfiguration, java.util.Map)}
     * .
     */
    @Test
    @Ignore
    public void testReplaceAttributesOnEndpointUrl()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.QueryCreator#replaceTags(java.lang.String, java.util.Map, java.util.regex.Pattern)}
     * .
     */
    @Test
    @Ignore
    public void testReplaceTags()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
}
