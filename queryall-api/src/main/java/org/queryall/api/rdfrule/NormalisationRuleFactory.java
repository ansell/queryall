/**
 * 
 */
package org.queryall.api.rdfrule;

import org.queryall.api.services.QueryAllFactory;

/**
 * A factory for creating parsers for QueryType objects
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface NormalisationRuleFactory extends
        QueryAllFactory<NormalisationRuleEnum, NormalisationRuleParser, NormalisationRule>
{
    
}
