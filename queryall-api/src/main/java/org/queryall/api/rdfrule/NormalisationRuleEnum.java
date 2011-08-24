/**
 * 
 */
package org.queryall.api.rdfrule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.openrdf.model.URI;
import org.queryall.api.services.QueryAllEnum;

/**
 * RdfRule implementations register themselves with this enumeration when they are loaded.
 * 
 * NOTE: This is not an enumeration as java does not support extensible enumerations, but it should
 * act like one for all purposes other than java's underpowered switch case statement.
 * 
 */
public class NormalisationRuleEnum extends QueryAllEnum
{
    protected static final Collection<NormalisationRuleEnum> ALL_NORMALISATION_RULES =
            new ArrayList<NormalisationRuleEnum>(5);
    
    public static Collection<NormalisationRuleEnum> byTypeUris(final List<URI> nextRdfRuleUris)
    {
        final List<NormalisationRuleEnum> results =
                new ArrayList<NormalisationRuleEnum>(NormalisationRuleEnum.ALL_NORMALISATION_RULES.size());
        
        for(final NormalisationRuleEnum nextRdfRuleEnum : NormalisationRuleEnum.ALL_NORMALISATION_RULES)
        {
            if(nextRdfRuleEnum.getTypeURIs().equals(nextRdfRuleUris))
            {
                results.add(nextRdfRuleEnum);
            }
        }
        
        return results;
    }
    
    /**
     * Registers the specified normalisation rule.
     */
    public static void register(final NormalisationRuleEnum nextRdfRule)
    {
        if(NormalisationRuleEnum.valueOf(nextRdfRule.getName()) != null)
        {
            QueryAllEnum.log.error("Cannot register this normalisation rule again name=" + nextRdfRule.getName());
        }
        else
        {
            NormalisationRuleEnum.ALL_NORMALISATION_RULES.add(nextRdfRule);
        }
    }
    
    public static NormalisationRuleEnum register(final String name, final List<URI> typeURIs)
    {
        final NormalisationRuleEnum newRdfRuleEnum = new NormalisationRuleEnum(name, typeURIs);
        NormalisationRuleEnum.register(newRdfRuleEnum);
        return newRdfRuleEnum;
    }
    
    public static NormalisationRuleEnum valueOf(final String string)
    {
        for(final NormalisationRuleEnum nextRdfRuleEnum : NormalisationRuleEnum.ALL_NORMALISATION_RULES)
        {
            if(nextRdfRuleEnum.getName().equals(string))
            {
                return nextRdfRuleEnum;
            }
        }
        
        return null;
    }
    
    /**
     * Returns all known/registered normalisation rules.
     */
    public static Collection<NormalisationRuleEnum> values()
    {
        return Collections.unmodifiableCollection(NormalisationRuleEnum.ALL_NORMALISATION_RULES);
    }
    
    /**
     * Create a new RdfRule enum using the given name, which must be unique
     * 
     * @param nextName
     * @param nextTypeURIs
     */
    public NormalisationRuleEnum(final String nextName, final List<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
}
