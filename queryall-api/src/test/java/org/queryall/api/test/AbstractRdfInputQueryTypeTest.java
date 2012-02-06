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
import org.queryall.api.querytype.InputQueryType;
import org.queryall.api.querytype.RdfInputQueryType;
import org.queryall.api.utils.Constants;

/**
 * Abstract unit test for InputQueryType API.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractRdfInputQueryTypeTest extends AbstractInputQueryTypeTest
{
    private RdfInputQueryType testRdfInputQueryType1;
    private String testRdfDocument;
    private Map<String, String> testQueryParameters;
    private String testSparqlInputSelect;
    
    @Override
    public final InputQueryType getNewTestInputQueryType()
    {
        return this.getNewTestRdfInputQueryType();
    }
    
    /**
     * This method must be overridden to return a new instance of the implemented QueryType class
     * for each successive invocation.
     * 
     * @return A new instance of the RdfInputQueryType implementation
     */
    public abstract RdfInputQueryType getNewTestRdfInputQueryType();
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        
        // final ValueFactory f = new MemValueFactory();
        
        this.testRdfInputQueryType1 = this.getNewTestRdfInputQueryType();
        
        this.testRdfDocument =
                "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"
                        + "            <rdf:Description rdf:ID=\"foo\">"
                        + "  <rdf:type rdf:resource=\"http://example.org/rdfinputtest:type1\"/>"
                        + "  <variable1 xmlns=\"http://example.org/rdfinputtest:\">abcc123</variable1>"
                        + "  <variable2 xmlns=\"http://example.org/rdfinputtest:\">zyxx902</variable2>"
                        + "</rdf:Description>" + "</rdf:RDF>";
        
        this.testSparqlInputSelect =
                "SELECT ?input_1 ?input_2 WHERE { ?testObjects a <http://example.org/rdfinputtest:type1> . ?testObjects <http://example.org/rdfinputtest:variable1> ?input_1 . ?testObjects <http://example.org/rdfinputtest:variable2> ?input_2 . }";
        this.testRdfInputQueryType1.setSparqlInputSelect(this.testSparqlInputSelect);
        
        this.testRdfInputQueryType1.addExpectedInputParameter("input_1");
        this.testRdfInputQueryType1.addExpectedInputParameter("input_2");
        
        this.testQueryParameters = new HashMap<String, String>();
        this.testQueryParameters.put(Constants.QUERY, this.testRdfDocument);
        this.testQueryParameters.put("inputMimeType", Constants.APPLICATION_RDF_XML);
    }
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    @After
    public void tearDown() throws Exception
    {
        super.tearDown();
        
        this.testRdfInputQueryType1 = null;
        this.testRdfDocument = null;
        this.testSparqlInputSelect = null;
        this.testQueryParameters = null;
    }
    
    @Test
    public void testGet()
    {
        Assert.assertEquals("SPARQL SELECT query input was not set properly", this.testSparqlInputSelect,
                this.testRdfInputQueryType1.getSparqlInputSelect());
        
        Assert.assertTrue("RDF document does not match for query parameters",
                this.testRdfInputQueryType1.matchesQueryParameters(this.testQueryParameters));
        
        final Map<String, List<String>> matchingQueryParams =
                this.testRdfInputQueryType1.matchesForQueryParameters(this.testQueryParameters);
        
        Assert.assertEquals("Query parameters were not parsed correctly", 2, matchingQueryParams.size());
        Assert.assertTrue("Query parameters were not parsed correctly", matchingQueryParams.containsKey("input_1"));
        Assert.assertTrue("Query parameters were not parsed correctly", matchingQueryParams.containsKey("input_2"));
        
        Assert.assertEquals("Query parameters were not processed correctly", 1, matchingQueryParams.get("input_1")
                .size());
        Assert.assertEquals("Query parameters were not processed correctly", 1, matchingQueryParams.get("input_2")
                .size());
        
        Assert.assertEquals("Query parameters were not processed correctly", "abcc123",
                matchingQueryParams.get("input_1").get(0));
        Assert.assertEquals("Query parameters were not processed correctly", "zyxx902",
                matchingQueryParams.get("input_2").get(0));
        
    }
    
}
