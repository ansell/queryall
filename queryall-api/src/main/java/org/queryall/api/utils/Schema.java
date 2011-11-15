/**
 * 
 */
package org.queryall.api.utils;

import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
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
        // FIXME: Use SchemaServiceLoader to fetch all of the known schemas
        return myRepository;
    }
    
}
