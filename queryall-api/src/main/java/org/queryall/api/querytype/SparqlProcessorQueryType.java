/**
 * 
 */
package org.queryall.api.querytype;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface SparqlProcessorQueryType extends OutputQueryType
{
    String getSparqlTemplateString();
    
    void setSparqlTemplateString(String templateString);
}
