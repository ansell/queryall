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
public class RegexNormalisationRuleImplEnum extends NormalisationRuleEnum
{
    private static final Logger log = LoggerFactory.getLogger(RegexNormalisationRuleImplEnum.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = RegexNormalisationRuleImplEnum.log.isTraceEnabled();
    private static final boolean _DEBUG = RegexNormalisationRuleImplEnum.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = RegexNormalisationRuleImplEnum.log.isInfoEnabled();
    
    public static final NormalisationRuleEnum REGEX_NORMALISATION_RULE_IMPL_ENUM = new RegexNormalisationRuleImplEnum();
    
    // static
    // {
    // NormalisationRuleEnum.register(REGEX_NORMALISATION_RULE_IMPL_ENUM);
    // }
    
    public RegexNormalisationRuleImplEnum()
    {
        this(RegexNormalisationRuleImpl.class.getName(), RegexNormalisationRuleImpl.myTypes());
        
        if(RegexNormalisationRuleImplEnum._DEBUG)
        {
            RegexNormalisationRuleImplEnum.log.debug("RegexNormalisationRuleImplEnum() registered");
        }
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public RegexNormalisationRuleImplEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
        
        if(RegexNormalisationRuleImplEnum._DEBUG)
        {
            RegexNormalisationRuleImplEnum.log.debug("RegexNormalisationRuleImplEnum(String, List<URI>) registered");
        }
    }
    
}
