
package org.queryall;

import java.util.Collection;

public abstract class NormalisationRule implements BaseQueryAllInterface, Comparable<NormalisationRule>
{
    public abstract org.openrdf.model.URI getProfileIncludeExcludeOrder();
    
    public abstract void setProfileIncludeExcludeOrder(org.openrdf.model.URI profileIncludeExcludeOrder);

    public abstract int getOrder();
    
    public abstract void setOrder(int order);

    protected abstract void setValidStages(Collection<org.openrdf.model.URI> validStages);
    
    public abstract Collection<org.openrdf.model.URI> getValidStages();
    
    public abstract String getDescription();
    
    public abstract Object normaliseByStage(org.openrdf.model.URI stage, Object input);
    
    public abstract Object stageQueryVariables(Object input);
        
    public abstract Object stageAfterQueryCreation(Object input);

    public abstract Object stageAfterQueryParsing(Object input);

    public abstract Object stageBeforeResultsImport(Object input);

    public abstract Object stageAfterResultsImport(Object input);

    public abstract Object stageAfterResultsToPool(Object input);

    public abstract Object stageAfterResultsToDocument(Object input);

    // public abstract boolean runTests(Collection<org.queryall.RuleTest> myRules);
    
    public abstract String toHtml();
    
    public abstract String toHtmlFormBody();
    
    @Override
    public String toString()
    {
        String result = "\n";
        
        result += "key=" + this.getKey().stringValue() + "\n";
        result += "order=" + this.getOrder() + "\n";
        result += "description=" + this.getDescription() + "\n";
        
        return result;
    }
    

    

}
