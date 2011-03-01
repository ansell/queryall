package org.queryall;

import org.queryall.BaseQueryAllInterface;
import org.openrdf.model.URI;

public abstract class Project implements BaseQueryAllInterface, Comparable<Project>
{
    public abstract String getTitle();

    public abstract void setTitle(String title);

    public abstract String getDescription();

    public abstract void setDescription(String description);

    public abstract org.openrdf.model.URI getAuthority();

    public abstract void setAuthority(org.openrdf.model.URI authority);

    
}
