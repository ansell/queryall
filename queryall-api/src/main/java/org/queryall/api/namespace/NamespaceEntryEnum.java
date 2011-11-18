/**
 * 
 */
package org.queryall.api.namespace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.model.URI;
import org.queryall.api.services.QueryAllEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NamespaceEntry implementations register themselves with this enumeration when they are loaded.
 * 
 * NOTE: This is not an enumeration as java does not support extensible enumerations, but it should
 * act like one for all purposes other than java's underpowered switch case statement.
 * 
 */
public class NamespaceEntryEnum extends QueryAllEnum
{
    private static final Logger log = LoggerFactory.getLogger(NamespaceEntryEnum.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = NamespaceEntryEnum.log.isTraceEnabled();
    private static final boolean _DEBUG = NamespaceEntryEnum.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = NamespaceEntryEnum.log.isInfoEnabled();
    
    protected static final Set<NamespaceEntryEnum> ALL_NAMESPACE_ENTRIES = new HashSet<NamespaceEntryEnum>();
    
    public static Collection<NamespaceEntryEnum> byTypeUris(final Set<URI> nextTypeUris)
    {
        if(nextTypeUris.size() == 0)
        {
            if(NamespaceEntryEnum._DEBUG)
            {
                NamespaceEntryEnum.log.debug("found an empty URI set for nextNamespaceEntryUris=" + nextTypeUris);
            }
            return Collections.emptyList();
        }
        
        final List<NamespaceEntryEnum> results =
                new ArrayList<NamespaceEntryEnum>(NamespaceEntryEnum.ALL_NAMESPACE_ENTRIES.size());
        
        for(final NamespaceEntryEnum nextEnum : NamespaceEntryEnum.ALL_NAMESPACE_ENTRIES)
        {
            if(nextEnum.matchForTypeUris(nextTypeUris))
            {
                if(NamespaceEntryEnum._DEBUG)
                {
                    NamespaceEntryEnum.log
                            .debug("found a matching URI set for nextNamespaceEntryUris=" + nextTypeUris);
                }
                results.add(nextEnum);
            }
        }
        
        if(NamespaceEntryEnum._DEBUG)
        {
            NamespaceEntryEnum.log.debug("returning results.size()=" + results.size() + " for nextNamespaceEntryUris="
                    + nextTypeUris);
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
            if(NamespaceEntryEnum._DEBUG)
            {
                NamespaceEntryEnum.log.debug("Cannot register this namespace entry again name="
                        + nextNamespaceEntry.getName());
            }
        }
        else
        {
            NamespaceEntryEnum.ALL_NAMESPACE_ENTRIES.add(nextNamespaceEntry);
        }
    }
    
    public static NamespaceEntryEnum register(final String name, final Set<URI> typeURIs)
    {
        final NamespaceEntryEnum newNamespaceEntryEnum = new NamespaceEntryEnum(name, typeURIs);
        NamespaceEntryEnum.register(newNamespaceEntryEnum);
        return newNamespaceEntryEnum;
    }
    
    public static NamespaceEntryEnum valueOf(final String string)
    {
        for(final NamespaceEntryEnum nextNamespaceEntryEnum : NamespaceEntryEnum.ALL_NAMESPACE_ENTRIES)
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
        return Collections.unmodifiableCollection(NamespaceEntryEnum.ALL_NAMESPACE_ENTRIES);
    }
    
    /**
     * Create a new NamespaceEntry enum using the given name, which must be unique
     * 
     * @param nextName
     * @param nextTypeURIs
     */
    public NamespaceEntryEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
        NamespaceEntryEnum.ALL_NAMESPACE_ENTRIES.add(this);
    }
}
