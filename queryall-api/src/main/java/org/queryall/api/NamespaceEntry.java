package org.queryall.api;

import java.util.Collection;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface NamespaceEntry extends BaseQueryAllInterface, Comparable<NamespaceEntry>
{
    Collection<String> getAlternativePrefixes();
    
    String getAuthority();
    
    boolean getConvertQueriesToPreferredPrefix();
    
    String getDescription();
    
    String getIdentifierRegex();
    
    String getPreferredPrefix();
    
    String getSeparator();
    
    String getUriTemplate();
    
    void setAlternativePrefixes(Collection<String> alternativePrefixes);
    
    void setAuthority(String authority);
    
    void setConvertQueriesToPreferredPrefix(boolean convertQueriesToPreferredPrefix);
    
    void setDescription(String description);
    
    void setIdentifierRegex(String identifierRegex);
    
    void setPreferredPrefix(String preferredPrefix);
    
    void setSeparator(String separator);
    
    void setUriTemplate(String uriTemplate);
}
