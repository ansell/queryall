
package org.queryall.api;

import java.util.Collection;

import org.queryall.exception.InvalidStageException;


public interface NormalisationRule extends BaseQueryAllInterface, Comparable<NormalisationRule>, ProfilableInterface
{
    int getOrder();
    
    void setOrder(int order);

    Collection<org.openrdf.model.URI> getValidStages();
    
    void setStages(Collection<org.openrdf.model.URI> stages) throws InvalidStageException;

    void addStage(org.openrdf.model.URI stage) throws InvalidStageException;
    
    boolean validInStage(org.openrdf.model.URI stage);

    boolean usedInStage(org.openrdf.model.URI stage);

    Collection<org.openrdf.model.URI> getStages();
    
    String getDescription();
    
    Object normaliseByStage(org.openrdf.model.URI stage, Object input);
    
    Object stageQueryVariables(Object input);
        
    Object stageAfterQueryCreation(Object input);

    Object stageAfterQueryParsing(Object input);

    Object stageBeforeResultsImport(Object input);

    Object stageAfterResultsImport(Object input);

    Object stageAfterResultsToPool(Object input);

    Object stageAfterResultsToDocument(Object input);
    
}
