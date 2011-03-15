package org.queryall;

import java.util.Collection;

public abstract class Profile implements BaseQueryAllInterface, Comparable<Profile>
{
    public abstract int usedWithProfilable(ProfilableInterface profilableObject);
    
    public abstract int getOrder();
    
    public abstract void setOrder(int order);

    public abstract org.openrdf.model.URI getDefaultProfileIncludeExcludeOrder();
    
    public abstract void setDefaultProfileIncludeExcludeOrder(org.openrdf.model.URI defaultProfileIncludeExcludeOrder);

    public abstract void setAllowImplicitQueryTypeInclusions(boolean allowImplicitQueryInclusions);

    public abstract boolean getAllowImplicitQueryTypeInclusions();


    public abstract void setAllowImplicitProviderInclusions(boolean allowImplicitProviderInclusions);

    public abstract boolean getAllowImplicitProviderInclusions();

    public abstract void setAllowImplicitRdfRuleInclusions(boolean allowImplicitRdfRuleInclusions);

    public abstract boolean getAllowImplicitRdfRuleInclusions();
    
    public abstract void setProfileAdministrators(Collection<org.openrdf.model.URI> profileAdministrators);
    
    public abstract Collection<org.openrdf.model.URI> getProfileAdministrators();
    
    
    public abstract void addIncludeProvider(org.openrdf.model.URI includeProvider);

    public abstract void setIncludeProviders(Collection<org.openrdf.model.URI> includeProviders);
    
    public abstract Collection<org.openrdf.model.URI> getIncludeProviders();
    

    public abstract void addExcludeProvider(org.openrdf.model.URI excludeProvider);

    public abstract void setExcludeProviders(Collection<org.openrdf.model.URI> excludeProviders);
    
    public abstract Collection<org.openrdf.model.URI> getExcludeProviders();
    

    public abstract void addIncludeQueryType(org.openrdf.model.URI includeQuery);

    public abstract void setIncludeQueryTypes(Collection<org.openrdf.model.URI> includeQueries);
    
    public abstract Collection<org.openrdf.model.URI> getIncludeQueryTypes();
    

    public abstract void addExcludeQueryType(org.openrdf.model.URI excludeQuery);

    public abstract void setExcludeQueryTypes(Collection<org.openrdf.model.URI> excludeQueries);
    
    public abstract Collection<org.openrdf.model.URI> getExcludeQueryTypes();


    public abstract void addIncludeRdfRule(org.openrdf.model.URI includeRdfRule);

    public abstract void setIncludeRdfRules(Collection<org.openrdf.model.URI> includeRdfRules);
    
    public abstract Collection<org.openrdf.model.URI> getIncludeRdfRules();
    

    public abstract void addExcludeRdfRule(org.openrdf.model.URI excludeRdfRule);

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
