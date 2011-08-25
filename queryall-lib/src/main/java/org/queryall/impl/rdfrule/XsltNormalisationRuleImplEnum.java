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
public class XsltNormalisationRuleImplEnum extends NormalisationRuleEnum
{
    public static final NormalisationRuleEnum XSLT_NORMALISATION_RULE_IMPL_ENUM = new XsltNormalisationRuleImplEnum(XsltNormalisationRuleImpl.class.getName(), XsltNormalisationRuleImpl.myTypes());
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public XsltNormalisationRuleImplEnum(String nextName, List<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
    
}
