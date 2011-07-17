package org.queryall.api;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface SparqlProvider extends Provider
{
    /**
     * The graph URI may contain template elements, so it cannot be directly verified as a URI
     * @return The sparql graph URI, which may contain template elements, or the empty string "" if the graph URI is not set or getUseSparqlGraph returns false
     */
    public abstract String getSparqlGraphUri();

    /**
     * Set the sparql graph URI using a string
     * The graph URI may contain template elements, so it cannot be directly verified as a URI
     * @param sparqlGraphUri The string representing the graph URI, may contain templates that are not valid in a URI until replaced by a query
     */
    public abstract void setSparqlGraphUri(String sparqlGraphUri);

    /**
     * @return true if this provider requires the use of a sparql graph URI
     */
    public abstract boolean getUseSparqlGraph();

    /**
     * @param useSparqlGraph true if this provider requires the use of a sparql graph URI
     */
    public abstract void setUseSparqlGraph(boolean useSparqlGraph);


}
