package org.queryall.api.rdfrule;

import java.util.List;

import org.openrdf.model.URI;

public interface SparqlConstructRule extends TransformingRule, SparqlNormalisationRule
{
    /**
     * Gets the mode, represented by a URI that this SPARQL Construct rule operates using.
     * 
     * Currently, SPARQL Construct rules can be set to delete matching triples, include all matching
     * triples, or only keep matching triples.
     * 
     * @return Returns a URI denoting the mode that this SPARQL Construct rule operates using.
     */
    URI getMode();
    
    /**
     * 
     * @return The list of SPARQL Construct queries setup for this rule, including one query for
     *         each of the Where patterns.
     */
    List<String> getSparqlConstructQueries();
    
    /**
     * 
     * @return The SPARQL Basic Graph Pattern (BGP) setup for the construct part of this rule.
     */
    String getSparqlConstructQueryTarget();
    
    /**
     * Sets the mode for this rule, based on the URI.
     * 
     * Currently, SPARQL Construct rules can be set to delete matching triples, include all matching
     * triples, or only keep matching triples.
     * 
     * @param mode
     *            Sets the mode that this rule will operate using.
     */
    void setMode(URI mode);
    
    /**
     * Sets the SPARQL Basic Graph Pattern (BGP) for the construct part of this rule.
     * 
     * This BGP is included along with each of the Where patterns to create the construct queries
     * that are used to normalise and denormalise RDF statements using this rule.
     * 
     * @param sparqlConstructQueryTarget
     */
    void setSparqlConstructQueryTarget(String sparqlConstructQueryTarget);
    
}