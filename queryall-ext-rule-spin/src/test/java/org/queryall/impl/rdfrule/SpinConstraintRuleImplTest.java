/**
 * 
 */
package org.queryall.impl.rdfrule;

import org.queryall.api.profile.Profile;
import org.queryall.api.rdfrule.SpinConstraintRule;
import org.queryall.api.test.AbstractSpinConstraintRuleTest;
import org.queryall.api.test.DummyProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class SpinConstraintRuleImplTest extends AbstractSpinConstraintRuleTest
{
    private static final Logger log = LoggerFactory.getLogger(SpinConstraintRuleImplTest.class);
    
    @Override
    public Profile getNewTestProfile()
    {
        return new DummyProfile();
    }
    
    @Override
    public final SpinConstraintRule getNewTestSpinConstraintRule()
    {
        return new SpinConstraintRuleImpl();
    }
}
