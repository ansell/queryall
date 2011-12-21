/**
 * 
 */
package org.queryall.api.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.queryall.api.profile.Profile;
import org.queryall.api.profile.ProfileSchema;
import org.queryall.api.project.ProjectSchema;
import org.queryall.api.querytype.InputQueryType;
import org.queryall.api.querytype.OutputQueryType;
import org.queryall.api.querytype.ProcessorQueryType;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.querytype.QueryTypeSchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.NamespaceMatch;
import org.queryall.api.utils.ProfileMatch;
import org.queryall.api.utils.QueryAllNamespaces;

/**
 * Dummy class that implements the basic contracts for each of QueryType, InputQueryType,
 * ProcessorQueryType, and OutputQueryType
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class DummyQueryType implements QueryType, InputQueryType, ProcessorQueryType, OutputQueryType
{
    
    private Collection<Statement> unrecognisedStatements = new ArrayList<Statement>();
    private URI curationStatus = ProjectSchema.getProjectNotCuratedUri();
    private String description = "";
    private URI key;
    private String title = "";
    private URI profileIncludeExcludeOrder = ProfileSchema.getProfileIncludeExcludeOrderUndefinedUri();
    private String outputString = "";
    private String processingTemplateString = "";
    private Collection<String> expectedInputParameters = new HashSet<String>();
    private boolean handleAllNamespaces = true;
    private boolean includeDefaults = true;
    private boolean inRobotsTxt = false;
    private boolean isDummyQueryType = false;
    private boolean isNamespaceSpecific = false;
    private boolean isPageable = false;
    private Set<URI> linkedQueryTypes = new HashSet<URI>();
    private Set<String> namespaceInputTags = new HashSet<String>();
    private NamespaceMatch namespaceMatchMethod = NamespaceMatch.ANY_MATCHED;
    private Set<URI> namespacesToHandle = new HashSet<URI>();
    private Set<String> publicIdentifierTags = new HashSet<String>();
    private String queryUriTemplateString = "";
    private String standardUriTemplateString = "";
    
    /**
     * 
     */
    public DummyQueryType()
    {
        // TODO Auto-generated constructor stub
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.InputQueryType#addExpectedInputParameter(java.lang.String)
     */
    @Override
    public void addExpectedInputParameter(final String expectedInputParameter)
    {
        this.expectedInputParameters.add(expectedInputParameter);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#addLinkedQueryType(org.openrdf.model.URI)
     */
    @Override
    public void addLinkedQueryType(final URI semanticallyLinkedQueryTypes)
    {
        this.linkedQueryTypes.add(semanticallyLinkedQueryTypes);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#addNamespaceInputTag(java.lang.String)
     */
    @Override
    public void addNamespaceInputTag(final String namespaceInputTag)
    {
        this.namespaceInputTags.add(namespaceInputTag);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#addNamespaceToHandle(org.openrdf.model.URI)
     */
    @Override
    public void addNamespaceToHandle(final URI namespaceToHandle)
    {
        this.namespacesToHandle.add(namespaceToHandle);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#addPublicIdentifierTag(java.lang.String)
     */
    @Override
    public void addPublicIdentifierTag(final String publicIdentifierTag)
    {
        this.publicIdentifierTags.add(publicIdentifierTag);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.queryall.api.base.BaseQueryAllInterface#addUnrecognisedStatement(org.openrdf.model.Statement
     * )
     */
    @Override
    public void addUnrecognisedStatement(final Statement unrecognisedStatement)
    {
        this.unrecognisedStatements.add(unrecognisedStatement);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final QueryType arg0)
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#getCurationStatus()
     */
    @Override
    public URI getCurationStatus()
    {
        return this.curationStatus;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#getDefaultNamespace()
     */
    @Override
    public QueryAllNamespaces getDefaultNamespace()
    {
        return QueryAllNamespaces.QUERY;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#getDescription()
     */
    @Override
    public String getDescription()
    {
        return this.description;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#getElementTypes()
     */
    @Override
    public Set<URI> getElementTypes()
    {
        final Set<URI> types = new HashSet<URI>();
        
        types.add(QueryTypeSchema.getQueryTypeUri());
        
        return types;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.InputQueryType#getExpectedInputParameters()
     */
    @Override
    public Collection<String> getExpectedInputParameters()
    {
        return this.expectedInputParameters;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#getHandleAllNamespaces()
     */
    @Override
    public boolean getHandleAllNamespaces()
    {
        return this.handleAllNamespaces;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#getIncludeDefaults()
     */
    @Override
    public boolean getIncludeDefaults()
    {
        return this.includeDefaults;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#getInRobotsTxt()
     */
    @Override
    public boolean getInRobotsTxt()
    {
        return this.inRobotsTxt;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#getIsDummyQueryType()
     */
    @Override
    public boolean getIsDummyQueryType()
    {
        return this.isDummyQueryType;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#getIsNamespaceSpecific()
     */
    @Override
    public boolean getIsNamespaceSpecific()
    {
        return this.isNamespaceSpecific;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#getIsPageable()
     */
    @Override
    public boolean getIsPageable()
    {
        return this.isPageable;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#getKey()
     */
    @Override
    public URI getKey()
    {
        return this.key;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#getLinkedQueryTypes()
     */
    @Override
    public Set<URI> getLinkedQueryTypes()
    {
        return this.linkedQueryTypes;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#getNamespaceInputTags()
     */
    @Override
    public Set<String> getNamespaceInputTags()
    {
        if(this.getIsNamespaceSpecific())
        {
            return this.namespaceInputTags;
        }
        else
        {
            return Collections.emptySet();
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#getNamespaceMatchMethod()
     */
    @Override
    public NamespaceMatch getNamespaceMatchMethod()
    {
        return this.namespaceMatchMethod;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#getNamespacesToHandle()
     */
    @Override
    public Set<URI> getNamespacesToHandle()
    {
        return this.namespacesToHandle;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.OutputQueryType#getOutputString()
     */
    @Override
    public String getOutputString()
    {
        return this.outputString;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.ProcessorQueryType#getProcessingTemplateString()
     */
    @Override
    public String getProcessingTemplateString()
    {
        return this.processingTemplateString;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.ProfilableInterface#getProfileIncludeExcludeOrder()
     */
    @Override
    public URI getProfileIncludeExcludeOrder()
    {
        return this.profileIncludeExcludeOrder;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#getPublicIdentifierTags()
     */
    @Override
    public Set<String> getPublicIdentifierTags()
    {
        return this.publicIdentifierTags;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#getQueryUriTemplateString()
     */
    @Override
    public String getQueryUriTemplateString()
    {
        return this.queryUriTemplateString;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#getStandardUriTemplateString()
     */
    @Override
    public String getStandardUriTemplateString()
    {
        return this.standardUriTemplateString;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#getTitle()
     */
    @Override
    public String getTitle()
    {
        return this.title;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#getUnrecognisedStatements()
     */
    @Override
    public Collection<Statement> getUnrecognisedStatements()
    {
        return Collections.unmodifiableCollection(this.unrecognisedStatements);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#handlesNamespacesSpecifically(java.util.Map)
     */
    @Override
    public boolean handlesNamespacesSpecifically(final Map<String, Collection<URI>> namespacesToCheck)
    {
        if(namespacesToCheck == null)
        {
            throw new IllegalArgumentException("Namespaces must be specified for this method");
        }
        
        if(!this.getIsNamespaceSpecific() || this.getNamespacesToHandle() == null)
        {
            return false;
        }
        
        return NamespaceMatch.matchNamespaces(namespacesToCheck, this.getNamespacesToHandle(),
                this.getNamespaceMatchMethod());
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#handlesNamespaceUris(java.util.Map)
     */
    @Override
    public boolean handlesNamespaceUris(final Map<String, Collection<URI>> namespacesToCheck)
    {
        if(this.handleAllNamespaces && this.isNamespaceSpecific)
        {
            return true;
        }
        else
        {
            return this.handlesNamespacesSpecifically(namespacesToCheck);
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#isInputVariableNamespace(java.lang.String)
     */
    @Override
    public boolean isInputVariableNamespace(final String nextMatchTag)
    {
        return this.namespaceInputTags.contains(nextMatchTag);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#isInputVariablePublic(java.lang.String)
     */
    @Override
    public boolean isInputVariablePublic(final String inputVariableTag)
    {
        return this.publicIdentifierTags.contains(inputVariableTag);
    }
    
    /**
     * Note: Defaults to true, or else every profile operation on this object would fail
     */
    @Override
    public boolean isUsedWithProfileList(final List<Profile> orderedProfileList, final boolean allowImplicitInclusions,
            final boolean includeNonProfileMatched)
    {
        return ProfileMatch.isUsedWithProfileList(this, orderedProfileList, allowImplicitInclusions,
                includeNonProfileMatched);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.InputQueryType#matchesForQueryParameters(java.util.Map)
     */
    @Override
    public Map<String, List<String>> matchesForQueryParameters(final Map<String, String> queryParameters)
    {
        return Collections.emptyMap();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.InputQueryType#matchesQueryParameters(java.util.Map)
     */
    @Override
    public boolean matchesQueryParameters(final Map<String, String> queryString)
    {
        return false;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.InputQueryType#parseInputs(java.util.Map)
     */
    @Override
    public Map<String, Object> parseInputs(final Map<String, Object> inputParameterMap)
    {
        return inputParameterMap;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.ProcessorQueryType#parseProcessorQuery(java.lang.String)
     */
    @Override
    public Object parseProcessorQuery(final String query)
    {
        return query;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.ProcessorQueryType#processQueryVariables(java.util.Map)
     */
    @Override
    public Map<String, Object> processQueryVariables(final Map<String, Object> queryVariables)
    {
        return queryVariables;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.InputQueryType#resetExpectedInputParameters()
     */
    @Override
    public boolean resetExpectedInputParameters()
    {
        this.expectedInputParameters = new ArrayList<String>();
        
        return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#resetLinkedQueryTypes()
     */
    @Override
    public boolean resetLinkedQueryTypes()
    {
        this.linkedQueryTypes = new HashSet<URI>();
        
        return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#resetNamespaceInputTags()
     */
    @Override
    public boolean resetNamespaceInputTags()
    {
        this.namespaceInputTags = new HashSet<String>();
        
        return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#resetNamespacesToHandle()
     */
    @Override
    public boolean resetNamespacesToHandle()
    {
        this.namespacesToHandle = new HashSet<URI>();
        
        return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#resetPublicIdentifierTags()
     */
    @Override
    public boolean resetPublicIdentifierTags()
    {
        this.publicIdentifierTags = new HashSet<String>();
        
        return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#resetUnrecognisedStatements()
     */
    @Override
    public Collection<Statement> resetUnrecognisedStatements()
    {
        final Collection<Statement> result = this.unrecognisedStatements;
        
        this.unrecognisedStatements = new ArrayList<Statement>();
        
        return result;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#setCurationStatus(org.openrdf.model.URI)
     */
    @Override
    public void setCurationStatus(final URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#setDescription(java.lang.String)
     */
    @Override
    public void setDescription(final String description)
    {
        this.description = description;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#setHandleAllNamespaces(boolean)
     */
    @Override
    public void setHandleAllNamespaces(final boolean handleAllNamespaces)
    {
        this.handleAllNamespaces = handleAllNamespaces;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#setIncludeDefaults(boolean)
     */
    @Override
    public void setIncludeDefaults(final boolean includeDefaults)
    {
        this.includeDefaults = includeDefaults;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#setInRobotsTxt(boolean)
     */
    @Override
    public void setInRobotsTxt(final boolean inRobotsTxt)
    {
        this.inRobotsTxt = inRobotsTxt;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#setIsDummyQueryType(boolean)
     */
    @Override
    public void setIsDummyQueryType(final boolean isDummyQueryType)
    {
        this.isDummyQueryType = isDummyQueryType;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#setIsNamespaceSpecific(boolean)
     */
    @Override
    public void setIsNamespaceSpecific(final boolean isNamespaceSpecific)
    {
        this.isNamespaceSpecific = isNamespaceSpecific;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#setIsPageable(boolean)
     */
    @Override
    public void setIsPageable(final boolean isPageable)
    {
        this.isPageable = isPageable;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#setKey(java.lang.String)
     */
    @Override
    public void setKey(final String nextKey) throws IllegalArgumentException
    {
        this.key = Constants.valueFactory.createURI(nextKey);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#setKey(org.openrdf.model.URI)
     */
    @Override
    public void setKey(final URI nextKey)
    {
        this.key = nextKey;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#setNamespaceMatchMethod(NamespaceMatch)
     */
    @Override
    public void setNamespaceMatchMethod(final NamespaceMatch namespaceMatchMethod)
    {
        this.namespaceMatchMethod = namespaceMatchMethod;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.OutputQueryType#setOutputString(java.lang.String)
     */
    @Override
    public void setOutputString(final String outputString)
    {
        this.outputString = outputString;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.queryall.api.querytype.ProcessorQueryType#setProcessingTemplateString(java.lang.String)
     */
    @Override
    public void setProcessingTemplateString(final String templateString)
    {
        this.processingTemplateString = templateString;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.queryall.api.base.ProfilableInterface#setProfileIncludeExcludeOrder(org.openrdf.model
     * .URI)
     */
    @Override
    public void setProfileIncludeExcludeOrder(final URI profileIncludeExcludeOrder)
    {
        this.profileIncludeExcludeOrder = profileIncludeExcludeOrder;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#setQueryUriTemplateString(java.lang.String)
     */
    @Override
    public void setQueryUriTemplateString(final String queryUriTemplateString)
    {
        this.queryUriTemplateString = queryUriTemplateString;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.QueryType#setStandardUriTemplateString(java.lang.String)
     */
    @Override
    public void setStandardUriTemplateString(final String standardUriTemplateString)
    {
        this.standardUriTemplateString = standardUriTemplateString;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#setTitle(java.lang.String)
     */
    @Override
    public void setTitle(final String title)
    {
        this.title = title;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.querytype.ProcessorQueryType#substituteQueryVariables(java.util.Map)
     */
    @Override
    public String substituteQueryVariables(final Map<String, Object> processedQueryVariables)
    {
        return getProcessingTemplateString();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.BaseQueryAllInterface#toRdf(org.openrdf.repository.Repository,
     * int, org.openrdf.model.URI[])
     */
    @Override
    public boolean toRdf(final Repository myRepository, final int modelVersion, final URI... contextUris)
        throws OpenRDFException
    {
        // TODO Auto-generated method stub
        return false;
    }
    
}
