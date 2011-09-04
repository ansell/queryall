package org.queryall.api.querytype;

import java.util.regex.Pattern;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface RegexInputQueryType extends QueryType
{
    // TODO: extract this into a new interface called RegexInputQueryType
    String getInputRegex();
    
    // TODO: extract this into a new interface called RegexInputQueryType
    Pattern getInputRegexPattern();
    
    // TODO: extract this into a new interface called RegexInputQueryType
    void setInputRegex(String nextInputRegex);
    
}
