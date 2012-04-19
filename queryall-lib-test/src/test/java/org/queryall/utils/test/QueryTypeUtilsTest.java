/**
 * 
 */
package org.queryall.utils.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.profile.Profile;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.querytype.RegexInputQueryType;
import org.queryall.api.utils.Constants;
import org.queryall.impl.namespace.NamespaceEntryImpl;
import org.queryall.impl.querytype.RegexInputQueryTypeImpl;
import org.queryall.utils.QueryTypeUtils;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class QueryTypeUtilsTest
{
    private RegexInputQueryType testRegexInputQueryType1;
    private NamespaceEntry testNamespaceEntry1;
    private ValueFactory testValueFactory;
    // private Map<String, String> testInputParametersPreferred;
    // private Map<String, String> testInputParametersAlternate;
    // private Map<String, String> testInputParametersFalse;
    private Map<String, Collection<URI>> testNamespacePrefixMap;
    private Map<String, Collection<URI>> testQueryParameterMatchesPreferred;
    private Map<String, Collection<URI>> testQueryParameterMatchesAlternate;
    private Map<URI, QueryType> testAllQueryTypes;
    private Map<URI, NamespaceEntry> testAllNamespaceEntries;
    private Map<String, String> testParametersRawPreferredConverted;
    private Map<String, String> testParametersRawAlternateConverted;
    private Map<String, String> testParametersRawFalse;
    private Map<String, String> testParametersRawPreferredUnconverted;
    private Map<String, String> testParametersRawAlternateUnconverted;
    private NamespaceEntry testNamespaceEntry2;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        this.testValueFactory = new ValueFactoryImpl();
        
        this.testNamespaceEntry1 = new NamespaceEntryImpl();
        this.testNamespaceEntry1.setKey("http://example.org/test/namespace/1");
        this.testNamespaceEntry1.setConvertQueriesToPreferredPrefix(true);
        this.testNamespaceEntry1.setPreferredPrefix("myPreferredNamespace");
        this.testNamespaceEntry1.addAlternativePrefix("alternateNs");
        this.testNamespaceEntry1.setSeparator(":");
        this.testNamespaceEntry1.setAuthority(this.testValueFactory.createURI("http://my.example.org/"));
        
        this.testNamespaceEntry2 = new NamespaceEntryImpl();
        this.testNamespaceEntry2.setKey("http://example.org/test/namespace/2");
        this.testNamespaceEntry2.setConvertQueriesToPreferredPrefix(true);
        this.testNamespaceEntry2.setPreferredPrefix("myCurrentPreferredNamespace");
        this.testNamespaceEntry2.addAlternativePrefix("validAlternateNs");
        this.testNamespaceEntry2.setSeparator(":");
        this.testNamespaceEntry2.setAuthority(this.testValueFactory.createURI("http://my.example.org/"));
        
        this.testRegexInputQueryType1 = new RegexInputQueryTypeImpl();
        this.testRegexInputQueryType1.setKey("http://example.org/test/querytype/1");
        this.testRegexInputQueryType1.setIsNamespaceSpecific(true);
        this.testRegexInputQueryType1.setHandleAllNamespaces(false);
        this.testRegexInputQueryType1.addNamespaceToHandle(this.testNamespaceEntry1.getKey());
        this.testRegexInputQueryType1.addNamespaceInputTag("input_1");
        this.testRegexInputQueryType1.addExpectedInputParameter("input_1");
        this.testRegexInputQueryType1.addExpectedInputParameter("input_2");
        this.testRegexInputQueryType1.addPublicIdentifierTag("input_1");
        this.testRegexInputQueryType1.setInputRegex("^([\\w-]+):(.+)$");
        
        this.testAllQueryTypes = new HashMap<URI, QueryType>();
        this.testAllQueryTypes.put(this.testRegexInputQueryType1.getKey(), this.testRegexInputQueryType1);
        
        this.testAllNamespaceEntries = new HashMap<URI, NamespaceEntry>();
        this.testAllNamespaceEntries.put(this.testNamespaceEntry1.getKey(), this.testNamespaceEntry1);
        
        this.testParametersRawPreferredConverted = new HashMap<String, String>();
        this.testParametersRawPreferredConverted.put(Constants.QUERY, "myPreferredNamespace:identifier1234567");
        
        this.testParametersRawAlternateConverted = new HashMap<String, String>();
        this.testParametersRawAlternateConverted.put(Constants.QUERY, "alternateNs:identifier7654321");
        
        this.testParametersRawPreferredUnconverted = new HashMap<String, String>();
        this.testParametersRawPreferredUnconverted.put(Constants.QUERY, "myCurrentPreferredNamespace:identifier56789");
        
        this.testParametersRawAlternateUnconverted = new HashMap<String, String>();
        this.testParametersRawAlternateUnconverted.put(Constants.QUERY, "validAlternateNs:identifier98765");
        
        this.testParametersRawFalse = new HashMap<String, String>();
        this.testParametersRawFalse.put(Constants.QUERY, "unknownfalsenamespace:identifier654");
        
        final Collection<URI> preferredNamespaces = new ArrayList<URI>(1);
        preferredNamespaces.add(this.testNamespaceEntry1.getKey());
        final Collection<URI> alternateNamespaces = new ArrayList<URI>(1);
        alternateNamespaces.add(this.testNamespaceEntry1.getKey());
        
        final Collection<URI> preferredUnconvertedNamespaces = new ArrayList<URI>(1);
        preferredUnconvertedNamespaces.add(this.testNamespaceEntry2.getKey());
        final Collection<URI> alternateUnconvertedNamespaces = new ArrayList<URI>(1);
        alternateUnconvertedNamespaces.add(this.testNamespaceEntry2.getKey());
        
        this.testNamespacePrefixMap = new HashMap<String, Collection<URI>>();
        this.testNamespacePrefixMap.put("myPreferredNamespace", preferredNamespaces);
        this.testNamespacePrefixMap.put("alternateNs", alternateNamespaces);
        this.testNamespacePrefixMap.put("myCurrentPreferredNamespace", preferredUnconvertedNamespaces);
        this.testNamespacePrefixMap.put("validAlternateNs", alternateUnconvertedNamespaces);
        
        this.testQueryParameterMatchesPreferred = new HashMap<String, Collection<URI>>();
        this.testQueryParameterMatchesPreferred.put("input_1", preferredNamespaces);
        
        this.testQueryParameterMatchesAlternate = new HashMap<String, Collection<URI>>();
        this.testQueryParameterMatchesAlternate.put("input_1", alternateNamespaces);
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.QueryTypeUtils#getQueryTypesMatchingQuery(java.util.Map, java.util.List, java.util.Map, java.util.Map, java.util.Map, boolean, boolean)}
     * .
     */
    @Test
    public void testGetQueryTypesMatchingQueryAlternate()
    {
        final Map<QueryType, Map<String, Collection<NamespaceEntry>>> queryTypesMatchingQueryAlternate =
                QueryTypeUtils.getQueryTypesMatchingQuery(this.testParametersRawAlternateConverted,
                        new ArrayList<Profile>(0), this.testAllQueryTypes, this.testNamespacePrefixMap,
                        this.testAllNamespaceEntries, true, true);
        
        Assert.assertEquals("alternate namespaces did not generate a single result", 1,
                queryTypesMatchingQueryAlternate.size());
        
        Assert.assertTrue("Query type was not in the alternate namespaces results set",
                queryTypesMatchingQueryAlternate.containsKey(this.testRegexInputQueryType1));
        
        Assert.assertEquals("alternate namespaces result did not have a parameter to namespace map attached to it", 1,
                queryTypesMatchingQueryAlternate.get(this.testRegexInputQueryType1).size());
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.QueryTypeUtils#getQueryTypesMatchingQuery(java.util.Map, java.util.List, java.util.Map, java.util.Map, java.util.Map, boolean, boolean)}
     * .
     */
    @Test
    public void testGetQueryTypesMatchingQueryFalse()
    {
        final Map<QueryType, Map<String, Collection<NamespaceEntry>>> queryTypesMatchingQueryFalse =
                QueryTypeUtils.getQueryTypesMatchingQuery(this.testParametersRawFalse, new ArrayList<Profile>(0),
                        this.testAllQueryTypes, this.testNamespacePrefixMap, this.testAllNamespaceEntries, true, true);
        
        Assert.assertEquals("false namespaces did not generated an empty result", 0,
                queryTypesMatchingQueryFalse.size());
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.QueryTypeUtils#getQueryTypesMatchingQuery(java.util.Map, java.util.List, java.util.Map, java.util.Map, java.util.Map, boolean, boolean)}
     * .
     */
    @Test
    public void testGetQueryTypesMatchingQueryPreferred()
    {
        final Map<QueryType, Map<String, Collection<NamespaceEntry>>> queryTypesMatchingQueryPreferred =
                QueryTypeUtils.getQueryTypesMatchingQuery(this.testParametersRawPreferredConverted,
                        new ArrayList<Profile>(0), this.testAllQueryTypes, this.testNamespacePrefixMap,
                        this.testAllNamespaceEntries, true, true);
        
        Assert.assertEquals("preferred namespaces did not generate a single result", 1,
                queryTypesMatchingQueryPreferred.size());
        
        Assert.assertTrue("Query type was not in the preferred namespaces results set",
                queryTypesMatchingQueryPreferred.containsKey(this.testRegexInputQueryType1));
        
        Assert.assertEquals("preferred namespaces result did not have a parameter to namespace map attached to it", 1,
                queryTypesMatchingQueryPreferred.get(this.testRegexInputQueryType1).size());
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.QueryTypeUtils#getQueryTypesMatchingQuery(java.util.Map, java.util.List, java.util.Map, java.util.Map, java.util.Map, boolean, boolean)}
     * .
     */
    @Test
    public void testGetQueryTypesMatchingQueryUnconvertedAlternate()
    {
        final Map<QueryType, Map<String, Collection<NamespaceEntry>>> queryTypesMatchingQueryUnconvertedAlternate =
                QueryTypeUtils.getQueryTypesMatchingQuery(this.testParametersRawAlternateConverted,
                        new ArrayList<Profile>(0), this.testAllQueryTypes, this.testNamespacePrefixMap,
                        this.testAllNamespaceEntries, true, true);
        
        Assert.assertEquals("alternate namespaces did not generate a single result", 1,
                queryTypesMatchingQueryUnconvertedAlternate.size());
        
        Assert.assertTrue("Query type was not in the alternate namespaces results set",
                queryTypesMatchingQueryUnconvertedAlternate.containsKey(this.testRegexInputQueryType1));
        
        Assert.assertEquals("alternate namespaces result did not have a parameter to namespace map attached to it", 1,
                queryTypesMatchingQueryUnconvertedAlternate.get(this.testRegexInputQueryType1).size());
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.QueryTypeUtils#getQueryTypesMatchingQuery(java.util.Map, java.util.List, java.util.Map, java.util.Map, java.util.Map, boolean, boolean)}
     * .
     */
    @Test
    public void testGetQueryTypesMatchingQueryUnconvertedPreferred()
    {
        final Map<QueryType, Map<String, Collection<NamespaceEntry>>> queryTypesMatchingQueryUnconvertedPreferred =
                QueryTypeUtils.getQueryTypesMatchingQuery(this.testParametersRawPreferredConverted,
                        new ArrayList<Profile>(0), this.testAllQueryTypes, this.testNamespacePrefixMap,
                        this.testAllNamespaceEntries, true, true);
        
        Assert.assertEquals("preferred namespaces did not generate a single result", 1,
                queryTypesMatchingQueryUnconvertedPreferred.size());
        
        Assert.assertTrue("Query type was not in the preferred namespaces results set",
                queryTypesMatchingQueryUnconvertedPreferred.containsKey(this.testRegexInputQueryType1));
        
        Assert.assertEquals("preferred namespaces result did not have a parameter to namespace map attached to it", 1,
                queryTypesMatchingQueryUnconvertedPreferred.get(this.testRegexInputQueryType1).size());
    }
    
    /**
     * Test method for
     * {@link org.queryall.utils.QueryTypeUtils#namespacesMatchesForQueryParameters(org.queryall.api.querytype.QueryType, java.util.Map, java.util.Map)}
     * .
     */
    @Test
    public void testNamespacesMatchesForQueryParameters()
    {
        final Map<String, Collection<URI>> namespacesMatches1 =
                QueryTypeUtils.namespacesMatchesForQueryParameters(this.testRegexInputQueryType1,
                        this.testParametersRawPreferredConverted, this.testNamespacePrefixMap);
        
        Assert.assertEquals("preferred namespaces did not generate a single result", 1, namespacesMatches1.size());
        
        final Map<String, Collection<URI>> namespacesMatches2 =
                QueryTypeUtils.namespacesMatchesForQueryParameters(this.testRegexInputQueryType1,
                        this.testParametersRawAlternateConverted, this.testNamespacePrefixMap);
        
        Assert.assertEquals("alternate namespaces did not generate a single result", 1, namespacesMatches2.size());
        
        final Map<String, Collection<URI>> namespacesMatchesUnconverted1 =
                QueryTypeUtils.namespacesMatchesForQueryParameters(this.testRegexInputQueryType1,
                        this.testParametersRawPreferredUnconverted, this.testNamespacePrefixMap);
        
        Assert.assertEquals("preferred namespaces unconverted did not generate a single result", 1,
                namespacesMatchesUnconverted1.size());
        
        final Map<String, Collection<URI>> namespacesMatchesUnconverted2 =
                QueryTypeUtils.namespacesMatchesForQueryParameters(this.testRegexInputQueryType1,
                        this.testParametersRawAlternateUnconverted, this.testNamespacePrefixMap);
        
        Assert.assertEquals("alternate namespaces unconverted did not generate a single result", 1,
                namespacesMatchesUnconverted2.size());
        
        final Map<String, Collection<URI>> namespacesMatchesFalse =
                QueryTypeUtils.namespacesMatchesForQueryParameters(this.testRegexInputQueryType1,
                        this.testParametersRawFalse, this.testNamespacePrefixMap);
        
        Assert.assertEquals("false namespaces generated a result", 0, namespacesMatchesFalse.size());
        
    }
    
}
