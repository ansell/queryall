/**
 * 
 */
package org.queryall.api.querytype;

import java.util.Collection;
import java.util.List;
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
     * @param expectedInputParameter
     *            A string identifying an input parameter that is expected by this query type
     */
    void addExpectedInputParameter(String expectedInputParameter);
    
    /**
     * 
     * @return A collection of strings identifying input parameters that are expected by this query
     *         type
     */
    Collection<String> getExpectedInputParameters();
    
    /**
     * 
     * @param queryParameters
     *            A Map of named inputs from a users query. NOTE: These will not directly provide
     * @return A list of matches based on the matching methodology for this InputQueryType
     */
    Map<String, List<String>> matchesForQueryParameters(Map<String, String> queryParameters);
    
    boolean matchesQueryParameters(Map<String, String> queryString);
    
    /**
     * This method parses the inputs between the afterQueryCreation and afterQueryParsing
     * normalisation stages
     * 
     * @param inputParameterMap
     *            A map containing input parameter names as keys and their corresponding values as
     *            Object values
     * @return A map containing the input parameters after they have been parsed for this query type
     */
    Map<String, Object> parseInputs(Map<String, Object> inputParameterMap);
    
    /**
     * Resets the list of expected input parameters
     * 
     * @return True if the collection of expected input parameters was reset and false otherwise.
     */
    boolean resetExpectedInputParameters();
    
}
