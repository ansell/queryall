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
    private static final Logger log = LoggerFactory.getLogger(EnumServiceLoader.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = EnumServiceLoader.log.isTraceEnabled();
    private static final boolean _DEBUG = EnumServiceLoader.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = EnumServiceLoader.log.isInfoEnabled();
    
    private static EnumServiceLoader defaultRegistry;
    
    public static synchronized EnumServiceLoader getInstance()
    {
        if(EnumServiceLoader.defaultRegistry == null)
        {
            EnumServiceLoader.defaultRegistry = new EnumServiceLoader();
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
        if(EnumServiceLoader._DEBUG)
        {
            for(final String nextKey : this.services.keySet())
            {
                EnumServiceLoader.log.debug("nextKey={} nextValue={}", nextKey, this.services.get(nextKey));
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
