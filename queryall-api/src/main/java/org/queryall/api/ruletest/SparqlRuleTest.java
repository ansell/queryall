package org.queryall.api.ruletest;

public interface SparqlRuleTest
{
    boolean getExpectedResult();
    
    String getTestInputMimeType();
    
    String getTestInputTriples();
    
    String getTestSparqlAsk();
    
    void setExpectedResult(boolean expectedResult);
    
    void setTestInputMimeType(String testInputMimeType);
    
    void setTestInputTriples(String testInputTriples);
    
    void setTestSparqlAsk(String testSparqlAsk);
}
