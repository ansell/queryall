/**
 * 
 */
package org.queryall.api.provider;

import java.util.Collection;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface HttpProvider extends Provider
{
    /**
     * All endpoint URLs defined for an HTTP Provider must be able to be used independently to
     * derive the same results after normalisation.
     * 
     * @param endpointUrl
     *            The HTTP endpoint URL template relevant to this provider. It may contain variables
     *            from the query type.
     */
    void addEndpointUrl(String endpointUrl);
    
    /**
     * 
     * @param defaultAcceptHeader
     *            The default accept header to use for HTTP providers that do not define accept
     *            headers
     * @return The accept header, or if the accept header is not defined for this provider, returns
     *         the given defaultAcceptHeader
     */
    String getAcceptHeaderString(String defaultAcceptHeader);
    
    /**
     * 
     * @return A collection of equivalent endpoint URL templates that are relevant to this provider.
     *         Any of the endpoint URL templates may be used interchangeably.
     */
    Collection<String> getEndpointUrls();
    
    /**
     * 
     * @return True if there is a valid endpoint URL template defined for this HTTP Provider
     */
    boolean hasEndpointUrl();
    
    /**
     * 
     * @return True if this providers getEndpointMethod function returns a URI that indicates that
     *         HTTP GET should be used with this provider.
     */
    boolean isHttpGetUrl();
    
    /**
     * 
     * @param acceptHeaderString
     *            A string that will be used in the HTTP Accept: Header in all HTTP calls to this
     *            provider.
     */
    void setAcceptHeaderString(String acceptHeaderString);
    
}
