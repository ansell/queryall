/**
 * 
 */
package org.queryall;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.sail.memory.model.MemValueFactory;

import org.queryall.api.NormalisationRule;
import org.queryall.api.Profile;
import org.queryall.api.SparqlNormalisationRule;

/**
 * @author peter
 *
 */
public abstract class AbstractSparqlNormalisationRuleTest
{
    private URI testTrueSparqlNormalisationRuleUri;
	private URI testFalseSparqlNormalisationRuleUri;
	private URI testMultipleWherePatternsSparqlNormalisationRuleUri;
	private SparqlNormalisationRule queryallRule;
	private String testStartingUriAEOBase;
	private String testFinalUriAEOBase;
	private String testStartingUriPOBase;
	private String testFinalUriPOBase;
	private URI testStageInclusionSparqlNormalisationRuleUri;

	/**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        ValueFactory f = new MemValueFactory();

        testTrueSparqlNormalisationRuleUri = f.createURI("http://example.org/test/includedNormalisationRule");
        testFalseSparqlNormalisationRuleUri = f.createURI("http://example.org/test/excludedNormalisationRule");
        testMultipleWherePatternsSparqlNormalisationRuleUri = f.createURI("http://example.org/test/multipleWherePatternsSparqlNormalisationRule");

		testStartingUriAEOBase = "http://purl.obolibrary.org/obo/AEO_";
		testFinalUriAEOBase = "http://bio2rdf.org/obo_aeo:";
		testStartingUriPOBase = "http://purl.obolibrary.org/obo/PO_";
		testFinalUriPOBase = "http://bio2rdf.org/obo_po:";
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
    	testTrueSparqlNormalisationRuleUri = null;
    	testFalseSparqlNormalisationRuleUri = null;

		testStartingUriAEOBase = null;
		testFinalUriAEOBase = null;
		testStartingUriPOBase = null;
		testFinalUriPOBase = null;
    }
    
    /**
     * Create a new instance of the SparqlNormalisationRule implementation being tested
     * @return a new instance of the implemented SparqlNormalisationRule
     */
    public abstract SparqlNormalisationRule getNewTestSparqlRule();

    /**
     * Create a new profile instance with default properties
     * @return A new profile instance with default properties
     */
	public abstract Profile getNewTestProfile();

	public abstract URI getProfileIncludeExcludeOrderUndefinedUri();

	public abstract URI getProfileIncludeThenExcludeURI();

	public abstract URI getProfileExcludeThenIncludeURI();
	
	public abstract URI getSparqlRuleModeAddAllMatchingTriplesURI();
	
	public abstract URI getSparqlRuleModeOnlyDeleteMatchesURI();

	public abstract URI getSparqlRuleModeOnlyIncludeMatchesURI();

	public abstract URI getRdfruleStageAfterResultsImportURI();
	
	public abstract URI getRdfruleStageQueryVariablesURI();

	public abstract URI getRdfruleStageAfterQueryCreationURI();

	public abstract URI getRdfruleStageAfterQueryParsingURI();

	public abstract URI getRdfruleStageBeforeResultsImportURI();

	public abstract URI getRdfruleStageAfterResultsToPoolURI();

	public abstract URI getRdfruleStageAfterResultsToDocumentURI();

    @Test
	public void testConstructQueryMultipleWherePatterns()
	{
		String testQueryConstructGraph = "?myUri ?property ?convertedUri . ?convertedUri <http://www.w3.org/2002/07/owl#sameAs> ?object . ";
		
		queryallRule = getNewTestSparqlRule();
		
		assertTrue(queryallRule instanceof NormalisationRule);

		((NormalisationRule)queryallRule).setKey(testMultipleWherePatternsSparqlNormalisationRuleUri);
		queryallRule.setSparqlConstructQueryTarget(testQueryConstructGraph);
		queryallRule.addSparqlWherePattern(generateConversionPattern(testStartingUriAEOBase, testFinalUriAEOBase));
		queryallRule.addSparqlWherePattern(generateConversionPattern(testStartingUriPOBase, testFinalUriPOBase));
		
		List<String> constructQueries = queryallRule.getSparqlConstructQueries();

		assertEquals(constructQueries.size(), 2);
		
		// TODO: this should be insensitive to spaces, possibly by using regular expression matchers, or by removing spaces
		//		Matcher matcher = new Matcher();
		//		assertThat(actual, matcher)
		// TODO: add prefix testing
		assertTrue(constructQueries.contains(mergeQuery("", testQueryConstructGraph, generateConversionPattern(testStartingUriAEOBase, testFinalUriAEOBase))));
		assertTrue(constructQueries.contains(mergeQuery("", testQueryConstructGraph, generateConversionPattern(testStartingUriPOBase, testFinalUriPOBase))));
	}
	
    @Test
	public void testStageInclusion()
	{
		queryallRule = getNewTestSparqlRule();
		
		assertTrue(queryallRule instanceof NormalisationRule);
		
		((NormalisationRule)queryallRule).setKey(testStageInclusionSparqlNormalisationRuleUri);
		((NormalisationRule)queryallRule).addStage(getRdfruleStageAfterResultsImportURI());
		
		List<String> constructQueries = queryallRule.getSparqlConstructQueries();

		assertEquals(constructQueries.size(), 0);
		
		assertTrue(((NormalisationRule)queryallRule).validInStage(getRdfruleStageAfterResultsImportURI()));
		assertTrue(((NormalisationRule)queryallRule).usedInStage(getRdfruleStageAfterResultsImportURI()));
		assertFalse(((NormalisationRule)queryallRule).usedInStage(getRdfruleStageQueryVariablesURI()));
	}

    /**
	 * @param testStartingUri
	 * @param testFinalUri
	 * @return
	 */
	private static String generateConversionPattern(String testStartingUri,
			String testFinalUri)
	{
		return "?myUri ?property ?object . " +
				" " +
				"FILTER(isUri(?object) && regex(str(?object), \"" +
				testStartingUri +
				"\")) . " +
				" " +
				"bind(" +
				"iri(" +
				"concat(" +
				"\"" +
				testFinalUri +
				"\"," +
				" " +
				"encode_for_uri(" +
				"lcase(" +
				"substr(" +
				"str(?object), " +
				(testStartingUri.length()+1) +
				")))" +
				" " +
				") " +
				") " +
				"AS ?convertedUri) . ";
	}
	
	private static String mergeQuery(String sparqlPrefixes, String constructGraphPattern, String wherePattern)
	{
		return new StringBuilder(sparqlPrefixes).append(" CONSTRUCT { ").append(constructGraphPattern).append(" } WHERE { ").append(wherePattern).append(" }").toString();
	}

}
