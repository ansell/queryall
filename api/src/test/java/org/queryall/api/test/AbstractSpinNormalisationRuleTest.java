/**
 * 
 */
package org.queryall.api.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.rdfrule.SpinNormalisationRule;

/**
 * Abstract unit test for SpinNormalisationRule API.
 * 
 * TODO: Create test for the API functions in SpinNormalisationRule
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractSpinNormalisationRuleTest extends AbstractNormalisationRuleTest
{
    private Repository testRepository;
    private RepositoryConnection testRepositoryConnection;
    private ValueFactory testValueFactory;
    
    /**
     * Final method, so that implementing test cases must specifically supply a
     * SpinNormalisationRule, through getNewTestSpinNormalisationRule.
     * 
     * @return A NormalisationRule that is also a SpinNormalisationRule
     */
    @Override
    public final NormalisationRule getNewTestRule()
    {
        return this.getNewTestSpinNormalisationRule();
    }
    
    /**
     * Create a new instance of the SpinNormalisationRule implementation being tested.
     * 
     * @return a new instance of the implemented SpinNormalisationRule
     */
    public abstract SpinNormalisationRule getNewTestSpinNormalisationRule();
    
    /**
     * @return the testRepository
     */
    protected Repository getTestRepository()
    {
        return this.testRepository;
    }
    
    /**
     * @return the testRepositoryConnection
     */
    protected RepositoryConnection getTestRepositoryConnection()
    {
        return this.testRepositoryConnection;
    }
    
    /**
     * @return the testValueFactory
     */
    protected ValueFactory getTestValueFactory()
    {
        return this.testValueFactory;
    }
    
    /**
     * @param nextTestRepository
     *            the testRepository to set
     */
    private void setTestRepository(final Repository nextTestRepository)
    {
        this.testRepository = nextTestRepository;
    }
    
    /**
     * @param nextTestRepositoryConnection
     *            the testRepositoryConnection to set
     */
    private void setTestRepositoryConnection(final RepositoryConnection nextTestRepositoryConnection)
    {
        this.testRepositoryConnection = nextTestRepositoryConnection;
    }
    
    /**
     * @param nextTestValueFactory
     *            the testValueFactory to set
     */
    private void setTestValueFactory(final ValueFactory nextTestValueFactory)
    {
        this.testValueFactory = nextTestValueFactory;
    }
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        
        this.setTestRepository(new SailRepository(new MemoryStore()));
        this.getTestRepository().initialize();
        this.setTestRepositoryConnection(this.getTestRepository().getConnection());
        this.setTestValueFactory(new ValueFactoryImpl());
        
    }
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    @After
    public void tearDown() throws Exception
    {
        super.tearDown();
        
        if(this.getTestRepositoryConnection() != null)
        {
            try
            {
                this.getTestRepositoryConnection().close();
            }
            catch(final RepositoryException ex)
            {
                ex.printStackTrace();
            }
            finally
            {
                this.setTestRepositoryConnection(null);
            }
        }
        
        this.setTestRepository(null);
        this.setTestValueFactory(null);
    }
    
    @Test
    public void testResetEntailmentUris()
    {
        final SpinNormalisationRule testSpinNormalisationRule = this.getNewTestSpinNormalisationRule();
        
        Assert.assertEquals(0, testSpinNormalisationRule.getEntailmentUris().size());
        
        testSpinNormalisationRule.addEntailmentUri(this.testValueFactory
                .createURI("http://example.org/test/entailment/uri/1"));
        
        Assert.assertEquals(1, testSpinNormalisationRule.getEntailmentUris().size());
        
        Assert.assertTrue(testSpinNormalisationRule.resetEntailmentUris());
        
        Assert.assertEquals(0, testSpinNormalisationRule.getEntailmentUris().size());
    }
    
    @Test
    public void testResetLocalImports()
    {
        final SpinNormalisationRule testSpinNormalisationRule = this.getNewTestSpinNormalisationRule();
        
        Assert.assertEquals(0, testSpinNormalisationRule.getLocalImports().size());
        
        testSpinNormalisationRule.addLocalImport("test/owlrl-all");
        
        Assert.assertEquals(1, testSpinNormalisationRule.getLocalImports().size());
        
        Assert.assertTrue(testSpinNormalisationRule.resetLocalImports());
        
        Assert.assertEquals(0, testSpinNormalisationRule.getLocalImports().size());
    }
    
    @Test
    public void testResetUrlImports()
    {
        final SpinNormalisationRule testSpinNormalisationRule = this.getNewTestSpinNormalisationRule();
        
        Assert.assertEquals(0, testSpinNormalisationRule.getURLImports().size());
        
        testSpinNormalisationRule.addUrlImport(this.testValueFactory.createURI("http://topbraid.org/spin/owlrl-all"));
        
        Assert.assertEquals(1, testSpinNormalisationRule.getURLImports().size());
        
        Assert.assertTrue(testSpinNormalisationRule.resetUrlImports());
        
        Assert.assertEquals(0, testSpinNormalisationRule.getURLImports().size());
    }
    
}
