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
    private static final Logger LOG = LoggerFactory.getLogger(Schema.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = Schema.LOG.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = Schema.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = Schema.LOG.isInfoEnabled();
    
    /**
     * 
     * 
     * @param myRepository
     * @param configVersion
     * @param contextUri
     * @return
     * @throws OpenRDFException
     *             if an exception occured with the repository
     */
    public static Repository getSchemas(final Repository myRepository, final int configVersion, final URI... contextUri)
        throws OpenRDFException
    {
        for(final QueryAllSchema nextSchema : SchemaServiceLoader.getInstance().getAll())
        {
            try
            {
                nextSchema.schemaToRdf(myRepository, configVersion, contextUri);
            }
            catch(final OpenRDFException e)
            {
                Schema.LOG.error("Failed to generate schema for nextSchema=" + nextSchema.getName());
                // rethrow the exception
                throw e;
            }
        }
        
        return myRepository;
    }
    
}
