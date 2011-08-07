/**
 * 
 */
package org.queryall.api.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.sail.memory.model.MemValueFactory;

import org.queryall.api.NormalisationRule;
import org.queryall.api.Profile;
import org.queryall.exception.InvalidStageException;

/**
 * Abstract unit test for NormalisationRule API
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractNormalisationRuleTest
{
    private URI testTrueSparqlNormalisationRuleUri;
	private URI testFalseSparqlNormalisationRuleUri;
	private URI testStageInvalidInclusionSparqlNormalisationRuleUri;
	private URI testStageAllValidAndInvalidSparqlNormalisationRuleUri;
	protected List<URI> validStages;
	protected List<URI> invalidStages;

	/**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        ValueFactory f = new MemValueFactory();

        testTrueSparqlNormalisationRuleUri = f.createURI("http://example.org/test/includedNormalisationRule");
        testFalseSparqlNormalisationRuleUri = f.createURI("http://example.org/test/excludedNormalisationRule");
        testStageInvalidInclusionSparqlNormalisationRuleUri = f.createURI("http://example.org/test/stageInclusionSparqlNormalisationRule");
        testStageAllValidAndInvalidSparqlNormalisationRuleUri = f.createURI("http://example.org/test/stageExclusionSparqlNormalisationRule");
        
		invalidStages = new ArrayList<URI>(5);
		
		invalidStages.add(getRdfruleStageQueryVariablesURI());
		invalidStages.add(getRdfruleStageAfterQueryCreationURI());
		invalidStages.add(getRdfruleStageAfterQueryParsingURI());
		invalidStages.add(getRdfruleStageBeforeResultsImportURI());
		invalidStages.add(getRdfruleStageAfterResultsToDocumentURI());

		validStages = new ArrayList<URI>(2);

		validStages.add(getRdfruleStageAfterResultsImportURI());
		validStages.add(getRdfruleStageAfterResultsToPoolURI());
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
    	testTrueSparqlNormalisationRuleUri = null;
    	testFalseSparqlNormalisationRuleUri = null;
        testStageInvalidInclusionSparqlNormalisationRuleUri = null;
        testStageAllValidAndInvalidSparqlNormalisationRuleUri = null;
        
		invalidStages = null;
		validStages = null;
    }
    
    /**
     * Create a new instance of the SparqlNormalisationRule implementation being tested
     * @return a new instance of the implemented SparqlNormalisationRule
     */
    public abstract NormalisationRule getNewTestRule();

    /**
     * Create a new profile instance with default properties
     * @return A new profile instance with default properties
     */
	public abstract Profile getNewTestProfile();

	public abstract URI getProfileIncludeExcludeOrderUndefinedUri();

	public abstract URI getProfileIncludeThenExcludeURI();

	public abstract URI getProfileExcludeThenIncludeURI();
	
	public abstract URI getRdfruleStageAfterResultsImportURI();
	
	public abstract URI getRdfruleStageQueryVariablesURI();

	public abstract URI getRdfruleStageAfterQueryCreationURI();

	public abstract URI getRdfruleStageAfterQueryParsingURI();

	public abstract URI getRdfruleStageBeforeResultsImportURI();

	public abstract URI getRdfruleStageAfterResultsToPoolURI();

	public abstract URI getRdfruleStageAfterResultsToDocumentURI();

    @Test
	public void testInvalidStageInclusion()
	{
		NormalisationRule queryallRule = getNewTestRule();
		
		assertTrue(queryallRule instanceof NormalisationRule);
		
		NormalisationRule normalisationRule = queryallRule;
		
		normalisationRule.setKey(testStageInvalidInclusionSparqlNormalisationRuleUri);

		Collection<URI> includedStages = new ArrayList<URI>(0);
		
		Collection<URI> excludedStages = invalidStages;
		
		for(URI nextInvalidStage : invalidStages)
		{
			boolean foundException = false;

			try 
			{
				normalisationRule.addStage(nextInvalidStage);
			} 
			catch (InvalidStageException e) 
			{
				foundException = true;
			}

			assertTrue("Did not find expected invalid stage exception for addStage", foundException);
		}
		
		assertEquals(normalisationRule.getStages().size(), 0);
		
		boolean foundSetException = false;

		try 
		{
			normalisationRule.setStages(invalidStages);
		} 
		catch (InvalidStageException e) 
		{
			foundSetException = true;
		}

		assertTrue("Did not find expected invalid stage exception for setStages", foundSetException);

		assertEquals(normalisationRule.getStages().size(), 0);
		
		NormalisationRuleTestUtil.testIsValidInStages(normalisationRule, validStages, true);
		NormalisationRuleTestUtil.testIsValidInStages(normalisationRule, invalidStages, false);

		NormalisationRuleTestUtil.testIsUsedInStages(normalisationRule, includedStages, true);
		NormalisationRuleTestUtil.testIsUsedInStages(normalisationRule, excludedStages, false);
	}

    @Test
	public void testAllValidAndInvalidStages()
	{
		NormalisationRule queryallRule = getNewTestRule();
		
		assertTrue(queryallRule instanceof NormalisationRule);

		NormalisationRule normalisationRule = queryallRule;
		
		normalisationRule.setKey(testStageAllValidAndInvalidSparqlNormalisationRuleUri);
		
		Collection<URI> includedStages = validStages;
		
		Collection<URI> excludedStages = invalidStages;
		
		for(URI nextIncludedStage : includedStages)
		{
			try
			{
				normalisationRule.addStage(nextIncludedStage);
			}
			catch(InvalidStageException ise)
			{
				assertTrue("InvalidStageException thrown for valid stage", false);
			}
		}
		
		NormalisationRuleTestUtil.testIsValidInStages(normalisationRule, validStages, true);
		NormalisationRuleTestUtil.testIsValidInStages(normalisationRule, invalidStages, false);

		NormalisationRuleTestUtil.testIsUsedInStages(normalisationRule, includedStages, true);
		NormalisationRuleTestUtil.testIsUsedInStages(normalisationRule, excludedStages, false);
	}
}
