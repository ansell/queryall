package org.queryall.api.namespace;

import java.util.Collection;

import org.openrdf.model.URI;
import org.queryall.api.base.BaseQueryAllInterface;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface NamespaceEntry extends BaseQueryAllInterface, Comparable<NamespaceEntry>
{
    Collection<String> getAlternativePrefixes();
    
    URI getAuthority();
    
    boolean getConvertQueriesToPreferredPrefix();
    
    String getDescription();
    
    String getPreferredPrefix();
    
    String getSeparator();
    
    String getUriTemplate();
    
    void addAlternativePrefix(String alternativePrefix);
    
    void setAuthority(URI authority);
    
    void setConvertQueriesToPreferredPrefix(boolean convertQueriesToPreferredPrefix);
    
    void setDescription(String description);
    
    void setPreferredPrefix(String preferredPrefix);
    
    void setSeparator(String separator);
    
    void setUriTemplate(String uriTemplate);
}
