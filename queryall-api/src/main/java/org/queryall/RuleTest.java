package org.queryall;

import java.util.Collection;

import org.openrdf.model.URI;

public abstract class RuleTest implements BaseQueryAllInterface, Comparable<RuleTest>
{
    public abstract Collection<URI> getRuleUris();
    
    public abstract void setRuleUris(Collection<URI> rdfRuleUris);
    
    public abstract Collection<URI> getStages();
    
    public abstract void setStages(Collection<URI> stages);
    
    public abstract URI getProfileIncludeExcludeOrder();
    
    public abstract void setProfileIncludeExcludeOrder(URI profileIncludeExcludeOrder);
    
    public abstract String getTestInputString();

    public abstract void setTestInputString(String testInputString);
    
    public abstract String getTestOutputString();

    public abstract void setTestOutputString(String testOutputString);

}
