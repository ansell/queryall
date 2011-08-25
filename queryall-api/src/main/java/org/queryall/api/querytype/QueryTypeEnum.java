/**
 * 
 */
package org.queryall.api.querytype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
    private static final Logger log = LoggerFactory.getLogger(QueryTypeEnum.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = QueryTypeEnum.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = QueryTypeEnum.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = QueryTypeEnum.log.isInfoEnabled();
    
    protected static final Collection<QueryTypeEnum> ALL_QUERY_TYPES = new ArrayList<QueryTypeEnum>(5);
    
    public static Collection<QueryTypeEnum> byTypeUris(final List<URI> nextQueryTypeUris)
    {
        if(nextQueryTypeUris.size() == 0)
        {
            QueryTypeEnum.log.info("found an empty URI set for nextQueryTypeUris=" + nextQueryTypeUris);
            return Collections.emptyList();
        }
        
        final List<QueryTypeEnum> results = new ArrayList<QueryTypeEnum>(QueryTypeEnum.ALL_QUERY_TYPES.size());
        
        for(final QueryTypeEnum nextQueryTypeEnum : QueryTypeEnum.ALL_QUERY_TYPES)
        {
            boolean matching = (nextQueryTypeEnum.getTypeURIs().size() == nextQueryTypeUris.size());
            
            for(final URI nextURI : nextQueryTypeEnum.getTypeURIs())
            {
                if(!nextQueryTypeUris.contains(nextURI))
                {
                    QueryTypeEnum.log.info("found an empty URI set for nextURI=" + nextURI.stringValue());
                    
                    matching = false;
                }
            }
            
            if(matching)
            {
                QueryTypeEnum.log.info("found an matching URI set for nextQueryTypeUris=" + nextQueryTypeUris);
                results.add(nextQueryTypeEnum);
            }
        }
        
        QueryTypeEnum.log.info("returning results.size()=" + results.size() + " for nextQueryTypeUris="
                + nextQueryTypeUris);
        
        return results;
    }
    
    /**
     * Registers the specified query type.
     */
    public static void register(final QueryTypeEnum nextQueryType)
    {
        if(QueryTypeEnum.valueOf(nextQueryType.getName()) != null)
        {
            QueryAllEnum.log.error("Cannot register this query type again name=" + nextQueryType.getName());
        }
        else
        {
            QueryTypeEnum.ALL_QUERY_TYPES.add(nextQueryType);
        }
    }
    
    public static QueryTypeEnum register(final String name, final List<URI> typeURIs)
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
     * Create a new QueryType enum using the given name, which must be unique
     * 
     * @param nextName
     * @param nextTypeURIs
     */
    public QueryTypeEnum(final String nextName, final List<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
}
