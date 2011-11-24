/**
 * 
 */
package org.queryall.impl.rule.test;

import org.queryall.api.profile.Profile;
import org.queryall.api.rdfrule.SparqlConstructRule;
import org.queryall.api.test.AbstractSparqlConstructRuleTest;
import org.queryall.impl.profile.ProfileImpl;
import org.queryall.impl.rdfrule.SparqlConstructRuleImpl;

/**
 * Provides the implementation of the Provider class for the Abstract test class provided with
 * queryall-api-tests.jar
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class SparqlConstructRuleImplTest extends AbstractSparqlConstructRuleTest
{
    @Override
    public Profile getNewTestProfile()
    {
        return new ProfileImpl();
    }
    
    @Override
    public SparqlConstructRule getNewTestSparqlConstructRule()
    {
        return new SparqlConstructRuleImpl();
    }
}
