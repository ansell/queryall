package org.queryall;

import java.util.Collection;
import org.openrdf.model.URI;

public abstract class Provider implements BaseQueryAllInterface, Comparable<Provider>
{
    public abstract boolean isHttpGetUrl();
    
    public abstract boolean needsRedirect();
    
    public abstract boolean hasEndpointUrl();
    
    public abstract URI getProfileIncludeExcludeOrder();
    
    public abstract void setProfileIncludeExcludeOrder(URI profileIncludeExcludeOrder);
    
    public abstract Collection<String> getEndpointUrls();
    
    public abstract void setEndpointUrls(Collection<String> endpointUrls);
    
    public abstract URI getEndpointMethod();

    public abstract void setEndpointMethod(URI endpointMethod);
    
    public abstract boolean getIsDefaultSource();

    public abstract void setIsDefaultSource(boolean isDefaultSource);

    public abstract boolean handlesQueryExplicitly(URI queryKey);

    public abstract Collection<URI> getNormalisationsNeeded();
    
    public abstract void setNormalisationsNeeded(Collection<URI> rdfNormalisationsNeeded);
    
    public abstract Collection<URI> getIncludedInCustomQueries();
    
    public abstract void setIncludedInCustomQueries(Collection<URI> includedInCustomQueries);
    
    public abstract Collection<URI> getNamespaces();
    
    public abstract void setNamespaces(Collection<URI> namespaces);
    
    public abstract boolean containsNamespaceUri(URI namespaceKey);
    
    public abstract String getSparqlGraphUri();

    public abstract void setSparqlGraphUri(String sparqlGraphUri);

    public abstract boolean getUseSparqlGraph();

    public abstract void setUseSparqlGraph(boolean useSparqlGraph);

    public abstract URI getRedirectOrProxy();

    public abstract void setRedirectOrProxy(URI redirectOrProxy);
    
    public abstract String getTitle();

    public abstract void setTitle(String title);

    public abstract String getAcceptHeaderString();
    
    public abstract void setAcceptHeaderString(String acceptHeaderString);
}

