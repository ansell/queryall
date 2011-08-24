/**
 * 
 */
package org.queryall.api.rdfrule;

import org.queryall.api.services.AbstractServiceLoader;

/**
 * Dynamically loads and keeps a track of the different NormalisationRule's that are available.
 * 
 * Uses NormalisationRuleEnum objects as keys, as defined in NormalisationRuleFactory
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class NormalisationRuleRegistry extends AbstractServiceLoader<NormalisationRuleEnum, NormalisationRuleFactory>
{
    private static NormalisationRuleRegistry defaultRegistry;
    
    public static synchronized NormalisationRuleRegistry getInstance()
    {
        if(NormalisationRuleRegistry.defaultRegistry == null)
        {
            NormalisationRuleRegistry.defaultRegistry = new NormalisationRuleRegistry();
        }
        
        return NormalisationRuleRegistry.defaultRegistry;
        
    }
    
    public NormalisationRuleRegistry()
    {
        super(NormalisationRuleFactory.class);
    }
    
    @Override
    protected NormalisationRuleEnum getKey(final NormalisationRuleFactory factory)
    {
        return factory.getEnum();
    }
    
}
