package org.queryall.impl;

import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Literal;
import org.openrdf.model.ValueFactory;

import org.openrdf.OpenRDFException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryConnection;

import java.util.regex.Pattern;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.queryall.queryutils.ProvenanceRecord;
import org.queryall.api.Profile;
import org.queryall.api.QueryType;
import org.queryall.helpers.*;

import org.apache.log4j.Logger;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class QueryTypeImpl extends QueryType
{
    private static final Logger log = Logger.getLogger(QueryType.class.getName());
    private static final boolean _TRACE = log.isTraceEnabled();
    private static final boolean _DEBUG = log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = log.isInfoEnabled();
    
    private static final String defaultNamespace = Settings.getSettings().getNamespaceForQueryType();
    
    private Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    private URI key;
    private String title = "";
    private URI curationStatus = ProjectImpl.getProjectNotCuratedUri();
    
    private boolean handleAllNamespaces = true;
    // If present, this is a list of namespaces which can be handled by this type of custom query (or at least this form of it), unless handleAllNamespaces is true, in which case any namespace can be present here without effect
    private Collection<URI> namespacesToHandle = new HashSet<URI>();
    // if a query is not namepsace specific it can be executed across providers which do not necessarily handle this namespace, but are not necessarily defaults per se
    private boolean isNamespaceSpecific = false;
    // these are the input_NN indexes that are either namespaces, or correspond to public identifiers that are not untouchable internal private identifiers
    // among other things, it can be used to make sure that these are lowercased per a given policy (in this case the Banff Manifesto)
    private int[] publicIdentifierIndexes;
    // these are the input_NN indexes that we will use to determine which namespace providers to perform this query using
    private int[] namespaceInputIndexes;
    // This is the method by which we determine whether any or all of the namespaces are required on a particular endpoint before we utilise it
    // if defaults are included in this query then we will always use default providers regardless of the namespaces they have declared on them
    // if we do not use all namespaces then this setting will still be in effect, but it will first match against the list that we do handle before getting to the provider choice stage
    // For example, if we match inputs 1 and 2 as namespaceInputIndexes, and we have the the namespaceMatchMethod set to QueryType.queryNamespaceMatchAll.stringValue(), and we do not handle all namespaces and inputs 1 and 2 both exist in namespacesToHandle then we will satisfy the initial test for query usability
    // Possible values are QueryType.queryNamespaceMatchAll.stringValue() and QueryType.queryNamespaceMatchAny.stringValue()
    private URI namespaceMatchMethod = QueryTypeImpl.getQueryNamespaceMatchAny();
    // if we are told we can include defaults, even if we are known to be namespace specific we can utilise the default providers as sources
    private boolean includeDefaults = true;
    // if this query can be paged using the pageoffsetNN mechanism, this should be true, and otherwise it should be false
    private boolean isPageable = false;
    // If this query is restricted by any of the robots.txt entries than declare that here, so that automatic bot detection is functional for this query
    private boolean inRobotsTxt = false;
    // use this to define which additional custom query rdf triples to add to a particular type of custom query
    // a typical use for this is for adding links and index triples to construct,index,links etc type queries, but not to others for instance
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
    public static URI OLDqueryTemplateString;
    public static URI OLDqueryQueryUriTemplateString;
    public static URI OLDqueryStandardUriTemplateString;
    public static URI OLDqueryOutputRdfXmlString;
    
    private static URI queryTemplateTerm;
    private static URI queryParameterTemplateTerm;
    private static URI queryStaticOutputTemplateTerm;
    
    private static URI queryInRobotsTxt;
    private static URI queryIsPageable;
    private static URI queryIsDummyQueryType;
    private static URI queryNamespaceMatchAny;
    private static URI queryNamespaceMatchAll;
    
    public static String queryNamespace;
    
    @SuppressWarnings("unused")
	public QueryTypeImpl(Collection<Statement> inputStatements, URI keyToUse, int modelVersion) throws OpenRDFException
	{
        // TEMPORARY
        // This is just used to assign unique identifiers to each of the included templates which previously did not have identifiers
        int templatecounter = 0;
        int parametercounter = 0;
        int outputcounter = 0;
        int includecounter = 0;
        
        Collection<URI> tempNamespacesToHandle = new HashSet<URI>();
        Collection<Integer> tempPublicIdentifierIndexes = new HashSet<Integer>();
        Collection<Integer> tempNamespaceInputIndexes = new HashSet<Integer>();
        Collection<URI> tempIncludedQueryTemplates = new HashSet<URI>();
        Collection<URI> tempIncludedQueryParameters = new HashSet<URI>();
        Collection<URI> tempIncludedStaticOutputTemplates = new HashSet<URI>();
        
        Collection<URI> tempsemanticallyLinkedCustomQueries = new HashSet<URI>();
        
        for(Statement nextStatement : inputStatements)
        {
            if(_DEBUG)
            {
                log.debug("QueryType: nextStatement: "+nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE) && nextStatement.getObject().equals(getQueryTypeUri()))
            {
                if(_TRACE)
                {
                    log.trace("QueryType: found valid type predicate for URI: "+keyToUse);
                }
                
                this.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(ProjectImpl.getProjectCurationStatusUri()))
            {
                this.setCurationStatus((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getQueryTitle()) || nextStatement.getPredicate().equals(Constants.DC_TITLE))
            {
                this.setTitle(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(getQueryHandleAllNamespaces()))
            {
                this.setHandleAllNamespaces(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(getQueryNamespaceToHandle()))
            {
                tempNamespacesToHandle.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getQueryPublicIdentifierIndex()))
            {
                tempPublicIdentifierIndexes.add(RdfUtils.getIntegerFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(getQueryNamespaceInputIndex()))
            {
                tempNamespaceInputIndexes.add(RdfUtils.getIntegerFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(getQueryNamespaceMatchMethod()))
            {
                this.setNamespaceMatchMethod((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(getQueryNamespaceSpecific()))
            {
                this.setIsNamespaceSpecific(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(getQueryIncludeDefaults()))
            {
                this.setIncludeDefaults(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(getQueryInputRegex()))
            {
                this.setInputRegex(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(getQueryIncludeQueryType()))
            {
                tempsemanticallyLinkedCustomQueries.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(OLDqueryTemplateString))
            {
                this.setTemplateString(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(OLDqueryQueryUriTemplateString))
            {
                this.setQueryUriTemplateString(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(OLDqueryStandardUriTemplateString))
            {
                this.setStandardUriTemplateString(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(OLDqueryOutputRdfXmlString))
            {
                this.setOutputRdfXmlString(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(getQueryInRobotsTxt()))
            {
                this.setInRobotsTxt(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(getQueryIsPageable()))
            {
                this.setIsPageable(RdfUtils.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(getQueryIsDummyQueryType()))
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
        this.setPublicIdentifierIndexes(ListUtils.getIntArrayFromArrayInteger(tempPublicIdentifierIndexes.toArray(new Integer[0])));
        this.setNamespaceInputIndexes(ListUtils.getIntArrayFromArrayInteger(tempNamespaceInputIndexes.toArray(new Integer[0])));
        
        this.setSemanticallyLinkedQueryTypes(tempsemanticallyLinkedCustomQueries);
        
        if(_DEBUG)
        {
            log.debug("QueryType.fromRdf: would have returned... keyToUse="+keyToUse+" result="+this.toString());
        }
    }

	public QueryTypeImpl()
	{
		// TODO Auto-generated constructor stub
	}

	public String getInputRegex()
    {
        return inputRegex;
    }
    
    public Pattern getInputRegexPattern()
    {
        if(inputRegexPattern == null && inputRegex != null)
            inputRegexPattern = Pattern.compile(inputRegex);
            
        return inputRegexPattern;
    }
    
    public void setInputRegex(String nextInputRegex)
    {
        inputRegex = nextInputRegex;
        inputRegexPattern = Pattern.compile(nextInputRegex);
    }
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        queryNamespace = Settings.getSettings().getOntologyTermUriPrefix()
                         +Settings.getSettings().getNamespaceForQueryType()
                         +Settings.getSettings().getOntologyTermUriSuffix();
                         
        setQueryTypeUri(f.createURI(queryNamespace,"Query"));
        setQueryTitle(f.createURI(queryNamespace,"title"));
        setQueryHandleAllNamespaces(f.createURI(queryNamespace,"handleAllNamespaces"));
        setQueryNamespaceToHandle(f.createURI(queryNamespace,"namespaceToHandle"));
        setQueryPublicIdentifierIndex(f.createURI(queryNamespace,"hasPublicIdentifierIndex"));
        setQueryNamespaceInputIndex(f.createURI(queryNamespace,"hasNamespaceInputIndex"));
        setQueryNamespaceMatchMethod(f.createURI(queryNamespace,"namespaceMatchMethod"));
        setQueryNamespaceSpecific(f.createURI(queryNamespace,"isNamespaceSpecific"));
        setQueryIncludeDefaults(f.createURI(queryNamespace,"includeDefaults"));
        setQueryIncludeQueryType(f.createURI(queryNamespace,"includeQueryType"));
        setQueryInputRegex(f.createURI(queryNamespace,"inputRegex"));
        OLDqueryTemplateString = f.createURI(queryNamespace,"templateString");
        OLDqueryQueryUriTemplateString = f.createURI(queryNamespace,"queryUriTemplateString");
        OLDqueryStandardUriTemplateString = f.createURI(queryNamespace,"standardUriTemplateString");
        OLDqueryOutputRdfXmlString = f.createURI(queryNamespace,"outputRdfXmlString");
        setQueryInRobotsTxt(f.createURI(queryNamespace,"inRobotsTxt"));
        setQueryIsPageable(f.createURI(queryNamespace,"isPageable"));
        setQueryNamespaceMatchAny(f.createURI(queryNamespace,"namespaceMatchAny"));
        setQueryNamespaceMatchAll(f.createURI(queryNamespace,"namespaceMatchAll"));
        setQueryTemplateTerm(f.createURI(queryNamespace,"includedQueryTemplate"));
        setQueryParameterTemplateTerm(f.createURI(queryNamespace,"includedQueryParameterTemplate"));
        setQueryStaticOutputTemplateTerm(f.createURI(queryNamespace,"includedStaticOutputTemplate"));
        setQueryIsDummyQueryType(f.createURI(queryNamespace,"isDummyQueryType"));
    }
    
    // returns true if the input variable is in the list of public input variables
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
    
    public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            // create some resources and literals to make statements out of
            URI queryInstanceUri = this.getKey();
            
            /***
                title
                handleAllNamespaces
                isNamespaceSpecific
                namespaceMatchMethod
                includeDefaults
                inputRegex
                templateString
                queryUriTemplateString
                standardUriTemplateString
                outputRdfXmlString
                inRobotsTxt
                profileIncludeExcludeOrder
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
            
            con.add(queryInstanceUri, RDF.TYPE, getQueryTypeUri(), keyToUse);
            con.add(queryInstanceUri, ProjectImpl.getProjectCurationStatusUri(), curationStatusLiteral, keyToUse);
            if(modelVersion == 1)
            {
                con.add(queryInstanceUri, getQueryTitle(), titleLiteral, keyToUse);
            }
            else
            {
                con.add(queryInstanceUri, Constants.DC_TITLE, titleLiteral, keyToUse);
            }
            
            con.add(queryInstanceUri, getQueryHandleAllNamespaces(), handleAllNamespacesLiteral, keyToUse);
            con.add(queryInstanceUri, getQueryNamespaceSpecific(), isNamespaceSpecificLiteral, keyToUse);
            con.add(queryInstanceUri, getQueryNamespaceMatchMethod(), namespaceMatchMethodLiteral, keyToUse);
            con.add(queryInstanceUri, getQueryIncludeDefaults(), includeDefaultsLiteral, keyToUse);
            con.add(queryInstanceUri, getQueryInputRegex(), inputRegexLiteral, keyToUse);
            con.add(queryInstanceUri, OLDqueryTemplateString, templateStringLiteral, keyToUse);
            con.add(queryInstanceUri, OLDqueryQueryUriTemplateString, queryUriTemplateStringLiteral, keyToUse);
            con.add(queryInstanceUri, OLDqueryStandardUriTemplateString, standardUriTemplateStringLiteral, keyToUse);
            con.add(queryInstanceUri, OLDqueryOutputRdfXmlString, outputRdfXmlStringLiteral, keyToUse);
            con.add(queryInstanceUri, getQueryInRobotsTxt(), inRobotsTxtLiteral, keyToUse);
            con.add(queryInstanceUri, getQueryIsPageable(), isPageableLiteral, keyToUse);
            con.add(queryInstanceUri, getQueryIsDummyQueryType(), isDummyQueryTypeLiteral, keyToUse);
            
            con.add(queryInstanceUri, ProfileImpl.getProfileIncludeExcludeOrderUri(), profileIncludeExcludeOrderLiteral, keyToUse);
            
            // log.info("after single URIs created");

            if(namespacesToHandle != null)
            {
            
                for(URI nextNamespaceToHandle : namespacesToHandle)
                {
                    if(nextNamespaceToHandle != null)
                    {
                        con.add(queryInstanceUri, getQueryNamespaceToHandle(), nextNamespaceToHandle, keyToUse);
                    }
                }
            }
            
            if(publicIdentifierIndexes != null)
            {
            
                for(int nextPublicIdentifierIndex : publicIdentifierIndexes)
                {
                    con.add(queryInstanceUri, getQueryPublicIdentifierIndex(), f.createLiteral(nextPublicIdentifierIndex), keyToUse);
                }
            }
            
            if(namespaceInputIndexes != null)
            {
                for(int nextNamespaceInputIndex : namespaceInputIndexes)
                {
                    con.add(queryInstanceUri, getQueryNamespaceInputIndex(), f.createLiteral(nextNamespaceInputIndex), keyToUse);
                }
            }
            
            if(semanticallyLinkedCustomQueries != null)
            {
                for(URI nextSemanticallyLinkedQueryType : semanticallyLinkedCustomQueries)
                {
                    if(nextSemanticallyLinkedQueryType != null)
                    {
                        con.add(queryInstanceUri, getQueryIncludeQueryType(), nextSemanticallyLinkedQueryType, keyToUse);
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
        catch (RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            
            if(con != null)
                con.rollback();
                
            log.error("RepositoryException: "+re.getMessage());
        }
        finally
        {
            if(con != null)
                con.close();
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
            
            con.add(getQueryTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);
            
            if(modelVersion == 1)
            {
                con.add(getQueryTitle(), RDF.TYPE, OWL.DEPRECATEDPROPERTY, contextKeyUri);
                con.add(getQueryTitle(), RDFS.SUBPROPERTYOF, Constants.DC_TITLE, contextKeyUri);
            }
            
            con.add(getQueryNamespaceToHandle(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(getQueryNamespaceToHandle(), RDFS.RANGE, NamespaceEntryImpl.getNamespaceTypeUri(), contextKeyUri);
            con.add(getQueryNamespaceToHandle(), RDFS.DOMAIN, getQueryTypeUri(), contextKeyUri);
            con.add(getQueryNamespaceToHandle(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(getQueryIncludeQueryType(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(getQueryIncludeQueryType(), RDFS.RANGE, QueryTypeImpl.getQueryTypeUri(), contextKeyUri);
            con.add(getQueryIncludeQueryType(), RDFS.DOMAIN, getQueryTypeUri(), contextKeyUri);
            con.add(getQueryIncludeQueryType(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);

            con.add(getQueryNamespaceMatchMethod(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(getQueryNamespaceMatchMethod(), RDFS.RANGE, RDFS.RESOURCE, contextKeyUri);
            con.add(getQueryNamespaceMatchMethod(), RDFS.DOMAIN, getQueryTypeUri(), contextKeyUri);
            con.add(getQueryNamespaceMatchMethod(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(getQueryHandleAllNamespaces(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getQueryHandleAllNamespaces(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(getQueryHandleAllNamespaces(), RDFS.DOMAIN, getQueryTypeUri(), contextKeyUri);
            con.add(getQueryHandleAllNamespaces(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            
            con.add(getQueryNamespaceSpecific(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getQueryNamespaceSpecific(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(getQueryNamespaceSpecific(), RDFS.DOMAIN, getQueryTypeUri(), contextKeyUri);
            con.add(getQueryNamespaceSpecific(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            
            con.add(getQueryIncludeDefaults(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getQueryIncludeDefaults(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(getQueryIncludeDefaults(), RDFS.DOMAIN, getQueryTypeUri(), contextKeyUri);
            con.add(getQueryIncludeDefaults(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            
            con.add(getQueryInputRegex(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getQueryInputRegex(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(getQueryInputRegex(), RDFS.DOMAIN, getQueryTypeUri(), contextKeyUri);
            con.add(getQueryInputRegex(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            
            con.add(getQueryInRobotsTxt(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getQueryInRobotsTxt(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(getQueryInRobotsTxt(), RDFS.DOMAIN, getQueryTypeUri(), contextKeyUri);
            con.add(getQueryInRobotsTxt(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            
            con.add(getQueryIsPageable(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getQueryIsPageable(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(getQueryIsPageable(), RDFS.DOMAIN, getQueryTypeUri(), contextKeyUri);
            con.add(getQueryIsPageable(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            
            con.add(getQueryIsDummyQueryType(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getQueryIsDummyQueryType(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(getQueryIsDummyQueryType(), RDFS.DOMAIN, getQueryTypeUri(), contextKeyUri);
            con.add(getQueryIsDummyQueryType(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            

            con.add(getQueryPublicIdentifierIndex(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getQueryPublicIdentifierIndex(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(getQueryPublicIdentifierIndex(), RDFS.DOMAIN, getQueryTypeUri(), contextKeyUri);
            con.add(getQueryPublicIdentifierIndex(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            
            con.add(getQueryNamespaceInputIndex(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(getQueryNamespaceInputIndex(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(getQueryNamespaceInputIndex(), RDFS.DOMAIN, getQueryTypeUri(), contextKeyUri);
            con.add(getQueryNamespaceInputIndex(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            
            con.add(OLDqueryTemplateString, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(OLDqueryTemplateString, RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(OLDqueryTemplateString, RDFS.DOMAIN, getQueryTypeUri(), contextKeyUri);
            con.add(OLDqueryTemplateString, RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            
            con.add(OLDqueryQueryUriTemplateString, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(OLDqueryQueryUriTemplateString, RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(OLDqueryQueryUriTemplateString, RDFS.DOMAIN, getQueryTypeUri(), contextKeyUri);
            con.add(OLDqueryQueryUriTemplateString, RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            
            con.add(OLDqueryStandardUriTemplateString, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(OLDqueryStandardUriTemplateString, RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(OLDqueryStandardUriTemplateString, RDFS.DOMAIN, getQueryTypeUri(), contextKeyUri);
            con.add(OLDqueryStandardUriTemplateString, RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            
            con.add(OLDqueryOutputRdfXmlString, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(OLDqueryOutputRdfXmlString, RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(OLDqueryOutputRdfXmlString, RDFS.DOMAIN, getQueryTypeUri(), contextKeyUri);
            con.add(OLDqueryOutputRdfXmlString, RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            // If everything went as planned, we can commit the result
            con.commit();
            
            return true;
        }
        catch (RepositoryException re)
        {
            // Something went wrong during the transaction, so we roll it back
            
            if(con != null)
                con.rollback();
                
            log.error("RepositoryException: "+re.getMessage());
        }
        finally
        {
            if(con != null)
                con.close();
        }
        
        return false;
    }
    
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append("key="+key+"\n");
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
    
    public String toHtmlFormBody()
    {
        StringBuilder sb = new StringBuilder();
        
        String prefix = "query_";
        
        if(getKey() != null)
        {
            sb.append("<div class=\""+prefix+"key_div\"><input type=\"hidden\" name=\"key\" value=\""+StringUtils.xmlEncodeString(getKey().stringValue())+"\" /></div>\n");
        }
        
        sb.append("<div class=\""+prefix+"title_div\"><span class=\""+prefix+"title_span\">Title:</span><input type=\"text\" name=\""+prefix+"title\" value=\""+StringUtils.xmlEncodeString(title)+"\" /></div>\n");
        
        sb.append("<div class=\""+prefix+"templateString_div\"><span class=\""+prefix+"templateString_span\">Query Template:</span><input type=\"text\" name=\""+prefix+"templateString\" value=\""+StringUtils.xmlEncodeString(templateString)+"\" /></div>\n");
        sb.append("<div class=\""+prefix+"standardUriTemplateString_div\"><span class=\""+prefix+"standardUriTemplateString_span\">Standard URI Template:</span><input type=\"text\" name=\""+prefix+"standardUriTemplateString\" value=\""+StringUtils.xmlEncodeString(standardUriTemplateString)+"\" /></div>\n");
        sb.append("<div class=\""+prefix+"queryUriTemplateString_div\"><span class=\""+prefix+"queryUriTemplateString_span\">Query URI Template:</span><input type=\"text\" name=\""+prefix+"queryUriTemplateString\" value=\""+StringUtils.xmlEncodeString(queryUriTemplateString)+"\" /></div>\n");
        sb.append("<div class=\""+prefix+"outputRdfXmlString_div\"><span class=\""+prefix+"outputRdfXmlString_span\">Static output RDF/XML Template:</span><input type=\"text\" name=\""+prefix+"outputRdfXmlString\" value=\""+StringUtils.xmlEncodeString(outputRdfXmlString)+"\" /></div>\n");
        
        sb.append("<div class=\""+prefix+"inputRegex_div\"><span class=\""+prefix+"inputRegex_span\">Input Regular Expression:</span><input type=\"text\" name=\""+prefix+"inputRegex\" value=\""+StringUtils.xmlEncodeString(inputRegex)+"\" /></div>\n");
        
        sb.append("<div class=\""+prefix+"isNamespaceSpecific_div\"><span class=\""+prefix+"isNamespaceSpecific_span\">Is Namespace Specific:</span><input type=\"checkbox\" name=\""+prefix+"isNamespaceSpecific\" value=\"isNamespaceSpecific\" ");
        
        if(isNamespaceSpecific)
        {
            sb.append(" checked=\"checked\" ");
        }
        
        sb.append(" /></div>\n");
        
        sb.append("<div class=\""+prefix+"handleAllNamespaces_div\"><span class=\""+prefix+"handleAllNamespaces_span\">Handle All Namespaces:</span><input type=\"checkbox\" name=\""+prefix+"handleAllNamespaces\" value=\"handleAllNamespaces\" ");
        
        if(handleAllNamespaces)
        {
            sb.append(" checked=\"checked\" ");
        }
        
        sb.append(" /></div>\n");
        
        sb.append("<div class=\""+prefix+"namespaceMatchMethod_div\"><span class=\""+prefix+"namespaceMatchMethod_span\">All namespaces must match?:</span><input type=\"checkbox\" name=\""+prefix+"namespaceMatchMethod\" value=\"namespaceMatchMethod\" ");
        
        if(namespaceMatchMethod.equals(QueryTypeImpl.getQueryNamespaceMatchAll()))
        {
            sb.append(" checked=\"checked\" ");
        }
        
        sb.append(" /></div>\n");
        
        sb.append("<div class=\""+prefix+"profileIncludeExcludeOrder_div\"><span class=\""+prefix+"profileIncludeExcludeOrder_span\">Use by default on all profiles?:</span><input type=\"checkbox\" name=\""+prefix+"profileIncludeExcludeOrder\" value=\"profileIncludeExcludeOrder\" ");
        
        if(profileIncludeExcludeOrder.equals(ProfileImpl.getExcludeThenIncludeUri()))
        {
            sb.append(" checked=\"checked\" ");
        }
        
        sb.append(" /></div>\n");
        
        sb.append("<div class=\""+prefix+"includeDefaults_div\"><span class=\""+prefix+"includeDefaults_span\">Include Default Providers that support this query but possibly not the defined namespaces?:</span><input type=\"checkbox\" name=\""+prefix+"includeDefaults\" value=\"includeDefaults\" ");
        
        if(includeDefaults)
        {
            sb.append(" checked=\"checked\" ");
        }
        
        sb.append(" /></div>\n");
        
        sb.append("<div class=\""+prefix+"inRobotsTxt_div\"><span class=\""+prefix+"inRobotsTxt_span\">Is this query restricted by Robots.txt?:</span><input type=\"checkbox\" name=\""+prefix+"inRobotsTxt\" value=\"inRobotsTxt\" ");
        
        if(inRobotsTxt)
        {
            sb.append(" checked=\"checked\" ");
        }
        
        sb.append(" /></div>\n");
        
        
        return sb.toString();
    }
    
    public List<String> matchesForQueryString(String nextQueryString)
    {
        return StringUtils.matchesForRegexOnString(getInputRegexPattern(), this.inputRegex, nextQueryString);
    }
    
	@Override
	public boolean matchesQueryString(String nextQueryString) 
	{
        return StringUtils.matchesRegexOnString(getInputRegexPattern(), this.inputRegex, nextQueryString);
	}    
	
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
    
    public boolean handlesNamespacesSpecifically(Collection<Collection<URI>> namespacesToCheck)
    {
        if(!isNamespaceSpecific || namespacesToHandle == null || namespacesToCheck == null)
        {
            return false;
        }
        
        if(_DEBUG)
        {
            log.debug("QueryType.handlesNamespacesSpecifically: starting to compute match for this.getKey()="+this.getKey()+" namespacesToHandle="+namespacesToHandle+" namespacesToCheck="+namespacesToCheck);
        }
        
        // Starting presumptions like this make the algorithm implementation simpler
        boolean anyMatched = false;
        
        boolean allMatched = true;
        
        // for each of the namespaces to check (represented by one or more URI's),
        //      check that we have a locally handled namespace URI that matches
        //             one of the URI's in each of the list of namespaces to check
        
        for(Collection<URI> nextNamespaceToCheckList : namespacesToCheck)
        {
            if(nextNamespaceToCheckList == null)
            {
                if(_DEBUG)
                {
                    log.debug("QueryType.handlesNamespacesSpecifically: nextNamespaceToCheckList was null");
                }
                
                continue;
            }
            
            boolean matchFound = false;
            
            for(URI nextLocalNamespace : namespacesToHandle)
            {
                if(nextLocalNamespace == null)
                {
                    if(_DEBUG)
                    {
                        log.debug("QueryType.handlesNamespacesSpecifically: nextLocalNamespace was null or empty string");
                    }
                    
                    continue;
                }
                
                
                for(URI nextNamespaceToCheck : nextNamespaceToCheckList)
                {
                    if(nextNamespaceToCheck.equals(nextLocalNamespace))
                    {
                        if(_DEBUG)
                        {
                            log.debug("QueryType.handlesNamespacesSpecifically: found match nextNamespaceToCheck="+nextNamespaceToCheck+" this.getKey()="+this.getKey());
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
                    if(_DEBUG)
                    {
                        log.debug("QueryType.handlesNamespacesSpecifically: any match confirmed this.getKey()="+this.getKey());
                    }
                    
                    break;
                }
            }
            else
            {
                allMatched = false;
                
                if(namespaceMatchMethod.equals(QueryTypeImpl.getQueryNamespaceMatchAll()))
                {
                    if(_DEBUG)
                    {
                        log.debug("QueryType.handlesNamespacesSpecifically: all match disproved this.getKey()="+this.getKey());
                    }
                    
                    break;
                }
            }
        }
        
        if(namespaceMatchMethod.equals(QueryTypeImpl.getQueryNamespaceMatchAny()))
        {
            if(_DEBUG)
            {
                if(anyMatched)
                {
                    log.debug("QueryType.handlesNamespacesSpecifically: any match return value true");
                }
                else
                {
                    log.debug("QueryType.handlesNamespacesSpecifically: any match return value false");
                }
            }
            
            return anyMatched;
        }
        else if(namespaceMatchMethod.equals(QueryTypeImpl.getQueryNamespaceMatchAll()))
        {
            if(_DEBUG)
            {
                if(allMatched)
                {
                    log.debug("QueryType.handlesNamespacesSpecifically: all match return value true");
                }
                else
                {
                    log.debug("QueryType.handlesNamespacesSpecifically: all match return value false");
                }
            }
            
            return allMatched;
        }
        else
        {
            log.error("Could not recognise the namespaceMatchMethod="+namespaceMatchMethod);
            
            throw new RuntimeException("Could not recognise the namespaceMatchMethod="+namespaceMatchMethod);
        }
    }
    
    public String toHtml()
    {
        StringBuilder sb = new StringBuilder();
        
        @SuppressWarnings("unused")
        String prefix = "query_";
        
        sb.append("<span>key:</span>"+StringUtils.xmlEncodeString(getKey().stringValue()));
        
        sb.append(StringUtils.xmlEncodeString(this.toString()));
        
        return sb.toString();
    }
    
    /**
     * @return the key
     */
    public URI getKey()
    {
        return key;
    }
    
    /**
     * @param key the key to set
     */
    public void setKey(String nextKey)
    {
        this.setKey(StringUtils.createURI(nextKey));
    }

    public void setKey(URI nextKey)
    {
        this.key = nextKey;
    }    
    /**
     * @return the namespace used to represent objects of this type by default
     */
    public String getDefaultNamespace()
    {
        return defaultNamespace;
    }

    /**
     * @return the URI used for the rdf Type of these elements
     */
    public URI getElementType()
    {
        return getQueryTypeUri();
    }
    
    public URI getProfileIncludeExcludeOrder()
    {
        return profileIncludeExcludeOrder;
    }

    public void setProfileIncludeExcludeOrder(URI profileIncludeExcludeOrder)
    {
        this.profileIncludeExcludeOrder = profileIncludeExcludeOrder;
    }

    public static URI getNamespaceMatchAnyUri()
    {
        return getQueryNamespaceMatchAny();
    }
    
    public static URI getNamespaceMatchAllUri()
    {
        return getQueryNamespaceMatchAll();
    }

    public void setCurationStatus(URI curationStatus)
    {
        this.curationStatus = curationStatus;
    }
    
    public URI getCurationStatus()
    {
        return curationStatus;
    }
    
    public int compareTo(QueryType otherQueryType)
    {
        @SuppressWarnings("unused")
        final int BEFORE = -1;
        final int EQUAL = 0;
        @SuppressWarnings("unused")
        final int AFTER = 1;
    
        if ( this == otherQueryType ) 
            return EQUAL;
    
        return getKey().stringValue().compareTo(otherQueryType.getKey().stringValue());
    }
    
    
    public void addUnrecognisedStatement(Statement unrecognisedStatement)
    {
        unrecognisedStatements.add(unrecognisedStatement);
    }

    public Collection<Statement> getUnrecognisedStatements()
    {
        return unrecognisedStatements;
    }
    
    public Collection<URI> getSemanticallyLinkedQueryTypes()
    {
        return semanticallyLinkedCustomQueries;
    }

    public void setSemanticallyLinkedQueryTypes(Collection<URI> semanticallyLinkedCustomQueries)
    {
        this.semanticallyLinkedCustomQueries = semanticallyLinkedCustomQueries;
    }

    public Collection<URI> getNamespacesToHandle()
    {
        return namespacesToHandle;
    }

    public void setNamespacesToHandle(Collection<URI> namespacesToHandle)
    {
        this.namespacesToHandle = namespacesToHandle;
    }

    public void addNamespaceToHandle(URI namespaceToHandle)
    {
        if(this.namespacesToHandle == null)
        {
            this.namespacesToHandle = new HashSet<URI>();
        }
        
        this.namespacesToHandle.add(namespaceToHandle);
    }
        
    public URI getNamespaceMatchMethod()
    {
        return namespaceMatchMethod;
    }
    
    public void setNamespaceMatchMethod(URI namespaceMatchMethod)
    {
        this.namespaceMatchMethod = namespaceMatchMethod;
    }
    
    public void setIsNamespaceSpecific(boolean isNamespaceSpecific)
    {
        this.isNamespaceSpecific = isNamespaceSpecific;
    }

    public boolean getIsNamespaceSpecific()
    {
        return isNamespaceSpecific;
    }

    public void setIncludeDefaults(boolean includeDefaults)
    {
        this.includeDefaults = includeDefaults;
    }

    public boolean getIncludeDefaults()
    {
        return includeDefaults;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String getQueryUriTemplateString()
    {
        return queryUriTemplateString;
    }

    public void setQueryUriTemplateString(String queryUriTemplateString)
    {
        this.queryUriTemplateString = queryUriTemplateString;
    }
    
    public String getStandardUriTemplateString()
    {
        return standardUriTemplateString;
    }

    public void setStandardUriTemplateString(String standardUriTemplateString)
    {
        this.standardUriTemplateString = standardUriTemplateString;
    }
    
    public String getTemplateString()
    {
        return templateString;
    }

    public void setTemplateString(String templateString)
    {
        this.templateString = templateString;
    }
    
    public String getOutputRdfXmlString()
    {
        return outputRdfXmlString;
    }

    public void setOutputRdfXmlString(String outputRdfXmlString)
    {
        this.outputRdfXmlString = outputRdfXmlString;
    }
    
    public void setPublicIdentifierIndexes(int[] publicIdentifierIndexes)
    {
        this.publicIdentifierIndexes = publicIdentifierIndexes;
    }

    public int[] getPublicIdentifierIndexes()
    {
        return publicIdentifierIndexes;
    }

    public void setNamespaceInputIndexes(int[] namespaceInputIndexes)
    {
        this.namespaceInputIndexes = namespaceInputIndexes;
    }

    public int[] getNamespaceInputIndexes()
    {
        return namespaceInputIndexes;
    }

    public boolean getIsPageable()
    {
        return isPageable;
    }
    
    public void setIsPageable(boolean isPageable)
    {
        this.isPageable = isPageable;
    }
    
    public boolean getInRobotsTxt()
    {
        return inRobotsTxt;
    }
    
    public void setInRobotsTxt(boolean inRobotsTxt)
    {
        this.inRobotsTxt = inRobotsTxt;
    }

    public boolean getHandleAllNamespaces()
    {
        return handleAllNamespaces;
    }
    
    public void setHandleAllNamespaces(boolean handleAllNamespaces)
    {
        this.handleAllNamespaces = handleAllNamespaces;
    }

	/**
	 * @param queryTypeUri the queryTypeUri to set
	 */
	public static void setQueryTypeUri(URI queryTypeUri) {
		QueryTypeImpl.queryTypeUri = queryTypeUri;
	}

	/**
	 * @return the queryTypeUri
	 */
	public static URI getQueryTypeUri() {
		return queryTypeUri;
	}

	/**
	 * @param queryTitle the queryTitle to set
	 */
	public static void setQueryTitle(URI queryTitle) {
		QueryTypeImpl.queryTitle = queryTitle;
	}

	/**
	 * @return the queryTitle
	 */
	public static URI getQueryTitle() {
		return queryTitle;
	}

	/**
	 * @param queryHandleAllNamespaces the queryHandleAllNamespaces to set
	 */
	public static void setQueryHandleAllNamespaces(
			URI queryHandleAllNamespaces) {
		QueryTypeImpl.queryHandleAllNamespaces = queryHandleAllNamespaces;
	}

	/**
	 * @return the queryHandleAllNamespaces
	 */
	public static URI getQueryHandleAllNamespaces() {
		return queryHandleAllNamespaces;
	}

	/**
	 * @param queryNamespaceToHandle the queryNamespaceToHandle to set
	 */
	public static void setQueryNamespaceToHandle(URI queryNamespaceToHandle) {
		QueryTypeImpl.queryNamespaceToHandle = queryNamespaceToHandle;
	}

	/**
	 * @return the queryNamespaceToHandle
	 */
	public static URI getQueryNamespaceToHandle() {
		return queryNamespaceToHandle;
	}

	/**
	 * @param queryPublicIdentifierIndex the queryPublicIdentifierIndex to set
	 */
	public static void setQueryPublicIdentifierIndex(
			URI queryPublicIdentifierIndex) {
		QueryTypeImpl.queryPublicIdentifierIndex = queryPublicIdentifierIndex;
	}

	/**
	 * @return the queryPublicIdentifierIndex
	 */
	public static URI getQueryPublicIdentifierIndex() {
		return queryPublicIdentifierIndex;
	}

	/**
	 * @param queryNamespaceInputIndex the queryNamespaceInputIndex to set
	 */
	public static void setQueryNamespaceInputIndex(
			URI queryNamespaceInputIndex) {
		QueryTypeImpl.queryNamespaceInputIndex = queryNamespaceInputIndex;
	}

	/**
	 * @return the queryNamespaceInputIndex
	 */
	public static URI getQueryNamespaceInputIndex() {
		return queryNamespaceInputIndex;
	}

	/**
	 * @param queryNamespaceMatchMethod the queryNamespaceMatchMethod to set
	 */
	public static void setQueryNamespaceMatchMethod(
			URI queryNamespaceMatchMethod) {
		QueryTypeImpl.queryNamespaceMatchMethod = queryNamespaceMatchMethod;
	}

	/**
	 * @return the queryNamespaceMatchMethod
	 */
	public static URI getQueryNamespaceMatchMethod() {
		return queryNamespaceMatchMethod;
	}

	/**
	 * @param queryNamespaceSpecific the queryNamespaceSpecific to set
	 */
	public static void setQueryNamespaceSpecific(URI queryNamespaceSpecific) {
		QueryTypeImpl.queryNamespaceSpecific = queryNamespaceSpecific;
	}

	/**
	 * @return the queryNamespaceSpecific
	 */
	public static URI getQueryNamespaceSpecific() {
		return queryNamespaceSpecific;
	}

	/**
	 * @param queryIncludeDefaults the queryIncludeDefaults to set
	 */
	public static void setQueryIncludeDefaults(URI queryIncludeDefaults) {
		QueryTypeImpl.queryIncludeDefaults = queryIncludeDefaults;
	}

	/**
	 * @return the queryIncludeDefaults
	 */
	public static URI getQueryIncludeDefaults() {
		return queryIncludeDefaults;
	}

	/**
	 * @param queryInputRegex the queryInputRegex to set
	 */
	public static void setQueryInputRegex(URI queryInputRegex) {
		QueryTypeImpl.queryInputRegex = queryInputRegex;
	}

	/**
	 * @return the queryInputRegex
	 */
	public static URI getQueryInputRegex() {
		return queryInputRegex;
	}

	/**
	 * @param queryIncludeQueryType the queryIncludeQueryType to set
	 */
	public static void setQueryIncludeQueryType(URI queryIncludeQueryType) {
		QueryTypeImpl.queryIncludeQueryType = queryIncludeQueryType;
	}

	/**
	 * @return the queryIncludeQueryType
	 */
	public static URI getQueryIncludeQueryType() {
		return queryIncludeQueryType;
	}

	/**
	 * @param queryTemplateTerm the queryTemplateTerm to set
	 */
	public static void setQueryTemplateTerm(URI queryTemplateTerm) {
		QueryTypeImpl.queryTemplateTerm = queryTemplateTerm;
	}

	/**
	 * @return the queryTemplateTerm
	 */
	public static URI getQueryTemplateTerm() {
		return queryTemplateTerm;
	}

	/**
	 * @param queryParameterTemplateTerm the queryParameterTemplateTerm to set
	 */
	public static void setQueryParameterTemplateTerm(
			URI queryParameterTemplateTerm) {
		QueryTypeImpl.queryParameterTemplateTerm = queryParameterTemplateTerm;
	}

	/**
	 * @return the queryParameterTemplateTerm
	 */
	public static URI getQueryParameterTemplateTerm() {
		return queryParameterTemplateTerm;
	}

	/**
	 * @param queryStaticOutputTemplateTerm the queryStaticOutputTemplateTerm to set
	 */
	public static void setQueryStaticOutputTemplateTerm(
			URI queryStaticOutputTemplateTerm) {
		QueryTypeImpl.queryStaticOutputTemplateTerm = queryStaticOutputTemplateTerm;
	}

	/**
	 * @return the queryStaticOutputTemplateTerm
	 */
	public static URI getQueryStaticOutputTemplateTerm() {
		return queryStaticOutputTemplateTerm;
	}

	/**
	 * @param queryInRobotsTxt the queryInRobotsTxt to set
	 */
	public static void setQueryInRobotsTxt(URI queryInRobotsTxt) {
		QueryTypeImpl.queryInRobotsTxt = queryInRobotsTxt;
	}

	/**
	 * @return the queryInRobotsTxt
	 */
	public static URI getQueryInRobotsTxt() {
		return queryInRobotsTxt;
	}

	/**
	 * @param queryIsPageable the queryIsPageable to set
	 */
	public static void setQueryIsPageable(URI queryIsPageable) {
		QueryTypeImpl.queryIsPageable = queryIsPageable;
	}

	/**
	 * @return the queryIsPageable
	 */
	public static URI getQueryIsPageable() {
		return queryIsPageable;
	}

	/**
	 * @param queryNamespaceMatchAny the queryNamespaceMatchAny to set
	 */
	public static void setQueryNamespaceMatchAny(URI queryNamespaceMatchAny) {
		QueryTypeImpl.queryNamespaceMatchAny = queryNamespaceMatchAny;
	}

	/**
	 * @return the queryNamespaceMatchAny
	 */
	public static URI getQueryNamespaceMatchAny() {
		return queryNamespaceMatchAny;
	}

	/**
	 * @param queryNamespaceMatchAll the queryNamespaceMatchAll to set
	 */
	public static void setQueryNamespaceMatchAll(URI queryNamespaceMatchAll) {
		QueryTypeImpl.queryNamespaceMatchAll = queryNamespaceMatchAll;
	}

	/**
	 * @return the queryNamespaceMatchAll
	 */
	public static URI getQueryNamespaceMatchAll() {
		return queryNamespaceMatchAll;
	}

    public boolean getIsDummyQueryType()
    {
        return this.isDummyQueryType;
    }

    public void setIsDummyQueryType(boolean isDummyQueryType)
    {
        this.isDummyQueryType = isDummyQueryType;
    }

    /**
     * @param queryIsDummyQueryType the queryIsDummyQueryType to set
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
        return queryIsDummyQueryType;
    }

    public boolean isUsedWithProfileList(List<Profile> orderedProfileList,
            boolean allowImplicitInclusions, boolean includeNonProfileMatched)
    {
        return ProfileImpl.isUsedWithProfileList(this, orderedProfileList, allowImplicitInclusions, includeNonProfileMatched);
    }
}
