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
public class RegexNormalisationRuleImplEnum extends NormalisationRuleEnum
{
    public static final NormalisationRuleEnum REGEX_NORMALISATION_RULE_IMPL_ENUM = new RegexNormalisationRuleImplEnum();
    
    // static
    // {
    // NormalisationRuleEnum.register(REGEX_NORMALISATION_RULE_IMPL_ENUM);
    // }
    
    public RegexNormalisationRuleImplEnum()
    {
        this(RegexNormalisationRuleImpl.class.getName(), RegexNormalisationRuleImpl.myTypes());
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public RegexNormalisationRuleImplEnum(final String nextName, final List<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
    
}
