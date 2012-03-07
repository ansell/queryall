/**
 * 
 */
package org.queryall.api.services;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class EnumServiceLoader extends AbstractServiceLoader<String, QueryAllEnum>
{
    private static final Logger LOG = LoggerFactory.getLogger(EnumServiceLoader.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = EnumServiceLoader.LOG.isTraceEnabled();
    private static final boolean DEBUG = EnumServiceLoader.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = EnumServiceLoader.LOG.isInfoEnabled();
    
    private static volatile EnumServiceLoader defaultRegistry;
    
    public static EnumServiceLoader getInstance()
    {
        if(EnumServiceLoader.defaultRegistry == null)
        {
            synchronized(EnumServiceLoader.class)
            {
                if(EnumServiceLoader.defaultRegistry == null)
                {
                    EnumServiceLoader.defaultRegistry = new EnumServiceLoader();
                }
            }
        }
        
        return EnumServiceLoader.defaultRegistry;
        
    }
    
    public EnumServiceLoader()
    {
        super(QueryAllEnum.class);
    }
    
    @Override
    public Collection<QueryAllEnum> getAll()
    {
        if(EnumServiceLoader.TRACE)
        {
            for(final String nextKey : this.services.keySet())
            {
                EnumServiceLoader.LOG.trace("nextKey={} nextValue={}", nextKey, this.services.get(nextKey));
            }
        }
        
        return super.getAll();
    }
    
    @Override
    protected String getKey(final QueryAllEnum service)
    {
        return service.getName();
    }
}
