package org.queryall.api;

import java.util.Collection;

import org.openrdf.model.URI;

public interface Provider extends BaseQueryAllInterface, Comparable<Provider>, ProfilableInterface
{
    public abstract boolean getIsDefaultSource();

    public abstract void setIsDefaultSource(boolean isDefaultSource);

    public abstract Collection<URI> getNormalisationUris();
    
    /**
     * Adds the normalisation to this current collection of normalisations
     * 
     * @param rdfNormalisationNeeded
     */
    public abstract void addNormalisationUri(URI rdfNormalisationNeeded);
    
    public abstract boolean containsNormalisationUri(URI normalisationKey);

    public abstract Collection<URI> getIncludedInQueryTypes();
    
    public abstract void setIncludedInQueryTypes(Collection<URI> includedInQueryTypes);
    
    public abstract void addIncludedInQueryType(URI includedInQueryType);

    public abstract boolean containsQueryTypeUri(URI queryKey);

    public abstract Collection<URI> getNamespaces();
    
    public abstract void setNamespaces(Collection<URI> namespaces);
    
    public abstract void addNamespace(URI namespace);

    public abstract boolean containsNamespaceUri(URI namespaceKey);
    
	public abstract boolean containsNamespaceOrDefault(URI namespaceKey);
	
	public abstract URI getRedirectOrProxy();

	public abstract void setRedirectOrProxy(URI redirectOrProxy);

	public abstract boolean needsRedirect();

	public abstract boolean needsProxy();

	public abstract URI getEndpointMethod();

	public abstract void setEndpointMethod(URI endpointMethod);

	public abstract String getAssumedContentType();
	
	public abstract void setAssumedContentType(String assumedContentType);

}

