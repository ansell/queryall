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
import org.queryall.api.utils.WebappConfig;
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
        this.testRepository = new SailRepository(new MemoryStore());
        this.testRepository.initialize();
        
        this.testValueFactory = this.testRepository.getValueFactory();
        
        this.testRepositoryConnection = this.testRepository.getConnection();
        
        // verify before all tests that testRepositoryConnection does not contain any triples which
        // could interfere with tests
        Assert.assertEquals(0L, this.testRepositoryConnection.size());
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
            catch(final RepositoryException rex)
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
        catch(final RepositoryException rex)
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
        final Collection<URI> webappConfigUris = new ArrayList<URI>(2);
        
        final URI defaultConfigUri = this.testValueFactory.createURI("http://example.org/test/webappconfig/default");
        final URI locationSpecificConfigUri =
                this.testValueFactory.createURI("http://example.org/test/webappconfig/locationSpecific");
        
        webappConfigUris.add(defaultConfigUri);
        webappConfigUris.add(locationSpecificConfigUri);
        
        // import the properties from the standard two configuration file format using two different
        // config URIs
        final InputStream testDefaultInput =
                SettingsFactoryTest.class.getResourceAsStream("/testconfigs/webapp-config-test-default.n3");
        
        Assert.assertNotNull(testDefaultInput);
        
        this.testRepositoryConnection.add(testDefaultInput, "", RDFFormat.N3);
        
        this.testRepositoryConnection.commit();
        
        Assert.assertEquals(148, this.testRepositoryConnection.size());
        
        final InputStream testSpecificInput =
                SettingsFactoryTest.class.getResourceAsStream("/testconfigs/webapp-config-test-locationspecific.n3");
        
        Assert.assertNotNull(testSpecificInput);
        
        this.testRepositoryConnection.add(testSpecificInput, "", RDFFormat.N3);
        
        this.testRepositoryConnection.commit();
        
        Assert.assertEquals(260, this.testRepositoryConnection.size());
        
        // TODO: test the properties have been pulled into testRepository correctly
        
        final URI useHardcodedRequestHostnameUri =
                this.testValueFactory
                        .createURI("http://purl.org/queryall/webapp_configuration:useHardcodedRequestHostname");
        
        Assert.assertTrue(this.testRepositoryConnection.hasStatement(null, useHardcodedRequestHostnameUri, null, false));
        
        Assert.assertTrue(this.testRepositoryConnection.hasStatement(locationSpecificConfigUri,
                useHardcodedRequestHostnameUri, null, false));
        
        // setup the test Settings object to extract the properties into
        // the property add methods for Settings are unit tested in
        // AbstractQueryAllConfigurationTest which is overridden eventually by SettingsTest
        final Settings testSettings = new Settings();
        
        SettingsFactory.extractProperties(testSettings, this.testRepository, webappConfigUris);
        
        // TODO: test the expected list of properties from testRepository against the properties
        // available from testSettings
        
        Assert.assertTrue("boolean property not set correctly",
                testSettings.getBooleanProperty(WebappConfig.USE_HARDCODED_REQUEST_HOSTNAME));
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
        final InputStream testInput =
                SettingsFactoryTest.class.getResourceAsStream("/testconfigs/configLocationsTest.n3");
        
        Assert.assertNotNull(testInput);
        
        this.testRepositoryConnection.add(testInput, "", RDFFormat.N3);
        
        this.testRepositoryConnection.commit();
        
        final Collection<String> webappConfigLocations =
                SettingsFactory.getBackupConfigLocations(this.testRepository, Arrays.asList(this.testValueFactory
                        .createURI("http://example.org/test/webappconfig/locationSpecific")));
        
        Assert.assertEquals(2, webappConfigLocations.size());
        
        Assert.assertTrue(webappConfigLocations.contains("/testconfigs/ruletest-1.n3"));
        Assert.assertTrue(webappConfigLocations.contains("/testconfigs/provider-1.n3"));
    }
    
    @Test
    public final void testGetConfigLocations() throws Exception
    {
        final InputStream testInput =
                SettingsFactoryTest.class.getResourceAsStream("/testconfigs/configLocationsTest.n3");
        
        Assert.assertNotNull(testInput);
        
        this.testRepositoryConnection.add(testInput, "", RDFFormat.N3);
        
        this.testRepositoryConnection.commit();
        
        final Collection<String> webappConfigLocations =
                SettingsFactory.getConfigLocations(this.testRepository, Arrays.asList(this.testValueFactory
                        .createURI("http://example.org/test/webappconfig/locationSpecific")));
        
        Assert.assertEquals(2, webappConfigLocations.size());
        
        Assert.assertTrue(webappConfigLocations.contains("/testconfigs/namespaceentry-1.n3"));
        Assert.assertTrue(webappConfigLocations.contains("/testconfigs/normalisationrule-1.n3"));
    }
    
    @Test
    public final void testGetDefaultBaseConfigLocationProperty()
    {
        final String defaultBaseConfigLocationProperty = SettingsFactory.getDefaultBaseConfigLocationProperty();
        
        Assert.assertEquals("/queryallBaseConfig.n3", defaultBaseConfigLocationProperty);
        
        // now set the system property to see if it changes the default
        System.setProperty("queryall.BaseConfigLocation", "/testallyourbasearebelongtous.n3");
        
        final String alteredBaseConfigLocationProperty = SettingsFactory.getDefaultBaseConfigLocationProperty();
        
        Assert.assertEquals("/testallyourbasearebelongtous.n3", alteredBaseConfigLocationProperty);
        
        System.clearProperty("queryall.BaseConfigLocation");
    }
    
    @Test
    public final void testGetDefaultBaseConfigMimeFormatProperty()
    {
        final String defaultBaseConfigMimeFormatProperty = SettingsFactory.getDefaultBaseConfigMimeFormatProperty();
        
        Assert.assertEquals("text/rdf+n3", defaultBaseConfigMimeFormatProperty);
        
        // now set the system property to see if it changes the default
        System.setProperty("queryall.BaseConfigMimeFormat", "application/rdf+xml");
        
        final String alteredBaseConfigMimeFormatProperty = SettingsFactory.getDefaultBaseConfigMimeFormatProperty();
        
        Assert.assertEquals("application/rdf+xml", alteredBaseConfigMimeFormatProperty);
        
        System.clearProperty("queryall.BaseConfigMimeFormat");
    }
    
    @Test
    public final void testGetDefaultBaseConfigUriProperty()
    {
        final String defaultBaseConfigUriProperty = SettingsFactory.getDefaultBaseConfigUriProperty();
        
        Assert.assertEquals("http://purl.org/queryall/webapp_configuration:theBaseConfig", defaultBaseConfigUriProperty);
        
        // now set the system property to see if it changes the default
        System.setProperty("queryall.BaseConfigUri", "http://example.org/mytest");
        
        final String alteredBaseConfigUriProperty = SettingsFactory.getDefaultBaseConfigUriProperty();
        
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
        final InputStream testInput = SettingsFactoryTest.class.getResourceAsStream("/queryallBaseConfig.n3");
        
        Assert.assertNotNull(testInput);
        
        this.testRepositoryConnection.add(testInput, "", RDFFormat.N3);
        
        this.testRepositoryConnection.commit();
        
        final Collection<Value> webappConfigLocations =
                SettingsFactory.getWebappConfigLocations(this.testRepository,
                        this.testValueFactory.createURI("http://example.org/test/webappconfig/testWebappBaseConfig"));
        
        Assert.assertEquals(1, webappConfigLocations.size());
        
        Assert.assertTrue(webappConfigLocations.contains(this.testValueFactory
                .createLiteral("http://queryall.example.org/test/webappconfigLocation")));
    }
    
    @Test
    public final void testGetWebappConfigUris() throws Exception
    {
        final InputStream testInput = SettingsFactoryTest.class.getResourceAsStream("/queryallBaseConfig.n3");
        
        Assert.assertNotNull(testInput);
        
        this.testRepositoryConnection.add(testInput, "", RDFFormat.N3);
        
        this.testRepositoryConnection.commit();
        
        final Collection<URI> webappConfigUris =
                SettingsFactory.getWebappConfigUris(this.testRepository,
                        this.testValueFactory.createURI("http://example.org/test/webappconfig/testWebappBaseConfig"));
        
        Assert.assertEquals(2, webappConfigUris.size());
        
        Assert.assertTrue(webappConfigUris.contains(this.testValueFactory
                .createURI("http://example.org/test/webappconfig/default")));
        Assert.assertTrue(webappConfigUris.contains(this.testValueFactory
                .createURI("http://example.org/test/webappconfig/locationSpecific")));
    }
    
}
