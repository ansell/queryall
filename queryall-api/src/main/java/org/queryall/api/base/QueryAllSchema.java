/**
 * 
 */
package org.queryall.api.base;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.queryall.api.provider.ProviderEnum;

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

    protected final void setName(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    @Override
    public String toString()
    {
        return getName();
    }
    
    public abstract boolean schemaToRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
        throws OpenRDFException;
}
