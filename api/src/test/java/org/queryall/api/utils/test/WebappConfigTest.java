/**
 * 
 */
package org.queryall.api.utils.test;

import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.queryall.api.utils.WebappConfig;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class WebappConfigTest
{
    /**
     * NOTE: This property needs to be updated if any new properties are added
     */
    private static final int EXPECTED_CONFIG_SIZE = 120;
    
    private ValueFactory testValueFactory;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        this.testValueFactory = new ValueFactoryImpl();
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        this.testValueFactory = null;
    }
    
    @Test
    public final void testAllEnumPropertiesNotNull()
    {
        Assert.assertEquals("EXPECTED_CONFIG_SIZE did not match the actual size. Did you add a property recently?",
                WebappConfigTest.EXPECTED_CONFIG_SIZE, WebappConfig.values().length);
        
        for(final WebappConfig nextEnum : WebappConfig.values())
        {
            Assert.assertNotNull(nextEnum.getDefaultValue());
            Assert.assertNotNull(nextEnum.getUri());
            Assert.assertNotNull(nextEnum.getKey());
            Assert.assertNotNull(nextEnum.getNamespace());
        }
    }
    
    /**
     * Test method for {@link org.queryall.api.utils.WebappConfig#getDefaultValue()}.
     */
    @Test
    public final void testGetDefaultValueBoolean()
    {
        Assert.assertFalse((Boolean)WebappConfig._TEST_BOOLEAN_PROPERTY.getDefaultValue());
    }
    
    /**
     * Test method for {@link org.queryall.api.utils.WebappConfig#getDefaultValue()}.
     */
    @Test
    public final void testGetDefaultValueFloat()
    {
        Assert.assertEquals(new Float(2.5), WebappConfig._TEST_FLOAT_PROPERTY.getDefaultValue());
    }
    
    /**
     * Test method for {@link org.queryall.api.utils.WebappConfig#getDefaultValue()}.
     */
    @Test
    public final void testGetDefaultValueInt()
    {
        Assert.assertEquals(new Integer(2), WebappConfig._TEST_INT_PROPERTY.getDefaultValue());
    }
    
    /**
     * Test method for {@link org.queryall.api.utils.WebappConfig#getDefaultValue()}.
     */
    @Test
    public final void testGetDefaultValueLong()
    {
        Assert.assertEquals(new Long(2), WebappConfig._TEST_LONG_PROPERTY.getDefaultValue());
    }
    
    /**
     * Test method for {@link org.queryall.api.utils.WebappConfig#getDefaultValue()}.
     */
    @Test
    public final void testGetDefaultValueString()
    {
        Assert.assertEquals("mySampleOnlyTestString", WebappConfig._TEST_STRING_PROPERTY.getDefaultValue());
    }
    
    /**
     * Test method for {@link org.queryall.api.utils.WebappConfig#getDefaultValue()}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testGetDefaultValueStringCollection()
    {
        final Object objectCollectionDefault = WebappConfig._TEST_STRING_COLLECTION_PROPERTY.getDefaultValue();
        
        Assert.assertTrue(objectCollectionDefault instanceof Collection<?>);
        
        final Collection<String> stringCollectionDefault = (Collection<String>)objectCollectionDefault;
        
        Assert.assertEquals(0, stringCollectionDefault.size());
    }
    
    /**
     * Test method for {@link org.queryall.api.utils.WebappConfig#getDefaultValue()}.
     */
    @Test
    public final void testGetDefaultValueUri()
    {
        Assert.assertEquals(this.testValueFactory.createURI("http://other.example.org/_testValueForUriProperty"),
                WebappConfig._TEST_URI_PROPERTY.getDefaultValue());
    }
    
    /**
     * Test method for {@link org.queryall.api.utils.WebappConfig#getDefaultValue()}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void testGetDefaultValueUriCollection()
    {
        final Object objectCollectionDefault = WebappConfig._TEST_URI_COLLECTION_PROPERTY.getDefaultValue();
        
        Assert.assertTrue(objectCollectionDefault instanceof Collection<?>);
        
        final Collection<URI> uriCollectionDefault = (Collection<URI>)objectCollectionDefault;
        
        Assert.assertEquals(0, uriCollectionDefault.size());
    }
    
    /**
     * Test method for {@link org.queryall.api.utils.WebappConfig#getKey()}.
     */
    @Test
    public final void testGetKey()
    {
        Assert.assertEquals("_testBooleanProperty", WebappConfig._TEST_BOOLEAN_PROPERTY.getKey());
    }
    
    /**
     * Test method for {@link org.queryall.api.utils.WebappConfig#getNamespace()}.
     */
    @Test
    public final void testGetNamespace()
    {
        Assert.assertEquals("http://test.example.org/", WebappConfig._TEST_FLOAT_PROPERTY.getNamespace());
    }
    
    /**
     * Test method for {@link org.queryall.api.utils.WebappConfig#getUri()}.
     */
    @Test
    public final void testGetUri()
    {
        final URI testUri1 = this.testValueFactory.createURI("http://test.example.org/", "_testIntProperty");
        
        final URI testUri2 =
                this.testValueFactory.createURI(WebappConfig._TEST_INT_PROPERTY.getNamespace(),
                        WebappConfig._TEST_INT_PROPERTY.getKey());
        
        Assert.assertEquals(testUri1, testUri2);
        Assert.assertEquals(testUri1.stringValue(), testUri2.stringValue());
        
        Assert.assertEquals(testUri1, WebappConfig._TEST_INT_PROPERTY.getUri());
        Assert.assertEquals(testUri2, WebappConfig._TEST_INT_PROPERTY.getUri());
    }
    
    /**
     * Test method for {@link org.queryall.api.utils.WebappConfig#overwrite()}.
     */
    @Test
    public final void testOverwrite()
    {
        Assert.assertFalse(WebappConfig._TEST_URI_COLLECTION_PROPERTY.overwrite());
        Assert.assertFalse(WebappConfig._TEST_STRING_COLLECTION_PROPERTY.overwrite());
        Assert.assertFalse(WebappConfig.TITLE_PROPERTIES.overwrite());
        Assert.assertFalse(WebappConfig.COMMENT_PROPERTIES.overwrite());
        Assert.assertFalse(WebappConfig.IMAGE_PROPERTIES.overwrite());
        Assert.assertFalse(WebappConfig.ACTIVE_PROFILES.overwrite());
        
        Assert.assertTrue(WebappConfig._TEST_BOOLEAN_PROPERTY.overwrite());
        Assert.assertTrue(WebappConfig._TEST_FLOAT_PROPERTY.overwrite());
        Assert.assertTrue(WebappConfig._TEST_INT_PROPERTY.overwrite());
        Assert.assertTrue(WebappConfig._TEST_LONG_PROPERTY.overwrite());
        Assert.assertTrue(WebappConfig._TEST_STRING_PROPERTY.overwrite());
        Assert.assertTrue(WebappConfig._TEST_URI_PROPERTY.overwrite());
        Assert.assertTrue(WebappConfig.USER_AGENT.overwrite());
        Assert.assertTrue(WebappConfig.DEFAULT_SEPARATOR.overwrite());
        
    }
    
    @Test
    public final void testSize()
    {
        Assert.assertEquals("EXPECTED_CONFIG_SIZE did not match the actual size. Did you add a property recently?",
                WebappConfigTest.EXPECTED_CONFIG_SIZE, WebappConfig.values().length);
    }
    
    /**
     * Test method for {@link org.queryall.api.utils.WebappConfig#valueOf(String)}.
     */
    @Test
    public final void testValueOfString()
    {
        Assert.assertEquals(WebappConfig.URI_SUFFIX, WebappConfig.valueOf("URI_SUFFIX"));
    }
    
    /**
     * Test method for {@link org.queryall.api.utils.WebappConfig#valueOf(org.openrdf.model.URI)}.
     */
    @Test
    public final void testValueOfURI()
    {
        Assert.assertEquals(WebappConfig.URI_PREFIX, WebappConfig.valueOf(this.testValueFactory
                .createURI("http://purl.org/queryall/webapp_configuration:uriPrefix")));
    }
    
}
