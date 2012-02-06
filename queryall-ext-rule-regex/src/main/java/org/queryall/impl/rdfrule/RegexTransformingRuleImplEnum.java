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
public class RegexTransformingRuleImplEnum extends NormalisationRuleEnum
{
    private static final Logger log = LoggerFactory.getLogger(RegexTransformingRuleImplEnum.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = RegexTransformingRuleImplEnum.log.isTraceEnabled();
    private static final boolean DEBUG = RegexTransformingRuleImplEnum.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = RegexTransformingRuleImplEnum.log.isInfoEnabled();
    
    public static final NormalisationRuleEnum REGEX_TRANSFORMING_RULE_IMPL_ENUM = new RegexTransformingRuleImplEnum();
    
    // static
    // {
    // NormalisationRuleEnum.register(REGEX_NORMALISATION_RULE_IMPL_ENUM);
    // }
    
    public RegexTransformingRuleImplEnum()
    {
        this(RegexTransformingRuleImpl.class.getName(), RegexTransformingRuleImpl.myTypes());
        
        if(RegexTransformingRuleImplEnum.DEBUG)
        {
            RegexTransformingRuleImplEnum.log.debug("RegexNormalisationRuleImplEnum() registered");
        }
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public RegexTransformingRuleImplEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
        
        if(RegexTransformingRuleImplEnum.DEBUG)
        {
            RegexTransformingRuleImplEnum.log.debug("RegexNormalisationRuleImplEnum(String, List<URI>) registered");
        }
    }
    
}
