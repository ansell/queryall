/**
 * 
 */
package org.queryall.api.profile;

import java.util.Collection;
import java.util.Set;

import org.openrdf.model.URI;
import org.queryall.api.services.QueryAllEnum;
import org.queryall.api.services.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Profile implementations register themselves with this enumeration when they are loaded.
 * 
 * NOTE: This is not an enumeration as java does not support extensible enumerations, but it should
 * act like one for all purposes other than java's underpowered switch case statement.
 * 
 */
public class ProfileEnum extends QueryAllEnum
{
    private static final Logger LOG = LoggerFactory.getLogger(ProfileEnum.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = ProfileEnum.LOG.isTraceEnabled();
    private static final boolean DEBUG = ProfileEnum.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = ProfileEnum.LOG.isInfoEnabled();
    
    /**
     * @deprecated Use {@link ServiceUtils#getProfileEnumsByTypeUris(Set<URI>)} instead
     */
    @Deprecated
    public static Collection<ProfileEnum> byTypeUris(final Set<URI> nextTypeUris)
    {
        return ServiceUtils.getProfileEnumsByTypeUris(nextTypeUris);
    }
    
    /**
     * Create a new Profile enum using the given name, which must be unique.
     * 
     * @param nextName
     * @param nextTypeURIs
     */
    public ProfileEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
}
