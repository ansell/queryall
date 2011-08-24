/**
 * 
 */
package org.queryall.api.provider;

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
public class ProviderEnum extends QueryAllEnum
{
    protected static final Collection<ProviderEnum> ALL_PROVIDERS = new ArrayList<ProviderEnum>(5);
    
    public static Collection<ProviderEnum> byTypeUris(final List<URI> nextRdfRuleUris)
    {
        final List<ProviderEnum> results = new ArrayList<ProviderEnum>(ProviderEnum.ALL_PROVIDERS.size());
        
        for(final ProviderEnum nextRdfRuleEnum : ProviderEnum.ALL_PROVIDERS)
        {
            if(nextRdfRuleEnum.getTypeURIs().equals(nextRdfRuleUris))
            {
                results.add(nextRdfRuleEnum);
            }
        }
        
        return results;
    }
    
    /**
     * Registers the specified query type.
     */
    public static void register(final ProviderEnum nextRdfRule)
    {
        if(ProviderEnum.valueOf(nextRdfRule.getName()) != null)
        {
            QueryAllEnum.log.error("Cannot register this query type again name=" + nextRdfRule.getName());
        }
        else
        {
            ProviderEnum.ALL_PROVIDERS.add(nextRdfRule);
        }
    }
    
    public static ProviderEnum register(final String name, final List<URI> typeURIs)
    {
        final ProviderEnum newRdfRuleEnum = new ProviderEnum(name, typeURIs);
        ProviderEnum.register(newRdfRuleEnum);
        return newRdfRuleEnum;
    }
    
    public static ProviderEnum valueOf(final String string)
    {
        for(final ProviderEnum nextRdfRuleEnum : ProviderEnum.ALL_PROVIDERS)
        {
            if(nextRdfRuleEnum.getName().equals(string))
            {
                return nextRdfRuleEnum;
            }
        }
        
        return null;
    }
    
    /**
     * Returns all known/registered query types.
     */
    public static Collection<ProviderEnum> values()
    {
        return Collections.unmodifiableCollection(ProviderEnum.ALL_PROVIDERS);
    }
    
    /**
     * Create a new RdfRule enum using the given name, which must be unique
     * 
     * @param nextName
     * @param nextTypeURIs
     */
    public ProviderEnum(final String nextName, final List<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
}
