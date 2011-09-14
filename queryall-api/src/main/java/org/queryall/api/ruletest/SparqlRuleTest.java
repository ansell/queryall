package org.queryall.api.ruletest;

public interface SparqlRuleTest
{
    String getTestSparqlAsk();
    
    void setTestSparqlAsk(String testSparqlAsk);
    
    boolean getExpectedResult();
    
    void setExpectedResult(boolean expectedResult);
}
