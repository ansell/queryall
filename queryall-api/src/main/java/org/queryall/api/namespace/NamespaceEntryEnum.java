/**
 * 
 */
package org.queryall.api.namespace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.openrdf.model.URI;
import org.queryall.api.services.QueryAllEnum;

/**
 * NamespaceEntry implementations register themselves with this enumeration when they are loaded.
 * 
 * NOTE: This is not an enumeration as java does not support extensible enumerations, but it should
 * act like one for all purposes other than java's underpowered switch case statement.
 * 
 */
public class NamespaceEntryEnum extends QueryAllEnum
{
    protected static final Collection<NamespaceEntryEnum> ALL_PROJECTS = new ArrayList<NamespaceEntryEnum>(5);
    
    public static Collection<NamespaceEntryEnum> byTypeUris(final List<URI> nextNamespaceEntryUris)
    {
        final List<NamespaceEntryEnum> results =
                new ArrayList<NamespaceEntryEnum>(NamespaceEntryEnum.ALL_PROJECTS.size());
        
        for(final NamespaceEntryEnum nextNamespaceEntryEnum : NamespaceEntryEnum.ALL_PROJECTS)
        {
            if(nextNamespaceEntryEnum.getTypeURIs().equals(nextNamespaceEntryUris))
            {
                results.add(nextNamespaceEntryEnum);
            }
        }
        
        return results;
    }
    
    /**
     * Registers the specified namespace entry.
     */
    public static void register(final NamespaceEntryEnum nextNamespaceEntry)
    {
        if(NamespaceEntryEnum.valueOf(nextNamespaceEntry.getName()) != null)
        {
            QueryAllEnum.log.error("Cannot register this namespace entry again name=" + nextNamespaceEntry.getName());
        }
        else
        {
            NamespaceEntryEnum.ALL_PROJECTS.add(nextNamespaceEntry);
        }
    }
    
    public static NamespaceEntryEnum register(final String name, final List<URI> typeURIs)
    {
        final NamespaceEntryEnum newNamespaceEntryEnum = new NamespaceEntryEnum(name, typeURIs);
        NamespaceEntryEnum.register(newNamespaceEntryEnum);
        return newNamespaceEntryEnum;
    }
    
    public static NamespaceEntryEnum valueOf(final String string)
    {
        for(final NamespaceEntryEnum nextNamespaceEntryEnum : NamespaceEntryEnum.ALL_PROJECTS)
        {
            if(nextNamespaceEntryEnum.getName().equals(string))
            {
                return nextNamespaceEntryEnum;
            }
        }
        
        return null;
    }
    
    /**
     * Returns all known/registered namespace entrys.
     */
    public static Collection<NamespaceEntryEnum> values()
    {
        return Collections.unmodifiableCollection(NamespaceEntryEnum.ALL_PROJECTS);
    }
    
    /**
     * Create a new NamespaceEntry enum using the given name, which must be unique
     * 
     * @param nextName
     * @param nextTypeURIs
     */
    public NamespaceEntryEnum(final String nextName, final List<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
        NamespaceEntryEnum.ALL_PROJECTS.add(this);
    }
}
