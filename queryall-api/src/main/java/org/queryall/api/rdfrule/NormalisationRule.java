package org.queryall.api.rdfrule;

import java.util.Collection;

import org.openrdf.model.URI;
import org.queryall.api.BaseQueryAllInterface;
import org.queryall.api.ProfilableInterface;
import org.queryall.exception.InvalidStageException;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface NormalisationRule extends BaseQueryAllInterface, Comparable<NormalisationRule>, ProfilableInterface
{
    void addRelatedNamespaces(URI nextRelatedNamespace);
    
    void addStage(URI nextStage) throws InvalidStageException;
    
    String getDescription();
    
    int getOrder();
    
    Collection<URI> getRelatedNamespaces();
    
    Collection<URI> getStages();
    
    Collection<URI> getValidStages();
    
    Object normaliseByStage(URI stage, Object input);
    
    void setOrder(int order);
    
    Object stageAfterQueryCreation(Object input);
    
    Object stageAfterQueryParsing(Object input);
    
    Object stageAfterResultsImport(Object input);
    
    Object stageAfterResultsToDocument(Object input);
    
    Object stageAfterResultsToPool(Object input);
    
    Object stageBeforeResultsImport(Object input);
    
    Object stageQueryVariables(Object input);
    
    boolean usedInStage(URI stage);
    
    boolean validInStage(URI stage);
    
}
