package org.queryall.api.provider;

import java.util.Collection;

import org.openrdf.model.URI;
import org.queryall.api.base.BaseQueryAllInterface;
import org.queryall.api.base.ProfilableInterface;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface Provider extends BaseQueryAllInterface, Comparable<Provider>, ProfilableInterface
{
    /**
     * Adds the query type to the current collection of query types that are applicable to this
     * provider.
     * 
     * @param includedInQueryType
     */
    void addIncludedInQueryType(URI includedInQueryType);
    
    /**
     * Adds the namespace to the current collection of namespaces that are available using this
     * provider.
     * 
     * @param namespace
     */
    void addNamespace(URI namespace);
    
    /**
     * Adds the normalisation to this current collection of normalisations
     * 
     * @param rdfNormalisationNeeded
     *            The URI of a normalisation rule that is needed by this provider
     */
    void addNormalisationUri(URI rdfNormalisationNeeded);
    
    /**
     * 
     * @param namespaceKey
     * @return True if the given namespace URI is explicitly defined for this provider, or the
     *         provider is a default provider for all namespaces
     */
    boolean containsNamespaceOrDefault(URI namespaceKey);
    
    /**
     * 
     * @param namespaceKey
     * @return True if the given namespace URI is explicitly defined for this provider
     */
    boolean containsNamespaceUri(URI namespaceKey);
    
    /**
     * 
     * @param normalisationKey
     * @return True if the given normalisation rule URI is explicitly defined for this provider
     */
    boolean containsNormalisationUri(URI normalisationKey);
    
    /**
     * 
     * @param queryKey
     * @return True if the given query type is explicitly defined for this provider
     */
    boolean containsQueryTypeUri(URI queryKey);
    
    /**
     * 
     * @return The content type to assume for this provider if it is known not to send accurate
     *         content types back
     */
    String getAssumedContentType();
    
    /**
     * 
     * @return The URI defining the method of communicating with this provider
     */
    URI getEndpointMethod();
    
    /**
     * 
     * @return A collection of URIs that represent the query types that are defined for this
     *         provider
     */
    Collection<URI> getIncludedInQueryTypes();
    
    /**
     * 
     * @return True if this provider is able to answer queries against all namespaces, as long as
     *         the relevant query types are useful for default providers
     */
    boolean getIsDefaultSource();
    
    /**
     * 
     * @return A collection of URIs that represent the namespaces that are explicitly defined for
     *         this provider
     */
    Collection<URI> getNamespaces();
    
    /**
     * 
     * @return A collection of URIs that represent the normalisation rules that are active for this
     *         provider
     */
    Collection<URI> getNormalisationUris();
    
    /**
     * 
     * @return A URI indicating whether the provider requires redirection, and if so which kind, or
     *         whether the results should be interpreted and combined with other results in a proxy
     *         manner.
     */
    URI getRedirectOrProxy();
    
    /**
     * 
     * @return True if the URI returned from getRedirectOrProxy indicates that the results should be
     *         interpreted and combined in a proxy manner with other results.
     */
    boolean needsProxy();
    
    /**
     * 
     * @return True if the URI returned from getRedirectOrProxy indicates that this provider
     *         redirects the user to another location.
     */
    boolean needsRedirect();
    
    /**
     * 
     * @param assumedContentType
     *            The MIME type that should be assumed for this provider to work around issues with
     *            providers not returning correct content types
     */
    void setAssumedContentType(String assumedContentType);
    
    /**
     * 
     * @param endpointMethod
     *            A URI indicating which method of communication is necessary for this provider.
     */
    void setEndpointMethod(URI endpointMethod);
    
    /**
     * A provider is a default source of information if it can be used with query types irrespective
     * of the namespaces that the query type may define. If a query types is not namespace specific,
     * then the default status on this provider does not make a difference in deciding whether the
     * provider will be used for a query.
     * 
     * It is not a default source of information for all query types. Query types must be defined on
     * providers for a provider to respond to a given query type.
     * 
     * @param isDefaultSource
     *            True to indicate that this provider is applicable for all namespaces for namespace
     *            specific query types that indicate they support default providers.
     */
    void setIsDefaultSource(boolean isDefaultSource);
    
    /**
     * 
     * @param redirectOrProxy
     *            A URI indicating whether this provider requires redirection or proxying, and if
     *            so, what the nature of the operation is.
     */
    void setRedirectOrProxy(URI redirectOrProxy);
}
