package org.queryall.api.querytype;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.openrdf.model.URI;
import org.queryall.api.base.BaseQueryAllInterface;
import org.queryall.api.base.ProfilableInterface;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface QueryType extends BaseQueryAllInterface, Comparable<QueryType>, ProfilableInterface
{
    void addNamespaceInputTag(String namespaceInputTag);
    
    void addNamespaceToHandle(URI namespaceToHandle);
    
    void addPublicIdentifierTag(String publicIdentifierTag);
    
    void addSemanticallyLinkedQueryType(URI semanticallyLinkedQueryTypes);
    
    boolean getHandleAllNamespaces();
    
    boolean getIncludeDefaults();
    
    boolean getInRobotsTxt();
    
    boolean getIsDummyQueryType();
    
    boolean getIsNamespaceSpecific();
    
    boolean getIsPageable();
    
    Collection<String> getNamespaceInputTags();
    
    URI getNamespaceMatchMethod();
    
    Collection<URI> getNamespacesToHandle();
    
    // TODO: extract this into a new interface called RdfXmlOutputQueryType
    String getOutputRdfXmlString();
    
    Collection<String> getPublicIdentifierTags();
    
    String getQueryUriTemplateString();
    
    Collection<URI> getSemanticallyLinkedQueryTypes();
    
    String getStandardUriTemplateString();
    
    String getTemplateString();
    
    boolean handlesNamespacesSpecifically(Collection<Collection<URI>> namespacesToCheck);
    
    boolean handlesNamespaceUris(Collection<Collection<URI>> namespacesToCheck);
    
    boolean isInputVariablePublic(String inputVariableTag);
    
    Map<String, List<String>> matchesForQueryString(String queryString);
    
    boolean matchesQueryString(String queryString);
    
    void setHandleAllNamespaces(boolean handleAllNamespaces);
    
    void setIncludeDefaults(boolean includeDefaults);
    
    void setInRobotsTxt(boolean inRobotsTxt);
    
    void setIsDummyQueryType(boolean isDummyQueryType);
    
    void setIsNamespaceSpecific(boolean isNamespaceSpecific);
    
    void setIsPageable(boolean isPageable);
    
    void setNamespaceMatchMethod(URI namespaceMatchMethod);
    
    // TODO: extract this into a new interface called RdfXmlOutputQueryType
    void setOutputRdfXmlString(String outputRdfXmlString);
    
    void setQueryUriTemplateString(String queryUriTemplateString);
    
    void setStandardUriTemplateString(String standardUriTemplateString);
    
    void setTemplateString(String templateString);
    
}
