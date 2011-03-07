package org.queryall;

import java.util.Collection;

public abstract class RuleTest implements BaseQueryAllInterface, Comparable<RuleTest>
{
    public abstract Collection<org.openrdf.model.URI> getRuleUris();
    
    public abstract void setRuleUris(Collection<org.openrdf.model.URI> rdfRuleUris);
    
    public abstract Collection<org.openrdf.model.URI> getStages();
    
    public abstract void setStages(Collection<org.openrdf.model.URI> stages);
    
    public abstract String getTestInputString();

    public abstract void setTestInputString(String testInputString);
    
    public abstract String getTestOutputString();

    public abstract void setTestOutputString(String testOutputString);

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        
        result.append("title=").append(this.getTitle());
        result.append("key=").append(this.getKey().stringValue());

        return result.toString();
    }

}
