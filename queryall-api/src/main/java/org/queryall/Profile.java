package org.queryall;

import java.util.Collection;

public abstract class Profile implements BaseQueryAllInterface, Comparable<Profile>
{
    public abstract int usedWithProvider(org.openrdf.model.URI provider, org.openrdf.model.URI profileIncludeExcludeOrder);

    public abstract int usedWithQuery(org.openrdf.model.URI query, org.openrdf.model.URI profileIncludeExcludeOrder);

    public abstract int usedWithRdfRule(org.openrdf.model.URI rdfRule, org.openrdf.model.URI profileIncludeExcludeOrder);
    
    public abstract int getOrder();
    
    public abstract void setOrder(int order);

    public abstract org.openrdf.model.URI getDefaultProfileIncludeExcludeOrder();
    
    public abstract void setDefaultProfileIncludeExcludeOrder(org.openrdf.model.URI defaultProfileIncludeExcludeOrder);

    public abstract void setAllowImplicitQueryInclusions(boolean allowImplicitQueryInclusions);

    public abstract boolean getAllowImplicitQueryInclusions();


    public abstract void setAllowImplicitProviderInclusions(boolean allowImplicitProviderInclusions);

    public abstract boolean getAllowImplicitProviderInclusions();

    public abstract void setAllowImplicitRdfRuleInclusions(boolean allowImplicitRdfRuleInclusions);

    public abstract boolean getAllowImplicitRdfRuleInclusions();
    
    public abstract void setProfileAdministrators(Collection<org.openrdf.model.URI> profileAdministrators);
    
    public abstract Collection<org.openrdf.model.URI> getProfileAdministrators();
    
    public abstract void setIncludeProviders(Collection<org.openrdf.model.URI> includeProviders);
    
    public abstract Collection<org.openrdf.model.URI> getIncludeProviders();
    
    public abstract void setExcludeProviders(Collection<org.openrdf.model.URI> excludeProviders);
    
    public abstract Collection<org.openrdf.model.URI> getExcludeProviders();
    
    public abstract void setIncludeQueries(Collection<org.openrdf.model.URI> includeQueries);
    
    public abstract Collection<org.openrdf.model.URI> getIncludeQueries();
    
    public abstract void setExcludeQueries(Collection<org.openrdf.model.URI> excludeQueries);
    
    public abstract Collection<org.openrdf.model.URI> getExcludeQueries();

    public abstract void setIncludeRdfRules(Collection<org.openrdf.model.URI> includeRdfRules);
    
    public abstract Collection<org.openrdf.model.URI> getIncludeRdfRules();
    
    public abstract void setExcludeRdfRules(Collection<org.openrdf.model.URI> excludeRdfRules);
    
    public abstract Collection<org.openrdf.model.URI> getExcludeRdfRules();
    
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        
        result.append("title=").append(this.getTitle());
        result.append("key=").append(this.getKey().stringValue());

        return result.toString();
    }
}
