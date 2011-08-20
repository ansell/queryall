/**
 * 
 */
package org.queryall.utils;

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
    
    public static Collection<URI> getNamespaceUrisForPrefix(final Map<String, Collection<URI>> allNamespacesByPrefix,
            final String namespacePrefix)
    {
        Collection<URI> results = new HashSet<URI>();
        
        results = allNamespacesByPrefix.get(namespacePrefix);
        
        if(results == null)
        {
            return null;
        }
        else
        {
            return Collections.unmodifiableCollection(results);
        }
    }
    
    /**
	 * 
	 */
    public NamespaceUtils()
    {
        // TODO Auto-generated constructor stub
    }
    
}
