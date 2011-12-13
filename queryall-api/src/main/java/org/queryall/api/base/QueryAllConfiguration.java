package org.queryall.api.base;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.Provider;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.api.utils.WebappConfig;

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
    
    Collection<Object> clearProperty(WebappConfig propertyKey);
    
    Map<URI, NamespaceEntry> getAllNamespaceEntries();
    
    Map<URI, NormalisationRule> getAllNormalisationRules();
    
    Map<URI, Profile> getAllProfiles();
    
    Map<URI, Provider> getAllProviders();
    
    Map<URI, QueryType> getAllQueryTypes();
    
    Map<URI, RuleTest> getAllRuleTests();
    
    boolean getBooleanProperty(WebappConfig propertyKey);
    
    boolean getBooleanProperty(WebappConfig propertyKey, boolean defaultValue);
    
    String getDefaultHostAddress();
    
    float getFloatProperty(WebappConfig key);
    
    float getFloatProperty(WebappConfig key, float defaultValue);
    
    int getIntProperty(WebappConfig key);
    
    int getIntProperty(WebappConfig key, int defaultValue);
    
    long getLongProperty(WebappConfig key);
    
    long getLongProperty(WebappConfig key, long defaultValue);
    
    NamespaceEntry getNamespaceEntry(URI nextNamespaceEntryUri);
    
    Map<String, Collection<URI>> getNamespacePrefixesToUris();
    
    NormalisationRule getNormalisationRule(URI nextNormalisationRuleUri);
    
    Pattern getPlainNamespaceAndIdentifierPattern();
    
    Pattern getPlainNamespacePattern();
    
    Profile getProfile(URI nextProfileUri);
    
    Provider getProvider(URI nextProviderUri);
    
    QueryType getQueryType(URI nextQueryTypeUri);
    
    RuleTest getRuleTest(URI nextRuleTestUri);
    
    String getSeparator();
    
    Collection<String> getStringProperties(WebappConfig key);
    
    String getStringProperty(WebappConfig key);
    
    String getStringProperty(WebappConfig key, String defaultValue);
    
    Pattern getTagPattern();
    
    Collection<URI> getURIProperties(WebappConfig key);
    
    URI getURIProperty(WebappConfig key);
    
    URI getURIProperty(WebappConfig key, URI defaultValue);
    
    void setProperty(WebappConfig propertyKey, boolean propertyValue);
    
    void setProperty(WebappConfig propertyKey, float propertyValue);
    
    void setProperty(WebappConfig propertyKey, int propertyValue);
    
    void setProperty(WebappConfig propertyKey, long propertyValue);
    
    void setProperty(WebappConfig propertyKey, String propertyValue);
    
    void setProperty(WebappConfig propertyKey, URI propertyValue);
    
    void setProperty(WebappConfig propertyKey, Value propertyValue);
    
    void setStringCollectionProperty(WebappConfig propertyKey, Collection<String> propertyValues);
    
    void setURICollectionProperty(WebappConfig propertyKey, Collection<URI> propertyValues);
    
    void setValueCollectionProperty(WebappConfig propertyKey, Collection<Value> propertyValues);
}