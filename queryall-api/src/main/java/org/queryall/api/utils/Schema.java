/**
 * 
 */
package org.queryall.api.utils;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.queryall.api.base.QueryAllSchema;
import org.queryall.api.services.SchemaServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class Schema
{
    private static final Logger log = LoggerFactory.getLogger(Schema.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = Schema.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = Schema.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = Schema.log.isInfoEnabled();
    
    public static Repository getSchemas(final Repository myRepository, final int configVersion)
    {
        return Schema.getSchemas(myRepository, null, configVersion);
    }
    
    public static Repository getSchemas(final Repository myRepository, final URI contextUri, final int configVersion)
    {
        for(final QueryAllSchema nextSchema : SchemaServiceLoader.getInstance().getAll())
        {
            try
            {
                nextSchema.schemaToRdf(myRepository, contextUri, configVersion);
            }
            catch(final OpenRDFException e)
            {
                Schema.log.error("Failed to generate schema for nextSchema=" + nextSchema.getName());
            }
        }
        
        return myRepository;
    }
    
}
