package org.queryall;

import java.util.Collection;

import org.openrdf.model.URI;

public abstract class Provider implements BaseQueryAllInterface, Comparable<Provider>
{
    public abstract String getTitle();

    public abstract void setTitle(String title);

    public abstract URI getProfileIncludeExcludeOrder();
    
    public abstract void setProfileIncludeExcludeOrder(URI profileIncludeExcludeOrder);
    
    public abstract boolean getIsDefaultSource();

    public abstract void setIsDefaultSource(boolean isDefaultSource);

    public abstract Collection<URI> getNormalisationUris();
    
    /**
     * Deletes all of the current normalisation rules and adds all of the normalisations in the given collection
     * 
     * @param rdfNormalisationsNeeded A collection of URIs designating normalisations that are relevant to this Provider
     */
    public abstract void setNormalisationUris(Collection<URI> rdfNormalisationsNeeded);

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
    
    public boolean containsNamespaceOrDefault(URI namespaceKey)
    {
        return containsNamespaceUri(namespaceKey) || getIsDefaultSource();
    }

    // TODO XXX FIXME: migrate the following methods to separate interfaces based on the type of provider they are relevant to 

    public abstract boolean isHttpGetUrl();
    
    public abstract boolean needsRedirect();
    
    public abstract boolean hasEndpointUrl();
    
    public abstract Collection<String> getEndpointUrls();
    
    public abstract void setEndpointUrls(Collection<String> endpointUrls);
    
    public abstract URI getEndpointMethod();

    public abstract void setEndpointMethod(URI endpointMethod);
        
    public abstract String getSparqlGraphUri();

    public abstract void setSparqlGraphUri(String sparqlGraphUri);

    public abstract boolean getUseSparqlGraph();

    public abstract void setUseSparqlGraph(boolean useSparqlGraph);

    public abstract URI getRedirectOrProxy();

    public abstract void setRedirectOrProxy(URI redirectOrProxy);
    
    public abstract String getAcceptHeaderString();
    
    public abstract void setAcceptHeaderString(String acceptHeaderString);
}

