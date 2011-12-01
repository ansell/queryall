/**
 * 
 */
package org.queryall.utils;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.queryall.api.base.QueryAllSchema;
import org.queryall.api.services.SchemaServiceLoader;
import org.queryall.query.Settings;
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
     * This field contains the expected number of schemas, if and when new schemas are added it
     * needs to be updated to match the expected number
     */
    private static final int CURRENT_EXPECTED_SCHEMA_COUNT = 33;
    
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
     * 
     * CURRENT_EXPECTED_SCHEMA_COUNT needs to be updated when new schemas are added
     */
    @Test
    public void testGetAll()
    {
        Assert.assertEquals("Did not find the expected number of schemas. Were there new schemas added recently?",
                SchemaServiceLoaderTest.CURRENT_EXPECTED_SCHEMA_COUNT, SchemaServiceLoader.getInstance().getAll()
                        .size());
        
        // for(QueryAllSchema nextSchema : SchemaServiceLoader.getInstance().getAll())
        // {
        // log.info("nextSchema="+nextSchema);
        // }
    }
    
    /**
     * Test method for {@link org.queryall.api.services.SchemaServiceLoader#getAll()}.
     * 
     * Tests that each QueryAllSchema.schemaToRdf is actually adding statements to the repository
     * 
     * @throws OpenRDFException
     */
    @Test
    public void testGetAllRepositories() throws OpenRDFException
    {
        final Repository testRepository = new SailRepository(new MemoryStore());
        testRepository.initialize();
        final ValueFactory vf = testRepository.getValueFactory();
        
        Assert.assertEquals(SchemaServiceLoaderTest.CURRENT_EXPECTED_SCHEMA_COUNT, SchemaServiceLoader.getInstance()
                .getAll().size());
        
        for(final QueryAllSchema nextSchema : SchemaServiceLoader.getInstance().getAll())
        {
            // log.info("nextSchema="+nextSchema);
            final RepositoryConnection testRepositoryConnection = testRepository.getConnection();
            
            final long previousCount = testRepositoryConnection.size();
            
            final boolean result =
                    nextSchema.schemaToRdf(testRepository, Settings.CONFIG_API_VERSION,
                            vf.createURI("http://test.queryall.example.com/schema/" + nextSchema.toString()));
            
            if(!result)
            {
                Assert.fail("Failed to put the RDF statements for the schema into the repository for nextSchema="
                        + nextSchema);
            }
            
            final long afterCount = testRepositoryConnection.size();
            
            SchemaServiceLoaderTest.log.info(" statementsAdded=" + (afterCount - previousCount) + "\tafterCount="
                    + afterCount + "\tpreviousCount=" + previousCount + "\tnextSchema=" + nextSchema);
            
            // test that statements were actually added, no schema should be empty in practice
            Assert.assertTrue("A schema failed to add any statements to the repository nextSchema=" + nextSchema,
                    afterCount > previousCount);
        }
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.services.SchemaServiceLoader#getKey(org.queryall.api.base.QueryAllSchema)}
     * .
     */
    @Test
    public void testGetKeyQueryAllSchema()
    {
        Assert.assertEquals(SchemaServiceLoaderTest.CURRENT_EXPECTED_SCHEMA_COUNT, SchemaServiceLoader.getInstance()
                .getAll().size());
        
        final Set<String> allSchemaNames = new HashSet<String>();
        
        for(final QueryAllSchema nextSchema : SchemaServiceLoader.getInstance().getAll())
        {
            // test for null schema names
            Assert.assertNotNull("Schema name should not be null", nextSchema.getName());
            
            // test that the schema name doesn't just contain whitespace
            Assert.assertTrue(nextSchema.getName().trim().length() > 0);
            
            Assert.assertFalse(allSchemaNames.contains(nextSchema.getName()));
            
            allSchemaNames.add(nextSchema.getName());
            
            // verify that the schema fetched using .get(String) is the same as the one we are
            // looking at
            Assert.assertEquals(nextSchema, SchemaServiceLoader.getInstance().get(nextSchema.getName()));
        }
        
        // Verify that unique, non-null strings were found for each schema
        // According to the Set contract, this should never fail, as the assertion in the loop,
        // assertFalse(allSchemaNames.contains()), should fail first, but checking just to make it
        // obvious
        Assert.assertEquals(SchemaServiceLoaderTest.CURRENT_EXPECTED_SCHEMA_COUNT, allSchemaNames.size());
    }
    
}
