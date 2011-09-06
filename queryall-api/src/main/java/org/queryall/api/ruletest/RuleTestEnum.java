/**
 * 
 */
package org.queryall.api.ruletest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.openrdf.model.URI;
import org.queryall.api.services.QueryAllEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RuleTest implementations register themselves with this enumeration when they are loaded.
 * 
 * NOTE: This is not an enumeration as java does not support extensible enumerations, but it should
 * act like one for all purposes other than java's underpowered switch case statement.
 * 
 */
public class RuleTestEnum extends QueryAllEnum
{
    private static final Logger log = LoggerFactory.getLogger(RuleTestEnum.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = RuleTestEnum.log.isTraceEnabled();
    private static final boolean _DEBUG = RuleTestEnum.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = RuleTestEnum.log.isInfoEnabled();
    
    protected static final Collection<RuleTestEnum> ALL_RULE_TESTS = new ArrayList<RuleTestEnum>(5);
    
    public static Collection<RuleTestEnum> byTypeUris(final Set<URI> nextTypeUris)
    {
        if(nextTypeUris.size() == 0)
        {
            if(RuleTestEnum._DEBUG)
            {
                RuleTestEnum.log.debug("found an empty URI set for nextRuleTestUris=" + nextTypeUris);
            }
            
            return Collections.emptyList();
        }
        
        final List<RuleTestEnum> results = new ArrayList<RuleTestEnum>(RuleTestEnum.ALL_RULE_TESTS.size());
        
        for(final RuleTestEnum nextEnum : RuleTestEnum.ALL_RULE_TESTS)
        {
            // NOTE: This restriction would force developers to include implementations for every possible combination of functionalities
            // This is not likely to be practical or useful, so it is not implemented
            // The minimum restriction is that there is at least one URI, ie, the standard default URI for this type of object
            //boolean matching = (nextRuleTestEnum.getTypeURIs().size() == nextRuleTestUris.size());
            boolean matching = true;
            
            for(final URI nextURI : nextTypeUris)
            {
                if(!nextEnum.getTypeURIs().contains(nextURI))
                {
                    if(RuleTestEnum._DEBUG)
                    {
                        RuleTestEnum.log.debug("found an empty URI set for nextURI=" + nextURI.stringValue());
                    }
                    
                    matching = false;
                }
            }
            
            if(matching)
            {
                if(RuleTestEnum._DEBUG)
                {
                    RuleTestEnum.log.debug("found an matching URI set for nextRuleTestUris=" + nextTypeUris);
                }
                
                results.add(nextEnum);
            }
        }
        
        if(RuleTestEnum._DEBUG)
        {
            RuleTestEnum.log.debug("returning results.size()=" + results.size() + " for nextNormalisationRuleUris="
                    + nextTypeUris);
        }
        
        return results;
    }
    
    /**
     * Registers the specified rule test.
     */
    public static void register(final RuleTestEnum nextRuleTest)
    {
        if(RuleTestEnum.valueOf(nextRuleTest.getName()) != null)
        {
            if(RuleTestEnum._DEBUG)
            {
                RuleTestEnum.log.debug("Cannot register this rule test again name=" + nextRuleTest.getName());
            }
        }
        else
        {
            RuleTestEnum.ALL_RULE_TESTS.add(nextRuleTest);
        }
    }
    
    public static RuleTestEnum register(final String name, final Set<URI> typeURIs)
    {
        final RuleTestEnum newRuleTestEnum = new RuleTestEnum(name, typeURIs);
        RuleTestEnum.register(newRuleTestEnum);
        return newRuleTestEnum;
    }
    
    public static RuleTestEnum valueOf(final String string)
    {
        for(final RuleTestEnum nextRuleTestEnum : RuleTestEnum.ALL_RULE_TESTS)
        {
            if(nextRuleTestEnum.getName().equals(string))
            {
                return nextRuleTestEnum;
            }
        }
        
        return null;
    }
    
    /**
     * Returns all known/registered rule tests.
     */
    public static Collection<RuleTestEnum> values()
    {
        return Collections.unmodifiableCollection(RuleTestEnum.ALL_RULE_TESTS);
    }
    
    /**
     * Create a new RuleTest enum using the given name, which must be unique
     * 
     * @param nextName
     * @param nextTypeURIs
     */
    public RuleTestEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
        RuleTestEnum.ALL_RULE_TESTS.add(this);
    }
}
