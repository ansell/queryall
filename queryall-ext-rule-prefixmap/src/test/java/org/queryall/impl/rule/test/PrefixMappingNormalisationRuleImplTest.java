/**
 * 
 */
package org.queryall.impl.rule.test;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.URI;
import org.queryall.api.profile.Profile;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.test.AbstractPrefixMappingNormalisationRuleTest;
import org.queryall.impl.profile.ProfileImpl;
import org.queryall.impl.rdfrule.PrefixMappingNormalisationRuleImpl;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class PrefixMappingNormalisationRuleImplTest extends AbstractPrefixMappingNormalisationRuleTest
{
    
    @Override
    public org.queryall.api.rdfrule.PrefixMappingNormalisationRule getNewTestMappingRule()
    {
        return new PrefixMappingNormalisationRuleImpl();
    }
    
    @Override
    public Profile getNewTestProfile()
    {
        return new ProfileImpl();
    }

}
