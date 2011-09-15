package org.queryall.api.rdfrule;

import java.util.Collection;

import org.openrdf.model.URI;

public interface SimplePrefixMappingNormalisationRule extends NormalisationRule
{
    void setInputUriPrefix(String inputUriPrefix);
    
    String getInputUriPrefix();
    
    void setOutputUriPrefix(String outputUriPrefix);
    
    String getOutputUriPrefix();
    
    void addMappingPredicate(String mappingPredicateString);
    
    void addMappingPredicate(URI mappingPredicateUri);

    Collection<URI> getMappingPredicates();
    
}