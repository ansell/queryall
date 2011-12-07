/**
 * 
 */
package org.queryall.utils.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.openrdf.model.URI;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.Provider;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.ruletest.RuleTest;

/**
 * This is a dummy class used for testing, it does not perform the actual operations needed
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class DummySettings implements QueryAllConfiguration
{
    
    private Map<URI, NamespaceEntry> namespaceEntries = new ConcurrentHashMap<URI, NamespaceEntry>();
    private Map<URI, NormalisationRule> normalisationRules = new ConcurrentHashMap<URI, NormalisationRule>();
    private Map<URI, Profile> profiles = new ConcurrentHashMap<URI, Profile>();
    private Map<URI, Provider> providers = new ConcurrentHashMap<URI, Provider>();
    private Map<URI, QueryType> queryTypes = new ConcurrentHashMap<URI, QueryType>();
    private Map<URI, RuleTest> ruleTests = new ConcurrentHashMap<URI, RuleTest>();

    /**
     * 
     */
    public DummySettings()
    {
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void addNamespaceEntry(final NamespaceEntry nextNamespaceEntry)
    {
        this.namespaceEntries.put(nextNamespaceEntry.getKey(), nextNamespaceEntry);
    }
    
    /**
     * 
     * @see
     * org.queryall.api.base.QueryAllConfiguration#addNormalisationRule(org.queryall.api.rdfrule
     * .NormalisationRule)
     */
    @Override
    public void addNormalisationRule(final NormalisationRule nextNormalisationRule)
    {
        this.normalisationRules.put(nextNormalisationRule.getKey(), nextNormalisationRule);
    }
    
    /**
     * 
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#addProfile(org.queryall.api.profile.Profile)
     */
    @Override
    public void addProfile(final Profile nextProfile)
    {
        this.profiles.put(nextProfile.getKey(), nextProfile);
    }
    
    /**
     * 
     * 
     * @see
     * org.queryall.api.base.QueryAllConfiguration#addProvider(org.queryall.api.provider.Provider)
     */
    @Override
    public void addProvider(final Provider nextProvider)
    {
        this.providers.put(nextProvider.getKey(), nextProvider);
    }
    
    /**
     *
     * 
     * @see
     * org.queryall.api.base.QueryAllConfiguration#addQueryType(org.queryall.api.querytype.QueryType
     * )
     */
    @Override
    public void addQueryType(final QueryType nextQueryType)
    {
        this.queryTypes.put(nextQueryType.getKey(), nextQueryType);
    }
    
    /**
     * 
     * 
     * @see
     * org.queryall.api.base.QueryAllConfiguration#addRuleTest(org.queryall.api.ruletest.RuleTest)
     */
    @Override
    public void addRuleTest(final RuleTest nextRuleTest)
    {
        this.ruleTests.put(nextRuleTest.getKey(), nextRuleTest);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getAllNamespaceEntries()
     */
    @Override
    public Map<URI, NamespaceEntry> getAllNamespaceEntries()
    {
        return Collections.unmodifiableMap(namespaceEntries);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getAllNormalisationRules()
     */
    @Override
    public Map<URI, NormalisationRule> getAllNormalisationRules()
    {
        return Collections.unmodifiableMap(normalisationRules);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getAllProfiles()
     */
    @Override
    public Map<URI, Profile> getAllProfiles()
    {
        return Collections.unmodifiableMap(profiles);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getAllProviders()
     */
    @Override
    public Map<URI, Provider> getAllProviders()
    {
        return Collections.unmodifiableMap(providers);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getAllQueryTypes()
     */
    @Override
    public Map<URI, QueryType> getAllQueryTypes()
    {
        return Collections.unmodifiableMap(queryTypes);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getAllRuleTests()
     */
    @Override
    public Map<URI, RuleTest> getAllRuleTests()
    {
        return Collections.unmodifiableMap(ruleTests);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getBooleanProperty(java.lang.String,
     * boolean)
     */
    @Override
    public boolean getBooleanProperty(final String propertyKey, final boolean defaultValue)
    {
        return defaultValue;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getDefaultHostAddress()
     */
    @Override
    public String getDefaultHostAddress()
    {
        return "";
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getFloatProperty(java.lang.String, float)
     */
    @Override
    public float getFloatProperty(final String key, final float defaultValue)
    {
        return defaultValue;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getIntProperty(java.lang.String, int)
     */
    @Override
    public int getIntProperty(final String key, final int defaultValue)
    {
        return defaultValue;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getLongProperty(java.lang.String, long)
     */
    @Override
    public long getLongProperty(final String key, final long defaultValue)
    {
        return defaultValue;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getNamespaceEntry(org.openrdf.model.URI)
     */
    @Override
    public NamespaceEntry getNamespaceEntry(final URI nextNamespaceEntryUri)
    {
        return namespaceEntries.get(nextNamespaceEntryUri);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getNamespacePrefixesToUris()
     */
    @Override
    public Map<String, Collection<URI>> getNamespacePrefixesToUris()
    {
        return Collections.emptyMap();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getNormalisationRule(org.openrdf.model.URI)
     */
    @Override
    public NormalisationRule getNormalisationRule(final URI nextNormalisationRuleUri)
    {
        return normalisationRules.get(nextNormalisationRuleUri);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getPlainNamespaceAndIdentifierPattern()
     */
    @Override
    public Pattern getPlainNamespaceAndIdentifierPattern()
    {
        return Pattern.compile(".*");
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getPlainNamespacePattern()
     */
    @Override
    public Pattern getPlainNamespacePattern()
    {
        return Pattern.compile(".*");
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getProfile(org.openrdf.model.URI)
     */
    @Override
    public Profile getProfile(final URI nextProfileUri)
    {
        return profiles.get(nextProfileUri);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getProvider(org.openrdf.model.URI)
     */
    @Override
    public Provider getProvider(final URI nextProviderUri)
    {
        return providers.get(nextProviderUri);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getQueryType(org.openrdf.model.URI)
     */
    @Override
    public QueryType getQueryType(final URI nextQueryTypeUri)
    {
        return queryTypes.get(nextQueryTypeUri);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getRuleTest(org.openrdf.model.URI)
     */
    @Override
    public RuleTest getRuleTest(final URI nextRuleTestUri)
    {
        return ruleTests.get(nextRuleTestUri);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getSeparator()
     */
    @Override
    public String getSeparator()
    {
        return ":";
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getStringProperties(java.lang.String)
     */
    @Override
    public Collection<String> getStringProperties(final String string)
    {
        return new ArrayList<String>(0);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getStringProperty(java.lang.String,
     * java.lang.String)
     */
    @Override
    public String getStringProperty(final String key, final String defaultValue)
    {
        return defaultValue;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getTagPattern()
     */
    @Override
    public Pattern getTagPattern()
    {
        return Pattern.compile(".*");
    }
    
    @Override
    public Collection<URI> getURIProperties(final String string)
    {
        return new ArrayList<URI>(0);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getURIProperty(java.lang.String,
     * org.openrdf.model.URI)
     */
    @Override
    public URI getURIProperty(final String key, final URI defaultValue)
    {
        // TODO Auto-generated method stub
        return defaultValue;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#setProperty(java.lang.String, boolean)
     */
    @Override
    public void setProperty(final String propertyKey, final boolean propertyValue)
    {
        // TODO Auto-generated method stub
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#setProperty(java.lang.String, float)
     */
    @Override
    public void setProperty(final String propertyKey, final float propertyValue)
    {
        // TODO Auto-generated method stub
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#setProperty(java.lang.String, int)
     */
    @Override
    public void setProperty(final String propertyKey, final int propertyValue)
    {
        // TODO Auto-generated method stub
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#setProperty(java.lang.String, long)
     */
    @Override
    public void setProperty(final String propertyKey, final long propertyValue)
    {
        // TODO Auto-generated method stub
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#setProperty(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void setProperty(final String propertyKey, final String propertyValue)
    {
        // TODO Auto-generated method stub
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#setProperty(java.lang.String,
     * org.openrdf.model.URI)
     */
    @Override
    public void setProperty(final String propertyKey, final URI propertyValue)
    {
        // TODO Auto-generated method stub
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.queryall.api.base.QueryAllConfiguration#setStringCollectionProperty(java.lang.String,
     * java.util.Collection)
     */
    @Override
    public void setStringCollectionProperty(final String propertyKey, final Collection<String> propertyValues)
    {
        // TODO Auto-generated method stub
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#setURICollectionProperty(java.lang.String,
     * java.util.Collection)
     */
    @Override
    public void setURICollectionProperty(final String propertyKey, final Collection<URI> propertyValues)
    {
        // TODO Auto-generated method stub
        
    }
    
}
