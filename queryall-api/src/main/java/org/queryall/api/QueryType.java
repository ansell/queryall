package org.queryall.api;

import java.util.List;
import java.util.Collection;
import java.util.regex.Pattern;

import org.openrdf.model.URI;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface QueryType extends BaseQueryAllInterface, Comparable<QueryType>, ProfilableInterface
{
    String getInputRegex();
    
    void setInputRegex(String nextInputRegex);

    Pattern getInputRegexPattern();
    
    boolean getInRobotsTxt();
    
    void setInRobotsTxt(boolean inRobotsTxt);

    boolean getHandleAllNamespaces();
    
    void setHandleAllNamespaces(boolean handleAllNamespaces);

    boolean matchesQueryString(String queryString);
    
    List<String> matchesForQueryString(String queryString);
    
    String getTemplateString();
    
    void setTemplateString(String templateString);
    
    String getOutputRdfXmlString();
    
    void setOutputRdfXmlString(String outputRdfXmlString);
    
    String getStandardUriTemplateString();
    
    void setStandardUriTemplateString(String standardUriTemplateString);
    
    String getQueryUriTemplateString();
    
    void setQueryUriTemplateString(String queryUriTemplateString);
    
    boolean isInputVariablePublic(int inputVariableIndex);

    void setIncludeDefaults(boolean includeDefaults);
    
    boolean getIncludeDefaults();

    void setIsNamespaceSpecific(boolean isNamespaceSpecific);

    boolean getIsNamespaceSpecific();

    void setIsPageable(boolean isPageable);

    boolean getIsPageable();

    boolean getIsDummyQueryType();    
    
    void setIsDummyQueryType(boolean isDummyQueryType);    

    Collection<URI> getSemanticallyLinkedQueryTypes();
    
    void setSemanticallyLinkedQueryTypes(Collection<URI> semanticallyLinkedQueryTypes);
    
    Collection<URI> getNamespacesToHandle();
    
    void setNamespacesToHandle(Collection<URI> namespacesToHandle);
    
    void addNamespaceToHandle(URI namespaceToHandle);
    
    URI getNamespaceMatchMethod();
    
    void setNamespaceMatchMethod(URI namespaceMatchMethod);
    
    boolean handlesNamespaceUris(Collection<Collection<URI>> namespacesToCheck);
    
    boolean handlesNamespacesSpecifically(Collection<Collection<URI>> namespacesToCheck);

    void setPublicIdentifierIndexes(int[] publicIdentifierIndexes);

    int[] getPublicIdentifierIndexes();

    void setNamespaceInputIndexes(int[] namespaceInputIndexes);

    int[] getNamespaceInputIndexes();

}
