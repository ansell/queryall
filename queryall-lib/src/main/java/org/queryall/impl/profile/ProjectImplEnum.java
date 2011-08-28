/**
 * 
 */
package org.queryall.impl.profile;

import java.util.List;

import org.kohsuke.MetaInfServices;
import org.openrdf.model.URI;
import org.queryall.api.project.ProjectEnum;
import org.queryall.api.services.QueryAllEnum;
import org.queryall.impl.project.ProjectImpl;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices(QueryAllEnum.class)
public class ProjectImplEnum extends ProjectEnum
{
    public static final ProjectEnum PROJECT_IMPL_ENUM = new ProjectImplEnum();
    
    // static
    // {
    // NormalisationRuleEnum.register(REGEX_NORMALISATION_RULE_IMPL_ENUM);
    // }
    
    public ProjectImplEnum()
    {
        this(ProjectImpl.class.getName(), ProjectImpl.myTypes());
    }
    
    /**
     * @param nextName
     * @param nextTypeURIs
     */
    public ProjectImplEnum(final String nextName, final List<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
    
}
