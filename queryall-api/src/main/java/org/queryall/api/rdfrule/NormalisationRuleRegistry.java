/**
 * 
 */
package org.queryall.api.rdfrule;

import org.queryall.api.querytype.QueryTypeEnum;
import org.queryall.api.querytype.QueryTypeFactory;
import org.queryall.api.services.AbstractServiceLoader;

/**
 * Dynamically loads and keeps a track of the different QueryType's that are available.
 * 
 * Uses QueryTypeEnum objects as keys, as defined in QueryTypeFactory
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class NormalisationRuleRegistry extends AbstractServiceLoader<NormalisationRuleEnum, NormalisationRuleFactory>
{
    private static NormalisationRuleRegistry defaultRegistry;
    
    // RDFParserRegistry.getInstance();
    //
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
