/**
 * 
 */
package org.queryall.impl.rule.test;

import org.queryall.api.profile.Profile;
import org.queryall.api.rdfrule.PrefixMappingNormalisationRule;
import org.queryall.api.test.AbstractPrefixMappingNormalisationRuleTest;
import org.queryall.api.test.DummyProfile;
import org.queryall.impl.rdfrule.PrefixMappingNormalisationRuleImpl;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class PrefixMappingNormalisationRuleImplTest extends AbstractPrefixMappingNormalisationRuleTest
{
    
    @Override
    public PrefixMappingNormalisationRule getNewTestMappingRule()
    {
        return new PrefixMappingNormalisationRuleImpl();
    }
    
    @Override
    public Profile getNewTestProfile()
    {
        return new DummyProfile();
    }
    
}
