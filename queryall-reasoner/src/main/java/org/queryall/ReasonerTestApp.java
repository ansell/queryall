package org.queryall;

import java.util.Iterator;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ValidityReport;
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
        
        // create an empty model
        Model emptyModel = ModelFactory.createDefaultModel();
        
        // create an inferencing model using Pellet reasoner
        InfModel model = ModelFactory.createInfModel(reasoner, emptyModel);
        
        // read the file
        //model.read( ont );
        
        model.read("http://localhost:8080/queryall/");
        model.read("http://localhost:8080/admin/configuration/3/n3","N3");

        System.out.println("model.size()="+model.size());
        
        // print validation report
        ValidityReport report = model.validate();
        
        printIterator(report.getReports(), "Validation Results");
        
        printIterator(model.listResourcesWithProperty(RDF.type, OWL.Class),"RDF type resources");
        
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
