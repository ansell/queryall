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
public class SpinInferencingRuleImplEnum extends NormalisationRuleEnum
{
    public static final NormalisationRuleEnum SPIN_INFERENCING_RULE_IMPL_ENUM = new SpinInferencingRuleImplEnum();
    
    public SpinInferencingRuleImplEnum()
    {
        this(SpinInferencingRuleImpl.class.getName(), SpinInferencingRuleImpl.myTypes());
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public SpinInferencingRuleImplEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
    
}
