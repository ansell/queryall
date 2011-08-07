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
import org.queryall.enumerations.Constants;
import org.queryall.query.ProvenanceRecord;
import org.queryall.query.Settings;
import org.queryall.utils.ListUtils;
import org.queryall.utils.ProfileUtils;
import org.queryall.utils.RdfUtils;
import org.queryall.utils.StringUtils;

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
    
    /**
     * @return the queryHandleAllNamespaces
     */
    public static URI getQueryHandleAllNamespaces()
    {
        return QueryTypeImpl.queryHandleAllNamespaces;
    }
    
    /**
     * @return the queryIncludeDefaults
     */
    public static URI getQueryIncludeDefaults()
    {
        return QueryTypeImpl.queryIncludeDefaults;
    }
    
    /**
     * @return the queryIncludeQueryType
     */
    public static URI getQueryIncludeQueryType()
    {
        return QueryTypeImpl.queryIncludeQueryType;
    }
    
    /**
     * @return the queryInputRegex
     */
    public static URI getQueryInputRegex()
    {
        return QueryTypeImpl.queryInputRegex;
    }
    
    /**
     * @return the queryInRobotsTxt
     */
    public static URI getQueryInRobotsTxt()
    {
        return QueryTypeImpl.queryInRobotsTxt;
    }
    
    /**
     * @return the queryIsDummyQueryType
     */
    public static URI getQueryIsDummyQueryType()
    {
        return QueryTypeImpl.queryIsDummyQueryType;
    }
    
    /**
     * @return the queryIsPageable
     */
    public static URI getQueryIsPageable()
    {
        return QueryTypeImpl.queryIsPageable;
    }
    
    /**
     * @return the queryNamespaceInputIndex
     */
    public static URI getQueryNamespaceInputIndex()
    {
        return QueryTypeImpl.queryNamespaceInputIndex;
    }
    
    /**
     * @return the queryNamespaceMatchAll
     */
    public static URI getQueryNamespaceMatchAll()
    {
        return QueryTypeImpl.queryNamespaceMatchAll;
    }
    
    /**
     * @return the queryNamespaceMatchAny
     */
    public static URI getQueryNamespaceMatchAny()
    {
        return QueryTypeImpl.queryNamespaceMatchAny;
    }
    
    /**
     * @return the queryNamespaceMatchMethod
     */
    public static URI getQueryNamespaceMatchMethod()
    {
        return QueryTypeImpl.queryNamespaceMatchMethod;
    }
    
    /**
     * @return the queryNamespaceSpecific
     */
    public static URI getQueryNamespaceSpecific()
    {
        return QueryTypeImpl.queryNamespaceSpecific;
    }
    
    /**
     * @return the queryNamespaceToHandle
     */
    public static URI getQueryNamespaceToHandle()
    {
        return QueryTypeImpl.queryNamespaceToHandle;
    }
    
    /**
     * @return the queryOutputRdfXmlString
     */
    public static URI getQueryOutputRdfXmlString()
    {
        return QueryTypeImpl.queryOutputRdfXmlString;
    }
    
    /**
     * @return the queryParameterTemplateTerm
     */
    public static URI getQueryParameterTemplateTerm()
    {
        return QueryTypeImpl.queryParameterTemplateTerm;
    }
    
    /**
     * @return the queryPublicIdentifierIndex
     */
    public static URI getQueryPublicIdentifierIndex()
    {
        return QueryTypeImpl.queryPublicIdentifierIndex;
    }
    
    /**
     * @return the queryQueryUriTemplateString
     */
    public static URI getQueryQueryUriTemplateString()
    {
        return QueryTypeImpl.queryQueryUriTemplateString;
    }
    
    /**
     * @return the queryStandardUriTemplateString
     */
    public static URI getQueryStandardUriTemplateString()
    {
        return QueryTypeImpl.queryStandardUriTemplateString;
    }
    
    /**
     * @return the queryStaticOutputTemplateTerm
     */
    public static URI getQueryStaticOutputTemplateTerm()
    {
        return QueryTypeImpl.queryStaticOutputTemplateTerm;
    }
    
    /**
     * @return the queryTemplateString
     */
    public static URI getQueryTemplateString()
    {
        return QueryTypeImpl.queryTemplateString;
    }
    
    /**
     * @return the queryTemplateTerm
     */
    public static URI getQueryTemplateTerm()
    {
        return QueryTypeImpl.queryTemplateTerm;
    }
    
    /**
     * @return the queryTitle
     */
    public static URI getQueryTitle()
    {
        return QueryTypeImpl.queryTitle;
    }
    
    /**
     * @return the queryTypeUri
     */
    public static URI getQueryTypeUri()
    {
        return QueryTypeImpl.queryTypeUri;
    }
    
    /**
     * @param queryHandleAllNamespaces
     *            the queryHandleAllNamespaces to set
     */
    public static void setQueryHandleAllNamespaces(final URI queryHandleAllNamespaces)
    {
        QueryTypeImpl.queryHandleAllNamespaces = queryHandleAllNamespaces;
    }
    
    /**
     * @param queryIncludeDefaults
     *            the queryIncludeDefaults to set
     */
    public static void setQueryIncludeDefaults(final URI queryIncludeDefaults)
    {
        QueryTypeImpl.queryIncludeDefaults = queryIncludeDefaults;
    }
    
    /**
     * @param queryIncludeQueryType
     *            the queryIncludeQueryType to set
     */
    public static void setQueryIncludeQueryType(final URI queryIncludeQueryType)
    {
        QueryTypeImpl.queryIncludeQueryType = queryIncludeQueryType;
    }
    
    /**
     * @param queryInputRegex
     *            the queryInputRegex to set
     */
    public static void setQueryInputRegex(final URI queryInputRegex)
    {
        QueryTypeImpl.queryInputRegex = queryInputRegex;
    }
    
    /**
     * @param queryInRobotsTxt
     *            the queryInRobotsTxt to set
     */
    public static void setQueryInRobotsTxt(final URI queryInRobotsTxt)
    {
        QueryTypeImpl.queryInRobotsTxt = queryInRobotsTxt;
    }
    
    /**
     * @param queryIsDummyQueryType
     *            the queryIsDummyQueryType to set
     */
    public static void setQueryIsDummyQueryType(final URI queryIsDummyQueryType)
    {
        QueryTypeImpl.queryIsDummyQueryType = queryIsDummyQueryType;
    }
    
    /**
     * @param queryIsPageable
     *            the queryIsPageable to set
     */
    public static void setQueryIsPageable(final URI queryIsPageable)
    {
        QueryTypeImpl.queryIsPageable = queryIsPageable;
    }
    
    /**
     * @param queryNamespaceInputIndex
     *            the queryNamespaceInputIndex to set
     */
    public static void setQueryNamespaceInputIndex(final URI queryNamespaceInputIndex)
    {
        QueryTypeImpl.queryNamespaceInputIndex = queryNamespaceInputIndex;
    }
    
    /**
     * @param queryNamespaceMatchAll
     *            the queryNamespaceMatchAll to set
     */
    public static void setQueryNamespaceMatchAll(final URI queryNamespaceMatchAll)
    {
        QueryTypeImpl.queryNamespaceMatchAll = queryNamespaceMatchAll;
    }
    
    /**
     * @param queryNamespaceMatchAny
     *            the queryNamespaceMatchAny to set
     */
    public static void setQueryNamespaceMatchAny(final URI queryNamespaceMatchAny)
    {
        QueryTypeImpl.queryNamespaceMatchAny = queryNamespaceMatchAny;
    }
    
    /**
     * @param queryNamespaceMatchMethod
     *            the queryNamespaceMatchMethod to set
     */
    public static void setQueryNamespaceMatchMethod(final URI queryNamespaceMatchMethod)
    {
        QueryTypeImpl.queryNamespaceMatchMethod = queryNamespaceMatchMethod;
    }
    
    /**
     * @param queryNamespaceSpecific
     *            the queryNamespaceSpecific to set
     */
    public static void setQueryNamespaceSpecific(final URI queryNamespaceSpecific)
    {
        QueryTypeImpl.queryNamespaceSpecific = queryNamespaceSpecific;
    }
    
    /**
     * @param queryNamespaceToHandle
     *            the queryNamespaceToHandle to set
     */
    public static void setQueryNamespaceToHandle(final URI queryNamespaceToHandle)
    {
        QueryTypeImpl.queryNamespaceToHandle = queryNamespaceToHandle;
    }
    
    /**
     * @param queryOutputRdfXmlString
     *            the queryOutputRdfXmlString to set
     */
    public static void setQueryOutputRdfXmlString(final URI queryOutputRdfXmlString)
    {
        QueryTypeImpl.queryOutputRdfXmlString = queryOutputRdfXmlString;
    }
    
    /**
     * @param queryParameterTemplateTerm
     *            the queryParameterTemplateTerm to set
     */
    public static void setQueryParameterTemplateTerm(final URI queryParameterTemplateTerm)
    {
        QueryTypeImpl.queryParameterTemplateTerm = queryParameterTemplateTerm;
    }
    
    /**
     * @param queryPublicIdentifierIndex
     *            the queryPublicIdentifierIndex to set
     */
    public static void setQueryPublicIdentifierIndex(final URI queryPublicIdentifierIndex)
    {
        QueryTypeImpl.queryPublicIdentifierIndex = queryPublicIdentifierIndex;
    }
    
    /**
     * @param queryQueryUriTemplateString
     *            the queryQueryUriTemplateString to set
     */
    public static void setQueryQueryUriTemplateString(final URI queryQueryUriTemplateString)
    {
        QueryTypeImpl.queryQueryUriTemplateString = queryQueryUriTemplateString;
    }
    
    /**
     * @param queryStandardUriTemplateString
     *            the queryStandardUriTemplateString to set
     */
    public static void setQueryStandardUriTemplateString(final URI queryStandardUriTemplateString)
    {
        QueryTypeImpl.queryStandardUriTemplateString = queryStandardUriTemplateString;
    }
    
    /**
     * @param queryStaticOutputTemplateTerm
     *            the queryStaticOutputTemplateTerm to set
     */
    public static void setQueryStaticOutputTemplateTerm(final URI queryStaticOutputTemplateTerm)
    {
        QueryTypeImpl.queryStaticOutputTemplateTerm = queryStaticOutputTemplateTerm;
    }
    
    /**
     * @param queryTemplateString
     *            the queryTemplateString to set
     */
    public static void setQueryTemplateString(final URI queryTemplateString)
    {
        QueryTypeImpl.queryTemplateString = queryTemplateString;
    }
    
    /**
     * @param queryTemplateTerm
     *            the queryTemplateTerm to set
     */
    public static void setQueryTemplateTerm(final URI queryTemplateTerm)
    {
        QueryTypeImpl.queryTemplateTerm = queryTemplateTerm;
    }
    
    /**
     * @param queryTitle
     *            the queryTitle to set
     */
    public static void setQueryTitle(final URI queryTitle)
    {
        QueryTypeImpl.queryTitle = queryTitle;
    }
    
    /**
     * @param queryTypeUri
     *            the queryTypeUri to set
     */
    public static void setQueryTypeUri(final URI queryTypeUri)
    {
        QueryTypeImpl.queryTypeUri = queryTypeUri;
    }
    
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
    
    public static URI getNamespaceMatchAllUri()
    {
        return QueryTypeImpl.getQueryNamespaceMatchAll();
    }
    
    public static URI getNamespaceMatchAnyUri()
    {
        return QueryTypeImpl.getQueryNamespaceMatchAny();
    }
    
    public static boolean schemaToRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            final URI contextKeyUri = keyToUse;
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
    
    public QueryTypeImpl()
    {
        // TODO Auto-generated constructor stub
    }
    
    public QueryTypeImpl(final Collection<Statement> inputStatements, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        final Collection<URI> tempNamespacesToHandle = new HashSet<URI>();
        final Collection<Integer> tempPublicIdentifierIndexes = new HashSet<Integer>();
        final Collection<Integer> tempNamespaceInputIndexes = new HashSet<Integer>();
        
        final Collection<URI> tempsemanticallyLinkedCustomQueries = new HashSet<URI>();
        
        for(final Statement nextStatement : inputStatements)
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
        final Collection<URI> results = new ArrayList<URI>(1);
        
        results.add(QueryTypeImpl.getQueryTypeUri());
        
        return results;
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
    public String getInputRegex()
    {
        return this.inputRegex;
    }
    
    @Override
    public Pattern getInputRegexPattern()
    {
        if(this.inputRegexPattern == null && this.inputRegex != null)
        {
            this.inputRegexPattern = Pattern.compile(this.inputRegex);
        }
        
        return this.inputRegexPattern;
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
    public int[] getNamespaceInputIndexes()
    {
        return this.namespaceInputIndexes;
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
    public String getOutputRdfXmlString()
    {
        return this.outputRdfXmlString;
    }
    
    @Override
    public URI getProfileIncludeExcludeOrder()
    {
        return this.profileIncludeExcludeOrder;
    }
    
    @Override
    public int[] getPublicIdentifierIndexes()
    {
        return this.publicIdentifierIndexes;
    }
    
    @Override
    public String getQueryUriTemplateString()
    {
        return this.queryUriTemplateString;
    }
    
    @Override
    public Collection<URI> getSemanticallyLinkedQueryTypes()
    {
        return this.semanticallyLinkedCustomQueries;
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
                
                if(this.namespaceMatchMethod.equals(QueryTypeImpl.getQueryNamespaceMatchAny()))
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
                
                if(this.namespaceMatchMethod.equals(QueryTypeImpl.getQueryNamespaceMatchAll()))
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
        
        if(this.namespaceMatchMethod.equals(QueryTypeImpl.getQueryNamespaceMatchAny()))
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
        else if(this.namespaceMatchMethod.equals(QueryTypeImpl.getQueryNamespaceMatchAll()))
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
    public boolean isInputVariablePublic(final int inputNumber)
    {
        if(this.publicIdentifierIndexes != null)
        {
            
            for(final int nextPublicIdentifierIndex : this.publicIdentifierIndexes)
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
    public boolean isUsedWithProfileList(final List<Profile> orderedProfileList, final boolean allowImplicitInclusions,
            final boolean includeNonProfileMatched)
    {
        return ProfileUtils.isUsedWithProfileList(this, orderedProfileList, allowImplicitInclusions,
                includeNonProfileMatched);
    }
    
    @Override
    public List<String> matchesForQueryString(final String nextQueryString)
    {
        return StringUtils.matchesForRegexOnString(this.getInputRegexPattern(), this.inputRegex, nextQueryString);
    }
    
    @Override
    public boolean matchesQueryString(final String nextQueryString)
    {
        return StringUtils.matchesRegexOnString(this.getInputRegexPattern(), this.inputRegex, nextQueryString);
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
    public void setInputRegex(final String nextInputRegex)
    {
        this.inputRegex = nextInputRegex;
        this.inputRegexPattern = Pattern.compile(nextInputRegex);
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
    public void setNamespaceInputIndexes(final int[] namespaceInputIndexes)
    {
        this.namespaceInputIndexes = namespaceInputIndexes;
    }
    
    @Override
    public void setNamespaceMatchMethod(final URI namespaceMatchMethod)
    {
        this.namespaceMatchMethod = namespaceMatchMethod;
    }
    
    @Override
    public void setNamespacesToHandle(final Collection<URI> namespacesToHandle)
    {
        this.namespacesToHandle = namespacesToHandle;
    }
    
    @Override
    public void setOutputRdfXmlString(final String outputRdfXmlString)
    {
        this.outputRdfXmlString = outputRdfXmlString;
    }
    
    @Override
    public void setProfileIncludeExcludeOrder(final URI profileIncludeExcludeOrder)
    {
        this.profileIncludeExcludeOrder = profileIncludeExcludeOrder;
    }
    
    @Override
    public void setPublicIdentifierIndexes(final int[] publicIdentifierIndexes)
    {
        this.publicIdentifierIndexes = publicIdentifierIndexes;
    }
    
    @Override
    public void setQueryUriTemplateString(final String queryUriTemplateString)
    {
        this.queryUriTemplateString = queryUriTemplateString;
    }
    
    @Override
    public void setSemanticallyLinkedQueryTypes(final Collection<URI> semanticallyLinkedCustomQueries)
    {
        this.semanticallyLinkedCustomQueries = semanticallyLinkedCustomQueries;
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
                + prefix + "outputRdfXmlString\" value=\"" + StringUtils.xmlEncodeString(this.outputRdfXmlString)
                + "\" /></div>\n");
        
        sb.append("<div class=\"" + prefix + "inputRegex_div\"><span class=\"" + prefix
                + "inputRegex_span\">Input Regular Expression:</span><input type=\"text\" name=\"" + prefix
                + "inputRegex\" value=\"" + StringUtils.xmlEncodeString(this.inputRegex) + "\" /></div>\n");
        
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
        
        if(this.namespaceMatchMethod.equals(QueryTypeImpl.getQueryNamespaceMatchAll()))
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
        
        if(this.profileIncludeExcludeOrder.equals(ProfileImpl.getExcludeThenIncludeUri()))
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
    public boolean toRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.valueFactory;
        
        try
        {
            // create some resources and literals to make statements out of
            final URI queryInstanceUri = this.getKey();
            
            /***
             * title handleAllNamespaces isNamespaceSpecific namespaceMatchMethod includeDefaults
             * inputRegex templateString queryUriTemplateString standardUriTemplateString
             * outputRdfXmlString inRobotsTxt profileIncludeExcludeOrder
             ***/
            
            final Literal titleLiteral = f.createLiteral(this.title);
            final Literal handleAllNamespacesLiteral = f.createLiteral(this.handleAllNamespaces);
            final Literal isNamespaceSpecificLiteral = f.createLiteral(this.isNamespaceSpecific);
            final URI namespaceMatchMethodLiteral = this.namespaceMatchMethod;
            final Literal includeDefaultsLiteral = f.createLiteral(this.includeDefaults);
            final Literal inputRegexLiteral = f.createLiteral(this.inputRegex);
            
            final Literal templateStringLiteral = f.createLiteral(this.templateString);
            final Literal queryUriTemplateStringLiteral = f.createLiteral(this.queryUriTemplateString);
            final Literal standardUriTemplateStringLiteral = f.createLiteral(this.standardUriTemplateString);
            final Literal outputRdfXmlStringLiteral = f.createLiteral(this.outputRdfXmlString);
            
            final Literal inRobotsTxtLiteral = f.createLiteral(this.inRobotsTxt);
            final Literal isPageableLiteral = f.createLiteral(this.isPageable);
            final Literal isDummyQueryTypeLiteral = f.createLiteral(this.isDummyQueryType);
            final URI profileIncludeExcludeOrderLiteral = this.profileIncludeExcludeOrder;
            
            URI curationStatusLiteral = null;
            
            if(this.curationStatus == null)
            {
                curationStatusLiteral = ProjectImpl.getProjectNotCuratedUri();
            }
            else
            {
                curationStatusLiteral = this.curationStatus;
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
            
            if(this.namespacesToHandle != null)
            {
                
                for(final URI nextNamespaceToHandle : this.namespacesToHandle)
                {
                    if(nextNamespaceToHandle != null)
                    {
                        con.add(queryInstanceUri, QueryTypeImpl.getQueryNamespaceToHandle(), nextNamespaceToHandle,
                                keyToUse);
                    }
                }
            }
            
            if(this.publicIdentifierIndexes != null)
            {
                
                for(final int nextPublicIdentifierIndex : this.publicIdentifierIndexes)
                {
                    con.add(queryInstanceUri, QueryTypeImpl.getQueryPublicIdentifierIndex(),
                            f.createLiteral(nextPublicIdentifierIndex), keyToUse);
                }
            }
            
            if(this.namespaceInputIndexes != null)
            {
                for(final int nextNamespaceInputIndex : this.namespaceInputIndexes)
                {
                    con.add(queryInstanceUri, QueryTypeImpl.getQueryNamespaceInputIndex(),
                            f.createLiteral(nextNamespaceInputIndex), keyToUse);
                }
            }
            
            if(this.semanticallyLinkedCustomQueries != null)
            {
                for(final URI nextSemanticallyLinkedQueryType : this.semanticallyLinkedCustomQueries)
                {
                    if(nextSemanticallyLinkedQueryType != null)
                    {
                        con.add(queryInstanceUri, QueryTypeImpl.getQueryIncludeQueryType(),
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
        // sb.append("outputRdfXmlString=" + outputRdfXmlString + "\n");
        sb.append("inputRegex=" + this.inputRegex + "\n");
        
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
