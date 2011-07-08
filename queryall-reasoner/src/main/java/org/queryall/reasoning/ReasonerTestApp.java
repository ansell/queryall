package org.queryall.reasoning;

import java.util.Iterator;

import org.mindswap.pellet.jena.PelletReasonerFactory;

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
        
    	printIterator(geneidModel.listStatements(), "sparql 1.1 query data");
    	
    	Model oboAeoModel = ModelFactory.createDefaultModel();
    	
    	oboAeoModel.read("http://bio2rdf.org/obo_aeo:0000293");
    	
    	String testUri = "http://purl.obolibrary.org/obo/AEO_";
    	String testQuery = "?myUri ?property ?object . FILTER(isUri(?object) && regex(str(?object), http://purl.obolibrary.org/obo/AEO_)) . bind(iri(concat(\"http://bio2rdf.org/obo_aeo:\", encode_for_uri(lcase(substr(str(?uri), 35)))) AS ?symbolUri)";
    	
    	Query query = QueryFactory.create("CONSTRUCT { ?myUri <http://bio2rdf.org/bio2rdf_resource:symbol_ref> ?symbolUri .  } WHERE { ?myUri <http://purl.org/science/owl/sciencecommons/ggp_has_primary_symbol> ?primarySymbol . bind(iri(concat(\"http://bio2rdf.org/symbol:\", encode_for_uri(lcase(str(?primarySymbol))))) AS ?symbolUri) }", Syntax.syntaxSPARQL_11);

    	
    	QueryExecution qe = QueryExecutionFactory.create(query, geneidModel);
    	
    	final Model resultsModel = ModelFactory.createDefaultModel();
        
    	qe.execConstruct(resultsModel);
    	
    	printIterator(resultsModel.listStatements(), "sparql 1.1 query results");
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
