package org.queryall.impl;

import info.aduna.iteration.Iterations;

import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Literal;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;

import org.openrdf.OpenRDFException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.model.MemValueFactory;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Hashtable;

import org.queryall.*;
import org.queryall.queryutils.ProvenanceRecord;
import org.queryall.helpers.*;

import org.apache.log4j.Logger;

public class QueryTypeImpl extends QueryType
{
    private static final Logger log = Logger.getLogger(QueryType.class.getName());
    private static final boolean _TRACE = log.isTraceEnabled();
    private static final boolean _DEBUG = log.isDebugEnabled();
    private static final boolean _INFO = log.isInfoEnabled();
    
    private static final String defaultNamespace = Settings.DEFAULT_RDF_QUERY_NAMESPACE;
    
    // this is a temporary flag used to enable a smooth transition 
    // from hard-coded templates to generic extensible templates in the future
    public static final boolean USING_TEMPLATES = false;
    
    private Collection<Statement> unrecognisedStatements = new HashSet<Statement>();
    
    private URI key;
    private String title = "";
    private URI curationStatus = ProjectImpl.projectNotCuratedUri;
    
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
    private URI namespaceMatchMethod = QueryTypeImpl.queryNamespaceMatchAny;
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
    
    // public String queryTemplateKey = null;
    
    // Each query template must be separately executed on any provide endpoints which support any of the content types for this query template
    private Collection<URI> includedQueryTemplates = new HashSet<URI>();
    
    // Query Parameters are references to Templates that are specific to this query with respect to the semantic meanings of terms that may be discovered at lower levels
    // queries that reference normalisedQueryUri will be matched against queries in this list since they are not going to be contextually independent templates, but are used in templates as query parameters
    private Collection<URI> includedQueryParameters = new HashSet<URI>();
    
    // Each static output template must be converted as necessary using the includedQueryParameters, 
    // but they result in RDF documents that can be included in the result sets
    private Collection<URI> includedStaticOutputTemplates = new HashSet<URI>();
    
    private String templateString = "";
    private String queryUriTemplateString = "";
    private String standardUriTemplateString = "";
    private String outputRdfXmlString = "";
    
    private Collection<ProvenanceRecord> relatedProvenance = new HashSet<ProvenanceRecord>();
    
    public static URI queryTypeUri;
    public static URI queryTitle;
    public static URI queryHandleAllNamespaces;
    public static URI queryNamespaceToHandle;
    public static URI queryPublicIdentifierIndex;
    public static URI queryNamespaceInputIndex;
    public static URI queryNamespaceMatchMethod;
    public static URI queryNamespaceSpecific;
    public static URI queryIncludeDefaults;
    public static URI queryInputRegex;
    
    public static URI queryIncludeQueryType;
    public static URI OLDqueryTemplateString;
    public static URI OLDqueryQueryUriTemplateString;
    public static URI OLDqueryStandardUriTemplateString;
    public static URI OLDqueryOutputRdfXmlString;
    
    public static URI queryTemplateTerm;
    public static URI queryParameterTemplateTerm;
    public static URI queryStaticOutputTemplateTerm;
    
    public static URI queryInRobotsTxt;
    public static URI queryIsPageable;
    public static URI queryNamespaceMatchAny;
    public static URI queryNamespaceMatchAll;
    
    public static String queryNamespace;
    
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
        ValueFactory f = new MemValueFactory();
        
        queryNamespace = Settings.DEFAULT_ONTOLOGYTERMURI_PREFIX
                         +Settings.DEFAULT_RDF_QUERY_NAMESPACE
                         +Settings.DEFAULT_ONTOLOGYTERMURI_SUFFIX;
                         
