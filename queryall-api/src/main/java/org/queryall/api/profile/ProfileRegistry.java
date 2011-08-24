/**
 * 
 */
package org.queryall.api.profile;

import org.queryall.api.project.ProjectEnum;
import org.queryall.api.project.ProjectFactory;
import org.queryall.api.services.AbstractServiceLoader;

/**
 * Dynamically loads and keeps a track of the different Project's that are available.
 * 
 * Uses ProjectEnum objects as keys, as defined in ProjectFactory
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProfileRegistry extends AbstractServiceLoader<ProjectEnum, ProjectFactory>
{
    private static ProfileRegistry defaultRegistry;
    
    // RDFParserRegistry.getInstance();
    //
    public static synchronized ProfileRegistry getInstance()
    {
        if(ProfileRegistry.defaultRegistry == null)
        {
            ProfileRegistry.defaultRegistry = new ProfileRegistry();
        }
        
        return ProfileRegistry.defaultRegistry;
        
    }
    
    public ProfileRegistry()
    {
        super(ProjectFactory.class);
    }
    
    @Override
    protected ProjectEnum getKey(final ProjectFactory factory)
    {
        return factory.getEnum();
    }
    
}
