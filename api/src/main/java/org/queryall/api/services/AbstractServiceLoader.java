/**
 * 
 */
package org.queryall.api.services;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
    private static final Logger LOG = LoggerFactory.getLogger(AbstractServiceLoader.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = AbstractServiceLoader.LOG.isTraceEnabled();
    private static final boolean DEBUG = AbstractServiceLoader.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = AbstractServiceLoader.LOG.isInfoEnabled();
    
    protected ConcurrentHashMap<K, S> services = new ConcurrentHashMap<K, S>();
    
    protected AbstractServiceLoader(final Class<S> serviceClass)
    {
        final ServiceLoader<S> serviceLoader =
                java.util.ServiceLoader.load(serviceClass, serviceClass.getClassLoader());
        
        final Iterator<S> servicesIterator = serviceLoader.iterator();
        
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
                if(!servicesIterator.hasNext())
                {
                    break;
                }
                
                final S service = servicesIterator.next();
                
                final S oldService = this.add(service);
                
                if(AbstractServiceLoader.DEBUG)
                {
                    if(oldService != null)
                    {
                        AbstractServiceLoader.LOG.debug("New service {} replaces existing service {}",
                                service.getClass(), oldService.getClass());
                    }
                    
                    AbstractServiceLoader.LOG.debug("Registered service class {}", service.getClass().getName());
                }
            }
            catch(final Exception e)
            {
                AbstractServiceLoader.LOG.error("Failed to instantiate service", e);
            }
        }
    }
    
    public S add(final S service)
    {
        if(AbstractServiceLoader.DEBUG)
        {
            AbstractServiceLoader.LOG.debug("add key {} service class {}", this.getKey(service), service.getClass()
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
