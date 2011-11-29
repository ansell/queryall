/**
 * 
 */
package org.queryall.api.base;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;

/**
 * 
 * Used to indicate that a class provides a schema for QueryAll objects
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public abstract class QueryAllSchema
{
    // private static final Set<QueryAllSchema> ALL_SCHEMAS = new HashSet<QueryAllSchema>();
    
    private String name;
    
    public QueryAllSchema()
    {
        
    }
    
    public QueryAllSchema(final String nextName)
    {
        this.setName(nextName);
        // QueryAllSchema.ALL_SCHEMAS.add(this);
    }
    
    @Override
    public boolean equals(final Object obj)
    {
        if(this == obj)
        {
            return true;
        }
        if(obj == null)
        {
            return false;
        }
        if(!(obj instanceof QueryAllSchema))
        {
            return false;
        }
        final QueryAllSchema other = (QueryAllSchema)obj;
        if(this.name == null)
        {
            if(other.name != null)
            {
                return false;
            }
        }
        else if(!this.name.equals(other.name))
        {
            return false;
        }
        return true;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }
    
    public abstract boolean schemaToRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
        throws OpenRDFException;
    
    protected final void setName(final String name)
    {
        this.name = name;
    }
    
    @Override
    public String toString()
    {
        return this.getName();
    }
}
