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
public class SimplePrefixMappingNormalisationRuleImplEnum extends NormalisationRuleEnum
{
    private static final Logger log = LoggerFactory.getLogger(SimplePrefixMappingNormalisationRuleImplEnum.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = SimplePrefixMappingNormalisationRuleImplEnum.log.isTraceEnabled();
    private static final boolean _DEBUG = SimplePrefixMappingNormalisationRuleImplEnum.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = SimplePrefixMappingNormalisationRuleImplEnum.log.isInfoEnabled();
    
    public static final NormalisationRuleEnum SIMPLE_PREFIX_MAPPING_NORMALISATION_RULE_IMPL_ENUM = new SimplePrefixMappingNormalisationRuleImplEnum();
    
    // static
    // {
    // NormalisationRuleEnum.register(REGEX_NORMALISATION_RULE_IMPL_ENUM);
    // }
    
    public SimplePrefixMappingNormalisationRuleImplEnum()
    {
        this(SimplePrefixMappingNormalisationRuleImpl.class.getName(), SimplePrefixMappingNormalisationRuleImpl.myTypes());
        
        if(SimplePrefixMappingNormalisationRuleImplEnum._DEBUG)
        {
            SimplePrefixMappingNormalisationRuleImplEnum.log.debug("SimplePrefixMappingNormalisationRuleImplEnum() registered");
        }
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public SimplePrefixMappingNormalisationRuleImplEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
        
        if(SimplePrefixMappingNormalisationRuleImplEnum._DEBUG)
        {
            SimplePrefixMappingNormalisationRuleImplEnum.log.debug("SimplePrefixMappingNormalisationRuleImplEnum(String, List<URI>) registered");
        }
    }
    
}
