/**
 * 
 */
package org.queryall.impl.rdfrule;

import org.queryall.api.profile.Profile;
import org.queryall.api.rdfrule.SpinInferencingRule;
import org.queryall.api.test.AbstractSpinInferencingRuleTest;
import org.queryall.api.test.DummyProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class SpinInferencingRuleImplTest extends AbstractSpinInferencingRuleTest
{
    private static final Logger log = LoggerFactory.getLogger(SpinInferencingRuleImplTest.class);
    
    @Override
    public Profile getNewTestProfile()
    {
        return new DummyProfile();
    }
    
    @Override
    public SpinInferencingRule getNewTestSpinInferencingRule()
    {
        return new SpinInferencingRuleImpl();
    }
    
}
