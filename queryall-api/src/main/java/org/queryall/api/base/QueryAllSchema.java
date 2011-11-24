/**
 * 
 */
package org.queryall.api.base;

import java.util.HashSet;
import java.util.Set;

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
    private static final Set<QueryAllSchema> ALL_SCHEMAS = new HashSet<QueryAllSchema>();
    
    private String name;
    
    public QueryAllSchema()
    {
        
    }
    
    public QueryAllSchema(final String nextName)
    {
        this.setName(nextName);
        QueryAllSchema.ALL_SCHEMAS.add(this);
    }
    
    public String getName()
    {
        return this.name;
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
