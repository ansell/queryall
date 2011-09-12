/**
 * 
 */
package org.queryall.api.querytype;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface RdfInputQueryType extends InputQueryType
{
    void setSparqlInputSelect(String sparqlInputSelect);
    
    String getSparqlInputSelect();
}
