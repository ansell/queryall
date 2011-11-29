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
import org.queryall.api.rdfrule.RegexNormalisationRule;

/**
 * Abstract unit test for RegexNormalisationRule API.
 * 
 * TODO: Create test for the API functions in RegexNormalisationRule
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractRegexNormalisationRuleTest extends AbstractNormalisationRuleTest
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
     * Create a new instance of the RegexNormalisationRule implementation being tested.
     * 
     * @return a new instance of the implemented RegexNormalisationRule
     */
    public abstract RegexNormalisationRule getNewTestRegexNormalisationRule();
    
    /**
     * Final method, so that implementing test cases must specifically supply a
     * RegexNormalisationRule, through getNewTestRegexNormalisationRule.
     * 
     * @return A NormalisationRule that is also a RegexNormalisationRule
     */
    @Override
    public final NormalisationRule getNewTestRule()
    {
        return this.getNewTestRegexNormalisationRule();
    }
    
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
