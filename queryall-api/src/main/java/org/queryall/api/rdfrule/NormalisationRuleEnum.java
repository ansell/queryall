/**
 * 
 */
package org.queryall.api.rdfrule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.model.URI;
import org.queryall.api.services.QueryAllEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RdfRule implementations register themselves with this enumeration when they are loaded.
 * 
 * NOTE: This is not an enumeration as java does not support extensible enumerations, but it should
 * act like one for all purposes other than java's underpowered switch case statement.
 * 
 */
public class NormalisationRuleEnum extends QueryAllEnum
{
    private static final Logger log = LoggerFactory.getLogger(NormalisationRuleEnum.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = NormalisationRuleEnum.log.isTraceEnabled();
    private static final boolean _DEBUG = NormalisationRuleEnum.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = NormalisationRuleEnum.log.isInfoEnabled();
    
    protected static final Set<NormalisationRuleEnum> ALL_NORMALISATION_RULES = new HashSet<NormalisationRuleEnum>();
    
    public static Collection<NormalisationRuleEnum> byTypeUris(final Set<URI> nextTypeUris)
    {
        if(nextTypeUris.size() == 0)
        {
            if(NormalisationRuleEnum._DEBUG)
            {
                NormalisationRuleEnum.log.debug("found an empty URI set for nextNormalisationRuleUris=" + nextTypeUris);
            }
            
            return Collections.emptyList();
        }
        
        final List<NormalisationRuleEnum> results =
                new ArrayList<NormalisationRuleEnum>(NormalisationRuleEnum.ALL_NORMALISATION_RULES.size());
        
        for(final NormalisationRuleEnum nextEnum : NormalisationRuleEnum.ALL_NORMALISATION_RULES)
        {
            if(nextEnum.matchForTypeUris(nextTypeUris))
            {
                if(NormalisationRuleEnum._DEBUG)
                {
                    NormalisationRuleEnum.log.debug("found an matching URI set for nextNormalisationRuleUris="
                            + nextTypeUris);
                }
                results.add(nextEnum);
            }
        }
        
        if(NormalisationRuleEnum._DEBUG)
        {
            NormalisationRuleEnum.log.debug("returning results.size()=" + results.size()
                    + " for nextNormalisationRuleUris=" + nextTypeUris);
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
            if(NormalisationRuleEnum._DEBUG)
            {
                NormalisationRuleEnum.log.debug("Cannot register this normalisation rule again name="
                        + nextRdfRule.getName());
            }
        }
        else
        {
            NormalisationRuleEnum.ALL_NORMALISATION_RULES.add(nextRdfRule);
        }
    }
    
    public static NormalisationRuleEnum register(final String name, final Set<URI> typeURIs)
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
    public NormalisationRuleEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
        NormalisationRuleEnum.ALL_NORMALISATION_RULES.add(this);
    }
}
