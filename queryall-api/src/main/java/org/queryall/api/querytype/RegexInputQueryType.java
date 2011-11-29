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
    String getInputRegex();
    
    Pattern getInputRegexPattern();
    
    void setInputRegex(String nextInputRegex);
}
