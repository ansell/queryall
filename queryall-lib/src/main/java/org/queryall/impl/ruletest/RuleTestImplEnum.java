/**
 * 
 */
package org.queryall.impl.ruletest;

import java.util.List;

import org.kohsuke.MetaInfServices;
import org.openrdf.model.URI;
import org.queryall.api.ruletest.RuleTestEnum;
import org.queryall.api.services.QueryAllEnum;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices(QueryAllEnum.class)
public class RuleTestImplEnum extends RuleTestEnum
{
    public static final RuleTestEnum RULE_TEST_IMPL_ENUM = new RuleTestImplEnum();
    
    // static
    // {
    // NormalisationRuleEnum.register(REGEX_NORMALISATION_RULE_IMPL_ENUM);
    // }
    
    public RuleTestImplEnum()
    {
        this(RuleTestImpl.class.getName(), RuleTestImpl.myTypes());
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public RuleTestImplEnum(final String nextName, final List<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
    
}
