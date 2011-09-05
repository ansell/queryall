/**
 * 
 */
package org.queryall.impl.ruletest;

import java.util.Set;

import org.kohsuke.MetaInfServices;
import org.openrdf.model.URI;
import org.queryall.api.ruletest.RuleTestEnum;
import org.queryall.api.services.QueryAllEnum;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices(QueryAllEnum.class)
public class RegexRuleTestImplEnum extends RuleTestEnum
{
    public static final RuleTestEnum REGEX_RULE_TEST_IMPL_ENUM = new RegexRuleTestImplEnum();
    
    // static
    // {
    // NormalisationRuleEnum.register(REGEX_NORMALISATION_RULE_IMPL_ENUM);
    // }
    
    public RegexRuleTestImplEnum()
    {
        this(RegexRuleTestImpl.class.getName(), RegexRuleTestImpl.myTypes());
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public RegexRuleTestImplEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
    
}
