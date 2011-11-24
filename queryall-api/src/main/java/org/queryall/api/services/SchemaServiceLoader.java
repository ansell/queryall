/**
 * 
 */
package org.queryall.api.services;

import java.util.Collection;

import org.queryall.api.base.QueryAllSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class SchemaServiceLoader extends AbstractServiceLoader<String, QueryAllSchema>
{
    private static final Logger log = LoggerFactory.getLogger(SchemaServiceLoader.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = SchemaServiceLoader.log.isTraceEnabled();
    private static final boolean _DEBUG = SchemaServiceLoader.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = SchemaServiceLoader.log.isInfoEnabled();
    
    private static SchemaServiceLoader defaultRegistry;
    
    public static synchronized SchemaServiceLoader getInstance()
    {
        if(SchemaServiceLoader.defaultRegistry == null)
        {
            SchemaServiceLoader.defaultRegistry = new SchemaServiceLoader();
        }
        
        return SchemaServiceLoader.defaultRegistry;
        
    }
    
    public SchemaServiceLoader()
    {
        super(QueryAllSchema.class);
    }
    
    @Override
    public Collection<QueryAllSchema> getAll()
    {
        if(SchemaServiceLoader._DEBUG)
        {
            for(final String nextKey : this.services.keySet())
            {
                SchemaServiceLoader.log.debug("nextKey={} nextValue={}", nextKey, this.services.get(nextKey));
            }
        }
        
        return super.getAll();
    }
    
    @Override
    protected String getKey(final QueryAllSchema service)
    {
        return service.getName();
    }
}
