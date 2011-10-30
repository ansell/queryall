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
 * Links the set of URIs that identify XSLT Normalisation Rules with the implementation class
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices(QueryAllEnum.class)
public class XsltNormalisationRuleImplEnum extends NormalisationRuleEnum
{
    public static final NormalisationRuleEnum XSLT_NORMALISATION_RULE_IMPL_ENUM = new XsltNormalisationRuleImplEnum();
    
    public XsltNormalisationRuleImplEnum()
    {
        this(XsltNormalisationRuleImpl.class.getName(), XsltNormalisationRuleImpl.myTypes());
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public XsltNormalisationRuleImplEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
    
}
