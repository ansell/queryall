package org.queryall;

import org.openrdf.model.URI;
import java.util.Collection;

public abstract class Profile implements BaseQueryAllInterface, Comparable<Profile>
{
    public abstract int usedWithProvider(URI provider, URI profileIncludeExcludeOrder);

    public abstract int usedWithQuery(URI query, URI profileIncludeExcludeOrder);

    public abstract int usedWithRdfRule(URI rdfRule, URI profileIncludeExcludeOrder);
    
    public abstract int getOrder();
    
    public abstract void setOrder(int order);

    public abstract String getTitle();
    
    public abstract void setTitle(String title);
    
    public abstract URI getDefaultProfileIncludeExcludeOrder();
    
    public abstract void setDefaultProfileIncludeExcludeOrder(URI defaultProfileIncludeExcludeOrder);

    public abstract void setAllowImplicitQueryInclusions(boolean allowImplicitQueryInclusions);

    public abstract boolean getAllowImplicitQueryInclusions();


    public abstract void setAllowImplicitProviderInclusions(boolean allowImplicitProviderInclusions);

    public abstract boolean getAllowImplicitProviderInclusions();

    public abstract void setAllowImplicitRdfRuleInclusions(boolean allowImplicitRdfRuleInclusions);

    public abstract boolean getAllowImplicitRdfRuleInclusions();
    
    public abstract void setProfileAdministrators(Collection<URI> profileAdministrators);
    
    public abstract Collection<URI> getProfileAdministrators();
    
    public abstract void setIncludeProviders(Collection<URI> includeProviders);
    
    public abstract Collection<URI> getIncludeProviders();
    
    public abstract void setExcludeProviders(Collection<URI> excludeProviders);
    
    public abstract Collection<URI> getExcludeProviders();
    
    public abstract void setIncludeQueries(Collection<URI> includeQueries);
    
    public abstract Collection<URI> getIncludeQueries();
    
    public abstract void setExcludeQueries(Collection<URI> excludeQueries);
    
    public abstract Collection<URI> getExcludeQueries();

    public abstract void setIncludeRdfRules(Collection<URI> includeRdfRules);
    
    public abstract Collection<URI> getIncludeRdfRules();
    
    public abstract void setExcludeRdfRules(Collection<URI> excludeRdfRules);
    
    public abstract Collection<URI> getExcludeRdfRules();
    
}
