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
public class RegexNormalisationRuleImplEnum extends NormalisationRuleEnum
{
    public static final NormalisationRuleEnum REGEX_NORMALISATION_RULE_IMPL_ENUM = new RegexNormalisationRuleImplEnum(RegexNormalisationRuleImpl.class.getName(), RegexNormalisationRuleImpl.myTypes());
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public RegexNormalisationRuleImplEnum(String nextName, List<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
    
}
