/**
 * 
 */
package org.queryall.reasoning;

import java.io.StringWriter;
import java.io.Writer;

import org.openrdf.model.vocabulary.RDF;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import de.fuberlin.wiwiss.r2r.JenaModelOutput;
import de.fuberlin.wiwiss.r2r.Mapper;
import de.fuberlin.wiwiss.r2r.Output;
import de.fuberlin.wiwiss.r2r.Repository;
import de.fuberlin.wiwiss.r2r.Source;
import de.fuberlin.wiwiss.r2r.JenaModelSource;;

/**
 *
 */
public class R2RTest
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Model sourceModel = ModelFactory.createDefaultModel();
		
		sourceModel.add(ResourceFactory.createResource("http://purl.org/commons/record/ncbi_gene/12334"), ResourceFactory.createProperty("http://purl.org/science/owl/sciencecommons/ggp_has_primary_symbol"), ResourceFactory.createPlainLiteral("Capn2"));
		
		Model results = ModelFactory.createDefaultModel();
		
		Model mappingModel = ModelFactory.createOntologyModel();
		
		mappingModel.add(ResourceFactory.createResource("http://bio2rdf.org/rdfrule:neurocommonsGeneSymbolToHgnc"), ResourceFactory.createProperty(RDF.TYPE.stringValue()), ResourceFactory.createResource("http://www4.wiwiss.fu-berlin.de/bizer/r2r/Mapping"));
		mappingModel.add(ResourceFactory.createResource("http://bio2rdf.org/rdfrule:neurocommonsGeneSymbolToHgnc"), ResourceFactory.createProperty("http://www4.wiwiss.fu-berlin.de/bizer/r2r/sourcePattern"), ResourceFactory.createPlainLiteral("?SUBJ <http://purl.org/science/owl/sciencecommons/ggp_has_primary_symbol> ?symbolText"));
//		mappingModel.add(ResourceFactory.createResource("http://bio2rdf.org/rdfrule:neurocommonsGeneSymbolToHgnc"), ResourceFactory.createProperty("http://www4.wiwiss.fu-berlin.de/bizer/r2r/targetPattern"), ResourceFactory.createPlainLiteral("?SUBJ <http://bio2rdf.org/hgnc_resource:symbol> ?symbolUri"));
		mappingModel.add(ResourceFactory.createResource("http://bio2rdf.org/rdfrule:neurocommonsGeneSymbolToHgnc"), ResourceFactory.createProperty("http://www4.wiwiss.fu-berlin.de/bizer/r2r/targetPattern"), ResourceFactory.createPlainLiteral("?SUBJ <http://bio2rdf.org/hgnc_resource:symbol> ?<symbolUri>"));
//		mappingModel.add(ResourceFactory.createResource("http://bio2rdf.org/rdfrule:neurocommonsGeneSymbolToHgnc"), ResourceFactory.createProperty("http://www4.wiwiss.fu-berlin.de/bizer/r2r/transformation"), ResourceFactory.createPlainLiteral("?symbolUri = iri(concat(\"http://bio2rdf.org/symbol:\",encode_for_uri(lcase(str(?symbolText)))))"));
//		mappingModel.add(ResourceFactory.createResource("http://bio2rdf.org/rdfrule:neurocommonsGeneSymbolToHgnc"), ResourceFactory.createProperty("http://www4.wiwiss.fu-berlin.de/bizer/r2r/transformation"), ResourceFactory.createPlainLiteral("?symbolUri = concat(\"http://bio2rdf.org/symbol:\",encode_for_uri(lcase(str(?symbolText))))"));
		mappingModel.add(ResourceFactory.createResource("http://bio2rdf.org/rdfrule:neurocommonsGeneSymbolToHgnc"), ResourceFactory.createProperty("http://www4.wiwiss.fu-berlin.de/bizer/r2r/transformation"), ResourceFactory.createPlainLiteral("?symbolUri = concat(\"http://bio2rdf.org/symbol:\",?symbolText)"));
//		mappingModel.add(ResourceFactory.createResource("http://bio2rdf.org/rdfrule:neurocommonsGeneSymbolToHgnc"), ResourceFactory.createProperty("http://www4.wiwiss.fu-berlin.de/bizer/r2r/transformation"), ResourceFactory.createPlainLiteral("?symbolUri = ?symbolText"));
				
		Source source = new JenaModelSource(sourceModel);
		Output out = new JenaModelOutput(results);
		
		Repository mappingRepository = Repository.createJenaModelRepository(mappingModel);
		
		// Specify target dataset. Just generate any statement containing one of the properties
		String vocabulary = "(<http://bio2rdf.org/hgnc_resource:symbol>)";
	
		Mapper.transform(source, out, mappingRepository, vocabulary);
		
		Writer resultsWriter = new StringWriter();
		results.write(resultsWriter);
		
		System.out.println(resultsWriter.toString());
		
		System.out.println("Finished.");
	}

}
