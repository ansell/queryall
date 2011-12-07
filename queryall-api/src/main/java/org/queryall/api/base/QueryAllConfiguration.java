package org.queryall.api.base;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

import org.openrdf.model.URI;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.Provider;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.ruletest.RuleTest;

/**
 * The QueryAllConfiguration object provides access to all of the properties and objects that are
 * managed by the current application.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface QueryAllConfiguration
{
    void addNamespaceEntry(NamespaceEntry nextNamespaceEntry);
    
    void addNormalisationRule(NormalisationRule nextNormalisationRule);
    
    void addProfile(Profile nextProfile);
    
    void addProvider(Provider nextProvider);
    
    void addQueryType(QueryType nextQueryType);
    
    void addRuleTest(RuleTest nextRuleTest);
    
    Map<URI, NamespaceEntry> getAllNamespaceEntries();
    
    NamespaceEntry getNamespaceEntry(URI nextNamespaceEntryUri);
    
    Map<URI, NormalisationRule> getAllNormalisationRules();
    
    NormalisationRule getNormalisationRule(URI nextNormalisationRuleUri);
    
    Map<URI, Profile> getAllProfiles();
    
    Profile getProfile(URI nextProfileUri);
    
    Map<URI, Provider> getAllProviders();
    
    Provider getProvider(URI nextProviderUri);
    
    Map<URI, QueryType> getAllQueryTypes();
    
    QueryType getQueryType(URI nextQueryTypeUri);
    
    Map<URI, RuleTest> getAllRuleTests();
    
    RuleTest getRuleTest(URI nextRuleTestUri);
    
    boolean getBooleanProperty(String propertyKey, boolean defaultValue);
    
    String getDefaultHostAddress();
    
    float getFloatProperty(String key, float defaultValue);
    
    int getIntProperty(String key, int defaultValue);
    
    long getLongProperty(String key, long defaultValue);
    
    Map<String, Collection<URI>> getNamespacePrefixesToUris();
    
    Pattern getPlainNamespaceAndIdentifierPattern();
    
    Pattern getPlainNamespacePattern();
    
    String getSeparator();
    
    Collection<String> getStringProperties(String string);
    
    String getStringProperty(String key, String defaultValue);
    
    Pattern getTagPattern();
    
    Collection<URI> getURIProperties(String string);
    
    URI getURIProperty(String key, URI defaultValue);
    
    void setProperty(String propertyKey, boolean propertyValue);
    
    void setProperty(String propertyKey, float propertyValue);
    
    void setProperty(String propertyKey, int propertyValue);
    
    void setProperty(String propertyKey, long propertyValue);
    
    void setProperty(String propertyKey, String propertyValue);
    
    void setProperty(String propertyKey, URI propertyValue);
    
    void setStringCollectionProperty(String propertyKey, Collection<String> propertyValues);
    
    void setURICollectionProperty(String propertyKey, Collection<URI> propertyValues);
    
}