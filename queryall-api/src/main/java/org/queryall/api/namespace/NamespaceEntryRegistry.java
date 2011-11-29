/**
 * 
 */
package org.queryall.api.namespace;

import org.queryall.api.services.AbstractServiceLoader;

/**
 * Dynamically loads and keeps a track of the different NamespaceEntry's that are available.
 * 
 * Uses NamespaceEntryEnum objects as keys, as defined in NamespaceEntryFactory
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class NamespaceEntryRegistry extends AbstractServiceLoader<NamespaceEntryEnum, NamespaceEntryFactory>
{
    private static final Object LOCK = new Object();
    
    private static volatile NamespaceEntryRegistry defaultRegistry;
    
    public static NamespaceEntryRegistry getInstance()
    {
        if(NamespaceEntryRegistry.defaultRegistry == null)
        {
            synchronized(LOCK)
            {
                if(NamespaceEntryRegistry.defaultRegistry == null)
                {
                    NamespaceEntryRegistry.defaultRegistry = new NamespaceEntryRegistry();
                }
            }
        }
        
        return NamespaceEntryRegistry.defaultRegistry;
        
    }
    
    public NamespaceEntryRegistry()
    {
        super(NamespaceEntryFactory.class);
    }
    
    @Override
    protected NamespaceEntryEnum getKey(final NamespaceEntryFactory factory)
    {
        return factory.getEnum();
    }
    
}
