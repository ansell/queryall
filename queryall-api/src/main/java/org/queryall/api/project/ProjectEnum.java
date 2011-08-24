/**
 * 
 */
package org.queryall.api.project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.openrdf.model.URI;
import org.queryall.api.services.QueryAllEnum;

/**
 * Project implementations register themselves with this enumeration when they are loaded.
 * 
 * NOTE: This is not an enumeration as java does not support extensible enumerations, but it should
 * act like one for all purposes other than java's underpowered switch case statement.
 * 
 */
public class ProjectEnum extends QueryAllEnum
{
    protected static final Collection<ProjectEnum> ALL_PROJECTS = new ArrayList<ProjectEnum>(5);
    
    public static Collection<ProjectEnum> byTypeUris(final List<URI> nextProjectUris)
    {
        final List<ProjectEnum> results = new ArrayList<ProjectEnum>(ProjectEnum.ALL_PROJECTS.size());
        
        for(final ProjectEnum nextProjectEnum : ProjectEnum.ALL_PROJECTS)
        {
            if(nextProjectEnum.getTypeURIs().equals(nextProjectUris))
            {
                results.add(nextProjectEnum);
            }
        }
        
        return results;
    }
    
    /**
     * Registers the specified query type.
     */
    public static void register(final ProjectEnum nextProject)
    {
        if(ProjectEnum.valueOf(nextProject.getName()) != null)
        {
            QueryAllEnum.log.error("Cannot register this project again name=" + nextProject.getName());
        }
        else
        {
            ProjectEnum.ALL_PROJECTS.add(nextProject);
        }
    }
    
    public static ProjectEnum register(final String name, final List<URI> typeURIs)
    {
        final ProjectEnum newProjectEnum = new ProjectEnum(name, typeURIs);
        ProjectEnum.register(newProjectEnum);
        return newProjectEnum;
    }
    
    public static ProjectEnum valueOf(final String string)
    {
        for(final ProjectEnum nextProjectEnum : ProjectEnum.ALL_PROJECTS)
        {
            if(nextProjectEnum.getName().equals(string))
            {
                return nextProjectEnum;
            }
        }
        
        return null;
    }
    
    /**
     * Returns all known/registered query types.
     */
    public static Collection<ProjectEnum> values()
    {
        return Collections.unmodifiableCollection(ProjectEnum.ALL_PROJECTS);
    }
    
    /**
     * Create a new Project enum using the given name, which must be unique
     * 
     * @param nextName
     * @param nextTypeURIs
     */
    public ProjectEnum(final String nextName, final List<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
}
