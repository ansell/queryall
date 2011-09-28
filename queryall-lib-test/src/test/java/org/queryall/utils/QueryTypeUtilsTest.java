/**
 * 
 */
package org.queryall.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class QueryTypeUtilsTest
{
    private RegexInputQueryType testRegexInputQueryType1;
    private NamespaceEntry testNamespaceEntry1;
    private ValueFactory testValueFactory;
//    private Map<String, String> testInputParametersPreferred;
//    private Map<String, String> testInputParametersAlternate;
//    private Map<String, String> testInputParametersFalse;
    private Map<String, Collection<URI>> testNamespacePrefixMap;
    private Map<String, Collection<URI>> testQueryParameterMatchesPreferred;
    private Map<String, Collection<URI>> testQueryParameterMatchesAlternate;
    private Map<URI, QueryType> testAllQueryTypes;
    private Map<URI, NamespaceEntry> testAllNamespaceEntries;
    private Map<String, String> testInputParametersRawPreferred;
    private Map<String, String> testInputParametersRawAlternate;
    private Map<String, String> testInputParametersRawFalse;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        this.testValueFactory = new ValueFactoryImpl();

        testNamespaceEntry1 = new NamespaceEntryImpl();
        testNamespaceEntry1.setKey("http://example.org/test/namespace/1");
        testNamespaceEntry1.setConvertQueriesToPreferredPrefix(true);
        testNamespaceEntry1.setPreferredPrefix("myPreferredNamespace");
        testNamespaceEntry1.addAlternativePrefix("alternateNs");
        testNamespaceEntry1.setSeparator(":");
        testNamespaceEntry1.setAuthority(testValueFactory.createURI("http://my.example.org/"));

        testRegexInputQueryType1 = new RegexInputQueryTypeImpl();
        testRegexInputQueryType1.setKey("http://example.org/test/querytype/1");
        testRegexInputQueryType1.setIsNamespaceSpecific(true);
        testRegexInputQueryType1.setHandleAllNamespaces(false);
        testRegexInputQueryType1.addNamespaceToHandle(testNamespaceEntry1.getKey());
        testRegexInputQueryType1.addNamespaceInputTag("input_1");
        testRegexInputQueryType1.addExpectedInputParameter("input_1");
        testRegexInputQueryType1.addExpectedInputParameter("input_2");
        testRegexInputQueryType1.addPublicIdentifierTag("input_1");
        testRegexInputQueryType1.setInputRegex("^([\\w-]+):(.+)$");
        
        testAllQueryTypes = new HashMap<URI, QueryType>();
        testAllQueryTypes.put(testRegexInputQueryType1.getKey(), testRegexInputQueryType1);
        
        testAllNamespaceEntries = new HashMap<URI, NamespaceEntry>();
        testAllNamespaceEntries.put(testNamespaceEntry1.getKey(), testNamespaceEntry1);
        
        testInputParametersRawPreferred = new HashMap<String, String>();
        testInputParametersRawPreferred.put(Constants.QUERY, "myPreferredNamespace:identifier1234567");
        // FIXME: Is this necessary in theory
//        testInputParametersRawPreferred.put("input_1", "myPreferredNamespace");
//        testInputParametersRawPreferred.put("input_2", "identifier1234567");

        testInputParametersRawAlternate = new HashMap<String, String>();
        testInputParametersRawAlternate.put(Constants.QUERY, "alternateNs:identifier7654321");
        // FIXME: Is this necessary in theory
//        testInputParametersRawAlternate.put("input_1", "alternateNs");
//        testInputParametersRawAlternate.put("input_2", "identifier7654321");

        testInputParametersRawFalse = new HashMap<String, String>();
        testInputParametersRawFalse.put(Constants.QUERY, "unknownfalsenamespace:identifier654");
        // FIXME: Is this necessary in theory
//        testInputParametersRawFalse.put("input_1", "unknownfalsenamespace");
//        testInputParametersRawFalse.put("input_2", "identifier654");

//        testInputParametersPreferred = new HashMap<String, String>();
//        testInputParametersPreferred.put("input_1", "myPreferredNamespace");
//        testInputParametersPreferred.put("input_2", "identifier1234567");

//        testInputParametersAlternate = new HashMap<String, String>();
//        testInputParametersAlternate.put("input_1", "alternateNs");
//        testInputParametersAlternate.put("input_2", "identifier7654321");

//        testInputParametersFalse = new HashMap<String, String>();
//        testInputParametersFalse.put("input_1", "unknownfalsenamespace");
//        testInputParametersFalse.put("input_2", "identifier654");
    
        Collection<URI> preferredNamespaces = new ArrayList<URI>(1);
        preferredNamespaces.add(testNamespaceEntry1.getKey());
        Collection<URI> alternateNamespaces = new ArrayList<URI>(1);
        alternateNamespaces.add(testNamespaceEntry1.getKey());

        testNamespacePrefixMap = new HashMap<String, Collection<URI>>();
        testNamespacePrefixMap.put("myPreferredNamespace", preferredNamespaces);
        testNamespacePrefixMap.put("alternateNs", alternateNamespaces);
        
        testQueryParameterMatchesPreferred = new HashMap<String, Collection<URI>>();
        testQueryParameterMatchesPreferred.put("input_1", preferredNamespaces);

        testQueryParameterMatchesAlternate = new HashMap<String, Collection<URI>>();
        testQueryParameterMatchesAlternate.put("input_1", alternateNamespaces);
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
    }
    
    /**
     * Test method for {@link org.queryall.utils.QueryTypeUtils#namespacesMatchesForQueryParameters(org.queryall.api.querytype.QueryType, java.util.Map, java.util.Map)}.
     */
    @Test
    public void testNamespacesMatchesForQueryParameters()
    {
        Map<String, Collection<URI>> namespacesMatches1 = QueryTypeUtils.namespacesMatchesForQueryParameters(testRegexInputQueryType1, testInputParametersRawPreferred, testNamespacePrefixMap);

        Assert.assertEquals("preferred namespaces did not generate a single result", 1, namespacesMatches1.size());

        Map<String, Collection<URI>> namespacesMatches2 = QueryTypeUtils.namespacesMatchesForQueryParameters(testRegexInputQueryType1, testInputParametersRawAlternate, testNamespacePrefixMap);
    
        Assert.assertEquals("alternate namespaces did not generate a single result", 1, namespacesMatches2.size());

        Map<String, Collection<URI>> namespacesMatchesFalse = QueryTypeUtils.namespacesMatchesForQueryParameters(testRegexInputQueryType1, testInputParametersRawFalse, testNamespacePrefixMap);
        
        Assert.assertEquals("false namespaces generated a result", 0, namespacesMatchesFalse.size());
        
    }
    
    /**
     * Test method for {@link org.queryall.utils.QueryTypeUtils#getQueryTypesMatchingQuery(java.util.Map, java.util.List, java.util.Map, java.util.Map, java.util.Map, boolean, boolean)}.
     */
    @Test
    public void testGetQueryTypesMatchingQuery()
    {
        Map<QueryType, Map<String, Collection<NamespaceEntry>>> queryTypesMatchingQueryPreferred = QueryTypeUtils.getQueryTypesMatchingQuery(testInputParametersRawPreferred, new ArrayList<Profile>(0), testAllQueryTypes, testNamespacePrefixMap, testAllNamespaceEntries, true, true);
        
        Assert.assertEquals("preferred namespaces did not generate a single result", 1, queryTypesMatchingQueryPreferred.size());
        
        Assert.assertTrue("Query type was not in the preferred namespaces results set", queryTypesMatchingQueryPreferred.containsKey(testRegexInputQueryType1));
        
        Assert.assertEquals("preferred namespaces result did not have a parameter to namespace map attached to it", 1, queryTypesMatchingQueryPreferred.get(testRegexInputQueryType1).size());

        Map<QueryType, Map<String, Collection<NamespaceEntry>>> queryTypesMatchingQueryAlternate = QueryTypeUtils.getQueryTypesMatchingQuery(testInputParametersRawAlternate, new ArrayList<Profile>(0), testAllQueryTypes, testNamespacePrefixMap, testAllNamespaceEntries, true, true);
        
        Assert.assertEquals("alternate namespaces did not generate a single result", 1, queryTypesMatchingQueryAlternate.size());

        Assert.assertTrue("Query type was not in the alternate namespaces results set", queryTypesMatchingQueryAlternate.containsKey(testRegexInputQueryType1));
        
        Assert.assertEquals("alternate namespaces result did not have a parameter to namespace map attached to it", 1, queryTypesMatchingQueryAlternate.get(testRegexInputQueryType1).size());

        Map<QueryType, Map<String, Collection<NamespaceEntry>>> queryTypesMatchingQueryFalse = QueryTypeUtils.getQueryTypesMatchingQuery(testInputParametersRawFalse, new ArrayList<Profile>(0), testAllQueryTypes, testNamespacePrefixMap, testAllNamespaceEntries, true, true);

        Assert.assertEquals("false namespaces did not generated a single result", 1, queryTypesMatchingQueryFalse.size());
        
        Assert.assertTrue("Query type was not in the false namespaces results set", queryTypesMatchingQueryFalse.containsKey(testRegexInputQueryType1));
        
        Assert.assertEquals("false namespaces result had a parameter to namespace map attached to it", 0, queryTypesMatchingQueryFalse.get(testRegexInputQueryType1).size());
    }
    
}
