/**
 * 
 */
package org.queryall.impl.rdfrule;

import java.util.List;

import org.kohsuke.MetaInfServices;
import org.openrdf.model.URI;
import org.queryall.api.rdfrule.NormalisationRuleEnum;
import org.queryall.api.services.QueryAllEnum;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices(QueryAllEnum.class)
public class SparqlNormalisationRuleImplEnum extends NormalisationRuleEnum
{
    public static final NormalisationRuleEnum SPARQL_NORMALISATION_RULE_IMPL_ENUM = new SparqlNormalisationRuleImplEnum();
    
    public SparqlNormalisationRuleImplEnum()
    {
        this(SparqlNormalisationRuleImpl.class.getName(), SparqlNormalisationRuleImpl.myTypes());
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public SparqlNormalisationRuleImplEnum(String nextName, List<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
    
}
