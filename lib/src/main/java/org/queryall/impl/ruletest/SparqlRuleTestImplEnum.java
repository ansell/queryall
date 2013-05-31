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
public class SparqlRuleTestImplEnum extends RuleTestEnum
{
    public static final RuleTestEnum SPARQL_RULE_TEST_IMPL_ENUM = new SparqlRuleTestImplEnum();
    
    public SparqlRuleTestImplEnum()
    {
        this(SparqlRuleTestImpl.class.getName(), SparqlRuleTestImpl.myTypes());
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public SparqlRuleTestImplEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
    
}
