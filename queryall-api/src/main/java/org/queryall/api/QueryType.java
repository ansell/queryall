package org.queryall.api;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.openrdf.model.URI;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface QueryType extends BaseQueryAllInterface, Comparable<QueryType>, ProfilableInterface
{
    void addNamespaceToHandle(URI namespaceToHandle);
    
    boolean getHandleAllNamespaces();
    
    boolean getIncludeDefaults();
    
    String getInputRegex();
    
    Pattern getInputRegexPattern();
    
    boolean getInRobotsTxt();
    
    boolean getIsDummyQueryType();
    
    boolean getIsNamespaceSpecific();
    
    boolean getIsPageable();
    
    int[] getNamespaceInputIndexes();
    
    URI getNamespaceMatchMethod();
    
    Collection<URI> getNamespacesToHandle();
    
    String getOutputRdfXmlString();
    
    int[] getPublicIdentifierIndexes();
    
    String getQueryUriTemplateString();
    
    Collection<URI> getSemanticallyLinkedQueryTypes();
    
    String getStandardUriTemplateString();
    
    String getTemplateString();
    
    boolean handlesNamespacesSpecifically(Collection<Collection<URI>> namespacesToCheck);
    
    boolean handlesNamespaceUris(Collection<Collection<URI>> namespacesToCheck);
    
    boolean isInputVariablePublic(int inputVariableIndex);
    
    List<String> matchesForQueryString(String queryString);
    
    boolean matchesQueryString(String queryString);
    
    void setHandleAllNamespaces(boolean handleAllNamespaces);
    
    void setIncludeDefaults(boolean includeDefaults);
    
    void setInputRegex(String nextInputRegex);
    
    void setInRobotsTxt(boolean inRobotsTxt);
    
    void setIsDummyQueryType(boolean isDummyQueryType);
    
    void setIsNamespaceSpecific(boolean isNamespaceSpecific);
    
    void setIsPageable(boolean isPageable);
    
    void setNamespaceInputIndexes(int[] namespaceInputIndexes);
    
    void setNamespaceMatchMethod(URI namespaceMatchMethod);
    
    void setNamespacesToHandle(Collection<URI> namespacesToHandle);
    
    void setOutputRdfXmlString(String outputRdfXmlString);
    
    void setPublicIdentifierIndexes(int[] publicIdentifierIndexes);
    
    void setQueryUriTemplateString(String queryUriTemplateString);
    
    void setSemanticallyLinkedQueryTypes(Collection<URI> semanticallyLinkedQueryTypes);
    
    void setStandardUriTemplateString(String standardUriTemplateString);
    
    void setTemplateString(String templateString);
    
}
