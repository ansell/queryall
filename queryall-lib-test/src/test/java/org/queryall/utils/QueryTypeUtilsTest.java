/**
 * 
 */
package org.queryall.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.Repository;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.profile.Profile;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.querytype.RegexInputQueryType;
import org.queryall.api.utils.QueryAllNamespaces;
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
    private Map<String, String> testInputParametersPreferred;
    private Map<String, String> testInputParametersAlternate;
    private Map<String, String> testInputParametersFalse;
    private Map<String, Collection<URI>> testNamespacePrefixMap;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        this.testValueFactory = new ValueFactoryImpl();

        testNamespaceEntry1 = new NamespaceEntryImpl();
        testNamespaceEntry1.setKey("http://example.org/test/namespace/1");
        testNamespaceEntry1.setPreferredPrefix("myPreferredNamespace");
        testNamespaceEntry1.addAlternativePrefix("alternateNs");
        testNamespaceEntry1.setSeparator(":");
        testNamespaceEntry1.setAuthority(testValueFactory.createURI("http://my.example.org/"));

        testRegexInputQueryType1 = new RegexInputQueryTypeImpl();
        testRegexInputQueryType1.setKey("http://example.org/test/querytype/1");
        testRegexInputQueryType1.addNamespaceToHandle(testNamespaceEntry1.getKey());
        testRegexInputQueryType1.addNamespaceInputTag("input_1");
        testRegexInputQueryType1.addExpectedInputParameter("input_1");
        testRegexInputQueryType1.addExpectedInputParameter("input_2");
        testRegexInputQueryType1.addPublicIdentifierTag("input_1");
        
        testInputParametersPreferred = new HashMap<String, String>();
        testInputParametersPreferred.put("input_1", "myPreferredNamespace");
        testInputParametersPreferred.put("input_2", "identifier1234567");

        testInputParametersAlternate = new HashMap<String, String>();
        testInputParametersAlternate.put("input_1", "alternateNs");
        testInputParametersAlternate.put("input_2", "identifier7654321");

        testInputParametersFalse = new HashMap<String, String>();
        testInputParametersFalse.put("input_1", "unknownfalsenamespace");
        testInputParametersFalse.put("input_2", "identifier654");
    
        testNamespacePrefixMap = new HashMap<String, Collection<URI>>();
        Collection<URI> preferredNamespaces = new ArrayList<URI>(1);
        preferredNamespaces.add(testNamespaceEntry1.getKey());
        Collection<URI> alternateNamespaces = new ArrayList<URI>(1);
        alternateNamespaces.add(testNamespaceEntry1.getKey());
        testNamespacePrefixMap.put("myPreferredNamespace", preferredNamespaces);
        testNamespacePrefixMap.put("alternateNs", alternateNamespaces);
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
        Map<String, Collection<URI>> namespacesMatches1 = QueryTypeUtils.namespacesMatchesForQueryParameters(testRegexInputQueryType1, testInputParametersPreferred, testNamespacePrefixMap);

        Assert.assertEquals("preferred namespaces did not generate a single result", 1, namespacesMatches1.size());

        Map<String, Collection<URI>> namespacesMatches2 = QueryTypeUtils.namespacesMatchesForQueryParameters(testRegexInputQueryType1, testInputParametersAlternate, testNamespacePrefixMap);
    
        Assert.assertEquals("alternate namespaces did not generate a single result", 1, namespacesMatches2.size());

        Map<String, Collection<URI>> namespacesMatchesFalse = QueryTypeUtils.namespacesMatchesForQueryParameters(testRegexInputQueryType1, testInputParametersFalse, testNamespacePrefixMap);
        
        Assert.assertEquals("false namespaces generated a result", 0, namespacesMatchesFalse.size());
        
    }
    
    /**
     * Test method for {@link org.queryall.utils.QueryTypeUtils#getQueryTypesMatchingQuery(java.util.Map, java.util.List, java.util.Map, java.util.Map, java.util.Map, boolean, boolean)}.
     */
    @Test
    @Ignore
    public void testGetQueryTypesMatchingQuery()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
}
