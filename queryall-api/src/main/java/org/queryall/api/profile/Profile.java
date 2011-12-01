package org.queryall.api.profile;

import java.util.Collection;

import org.queryall.api.base.BaseQueryAllInterface;

/**
 * Profiles define which query types, providers, and normalisation rules, are acceptable. They can
 * be layered, and shared, independent of the objects they reference.
 * 
 * Objects that are influenced by Profiles should implement the
 * org.queryall.api.base.ProfilableInterface interface.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface Profile extends BaseQueryAllInterface, Comparable<Profile>
{
    /**
     * 
     * @param excludeProvider
     *            The URI of a provider that is explicitly excluded by this profile.
     */
    void addExcludeProvider(org.openrdf.model.URI excludeProvider);
    
    /**
     * 
     * @return True if the collection was reset and false otherwise.
     */
    boolean resetExcludedProviders();
    
    /**
     * 
     * @param excludeQuery
     *            The URI of a query type that is explicitly excluded by this profile.
     */
    void addExcludeQueryType(org.openrdf.model.URI excludeQuery);
    
    /**
     * 
     * @return True if the collection was reset and false otherwise.
     */
    boolean resetExcludedQueryTypes();
    
    /**
     * 
     * @param excludeRdfRule
     *            The URI of a normalisation rule that is explicitly excluded by this profile.
     */
    void addExcludeRdfRule(org.openrdf.model.URI excludeRdfRule);
    
    /**
     * 
     * @return True if the collection was reset and false otherwise.
     */
    boolean resetExcludedRdfRules();
    
    /**
     * 
     * @param includeProvider
     *            The URI of a provider that is explicitly included by this profile.
     */
    void addIncludeProvider(org.openrdf.model.URI includeProvider);
    
    /**
     * 
     * @return True if the collection was reset and false otherwise.
     */
    boolean resetIncludedProviders();
    
    /**
     * 
     * @param includeQuery
     *            The URI of a query type that is explicitly included by this profile.
     */
    void addIncludeQueryType(org.openrdf.model.URI includeQuery);
    
    /**
     * 
     * @return True if the collection was reset and false otherwise.
     */
    boolean resetIncludedQueryTypes();
    
    /**
     * 
     * @param includeRdfRule
     *            The URI of a normalisation rule that is explicitly included by this profile.
     */
    void addIncludeRdfRule(org.openrdf.model.URI includeRdfRule);
    
    /**
     * 
     * @return True if the collection was reset and false otherwise.
     */
    boolean resetIncludedRdfRules();
    
    /**
     * 
     * @param profileAdministrator
     *            A URI denoting an administrator for this profile.
     */
    void addProfileAdministrator(org.openrdf.model.URI profileAdministrator);
    
    /**
     * 
     * @return True if the collection was reset and false otherwise.
     */
    boolean resetProfileAdministrators();
    
    /**
     * 
     * @return True if this profile is set to allow implicit provider inclusions and false if this
     *         profile should be ignored if a provider is implicitly includable.
     */
    boolean getAllowImplicitProviderInclusions();
    
    /**
     * 
     * @return True if this profile is set to allow implicit query type inclusions and false if this
     *         profile should be ignored if a query type is implicitly includable.
     */
    boolean getAllowImplicitQueryTypeInclusions();
    
    /**
     * 
     * @return True if this profile is set to allow implicit normalisation rule inclusions and false
     *         if this profile should be ignored if a normalisation rule is implicitly includable.
     */
    boolean getAllowImplicitRdfRuleInclusions();
    
    /**
     * 
     * @return A URI indicating what the default behaviour should be when processing items that do
     *         not explicitly defing their include or exclude behaviour.
     */
    org.openrdf.model.URI getDefaultProfileIncludeExcludeOrder();
    
    /**
     * 
     * @return A collection of providers that are explicitly excluded by this profile.
     */
    Collection<org.openrdf.model.URI> getExcludeProviders();
    
    /**
     * 
     * @return A collection of query types that are explicitly excluded by this profile.
     */
    Collection<org.openrdf.model.URI> getExcludeQueryTypes();
    
    /**
     * 
     * @return A collection of normalisation rules that are explicitly excluded by this profile.
     */
    Collection<org.openrdf.model.URI> getExcludeRdfRules();
    
    /**
     * 
     * @return A collection of providers that are explicitly included by this profile.
     */
    Collection<org.openrdf.model.URI> getIncludeProviders();
    
    /**
     * 
     * @return A collection of query types that are explicitly included by this profile.
     */
    Collection<org.openrdf.model.URI> getIncludeQueryTypes();
    
    /**
     * 
     * @return A collection of normalisation rules that are explicitly included by this profile.
     */
    Collection<org.openrdf.model.URI> getIncludeRdfRules();
    
    /**
     * 
     * @return An integer indicating the order that this profile should be processed in. Profiles
     *         are processed from Low orders to High orders until an explicit or acceptable implicit
     *         include or exclude is found, or all of the profiles do not match.
     */
    int getOrder();
    
    /**
     * 
     * @return A collection of URIs denoting the administrators of this profile
     */
    Collection<org.openrdf.model.URI> getProfileAdministrators();
    
    /**
     * 
     * @param allowImplicitProviderInclusions
     *            True if this profile allows providers to be implicitly included in cases where
     *            they are not otherwise excluded or explicitly included.
     */
    void setAllowImplicitProviderInclusions(boolean allowImplicitProviderInclusions);
    
    /**
     * 
     * @param allowImplicitQueryInclusions
     *            True if this profile allows query types to be implicitly included in cases where
     *            they are not otherwise excluded or explicitly included.
     */
    void setAllowImplicitQueryTypeInclusions(boolean allowImplicitQueryInclusions);
    
    /**
     * 
     * @param allowImplicitRdfRuleInclusions
     *            True if this profile allows query types to be implicitly included in cases where
     *            they are not otherwise excluded or explicitly included.
     */
    void setAllowImplicitRdfRuleInclusions(boolean allowImplicitRdfRuleInclusions);
    
    /**
     * 
     * @param defaultProfileIncludeExcludeOrder
     *            A URI indicating what the default include or exclude behaviour should be for
     *            providers that do not define their own behaviour.
     */
    void setDefaultProfileIncludeExcludeOrder(org.openrdf.model.URI defaultProfileIncludeExcludeOrder);
    
    /**
     * 
     * @param order
     *            An integer indicating the order that this profile should be processed in. Profiles
     *            are processed from Low orders to High orders until an explicit or acceptable
     *            implicit include or exclude is found, or all of the profiles do not match.
     */
    void setOrder(int order);
}
