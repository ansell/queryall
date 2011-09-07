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
public class StringRuleTestImplEnum extends RuleTestEnum
{
    public static final RuleTestEnum STRING_RULE_TEST_IMPL_ENUM = new StringRuleTestImplEnum();
    
    public StringRuleTestImplEnum()
    {
        this(StringRuleTestImpl.class.getName(), StringRuleTestImpl.myTypes());
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public StringRuleTestImplEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
    
}
