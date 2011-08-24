/**
 * 
 */
package org.queryall.api.querytype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.openrdf.model.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * QueryType implementations register themselves with this enumeration when they are loaded.
 * 
 * NOTE: This is not an enumeration as java does not support extensible enumerations, but it should
 * act like one for all purposes other than java's underpowered switch case statement.
 * 
 */
public class QueryTypeEnum
{
    private static final Logger log = LoggerFactory.getLogger(QueryTypeEnum.class);
    private static final Collection<QueryTypeEnum> ALL_QUERY_TYPES = new ArrayList<QueryTypeEnum>(5);
    
    public static Collection<QueryTypeEnum> byTypeUris(final List<URI> nextQueryTypeUris)
    {
        final List<QueryTypeEnum> results = new ArrayList<QueryTypeEnum>(QueryTypeEnum.ALL_QUERY_TYPES.size());
        
        for(final QueryTypeEnum nextQueryTypeEnum : QueryTypeEnum.ALL_QUERY_TYPES)
        {
            if(nextQueryTypeEnum.getTypeURIs().equals(nextQueryTypeUris))
            {
                results.add(nextQueryTypeEnum);
            }
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
            QueryTypeEnum.log.error("Cannot register this query type again name=" + nextQueryType.getName());
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
    
    private List<URI> typeURIs;
    
    private String name;
    
    /**
     * Create a new QueryType enum using the given name, which must be unique
     * 
     * @param nextName
     * @param nextTypeURIs
     */
    public QueryTypeEnum(final String nextName, final List<URI> nextTypeURIs)
    {
        this.setName(nextName);
        this.setTypeURI(nextTypeURIs);
    }
    
    /**
     * @return the name
     */
    public String getName()
    {
        return this.name;
    }
    
    /**
     * 
     * @return the typeURIs
     */
    public List<URI> getTypeURIs()
    {
        return Collections.unmodifiableList(this.typeURIs);
    }
    
    /**
     * The name can only be set using the constructor.
     * 
     * @param name
     *            the name to set
     */
    private void setName(final String name)
    {
        this.name = name;
    }
    
    /**
     * The type can only be set using the constructor.
     * 
     * @param typeURIs
     *            the typeURIs to set
     */
    private void setTypeURI(final List<URI> typeURI)
    {
        this.typeURIs = typeURI;
    }
}
