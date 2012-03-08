/**
 * 
 */
package org.queryall.api.namespace;

import java.util.Collection;
import java.util.Set;

import org.openrdf.model.URI;
import org.queryall.api.services.QueryAllEnum;
import org.queryall.api.services.ServiceUtils;
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
    private static final Logger LOG = LoggerFactory.getLogger(NamespaceEntryEnum.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = NamespaceEntryEnum.LOG.isTraceEnabled();
    private static final boolean DEBUG = NamespaceEntryEnum.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = NamespaceEntryEnum.LOG.isInfoEnabled();
    
    /**
     * @deprecated Use {@link ServiceUtils#getNamespaceEntryEnumsByTypeUris(Set<URI>)} instead
     */
    @Deprecated
    public static Collection<NamespaceEntryEnum> byTypeUris(final Set<URI> nextTypeUris)
    {
        return ServiceUtils.getNamespaceEntryEnumsByTypeUris(nextTypeUris);
    }
    
    /**
     * Create a new NamespaceEntry enum using the given name, which must be unique.
     * 
     * @param nextName
     * @param nextTypeURIs
     */
    public NamespaceEntryEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
}
