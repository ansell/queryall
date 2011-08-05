package org.queryall.api;


import java.util.Collection;

public interface NamespaceEntry extends BaseQueryAllInterface, Comparable<NamespaceEntry>
{
    String getPreferredPrefix();
    
    void setPreferredPrefix(String preferredPrefix);
    
    Collection<String> getAlternativePrefixes();
    
    void setAlternativePrefixes(Collection<String> alternativePrefixes);
    
    void setAuthority(String authority);
    
    String getAuthority();
    
    void setDescription(String description);
    
    String getDescription();
    
    void setIdentifierRegex(String identifierRegex);
    
    String getIdentifierRegex();

    void setUriTemplate(String uriTemplate);
    
    String getUriTemplate();
    
    void setSeparator(String separator);
    
    String getSeparator();
    
    void setConvertQueriesToPreferredPrefix(boolean convertQueriesToPreferredPrefix);
    
    boolean getConvertQueriesToPreferredPrefix();
}
