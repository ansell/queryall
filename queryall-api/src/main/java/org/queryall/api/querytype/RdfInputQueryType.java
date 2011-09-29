/**
 * 
 */
package org.queryall.api.querytype;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface RdfInputQueryType extends InputQueryType
{
    String getSparqlInputSelect();
    
    void setSparqlInputSelect(String sparqlInputSelect);
}
