/**
 * 
 */
package org.queryall.api.querytype;

import java.util.Map;

/**
 * A Query Type that processes a query using some mechanism
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface ProcessorQueryType extends QueryType
{
    /**
     * Set the processing template string for this processor
     * 
     * @return The templated string to use for this processor
     */
    String getProcessingTemplateString();
    
    /**
     * Parses the query for this Processor Query Type to create an object that matches the type of
     * this ProcessorQueryType.
     * 
     * @param query
     *            A string that contains the query after it has had all of its variables processed
     *            and substituted.
     * @return An object representing the parsed query as relevant to this processor type.
     */
    Object parseProcessorQuery(String query);
    
    /**
     * Processes the query variables based on the context of this processor query type.
     * 
     * The query variables are created by the InputQueryType implementation for this QueryType using
     * InputQueryType.parseInputs()
     * 
     * @param queryVariables
     * @return The query variables after processing to match the processor for this query type
     */
    Map<String, Object> processQueryVariables(Map<String, Object> queryVariables);
    
    /**
     * Set the processing template string for this processor
     * 
     * @param templateString
     *            The templated string to use for this processor
     */
    void setProcessingTemplateString(String templateString);
    
    /**
     * Substitutes the given processed query variables into the processing template that is returned
     * from getProcessingTemplateString()
     * 
     * @param processedQueryVariables
     * @return The query template with the given variables substituted into the query
     */
    String substituteQueryVariables(Map<String, Object> processedQueryVariables);
}
