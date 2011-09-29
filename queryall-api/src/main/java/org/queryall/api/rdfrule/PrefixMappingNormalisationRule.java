package org.queryall.api.rdfrule;

import java.util.Collection;

import org.openrdf.model.URI;

public interface PrefixMappingNormalisationRule extends NormalisationRule
{
    void addObjectMappingPredicate(URI sameas);
    
    void addPredicateMappingPredicate(URI equivalentproperty);
    
    void addSubjectMappingPredicate(URI equivalentclass);
    
    String getInputUriPrefix();
    
    Collection<URI> getObjectMappingPredicates();
    
    String getOutputUriPrefix();
    
    Collection<URI> getPredicateMappingPredicates();
    
    Collection<URI> getSubjectMappingPredicates();
    
    void setInputUriPrefix(String inputUriPrefix);
    
    void setOutputUriPrefix(String outputUriPrefix);
    
}