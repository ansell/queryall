/**
 * 
 */
package org.queryall.api.rdfrule;

import java.util.Collection;
import java.util.Set;

import org.openrdf.model.URI;
import org.queryall.api.services.QueryAllEnum;
import org.queryall.api.services.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RdfRule implementations register themselves with this enumeration when they are loaded.
 * 
 * NOTE: This is not an enumeration as java does not support extensible enumerations, but it should
 * act like one for all purposes other than java's underpowered switch case statement.
 * 
 */
public class NormalisationRuleEnum extends QueryAllEnum
{
    private static final Logger LOG = LoggerFactory.getLogger(NormalisationRuleEnum.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = NormalisationRuleEnum.LOG.isTraceEnabled();
    private static final boolean DEBUG = NormalisationRuleEnum.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = NormalisationRuleEnum.LOG.isInfoEnabled();
    
    /**
     * @deprecated Use {@link ServiceUtils#getNormalisationRuleEnumsByTypeUris(Set<URI>)} instead
     */
    @Deprecated
    public static Collection<NormalisationRuleEnum> byTypeUris(final Set<URI> nextTypeUris)
    {
        return ServiceUtils.getNormalisationRuleEnumsByTypeUris(nextTypeUris);
    }
    
    /**
     * Create a new RdfRule enum using the given name, which must be unique.
     * 
     * @param nextName
     * @param nextTypeURIs
     */
    public NormalisationRuleEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
}
