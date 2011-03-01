package org.queryall;

import java.util.Collection;
import org.openrdf.model.URI;

public abstract class Provider implements BaseQueryAllInterface, Comparable<Provider>
{
    public abstract boolean isHttpGetUrl();
    
    public abstract boolean needsRedirect();
    
    public abstract boolean hasEndpointUrl();
    
    public abstract org.openrdf.model.URI getProfileIncludeExcludeOrder();
    
    public abstract void setProfileIncludeExcludeOrder(org.openrdf.model.URI profileIncludeExcludeOrder);
    
    public abstract Collection<String> getEndpointUrls();
    
    public abstract void setEndpointUrls(Collection<String> endpointUrls);
    
    public abstract org.openrdf.model.URI getEndpointMethod();

    public abstract void setEndpointMethod(org.openrdf.model.URI endpointMethod);
    
    public abstract boolean getIsDefaultSource();

    public abstract void setIsDefaultSource(boolean isDefaultSource);

    public abstract boolean handlesQueryExplicitly(org.openrdf.model.URI queryKey);

    public abstract Collection<org.openrdf.model.URI> getNormalisationsNeeded();
    
    public abstract void setNormalisationsNeeded(Collection<org.openrdf.model.URI> rdfNormalisationsNeeded);
    
    public abstract Collection<org.openrdf.model.URI> getIncludedInCustomQueries();
    
    public abstract void setIncludedInCustomQueries(Collection<org.openrdf.model.URI> includedInCustomQueries);
    
    public abstract Collection<org.openrdf.model.URI> getNamespaces();
    
    public abstract void setNamespaces(Collection<org.openrdf.model.URI> namespaces);
    
    public abstract boolean containsNamespaceUri(org.openrdf.model.URI namespaceKey);
    
    public abstract String getSparqlGraphUri();

    public abstract void setSparqlGraphUri(String sparqlGraphUri);

    public abstract boolean getUseSparqlGraph();

    public abstract void setUseSparqlGraph(boolean useSparqlGraph);

    public abstract org.openrdf.model.URI getRedirectOrProxy();

    public abstract void setRedirectOrProxy(org.openrdf.model.URI redirectOrProxy);
    
    public abstract String getTitle();

    public abstract void setTitle(String title);

    public abstract String getAcceptHeaderString();
    
    public abstract void setAcceptHeaderString(String acceptHeaderString);
}

