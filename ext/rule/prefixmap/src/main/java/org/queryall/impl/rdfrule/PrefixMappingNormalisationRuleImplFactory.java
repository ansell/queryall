/**
 * 
 */
package org.queryall.impl.rdfrule;

import org.kohsuke.MetaInfServices;
import org.queryall.api.rdfrule.NormalisationRuleEnum;
import org.queryall.api.rdfrule.NormalisationRuleFactory;
import org.queryall.api.rdfrule.NormalisationRuleParser;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices
public class PrefixMappingNormalisationRuleImplFactory implements NormalisationRuleFactory
{
    /**
     * Returns the enumeration from the enumeration that matches this factory
     */
    @Override
    public NormalisationRuleEnum getEnum()
    {
        return PrefixMappingNormalisationRuleImplEnum.SIMPLE_PREFIX_MAPPING_NORMALISATION_RULE_IMPL_ENUM;
        // return NormalisationRuleEnum.valueOf(RegexNormalisationRuleImpl.class.getName());
    }
    
    /**
     * Returns the parser from the enumeration that matches this factory
     */
    @Override
    public NormalisationRuleParser getParser()
    {
        return new PrefixMappingNormalisationRuleImplParser();
    }
    
}
