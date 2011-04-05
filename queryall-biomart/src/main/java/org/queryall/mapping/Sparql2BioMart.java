/**
 * 
 */
package org.queryall.mapping;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.query.Dataset;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.ast.ASTDatasetClause;
import org.openrdf.query.parser.sparql.ast.ASTQueryContainer;
import org.openrdf.query.parser.sparql.ast.ParseException;
import org.openrdf.query.parser.sparql.ast.SyntaxTreeBuilder;
import org.openrdf.query.parser.sparql.ast.TokenMgrError;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class Sparql2BioMart 
{
	public static void main(String[] args)
	{
		try 
		{
			String exampleQuery = "CONSTRUCT { ?s ?p ?o } FROM NAMED <urn:exampleNamedGraphUri> WHERE { GRAPH <urn:exampleGraphUri> { ?s ?p ?o . } }";
			String example2Query = "CONSTRUCT { ?resultObject <ensembl_gene_id> ?ensembl_gene_id . ?resultObject <pathway_id_list> ?pathway_id_list . ?resultObject <stableidentifier_identifier> ?stableidentifier_identifier . ?resultObject <pathway_db_id> ?pathway_db_id . } WHERE {	GRAPH <urn:biomart:dataset:hsapiens_gene_ensembl>    {		?resultObject <ensembl_gene_id> ?ensembl_gene_id .    }	GRAPH <urn:biomart:dataset:pathway>    {		?resultObject <pathway_id_list> ?pathway_id_list .    	FILTER(STR(?pathway_id_list) = \"REACT_1698\") .    		?resultObject <stableidentifier_identifier> ?stableidentifier_identifier .    		?resultObject <pathway_db_id> ?pathway_db_id .    }}";

			List<String> exampleQueries = new LinkedList<String>();
			exampleQueries.add(exampleQuery);
			exampleQueries.add(example2Query);
			
			for(String nextExampleQuery : exampleQueries)
			{
				
				ParsedQuery parsedQuery = new org.openrdf.query.parser.sparql.SPARQLParser().parseQuery(nextExampleQuery, "urn:biomart:");
				if(parsedQuery.getDataset() != null)
				{
					Set<URI> defaultGraphUris = parsedQuery.getDataset().getDefaultGraphs();
					Set<URI> namedGraphUris = parsedQuery.getDataset().getNamedGraphs();
				}
				
				System.out.println("parsedQuery.toString()=");
				System.out.println(parsedQuery.toString());
				
				ASTQueryContainer qc = SyntaxTreeBuilder.parseQuery(nextExampleQuery);
				
				List<ASTDatasetClause> datasetClauseList = qc.getQuery().getDatasetClauseList();
				
				if(datasetClauseList != null)
				{
					for(ASTDatasetClause nextDatasetClause : datasetClauseList)
					{
						System.out.println("nextDatasetClause="+nextDatasetClause.dump(null));
					}
				}
			}
		} 
		catch (MalformedQueryException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TokenMgrError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void mapTo(ParsedQuery parsedSparqlQuery)
	{
		
	}
}
