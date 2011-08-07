/**
 * 
 */
package org.queryall.helpers;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import org.openrdf.model.URI;
import org.queryall.api.Provider;
import org.queryall.impl.QueryTypeImpl;

/**
 * @author karina
 *
 */
public final class ProviderUtils
{

	/**
	 * 
	 */
	public ProviderUtils()
	{
		// TODO Auto-generated constructor stub
	}

	public static Map<URI, Provider> getProvidersForQueryType(Map<URI, Provider> allProviders, URI nextQueryType)
	{
	    final Map<URI, Provider> results = new TreeMap<URI, Provider>();
	    
	    for(final Provider nextProvider : allProviders.values())
	    {
	        if(nextProvider.containsQueryTypeUri(nextQueryType))
	        {
	            results.put(nextProvider.getKey(), nextProvider);
	        }
	    }

	    if(Settings._DEBUG)
	    {
	        Settings.log.debug("Settings.getProvidersForQueryType: Found "
	                + results.size() + " providers for customService="
	                + nextQueryType.stringValue());
	    }
	    if(Settings._TRACE)
	    {
	        for(final Provider nextResult : results.values())
	        {
	            Settings.log
	                    .trace("Settings.getProvidersForQueryType: nextResult="
	                            + nextResult.toString());
	        }
	    }
	    return results;
	}

	public static Map<URI, Provider> getProvidersForNamespaceUris(Map<URI, Provider> allProviders,
	        Collection<Collection<URI>> namespaceUris, URI namespaceMatchMethod)
	{
	    if((namespaceUris == null) || (namespaceUris.size() == 0))
	    {
	        if(Settings._DEBUG)
	        {
	            Settings.log
	                    .debug("Settings.getProvidersForNamespaceUris: namespaceUris was either null or empty");
	        }
	        return Collections.emptyMap();
	    }
	    if(Settings._TRACE)
	    {
	        Settings.log
	                .trace("Settings.getProvidersForNamespaceUris: namespaceUris="
	                        + namespaceUris);
	    }
	    final Map<URI, Provider> results = new TreeMap<URI, Provider>();
	
	    for(final Provider nextProvider : allProviders.values())
	    {
	        boolean anyFound = false;
	        boolean allFound = true;
	        if(Settings._TRACE)
	        {
	            Settings.log
	                    .trace("Settings.getProvidersForNamespaceUris: nextProvider.getKey()="
	                            + nextProvider.getKey().stringValue());
	        }
	        
	        
	        for(final Collection<URI> nextNamespaceUriList : namespaceUris)
	        {
	            if(nextNamespaceUriList == null)
	            {
	                if(Settings._DEBUG)
	                {
	                    Settings.log
	                            .debug("Settings.getProvidersForNamespaceUris: nextNamespaceUriList was null");
	                }
	                continue;
	            }
	            if(Settings._TRACE)
	            {
	                Settings.log
	                        .trace("Settings.getProvidersForNamespaceUris: nextNamespaceUriList="
	                                + nextNamespaceUriList);
	            }
	            boolean somethingFound = false;
	            for(final URI nextNamespaceUri : nextNamespaceUriList)
	            {
	                if(Settings._TRACE)
	                {
	                    Settings.log
	                            .trace("Settings.getProvidersForNamespaceUris: nextNamespaceUri="
	                                    + nextNamespaceUri);
	                }
	                if(nextProvider.containsNamespaceUri(nextNamespaceUri))
	                {
	                    somethingFound = true;
	                    break;
	                }
	            }
	            if(somethingFound)
	            {
	                anyFound = true;
	            }
	            else
	            {
	                allFound = false;
	            }
	        }
	        if(anyFound
	                && namespaceMatchMethod.equals(QueryTypeImpl.getNamespaceMatchAnyUri()))
	        {
	            results.put(nextProvider.getKey(), nextProvider);
	        }
	        else if(allFound
	                && namespaceMatchMethod.equals(QueryTypeImpl.getNamespaceMatchAllUri()))
	        {
	            results.put(nextProvider.getKey(), nextProvider);
	        }
	    }
	    return results;
	}

}
