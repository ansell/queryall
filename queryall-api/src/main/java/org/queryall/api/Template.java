package org.queryall.api;


import java.util.Collection;

public abstract class Template implements BaseQueryAllInterface, Comparable<Template>
{
    public abstract String getTemplateString();
    
    public abstract void setTemplateString(String templateString);
    
    public abstract String getContentType();
    
    public abstract void setContentType(String contentType);
    
    public abstract String getMatchRegex();
    
    public abstract void setMatchRegex(String matchRegex);
    
    public abstract Collection<org.openrdf.model.URI> getReferencedTemplates();
    
    public abstract void setReferencedTemplates(Collection<org.openrdf.model.URI> referencedTemplates);
    
    public abstract boolean isNativeFunction();
    
    public abstract String getNativeFunctionUri();
}
