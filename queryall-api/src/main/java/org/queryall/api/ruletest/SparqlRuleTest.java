package org.queryall.api.ruletest;

public interface SparqlRuleTest
{
    String getTestInputMimeType();
    
    void setTestInputMimeType(String testInputMimeType);
    
    String getTestInputTriples();
    
    void setTestInputTriples(String testInputTriples);
    
    String getTestSparqlAsk();
    
    void setTestSparqlAsk(String testSparqlAsk);
    
    boolean getExpectedResult();
    
    void setExpectedResult(boolean expectedResult);
}
