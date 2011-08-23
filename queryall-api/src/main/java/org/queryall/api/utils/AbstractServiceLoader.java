/**
 * 
 */
package org.queryall.api.utils;

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
 *
 */
public abstract class AbstractServiceLoader<K, S> 
{
		protected final Logger logger = LoggerFactory.getLogger(this.getClass());

		protected Map<K, S> services = new HashMap<K, S>();

		protected AbstractServiceLoader(Class<S> serviceClass) 
		{
			ServiceLoader<S> serviceLoader = java.util.ServiceLoader.load(serviceClass, serviceClass.getClassLoader());
			
			Iterator<S> services = serviceLoader.iterator();
			
			while (true) 
			{
				try 
				{
					if (services.hasNext()) 
					{
						S service = services.next();

						S oldService = add(service);

						if (oldService != null) 
						{
							logger.warn("New service {} replaces existing service {}", service.getClass(),
									oldService.getClass());
						}

						logger.debug("Registered service class {}", service.getClass().getName());
					}
					else 
					{
						break;
					}
				}
				catch (Error e) 
				{
					logger.error("Failed to instantiate service", e);
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

		protected abstract K getKey(S service);	
}
