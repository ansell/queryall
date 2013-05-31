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
public class SparqlAskRuleImplEnum extends NormalisationRuleEnum
{
    public static final NormalisationRuleEnum SPARQL_ASK_RULE_IMPL_ENUM = new SparqlAskRuleImplEnum();
    
    public SparqlAskRuleImplEnum()
    {
        this(SparqlAskRuleImpl.class.getName(), SparqlAskRuleImpl.myTypes());
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public SparqlAskRuleImplEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
    
}
