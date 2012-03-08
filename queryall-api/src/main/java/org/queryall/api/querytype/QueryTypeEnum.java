/**
 * 
 */
package org.queryall.api.querytype;

import java.util.Collection;
import java.util.Set;

import org.openrdf.model.URI;
import org.queryall.api.services.QueryAllEnum;
import org.queryall.api.services.ServiceUtils;
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
    
    /**
     * @deprecated Use {@link ServiceUtils#getQueryTypeEnumsByTypeUris(Set<URI>)} instead
     */
    @Deprecated
    public static Collection<QueryTypeEnum> byTypeUris(final Set<URI> nextTypeUris)
    {
        return ServiceUtils.getQueryTypeEnumsByTypeUris(nextTypeUris);
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
    }
}
