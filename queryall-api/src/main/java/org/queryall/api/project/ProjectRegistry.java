/**
 * 
 */
package org.queryall.api.project;

import org.queryall.api.services.AbstractServiceLoader;

/**
 * Dynamically loads and keeps a track of the different Project's that are available.
 * 
 * Uses ProjectEnum objects as keys, as defined in ProjectFactory
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProjectRegistry extends AbstractServiceLoader<ProjectEnum, ProjectFactory>
{
    private static final Object LOCK = new Object();
    
    private static volatile ProjectRegistry defaultRegistry;
    
    public static ProjectRegistry getInstance()
    {
        if(ProjectRegistry.defaultRegistry == null)
        {
            synchronized(ProjectRegistry.LOCK)
            {
                if(ProjectRegistry.defaultRegistry == null)
                {
                    ProjectRegistry.defaultRegistry = new ProjectRegistry();
                }
            }
        }
        
        return ProjectRegistry.defaultRegistry;
        
    }
    
    public ProjectRegistry()
    {
        super(ProjectFactory.class);
    }
    
    @Override
    protected ProjectEnum getKey(final ProjectFactory factory)
    {
        return factory.getEnum();
    }
    
}
