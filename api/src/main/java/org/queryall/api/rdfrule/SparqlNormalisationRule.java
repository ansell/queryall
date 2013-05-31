package org.queryall.api.rdfrule;

import java.util.Collection;

/**
 * The SparqlNormalisationRule interface encapsulates the common functionality for the
 * SparqlConstructRule and SparqlAskRule types. It is neither a ValidatingRule, nor a
 * TransformationRule, so it cannot be used for transformations without further implementation.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface SparqlNormalisationRule extends NormalisationRule
{
    /**
     * All SPARQL queries support a Where pattern, so it is included here.
     * 
     * More than one Where pattern may be used by a single rule, to easily support different rules
     * mapping to the same output.
     * 
     * @param sparqlWherePattern
     *            The Basic Graph Pattern string representing one of the Where patterns supported by
     *            this rule.
     */
    void addSparqlWherePattern(String sparqlWherePattern);
    
    /**
     * 
     * @return The list of prefix declarations in an N3 compatible string.
     */
    String getSparqlPrefixes();
    
    /**
     * 
     * @return A list of the SPARQL Where patterns that make up this rule. Each pattern represents
     *         one of the mappings that make up this rule.
     */
    Collection<String> getSparqlWherePatterns();
    
    /**
     * 
     * @return True if the sparql where patterns collection was reset and false otherwise
     */
    boolean resetSparqlWherePatterns();
    
    /**
     * 
     * @param sparqlPrefixes
     *            A string representing a prefix declaration in the N3 RDF format.
     */
    void setSparqlPrefixes(String sparqlPrefixes);
}