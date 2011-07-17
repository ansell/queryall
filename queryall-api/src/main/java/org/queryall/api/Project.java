package org.queryall.api;


public interface Project extends BaseQueryAllInterface, Comparable<Project>
{
    public abstract String getDescription();

    public abstract void setDescription(String description);

    public abstract org.openrdf.model.URI getAuthority();

    public abstract void setAuthority(org.openrdf.model.URI authority);
}
