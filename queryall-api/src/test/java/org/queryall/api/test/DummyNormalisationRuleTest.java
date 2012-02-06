/**
 * 
 */
package org.queryall.api.test;

import java.util.Collections;
import java.util.Set;

import org.openrdf.model.URI;
import org.queryall.api.profile.Profile;
import org.queryall.api.rdfrule.NormalisationRule;

/**
 * Test the DummyNormalisationRule interface is implemented according to the NormalisationRule
 * contract using AbstractNormalisationRuleTest.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class DummyNormalisationRuleTest extends AbstractNormalisationRuleTest
{
    
    @Override
    public Set<URI> getExpectedValidStages()
    {
        return Collections.emptySet();
    }
    
    @Override
    public Profile getNewTestProfile()
    {
        return new DummyProfile();
    }
    
    @Override
    public NormalisationRule getNewTestRule()
    {
        return new DummyNormalisationRule();
    }
    
}
