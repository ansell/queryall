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
    public abstract String getName();
    
    public abstract boolean schemaToRdf(final Repository myRepository, final URI keyToUse, final int modelVersion)
        throws OpenRDFException;
}
