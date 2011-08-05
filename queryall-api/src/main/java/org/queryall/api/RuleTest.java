package org.queryall.api;

import java.util.Collection;

public interface RuleTest extends BaseQueryAllInterface, Comparable<RuleTest>
{
    Collection<org.openrdf.model.URI> getRuleUris();
    
    void setRuleUris(Collection<org.openrdf.model.URI> rdfRuleUris);
    
    Collection<org.openrdf.model.URI> getStages();
    
    void setStages(Collection<org.openrdf.model.URI> stages);
    
    String getTestInputString();

    void setTestInputString(String testInputString);
    
    String getTestOutputString();

    void setTestOutputString(String testOutputString);
}
