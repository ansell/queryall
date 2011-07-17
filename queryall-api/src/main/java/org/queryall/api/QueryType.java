package org.queryall.api;

import java.util.List;
import java.util.Collection;
import java.util.regex.Pattern;

import org.openrdf.model.URI;

public interface QueryType extends BaseQueryAllInterface, Comparable<QueryType>, ProfilableInterface
{
    public abstract String getInputRegex();
    
    public abstract void setInputRegex(String nextInputRegex);

    public abstract Pattern getInputRegexPattern();
    
    public abstract boolean getInRobotsTxt();
    
    public abstract void setInRobotsTxt(boolean inRobotsTxt);

    public abstract boolean getHandleAllNamespaces();
    
    public abstract void setHandleAllNamespaces(boolean handleAllNamespaces);

    public abstract boolean matchesQueryString(String queryString);
    
    public abstract List<String> matchesForQueryString(String queryString);
    
    public abstract String getTemplateString();
    
    public abstract void setTemplateString(String templateString);
    
    public abstract String getOutputRdfXmlString();
    
    public abstract void setOutputRdfXmlString(String outputRdfXmlString);
    
    public abstract String getStandardUriTemplateString();
    
    public abstract void setStandardUriTemplateString(String standardUriTemplateString);
    
    public abstract String getQueryUriTemplateString();
    
    public abstract void setQueryUriTemplateString(String queryUriTemplateString);
    
    public abstract boolean isInputVariablePublic(int inputVariableIndex);

    public abstract void setIncludeDefaults(boolean includeDefaults);
    
    public abstract boolean getIncludeDefaults();

    public abstract void setIsNamespaceSpecific(boolean isNamespaceSpecific);

    public abstract boolean getIsNamespaceSpecific();

    public abstract void setIsPageable(boolean isPageable);

    public abstract boolean getIsPageable();

    public abstract boolean getIsDummyQueryType();    
    
    public abstract void setIsDummyQueryType(boolean isDummyQueryType);    

    public abstract Collection<URI> getSemanticallyLinkedQueryTypes();
    
    public abstract void setSemanticallyLinkedQueryTypes(Collection<URI> semanticallyLinkedQueryTypes);
    
    public abstract Collection<URI> getNamespacesToHandle();
    
    public abstract void setNamespacesToHandle(Collection<URI> namespacesToHandle);
    
    public abstract void addNamespaceToHandle(URI namespaceToHandle);
    
    public abstract URI getNamespaceMatchMethod();
    
    public abstract void setNamespaceMatchMethod(URI namespaceMatchMethod);
    
    public abstract boolean handlesNamespaceUris(Collection<Collection<URI>> namespacesToCheck);
    
    public abstract boolean handlesNamespacesSpecifically(Collection<Collection<URI>> namespacesToCheck);

    public abstract void setPublicIdentifierIndexes(int[] publicIdentifierIndexes);

    public abstract int[] getPublicIdentifierIndexes();

    public abstract void setNamespaceInputIndexes(int[] namespaceInputIndexes);

    public abstract int[] getNamespaceInputIndexes();

}
