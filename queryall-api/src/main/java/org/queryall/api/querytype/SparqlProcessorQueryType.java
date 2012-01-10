/**
 * 
 */
package org.queryall.api.querytype;

/**
 * A ProcessorQueryType that processes RDF statements using a SPARQL template.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface SparqlProcessorQueryType extends ProcessorQueryType
{
    /**
     * 
     * @return The SPARQL Template string used by this query type
     */
    String getSparqlTemplateString();
    
    /**
     * 
     * @param templateString
     *            The SPARQL Template string to be used by this query type
     */
    void setSparqlTemplateString(String templateString);
}
