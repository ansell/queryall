/**
 * 
 */
package org.queryall.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.Provider;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.WebappConfig;
import org.queryall.exception.QueryAllRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manages settings, including all of the main API objects, along with configuration
 * settings based on the WebappConfig enum.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class Settings implements QueryAllConfiguration
{
    private static final Logger log = LoggerFactory.getLogger(Settings.class);
    private static final boolean TRACE = Settings.log.isTraceEnabled();
    private static final boolean DEBUG = Settings.log.isDebugEnabled();
    private static final boolean INFO = Settings.log.isInfoEnabled();
    
    private ConcurrentHashMap<URI, NamespaceEntry> namespaceEntries = new ConcurrentHashMap<URI, NamespaceEntry>();
    private ConcurrentHashMap<URI, NormalisationRule> normalisationRules =
            new ConcurrentHashMap<URI, NormalisationRule>();
    private ConcurrentHashMap<URI, Profile> profiles = new ConcurrentHashMap<URI, Profile>();
    private ConcurrentHashMap<URI, Provider> providers = new ConcurrentHashMap<URI, Provider>();
    private ConcurrentHashMap<URI, QueryType> queryTypes = new ConcurrentHashMap<URI, QueryType>();
    private ConcurrentHashMap<URI, RuleTest> ruleTests = new ConcurrentHashMap<URI, RuleTest>();
    private ConcurrentHashMap<String, Collection<URI>> namespacePrefixesToUris =
            new ConcurrentHashMap<String, Collection<URI>>();
    
    private ConcurrentHashMap<WebappConfig, Collection<Object>> properties =
            new ConcurrentHashMap<WebappConfig, Collection<Object>>();
    private Pattern cachedTagPattern;
    private String cachedSeparator;
    private long lastInitialised = System.currentTimeMillis();
    
    /**
     * 
     */
    public Settings()
    {
    }
    
    @Override
    public void addNamespaceEntry(final NamespaceEntry nextNamespaceEntry)
    {
        this.namespaceEntries.put(nextNamespaceEntry.getKey(), nextNamespaceEntry);
        
        final Collection<URI> ifPreferredAbsent =
                this.namespacePrefixesToUris.putIfAbsent(nextNamespaceEntry.getPreferredPrefix(),
                        Arrays.asList(nextNamespaceEntry.getKey()));
        
        if(ifPreferredAbsent != null)
        {
            final Collection<URI> nextPreferredList = new ArrayList<URI>();
            nextPreferredList.add(nextNamespaceEntry.getKey());
            
            nextPreferredList.addAll(ifPreferredAbsent);
            this.namespacePrefixesToUris.put(nextNamespaceEntry.getPreferredPrefix(), nextPreferredList);
        }
        
        for(final String nextAlternate : nextNamespaceEntry.getAlternativePrefixes())
        {
            final Collection<URI> ifAlternateAbsent =
                    this.namespacePrefixesToUris.putIfAbsent(nextAlternate, Arrays.asList(nextNamespaceEntry.getKey()));
            
            if(ifAlternateAbsent != null)
            {
                final Collection<URI> nextAlternateList = new ArrayList<URI>();
                nextAlternateList.add(nextNamespaceEntry.getKey());
                
                nextAlternateList.addAll(ifAlternateAbsent);
                this.namespacePrefixesToUris.put(nextAlternate, nextAlternateList);
            }
        }
    }
    
    /**
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#addNormalisationRule(org.queryall.api.rdfrule
     *      .NormalisationRule)
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
     * @see org.queryall.api.base.QueryAllConfiguration#addProvider(org.queryall.api.provider.Provider)
     */
    @Override
    public void addProvider(final Provider nextProvider)
    {
        this.providers.put(nextProvider.getKey(), nextProvider);
    }
    
    /**
     * 
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#addQueryType(org.queryall.api.querytype.QueryType
     *      )
     */
    @Override
    public void addQueryType(final QueryType nextQueryType)
    {
        this.queryTypes.put(nextQueryType.getKey(), nextQueryType);
    }
    
    /**
     * 
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#addRuleTest(org.queryall.api.ruletest.RuleTest)
     */
    @Override
    public void addRuleTest(final RuleTest nextRuleTest)
    {
        this.ruleTests.put(nextRuleTest.getKey(), nextRuleTest);
    }
    
    @Override
    public Collection<Object> clearProperty(final WebappConfig propertyKey)
    {
        if(Settings.TRACE)
        {
            Settings.log.trace("clearProperty propertyKey=" + propertyKey);
        }
        
        return this.properties.remove(propertyKey);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getAllNamespaceEntries()
     */
    @Override
    public Map<URI, NamespaceEntry> getAllNamespaceEntries()
    {
        return Collections.unmodifiableMap(this.namespaceEntries);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getAllNormalisationRules()
     */
    @Override
    public Map<URI, NormalisationRule> getAllNormalisationRules()
    {
        return Collections.unmodifiableMap(this.normalisationRules);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getAllProfiles()
     */
    @Override
    public Map<URI, Profile> getAllProfiles()
    {
        return Collections.unmodifiableMap(this.profiles);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getAllProviders()
     */
    @Override
    public Map<URI, Provider> getAllProviders()
    {
        return Collections.unmodifiableMap(this.providers);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getAllQueryTypes()
     */
    @Override
    public Map<URI, QueryType> getAllQueryTypes()
    {
        return Collections.unmodifiableMap(this.queryTypes);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getAllRuleTests()
     */
    @Override
    public Map<URI, RuleTest> getAllRuleTests()
    {
        return Collections.unmodifiableMap(this.ruleTests);
    }
    
    @Override
    public boolean getBooleanProperty(final WebappConfig propertyKey)
    {
        return this.getBooleanProperty(propertyKey, (Boolean)propertyKey.getDefaultValue());
    }
    
    @Override
    public boolean getBooleanProperty(final WebappConfig propertyKey, final boolean defaultValue)
    {
        if(this.properties.containsKey(propertyKey))
        {
            final Collection<Object> properties = this.properties.get(propertyKey);
            
            if(properties.size() == 1)
            {
                final Boolean next = (Boolean)properties.iterator().next();
                
                return next;
            }
            else if(properties.size() == 0)
            {
                Settings.log.error("No property value found for propertyKey=" + propertyKey);
            }
            else
            {
                Settings.log.error("More than one property value found for propertyKey=" + propertyKey);
            }
        }
        
        return defaultValue;
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
        return this.getStringProperty(WebappConfig.URI_PREFIX, (String)WebappConfig.URI_PREFIX.getDefaultValue())
                + this.getStringProperty(WebappConfig.HOST_NAME, (String)WebappConfig.HOST_NAME.getDefaultValue())
                + this.getStringProperty(WebappConfig.URI_SUFFIX, (String)WebappConfig.URI_SUFFIX.getDefaultValue());
    }
    
    @Override
    public float getFloatProperty(final WebappConfig propertyKey)
    {
        return this.getFloatProperty(propertyKey, (Float)propertyKey.getDefaultValue());
    }
    
    @Override
    public float getFloatProperty(final WebappConfig propertyKey, final float defaultValue)
    {
        if(this.properties.containsKey(propertyKey))
        {
            final Collection<Object> properties = this.properties.get(propertyKey);
            
            if(properties.size() == 1)
            {
                final Float next = (Float)properties.iterator().next();
                
                return next;
            }
            else if(properties.size() == 0)
            {
                Settings.log.error("No property value found for propertyKey=" + propertyKey);
            }
            else
            {
                Settings.log.error("More than one property value found for propertyKey=" + propertyKey);
            }
        }
        
        return defaultValue;
    }
    
    @Override
    public int getIntProperty(final WebappConfig propertyKey)
    {
        return this.getIntProperty(propertyKey, (Integer)propertyKey.getDefaultValue());
    }
    
    @Override
    public int getIntProperty(final WebappConfig propertyKey, final int defaultValue)
    {
        if(this.properties.containsKey(propertyKey))
        {
            final Collection<Object> properties = this.properties.get(propertyKey);
            
            if(properties.size() == 1)
            {
                final Integer next = (Integer)properties.iterator().next();
                
                return next;
            }
            else if(properties.size() == 0)
            {
                Settings.log.error("No property value found for propertyKey=" + propertyKey);
            }
            else
            {
                Settings.log.error("More than one property value found for propertyKey=" + propertyKey);
            }
        }
        
        return defaultValue;
    }
    
    @Override
    public long getLastInitialised()
    {
        return this.lastInitialised;
    }
    
    @Override
    public long getLongProperty(final WebappConfig propertyKey)
    {
        return this.getLongProperty(propertyKey, (Long)propertyKey.getDefaultValue());
    }
    
    @Override
    public long getLongProperty(final WebappConfig propertyKey, final long defaultValue)
    {
        if(this.properties.containsKey(propertyKey))
        {
            final Collection<Object> properties = this.properties.get(propertyKey);
            
            if(properties.size() == 1)
            {
                final Long next = (Long)properties.iterator().next();
                
                return next;
            }
            else if(properties.size() == 0)
            {
                Settings.log.error("No property value found for propertyKey=" + propertyKey);
            }
            else
            {
                Settings.log.error("More than one property value found for propertyKey=" + propertyKey);
            }
        }
        
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
        return this.namespaceEntries.get(nextNamespaceEntryUri);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getNamespacePrefixesToUris()
     */
    @Override
    public Map<String, Collection<URI>> getNamespacePrefixesToUris()
    {
        return Collections.unmodifiableMap(this.namespacePrefixesToUris);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getNormalisationRule(org.openrdf.model.URI)
     */
    @Override
    public NormalisationRule getNormalisationRule(final URI nextNormalisationRuleUri)
    {
        return this.normalisationRules.get(nextNormalisationRuleUri);
    }
    
    @Override
    public Pattern getPlainNamespaceAndIdentifierPattern()
    {
        return Pattern.compile(this.getStringProperty(WebappConfig.PLAIN_NAMESPACE_AND_IDENTIFIER_REGEX,
                (String)WebappConfig.PLAIN_NAMESPACE_AND_IDENTIFIER_REGEX.getDefaultValue()));
    }
    
    @Override
    public Pattern getPlainNamespacePattern()
    {
        return Pattern.compile(this.getStringProperty(WebappConfig.PLAIN_NAMESPACE_REGEX,
                (String)WebappConfig.PLAIN_NAMESPACE_REGEX.getDefaultValue()));
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getProfile(org.openrdf.model.URI)
     */
    @Override
    public Profile getProfile(final URI nextProfileUri)
    {
        return this.profiles.get(nextProfileUri);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getProvider(org.openrdf.model.URI)
     */
    @Override
    public Provider getProvider(final URI nextProviderUri)
    {
        return this.providers.get(nextProviderUri);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getQueryType(org.openrdf.model.URI)
     */
    @Override
    public QueryType getQueryType(final URI nextQueryTypeUri)
    {
        return this.queryTypes.get(nextQueryTypeUri);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getRuleTest(org.openrdf.model.URI)
     */
    @Override
    public RuleTest getRuleTest(final URI nextRuleTestUri)
    {
        return this.ruleTests.get(nextRuleTestUri);
    }
    
    /**
     * This is a helper method to increase performance for the highly accessed default separator
     * property
     * 
     * It caches the value so it may return a different value to
     * getStringProperty("defaultSeparator", ":") if the property changes after the first call to
     * this method.
     */
    @Override
    public String getSeparator()
    {
        if(this.cachedSeparator != null)
        {
            return this.cachedSeparator;
        }
        
        final String separator = this.getStringProperty(WebappConfig.DEFAULT_SEPARATOR, ":");
        
        if(separator != null)
        {
            this.cachedSeparator = separator;
        }
        
        return separator;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#getStringProperties(java.lang.String)
     */
    @Override
    public Collection<String> getStringProperties(final WebappConfig propertyKey)
    {
        if(this.properties.containsKey(propertyKey))
        {
            final Collection<Object> nextProperties = this.properties.get(propertyKey);
            
            final Collection<String> results = new ArrayList<String>(nextProperties.size());
            
            for(final Object nextProperty : nextProperties)
            {
                if(nextProperty instanceof String)
                {
                    results.add((String)nextProperty);
                }
                else
                {
                    Settings.log
                            .warn("Automatically converting a property that was not a String in getStringProperties propertyKey="
                                    + propertyKey + " nextProperty=" + nextProperty);
                    
                    results.add(nextProperty.toString());
                }
            }
            
            return results;
        }
        
        return Collections.emptySet();
    }
    
    @Override
    public String getStringProperty(final WebappConfig propertyKey)
    {
        return this.getStringProperty(propertyKey, (String)propertyKey.getDefaultValue());
    }
    
    @Override
    public String getStringProperty(final WebappConfig propertyKey, final String defaultValue)
    {
        if(this.properties.containsKey(propertyKey))
        {
            final Collection<Object> properties = this.properties.get(propertyKey);
            
            if(properties.size() == 1)
            {
                final String next = (String)properties.iterator().next();
                
                return next;
            }
            else if(properties.size() == 0)
            {
                if(Settings.DEBUG)
                {
                    Settings.log.debug("No property value found for propertyKey=" + propertyKey);
                }
            }
            else
            {
                Settings.log.error("More than one property value found for propertyKey=" + propertyKey);
            }
        }
        
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
        if(this.cachedTagPattern != null)
        {
            return this.cachedTagPattern;
        }
        
        // TODO: Do we need to split this into two patterns so that we can identify the parameter
        // names as separate matching groups from the entire tag with braces etc.?
        final Pattern tempPattern =
                Pattern.compile(this.getStringProperty(WebappConfig.TAG_PATTERN_REGEX,
                        (String)WebappConfig.TAG_PATTERN_REGEX.getDefaultValue()));
        
        if(tempPattern != null)
        {
            this.cachedTagPattern = tempPattern;
        }
        
        return tempPattern;
    }
    
    @Override
    public Collection<URI> getURIProperties(final WebappConfig propertyKey)
    {
        if(this.properties.containsKey(propertyKey))
        {
            final Collection<Object> nextProperties = this.properties.get(propertyKey);
            
            final Collection<URI> results = new ArrayList<URI>(nextProperties.size());
            
            for(final Object nextProperty : nextProperties)
            {
                if(nextProperty instanceof URI)
                {
                    results.add((URI)nextProperty);
                }
                else
                {
                    Settings.log.warn("Found a property that was not a URI in getURIProperties propertyKey="
                            + propertyKey + " nextProperty=" + nextProperty);
                }
            }
            
            return results;
        }
        
        return Collections.emptySet();
    }
    
    @Override
    public URI getURIProperty(final WebappConfig propertyKey)
    {
        return this.getURIProperty(propertyKey, (URI)propertyKey.getDefaultValue());
    }
    
    @Override
    public URI getURIProperty(final WebappConfig propertyKey, final URI defaultValue)
    {
        final Collection<URI> uriProperties = this.getURIProperties(propertyKey);
        
        if(uriProperties.size() == 1)
        {
            return uriProperties.iterator().next();
        }
        else if(uriProperties.size() == 0)
        {
            if(Settings.DEBUG)
            {
                Settings.log.debug("No property value found for propertyKey=" + propertyKey);
            }
        }
        else
        {
            Settings.log.warn("More than one property value for propertyKey=" + propertyKey);
        }
        
        return defaultValue;
    }
    
    @Override
    public boolean resetNamespaceEntries()
    {
        synchronized(this.namespaceEntries)
        {
            this.namespaceEntries.clear();
        }
        
        synchronized(this.namespacePrefixesToUris)
        {
            this.namespacePrefixesToUris.clear();
        }
        
        return true;
    }
    
    @Override
    public boolean resetNormalisationRules()
    {
        synchronized(this.normalisationRules)
        {
            this.normalisationRules.clear();
        }
        
        return true;
    }
    
    @Override
    public boolean resetProfiles()
    {
        synchronized(this.profiles)
        {
            this.profiles.clear();
        }
        
        return true;
    }
    
    @Override
    public boolean resetProperties()
    {
        synchronized(this.properties)
        {
            this.properties.clear();
        }
        
        return true;
    }
    
    @Override
    public boolean resetProviders()
    {
        synchronized(this.providers)
        {
            this.providers.clear();
        }
        
        return true;
    }
    
    @Override
    public boolean resetQueryTypes()
    {
        synchronized(this.queryTypes)
        {
            this.queryTypes.clear();
        }
        
        return true;
    }
    
    @Override
    public boolean resetRuleTests()
    {
        synchronized(this.ruleTests)
        {
            this.ruleTests.clear();
        }
        
        return true;
    }
    
    @Override
    public void setLastInitialised(final long lastInitialised)
    {
        this.lastInitialised = lastInitialised;
    }
    
    /**
     * Sets the property into the internal property cache
     * 
     * @param propertyKey
     *            The key for the property value
     * @param propertyValue
     *            The property value to set
     * @param overwrite
     *            If true, the given property value will overwrite the current properties, otherwise
     *            they will both be included
     */
    private void setObjectCollectionPropertyHelper(final WebappConfig propertyKey,
            final Collection<Object> propertyValue, final boolean overwrite)
    {
        if(Settings.TRACE)
        {
            Settings.log.trace("setObjectPropertyHelper(String,Object) propertyKey=" + propertyKey + " propertyValue="
                    + propertyValue.toString());
        }
        
        final Collection<Object> ifAbsent = this.properties.putIfAbsent(propertyKey, propertyValue);
        
        if(Settings.TRACE)
        {
            Settings.log.trace("setObjectPropertyHelper(String,Object) this.properties.get(propertyKey)="
                    + this.properties.get(propertyKey));
        }
        
        // if there were properties previously, than synchronise access to the properties object and
        // add the list, any other additions to this property should wait to synchronise here
        if(ifAbsent != null)
        {
            if(Settings.TRACE)
            {
                Settings.log.trace("setObjectPropertyHelper(String,Object) ifAbsent not equal to null");
            }
            
            synchronized(this.properties)
            {
                // do this to make sure that it is writeable, they could have sent us a collection
                // that was not modifiable
                final Collection<Object> nextList = new ArrayList<Object>(propertyValue);
                
                if(!overwrite)
                {
                    nextList.addAll(ifAbsent);
                }
                
                if(Settings.TRACE)
                {
                    Settings.log.trace("setObjectPropertyHelper(String,Object) ifAbsent not equal to null nextList="
                            + nextList);
                }
                
                this.properties.put(propertyKey, nextList);
            }
        }
    }
    
    /**
     * Sets the property into the internal property cache
     * 
     * @param propertyKey
     *            The key for the property value
     * @param propertyValue
     *            The property value to set
     * @param overwrite
     *            If true, the given property value will overwrite the current properties, otherwise
     *            they will both be included
     */
    private void setObjectPropertyHelper(final WebappConfig propertyKey, final Object propertyValue,
            final boolean overwrite)
    {
        if(Settings.TRACE)
        {
            Settings.log.trace("setObjectPropertyHelper(String,Object) propertyKey=" + propertyKey + " propertyValue="
                    + propertyValue.toString());
        }
        
        final Collection<Object> nextList = new ArrayList<Object>(5);
        nextList.add(propertyValue);
        
        final Collection<Object> ifAbsent = this.properties.putIfAbsent(propertyKey, nextList);
        
        if(Settings.TRACE)
        {
            Settings.log.trace("setObjectPropertyHelper(String,Object) this.properties.get(propertyKey)="
                    + this.properties.get(propertyKey));
        }
        
        // if there were properties previously, than synchronise access to the properties object and
        // add the list, any other additions to this property should wait to synchronise here
        if(ifAbsent != null)
        {
            if(Settings.TRACE)
            {
                Settings.log.trace("setObjectPropertyHelper(String,Object) ifAbsent not equal to null");
            }
            
            synchronized(this.properties)
            {
                if(!overwrite)
                {
                    nextList.addAll(ifAbsent);
                }
                if(Settings.TRACE)
                {
                    Settings.log.trace("setObjectPropertyHelper(String,Object) ifAbsent not equal to null nextList="
                            + nextList);
                }
                
                this.properties.put(propertyKey, nextList);
            }
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#setProperty(java.lang.String, boolean)
     */
    @Override
    public void setProperty(final WebappConfig propertyKey, final boolean propertyValue)
    {
        if(Settings.TRACE)
        {
            Settings.log.trace("setProperty(String,boolean) propertyKey=" + propertyKey + " propertyValue="
                    + Boolean.valueOf(propertyValue).toString());
        }
        
        this.setProperty(propertyKey, propertyValue, propertyKey.overwrite());
    }
    
    private void setProperty(final WebappConfig propertyKey, final boolean propertyValue, final boolean overwrite)
    {
        this.setObjectPropertyHelper(propertyKey, Boolean.valueOf(propertyValue), overwrite);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#setProperty(java.lang.String, float)
     */
    @Override
    public void setProperty(final WebappConfig propertyKey, final float propertyValue)
    {
        if(Settings.TRACE)
        {
            Settings.log.trace("setProperty(String,float) propertyKey=" + propertyKey + " propertyValue="
                    + Float.valueOf(propertyValue).toString());
        }
        
        this.setProperty(propertyKey, propertyValue, propertyKey.overwrite());
    }
    
    private void setProperty(final WebappConfig propertyKey, final float propertyValue, final boolean overwrite)
    {
        this.setObjectPropertyHelper(propertyKey, Float.valueOf(propertyValue), overwrite);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#setProperty(java.lang.String, int)
     */
    @Override
    public void setProperty(final WebappConfig propertyKey, final int propertyValue)
    {
        if(Settings.TRACE)
        {
            Settings.log.trace("setProperty(String,int) propertyKey=" + propertyKey + " propertyValue="
                    + Integer.valueOf(propertyValue).toString());
        }
        
        this.setProperty(propertyKey, propertyValue, propertyKey.overwrite());
    }
    
    private void setProperty(final WebappConfig propertyKey, final int propertyValue, final boolean overwrite)
    {
        this.setObjectPropertyHelper(propertyKey, Integer.valueOf(propertyValue), overwrite);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#setProperty(java.lang.String, long)
     */
    @Override
    public void setProperty(final WebappConfig propertyKey, final long propertyValue)
    {
        if(Settings.TRACE)
        {
            Settings.log.trace("setProperty(String,long) propertyKey=" + propertyKey + " propertyValue="
                    + Long.valueOf(propertyValue).toString());
        }
        
        this.setProperty(propertyKey, propertyValue, propertyKey.overwrite());
    }
    
    private void setProperty(final WebappConfig propertyKey, final long propertyValue, final boolean overwrite)
    {
        this.setObjectPropertyHelper(propertyKey, Long.valueOf(propertyValue), overwrite);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#setProperty(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void setProperty(final WebappConfig propertyKey, final String propertyValue)
    {
        if(propertyValue == null)
        {
            throw new NullPointerException("property value cannot be null propertyKey=" + propertyKey);
        }
        
        if(Settings.TRACE)
        {
            Settings.log.trace("setProperty(String,String) propertyKey=" + propertyKey + " propertyValue="
                    + String.valueOf(propertyValue));
        }
        
        this.setProperty(propertyKey, propertyValue, propertyKey.overwrite());
    }
    
    private void setProperty(final WebappConfig propertyKey, final String propertyValue, final boolean overwrite)
    {
        this.setObjectPropertyHelper(propertyKey, String.valueOf(propertyValue), overwrite);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#setProperty(java.lang.String,
     * org.openrdf.model.URI)
     */
    @Override
    public void setProperty(final WebappConfig propertyKey, final URI propertyValue)
    {
        if(propertyValue == null)
        {
            throw new NullPointerException("property value cannot be null propertyKey=" + propertyKey);
        }
        
        if(Settings.DEBUG)
        {
            Settings.log.debug("setProperty(String,URI) propertyKey=" + propertyKey + " propertyValue="
                    + propertyValue.stringValue());
        }
        
        this.setProperty(propertyKey, propertyValue, propertyKey.overwrite());
    }
    
    private void setProperty(final WebappConfig propertyKey, final URI propertyValue, final boolean overwrite)
    {
        if(propertyValue == null)
        {
            throw new NullPointerException("property value cannot be null propertyKey=" + propertyKey);
        }
        
        this.setObjectPropertyHelper(propertyKey, propertyValue, overwrite);
    }
    
    @Override
    public void setProperty(final WebappConfig propertyKey, final Value propertyValue)
    {
        if(Settings.TRACE)
        {
            Settings.log.trace("setProperty(String,Value) propertyKey=" + propertyKey + " propertyValue="
                    + propertyValue + " propertyValue.stringValue()=" + propertyValue.stringValue()
                    + " propertyValue.getClass().getName()=" + propertyValue.getClass().getName());
        }
        
        this.setProperty(propertyKey, propertyValue, propertyKey.overwrite());
    }
    
    private void setProperty(final WebappConfig propertyKey, final Value propertyValue, final boolean overwrite)
    {
        if(propertyValue instanceof URI)
        {
            this.setProperty(propertyKey, (URI)propertyValue, overwrite);
            return;
        }
        
        if(propertyValue instanceof BNode)
        {
            throw new QueryAllRuntimeException("Cannot handle blank nodes as property values, they have no meaning");
        }
        
        if(propertyValue instanceof Literal)
        {
            final Literal literalValue = (Literal)propertyValue;
            final URI datatype = literalValue.getDatatype();
            
            if(Settings.TRACE)
            {
                Settings.log.trace("literalValue.getDatatype()=" + datatype);
            }
            
            if(datatype == null)
            {
                // resort to setting it as a String as it didn't have type information attached
                this.setProperty(propertyKey, propertyValue.stringValue(), overwrite);
            }
            else
            {
                
                if(datatype.equals(Constants.XSD_BOOLEAN))
                {
                    if(Settings.TRACE)
                    {
                        Settings.log.trace("boolean datatype");
                    }
                    
                    try
                    {
                        final boolean booleanFromValue = RdfUtils.getBooleanFromValue(literalValue);
                        
                        if(Settings.TRACE)
                        {
                            Settings.log.trace("booleanFromValue=" + booleanFromValue);
                        }
                        
                        this.setProperty(propertyKey, booleanFromValue);
                        
                        return;
                    }
                    catch(final QueryAllRuntimeException rex)
                    {
                        Settings.log.warn("Could not parse boolean propertyKey=" + propertyKey + " value="
                                + literalValue);
                    }
                }
                else if(datatype.equals(Constants.XSD_INT) || datatype.equals(Constants.XSD_INTEGER))
                {
                    if(Settings.TRACE)
                    {
                        Settings.log.trace("int or integer datatype");
                    }
                    
                    try
                    {
                        final int intFromValue = RdfUtils.getIntegerFromValue(literalValue);
                        
                        if(Settings.TRACE)
                        {
                            Settings.log.trace("intFromValue=" + intFromValue);
                        }
                        
                        this.setProperty(propertyKey, intFromValue);
                        
                        return;
                    }
                    catch(final NumberFormatException nfe)
                    {
                        Settings.log.warn("Could not parse int propertyKey=" + propertyKey + " value=" + literalValue);
                    }
                }
                else if(datatype.equals(Constants.XSD_LONG))
                {
                    if(Settings.TRACE)
                    {
                        Settings.log.trace("long datatype");
                    }
                    
                    try
                    {
                        final long longFromValue = RdfUtils.getLongFromValue(literalValue);
                        
                        if(Settings.TRACE)
                        {
                            Settings.log.trace("longFromValue=" + longFromValue);
                        }
                        
                        this.setProperty(propertyKey, longFromValue);
                        
                        return;
                    }
                    catch(final NumberFormatException nfe)
                    {
                        Settings.log.warn("Could not parse long propertyKey=" + propertyKey + " value=" + literalValue);
                    }
                }
                else if(datatype.equals(Constants.XSD_FLOAT))
                {
                    if(Settings.TRACE)
                    {
                        Settings.log.trace("float datatype");
                    }
                    
                    try
                    {
                        final float floatFromValue = RdfUtils.getFloatFromValue(literalValue);
                        
                        if(Settings.TRACE)
                        {
                            Settings.log.trace("floatFromValue=" + floatFromValue);
                        }
                        
                        this.setProperty(propertyKey, floatFromValue);
                        
                        return;
                    }
                    catch(final NumberFormatException nfe)
                    {
                        Settings.log
                                .warn("Could not parse float propertyKey=" + propertyKey + " value=" + literalValue);
                    }
                }
                else
                {
                    Settings.log.warn("unrecognised datatype, parsing as string, propertyKey=" + propertyKey
                            + " value=" + propertyValue.stringValue());
                    // resort to setting it as a String
                    this.setProperty(propertyKey, propertyValue.stringValue());
                }
            }
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.queryall.api.base.QueryAllConfiguration#setStringCollectionProperty(java.lang.String,
     * java.util.Collection)
     */
    @Override
    public void setStringCollectionProperty(final WebappConfig propertyKey, final Collection<String> propertyValues)
    {
        if(Settings.TRACE)
        {
            Settings.log.trace("setProperty(String,Collection<String>) propertyKey=" + propertyKey + " propertyValue="
                    + propertyValues.toString());
        }
        
        final Collection<Object> newPropertyValues = new ArrayList<Object>(propertyValues);
        
        this.setObjectCollectionPropertyHelper(propertyKey, newPropertyValues, true);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.api.base.QueryAllConfiguration#setURICollectionProperty(java.lang.String,
     * java.util.Collection)
     */
    @Override
    public void setURICollectionProperty(final WebappConfig propertyKey, final Collection<URI> propertyValues)
    {
        if(Settings.TRACE)
        {
            Settings.log.trace("setProperty(String,Collection<URI>) propertyKey=" + propertyKey + " propertyValue="
                    + propertyValues.toString());
        }
        
        final Collection<Object> newPropertyValues = new ArrayList<Object>(propertyValues);
        
        this.setObjectCollectionPropertyHelper(propertyKey, newPropertyValues, true);
    }
    
    @Override
    public void setValueCollectionProperty(final WebappConfig propertyKey, final Collection<Value> propertyValues)
    {
        this.clearProperty(propertyKey);
        
        for(final Value nextValue : propertyValues)
        {
            this.setProperty(propertyKey, nextValue, propertyKey.overwrite());
        }
    }
    
}
