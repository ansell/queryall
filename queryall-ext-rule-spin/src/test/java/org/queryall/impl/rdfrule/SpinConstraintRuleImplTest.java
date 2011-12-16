/**
 * 
 */
package org.queryall.impl.rdfrule;

import org.junit.After;
import org.junit.Before;
import org.queryall.api.profile.Profile;
import org.queryall.api.rdfrule.SpinConstraintRule;
import org.queryall.api.test.AbstractSpinConstraintRuleTest;
import org.queryall.impl.profile.ProfileImpl;
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
        return new ProfileImpl();
    }
    
    @Override
    public final SpinConstraintRule getNewTestSpinConstraintRule()
    {
        return new SpinConstraintRuleImpl();
    }
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
    }
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    @After
    public void tearDown() throws Exception
    {
        super.tearDown();
    }
    
}
