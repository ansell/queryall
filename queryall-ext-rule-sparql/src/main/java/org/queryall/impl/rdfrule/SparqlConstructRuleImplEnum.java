/**
 * 
 */
package org.queryall.impl.rdfrule;

import java.util.Set;

import org.kohsuke.MetaInfServices;
import org.openrdf.model.URI;
import org.queryall.api.rdfrule.NormalisationRuleEnum;
import org.queryall.api.services.QueryAllEnum;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices(QueryAllEnum.class)
public class SparqlConstructRuleImplEnum extends NormalisationRuleEnum
{
    public static final NormalisationRuleEnum SPARQL_CONSTRUCT_RULE_IMPL_ENUM =
            new SparqlConstructRuleImplEnum();
    
    public SparqlConstructRuleImplEnum()
    {
        this(SparqlConstructRuleImpl.class.getName(), SparqlConstructRuleImpl.myTypes());
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public SparqlConstructRuleImplEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
    
}
