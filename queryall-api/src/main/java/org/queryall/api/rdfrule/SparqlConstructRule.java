package org.queryall.api.rdfrule;

import java.util.List;

import org.openrdf.model.URI;

public interface SparqlConstructRule extends TransformingRule, SparqlNormalisationRule
{
    
    public abstract URI getMode();
    
    public abstract List<String> getSparqlConstructQueries();
    
    public abstract String getSparqlConstructQueryTarget();
    
    /**
     * @param mode
     *            the mode to set
     */
    void setMode(URI mode);
    
    public abstract void setSparqlConstructQueryTarget(String sparqlConstructQueryTarget);
    
}