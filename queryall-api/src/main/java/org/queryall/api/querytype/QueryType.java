package org.queryall.api.querytype;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.openrdf.model.URI;
import org.queryall.api.base.BaseQueryAllInterface;
import org.queryall.api.base.ProfilableInterface;

/**
 * The base type for queries, containing a variety of links to other query types, namespace
 * matching, and named input tags.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface QueryType extends BaseQueryAllInterface, Comparable<QueryType>, ProfilableInterface
{
    void addLinkedQueryType(URI semanticallyLinkedQueryTypes);
    
    void addNamespaceInputTag(String namespaceInputTag);
    
    void addNamespaceToHandle(URI namespaceToHandle);
    
    void addPublicIdentifierTag(String publicIdentifierTag);
    
    boolean getHandleAllNamespaces();
    
    boolean getIncludeDefaults();
    
    boolean getInRobotsTxt();
    
    boolean getIsDummyQueryType();
    
    boolean getIsNamespaceSpecific();
    
    boolean getIsPageable();
    
    Collection<URI> getLinkedQueryTypes();
    
    Collection<String> getNamespaceInputTags();
    
    URI getNamespaceMatchMethod();
    
    Collection<URI> getNamespacesToHandle();
    
    Collection<String> getPublicIdentifierTags();
    
    String getQueryUriTemplateString();
    
    String getStandardUriTemplateString();
    
    String getTemplateString();
    
    boolean handlesNamespacesSpecifically(Collection<Collection<URI>> namespacesToCheck);
    
    boolean handlesNamespaceUris(Collection<Collection<URI>> namespacesToCheck);
    
    boolean isInputVariableNamespace(String nextMatchTag);
    
    boolean isInputVariablePublic(String inputVariableTag);
    
    Map<String, List<String>> matchesForQueryParameters(Map<String, String> queryParameters);
    
    boolean matchesQueryParameters(Map<String, String> queryString);
    
    boolean resetLinkedQueryTypes();
    
    boolean resetNamespaceInputTags();
    
    boolean resetNamespacesToHandle();
    
    boolean resetPublicIdentifierTags();
    
    void setHandleAllNamespaces(boolean handleAllNamespaces);
    
    void setIncludeDefaults(boolean includeDefaults);
    
    void setInRobotsTxt(boolean inRobotsTxt);
    
    void setIsDummyQueryType(boolean isDummyQueryType);
    
    void setIsNamespaceSpecific(boolean isNamespaceSpecific);
    
    void setIsPageable(boolean isPageable);
    
    void setNamespaceMatchMethod(URI namespaceMatchMethod);
    
    void setQueryUriTemplateString(String queryUriTemplateString);
    
    void setStandardUriTemplateString(String standardUriTemplateString);
    
    void setTemplateString(String templateString);
    
}
