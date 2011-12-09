package org.queryall.utils.test;

import java.io.InputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

/**
 * Tests the SettingsFactory class which is the main interface for extracting queryall objects and
 * property settings from Sesame Repositories
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class SettingsFactoryTest
{
    
    private Repository testRepository;
    private RepositoryConnection testRepositoryConnection;

    @Before
    public void setUp() throws Exception
    {
        testRepository = new SailRepository(new MemoryStore());
        testRepository.initialize();
        
        testRepositoryConnection = testRepository.getConnection();
    }
    
    @After
    public void tearDown()
    {
        if(this.testRepositoryConnection != null)
        {
            try
            {
                this.testRepositoryConnection.close();
            }
            catch(RepositoryException rex)
            {
                rex.printStackTrace();
            }
            finally
            {
                this.testRepositoryConnection = null;
            }
        }
        
        try
        {
            this.testRepository.shutDown();
        }
        catch(RepositoryException rex)
        {
            rex.printStackTrace();
        }
        finally
        {
            this.testRepository = null;
        }
    }
    
    @Ignore
    @Test
    public final void testAddNamespaceEntries()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    @Ignore
    @Test
    public final void testAddNormalisationRules()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    @Ignore
    @Test
    public final void testAddProfiles()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    @Ignore
    @Test
    public final void testAddProviders()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    @Ignore
    @Test
    public final void testAddQueryTypes()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    @Ignore
    @Test
    public final void testAddRuleTests()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    @Ignore
    @Test
    public final void testConfigRefreshCheck()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    @Ignore
    @Test
    public final void testExtractProperties()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    @Ignore
    @Test
    public final void testGenerateSettings()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    @Ignore
    @Test
    public final void testGenerateSettingsStringStringString()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    @Ignore
    @Test
    public final void testGetBackupConfigLocations()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    @Ignore
    @Test
    public final void testGetConfigLocations()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    @Ignore
    @Test
    public final void testGetDefaultBaseConfigLocationProperty()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    @Ignore
    @Test
    public final void testGetDefaultBaseConfigMimeFormatProperty()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    @Ignore
    @Test
    public final void testGetDefaultBaseConfigUriProperty()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    @Ignore
    @Test
    public final void testGetStatementProperties()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    @Ignore
    @Test
    public final void testGetWebappConfigLocations()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    @Test
    public final void testGetWebappConfigUris()
    {
        InputStream testInput = SettingsFactoryTest.class.getResourceAsStream("/queryallBaseConfig.n3");
        
        Assert.assertNotNull(testInput);
        
        testRepositoryConnection.add(testInput, "", RDFFormat.N3);
    }
    
}
