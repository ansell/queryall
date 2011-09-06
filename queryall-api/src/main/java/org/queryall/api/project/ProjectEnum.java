/**
 * 
 */
package org.queryall.api.project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.URI;
import org.queryall.api.services.QueryAllEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Project implementations register themselves with this enumeration when they are loaded.
 * 
 * NOTE: This is not an enumeration as java does not support extensible enumerations, but it should
 * act like one for all purposes other than java's underpowered switch case statement.
 * 
 */
public class ProjectEnum extends QueryAllEnum
{
    private static final Logger log = LoggerFactory.getLogger(ProjectEnum.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = ProjectEnum.log.isTraceEnabled();
    private static final boolean _DEBUG = ProjectEnum.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = ProjectEnum.log.isInfoEnabled();
    
    protected static final Collection<ProjectEnum> ALL_PROJECTS = new ArrayList<ProjectEnum>(5);
    
    public static Collection<ProjectEnum> byTypeUris(final Set<URI> nextTypeUris)
    {
        if(nextTypeUris.size() == 0)
        {
            if(ProjectEnum._DEBUG)
            {
                ProjectEnum.log.debug("found an empty URI set for nextProjectUris=" + nextTypeUris);
            }
            return Collections.emptySet();
        }
        
        final Set<ProjectEnum> results = new HashSet<ProjectEnum>();
        
        for(final ProjectEnum nextEnum : ProjectEnum.ALL_PROJECTS)
        {
            // NOTE: This restriction would force developers to include implementations for every possible combination of functionalities
            // This is not likely to be practical or useful, so it is not implemented
            // The minimum restriction is that there is at least one URI, ie, the standard default URI for this type of object
            // boolean matching = (nextProjectEnum.getTypeURIs().size() == nextProjectUris.size());
            boolean matching = true;
            
            for(final URI nextURI : nextTypeUris)
            {
                if(!nextEnum.getTypeURIs().contains(nextURI))
                {
                    if(ProjectEnum._DEBUG)
                    {
                        ProjectEnum.log.debug("found an empty URI set for nextURI=" + nextURI.stringValue());
                    }
                    
                    matching = false;
                }
            }
            
            if(matching)
            {
                if(ProjectEnum._DEBUG)
                {
                    ProjectEnum.log.debug("found an matching URI set for nextProjectUris=" + nextTypeUris);
                }
                results.add(nextEnum);
            }
        }
        
        if(ProjectEnum._DEBUG)
        {
            ProjectEnum.log.debug("returning results.size()=" + results.size() + " for nextProjectUris="
                    + nextTypeUris);
        }
        
        return results;
    }
    
    /**
     * Registers the specified project.
     */
    public static void register(final ProjectEnum nextProject)
    {
        if(ProjectEnum.valueOf(nextProject.getName()) != null)
        {
            if(ProjectEnum._DEBUG)
            {
                ProjectEnum.log.debug("Cannot register this project again name=" + nextProject.getName());
            }
        }
        else
        {
            ProjectEnum.ALL_PROJECTS.add(nextProject);
        }
    }
    
    public static ProjectEnum register(final String name, final Set<URI> typeURIs)
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
     * Returns all known/registered projects.
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
    public ProjectEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
        ProjectEnum.ALL_PROJECTS.add(this);
    }
}
