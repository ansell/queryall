/**
 * 
 */
package org.queryall.impl.namespace;

import org.kohsuke.MetaInfServices;
import org.queryall.api.namespace.NamespaceEntryEnum;
import org.queryall.api.namespace.NamespaceEntryFactory;
import org.queryall.api.namespace.NamespaceEntryParser;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices
public class NamespaceEntryImplFactory implements NamespaceEntryFactory
{
    /**
     * Returns the enumeration from the enumeration that matches this factory
     */
    @Override
    public NamespaceEntryEnum getEnum()
    {
        return NamespaceEntryImplEnum.NAMESPACE_ENTRY_IMPL_ENUM;
        // return NormalisationRuleEnum.valueOf(RegexNormalisationRuleImpl.class.getName());
    }
    
    /**
     * Returns the parser from the enumeration that matches this factory
     */
    @Override
    public NamespaceEntryParser getParser()
    {
        return new NamespaceEntryImplParser();
    }
    
}
