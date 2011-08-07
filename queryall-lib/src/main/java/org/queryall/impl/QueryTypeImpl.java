package org.queryall.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.Profile;
import org.queryall.api.QueryType;
import org.queryall.helpers.Constants;
import org.queryall.helpers.ListUtils;
import org.queryall.helpers.ProfileUtils;
import org.queryall.helpers.RdfUtils;
import org.queryall.helpers.Settings;
import org.queryall.helpers.StringUtils;
import org.queryall.queryutils.ProvenanceRecord;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class QueryTypeImpl implements QueryType
{
    private static final Logger log = Logger.getLogger(QueryType.class.getName());
    private static final boolean _TRACE = QueryTypeImpl.log.isTraceEnabled();
    private static final boolean _DEBUG = QueryTypeImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = QueryTypeImpl.log.isInfoEnabled();
    
    private static final String defaultNamespace = Settings.getSettings().getNamespaceForQueryType();
    
    private Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    private URI key;
    private String title = "";
    private URI curationStatus = ProjectImpl.getProjectNotCuratedUri();
    
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
    private int[] publicIdentifierIndexes;
    // these are the input_NN indexes that we will use to determine which namespace providers to
    // perform this query using
    private int[] namespaceInputIndexes;
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
    private URI namespaceMatchMethod = QueryTypeImpl.getQueryNamespaceMatchAny();
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
    
    private URI profileIncludeExcludeOrder = ProfileImpl.getProfileIncludeExcludeOrderUndefinedUri();
    
    private String inputRegex = "";
    private Pattern inputRegexPattern = null;
    
    private String templateString = "";
    private String queryUriTemplateString = "";
    private String standardUriTemplateString = "";
    private String outputRdfXmlString = "";
    
    @SuppressWarnings("unused")
    private Collection<ProvenanceRecord> relatedProvenance = new HashSet<ProvenanceRecord>();
    private boolean isDummyQueryType = false;
    
    private static URI queryTypeUri;
    private static URI queryTitle;
    private static URI queryHandleAllNamespaces;
    private static URI queryNamespaceToHandle;
    private static URI queryPublicIdentifierIndex;
    private static URI queryNamespaceInputIndex;
    private static URI queryNamespaceMatchMethod;
    private static URI queryNamespaceSpecific;
    private static URI queryIncludeDefaults;
    private static URI queryInputRegex;
    
    private static URI queryIncludeQueryType;
    private static URI queryTemplateString;
    private static URI queryQueryUriTemplateString;
    private static URI queryStandardUriTemplateString;
    private static URI queryOutputRdfXmlString;
    
    private static URI queryTemplateTerm;
    private static URI queryParameterTemplateTerm;
    private static URI queryStaticOutputTemplateTerm;
    
    private static URI queryInRobotsTxt;
    private static URI queryIsPageable;
    private static URI queryIsDummyQueryType;
    private static URI queryNamespaceMatchAny;
    private static URI queryNamespaceMatchAll;
    
    public static String queryNamespace;
    
    public QueryTypeImpl(Collection<Statement> inputStatements, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        Collection<URI> tempNamespacesToHandle = new HashSet<URI>();
        Collection<Integer> tempPublicIdentifierIndexes = new HashSet<Integer>();
        Collection<Integer> tempNamespaceInputIndexes = new HashSet<Integer>();
        
        Collection<URI> tempsemanticallyLinkedCustomQueries = new HashSet<URI>();
        
        for(Statement nextStatement : inputStatements)
        {
            if(QueryTypeImpl._DEBUG)
            {
                QueryTypeImpl.log.debug("QueryType: nextStatement: " + nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE)
                    && nextStatement.getObject().equals(QueryTypeImpl.getQueryTypeUri()))
            {
                if(QueryTypeImpl._TRACE)
                {
                    QueryTypeImpl.log.trace("QueryType: found valid type predicate for URI: " + keyToUse);
                }
                
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(ProjectImpl.getProjectCurationStatusUri()))
            {
                this.setCurationStatus((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(QueryTypeImpl.getQueryTitle())
                    || nextStatement.getPredicate().equals(Constants.DC_TITLE))
            {
                this.setTitle(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(QueryTypeImpl.getQueryHandleAllNamespaces()))
            {
                this.setHandleAllNamespaces(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(QueryTypeImpl.getQueryNamespaceToHandle()))
            {
                tempNamespacesToHandle.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(QueryTypeImpl.getQueryPublicIdentifierIndex()))
            {
                tempPublicIdentifierIndexes.add(RdfUtils.getIntegerFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(QueryTypeImpl.getQueryNamespaceInputIndex()))
            {
                tempNamespaceInputIndexes.add(RdfUtils.getIntegerFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(QueryTypeImpl.getQueryNamespaceMatchMethod()))
            {
                this.setNamespaceMatchMethod((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(QueryTypeImpl.getQueryNamespaceSpecific()))
            {
                this.setIsNamespaceSpecific(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(QueryTypeImpl.getQueryIncludeDefaults()))
            {
                this.setIncludeDefaults(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(QueryTypeImpl.getQueryInputRegex()))
            {
                this.setInputRegex(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(QueryTypeImpl.getQueryIncludeQueryType()))
            {
                tempsemanticallyLinkedCustomQueries.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(QueryTypeImpl.getQueryTemplateString()))
            {
                this.setTemplateString(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(QueryTypeImpl.getQueryQueryUriTemplateString()))
            {
                this.setQueryUriTemplateString(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(QueryTypeImpl.getQueryStandardUriTemplateString()))
            {
                this.setStandardUriTemplateString(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(QueryTypeImpl.getQueryOutputRdfXmlString()))
            {
                this.setOutputRdfXmlString(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(QueryTypeImpl.getQueryInRobotsTxt()))
            {
                this.setInRobotsTxt(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(QueryTypeImpl.getQueryIsPageable()))
            {
                this.setIsPageable(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(QueryTypeImpl.getQueryIsDummyQueryType()))
            {
                this.setIsDummyQueryType(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(ProfileImpl.getProfileIncludeExcludeOrderUri()))
            {
                this.setProfileIncludeExcludeOrder((URI)nextStatement.getObject());
            }
            else
            {
                this.addUnrecognisedStatement(nextStatement);
            }
        }
        
        this.setNamespacesToHandle(tempNamespacesToHandle);
        this.setPublicIdentifierIndexes(ListUtils.getIntArrayFromArrayInteger(tempPublicIdentifierIndexes
                .toArray(new Integer[0])));
        this.setNamespaceInputIndexes(ListUtils.getIntArrayFromArrayInteger(tempNamespaceInputIndexes
                .toArray(new Integer[0])));
        
        this.setSemanticallyLinkedQueryTypes(tempsemanticallyLinkedCustomQueries);
        
        if(QueryTypeImpl._DEBUG)
        {
            QueryTypeImpl.log.debug("QueryType.fromRdf: would have returned... keyToUse=" + keyToUse + " result="
                    + this.toString());
        }
    }
    
    public QueryTypeImpl()
    {
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public String getInputRegex()
    {
        return inputRegex;
    }
    
    @Override
    public Pattern getInputRegexPattern()
    {
        if(inputRegexPattern == null && inputRegex != null)
        {
            inputRegexPattern = Pattern.compile(inputRegex);
        }
        
        return inputRegexPattern;
    }
    
    @Override
    public void setInputRegex(String nextInputRegex)
    {
        inputRegex = nextInputRegex;
        inputRegexPattern = Pattern.compile(nextInputRegex);
    }
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        QueryTypeImpl.queryNamespace =
                Settings.getSettings().getOntologyTermUriPrefix() + Settings.getSettings().getNamespaceForQueryType()
                        + Settings.getSettings().getOntologyTermUriSuffix();
        
        QueryTypeImpl.setQueryTypeUri(f.createURI(QueryTypeImpl.queryNamespace, "Query"));
        QueryTypeImpl.setQueryTitle(f.createURI(QueryTypeImpl.queryNamespace, "title"));
        QueryTypeImpl.setQueryHandleAllNamespaces(f.createURI(QueryTypeImpl.queryNamespace, "handleAllNamespaces"));
        QueryTypeImpl.setQueryNamespaceToHandle(f.createURI(QueryTypeImpl.queryNamespace, "namespaceToHandle"));
        QueryTypeImpl.setQueryPublicIdentifierIndex(f.createURI(QueryTypeImpl.queryNamespace,
                "hasPublicIdentifierIndex"));
        QueryTypeImpl.setQueryNamespaceInputIndex(f.createURI(QueryTypeImpl.queryNamespace, "hasNamespaceInputIndex"));
        QueryTypeImpl.setQueryNamespaceMatchMethod(f.createURI(QueryTypeImpl.queryNamespace, "namespaceMatchMethod"));
        QueryTypeImpl.setQueryNamespaceSpecific(f.createURI(QueryTypeImpl.queryNamespace, "isNamespaceSpecific"));
        QueryTypeImpl.setQueryIncludeDefaults(f.createURI(QueryTypeImpl.queryNamespace, "includeDefaults"));
        QueryTypeImpl.setQueryIncludeQueryType(f.createURI(QueryTypeImpl.queryNamespace, "includeQueryType"));
        QueryTypeImpl.setQueryInputRegex(f.createURI(QueryTypeImpl.queryNamespace, "inputRegex"));
        QueryTypeImpl.setQueryTemplateString(f.createURI(QueryTypeImpl.queryNamespace, "templateString"));
        QueryTypeImpl.setQueryQueryUriTemplateString(f
                .createURI(QueryTypeImpl.queryNamespace, "queryUriTemplateString"));
        QueryTypeImpl.setQueryStandardUriTemplateString(f.createURI(QueryTypeImpl.queryNamespace,
                "standardUriTemplateString"));
        QueryTypeImpl.setQueryOutputRdfXmlString(f.createURI(QueryTypeImpl.queryNamespace, "outputRdfXmlString"));
        QueryTypeImpl.setQueryInRobotsTxt(f.createURI(QueryTypeImpl.queryNamespace, "inRobotsTxt"));
        QueryTypeImpl.setQueryIsPageable(f.createURI(QueryTypeImpl.queryNamespace, "isPageable"));
        QueryTypeImpl.setQueryNamespaceMatchAny(f.createURI(QueryTypeImpl.queryNamespace, "namespaceMatchAny"));
        QueryTypeImpl.setQueryNamespaceMatchAll(f.createURI(QueryTypeImpl.queryNamespace, "namespaceMatchAll"));
        QueryTypeImpl.setQueryTemplateTerm(f.createURI(QueryTypeImpl.queryNamespace, "includedQueryTemplate"));
        QueryTypeImpl.setQueryParameterTemplateTerm(f.createURI(QueryTypeImpl.queryNamespace,
                "includedQueryParameterTemplate"));
        QueryTypeImpl.setQueryStaticOutputTemplateTerm(f.createURI(QueryTypeImpl.queryNamespace,
                "includedStaticOutputTemplate"));
        QueryTypeImpl.setQueryIsDummyQueryType(f.createURI(QueryTypeImpl.queryNamespace, "isDummyQueryType"));
    }
    
    // returns true if the input variable is in the list of public input variables
    @Override
    public boolean isInputVariablePublic(int inputNumber)
    {
        if(publicIdentifierIndexes != null)
        {
            
            for(int nextPublicIdentifierIndex : publicIdentifierIndexes)
            {
                if(inputNumber == nextPublicIdentifierIndex)
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
    public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            // create some resources and literals to make statements out of
            URI queryInstanceUri = this.getKey();
            
            /***
             * title handleAllNamespaces isNamespaceSpecific namespaceMatchMethod includeDefaults
             * inputRegex templateString queryUriTemplateString standardUriTemplateString
             * outputRdfXmlString inRobotsTxt profileIncludeExcludeOrder
             ***/
            
            Literal titleLiteral = f.createLiteral(title);
            Literal handleAllNamespacesLiteral = f.createLiteral(handleAllNamespaces);
            Literal isNamespaceSpecificLiteral = f.createLiteral(isNamespaceSpecific);
            URI namespaceMatchMethodLiteral = namespaceMatchMethod;
            Literal includeDefaultsLiteral = f.createLiteral(includeDefaults);
            Literal inputRegexLiteral = f.createLiteral(inputRegex);
            
            Literal templateStringLiteral = f.createLiteral(templateString);
            Literal queryUriTemplateStringLiteral = f.createLiteral(queryUriTemplateString);
            Literal standardUriTemplateStringLiteral = f.createLiteral(standardUriTemplateString);
            Literal outputRdfXmlStringLiteral = f.createLiteral(outputRdfXmlString);
            
            Literal inRobotsTxtLiteral = f.createLiteral(inRobotsTxt);
            Literal isPageableLiteral = f.createLiteral(isPageable);
            Literal isDummyQueryTypeLiteral = f.createLiteral(isDummyQueryType);
            URI profileIncludeExcludeOrderLiteral = profileIncludeExcludeOrder;
            
            URI curationStatusLiteral = null;
            
            if(curationStatus == null)
            {
                curationStatusLiteral = ProjectImpl.getProjectNotCuratedUri();
            }
            else
            {
                curationStatusLiteral = curationStatus;
            }
            
            // log.info("after literals created");
            
            con.setAutoCommit(false);
            
            con.add(queryInstanceUri, RDF.TYPE, QueryTypeImpl.getQueryTypeUri(), keyToUse);
            con.add(queryInstanceUri, ProjectImpl.getProjectCurationStatusUri(), curationStatusLiteral, keyToUse);
            if(modelVersion == 1)
            {
                con.add(queryInstanceUri, QueryTypeImpl.getQueryTitle(), titleLiteral, keyToUse);
            }
            else
            {
                con.add(queryInstanceUri, Constants.DC_TITLE, titleLiteral, keyToUse);
            }
            
            con.add(queryInstanceUri, QueryTypeImpl.getQueryHandleAllNamespaces(), handleAllNamespacesLiteral, keyToUse);
            con.add(queryInstanceUri, QueryTypeImpl.getQueryNamespaceSpecific(), isNamespaceSpecificLiteral, keyToUse);
            con.add(queryInstanceUri, QueryTypeImpl.getQueryNamespaceMatchMethod(), namespaceMatchMethodLiteral,
                    keyToUse);
            con.add(queryInstanceUri, QueryTypeImpl.getQueryIncludeDefaults(), includeDefaultsLiteral, keyToUse);
            con.add(queryInstanceUri, QueryTypeImpl.getQueryInputRegex(), inputRegexLiteral, keyToUse);
            con.add(queryInstanceUri, QueryTypeImpl.getQueryTemplateString(), templateStringLiteral, keyToUse);
            con.add(queryInstanceUri, QueryTypeImpl.getQueryQueryUriTemplateString(), queryUriTemplateStringLiteral,
                    keyToUse);
            con.add(queryInstanceUri, QueryTypeImpl.getQueryStandardUriTemplateString(),
                    standardUriTemplateStringLiteral, keyToUse);
            con.add(queryInstanceUri, QueryTypeImpl.getQueryOutputRdfXmlString(), outputRdfXmlStringLiteral, keyToUse);
            con.add(queryInstanceUri, QueryTypeImpl.getQueryInRobotsTxt(), inRobotsTxtLiteral, keyToUse);
            con.add(queryInstanceUri, QueryTypeImpl.getQueryIsPageable(), isPageableLiteral, keyToUse);
            con.add(queryInstanceUri, QueryTypeImpl.getQueryIsDummyQueryType(), isDummyQueryTypeLiteral, keyToUse);
            
            con.add(queryInstanceUri, ProfileImpl.getProfileIncludeExcludeOrderUri(),
                    profileIncludeExcludeOrderLiteral, keyToUse);
            
            // log.info("after single URIs created");
            
            if(namespacesToHandle != null)
            {
                
                for(URI nextNamespaceToHandle : namespacesToHandle)
                {
                    if(nextNamespaceToHandle != null)
                    {
                        con.add(queryInstanceUri, QueryTypeImpl.getQueryNamespaceToHandle(), nextNamespaceToHandle,
                                keyToUse);
                    }
                }
            }
            
            if(publicIdentifierIndexes != null)
            {
                
                for(int nextPublicIdentifierIndex : publicIdentifierIndexes)
                {
                    con.add(queryInstanceUri, QueryTypeImpl.getQueryPublicIdentifierIndex(),
                            f.createLiteral(nextPublicIdentifierIndex), keyToUse);
                }
            }
            
            if(namespaceInputIndexes != null)
            {
                for(int nextNamespaceInputIndex : namespaceInputIndexes)
                {
                    con.add(queryInstanceUri, QueryTypeImpl.getQueryNamespaceInputIndex(),
                            f.createLiteral(nextNamespaceInputIndex), keyToUse);
                }
            }
            
            if(semanticallyLinkedCustomQueries != null)
            {
                for(URI nextSemanticallyLinkedQueryType : semanticallyLinkedCustomQueries)
                {
                    if(nextSemanticallyLinkedQueryType != null)
                    {
                        con.add(queryInstanceUri, QueryTypeImpl.getQueryIncludeQueryType(),
                                nextSemanticallyLinkedQueryType, keyToUse);
                    }
                }
            }
            
            if(unrecognisedStatements != null)
            {
                
                for(Statement nextUnrecognisedStatement : unrecognisedStatements)
                {
                    con.add(nextUnrecognisedStatement, keyToUse);
                }
            }
            
            // log.info("after unrecognised statements added");
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(RepositoryException re)
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
    
    public static boolean schemaToRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            URI contextKeyUri = keyToUse;
            con.setAutoCommit(false);
            
            con.add(QueryTypeImpl.getQueryTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);
            
            if(modelVersion == 1)
            {
                con.add(QueryTypeImpl.getQueryTitle(), RDF.TYPE, OWL.DEPRECATEDPROPERTY, contextKeyUri);
                con.add(QueryTypeImpl.getQueryTitle(), RDFS.SUBPROPERTYOF, Constants.DC_TITLE, contextKeyUri);
            }
            
            con.add(QueryTypeImpl.getQueryNamespaceToHandle(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(QueryTypeImpl.getQueryNamespaceToHandle(), RDFS.RANGE, NamespaceEntryImpl.getNamespaceTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeImpl.getQueryNamespaceToHandle(), RDFS.DOMAIN, QueryTypeImpl.getQueryTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeImpl.getQueryNamespaceToHandle(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeImpl.getQueryIncludeQueryType(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(QueryTypeImpl.getQueryIncludeQueryType(), RDFS.RANGE, QueryTypeImpl.getQueryTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeImpl.getQueryIncludeQueryType(), RDFS.DOMAIN, QueryTypeImpl.getQueryTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeImpl.getQueryIncludeQueryType(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeImpl.getQueryNamespaceMatchMethod(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(QueryTypeImpl.getQueryNamespaceMatchMethod(), RDFS.RANGE, RDFS.RESOURCE, contextKeyUri);
            con.add(QueryTypeImpl.getQueryNamespaceMatchMethod(), RDFS.DOMAIN, QueryTypeImpl.getQueryTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeImpl.getQueryNamespaceMatchMethod(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeImpl.getQueryHandleAllNamespaces(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(QueryTypeImpl.getQueryHandleAllNamespaces(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(QueryTypeImpl.getQueryHandleAllNamespaces(), RDFS.DOMAIN, QueryTypeImpl.getQueryTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeImpl.getQueryHandleAllNamespaces(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeImpl.getQueryNamespaceSpecific(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(QueryTypeImpl.getQueryNamespaceSpecific(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(QueryTypeImpl.getQueryNamespaceSpecific(), RDFS.DOMAIN, QueryTypeImpl.getQueryTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeImpl.getQueryNamespaceSpecific(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeImpl.getQueryIncludeDefaults(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(QueryTypeImpl.getQueryIncludeDefaults(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(QueryTypeImpl.getQueryIncludeDefaults(), RDFS.DOMAIN, QueryTypeImpl.getQueryTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeImpl.getQueryIncludeDefaults(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeImpl.getQueryInputRegex(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(QueryTypeImpl.getQueryInputRegex(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(QueryTypeImpl.getQueryInputRegex(), RDFS.DOMAIN, QueryTypeImpl.getQueryTypeUri(), contextKeyUri);
            con.add(QueryTypeImpl.getQueryInputRegex(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeImpl.getQueryInRobotsTxt(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(QueryTypeImpl.getQueryInRobotsTxt(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(QueryTypeImpl.getQueryInRobotsTxt(), RDFS.DOMAIN, QueryTypeImpl.getQueryTypeUri(), contextKeyUri);
            con.add(QueryTypeImpl.getQueryInRobotsTxt(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeImpl.getQueryIsPageable(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(QueryTypeImpl.getQueryIsPageable(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(QueryTypeImpl.getQueryIsPageable(), RDFS.DOMAIN, QueryTypeImpl.getQueryTypeUri(), contextKeyUri);
            con.add(QueryTypeImpl.getQueryIsPageable(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeImpl.getQueryIsDummyQueryType(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(QueryTypeImpl.getQueryIsDummyQueryType(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(QueryTypeImpl.getQueryIsDummyQueryType(), RDFS.DOMAIN, QueryTypeImpl.getQueryTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeImpl.getQueryIsDummyQueryType(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeImpl.getQueryPublicIdentifierIndex(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(QueryTypeImpl.getQueryPublicIdentifierIndex(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(QueryTypeImpl.getQueryPublicIdentifierIndex(), RDFS.DOMAIN, QueryTypeImpl.getQueryTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeImpl.getQueryPublicIdentifierIndex(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeImpl.getQueryNamespaceInputIndex(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(QueryTypeImpl.getQueryNamespaceInputIndex(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(QueryTypeImpl.getQueryNamespaceInputIndex(), RDFS.DOMAIN, QueryTypeImpl.getQueryTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeImpl.getQueryNamespaceInputIndex(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeImpl.getQueryTemplateString(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(QueryTypeImpl.getQueryTemplateString(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(QueryTypeImpl.getQueryTemplateString(), RDFS.DOMAIN, QueryTypeImpl.getQueryTypeUri(), contextKeyUri);
            con.add(QueryTypeImpl.getQueryTemplateString(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeImpl.getQueryQueryUriTemplateString(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(QueryTypeImpl.getQueryQueryUriTemplateString(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(QueryTypeImpl.getQueryQueryUriTemplateString(), RDFS.DOMAIN, QueryTypeImpl.getQueryTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeImpl.getQueryQueryUriTemplateString(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeImpl.getQueryStandardUriTemplateString(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(QueryTypeImpl.getQueryStandardUriTemplateString(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(QueryTypeImpl.getQueryStandardUriTemplateString(), RDFS.DOMAIN, QueryTypeImpl.getQueryTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeImpl.getQueryStandardUriTemplateString(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeImpl.getQueryOutputRdfXmlString(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(QueryTypeImpl.getQueryOutputRdfXmlString(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(QueryTypeImpl.getQueryOutputRdfXmlString(), RDFS.DOMAIN, QueryTypeImpl.getQueryTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeImpl.getQueryOutputRdfXmlString(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch(RepositoryException re)
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
        StringBuilder sb = new StringBuilder();
        
        sb.append("key=" + key + "\n");
        sb.append("title=" + title + "\n");
        
        // sb.append("templateString=" + templateString + "\n");
        // sb.append("standardUriTemplateString=" + standardUriTemplateString + "\n");
        // sb.append("queryUriTemplateString=" + queryUriTemplateString + "\n");
        // sb.append("outputRdfXmlString=" + outputRdfXmlString + "\n");
        sb.append("inputRegex=" + inputRegex + "\n");
        
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
        
        sb.append("handleAllNamespaces=" + handleAllNamespaces + "\n");
        sb.append("isNamespaceSpecific=" + isNamespaceSpecific + "\n");
        sb.append("includeDefaults=" + includeDefaults + "\n");
        
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
    
    @Override
    public String toHtmlFormBody()
    {
        StringBuilder sb = new StringBuilder();
        
        String prefix = "query_";
        
        if(getKey() != null)
        {
            sb.append("<div class=\"" + prefix + "key_div\"><input type=\"hidden\" name=\"key\" value=\""
                    + StringUtils.xmlEncodeString(getKey().stringValue()) + "\" /></div>\n");
        }
        
        sb.append("<div class=\"" + prefix + "title_div\"><span class=\"" + prefix
                + "title_span\">Title:</span><input type=\"text\" name=\"" + prefix + "title\" value=\""
                + StringUtils.xmlEncodeString(title) + "\" /></div>\n");
        
        sb.append("<div class=\"" + prefix + "templateString_div\"><span class=\"" + prefix
                + "templateString_span\">Query Template:</span><input type=\"text\" name=\"" + prefix
                + "templateString\" value=\"" + StringUtils.xmlEncodeString(templateString) + "\" /></div>\n");
        sb.append("<div class=\"" + prefix + "standardUriTemplateString_div\"><span class=\"" + prefix
                + "standardUriTemplateString_span\">Standard URI Template:</span><input type=\"text\" name=\"" + prefix
                + "standardUriTemplateString\" value=\"" + StringUtils.xmlEncodeString(standardUriTemplateString)
                + "\" /></div>\n");
        sb.append("<div class=\"" + prefix + "queryUriTemplateString_div\"><span class=\"" + prefix
                + "queryUriTemplateString_span\">Query URI Template:</span><input type=\"text\" name=\"" + prefix
                + "queryUriTemplateString\" value=\"" + StringUtils.xmlEncodeString(queryUriTemplateString)
                + "\" /></div>\n");
        sb.append("<div class=\"" + prefix + "outputRdfXmlString_div\"><span class=\"" + prefix
                + "outputRdfXmlString_span\">Static output RDF/XML Template:</span><input type=\"text\" name=\""
                + prefix + "outputRdfXmlString\" value=\"" + StringUtils.xmlEncodeString(outputRdfXmlString)
                + "\" /></div>\n");
        
        sb.append("<div class=\"" + prefix + "inputRegex_div\"><span class=\"" + prefix
                + "inputRegex_span\">Input Regular Expression:</span><input type=\"text\" name=\"" + prefix
                + "inputRegex\" value=\"" + StringUtils.xmlEncodeString(inputRegex) + "\" /></div>\n");
        
        sb.append("<div class=\"" + prefix + "isNamespaceSpecific_div\"><span class=\"" + prefix
                + "isNamespaceSpecific_span\">Is Namespace Specific:</span><input type=\"checkbox\" name=\"" + prefix
                + "isNamespaceSpecific\" value=\"isNamespaceSpecific\" ");
        
        if(isNamespaceSpecific)
        {
            sb.append(" checked=\"checked\" ");
        }
        
        sb.append(" /></div>\n");
        
        sb.append("<div class=\"" + prefix + "handleAllNamespaces_div\"><span class=\"" + prefix
                + "handleAllNamespaces_span\">Handle All Namespaces:</span><input type=\"checkbox\" name=\"" + prefix
                + "handleAllNamespaces\" value=\"handleAllNamespaces\" ");
        
        if(handleAllNamespaces)
        {
            sb.append(" checked=\"checked\" ");
        }
        
        sb.append(" /></div>\n");
        
        sb.append("<div class=\"" + prefix + "namespaceMatchMethod_div\"><span class=\"" + prefix
                + "namespaceMatchMethod_span\">All namespaces must match?:</span><input type=\"checkbox\" name=\""
                + prefix + "namespaceMatchMethod\" value=\"namespaceMatchMethod\" ");
        
        if(namespaceMatchMethod.equals(QueryTypeImpl.getQueryNamespaceMatchAll()))
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
        
        if(profileIncludeExcludeOrder.equals(ProfileImpl.getExcludeThenIncludeUri()))
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
        
        if(includeDefaults)
        {
            sb.append(" checked=\"checked\" ");
        }
        
        sb.append(" /></div>\n");
        
        sb.append("<div class=\"" + prefix + "inRobotsTxt_div\"><span class=\"" + prefix
                + "inRobotsTxt_span\">Is this query restricted by Robots.txt?:</span><input type=\"checkbox\" name=\""
                + prefix + "inRobotsTxt\" value=\"inRobotsTxt\" ");
        
        if(inRobotsTxt)
        {
            sb.append(" checked=\"checked\" ");
        }
        
        sb.append(" /></div>\n");
        
        return sb.toString();
    }
    
    @Override
    public List<String> matchesForQueryString(String nextQueryString)
    {
        return StringUtils.matchesForRegexOnString(getInputRegexPattern(), this.inputRegex, nextQueryString);
    }
    
    @Override
    public boolean matchesQueryString(String nextQueryString)
    {
        return StringUtils.matchesRegexOnString(getInputRegexPattern(), this.inputRegex, nextQueryString);
    }
    
    @Override
    public boolean handlesNamespaceUris(Collection<Collection<URI>> namespacesToCheck)
    {
        if(handleAllNamespaces && isNamespaceSpecific)
        {
            return true;
        }
        else
        {
            return handlesNamespacesSpecifically(namespacesToCheck);
        }
    }
    
    @Override
    public boolean handlesNamespacesSpecifically(Collection<Collection<URI>> namespacesToCheck)
    {
        if(!isNamespaceSpecific || namespacesToHandle == null || namespacesToCheck == null)
        {
            return false;
        }
        
        if(QueryTypeImpl._DEBUG)
        {
            QueryTypeImpl.log
                    .debug("QueryType.handlesNamespacesSpecifically: starting to compute match for this.getKey()="
                            + this.getKey() + " namespacesToHandle=" + namespacesToHandle + " namespacesToCheck="
                            + namespacesToCheck);
        }
        
        // Starting presumptions like this make the algorithm implementation simpler
        boolean anyMatched = false;
        
        boolean allMatched = true;
        
        // for each of the namespaces to check (represented by one or more URI's),
        // check that we have a locally handled namespace URI that matches
        // one of the URI's in each of the list of namespaces to check
        
        for(Collection<URI> nextNamespaceToCheckList : namespacesToCheck)
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
            
            for(URI nextLocalNamespace : namespacesToHandle)
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
                
                for(URI nextNamespaceToCheck : nextNamespaceToCheckList)
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
                
                if(namespaceMatchMethod.equals(QueryTypeImpl.getQueryNamespaceMatchAny()))
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
                
                if(namespaceMatchMethod.equals(QueryTypeImpl.getQueryNamespaceMatchAll()))
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
        
        if(namespaceMatchMethod.equals(QueryTypeImpl.getQueryNamespaceMatchAny()))
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
        else if(namespaceMatchMethod.equals(QueryTypeImpl.getQueryNamespaceMatchAll()))
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
            QueryTypeImpl.log.error("Could not recognise the namespaceMatchMethod=" + namespaceMatchMethod);
            
            throw new RuntimeException("Could not recognise the namespaceMatchMethod=" + namespaceMatchMethod);
        }
    }
    
    @Override
    public String toHtml()
    {
        StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        String prefix = "query_";
        
        sb.append("<span>key:</span>" + StringUtils.xmlEncodeString(getKey().stringValue()));
        
        sb.append(StringUtils.xmlEncodeString(this.toString()));
        
        return sb.toString();
    }
    
    /**
     * @return the key
     */
    @Override
    public URI getKey()
    {
        return key;
    }
    
    /**
     * @param key
     *            the key to set
     */
    @Override
    public void setKey(String nextKey)
    {
        this.setKey(StringUtils.createURI(nextKey));
    }
    
    @Override
    public void setKey(URI nextKey)
    {
        this.key = nextKey;
    }
    
    /**
     * @return the namespace used to represent objects of this type by default
     */
    @Override
    public String getDefaultNamespace()
    {
        return QueryTypeImpl.defaultNamespace;
    }
    
    /**
     * @return a collection of the relevant element types that are implemented by this class,
     *         including abstract implementations
     */
    @Override
    public Collection<URI> getElementTypes()
    {
        Collection<URI> results = new ArrayList<URI>(1);
        
        results.add(QueryTypeImpl.getQueryTypeUri());
        
        return results;
    }
    
    @Override
    public URI getProfileIncludeExcludeOrder()
    {
        return profileIncludeExcludeOrder;
    }
    
    @Override
    public void setProfileIncludeExcludeOrder(URI profileIncludeExcludeOrder)
    {
        this.profileIncludeExcludeOrder = profileIncludeExcludeOrder;
    }
    
    public static URI getNamespaceMatchAnyUri()
    {
        return QueryTypeImpl.getQueryNamespaceMatchAny();
    }
    
    public static URI getNamespaceMatchAllUri()
    {
        return QueryTypeImpl.getQueryNamespaceMatchAll();
    }
    
    @Override
    public void setCurationStatus(URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }
    
    @Override
    public URI getCurationStatus()
    {
        return curationStatus;
    }
    
    @Override
    public int compareTo(QueryType otherQueryType)
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
        
        return getKey().stringValue().compareTo(otherQueryType.getKey().stringValue());
    }
    
    @Override
    public void addUnrecognisedStatement(Statement unrecognisedStatement)
    {
        unrecognisedStatements.add(unrecognisedStatement);
    }
    
    @Override
    public Collection<Statement> getUnrecognisedStatements()
    {
        return unrecognisedStatements;
    }
    
    @Override
    public Collection<URI> getSemanticallyLinkedQueryTypes()
    {
        return semanticallyLinkedCustomQueries;
    }
    
    @Override
    public void setSemanticallyLinkedQueryTypes(Collection<URI> semanticallyLinkedCustomQueries)
    {
        this.semanticallyLinkedCustomQueries = semanticallyLinkedCustomQueries;
    }
    
    @Override
    public Collection<URI> getNamespacesToHandle()
    {
        return namespacesToHandle;
    }
    
    @Override
    public void setNamespacesToHandle(Collection<URI> namespacesToHandle)
    {
        this.namespacesToHandle = namespacesToHandle;
    }
    
    @Override
    public void addNamespaceToHandle(URI namespaceToHandle)
    {
        if(this.namespacesToHandle == null)
        {
            this.namespacesToHandle = new HashSet<URI>();
        }
        
        this.namespacesToHandle.add(namespaceToHandle);
    }
    
    @Override
    public URI getNamespaceMatchMethod()
    {
        return namespaceMatchMethod;
    }
    
    @Override
    public void setNamespaceMatchMethod(URI namespaceMatchMethod)
    {
        this.namespaceMatchMethod = namespaceMatchMethod;
    }
    
    @Override
    public void setIsNamespaceSpecific(boolean isNamespaceSpecific)
    {
        this.isNamespaceSpecific = isNamespaceSpecific;
    }
    
    @Override
    public boolean getIsNamespaceSpecific()
    {
        return isNamespaceSpecific;
    }
    
    @Override
    public void setIncludeDefaults(boolean includeDefaults)
    {
        this.includeDefaults = includeDefaults;
    }
    
    @Override
    public boolean getIncludeDefaults()
    {
        return includeDefaults;
    }
    
    @Override
    public String getTitle()
    {
        return title;
    }
    
    @Override
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    @Override
    public String getQueryUriTemplateString()
    {
        return queryUriTemplateString;
    }
    
    @Override
    public void setQueryUriTemplateString(String queryUriTemplateString)
    {
        this.queryUriTemplateString = queryUriTemplateString;
    }
    
    @Override
    public String getStandardUriTemplateString()
    {
        return standardUriTemplateString;
    }
    
    @Override
    public void setStandardUriTemplateString(String standardUriTemplateString)
    {
        this.standardUriTemplateString = standardUriTemplateString;
    }
    
    @Override
    public String getTemplateString()
    {
        return templateString;
    }
    
    @Override
    public void setTemplateString(String templateString)
    {
        this.templateString = templateString;
    }
    
    @Override
    public String getOutputRdfXmlString()
    {
        return outputRdfXmlString;
    }
    
    @Override
    public void setOutputRdfXmlString(String outputRdfXmlString)
    {
        this.outputRdfXmlString = outputRdfXmlString;
    }
    
    @Override
    public void setPublicIdentifierIndexes(int[] publicIdentifierIndexes)
    {
        this.publicIdentifierIndexes = publicIdentifierIndexes;
    }
    
    @Override
    public int[] getPublicIdentifierIndexes()
    {
        return publicIdentifierIndexes;
    }
    
    @Override
    public void setNamespaceInputIndexes(int[] namespaceInputIndexes)
    {
        this.namespaceInputIndexes = namespaceInputIndexes;
    }
    
    @Override
    public int[] getNamespaceInputIndexes()
    {
        return namespaceInputIndexes;
    }
    
    @Override
    public boolean getIsPageable()
    {
        return isPageable;
    }
    
    @Override
    public void setIsPageable(boolean isPageable)
    {
        this.isPageable = isPageable;
    }
    
    @Override
    public boolean getInRobotsTxt()
    {
        return inRobotsTxt;
    }
    
    @Override
    public void setInRobotsTxt(boolean inRobotsTxt)
    {
        this.inRobotsTxt = inRobotsTxt;
    }
    
    @Override
    public boolean getHandleAllNamespaces()
    {
        return handleAllNamespaces;
    }
    
    @Override
    public void setHandleAllNamespaces(boolean handleAllNamespaces)
    {
        this.handleAllNamespaces = handleAllNamespaces;
    }
    
    /**
     * @param queryTypeUri
     *            the queryTypeUri to set
     */
    public static void setQueryTypeUri(URI queryTypeUri)
    {
        QueryTypeImpl.queryTypeUri = queryTypeUri;
    }
    
    /**
     * @return the queryTypeUri
     */
    public static URI getQueryTypeUri()
    {
        return QueryTypeImpl.queryTypeUri;
    }
    
    /**
     * @param queryTitle
     *            the queryTitle to set
     */
    public static void setQueryTitle(URI queryTitle)
    {
        QueryTypeImpl.queryTitle = queryTitle;
    }
    
    /**
     * @return the queryTitle
     */
    public static URI getQueryTitle()
    {
        return QueryTypeImpl.queryTitle;
    }
    
    /**
     * @param queryHandleAllNamespaces
     *            the queryHandleAllNamespaces to set
     */
    public static void setQueryHandleAllNamespaces(URI queryHandleAllNamespaces)
    {
        QueryTypeImpl.queryHandleAllNamespaces = queryHandleAllNamespaces;
    }
    
    /**
     * @return the queryHandleAllNamespaces
     */
    public static URI getQueryHandleAllNamespaces()
    {
        return QueryTypeImpl.queryHandleAllNamespaces;
    }
    
    /**
     * @param queryNamespaceToHandle
     *            the queryNamespaceToHandle to set
     */
    public static void setQueryNamespaceToHandle(URI queryNamespaceToHandle)
    {
        QueryTypeImpl.queryNamespaceToHandle = queryNamespaceToHandle;
    }
    
    /**
     * @return the queryNamespaceToHandle
     */
    public static URI getQueryNamespaceToHandle()
    {
        return QueryTypeImpl.queryNamespaceToHandle;
    }
    
    /**
     * @param queryPublicIdentifierIndex
     *            the queryPublicIdentifierIndex to set
     */
    public static void setQueryPublicIdentifierIndex(URI queryPublicIdentifierIndex)
    {
        QueryTypeImpl.queryPublicIdentifierIndex = queryPublicIdentifierIndex;
    }
    
    /**
     * @return the queryPublicIdentifierIndex
     */
    public static URI getQueryPublicIdentifierIndex()
    {
        return QueryTypeImpl.queryPublicIdentifierIndex;
    }
    
    /**
     * @param queryNamespaceInputIndex
     *            the queryNamespaceInputIndex to set
     */
    public static void setQueryNamespaceInputIndex(URI queryNamespaceInputIndex)
    {
        QueryTypeImpl.queryNamespaceInputIndex = queryNamespaceInputIndex;
    }
    
    /**
     * @return the queryNamespaceInputIndex
     */
    public static URI getQueryNamespaceInputIndex()
    {
        return QueryTypeImpl.queryNamespaceInputIndex;
    }
    
    /**
     * @param queryNamespaceMatchMethod
     *            the queryNamespaceMatchMethod to set
     */
    public static void setQueryNamespaceMatchMethod(URI queryNamespaceMatchMethod)
    {
        QueryTypeImpl.queryNamespaceMatchMethod = queryNamespaceMatchMethod;
    }
    
    /**
     * @return the queryNamespaceMatchMethod
     */
    public static URI getQueryNamespaceMatchMethod()
    {
        return QueryTypeImpl.queryNamespaceMatchMethod;
    }
    
    /**
     * @param queryNamespaceSpecific
     *            the queryNamespaceSpecific to set
     */
    public static void setQueryNamespaceSpecific(URI queryNamespaceSpecific)
    {
        QueryTypeImpl.queryNamespaceSpecific = queryNamespaceSpecific;
    }
    
    /**
     * @return the queryNamespaceSpecific
     */
    public static URI getQueryNamespaceSpecific()
    {
        return QueryTypeImpl.queryNamespaceSpecific;
    }
    
    /**
     * @param queryIncludeDefaults
     *            the queryIncludeDefaults to set
     */
    public static void setQueryIncludeDefaults(URI queryIncludeDefaults)
    {
        QueryTypeImpl.queryIncludeDefaults = queryIncludeDefaults;
    }
    
    /**
     * @return the queryIncludeDefaults
     */
    public static URI getQueryIncludeDefaults()
    {
        return QueryTypeImpl.queryIncludeDefaults;
    }
    
    /**
     * @param queryInputRegex
     *            the queryInputRegex to set
     */
    public static void setQueryInputRegex(URI queryInputRegex)
    {
        QueryTypeImpl.queryInputRegex = queryInputRegex;
    }
    
    /**
     * @return the queryInputRegex
     */
    public static URI getQueryInputRegex()
    {
        return QueryTypeImpl.queryInputRegex;
    }
    
    /**
     * @param queryIncludeQueryType
     *            the queryIncludeQueryType to set
     */
    public static void setQueryIncludeQueryType(URI queryIncludeQueryType)
    {
        QueryTypeImpl.queryIncludeQueryType = queryIncludeQueryType;
    }
    
    /**
     * @return the queryIncludeQueryType
     */
    public static URI getQueryIncludeQueryType()
    {
        return QueryTypeImpl.queryIncludeQueryType;
    }
    
    /**
     * @param queryTemplateTerm
     *            the queryTemplateTerm to set
     */
    public static void setQueryTemplateTerm(URI queryTemplateTerm)
    {
        QueryTypeImpl.queryTemplateTerm = queryTemplateTerm;
    }
    
    /**
     * @return the queryTemplateTerm
     */
    public static URI getQueryTemplateTerm()
    {
        return QueryTypeImpl.queryTemplateTerm;
    }
    
    /**
     * @param queryParameterTemplateTerm
     *            the queryParameterTemplateTerm to set
     */
    public static void setQueryParameterTemplateTerm(URI queryParameterTemplateTerm)
    {
        QueryTypeImpl.queryParameterTemplateTerm = queryParameterTemplateTerm;
    }
    
    /**
     * @return the queryParameterTemplateTerm
     */
    public static URI getQueryParameterTemplateTerm()
    {
        return QueryTypeImpl.queryParameterTemplateTerm;
    }
    
    /**
     * @param queryStaticOutputTemplateTerm
     *            the queryStaticOutputTemplateTerm to set
     */
    public static void setQueryStaticOutputTemplateTerm(URI queryStaticOutputTemplateTerm)
    {
        QueryTypeImpl.queryStaticOutputTemplateTerm = queryStaticOutputTemplateTerm;
    }
    
    /**
     * @return the queryStaticOutputTemplateTerm
     */
    public static URI getQueryStaticOutputTemplateTerm()
    {
        return QueryTypeImpl.queryStaticOutputTemplateTerm;
    }
    
    /**
     * @param queryInRobotsTxt
     *            the queryInRobotsTxt to set
     */
    public static void setQueryInRobotsTxt(URI queryInRobotsTxt)
    {
        QueryTypeImpl.queryInRobotsTxt = queryInRobotsTxt;
    }
    
    /**
     * @return the queryInRobotsTxt
     */
    public static URI getQueryInRobotsTxt()
    {
        return QueryTypeImpl.queryInRobotsTxt;
    }
    
    /**
     * @param queryIsPageable
     *            the queryIsPageable to set
     */
    public static void setQueryIsPageable(URI queryIsPageable)
    {
        QueryTypeImpl.queryIsPageable = queryIsPageable;
    }
    
    /**
     * @return the queryIsPageable
     */
    public static URI getQueryIsPageable()
    {
        return QueryTypeImpl.queryIsPageable;
    }
    
    /**
     * @param queryNamespaceMatchAny
     *            the queryNamespaceMatchAny to set
     */
    public static void setQueryNamespaceMatchAny(URI queryNamespaceMatchAny)
    {
        QueryTypeImpl.queryNamespaceMatchAny = queryNamespaceMatchAny;
    }
    
    /**
     * @return the queryNamespaceMatchAny
     */
    public static URI getQueryNamespaceMatchAny()
    {
        return QueryTypeImpl.queryNamespaceMatchAny;
    }
    
    /**
     * @param queryNamespaceMatchAll
     *            the queryNamespaceMatchAll to set
     */
    public static void setQueryNamespaceMatchAll(URI queryNamespaceMatchAll)
    {
        QueryTypeImpl.queryNamespaceMatchAll = queryNamespaceMatchAll;
    }
    
    /**
     * @return the queryNamespaceMatchAll
     */
    public static URI getQueryNamespaceMatchAll()
    {
        return QueryTypeImpl.queryNamespaceMatchAll;
    }
    
    @Override
    public boolean getIsDummyQueryType()
    {
        return this.isDummyQueryType;
    }
    
    @Override
    public void setIsDummyQueryType(boolean isDummyQueryType)
    {
        this.isDummyQueryType = isDummyQueryType;
    }
    
    /**
     * @param queryIsDummyQueryType
     *            the queryIsDummyQueryType to set
     */
    public static void setQueryIsDummyQueryType(URI queryIsDummyQueryType)
    {
        QueryTypeImpl.queryIsDummyQueryType = queryIsDummyQueryType;
    }
    
    /**
     * @return the queryIsDummyQueryType
     */
    public static URI getQueryIsDummyQueryType()
    {
        return QueryTypeImpl.queryIsDummyQueryType;
    }
    
    @Override
    public boolean isUsedWithProfileList(List<Profile> orderedProfileList, boolean allowImplicitInclusions,
            boolean includeNonProfileMatched)
    {
        return ProfileUtils.isUsedWithProfileList(this, orderedProfileList, allowImplicitInclusions,
                includeNonProfileMatched);
    }
    
    /**
     * @return the queryTemplateString
     */
    public static URI getQueryTemplateString()
    {
        return QueryTypeImpl.queryTemplateString;
    }
    
    /**
     * @param queryTemplateString
     *            the queryTemplateString to set
     */
    public static void setQueryTemplateString(URI queryTemplateString)
    {
        QueryTypeImpl.queryTemplateString = queryTemplateString;
    }
    
    /**
     * @return the queryQueryUriTemplateString
     */
    public static URI getQueryQueryUriTemplateString()
    {
        return QueryTypeImpl.queryQueryUriTemplateString;
    }
    
    /**
     * @param queryQueryUriTemplateString
     *            the queryQueryUriTemplateString to set
     */
    public static void setQueryQueryUriTemplateString(URI queryQueryUriTemplateString)
    {
        QueryTypeImpl.queryQueryUriTemplateString = queryQueryUriTemplateString;
    }
    
    /**
     * @return the queryStandardUriTemplateString
     */
    public static URI getQueryStandardUriTemplateString()
    {
        return QueryTypeImpl.queryStandardUriTemplateString;
    }
    
    /**
     * @param queryStandardUriTemplateString
     *            the queryStandardUriTemplateString to set
     */
    public static void setQueryStandardUriTemplateString(URI queryStandardUriTemplateString)
    {
        QueryTypeImpl.queryStandardUriTemplateString = queryStandardUriTemplateString;
    }
    
    /**
     * @return the queryOutputRdfXmlString
     */
    public static URI getQueryOutputRdfXmlString()
    {
        return QueryTypeImpl.queryOutputRdfXmlString;
    }
    
    /**
     * @param queryOutputRdfXmlString
     *            the queryOutputRdfXmlString to set
     */
    public static void setQueryOutputRdfXmlString(URI queryOutputRdfXmlString)
    {
        QueryTypeImpl.queryOutputRdfXmlString = queryOutputRdfXmlString;
    }
}
