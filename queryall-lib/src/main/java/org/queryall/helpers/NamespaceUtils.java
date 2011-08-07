/**
 * 
 */
package org.queryall.helpers;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import org.openrdf.model.URI;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class NamespaceUtils
{

	/**
	 * 
	 */
	public NamespaceUtils()
	{
		// TODO Auto-generated constructor stub
	}

	public static Collection<URI> getNamespaceUrisForTitle(Map<String, Collection<URI>> allNamespacesByPrefix, String namespacePrefix)
	{
	    Collection<URI> results = new HashSet<URI>();
	    
	    results = allNamespacesByPrefix.get(namespacePrefix);
	
	    if(results == null)
	    	return null;
	    else
	    	return Collections.unmodifiableCollection(results);
	}

}
