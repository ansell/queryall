package org.queryall.api.rdfrule;

import java.util.Collection;
import java.util.Set;

import org.openrdf.model.URI;
import org.queryall.api.base.BaseQueryAllInterface;
import org.queryall.api.base.ProfilableInterface;
import org.queryall.exception.InvalidStageException;
import org.queryall.exception.QueryAllException;

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
    
    Set<URI> getStages();
    
    Set<URI> getValidStages();
    
    Object normaliseByStage(URI stage, Object input) throws InvalidStageException, QueryAllException;
    
    void setOrder(int order);
    
    Object stageAfterQueryCreation(Object input) throws QueryAllException;
    
    Object stageAfterQueryParsing(Object input) throws QueryAllException;
    
    Object stageAfterResultsImport(Object input) throws QueryAllException;
    
    Object stageAfterResultsToDocument(Object input) throws QueryAllException;
    
    Object stageAfterResultsToPool(Object input) throws QueryAllException;
    
    Object stageBeforeResultsImport(Object input) throws QueryAllException;
    
    Object stageQueryVariables(Object input) throws QueryAllException;
    
    boolean usedInStage(URI stage);
    
    boolean validInStage(URI stage);
    
}
