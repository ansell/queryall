/**
 * 
 */
package org.queryall.api.querytype;

/**
 * A ProcessorQueryType that processes RDF statements using a SPARQL template
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface SparqlProcessorQueryType extends OutputQueryType
{
    String getSparqlTemplateString();
    
    void setSparqlTemplateString(String templateString);
}
