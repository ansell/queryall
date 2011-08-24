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
    void addIncludedInQueryType(URI includedInQueryType);
    
    void addNamespace(URI namespace);
    
    /**
     * Adds the normalisation to this current collection of normalisations
     * 
     * @param rdfNormalisationNeeded
     */
    void addNormalisationUri(URI rdfNormalisationNeeded);
    
    boolean containsNamespaceOrDefault(URI namespaceKey);
    
    boolean containsNamespaceUri(URI namespaceKey);
    
    boolean containsNormalisationUri(URI normalisationKey);
    
    boolean containsQueryTypeUri(URI queryKey);
    
    String getAssumedContentType();
    
    URI getEndpointMethod();
    
    Collection<URI> getIncludedInQueryTypes();
    
    boolean getIsDefaultSource();
    
    Collection<URI> getNamespaces();
    
    Collection<URI> getNormalisationUris();
    
    URI getRedirectOrProxy();
    
    boolean needsProxy();
    
    boolean needsRedirect();
    
    void setAssumedContentType(String assumedContentType);
    
    void setEndpointMethod(URI endpointMethod);
    
    void setIncludedInQueryTypes(Collection<URI> includedInQueryTypes);
    
    void setIsDefaultSource(boolean isDefaultSource);
    
    void setNamespaces(Collection<URI> namespaces);
    
    void setRedirectOrProxy(URI redirectOrProxy);
}
