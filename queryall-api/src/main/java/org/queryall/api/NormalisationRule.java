package org.queryall.api;

import java.util.Collection;

import org.queryall.exception.InvalidStageException;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface NormalisationRule extends BaseQueryAllInterface, Comparable<NormalisationRule>, ProfilableInterface
{
    void addStage(org.openrdf.model.URI stage) throws InvalidStageException;
    
    String getDescription();
    
    int getOrder();
    
    Collection<org.openrdf.model.URI> getStages();
    
    Collection<org.openrdf.model.URI> getValidStages();
    
    Object normaliseByStage(org.openrdf.model.URI stage, Object input);
    
    void setOrder(int order);
    
    void setStages(Collection<org.openrdf.model.URI> stages) throws InvalidStageException;
    
    Object stageAfterQueryCreation(Object input);
    
    Object stageAfterQueryParsing(Object input);
    
    Object stageAfterResultsImport(Object input);
    
    Object stageAfterResultsToDocument(Object input);
    
    Object stageAfterResultsToPool(Object input);
    
    Object stageBeforeResultsImport(Object input);
    
    Object stageQueryVariables(Object input);
    
    boolean usedInStage(org.openrdf.model.URI stage);
    
    boolean validInStage(org.openrdf.model.URI stage);
    
}
