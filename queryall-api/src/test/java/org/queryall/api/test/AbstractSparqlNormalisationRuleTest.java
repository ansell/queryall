/**
 * 
 */
package org.queryall.api.test;

import org.junit.After;
import org.junit.Before;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.rdfrule.SparqlNormalisationRule;

/**
 * Abstract unit test for SparqlNormalisationRule API.
 * 
 * TODO: Create test for the API functions in SparqlNormalisationRule
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractSparqlNormalisationRuleTest extends AbstractNormalisationRuleTest
{
    private Repository testRepository;
    private RepositoryConnection testRepositoryConnection;
    private ValueFactory testValueFactory;
    
    /**
     * Final method, so that implementing test cases must specifically supply a
     * SparqlNormalisationRule, through getNewTestSparqlNormalisationRule.
     * 
     * @return A NormalisationRule that is also a SparqlNormalisationRule
     */
    @Override
    public final NormalisationRule getNewTestRule()
    {
        return this.getNewTestSparqlNormalisationRule();
    }
    
    /**
     * Create a new instance of the SparqlNormalisationRule implementation being tested.
     * 
     * @return a new instance of the implemented SparqlNormalisationRule
     */
    public abstract SparqlNormalisationRule getNewTestSparqlNormalisationRule();
    
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
     * @param testRepository
     *            the testRepository to set
     */
    private void setTestRepository(final Repository testRepository)
    {
        this.testRepository = testRepository;
    }
    
    /**
     * @param testRepositoryConnection
     *            the testRepositoryConnection to set
     */
    private void setTestRepositoryConnection(final RepositoryConnection testRepositoryConnection)
    {
        this.testRepositoryConnection = testRepositoryConnection;
    }
    
    /**
     * @param testValueFactory
     *            the testValueFactory to set
     */
    private void setTestValueFactory(final ValueFactory testValueFactory)
    {
        this.testValueFactory = testValueFactory;
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
    
}
