/**
 * 
 */
package org.queryall.api.ruletest;

import org.queryall.api.services.AbstractServiceLoader;

/**
 * Dynamically loads and keeps a track of the different RuleTest's that are available.
 * 
 * Uses RuleTestEnum objects as keys, as defined in RuleTestFactory
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RuleTestRegistry extends AbstractServiceLoader<RuleTestEnum, RuleTestFactory>
{
    private static final Object LOCK = new Object();
    private static volatile RuleTestRegistry defaultRegistry;
    
    public static RuleTestRegistry getInstance()
    {
        if(RuleTestRegistry.defaultRegistry == null)
        {
            synchronized(RuleTestRegistry.LOCK)
            {
                if(RuleTestRegistry.defaultRegistry == null)
                {
                    RuleTestRegistry.defaultRegistry = new RuleTestRegistry();
                }
            }
        }
        
        return RuleTestRegistry.defaultRegistry;
        
    }
    
    public RuleTestRegistry()
    {
        super(RuleTestFactory.class);
    }
    
    @Override
    protected RuleTestEnum getKey(final RuleTestFactory factory)
    {
        return factory.getEnum();
    }
    
}
