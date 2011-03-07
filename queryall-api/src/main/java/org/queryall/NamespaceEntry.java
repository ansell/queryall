package org.queryall;

import org.queryall.BaseQueryAllInterface;
import java.util.Collection;

public abstract class NamespaceEntry implements BaseQueryAllInterface, Comparable<NamespaceEntry>
{
    public abstract String getPreferredPrefix();
    
    public abstract void setPreferredPrefix(String preferredPrefix);
    
    public abstract Collection<String> getAlternativePrefixes();
    
    public abstract void setAlternativePrefixes(Collection<String> alternativePrefixes);
    
    public abstract void setAuthority(String authority);
    
    public abstract String getAuthority();
    
    public abstract void setDescription(String description);
    
    public abstract String getDescription();
    
    public abstract void setIdentifierRegex(String identifierRegex);
    
    public abstract String getIdentifierRegex();

    public abstract void setUriTemplate(String uriTemplate);
    
    public abstract String getUriTemplate();
    
    public abstract void setSeparator(String separator);
    
    public abstract String getSeparator();
    
    public abstract void setConvertQueriesToPreferredPrefix(boolean convertQueriesToPreferredPrefix);
    
    public abstract boolean getConvertQueriesToPreferredPrefix();
    
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        
        result.append("title=").append(this.getTitle());
        result.append("key=").append(this.getKey().stringValue());

        return result.toString();
    }
}
