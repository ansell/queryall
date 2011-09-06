package org.queryall.api.querytype;

import java.util.regex.Pattern;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface RegexInputQueryType extends InputQueryType
{
    String getInputRegex();
    
    Pattern getInputRegexPattern();
    
    void setInputRegex(String nextInputRegex);
}
