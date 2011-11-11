package org.queryall.api.rdfrule;

import java.util.List;

import org.openrdf.model.URI;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface SparqlNormalisationRule extends NormalisationRule
{
    
    void addSparqlWherePattern(String sparqlWherePattern);
    
    String getSparqlPrefixes();
    
    List<String> getSparqlWherePatterns();
    
    /**
     * @param mode
     *            the mode to set
     */
    void setMode(URI mode);
    
    void setSparqlPrefixes(String sparqlPrefixes);
}