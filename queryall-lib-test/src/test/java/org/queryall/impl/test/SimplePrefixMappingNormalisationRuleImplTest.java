/**
 * 
 */
package org.queryall.impl.test;

import org.queryall.api.profile.Profile;
import org.queryall.api.test.AbstractSimplePrefixMappingNormalisationRuleTest;
import org.queryall.impl.profile.ProfileImpl;
import org.queryall.impl.rdfrule.SimplePrefixMappingNormalisationRuleImpl;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class SimplePrefixMappingNormalisationRuleImplTest extends AbstractSimplePrefixMappingNormalisationRuleTest
{

    @Override
    public org.queryall.api.rdfrule.SimplePrefixMappingNormalisationRule getNewTestMappingRule()
    {
        return new SimplePrefixMappingNormalisationRuleImpl();
    }

    @Override
    public Profile getNewTestProfile()
    {
        return new ProfileImpl();
    }
    
}
