package org.queryall.api.ruletest;

import java.util.Collection;

import org.queryall.api.BaseQueryAllInterface;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface RuleTest extends BaseQueryAllInterface, Comparable<RuleTest>
{
    Collection<org.openrdf.model.URI> getRuleUris();
    
    Collection<org.openrdf.model.URI> getStages();
    
    String getTestInputString();
    
    String getTestOutputString();
    
    void setRuleUris(Collection<org.openrdf.model.URI> rdfRuleUris);
    
    void setStages(Collection<org.openrdf.model.URI> stages);
    
    void setTestInputString(String testInputString);
    
    void setTestOutputString(String testOutputString);
}
