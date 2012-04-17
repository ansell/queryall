/**
 * 
 */
package org.queryall.api.project;

import java.util.Collection;
import java.util.Set;

import org.openrdf.model.URI;
import org.queryall.api.services.QueryAllEnum;
import org.queryall.api.services.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Project implementations register themselves with this enumeration when they are loaded.
 * 
 * NOTE: This is not an enumeration as java does not support extensible enumerations, but it should
 * act like one for all purposes other than java's underpowered switch case statement.
 * 
 */
public class ProjectEnum extends QueryAllEnum
{
    private static final Logger LOG = LoggerFactory.getLogger(ProjectEnum.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = ProjectEnum.LOG.isTraceEnabled();
    private static final boolean DEBUG = ProjectEnum.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = ProjectEnum.LOG.isInfoEnabled();
    
    /**
     * @deprecated Use {@link ServiceUtils#getProjectEnumsByTypeUris(Set<URI>)} instead
     */
    @Deprecated
    public static Collection<ProjectEnum> byTypeUris(final Set<URI> nextTypeUris)
    {
        return ServiceUtils.getProjectEnumsByTypeUris(nextTypeUris);
    }
    
    /**
     * Create a new Project enum using the given name, which must be unique.
     * 
     * @param nextName
     * @param nextTypeURIs
     */
    public ProjectEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
}
