package org.queryall.api;

import java.util.Collection;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface Profile extends BaseQueryAllInterface, Comparable<Profile>
{
    int getOrder();
    
    void setOrder(int order);

    org.openrdf.model.URI getDefaultProfileIncludeExcludeOrder();
    
    void setDefaultProfileIncludeExcludeOrder(org.openrdf.model.URI defaultProfileIncludeExcludeOrder);

    void setAllowImplicitQueryTypeInclusions(boolean allowImplicitQueryInclusions);

    boolean getAllowImplicitQueryTypeInclusions();

    void setAllowImplicitProviderInclusions(boolean allowImplicitProviderInclusions);

    boolean getAllowImplicitProviderInclusions();

    void setAllowImplicitRdfRuleInclusions(boolean allowImplicitRdfRuleInclusions);

    boolean getAllowImplicitRdfRuleInclusions();
    
    void addProfileAdministrator(org.openrdf.model.URI profileAdministrator);
    
    Collection<org.openrdf.model.URI> getProfileAdministrators();
    
    
    void addIncludeProvider(org.openrdf.model.URI includeProvider);

    Collection<org.openrdf.model.URI> getIncludeProviders();
    

    void addExcludeProvider(org.openrdf.model.URI excludeProvider);

    Collection<org.openrdf.model.URI> getExcludeProviders();
    

    void addIncludeQueryType(org.openrdf.model.URI includeQuery);

    Collection<org.openrdf.model.URI> getIncludeQueryTypes();
    

    void addExcludeQueryType(org.openrdf.model.URI excludeQuery);

    Collection<org.openrdf.model.URI> getExcludeQueryTypes();


    void addIncludeRdfRule(org.openrdf.model.URI includeRdfRule);

    Collection<org.openrdf.model.URI> getIncludeRdfRules();
    

    void addExcludeRdfRule(org.openrdf.model.URI excludeRdfRule);

    Collection<org.openrdf.model.URI> getExcludeRdfRules();
}
