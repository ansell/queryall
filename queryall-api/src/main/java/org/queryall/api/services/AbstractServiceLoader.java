/**
 * 
 */
package org.queryall.api.services;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract service loader.
 * 
 * This is a java 6 version of info.aduna.lang.service.ServiceRegistry.
 * 
 */
public abstract class AbstractServiceLoader<K, S>
{
    private static final Logger log = LoggerFactory.getLogger(AbstractServiceLoader.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = AbstractServiceLoader.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = AbstractServiceLoader.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = AbstractServiceLoader.log.isInfoEnabled();
    
    protected Map<K, S> services = Collections.synchronizedMap(new HashMap<K, S>());
    
    protected AbstractServiceLoader(final Class<S> serviceClass)
    {
        final ServiceLoader<S> serviceLoader =
                java.util.ServiceLoader.load(serviceClass, serviceClass.getClassLoader());
        
        final Iterator<S> services = serviceLoader.iterator();
        
        // Iterator<S> services =
        // javax.imageio.spi.ServiceRegistry.lookupProviders(serviceClass,
        // serviceClass.getClassLoader());
        
        // Loop through this way so we can catch all errors for each iteration
        // and only discard
        // plugins that are invalid
        while(true)
        {
            try
            {
                if(!services.hasNext())
                {
                    break;
                }
                
                final S service = services.next();
                
                final S oldService = this.add(service);
                
                if(oldService != null)
                {
                    if(_DEBUG)
                    {
                        AbstractServiceLoader.log.debug("New service {} replaces existing service {}", service.getClass(),
                            oldService.getClass());
                    }
                }
                
                if(_DEBUG)
                {
                    AbstractServiceLoader.log.debug("Registered service class {}", service.getClass().getName());
                }
            }
            catch(final Error e)
            {
                AbstractServiceLoader.log.error("Failed to instantiate service", e);
            }
        }
    }
    
    public S add(final S service)
    {
        if(_DEBUG)
        {
            AbstractServiceLoader.log.debug("add key {} service class {}", this.getKey(service), service.getClass()
                .getName());
        }
        
        return this.services.put(this.getKey(service), service);
    }
    
    public S get(final K key)
    {
        return this.services.get(key);
    }
    
    public Collection<S> getAll()
    {
        return Collections.unmodifiableCollection(this.services.values());
    }
    
    /**
     * This method needs to be overriden to provide a unique key, based on the generic key type (K)
     * to use as the identifier for the given service.
     * 
     * The key must be unique within this registry.
     * 
     * @param service
     *            A service to return a key for
     * 
     * @return The unique key for the given service.
     */
    protected abstract K getKey(S service);
    
    public Set<K> getKeys()
    {
        return Collections.unmodifiableSet(this.services.keySet());
    }
    
    public boolean has(final K key)
    {
        return this.services.containsKey(key);
    }
    
    public void remove(final S service)
    {
        this.services.remove(this.getKey(service));
    }
}
