/**
 * 
 */
package org.queryall.api.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
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
		protected final static Logger log = LoggerFactory.getLogger(AbstractServiceLoader.class);

		protected Map<K, S> services = new ConcurrentHashMap<K, S>();

		protected AbstractServiceLoader(Class<S> serviceClass) 
		{
			ServiceLoader<S> serviceLoader = java.util.ServiceLoader.load(serviceClass, serviceClass.getClassLoader());
			
			Iterator<S> services = serviceLoader.iterator();
			
			// Loop through this way so we can catch all errors for each iteration and only discard plugins that are invalid
			while(true) 
			{
				try 
				{
					if(!services.hasNext()) 
					{
                        break;
					}

					S service = services.next();

					S oldService = add(service);

					if(oldService != null) 
					{
						log.warn("New service {} replaces existing service {}", service.getClass(),
								oldService.getClass());
					}

					log.debug("Registered service class {}", service.getClass().getName());
				}
				catch (Error e) 
				{
					log.error("Failed to instantiate service", e);
				}
			}
		}

		public S add(S service) 
		{
			return services.put(getKey(service), service);
		}

		public void remove(S service) 
		{
			services.remove(getKey(service));
		}

		public S get(K key) 
		{
			return services.get(key);
		}

		public boolean has(K key) 
		{
			return services.containsKey(key);
		}

		public Collection<S> getAll() 
		{
			return Collections.unmodifiableCollection(services.values());
		}

		public Set<K> getKeys() 
		{
			return Collections.unmodifiableSet(services.keySet());
		}

		/**
		 * This method needs to be overriden to provide a unique key, based on the generic key type (K) to use as the identifier for the given service.
		 * 
		 * The key must be unique within this registry.
		 * 
		 * @param service A service to return a key for
		 * 
		 * @return The unique key for the given service.
		 */
		protected abstract K getKey(S service);	
}
