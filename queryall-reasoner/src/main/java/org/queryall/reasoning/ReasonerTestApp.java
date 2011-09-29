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
    /**
     * @param testStartingUri
     * @param testFinalUri
     * @return
     */
    private static String generateObjectConversionPattern(final String testStartingUri, final String testFinalUri)
    {
        return "?myUri ?property ?object . " + " " + "FILTER(isUri(?object) && regex(str(?object), \""
                + testStartingUri + "\")) . " + " " + "bind(" + "iri(" + "concat(" + "\"" + testFinalUri + "\"," + " "
                + "encode_for_uri(" + "lcase(" + "substr(" + "str(?object), " + (testStartingUri.length() + 1) + ")))"
                + " " + ") " + ") " + "AS ?convertedUri) . ";
    }
    
    /**
     * @param testStartingUri
     * @param testFinalUri
     * @return
     */
    private static String generatePredicateConversionPattern(final String testStartingUri, final String testFinalUri)
    {
        return "?myUri ?property ?object . " + " " + "FILTER(strStarts(str(?property), \"" + testStartingUri
                + "\")) . " + " " + "bind(" + "iri(" + "concat(" + "\"" + testFinalUri + "\"," + " "
                + "encode_for_uri(" + "lcase(" + "substr(" + "str(?property), " + (testStartingUri.length() + 1)
                + ")))" + " " + ") " + ") " + "AS ?convertedUri) . ";
    }
    
    public static void main(final String[] args)
    {
        final Reasoner reasoner = PelletReasonerFactory.theInstance().create();
        
        // create the base model
        final OntModel baseModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_TRANS_INF);
        // baseModel.read("http://localhost:8080/queryall/");
        // baseModel.read("http://config.bio2rdf.org/admin/configuration/4/n3","N3");
        
        // create an inferencing model using Pellet reasoner
        final InfModel model = ModelFactory.createInfModel(reasoner, baseModel);
        System.out.println("model.size()=" + model.size());
        
        // print validation report
        if(false)
        {
            final ValidityReport report = model.validate();
            
            ReasonerTestApp.printIterator(report.getReports(), "Validation Results");
            
            ReasonerTestApp.printIterator(model.listResourcesWithProperty(RDF.type, OWL.Class), "RDF type resources");
            ReasonerTestApp.printIterator(model.listStatements().filterDrop(new Filter<Statement>()
                {
                    @Override
                    public boolean accept(final Statement o)
                    {
                        return model.getRawModel().contains(o);
                    }
                }), "Inferred statements");
        }
        
        final Model geneidModel = ModelFactory.createDefaultModel();
        
        geneidModel.read("http://bio2rdf.org/geneid:12334");
        
        // printIterator(geneidModel.listStatements(), "sparql 1.1 query data");
        // "CONSTRUCT { ?myUri <http://bio2rdf.org/bio2rdf_resource:symbol_ref> ?symbolUri .  } WHERE { ?myUri <http://purl.org/science/owl/sciencecommons/ggp_has_primary_symbol> ?primarySymbol . bind(iri(concat(\"http://bio2rdf.org/symbol:\", encode_for_uri(lcase(str(?primarySymbol))))) AS ?symbolUri) }"
        
        final Model oboAeoModel = ModelFactory.createDefaultModel();
        
        oboAeoModel.read("http://bio2rdf.org/obo_aeo:0000293");
        
        ReasonerTestApp.printIterator(oboAeoModel.listStatements(), "sparql 1.1 query data");
        
        final String testStartingUri = "http://purl.obolibrary.org/obo/AEO_";
        final String testFinalUri = "http://bio2rdf.org/obo_aeo:";
        final String testQueryConstructGraph =
                "?myUri ?property ?convertedUri . ?convertedUri <http://www.w3.org/2002/07/owl#sameAs> ?object . ";
        final String testQueryWherePattern =
                ReasonerTestApp.generateObjectConversionPattern(testStartingUri, testFinalUri);
        
        final SparqlNormalisationRule queryallRule = new SparqlNormalisationRuleImpl();
        
        queryallRule.setKey("http://bio2rdf.org/rdfrule:oboaeosparqlrule");
        queryallRule.setMode(SparqlNormalisationRuleSchema.getSparqlRuleModeAddAllMatchingTriples());
        queryallRule.setOrder(100);
        queryallRule.setSparqlConstructQueryTarget(testQueryConstructGraph);
        queryallRule.addSparqlWherePattern(testQueryWherePattern);
        
        try
        {
            queryallRule.addStage(NormalisationRuleSchema.getRdfruleStageAfterResultsImport());
        }
        catch(final InvalidStageException ise)
        {
            System.err.println("Found invalid stage exception");
        }
        
        queryallRule.setProfileIncludeExcludeOrder(ProfileSchema.getProfileExcludeThenIncludeUri());
        
        final Query query =
                QueryFactory.create(ReasonerTestApp.mergeQuery(testQueryConstructGraph, testQueryWherePattern),
                        Syntax.syntaxSPARQL_11);
        
        final QueryExecution qe = QueryExecutionFactory.create(query, oboAeoModel);
        
        final Model resultsModel = ModelFactory.createDefaultModel();
        
        qe.execConstruct(resultsModel);
        
        ReasonerTestApp.printIterator(resultsModel.listStatements(), "sparql 1.1 query object results");
        
        final String testStartingPredicateUri = "http://www.w3.org/2004/02/skos/core#";
        final String testFinalPredicateUri = "http://bio2rdf.org/skoscore_resource:";
        final String testPredicateQueryConstructGraph =
                "?myUri ?convertedUri ?object . ?convertedUri <http://www.w3.org/2002/07/owl#sameAs> ?property . ";
        final String testPredicateQueryWherePattern =
                ReasonerTestApp.generatePredicateConversionPattern(testStartingPredicateUri, testFinalPredicateUri);
        
        final Query predicateQuery =
                QueryFactory.create(
                        ReasonerTestApp.mergeQuery(testPredicateQueryConstructGraph, testPredicateQueryWherePattern),
                        Syntax.syntaxSPARQL_11);
        
        final QueryExecution predicateQueryExecution = QueryExecutionFactory.create(predicateQuery, oboAeoModel);
        
        final Model predicateResultsModel = ModelFactory.createDefaultModel();
        
        predicateQueryExecution.execConstruct(predicateResultsModel);
        
        ReasonerTestApp.printIterator(predicateResultsModel.listStatements(), "sparql 1.1 query predicate results");
        
    }
    
    public static String mergeQuery(final String constructGraphPattern, final String wherePattern)
    {
        return new StringBuilder(300).append("CONSTRUCT { ").append(constructGraphPattern).append(" } WHERE { ")
                .append(wherePattern).append(" }").toString();
    }
    
    public static void printIterator(final Iterator<?> i, final String header)
    {
        System.out.println(header);
        for(int c = 0; c < header.length(); c++)
        {
            System.out.print("=");
        }
        System.out.println();
        
        if(i.hasNext())
        {
            while(i.hasNext())
            {
                System.out.println(i.next());
            }
        }
        else
        {
            System.out.println("<EMPTY>");
        }
        
        System.out.println();
    }
    
}
