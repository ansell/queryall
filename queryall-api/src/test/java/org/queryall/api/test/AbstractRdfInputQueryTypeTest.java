/**
 * 
 */
package org.queryall.api.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.queryall.api.querytype.RdfInputQueryType;
import org.queryall.api.querytype.RegexInputQueryType;

/**
 * Abstract unit test for InputQueryType API
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractRdfInputQueryTypeTest
{
    private RdfInputQueryType testRdfInputQueryType1;
    private String testRdfDocument;
    private Map<String, String> testQueryParameters;
    private String testSparqlInputSelect;
    
    /**
     * This method must be overridden to return a new instance of the implemented QueryType class
     * for each successive invocation
     * 
     * @return A new instance of the RdfInputQueryType implementation
     */
    public abstract RdfInputQueryType getNewTestRdfInputQueryType();
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
//        final ValueFactory f = new MemValueFactory();
        
        this.testRdfInputQueryType1 = getNewTestRdfInputQueryType();
        
        this.testRdfDocument = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">" + 
        		"            <rdf:Description rdf:ID=\"foo\">" + 
        		"  <rdf:type rdf:resource=\"http://example.org/rdfinputtest:type1\"/>" + 
        		"  <variable1 xmlns=\"http://example.org/rdfinputtest:\">abcc123</variable1>" + 
        		"  <variable2 xmlns=\"http://example.org/rdfinputtest:\">zyxx902</variable2>" + 
        		"</rdf:Description>" + 
        		"</rdf:RDF>";

        this.testSparqlInputSelect = "SELECT ?input_1 ?input_2 WHERE { ?testObjects rdf:type <http://example.org/rdfinputtest:type1> . ?testObjects <http://example.org/rdfinputtest:variable1> ?input_1 . ?testObjects <http://example.org/rdfinputtest:variable2> ?input_2 . }";
        this.testRdfInputQueryType1.setSparqlInputSelect(this.testSparqlInputSelect);
        
        testQueryParameters = new HashMap<String, String>();
        testQueryParameters.put("query", this.testRdfDocument);
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        this.testRdfInputQueryType1 = null;
        this.testRdfDocument = null;
        this.testSparqlInputSelect = null;
        this.testQueryParameters = null;
    }
    
    @Test
    public void testGet()
    {
        Assert.assertEquals("SPARQL SELECT query input was not set properly", this.testSparqlInputSelect, this.testRdfInputQueryType1.getSparqlInputSelect());
        
        Assert.assertTrue("RDF document does not match for query parameters", this.testRdfInputQueryType1.matchesQueryParameters(testQueryParameters));
        Map<String, List<String>> matchingQueryParams = this.testRdfInputQueryType1.matchesForQueryParameters(testQueryParameters);
        
        Assert.assertEquals("Query parameters were not parsed correctly", 2, matchingQueryParams.size());
        Assert.assertTrue("Query parameters were not parsed correctly", matchingQueryParams.containsKey("input_1"));
        Assert.assertTrue("Query parameters were not parsed correctly", matchingQueryParams.containsKey("input_2"));
        
        Assert.assertEquals("Query parameters were not processed correctly", 1, matchingQueryParams.get("input_1").size());
        Assert.assertEquals("Query parameters were not processed correctly", 1, matchingQueryParams.get("input_2").size());

        Assert.assertEquals("Query parameters were not processed correctly", "abcc123", matchingQueryParams.get("input_1").get(0));
        Assert.assertEquals("Query parameters were not processed correctly", "zyxx902", matchingQueryParams.get("input_2").get(0));
            
    }
    
}
