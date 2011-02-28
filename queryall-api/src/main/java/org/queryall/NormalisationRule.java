
package org.queryall;

import java.util.Collection;

import org.openrdf.model.URI;

public abstract class NormalisationRule implements BaseQueryAllInterface, Comparable<NormalisationRule>
{
    public abstract URI getProfileIncludeExcludeOrder();
    
    public abstract void setProfileIncludeExcludeOrder(URI profileIncludeExcludeOrder);

    public abstract int getOrder();
    
    public abstract void setOrder(int order);

    protected abstract void setValidStages(Collection<URI> validStages);
    
    public abstract Collection<URI> getValidStages();
    
    public abstract String getDescription();
    
    public abstract Object normaliseByStage(URI stage, Object input);
    
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
