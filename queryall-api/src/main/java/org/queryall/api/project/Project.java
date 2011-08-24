package org.queryall.api.project;

import org.queryall.api.base.BaseQueryAllInterface;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface Project extends BaseQueryAllInterface, Comparable<Project>
{
    org.openrdf.model.URI getAuthority();
    
    String getDescription();
    
    void setAuthority(org.openrdf.model.URI authority);
    
    void setDescription(String description);
}
