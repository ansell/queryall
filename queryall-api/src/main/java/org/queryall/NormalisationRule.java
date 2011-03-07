
package org.queryall;

import java.util.Collection;

public abstract class NormalisationRule implements BaseQueryAllInterface, Comparable<NormalisationRule>, ProfilableInterface
{
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

    public abstract boolean isRdfRuleUsedWithProfileList(Collection<Profile> nextSortedProfileList, boolean recogniseImplicitRdfRuleInclusions, boolean includeNonProfileMatchedRdfRules);
    
    // public abstract boolean runTests(Collection<org.queryall.RuleTest> myRules);
    
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        
        result.append("title=").append(this.getTitle());
        result.append("key=").append(this.getKey().stringValue());
        result.append("order=").append(this.getOrder());
        result.append("description=").append(this.getDescription());
        
        return result.toString();
    }
}
