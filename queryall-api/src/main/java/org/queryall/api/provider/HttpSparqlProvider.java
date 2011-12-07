package org.queryall.api.provider;

/**
 * An HTTP Provider that is also a SPARQL Provider.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface HttpSparqlProvider extends HttpProvider, SparqlProvider
{
    /**
     * 
     * @return True if the URI returned by getEndpointMethod indicates that this provider requires
     *         HTTP GET based SPARQL queries.
     */
    boolean isHttpGetSparql();
    
    /**
     * 
     * @return True if the URI returned by getEndpointMethod indicates that this provider requires
     *         HTTP POST based SPARQL queries.
     */
    boolean isHttpPostSparql();
    
}
