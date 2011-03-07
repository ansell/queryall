package org.queryall;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.sail.memory.model.MemValueFactory;

/**
 * Abstract unit test for Provider API
 * 
 * Implementations must implement the abstract setUp method
 */

@SuppressWarnings("unused")
public abstract class AbstractProviderTest 
    extends TestCase
{
    private Provider providerNonDefault;
    private Provider providerSpecificDefault;
    private Provider providerNoNamespacesDefault;
    
    protected URI testTrueQueryTypeUri;
    protected URI testFalseQueryTypeUri;
    protected URI testTrueRuleUri;
    protected URI testFalseRuleUri;
    protected URI testTrueNamespaceUri;
    protected URI testFalseNamespaceUri;
    
    /**
     * This method performs the following actions:
     * - Creates new Providers for the Provider type fields using multiple calls to getNewTestProvider
     * - Create org.openrdf.model.URI instances for the test URIs
     * - Add testTrue*'s using the relevant methods from the API
     */
    @Before
    public void setUp() throws Exception
    {
        ValueFactory f = new MemValueFactory();

        testTrueQueryTypeUri = f.createURI("http://example.org/test/includedQueryType");
        testFalseQueryTypeUri = f.createURI("http://example.org/test/excludedQueryType");
        testTrueRuleUri = f.createURI("http://example.org/test/includedRule");
        testFalseRuleUri = f.createURI("http://example.org/test/excludedRule");
        testTrueNamespaceUri = f.createURI("http://example.org/test/includedNamespace");
        testFalseNamespaceUri = f.createURI("http://example.org/test/excludedNamespace");

        providerNonDefault = getNewTestProvider();
        providerNonDefault.setIsDefaultSource(false);
        providerNonDefault.addIncludedInQueryType(testTrueQueryTypeUri);
        providerNonDefault.addNormalisationUri(testTrueRuleUri);
        providerNonDefault.addNamespace(testTrueNamespaceUri);
        
        providerSpecificDefault = getNewTestProvider();
        providerSpecificDefault.setIsDefaultSource(true);
        providerSpecificDefault.addIncludedInQueryType(testTrueQueryTypeUri);
        providerSpecificDefault.addNormalisationUri(testTrueRuleUri);
        providerSpecificDefault.addNamespace(testTrueNamespaceUri);
        
        providerNoNamespacesDefault = getNewTestProvider();
        providerNoNamespacesDefault.setIsDefaultSource(true);
        providerNoNamespacesDefault.addIncludedInQueryType(testTrueQueryTypeUri);
        providerNoNamespacesDefault.addNormalisationUri(testTrueRuleUri);
        
    }
    
    /**
     * This method must be overridden to return a new instance of 
     * the implemented Provider class for each successive invocation
     */
    public abstract Provider getNewTestProvider();
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        providerSpecificDefault = null;
        providerNonDefault = null;
        providerNoNamespacesDefault = null;
        testTrueQueryTypeUri = null;
        testFalseQueryTypeUri = null;
        testTrueRuleUri = null;
        testFalseRuleUri = null;
        testTrueNamespaceUri = null;
        testFalseNamespaceUri = null;
    }

    /**
     * Test method for {@link org.queryall.Provider#containsQueryTypeUri(org.openrdf.model.URI)}.
     */
    @Test
    public void testHandlesQueryTypes()
    {
        assertTrue(providerSpecificDefault.containsQueryTypeUri(testTrueQueryTypeUri));
        assertFalse(providerSpecificDefault.containsQueryTypeUri(testFalseQueryTypeUri));
        assertTrue(providerNonDefault.containsQueryTypeUri(testTrueQueryTypeUri));
        assertFalse(providerNonDefault.containsQueryTypeUri(testFalseQueryTypeUri));
        assertTrue(providerNoNamespacesDefault.containsQueryTypeUri(testTrueQueryTypeUri));
        assertFalse(providerNoNamespacesDefault.containsQueryTypeUri(testFalseQueryTypeUri));
    }

    /**
     * Test method for {@link org.queryall.Provider#containsNamespaceUri(org.openrdf.model.URI)}.
     */
    @Test
    public void testContainsNamespaceUri()
    {
        assertTrue(providerSpecificDefault.containsNamespaceUri(testTrueNamespaceUri));
        assertFalse(providerSpecificDefault.containsNamespaceUri(testFalseNamespaceUri));
        assertTrue(providerNonDefault.containsNamespaceUri(testTrueNamespaceUri));
        assertFalse(providerNonDefault.containsNamespaceUri(testFalseNamespaceUri));
        assertFalse(providerNoNamespacesDefault.containsNamespaceUri(testTrueNamespaceUri));
        assertFalse(providerNoNamespacesDefault.containsNamespaceUri(testFalseNamespaceUri));
    }
    
    /**
     * Test method for {@link org.queryall.Provider#containsNamespaceOrDefault(org.openrdf.model.URI)}.
     */
    @Test
    public void testContainsNamespaceOrDefault()
    {
        assertTrue(providerSpecificDefault.containsNamespaceOrDefault(testTrueNamespaceUri));
        assertTrue(providerSpecificDefault.containsNamespaceOrDefault(testFalseNamespaceUri));
        assertTrue(providerNonDefault.containsNamespaceOrDefault(testTrueNamespaceUri));
        assertFalse(providerNonDefault.containsNamespaceOrDefault(testFalseNamespaceUri));
        assertTrue(providerNoNamespacesDefault.containsNamespaceOrDefault(testTrueNamespaceUri));
        assertTrue(providerNoNamespacesDefault.containsNamespaceOrDefault(testFalseNamespaceUri));

    }    
}
