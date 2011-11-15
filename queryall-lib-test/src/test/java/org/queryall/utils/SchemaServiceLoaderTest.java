/**
 * 
 */
package org.queryall.utils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.queryall.api.base.QueryAllSchema;
import org.queryall.api.services.SchemaServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 *
 */
public class SchemaServiceLoaderTest
{
    private static final Logger log = LoggerFactory.getLogger(SchemaServiceLoaderTest.class);
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
    }
    
    /**
     * Test method for {@link org.queryall.api.services.SchemaServiceLoader#getAll()}.
     */
    @Test
    public void testGetAll()
    {
        Assert.assertEquals(32, SchemaServiceLoader.getInstance().getAll().size());
        
        for(QueryAllSchema nextSchema : SchemaServiceLoader.getInstance().getAll())
        {
            log.info("nextSchema="+nextSchema.getName());
        }
    }
    
    /**
     * Test method for {@link org.queryall.api.services.SchemaServiceLoader#getKey(org.queryall.api.base.QueryAllSchema)}.
     */
    @Test
    @Ignore
    public void testGetKeyQueryAllSchema()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
}
