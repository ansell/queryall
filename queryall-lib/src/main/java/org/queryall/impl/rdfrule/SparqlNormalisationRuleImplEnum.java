/**
 * 
 */
package org.queryall.impl.rdfrule;

import java.util.List;

import org.openrdf.model.URI;
import org.queryall.api.rdfrule.NormalisationRuleEnum;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class SparqlNormalisationRuleImplEnum extends NormalisationRuleEnum
{
    public static final NormalisationRuleEnum SPARQL_NORMALISATION_RULE_IMPL_ENUM = new SparqlNormalisationRuleImplEnum(SparqlNormalisationRuleImpl.class.getName(), SparqlNormalisationRuleImpl.myTypes());
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public SparqlNormalisationRuleImplEnum(String nextName, List<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
    
}