        queryTypeUri = f.createURI(queryNamespace+"Query");
        queryTitle = f.createURI(queryNamespace+"title");
        queryHandleAllNamespaces = f.createURI(queryNamespace+"handleAllNamespaces");
        queryNamespaceToHandle = f.createURI(queryNamespace+"namespaceToHandle");
        queryPublicIdentifierIndex = f.createURI(queryNamespace+"hasPublicIdentifierIndex");
        queryNamespaceInputIndex = f.createURI(queryNamespace+"hasNamespaceInputIndex");
        queryNamespaceMatchMethod = f.createURI(queryNamespace+"namespaceMatchMethod");
        queryNamespaceSpecific = f.createURI(queryNamespace+"isNamespaceSpecific");
        queryIncludeDefaults = f.createURI(queryNamespace+"includeDefaults");
        queryIncludeQueryType = f.createURI(queryNamespace+"includeQueryType");
        queryInputRegex = f.createURI(queryNamespace+"inputRegex");
        OLDqueryTemplateString = f.createURI(queryNamespace+"templateString");
        OLDqueryQueryUriTemplateString = f.createURI(queryNamespace+"queryUriTemplateString");
        OLDqueryStandardUriTemplateString = f.createURI(queryNamespace+"standardUriTemplateString");
        OLDqueryOutputRdfXmlString = f.createURI(queryNamespace+"outputRdfXmlString");
        queryInRobotsTxt = f.createURI(queryNamespace+"inRobotsTxt");
        queryIsPageable = f.createURI(queryNamespace+"isPageable");
        queryNamespaceMatchAny = f.createURI(queryNamespace+"namespaceMatchAny");
        queryNamespaceMatchAll = f.createURI(queryNamespace+"namespaceMatchAll");
        queryTemplateTerm = f.createURI(queryNamespace+"includedQueryTemplate");
        queryParameterTemplateTerm = f.createURI(queryNamespace+"includedQueryParameterTemplate");
        queryStaticOutputTemplateTerm = f.createURI(queryNamespace+"includedStaticOutputTemplate");
        
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
    
    public static Map<URI, QueryType> getCustomQueriesFromRepository(Repository myRepository, int modelVersion) throws org.openrdf.repository.RepositoryException
    {
        Map<URI, QueryType> results = new Hashtable<URI, QueryType>();
        
        String queryTypeUri = new QueryTypeImpl().getElementType();
        
        RepositoryConnection myRepositoryConnection = null;
        
        try
        {
            myRepositoryConnection = myRepository.getConnection();
            
            final String queryString = "SELECT ?QueryTypeUri WHERE { ?QueryTypeUri a <"
                    + queryTypeUri + "> . }";
            final TupleQuery tupleQuery = myRepositoryConnection.prepareTupleQuery(
                    QueryLanguage.SPARQL, queryString);
            final TupleQueryResult queryResult = tupleQuery.evaluate();
            try
            {
                while(queryResult.hasNext())
                {
                    final BindingSet bindingSet = queryResult.next();
                    final Value valueOfQueryTypeUri = bindingSet
                            .getValue("QueryTypeUri");
                    if(_TRACE)
                    {
                        log.trace("QueryType.getCustomQueriesFromRepository: found QueryType: valueOfQueryTypeUri="
                                        + valueOfQueryTypeUri);
                    }
                    final RepositoryResult<Statement> statements = 
                            myRepositoryConnection.getStatements((URI) valueOfQueryTypeUri,
                                    (URI) null, (Value) null, true);
                    final Collection<Statement> nextStatementList = 
                            Iterations.addAll(statements, new HashSet<Statement>());
                    final QueryType nextRecord = QueryTypeImpl.fromRdf(nextStatementList, (URI)valueOfQueryTypeUri, modelVersion);
                    
                    if(nextRecord != null)
                    {
                        results.put((URI)valueOfQueryTypeUri,
                                nextRecord);
                    }
                    else
                    {
                        log.error("QueryType.getCustomQueriesFromRepository: was not able to create a custom query with URI valueOfQueryTypeUri="
                                        + valueOfQueryTypeUri.toString());
                    }
                }
            }
            finally
            {
                queryResult.close();
            }
        }
        catch (OpenRDFException e)
        {
            // handle exception
            log.error("QueryType.getCustomQueriesFromRepository:", e);
        }
        finally
        {
            if(myRepositoryConnection != null)
                myRepositoryConnection.close();
        }
        
        return results;
    }
    
