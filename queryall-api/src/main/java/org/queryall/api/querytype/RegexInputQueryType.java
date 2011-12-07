package org.queryall.api.querytype;

import java.util.regex.Pattern;

/**
 * An InputQueryType that uses a Regular Expression to derive input parameters from a single input
 * string.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface RegexInputQueryType extends InputQueryType
{
    /**
     * 
     * @return The Regular Expression used to match this query type against input query strings
     */
    String getInputRegex();
    
    /**
     * 
     * @return The compiled Regular Expression Pattern used to match this query type against input
     *         query strings
     */
    Pattern getInputRegexPattern();
    
    /**
     * 
     * @param nextInputRegex
     *            The Regular Expression used to match this query type against input query strings
     */
    void setInputRegex(String nextInputRegex);
}
