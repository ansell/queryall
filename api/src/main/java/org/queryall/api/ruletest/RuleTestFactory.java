/**
 * 
 */
package org.queryall.api.ruletest;

import org.queryall.api.services.QueryAllFactory;

/**
 * A factory for creating parsers for RuleTest objects.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface RuleTestFactory extends QueryAllFactory<RuleTestEnum, RuleTestParser, RuleTest>
{
    
}
