
package org.queryall.api;

import java.util.Collection;

import org.queryall.exception.InvalidStageException;


public abstract class NormalisationRule implements BaseQueryAllInterface, Comparable<NormalisationRule>, ProfilableInterface
{
    public abstract int getOrder();
    
    public abstract void setOrder(int order);

    protected abstract void setValidStages(Collection<org.openrdf.model.URI> validStages);
    
    protected abstract void addValidStage(org.openrdf.model.URI validStage);
    
    public abstract Collection<org.openrdf.model.URI> getValidStages();
    
    public abstract void setStages(Collection<org.openrdf.model.URI> stages) throws InvalidStageException;

    public abstract void addStage(org.openrdf.model.URI stage) throws InvalidStageException;
    
    public abstract boolean validInStage(org.openrdf.model.URI stage);

    public abstract boolean usedInStage(org.openrdf.model.URI stage);

    public abstract Collection<org.openrdf.model.URI> getStages();
    
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
