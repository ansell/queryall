/**
 * 
 */
package org.queryall.servlets.queryparsers.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.WebappConfig;
import org.queryall.servlets.queryparsers.ConfigurationQueryOptions;
import org.queryall.utils.Settings;
import org.queryall.utils.SettingsFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class ConfigurationQueryOptionsTest
{
    
    private QueryAllConfiguration settings;
    private String contextPath;
    private String adminPath;
    private String adminConfigurationPath;
    private String adminConfigurationApiSuffix;
    private String versionedAdminConfigurationPathPrefix;
    
    /**
     * @param format
     * @param contentType
     */
    protected void assertAdminConfigurationFormat(final String format, final String contentType)
    {
        final ConfigurationQueryOptions options =
                new ConfigurationQueryOptions(this.getAdminConfigurationUrl(format), this.contextPath, this.settings);
        Assert.assertTrue(options.containsExplicitFormat());
        Assert.assertEquals(contentType, options.getExplicitFormat());
    }
    
    /**
     * @param format
     * @return
     */
    protected String getAdminConfigurationUrl(final String format)
    {
        return this.versionedAdminConfigurationPathPrefix + format + this.adminConfigurationApiSuffix;
    }
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        this.settings = new Settings();
        this.contextPath = "/queryall-test-context";
        this.adminPath = this.contextPath + this.settings.getString(WebappConfig.ADMIN_URL_PREFIX);
        this.adminConfigurationPath = this.adminPath + this.settings.getString(WebappConfig.ADMIN_CONFIGURATION_PREFIX);
        final String adminConfigurationApiOpeningPrefix =
                this.settings.getString(WebappConfig.ADMIN_CONFIGURATION_API_VERSION_OPENING_PREFIX);
        final String adminConfigurationApiClosingPrefix =
                this.settings.getString(WebappConfig.ADMIN_CONFIGURATION_API_VERSION_CLOSING_PREFIX);
        this.adminConfigurationApiSuffix = this.settings.getString(WebappConfig.ADMIN_CONFIGURATION_API_VERSION_SUFFIX);
        
        this.versionedAdminConfigurationPathPrefix =
                this.adminConfigurationPath + adminConfigurationApiOpeningPrefix + SettingsFactory.CONFIG_API_VERSION
                        + adminConfigurationApiClosingPrefix;
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        this.settings = null;
    }
    
    /**
     * Test method for
     * {@link org.queryall.servlets.queryparsers.ConfigurationQueryOptions#ConfigurationQueryOptions(java.lang.String, java.lang.String, org.queryall.api.base.QueryAllConfiguration)}
     * .
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testConfigurationQueryOptions()
    {
        Assert.fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.queryall.servlets.queryparsers.ConfigurationQueryOptions#containsAdminBasicWebappConfiguration()}
     * .
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testContainsAdminBasicWebappConfiguration()
    {
        Assert.fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.queryall.servlets.queryparsers.ConfigurationQueryOptions#containsAdminConfiguration()}
     * .
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testContainsAdminConfiguration()
    {
        Assert.fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.queryall.servlets.queryparsers.ConfigurationQueryOptions#containsExplicitApiVersion()}
     * .
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testContainsExplicitApiVersion()
    {
        Assert.fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.queryall.servlets.queryparsers.ConfigurationQueryOptions#containsExplicitFormat()}
     * .
     */
    @Test
    public void testContainsExplicitFormatHtml()
    {
        this.assertAdminConfigurationFormat("html", Constants.TEXT_HTML);
    }
    
    /**
     * Test method for
     * {@link org.queryall.servlets.queryparsers.ConfigurationQueryOptions#containsExplicitFormat()}
     * .
     */
    @Test
    public void testContainsExplicitFormatJsonld()
    {
        this.assertAdminConfigurationFormat("jsonld", Constants.APPLICATION_LD_JSON);
    }
    
    /**
     * Test method for
     * {@link org.queryall.servlets.queryparsers.ConfigurationQueryOptions#containsExplicitFormat()}
     * .
     */
    @Test
    public void testContainsExplicitFormatN3()
    {
        this.assertAdminConfigurationFormat("n3", Constants.TEXT_RDF_N3);
    }
    
    /**
     * Test method for
     * {@link org.queryall.servlets.queryparsers.ConfigurationQueryOptions#containsExplicitFormat()}
     * .
     */
    @Test
    public void testContainsExplicitFormatNQuads()
    {
        this.assertAdminConfigurationFormat("nquads", Constants.TEXT_X_NQUADS);
    }
    
    /**
     * Test method for
     * {@link org.queryall.servlets.queryparsers.ConfigurationQueryOptions#containsExplicitFormat()}
     * .
     */
    @Test
    public void testContainsExplicitFormatNTriples()
    {
        this.assertAdminConfigurationFormat("ntriples", Constants.TEXT_PLAIN);
    }
    
    /**
     * Test method for
     * {@link org.queryall.servlets.queryparsers.ConfigurationQueryOptions#containsExplicitFormat()}
     * .
     */
    @Test
    public void testContainsExplicitFormatRdfJson()
    {
        this.assertAdminConfigurationFormat("json", Constants.APPLICATION_JSON);
    }
    
    /**
     * Test method for
     * {@link org.queryall.servlets.queryparsers.ConfigurationQueryOptions#containsExplicitFormat()}
     * .
     */
    @Test
    public void testContainsExplicitFormatRdfXml()
    {
        this.assertAdminConfigurationFormat("rdfxml", Constants.APPLICATION_RDF_XML);
    }
    
    /**
     * Test method for
     * {@link org.queryall.servlets.queryparsers.ConfigurationQueryOptions#getApiVersion()} .
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testGetApiVersion()
    {
        Assert.fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.queryall.servlets.queryparsers.ConfigurationQueryOptions#getExplicitFormat()} .
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testGetExplicitFormat()
    {
        Assert.fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.queryall.servlets.queryparsers.ConfigurationQueryOptions#getParsedRequest()} .
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testGetParsedRequest()
    {
        Assert.fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.queryall.servlets.queryparsers.ConfigurationQueryOptions#isPlainNamespaceAndIdentifier()}
     * .
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testIsPlainNamespaceAndIdentifier()
    {
        Assert.fail("Not yet implemented");
    }
    
    /**
     * Test method for
     * {@link org.queryall.servlets.queryparsers.ConfigurationQueryOptions#isRefresh()} .
     */
    @Ignore("TODO: Implement me")
    @Test
    public void testIsRefresh()
    {
        Assert.fail("Not yet implemented");
    }
    
}
