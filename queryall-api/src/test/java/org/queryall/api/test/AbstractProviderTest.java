package org.queryall.api.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.sail.memory.model.MemValueFactory;
import org.queryall.api.provider.Provider;

/**
 * Abstract unit test for Provider API.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractProviderTest extends AbstractProfilableProviderTest
{
    protected URI testTrueQueryTypeUri = null;
    protected URI testFalseQueryTypeUri = null;
    protected URI testTrueRuleUri = null;
    protected URI testFalseRuleUri = null;
    protected URI testTrueNamespaceUri = null;
    protected URI testFalseNamespaceUri = null;
    protected URI testTrueProviderUri = null;
    protected URI testFalseProviderUri = null;
    
    private Provider providerNonDefault = null;
    private Provider providerSpecificDefault = null;
    private Provider providerNoNamespacesDefault = null;
    
    @Override
    public final Provider getNewTestProfilable()
    {
        return this.getNewTestProvider();
    }
    
    /**
     * This method must be overridden to return a new instance of the implemented Provider class for
     * each successive invocation.
     */
    public abstract Provider getNewTestProvider();
    
    /**
     * This method performs the following actions: - Creates new Providers for the Provider type
     * fields using multiple calls to getNewTestProvider - Create org.openrdf.model.URI instances
     * for the test URIs - Add testTrue*'s using the relevant methods from the API.
     */
    @Override
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        
        final ValueFactory f = new MemValueFactory();
        
        this.testTrueQueryTypeUri = f.createURI("http://example.org/test/includedQueryType");
        this.testFalseQueryTypeUri = f.createURI("http://example.org/test/excludedQueryType");
        this.testTrueRuleUri = f.createURI("http://example.org/test/includedRule");
        this.testFalseRuleUri = f.createURI("http://example.org/test/excludedRule");
        this.testTrueNamespaceUri = f.createURI("http://example.org/test/includedNamespace");
        this.testFalseNamespaceUri = f.createURI("http://example.org/test/excludedNamespace");
        this.testTrueProviderUri = f.createURI("http://example.org/test/includedProvider");
        this.testFalseProviderUri = f.createURI("http://example.org/test/excludedProvider");
        
        this.providerNonDefault = this.getNewTestProvider();
        this.providerNonDefault.setIsDefaultSource(false);
        this.providerNonDefault.addIncludedInQueryType(this.testTrueQueryTypeUri);
        this.providerNonDefault.addNormalisationUri(this.testTrueRuleUri);
        this.providerNonDefault.addNamespace(this.testTrueNamespaceUri);
        
        this.providerSpecificDefault = this.getNewTestProvider();
        this.providerSpecificDefault.setIsDefaultSource(true);
        this.providerSpecificDefault.addIncludedInQueryType(this.testTrueQueryTypeUri);
        this.providerSpecificDefault.addNormalisationUri(this.testTrueRuleUri);
        this.providerSpecificDefault.addNamespace(this.testTrueNamespaceUri);
        
        this.providerNoNamespacesDefault = this.getNewTestProvider();
        this.providerNoNamespacesDefault.setIsDefaultSource(true);
        this.providerNoNamespacesDefault.addIncludedInQueryType(this.testTrueQueryTypeUri);
        this.providerNoNamespacesDefault.addNormalisationUri(this.testTrueRuleUri);
    }
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    @After
    public void tearDown() throws Exception
    {
        super.tearDown();
        
        this.testTrueQueryTypeUri = null;
        this.testFalseQueryTypeUri = null;
        this.testTrueRuleUri = null;
        this.testFalseRuleUri = null;
        this.testTrueNamespaceUri = null;
        this.testFalseNamespaceUri = null;
        this.testTrueProviderUri = null;
        this.testFalseProviderUri = null;
        
        this.providerNonDefault = null;
        this.providerSpecificDefault = null;
        this.providerNoNamespacesDefault = null;
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.provider.Provider#containsNamespaceOrDefault(org.openrdf.model.URI)}
     * .
     */
    @Test
    public void testContainsNamespaceOrDefault()
    {
        Assert.assertNotNull("Did you call super.setUp()?", this.providerSpecificDefault);
        
        Assert.assertTrue(this.providerSpecificDefault.containsNamespaceOrDefault(this.testTrueNamespaceUri));
        Assert.assertTrue(this.providerSpecificDefault.containsNamespaceOrDefault(this.testFalseNamespaceUri));
        Assert.assertTrue(this.providerNonDefault.containsNamespaceOrDefault(this.testTrueNamespaceUri));
        Assert.assertFalse(this.providerNonDefault.containsNamespaceOrDefault(this.testFalseNamespaceUri));
        Assert.assertTrue(this.providerNoNamespacesDefault.containsNamespaceOrDefault(this.testTrueNamespaceUri));
        Assert.assertTrue(this.providerNoNamespacesDefault.containsNamespaceOrDefault(this.testFalseNamespaceUri));
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.provider.Provider#containsNamespaceUri(org.openrdf.model.URI)} .
     */
    @Test
    public void testContainsNamespaceUri()
    {
        Assert.assertNotNull("Did you call super.setUp()?", this.providerSpecificDefault);
        
        Assert.assertTrue(this.providerSpecificDefault.containsNamespaceUri(this.testTrueNamespaceUri));
        Assert.assertFalse(this.providerSpecificDefault.containsNamespaceUri(this.testFalseNamespaceUri));
        Assert.assertTrue(this.providerNonDefault.containsNamespaceUri(this.testTrueNamespaceUri));
        Assert.assertFalse(this.providerNonDefault.containsNamespaceUri(this.testFalseNamespaceUri));
        Assert.assertFalse(this.providerNoNamespacesDefault.containsNamespaceUri(this.testTrueNamespaceUri));
        Assert.assertFalse(this.providerNoNamespacesDefault.containsNamespaceUri(this.testFalseNamespaceUri));
    }
    
    /**
     * Test method for
     * {@link org.queryall.api.provider.Provider#containsQueryTypeUri(org.openrdf.model.URI)} .
     */
    @Test
    public void testHandlesQueryTypes()
    {
        Assert.assertNotNull("Did you call super.setUp()?", this.providerSpecificDefault);
        
        Assert.assertTrue(this.providerSpecificDefault.containsQueryTypeUri(this.testTrueQueryTypeUri));
        Assert.assertFalse(this.providerSpecificDefault.containsQueryTypeUri(this.testFalseQueryTypeUri));
        Assert.assertTrue(this.providerNonDefault.containsQueryTypeUri(this.testTrueQueryTypeUri));
        Assert.assertFalse(this.providerNonDefault.containsQueryTypeUri(this.testFalseQueryTypeUri));
        Assert.assertTrue(this.providerNoNamespacesDefault.containsQueryTypeUri(this.testTrueQueryTypeUri));
        Assert.assertFalse(this.providerNoNamespacesDefault.containsQueryTypeUri(this.testFalseQueryTypeUri));
    }
    
    @Test
    public void testResetIncludedInQueryTypes()
    {
        final Provider testProvider = this.getNewTestProvider();
        
        Assert.assertEquals(0, testProvider.getIncludedInQueryTypes().size());
        
        testProvider.addIncludedInQueryType(this.testTrueQueryTypeUri);
        
        Assert.assertEquals(1, testProvider.getIncludedInQueryTypes().size());
        
        Assert.assertTrue(testProvider.resetIncludedInQueryTypes());
        
        Assert.assertEquals(0, testProvider.getIncludedInQueryTypes().size());
    }
    
    @Test
    public void testResetNamespaces()
    {
        final Provider testProvider = this.getNewTestProvider();
        
        Assert.assertEquals(0, testProvider.getNamespaces().size());
        
        testProvider.addNamespace(this.testTrueNamespaceUri);
        
        Assert.assertEquals(1, testProvider.getNamespaces().size());
        
        Assert.assertTrue(testProvider.resetNamespaces());
        
        Assert.assertEquals(0, testProvider.getNamespaces().size());
    }
    
    @Test
    public void testResetNormalisationUris()
    {
        final Provider testProvider = this.getNewTestProvider();
        
        Assert.assertEquals(0, testProvider.getNormalisationUris().size());
        
        testProvider.addNormalisationUri(this.testTrueRuleUri);
        
        Assert.assertEquals(1, testProvider.getNormalisationUris().size());
        
        Assert.assertTrue(testProvider.resetNormalisationUris());
        
        Assert.assertEquals(0, testProvider.getNormalisationUris().size());
    }
}
