package org.queryall.reasoning;

import java.util.Iterator;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
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
        baseModel.read("http://config.bio2rdf.org/admin/configuration/4/n3","N3");
        
        // create an inferencing model using Pellet reasoner
        final InfModel model = ModelFactory.createInfModel(reasoner, baseModel);


        System.out.println("model.size()="+model.size());
        
        // print validation report
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
