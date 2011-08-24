/**
 * 
 */
package org.queryall.api.project;

import org.queryall.api.services.QueryAllFactory;

/**
 * A factory for creating parsers for Project objects
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface ProjectFactory extends QueryAllFactory<ProjectEnum, ProjectParser, Project>
{
    
}
