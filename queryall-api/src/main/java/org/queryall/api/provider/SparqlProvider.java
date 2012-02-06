package org.queryall.api.provider;

/**
 * A SPARQL Provider provides RDF statements using SPARQL queries, including references to Graphs
 * that may be defined as variables on the input template from a SparqlProcessingQueryType that is
 * linked to it.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface SparqlProvider extends Provider
{
    /**
     * The graph URI may contain template elements, so it cannot be directly verified as a URI.
     * 
     * @return The sparql graph URI, which may contain template elements, or the empty string "" if
     *         the graph URI is not set or getUseSparqlGraph returns false
     */
    String getSparqlGraphUri();
    
    /**
     * @return true if this provider requires the use of a sparql graph URI
     */
    boolean getUseSparqlGraph();
    
    /**
     * Set the sparql graph URI using a string The graph URI may contain template elements, so it
     * cannot be directly verified as a URI.
     * 
     * @param sparqlGraphUri
     *            The string representing the graph URI, may contain templates that are not valid in
     *            a URI until replaced by a query
     */
    void setSparqlGraphUri(String sparqlGraphUri);
    
    /**
     * @param useSparqlGraph
     *            true if this provider requires the use of a sparql graph URI
     */
    void setUseSparqlGraph(boolean useSparqlGraph);
}
