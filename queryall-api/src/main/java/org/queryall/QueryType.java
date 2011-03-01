package org.queryall;

import java.util.List;
import java.util.Collection;
import java.util.regex.Pattern;

public abstract class QueryType implements BaseQueryAllInterface, Comparable<QueryType>
{
    public abstract String getInputRegex();
    
    public abstract void setInputRegex(String nextInputRegex);

    public abstract Pattern getInputRegexPattern();
    
    public abstract boolean getInRobotsTxt();
    
    public abstract void setInRobotsTxt(boolean inRobotsTxt);

    public abstract boolean getHandleAllNamespaces();
    
    public abstract void setHandleAllNamespaces(boolean handleAllNamespaces);

    public abstract List<String> matchesForQueryString(String queryString);
    
    public abstract org.openrdf.model.URI getProfileIncludeExcludeOrder();
    
    public abstract void setProfileIncludeExcludeOrder(org.openrdf.model.URI profileIncludeExcludeOrder);
    
    public abstract String getTemplateString();
    
    public abstract void setTemplateString(String templateString);
    
    public abstract String getOutputRdfXmlString();
    
    public abstract void setOutputRdfXmlString(String outputRdfXmlString);
    
    public abstract String getStandardUriTemplateString();
    
    public abstract void setStandardUriTemplateString(String standardUriTemplateString);
    
    public abstract String getQueryUriTemplateString();
    
    public abstract void setQueryUriTemplateString(String queryUriTemplateString);
    
    public abstract boolean isInputVariablePublic(int inputVariableIndex);

    public abstract void setTitle(String title);
    
    public abstract String getTitle();

    public abstract void setIncludeDefaults(boolean includeDefaults);
    
    public abstract boolean getIncludeDefaults();

    public abstract void setIsNamespaceSpecific(boolean isNamespaceSpecific);

    public abstract boolean getIsNamespaceSpecific();

    public abstract void setIsPageable(boolean isPageable);

    public abstract boolean getIsPageable();

    public abstract Collection<org.openrdf.model.URI> getSemanticallyLinkedQueryTypes();
    
    public abstract void setSemanticallyLinkedQueryTypes(Collection<org.openrdf.model.URI> semanticallyLinkedQueryTypes);
    
    public abstract Collection<org.openrdf.model.URI> getNamespacesToHandle();
    
    public abstract void setNamespacesToHandle(Collection<org.openrdf.model.URI> namespacesToHandle);
    
    public abstract org.openrdf.model.URI getNamespaceMatchMethod();
    
    public abstract void setNamespaceMatchMethod(org.openrdf.model.URI namespaceMatchMethod);
    
    public abstract boolean handlesNamespaceUris(Collection<Collection<org.openrdf.model.URI>> namespacesToCheck);
    
    public abstract boolean handlesNamespacesSpecifically(Collection<Collection<org.openrdf.model.URI>> namespacesToCheck);

    public abstract void setPublicIdentifierIndexes(int[] publicIdentifierIndexes);

    public abstract int[] getPublicIdentifierIndexes();

    public abstract void setNamespaceInputIndexes(int[] namespaceInputIndexes);

    public abstract int[] getNamespaceInputIndexes();

    public abstract Collection<org.openrdf.model.URI> getIncludedQueryTemplates();
    
    public abstract void setIncludedQueryTemplates(Collection<org.openrdf.model.URI> includedQueryTemplates);
    
    public abstract Collection<org.openrdf.model.URI> getIncludedQueryParameters();
    
    public abstract void setIncludedQueryParameters(Collection<org.openrdf.model.URI> includedQueryParameters);
    
    public abstract Collection<org.openrdf.model.URI> getIncludedStaticOutputTemplates();
    
    public abstract void setIncludedStaticOutputTemplates(Collection<org.openrdf.model.URI> includedStaticOutputTemplates);
    
    
    
}
