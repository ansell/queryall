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
public class XsltTransformingRuleImplFactory implements NormalisationRuleFactory
{
    /**
     * Returns the enumeration from the enumeration that matches this factory
     */
    @Override
    public NormalisationRuleEnum getEnum()
    {
        return XsltTransformingRuleImplEnum.XSLT_TRANSFORMING_RULE_IMPL_ENUM;
    }
    
    /**
     * Returns the parser from the enumeration that matches this factory
     */
    @Override
    public NormalisationRuleParser getParser()
    {
        return new XsltTransformingRuleImplParser();
    }
    
}
