package org.queryall.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.Provider;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.exception.QueryAllRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class used to get access to settings
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class Settings implements QueryAllConfiguration
{
    // Wrap up a singleton instance in its own inner static class
    // Note: this class does not need to be used as a singleton
    private static class SettingsHolder
    {
        public static final QueryAllConfiguration helper = new Settings();
    }
    
    private static final Logger log = LoggerFactory.getLogger(Settings.class);
    private static final boolean _TRACE = Settings.log.isTraceEnabled();
    private static final boolean _DEBUG = Settings.log.isDebugEnabled();
    private static final boolean _INFO = Settings.log.isInfoEnabled();
    
    public static QueryAllConfiguration getSettings()
    {
        return SettingsHolder.helper;
    }
    
    private volatile ConcurrentHashMap<URI, Provider> cachedProviders = new ConcurrentHashMap<URI, Provider>(200);
    private volatile ConcurrentHashMap<URI, NormalisationRule> cachedNormalisationRules =
            new ConcurrentHashMap<URI, NormalisationRule>(200);
    private volatile ConcurrentHashMap<URI, RuleTest> cachedRuleTests = new ConcurrentHashMap<URI, RuleTest>(200);
    private volatile ConcurrentHashMap<URI, QueryType> cachedQueryTypes = new ConcurrentHashMap<URI, QueryType>(200);
    private volatile ConcurrentHashMap<URI, Profile> cachedProfiles = new ConcurrentHashMap<URI, Profile>(200);
    private volatile ConcurrentHashMap<URI, NamespaceEntry> cachedNamespaceEntries =
            new ConcurrentHashMap<URI, NamespaceEntry>(200);
    private volatile ConcurrentHashMap<String, Collection<URI>> cachedNamespacePrefixToUriEntries =
            new ConcurrentHashMap<String, Collection<URI>>(200);
    
    private volatile ConcurrentHashMap<String, Collection<Object>> configPropertiesCache =
            new ConcurrentHashMap<String, Collection<Object>>(200);
    private volatile Pattern cachedTagPattern = null;
    
    private volatile long initialisedTimestamp = System.currentTimeMillis();
    
    private volatile String separator;
    
    public Settings()
    {
    }
    
    @Override
    public void addNamespaceEntry(final NamespaceEntry nextNamespaceEntry)
    {
        this.addNamespaceEntryAndPrefix(nextNamespaceEntry.getKey(), nextNamespaceEntry);
    }
    
    /**
     * Helper method to add a new namespace entry and its prefixes to the internal caches.
     * 
     * @param nextNamespaceEntryUri
     * @param nextNamespaceEntryConfiguration
     */
    private void addNamespaceEntryAndPrefix(final URI nextNamespaceEntryUri,
            final NamespaceEntry nextNamespaceEntryConfiguration)
    {
        // TODO: refactor this code to use the ConcurrentHashMap API
        
        this.cachedNamespaceEntries.put(nextNamespaceEntryUri, nextNamespaceEntryConfiguration);
        
        // cache the preferred prefix
        if(this.cachedNamespacePrefixToUriEntries.containsKey(nextNamespaceEntryConfiguration.getPreferredPrefix()))
        {
            final Collection<URI> currentnamespacePreferredPrefixToUriList =
                    this.cachedNamespacePrefixToUriEntries.get(nextNamespaceEntryConfiguration.getPreferredPrefix());
            if(!currentnamespacePreferredPrefixToUriList.contains(nextNamespaceEntryUri))
            {
                currentnamespacePreferredPrefixToUriList.add(nextNamespaceEntryUri);
            }
        }
        else
        {
            final Collection<URI> newnamespacePreferredPrefixToUriList = new HashSet<URI>();
            newnamespacePreferredPrefixToUriList.add(nextNamespaceEntryConfiguration.getKey());
            this.cachedNamespacePrefixToUriEntries.put(nextNamespaceEntryConfiguration.getPreferredPrefix(),
                    newnamespacePreferredPrefixToUriList);
        }
        
        // then cache any alternative prefixes as well
        if(nextNamespaceEntryConfiguration.getAlternativePrefixes() != null)
        {
            for(final String nextAlternativePrefix : nextNamespaceEntryConfiguration.getAlternativePrefixes())
            {
                if(this.cachedNamespacePrefixToUriEntries.containsKey(nextAlternativePrefix))
                {
                    final Collection<URI> currentNamespacePrefixToUriList =
                            this.cachedNamespacePrefixToUriEntries.get(nextAlternativePrefix);
                    if(!currentNamespacePrefixToUriList.contains(nextNamespaceEntryUri))
                    {
                        currentNamespacePrefixToUriList.add(nextNamespaceEntryUri);
                    }
                }
                else
                {
                    final Collection<URI> newNamespacePrefixToUriList = new HashSet<URI>();
                    newNamespacePrefixToUriList.add(nextNamespaceEntryUri);
                    this.cachedNamespacePrefixToUriEntries.put(nextAlternativePrefix, newNamespacePrefixToUriList);
                }
            }
        }
    }
    
    @Override
    public void addNormalisationRule(final NormalisationRule nextNormalisationRule)
    {
        this.cachedNormalisationRules.put(nextNormalisationRule.getKey(), nextNormalisationRule);
    }
    
    @Override
    public void addProfile(final Profile nextProfile)
    {
        this.cachedProfiles.put(nextProfile.getKey(), nextProfile);
    }
    
    @Override
    public void addProvider(final Provider nextProvider)
    {
        this.cachedProviders.put(nextProvider.getKey(), nextProvider);
    }
    
    @Override
    public void addQueryType(final QueryType nextQueryType)
    {
        this.cachedQueryTypes.put(nextQueryType.getKey(), nextQueryType);
    }
    
    @Override
    public void addRuleTest(final RuleTest nextRuleTest)
    {
        this.cachedRuleTests.put(nextRuleTest.getKey(), nextRuleTest);
    }
    
    /**
     * 
     * @return The Collection of Objects that were set for this property or null if the property was
     *         not set
     */
    @Override
    public Collection<Object> clearProperty(final String propertyKey)
    {
        return this.configPropertiesCache.remove(propertyKey);
    }
    
    @Override
    public Map<URI, NamespaceEntry> getAllNamespaceEntries()
    {
        return this.getAllNamespaceEntries(true);
    }
    
    public Map<URI, NamespaceEntry> getAllNamespaceEntries(final boolean useCache)
    {
        return Collections.unmodifiableMap(this.cachedNamespaceEntries);
    }
    
    @Override
    public Map<URI, NormalisationRule> getAllNormalisationRules()
    {
        return this.getAllNormalisationRules(true);
    }
    
    public synchronized Map<URI, NormalisationRule> getAllNormalisationRules(final boolean useCache)
    {
        return Collections.unmodifiableMap(this.cachedNormalisationRules);
    }
    
    @Override
    public Map<URI, Profile> getAllProfiles()
    {
        return this.getAllProfiles(true);
    }
    
    public Map<URI, Profile> getAllProfiles(final boolean useCache)
    {
        return Collections.unmodifiableMap(this.cachedProfiles);
    }
    
    @Override
    public Map<URI, Provider> getAllProviders()
    {
        return this.getAllProviders(true);
    }
    
    public Map<URI, Provider> getAllProviders(final boolean useCache)
    {
        return Collections.unmodifiableMap(this.cachedProviders);
    }
    
    @Override
    public Map<URI, QueryType> getAllQueryTypes()
    {
        return this.getAllQueryTypes(true);
    }
    
    public Map<URI, QueryType> getAllQueryTypes(final boolean useCache)
    {
        return Collections.unmodifiableMap(this.cachedQueryTypes);
    }
    
    @Override
    public Map<URI, RuleTest> getAllRuleTests()
    {
        return this.getAllRuleTests(true);
    }
    
    public Map<URI, RuleTest> getAllRuleTests(final boolean useCache)
    {
        return Collections.unmodifiableMap(this.cachedRuleTests);
    }
    
    @Override
    public boolean getBooleanProperty(final String key, final boolean defaultValue)
    {
        boolean result = defaultValue;
        
        final Collection<Object> values = this.configPropertiesCache.get(key);
        
        if(values == null || values.size() != 1)
        {
            Settings.log.warn("getBooleanProperty: Did not find a unique result for key=" + key
                    + " values=" + values + " defaultValue=" + defaultValue);
            return defaultValue;
        }
        
        Object nextValue = values.iterator().next();
        
        if(nextValue instanceof String)
        {
            result = Boolean.valueOf((String)values.iterator().next());
        }
        else
        {
            result = (Boolean)nextValue;
        }
        
        
        if(Settings._TRACE)
        {
            Settings.log.trace("getBooleanProperty: key=" + key + " result=" + result);
        }
        
        return result;
    }
    
    /**
     * Defaults to http://bio2rdf.org/ if the configuration files do not contain any of the relevant
     * properties, or some part of that if they only contain some of the relevant properties.
     * 
     * The properties used to generate the result are {uriPrefix}{hostName}{uriSuffix}
     * 
     * @return the Default host address for this configuration
     */
    @Override
    public String getDefaultHostAddress()
    {
        return this.getStringProperty("uriPrefix", "http://") + this.getStringProperty("hostName", "bio2rdf.org")
                + this.getStringProperty("uriSuffix", "/");
    }
    
    @Override
    public float getFloatProperty(final String key, final float defaultValue)
    {
        float result = defaultValue;
        
        final Collection<Object> values = this.configPropertiesCache.get(key);
        
        if(values == null || values.size() != 1)
        {
            Settings.log.warn("getFloatProperty: Did not find a unique result for key=" + key
                    + " values=" + values + " defaultValue=" + defaultValue);
            return defaultValue;
        }
        
        Object nextValue = values.iterator().next();
        
        if(nextValue instanceof String)
        {
            result = Float.valueOf((String)values.iterator().next());
        }
        else
        {
            result = (Float)nextValue;
        }
        
        
        if(Settings._TRACE)
        {
            Settings.log.trace("getFloatProperty: key=" + key + " result=" + result);
        }
        
        return result;
    }
    
    @Override
    public int getIntProperty(final String key, final int defaultValue)
    {
        int result = defaultValue;
        
        final Collection<Object> values = this.configPropertiesCache.get(key);
        
        if(values == null || values.size() != 1)
        {
            Settings.log.warn("getIntProperty: Did not find a unique result for key=" + key
                    + " values=" + values + " defaultValue=" + defaultValue);
            return defaultValue;
        }
        
        Object nextValue = values.iterator().next();
        
        if(nextValue instanceof String)
        {
            result = Integer.valueOf((String)values.iterator().next());
        }
        else
        {
            result = (Integer)nextValue;
        }
        
        
        if(Settings._TRACE)
        {
            Settings.log.trace("getIntProperty: key=" + key + " result=" + result);
        }
        
        return result;
    }
    
    @Override
    public long getLongProperty(final String key, final long defaultValue)
    {
        long result = defaultValue;
        
        final Collection<Object> values = this.configPropertiesCache.get(key);
        
        if(values == null || values.size() != 1)
        {
            Settings.log.warn("getLongProperty: Did not find a unique result for key=" + key
                    + " values=" + values + " defaultValue=" + defaultValue);
            return defaultValue;
        }
        
        Object nextValue = values.iterator().next();
        
        if(nextValue instanceof String)
        {
            result = Long.valueOf((String)values.iterator().next());
        }
        else
        {
            result = (Long)nextValue;
        }
        
        if(Settings._TRACE)
        {
            Settings.log.trace("getLongProperty: key=" + key + " result=" + result);
        }
        
        return result;
    }
    
    @Override
    public NamespaceEntry getNamespaceEntry(final URI nextNamespaceEntryUri)
    {
        return this.cachedNamespaceEntries.get(nextNamespaceEntryUri);
    }
    
    /**
     * @return the cachedNamespacePrefixToUriEntries
     */
    @Override
    public Map<String, Collection<URI>> getNamespacePrefixesToUris()
    {
        return this.cachedNamespacePrefixToUriEntries;
    }
    
    @Override
    public NormalisationRule getNormalisationRule(final URI nextNormalisationRuleUri)
    {
        return this.cachedNormalisationRules.get(nextNormalisationRuleUri);
    }
    
    @Override
    public Pattern getPlainNamespaceAndIdentifierPattern()
    {
        return Pattern.compile(this.getStringProperty("plainNamespaceAndIdentifierRegex", "^([\\w-]+):(.+)$"));
    }
    
    @Override
    public Pattern getPlainNamespacePattern()
    {
        return Pattern.compile(this.getStringProperty("plainNamespaceRegex", "^([\\w-]+)$"));
    }
    
    @Override
    public Profile getProfile(final URI nextProfileUri)
    {
        return this.cachedProfiles.get(nextProfileUri);
    }
    
    @Override
    public Provider getProvider(final URI nextProviderUri)
    {
        return this.cachedProviders.get(nextProviderUri);
    }
    
    @Override
    public QueryType getQueryType(final URI nextQueryTypeUri)
    {
        return this.cachedQueryTypes.get(nextQueryTypeUri);
    }
    
    @Override
    public RuleTest getRuleTest(final URI nextRuleTestUri)
    {
        return this.cachedRuleTests.get(nextRuleTestUri);
    }
    
    /**
     * Helper method to get the default separator, using a non-lookup cache if possible
     */
    @Override
    public String getSeparator()
    {
        if(this.separator == null)
        {
            synchronized(this)
            {
                if(this.separator == null)
                {
                    this.separator = this.getStringProperty("separator", ":");
                }
            }
        }
        
        return this.separator;
    }
    
    @Override
    public Collection<String> getStringProperties(final String key)
    {
        if(Settings._TRACE)
        {
            Settings.log.trace("getStringCollectionPropertiesFromConfig: key=" + key);
        }
        
        final Collection<Object> values = this.configPropertiesCache.get(key);
        
        
        final Collection<String> results = new ArrayList<String>();
        
        if(values != null)
        {
            for(final Object nextValue : values)
            {
                results.add(nextValue.toString());
            }
        }
        
        return results;
    }
    
    @Override
    public String getStringProperty(final String key, final String defaultValue)
    {
        String result = defaultValue;
        
        if(Settings._TRACE)
        {
            Settings.log.trace("getStringPropertyFromConfig: key=" + key + " defaultValue=" + defaultValue);
        }
        
        final Collection<String> values = this.getStringProperties(key);
        
        if(values == null || values.size() != 1)
        {
            Settings.log.error("getStringPropertyFromConfig: Did not find a unique result for key=" + key
                    + " values=" + values + " defaultValue=" + defaultValue);
            return defaultValue;
        }
        
        for(final String nextValue : values)
        {
            result = nextValue;
        }
        
        if(Settings._TRACE)
        {
            Settings.log.trace("getStringPropertyFromConfig: key=" + key + " defaultValue=" + defaultValue
                    + " returning result=" + result);
        }
        
        return result;
    }
    
    @Override
    public Pattern getTagPattern()
    {
        if(this.cachedTagPattern != null)
        {
            return this.cachedTagPattern;
        }
        
        // TODO: Do we need to split this into two patterns so that we can identify the parameter
        // names as separate matching groups from the entire tag with braces etc.?
        final Pattern tempPattern =
                Pattern.compile(this.getStringProperty("tagPatternRegex", ".*(\\$\\{[\\w_-]+\\}).*"));
        
        if(tempPattern != null)
        {
            this.cachedTagPattern = tempPattern;
        }
        
        return tempPattern;
    }
    
    @Override
    public Collection<URI> getURIProperties(final String key)
    {
        if(Settings._TRACE)
        {
            Settings.log.trace("getURICollectionPropertiesFromConfig: key=" + key);
        }
        
        final Collection<Object> cachedObjects = this.configPropertiesCache.get(key);
        
        final Collection<URI> results = new HashSet<URI>();
        
        if(cachedObjects == null)
        {
            return results;
        }
        
        for(final Object nextValue : cachedObjects)
        {
            if(nextValue instanceof URI)
            {
                results.add((URI)nextValue);
            }
            else
            {
                Settings.log.error("getURIProperties: nextValue was not an instance of URI key=" + key
                        + " nextValue=" + nextValue);
            }
        }
        
        return results;
    }
    
    @Override
    public URI getURIProperty(final String key, final URI defaultValue)
    {
        URI result = defaultValue;
        
        if(Settings._TRACE)
        {
            Settings.log.trace("getURIProperty: key=" + key + " defaultValue=" + defaultValue);
        }
        
        final Collection<URI> values = this.getURIProperties(key);
        
        if(values == null || values.size() != 1)
        {
            Settings.log.warn("getURIProperty: Did not find a unique result for key=" + key
                    + " values=" + values + " defaultValue=" + defaultValue);
            return defaultValue;
        }
        
        for(final URI nextValue : values)
        {
            result = nextValue;
        }
        
        if(Settings._TRACE)
        {
            Settings.log.trace("getURIProperty: key=" + key + " result=" + result);
        }
        
        return result;
        
    }
    
    @Deprecated
    public boolean isManualRefreshAllowed()
    {
        final boolean manualRefresh = this.getBooleanProperty("enableManualConfigurationRefresh", true);
        final long timestampDiff = (System.currentTimeMillis() - this.initialisedTimestamp);
        final long manualRefreshMinimum = this.getLongProperty("manualConfigurationMinimumMilliseconds", 60000L);
        
        if(Settings._DEBUG)
        {
            Settings.log.debug("isManualRefreshAllowed: manualRefresh=" + manualRefresh);
            Settings.log.debug("isManualRefreshAllowed: timestampDiff=" + timestampDiff);
            Settings.log.debug("isManualRefreshAllowed: manualRefreshMinimum=" + manualRefreshMinimum);
            
        }
        
        if(manualRefreshMinimum < 0)
        {
            Settings.log.error("isManualRefreshAllowed: manualRefreshMinimum was less than 0");
        }
        
        return manualRefresh && (timestampDiff > manualRefreshMinimum);
    }
    
    private void setObjectPropertyHelper(final String propertyKey, final Object propertyValue)
    {
        final Collection<Object> nextList = new ArrayList<Object>(5);
        nextList.add(propertyValue);
        
        final Collection<Object> ifAbsent = this.configPropertiesCache.putIfAbsent(propertyKey, nextList);
        
        if(ifAbsent != null)
        {
            synchronized(this.configPropertiesCache)
            {
                if(!this.configPropertiesCache.containsKey(propertyKey))
                {
                    nextList.addAll(ifAbsent);
                    this.configPropertiesCache.put(propertyKey, nextList);
                }
            }
        }
    }
    
    @Override
    public void setProperty(final String propertyKey, final boolean propertyValue)
    {
        this.setObjectPropertyHelper(propertyKey, propertyValue);
    }
    
    @Override
    public void setProperty(final String propertyKey, final float propertyValue)
    {
        this.setObjectPropertyHelper(propertyKey, propertyValue);
    }
    
    @Override
    public void setProperty(final String propertyKey, final int propertyValue)
    {
        this.setObjectPropertyHelper(propertyKey, propertyValue);
    }
    
    @Override
    public void setProperty(final String propertyKey, final long propertyValue)
    {
        this.setObjectPropertyHelper(propertyKey, propertyValue);
    }
    
    @Override
    public void setProperty(final String propertyKey, final String propertyValue)
    {
        this.setObjectPropertyHelper(propertyKey, propertyValue);
    }
    
    @Override
    public void setProperty(final String propertyKey, final URI propertyValue)
    {
        this.setObjectPropertyHelper(propertyKey, propertyValue);
    }
    
    @Override
    public void setStringCollectionProperty(final String propertyKey, final Collection<String> propertyValues)
    {
        this.setObjectPropertyHelper(propertyKey, propertyValues);
    }
    
    @Override
    public void setURICollectionProperty(final String propertyKey, final Collection<URI> propertyValues)
    {
        this.setObjectPropertyHelper(propertyKey, propertyValues);
    }

    @Override
    public void setProperty(String propertyKey, Value propertyValue)
    {
        if(propertyValue instanceof URI)
        {
            setProperty(propertyKey, (URI)propertyValue);
            return;
        }
        
        try
        {
            boolean booleanFromValue = RdfUtils.getBooleanFromValue(propertyValue);
            
            setProperty(propertyKey, booleanFromValue);
            
            return;
        }
        catch(QueryAllRuntimeException rex)
        {
            if(_DEBUG)
            {
                log.debug("Could not parse boolean value="+propertyValue);
            }
        }
        
        try
        {
            int intFromValue = RdfUtils.getIntegerFromValue(propertyValue);
            
            setProperty(propertyKey, intFromValue);
            
            return;
        }
        catch(NumberFormatException nfe)
        {
            if(_DEBUG)
            {
                log.debug("Could not parse int value="+propertyValue);
            }
        }
        
        try
        {
            long longFromValue = RdfUtils.getLongFromValue(propertyValue);
            
            setProperty(propertyKey, longFromValue);
            
            return;
        }
        catch(NumberFormatException nfe)
        {
            if(_DEBUG)
            {
                log.debug("Could not parse long value="+propertyValue);
            }
        }
        
        try
        {
            float floatFromValue = RdfUtils.getFloatFromValue(propertyValue);
            
            setProperty(propertyKey, floatFromValue);
            
            return;
        }
        catch(NumberFormatException nfe)
        {
            if(_DEBUG)
            {
                log.debug("Could not parse float value="+propertyValue);
            }
        }
        
        // resort to setting it as a String
        setProperty(propertyKey, propertyValue.stringValue());
    }
}
