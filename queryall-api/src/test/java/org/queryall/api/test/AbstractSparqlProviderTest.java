package org.queryall.api.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.sail.memory.model.MemValueFactory;
import org.queryall.api.provider.Provider;
import org.queryall.api.provider.SparqlProvider;

/**
 * Abstract unit test for SparqlProvider API
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractSparqlProviderTest extends AbstractProviderTest
{
    private SparqlProvider providerDoesUseSparqlGraphWithGraphUri;
    private SparqlProvider providerDoesUseSparqlGraphWithoutGraphUri;
    private SparqlProvider providerDoesNotUseSparqlGraphWithUri;
    private SparqlProvider providerDoesNotUseSparqlGraphWithoutUri;
    
    private URI testTrueGraphUri;
    private URI testFalseGraphUri;
    
    @Override
    public final Provider getNewTestProvider()
    {
        return this.getNewTestSparqlProvider();
    }
    
    /**
     * Subclasses need to override this to return a new instance of an implementation of the
     * SparqlProvider interface for each call.
     * 
     * @return A new instance of an implementation of the SparqlProvider interface
     */
    public abstract SparqlProvider getNewTestSparqlProvider();
    
    /**
     * This method performs the following actions: - Creates new Providers for the Provider type
     * fields using multiple calls to getNewTestProvider - Create org.openrdf.model.URI instances
     * for the test URIs - Add testTrue*'s using the relevant methods from the API
     */
    @Override
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        
        final ValueFactory f = new MemValueFactory();
        
        this.testTrueGraphUri = f.createURI("http://example.org/test/trueGraphUri");
        this.testFalseGraphUri = f.createURI("http://example.org/test/falseGraphUri");
        
        this.providerDoesUseSparqlGraphWithGraphUri = this.getNewTestSparqlProvider();
        this.providerDoesUseSparqlGraphWithGraphUri.setSparqlGraphUri(this.testTrueGraphUri.stringValue());
        this.providerDoesUseSparqlGraphWithGraphUri.setUseSparqlGraph(true);
        
        this.providerDoesUseSparqlGraphWithoutGraphUri = this.getNewTestSparqlProvider();
        this.providerDoesUseSparqlGraphWithoutGraphUri.setSparqlGraphUri("");
        this.providerDoesUseSparqlGraphWithoutGraphUri.setUseSparqlGraph(true);
        
        this.providerDoesNotUseSparqlGraphWithUri = this.getNewTestSparqlProvider();
        this.providerDoesNotUseSparqlGraphWithUri.setUseSparqlGraph(false);
        this.providerDoesNotUseSparqlGraphWithUri.setSparqlGraphUri(this.testFalseGraphUri.stringValue());
        
        this.providerDoesNotUseSparqlGraphWithoutUri = this.getNewTestSparqlProvider();
        this.providerDoesNotUseSparqlGraphWithoutUri.setUseSparqlGraph(false);
        this.providerDoesNotUseSparqlGraphWithoutUri.setSparqlGraphUri("");
    }
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    @After
    public void tearDown() throws Exception
    {
        super.tearDown();
        
        this.testTrueGraphUri = null;
        this.testFalseGraphUri = null;
        
        this.providerDoesUseSparqlGraphWithGraphUri = null;
        this.providerDoesUseSparqlGraphWithoutGraphUri = null;
        this.providerDoesNotUseSparqlGraphWithUri = null;
        this.providerDoesNotUseSparqlGraphWithoutUri = null;
    }
    
    @Test
    public void testSparqlProviderSparqlGraphUri()
    {
        Assert.assertTrue(this.providerDoesUseSparqlGraphWithGraphUri.getSparqlGraphUri() != null
                && this.providerDoesUseSparqlGraphWithGraphUri.getSparqlGraphUri().trim().length() > 0);
        Assert.assertTrue(this.providerDoesUseSparqlGraphWithoutGraphUri.getSparqlGraphUri() != null
                && this.providerDoesUseSparqlGraphWithoutGraphUri.getSparqlGraphUri().trim().length() == 0);
        Assert.assertTrue(this.providerDoesNotUseSparqlGraphWithUri.getSparqlGraphUri() != null
                && this.providerDoesNotUseSparqlGraphWithUri.getSparqlGraphUri().trim().length() == 0);
        Assert.assertTrue(this.providerDoesNotUseSparqlGraphWithoutUri.getSparqlGraphUri() != null
                && this.providerDoesNotUseSparqlGraphWithoutUri.getSparqlGraphUri().trim().length() == 0);
    }
    
    @Test
    public void testSparqlProviderUseSparqlGraph()
    {
        Assert.assertTrue(this.providerDoesUseSparqlGraphWithGraphUri.getUseSparqlGraph() == true);
        Assert.assertTrue(this.providerDoesUseSparqlGraphWithoutGraphUri.getUseSparqlGraph() == true);
        Assert.assertTrue(this.providerDoesNotUseSparqlGraphWithUri.getUseSparqlGraph() == false);
        Assert.assertTrue(this.providerDoesNotUseSparqlGraphWithoutUri.getUseSparqlGraph() == false);
    }
}
