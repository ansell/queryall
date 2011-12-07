package org.queryall.api.ruletest;

/**
 * A SparqlRuleTest uses a SPARQL ASK query to determine whether the given input triples match the
 * desired specification, with either true or false able to be expected by a successful test.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface SparqlRuleTest extends RuleTest
{
    /**
     * 
     * @return True if the SPARQL ASK query is expected to succeed in a valid test, and false if it
     *         is expected to fail in a valid test
     */
    boolean getExpectedResult();
    
    /**
     * 
     * @return The MIME type that the input triples are encoded using. For example, it could be
     *         application/rdf+xml or text/rdf+n3
     */
    String getTestInputMimeType();
    
    /**
     * 
     * @return A string containing the input triples encoded using the encoding returned from
     *         getTestInputMimeType()
     */
    String getTestInputTriples();
    
    /**
     * 
     * @return A string containing the SPARQL ASK query that is to be executed by this test
     */
    String getTestSparqlAsk();
    
    /**
     * 
     * @param expectedResult
     *            True if the SPARQL ASK query is expected to succeed in a valid test, and false if
     *            it is expected to fail in a valid test
     */
    void setExpectedResult(boolean expectedResult);
    
    /**
     * 
     * @param testInputMimeType
     *            The MIME type that the input triples are encoded using. For example, it could be
     *            application/rdf+xml or text/rdf+n3
     */
    void setTestInputMimeType(String testInputMimeType);
    
    /**
     * 
     * @param testInputTriples
     *            A string containing the input triples encoded using the encoding returned from
     *            getTestInputMimeType()
     */
    void setTestInputTriples(String testInputTriples);
    
    /**
     * 
     * @param testSparqlAsk
     *            A string containing the SPARQL ASK query that is to be executed by this test
     */
    void setTestSparqlAsk(String testSparqlAsk);
}
