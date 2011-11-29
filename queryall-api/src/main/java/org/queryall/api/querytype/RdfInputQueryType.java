/**
 * 
 */
package org.queryall.api.querytype;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface RdfInputQueryType extends InputQueryType
{
    /**
     * 
     * Note: Any unexpected parameters from the results of the SPARQL SELECT query will not be usable. All parameters need to be defined before use.
     * 
     * @return A SPARQL SELECT statement that is used to populate the expected input parameters for this query
     */
    String getSparqlInputSelect();
    
    /**
     * 
     * Note: Any unexpected parameters from the results of the SPARQL SELECT query will not be usable. All parameters need to be defined before use.
     * 
     * @param sparqlInputSelect A SPARQL SELECT statement that is used to populate the expected input parameters for this query
     */
    void setSparqlInputSelect(String sparqlInputSelect);
}
