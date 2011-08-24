/**
 * 
 */
package org.queryall.api.querytype;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.queryall.api.namespace.NamespaceEntrySchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.QueryAllNamespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class QueryTypeSchema
{
    private static final Logger log = LoggerFactory.getLogger(QueryTypeSchema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = QueryTypeSchema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = QueryTypeSchema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = QueryTypeSchema.log.isInfoEnabled();
    
    
    
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
     * @return the queryInputRegex
     */
    public static URI getQueryInputRegex()
    {
        return QueryTypeSchema.queryInputRegex;
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
     * @return the queryOutputRdfXmlString
     */
    public static URI getQueryOutputRdfXmlString()
    {
        return QueryTypeSchema.queryOutputRdfXmlString;
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
     * @param queryHandleAllNamespaces
     *            the queryHandleAllNamespaces to set
     */
    public static void setQueryHandleAllNamespaces(final URI queryHandleAllNamespaces)
    {
        QueryTypeSchema.queryHandleAllNamespaces = queryHandleAllNamespaces;
    }
    
    /**
     * @param queryIncludeDefaults
     *            the queryIncludeDefaults to set
     */
    public static void setQueryIncludeDefaults(final URI queryIncludeDefaults)
    {
        QueryTypeSchema.queryIncludeDefaults = queryIncludeDefaults;
    }
    
    /**
     * @param queryIncludeQueryType
     *            the queryIncludeQueryType to set
     */
    public static void setQueryIncludeQueryType(final URI queryIncludeQueryType)
    {
        QueryTypeSchema.queryIncludeQueryType = queryIncludeQueryType;
    }
    
    /**
     * @param queryInputRegex
     *            the queryInputRegex to set
     */
    public static void setQueryInputRegex(final URI queryInputRegex)
    {
        QueryTypeSchema.queryInputRegex = queryInputRegex;
    }
    
    /**
     * @param queryInRobotsTxt
     *            the queryInRobotsTxt to set
     */
    public static void setQueryInRobotsTxt(final URI queryInRobotsTxt)
    {
        QueryTypeSchema.queryInRobotsTxt = queryInRobotsTxt;
    }
    
    /**
     * @param queryIsDummyQueryType
     *            the queryIsDummyQueryType to set
     */
    public static void setQueryIsDummyQueryType(final URI queryIsDummyQueryType)
    {
        QueryTypeSchema.queryIsDummyQueryType = queryIsDummyQueryType;
    }
    
    /**
     * @param queryIsPageable
     *            the queryIsPageable to set
     */
    public static void setQueryIsPageable(final URI queryIsPageable)
    {
        QueryTypeSchema.queryIsPageable = queryIsPageable;
    }
    
    /**
     * @param queryNamespaceInputIndex
     *            the queryNamespaceInputIndex to set
     */
    public static void setQueryNamespaceInputIndex(final URI queryNamespaceInputIndex)
    {
        QueryTypeSchema.queryNamespaceInputIndex = queryNamespaceInputIndex;
    }
    
    /**
     * @param queryNamespaceMatchAll
     *            the queryNamespaceMatchAll to set
     */
    public static void setQueryNamespaceMatchAll(final URI queryNamespaceMatchAll)
    {
        QueryTypeSchema.queryNamespaceMatchAll = queryNamespaceMatchAll;
    }
    
    /**
     * @param queryNamespaceMatchAny
     *            the queryNamespaceMatchAny to set
     */
    public static void setQueryNamespaceMatchAny(final URI queryNamespaceMatchAny)
    {
        QueryTypeSchema.queryNamespaceMatchAny = queryNamespaceMatchAny;
    }
    
    /**
     * @param queryNamespaceMatchMethod
     *            the queryNamespaceMatchMethod to set
     */
    public static void setQueryNamespaceMatchMethod(final URI queryNamespaceMatchMethod)
    {
        QueryTypeSchema.queryNamespaceMatchMethod = queryNamespaceMatchMethod;
    }
    
    /**
     * @param queryNamespaceSpecific
     *            the queryNamespaceSpecific to set
     */
    public static void setQueryNamespaceSpecific(final URI queryNamespaceSpecific)
    {
        QueryTypeSchema.queryNamespaceSpecific = queryNamespaceSpecific;
    }
    
    /**
     * @param queryNamespaceToHandle
     *            the queryNamespaceToHandle to set
     */
    public static void setQueryNamespaceToHandle(final URI queryNamespaceToHandle)
    {
        QueryTypeSchema.queryNamespaceToHandle = queryNamespaceToHandle;
    }
    
    /**
     * @param queryOutputRdfXmlString
     *            the queryOutputRdfXmlString to set
     */
    public static void setQueryOutputRdfXmlString(final URI queryOutputRdfXmlString)
    {
        QueryTypeSchema.queryOutputRdfXmlString = queryOutputRdfXmlString;
    }
    
    /**
     * @param queryParameterTemplateTerm
     *            the queryParameterTemplateTerm to set
     */
    public static void setQueryParameterTemplateTerm(final URI queryParameterTemplateTerm)
    {
        QueryTypeSchema.queryParameterTemplateTerm = queryParameterTemplateTerm;
    }
    
    /**
     * @param queryPublicIdentifierIndex
     *            the queryPublicIdentifierIndex to set
     */
    public static void setQueryPublicIdentifierIndex(final URI queryPublicIdentifierIndex)
    {
        QueryTypeSchema.queryPublicIdentifierIndex = queryPublicIdentifierIndex;
    }
    
    /**
     * @param queryQueryUriTemplateString
     *            the queryQueryUriTemplateString to set
     */
    public static void setQueryQueryUriTemplateString(final URI queryQueryUriTemplateString)
    {
        QueryTypeSchema.queryQueryUriTemplateString = queryQueryUriTemplateString;
    }
    
    /**
     * @param queryStandardUriTemplateString
     *            the queryStandardUriTemplateString to set
     */
    public static void setQueryStandardUriTemplateString(final URI queryStandardUriTemplateString)
    {
        QueryTypeSchema.queryStandardUriTemplateString = queryStandardUriTemplateString;
    }
    
    /**
     * @param queryStaticOutputTemplateTerm
     *            the queryStaticOutputTemplateTerm to set
     */
    public static void setQueryStaticOutputTemplateTerm(final URI queryStaticOutputTemplateTerm)
    {
        QueryTypeSchema.queryStaticOutputTemplateTerm = queryStaticOutputTemplateTerm;
    }
    
    /**
     * @param queryTemplateString
     *            the queryTemplateString to set
     */
    public static void setQueryTemplateString(final URI queryTemplateString)
    {
        QueryTypeSchema.queryTemplateString = queryTemplateString;
    }
    
    /**
     * @param queryTemplateTerm
     *            the queryTemplateTerm to set
     */
    public static void setQueryTemplateTerm(final URI queryTemplateTerm)
    {
        QueryTypeSchema.queryTemplateTerm = queryTemplateTerm;
    }
    
    /**
     * @param queryTitle
     *            the queryTitle to set
     */
    public static void setQueryTitle(final URI queryTitle)
    {
        QueryTypeSchema.queryTitle = queryTitle;
    }
    
    /**
     * @param queryTypeUri
     *            the queryTypeUri to set
     */
    public static void setQueryTypeUri(final URI queryTypeUri)
    {
        QueryTypeSchema.queryTypeUri = queryTypeUri;
    }
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
    
    static
    {
        final ValueFactory f = Constants.valueFactory;
        
        final String baseUri = QueryAllNamespaces.QUERY.getBaseURI();
        
        QueryTypeSchema.setQueryTypeUri(f.createURI(baseUri, "Query"));
        QueryTypeSchema.setQueryTitle(f.createURI(baseUri, "title"));
        QueryTypeSchema.setQueryHandleAllNamespaces(f.createURI(baseUri, "handleAllNamespaces"));
        QueryTypeSchema.setQueryNamespaceToHandle(f.createURI(baseUri, "namespaceToHandle"));
        QueryTypeSchema.setQueryPublicIdentifierIndex(f.createURI(baseUri, "hasPublicIdentifierIndex"));
        QueryTypeSchema.setQueryNamespaceInputIndex(f.createURI(baseUri, "hasNamespaceInputIndex"));
        QueryTypeSchema.setQueryNamespaceMatchMethod(f.createURI(baseUri, "namespaceMatchMethod"));
        QueryTypeSchema.setQueryNamespaceSpecific(f.createURI(baseUri, "isNamespaceSpecific"));
        QueryTypeSchema.setQueryIncludeDefaults(f.createURI(baseUri, "includeDefaults"));
        QueryTypeSchema.setQueryIncludeQueryType(f.createURI(baseUri, "includeQueryType"));
        QueryTypeSchema.setQueryInputRegex(f.createURI(baseUri, "inputRegex"));
        QueryTypeSchema.setQueryTemplateString(f.createURI(baseUri, "templateString"));
        QueryTypeSchema.setQueryQueryUriTemplateString(f.createURI(baseUri, "queryUriTemplateString"));
        QueryTypeSchema.setQueryStandardUriTemplateString(f.createURI(baseUri, "standardUriTemplateString"));
        QueryTypeSchema.setQueryOutputRdfXmlString(f.createURI(baseUri, "outputRdfXmlString"));
        QueryTypeSchema.setQueryInRobotsTxt(f.createURI(baseUri, "inRobotsTxt"));
        QueryTypeSchema.setQueryIsPageable(f.createURI(baseUri, "isPageable"));
        QueryTypeSchema.setQueryNamespaceMatchAny(f.createURI(baseUri, "namespaceMatchAny"));
        QueryTypeSchema.setQueryNamespaceMatchAll(f.createURI(baseUri, "namespaceMatchAll"));
        QueryTypeSchema.setQueryTemplateTerm(f.createURI(baseUri, "includedQueryTemplate"));
        QueryTypeSchema.setQueryParameterTemplateTerm(f.createURI(baseUri, "includedQueryParameterTemplate"));
        QueryTypeSchema.setQueryStaticOutputTemplateTerm(f.createURI(baseUri, "includedStaticOutputTemplate"));
        QueryTypeSchema.setQueryIsDummyQueryType(f.createURI(baseUri, "isDummyQueryType"));
        
    }
    
    public static URI getNamespaceMatchAllUri()
    {
        return QueryTypeSchema.getQueryNamespaceMatchAll();
    }
    
    public static URI getNamespaceMatchAnyUri()
    {
        return QueryTypeSchema.getQueryNamespaceMatchAny();
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
            
            con.add(QueryTypeSchema.getQueryTypeUri(), RDF.TYPE, OWL.CLASS, contextKeyUri);
            
            if(modelVersion == 1)
            {
                con.add(QueryTypeSchema.getQueryTitle(), RDF.TYPE, OWL.DEPRECATEDPROPERTY, contextKeyUri);
                con.add(QueryTypeSchema.getQueryTitle(), RDFS.SUBPROPERTYOF, Constants.DC_TITLE, contextKeyUri);
            }
            
            con.add(QueryTypeSchema.getQueryNamespaceToHandle(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(QueryTypeSchema.getQueryNamespaceToHandle(), RDFS.RANGE, NamespaceEntrySchema.getNamespaceTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeSchema.getQueryNamespaceToHandle(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeSchema.getQueryNamespaceToHandle(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeSchema.getQueryIncludeQueryType(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(QueryTypeSchema.getQueryIncludeQueryType(), RDFS.RANGE, QueryTypeSchema.getQueryTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeSchema.getQueryIncludeQueryType(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeSchema.getQueryIncludeQueryType(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeSchema.getQueryNamespaceMatchMethod(), RDF.TYPE, OWL.OBJECTPROPERTY, contextKeyUri);
            con.add(QueryTypeSchema.getQueryNamespaceMatchMethod(), RDFS.RANGE, RDFS.RESOURCE, contextKeyUri);
            con.add(QueryTypeSchema.getQueryNamespaceMatchMethod(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeSchema.getQueryNamespaceMatchMethod(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeSchema.getQueryHandleAllNamespaces(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(QueryTypeSchema.getQueryHandleAllNamespaces(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(QueryTypeSchema.getQueryHandleAllNamespaces(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeSchema.getQueryHandleAllNamespaces(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeSchema.getQueryNamespaceSpecific(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(QueryTypeSchema.getQueryNamespaceSpecific(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(QueryTypeSchema.getQueryNamespaceSpecific(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeSchema.getQueryNamespaceSpecific(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeSchema.getQueryIncludeDefaults(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(QueryTypeSchema.getQueryIncludeDefaults(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(QueryTypeSchema.getQueryIncludeDefaults(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeSchema.getQueryIncludeDefaults(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeSchema.getQueryInputRegex(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(QueryTypeSchema.getQueryInputRegex(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(QueryTypeSchema.getQueryInputRegex(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(), contextKeyUri);
            con.add(QueryTypeSchema.getQueryInputRegex(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeSchema.getQueryInRobotsTxt(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(QueryTypeSchema.getQueryInRobotsTxt(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(QueryTypeSchema.getQueryInRobotsTxt(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(), contextKeyUri);
            con.add(QueryTypeSchema.getQueryInRobotsTxt(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeSchema.getQueryIsPageable(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(QueryTypeSchema.getQueryIsPageable(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(QueryTypeSchema.getQueryIsPageable(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(), contextKeyUri);
            con.add(QueryTypeSchema.getQueryIsPageable(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeSchema.getQueryIsDummyQueryType(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(QueryTypeSchema.getQueryIsDummyQueryType(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(QueryTypeSchema.getQueryIsDummyQueryType(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeSchema.getQueryIsDummyQueryType(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeSchema.getQueryPublicIdentifierIndex(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(QueryTypeSchema.getQueryPublicIdentifierIndex(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(QueryTypeSchema.getQueryPublicIdentifierIndex(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeSchema.getQueryPublicIdentifierIndex(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeSchema.getQueryNamespaceInputIndex(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(QueryTypeSchema.getQueryNamespaceInputIndex(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(QueryTypeSchema.getQueryNamespaceInputIndex(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeSchema.getQueryNamespaceInputIndex(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeSchema.getQueryTemplateString(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(QueryTypeSchema.getQueryTemplateString(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(QueryTypeSchema.getQueryTemplateString(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(), contextKeyUri);
            con.add(QueryTypeSchema.getQueryTemplateString(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeSchema.getQueryQueryUriTemplateString(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(QueryTypeSchema.getQueryQueryUriTemplateString(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(QueryTypeSchema.getQueryQueryUriTemplateString(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeSchema.getQueryQueryUriTemplateString(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeSchema.getQueryStandardUriTemplateString(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(QueryTypeSchema.getQueryStandardUriTemplateString(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(QueryTypeSchema.getQueryStandardUriTemplateString(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeSchema.getQueryStandardUriTemplateString(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
            con.add(QueryTypeSchema.getQueryOutputRdfXmlString(), RDF.TYPE, OWL.DATATYPEPROPERTY, contextKeyUri);
            con.add(QueryTypeSchema.getQueryOutputRdfXmlString(), RDFS.RANGE, RDFS.LITERAL, contextKeyUri);
            con.add(QueryTypeSchema.getQueryOutputRdfXmlString(), RDFS.DOMAIN, QueryTypeSchema.getQueryTypeUri(),
                    contextKeyUri);
            con.add(QueryTypeSchema.getQueryOutputRdfXmlString(), RDFS.LABEL, f.createLiteral("."), contextKeyUri);
            
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
            
            QueryTypeSchema.log.error("RepositoryException: " + re.getMessage());
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
