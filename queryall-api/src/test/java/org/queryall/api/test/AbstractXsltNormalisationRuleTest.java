/**
 * 
 */
package org.queryall.api.test;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.openrdf.model.URI;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.rdfrule.XsltNormalisationRule;

/**
 * Abstract unit test for XsltNormalisationRule API.
 * 
 * TODO: Create test for the API functions in XsltNormalisationRule
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractXsltNormalisationRuleTest extends AbstractNormalisationRuleTest
{
    @Override
    public final Set<URI> getExpectedValidStages()
    {
        final Set<URI> results = new HashSet<URI>();
        
        results.add(NormalisationRuleSchema.getRdfruleStageQueryVariables());
        results.add(NormalisationRuleSchema.getRdfruleStageAfterQueryCreation());
        results.add(NormalisationRuleSchema.getRdfruleStageBeforeResultsImport());
        results.add(NormalisationRuleSchema.getRdfruleStageAfterResultsToDocument());
        
        return results;
    }
    
    /**
     * Final method, so that implementing test cases must specifically supply a
     * XsltNormalisationRule, through getNewTestXsltNormalisationRule.
     * 
     * @return A NormalisationRule that is also a XsltNormalisationRule
     */
    @Override
    public final NormalisationRule getNewTestRule()
    {
        return this.getNewTestXsltNormalisationRule();
    }
    
    /**
     * Create a new instance of the XsltNormalisationRule implementation being tested.
     * 
     * @return a new instance of the implemented XsltNormalisationRule
     */
    public abstract XsltNormalisationRule getNewTestXsltNormalisationRule();
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public final void setUp() throws Exception
    {
        super.setUp();
    }
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    @After
    public final void tearDown() throws Exception
    {
        super.tearDown();
    }
    
}
