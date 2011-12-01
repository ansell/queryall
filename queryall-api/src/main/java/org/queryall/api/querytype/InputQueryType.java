/**
 * 
 */
package org.queryall.api.querytype;

import java.util.Collection;
import java.util.Map;

/**
 * A Query Type that uses named parameters to identify inputs.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface InputQueryType extends QueryType
{
    /**
     * 
     * @param expectedInputParameter A string identifying an input parameter that is expected by this query type
     */
    void addExpectedInputParameter(String expectedInputParameter);
    
    /**
     * Resets the list of expected input parameters
     * 
     * @return True if the collection of expected input parameters was reset and false otherwise.
     */
    boolean resetExpectedInputParameters();
    
    /**
     * 
     * @return A collection of strings identifying input parameters that are expected by this query type
     */
    Collection<String> getExpectedInputParameters();
    
    /**
     * This method parses the inputs between the afterQueryCreation and afterQueryParsing normalisation stages
     * 
     * @param inputParameterMap A map containing input parameter names as keys and their corresponding values as values
     * @return A map containing the input parameters after they have been parsed for this query type
     */
    Map<String, Object> parseInputs(Map<String, Object> inputParameterMap);
}
