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
    void addEndpointUrl(String endpointUrl);
    
    String getAcceptHeaderString(String defaultAcceptHeader);
    
    Collection<String> getEndpointUrls();
    
    boolean hasEndpointUrl();
    
    boolean isHttpGetUrl();
    
    boolean isHttpPostSparql();
    
    void setAcceptHeaderString(String acceptHeaderString);
    
    void setEndpointUrls(Collection<String> endpointUrls);
    
}