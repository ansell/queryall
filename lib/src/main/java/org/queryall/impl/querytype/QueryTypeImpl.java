package org.queryall.impl.querytype;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.base.HtmlExport;
import org.queryall.api.profile.Profile;
import org.queryall.api.profile.ProfileSchema;
import org.queryall.api.querytype.InputQueryType;
import org.queryall.api.querytype.InputQueryTypeSchema;
import org.queryall.api.querytype.OutputQueryType;
import org.queryall.api.querytype.ProcessorQueryType;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.querytype.QueryTypeSchema;
import org.queryall.api.querytype.RdfOutputQueryType;
import org.queryall.api.querytype.RdfOutputQueryTypeSchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.NamespaceMatch;
import org.queryall.api.utils.ProfileIncludeExclude;
import org.queryall.api.utils.ProfileMatch;
import org.queryall.api.utils.QueryAllNamespaces;
import org.queryall.impl.base.BaseQueryAllImpl;
import org.queryall.utils.RdfUtils;
import org.queryall.utils.StringUtils;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class QueryTypeImpl extends BaseQueryAllImpl implements QueryType, InputQueryType, ProcessorQueryType,
        RdfOutputQueryType, HtmlExport
{
    private boolean handleAllNamespaces = true;
    
    /**
     * If present, this is a list of namespaces which can be handled by this type of custom query
     * (or at least this form of it), unless handleAllNamespaces is true, in which case any
     * namespace can be present here without effect
     */
    private Set<URI> namespacesToHandle = new HashSet<URI>();
    
    /**
     * if a query is not namepsace specific it can be executed across providers which do not
     * necessarily handle this namespace, but are not necessarily defaults per se
     */
    private boolean isNamespaceSpecific = false;
    
    /**
     * these are the input_NN indexes that are either namespaces, or correspond to public
     * identifiers that are not untouchable internal private identifiers among other things, it can
     * be used to make sure that these are lowercased per a given policy (in this case the Banff
     * Manifesto)
     */
    private Set<String> publicIdentifierTags = new HashSet<String>(2);
    
    /**
     * these are the input_NN indexes that we will use to determine which namespace providers to
     * perform this query using
     */
    private Set<String> namespaceInputTags = new HashSet<String>(2);
    
    /**
     * This is the method by which we determine whether any or all of the namespaces are required on
     * a particular endpoint before we utilise it if defaults are included in this query then we
     * will always use default providers regardless of the namespaces they have declared on them if
     * we do not use all namespaces then this setting will still be in effect, but it will first
     * match against the list that we do handle before getting to the provider choice stage For
     * example, if we match inputs 1 and 2 as namespaceInputIndexes, and we have the the
     * namespaceMatchMethod set to QueryType.queryNamespaceMatchAll.stringValue(), and we do not
     * handle all namespaces and inputs 1 and 2 both exist in namespacesToHandle then we will
     * satisfy the initial test for query usability
     * 
     * <br />
     * 
     * Possible values are defined in NamespaceMatch
     **/
    private NamespaceMatch namespaceMatchMethod = NamespaceMatch.ANY_MATCHED;
    
    /**
     * if we are told we can include defaults, even if we are known to be namespace specific we can
     * utilise the default providers as sources
     */
    private boolean includeDefaults = true;
    
    /**
     * if this query can be paged using the pageoffsetNN mechanism, this should be true, and
     * otherwise it should be false
     */
    private boolean isPageable = false;
    
    /**
     * If this query is restricted by any of the robots.txt entries than declare that here, so that
     * automatic bot detection is functional for this query
     */
    private boolean inRobotsTxt = false;
    
    /**
     * use this to define which additional custom query rdf triples to add to a particular type of
     * custom query a typical use for this is for adding links and index triples to
     * construct,index,links etc type queries, but not to others for instance
     */
    private Set<URI> semanticallyLinkedCustomQueries = new HashSet<URI>();
    
    private ProfileIncludeExclude profileIncludeExcludeOrder = ProfileIncludeExclude.UNDEFINED;
    
    private String templateString = "";
    
    private String queryUriTemplateString = "";
    
    private String standardUriTemplateString = "";
    
    private String outputRdfString = "";
    
    private boolean isDummyQueryType = false;
    
    /**
     * Default to universally available RDF/XML, and for backwards compatibility with previous
     * versions (<5) that only supported RDF/XML output
     */
    private String outputRdfFormat = Constants.APPLICATION_RDF_XML;
    private Set<String> expectedInputParameters = new HashSet<String>();
    
    protected QueryTypeImpl()
    {
        super();
    }
    
    protected QueryTypeImpl(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        super(inputStatements, keyToUse, modelVersion);
        
        final Collection<Statement> currentUnrecognisedStatements = this.resetUnrecognisedStatements();
        
        for(final Statement nextStatement : currentUnrecognisedStatements)
        {
            if(this.log.isTraceEnabled())
            {
                this.log.trace("QueryType: nextStatement: " + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(QueryTypeSchema.getQueryTypeUri()))
            {
                if(this.log.isTraceEnabled())
                {
                    this.log.trace("QueryType: found valid type predicate for URI: " + keyToUse);
                }
                
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(QueryTypeSchema.getQueryHandleAllNamespaces()))
            {
                this.setHandleAllNamespaces(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(QueryTypeSchema.getQueryNamespaceToHandle()))
            {
                this.addNamespaceToHandle((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(QueryTypeSchema.getQueryPublicIdentifierIndex()))
            {
                // BACKWARDS COMPATIBILITY
                this.addPublicIdentifierTag("input_"
                        + Integer.toString(RdfUtils.getIntegerFromValue(nextStatement.getObject())));
            }
            else if(nextStatement.getPredicate().equals(QueryTypeSchema.getQueryPublicIdentifierTag()))
            {
                this.addPublicIdentifierTag(RdfUtils.getUTF8StringValueFromSesameValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(QueryTypeSchema.getQueryNamespaceInputIndex()))
            {
                // BACKWARDS COMPATIBILITY
                this.addNamespaceInputTag("input_"
                        + Integer.toString(RdfUtils.getIntegerFromValue(nextStatement.getObject())));
            }
            else if(nextStatement.getPredicate().equals(QueryTypeSchema.getQueryNamespaceInputTag()))
            {
                this.addNamespaceInputTag(RdfUtils.getUTF8StringValueFromSesameValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(QueryTypeSchema.getQueryNamespaceMatchMethod()))
            {
                final NamespaceMatch match = NamespaceMatch.valueOf((URI)nextStatement.getObject());
                
                if(match != null)
                {
                    this.setNamespaceMatchMethod(match);
                }
                else
                {
                    this.log.error("Found an unrecognised NamespaceMatch method value="
                            + nextStatement.getObject().stringValue());
                }
            }
            else if(nextStatement.getPredicate().equals(QueryTypeSchema.getQueryNamespaceSpecific()))
            {
                this.setIsNamespaceSpecific(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(QueryTypeSchema.getQueryIncludeDefaults()))
            {
                this.setIncludeDefaults(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(QueryTypeSchema.getQueryIncludeQueryType()))
            {
                this.addLinkedQueryType((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(QueryTypeSchema.getQueryTemplateString()))
            {
                this.setProcessingTemplateString(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(QueryTypeSchema.getQueryQueryUriTemplateString()))
            {
                this.setQueryUriTemplateString(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(QueryTypeSchema.getQueryStandardUriTemplateString()))
            {
                this.setStandardUriTemplateString(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(QueryTypeSchema.getQueryInRobotsTxt()))
            {
                this.setInRobotsTxt(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(QueryTypeSchema.getQueryIsPageable()))
            {
                this.setIsPageable(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(QueryTypeSchema.getQueryIsDummyQueryType()))
            {
                this.setIsDummyQueryType(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(InputQueryTypeSchema.getQueryExpectedInputParameters()))
            {
                this.addExpectedInputParameter(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(RdfOutputQueryTypeSchema.getQueryOutputRdfString())
                    || nextStatement.getPredicate().equals(RdfOutputQueryTypeSchema.getOLDQueryOutputRdfXmlString()))
            {
                this.setOutputString(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(RdfOutputQueryTypeSchema.getQueryOutputRdfFormat()))
            {
                this.setOutputRdfFormat(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(ProfileSchema.getProfileIncludeExcludeOrderUri()))
            {
                this.setProfileIncludeExcludeOrder(ProfileIncludeExclude.valueOf((URI)nextStatement.getObject()));
            }
            else
            {
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
        // this.setSemanticallyLinkedQueryTypes(tempsemanticallyLinkedCustomQueries);
        
        if(this.log.isTraceEnabled())
        {
            this.log.trace("QueryType.fromRdf: would have returned... keyToUse=" + keyToUse + " result="
                    + this.toString());
        }
    }
    
    @Override
    public void addExpectedInputParameter(final String expectedInputParameter)
    {
        this.expectedInputParameters.add(expectedInputParameter);
    }
    
    @Override
    public void addLinkedQueryType(final URI semanticallyLinkedCustomQuery)
    {
        this.semanticallyLinkedCustomQueries.add(semanticallyLinkedCustomQuery);
    }
    
    @Override
    public void addNamespaceInputTag(final String namespaceInputTag)
    {
        this.namespaceInputTags.add(namespaceInputTag);
    }
    
    @Override
    public void addNamespaceToHandle(final URI namespaceToHandle)
    {
        this.namespacesToHandle.add(namespaceToHandle);
    }
    
    @Override
    public void addPublicIdentifierTag(final String publicIdentifierTag)
    {
        this.publicIdentifierTags.add(publicIdentifierTag);
    }
    
    @Override
    public int compareTo(final QueryType otherQueryType)
    {
        @SuppressWarnings("unused")
        final int BEFORE = -1;
        final int EQUAL = 0;
        @SuppressWarnings("unused")
        final int AFTER = 1;
        
        if(this == otherQueryType)
        {
            return EQUAL;
        }
        
        return this.getKey().stringValue().compareTo(otherQueryType.getKey().stringValue());
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj)
    {
        if(this == obj)
        {
            return true;
        }
        if(!super.equals(obj))
        {
            return false;
        }
        if(!(obj instanceof QueryType))
        {
            return false;
        }
        final QueryType other = (QueryType)obj;
        if(other instanceof InputQueryType)
        {
            final InputQueryType otherInput = (InputQueryType)other;
            
            if(this.getExpectedInputParameters() == null)
            {
                if(otherInput.getExpectedInputParameters() != null)
                {
                    return false;
                }
            }
            else if(!this.getExpectedInputParameters().equals(otherInput.getExpectedInputParameters()))
            {
                return false;
            }
        }
        else
        {
            return false;
        }
        
        if(this.getHandleAllNamespaces() != other.getHandleAllNamespaces())
        {
            return false;
        }
        if(this.getInRobotsTxt() != other.getInRobotsTxt())
        {
            return false;
        }
        if(this.getIncludeDefaults() != other.getIncludeDefaults())
        {
            return false;
        }
        if(this.getIsDummyQueryType() != other.getIsDummyQueryType())
        {
            return false;
        }
        if(this.getIsNamespaceSpecific() != other.getIsNamespaceSpecific())
        {
            return false;
        }
        if(this.getIsPageable() != other.getIsPageable())
        {
            return false;
        }
        if(this.getNamespaceInputTags() == null)
        {
            if(other.getNamespaceInputTags() != null)
            {
                return false;
            }
        }
        else if(!this.getNamespaceInputTags().equals(other.getNamespaceInputTags()))
        {
            return false;
        }
        if(this.getNamespaceMatchMethod() == null)
        {
            if(other.getNamespaceMatchMethod() != null)
            {
                return false;
            }
        }
        else if(!this.getNamespaceMatchMethod().equals(other.getNamespaceMatchMethod()))
        {
            return false;
        }
        if(this.getNamespacesToHandle() == null)
        {
            if(other.getNamespacesToHandle() != null)
            {
                return false;
            }
        }
        else if(!this.getNamespacesToHandle().equals(other.getNamespacesToHandle()))
        {
            return false;
        }
        if(other instanceof OutputQueryType)
        {
            final OutputQueryType otherOutput = (OutputQueryType)other;
            
            if(this.getOutputString() == null)
            {
                if(otherOutput.getOutputString() != null)
                {
                    return false;
                }
            }
            else if(!this.getOutputString().equals(otherOutput.getOutputString()))
            {
                return false;
            }
            
            if(otherOutput instanceof RdfOutputQueryType)
            {
                final RdfOutputQueryType otherRdfOutput = (RdfOutputQueryType)otherOutput;
                if(this.getOutputRdfFormat() == null)
                {
                    if(otherRdfOutput.getOutputRdfFormat() != null)
                    {
                        return false;
                    }
                }
                else if(!this.getOutputRdfFormat().equals(otherRdfOutput.getOutputRdfFormat()))
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
        
        if(this.getProfileIncludeExcludeOrder() == null)
        {
            if(other.getProfileIncludeExcludeOrder() != null)
            {
                return false;
            }
        }
        else if(!this.getProfileIncludeExcludeOrder().equals(other.getProfileIncludeExcludeOrder()))
        {
            return false;
        }
        
        if(this.getPublicIdentifierTags() == null)
        {
            if(other.getPublicIdentifierTags() != null)
            {
                return false;
            }
        }
        else if(!this.getPublicIdentifierTags().equals(other.getPublicIdentifierTags()))
        {
            return false;
        }
        
        if(this.getQueryUriTemplateString() == null)
        {
            if(other.getQueryUriTemplateString() != null)
            {
                return false;
            }
        }
        else if(!this.getQueryUriTemplateString().equals(other.getQueryUriTemplateString()))
        {
            return false;
        }
        
        if(this.getLinkedQueryTypes() == null)
        {
            if(other.getLinkedQueryTypes() != null)
            {
                return false;
            }
        }
        else if(!this.getLinkedQueryTypes().equals(other.getLinkedQueryTypes()))
        {
            return false;
        }
        if(this.getStandardUriTemplateString() == null)
        {
            if(other.getStandardUriTemplateString() != null)
            {
                return false;
            }
        }
        else if(!this.getStandardUriTemplateString().equals(other.getStandardUriTemplateString()))
        {
            return false;
        }
        if(other instanceof ProcessorQueryType)
        {
            final ProcessorQueryType otherProcessor = (ProcessorQueryType)other;
            
            if(this.getProcessingTemplateString() == null)
            {
                if(otherProcessor.getProcessingTemplateString() != null)
                {
                    return false;
                }
            }
            else if(!this.getProcessingTemplateString().equals(otherProcessor.getProcessingTemplateString()))
            {
                return false;
            }
        }
        else
        {
            return false;
        }
        return true;
    }
    
    /**
     * @return the namespace used to represent objects of this type by default
     */
    @Override
    public QueryAllNamespaces getDefaultNamespace()
    {
        return QueryAllNamespaces.QUERY;
    }
    
    @Override
    public Collection<String> getExpectedInputParameters()
    {
        return Collections.unmodifiableCollection(this.expectedInputParameters);
    }
    
    @Override
    public boolean getHandleAllNamespaces()
    {
        return this.handleAllNamespaces;
    }
    
    @Override
    public boolean getIncludeDefaults()
    {
        return this.includeDefaults;
    }
    
    @Override
    public boolean getInRobotsTxt()
    {
        return this.inRobotsTxt;
    }
    
    @Override
    public boolean getIsDummyQueryType()
    {
        return this.isDummyQueryType;
    }
    
    @Override
    public boolean getIsNamespaceSpecific()
    {
        return this.isNamespaceSpecific;
    }
    
    @Override
    public boolean getIsPageable()
    {
        return this.isPageable;
    }
    
    @Override
    public Set<URI> getLinkedQueryTypes()
    {
        return this.semanticallyLinkedCustomQueries;
    }
    
    @Override
    public Set<String> getNamespaceInputTags()
    {
        if(!this.getIsNamespaceSpecific())
        {
            return Collections.emptySet();
        }
        else
        {
            return this.namespaceInputTags;
        }
    }
    
    @Override
    public NamespaceMatch getNamespaceMatchMethod()
    {
        return this.namespaceMatchMethod;
    }
    
    @Override
    public Set<URI> getNamespacesToHandle()
    {
        if(!this.getIsNamespaceSpecific())
        {
            return Collections.emptySet();
        }
        else
        {
            return this.namespacesToHandle;
        }
    }
    
    @Override
    public String getOutputRdfFormat()
    {
        return this.outputRdfFormat;
    }
    
    @Override
    public String getOutputString()
    {
        return this.outputRdfString;
    }
    
    @Override
    public String getProcessingTemplateString()
    {
        return this.templateString;
    }
    
    @Override
    public ProfileIncludeExclude getProfileIncludeExcludeOrder()
    {
        return this.profileIncludeExcludeOrder;
    }
    
    @Override
    public Set<String> getPublicIdentifierTags()
    {
        return this.publicIdentifierTags;
    }
    
    @Override
    public String getQueryUriTemplateString()
    {
        return this.queryUriTemplateString;
    }
    
    @Override
    public String getStandardUriTemplateString()
    {
        return this.standardUriTemplateString;
    }
    
    @Override
    public boolean handlesNamespacesSpecifically(final Map<String, Collection<URI>> namespacesToCheck)
    {
        if(namespacesToCheck == null)
        {
            throw new IllegalArgumentException("Namespaces must be specified for this method");
        }
        
        if(!this.isNamespaceSpecific || this.namespacesToHandle == null)
        {
            return false;
        }
        
        return NamespaceMatch.matchNamespaces(namespacesToCheck, this.namespacesToHandle, this.namespaceMatchMethod);
    }
    
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
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result =
                prime * result + ((this.expectedInputParameters == null) ? 0 : this.expectedInputParameters.hashCode());
        result = prime * result + (this.handleAllNamespaces ? 1231 : 1237);
        result = prime * result + (this.inRobotsTxt ? 1231 : 1237);
        result = prime * result + (this.includeDefaults ? 1231 : 1237);
        result = prime * result + (this.isDummyQueryType ? 1231 : 1237);
        result = prime * result + (this.isNamespaceSpecific ? 1231 : 1237);
        result = prime * result + (this.isPageable ? 1231 : 1237);
        result = prime * result + ((this.namespaceInputTags == null) ? 0 : this.namespaceInputTags.hashCode());
        result = prime * result + ((this.namespaceMatchMethod == null) ? 0 : this.namespaceMatchMethod.hashCode());
        result = prime * result + ((this.namespacesToHandle == null) ? 0 : this.namespacesToHandle.hashCode());
        result = prime * result + ((this.outputRdfFormat == null) ? 0 : this.outputRdfFormat.hashCode());
        result = prime * result + ((this.outputRdfString == null) ? 0 : this.outputRdfString.hashCode());
        result =
                prime * result
                        + ((this.profileIncludeExcludeOrder == null) ? 0 : this.profileIncludeExcludeOrder.hashCode());
        result = prime * result + ((this.publicIdentifierTags == null) ? 0 : this.publicIdentifierTags.hashCode());
        result = prime * result + ((this.queryUriTemplateString == null) ? 0 : this.queryUriTemplateString.hashCode());
        result =
                prime
                        * result
                        + ((this.semanticallyLinkedCustomQueries == null) ? 0 : this.semanticallyLinkedCustomQueries
                                .hashCode());
        result =
                prime * result
                        + ((this.standardUriTemplateString == null) ? 0 : this.standardUriTemplateString.hashCode());
        result = prime * result + ((this.templateString == null) ? 0 : this.templateString.hashCode());
        return result;
    }
    
    // returns true if the input variable is in the list of public input variables
    @Override
    public boolean isInputVariableNamespace(final String inputVariable)
    {
        if(inputVariable == null)
        {
            throw new IllegalArgumentException("Cannot have null input variables");
        }
        
        if(!this.isNamespaceSpecific)
        {
            return false;
        }
        
        if(this.namespaceInputTags != null)
        {
            if(this.namespaceInputTags.contains(inputVariable))
            {
                return true;
            }
        }
        
        // if there are no defined public indexes we default to false
        // also default to false if we didn't find the index in the list
        return false;
    }
    
    // returns true if the input variable is in the list of public input variables
    @Override
    public boolean isInputVariablePublic(final String inputVariable)
    {
        if(inputVariable == null)
        {
            throw new IllegalArgumentException("Cannot have null input variables");
        }
        
        if(this.publicIdentifierTags != null)
        {
            if(this.getPublicIdentifierTags().contains(inputVariable))
            {
                return true;
            }
        }
        
        // if there are no defined public indexes we default to false
        // also default to false if we didn't find the index in the list
        return false;
    }
    
    @Override
    public boolean isUsedWithProfileList(final List<Profile> orderedProfileList, final boolean allowImplicitInclusions,
            final boolean includeNonProfileMatched)
    {
        return ProfileMatch.isUsedWithProfileList(this, orderedProfileList, allowImplicitInclusions,
                includeNonProfileMatched);
    }
    
    @Override
    public boolean resetExpectedInputParameters()
    {
        try
        {
            this.expectedInputParameters.clear();
            
            return true;
        }
        catch(final UnsupportedOperationException uoe)
        {
            this.log.debug("Could not clear collection");
        }
        
        this.expectedInputParameters = new HashSet<String>();
        
        return true;
    }
    
    @Override
    public boolean resetLinkedQueryTypes()
    {
        try
        {
            this.semanticallyLinkedCustomQueries.clear();
            
            return true;
        }
        catch(final UnsupportedOperationException uoe)
        {
            this.log.debug("Could not clear collection");
        }
        
        this.semanticallyLinkedCustomQueries = new HashSet<URI>();
        
        return true;
    }
    
    @Override
    public boolean resetNamespaceInputTags()
    {
        try
        {
            this.namespaceInputTags.clear();
            
            return true;
        }
        catch(final UnsupportedOperationException uoe)
        {
            this.log.debug("Could not clear collection");
        }
        
        this.namespaceInputTags = new HashSet<String>();
        
        return true;
    }
    
    @Override
    public boolean resetNamespacesToHandle()
    {
        try
        {
            this.namespacesToHandle.clear();
            
            return true;
        }
        catch(final UnsupportedOperationException uoe)
        {
            this.log.debug("Could not clear collection");
        }
        
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
        try
        {
            this.publicIdentifierTags.clear();
            
            return true;
        }
        catch(final UnsupportedOperationException uoe)
        {
            this.log.debug("Could not clear collection");
        }
        
        this.publicIdentifierTags = new HashSet<String>();
        
        return true;
    }
    
    @Override
    public void setHandleAllNamespaces(final boolean handleAllNamespaces)
    {
        this.handleAllNamespaces = handleAllNamespaces;
    }
    
    @Override
    public void setIncludeDefaults(final boolean includeDefaults)
    {
        this.includeDefaults = includeDefaults;
    }
    
    @Override
    public void setInRobotsTxt(final boolean inRobotsTxt)
    {
        this.inRobotsTxt = inRobotsTxt;
    }
    
    @Override
    public void setIsDummyQueryType(final boolean isDummyQueryType)
    {
        this.isDummyQueryType = isDummyQueryType;
    }
    
    @Override
    public void setIsNamespaceSpecific(final boolean isNamespaceSpecific)
    {
        this.isNamespaceSpecific = isNamespaceSpecific;
    }
    
    @Override
    public void setIsPageable(final boolean isPageable)
    {
        this.isPageable = isPageable;
    }
    
    @Override
    public void setNamespaceMatchMethod(final NamespaceMatch namespaceMatchMethod)
    {
        this.namespaceMatchMethod = namespaceMatchMethod;
    }
    
    @Override
    public void setOutputRdfFormat(final String rdfFormat)
    {
        this.outputRdfFormat = rdfFormat;
    }
    
    @Override
    public void setOutputString(final String outputRdfXmlString)
    {
        this.outputRdfString = outputRdfXmlString;
    }
    
    @Override
    public void setProcessingTemplateString(final String templateString)
    {
        this.templateString = templateString;
    }
    
    @Override
    public void setProfileIncludeExcludeOrder(final ProfileIncludeExclude profileIncludeExcludeOrder)
    {
        this.profileIncludeExcludeOrder = profileIncludeExcludeOrder;
    }
    
    @Override
    public void setQueryUriTemplateString(final String queryUriTemplateString)
    {
        this.queryUriTemplateString = queryUriTemplateString;
    }
    
    @Override
    public void setStandardUriTemplateString(final String standardUriTemplateString)
    {
        this.standardUriTemplateString = standardUriTemplateString;
    }
    
    @Override
    public String toHtml()
    {
        final StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        final String prefix = "query_";
        
        sb.append("<span>key:</span>" + StringUtils.xmlEncodeString(this.getKey().stringValue()));
        
        sb.append(StringUtils.xmlEncodeString(this.toString()));
        
        return sb.toString();
    }
    
    @Override
    public String toHtmlFormBody()
    {
        final StringBuilder sb = new StringBuilder();
        
        final String prefix = "query_";
        
        if(this.getKey() != null)
        {
            sb.append("<div class=\"" + prefix + "key_div\"><input type=\"hidden\" name=\"key\" value=\""
                    + StringUtils.xmlEncodeString(this.getKey().stringValue()) + "\" /></div>\n");
        }
        
        sb.append("<div class=\"" + prefix + "title_div\"><span class=\"" + prefix
                + "title_span\">Title:</span><input type=\"text\" name=\"" + prefix + "title\" value=\""
                + StringUtils.xmlEncodeString(this.getTitle()) + "\" /></div>\n");
        
        sb.append("<div class=\"" + prefix + "templateString_div\"><span class=\"" + prefix
                + "templateString_span\">Query Template:</span><input type=\"text\" name=\"" + prefix
                + "templateString\" value=\"" + StringUtils.xmlEncodeString(this.templateString) + "\" /></div>\n");
        sb.append("<div class=\"" + prefix + "standardUriTemplateString_div\"><span class=\"" + prefix
                + "standardUriTemplateString_span\">Standard URI Template:</span><input type=\"text\" name=\"" + prefix
                + "standardUriTemplateString\" value=\"" + StringUtils.xmlEncodeString(this.standardUriTemplateString)
                + "\" /></div>\n");
        sb.append("<div class=\"" + prefix + "queryUriTemplateString_div\"><span class=\"" + prefix
                + "queryUriTemplateString_span\">Query URI Template:</span><input type=\"text\" name=\"" + prefix
                + "queryUriTemplateString\" value=\"" + StringUtils.xmlEncodeString(this.queryUriTemplateString)
                + "\" /></div>\n");
        sb.append("<div class=\"" + prefix + "outputRdfXmlString_div\"><span class=\"" + prefix
                + "outputRdfXmlString_span\">Static output RDF/XML Template:</span><input type=\"text\" name=\""
                + prefix + "outputRdfString\" value=\"" + StringUtils.xmlEncodeString(this.outputRdfString)
                + "\" /></div>\n");
        
        sb.append("<div class=\"" + prefix + "isNamespaceSpecific_div\"><span class=\"" + prefix
                + "isNamespaceSpecific_span\">Is Namespace Specific:</span><input type=\"checkbox\" name=\"" + prefix
                + "isNamespaceSpecific\" value=\"isNamespaceSpecific\" ");
        
        if(this.isNamespaceSpecific)
        {
            sb.append(" checked=\"checked\" ");
        }
        
        sb.append(" /></div>\n");
        
        sb.append("<div class=\"" + prefix + "handleAllNamespaces_div\"><span class=\"" + prefix
                + "handleAllNamespaces_span\">Handle All Namespaces:</span><input type=\"checkbox\" name=\"" + prefix
                + "handleAllNamespaces\" value=\"handleAllNamespaces\" ");
        
        if(this.handleAllNamespaces)
        {
            sb.append(" checked=\"checked\" ");
        }
        
        sb.append(" /></div>\n");
        
        sb.append("<div class=\"" + prefix + "namespaceMatchMethod_div\"><span class=\"" + prefix
                + "namespaceMatchMethod_span\">All namespaces must match?:</span><input type=\"checkbox\" name=\""
                + prefix + "namespaceMatchMethod\" value=\"namespaceMatchMethod\" ");
        
        if(this.namespaceMatchMethod.equals(NamespaceMatch.ALL_MATCHED))
        {
            sb.append(" checked=\"checked\" ");
        }
        
        sb.append(" /></div>\n");
        
        sb.append("<div class=\""
                + prefix
                + "profileIncludeExcludeOrder_div\"><span class=\""
                + prefix
                + "profileIncludeExcludeOrder_span\">Use by default on all profiles?:</span><input type=\"checkbox\" name=\""
                + prefix + "profileIncludeExcludeOrder\" value=\"profileIncludeExcludeOrder\" ");
        
        if(this.profileIncludeExcludeOrder.equals(ProfileSchema.getProfileExcludeThenIncludeUri()))
        {
            sb.append(" checked=\"checked\" ");
        }
        
        sb.append(" /></div>\n");
        
        sb.append("<div class=\""
                + prefix
                + "includeDefaults_div\"><span class=\""
                + prefix
                + "includeDefaults_span\">Include Default Providers that support this query but possibly not the defined namespaces?:</span><input type=\"checkbox\" name=\""
                + prefix + "includeDefaults\" value=\"includeDefaults\" ");
        
        if(this.includeDefaults)
        {
            sb.append(" checked=\"checked\" ");
        }
        
        sb.append(" /></div>\n");
        
        sb.append("<div class=\"" + prefix + "inRobotsTxt_div\"><span class=\"" + prefix
                + "inRobotsTxt_span\">Is this query restricted by Robots.txt?:</span><input type=\"checkbox\" name=\""
                + prefix + "inRobotsTxt\" value=\"inRobotsTxt\" ");
        
        if(this.inRobotsTxt)
        {
            sb.append(" checked=\"checked\" ");
        }
        
        sb.append(" /></div>\n");
        
        return sb.toString();
    }
    
    @Override
    public boolean toRdf(final Repository myRepository, final int modelVersion, final URI... contextKey)
        throws OpenRDFException
    {
        super.toRdf(myRepository, modelVersion, contextKey);
        
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        try
        {
            // create some resources and literals to make statements out of
            final URI queryInstanceUri = this.getKey();
            
            final Literal handleAllNamespacesLiteral = f.createLiteral(this.handleAllNamespaces);
            final Literal isNamespaceSpecificLiteral = f.createLiteral(this.isNamespaceSpecific);
            final URI namespaceMatchMethodLiteral = this.namespaceMatchMethod.getNamespaceMatchUri();
            final Literal includeDefaultsLiteral = f.createLiteral(this.includeDefaults);
            final Literal templateStringLiteral = f.createLiteral(this.templateString);
            final Literal queryUriTemplateStringLiteral = f.createLiteral(this.queryUriTemplateString);
            final Literal standardUriTemplateStringLiteral = f.createLiteral(this.standardUriTemplateString);
            
            final Literal inRobotsTxtLiteral = f.createLiteral(this.inRobotsTxt);
            final Literal isPageableLiteral = f.createLiteral(this.isPageable);
            final Literal isDummyQueryTypeLiteral = f.createLiteral(this.isDummyQueryType);
            final URI profileIncludeExcludeOrderLiteral = this.profileIncludeExcludeOrder.getUri();
            
            final Literal outputRdfStringLiteral = f.createLiteral(this.outputRdfString);
            final Literal outputRdfFormatLiteral = f.createLiteral(this.outputRdfFormat);
            
            // log.info("after literals created");
            
            con.begin();
            
            for(final URI nextElementType : this.getElementTypes())
            {
                con.add(queryInstanceUri, RDF.TYPE, nextElementType, contextKey);
            }
            
            con.add(queryInstanceUri, QueryTypeSchema.getQueryHandleAllNamespaces(), handleAllNamespacesLiteral,
                    contextKey);
            con.add(queryInstanceUri, QueryTypeSchema.getQueryNamespaceSpecific(), isNamespaceSpecificLiteral,
                    contextKey);
            con.add(queryInstanceUri, QueryTypeSchema.getQueryNamespaceMatchMethod(), namespaceMatchMethodLiteral,
                    contextKey);
            con.add(queryInstanceUri, QueryTypeSchema.getQueryIncludeDefaults(), includeDefaultsLiteral, contextKey);
            con.add(queryInstanceUri, QueryTypeSchema.getQueryTemplateString(), templateStringLiteral, contextKey);
            con.add(queryInstanceUri, QueryTypeSchema.getQueryQueryUriTemplateString(), queryUriTemplateStringLiteral,
                    contextKey);
            con.add(queryInstanceUri, QueryTypeSchema.getQueryStandardUriTemplateString(),
                    standardUriTemplateStringLiteral, contextKey);
            con.add(queryInstanceUri, QueryTypeSchema.getQueryInRobotsTxt(), inRobotsTxtLiteral, contextKey);
            con.add(queryInstanceUri, QueryTypeSchema.getQueryIsPageable(), isPageableLiteral, contextKey);
            con.add(queryInstanceUri, QueryTypeSchema.getQueryIsDummyQueryType(), isDummyQueryTypeLiteral, contextKey);
            
            con.add(queryInstanceUri, ProfileSchema.getProfileIncludeExcludeOrderUri(),
                    profileIncludeExcludeOrderLiteral, contextKey);
            
            if(modelVersion < 5)
            {
                if(this.getOutputRdfFormat().equals(Constants.APPLICATION_RDF_XML))
                {
                    con.add(queryInstanceUri, RdfOutputQueryTypeSchema.getOLDQueryOutputRdfXmlString(),
                            outputRdfStringLiteral, contextKey);
                }
                else
                {
                    this.log.info("Unable to supply RDF Output string to this user as they requested an old version of the api, and the template was not in application/rdf+xml format");
                }
            }
            else
            {
                con.add(queryInstanceUri, RdfOutputQueryTypeSchema.getQueryOutputRdfString(), outputRdfStringLiteral,
                        contextKey);
                con.add(queryInstanceUri, RdfOutputQueryTypeSchema.getQueryOutputRdfFormat(), outputRdfFormatLiteral,
                        contextKey);
                
            }
            
            // log.info("after single URIs created");
            
            if(this.expectedInputParameters != null)
            {
                
                for(final String nextExpectedInputParameter : this.expectedInputParameters)
                {
                    if(nextExpectedInputParameter != null)
                    {
                        con.add(queryInstanceUri, InputQueryTypeSchema.getQueryExpectedInputParameters(),
                                f.createLiteral(nextExpectedInputParameter), contextKey);
                    }
                }
            }
            
            if(this.namespacesToHandle != null)
            {
                
                for(final URI nextNamespaceToHandle : this.namespacesToHandle)
                {
                    if(nextNamespaceToHandle != null)
                    {
                        con.add(queryInstanceUri, QueryTypeSchema.getQueryNamespaceToHandle(), nextNamespaceToHandle,
                                contextKey);
                    }
                }
            }
            
            if(this.publicIdentifierTags != null)
            {
                if(modelVersion < 5)
                {
                    for(final String nextPublicIdentifierTag : this.publicIdentifierTags)
                    {
                        if(nextPublicIdentifierTag.startsWith("input_"))
                        {
                            try
                            {
                                con.add(queryInstanceUri, QueryTypeSchema.getQueryPublicIdentifierTag(), f
                                        .createLiteral(Integer.parseInt(nextPublicIdentifierTag.substring("input_"
                                                .length()))), contextKey);
                            }
                            catch(final NumberFormatException nfe)
                            {
                                this.log.info("Could not convert input_NN tag backwards to previous index version due to an issue with the tag nextPublicIdentifierTag="
                                        + nextPublicIdentifierTag
                                        + " querytype.getKey()="
                                        + this.getKey().stringValue());
                            }
                        }
                    }
                }
                else
                {
                    for(final String nextPublicIdentifierTag : this.publicIdentifierTags)
                    {
                        con.add(queryInstanceUri, QueryTypeSchema.getQueryPublicIdentifierTag(),
                                f.createLiteral(nextPublicIdentifierTag), contextKey);
                    }
                }
            }
            
            if(this.namespaceInputTags != null)
            {
                if(modelVersion < 5)
                {
                    for(final String nextNamespaceInputTag : this.namespaceInputTags)
                    {
                        if(nextNamespaceInputTag.startsWith("input_"))
                        {
                            try
                            {
                                con.add(queryInstanceUri, QueryTypeSchema.getQueryNamespaceInputTag(), f
                                        .createLiteral(Integer.parseInt(nextNamespaceInputTag.substring("input_"
                                                .length()))), contextKey);
                            }
                            catch(final NumberFormatException nfe)
                            {
                                this.log.info("Could not convert input_NN tag backwards to previous index version due to an issue with the tag nextNamespaceInputTag="
                                        + nextNamespaceInputTag + " querytype.getKey()=" + this.getKey().stringValue());
                            }
                        }
                    }
                }
                else
                {
                    for(final String nextNamespaceInputTag : this.namespaceInputTags)
                    {
                        con.add(queryInstanceUri, QueryTypeSchema.getQueryNamespaceInputTag(),
                                f.createLiteral(nextNamespaceInputTag), contextKey);
                    }
                }
            }
            
            if(this.semanticallyLinkedCustomQueries != null)
            {
                for(final URI nextSemanticallyLinkedQueryType : this.semanticallyLinkedCustomQueries)
                {
                    if(nextSemanticallyLinkedQueryType != null)
                    {
                        con.add(queryInstanceUri, QueryTypeSchema.getQueryIncludeQueryType(),
                                nextSemanticallyLinkedQueryType, contextKey);
                    }
                }
            }
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(final RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            
            if(con != null)
            {
                con.rollback();
            }
            
            this.log.error("RepositoryException: " + re.getMessage());
        }
        finally
        {
            if(con != null)
            {
                con.close();
            }
        }
        
        return false;
    }
    
    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        
        sb.append("key=" + this.getKey() + "\n");
        sb.append("title=" + this.getTitle() + "\n");
        
        // sb.append("templateString=" + templateString + "\n");
        // sb.append("standardUriTemplateString=" + standardUriTemplateString + "\n");
        // sb.append("queryUriTemplateString=" + queryUriTemplateString + "\n");
        // sb.append("outputRdfString=" + outputRdfString + "\n");
        // sb.append("inputRegex=" + this.inputRegex + "\n");
        
        // if(semanticallyLinkedCustomQueries == null)
        // {
        // sb.append("semanticallyLinkedCustomQueries=null\n");
        // }
        // else
        // {
        // sb.append("semanticallyLinkedCustomQueries=");
        //
        // RdfUtils.joinStringCollectionHelper(semanticallyLinkedCustomQueries, ", ", sb);
        //
        // sb.append("\n");
        // }
        
        sb.append("handleAllNamespaces=" + this.handleAllNamespaces + "\n");
        sb.append("isNamespaceSpecific=" + this.isNamespaceSpecific + "\n");
        sb.append("includeDefaults=" + this.includeDefaults + "\n");
        
        // if(namespacesToHandle == null)
        // {
        // sb.append("namespacesToHandle=null\n");
        // }
        // else
        // {
        // sb.append("namespacesToHandle=");
        //
        // RdfUtils.joinStringCollectionHelper(namespacesToHandle, ", ", sb);
        //
        // sb.append("\n");
        // }
        
        return sb.toString();
    }
}
