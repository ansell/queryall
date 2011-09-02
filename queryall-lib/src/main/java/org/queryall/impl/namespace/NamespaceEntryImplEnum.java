/**
 * 
 */
package org.queryall.impl.namespace;

import java.util.List;

import org.kohsuke.MetaInfServices;
import org.openrdf.model.URI;
import org.queryall.api.namespace.NamespaceEntryEnum;
import org.queryall.api.services.QueryAllEnum;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices(QueryAllEnum.class)
public class NamespaceEntryImplEnum extends NamespaceEntryEnum
{
    public static final NamespaceEntryEnum NAMESPACE_ENTRY_IMPL_ENUM = new NamespaceEntryImplEnum();
    
    // static
    // {
    // NormalisationRuleEnum.register(REGEX_NORMALISATION_RULE_IMPL_ENUM);
    // }
    
    public NamespaceEntryImplEnum()
    {
        this(NamespaceEntryImpl.class.getName(), NamespaceEntryImpl.myTypes());
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public NamespaceEntryImplEnum(final String nextName, final List<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
    
}
