/**
 * 
 */
package org.queryall.api.ruletest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.openrdf.model.URI;
import org.queryall.api.profile.ProfileEnum;
import org.queryall.api.services.QueryAllEnum;

/**
 * RuleTest implementations register themselves with this enumeration when they are loaded.
 * 
 * NOTE: This is not an enumeration as java does not support extensible enumerations, but it should
 * act like one for all purposes other than java's underpowered switch case statement.
 * 
 */
public class RuleTestEnum extends QueryAllEnum
{
    protected static final Collection<RuleTestEnum> ALL_RULE_TESTS = new ArrayList<RuleTestEnum>(5);
    
    public static Collection<RuleTestEnum> byTypeUris(final List<URI> nextRuleTestUris)
    {
        final List<RuleTestEnum> results = new ArrayList<RuleTestEnum>(RuleTestEnum.ALL_RULE_TESTS.size());
        
        for(final RuleTestEnum nextRuleTestEnum : RuleTestEnum.ALL_RULE_TESTS)
        {
            if(nextRuleTestEnum.getTypeURIs().equals(nextRuleTestUris))
            {
                results.add(nextRuleTestEnum);
            }
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
            QueryAllEnum.log.error("Cannot register this rule test again name=" + nextRuleTest.getName());
        }
        else
        {
            RuleTestEnum.ALL_RULE_TESTS.add(nextRuleTest);
        }
    }
    
    public static RuleTestEnum register(final String name, final List<URI> typeURIs)
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
    public RuleTestEnum(final String nextName, final List<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
        RuleTestEnum.ALL_RULE_TESTS.add(this);
    }
}
