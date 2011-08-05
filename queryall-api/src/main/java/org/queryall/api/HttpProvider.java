/**
 * 
 */
package org.queryall.api;

import java.util.Collection;

/**
 * @author karina
 *
 */
public interface HttpProvider extends Provider
{
	boolean isHttpGetUrl();

	boolean hasEndpointUrl();

	Collection<String> getEndpointUrls();

	void setEndpointUrls(Collection<String> endpointUrls);

	void addEndpointUrl(String endpointUrl);

	String getAcceptHeaderString();

	void setAcceptHeaderString(String acceptHeaderString);

	boolean isHttpPostSparql();

}
