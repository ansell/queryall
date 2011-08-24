/**
 * 
 */
package org.queryall.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.queryall.api.Profile;
import org.queryall.api.QueryType;
import org.queryall.api.utils.QueryTypeEnum;
import org.queryall.api.utils.QueryTypeParser;
import org.queryall.api.utils.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public final class QueryTypeUtils implements QueryTypeParser
{
    public static final Logger log = LoggerFactory.getLogger(QueryTypeUtils.class);
    public static final boolean _TRACE = QueryTypeUtils.log.isTraceEnabled();
    public static final boolean _DEBUG = QueryTypeUtils.log.isDebugEnabled();
    public static final boolean _INFO = QueryTypeUtils.log.isInfoEnabled();
    
    public static Collection<QueryType> getQueryTypesByUri(final Map<URI, QueryType> allQueryTypes,
            final URI queryTypeUri)
    {
        final Collection<QueryType> results = new HashSet<QueryType>();
        for(final QueryType nextQueryType : allQueryTypes.values())
        {
            if(nextQueryType.getKey().equals(queryTypeUri))
            {
                results.add(nextQueryType);
            }
        }
        return results;
    }
    
    public static Collection<QueryType> getQueryTypesMatchingQueryString(final String queryString,
            final List<Profile> profileList, final Map<URI, QueryType> allQueryTypes,
            final boolean recogniseImplicitQueryInclusions, final boolean includeNonProfileMatchedQueries)
    {
        if(QueryTypeUtils._DEBUG)
        {
            QueryTypeUtils.log.debug("getQueryTypesMatchingQueryString: profileList.size()=" + profileList.size());
            
            if(QueryTypeUtils._TRACE)
            {
                for(final Profile nextProfile : profileList)
                {
                    QueryTypeUtils.log.trace("getQueryTypesMatchingQueryString: nextProfile.getKey()="
                            + nextProfile.getKey().stringValue());
                }
            }
        }
        
        final Collection<QueryType> results = new HashSet<QueryType>();
        
        for(final QueryType nextQuery : allQueryTypes.values())
        {
            if(nextQuery.matchesQueryString(queryString))
            {
                if(QueryTypeUtils._TRACE)
                {
                    QueryTypeUtils.log
                            .trace("getQueryTypesMatchingQueryString: tentative, pre-profile-check match for"
                                    + " nextQuery.getKey()=" + nextQuery.getKey().stringValue() + " queryString="
                                    + queryString);
                }
                if(nextQuery.isUsedWithProfileList(profileList, recogniseImplicitQueryInclusions,
                        includeNonProfileMatchedQueries))
                {
                    if(QueryTypeUtils._DEBUG)
                    {
                        QueryTypeUtils.log.debug("getQueryTypesMatchingQueryString: profileList suitable for"
                                + " nextQuery.getKey()=" + nextQuery.getKey().stringValue() + " queryString="
                                + queryString);
                    }
                    results.add(nextQuery);
                }
                else if(QueryTypeUtils._TRACE)
                {
                    QueryTypeUtils.log
                            .trace("getQueryTypesMatchingQueryString: profileList not suitable for"
                                    + " nextQuery.getKey()=" + nextQuery.getKey().stringValue() + " queryString="
                                    + queryString);
                }
            }
        }
        return results;
    }
    
    /**
	 * 
	 */
    public QueryTypeUtils()
    {
        // TODO Auto-generated constructor stub
    }

    @Override
    public QueryType createQueryType(Collection<Statement> rdfStatements, URI subjectKey, int modelVersion)
        throws IllegalArgumentException
    {
        List<URI> typeStatements = new ArrayList<URI>(4);
        
        for(Statement nextStatement : rdfStatements)
        {
            if(nextStatement.getPredicate().equals(RDF.TYPE) && nextStatement.getSubject().equals(subjectKey) && nextStatement.getObject() instanceof URI)
            {
                typeStatements.add((URI)nextStatement.getObject());
            }
        }
        
        Collection<QueryTypeEnum> matchingQueryTypes = QueryTypeEnum.byTypeUris(typeStatements);
        
        for(QueryTypeEnum nextQueryType : matchingQueryTypes)
        {
            ServiceUtils.createQueryTypeParser(nextQueryType);
        }
        
        return null;
    }
    
}
