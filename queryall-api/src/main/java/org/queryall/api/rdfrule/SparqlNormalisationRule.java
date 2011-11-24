package org.queryall.api.rdfrule;

import java.util.List;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface SparqlNormalisationRule extends NormalisationRule
{
    
    void addSparqlWherePattern(String sparqlWherePattern);
    
    String getSparqlPrefixes();
    
    List<String> getSparqlWherePatterns();
    
    void setSparqlPrefixes(String sparqlPrefixes);
}