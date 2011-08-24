package org.queryall.api.profile;

import java.util.Collection;

import org.queryall.api.base.BaseQueryAllInterface;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface Profile extends BaseQueryAllInterface, Comparable<Profile>
{
    void addExcludeProvider(org.openrdf.model.URI excludeProvider);
    
    void addExcludeQueryType(org.openrdf.model.URI excludeQuery);
    
    void addExcludeRdfRule(org.openrdf.model.URI excludeRdfRule);
    
    void addIncludeProvider(org.openrdf.model.URI includeProvider);
    
    void addIncludeQueryType(org.openrdf.model.URI includeQuery);
    
    void addIncludeRdfRule(org.openrdf.model.URI includeRdfRule);
    
    void addProfileAdministrator(org.openrdf.model.URI profileAdministrator);
    
    boolean getAllowImplicitProviderInclusions();
    
    boolean getAllowImplicitQueryTypeInclusions();
    
    boolean getAllowImplicitRdfRuleInclusions();
    
    org.openrdf.model.URI getDefaultProfileIncludeExcludeOrder();
    
    Collection<org.openrdf.model.URI> getExcludeProviders();
    
    Collection<org.openrdf.model.URI> getExcludeQueryTypes();
    
    Collection<org.openrdf.model.URI> getExcludeRdfRules();
    
    Collection<org.openrdf.model.URI> getIncludeProviders();
    
    Collection<org.openrdf.model.URI> getIncludeQueryTypes();
    
    Collection<org.openrdf.model.URI> getIncludeRdfRules();
    
    int getOrder();
    
    Collection<org.openrdf.model.URI> getProfileAdministrators();
    
    void setAllowImplicitProviderInclusions(boolean allowImplicitProviderInclusions);
    
    void setAllowImplicitQueryTypeInclusions(boolean allowImplicitQueryInclusions);
    
    void setAllowImplicitRdfRuleInclusions(boolean allowImplicitRdfRuleInclusions);
    
    void setDefaultProfileIncludeExcludeOrder(org.openrdf.model.URI defaultProfileIncludeExcludeOrder);
    
    void setOrder(int order);
}
