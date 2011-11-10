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
public class SpinNormalisationRuleImplEnum extends NormalisationRuleEnum
{
    public static final NormalisationRuleEnum SPIN_NORMALISATION_RULE_IMPL_ENUM =
            new SpinNormalisationRuleImplEnum();
    
    public SpinNormalisationRuleImplEnum()
    {
        this(SpinNormalisationRuleImpl.class.getName(), SpinNormalisationRuleImpl.myTypes());
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public SpinNormalisationRuleImplEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
    
}
