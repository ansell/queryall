/**
 * 
 */
package org.queryall.api.querytype;

/**
 * A QueryType that takes input in the form of an RDF document and translates it into named
 * parameters using a SPARQL SELECT query.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface RdfInputQueryType extends InputQueryType
{
    /**
     * 
     * Note: Any unexpected parameters from the results of the SPARQL SELECT query will not be
     * usable. All parameters need to be defined before use.
     * 
     * @return A SPARQL SELECT statement that is used to populate the expected input parameters for
     *         this query
     */
    String getSparqlInputSelect();
    
    /**
     * 
     * Note: Any unexpected parameters from the results of the SPARQL SELECT query will not be
     * usable. All parameters need to be defined before use.
     * 
     * @param sparqlInputSelect
     *            A SPARQL SELECT statement that is used to populate the expected input parameters
     *            for this query
     */
    void setSparqlInputSelect(String sparqlInputSelect);
}
