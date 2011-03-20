
package org.queryall.helpers;

import java.util.HashSet;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;

/**
 * A utility class that is used by multiple different Bio2RDF classes
 * @author peter
 * @version $Id: $
 */
public class VoidUtility
{
    private static final Logger log = Logger.getLogger(VoidUtility.class
            .getName());
    @SuppressWarnings("unused")
    private static final boolean _TRACE = VoidUtility.log.isTraceEnabled();
    private static final boolean _DEBUG = VoidUtility.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = VoidUtility.log.isInfoEnabled();
    private Map<String,Collection<String>> queryUriToVoidSparqlConstructQueries;
    
    public static void testMethod(
            Repository nextRepository)
            throws OpenRDFException
    {
        String constructQueryUri = "http://bio2rdf.org/query:construct";
        String linksQueryUri = "http://bio2rdf.org/query:links";
        
        String sparqlQuery = "CONSTRUCT { "
                                + "  <${resultProviderUri}> a <http://bio2rdf.org/ns/provider:Provider> . "
                                + "  <${resultProviderUri}> <http://bio2rdf.org/ns/provider:resolutionStrategy> <http://bio2rdf.org/ns/provider:proxy> . "
                                + "  <${resultProviderUri}> <http://bio2rdf.org/ns/provider:requiresGraphUri> \"false\"^^<http://www.w3.org/2001/XMLSchema#boolean> . "
                                + "  <${resultProviderUri}> <http://bio2rdf.org/ns/profile:profileIncludeExcludeOrder> <http://bio2rdf.org/ns/profile:excludeThenInclude> . "
                                + "  <${resultProviderUri}> <http://bio2rdf.org/ns/provider:endpointUrl> ?endpoint . "
                                + "  <${resultProviderUri}> <http://bio2rdf.org/ns/provider:handlesNamespace> <${resultNamespaceUri}> . "
                                + "  <${resultProviderUri}> <http://bio2rdf.org/ns/provider:includedInQuery> <${queryUri}> . "
                                + " } "
                                + " WHERE "
                                + " { "
                                + "  ?dataset a <http://rdfs.org/ns/void#Dataset> . "
                                + "  ?dataset <http://rdfs.org/ns/void#sparqlEndpoint> ?endpoint . "
                                + " } ";
        Map<String,Collection<String>> testMapping = new Hashtable<String, Collection<String>>();
        
        Collection<String> constructQueries = new HashSet<String>();
        constructQueries.add(sparqlQuery);
        
        testMapping.put(constructQueryUri, constructQueries);
        
        Collection<String> linksQueries = new HashSet<String>();
        linksQueries.add(sparqlQuery);
        
        testMapping.put(linksQueryUri, linksQueries);
        
        VoidUtility testUtility = new VoidUtility();
        
        testUtility.setQueryTypeMappings(testMapping);
        
        testUtility.parseFromVoidRepository(nextRepository);
    }
    
    public void setQueryTypeMappings(Map<String, Collection<String>> nextMapping)
    {
        queryUriToVoidSparqlConstructQueries = nextMapping;
    }
    
    public Map<String, Collection<String>> getQueryTypeMappings()
    {
        if(queryUriToVoidSparqlConstructQueries == null)
            queryUriToVoidSparqlConstructQueries = new Hashtable<String, Collection<String>>();
        
        return queryUriToVoidSparqlConstructQueries;
    }
    
    public VoidUtility()
    {
        queryUriToVoidSparqlConstructQueries = new Hashtable<String, Collection<String>>();
    }
    
    /**
     * @param nextRepository
     * @throws OpenRDFException
     */
    public void parseFromVoidRepository(
            Repository nextRepository)
            throws OpenRDFException
    {
        if(VoidUtility._DEBUG)
        {
            VoidUtility.log
                    .debug("VoidUtility.parseFromVoidRepository: entering method");
            VoidUtility.log.debug(nextRepository);
        }
        
        RepositoryConnection con = nextRepository.getConnection();
        
        Map<String, Collection<String>> currentMappings = getQueryTypeMappings();
        
        Collection<String> queries = new HashSet<String>();
        
        int queryCount = 1;
        for(String nextQueryUri : currentMappings.keySet())
        {
            int subQueryCount = 1;
            
            Collection<String> mappedQueries = currentMappings.get(nextQueryUri);
            
            for(String nextMappedQuery : mappedQueries)
            {
                String replacedQuery = nextMappedQuery.replace("${resultProviderUri}","http://testnamespace.org/provider:"+queryCount+"_"+subQueryCount++);
                replacedQuery = replacedQuery.replace("${resultNamespaceUri}","http://testnamespace.org/namespace:"+queryCount+"_"+subQueryCount++);
                replacedQuery = replacedQuery.replace("${queryUri}", nextQueryUri);
                
                
                queries.add(replacedQuery);
            }
            
            queryCount++;
        }
        
        for(String nextQuery : queries)
        {
            VoidUtility.log.debug("VoidUtility.parseFromVoidRepository: nextQuery="+nextQuery);
            
            
            GraphQuery graphQuery = con.prepareGraphQuery(QueryLanguage.SPARQL, nextQuery);
            GraphQueryResult graphQueryResult = graphQuery.evaluate();
            
            VoidUtility.log.debug("VoidUtility.parseFromVoidRepository: query evaluated");
            
            try
            {
                // if(true)
                // {
                    // List<Statement> allStatements = RdfUtils.getAllStatementsFromRepository(nextRepository);
                    // 
                    // VoidUtility.log.debug("VoidUtility.parseFromVoidRepository: allStatements.size()="+allStatements.size());
                    // 
                    // for(Statement nextStatement : allStatements)
                    // {
                        // if(VoidUtility._DEBUG)
                        // {
                            // VoidUtility.log
                                    // .debug("VoidUtility.parseFromVoidRepository: found statement: nextStatement="
                                            // + nextStatement);
                        // }
                    // }
                // }
                // else
                // {
                    while(graphQueryResult.hasNext())
                    {
                        Statement nextStatement = graphQueryResult.next();
                        
                        if(VoidUtility._DEBUG)
                        {
                            VoidUtility.log
                                    .debug("VoidUtility.parseFromVoidRepository: found statement: nextStatement="
                                            + nextStatement);
                        }
                        
                        // results.add(nextStatement);
                    }
                // }
            }
            catch (final OpenRDFException ordfe)
            {
                VoidUtility.log
                        .error("VoidUtility.parseFromVoidRepository: inner caught exception "
                                + ordfe);
                
                throw ordfe;
            }
            // finally
            // {
                // queryResult.close();
            // }
            
            
        }
    }
    
}
