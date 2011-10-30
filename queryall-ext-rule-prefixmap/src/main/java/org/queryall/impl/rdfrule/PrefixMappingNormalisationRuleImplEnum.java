/**
 * 
 */
package org.queryall.impl.rdfrule;

import java.util.Set;

import org.kohsuke.MetaInfServices;
import org.openrdf.model.URI;
import org.queryall.api.rdfrule.NormalisationRuleEnum;
import org.queryall.api.services.QueryAllEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices(QueryAllEnum.class)
public class PrefixMappingNormalisationRuleImplEnum extends NormalisationRuleEnum
{
    private static final Logger log = LoggerFactory.getLogger(PrefixMappingNormalisationRuleImplEnum.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = PrefixMappingNormalisationRuleImplEnum.log.isTraceEnabled();
    private static final boolean _DEBUG = PrefixMappingNormalisationRuleImplEnum.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = PrefixMappingNormalisationRuleImplEnum.log.isInfoEnabled();
    
    public static final NormalisationRuleEnum SIMPLE_PREFIX_MAPPING_NORMALISATION_RULE_IMPL_ENUM =
            new PrefixMappingNormalisationRuleImplEnum();
    
    // static
    // {
    // NormalisationRuleEnum.register(REGEX_NORMALISATION_RULE_IMPL_ENUM);
    // }
    
    public PrefixMappingNormalisationRuleImplEnum()
    {
        this(PrefixMappingNormalisationRuleImpl.class.getName(), PrefixMappingNormalisationRuleImpl.myTypes());
        
        if(PrefixMappingNormalisationRuleImplEnum._DEBUG)
        {
            PrefixMappingNormalisationRuleImplEnum.log.debug("PrefixMappingNormalisationRuleImplEnum() registered");
        }
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public PrefixMappingNormalisationRuleImplEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
        
        if(PrefixMappingNormalisationRuleImplEnum._DEBUG)
        {
            PrefixMappingNormalisationRuleImplEnum.log
                    .debug("PrefixMappingNormalisationRuleImplEnum(String, List<URI>) registered");
        }
    }
    
}
