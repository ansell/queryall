/**
 * 
 */
package org.queryall.api.services;

import java.util.Collection;

/**
 *
 */
public class EnumServiceLoader extends AbstractServiceLoader<String, QueryAllEnum>
{
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
    protected String getKey(final QueryAllEnum service)
    {
        return service.getName();
    }
    
    public Collection<QueryAllEnum> getAll()
    {
        for(String nextKey : this.services.keySet())
        {
            log.info("nextKey={} nextValue={}", nextKey, this.services.get(nextKey));
        }
        
        return super.getAll();
    }
}
