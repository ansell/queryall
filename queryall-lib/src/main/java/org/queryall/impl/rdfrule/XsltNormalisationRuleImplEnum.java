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
public class XsltNormalisationRuleImplEnum extends NormalisationRuleEnum
{
    public static final NormalisationRuleEnum XSLT_NORMALISATION_RULE_IMPL_ENUM = new XsltNormalisationRuleImplEnum();
    
//    static
//    {
//        NormalisationRuleEnum.register(XSLT_NORMALISATION_RULE_IMPL_ENUM);
//    }
    
    public XsltNormalisationRuleImplEnum()
    {
        this(XsltNormalisationRuleImpl.class.getName(), XsltNormalisationRuleImpl.myTypes());
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public XsltNormalisationRuleImplEnum(String nextName, List<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
    
}
