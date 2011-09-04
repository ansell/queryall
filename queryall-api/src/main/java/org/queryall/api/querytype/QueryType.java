package org.queryall.api.querytype;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.openrdf.model.URI;
import org.queryall.api.base.BaseQueryAllInterface;
import org.queryall.api.base.ProfilableInterface;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface QueryType extends BaseQueryAllInterface, Comparable<QueryType>, ProfilableInterface
{
    void addNamespaceToHandle(URI namespaceToHandle);
    
    boolean getHandleAllNamespaces();
    
    boolean getIncludeDefaults();
    
    boolean getInRobotsTxt();
    
    boolean getIsDummyQueryType();
    
    boolean getIsNamespaceSpecific();
    
    boolean getIsPageable();
    
    URI getNamespaceMatchMethod();
    
    Collection<URI> getNamespacesToHandle();
    
    // TODO: extract this into a new interface called RdfXmlOutputQueryType
    String getOutputRdfXmlString();

    Collection<URI> getSemanticallyLinkedQueryTypes();
    
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
    
    // TODO: extract this into a new interface called RegexInputQueryType
    void setNamespaceInputTags(Collection<String> namespaceInputTags);
    
    void setNamespaceMatchMethod(URI namespaceMatchMethod);
    
    void setNamespacesToHandle(Collection<URI> namespacesToHandle);
    
    // TODO: extract this into a new interface called RdfXmlOutputQueryType
    void setOutputRdfXmlString(String outputRdfXmlString);
    
    // TODO: extract this into a new interface called RegexInputQueryType
    void setPublicIdentifierIndexes(int[] publicIdentifierIndexes);
    
    // TODO: extract this into a new interface called RegexInputQueryType
    void setQueryUriTemplateString(String queryUriTemplateString);
    
    void setSemanticallyLinkedQueryTypes(Collection<URI> semanticallyLinkedQueryTypes);
    
    // TODO: extract this into a new interface called RegexInputQueryType
    void setStandardUriTemplateString(String standardUriTemplateString);
    
    // TODO: extract this into a new interface called RegexInputQueryType
    void setTemplateString(String templateString);
    
}
