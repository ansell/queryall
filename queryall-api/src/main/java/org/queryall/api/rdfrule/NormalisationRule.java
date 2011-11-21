package org.queryall.api.rdfrule;

import java.util.Collection;
import java.util.Set;

import org.openrdf.model.URI;
import org.queryall.api.base.BaseQueryAllInterface;
import org.queryall.api.base.ProfilableInterface;
import org.queryall.exception.InvalidStageException;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface NormalisationRule extends BaseQueryAllInterface, Comparable<NormalisationRule>, ProfilableInterface
{
    void addRelatedNamespaces(URI nextRelatedNamespace);
    
    void addStage(URI nextStage) throws InvalidStageException;
    
    @Override
    String getDescription();
    
    int getOrder();
    
    Collection<URI> getRelatedNamespaces();
    
    Set<URI> getStages();
    
    Set<URI> getValidStages();
    
    void setOrder(int order);
    
    boolean usedInStage(URI stage);
    
    boolean validInStage(URI stage);
    
}
