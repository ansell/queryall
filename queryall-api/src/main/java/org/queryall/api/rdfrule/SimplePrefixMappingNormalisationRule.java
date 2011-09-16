package org.queryall.api.rdfrule;

import java.util.Collection;

import org.openrdf.model.URI;

public interface SimplePrefixMappingNormalisationRule extends NormalisationRule
{
    void setInputUriPrefix(String inputUriPrefix);
    
    String getInputUriPrefix();
    
    void setOutputUriPrefix(String outputUriPrefix);
    
    String getOutputUriPrefix();
    
    void addSubjectMappingPredicate(URI equivalentclass);
    
    Collection<URI> getSubjectMappingPredicates();

    void addPredicateMappingPredicate(URI equivalentproperty);

    Collection<URI> getPredicateMappingPredicates();

    void addObjectMappingPredicate(URI sameas);
    
    Collection<URI> getObjectMappingPredicates();

}