package org.queryall.api.querytype;

import java.util.List;
import java.util.regex.Pattern;

import org.queryall.api.base.BaseQueryAllInterface;
import org.queryall.api.base.ProfilableInterface;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface RegexInputQueryType extends BaseQueryAllInterface, Comparable<RegexInputQueryType>, ProfilableInterface
{
    // TODO: extract this into a new interface called RegexInputQueryType
    String getInputRegex();
    
    // TODO: extract this into a new interface called RegexInputQueryType
    Pattern getInputRegexPattern();
    
    // TODO: extract this into a new interface called RegexInputQueryType
    int[] getNamespaceInputIndexes();
    
    // TODO: extract this into a new interface called RegexInputQueryType
    int[] getPublicIdentifierIndexes();
    
    // TODO: extract this into a new interface called RegexInputQueryType
    String getQueryUriTemplateString();
    
    // TODO: extract this into a new interface called RegexInputQueryType
    String getStandardUriTemplateString();
    
    // TODO: extract this into a new interface called RegexInputQueryType
    String getTemplateString();
    
    // TODO: extract this into a new interface called RegexInputQueryType
    void setInputRegex(String nextInputRegex);
    
    // TODO: extract this into a new interface called RegexInputQueryType
    void setNamespaceInputIndexes(int[] namespaceInputIndexes);
    
    // TODO: extract this into a new interface called RegexInputQueryType
    void setPublicIdentifierIndexes(int[] publicIdentifierIndexes);
    
    // TODO: extract this into a new interface called RegexInputQueryType
    void setQueryUriTemplateString(String queryUriTemplateString);
    
    // TODO: extract this into a new interface called RegexInputQueryType
    void setStandardUriTemplateString(String standardUriTemplateString);
    
    // TODO: extract this into a new interface called SparqlInputQueryType
    void setTemplateString(String templateString);
    
}
