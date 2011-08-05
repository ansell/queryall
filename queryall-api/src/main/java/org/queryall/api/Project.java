package org.queryall.api;


public interface Project extends BaseQueryAllInterface, Comparable<Project>
{
    String getDescription();

    void setDescription(String description);

    org.openrdf.model.URI getAuthority();

    void setAuthority(org.openrdf.model.URI authority);
}
