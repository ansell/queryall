/**
 * 
 */
package org.queryall.api.utils;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
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
    
    public static Repository getSchemas(final int configVersion) throws OpenRDFException
    {
        return Schema.getSchemas(null, null, configVersion);
    }
    
    public static Repository getSchemas(final Repository myRepository, final int configVersion) throws OpenRDFException
    {
        return Schema.getSchemas(myRepository, null, configVersion);
    }
    
    /**
     * 
     * 
     * @param myRepository
     * @param contextUri
     * @param configVersion
     * @return
     * @throws OpenRDFException if an exception occured with the repository
     */
    public static Repository getSchemas(Repository myRepository, final URI contextUri, final int configVersion) throws OpenRDFException
    {
        if(myRepository == null)
        {
            myRepository = new SailRepository(new MemoryStore());
            myRepository.initialize();
        }
        
        for(final QueryAllSchema nextSchema : SchemaServiceLoader.getInstance().getAll())
        {
            try
            {
                nextSchema.schemaToRdf(myRepository, contextUri, configVersion);
            }
            catch(final OpenRDFException e)
            {
                Schema.log.error("Failed to generate schema for nextSchema=" + nextSchema.getName());
                // rethrow the exception
                throw e;
            }
        }
        
        return myRepository;
    }
    
}
