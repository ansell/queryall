package org.queryall.query;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.Provider;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.api.utils.PropertyUtils;
import org.queryall.api.utils.QueryAllNamespaces;
import org.queryall.api.utils.Schema;
import org.queryall.exception.QueryAllRuntimeException;
import org.queryall.utils.RdfUtils;
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
    
    public static final int CONFIG_API_VERSION = 5;
    
    public static final String VERSION = Settings.getVersion();
    
    /**
     * Checks for the base config location first in the system vm properties, then in the
     * localisation properties file, by default, "queryall.properties", Uses the key
     * "queryall.BaseConfigLocation"
     * 
     * @return The location of the base configuration file, defaults to "/queryallBaseConfig.n3"
     */
    private static String getDefaultBaseConfigLocationProperty()
    {
        return PropertyUtils.getSystemOrPropertyString("queryall.BaseConfigLocation", "/queryallBaseConfig.n3");
    }
    
    /**
     * Uses the key "queryall.BaseConfigMimeFormat" in the properties file or system properties
     * 
     * @return The MIME format of the base configuration file, defaults to "text/rdf+n3"
     */
    private static String getDefaultBaseConfigMimeFormatProperty()
    {
        return PropertyUtils.getSystemOrPropertyString("queryall.BaseConfigMimeFormat", "text/rdf+n3");
    }
    
    /**
     * 
     * Uses the key "queryall.BaseConfigUri"
     * 
     * @return The URI of the configuration object in the base config file, defaults to
     *         "http://purl.org/queryall/webapp_configuration:theBaseConfig"
     */
    private static String getDefaultBaseConfigUriProperty()
    {
        return PropertyUtils.getSystemOrPropertyString("queryall.BaseConfigUri",
                "http://purl.org/queryall/webapp_configuration:theBaseConfig");
    };
    
    public static QueryAllConfiguration getSettings()
    {
        return SettingsHolder.helper;
    }
    
    /**
     * Checks for the configured version first in the system vm properties, then in the localisation
     * properties file, by default, "queryall.properties", Uses the key "queryall.Version"
     * 
     * @return The version, defaults to "0.0.1"
     */
    private static String getVersion()
    {
        return PropertyUtils.getSystemOrPropertyString("queryall.Version", "0.0.1");
    }
    
    private volatile String baseConfigLocation = Settings.getDefaultBaseConfigLocationProperty();
    
    private volatile String baseConfigUri = Settings.getDefaultBaseConfigUriProperty();
    private volatile String baseConfigMimeFormat = Settings.getDefaultBaseConfigMimeFormatProperty();
    private volatile Collection<String> webappConfigUriList = new HashSet<String>();
    
    private volatile Repository currentBaseConfigurationRepository = null;
    private volatile Repository currentWebAppConfigurationRepository = null;
    private volatile Repository currentConfigurationRepository = null;
    private volatile Map<URI, Provider> cachedProviders = null;
    private volatile Map<URI, NormalisationRule> cachedNormalisationRules = null;
    private volatile Map<URI, RuleTest> cachedRuleTests = null;
    private volatile Map<URI, QueryType> cachedQueryTypes = null;
    private volatile Map<URI, Profile> cachedProfiles = null;
    
    private volatile Map<URI, NamespaceEntry> cachedNamespaceEntries = null;
    
    private volatile Map<String, Collection<URI>> cachedNamespacePrefixToUriEntries = null;
    private volatile Pattern cachedTagPattern = null;
    
    private volatile Map<URI, Map<URI, Collection<Value>>> configPropertiesCacheByValue = null;
    
    private volatile long initialisedTimestamp = System.currentTimeMillis();
    
    private volatile String separator;
    
    public Settings()
    {
        this.baseConfigLocation = Settings.getDefaultBaseConfigLocationProperty();
        this.baseConfigMimeFormat = Settings.getDefaultBaseConfigMimeFormatProperty();
        this.baseConfigUri = Settings.getDefaultBaseConfigUriProperty();
    }
    
    /**
     * Creates a settings object using a classpath resource location, the mime format to expect, and
     * the URI of the base configuration inside the file.
     * 
     * @param baseConfigLocation
     *            A string path to a classpath resource which contains information about where to
     *            find the further configuration files.
     * @param baseConfigMimeFormat
     *            The MIME format of the baseConfigLocation file.
     * @param baseConfigUri
     *            The subject URI to use to fetch the relevant statements from the
     *            baseConfigLocation
     */
    public Settings(final String baseConfigLocation, final String baseConfigMimeFormat, final String baseConfigUri)
    {
        // Do a quick test on the base config file existence
        
        final InputStream baseConfig = this.getClass().getResourceAsStream(baseConfigLocation);
        
        if(baseConfig == null)
        {
            Settings.log.error("Settings.init: TEST: baseConfig was null baseConfigLocation=" + baseConfigLocation);
        }
        else
        {
            Settings.log.debug("Settings.init: TEST: baseConfig was not null baseConfigLocation=" + baseConfigLocation);
        }
        
        this.baseConfigLocation = baseConfigLocation;
        this.baseConfigMimeFormat = baseConfigMimeFormat;
        this.baseConfigUri = baseConfigUri;
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
        if(this.cachedNamespaceEntries == null)
        {
            synchronized(this)
            {
                if(this.cachedNamespaceEntries == null)
                {
                    this.cachedNamespaceEntries = new ConcurrentHashMap<URI, NamespaceEntry>(200);
                    this.cachedNamespacePrefixToUriEntries = new ConcurrentHashMap<String, Collection<URI>>(200);
                }
            }
        }
        
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
        if(this.cachedNormalisationRules == null)
        {
            synchronized(this)
            {
                if(this.cachedNormalisationRules == null)
                {
                    this.cachedNormalisationRules = new ConcurrentHashMap<URI, NormalisationRule>(200);
                }
            }
        }
        
        this.cachedNormalisationRules.put(nextNormalisationRule.getKey(), nextNormalisationRule);
    }
    
    @Override
    public void addProfile(final Profile nextProfile)
    {
        if(this.cachedProfiles == null)
        {
            synchronized(this)
            {
                if(this.cachedProfiles == null)
                {
                    this.cachedProfiles = new ConcurrentHashMap<URI, Profile>(200);
                }
            }
        }
        
        this.cachedProfiles.put(nextProfile.getKey(), nextProfile);
    }
    
    @Override
    public void addProvider(final Provider nextProvider)
    {
        if(this.cachedProviders == null)
        {
            synchronized(this)
            {
                if(this.cachedProviders == null)
                {
                    this.cachedProviders = new ConcurrentHashMap<URI, Provider>(200);
                }
            }
        }
        
        this.cachedProviders.put(nextProvider.getKey(), nextProvider);
    }
    
    @Override
    public void addQueryType(final QueryType nextQueryType)
    {
        if(this.cachedQueryTypes == null)
        {
            synchronized(this)
            {
                if(this.cachedQueryTypes == null)
                {
                    this.cachedQueryTypes = new ConcurrentHashMap<URI, QueryType>(200);
                }
            }
        }
        
        this.cachedQueryTypes.put(nextQueryType.getKey(), nextQueryType);
    }
    
    @Override
    public void addRuleTest(final RuleTest nextRuleTest)
    {
        if(this.cachedRuleTests == null)
        {
            synchronized(this)
            {
                if(this.cachedRuleTests == null)
                {
                    this.cachedRuleTests = new ConcurrentHashMap<URI, RuleTest>(200);
                }
            }
        }
        
        this.cachedRuleTests.put(nextRuleTest.getKey(), nextRuleTest);
    }
    
    public boolean configRefreshCheck(final boolean tryToForceRefresh)
    {
        final long currentTimestamp = System.currentTimeMillis();
        
        if(Settings._DEBUG)
        {
            Settings.log.debug("Settings.configRefreshCheck: before check Settings.PERIODIC_CONFIGURATION_REFRESH="
                    + this.getBooleanProperty("enablePeriodicConfigurationRefresh", true)
                    + " Settings.PERIODIC_REFRESH_MILLISECONDS="
                    + this.getLongProperty("periodicConfigurationMilliseconds", 60000L)
                    + " currentTimestamp - initialisedTimestamp=" + (currentTimestamp - this.initialisedTimestamp)
                    + " ");
        }
        if(tryToForceRefresh && !this.isManualRefreshAllowed())
        {
            Settings.log
                    .error("Settings.configRefreshCheck: attempted to force refresh outside of manual refresh time and ability guidelines");
            return false;
        }
        
        final boolean enablePeriodicConfigurationRefresh =
                this.getBooleanProperty("enablePeriodicConfigurationRefresh", true);
        final long periodicConfigurationMilliseconds =
                this.getLongProperty("periodicConfigurationMilliseconds", 60000L);
        
        if(Settings._DEBUG)
        {
            Settings.log.debug("Settings.configRefreshCheck: enablePeriodicConfigurationRefresh="
                    + enablePeriodicConfigurationRefresh);
            Settings.log.debug("Settings.configRefreshCheck: periodicConfigurationMilliseconds="
                    + periodicConfigurationMilliseconds);
        }
        
        if(tryToForceRefresh
                || (enablePeriodicConfigurationRefresh && ((currentTimestamp - this.initialisedTimestamp) > periodicConfigurationMilliseconds)))
        {
            synchronized(this)
            {
                Repository previousConfiguration = null;
                Repository previousWebappConfiguration = null;
                try
                {
                    if(Settings._INFO)
                    {
                        Settings.log.info("Settings.configRefreshCheck: refresh required... starting process");
                    }
                    if(this.currentWebAppConfigurationRepository != null)
                    {
                        previousWebappConfiguration = this.currentWebAppConfigurationRepository;
                        if(Settings._DEBUG)
                        {
                            Settings.log
                                    .debug("Settings.configRefreshCheck: refresh required... currentWebappConfigurationRepository about to be set to null");
                        }
                        this.currentWebAppConfigurationRepository = null;
                    }
                    
                    this.getWebAppConfigurationRdf();
                    
                    if(this.currentWebAppConfigurationRepository == null)
                    {
                        this.currentWebAppConfigurationRepository = previousWebappConfiguration;
                        
                        Settings.log
                                .error("Settings.configRefreshCheck: WebappConfiguration was not valid after the refresh, resetting to the previousWebappConfiguration");
                        
                        // reset the timestamp so that we don't try too often
                        // TODO: improve functionality for specifying retry time if
                        // failures occur
                        // this.initialisedTimestamp = System.currentTimeMillis();
                        
                        // return false;
                    }
                    
                    if(this.currentConfigurationRepository != null)
                    {
                        previousConfiguration = this.currentConfigurationRepository;
                        if(Settings._DEBUG)
                        {
                            Settings.log
                                    .debug("Settings.configRefreshCheck: refresh required... currentConfigurationRepository about to be set to null");
                        }
                        this.currentConfigurationRepository = null;
                    }
                    if(Settings._INFO)
                    {
                        Settings.log
                                .info("Settings.configRefreshCheck: refresh required... getServerConfigurationRdf about to be called");
                    }
                    this.getServerConfigurationRdf();
                    
                    if(this.currentConfigurationRepository == null)
                    {
                        this.currentConfigurationRepository = previousConfiguration;
                        
                        Settings.log
                                .error("Settings.configRefreshCheck: configuration was not valid after the refresh, resetting to the previousConfiguration");
                        
                        // reset the timestamp so that we don't try too often
                        // TODO: improve functionality for specifying retry time if
                        // failures occur
                        this.initialisedTimestamp = System.currentTimeMillis();
                        
                        return false;
                    }
                    
                    if(Settings._TRACE)
                    {
                        Settings.log
                                .trace("Settings.configRefreshCheck: refresh required... currentConfigurationRepository refreshed");
                    }
                    if(this.cachedProviders != null)
                    {
                        this.cachedProviders = null;
                    }
                    this.getAllProviders();
                    if(Settings._TRACE)
                    {
                        Settings.log
                                .trace("Settings.configRefreshCheck: refresh required... cachedProviders refreshed");
                    }
                    if(this.cachedQueryTypes != null)
                    {
                        this.cachedQueryTypes = null;
                    }
                    this.getAllQueryTypes();
                    if(Settings._TRACE)
                    {
                        Settings.log
                                .trace("Settings.configRefreshCheck: refresh required... cachedCustomQueries refreshed");
                    }
                    if(this.cachedNamespaceEntries != null)
                    {
                        this.cachedNamespaceEntries = null;
                    }
                    if(this.getNamespacePrefixesToUris() != null)
                    {
                        this.cachedNamespacePrefixToUriEntries = null;
                    }
                    this.getAllNamespaceEntries();
                    if(Settings._TRACE)
                    {
                        Settings.log
                                .trace("Settings.configRefreshCheck: refresh required... cachedNamespaceEntries and cachedNamespacePrefixToUriEntries refreshed");
                    }
                    
                    if(this.cachedProfiles != null)
                    {
                        this.cachedProfiles = null;
                    }
                    this.getAllProfiles();
                    
                    if(Settings._TRACE)
                    {
                        Settings.log.trace("Settings.configRefreshCheck: refresh required... cachedProfiles refreshed");
                    }
                    
                    if(this.cachedNormalisationRules != null)
                    {
                        this.cachedNormalisationRules = null;
                    }
                    this.getAllNormalisationRules();
                    if(Settings._TRACE)
                    {
                        Settings.log
                                .trace("Settings.configRefreshCheck: refresh required... cachedNormalisationRules refreshed");
                    }
                    if(this.cachedRuleTests != null)
                    {
                        this.cachedRuleTests = null;
                    }
                    
                    this.getAllRuleTests();
                    
                    if(Settings._TRACE)
                    {
                        Settings.log
                                .trace("Settings.configRefreshCheck: refresh required... cachedRuleTests refreshed");
                    }
                    
                    if(Settings._TRACE)
                    {
                        Settings.log
                                .trace("Settings.configRefreshCheck: refresh required... cachedTemplates refreshed");
                    }
                    
                    this.initialisedTimestamp = System.currentTimeMillis();
                    
                    if(Settings._INFO)
                    {
                        Settings.log.info("Settings.configRefreshCheck: refresh required... finished process");
                    }
                    
                    return true;
                }
                catch(final java.lang.InterruptedException ie)
                {
                    Settings.log.error("Settings.configRefreshCheck: failed due to java.lang.InterruptedException");
                    this.currentConfigurationRepository = previousConfiguration;
                    return false;
                }
            }
        }
        if(Settings._DEBUG)
        {
            Settings.log.debug("Settings.configRefreshCheck: returning false");
        }
        return false;
    }
    
    private void doConfigKeyByValueCache(final URI subjectKey, final URI propertyKey, final Collection<Value> newObject)
    {
        if(newObject == null)
        {
            throw new RuntimeException("Cannot cache null property items subjectKey=" + subjectKey + " propertyKey="
                    + propertyKey);
        }
        
        if(this.configPropertiesCacheByValue == null)
        {
            synchronized(this)
            {
                if(this.configPropertiesCacheByValue == null)
                {
                    this.configPropertiesCacheByValue = new ConcurrentHashMap<URI, Map<URI, Collection<Value>>>(200);
                }
            }
        }
        
        if(this.configPropertiesCacheByValue.containsKey(subjectKey))
        {
            final Map<URI, Collection<Value>> currentCache = this.configPropertiesCacheByValue.get(subjectKey);
            
            if(currentCache == null)
            {
                throw new RuntimeException("Found a null cache item for subjectKey=" + subjectKey);
            }
            else if(!currentCache.containsKey(propertyKey))
            {
                currentCache.put(propertyKey, newObject);
                // log.trace("Settings.doConfigKeyCache: Added new cache property item for subjectKey="+subjectKey+" propertyKey="+propertyKey);
            }
            else if(Settings._TRACE)
            {
                Settings.log.trace("Settings.doConfigKeyCache: Already cached item for subjectKey=" + subjectKey
                        + " propertyKey=" + propertyKey);
            }
        }
        else
        {
            final Map<URI, Collection<Value>> newCache = new ConcurrentHashMap<URI, Collection<Value>>();
            newCache.put(propertyKey, newObject);
            this.configPropertiesCacheByValue.put(subjectKey, newCache);
            // log.trace("Settings.doConfigKeyCache: New cached item for subjectKey="+subjectKey+" propertyKey="+propertyKey);
        }
    }
    
    @Override
    public Map<URI, NamespaceEntry> getAllNamespaceEntries()
    {
        return this.getAllNamespaceEntries(true);
    }
    
    public Map<URI, NamespaceEntry> getAllNamespaceEntries(final boolean useCache)
    {
        if(this.cachedNamespaceEntries == null)
        {
            synchronized(this)
            {
                if(this.cachedNamespaceEntries == null)
                {
                    try
                    {
                        final Map<URI, NamespaceEntry> results =
                                RdfUtils.getNamespaceEntries(this.getServerConfigurationRdf());
                        
                        if(Settings._INFO)
                        {
                            Settings.log.info("Settings.getAllNamespaceEntries: found " + results.size()
                                    + " namespaces");
                        }
                        
                        for(final URI nextNamespaceEntryUri : results.keySet())
                        {
                            final NamespaceEntry nextNamespaceEntryConfiguration = results.get(nextNamespaceEntryUri);
                            
                            this.addNamespaceEntryAndPrefix(nextNamespaceEntryUri, nextNamespaceEntryConfiguration);
                        }
                        
                        this.cachedNamespaceEntries = results;
                        
                        return Collections.unmodifiableMap(this.cachedNamespaceEntries);
                    }
                    catch(final java.lang.InterruptedException ie)
                    {
                        Settings.log
                                .error("Settings.getAllNamespaceEntries: caught java.lang.InterruptedException: not throwing it.",
                                        ie);
                        
                        return null;
                    }
                }
            }
        }
        return Collections.unmodifiableMap(this.cachedNamespaceEntries);
    }
    
    @Override
    public Map<URI, NormalisationRule> getAllNormalisationRules()
    {
        return this.getAllNormalisationRules(true);
    }
    
    public synchronized Map<URI, NormalisationRule> getAllNormalisationRules(final boolean useCache)
    {
        if(this.cachedNormalisationRules == null)
        {
            synchronized(this)
            {
                if(this.cachedNormalisationRules == null)
                {
                    try
                    {
                        final Repository myRepository = this.getServerConfigurationRdf();
                        
                        final Map<URI, NormalisationRule> results = RdfUtils.getNormalisationRules(myRepository);
                        
                        if(Settings._INFO)
                        {
                            Settings.log.info("Settings.getAllNormalisationRules: found " + results.size()
                                    + " normalisation rules");
                        }
                        
                        this.cachedNormalisationRules = results;
                        
                    }
                    catch(final java.lang.InterruptedException ie)
                    {
                        Settings.log
                                .error("Settings.getAllNormalisationRules: caught java.lang.InterruptedException: not throwing it.",
                                        ie);
                        
                        return null;
                    }
                }
            }
        }
        
        return Collections.unmodifiableMap(this.cachedNormalisationRules);
    }
    
    @Override
    public Map<URI, Profile> getAllProfiles()
    {
        return this.getAllProfiles(true);
    }
    
    public Map<URI, Profile> getAllProfiles(final boolean useCache)
    {
        if(this.cachedProfiles == null)
        {
            synchronized(this)
            {
                if(this.cachedProfiles == null)
                {
                    try
                    {
                        final Repository myRepository = this.getServerConfigurationRdf();
                        
                        final Map<URI, Profile> results = RdfUtils.getProfiles(myRepository);
                        
                        if(Settings._INFO)
                        {
                            Settings.log.info("Settings.getAllProfiles: found " + results.size() + " profiles");
                        }
                        
                        this.cachedProfiles = results;
                        
                    }
                    catch(final java.lang.InterruptedException ie)
                    {
                        Settings.log.error(
                                "Settings.getAllProfiles: caught java.lang.InterruptedException: not throwing it.", ie);
                        
                        return null;
                    }
                }
            }
        }
        
        return Collections.unmodifiableMap(this.cachedProfiles);
    }
    
    @Override
    public Map<URI, Provider> getAllProviders()
    {
        return this.getAllProviders(true);
    }
    
    public Map<URI, Provider> getAllProviders(final boolean useCache)
    {
        if(this.cachedProviders == null)
        {
            synchronized(this)
            {
                if(this.cachedProviders == null)
                {
                    Map<URI, Provider> results = null;
                    
                    try
                    {
                        final Repository myRepository = this.getServerConfigurationRdf();
                        
                        results = RdfUtils.getProviders(myRepository);
                        
                        this.cachedProviders = results;
                        
                        if(Settings._INFO)
                        {
                            Settings.log.info("Settings.getAllProviders: found " + results.size() + " providers");
                        }
                        
                        return Collections.unmodifiableMap(this.cachedProviders);
                    }
                    catch(final java.lang.InterruptedException ie)
                    {
                        Settings.log
                                .error("Settings.getAllProviders: caught java.lang.InterruptedException: not throwing it.",
                                        ie);
                        
                        return null;
                    }
                }
            }
        }
        
        return Collections.unmodifiableMap(this.cachedProviders);
    }
    
    @Override
    public Map<URI, QueryType> getAllQueryTypes()
    {
        return this.getAllQueryTypes(true);
    }
    
    public Map<URI, QueryType> getAllQueryTypes(final boolean useCache)
    {
        if(this.cachedQueryTypes == null)
        {
            synchronized(this)
            {
                if(this.cachedQueryTypes == null)
                {
                    try
                    {
                        final Repository myRepository = this.getServerConfigurationRdf();
                        
                        final Map<URI, QueryType> results = RdfUtils.getQueryTypes(myRepository);
                        
                        if(Settings._INFO)
                        {
                            Settings.log.info("Settings.getAllQueryTypes: found " + results.size() + " queries");
                        }
                        
                        this.cachedQueryTypes = results;
                    }
                    catch(final java.lang.InterruptedException ie)
                    {
                        Settings.log.error(
                                "Settings.getAllQueryTypes: caught java.lang.InterruptedException: not throwing it.",
                                ie);
                        
                        return null;
                    }
                }
            }
        }
        
        return Collections.unmodifiableMap(this.cachedQueryTypes);
    }
    
    @Override
    public Map<URI, RuleTest> getAllRuleTests()
    {
        return this.getAllRuleTests(true);
    }
    
    public Map<URI, RuleTest> getAllRuleTests(final boolean useCache)
    {
        if(this.cachedRuleTests == null)
        {
            synchronized(this)
            {
                if(this.cachedRuleTests == null)
                {
                    try
                    {
                        final Repository myRepository = this.getServerConfigurationRdf();
                        
                        final Map<URI, RuleTest> results = RdfUtils.getRuleTests(myRepository);
                        
                        if(Settings._INFO)
                        {
                            Settings.log.info("Settings.getAllRuleTests: found " + results.size() + " rule tests");
                        }
                        
                        this.cachedRuleTests = results;
                        
                    }
                    catch(final java.lang.InterruptedException ie)
                    {
                        Settings.log
                                .error("Settings.getAllRuleTests: caught java.lang.InterruptedException: not throwing it.",
                                        ie);
                        
                        return null;
                    }
                }
            }
        }
        
        return Collections.unmodifiableMap(this.cachedRuleTests);
    }
    
    public String getBaseConfigLocation()
    {
        return this.baseConfigLocation;
    }
    
    /**
     * 
     * Uses the key "queryall.BaseConfigMimeFormat"
     * 
     * @return The mime format of the base config file, defaults to "text/rdf+n3"
     */
    public String getBaseConfigMimeFormat()
    {
        return this.baseConfigMimeFormat;
    }
    
    private Repository getBaseConfigurationRdf() throws java.lang.InterruptedException
    {
        if(Settings._TRACE)
        {
            Settings.log.trace("Settings.getBaseConfigurationRdf: entering method");
        }
        
        if(this.currentBaseConfigurationRepository == null)
        {
            synchronized(this)
            {
                if(this.currentBaseConfigurationRepository == null)
                {
                    
                    if(Settings._DEBUG)
                    {
                        Settings.log.debug("Settings.getBaseConfigurationRdf: constructing a new repository");
                    }
                    
                    final long start = System.currentTimeMillis();
                    final String configMIMEFormat = this.getBaseConfigMimeFormat();
                    final String baseURI = this.getBaseConfigUri();
                    Repository tempConfigurationRepository = null;
                    
                    try
                    {
                        tempConfigurationRepository = new SailRepository(new MemoryStore());
                        tempConfigurationRepository.initialize();
                        
                        if(Settings._DEBUG)
                        {
                            Settings.log.debug("Settings.getBaseConfigurationRdf: temp repository initialised");
                        }
                        
                        // Settings.log.error("Settings.getBaseConfigurationRdf: Settings.WEBAPP_CONFIG_LOCATION_LIST.size()="+Settings.WEBAPP_CONFIG_LOCATION_LIST);
                        
                        final RepositoryConnection myRepositoryConnection = tempConfigurationRepository.getConnection();
                        
                        final String nextLocation = this.getBaseConfigLocation();
                        final InputStream nextInputStream = this.getClass().getResourceAsStream(nextLocation);
                        
                        if(nextInputStream == null)
                        {
                            throw new QueryAllRuntimeException("Was not able to find base config location nextLocation="+nextLocation);
                        }
                        
                        try
                        {
                            if(Settings._INFO)
                            {
                                Settings.log
                                        .info("Settings.getBaseConfigurationRdf: getting configuration from file: nextLocation="
                                                + nextLocation + " nextInputStream=" + nextInputStream);
                            }
                            
                            myRepositoryConnection.add(nextInputStream, baseURI,
                                    RDFFormat.forMIMEType(configMIMEFormat));
                            if(Settings._INFO)
                            {
                                Settings.log
                                        .info("Settings.getBaseConfigurationRdf: finished getting configuration from file: nextLocation="
                                                + nextLocation);
                            }
                        }
                        catch(final RDFParseException rdfpe)
                        {
                            Settings.log.error(
                                    "Settings.getBaseConfigurationRdf: failed to get the configuration repository. Caught RDFParseException. nextLocation="
                                            + nextLocation, rdfpe);
                            throw new RuntimeException(
                                    "Settings.getBaseConfigurationRdf: failed to initialise the configuration repository. Caught RDFParseException. nextLocation="
                                            + nextLocation);
                        }
                        catch(final OpenRDFException ordfe)
                        {
                            Settings.log
                                    .error("Settings.getBaseConfigurationRdf: failed to initialise the base configuration repository. Caught OpenRDFException. nextLocation="
                                            + nextLocation, ordfe);
                            throw new RuntimeException(
                                    "Settings.getBaseConfigurationRdf: failed to initialise the base configuration repository. Caught OpenRDFException. nextLocation="
                                            + nextLocation);
                        }
                        catch(final java.io.IOException ioe)
                        {
                            Settings.log
                                    .error("Settings.getBaseConfigurationRdf: failed to initialise the base configuration repository. Caught java.io.IOException. nextLocation="
                                            + nextLocation, ioe);
                            throw new RuntimeException(
                                    "Settings.getBaseConfigurationRdf: failed to initialise the base configuration repository. Caught java.io.IOException. nextLocation="
                                            + nextLocation);
                        }
                        finally
                        {
                            if(myRepositoryConnection != null)
                            {
                                myRepositoryConnection.close();
                            }
                        }
                    }
                    catch(final OpenRDFException ordfe)
                    {
                        Settings.log
                                .error("Settings.getBaseConfigurationRdf: failed to initialise the webapp configuration repository. Caught OpenRDFException",
                                        ordfe);
                        throw new RuntimeException(
                                "Settings.getBaseConfigurationRdf: failed to initialise the webapp configuration repository. Caught OpenRDFException");
                    }
                    
                    this.currentBaseConfigurationRepository = tempConfigurationRepository;
                    
                    if(Settings._INFO)
                    {
                        final long end = System.currentTimeMillis();
                        Settings.log.info(String.format("%s: timing=%10d", "Settings.getBaseConfigurationRdf",
                                (end - start)));
                        
                    }
                    
                    if(Settings._DEBUG)
                    {
                        Settings.log.debug("Settings.getBaseConfigurationRdf: finished parsing configuration files");
                    }
                    
                    if(Settings._INFO)
                    {
                        try
                        {
                            Settings.log.info("Settings.getBaseConfigurationRdf: found "
                                    + this.currentBaseConfigurationRepository.getConnection().size()
                                    + " statements in base configuration");
                        }
                        catch(final RepositoryException rex)
                        {
                            Settings.log
                                    .error("Settings.getBaseConfigurationRdf: could not determine the number of statements in webapp configuration");
                        }
                    }
                    
                    if(Settings._TRACE)
                    {
                        try
                        {
                            for(final Statement nextStatement : RdfUtils
                                    .getAllStatementsFromRepository(this.currentBaseConfigurationRepository))
                            {
                                Settings.log.trace(nextStatement.toString());
                            }
                        }
                        catch(final Exception ex)
                        {
                            Settings.log.error("Could not dump statements", ex);
                        }
                    }
                }
            }
        }
        
        return this.currentBaseConfigurationRepository;
    }
    
    private String getBaseConfigUri()
    {
        return this.baseConfigUri;
    }
    
    @Override
    public boolean getBooleanProperty(final String key, final boolean defaultValue)
    {
        if(Settings._TRACE)
        {
            Settings.log.trace("Settings.getBooleanPropertyFromConfig: key=" + key + " defaultValue=" + defaultValue);
        }
        
        boolean result = defaultValue;
        
        final Collection<Value> values = this.getValueProperties(key);
        
        if(values.size() != 1)
        {
            Settings.log.error("Settings.getBooleanPropertyFromConfig: Did not find a unique result for key=" + key
                    + " values.size()=" + values.size() + " defaultValue=" + defaultValue);
            return defaultValue;
        }
        
        for(final Value nextValue : values)
        {
            result = RdfUtils.getBooleanFromValue(nextValue);
        }
        
        if(Settings._TRACE)
        {
            Settings.log.trace("Settings.getBooleanPropertyFromConfig: key=" + key + " result=" + result);
        }
        
        return result;
    }
    
    private Collection<Value> getConfigKeyByValueCached(final URI subjectKey, final URI propertyKey)
    {
        if(this.configPropertiesCacheByValue != null && this.configPropertiesCacheByValue.containsKey(subjectKey))
        {
            final Map<URI, Collection<Value>> currentCache = this.configPropertiesCacheByValue.get(subjectKey);
            
            if(currentCache == null)
            {
                // log.info("Settings.getConfigKeyCached: Found subjectKey, but no entry for propertyKey="+propertyKey);
                return null;
            }
            else if(currentCache.containsKey(propertyKey))
            {
                final Collection<Value> currentCacheObject = currentCache.get(propertyKey);
                if(currentCacheObject == null)
                {
                    Settings.log.error("Settings.getConfigKeyCached: Cache contained a null object for propertyKey="
                            + propertyKey);
                }
                else
                {
                    // log.debug("Settings.getConfigKeyCached: Returning cached object for propertyKey="+propertyKey);
                    return currentCacheObject;
                }
            }
        }
        
        return null;
    }
    
    @Override
    /**
     * Defaults to http://bio2rdf.org/ if the configuration files do not contain any of the relevant properties, 
     * or some part of that if they only contain some of the relevant properties.
     * 
     * The properties used to generate the result are {uriPrefix}{hostName}{uriSuffix}
     */
    public String getDefaultHostAddress()
    {
        return this.getStringProperty("uriPrefix", "http://") + this.getStringProperty("hostName", "bio2rdf.org")
                + this.getStringProperty("uriSuffix", "/");
    }
    
    @Override
    public float getFloatProperty(final String key, final float defaultValue)
    {
        float result = defaultValue;
        
        if(Settings._TRACE)
        {
            Settings.log.trace("Settings.getFloatPropertyFromConfig: key=" + key + " defaultValue=" + defaultValue);
        }
        
        final Collection<Value> values = this.getValueProperties(key);
        
        if(values.size() != 1)
        {
            Settings.log.error("Settings.getFloatPropertyFromConfig: Did not find a unique result for key=" + key
                    + " values.size()=" + values.size() + " defaultValue=" + defaultValue);
            return defaultValue;
        }
        
        for(final Value nextValue : values)
        {
            result = RdfUtils.getFloatFromValue(nextValue);
        }
        
        if(Settings._TRACE)
        {
            Settings.log.trace("Settings.getFloatPropertyFromConfig: key=" + key + " result=" + result);
        }
        
        return result;
    }
    
    @Override
    public int getIntProperty(final String key, final int defaultValue)
    {
        int result = defaultValue;
        
        if(Settings._TRACE)
        {
            Settings.log.trace("Settings.getIntPropertyFromConfig: key=" + key + " defaultValue=" + defaultValue);
        }
        
        final Collection<Value> values = this.getValueProperties(key);
        
        if(values.size() != 1)
        {
            Settings.log.error("Settings.getIntPropertyFromConfig: Did not find a unique result for key=" + key
                    + " values.size()=" + values.size() + " defaultValue=" + defaultValue);
            return defaultValue;
        }
        
        for(final Value nextValue : values)
        {
            result = RdfUtils.getIntegerFromValue(nextValue);
        }
        
        if(Settings._TRACE)
        {
            Settings.log.trace("Settings.getIntPropertyFromConfig: key=" + key + " result=" + result);
        }
        
        return result;
    }
    
    @Override
    public long getLongProperty(final String key, final long defaultValue)
    {
        long result = defaultValue;
        
        if(Settings._TRACE)
        {
            Settings.log.trace("Settings.getLongPropertyFromConfig: key=" + key + " defaultValue=" + defaultValue);
        }
        
        final Collection<Value> values = this.getValueProperties(key);
        
        if(values.size() != 1)
        {
            Settings.log.error("Settings.getLongPropertyFromConfig: Did not find a unique result for key=" + key
                    + " values.size()=" + values.size() + " defaultValue=" + defaultValue);
            return defaultValue;
        }
        
        for(final Value nextValue : values)
        {
            result = RdfUtils.getLongFromValue(nextValue);
        }
        
        if(Settings._TRACE)
        {
            Settings.log.trace("Settings.getLongPropertyFromConfig: key=" + key + " result=" + result);
        }
        
        return result;
    }
    
    /**
     * @return the cachedNamespacePrefixToUriEntries
     */
    @Override
    public Map<String, Collection<URI>> getNamespacePrefixesToUris()
    {
        if(this.cachedNamespacePrefixToUriEntries == null)
        {
            synchronized(this)
            {
                this.getAllNamespaceEntries();
            }
        }
        
        return this.cachedNamespacePrefixToUriEntries;
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
                this.separator = this.getStringProperty("separator", ":");
            }
        }
        
        return this.separator;
    }
    
    private Repository getServerConfigurationRdf() throws java.lang.InterruptedException
    {
        if(this.currentConfigurationRepository == null)
        {
            synchronized(this)
            {
                if(this.currentConfigurationRepository == null)
                {
                    final long start = System.currentTimeMillis();
                    final String configMIMEFormat = this.getBaseConfigMimeFormat();
                    final String baseURI = this.getDefaultHostAddress();
                    Repository tempConfigurationRepository = null;
                    boolean backupNeeded = false;
                    final boolean backupFailed = false;
                    
                    try
                    {
                        // start off with the schemas in the repository
                        tempConfigurationRepository = new SailRepository(new MemoryStore());
                        tempConfigurationRepository.initialize();
                        
                        tempConfigurationRepository =
                                Schema.getSchemas(tempConfigurationRepository, Settings.CONFIG_API_VERSION);
                        
                        final Collection<String> queryConfigLocationsList =
                                this.getStringProperties("queryConfigLocations");
                        
                        if(queryConfigLocationsList == null)
                        {
                            Settings.log.error("queryConfigLocationsList was null");
                            throw new RuntimeException("Configuration locations were not discovered, failing fast.");
                        }
                        
                        for(final String nextLocation : queryConfigLocationsList)
                        {
                            // TODO: negotiate between local and non-local addresses better
                            // than this
                            final RepositoryConnection myRepositoryConnection =
                                    tempConfigurationRepository.getConnection();
                            try
                            {
                                if(nextLocation.startsWith("http://") || nextLocation.startsWith("https://"))
                                {
                                    // final URL url = new
                                    // URL("http://quebec.bio2rdf.org/n3/provider:mirroredgeneid");
                                    final URL url = new URL(nextLocation);
                                    
                                    if(Settings._INFO)
                                    {
                                        Settings.log
                                                .info("Settings.getServerConfigurationRdf: getting configuration from URL: nextLocation="
                                                        + nextLocation + " url=" + url.toString());
                                    }
                                    
                                    myRepositoryConnection.add(url, baseURI, RDFFormat.forMIMEType(configMIMEFormat));
                                    
                                    if(Settings._INFO)
                                    {
                                        Settings.log
                                                .info("Settings.getServerConfigurationRdf: finished getting configuration from URL: url="
                                                        + url.toString());
                                    }
                                }
                                else
                                {
                                    if(Settings._INFO)
                                    {
                                        Settings.log.info("Settings: getting configuration from file: nextLocation="
                                                + nextLocation);
                                    }
                                    final InputStream nextInputStream =
                                            this.getClass().getResourceAsStream(nextLocation);
                                    
                                    myRepositoryConnection.add(nextInputStream, baseURI,
                                            RDFFormat.forMIMEType(configMIMEFormat));
                                    if(Settings._INFO)
                                    {
                                        Settings.log
                                                .info("Settings: finished getting configuration from file: nextLocation="
                                                        + nextLocation);
                                    }
                                }
                            }
                            catch(final RDFParseException rdfpe)
                            {
                                Settings.log.error(
                                        "Settings: failed to get the configuration repository. Caught RDFParseException. nextLocation="
                                                + nextLocation, rdfpe);
                                throw new RuntimeException(
                                        "Settings: failed to initialise the configuration repository. Caught RDFParseException. nextLocation="
                                                + nextLocation);
                            }
                            catch(final OpenRDFException ordfe)
                            {
                                Settings.log.error(
                                        "Settings: failed to initialise the configuration repository. Caught OpenRDFException. nextLocation="
                                                + nextLocation, ordfe);
                                throw new RuntimeException(
                                        "Settings: failed to initialise the configuration repository. Caught OpenRDFException. nextLocation="
                                                + nextLocation);
                            }
                            catch(final java.io.IOException ioe)
                            {
                                Settings.log.error(
                                        "Settings: failed to initialise the configuration repository. Caught java.io.IOException. nextLocation="
                                                + nextLocation, ioe);
                                // throw new
                                // RuntimeException("Settings: failed to initialise the configuration repository. Caught java.io.IOException");
                                backupNeeded = true;
                            }
                            finally
                            {
                                if(myRepositoryConnection != null)
                                {
                                    myRepositoryConnection.close();
                                }
                            }
                        }
                    }
                    catch(final OpenRDFException ordfe)
                    {
                        Settings.log.error(
                                "Settings: failed to initialise the configuration repository. Caught OpenRDFException",
                                ordfe);
                        throw new RuntimeException(
                                "Settings: failed to initialise the configuration repository. Caught OpenRDFException");
                    }
                    
                    if(backupNeeded)
                    {
                        // Try again with the backup configuration list...
                        try
                        {
                            tempConfigurationRepository = new SailRepository(new MemoryStore());
                            tempConfigurationRepository.initialize();
                            
                            tempConfigurationRepository =
                                    Schema.getSchemas(tempConfigurationRepository, Settings.CONFIG_API_VERSION);
                            
                            for(final String nextLocation : this.getStringProperties("backupQueryConfigLocations"))
                            {
                                // TODO: negotiate between local and non-local addresses better than
                                // this
                                final RepositoryConnection myRepositoryConnection =
                                        tempConfigurationRepository.getConnection();
                                try
                                {
                                    if(nextLocation.startsWith("http://") || nextLocation.startsWith("https://"))
                                    {
                                        // final URL url = new
                                        // URL("http://quebec.bio2rdf.org/n3/provider:mirroredgeneid");
                                        final URL url = new URL(nextLocation);
                                        
                                        if(Settings._INFO)
                                        {
                                            Settings.log
                                                    .info("Settings.getServerConfigurationRdf: getting backup configuration from URL: nextLocation="
                                                            + nextLocation + " url=" + url.toString());
                                        }
                                        
                                        myRepositoryConnection.add(url, baseURI,
                                                RDFFormat.forMIMEType(configMIMEFormat));
                                        
                                        if(Settings._INFO)
                                        {
                                            Settings.log
                                                    .info("Settings.getServerConfigurationRdf: finished getting backup configuration from URL: url="
                                                            + url.toString());
                                        }
                                    }
                                    else
                                    {
                                        if(Settings._INFO)
                                        {
                                            Settings.log
                                                    .info("Settings: getting backup configuration from file: nextLocation="
                                                            + nextLocation);
                                        }
                                        final InputStream nextInputStream =
                                                this.getClass().getResourceAsStream(nextLocation);
                                        
                                        myRepositoryConnection.add(nextInputStream, baseURI,
                                                RDFFormat.forMIMEType(configMIMEFormat));
                                        if(Settings._INFO)
                                        {
                                            Settings.log
                                                    .info("Settings: finished getting backup configuration from file: nextLocation="
                                                            + nextLocation);
                                        }
                                    }
                                }
                                catch(final RDFParseException rdfpe)
                                {
                                    Settings.log
                                            .error("Settings: failed to get the backup configuration repository. Caught RDFParseException",
                                                    rdfpe);
                                    throw new RuntimeException(
                                            "Settings: failed to initialise the backup configuration repository. Caught RDFParseException");
                                }
                                catch(final OpenRDFException ordfe)
                                {
                                    Settings.log
                                            .error("Settings: failed to initialise the backup configuration repository. Caught OpenRDFException",
                                                    ordfe);
                                    throw new RuntimeException(
                                            "Settings: failed to initialise the backup configuration repository. Caught OpenRDFException");
                                }
                                catch(final java.io.IOException ioe)
                                {
                                    Settings.log
                                            .error("Settings: failed to initialise the backup configuration repository. Caught java.io.IOException",
                                                    ioe);
                                    throw new RuntimeException(
                                            "Settings: failed to initialise the backup configuration repository. Caught java.io.IOException");
                                }
                                finally
                                {
                                    if(myRepositoryConnection != null)
                                    {
                                        myRepositoryConnection.close();
                                    }
                                }
                            }
                        }
                        catch(final OpenRDFException ordfe)
                        {
                            Settings.log
                                    .error("Settings: failed to initialise the backup configuration repository. Caught OpenRDFException",
                                            ordfe);
                            throw new RuntimeException(
                                    "Settings: failed to initialise the backup configuration repository. Caught OpenRDFException");
                        }
                    } // end if(backupNeeded)
                    
                    this.currentConfigurationRepository = tempConfigurationRepository;
                    
                    if(Settings._INFO)
                    {
                        final long end = System.currentTimeMillis();
                        Settings.log.info(String.format("%s: timing=%10d", "Settings.getServerConfigurationRdf",
                                (end - start)));
                        
                    }
                    if(Settings._DEBUG)
                    {
                        Settings.log.debug("Settings.getServerConfigurationRdf: finished parsing configuration files");
                    }
                    
                    if(Settings._INFO)
                    {
                        try
                        {
                            Settings.log.info("Settings: found "
                                    + this.currentConfigurationRepository.getConnection().size()
                                    + " statements in model configuration");
                        }
                        catch(final RepositoryException rex)
                        {
                            Settings.log
                                    .error("Settings: could not determine the number of statements in configuration");
                        }
                    }
                }
            }
        }
        
        return this.currentConfigurationRepository;
    }
    
    public Collection<Statement> getStatementProperties(final String key)
    {
        if(Settings._TRACE)
        {
            Settings.log.trace("Settings.getStatementPropertiesFromConfig: key=" + key);
        }
        
        final Collection<Statement> results = new HashSet<Statement>();
        
        try
        {
            final Repository webappConfig = this.getWebAppConfigurationRdf();
            
            final ValueFactory f = webappConfig.getValueFactory();
            
            // TODO: in future should reform this to accept a full URI as the
            // key so properties outside of the queryall vocabulary can be used
            // for properties
            final URI propertyUri = f.createURI(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), key);
            
            // if(Settings._TRACE)
            // {
            // Settings.log.trace("Settings.getStatementPropertiesFromConfig: WEBAPP_CONFIG_URI_LIST.size()="
            // + this.getWebappConfigUriList().size());
            // }
            
            for(final String nextConfigUri : this.getWebappConfigUriList())
            {
                final URI configUri = f.createURI(nextConfigUri);
                
                if(Settings._TRACE)
                {
                    Settings.log.trace("Settings.getStatementPropertiesFromConfig: configUri="
                            + configUri.stringValue() + " propertyUri=" + propertyUri.stringValue());
                }
                
                results.addAll(this.getStatementProperties(configUri, propertyUri, webappConfig));
            }
        }
        catch(final Exception ex)
        {
            Settings.log.error("Settings.getStatementPropertiesFromConfig: error", ex);
        }
        
        return results;
    }
    
    private Collection<Statement> getStatementProperties(final URI subjectUri, final URI propertyUri,
            final Repository nextRepository)
    {
        if(Settings._TRACE)
        {
            Settings.log.trace("Settings.getStatementCollectionPropertiesFromConfig: subjectUri="
                    + subjectUri.stringValue() + " propertyUri=" + propertyUri.stringValue() + " nextRepository="
                    + nextRepository);
        }
        
        try
        {
            return RdfUtils.getStatementsFromRepositoryByPredicateUrisAndSubject(nextRepository, propertyUri,
                    subjectUri);
        }
        catch(final Exception ex)
        {
            Settings.log.error("Settings.getStatementCollectionPropertiesFromConfig: error", ex);
        }
        
        return new HashSet<Statement>();
    }
    
    @Override
    public Collection<String> getStringProperties(final String key)
    {
        if(Settings._TRACE)
        {
            Settings.log.trace("Settings.getStringCollectionPropertiesFromConfig: key=" + key);
        }
        
        final Collection<Value> values = this.getValueProperties(key);
        
        final Collection<String> results = new ArrayList<String>(values.size());
        
        for(final Value nextValue : values)
        {
            results.add(nextValue.stringValue());
            // results.add(RdfUtils.getUTF8StringValueFromSesameValue(nextValue));
        }
        
        return results;
    }
    
    @Override
    public String getStringProperty(final String key, final String defaultValue)
    {
        String result = defaultValue;
        
        if(Settings._TRACE)
        {
            Settings.log.trace("Settings.getStringPropertyFromConfig: key=" + key + " defaultValue=" + defaultValue);
        }
        
        final Collection<String> values = this.getStringProperties(key);
        
        if(values.size() != 1)
        {
            Settings.log.error("Settings.getStringPropertyFromConfig: Did not find a unique result for key=" + key
                    + " values.size()=" + values.size() + " defaultValue=" + defaultValue);
            return defaultValue;
        }
        
        for(final String nextValue : values)
        {
            result = nextValue;
        }
        
        if(Settings._TRACE)
        {
            Settings.log.trace("Settings.getStringPropertyFromConfig: key=" + key + " defaultValue=" + defaultValue
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
            Settings.log.trace("Settings.getURICollectionPropertiesFromConfig: key=" + key);
        }
        
        final Collection<URI> results = new HashSet<URI>();
        
        for(final Value nextValue : this.getValueProperties(key))
        {
            if(nextValue instanceof URI)
            {
                results.add((URI)nextValue);
            }
            else
            {
                Settings.log
                        .error("Settings.getURICollectionPropertiesFromConfig: nextValue was not an instance of URI key="
                                + key + " nextValue=" + nextValue);
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
            Settings.log.trace("Settings.getUriPropertyFromConfig: key=" + key + " defaultValue=" + defaultValue);
        }
        
        final Collection<URI> values = this.getURIProperties(key);
        
        if(values.size() != 1)
        {
            Settings.log.warn("Settings.getUriPropertyFromConfig: Did not find a unique result for key=" + key
                    + " values.size()=" + values.size() + " defaultValue=" + defaultValue);
            return defaultValue;
        }
        
        for(final URI nextValue : values)
        {
            result = nextValue;
        }
        
        if(Settings._TRACE)
        {
            Settings.log.trace("Settings.getUriPropertyFromConfig: key=" + key + " result=" + result);
        }
        
        return result;
        
    }
    
    private Collection<Value> getValueProperties(final String key)
    {
        if(Settings._TRACE)
        {
            Settings.log.trace("Settings.getValueCollectionPropertiesFromConfig: key=" + key);
        }
        
        final Collection<Value> results = new HashSet<Value>();
        
        try
        {
            final ValueFactory f = this.getWebAppConfigurationRdf().getValueFactory();
            
            // XXX: in future should reform this to accept a full URI as the key
            // so properties outside of the queryall vocabulary can be used for
            // properties
            final URI propertyUri = f.createURI(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), key);
            
            // if(Settings._TRACE)
            // {
            // Settings.log.trace("Settings.getValueCollectionPropertiesFromConfig: getWebappConfigUriList().size()="
            // + this.getWebappConfigUriList().size());
            // }
            
            for(final String nextConfigUri : this.getWebappConfigUriList())
            {
                final URI configUri = f.createURI(nextConfigUri);
                
                if(Settings._TRACE)
                {
                    Settings.log.trace("Settings.getValueCollectionPropertiesFromConfig: configUri="
                            + configUri.stringValue() + " propertyUri=" + propertyUri.stringValue());
                }
                
                results.addAll(this.getValueProperties(configUri, propertyUri));
            }
        }
        catch(final InterruptedException ex)
        {
            Settings.log.error("Settings.getValueCollectionPropertiesFromConfig: InterruptedException", ex);
        }
        
        return results;
    }
    
    private Collection<Value> getValueProperties(final URI subjectUri, final URI propertyUri)
        throws InterruptedException
    {
        if(Settings._TRACE)
        {
            Settings.log.trace("Settings.getValueCollectionPropertiesFromConfig: subjectUri="
                    + subjectUri.stringValue() + " propertyUri=" + propertyUri.stringValue());
        }
        
        final Collection<Value> cachedResults = this.getConfigKeyByValueCached(subjectUri, propertyUri);
        Collection<Value> results = new HashSet<Value>();
        
        if(cachedResults != null)
        {
            if(Settings._TRACE)
            {
                Settings.log
                        .trace("Settings.getValueCollectionPropertiesFromConfig: returning cached values subjectUri="
                                + subjectUri.stringValue() + " propertyUri=" + propertyUri.stringValue());
            }
            
            return cachedResults;
        }
        else
        {
            try
            {
                results =
                        RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject(this.getWebAppConfigurationRdf(),
                                propertyUri, subjectUri);
                
                if(results != null)
                {
                    this.doConfigKeyByValueCache(subjectUri, propertyUri, results);
                }
            }
            catch(final InterruptedException ex)
            {
                throw ex;
            }
            catch(final OpenRDFException ex)
            {
                throw new RuntimeException(ex);
            }
        }
        
        return results;
    }
    
    private Repository getWebAppConfigurationRdf() throws java.lang.InterruptedException
    {
        if(Settings._TRACE)
        {
            Settings.log.trace("Settings.getWebAppConfigurationRdf: entering");
        }
        
        if(this.currentWebAppConfigurationRepository == null)
        {
            synchronized(this)
            {
                if(this.currentWebAppConfigurationRepository == null)
                {
                    // null out this optimisation cached property
                    this.cachedTagPattern = null;
                    this.configPropertiesCacheByValue = null;
                    this.separator = null;
                    
                    if(Settings._DEBUG)
                    {
                        Settings.log.debug("Settings.getWebAppConfigurationRdf: constructing a new repository");
                    }
                    
                    final long start = System.currentTimeMillis();
                    final Repository nextBaseConfigurationRepository = this.getBaseConfigurationRdf();
                    final String configMIMEFormat = this.getBaseConfigMimeFormat();
                    final String baseURI = this.getBaseConfigUri();
                    Repository tempConfigurationRepository = null;
                    Repository finalConfigurationRepository = null;
                    boolean backupNeeded = false;
                    final boolean backupFailed = false;
                    
                    RepositoryConnection finalRepositoryConnection = null;
                    
                    try
                    {
                        finalConfigurationRepository = new SailRepository(new MemoryStore());
                        finalConfigurationRepository.initialize();
                        
                        finalRepositoryConnection = finalConfigurationRepository.getConnection();
                        
                        if(Settings._DEBUG)
                        {
                            Settings.log.debug("Settings.getWebAppConfigurationRdf: temp repository initialised");
                        }
                        
                        // Settings.log.error("Settings.getWebAppConfigurationRdf: Settings.WEBAPP_CONFIG_LOCATION_LIST.size()="+Settings.WEBAPP_CONFIG_LOCATION_LIST);
                        
                        final ValueFactory f = finalConfigurationRepository.getValueFactory();
                        
                        final URI subjectConfigUri = f.createURI(baseURI);
                        
                        final URI webappConfigLocationsUri =
                                f.createURI(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "webappConfigLocations");
                        
                        final URI activeWebappConfigsUri =
                                f.createURI(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "activeWebappConfigs");
                        
                        final Collection<Value> webappConfigFiles =
                                RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject(
                                        nextBaseConfigurationRepository, webappConfigLocationsUri, subjectConfigUri);
                        
                        final Collection<Value> activeWebappConfigs =
                                RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject(
                                        nextBaseConfigurationRepository, activeWebappConfigsUri, subjectConfigUri);
                        
                        final Collection<String> tempCollection = new HashSet<String>();
                        
                        for(final Value nextValue1 : activeWebappConfigs)
                        {
                            tempCollection.add(nextValue1.stringValue());
                        }
                        
                        this.webappConfigUriList = tempCollection;
                        
                        if(Settings._DEBUG)
                        {
                            Settings.log.debug("webappConfigFiles.size()=" + webappConfigFiles.size());
                            Settings.log.debug("activeWebappConfigs.size()=" + activeWebappConfigs.size());
                        }
                        
                        // for(final String nextLocation : BASE_CONFIG_FILES.split(","))
                        for(final Value nextConfigFile : webappConfigFiles)
                        {
                            tempConfigurationRepository = new SailRepository(new MemoryStore());
                            tempConfigurationRepository.initialize();
                            
                            final String nextLocation = nextConfigFile.stringValue();
                            
                            // TODO: negotiate between local and non-local addresses better
                            // than this
                            
                            final RepositoryConnection myRepositoryConnection =
                                    tempConfigurationRepository.getConnection();
                            
                            try
                            {
                                if(nextLocation.startsWith("http://") || nextLocation.startsWith("https://"))
                                {
                                    // final URL url = new
                                    // URL("http://quebec.bio2rdf.org/n3/provider:mirroredgeneid");
                                    final URL url = new URL(nextLocation);
                                    
                                    if(Settings._INFO)
                                    {
                                        Settings.log
                                                .info("Settings.getWebAppConfigurationRdf: getting configuration from URL: nextLocation="
                                                        + nextLocation
                                                        + " url="
                                                        + url.toString()
                                                        + " myRepositoryConnection.size()="
                                                        + myRepositoryConnection.size());
                                    }
                                    
                                    myRepositoryConnection.add(url, baseURI, RDFFormat.forMIMEType(configMIMEFormat));
                                    
                                    if(Settings._INFO)
                                    {
                                        Settings.log
                                                .info("Settings.getWebAppConfigurationRdf: finished getting configuration from URL: url="
                                                        + url.toString()
                                                        + " myRepositoryConnection.size()="
                                                        + myRepositoryConnection.size());
                                    }
                                }
                                else
                                {
                                    if(Settings._INFO)
                                    {
                                        Settings.log
                                                .info("Settings.getWebAppConfigurationRdf: getting configuration from file: nextLocation="
                                                        + nextLocation);
                                    }
                                    final InputStream nextInputStream =
                                            this.getClass().getResourceAsStream(nextLocation);
                                    
                                    if(nextInputStream != null)
                                    {
                                        myRepositoryConnection.add(nextInputStream, baseURI,
                                                RDFFormat.forMIMEType(configMIMEFormat));
                                        if(Settings._INFO)
                                        {
                                            Settings.log
                                                    .info("Settings.getWebAppConfigurationRdf: finished getting configuration from file: nextLocation="
                                                            + nextLocation);
                                        }
                                    }
                                    else
                                    {
                                        Settings.log
                                                .error("Could not resolve config location to an input stream nextLocation="
                                                        + nextLocation);
                                    }
                                }
                                
                                for(final Value nextValue : activeWebappConfigs)
                                {
                                    Settings.log
                                            .debug("Settings.getWebAppConfigurationRdf: started adding statements to finalrepository for nextValue="
                                                    + nextValue.stringValue()
                                                    + " finalRepositoryConnection.size()="
                                                    + finalRepositoryConnection.size());
                                    this.webappConfigUriList.add(nextValue.stringValue());
                                    finalRepositoryConnection.add(myRepositoryConnection.getStatements((URI)nextValue,
                                            (URI)null, (Resource)null, true));
                                    Settings.log
                                            .debug("Settings.getWebAppConfigurationRdf: finished adding statements to finalrepository for nextValue="
                                                    + nextValue.stringValue()
                                                    + " finalRepositoryConnection.size()="
                                                    + finalRepositoryConnection.size());
                                }
                            }
                            catch(final RDFParseException rdfpe)
                            {
                                Settings.log
                                        .error("Settings.getWebAppConfigurationRdf: failed to get the webapp configuration repository. Caught RDFParseException. nextLocation="
                                                + nextLocation, rdfpe);
                                throw new RuntimeException(
                                        "Settings.getWebAppConfigurationRdf: failed to initialise the webapp configuration repository. Caught RDFParseException. nextLocation="
                                                + nextLocation);
                            }
                            catch(final OpenRDFException ordfe)
                            {
                                Settings.log
                                        .error("Settings.getWebAppConfigurationRdf: failed to initialise the webapp configuration repository. Caught OpenRDFException. nextLocation="
                                                + nextLocation, ordfe);
                                throw new RuntimeException(
                                        "Settings.getWebAppConfigurationRdf: failed to initialise the webapp configuration repository. Caught OpenRDFException. nextLocation="
                                                + nextLocation);
                            }
                            catch(final java.io.IOException ioe)
                            {
                                Settings.log
                                        .error("Settings.getWebAppConfigurationRdf: failed to initialise the webapp configuration repository. Caught java.io.IOException. nextLocation="
                                                + nextLocation, ioe);
                                // throw new
                                // RuntimeException("Settings: failed to initialise the configuration repository. Caught java.io.IOException");
                                backupNeeded = true;
                            }
                            finally
                            {
                                if(myRepositoryConnection != null)
                                {
                                    myRepositoryConnection.close();
                                }
                            }
                        } // end for(Value nextConfigFile : webappConfigFiles)
                    }
                    catch(final OpenRDFException ordfe)
                    {
                        Settings.log
                                .error("Settings.getWebAppConfigurationRdf: failed to initialise the webapp configuration repository. Caught OpenRDFException",
                                        ordfe);
                        throw new RuntimeException(
                                "Settings.getWebAppConfigurationRdf: failed to initialise the webapp configuration repository. Caught OpenRDFException");
                    }
                    finally
                    {
                        if(finalConfigurationRepository != null)
                        {
                            try
                            {
                                finalRepositoryConnection.close();
                            }
                            catch(final Exception ex)
                            {
                                Settings.log.error(ex.getMessage());
                            }
                        }
                    }
                    
                    if(finalConfigurationRepository != null)
                    {
                        this.currentWebAppConfigurationRepository = finalConfigurationRepository;
                    }
                    else
                    {
                        throw new RuntimeException(
                                "Settings.getWebAppConfigurationRdf: failed to initialise the webapp configuration repository");
                    }
                    
                    if(Settings._INFO)
                    {
                        final long end = System.currentTimeMillis();
                        Settings.log.info(String.format("%s: timing=%10d", "Settings.getWebAppConfigurationRdf",
                                (end - start)));
                        
                    }
                    
                    if(Settings._DEBUG)
                    {
                        Settings.log.debug("Settings.getWebAppConfigurationRdf: finished parsing configuration files");
                    }
                    
                    if(Settings._INFO)
                    {
                        try
                        {
                            Settings.log.info("Settings.getWebAppConfigurationRdf: found "
                                    + this.currentWebAppConfigurationRepository.getConnection().size()
                                    + " statements in webapp configuration");
                        }
                        catch(final RepositoryException rex)
                        {
                            Settings.log
                                    .error("Settings.getWebAppConfigurationRdf: could not determine the number of statements in webapp configuration");
                        }
                    }
                }
            }
        }
        
        return this.currentWebAppConfigurationRepository;
    }
    
    /**
     * @return the webappConfigUriList
     */
    public Collection<String> getWebappConfigUriList()
    {
        return this.webappConfigUriList;
    }
    
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
    
    @Override
    public void setProperty(final String propertyKey, final boolean propertyValue)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void setProperty(final String propertyKey, final float propertyValue)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void setProperty(final String propertyKey, final int propertyValue)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void setProperty(final String propertyKey, final long propertyValue)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void setProperty(final String propertyKey, final String propertyValue)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void setProperty(final String propertyKey, final URI propertyValue)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void setStringCollectionProperty(final String propertyKey, final Collection<String> propertyValues)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void setURICollectionProperty(final String propertyKey, final Collection<URI> propertyValues)
    {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * @param webappConfigUriList
     *            the webappConfigUriList to set
     */
    public synchronized void setWebappConfigUriList(final Collection<String> webappConfigUriList)
    {
        this.webappConfigUriList = webappConfigUriList;
        
        try
        {
            this.getWebAppConfigurationRdf();
        }
        catch(final InterruptedException ex)
        {
            Settings.log.error("Interrupted", ex);
        }
    }

    @Override
    public NamespaceEntry getNamespaceEntry(URI nextNamespaceEntryUri)
    {
        return this.cachedNamespaceEntries.get(nextNamespaceEntryUri);
    }

    @Override
    public NormalisationRule getNormalisationRule(URI nextNormalisationRuleUri)
    {
        return this.cachedNormalisationRules.get(nextNormalisationRuleUri);
    }

    @Override
    public Profile getProfile(URI nextProfileUri)
    {
        return this.cachedProfiles.get(nextProfileUri);
    }

    @Override
    public Provider getProvider(URI nextProviderUri)
    {
        return this.cachedProviders.get(nextProviderUri);
    }

    @Override
    public QueryType getQueryType(URI nextQueryTypeUri)
    {
        return this.cachedQueryTypes.get(nextQueryTypeUri);
    }

    @Override
    public RuleTest getRuleTest(URI nextRuleTestUri)
    {
        return this.cachedRuleTests.get(nextRuleTestUri);
    }
}
