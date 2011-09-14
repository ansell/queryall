package org.queryall.reasoning;

import java.util.Iterator;

import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.queryall.api.profile.ProfileSchema;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.rdfrule.SparqlNormalisationRule;
import org.queryall.api.rdfrule.SparqlNormalisationRuleSchema;
import org.queryall.exception.InvalidStageException;
import org.queryall.impl.rdfrule.SparqlNormalisationRuleImpl;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Hello world!
 *
 */
public class ReasonerTestApp 
{
    public static void main(String[] args)
    {
        Reasoner reasoner = PelletReasonerFactory.theInstance().create();
        
        // create the base model
        OntModel baseModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_TRANS_INF);
        //baseModel.read("http://localhost:8080/queryall/");
        //baseModel.read("http://config.bio2rdf.org/admin/configuration/4/n3","N3");
        
        // create an inferencing model using Pellet reasoner
        final InfModel model = ModelFactory.createInfModel(reasoner, baseModel);
        System.out.println("model.size()="+model.size());
        
        // print validation report
        if(false)
        {
	        ValidityReport report = model.validate();
	        
	        printIterator(report.getReports(), "Validation Results");
	        
	        printIterator(model.listResourcesWithProperty(RDF.type, OWL.Class),"RDF type resources");
	    	printIterator(model.listStatements().filterDrop( new Filter<Statement>() {
	    		public boolean accept(Statement o) {
	    			return model.getRawModel().contains(o);
	    			}
	    			})
				, "Inferred statements");
        }
        
        Model geneidModel = ModelFactory.createDefaultModel();
        
        geneidModel.read("http://bio2rdf.org/geneid:12334");
        
//   	printIterator(geneidModel.listStatements(), "sparql 1.1 query data");
		//"CONSTRUCT { ?myUri <http://bio2rdf.org/bio2rdf_resource:symbol_ref> ?symbolUri .  } WHERE { ?myUri <http://purl.org/science/owl/sciencecommons/ggp_has_primary_symbol> ?primarySymbol . bind(iri(concat(\"http://bio2rdf.org/symbol:\", encode_for_uri(lcase(str(?primarySymbol))))) AS ?symbolUri) }"
    	
    	Model oboAeoModel = ModelFactory.createDefaultModel();
    	
    	oboAeoModel.read("http://bio2rdf.org/obo_aeo:0000293");
    	
    	printIterator(oboAeoModel.listStatements(), "sparql 1.1 query data");
    	
    	String testStartingUri = "http://purl.obolibrary.org/obo/AEO_";
    	String testFinalUri = "http://bio2rdf.org/obo_aeo:";
    	String testQueryConstructGraph = "?myUri ?property ?convertedUri . ?convertedUri <http://www.w3.org/2002/07/owl#sameAs> ?object . ";
    	String testQueryWherePattern = generateObjectConversionPattern(testStartingUri, testFinalUri);
    	
    	SparqlNormalisationRule queryallRule = new SparqlNormalisationRuleImpl();
    	
    	queryallRule.setKey("http://bio2rdf.org/rdfrule:oboaeosparqlrule");
    	queryallRule.setMode(SparqlNormalisationRuleSchema.getSparqlRuleModeAddAllMatchingTriples());
    	queryallRule.setOrder(100);
    	queryallRule.setSparqlConstructQueryTarget(testQueryConstructGraph);
    	queryallRule.addSparqlWherePattern(testQueryWherePattern);
    	
    	try
    	{
    		queryallRule.addStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport());
    	}
    	catch(InvalidStageException ise)
    	{
    		System.err.println("Found invalid stage exception");
    	}
    	
    	queryallRule.setProfileIncludeExcludeOrder(ProfileSchema.getProfileExcludeThenIncludeUri());
    	
    	
    	
    	Query query = QueryFactory.create(
    			mergeQuery(testQueryConstructGraph, testQueryWherePattern)
    			, Syntax.syntaxSPARQL_11);
    	
    	QueryExecution qe = QueryExecutionFactory.create(query, oboAeoModel);
    	
    	final Model resultsModel = ModelFactory.createDefaultModel();
        
    	qe.execConstruct(resultsModel);
    	
    	printIterator(resultsModel.listStatements(), "sparql 1.1 query object results");
    	
        String testStartingPredicateUri = "http://www.w3.org/2004/02/skos/core#";
        String testFinalPredicateUri = "http://bio2rdf.org/skoscore_resource:";
        String testPredicateQueryConstructGraph = "?myUri ?convertedUri ?object . ?convertedUri <http://www.w3.org/2002/07/owl#sameAs> ?property . ";
        String testPredicateQueryWherePattern = generatePredicateConversionPattern(testStartingPredicateUri, testFinalPredicateUri);
    	
        Query predicateQuery = QueryFactory.create(
                mergeQuery(testPredicateQueryConstructGraph, testPredicateQueryWherePattern)
                , Syntax.syntaxSPARQL_11);
        
        QueryExecution predicateQueryExecution = QueryExecutionFactory.create(predicateQuery, oboAeoModel);
        
        final Model predicateResultsModel = ModelFactory.createDefaultModel();
        
        predicateQueryExecution.execConstruct(predicateResultsModel);
        
        printIterator(predicateResultsModel.listStatements(), "sparql 1.1 query predicate results");
        
    }

    /**
     * @param testStartingUri
     * @param testFinalUri
     * @return
     */
    private static String generatePredicateConversionPattern(String testStartingUri,
            String testFinalUri)
    {
        return "?myUri ?property ?object . " +
                " " +
                "FILTER(strStarts(str(?property), \"" +
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
                "str(?property), " +
                (testStartingUri.length()+1) +
                ")))" +
                " " +
                ") " +
                ") " +
                "AS ?convertedUri) . ";
    }

    /**
	 * @param testStartingUri
	 * @param testFinalUri
	 * @return
	 */
	private static String generateObjectConversionPattern(String testStartingUri,
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
    
    public static String mergeQuery(String constructGraphPattern, String wherePattern)
    {
    	return new StringBuilder(300).append("CONSTRUCT { ").append(constructGraphPattern).append(" } WHERE { ").append(wherePattern).append(" }").toString();
    }
    
    public static void printIterator(Iterator<?> i, String header) {
        System.out.println(header);
        for(int c = 0; c < header.length(); c++)
            System.out.print("=");
        System.out.println();
        
        if(i.hasNext()) {
            while (i.hasNext()) 
                System.out.println( i.next() );
        }       
        else
            System.out.println("<EMPTY>");
        
        System.out.println();
    }

}
