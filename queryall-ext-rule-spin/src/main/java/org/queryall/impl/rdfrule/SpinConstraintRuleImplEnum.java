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
public class SpinConstraintRuleImplEnum extends NormalisationRuleEnum
{
    public static final NormalisationRuleEnum SPIN_CONSTRAINT_RULE_IMPL_ENUM =
            new SpinConstraintRuleImplEnum();
    
    public SpinConstraintRuleImplEnum()
    {
        this(SpinConstraintRuleImpl.class.getName(), SpinConstraintRuleImpl.myTypes());
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public SpinConstraintRuleImplEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
    
}
