package org.queryall.utils.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;
import org.queryall.utils.Settings;
import org.queryall.utils.SettingsFactory;

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
    private ValueFactory testValueFactory;

    @Before
    public void setUp() throws Exception
    {
        testRepository = new SailRepository(new MemoryStore());
        testRepository.initialize();
        
        testValueFactory = testRepository.getValueFactory();
        
        testRepositoryConnection = testRepository.getConnection();
        
        // verify before all tests that testRepositoryConnection does not contain any triples which could interfere with tests
        Assert.assertEquals(0L, testRepositoryConnection.size());        
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
        
        this.testValueFactory = null;
        
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
    
    @Test
    public final void testExtractProperties() throws RDFParseException, RepositoryException, IOException
    {
        Collection<URI> webappConfigUris = new ArrayList<URI>(2);
        
        URI defaultConfigUri = testValueFactory.createURI("http://example.org/test/webappconfig/default");
        URI locationSpecificConfigUri = testValueFactory.createURI("http://example.org/test/webappconfig/locationSpecific");
        
        webappConfigUris.add(defaultConfigUri);
        webappConfigUris.add(locationSpecificConfigUri);
        
        // import the properties from the standard two configuration file format using two different config URIs
        InputStream testDefaultInput = SettingsFactoryTest.class.getResourceAsStream("/testconfigs/webapp-config-test-default.n3");
        
        Assert.assertNotNull(testDefaultInput);
        
        testRepositoryConnection.add(testDefaultInput, "", RDFFormat.N3);
        
        testRepositoryConnection.commit();
        
        Assert.assertEquals(148, testRepositoryConnection.size());

        InputStream testSpecificInput = SettingsFactoryTest.class.getResourceAsStream("/testconfigs/webapp-config-test-locationspecific.n3");
        
        Assert.assertNotNull(testSpecificInput);
        
        testRepositoryConnection.add(testSpecificInput, "", RDFFormat.N3);
        
        testRepositoryConnection.commit();
        
        Assert.assertEquals(260, testRepositoryConnection.size());
        
        // TODO: test the properties have been pulled into testRepository correctly
        
        URI useHardcodedRequestHostnameUri = testValueFactory.createURI("http://purl.org/queryall/webapp_configuration:useHardcodedRequestHostname");

        Assert.assertTrue(testRepositoryConnection.hasStatement(null, useHardcodedRequestHostnameUri, null, false));
        
        Assert.assertTrue(testRepositoryConnection.hasStatement(locationSpecificConfigUri, useHardcodedRequestHostnameUri, null, false));
        
        // setup the test Settings object to extract the properties into
        // the property add methods for Settings are unit tested in AbstractQueryAllConfigurationTest which is overridden eventually by SettingsTest
        Settings testSettings = new Settings();
        
        SettingsFactory.extractProperties(testSettings, testRepository, webappConfigUris);
        
        // TODO: test the expected list of properties from testRepository against the properties available from testSettings
        
        Assert.assertTrue("boolean property not set correctly", testSettings.getBooleanProperty("useHardcodedRequestHostname", false));
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
    
    @Test
    public final void testGetBackupConfigLocations() throws Exception
    {
        InputStream testInput = SettingsFactoryTest.class.getResourceAsStream("/testconfigs/configLocationsTest.n3");
        
        Assert.assertNotNull(testInput);
        
        testRepositoryConnection.add(testInput, "", RDFFormat.N3);
        
        testRepositoryConnection.commit();
        
        Collection<String> webappConfigLocations = SettingsFactory.getBackupConfigLocations(testRepository, Arrays.asList(testValueFactory.createURI("http://example.org/test/webappconfig/locationSpecific")));
        
        Assert.assertEquals(2, webappConfigLocations.size());
        
        Assert.assertTrue(webappConfigLocations.contains("/testconfigs/ruletest-1.n3"));
        Assert.assertTrue(webappConfigLocations.contains("/testconfigs/provider-1.n3"));
    }
    
    @Test
    public final void testGetConfigLocations() throws Exception
    {
        InputStream testInput = SettingsFactoryTest.class.getResourceAsStream("/testconfigs/configLocationsTest.n3");
        
        Assert.assertNotNull(testInput);
        
        testRepositoryConnection.add(testInput, "", RDFFormat.N3);
        
        testRepositoryConnection.commit();
        
        Collection<String> webappConfigLocations = SettingsFactory.getConfigLocations(testRepository, Arrays.asList(testValueFactory.createURI("http://example.org/test/webappconfig/locationSpecific")));
        
        Assert.assertEquals(2, webappConfigLocations.size());
        
        Assert.assertTrue(webappConfigLocations.contains("/testconfigs/namespaceentry-1.n3"));
        Assert.assertTrue(webappConfigLocations.contains("/testconfigs/normalisationrule-1.n3"));
    }
    
    @Test
    public final void testGetDefaultBaseConfigLocationProperty()
    {
        String defaultBaseConfigLocationProperty = SettingsFactory.getDefaultBaseConfigLocationProperty();
        
        Assert.assertEquals("/queryallBaseConfig.n3", defaultBaseConfigLocationProperty);
        
        // now set the system property to see if it changes the default
        System.setProperty("queryall.BaseConfigLocation", "/testallyourbasearebelongtous.n3");
        
        String alteredBaseConfigLocationProperty = SettingsFactory.getDefaultBaseConfigLocationProperty();
        
        Assert.assertEquals("/testallyourbasearebelongtous.n3", alteredBaseConfigLocationProperty);
        
        System.clearProperty("queryall.BaseConfigLocation");
    }

    @Test
    public final void testGetDefaultBaseConfigMimeFormatProperty()
    {
        String defaultBaseConfigMimeFormatProperty = SettingsFactory.getDefaultBaseConfigMimeFormatProperty();
        
        Assert.assertEquals("text/rdf+n3", defaultBaseConfigMimeFormatProperty);
        
        // now set the system property to see if it changes the default
        System.setProperty("queryall.BaseConfigMimeFormat", "application/rdf+xml");
        
        String alteredBaseConfigMimeFormatProperty = SettingsFactory.getDefaultBaseConfigMimeFormatProperty();
        
        Assert.assertEquals("application/rdf+xml", alteredBaseConfigMimeFormatProperty);
        
        System.clearProperty("queryall.BaseConfigMimeFormat");
    }
    
    @Test
    public final void testGetDefaultBaseConfigUriProperty()
    {
        String defaultBaseConfigUriProperty = SettingsFactory.getDefaultBaseConfigUriProperty();
        
        Assert.assertEquals("http://purl.org/queryall/webapp_configuration:theBaseConfig", defaultBaseConfigUriProperty);
        
        // now set the system property to see if it changes the default
        System.setProperty("queryall.BaseConfigUri", "http://example.org/mytest");
        
        String alteredBaseConfigUriProperty = SettingsFactory.getDefaultBaseConfigUriProperty();
        
        Assert.assertEquals("http://example.org/mytest", alteredBaseConfigUriProperty);
        
        System.clearProperty("queryall.BaseConfigUri");
    }
    
    @Ignore
    @Test
    public final void testGetStatementProperties()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    @Test
    public final void testGetWebappConfigLocations() throws Exception
    {
        InputStream testInput = SettingsFactoryTest.class.getResourceAsStream("/queryallBaseConfig.n3");
        
        Assert.assertNotNull(testInput);
        
        testRepositoryConnection.add(testInput, "", RDFFormat.N3);
        
        testRepositoryConnection.commit();
        
        Collection<Value> webappConfigLocations = SettingsFactory.getWebappConfigLocations(testRepository, testValueFactory.createURI("http://example.org/test/webappconfig/testWebappBaseConfig"));
        
        Assert.assertEquals(1, webappConfigLocations.size());
        
        Assert.assertTrue(webappConfigLocations.contains(testValueFactory.createLiteral("http://queryall.example.org/test/webappconfigLocation")));
    }
    
    @Test
    public final void testGetWebappConfigUris() throws Exception
    {
        InputStream testInput = SettingsFactoryTest.class.getResourceAsStream("/queryallBaseConfig.n3");
        
        Assert.assertNotNull(testInput);
        
        testRepositoryConnection.add(testInput, "", RDFFormat.N3);
        
        testRepositoryConnection.commit();
        
        Collection<URI> webappConfigUris = SettingsFactory.getWebappConfigUris(testRepository, testValueFactory.createURI("http://example.org/test/webappconfig/testWebappBaseConfig"));
        
        Assert.assertEquals(2, webappConfigUris.size());
        
        Assert.assertTrue(webappConfigUris.contains(testValueFactory.createURI("http://example.org/test/webappconfig/default")));
        Assert.assertTrue(webappConfigUris.contains(testValueFactory.createURI("http://example.org/test/webappconfig/locationSpecific")));
    }
    
}
