/**
 * 
 */
package org.queryall.api.ruletest;

import java.util.Collection;
import java.util.Set;

import org.openrdf.model.URI;
import org.queryall.api.services.QueryAllEnum;
import org.queryall.api.services.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RuleTest implementations register themselves with this enumeration when they are loaded.
 * 
 * NOTE: This is not an enumeration as java does not support extensible enumerations, but it should
 * act like one for all purposes other than java's underpowered switch case statement.
 * 
 */
public class RuleTestEnum extends QueryAllEnum
{
    private static final Logger LOG = LoggerFactory.getLogger(RuleTestEnum.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = RuleTestEnum.LOG.isTraceEnabled();
    private static final boolean DEBUG = RuleTestEnum.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = RuleTestEnum.LOG.isInfoEnabled();
    
    /**
     * @deprecated Use {@link ServiceUtils#getRuleTestEnumsByTypeUris(Set<URI>)} instead
     */
    @Deprecated
    public static Collection<RuleTestEnum> byTypeUris(final Set<URI> nextTypeUris)
    {
        return ServiceUtils.getRuleTestEnumsByTypeUris(nextTypeUris);
    }
    
    /**
     * Create a new RuleTest enum using the given name, which must be unique.
     * 
     * @param nextName
     * @param nextTypeURIs
     */
    public RuleTestEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
}
