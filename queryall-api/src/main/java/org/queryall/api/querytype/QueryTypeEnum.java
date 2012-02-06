/**
 * 
 */
package org.queryall.api.querytype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.model.URI;
import org.queryall.api.services.QueryAllEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * QueryType implementations register themselves with this enumeration when they are loaded.
 * 
 * NOTE: This is not an enumeration as java does not support extensible enumerations, but it should
 * act like one for all purposes other than java's underpowered switch case statement.
 * 
 */
public class QueryTypeEnum extends QueryAllEnum
{
    private static final Logger LOG = LoggerFactory.getLogger(QueryTypeEnum.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = QueryTypeEnum.LOG.isTraceEnabled();
    private static final boolean DEBUG = QueryTypeEnum.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = QueryTypeEnum.LOG.isInfoEnabled();
    
    protected static final Set<QueryTypeEnum> ALL_QUERY_TYPES = new HashSet<QueryTypeEnum>();
    
    public static Collection<QueryTypeEnum> byTypeUris(final Set<URI> nextTypeUris)
    {
        if(nextTypeUris.size() == 0)
        {
            if(QueryTypeEnum.DEBUG)
            {
                QueryTypeEnum.LOG.debug("found an empty URI set for nextQueryTypeUris=" + nextTypeUris);
            }
            return Collections.emptyList();
        }
        
        final List<QueryTypeEnum> results = new ArrayList<QueryTypeEnum>(QueryTypeEnum.ALL_QUERY_TYPES.size());
        
        for(final QueryTypeEnum nextEnum : QueryTypeEnum.ALL_QUERY_TYPES)
        {
            if(nextEnum.matchForTypeUris(nextTypeUris))
            {
                if(QueryTypeEnum.DEBUG)
                {
                    QueryTypeEnum.LOG.debug("found an matching URI set for nextQueryTypeUris=" + nextTypeUris);
                }
                
                results.add(nextEnum);
            }
        }
        
        if(QueryTypeEnum.DEBUG)
        {
            QueryTypeEnum.LOG.debug("returning results.size()=" + results.size() + " for nextQueryTypeUris="
                    + nextTypeUris);
        }
        
        return results;
    }
    
    /**
     * Registers the specified query type.
     */
    public static void register(final QueryTypeEnum nextQueryType)
    {
        if(QueryTypeEnum.valueOf(nextQueryType.getName()) != null)
        {
            if(QueryTypeEnum.DEBUG)
            {
                QueryTypeEnum.LOG.debug("Cannot register this query type again name=" + nextQueryType.getName());
            }
        }
        else
        {
            QueryTypeEnum.ALL_QUERY_TYPES.add(nextQueryType);
        }
    }
    
    public static QueryTypeEnum register(final String name, final Set<URI> typeURIs)
    {
        final QueryTypeEnum newQueryTypeEnum = new QueryTypeEnum(name, typeURIs);
        QueryTypeEnum.register(newQueryTypeEnum);
        return newQueryTypeEnum;
    }
    
    public static QueryTypeEnum valueOf(final String string)
    {
        for(final QueryTypeEnum nextQueryTypeEnum : QueryTypeEnum.ALL_QUERY_TYPES)
        {
            if(nextQueryTypeEnum.getName().equals(string))
            {
                return nextQueryTypeEnum;
            }
        }
        
        return null;
    }
    
    /**
     * Returns all known/registered query types.
     */
    public static Collection<QueryTypeEnum> values()
    {
        return Collections.unmodifiableCollection(QueryTypeEnum.ALL_QUERY_TYPES);
    }
    
    /**
     * Create a new QueryType enum using the given name, which must be unique.
     * 
     * @param nextName
     * @param nextTypeURIs
     */
    public QueryTypeEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
        QueryTypeEnum.ALL_QUERY_TYPES.add(this);
    }
}
