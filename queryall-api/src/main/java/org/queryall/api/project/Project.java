package org.queryall.api.project;

import org.queryall.api.base.BaseQueryAllInterface;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface Project extends BaseQueryAllInterface, Comparable<Project>
{
    /**
     * 
     * @return The URI of the authority which is administrating this project
     */
    org.openrdf.model.URI getAuthority();
    
    /**
     * 
     * @param authority The URI of the authority which is administrating this project
     */
    void setAuthority(org.openrdf.model.URI authority);
}
