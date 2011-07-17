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
	public abstract boolean isHttpGetUrl();

	public abstract boolean hasEndpointUrl();

	public abstract Collection<String> getEndpointUrls();

	public abstract void setEndpointUrls(Collection<String> endpointUrls);

	public abstract void addEndpointUrl(String endpointUrl);

	public abstract String getAcceptHeaderString();

	public abstract void setAcceptHeaderString(String acceptHeaderString);

	public abstract boolean isHttpPostSparql();

}
