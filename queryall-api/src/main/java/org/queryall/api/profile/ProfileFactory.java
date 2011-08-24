/**
 * 
 */
package org.queryall.api.profile;

import org.queryall.api.project.Project;
import org.queryall.api.project.ProjectEnum;
import org.queryall.api.project.ProjectParser;
import org.queryall.api.services.QueryAllFactory;

/**
 * A factory for creating parsers for Project objects
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface ProfileFactory extends QueryAllFactory<ProjectEnum, ProjectParser, Project>
{
    
}
