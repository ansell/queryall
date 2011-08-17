package org.queryall.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class that is used by multiple different Bio2RDF classes
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class VoidUtility
{
    private static final Logger log = LoggerFactory.getLogger(VoidUtility.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = VoidUtility.log.isTraceEnabled();
    private static final boolean _DEBUG = VoidUtility.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = VoidUtility.log.isInfoEnabled();
    
    public static void testMethod(final Repository nextRepository) throws OpenRDFException
    {
        final String constructQueryUri = "http://bio2rdf.org/query:construct";
        final String linksQueryUri = "http://bio2rdf.org/query:links";
        
        final String sparqlQuery =
                "CONSTRUCT { "
                        + "  <${resultProviderUri}> a <http://purl.org/queryall/provider:Provider> . "
                        + "  <${resultProviderUri}> <http://purl.org/queryall/provider:resolutionStrategy> <http://purl.org/queryall/provider:proxy> . "
                        + "  <${resultProviderUri}> <http://purl.org/queryall/provider:requiresGraphUri> \"false\"^^<http://www.w3.org/2001/XMLSchema#boolean> . "
                        + "  <${resultProviderUri}> <http://purl.org/queryall/profile:profileIncludeExcludeOrder> <http://purl.org/queryall/profile:excludeThenInclude> . "
                        + "  <${resultProviderUri}> <http://purl.org/queryall/provider:endpointUrl> ?endpoint . "
                        + "  <${resultProviderUri}> <http://purl.org/queryall/provider:handlesNamespace> <${resultNamespaceUri}> . "
                        + "  <${resultProviderUri}> <http://purl.org/queryall/provider:includedInQuery> <${queryUri}> . "
                        + " } " + " WHERE " + " { " + "  ?dataset a <http://rdfs.org/ns/void#Dataset> . "
                        + "  ?dataset <http://rdfs.org/ns/void#sparqlEndpoint> ?endpoint . " + " } ";
        
        final Map<String, Collection<String>> testMapping = new Hashtable<String, Collection<String>>();
        
        final Collection<String> constructQueries = new HashSet<String>();
        constructQueries.add(sparqlQuery);
        
        testMapping.put(constructQueryUri, constructQueries);
        
        final Collection<String> linksQueries = new HashSet<String>();
        linksQueries.add(sparqlQuery);
        
        testMapping.put(linksQueryUri, linksQueries);
        
        final VoidUtility testUtility = new VoidUtility();
        
        testUtility.setQueryTypeMappings(testMapping);
        
        testUtility.parseFromVoidRepository(nextRepository);
    }
    
    private Map<String, Collection<String>> queryUriToVoidSparqlConstructQueries;
    
    public VoidUtility()
    {
        this.queryUriToVoidSparqlConstructQueries = new Hashtable<String, Collection<String>>();
    }
    
    public Map<String, Collection<String>> getQueryTypeMappings()
    {
        if(this.queryUriToVoidSparqlConstructQueries == null)
        {
            this.queryUriToVoidSparqlConstructQueries = new Hashtable<String, Collection<String>>();
        }
        
        return this.queryUriToVoidSparqlConstructQueries;
    }
    
    /**
     * @param nextRepository
     * @throws OpenRDFException
     */
    public void parseFromVoidRepository(final Repository nextRepository) throws OpenRDFException
    {
        if(VoidUtility._DEBUG)
        {
            VoidUtility.log.debug("VoidUtility.parseFromVoidRepository: entering method");
            // VoidUtility.log.debug(nextRepository);
        }
        
        final RepositoryConnection con = nextRepository.getConnection();
        
        final Map<String, Collection<String>> currentMappings = this.getQueryTypeMappings();
        
        final Collection<String> queries = new HashSet<String>();
        
        int queryCount = 1;
        for(final String nextQueryUri : currentMappings.keySet())
        {
            int subQueryCount = 1;
            
            final Collection<String> mappedQueries = currentMappings.get(nextQueryUri);
            
            for(final String nextMappedQuery : mappedQueries)
            {
                String replacedQuery =
                        nextMappedQuery.replace("${resultProviderUri}", "http://testnamespace.org/provider:"
                                + queryCount + "_" + subQueryCount++);
                replacedQuery =
                        replacedQuery.replace("${resultNamespaceUri}", "http://testnamespace.org/namespace:"
                                + queryCount + "_" + subQueryCount++);
                replacedQuery = replacedQuery.replace("${queryUri}", nextQueryUri);
                
                queries.add(replacedQuery);
            }
            
            queryCount++;
        }
        
        for(final String nextQuery : queries)
        {
            VoidUtility.log.debug("VoidUtility.parseFromVoidRepository: nextQuery=" + nextQuery);
            
            final GraphQuery graphQuery = con.prepareGraphQuery(QueryLanguage.SPARQL, nextQuery);
            final GraphQueryResult graphQueryResult = graphQuery.evaluate();
            
            VoidUtility.log.debug("VoidUtility.parseFromVoidRepository: query evaluated");
            
            try
            {
                // if(true)
                // {
                // List<Statement> allStatements =
                // RdfUtils.getAllStatementsFromRepository(nextRepository);
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
                    final Statement nextStatement = graphQueryResult.next();
                    
                    if(VoidUtility._DEBUG)
                    {
                        VoidUtility.log.debug("VoidUtility.parseFromVoidRepository: found statement: nextStatement="
                                + nextStatement);
                    }
                    
                    // results.add(nextStatement);
                }
                // }
            }
            catch(final OpenRDFException ordfe)
            {
                VoidUtility.log.error("VoidUtility.parseFromVoidRepository: inner caught exception " + ordfe);
                
                throw ordfe;
            }
            // finally
            // {
            // queryResult.close();
            // }
            
        }
    }
    
    public void setQueryTypeMappings(final Map<String, Collection<String>> nextMapping)
    {
        this.queryUriToVoidSparqlConstructQueries = nextMapping;
    }
    
}
