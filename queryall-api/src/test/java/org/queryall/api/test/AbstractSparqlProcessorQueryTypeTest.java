/**
 * 
 */
package org.queryall.api.test;

import org.junit.After;
import org.junit.Before;
import org.queryall.api.querytype.ProcessorQueryType;
import org.queryall.api.querytype.SparqlProcessorQueryType;

/**
 * Abstract unit test for SparqlProcessorQueryType API
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractSparqlProcessorQueryTypeTest extends AbstractProcessorQueryTypeTest
{
    @Override
    public final String getNewTestProcessingTemplateString()
    {
        return "CONSTRUCT { ?test <http://example.org/test/predicate/1> ?somethingElse . } WHERE { ${normalisedStandardUri} a <http://example.org/test/class/a> } ";
    }
    
    @Override
    public final ProcessorQueryType getNewTestProcessorQueryType()
    {
        return this.getNewTestSparqlProcessorQueryType();
    }
    
    /**
     * This method must be overridden to return a new instance of the implemented QueryType class
     * for each successive invocation
     * 
     * @return A new instance of the SparqlProcessorQueryType implementation
     */
    public abstract SparqlProcessorQueryType getNewTestSparqlProcessorQueryType();
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        
    }
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    @After
    public void tearDown() throws Exception
    {
        super.tearDown();
        
    }
    
}
