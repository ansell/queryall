package org.queryall.api;

import java.util.Collection;

public interface RuleTest extends BaseQueryAllInterface, Comparable<RuleTest>
{
    public abstract Collection<org.openrdf.model.URI> getRuleUris();
    
    public abstract void setRuleUris(Collection<org.openrdf.model.URI> rdfRuleUris);
    
    public abstract Collection<org.openrdf.model.URI> getStages();
    
    public abstract void setStages(Collection<org.openrdf.model.URI> stages);
    
    public abstract String getTestInputString();

    public abstract void setTestInputString(String testInputString);
    
    public abstract String getTestOutputString();

    public abstract void setTestOutputString(String testOutputString);
}
