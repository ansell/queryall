package org.queryall.api;

import java.util.Collection;

import org.openrdf.model.URI;

public interface Provider extends BaseQueryAllInterface, Comparable<Provider>, ProfilableInterface
{
    boolean getIsDefaultSource();

    void setIsDefaultSource(boolean isDefaultSource);

    Collection<URI> getNormalisationUris();
    
    /**
     * Adds the normalisation to this current collection of normalisations
     * 
     * @param rdfNormalisationNeeded
     */
    void addNormalisationUri(URI rdfNormalisationNeeded);
    
    boolean containsNormalisationUri(URI normalisationKey);

    Collection<URI> getIncludedInQueryTypes();
    
    void setIncludedInQueryTypes(Collection<URI> includedInQueryTypes);
    
    void addIncludedInQueryType(URI includedInQueryType);

    boolean containsQueryTypeUri(URI queryKey);

    Collection<URI> getNamespaces();
    
    void setNamespaces(Collection<URI> namespaces);
    
    void addNamespace(URI namespace);

    boolean containsNamespaceUri(URI namespaceKey);
    
	boolean containsNamespaceOrDefault(URI namespaceKey);
	
	URI getRedirectOrProxy();

	void setRedirectOrProxy(URI redirectOrProxy);

	boolean needsRedirect();

	boolean needsProxy();

	URI getEndpointMethod();

	void setEndpointMethod(URI endpointMethod);

	String getAssumedContentType();
	
	void setAssumedContentType(String assumedContentType);
}

