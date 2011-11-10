/**
 * 
 */
package org.queryall.impl.rule.test;

import org.queryall.api.profile.Profile;
import org.queryall.api.rdfrule.SparqlNormalisationRule;
import org.queryall.api.test.AbstractSparqlNormalisationRuleTest;
import org.queryall.impl.profile.ProfileImpl;
import org.queryall.impl.rdfrule.SparqlNormalisationRuleImpl;

/**
 * Provides the implementation of the Provider class for the Abstract test class provided with
 * queryall-api-tests.jar
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class SparqlNormalisationRuleImplTest extends AbstractSparqlNormalisationRuleTest
{
    @Override
    public Profile getNewTestProfile()
    {
        return new ProfileImpl();
    }
    
    @Override
    public SparqlNormalisationRule getNewTestSparqlRule()
    {
        return new SparqlNormalisationRuleImpl();
    }
}