    // keyToUse is the URI of the next instance that can be found in myRepository
    // returns null if the URI is not in the repository or the information is not enough to create a minimal query configuration
    public static QueryType fromRdf(Collection<Statement> inputStatements, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        QueryType result = new QueryTypeImpl();
        
        boolean resultIsValid = false;
        
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
        
        ValueFactory f = new MemValueFactory();
        
        URI queryInstanceUri = keyToUse;
        
        for(Statement nextStatement : inputStatements)
        {
            if(_DEBUG)
            {
                log.debug("QueryType: nextStatement: "+nextStatement.toString());
            }
            
            if(nextStatement.getPredicate().equals(RDF.TYPE) && nextStatement.getObject().equals(queryTypeUri))
            {
                if(_TRACE)
                {
                    log.trace("QueryType: found valid type predicate for URI: "+keyToUse);
                }
                
                resultIsValid = true;
                result.setKey(keyToUse);
            }
            else if(nextStatement.getPredicate().equals(ProjectImpl.projectCurationStatusUri))
            {
                result.setCurationStatus((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(queryTitle) || nextStatement.getPredicate().equals(Settings.DC_TITLE))
            {
                result.setTitle(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(queryHandleAllNamespaces))
            {
                result.setHandleAllNamespaces(Utilities.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(queryNamespaceToHandle))
            {
                tempNamespacesToHandle.add((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(queryPublicIdentifierIndex))
            {
                tempPublicIdentifierIndexes.add(Utilities.getIntegerFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(queryNamespaceInputIndex))
            {
                tempNamespaceInputIndexes.add(Utilities.getIntegerFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(queryNamespaceMatchMethod))
            {
                result.setNamespaceMatchMethod((URI)nextStatement.getObject());
            }
            else if(nextStatement.getPredicate().equals(queryNamespaceSpecific))
            {
                result.setIsNamespaceSpecific(Utilities.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(queryIncludeDefaults))
            {
                result.setIncludeDefaults(Utilities.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(queryInputRegex))
            {
                result.setInputRegex(nextStatement.getObject().stringValue());
            }
            else if(USING_TEMPLATES && nextStatement.getPredicate().equals(queryIncludeQueryType))
            {
                Template tempTemplate = Settings.createNewTemplateByKey(nextStatement.getObject().stringValue()+"_outputtemplate_0", TemplateImpl.templateContentTypeRdfXml.stringValue());
                
                Template existingTemplate = Settings.getTemplate(tempTemplate.getKey());
                
                if(existingTemplate == null)
                {
                    Settings.addTemplate(tempTemplate, false);
                }
                // else
                // {
                    // log.info("QueryType.fromRdf: already found existing template : keyToUse="+keyToUse+" tempTemplate.getKey()="+tempTemplate.getKey());
                // }
                
                tempIncludedStaticOutputTemplates.add(tempTemplate.getKey());
                
                includecounter++;
            }
            else if(USING_TEMPLATES && nextStatement.getPredicate().equals(OLDqueryTemplateString))
            {
                Template tempTemplate = Settings.createNewTemplateByString(keyToUse+"_querytemplate_"+templatecounter, nextStatement.getObject().stringValue(), TemplateImpl.templateContentTypeSparqlQuery.stringValue());
                
                Template existingTemplate = Settings.getTemplate(tempTemplate.getKey());
                
                if(existingTemplate == null)
                {
                    if(!Settings.addTemplate(tempTemplate, false))
                    {
                        log.error("QueryType.fromRdf: failed to add template to collection : keyToUse="+keyToUse+" tempTemplate.getKey()="+tempTemplate.getKey());
                    }
                }
                else
                {
                    log.info("QueryType.fromRdf: already found existing template : keyToUse="+keyToUse+" tempTemplate.getKey()="+tempTemplate.getKey());
                }
                
                tempIncludedQueryTemplates.add(tempTemplate.getKey());
                
                templatecounter++;
            }
            else if(USING_TEMPLATES && nextStatement.getPredicate().equals(OLDqueryOutputRdfXmlString))
            {
                Template tempTemplate = Settings.createNewTemplateByString(keyToUse+"_outputtemplate_"+outputcounter, nextStatement.getObject().stringValue(), TemplateImpl.templateContentTypeRdfXml.stringValue());
                
                if(!Settings.addTemplate(tempTemplate, true))
                {
                    log.error("QueryType.fromRdf: failed to add template to collection : keyToUse="+keyToUse+" tempTemplate.getKey()="+tempTemplate.getKey());
                }
                
                // The previous semantics was that these templates were not recognised unless they were included from somewhere, so not adding here or it will break that semantics
                // tempIncludedStaticOutputTemplates.add(tempTemplate.getKey());
                
                outputcounter++;
            }
            else if(USING_TEMPLATES && 
                    (nextStatement.getPredicate().equals(OLDqueryQueryUriTemplateString) 
                  || nextStatement.getPredicate().equals(OLDqueryStandardUriTemplateString))
                 )
            {
                Template tempTemplate = Settings.createNewTemplateByString(keyToUse+"_parametertemplate_"+parametercounter, nextStatement.getObject().stringValue(), TemplateImpl.templateContentTypePlainText.stringValue());
                
                Template existingTemplate = Settings.getTemplate(tempTemplate.getKey());
                
                if(existingTemplate == null)
                {
                    if(!Settings.addTemplate(tempTemplate, false))
                    {
                        log.error("QueryType.fromRdf: failed to add template to collection : keyToUse="+keyToUse+" tempTemplate.getKey()="+tempTemplate.getKey());
                    }
                }
                else
                {
                    log.info("QueryType.fromRdf: already found existing template : keyToUse="+keyToUse+" tempTemplate.getKey()="+tempTemplate.getKey());
                }
                
                tempIncludedQueryParameters.add(tempTemplate.getKey());
                
                parametercounter++;
            }
            else if(!USING_TEMPLATES && nextStatement.getPredicate().equals(queryIncludeQueryType))
            {
                tempsemanticallyLinkedCustomQueries.add((URI)nextStatement.getObject());
            }
            else if(!USING_TEMPLATES && nextStatement.getPredicate().equals(OLDqueryTemplateString))
            {
                result.setTemplateString(nextStatement.getObject().stringValue());
            }
            else if(!USING_TEMPLATES && nextStatement.getPredicate().equals(OLDqueryQueryUriTemplateString))
            {
                result.setQueryUriTemplateString(nextStatement.getObject().stringValue());
            }
            else if(!USING_TEMPLATES && nextStatement.getPredicate().equals(OLDqueryStandardUriTemplateString))
            {
                result.setStandardUriTemplateString(nextStatement.getObject().stringValue());
            }
            else if(!USING_TEMPLATES && nextStatement.getPredicate().equals(OLDqueryOutputRdfXmlString))
            {
                result.setOutputRdfXmlString(nextStatement.getObject().stringValue());
            }
            else if(nextStatement.getPredicate().equals(queryInRobotsTxt))
            {
                result.setInRobotsTxt(Utilities.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(queryIsPageable))
            {
                result.setIsPageable(Utilities.getBooleanFromValue(nextStatement.getObject()));
            }
            else if(nextStatement.getPredicate().equals(ProfileImpl.getProfileIncludeExcludeOrderUri()))
            {
                result.setProfileIncludeExcludeOrder((URI)nextStatement.getObject());
            }
            else
            {
                result.addUnrecognisedStatement(nextStatement);
            }
        }
        
        result.setNamespacesToHandle(tempNamespacesToHandle);
        result.setPublicIdentifierIndexes(Utilities.getIntArrayFromArrayInteger(tempPublicIdentifierIndexes.toArray(new Integer[0])));
        result.setNamespaceInputIndexes(Utilities.getIntArrayFromArrayInteger(tempNamespaceInputIndexes.toArray(new Integer[0])));
        
        if(USING_TEMPLATES)
        {
            result.setIncludedQueryTemplates(tempIncludedQueryTemplates);
            result.setIncludedQueryParameters(tempIncludedQueryParameters);
            result.setIncludedStaticOutputTemplates(tempIncludedStaticOutputTemplates);
        }
        else
        {
            result.setSemanticallyLinkedQueryTypes(tempsemanticallyLinkedCustomQueries);
        }
        
        if(_DEBUG)
        {
            log.debug("QueryType.fromRdf: would have returned... keyToUse="+keyToUse+" result="+result.toString());
        }
        
        
        if(resultIsValid)
        {
            return result;
        }
        else
        {
            throw new RuntimeException("QueryType.fromRdf: result was not valid keyToUse="+keyToUse);
        }
    }
    
    @Override
    public boolean toRdf(Repository myRepository, URI keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            // create some resources and literals to make statements out of
            URI queryInstanceUri = keyToUse;
            
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
            URI profileIncludeExcludeOrderLiteral = profileIncludeExcludeOrder;
            
            
            URI curationStatusLiteral = null;
            
            if(curationStatus == null)
            {
                curationStatusLiteral = ProjectImpl.projectNotCuratedUri;
            }
            else
            {
                curationStatusLiteral = curationStatus;
            }
            
            // log.info("after literals created");
            
            con.setAutoCommit(false);
            
            con.add(queryInstanceUri, RDF.TYPE, queryTypeUri, queryInstanceUri);
            con.add(queryInstanceUri, ProjectImpl.projectCurationStatusUri, curationStatusLiteral, queryInstanceUri);
            if(modelVersion == 1)
            {
                con.add(queryInstanceUri, queryTitle, titleLiteral, queryInstanceUri);
            }
            else
            {
                con.add(queryInstanceUri, Settings.DC_TITLE, titleLiteral, queryInstanceUri);
            }
            
            con.add(queryInstanceUri, queryHandleAllNamespaces, handleAllNamespacesLiteral, queryInstanceUri);
            con.add(queryInstanceUri, queryNamespaceSpecific, isNamespaceSpecificLiteral, queryInstanceUri);
            con.add(queryInstanceUri, queryNamespaceMatchMethod, namespaceMatchMethodLiteral, queryInstanceUri);
            con.add(queryInstanceUri, queryIncludeDefaults, includeDefaultsLiteral, queryInstanceUri);
            con.add(queryInstanceUri, queryInputRegex, inputRegexLiteral, queryInstanceUri);
            if(!USING_TEMPLATES)
            {
                con.add(queryInstanceUri, OLDqueryTemplateString, templateStringLiteral, queryInstanceUri);
                con.add(queryInstanceUri, OLDqueryQueryUriTemplateString, queryUriTemplateStringLiteral, queryInstanceUri);
                con.add(queryInstanceUri, OLDqueryStandardUriTemplateString, standardUriTemplateStringLiteral, queryInstanceUri);
                con.add(queryInstanceUri, OLDqueryOutputRdfXmlString, outputRdfXmlStringLiteral, queryInstanceUri);
            }
            con.add(queryInstanceUri, queryInRobotsTxt, inRobotsTxtLiteral, queryInstanceUri);
            con.add(queryInstanceUri, queryIsPageable, isPageableLiteral, queryInstanceUri);
            
            
            con.add(queryInstanceUri, ProfileImpl.getProfileIncludeExcludeOrderUri(), profileIncludeExcludeOrderLiteral, queryInstanceUri);
            
            // log.info("after single URIs created");

            if(namespacesToHandle != null)
            {
            
                for(URI nextNamespaceToHandle : namespacesToHandle)
                {
                    if(nextNamespaceToHandle != null)
                    {
                        con.add(queryInstanceUri, queryNamespaceToHandle, nextNamespaceToHandle, queryInstanceUri);
                    }
                }
            }
            
            if(publicIdentifierIndexes != null)
            {
            
                for(int nextPublicIdentifierIndex : publicIdentifierIndexes)
                {
                    con.add(queryInstanceUri, queryPublicIdentifierIndex, f.createLiteral(nextPublicIdentifierIndex), queryInstanceUri);
                }
            }
            
            if(namespaceInputIndexes != null)
            {
            
                for(int nextNamespaceInputIndex : namespaceInputIndexes)
                {
                    con.add(queryInstanceUri, queryNamespaceInputIndex, f.createLiteral(nextNamespaceInputIndex), queryInstanceUri);
                }
            }
            
            // log.info("before included query templates created");
            
            if(USING_TEMPLATES)
            {
                if(includedQueryTemplates != null)
                {
                    for(URI nextIncludeQueryTemplate : includedQueryTemplates)
                    {
                        if(nextIncludeQueryTemplate != null)
                        {
                            con.add(queryInstanceUri, queryTemplateTerm, nextIncludeQueryTemplate, queryInstanceUri);
                        }
                    }
                }
                
                // log.info("before included query parameters created");
                
                if(includedQueryParameters != null)
                {
                    for(URI nextIncludedQueryParameter : includedQueryParameters)
                    {
                        if(nextIncludedQueryParameter != null)
                        {
                            con.add(queryInstanceUri, queryParameterTemplateTerm, nextIncludedQueryParameter, queryInstanceUri);
                        }
                    }
                }
                
                // log.info("before included static output templates created");
                
                if(includedStaticOutputTemplates != null)
                {
                    for(URI nextIncludedStaticOutputTemplate : includedStaticOutputTemplates)
                    {
                        if(nextIncludedStaticOutputTemplate != null)
                        {
                            con.add(queryInstanceUri, queryStaticOutputTemplateTerm, nextIncludedStaticOutputTemplate, queryInstanceUri);
                        }
                    }
                }
            } // end if(USING_TEMPLATES)
            else
            {
                if(semanticallyLinkedCustomQueries != null)
                {
                    for(URI nextSemanticallyLinkedQueryType : semanticallyLinkedCustomQueries)
                    {
                        if(nextSemanticallyLinkedQueryType != null)
                        {
                            con.add(queryInstanceUri, queryIncludeQueryType, nextSemanticallyLinkedQueryType, queryInstanceUri);
                        }
                    }
                }
            }
            // log.info("before unrecognised statements added");
            
            if(unrecognisedStatements != null)
            {
            
                for(Statement nextUnrecognisedStatement : unrecognisedStatements)
                {
                    con.add(nextUnrecognisedStatement);
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
        catch (OpenRDFException ordfe)
        {
            log.error(ordfe);
            
            // Something went wrong during the transaction, so we roll it back
            
            if(con != null)
                con.rollback();
                
            throw ordfe;
        }
        finally
        {
            if(con != null)
                con.close();
        }
        
        return false;
    }
    
    public static boolean schemaToRdf(Repository myRepository, String keyToUse, int modelVersion) throws OpenRDFException
    {
        RepositoryConnection con = myRepository.getConnection();
        
        ValueFactory f = myRepository.getValueFactory();
        
        try
        {
            URI contextKeyUri = f.createURI(keyToUse);
            con.setAutoCommit(false);
            
            con.add(queryTypeUri, RDF.TYPE, OWL.CLASS, contextKeyUri);
            
            if(modelVersion == 1)
            {
                con.add(queryTitle, RDFS.SUBPROPERTYOF, f.createURI(Settings.DC_NAMESPACE+"title"), contextKeyUri);
            }
            
            con.add(queryHandleAllNamespaces, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(queryNamespaceSpecific, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(queryNamespaceMatchMethod, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(queryIncludeDefaults, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(queryInputRegex, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            if(!QueryTypeImpl.USING_TEMPLATES)
            {
                con.add(OLDqueryTemplateString, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
                
                con.add(OLDqueryQueryUriTemplateString, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
                
                con.add(OLDqueryStandardUriTemplateString, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
                
                con.add(OLDqueryOutputRdfXmlString, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            }
            else
            {
                con.add(queryTemplateTerm, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
                
                con.add(queryParameterTemplateTerm, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
                
                con.add(queryStaticOutputTemplateTerm, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            }
            
            con.add(queryInRobotsTxt, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(queryIsPageable, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(queryNamespaceToHandle, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            
            con.add(queryPublicIdentifierIndex, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(queryNamespaceInputIndex, RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            
            con.add(queryIncludeQueryType, RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            
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
        catch (OpenRDFException ordfe)
        {
            log.error(ordfe);
            
            // Something went wrong during the transaction, so we roll it back
            
            if(con != null)
                con.rollback();
                
            throw ordfe;
        }
        finally
        {
            if(con != null)
                con.close();
        }
        
        return false;
    }
    
    @Override
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
            // Utilities.joinStringCollectionHelper(semanticallyLinkedCustomQueries, ", ", sb);
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
            // Utilities.joinStringCollectionHelper(namespacesToHandle, ", ", sb);
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
            sb.append("<div class=\""+prefix+"key_div\"><input type=\"hidden\" name=\"key\" value=\""+Utilities.xmlEncodeString(getKey().stringValue())+"\" /></div>\n");
        }
        
        sb.append("<div class=\""+prefix+"title_div\"><span class=\""+prefix+"title_span\">Title:</span><input type=\"text\" name=\""+prefix+"title\" value=\""+Utilities.xmlEncodeString(title)+"\" /></div>\n");
        
        sb.append("<div class=\""+prefix+"templateString_div\"><span class=\""+prefix+"templateString_span\">Query Template:</span><input type=\"text\" name=\""+prefix+"templateString\" value=\""+Utilities.xmlEncodeString(templateString)+"\" /></div>\n");
        sb.append("<div class=\""+prefix+"standardUriTemplateString_div\"><span class=\""+prefix+"standardUriTemplateString_span\">Standard URI Template:</span><input type=\"text\" name=\""+prefix+"standardUriTemplateString\" value=\""+Utilities.xmlEncodeString(standardUriTemplateString)+"\" /></div>\n");
        sb.append("<div class=\""+prefix+"queryUriTemplateString_div\"><span class=\""+prefix+"queryUriTemplateString_span\">Query URI Template:</span><input type=\"text\" name=\""+prefix+"queryUriTemplateString\" value=\""+Utilities.xmlEncodeString(queryUriTemplateString)+"\" /></div>\n");
        sb.append("<div class=\""+prefix+"outputRdfXmlString_div\"><span class=\""+prefix+"outputRdfXmlString_span\">Static output RDF/XML Template:</span><input type=\"text\" name=\""+prefix+"outputRdfXmlString\" value=\""+Utilities.xmlEncodeString(outputRdfXmlString)+"\" /></div>\n");
        
        sb.append("<div class=\""+prefix+"inputRegex_div\"><span class=\""+prefix+"inputRegex_span\">Input Regular Expression:</span><input type=\"text\" name=\""+prefix+"inputRegex\" value=\""+Utilities.xmlEncodeString(inputRegex)+"\" /></div>\n");
        
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
        
        if(namespaceMatchMethod.equals(QueryTypeImpl.queryNamespaceMatchAll))
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
        return Utilities.matchesForRegexOnString(getInputRegexPattern(), this.inputRegex, nextQueryString);
    }
    
    public boolean handlesNamespaceUris(Collection<Collection<URI>> namespacesToCheck)
    {
        if(handleAllNamespaces)
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
        if(namespacesToHandle == null || namespacesToCheck == null)
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
                
                if(namespaceMatchMethod.equals(QueryTypeImpl.queryNamespaceMatchAny))
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
                
                if(namespaceMatchMethod.equals(QueryTypeImpl.queryNamespaceMatchAll))
                {
                    if(_DEBUG)
                    {
                        log.debug("QueryType.handlesNamespacesSpecifically: all match disproved this.getKey()="+this.getKey());
                    }
                    
                    break;
                }
            }
        }
        
        if(namespaceMatchMethod.equals(QueryTypeImpl.queryNamespaceMatchAny))
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
        else if(namespaceMatchMethod.equals(QueryTypeImpl.queryNamespaceMatchAll))
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
    
    @Override
    public String toHtml()
    {
        StringBuilder sb = new StringBuilder();
        
        String prefix = "query_";
        
        sb.append("<span>key:</span>"+Utilities.xmlEncodeString(getKey().stringValue()));
        
        sb.append(Utilities.xmlEncodeString(this.toString()));
        
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
     * @param key the key to set
     */
    @Override
    public void setKey(String nextKey)
    {
        this.setKey(Utilities.createURI(nextKey));
    }

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
        return defaultNamespace;
    }

    /**
     * @return the URI used for the rdf Type of these elements
     */
    @Override
    public String getElementType()
    {
        return queryTypeUri.stringValue();
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
        return queryNamespaceMatchAny;
    }
    
    public static URI getNamespaceMatchAllUri()
    {
        return queryNamespaceMatchAll;
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
        final int BEFORE = -1;
        final int EQUAL = 0;
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

    
    public Collection<URI> getIncludedQueryTemplates()
    {
        return includedQueryTemplates;
    }

    public void setIncludedQueryTemplates(Collection<URI> includedQueryTemplates)
    {
        this.includedQueryTemplates = includedQueryTemplates;
    }
    
    public Collection<URI> getIncludedQueryParameters()
    {
        return includedQueryParameters;
    }

    public void setIncludedQueryParameters(Collection<URI> includedQueryParameters)
    {
        this.includedQueryParameters = includedQueryParameters;
    }
    
    public Collection<URI> getIncludedStaticOutputTemplates()
    {
        return includedStaticOutputTemplates;
    }

    public void setIncludedStaticOutputTemplates(Collection<URI> includedStaticOutputTemplates)
    {
        this.includedStaticOutputTemplates = includedStaticOutputTemplates;
    }
    
    
    public Collection<URI> getNamespacesToHandle()
    {
        return namespacesToHandle;
    }

    public void setNamespacesToHandle(Collection<URI> namespacesToHandle)
    {
        this.namespacesToHandle = namespacesToHandle;
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

    public void setOutputRdfXmlString(String outputrdfxmlString)
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
    
    public void setInRobotsTxt(boolean InRobotsTxt)
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
    
    
}
