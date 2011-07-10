/**
 * 
 */
package org.queryall;

import static org.junit.Assert.*;

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
	private SparqlNormalisationRule queryallRule;

	/**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        ValueFactory f = new MemValueFactory();

        testTrueSparqlNormalisationRuleUri = f.createURI("http://example.org/test/includedNormalisationRule");
        testFalseSparqlNormalisationRuleUri = f.createURI("http://example.org/test/excludedNormalisationRule");
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
    	testTrueSparqlNormalisationRuleUri = null;
    	testFalseSparqlNormalisationRuleUri = null;
    }
    
    public abstract SparqlNormalisationRule getNewTestSparqlRule();

	public abstract Profile getNewTestProfile();

	public abstract URI getProfileIncludeExcludeOrderUndefinedUri();

	public abstract URI getProfileIncludeThenExcludeURI();

	public abstract URI getProfileExcludeThenIncludeURI();
	
	public abstract URI getSparqlRuleModeAddAllMatchingTriplesURI();
	
	public abstract URI getRdfruleStageAfterResultsImportURI();
	
    @Test
	public void testConstructQueryMultipleWherePatterns()
	{
		String testStartingUriAEOBase = "http://purl.obolibrary.org/obo/AEO_";
		String testFinalUriAEOBase = "http://bio2rdf.org/obo_aeo:";
		String testStartingUriPOBase = "http://purl.obolibrary.org/obo/PO_";
		String testFinalUriPOBase = "http://bio2rdf.org/obo_po:";
		String testQueryConstructGraph = "?myUri ?property ?convertedUri . ?convertedUri <http://www.w3.org/2002/07/owl#sameAs> ?object . ";
		
		queryallRule = getNewTestSparqlRule();
		
		((NormalisationRule)queryallRule).setKey("http://bio2rdf.org/rdfrule:oboaeosparqlrule");
		queryallRule.setMode(getSparqlRuleModeAddAllMatchingTriplesURI());
		((NormalisationRule)queryallRule).setOrder(100);
		queryallRule.setSparqlConstructQueryTarget(testQueryConstructGraph);
		queryallRule.addSparqlWherePattern(generateConversionPattern(testStartingUriAEOBase, testFinalUriAEOBase));
		queryallRule.addSparqlWherePattern(generateConversionPattern(testStartingUriPOBase, testFinalUriPOBase));
		((NormalisationRule)queryallRule).addStage(getRdfruleStageAfterResultsImportURI());
		((NormalisationRule)queryallRule).setProfileIncludeExcludeOrder(getProfileExcludeThenIncludeURI());
		
		
		
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
	
	private static String mergeQuery(String constructGraphPattern, String wherePattern)
	{
		return new StringBuilder(300).append("CONSTRUCT { ").append(constructGraphPattern).append(" } WHERE { ").append(wherePattern).append(" }").toString();
	}
}
