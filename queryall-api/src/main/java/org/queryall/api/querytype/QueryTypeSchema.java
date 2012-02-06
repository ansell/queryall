/**
 * 
 */
package org.queryall.api.querytype;

import org.kohsuke.MetaInfServices;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.base.QueryAllSchema;
import org.queryall.api.namespace.NamespaceEntrySchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices(QueryAllSchema.class)
public class QueryTypeSchema extends QueryAllSchema
{
    private static final Logger LOG = LoggerFactory.getLogger(QueryTypeSchema.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = QueryTypeSchema.LOG.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = QueryTypeSchema.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = QueryTypeSchema.LOG.isInfoEnabled();
    
    private static URI queryTypeUri;
    
    private static URI queryTitle;
    
    private static URI queryHandleAllNamespaces;
    
    private static URI queryNamespaceToHandle;
    
    private static URI queryPublicIdentifierIndex;
    private static URI queryPublicIdentifierTag;
    
    private static URI queryNamespaceInputIndex;
    
    private static URI queryNamespaceMatchMethod;
    
    private static URI queryNamespaceSpecific;
    
    private static URI queryIncludeDefaults;
    
    private static URI queryIncludeQueryType;
    
    private static URI queryTemplateString;
    
    private static URI queryQueryUriTemplateString;
    
    private static URI queryStandardUriTemplateString;
    
    private static URI queryTemplateTerm;
    
    private static URI queryParameterTemplateTerm;
    
    private static URI queryStaticOutputTemplateTerm;
    
    private static URI queryInRobotsTxt;
    
    private static URI queryIsPageable;
    
    private static URI queryIsDummyQueryType;
    
    private static URI queryNamespaceMatchAny;
    
    private static URI queryNamespaceMatchAll;
    private static URI queryNamespaceInputTag;
    
    static
    {
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        final String baseUri = QueryAllNamespaces.QUERY.getBaseURI();
        
        QueryTypeSchema.setQueryTypeUri(f.createURI(baseUri, "Query"));
        QueryTypeSchema.setQueryTitle(f.createURI(baseUri, "title"));
        QueryTypeSchema.setQueryHandleAllNamespaces(f.createURI(baseUri, "handleAllNamespaces"));
        QueryTypeSchema.setQueryNamespaceToHandle(f.createURI(baseUri, "namespaceToHandle"));
        QueryTypeSchema.setQueryPublicIdentifierIndex(f.createURI(baseUri, "hasPublicIdentifierIndex"));
        QueryTypeSchema.setQueryPublicIdentifierTag(f.createURI(baseUri, "hasPublicIdentifierTag"));
        
        QueryTypeSchema.setQueryNamespaceInputIndex(f.createURI(baseUri, "hasNamespaceInputIndex"));
        QueryTypeSchema.setQueryNamespaceInputTag(f.createURI(baseUri, "hasNamespaceInputTag"));
        QueryTypeSchema.setQueryNamespaceMatchMethod(f.createURI(baseUri, "namespaceMatchMethod"));
        QueryTypeSchema.setQueryNamespaceSpecific(f.createURI(baseUri, "isNamespaceSpecific"));
        QueryTypeSchema.setQueryIncludeDefaults(f.createURI(baseUri, "includeDefaults"));
        QueryTypeSchema.setQueryIncludeQueryType(f.createURI(baseUri, "includeQueryType"));
        QueryTypeSchema.setQueryTemplateString(f.createURI(baseUri, "templateString"));
        QueryTypeSchema.setQueryQueryUriTemplateString(f.createURI(baseUri, "queryUriTemplateString"));
        QueryTypeSchema.setQueryStandardUriTemplateString(f.createURI(baseUri, "standardUriTemplateString"));
        QueryTypeSchema.setQueryInRobotsTxt(f.createURI(baseUri, "inRobotsTxt"));
        QueryTypeSchema.setQueryIsPageable(f.createURI(baseUri, "isPageable"));
        QueryTypeSchema.setQueryNamespaceMatchAny(f.createURI(baseUri, "namespaceMatchAny"));
        QueryTypeSchema.setQueryNamespaceMatchAll(f.createURI(baseUri, "namespaceMatchAll"));
        QueryTypeSchema.setQueryTemplateTerm(f.createURI(baseUri, "includedQueryTemplate"));
        QueryTypeSchema.setQueryParameterTemplateTerm(f.createURI(baseUri, "includedQueryParameterTemplate"));
        QueryTypeSchema.setQueryStaticOutputTemplateTerm(f.createURI(baseUri, "includedStaticOutputTemplate"));
        QueryTypeSchema.setQueryIsDummyQueryType(f.createURI(baseUri, "isDummyQueryType"));
        
    }
    
    /**
     * The pre-instantiated schema object for QueryTypeSchema.
     */
    public static final QueryAllSchema QUERY_TYPE_SCHEMA = new QueryTypeSchema();
    
    /**
     * @return the queryHandleAllNamespaces
     */
    public static URI getQueryHandleAllNamespaces()
    {
        return QueryTypeSchema.queryHandleAllNamespaces;
    }
    
    /**
     * @return the queryIncludeDefaults
     */
    public static URI getQueryIncludeDefaults()
    {
        return QueryTypeSchema.queryIncludeDefaults;
    }
    
    /**
     * @return the queryIncludeQueryType
     */
    public static URI getQueryIncludeQueryType()
    {
        return QueryTypeSchema.queryIncludeQueryType;
    }
    
    /**
     * @return the queryInRobotsTxt
     */
    public static URI getQueryInRobotsTxt()
    {
        return QueryTypeSchema.queryInRobotsTxt;
    }
    
    /**
     * @return the queryIsDummyQueryType
     */
    public static URI getQueryIsDummyQueryType()
    {
        return QueryTypeSchema.queryIsDummyQueryType;
    }
    
    /**
     * @return the queryIsPageable
     */
    public static URI getQueryIsPageable()
    {
        return QueryTypeSchema.queryIsPageable;
    }
    
    /**
     * @return the queryNamespaceInputIndex
     */
    public static URI getQueryNamespaceInputIndex()
    {
        return QueryTypeSchema.queryNamespaceInputIndex;
    }
    
    public static URI getQueryNamespaceInputTag()
    {
        return QueryTypeSchema.queryNamespaceInputTag;
    }
    
    /**
     * @return the queryNamespaceMatchAll
     */
    public static URI getQueryNamespaceMatchAll()
    {
        return QueryTypeSchema.queryNamespaceMatchAll;
    }
    
    /**
     * @return the queryNamespaceMatchAny
     */
    public static URI getQueryNamespaceMatchAny()
    {
        return QueryTypeSchema.queryNamespaceMatchAny;
    }
    
    /**
     * @return the queryNamespaceMatchMethod
     */
    public static URI getQueryNamespaceMatchMethod()
    {
        return QueryTypeSchema.queryNamespaceMatchMethod;
    }
    
    /**
     * @return the queryNamespaceSpecific
     */
    public static URI getQueryNamespaceSpecific()
    {
        return QueryTypeSchema.queryNamespaceSpecific;
    }
    
    /**
     * @return the queryNamespaceToHandle
     */
    public static URI getQueryNamespaceToHandle()
    {
        return QueryTypeSchema.queryNamespaceToHandle;
    }
    
    /**
     * @return the queryParameterTemplateTerm
     */
    public static URI getQueryParameterTemplateTerm()
    {
        return QueryTypeSchema.queryParameterTemplateTerm;
    }
    
    /**
     * @return the queryPublicIdentifierIndex
     */
    public static URI getQueryPublicIdentifierIndex()
    {
        return QueryTypeSchema.queryPublicIdentifierIndex;
    }
    
    /**
     * @return the queryPublicIdentifierTag
     */
    public static URI getQueryPublicIdentifierTag()
    {
        return QueryTypeSchema.queryPublicIdentifierTag;
    }
    
    /**
     * @return the queryQueryUriTemplateString
     */
    public static URI getQueryQueryUriTemplateString()
    {
        return QueryTypeSchema.queryQueryUriTemplateString;
    }
    
    /**
     * @return the queryStandardUriTemplateString
     */
    public static URI getQueryStandardUriTemplateString()
    {
        return QueryTypeSchema.queryStandardUriTemplateString;
    }
    
    /**
     * @return the queryStaticOutputTemplateTerm
     */
    public static URI getQueryStaticOutputTemplateTerm()
    {
        return QueryTypeSchema.queryStaticOutputTemplateTerm;
    }
    
    /**
     * @return the queryTemplateString
     */
    public static URI getQueryTemplateString()
    {
        return QueryTypeSchema.queryTemplateString;
    }
    
    /**
     * @return the queryTemplateTerm
     */
    public static URI getQueryTemplateTerm()
    {
        return QueryTypeSchema.queryTemplateTerm;
    }
    
    /**
     * @return the queryTitle
     */
    public static URI getQueryTitle()
    {
        return QueryTypeSchema.queryTitle;
    }
    
    /**
     * @return the queryTypeUri
     */
    public static URI getQueryTypeUri()
    {
        return QueryTypeSchema.queryTypeUri;
    }
    
    /**
     * @param nextQueryHandleAllNamespaces
     *            the queryHandleAllNamespaces to set
     */
    public static void setQueryHandleAllNamespaces(final URI nextQueryHandleAllNamespaces)
    {
        QueryTypeSchema.queryHandleAllNamespaces = nextQueryHandleAllNamespaces;
    }
    
    /**
     * @param nextQueryIncludeDefaults
     *            the queryIncludeDefaults to set
     */
    public static void setQueryIncludeDefaults(final URI nextQueryIncludeDefaults)
    {
        QueryTypeSchema.queryIncludeDefaults = nextQueryIncludeDefaults;
    }
    
    /**
     * @param nextQueryIncludeQueryType
     *            the queryIncludeQueryType to set
     */
    public static void setQueryIncludeQueryType(final URI nextQueryIncludeQueryType)
    {
        QueryTypeSchema.queryIncludeQueryType = nextQueryIncludeQueryType;
    }
    
    /**
     * @param nextQueryInRobotsTxt
     *            the queryInRobotsTxt to set
     */
    public static void setQueryInRobotsTxt(final URI nextQueryInRobotsTxt)
    {
        QueryTypeSchema.queryInRobotsTxt = nextQueryInRobotsTxt;
    }
    
    /**
     * @param nextQueryIsDummyQueryType
     *            the queryIsDummyQueryType to set
     */
    public static void setQueryIsDummyQueryType(final URI nextQueryIsDummyQueryType)
    {
        QueryTypeSchema.queryIsDummyQueryType = nextQueryIsDummyQueryType;
    }
    
    /**
     * @param nextQueryIsPageable
     *            the queryIsPageable to set
     */
    public static void setQueryIsPageable(final URI nextQueryIsPageable)
    {
        QueryTypeSchema.queryIsPageable = nextQueryIsPageable;
    }
    
    /**
     * @param nextQueryNamespaceInputIndex
     *            the queryNamespaceInputIndex to set
     */
    public static void setQueryNamespaceInputIndex(final URI nextQueryNamespaceInputIndex)
    {
        QueryTypeSchema.queryNamespaceInputIndex = nextQueryNamespaceInputIndex;
    }
    
    private static void setQueryNamespaceInputTag(final URI nextQueryNamespaceInputTag)
    {
        QueryTypeSchema.queryNamespaceInputTag = nextQueryNamespaceInputTag;
    }
    
    /**
     * @param nextQueryNamespaceMatchAll
     *            the queryNamespaceMatchAll to set
     */
    public static void setQueryNamespaceMatchAll(final URI nextQueryNamespaceMatchAll)
    {
        QueryTypeSchema.queryNamespaceMatchAll = nextQueryNamespaceMatchAll;
    }
    
    /**
     * @param nextQueryNamespaceMatchAny
     *            the queryNamespaceMatchAny to set
     */
    public static void setQueryNamespaceMatchAny(final URI nextQueryNamespaceMatchAny)
    {
        QueryTypeSchema.queryNamespaceMatchAny = nextQueryNamespaceMatchAny;
    }
    
    /**
     * @param nextQueryNamespaceMatchMethod
     *            the queryNamespaceMatchMethod to set
     */
    public static void setQueryNamespaceMatchMethod(final URI nextQueryNamespaceMatchMethod)
    {
        QueryTypeSchema.queryNamespaceMatchMethod = nextQueryNamespaceMatchMethod;
    }
    
    /**
     * @param nextQueryNamespaceSpecific
     *            the queryNamespaceSpecific to set
     */
    public static void setQueryNamespaceSpecific(final URI nextQueryNamespaceSpecific)
    {
        QueryTypeSchema.queryNamespaceSpecific = nextQueryNamespaceSpecific;
    }
    
    /**
     * @param nextQueryNamespaceToHandle
     *            the queryNamespaceToHandle to set
     */
    public static void setQueryNamespaceToHandle(final URI nextQueryNamespaceToHandle)
    {
        QueryTypeSchema.queryNamespaceToHandle = nextQueryNamespaceToHandle;
    }
    
    /**
     * @param nextQueryParameterTemplateTerm
     *            the queryParameterTemplateTerm to set
     */
    public static void setQueryParameterTemplateTerm(final URI nextQueryParameterTemplateTerm)
    {
        QueryTypeSchema.queryParameterTemplateTerm = nextQueryParameterTemplateTerm;
    }
    
    /**
     * @param nextQueryPublicIdentifierIndex
     *            the queryPublicIdentifierIndex to set
     */
    public static void setQueryPublicIdentifierIndex(final URI nextQueryPublicIdentifierIndex)
    {
        QueryTypeSchema.queryPublicIdentifierIndex = nextQueryPublicIdentifierIndex;
    }
    
    /**
     * @param nextQueryPublicIdentifierTag
     *            the queryPublicIdentifierTag to set
     */
    public static void setQueryPublicIdentifierTag(final URI nextQueryPublicIdentifierTag)
    {
        QueryTypeSchema.queryPublicIdentifierTag = nextQueryPublicIdentifierTag;
    }
    
    /**
     * @param nextQueryQueryUriTemplateString
     *            the queryQueryUriTemplateString to set
     */
    public static void setQueryQueryUriTemplateString(final URI nextQueryQueryUriTemplateString)
    {
        QueryTypeSchema.queryQueryUriTemplateString = nextQueryQueryUriTemplateString;
    }
    
    /**
     * @param nextQueryStandardUriTemplateString
     *            the queryStandardUriTemplateString to set
     */
    public static void setQueryStandardUriTemplateString(final URI nextQueryStandardUriTemplateString)
    {
        QueryTypeSchema.queryStandardUriTemplateString = nextQueryStandardUriTemplateString;
    }
    
    /**
     * @param nextQueryStaticOutputTemplateTerm
     *            the queryStaticOutputTemplateTerm to set
     */
    public static void setQueryStaticOutputTemplateTerm(final URI nextQueryStaticOutputTemplateTerm)
    {
        QueryTypeSchema.queryStaticOutputTemplateTerm = nextQueryStaticOutputTemplateTerm;
    }
    
    /**
     * @param nextQueryTemplateString
     *            the queryTemplateString to set
     */
    public static void setQueryTemplateString(final URI nextQueryTemplateString)
    {
        QueryTypeSchema.queryTemplateString = nextQueryTemplateString;
    }
    
    /**
     * @param nextQueryTemplateTerm
     *            the queryTemplateTerm to set
     */
    public static void setQueryTemplateTerm(final URI nextQueryTemplateTerm)
    {
        QueryTypeSchema.queryTemplateTerm = nextQueryTemplateTerm;
    }
    
    /**
     * @param nextQueryTitle
     *            the queryTitle to set
     */
    public static void setQueryTitle(final URI nextQueryTitle)
    {
        QueryTypeSchema.queryTitle = nextQueryTitle;
    }
    
    /**
     * @param nextQueryTypeUri
     *            the queryTypeUri to set
     */
    public static void setQueryTypeUri(final URI nextQueryTypeUri)
    {
        QueryTypeSchema.queryTypeUri = nextQueryTypeUri;
    }
    
    /**
     * Default constructor, uses the name of this class as the name.
     */
    public QueryTypeSchema()
    {
        this(QueryTypeSchema.class.getName());
    }
    
    /**
     * @param nextName
     *            The name for this schema object
     */
    public QueryTypeSchema(final String nextName)
    {
        super(nextName);
    }
    
    @Override
    public boolean schemaToRdf(final Repository myRepository, final int modelVersion, final URI... contexts)
        throws OpenRDFException
    {
        final RepositoryConnection con = myRepository.getConnection();
        
        final ValueFactory f = Constants.VALUE_FACTORY;
        
        try
        {
            con.setAutoCommit(false);
            
            con.add(QueryTypeSchema.getQueryTypeUri(), RDF.TYPE, OWL.CLASS, contexts);
            
            if(modelVersion == 1)
            {
                con.add(QueryTypeSchema.getQueryTitle(), RDF.TYPE, OWL.DEPRECATEDPROPERTY, contexts);
                con.add(QueryTypeSchema.getQueryTitle(), RDFS.SUBPROPERTYOF, Constants.DC_TITLE, contexts);
            }
            
            con.add(QueryTypeSchema.getQueryNamespaceToHandle(), RDF.TYPE, OWL.OBJECTPROPERTY, contexts);
            con.add(QueryTypeSchema.getQueryNamespaceToHandle(), RDFS.RANGE,
                    NamespaceEntrySchema.getNamespaceTypeUri(), contexts);
            con.add(QueryTypeSchema.getQueryNamespaceToHandle(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(),
                    contexts);
            con.add(QueryTypeSchema.getQueryNamespaceToHandle(), RDFS.LABEL, f.createLiteral("."), contexts);
            
            con.add(QueryTypeSchema.getQueryIncludeQueryType(), RDF.TYPE, OWL.OBJECTPROPERTY, contexts);
            con.add(QueryTypeSchema.getQueryIncludeQueryType(), RDFS.RANGE, QueryTypeSchema.getQueryTypeUri(), contexts);
            con.add(QueryTypeSchema.getQueryIncludeQueryType(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(),
                    contexts);
            con.add(QueryTypeSchema.getQueryIncludeQueryType(), RDFS.LABEL, f.createLiteral("."), contexts);
            
            con.add(QueryTypeSchema.getQueryNamespaceMatchMethod(), RDF.TYPE, OWL.OBJECTPROPERTY, contexts);
            con.add(QueryTypeSchema.getQueryNamespaceMatchMethod(), RDFS.RANGE, RDFS.RESOURCE, contexts);
            con.add(QueryTypeSchema.getQueryNamespaceMatchMethod(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(),
                    contexts);
            con.add(QueryTypeSchema.getQueryNamespaceMatchMethod(), RDFS.LABEL, f.createLiteral("."), contexts);
            
            con.add(QueryTypeSchema.getQueryHandleAllNamespaces(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
            con.add(QueryTypeSchema.getQueryHandleAllNamespaces(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(QueryTypeSchema.getQueryHandleAllNamespaces(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(),
                    contexts);
            con.add(QueryTypeSchema.getQueryHandleAllNamespaces(), RDFS.LABEL, f.createLiteral("."), contexts);
            
            con.add(QueryTypeSchema.getQueryNamespaceSpecific(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
            con.add(QueryTypeSchema.getQueryNamespaceSpecific(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(QueryTypeSchema.getQueryNamespaceSpecific(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(),
                    contexts);
            con.add(QueryTypeSchema.getQueryNamespaceSpecific(), RDFS.LABEL, f.createLiteral("."), contexts);
            
            con.add(QueryTypeSchema.getQueryIncludeDefaults(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
            con.add(QueryTypeSchema.getQueryIncludeDefaults(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(QueryTypeSchema.getQueryIncludeDefaults(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(), contexts);
            con.add(QueryTypeSchema.getQueryIncludeDefaults(), RDFS.LABEL, f.createLiteral("."), contexts);
            
            con.add(QueryTypeSchema.getQueryInRobotsTxt(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
            con.add(QueryTypeSchema.getQueryInRobotsTxt(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(QueryTypeSchema.getQueryInRobotsTxt(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(), contexts);
            con.add(QueryTypeSchema.getQueryInRobotsTxt(), RDFS.LABEL, f.createLiteral("."), contexts);
            
            con.add(QueryTypeSchema.getQueryIsPageable(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
            con.add(QueryTypeSchema.getQueryIsPageable(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(QueryTypeSchema.getQueryIsPageable(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(), contexts);
            con.add(QueryTypeSchema.getQueryIsPageable(), RDFS.LABEL, f.createLiteral("."), contexts);
            
            con.add(QueryTypeSchema.getQueryIsDummyQueryType(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
            con.add(QueryTypeSchema.getQueryIsDummyQueryType(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(QueryTypeSchema.getQueryIsDummyQueryType(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(),
                    contexts);
            con.add(QueryTypeSchema.getQueryIsDummyQueryType(), RDFS.LABEL, f.createLiteral("."), contexts);
            
            con.add(QueryTypeSchema.getQueryPublicIdentifierIndex(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
            con.add(QueryTypeSchema.getQueryPublicIdentifierIndex(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(QueryTypeSchema.getQueryPublicIdentifierIndex(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(),
                    contexts);
            con.add(QueryTypeSchema.getQueryPublicIdentifierIndex(), RDFS.LABEL,
                    f.createLiteral("Deprecated: use publicIdentifierTag with input_N where N is the index."), contexts);
            
            con.add(QueryTypeSchema.getQueryNamespaceInputIndex(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
            con.add(QueryTypeSchema.getQueryNamespaceInputIndex(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(QueryTypeSchema.getQueryNamespaceInputIndex(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(),
                    contexts);
            con.add(QueryTypeSchema.getQueryNamespaceInputIndex(), RDFS.LABEL,
                    f.createLiteral("Deprecated: use namespaceInputTag with input_N where N is the index."), contexts);
            
            con.add(QueryTypeSchema.getQueryPublicIdentifierTag(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
            con.add(QueryTypeSchema.getQueryPublicIdentifierTag(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(QueryTypeSchema.getQueryPublicIdentifierTag(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(),
                    contexts);
            con.add(QueryTypeSchema.getQueryPublicIdentifierTag(),
                    RDFS.LABEL,
                    f.createLiteral("Defines the tags that are to be identified as public, and hence convertable according to public registries and rules."),
                    contexts);
            
            con.add(QueryTypeSchema.getQueryNamespaceInputTag(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
            con.add(QueryTypeSchema.getQueryNamespaceInputTag(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(QueryTypeSchema.getQueryNamespaceInputTag(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(),
                    contexts);
            con.add(QueryTypeSchema.getQueryNamespaceInputTag(),
                    RDFS.LABEL,
                    f.createLiteral("Defines the tags that are to be identified as namespaces, and hence convertable according to namespace registries."),
                    contexts);
            
            con.add(QueryTypeSchema.getQueryTemplateString(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
            con.add(QueryTypeSchema.getQueryTemplateString(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(QueryTypeSchema.getQueryTemplateString(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(), contexts);
            con.add(QueryTypeSchema.getQueryTemplateString(), RDFS.LABEL, f.createLiteral("."), contexts);
            
            con.add(QueryTypeSchema.getQueryQueryUriTemplateString(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
            con.add(QueryTypeSchema.getQueryQueryUriTemplateString(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(QueryTypeSchema.getQueryQueryUriTemplateString(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(),
                    contexts);
            con.add(QueryTypeSchema.getQueryQueryUriTemplateString(), RDFS.LABEL, f.createLiteral("."), contexts);
            
            con.add(QueryTypeSchema.getQueryStandardUriTemplateString(), RDF.TYPE, OWL.DATATYPEPROPERTY, contexts);
            con.add(QueryTypeSchema.getQueryStandardUriTemplateString(), RDFS.RANGE, RDFS.LITERAL, contexts);
            con.add(QueryTypeSchema.getQueryStandardUriTemplateString(), RDFS.DOMAIN,
                    QueryTypeSchema.getQueryTypeUri(), contexts);
            con.add(QueryTypeSchema.getQueryStandardUriTemplateString(), RDFS.LABEL, f.createLiteral("."), contexts);
            
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
            
            QueryTypeSchema.LOG.error("RepositoryException: " + re.getMessage());
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
}
