/**
 * 
 */
package org.queryall.impl.rule.test;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.URI;
import org.queryall.api.profile.Profile;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.rdfrule.SparqlAskRule;
import org.queryall.api.test.AbstractSparqlAskRuleTest;
import org.queryall.impl.profile.ProfileImpl;
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
        return new ProfileImpl();
    }
    
    @Override
    public SparqlAskRule getNewTestSparqlAskRule()
    {
        return new SparqlAskRuleImpl();
    }
}
