package org.queryall.api.querytype;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.openrdf.model.URI;
import org.queryall.api.BaseQueryAllInterface;
import org.queryall.api.ProfilableInterface;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface QueryType extends BaseQueryAllInterface, Comparable<QueryType>, ProfilableInterface
{
    void addNamespaceToHandle(URI namespaceToHandle);
    
    boolean getHandleAllNamespaces();
    
    boolean getIncludeDefaults();
    
    // TODO: extract this into a new interface called RegexInputQueryType
    String getInputRegex();
    
    // TODO: extract this into a new interface called RegexInputQueryType
    Pattern getInputRegexPattern();
    
    boolean getInRobotsTxt();
    
    boolean getIsDummyQueryType();
    
    boolean getIsNamespaceSpecific();
    
    boolean getIsPageable();
    
    // TODO: extract this into a new interface called RegexInputQueryType
    int[] getNamespaceInputIndexes();
    
    URI getNamespaceMatchMethod();
    
    Collection<URI> getNamespacesToHandle();
    
    // TODO: extract this into a new interface called RdfXmlOutputQueryType
    String getOutputRdfXmlString();
    
    // TODO: extract this into a new interface called RegexInputQueryType
    int[] getPublicIdentifierIndexes();
    
    // TODO: extract this into a new interface called RegexInputQueryType
    String getQueryUriTemplateString();
    
    Collection<URI> getSemanticallyLinkedQueryTypes();
    
    // TODO: extract this into a new interface called RegexInputQueryType
    String getStandardUriTemplateString();
    
    // TODO: extract this into a new interface called RegexInputQueryType
    String getTemplateString();
    
    boolean handlesNamespacesSpecifically(Collection<Collection<URI>> namespacesToCheck);
    
    boolean handlesNamespaceUris(Collection<Collection<URI>> namespacesToCheck);
    
    // TODO: extract this into a new interface called RegexInputQueryType
    boolean isInputVariablePublic(int inputVariableIndex);
    
    // TODO: extract this into a new interface called RegexInputQueryType
    List<String> matchesForQueryString(String queryString);
    
    // TODO: extract this into a new interface called RegexInputQueryType
    boolean matchesQueryString(String queryString);
    
    void setHandleAllNamespaces(boolean handleAllNamespaces);
    
    void setIncludeDefaults(boolean includeDefaults);
    
    // TODO: extract this into a new interface called RegexInputQueryType
    void setInputRegex(String nextInputRegex);
    
    void setInRobotsTxt(boolean inRobotsTxt);
    
    void setIsDummyQueryType(boolean isDummyQueryType);
    
    void setIsNamespaceSpecific(boolean isNamespaceSpecific);
    
    void setIsPageable(boolean isPageable);
    
    // TODO: extract this into a new interface called RegexInputQueryType
    void setNamespaceInputIndexes(int[] namespaceInputIndexes);
    
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
