/**
 * 
 */
package org.queryall.impl.rule.test;

import org.queryall.api.profile.Profile;
import org.queryall.api.rdfrule.SparqlAskRule;
import org.queryall.api.test.AbstractSparqlAskRuleTest;
import org.queryall.api.test.DummyProfile;
import org.queryall.impl.rdfrule.SparqlAskRuleImpl;

/**
 * Provides the implementation of the Provider class for the Abstract test class provided with
 * queryall-api-tests.jar
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class SparqlAskRuleImplTest extends AbstractSparqlAskRuleTest
{
    @Override
    public Profile getNewTestProfile()
    {
        return new DummyProfile();
    }
    
    @Override
    public SparqlAskRule getNewTestSparqlAskRule()
    {
        return new SparqlAskRuleImpl();
    }
}
