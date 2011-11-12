package org.queryall.impl.querytype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

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
import org.queryall.api.project.ProjectSchema;
import org.queryall.api.querytype.InputQueryType;
import org.queryall.api.querytype.InputQueryTypeSchema;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.querytype.QueryTypeSchema;
import org.queryall.api.querytype.RdfOutputQueryType;
import org.queryall.api.querytype.RdfOutputQueryTypeSchema;
import org.queryall.api.querytype.SparqlProcessorQueryType;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.queryall.query.ProvenanceRecord;
import org.queryall.utils.ProfileUtils;
import org.queryall.utils.RdfUtils;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class QueryTypeImpl implements QueryType, InputQueryType, SparqlProcessorQueryType, RdfOutputQueryType,
        HtmlExport
{
    private static final Logger log = LoggerFactory.getLogger(QueryTypeImpl.class);
    private static final boolean _TRACE = QueryTypeImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = QueryTypeImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = QueryTypeImpl.log.isInfoEnabled();
    
    protected Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    private URI key;
    
    private String title = "";
    
    private URI curationStatus = ProjectSchema.getProjectNotCuratedUri();
    
    private boolean handleAllNamespaces = true;
    
    // If present, this is a list of namespaces which can be handled by this type of custom query
    // (or at least this form of it), unless handleAllNamespaces is true, in which case any
    // namespace can be present here without effect
    private Collection<URI> namespacesToHandle = new HashSet<URI>();
    
    // if a query is not namepsace specific it can be executed across providers which do not
    // necessarily handle this namespace, but are not necessarily defaults per se
    private boolean isNamespaceSpecific = false;
    
    // these are the input_NN indexes that are either namespaces, or correspond to public
    // identifiers that are not untouchable internal private identifiers
    // among other things, it can be used to make sure that these are lowercased per a given policy
    // (in this case the Banff Manifesto)
    private Collection<String> publicIdentifierTags = new ArrayList<String>(2);
    
    // these are the input_NN indexes that we will use to determine which namespace providers to
    // perform this query using
    private Collection<String> namespaceInputTags = new ArrayList<String>(2);
    
    // This is the method by which we determine whether any or all of the namespaces are required on
    // a particular endpoint before we utilise it
    // if defaults are included in this query then we will always use default providers regardless
    // of the namespaces they have declared on them
    // if we do not use all namespaces then this setting will still be in effect, but it will first
    // match against the list that we do handle before getting to the provider choice stage
    // For example, if we match inputs 1 and 2 as namespaceInputIndexes, and we have the the
    // namespaceMatchMethod set to QueryType.queryNamespaceMatchAll.stringValue(), and we do not
    // handle all namespaces and inputs 1 and 2 both exist in namespacesToHandle then we will
    // satisfy the initial test for query usability
    // Possible values are QueryType.queryNamespaceMatchAll.stringValue() and
    // QueryType.queryNamespaceMatchAny.stringValue()
    private URI namespaceMatchMethod = QueryTypeSchema.getQueryNamespaceMatchAny();
    
    // if we are told we can include defaults, even if we are known to be namespace specific we can
    // utilise the default providers as sources
    private boolean includeDefaults = true;
    
    // if this query can be paged using the pageoffsetNN mechanism, this should be true, and
    // otherwise it should be false
    private boolean isPageable = false;
    
    // If this query is restricted by any of the robots.txt entries than declare that here, so that
    // automatic bot detection is functional for this query
    private boolean inRobotsTxt = false;
    
    // use this to define which additional custom query rdf triples to add to a particular type of
    // custom query
    // a typical use for this is for adding links and index triples to construct,index,links etc
    // type queries, but not to others for instance
    private Collection<URI> semanticallyLinkedCustomQueries = new HashSet<URI>();
    
    private URI profileIncludeExcludeOrder = ProfileSchema.getProfileIncludeExcludeOrderUndefinedUri();
    
    private String templateString = "";
    
    private String queryUriTemplateString = "";
    
    private String standardUriTemplateString = "";
    
    private String outputRdfString = "";
    
    @SuppressWarnings("unused")
    private Collection<ProvenanceRecord> relatedProvenance = new HashSet<ProvenanceRecord>();
    
    private boolean isDummyQueryType = false;
    // default to universally available RDF/XML, and for backwards compatibility with previous
    // versions (<5) that only supported RDF/XML output
    private String outputRdfFormat = Constants.APPLICATION_RDF_XML;
    private Collection<String> expectedInputParameters = new ArrayList<String>(5);
    
    protected QueryTypeImpl()
    {
        // TODO Auto-generated constructor stub
    }
    
    protected QueryTypeImpl(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        for(final Statement nextStatement : inputStatements)
        {
            if(QueryTypeImpl._DEBUG)
            {
                QueryTypeImpl.log.debug("QueryType: nextStatement: " + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(QueryTypeSchema.getQueryTypeUri()))
            {
                if(QueryTypeImpl._TRACE)
                {
                    QueryTypeImpl.log.trace("QueryType: found valid type predicate for URI: " + keyToUse);
                }
                
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(QueryTypeSchema.getQueryTitle())
                    || nextStatement.getPredicate().equals(Constants.DC_TITLE))
            {
                this.setTitle(nextStatement.getObject().stringValue());
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
                this.setNamespaceMatchMethod((URI)nextStatement.getObject());
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
                this.setTemplateString(nextStatement.getObject().stringValue());
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
                this.setProfileIncludeExcludeOrder((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(ProjectSchema.getProjectCurationStatusUri()))
            {
                this.setCurationStatus((URI)nextStatement.getObject());
            }
            else
            {
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
        // this.setSemanticallyLinkedQueryTypes(tempsemanticallyLinkedCustomQueries);
        
        if(QueryTypeImpl._DEBUG)
        {
            QueryTypeImpl.log.debug("QueryType.fromRdf: would have returned... keyToUse=" + keyToUse + " result="
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
        if(this.namespacesToHandle == null)
        {
            this.namespacesToHandle = new HashSet<URI>();
        }
        
        this.namespacesToHandle.add(namespaceToHandle);
    }
    
    @Override
    public void addPublicIdentifierTag(final String publicIdentifierTag)
    {
        this.publicIdentifierTags.add(publicIdentifierTag);
    }
    
    @Override
    public void addUnrecognisedStatement(final Statement unrecognisedStatement)
    {
        this.unrecognisedStatements.add(unrecognisedStatement);
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
    
    @Override
    public URI getCurationStatus()
    {
        return this.curationStatus;
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
    
    /**
     * @return the key
     */
    @Override
    public URI getKey()
    {
        return this.key;
    }
    
    @Override
    public Collection<URI> getLinkedQueryTypes()
    {
        return this.semanticallyLinkedCustomQueries;
    }
    
    @Override
    public Collection<String> getNamespaceInputTags()
    {
        return this.namespaceInputTags;
    }
    
    @Override
    public URI getNamespaceMatchMethod()
    {
        return this.namespaceMatchMethod;
    }
    
    @Override
    public Collection<URI> getNamespacesToHandle()
    {
        return this.namespacesToHandle;
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
    public URI getProfileIncludeExcludeOrder()
    {
        return this.profileIncludeExcludeOrder;
    }
    
    @Override
    public Collection<String> getPublicIdentifierTags()
    {
        return this.publicIdentifierTags;
    }
    
    @Override
    public String getQueryUriTemplateString()
    {
        return this.queryUriTemplateString;
    }
    
    @Override
    public String getSparqlTemplateString()
    {
        // Wrappers around the getTemplateString function for now
        return this.getTemplateString();
    }
    
    @Override
    public String getStandardUriTemplateString()
    {
        return this.standardUriTemplateString;
    }
    
    @Override
    public String getTemplateString()
    {
        return this.templateString;
    }
    
    @Override
    public String getTitle()
    {
        return this.title;
    }
    
    @Override
    public Collection<Statement> getUnrecognisedStatements()
    {
        return this.unrecognisedStatements;
    }
    
    @Override
    public boolean handlesNamespacesSpecifically(final Collection<Collection<URI>> namespacesToCheck)
    {
        if(!this.isNamespaceSpecific || this.namespacesToHandle == null || namespacesToCheck == null)
        {
            return false;
        }
        
        if(QueryTypeImpl._DEBUG)
        {
            QueryTypeImpl.log
                    .debug("QueryType.handlesNamespacesSpecifically: starting to compute match for this.getKey()="
                            + this.getKey() + " namespacesToHandle=" + this.namespacesToHandle + " namespacesToCheck="
                            + namespacesToCheck);
        }
        
        // Starting presumptions like this make the algorithm implementation simpler
        boolean anyMatched = false;
        
        boolean allMatched = true;
        
        // for each of the namespaces to check (represented by one or more URI's),
        // check that we have a locally handled namespace URI that matches
        // one of the URI's in each of the list of namespaces to check
        
        for(final Collection<URI> nextNamespaceToCheckList : namespacesToCheck)
        {
            if(nextNamespaceToCheckList == null)
            {
                if(QueryTypeImpl._DEBUG)
                {
                    QueryTypeImpl.log
                            .debug("QueryType.handlesNamespacesSpecifically: nextNamespaceToCheckList was null");
                }
                
                continue;
            }
            
            boolean matchFound = false;
            
            for(final URI nextLocalNamespace : this.namespacesToHandle)
            {
                if(nextLocalNamespace == null)
                {
                    if(QueryTypeImpl._DEBUG)
                    {
                        QueryTypeImpl.log
                                .debug("QueryType.handlesNamespacesSpecifically: nextLocalNamespace was null or empty string");
                    }
                    
                    continue;
                }
                
                for(final URI nextNamespaceToCheck : nextNamespaceToCheckList)
                {
                    if(nextNamespaceToCheck.equals(nextLocalNamespace))
                    {
                        if(QueryTypeImpl._DEBUG)
                        {
                            QueryTypeImpl.log
                                    .debug("QueryType.handlesNamespacesSpecifically: found match nextNamespaceToCheck="
                                            + nextNamespaceToCheck + " this.getKey()=" + this.getKey());
                        }
                        
                        matchFound = true;
                        break;
                    }
                }
            }
            
            if(matchFound)
            {
                anyMatched = true;
                
                if(this.namespaceMatchMethod.equals(QueryTypeSchema.getQueryNamespaceMatchAny()))
                {
                    if(QueryTypeImpl._DEBUG)
                    {
                        QueryTypeImpl.log
                                .debug("QueryType.handlesNamespacesSpecifically: any match confirmed this.getKey()="
                                        + this.getKey());
                    }
                    
                    break;
                }
            }
            else
            {
                allMatched = false;
                
                if(this.namespaceMatchMethod.equals(QueryTypeSchema.getQueryNamespaceMatchAll()))
                {
                    if(QueryTypeImpl._DEBUG)
                    {
                        QueryTypeImpl.log
                                .debug("QueryType.handlesNamespacesSpecifically: all match disproved this.getKey()="
                                        + this.getKey());
                    }
                    
                    break;
                }
            }
        }
        
        if(this.namespaceMatchMethod.equals(QueryTypeSchema.getQueryNamespaceMatchAny()))
        {
            if(QueryTypeImpl._DEBUG)
            {
                if(anyMatched)
                {
                    QueryTypeImpl.log.debug("QueryType.handlesNamespacesSpecifically: any match return value true");
                }
                else
                {
                    QueryTypeImpl.log.debug("QueryType.handlesNamespacesSpecifically: any match return value false");
                }
            }
            
            return anyMatched;
        }
        else if(this.namespaceMatchMethod.equals(QueryTypeSchema.getQueryNamespaceMatchAll()))
        {
            if(QueryTypeImpl._DEBUG)
            {
                if(allMatched)
                {
                    QueryTypeImpl.log.debug("QueryType.handlesNamespacesSpecifically: all match return value true");
                }
                else
                {
                    QueryTypeImpl.log.debug("QueryType.handlesNamespacesSpecifically: all match return value false");
                }
            }
            
            return allMatched;
        }
        else
        {
            QueryTypeImpl.log.error("Could not recognise the namespaceMatchMethod=" + this.namespaceMatchMethod);
            
            throw new RuntimeException("Could not recognise the namespaceMatchMethod=" + this.namespaceMatchMethod);
        }
    }
    
    @Override
    public boolean handlesNamespaceUris(final Collection<Collection<URI>> namespacesToCheck)
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
    
    // returns true if the input variable is in the list of public input variables
    @Override
    public boolean isInputVariableNamespace(final String inputVariable)
    {
        if(inputVariable == null)
        {
            throw new IllegalArgumentException("Cannot have null input variables");
        }
        
        if(this.namespaceInputTags != null)
        {
            for(final String nextNamespaceInputTag : this.getNamespaceInputTags())
            {
                if(inputVariable.equals(nextNamespaceInputTag))
                {
                    return true;
                }
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
            for(final String nextPublicIdentifierTag : this.getPublicIdentifierTags())
            {
                if(inputVariable.equals(nextPublicIdentifierTag))
                {
                    return true;
                }
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
        return ProfileUtils.isUsedWithProfileList(this, orderedProfileList, allowImplicitInclusions,
                includeNonProfileMatched);
    }
    
    @Override
    public void setCurationStatus(final URI curationStatus)
    {
        this.curationStatus = curationStatus;
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
    
    /**
     * @param key
     *            the key to set
     */
    @Override
    public void setKey(final String nextKey)
    {
        this.setKey(StringUtils.createURI(nextKey));
    }
    
    @Override
    public void setKey(final URI nextKey)
    {
        this.key = nextKey;
    }
    
    @Override
    public void setNamespaceMatchMethod(final URI namespaceMatchMethod)
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
    public void setProfileIncludeExcludeOrder(final URI profileIncludeExcludeOrder)
    {
        this.profileIncludeExcludeOrder = profileIncludeExcludeOrder;
    }
    
    @Override
    public void setQueryUriTemplateString(final String queryUriTemplateString)
    {
        this.queryUriTemplateString = queryUriTemplateString;
    }
    
    @Override
    public void setSparqlTemplateString(final String templateString)
    {
        // Wrappers around the setTemplateString function for now
        this.setTemplateString(templateString);
    }
    
    @Override
    public void setStandardUriTemplateString(final String standardUriTemplateString)
    {
        this.standardUriTemplateString = standardUriTemplateString;
    }
    
    @Override
    public void setTemplateString(final String templateString)
    {
        this.templateString = templateString;
    }
    
    @Override
    public void setTitle(final String title)
    {
        this.title = title;
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
                + StringUtils.xmlEncodeString(this.title) + "\" /></div>\n");
        
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
        
        if(this.namespaceMatchMethod.equals(QueryTypeSchema.getQueryNamespaceMatchAll()))
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
    public boolean toRdf(final Repository myRepository, final int modelVersion, final URI... keyToUse)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            // create some resources and literals to make statements out of
            final URI queryInstanceUri = this.getKey();
            
            final Literal titleLiteral = f.createLiteral(this.title);
            final Literal handleAllNamespacesLiteral = f.createLiteral(this.handleAllNamespaces);
            final Literal isNamespaceSpecificLiteral = f.createLiteral(this.isNamespaceSpecific);
            final URI namespaceMatchMethodLiteral = this.namespaceMatchMethod;
            final Literal includeDefaultsLiteral = f.createLiteral(this.includeDefaults);
            final Literal templateStringLiteral = f.createLiteral(this.templateString);
            final Literal queryUriTemplateStringLiteral = f.createLiteral(this.queryUriTemplateString);
            final Literal standardUriTemplateStringLiteral = f.createLiteral(this.standardUriTemplateString);
            
            final Literal inRobotsTxtLiteral = f.createLiteral(this.inRobotsTxt);
            final Literal isPageableLiteral = f.createLiteral(this.isPageable);
            final Literal isDummyQueryTypeLiteral = f.createLiteral(this.isDummyQueryType);
            final URI profileIncludeExcludeOrderLiteral = this.profileIncludeExcludeOrder;
            
            final Literal outputRdfStringLiteral = f.createLiteral(this.outputRdfString);
            final Literal outputRdfFormatLiteral = f.createLiteral(this.outputRdfFormat);
            
            URI curationStatusLiteral = null;
            
            if(this.curationStatus == null)
            {
                curationStatusLiteral = ProjectSchema.getProjectNotCuratedUri();
            }
            else
            {
                curationStatusLiteral = this.curationStatus;
            }
            
            // log.info("after literals created");
            
            con.setAutoCommit(false);
            
            for(final URI nextElementType : this.getElementTypes())
            {
                con.add(queryInstanceUri, RDF.TYPE, nextElementType, keyToUse);
            }
            
            con.add(queryInstanceUri, ProjectSchema.getProjectCurationStatusUri(), curationStatusLiteral, keyToUse);
            if(modelVersion == 1)
            {
                con.add(queryInstanceUri, QueryTypeSchema.getQueryTitle(), titleLiteral, keyToUse);
            }
            else
            {
                con.add(queryInstanceUri, Constants.DC_TITLE, titleLiteral, keyToUse);
            }
            
            con.add(queryInstanceUri, QueryTypeSchema.getQueryHandleAllNamespaces(), handleAllNamespacesLiteral,
                    keyToUse);
            con.add(queryInstanceUri, QueryTypeSchema.getQueryNamespaceSpecific(), isNamespaceSpecificLiteral, keyToUse);
            con.add(queryInstanceUri, QueryTypeSchema.getQueryNamespaceMatchMethod(), namespaceMatchMethodLiteral,
                    keyToUse);
            con.add(queryInstanceUri, QueryTypeSchema.getQueryIncludeDefaults(), includeDefaultsLiteral, keyToUse);
            con.add(queryInstanceUri, QueryTypeSchema.getQueryTemplateString(), templateStringLiteral, keyToUse);
            con.add(queryInstanceUri, QueryTypeSchema.getQueryQueryUriTemplateString(), queryUriTemplateStringLiteral,
                    keyToUse);
            con.add(queryInstanceUri, QueryTypeSchema.getQueryStandardUriTemplateString(),
                    standardUriTemplateStringLiteral, keyToUse);
            con.add(queryInstanceUri, QueryTypeSchema.getQueryInRobotsTxt(), inRobotsTxtLiteral, keyToUse);
            con.add(queryInstanceUri, QueryTypeSchema.getQueryIsPageable(), isPageableLiteral, keyToUse);
            con.add(queryInstanceUri, QueryTypeSchema.getQueryIsDummyQueryType(), isDummyQueryTypeLiteral, keyToUse);
            
            con.add(queryInstanceUri, ProfileSchema.getProfileIncludeExcludeOrderUri(),
                    profileIncludeExcludeOrderLiteral, keyToUse);
            
            if(modelVersion < 5)
            {
                if(this.getOutputRdfFormat().equals(Constants.APPLICATION_RDF_XML))
                {
                    con.add(queryInstanceUri, RdfOutputQueryTypeSchema.getOLDQueryOutputRdfXmlString(),
                            outputRdfStringLiteral, keyToUse);
                }
                else
                {
                    QueryTypeImpl.log
                            .info("Unable to supply RDF Output string to this user as they requested an old version of the api, and the template was not in application/rdf+xml format");
                }
            }
            else
            {
                con.add(queryInstanceUri, RdfOutputQueryTypeSchema.getQueryOutputRdfString(), outputRdfStringLiteral,
                        keyToUse);
                con.add(queryInstanceUri, RdfOutputQueryTypeSchema.getQueryOutputRdfFormat(), outputRdfFormatLiteral,
                        keyToUse);
                
            }
            
            // log.info("after single URIs created");
            
            if(this.expectedInputParameters != null)
            {
                
                for(final String nextExpectedInputParameter : this.expectedInputParameters)
                {
                    if(nextExpectedInputParameter != null)
                    {
                        con.add(queryInstanceUri, InputQueryTypeSchema.getQueryExpectedInputParameters(),
                                f.createLiteral(nextExpectedInputParameter), keyToUse);
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
                                keyToUse);
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
                                                .length()))), keyToUse);
                            }
                            catch(final NumberFormatException nfe)
                            {
                                QueryTypeImpl.log
                                        .info("Could not convert input_NN tag backwards to previous index version due to an issue with the tag nextPublicIdentifierTag="
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
                                f.createLiteral(nextPublicIdentifierTag), keyToUse);
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
                                                .length()))), keyToUse);
                            }
                            catch(final NumberFormatException nfe)
                            {
                                QueryTypeImpl.log
                                        .info("Could not convert input_NN tag backwards to previous index version due to an issue with the tag nextNamespaceInputTag="
                                                + nextNamespaceInputTag
                                                + " querytype.getKey()="
                                                + this.getKey().stringValue());
                            }
                        }
                    }
                }
                else
                {
                    for(final String nextNamespaceInputTag : this.namespaceInputTags)
                    {
                        con.add(queryInstanceUri, QueryTypeSchema.getQueryNamespaceInputTag(),
                                f.createLiteral(nextNamespaceInputTag), keyToUse);
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
                                nextSemanticallyLinkedQueryType, keyToUse);
                    }
                }
            }
            
            if(this.unrecognisedStatements != null)
            {
                
                for(final Statement nextUnrecognisedStatement : this.unrecognisedStatements)
                {
                    con.add(nextUnrecognisedStatement, keyToUse);
                }
            }
            
            // log.info("after unrecognised statements added");
            
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
            
            QueryTypeImpl.log.error("RepositoryException: " + re.getMessage());
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
        
        sb.append("key=" + this.key + "\n");
        sb.append("title=" + this.title + "\n");
        
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
