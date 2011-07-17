package org.queryall.api.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.sail.memory.model.MemValueFactory;
import org.queryall.api.SparqlProvider;

/**
 * Abstract unit test for SparqlProvider API
 */
public abstract class AbstractSparqlProviderTest
{
    private SparqlProvider providerDoesUseSparqlGraphWithGraphUri;
    private SparqlProvider providerDoesUseSparqlGraphWithoutGraphUri;
    private SparqlProvider providerDoesNotUseSparqlGraphWithUri;
    private SparqlProvider providerDoesNotUseSparqlGraphWithoutUri;

    private URI testTrueGraphUri;
	private URI testFalseGraphUri;
    
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

        testTrueGraphUri = f.createURI("http://example.org/test/trueGraphUri");
        testFalseGraphUri = f.createURI("http://example.org/test/falseGraphUri");

        providerDoesUseSparqlGraphWithGraphUri = getNewTestSparqlProvider();
        providerDoesUseSparqlGraphWithGraphUri.setSparqlGraphUri(testTrueGraphUri.stringValue());
        providerDoesUseSparqlGraphWithGraphUri.setUseSparqlGraph(true);
        
        providerDoesUseSparqlGraphWithoutGraphUri = getNewTestSparqlProvider();
        providerDoesUseSparqlGraphWithoutGraphUri.setSparqlGraphUri("");
        providerDoesUseSparqlGraphWithoutGraphUri.setUseSparqlGraph(true);
        
        providerDoesNotUseSparqlGraphWithUri = getNewTestSparqlProvider();
        providerDoesNotUseSparqlGraphWithUri.setUseSparqlGraph(false);
        providerDoesNotUseSparqlGraphWithUri.setSparqlGraphUri(testFalseGraphUri.stringValue());
        
        providerDoesNotUseSparqlGraphWithoutUri = getNewTestSparqlProvider();
        providerDoesNotUseSparqlGraphWithoutUri.setUseSparqlGraph(false);
        providerDoesNotUseSparqlGraphWithoutUri.setSparqlGraphUri("");
    }
    
    public abstract SparqlProvider getNewTestSparqlProvider();

	/**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
    	testTrueGraphUri = null;
    	testFalseGraphUri = null;

        providerDoesUseSparqlGraphWithGraphUri = null;
        providerDoesUseSparqlGraphWithoutGraphUri = null;
        providerDoesNotUseSparqlGraphWithUri = null;
        providerDoesNotUseSparqlGraphWithoutUri = null;
    }

    @Test
    public void testSparqlProviderUseSparqlGraph()
    {
    	assertTrue(providerDoesUseSparqlGraphWithGraphUri.getUseSparqlGraph() == true);
    	assertTrue(providerDoesUseSparqlGraphWithoutGraphUri.getUseSparqlGraph() == true);
    	assertTrue(providerDoesNotUseSparqlGraphWithUri.getUseSparqlGraph() == false);
    	assertTrue(providerDoesNotUseSparqlGraphWithoutUri.getUseSparqlGraph() == false);
    }
    
    @Test
    public void testSparqlProviderSparqlGraphUri()
    {
    	assertTrue(providerDoesUseSparqlGraphWithGraphUri.getSparqlGraphUri() != null && providerDoesUseSparqlGraphWithGraphUri.getSparqlGraphUri().trim().length() > 0);
    	assertTrue(providerDoesUseSparqlGraphWithoutGraphUri.getSparqlGraphUri() != null && providerDoesUseSparqlGraphWithoutGraphUri.getSparqlGraphUri().trim().length() == 0);
    	assertTrue(providerDoesNotUseSparqlGraphWithUri.getSparqlGraphUri() != null && providerDoesNotUseSparqlGraphWithUri.getSparqlGraphUri().trim().length() == 0);
    	assertTrue(providerDoesNotUseSparqlGraphWithoutUri.getSparqlGraphUri() != null && providerDoesNotUseSparqlGraphWithoutUri.getSparqlGraphUri().trim().length() == 0);
    }
}
