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
public class XsltNormalisationRuleImplFactory implements NormalisationRuleFactory
{
    /**
     * Returns the enumeration from the enumeration that matches this factory
     */
    @Override
    public NormalisationRuleEnum getEnum()
    {
        return NormalisationRuleEnum.valueOf(XsltNormalisationRuleImpl.class.getName());
    }
    
    /**
     * Returns the parser from the enumeration that matches this factory
     */
    @Override
    public NormalisationRuleParser getParser()
    {
        return new XsltNormalisationRuleImplParser();
    }
    
}
