package org.queryall.api;


public abstract class Project implements BaseQueryAllInterface, Comparable<Project>
{
    public abstract String getDescription();

    public abstract void setDescription(String description);

    public abstract org.openrdf.model.URI getAuthority();

    public abstract void setAuthority(org.openrdf.model.URI authority);

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        
        result.append("title=").append(this.getTitle());
        result.append("key=").append(this.getKey().stringValue());

        return result.toString();
    }    
}
