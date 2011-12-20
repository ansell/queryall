/**
 * 
 */
package org.queryall.api.test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.queryall.api.profile.Profile;
import org.queryall.api.querytype.InputQueryType;
import org.queryall.api.querytype.OutputQueryType;
import org.queryall.api.querytype.ProcessorQueryType;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.utils.QueryAllNamespaces;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 *
 */
public class DummyQueryType implements QueryType, InputQueryType, ProcessorQueryType, OutputQueryType
{
    
    /**
     * 
     */
    public DummyQueryType()
    {
        // TODO Auto-generated constructor stub
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.BaseQueryAllInterface#addUnrecognisedStatement(org.openrdf.model.Statement)
     */
    @Override
    public void addUnrecognisedStatement(Statement unrecognisedStatement)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.BaseQueryAllInterface#getCurationStatus()
     */
    @Override
    public URI getCurationStatus()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.BaseQueryAllInterface#getDefaultNamespace()
     */
    @Override
    public QueryAllNamespaces getDefaultNamespace()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.BaseQueryAllInterface#getDescription()
     */
    @Override
    public String getDescription()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.BaseQueryAllInterface#getElementTypes()
     */
    @Override
    public Set<URI> getElementTypes()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.BaseQueryAllInterface#getKey()
     */
    @Override
    public URI getKey()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.BaseQueryAllInterface#getTitle()
     */
    @Override
    public String getTitle()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.BaseQueryAllInterface#getUnrecognisedStatements()
     */
    @Override
    public Collection<Statement> getUnrecognisedStatements()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.BaseQueryAllInterface#resetUnrecognisedStatements()
     */
    @Override
    public Collection<Statement> resetUnrecognisedStatements()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.BaseQueryAllInterface#setCurationStatus(org.openrdf.model.URI)
     */
    @Override
    public void setCurationStatus(URI curationStatus)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.BaseQueryAllInterface#setDescription(java.lang.String)
     */
    @Override
    public void setDescription(String description)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.BaseQueryAllInterface#setKey(java.lang.String)
     */
    @Override
    public void setKey(String nextKey) throws IllegalArgumentException
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.BaseQueryAllInterface#setKey(org.openrdf.model.URI)
     */
    @Override
    public void setKey(URI nextKey)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.BaseQueryAllInterface#setTitle(java.lang.String)
     */
    @Override
    public void setTitle(String title)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.BaseQueryAllInterface#toRdf(org.openrdf.repository.Repository, int, org.openrdf.model.URI[])
     */
    @Override
    public boolean toRdf(Repository myRepository, int modelVersion, URI... contextUris) throws OpenRDFException
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(QueryType arg0)
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.ProfilableInterface#getProfileIncludeExcludeOrder()
     */
    @Override
    public URI getProfileIncludeExcludeOrder()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.ProfilableInterface#isUsedWithProfileList(java.util.List, boolean, boolean)
     */
    @Override
    public boolean isUsedWithProfileList(List<Profile> orderedProfileList, boolean allowImplicitInclusions,
            boolean includeNonProfileMatched)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.base.ProfilableInterface#setProfileIncludeExcludeOrder(org.openrdf.model.URI)
     */
    @Override
    public void setProfileIncludeExcludeOrder(URI profileIncludeExcludeOrder)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.OutputQueryType#getOutputString()
     */
    @Override
    public String getOutputString()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.OutputQueryType#setOutputString(java.lang.String)
     */
    @Override
    public void setOutputString(String outputString)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.ProcessorQueryType#getProcessingTemplateString()
     */
    @Override
    public String getProcessingTemplateString()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.ProcessorQueryType#parseProcessorQuery(java.lang.String)
     */
    @Override
    public Object parseProcessorQuery(String query)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.ProcessorQueryType#processQueryVariables(java.util.Map)
     */
    @Override
    public Map<String, Object> processQueryVariables(Map<String, Object> queryVariables)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.ProcessorQueryType#setProcessingTemplateString(java.lang.String)
     */
    @Override
    public void setProcessingTemplateString(String templateString)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.ProcessorQueryType#substituteQueryVariables(java.util.Map)
     */
    @Override
    public String substituteQueryVariables(Map<String, Object> processedQueryVariables)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.InputQueryType#addExpectedInputParameter(java.lang.String)
     */
    @Override
    public void addExpectedInputParameter(String expectedInputParameter)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.InputQueryType#getExpectedInputParameters()
     */
    @Override
    public Collection<String> getExpectedInputParameters()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.InputQueryType#matchesForQueryParameters(java.util.Map)
     */
    @Override
    public Map<String, List<String>> matchesForQueryParameters(Map<String, String> queryParameters)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.InputQueryType#matchesQueryParameters(java.util.Map)
     */
    @Override
    public boolean matchesQueryParameters(Map<String, String> queryString)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.InputQueryType#parseInputs(java.util.Map)
     */
    @Override
    public Map<String, Object> parseInputs(Map<String, Object> inputParameterMap)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.InputQueryType#resetExpectedInputParameters()
     */
    @Override
    public boolean resetExpectedInputParameters()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#addLinkedQueryType(org.openrdf.model.URI)
     */
    @Override
    public void addLinkedQueryType(URI semanticallyLinkedQueryTypes)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#addNamespaceInputTag(java.lang.String)
     */
    @Override
    public void addNamespaceInputTag(String namespaceInputTag)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#addNamespaceToHandle(org.openrdf.model.URI)
     */
    @Override
    public void addNamespaceToHandle(URI namespaceToHandle)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#addPublicIdentifierTag(java.lang.String)
     */
    @Override
    public void addPublicIdentifierTag(String publicIdentifierTag)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#getHandleAllNamespaces()
     */
    @Override
    public boolean getHandleAllNamespaces()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#getIncludeDefaults()
     */
    @Override
    public boolean getIncludeDefaults()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#getInRobotsTxt()
     */
    @Override
    public boolean getInRobotsTxt()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#getIsDummyQueryType()
     */
    @Override
    public boolean getIsDummyQueryType()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#getIsNamespaceSpecific()
     */
    @Override
    public boolean getIsNamespaceSpecific()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#getIsPageable()
     */
    @Override
    public boolean getIsPageable()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#getLinkedQueryTypes()
     */
    @Override
    public Set<URI> getLinkedQueryTypes()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#getNamespaceInputTags()
     */
    @Override
    public Set<String> getNamespaceInputTags()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#getNamespaceMatchMethod()
     */
    @Override
    public URI getNamespaceMatchMethod()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#getNamespacesToHandle()
     */
    @Override
    public Set<URI> getNamespacesToHandle()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#getPublicIdentifierTags()
     */
    @Override
    public Set<String> getPublicIdentifierTags()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#getQueryUriTemplateString()
     */
    @Override
    public String getQueryUriTemplateString()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#getStandardUriTemplateString()
     */
    @Override
    public String getStandardUriTemplateString()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#handlesNamespacesSpecifically(java.util.Map)
     */
    @Override
    public boolean handlesNamespacesSpecifically(Map<String, Collection<URI>> namespacesToCheck)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#handlesNamespaceUris(java.util.Map)
     */
    @Override
    public boolean handlesNamespaceUris(Map<String, Collection<URI>> namespacesToCheck)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#isInputVariableNamespace(java.lang.String)
     */
    @Override
    public boolean isInputVariableNamespace(String nextMatchTag)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#isInputVariablePublic(java.lang.String)
     */
    @Override
    public boolean isInputVariablePublic(String inputVariableTag)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#resetLinkedQueryTypes()
     */
    @Override
    public boolean resetLinkedQueryTypes()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#resetNamespaceInputTags()
     */
    @Override
    public boolean resetNamespaceInputTags()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#resetNamespacesToHandle()
     */
    @Override
    public boolean resetNamespacesToHandle()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#resetPublicIdentifierTags()
     */
    @Override
    public boolean resetPublicIdentifierTags()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#setHandleAllNamespaces(boolean)
     */
    @Override
    public void setHandleAllNamespaces(boolean handleAllNamespaces)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#setIncludeDefaults(boolean)
     */
    @Override
    public void setIncludeDefaults(boolean includeDefaults)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#setInRobotsTxt(boolean)
     */
    @Override
    public void setInRobotsTxt(boolean inRobotsTxt)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#setIsDummyQueryType(boolean)
     */
    @Override
    public void setIsDummyQueryType(boolean isDummyQueryType)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#setIsNamespaceSpecific(boolean)
     */
    @Override
    public void setIsNamespaceSpecific(boolean isNamespaceSpecific)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#setIsPageable(boolean)
     */
    @Override
    public void setIsPageable(boolean isPageable)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#setNamespaceMatchMethod(org.openrdf.model.URI)
     */
    @Override
    public void setNamespaceMatchMethod(URI namespaceMatchMethod)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#setQueryUriTemplateString(java.lang.String)
     */
    @Override
    public void setQueryUriTemplateString(String queryUriTemplateString)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see org.queryall.api.querytype.QueryType#setStandardUriTemplateString(java.lang.String)
     */
    @Override
    public void setStandardUriTemplateString(String standardUriTemplateString)
    {
        // TODO Auto-generated method stub
        
    }
    
}
