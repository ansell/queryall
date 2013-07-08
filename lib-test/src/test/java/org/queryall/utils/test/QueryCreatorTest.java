/**
 * 
 */
package org.queryall.utils.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import org.queryall.api.provider.HttpProvider;
import org.queryall.api.provider.Provider;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.WebappConfig;
import org.queryall.exception.QueryAllException;
import org.queryall.impl.namespace.NamespaceEntryImpl;
import org.queryall.impl.provider.HttpSparqlProviderImpl;
import org.queryall.impl.querytype.RegexInputQueryTypeImpl;
import org.queryall.query.QueryCreator;
import org.queryall.utils.Settings;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class QueryCreatorTest
{
    private List<String> emptySpecialInstructions;
    
    private Map<String, String> testAttributeList1;
    private Map<String, String> testAttributeList2;
    private Map<String, String> testAttributeList3;
    
    private boolean testConvertAlternateToPreferredPrefix;
    
    private List<Profile> testIncludedProfiles;
    
    private boolean testIncludeNonProfileMatchedRdfRules;
    
    private QueryAllConfiguration testLocalSettings1;
    private QueryAllConfiguration testLocalSettings2;
    private QueryAllConfiguration testLocalSettings3;
    
    private Collection<NamespaceEntry> testNamespaceEntries1;
    private Collection<NamespaceEntry> testNamespaceEntries2;
    private Collection<NamespaceEntry> testNamespaceEntries3;
    
    private NamespaceEntry testNamespaceEntry1;
    private NamespaceEntry testNamespaceEntry2;
    
    private Map<String, Collection<NamespaceEntry>> testNamespaceInputVariables1;
    private Map<String, Collection<NamespaceEntry>> testNamespaceInputVariables2;
    private Map<String, Collection<NamespaceEntry>> testNamespaceInputVariables3;
    
    private Provider testProvider1;
    private Provider testProvider2;
    private HttpProvider testHttpProvider3;
    
    private boolean testRecogniseImplicitRdfRuleInclusions;
    
    private RegexInputQueryTypeImpl testRegexInputQueryType1;
    private RegexInputQueryTypeImpl testRegexInputQueryType2;
    private RegexInputQueryTypeImpl testRegexInputQueryType3;
    
    private ValueFactory vf;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        this.vf = new ValueFactoryImpl();
        
        this.testNamespaceEntry1 = new NamespaceEntryImpl();
        this.testNamespaceEntry1.setKey("http://example.org/test/namespace/1");
        this.testNamespaceEntry1.setPreferredPrefix("myPreferredNamespace");
        this.testNamespaceEntry1.addAlternativePrefix("alternateNs");
        this.testNamespaceEntry1.setSeparator(":");
        this.testNamespaceEntry1.setAuthority(this.vf.createURI("http://my.example.org/"));
        
        this.testNamespaceEntry2 = new NamespaceEntryImpl();
        this.testNamespaceEntry2.setKey("http://example.org/test/namespace/2");
        this.testNamespaceEntry2.setPreferredPrefix("myOtherNamespace");
        this.testNamespaceEntry2.addAlternativePrefix("otherNs");
        this.testNamespaceEntry2.setSeparator("/");
        this.testNamespaceEntry2.setAuthority(this.vf.createURI("http://other.example.org/"));
        
        this.testRegexInputQueryType1 = new RegexInputQueryTypeImpl();
        this.testRegexInputQueryType1.setKey("http://example.org/test/query/1");
        this.testRegexInputQueryType1.addNamespaceInputTag("input_1");
        this.testRegexInputQueryType1.setIsNamespaceSpecific(true);
        this.testRegexInputQueryType1.addNamespaceToHandle(this.testNamespaceEntry1.getKey());
        this.testRegexInputQueryType1.setInputRegex("^([\\w-]+):(.+)$");
        this.testRegexInputQueryType1
                .setProcessingTemplateString("Select * Where { <http://example.org/ns/${input_1}> dc:identifier \"${input_2}\" . }");
        
        this.testRegexInputQueryType2 = new RegexInputQueryTypeImpl();
        this.testRegexInputQueryType2.setKey("http://example.org/test/query/2");
        this.testRegexInputQueryType2.addNamespaceInputTag("input_1");
        this.testRegexInputQueryType2.setStandardUriTemplateString("${authority}${input_1}${separator}${input_2}");
        this.testRegexInputQueryType2.setIsNamespaceSpecific(true);
        this.testRegexInputQueryType2.addNamespaceToHandle(this.testNamespaceEntry2.getKey());
        this.testRegexInputQueryType2.setInputRegex("^([\\w-]+):(.+)$");
        this.testRegexInputQueryType2
                .setProcessingTemplateString("Select * Where { <${normalisedStandardUri}> dc:identifier \"${endpointSpecificUri}\" . }");
        
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
        
        this.testHttpProvider3 = new HttpSparqlProviderImpl();
        this.testHttpProvider3.setKey("http://example.org/test/provider/2");
        this.testHttpProvider3.addIncludedInQueryType(this.testRegexInputQueryType3.getKey());
        this.testHttpProvider3.addEndpointUrl("http://testendpointurl.net/${input_1}/goingwell/${input_2}");
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
        
        this.testLocalSettings1 = new Settings();
        this.testLocalSettings1.setString(WebappConfig.HOST_NAME, "1.example.org");
        this.testLocalSettings1.setBoolean(WebappConfig.CONVERT_ALTERNATE_NAMESPACE_PREFIXES_TO_PREFERRED, true);
        this.testLocalSettings1.addQueryType(this.testRegexInputQueryType1);
        this.testLocalSettings1.addProvider(this.testProvider1);
        this.testLocalSettings1.addNamespaceEntry(this.testNamespaceEntry1);
        
        this.testLocalSettings2 = new Settings();
        this.testLocalSettings2.setString(WebappConfig.HOST_NAME, "1.example.org");
        this.testLocalSettings2.setBoolean(WebappConfig.CONVERT_ALTERNATE_NAMESPACE_PREFIXES_TO_PREFERRED, true);
        this.testLocalSettings2.addQueryType(this.testRegexInputQueryType2);
        this.testLocalSettings2.addProvider(this.testProvider2);
        this.testLocalSettings2.addNamespaceEntry(this.testNamespaceEntry2);
        
        this.testLocalSettings3 = new Settings();
        this.testLocalSettings3.setString(WebappConfig.HOST_NAME, "1.example.org");
        this.testLocalSettings3.setBoolean(WebappConfig.CONVERT_ALTERNATE_NAMESPACE_PREFIXES_TO_PREFERRED, true);
        
        this.testLocalSettings3.addQueryType(this.testRegexInputQueryType3);
        this.testLocalSettings3.addProvider(this.testHttpProvider3);
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
        
        this.emptySpecialInstructions = Collections.emptyList();
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        this.vf = null;
        
        this.testAttributeList1 = null;
        this.testAttributeList2 = null;
        this.testAttributeList3 = null;
        
        this.testIncludedProfiles = null;
        
        this.testLocalSettings1 = null;
        this.testLocalSettings2 = null;
        this.testLocalSettings3 = null;
        
        this.testNamespaceEntries1 = null;
        this.testNamespaceEntries2 = null;
        this.testNamespaceEntries3 = null;
        
        this.testNamespaceEntry1 = null;
        this.testNamespaceEntry2 = null;
        
        this.testNamespaceInputVariables1 = null;
        this.testNamespaceInputVariables2 = null;
        this.testNamespaceInputVariables3 = null;
        
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.QueryCreator#createQuery(org.queryall.api.querytype.QueryType, org.queryall.api.provider.Provider, java.util.Map, java.util.List, boolean, boolean, boolean, org.queryall.api.base.QueryAllConfiguration, java.util.Map)}
     * .
     * 
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
     * 
     * @throws QueryAllException
     */
    @Ignore
    @Test
    public void testCreateStaticRdfXmlString() throws QueryAllException
    {
        Assert.assertEquals("testsomething", QueryCreator.createStaticRdfXmlString(this.testRegexInputQueryType3,
                this.testRegexInputQueryType3, this.testHttpProvider3, this.testAttributeList3,
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
     * {@link org.queryall.query.QueryCreator#getAttributeListFor(org.queryall.api.querytype.QueryType, org.queryall.api.provider.Provider, java.util.Map, java.lang.String, java.lang.String, int, String, String, String)}
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
    public void testMatchAndReplaceInputVariablesForQueryTypeEmptyTemplate()
    {
        final String resultString =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(this.testRegexInputQueryType1,
                        this.testAttributeList1, "", this.emptySpecialInstructions, false,
                        this.testNamespaceInputVariables1, this.testProvider1, this.testLocalSettings1);
        
        Assert.assertEquals("", resultString);
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.QueryCreator#matchAndReplaceInputVariablesForQueryType(org.queryall.api.querytype.QueryType, java.util.Map, java.lang.String, java.util.List, boolean, java.util.Map, org.queryall.api.provider.Provider)}
     * .
     */
    @Test
    public void testMatchAndReplaceInputVariablesForQueryTypeFourVariableAlternateNs2()
    {
        final String resultString =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(this.testRegexInputQueryType2,
                        this.testAttributeList2, "${authority}${input_1}${separator}${input_2}",
                        this.emptySpecialInstructions, false, this.testNamespaceInputVariables2, this.testProvider2,
                        this.testLocalSettings2);
        
        Assert.assertEquals("http://other.example.org/otherNs/otherNsUniqueId", resultString);
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.QueryCreator#matchAndReplaceInputVariablesForQueryType(org.queryall.api.querytype.QueryType, java.util.Map, java.lang.String, java.util.List, boolean, java.util.Map, org.queryall.api.provider.Provider)}
     * .
     */
    @Test
    public void testMatchAndReplaceInputVariablesForQueryTypeFourVariablePreferredNs2()
    {
        final String resultString =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(this.testRegexInputQueryType2,
                        this.testAttributeList2, "${authority}${input_1}${separator}${input_2}",
                        this.emptySpecialInstructions, true, this.testNamespaceInputVariables2, this.testProvider2,
                        this.testLocalSettings2);
        
        Assert.assertEquals("http://other.example.org/myOtherNamespace/otherNsUniqueId", resultString);
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.QueryCreator#matchAndReplaceInputVariablesForQueryType(org.queryall.api.querytype.QueryType, java.util.Map, java.lang.String, java.util.List, boolean, java.util.Map, org.queryall.api.provider.Provider)}
     * .
     */
    @Test
    public void testMatchAndReplaceInputVariablesForQueryTypeSingleVariableAlternateNs1()
    {
        final String resultString =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(this.testRegexInputQueryType1,
                        this.testAttributeList1, "${input_1}", this.emptySpecialInstructions, false,
                        this.testNamespaceInputVariables1, this.testProvider1, this.testLocalSettings1);
        
        Assert.assertEquals("alternateNs", resultString);
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.QueryCreator#matchAndReplaceInputVariablesForQueryType(org.queryall.api.querytype.QueryType, java.util.Map, java.lang.String, java.util.List, boolean, java.util.Map, org.queryall.api.provider.Provider)}
     * .
     */
    @Test
    public void testMatchAndReplaceInputVariablesForQueryTypeSingleVariableAlternateNs2()
    {
        final String resultString =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(this.testRegexInputQueryType2,
                        this.testAttributeList2, "${input_1}", this.emptySpecialInstructions, false,
                        this.testNamespaceInputVariables2, this.testProvider2, this.testLocalSettings2);
        
        Assert.assertEquals("otherNs", resultString);
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.QueryCreator#matchAndReplaceInputVariablesForQueryType(org.queryall.api.querytype.QueryType, java.util.Map, java.lang.String, java.util.List, boolean, java.util.Map, org.queryall.api.provider.Provider)}
     * .
     */
    @Test
    public void testMatchAndReplaceInputVariablesForQueryTypeSingleVariablePreferredNs1()
    {
        final String resultString =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(this.testRegexInputQueryType1,
                        this.testAttributeList1, "${input_1}", this.emptySpecialInstructions, true,
                        this.testNamespaceInputVariables1, this.testProvider1, this.testLocalSettings1);
        
        Assert.assertEquals("myPreferredNamespace", resultString);
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.QueryCreator#matchAndReplaceInputVariablesForQueryType(org.queryall.api.querytype.QueryType, java.util.Map, java.lang.String, java.util.List, boolean, java.util.Map, org.queryall.api.provider.Provider)}
     * .
     */
    @Test
    public void testMatchAndReplaceInputVariablesForQueryTypeSingleVariablePreferredNs2()
    {
        final String resultString =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(this.testRegexInputQueryType2,
                        this.testAttributeList2, "${input_1}", this.emptySpecialInstructions, true,
                        this.testNamespaceInputVariables2, this.testProvider2, this.testLocalSettings2);
        
        Assert.assertEquals("myOtherNamespace", resultString);
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.QueryCreator#matchAndReplaceInputVariablesForQueryType(org.queryall.api.querytype.QueryType, java.util.Map, java.lang.String, java.util.List, boolean, java.util.Map, org.queryall.api.provider.Provider)}
     * .
     */
    @Test
    public void testMatchAndReplaceInputVariablesForQueryTypeThreeVariableAlternateNs2()
    {
        final String resultString =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(this.testRegexInputQueryType2,
                        this.testAttributeList2, "${authority}${input_1}${separator}", this.emptySpecialInstructions,
                        false, this.testNamespaceInputVariables2, this.testProvider2, this.testLocalSettings2);
        
        Assert.assertEquals("http://other.example.org/otherNs/", resultString);
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.QueryCreator#matchAndReplaceInputVariablesForQueryType(org.queryall.api.querytype.QueryType, java.util.Map, java.lang.String, java.util.List, boolean, java.util.Map, org.queryall.api.provider.Provider)}
     * .
     */
    @Test
    public void testMatchAndReplaceInputVariablesForQueryTypeThreeVariablePreferredNs2()
    {
        final String resultString =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(this.testRegexInputQueryType2,
                        this.testAttributeList2, "${authority}${input_1}${separator}", this.emptySpecialInstructions,
                        true, this.testNamespaceInputVariables2, this.testProvider2, this.testLocalSettings2);
        
        Assert.assertEquals("http://other.example.org/myOtherNamespace/", resultString);
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.QueryCreator#matchAndReplaceInputVariablesForQueryType(org.queryall.api.querytype.QueryType, java.util.Map, java.lang.String, java.util.List, boolean, java.util.Map, org.queryall.api.provider.Provider)}
     * .
     */
    @Test
    public void testMatchAndReplaceInputVariablesForQueryTypeTwoVariableAlternateNs1()
    {
        final String resultString =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(this.testRegexInputQueryType1,
                        this.testAttributeList1, "${input_1}${separator}", this.emptySpecialInstructions, false,
                        this.testNamespaceInputVariables1, this.testProvider1, this.testLocalSettings1);
        
        Assert.assertEquals("alternateNs:", resultString);
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.QueryCreator#matchAndReplaceInputVariablesForQueryType(org.queryall.api.querytype.QueryType, java.util.Map, java.lang.String, java.util.List, boolean, java.util.Map, org.queryall.api.provider.Provider)}
     * .
     */
    @Test
    public void testMatchAndReplaceInputVariablesForQueryTypeTwoVariableAlternateNs2()
    {
        final String resultString =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(this.testRegexInputQueryType2,
                        this.testAttributeList2, "${input_1}${separator}", this.emptySpecialInstructions, false,
                        this.testNamespaceInputVariables2, this.testProvider2, this.testLocalSettings2);
        
        Assert.assertEquals("otherNs/", resultString);
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.QueryCreator#matchAndReplaceInputVariablesForQueryType(org.queryall.api.querytype.QueryType, java.util.Map, java.lang.String, java.util.List, boolean, java.util.Map, org.queryall.api.provider.Provider)}
     * .
     */
    @Test
    public void testMatchAndReplaceInputVariablesForQueryTypeTwoVariablePreferredNs1()
    {
        final String resultString =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(this.testRegexInputQueryType1,
                        this.testAttributeList1, "${input_1}${separator}", this.emptySpecialInstructions, true,
                        this.testNamespaceInputVariables1, this.testProvider1, this.testLocalSettings1);
        
        Assert.assertEquals("myPreferredNamespace:", resultString);
    }
    
    /**
     * Test method for
     * {@link org.queryall.query.QueryCreator#matchAndReplaceInputVariablesForQueryType(org.queryall.api.querytype.QueryType, java.util.Map, java.lang.String, java.util.List, boolean, java.util.Map, org.queryall.api.provider.Provider)}
     * .
     */
    @Test
    public void testMatchAndReplaceInputVariablesForQueryTypeTwoVariablePreferredNs2()
    {
        final String resultString =
                QueryCreator.matchAndReplaceInputVariablesForQueryType(this.testRegexInputQueryType2,
                        this.testAttributeList2, "${input_1}${separator}", this.emptySpecialInstructions, true,
                        this.testNamespaceInputVariables2, this.testProvider2, this.testLocalSettings2);
        
        Assert.assertEquals("myOtherNamespace/", resultString);
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
        // TODO: Test after implementation is complete
        
        final Map<String, String> myTestMap = new TreeMap<String, String>();
        
        myTestMap.put("${input_1}", "MyInput1");
        myTestMap.put("${inputUrlEncoded_privatelowercase_input_1}", "myinput1");
        myTestMap.put("${input_2}", "YourInput2");
        myTestMap.put("${inputUrlEncoded_privatelowercase_input_2}", "yourinput2");
        
        final String returnString =
                QueryCreator
                        .replaceTags(
                                "${input_1}:--:${inputUrlEncoded_privatelowercase_input_2}:--:${input_2}:--:${inputUrlEncoded_privatelowercase_input_1}",
                                myTestMap, this.testLocalSettings1.getTagPattern());
        
        // log.warn("QueryCreator.testReplaceMethod returnString="+returnString);
        
        Assert.assertEquals("MyInput1:--:yourinput2:--:YourInput2:--:myinput1", returnString);
    }
    
}
