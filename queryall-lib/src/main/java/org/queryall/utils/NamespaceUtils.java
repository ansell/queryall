/**
 * 
 */
package org.queryall.utils;

import java.util.Collection;
import java.util.Collections;
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
        final Collection<URI> results = allNamespacesByPrefix.get(namespacePrefix);
        
        if(results == null)
        {
            return Collections.emptyList();
        }
        else
        {
            return Collections.unmodifiableCollection(results);
        }
    }
    
}
