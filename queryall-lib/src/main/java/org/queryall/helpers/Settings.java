package org.queryall.helpers;

import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;

import org.queryall.api.NamespaceEntry;
import org.queryall.api.NormalisationRule;
import org.queryall.api.Profile;
import org.queryall.api.Provider;
import org.queryall.api.QueryAllConfiguration;
import org.queryall.api.QueryType;
import org.queryall.api.RuleTest;
import org.queryall.impl.HttpProviderImpl;
import org.queryall.impl.NamespaceEntryImpl;
import org.queryall.impl.ProfileImpl;
import org.queryall.impl.ProjectImpl;
import org.queryall.impl.ProviderImpl;
import org.queryall.impl.QueryTypeImpl;
import org.queryall.impl.RegexNormalisationRuleImpl;
import org.queryall.impl.RuleTestImpl;
import org.queryall.impl.SparqlNormalisationRuleImpl;
import org.queryall.impl.XsltNormalisationRuleImpl;
import org.queryall.queryutils.ProvenanceRecord;
import org.queryall.queryutils.QueryBundle;
import org.queryall.statistics.StatisticsEntry;

/**
 * A class used to get access to settings
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class Settings implements QueryAllConfiguration
{
    public static final Logger log = Logger
            .getLogger(Settings.class.getName());
    public static final boolean _TRACE = Settings.log.isTraceEnabled();
    public static final boolean _DEBUG = Settings.log.isDebugEnabled();
    public static final boolean _INFO = Settings.log.isInfoEnabled();

    // This matches the queryall.properties file where
    // the generally static API specific section of the configuration settings are stored
    public static final String DEFAULT_PROPERTIES_BUNDLE_NAME = "queryall";
    public static final int CONFIG_API_VERSION = 5;
    public static final String VERSION = getVersion();
    
    public static Settings getSettings()
    {
        if(defaultSettings == null)
        {
            defaultSettings = new Settings();
        }
        
        return defaultSettings;
    }
    
    /**
     * Checks for the key first in the system vm properties, 
     * then in the localisation properties file, by default, "queryall.properties",
     * then uses the defaultValue if the location is still unknown
     * 
     * @param key The key to check for first in system vm properties and then in the localisation properties file
     * @param defaultValue The value to return if the key does not match any configured value
     * @return the string matching the key
     */
    public static String getSystemOrPropertyString(String key, String defaultValue)
    {
        String result = System.getProperty(key);
        
        if(result == null)
        {
            try
            {
                result = ResourceBundle.getBundle(Settings.DEFAULT_PROPERTIES_BUNDLE_NAME).getString(key);
            }
            catch(final MissingResourceException mre)
            {
            	if(_TRACE)
            		Settings.log.trace(mre, mre);
            }
            catch (final Exception ex)
            {
            	if(_DEBUG)
            		Settings.log.debug(ex, ex);
            }
        }
        
        if(result == null)
            return defaultValue;
        else
            return result;
    }
    
    /**
     * Checks for the configured version first in the system vm properties, 
     * then in the localisation properties file, by default, "queryall.properties",
     * Uses the key "queryall.Version"
     * @return The version, defaults to "0.0.1"
     */
    private static String getVersion()
    {
    	return getSystemOrPropertyString("queryall.Version", "0.0.1");
    }
    
    // These properties are pulled out of the queryall.properties file
    private final String defaultOntologyTermUriPrefix = Settings.getSystemOrPropertyString("queryall.ontologyTermUriPrefix", "http://purl.org/queryall/");
    private final String defaultOntologyTermUriSuffix = Settings.getSystemOrPropertyString("queryall.ontologyTermUriSuffix", ":");
    private final String defaultRdfWebappConfigurationNamespace = Settings.getSystemOrPropertyString("queryall.WebappConfigurationNamespace", "webapp_configuration");
    private final String defaultRdfProjectNamespace = Settings.getSystemOrPropertyString("queryall.ProjectNamespace", "project");
    private final String defaultRdfProviderNamespace = Settings.getSystemOrPropertyString("queryall.ProviderNamespace", "provider");
    private final String defaultRdfTemplateNamespace = Settings.getSystemOrPropertyString("queryall.TemplateNamespace", "template");
    private final String defaultRdfQueryNamespace = Settings.getSystemOrPropertyString("queryall.QueryNamespace", "query");
    private final String defaultRdfQuerybundleNamespace = Settings.getSystemOrPropertyString("queryall.QueryBundleNamespace", "querybundle");
    private final String defaultRdfRuleNamespace = Settings.getSystemOrPropertyString("queryall.RuleNamespace", "rdfrule");
    private final String defaultRdfRuleTestNamespace = Settings.getSystemOrPropertyString("queryall.RuleTestNamespace", "ruletest");
    private final String defaultRdfNamespaceEntryNamespace = Settings.getSystemOrPropertyString("queryall.NamespaceEntryNamespace", "ns");
    private final String defaultRdfProfileNamespace = Settings.getSystemOrPropertyString("queryall.ProfileNamespace", "profile");
    private final String defaultRdfProvenanceNamespace = Settings.getSystemOrPropertyString("queryall.ProvenanceNamespace", "provenance");
    private final String defaultRdfStatisticsNamespace = Settings.getSystemOrPropertyString("queryall.StatisticsNamespace", "statistics");

    private String currentOntologyTermUriPrefix = defaultOntologyTermUriPrefix;
    private String currentOntologyTermUriSuffix = defaultOntologyTermUriSuffix;
    private String currentRdfWebappConfigurationNamespace = defaultRdfWebappConfigurationNamespace;
    private String currentRdfProjectNamespace = defaultRdfProjectNamespace;
    private String currentRdfProviderNamespace = defaultRdfProviderNamespace;
    private String currentRdfTemplateNamespace = defaultRdfTemplateNamespace;
    private String currentRdfQueryNamespace = defaultRdfQueryNamespace;
    private String currentRdfQuerybundleNamespace = defaultRdfQuerybundleNamespace;
    private String currentRdfRuleNamespace = defaultRdfRuleNamespace;
    private String currentRdfRuleTestNamespace = defaultRdfRuleTestNamespace;
    private String currentRdfNamespaceEntryNamespace = defaultRdfNamespaceEntryNamespace;
    private String currentRdfProfileNamespace = defaultRdfProfileNamespace;
    private String currentRdfProvenanceNamespace = defaultRdfProvenanceNamespace;
    private String currentRdfStatisticsNamespace = defaultRdfStatisticsNamespace;

    private final String defaultAutogeneratedQueryPrefix = Settings.getSystemOrPropertyString("queryall.AutogeneratedQueryPrefix", "autogen-");
    private final String defaultAutogeneratedQuerySuffix = Settings.getSystemOrPropertyString("queryall.AutogeneratedQuerySuffix", "");
    private final String defaultAutogeneratedProviderPrefix = Settings.getSystemOrPropertyString("queryall.AutogeneratedProviderPrefix", "autogen-");
    private final String defaultAutogeneratedProviderSuffix = Settings.getSystemOrPropertyString("queryall.AutogeneratedProviderSuffix", "");
    
    private String currentAutogeneratedQueryPrefix = defaultAutogeneratedQueryPrefix;
    private String currentAutogeneratedQuerySuffix = defaultAutogeneratedQuerySuffix;
    private String currentAutogeneratedProviderPrefix = defaultAutogeneratedProviderPrefix;
    private String currentAutogeneratedProviderSuffix = defaultAutogeneratedProviderSuffix;
    
    private String baseConfigLocation = null;
    private String baseConfigUri = null;
    private String baseConfigMimeFormat = null;

    private Collection<String> webappConfigUriList = new HashSet<String>();

    private volatile Repository currentBaseConfigurationRepository = null;
    private volatile Repository currentWebAppConfigurationRepository = null;
    private volatile Repository currentConfigurationRepository = null;
    
    private volatile Map<URI, Provider> cachedProviders = null;
    private volatile Map<URI, NormalisationRule> cachedNormalisationRules = null;
    private Map<URI, RuleTest> cachedRuleTests = null;
    private Map<URI, QueryType> cachedCustomQueries = null;
    private Map<URI, Profile> cachedProfiles = null;
    private Map<URI, NamespaceEntry> cachedNamespaceEntries = null;
    private Map<String, Collection<URI>> cachedNamespacePrefixToUriEntries = null;

    // TODO: monitor this for size and see whether we should be resetting it regularly
    private Map<URI, Map<URI, Collection<Value>>> cachedWebAppConfigSearches = new Hashtable<URI, Map<URI, Collection<Value>>>(200);
    
    private long initialisedTimestamp = System.currentTimeMillis();
    
    private static Settings defaultSettings = null;
    
    /**
     * Checks for the base config location first in the system vm properties, 
     * then in the localisation properties file, by default, "queryall.properties",
     * Uses the key "queryall.BaseConfigLocation"
     * @return The location of the base configuration file, defaults to "/queryallBaseConfig.n3"
     */
    private static String getDefaultBaseConfigLocationProperty()
    {
    	return getSystemOrPropertyString("queryall.BaseConfigLocation", "/queryallBaseConfig.n3");
    }
    
    /**
     * Uses the key "queryall.BaseConfigMimeFormat" in the properties file or system properties
     * @return The MIME format of the base configuration file, defaults to "text/rdf+n3"
     */
    private static String getDefaultBaseConfigMimeFormatProperty()
    {
        return getSystemOrPropertyString("queryall.BaseConfigMimeFormat", "text/rdf+n3");
    }
    
    
    
    /**
     * 
     * Uses the key "queryall.BaseConfigUri"
     * @return The URI of the configuration object in the base config file, defaults to "http://purl.org/queryall/webapp_configuration:theBaseConfig"
     */
    private static String getDefaultBaseConfigUriProperty()
    {
    	return getSystemOrPropertyString("queryall.BaseConfigUri", "http://purl.org/queryall/webapp_configuration:theBaseConfig");
    }
    
    public Settings()
    {
        this.baseConfigLocation = getDefaultBaseConfigLocationProperty();
        this.baseConfigMimeFormat = getDefaultBaseConfigMimeFormatProperty();
        this.baseConfigUri = getDefaultBaseConfigUriProperty();
    }
    
    public Settings(String baseConfigLocation, String baseConfigMimeFormat, String baseConfigUri)
    {
        // Do a quick test on the base config file existence
        
        InputStream baseConfig = this.getClass().getResourceAsStream(baseConfigLocation);
        
        if(baseConfig == null)
        {
        	log.error("Settings.init: TEST: baseConfig was null baseConfigLocation="+baseConfigLocation);
        }
        else
        {
        	log.debug("Settings.init: TEST: baseConfig was not null baseConfigLocation="+baseConfigLocation);
        }
        
        this.baseConfigLocation = baseConfigLocation;
        this.baseConfigMimeFormat = baseConfigMimeFormat;
        this.baseConfigUri = baseConfigUri;
    }
    
    public synchronized boolean configRefreshCheck(boolean tryToForceRefresh)
    {
        final long currentTimestamp = System.currentTimeMillis();
        
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.configRefreshCheck: before check Settings.PERIODIC_CONFIGURATION_REFRESH="
                            + this.getBooleanProperty("enablePeriodicConfigurationRefresh", true)
                            + " Settings.PERIODIC_REFRESH_MILLISECONDS="
                            + this.getLongProperty("periodicConfigurationMilliseconds", 60000L)
                            + " currentTimestamp - initialisedTimestamp="
                            + (currentTimestamp - this.initialisedTimestamp)
                            + " ");
        }
        if(tryToForceRefresh && !this.isManualRefreshAllowed())
        {
            Settings.log
                    .error("Settings.configRefreshCheck: attempted to force refresh outside of manual refresh time and ability guidelines");
            return false;
        }
        
        boolean enablePeriodicConfigurationRefresh = this.getBooleanProperty("enablePeriodicConfigurationRefresh", true);
        long periodicConfigurationMilliseconds = this.getLongProperty("periodicConfigurationMilliseconds", 60000L);
        
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.configRefreshCheck: enablePeriodicConfigurationRefresh="+enablePeriodicConfigurationRefresh);
            Settings.log
                    .debug("Settings.configRefreshCheck: periodicConfigurationMilliseconds="+periodicConfigurationMilliseconds);
        }
    
        if(tryToForceRefresh
                || (enablePeriodicConfigurationRefresh && ((currentTimestamp - this.initialisedTimestamp) > periodicConfigurationMilliseconds)))
        {
            Repository previousConfiguration = null;
            Repository previousWebappConfiguration = null;
            try
            {
                if(Settings._INFO)
                {
                    Settings.log
                            .info("Settings.configRefreshCheck: refresh required... starting process");
                }
                if(this.currentWebAppConfigurationRepository != null)
                {
                    synchronized(this.currentWebAppConfigurationRepository)
                    {
                        previousWebappConfiguration = this.currentWebAppConfigurationRepository;
                        if(Settings._DEBUG)
                        {
                            Settings.log
                                    .debug("Settings.configRefreshCheck: refresh required... currentWebappConfigurationRepository about to be set to null");
                        }
                        this.currentWebAppConfigurationRepository = null;
                    }
                }
                
                this.getWebAppConfigurationRdf();
                
                if(this.currentWebAppConfigurationRepository == null)
                {
                    currentWebAppConfigurationRepository = previousWebappConfiguration;
                    
                    Settings.log
                            .error("Settings.configRefreshCheck: WebappConfiguration was not valid after the refresh, resetting to the previousWebappConfiguration");

                    // reset the timestamp so that we don't try too often
                    // TODO: improve functionality for specifying retry time if failures occur
                    // this.initialisedTimestamp = System.currentTimeMillis();
                    
                    // return false;
                }

                
                if(this.currentConfigurationRepository != null)
                {
                    synchronized(this.currentConfigurationRepository)
                    {
                        previousConfiguration = this.currentConfigurationRepository;
                        if(Settings._DEBUG)
                        {
                            Settings.log
                                    .debug("Settings.configRefreshCheck: refresh required... currentConfigurationRepository about to be set to null");
                        }
                        this.currentConfigurationRepository = null;
                    }
                }
                if(Settings._INFO)
                {
                    Settings.log
                            .info("Settings.configRefreshCheck: refresh required... getServerConfigurationRdf about to be called");
                }
                this.getServerConfigurationRdf();
                
                if(this.currentConfigurationRepository == null)
                {
                    currentConfigurationRepository = previousConfiguration;
                    
                    Settings.log
                            .error("Settings.configRefreshCheck: configuration was not valid after the refresh, resetting to the previousConfiguration");

                    // reset the timestamp so that we don't try too often
                    // TODO: improve functionality for specifying retry time if failures occur
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
                    synchronized(this.cachedProviders)
                    {
                        this.cachedProviders = null;
                    }
                }
                this.getAllProviders();
                if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.configRefreshCheck: refresh required... cachedProviders refreshed");
                }
                if(this.cachedCustomQueries != null)
                {
                    synchronized(this.cachedCustomQueries)
                    {
                        this.cachedCustomQueries = null;
                    }
                }
                this.getAllQueryTypes();
                if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.configRefreshCheck: refresh required... cachedCustomQueries refreshed");
                }
                if(this.cachedNamespaceEntries != null)
                {
                    synchronized(this.cachedNamespaceEntries)
                    {
                        this.cachedNamespaceEntries = null;
                    }
                }
                if(this.getNamespacePrefixesToUris() != null)
                {
                    synchronized(this.getNamespacePrefixesToUris())
                    {
                        this.setNamespacePrefixesToUris(null);
                    }
                }
                this.getAllNamespaceEntries();
                if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.configRefreshCheck: refresh required... cachedNamespaceEntries and cachedNamespacePrefixToUriEntries refreshed");
                }
                
                if(this.cachedProfiles != null)
                {
                    synchronized(this.cachedProfiles)
                    {
                        this.cachedProfiles = null;
                    }
                }
                this.getAllProfiles();
                
                if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.configRefreshCheck: refresh required... cachedProfiles refreshed");
                }
                
                if(this.cachedNormalisationRules != null)
                {
                    synchronized(this.cachedNormalisationRules)
                    {
                        this.cachedNormalisationRules = null;
                    }
                }
                this.getAllNormalisationRules();
                if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.configRefreshCheck: refresh required... cachedNormalisationRules refreshed");
                }
                if(this.cachedRuleTests != null)
                {
                    synchronized(this.cachedRuleTests)
                    {
                        this.cachedRuleTests = null;
                    }
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
                    Settings.log
                            .info("Settings.configRefreshCheck: refresh required... finished process");
                }

                return true;
            }
            catch(java.lang.InterruptedException ie)
            {
                Settings.log.fatal("Settings.configRefreshCheck: failed due to java.lang.InterruptedException");
                this.currentConfigurationRepository = previousConfiguration;
                return false;
            }
        }
        if(Settings._DEBUG)
        {
            Settings.log.debug("Settings.configRefreshCheck: returning");
        }
        return false;
    }

    @Override
	public synchronized Map<URI, NamespaceEntry> getAllNamespaceEntries()
    {
        if(this.cachedNamespaceEntries != null)
        {
            return this.cachedNamespaceEntries;
        }
        
        final Map<String, Collection<URI>> tempNamespacePrefixToUriEntries = new Hashtable<String, Collection<URI>>();
        
        try
        {
            Map<URI, NamespaceEntry> results = RdfUtils.getNamespaceEntries(getServerConfigurationRdf());
            
            if(_INFO)
            {
                log.info("Settings.getAllNamespaceEntries: found "+results.size()+" namespaces");
            }

            
            for(NamespaceEntry nextNamespaceEntryConfiguration : results.values())
            {
                if(tempNamespacePrefixToUriEntries
                        .containsKey(nextNamespaceEntryConfiguration.getPreferredPrefix()))
                {
                    final Collection<URI> currentnamespacePreferredPrefixToUriList = tempNamespacePrefixToUriEntries
                            .get(nextNamespaceEntryConfiguration.getPreferredPrefix());
                    if(!currentnamespacePreferredPrefixToUriList
                            .contains(nextNamespaceEntryConfiguration.getKey()))
                    {
                        currentnamespacePreferredPrefixToUriList
                                .add(nextNamespaceEntryConfiguration.getKey());
                    }
                }
                else
                {
                    final Collection<URI> newnamespacePreferredPrefixToUriList = new HashSet<URI>();
                    newnamespacePreferredPrefixToUriList
                            .add(nextNamespaceEntryConfiguration.getKey());
                    tempNamespacePrefixToUriEntries
                            .put(
                                    nextNamespaceEntryConfiguration.getPreferredPrefix(),
                                    newnamespacePreferredPrefixToUriList);
                }
                if(nextNamespaceEntryConfiguration.getAlternativePrefixes() != null)
                {
                    for(final String nextAlternativePrefix : nextNamespaceEntryConfiguration.getAlternativePrefixes())
                    {
                        if(tempNamespacePrefixToUriEntries
                                .containsKey(nextAlternativePrefix))
                        {
                            final Collection<URI> currentNamespacePrefixToUriList = tempNamespacePrefixToUriEntries
                                    .get(nextAlternativePrefix);
                            if(!currentNamespacePrefixToUriList
                                    .contains(nextNamespaceEntryConfiguration.getKey()))
                            {
                                currentNamespacePrefixToUriList
                                        .add(nextNamespaceEntryConfiguration.getKey());
                            }
                        }
                        else
                        {
                            final Collection<URI> newNamespacePrefixToUriList = new HashSet<URI>();
                            newNamespacePrefixToUriList
                                    .add(nextNamespaceEntryConfiguration.getKey());
                            tempNamespacePrefixToUriEntries.put(
                                    nextAlternativePrefix,
                                    newNamespacePrefixToUriList);
                        }
                    }
                }
            }
            
            this.setNamespacePrefixesToUris(tempNamespacePrefixToUriEntries);
            this.cachedNamespaceEntries = results;
            
            return results;
        }
        catch(java.lang.InterruptedException ie)
        {
            Settings.log.fatal("Settings.getAllNamespaceEntries: caught java.lang.InterruptedException: not throwing it.", ie);
            
            return null;
        }
    }
    
    @Override
	public synchronized Map<URI, NormalisationRule> getAllNormalisationRules()
    {
        if(this.cachedNormalisationRules != null)
        {
            return this.cachedNormalisationRules;
        }

        try
        {
            final Repository myRepository = getServerConfigurationRdf();
            
            Map<URI, NormalisationRule> results = RdfUtils.getNormalisationRules(myRepository);
            
            if(_INFO)
            {
                log.info("Settings.getAllNormalisationRules: found "+results.size()+" normalisation rules");
            }
            
            this.cachedNormalisationRules = results;
            
            return results;
        }
        catch(java.lang.InterruptedException ie)
        {
            Settings.log.fatal("Settings.getAllNormalisationRules: caught java.lang.InterruptedException: not throwing it.", ie);
            
            return null;
        }
    }
    
    @Override
	public synchronized Map<URI, Profile> getAllProfiles()
    {
        if(this.cachedProfiles != null)
        {
            return this.cachedProfiles;
        }
        
        
        try
        {
            final Repository myRepository = getServerConfigurationRdf();
            
            Map<URI, Profile> results = RdfUtils.getProfiles(myRepository);
            
            if(_INFO)
            {
                log.info("Settings.getAllProfiles: found "+results.size()+" profiles");
            }
            
            this.cachedProfiles = results;
            
            return results;
        }
        catch(java.lang.InterruptedException ie)
        {
            Settings.log.fatal("Settings.getAllProfiles: caught java.lang.InterruptedException: not throwing it.", ie);
            
            return null;
        }
    }
    
    @Override
	public synchronized Map<URI, Provider> getAllProviders()
    {
        if(this.cachedProviders != null)
        {
            return this.cachedProviders;
        }
        
        Map<URI, Provider> results = null;
        
        try
        {
            final Repository myRepository = this.getServerConfigurationRdf();
            
            results = RdfUtils.getProviders(myRepository);
            
            if(results != null)
            {
                this.cachedProviders = results;
            }
        }
        catch(java.lang.InterruptedException ie)
        {
            Settings.log.fatal("Settings.getAllProviders: caught java.lang.InterruptedException: not throwing it.", ie);
            
            return null;
        }
        
        if(_INFO)
        {
            log.info("Settings.getAllProviders: found "+results.size()+" providers");
        }
        
        return results;
    }
    
    @Override
	public synchronized Map<URI, QueryType> getAllQueryTypes()
    {
        if(this.cachedCustomQueries != null)
        {
            return this.cachedCustomQueries;
        }
        
        try
        {
            final Repository myRepository = getServerConfigurationRdf();
            
            Map<URI, QueryType> results = RdfUtils.getQueryTypes(myRepository);
            
            if(_INFO)
            {
                log.info("Settings.getAllQueryTypes: found "+results.size()+" queries");
            }
            
            this.cachedCustomQueries = results;
            
            return results;
        }
        catch(java.lang.InterruptedException ie)
        {
            Settings.log.fatal("Settings.getAllQueryTypes: caught java.lang.InterruptedException: not throwing it.", ie);
            
            return null;
        }
    }
    
    @Override
	public synchronized Map<URI, RuleTest> getAllRuleTests()
    {
        if(this.cachedRuleTests != null)
        {
            return this.cachedRuleTests;
        }
        
        try
        {
            final Repository myRepository = getServerConfigurationRdf();
            
            Map<URI, RuleTest> results = RdfUtils.getRuleTests(myRepository);
            
            if(_INFO)
            {
                log.info("Settings.getAllRuleTests: found "+results.size()+" rule tests");
            }
            
            this.cachedRuleTests = results;
            
            return results;
        }
        catch(java.lang.InterruptedException ie)
        {
            Settings.log.fatal("Settings.getAllRuleTests: caught java.lang.InterruptedException: not throwing it.", ie);
            
            return null;
        }
    }
    
    /**
	 * @return the aUTOGENERATED_PROVIDER_PREFIX
	 */
	public String getAutogeneratedProviderPrefix() {
		return currentAutogeneratedProviderPrefix;
	}
    
    /**
	 * @return the aUTOGENERATED_PROVIDER_SUFFIX
	 */
	public String getAutogeneratedProviderSuffix() {
		return currentAutogeneratedProviderSuffix;
	}
    
    /**
	 * @return the aUTOGENERATED_QUERY_PREFIX
	 */
	public String getAutogeneratedQueryPrefix() {
		return currentAutogeneratedQueryPrefix;
	}
    
    /**
	 * @return the aUTOGENERATED_QUERY_SUFFIX
	 */
	public String getAutogeneratedQuerySuffix() {
		return currentAutogeneratedQuerySuffix;
	}
    
    public boolean getBooleanProperty(String key, boolean defaultValue)
    {
        if(_TRACE)
            log.trace("Settings.getBooleanPropertyFromConfig: key="+key+" defaultValue="+defaultValue);

        boolean result = defaultValue;

        Collection<Value> values = getValueProperties(key);
        
        if(values.size() != 1)
        {
            log.error("Settings.getBooleanPropertyFromConfig: Did not find a unique result for key="+key+ " values.size()="+values.size()+" defaultValue="+defaultValue);
            return defaultValue;
        }

        for(Value nextValue : values)
        {
            result = RdfUtils.getBooleanFromValue(nextValue);
        }
        
        if(_TRACE)
            log.trace("Settings.getBooleanPropertyFromConfig: key="+key+" result="+result);

        return result;
    }
    
    public String getDefaultHostAddress()
    {
        return this.getStringProperty("uriPrefix", "http://")+this.getStringProperty("hostName", "bio2rdf.org")+this.getStringProperty("uriSuffix", "/");
    }
    
    public float getFloatProperty(String key, float defaultValue)
    {
        float result = defaultValue;
        
        if(_TRACE)
            log.trace("Settings.getFloatPropertyFromConfig: key="+key+" defaultValue="+defaultValue);

        Collection<Value> values = getValueProperties(key);
        
        if(values.size() != 1)
        {
            log.error("Settings.getFloatPropertyFromConfig: Did not find a unique result for key="+key+ " values.size()="+values.size()+" defaultValue="+defaultValue);
            return defaultValue;
        }

        for(Value nextValue : values)
        {
            result = RdfUtils.getFloatFromValue(nextValue);
        }
        
        if(_TRACE)
            log.trace("Settings.getFloatPropertyFromConfig: key="+key+" result="+result);

        return result;
    }
    
    public int getIntProperty(String key, int defaultValue)
    {
        int result = defaultValue;
        
        if(_TRACE)
            log.trace("Settings.getIntPropertyFromConfig: key="+key+" defaultValue="+defaultValue);

        Collection<Value> values = getValueProperties(key);
        
        if(values.size() != 1)
        {
            log.error("Settings.getIntPropertyFromConfig: Did not find a unique result for key="+key+ " values.size()="+values.size()+" defaultValue="+defaultValue);
            return defaultValue;
        }

        for(Value nextValue : values)
        {
            result = RdfUtils.getIntegerFromValue(nextValue);
        }
        
        if(_TRACE)
            log.trace("Settings.getIntPropertyFromConfig: key="+key+" result="+result);

        return result;
    }
    
    public URI getURIProperty(String key, URI defaultValue)
    {
    	URI result = defaultValue;
    	
        if(_TRACE)
            log.trace("Settings.getUriPropertyFromConfig: key="+key+" defaultValue="+defaultValue);

        Collection<URI> values = getURIProperties(key);
        
        if(values.size() != 1)
        {
            log.error("Settings.getUriPropertyFromConfig: Did not find a unique result for key="+key+ " values.size()="+values.size()+" defaultValue="+defaultValue);
            return defaultValue;
        }

        for(URI nextValue : values)
        {
            result = nextValue;
        }
        
        if(_TRACE)
            log.trace("Settings.getUriPropertyFromConfig: key="+key+" result="+result);

        return result;
    	
    }
    
	public long getLongProperty(String key, long defaultValue)
    {
        long result = defaultValue;
        
        if(_TRACE)
            log.trace("Settings.getLongPropertyFromConfig: key="+key+" defaultValue="+defaultValue);

        Collection<Value> values = getValueProperties(key);
        
        if(values.size() != 1)
        {
            log.error("Settings.getLongPropertyFromConfig: Did not find a unique result for key="+key+ " values.size()="+values.size()+" defaultValue="+defaultValue);
            return defaultValue;
        }

        for(Value nextValue : values)
        {
            result = RdfUtils.getLongFromValue(nextValue);
        }
        
        if(_TRACE)
            log.trace("Settings.getLongPropertyFromConfig: key="+key+" result="+result);

        return result;
    }
    
	/**
	 * @return the current_RDF_NAMESPACEENTRY_NAMESPACE
	 */
	@Override
	public String getNamespaceForNamespaceEntry() {
		return currentRdfNamespaceEntryNamespace;
	}
    
    /**
	 * @return the current_RDF_RDFRULE_NAMESPACE
	 */
	@Override
	public String getNamespaceForNormalisationRule() {
		return currentRdfRuleNamespace;
	}
    
    /**
	 * @return the current_RDF_PROFILE_NAMESPACE
	 */
	@Override
	public String getNamespaceForProfile() {
		return currentRdfProfileNamespace;
	}
    
    /**
	 * @return the current_RDF_PROJECT_NAMESPACE
	 */
	@Override
	public String getNamespaceForProject() {
		return currentRdfProjectNamespace;
	}
    
    /**
	 * @return the current_RDF_PROVENANCE_NAMESPACE
	 */
	@Override
	public String getNamespaceForProvenance() {
		return currentRdfProvenanceNamespace;
	}
    
    /**
	 * @return the current_RDF_PROVIDER_NAMESPACE
	 */
	@Override
	public String getNamespaceForProvider() {
		return currentRdfProviderNamespace;
	}
    
    /**
	 * @return the current_RDF_QUERYBUNDLE_NAMESPACE
	 */
	@Override
	public String getNamespaceForQueryBundle() {
		return currentRdfQuerybundleNamespace;
	}
    
    /**
	 * @return the current_RDF_QUERY_NAMESPACE
	 */
	@Override
	public String getNamespaceForQueryType() {
		return currentRdfQueryNamespace;
	}
    
    /**
	 * @return the current_RDF_RULETEST_NAMESPACE
	 */
	@Override
	public String getNamespaceForRuleTest() {
		return currentRdfRuleTestNamespace;
	}
    
    /**
	 * @return the current_RDF_STATISTICS_NAMESPACE
	 */
	@Override
	public String getNamespaceForStatistics() {
		return currentRdfStatisticsNamespace;
	}
    
    /**
	 * @return the current_RDF_TEMPLATE_NAMESPACE
	 */
	@Override
	public String getNamespaceForTemplate() {
		return currentRdfTemplateNamespace;
	}
    
    /**
	 * @return the current_RDF_WEBAPP_CONFIGURATION_NAMESPACE
	 */
	@Override
	public String getNamespaceForWebappConfiguration() {
		return currentRdfWebappConfigurationNamespace;
	}
    
    /**
	 * @return the dEFAULT_ONTOLOGYTERMURI_PREFIX
	 */
	@Override
	public String getOntologyTermUriPrefix() {
		return currentOntologyTermUriPrefix;
	}

    /**
	 * @return the current_ONTOLOGYTERMURI_SUFFIX
	 */
	@Override
	public String getOntologyTermUriSuffix() {
		return currentOntologyTermUriSuffix;
	}
    
    public Pattern getPlainNamespaceAndIdentifierPattern()
    {
        return Pattern.compile(this.getStringProperty("plainNamespaceAndIdentifierRegex", ""));
    }
    
    public Pattern getPlainNamespacePattern()
    {
        return Pattern.compile(this.getStringProperty("plainNamespaceRegex", ""));
    }
    
	public static Map<URI, Provider> getProvidersForQueryTypeForNamespaceUris(Map<URI, Provider> allProviders,
            URI queryType, Collection<Collection<URI>> namespaceUris,
            URI namespaceMatchMethod)
    {
        if(Settings._TRACE)
        {
            Settings.log
                    .trace("Settings.getProvidersForQueryTypeForNamespaceUris: queryType="
                            + queryType
                            + " namespaceMatchMethod="
                            + namespaceMatchMethod
                            + " namespaceUris="
                            + namespaceUris);
        }
        
        final Map<URI, Provider> namespaceProviders = ProviderUtils.getProvidersForNamespaceUris(allProviders, namespaceUris, namespaceMatchMethod);
        
        if(Settings._TRACE)
        {
            Settings.log
                    .trace("Settings.getProvidersForQueryTypeForNamespaceUris: queryType="
                            + queryType
                            + " namespaceProviders="
                            + namespaceProviders);
        }
        
        final Map<URI, Provider> results = ProviderUtils.getProvidersForQueryType(namespaceProviders, queryType);
        
        if(Settings._TRACE)
        {
            Settings.log
                    .trace("Settings.getProvidersForQueryTypeForNamespaceUris: queryType="
                            + queryType + " results=" + results);
        }
        return results;
    }

	public Collection<QueryType> getQueryTypesByUri(URI queryTypeUri)
    {
        final Collection<QueryType> results = new HashSet<QueryType>();
        for(final QueryType nextQueryType : this.getAllQueryTypes().values())
        {
            if(nextQueryType.getKey().equals(queryTypeUri))
            {
                results.add(nextQueryType);
            }
        }
        return results;
    }
        
    public Collection<QueryType> getQueryTypesMatchingQueryString(String queryString, List<Profile> profileList)
    {
    	if(Settings._DEBUG)
    	{
    		log.debug("getQueryTypesMatchingQueryString: profileList.size()="+profileList.size());
    		
    		if(Settings._TRACE)
    		{
    			for(Profile nextProfile : profileList)
		        {
		            log.trace("getQueryTypesMatchingQueryString: nextProfile.getKey()="+nextProfile.getKey().stringValue());
		        }
    		}
    	}
    	
        final Collection<QueryType> results = new HashSet<QueryType>();
        
        for(QueryType nextQuery : this.getAllQueryTypes().values())
        {
            if(nextQuery.matchesQueryString(queryString))
            {
                if(Settings._TRACE)
                {
                    Settings.log
                            .trace("getQueryTypesMatchingQueryString: tentative, pre-profile-check match for"
                                    + " nextQuery.getKey()="
                                    + nextQuery.getKey().stringValue()
                                    + " queryString="
                                    + queryString);
                }
                if(nextQuery.isUsedWithProfileList(profileList, this.getBooleanProperty(Constants.RECOGNISE_IMPLICIT_QUERY_INCLUSIONS, true), this.getBooleanProperty(Constants.INCLUDE_NON_PROFILE_MATCHED_QUERIES, true)))
                {
                    if(Settings._DEBUG)
                    {
                        Settings.log
                                .debug("getQueryTypesMatchingQueryString: profileList suitable for"
                                        + " nextQuery.getKey()="
                                        + nextQuery.getKey().stringValue()
                                        + " queryString="
                                        + queryString);
                    }
                    results.add(nextQuery);
                }
                else if(Settings._TRACE)
                {
                    Settings.log
                            .trace("getQueryTypesMatchingQueryString: profileList not suitable for"
                                    + " nextQuery.getKey()="
                                    + nextQuery.getKey().stringValue()
                                    + " queryString="
                                    + queryString);
                }
            }
        }
        return results;
    }
        
    public Collection<Statement> getStatementProperties(String key)
    {
        if(_TRACE)
            log.trace("Settings.getStatementPropertiesFromConfig: key="+key);
        
        Collection<Statement> results = new HashSet<Statement>();
        
        try
        {
            Repository webappConfig = getWebAppConfigurationRdf();
            
            final ValueFactory f = webappConfig.getValueFactory();

            // TODO: in future should reform this to accept a full URI as the key so properties outside of the queryall vocabulary can be used for properties
            URI propertyUri = f.createURI(this.getOntologyTermUriPrefix() + this.getNamespaceForWebappConfiguration() + this.getOntologyTermUriSuffix(), key);
            
            if(_TRACE)
                Settings.log.trace("Settings.getStatementPropertiesFromConfig: WEBAPP_CONFIG_URI_LIST.size()="+getWebappConfigUriList().size());

            for(String nextConfigUri : getWebappConfigUriList())
            {
                URI configUri = f.createURI(nextConfigUri);
                
                if(_TRACE)
                    Settings.log.trace("Settings.getStatementPropertiesFromConfig: configUri="+configUri.stringValue()+" propertyUri="+propertyUri.stringValue());

                results.addAll(getStatementProperties(configUri, propertyUri, webappConfig));
            }
        }
        catch(Exception ex)
        {
            Settings.log.error("Settings.getStatementPropertiesFromConfig: error", ex);
        }
        
        return results;
    }

    public Collection<String> getStringProperties(String key)
    {
        if(_TRACE)
            log.trace("Settings.getStringCollectionPropertiesFromConfig: key="+key);

        Collection<String> results = new LinkedList<String>();
        
        Collection<Value> values = getValueProperties(key);
        
        for(Value nextValue : values)
        {
            results.add(nextValue.stringValue());
            //results.add(RdfUtils.getUTF8StringValueFromSesameValue(nextValue));
        }
        
        return results;
    }

	public String getStringProperty(String key, String defaultValue)
    {
        String result = defaultValue;
        
        if(_TRACE)
            log.trace("Settings.getStringPropertyFromConfig: key="+key+" defaultValue="+defaultValue);

        Collection<String> values = getStringProperties(key);
        
        if(values.size() != 1)
        {
            log.error("Settings.getStringPropertyFromConfig: Did not find a unique result for key="+key+ " values.size()="+values.size()+" defaultValue="+defaultValue);
            return defaultValue;
        }

        for(String nextValue : values)
        {
            result = nextValue;
        }
        
        if(_TRACE)
            log.trace("Settings.getStringPropertyFromConfig: key="+key+" defaultValue="+defaultValue+" returning result="+result);

        return result;
    }

    public Pattern getTagPattern()
    {
        return Pattern.compile(this.getStringProperty("tagPatternRegex", ""));
    }

    public Collection<URI> getURIProperties(String key)
    {
        if(_TRACE)
            log.trace("Settings.getURICollectionPropertiesFromConfig: key="+key);

        Collection<URI> results = new HashSet<URI>();
        
        for(Value nextValue : getValueProperties(key))
        {
            if(nextValue instanceof URI)
            {
                results.add((URI)nextValue);
            }
            else
            {
                log.fatal("Settings.getURICollectionPropertiesFromConfig: nextValue was not an instance of URI key="+key+" nextValue="+nextValue);
            }
        }
        
        return results;
    }

	public Collection<Value> getValueProperties(String key)
    {
        if(_TRACE)
            log.trace("Settings.getValueCollectionPropertiesFromConfig: key="+key);

        Collection<Value> results = new HashSet<Value>();
        
        try
        {
            final ValueFactory f = getWebAppConfigurationRdf().getValueFactory();

            // XXX: in future should reform this to accept a full URI as the key so properties outside of the queryall vocabulary can be used for properties
            URI propertyUri = f.createURI(this.getOntologyTermUriPrefix() + this.getNamespaceForWebappConfiguration() + this.getOntologyTermUriSuffix(), key);
            
            if(_TRACE)
                Settings.log.trace("Settings.getValueCollectionPropertiesFromConfig: getWebappConfigUriList().size()="+getWebappConfigUriList().size());

            for(String nextConfigUri : getWebappConfigUriList())
            {
                URI configUri = f.createURI(nextConfigUri);
                
                if(_TRACE)
                    Settings.log.trace("Settings.getValueCollectionPropertiesFromConfig: configUri="+configUri.stringValue()+" propertyUri="+propertyUri.stringValue());

                results.addAll(getValueProperties(configUri, propertyUri));
            }
        }
        catch(Exception ex)
        {
            Settings.log.error("Settings.getValueCollectionPropertiesFromConfig: error", ex);
        }
        
        return results;
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
        boolean manualRefresh = this.getBooleanProperty("enableManualConfigurationRefresh", true);
        long timestampDiff = (System.currentTimeMillis() - this.initialisedTimestamp);
        long manualRefreshMinimum = this.getLongProperty("manualConfigurationMinimumMilliseconds", 60000L);
        
        if(_DEBUG)
        {
            log.debug("isManualRefreshAllowed: manualRefresh="+manualRefresh);
            log.debug("isManualRefreshAllowed: timestampDiff="+timestampDiff);
            log.debug("isManualRefreshAllowed: manualRefreshMinimum="+manualRefreshMinimum);
        
        }
        
        if(manualRefreshMinimum < 0)
        {
            log.error("isManualRefreshAllowed: manualRefreshMinimum was less than 0");
        }
        
        return manualRefresh && (timestampDiff > manualRefreshMinimum);
    }

	/**
	 * @param aUTOGENERATED_PROVIDER_PREFIX the aUTOGENERATED_PROVIDER_PREFIX to set
	 */
	public void setAutogeneratedProviderPrefix(
			String aUTOGENERATED_PROVIDER_PREFIX) {
		currentAutogeneratedProviderPrefix = aUTOGENERATED_PROVIDER_PREFIX;
	}

    /**
	 * @param aUTOGENERATED_PROVIDER_SUFFIX the aUTOGENERATED_PROVIDER_SUFFIX to set
	 */
	public void setAutogeneratedProviderSuffix(
			String aUTOGENERATED_PROVIDER_SUFFIX) {
		currentAutogeneratedProviderSuffix = aUTOGENERATED_PROVIDER_SUFFIX;
	}

	/**
	 * @param autogeneratedQueryPrefix the aUTOGENERATED_QUERY_PREFIX to set
	 */
	public void setAutogeneratedQueryPrefix(String autogeneratedQueryPrefix) {
		currentAutogeneratedQueryPrefix = autogeneratedQueryPrefix;
	}

	/**
	 * @param aUTOGENERATED_QUERY_SUFFIX the aUTOGENERATED_QUERY_SUFFIX to set
	 */
	public void setAutogeneratedQuerySuffix(String aUTOGENERATED_QUERY_SUFFIX) {
		currentAutogeneratedQuerySuffix = aUTOGENERATED_QUERY_SUFFIX;
	}

	/**
	 * @param current_RDF_NAMESPACEENTRY_NAMESPACE the current_RDF_NAMESPACEENTRY_NAMESPACE to set
	 */
	@Override
	public void setNamespaceForNamespaceEntry(
			String current_RDF_NAMESPACEENTRY_NAMESPACE) {
		currentRdfNamespaceEntryNamespace = current_RDF_NAMESPACEENTRY_NAMESPACE;
	}

	/**
	 * @param current_RDF_RDFRULE_NAMESPACE the current_RDF_RDFRULE_NAMESPACE to set
	 */
	@Override
	public void setNamespaceForNormalisationRule(
			String current_RDF_RDFRULE_NAMESPACE) {
		currentRdfRuleNamespace = current_RDF_RDFRULE_NAMESPACE;
	}

	/**
	 * @param current_RDF_PROFILE_NAMESPACE the current_RDF_PROFILE_NAMESPACE to set
	 */
	@Override
	public void setNamespaceForProfile(String current_RDF_PROFILE_NAMESPACE) {
		currentRdfProfileNamespace = current_RDF_PROFILE_NAMESPACE;
	}

	/**
	 * @param rdfProjectNamespace the current_RDF_PROJECT_NAMESPACE to set
	 */
	@Override
	public void setNamespaceForProject(String rdfProjectNamespace) {
		currentRdfProjectNamespace = rdfProjectNamespace;
	}

	/**
	 * @param current_RDF_PROVENANCE_NAMESPACE the current_RDF_PROVENANCE_NAMESPACE to set
	 */
	@Override
	public void setNamespaceForProvenance(
			String current_RDF_PROVENANCE_NAMESPACE) {
		currentRdfProvenanceNamespace = current_RDF_PROVENANCE_NAMESPACE;
	}

	/**
	 * @param current_RDF_PROVIDER_NAMESPACE the current_RDF_PROVIDER_NAMESPACE to set
	 */
	@Override
	public void setNamespaceForProvider(String current_RDF_PROVIDER_NAMESPACE) {
		currentRdfProviderNamespace = current_RDF_PROVIDER_NAMESPACE;
	}

	/**
	 * @param current_RDF_QUERYBUNDLE_NAMESPACE the current_RDF_QUERYBUNDLE_NAMESPACE to set
	 */
	@Override
	public void setNamespaceForQueryBundle(
			String current_RDF_QUERYBUNDLE_NAMESPACE) {
		currentRdfQuerybundleNamespace = current_RDF_QUERYBUNDLE_NAMESPACE;
	}

	/**
	 * @param current_RDF_QUERY_NAMESPACE the current_RDF_QUERY_NAMESPACE to set
	 */
	@Override
	public void setNamespaceForQueryType(String current_RDF_QUERY_NAMESPACE) {
		currentRdfQueryNamespace = current_RDF_QUERY_NAMESPACE;
	}

	/**
	 * @param current_RDF_RULETEST_NAMESPACE the current_RDF_RULETEST_NAMESPACE to set
	 */
	@Override
	public void setNamespaceForRuleTest(String current_RDF_RULETEST_NAMESPACE) {
		currentRdfRuleTestNamespace = current_RDF_RULETEST_NAMESPACE;
	}

	/**
	 * @param current_RDF_STATISTICS_NAMESPACE the current_RDF_STATISTICS_NAMESPACE to set
	 */
	@Override
	public void setNamespaceForStatistics(
			String current_RDF_STATISTICS_NAMESPACE) {
		currentRdfStatisticsNamespace = current_RDF_STATISTICS_NAMESPACE;
	}

	/**
	 * @param current_RDF_TEMPLATE_NAMESPACE the current_RDF_TEMPLATE_NAMESPACE to set
	 */
	@Override
	public void setNamespaceForTemplate(String current_RDF_TEMPLATE_NAMESPACE) {
		currentRdfTemplateNamespace = current_RDF_TEMPLATE_NAMESPACE;
	}

	/**
	 * @param rdfWebappConfigurationNamespace the currentRdfWebappConfigurationNamespace to set
	 */
	@Override
	public void setNamespaceForWebappConfiguration(String rdfWebappConfigurationNamespace) 
	{
		currentRdfWebappConfigurationNamespace = rdfWebappConfigurationNamespace;
	}

	/**
	 * @param ontologyTermUriPrefix the dEFAULT_ONTOLOGYTERMURI_PREFIX to set
	 */
	@Override
	public void setOntologyTermUriPrefix(String ontologyTermUriPrefix) {
		currentOntologyTermUriPrefix = ontologyTermUriPrefix;
	}

	/**
	 * @param current_ONTOLOGYTERMURI_SUFFIX the current_ONTOLOGYTERMURI_SUFFIX to set
	 */
	@Override
	public void setOntologyTermUriSuffix(String current_ONTOLOGYTERMURI_SUFFIX) {
		currentOntologyTermUriSuffix = current_ONTOLOGYTERMURI_SUFFIX;
	}

	/**
     * @param webappConfigUriList the webappConfigUriList to set
     */
    public void setWebappConfigUriList(Collection<String> webappConfigUriList)
    {
        this.webappConfigUriList = webappConfigUriList;
    }

	private void doConfigKeyCache(URI subjectKey, URI propertyKey, Collection<Value> newObject)
    {
        if(newObject == null)
        {
            throw new RuntimeException("Cannot cache null property items subjectKey="+subjectKey+" propertyKey="+propertyKey);            
        }
        else if(cachedWebAppConfigSearches.containsKey(subjectKey))
        {
            Map<URI, Collection<Value>> currentCache = cachedWebAppConfigSearches.get(subjectKey);

            if(currentCache == null)
            {
                throw new RuntimeException("Found a null cache item for subjectKey="+subjectKey);
            }
            else if(!currentCache.containsKey(propertyKey))
            {
                currentCache.put(propertyKey, newObject);
                // log.trace("Settings.doConfigKeyCache: Added new cache property item for subjectKey="+subjectKey+" propertyKey="+propertyKey);
            }
            else if(_TRACE)
            {
                log.trace("Settings.doConfigKeyCache: Already cached item for subjectKey="+subjectKey+" propertyKey="+propertyKey);
            }
        }
        else
        {
            Map<URI, Collection<Value>> newCache = Collections.synchronizedMap(new HashMap<URI, Collection<Value>>());
            newCache.put(propertyKey, newObject);
            cachedWebAppConfigSearches.put(subjectKey, newCache);
            // log.trace("Settings.doConfigKeyCache: New cached item for subjectKey="+subjectKey+" propertyKey="+propertyKey);
        }
    }

	public String getBaseConfigLocation()
    {
        if(this.baseConfigLocation == null)
        {
            this.baseConfigLocation = Settings.getDefaultBaseConfigLocationProperty();
        }
        
        return this.baseConfigLocation;
    }

	/**
     * 
     * Uses the key "queryall.BaseConfigMimeFormat"
     * @return The mime format of the base config file, defaults to "text/rdf+n3"
     */
    public String getBaseConfigMimeFormat()
    {
        if(this.baseConfigMimeFormat == null)
        {
            this.baseConfigMimeFormat = Settings.getDefaultBaseConfigMimeFormatProperty();
        }
        
        return this.baseConfigMimeFormat;
    }

	private synchronized Repository getBaseConfigurationRdf() throws java.lang.InterruptedException
    {
        if(_TRACE)
        {
            Settings.log.trace("Settings.getBaseConfigurationRdf: entering method");
        }

        if(this.currentBaseConfigurationRepository != null)
        {
            return this.currentBaseConfigurationRepository;
        }
        
        if(_DEBUG)
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
            
            if(_DEBUG)
            {
                Settings.log.debug("Settings.getBaseConfigurationRdf: temp repository initialised");
            }
            
            // Settings.log.error("Settings.getBaseConfigurationRdf: Settings.WEBAPP_CONFIG_LOCATION_LIST.size()="+Settings.WEBAPP_CONFIG_LOCATION_LIST);

            final RepositoryConnection myRepositoryConnection = tempConfigurationRepository
                    .getConnection();
            
            String nextLocation = this.getBaseConfigLocation();
            InputStream nextInputStream = getClass().getResourceAsStream(nextLocation);
            
            try
            {
                if(Settings._INFO)
                {
                    Settings.log
                            .info("Settings.getBaseConfigurationRdf: getting configuration from file: nextLocation="
                                    + nextLocation+" nextInputStream="+nextInputStream);
                }
                
                myRepositoryConnection.add(nextInputStream, baseURI, RDFFormat.forMIMEType(configMIMEFormat));
                if(Settings._INFO)
                {
                    Settings.log
                            .info("Settings.getBaseConfigurationRdf: finished getting configuration from file: nextLocation="
                                    + nextLocation);
                }
            }
            catch (final RDFParseException rdfpe)
            {
                Settings.log
                        .fatal(
                                "Settings.getBaseConfigurationRdf: failed to get the configuration repository. Caught RDFParseException. nextLocation="+nextLocation,
                                rdfpe);
                throw new RuntimeException(
                        "Settings.getBaseConfigurationRdf: failed to initialise the configuration repository. Caught RDFParseException. nextLocation="+nextLocation);
            }
            catch (final OpenRDFException ordfe)
            {
                Settings.log
                        .fatal(
                                "Settings.getBaseConfigurationRdf: failed to initialise the base configuration repository. Caught OpenRDFException. nextLocation="+nextLocation,
                                ordfe);
                throw new RuntimeException(
                        "Settings.getBaseConfigurationRdf: failed to initialise the base configuration repository. Caught OpenRDFException. nextLocation="+nextLocation);
            }
            catch (final java.io.IOException ioe)
            {
                Settings.log.fatal("Settings.getBaseConfigurationRdf: failed to initialise the base configuration repository. Caught java.io.IOException. nextLocation="+nextLocation, ioe);
                throw new RuntimeException(
                        "Settings.getBaseConfigurationRdf: failed to initialise the base configuration repository. Caught java.io.IOException. nextLocation="+nextLocation);
            }
            finally
            {
                if(myRepositoryConnection != null)
                {
                    myRepositoryConnection.close();
                }
            }
        }
        catch (final OpenRDFException ordfe)
        {
            Settings.log
                    .fatal(
                            "Settings.getBaseConfigurationRdf: failed to initialise the webapp configuration repository. Caught OpenRDFException",
                            ordfe);
            throw new RuntimeException(
                    "Settings.getBaseConfigurationRdf: failed to initialise the webapp configuration repository. Caught OpenRDFException");
        }
        
        this.currentBaseConfigurationRepository = tempConfigurationRepository;

        if(Settings._INFO)
        {
            final long end = System.currentTimeMillis();
            Settings.log.info(String.format("%s: timing=%10d",
                    "Settings.getBaseConfigurationRdf", (end - start)));

        }
        
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getBaseConfigurationRdf: finished parsing configuration files");
        }

        if(_INFO)
        {
            try
            {
                Settings.log.info("Settings.getBaseConfigurationRdf: found "+this.currentBaseConfigurationRepository.getConnection().size()+" statements in base configuration");
            }
            catch(RepositoryException rex)
            {
                Settings.log.error("Settings.getBaseConfigurationRdf: could not determine the number of statements in webapp configuration");
            }
        }
        
        if(_TRACE)
        {
            try
            {
                for(Statement nextStatement : RdfUtils.getAllStatementsFromRepository(this.currentBaseConfigurationRepository))
                {
                    Settings.log.trace(nextStatement.toString());
                }
            }
            catch(Exception ex)
            {
                log.error("Could not dump statements",ex);
            }
        }
        
        return this.currentBaseConfigurationRepository;
    }

	private String getBaseConfigUri()
    {
        if(this.baseConfigUri == null)
        {
            this.baseConfigUri = Settings.getDefaultBaseConfigUriProperty();
        }
        
        return this.baseConfigUri;
    }

	private Collection<Value> getConfigKeyCached(URI subjectKey, URI propertyKey)
    {
        if(cachedWebAppConfigSearches.containsKey(subjectKey))
        {
            Map<URI, Collection<Value>> currentCache = cachedWebAppConfigSearches.get(subjectKey);

            if(currentCache == null)
            {
                // log.info("Settings.getConfigKeyCached: Found subjectKey, but no entry for propertyKey="+propertyKey);
                return null;
            }
            else if(currentCache.containsKey(propertyKey))
            {
                Collection<Value> currentCacheObject = currentCache.get(propertyKey);
                if(currentCacheObject == null)
                {
                    log.error("Settings.getConfigKeyCached: Cache contained a null object for propertyKey="+propertyKey);
                }
                else
                {
                    //log.debug("Settings.getConfigKeyCached: Returning cached object for propertyKey="+propertyKey);
                    return currentCacheObject;
                }
            }
        }

        return null;
    }

	private synchronized Repository getServerConfigurationRdf() throws java.lang.InterruptedException
    {
        if(this.currentConfigurationRepository != null)
        {
            return this.currentConfigurationRepository;
        }
        final long start = System.currentTimeMillis();
        final String configMIMEFormat = this.getBaseConfigMimeFormat();
        final String baseURI = this.getDefaultHostAddress();
        Repository tempConfigurationRepository = null;
        boolean backupNeeded = false;
        boolean backupFailed = false;
        
        try
        {
        	// start off with the schemas in the repository
            tempConfigurationRepository = Settings.getSchemas();
            
            Collection<String> queryConfigLocationsList = this.getStringProperties("queryConfigLocations");
            
            if(queryConfigLocationsList == null)
            {
            	log.fatal("queryConfigLocationsList was null");
            	throw new RuntimeException("Configuration locations were not discovered, failing fast.");
            }
            
            for(final String nextLocation : queryConfigLocationsList)
            {
                // TODO: negotiate between local and non-local addresses better
                // than this
                final RepositoryConnection myRepositoryConnection = tempConfigurationRepository
                        .getConnection();
                try
                {
                    if(nextLocation.startsWith("http://") || nextLocation.startsWith("https://"))
                    {
                        //final URL url = new URL("http://quebec.bio2rdf.org/n3/provider:mirroredgeneid");
                        final URL url = new URL(nextLocation);
                        
                        if(Settings._INFO)
                        {
                            Settings.log.info("Settings.getServerConfigurationRdf: getting configuration from URL: nextLocation="+ nextLocation+" url="+url.toString());
                        }
                        
                        myRepositoryConnection.add(url, baseURI, RDFFormat.forMIMEType(configMIMEFormat));

                        if(Settings._INFO)
                        {
                            Settings.log.info("Settings.getServerConfigurationRdf: finished getting configuration from URL: url="+ url.toString());
                        }
                    }
                    else
                    {
                        if(Settings._INFO)
                        {
                            Settings.log
                                    .info("Settings: getting configuration from file: nextLocation="
                                            + nextLocation);
                        }
                        InputStream nextInputStream = getClass().getResourceAsStream(nextLocation);
                        
                        myRepositoryConnection.add(nextInputStream, baseURI, RDFFormat.forMIMEType(configMIMEFormat));
                        if(Settings._INFO)
                        {
                            Settings.log
                                    .info("Settings: finished getting configuration from file: nextLocation="
                                            + nextLocation);
                        }
                    }
                }
                catch (final RDFParseException rdfpe)
                {
                    Settings.log
                            .fatal(
                                    "Settings: failed to get the configuration repository. Caught RDFParseException. nextLocation="+nextLocation,
                                    rdfpe);
                    throw new RuntimeException(
                            "Settings: failed to initialise the configuration repository. Caught RDFParseException. nextLocation="+nextLocation);
                }
                catch (final OpenRDFException ordfe)
                {
                    Settings.log
                            .fatal(
                                    "Settings: failed to initialise the configuration repository. Caught OpenRDFException. nextLocation="+nextLocation,
                                    ordfe);
                    throw new RuntimeException(
                            "Settings: failed to initialise the configuration repository. Caught OpenRDFException. nextLocation="+nextLocation);
                }
                catch (final java.io.IOException ioe)
                {
                    Settings.log.error("Settings: failed to initialise the configuration repository. Caught java.io.IOException. nextLocation="+nextLocation, ioe);
                    // throw new RuntimeException("Settings: failed to initialise the configuration repository. Caught java.io.IOException");
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
        catch (final OpenRDFException ordfe)
        {
            Settings.log
                    .fatal(
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
                tempConfigurationRepository = Settings.getSchemas();
                
                for(final String nextLocation : this.getStringProperties("backupQueryConfigLocations"))
                {
                    // TODO: negotiate between local and non-local addresses better
                    // than this
                    final RepositoryConnection myRepositoryConnection = tempConfigurationRepository
                            .getConnection();
                    try
                    {
                        if(nextLocation.startsWith("http://") || nextLocation.startsWith("https://"))
                        {
                            //final URL url = new URL("http://quebec.bio2rdf.org/n3/provider:mirroredgeneid");
                            final URL url = new URL(nextLocation);
                            
                            if(Settings._INFO)
                            {
                                Settings.log.info("Settings.getServerConfigurationRdf: getting backup configuration from URL: nextLocation="+ nextLocation+" url="+url.toString());
                            }
                            
                            myRepositoryConnection.add(url, baseURI, RDFFormat.forMIMEType(configMIMEFormat));

                            if(Settings._INFO)
                            {
                                Settings.log.info("Settings.getServerConfigurationRdf: finished getting backup configuration from URL: url="+ url.toString());
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
                            InputStream nextInputStream = getClass().getResourceAsStream(nextLocation);

                            myRepositoryConnection.add(nextInputStream, baseURI, RDFFormat.forMIMEType(configMIMEFormat));
                            if(Settings._INFO)
                            {
                                Settings.log
                                        .info("Settings: finished getting backup configuration from file: nextLocation="
                                                + nextLocation);
                            }
                        }
                    }
                    catch (final RDFParseException rdfpe)
                    {
                        Settings.log
                                .fatal(
                                        "Settings: failed to get the backup configuration repository. Caught RDFParseException",
                                        rdfpe);
                        throw new RuntimeException(
                                "Settings: failed to initialise the backup configuration repository. Caught RDFParseException");
                    }
                    catch (final OpenRDFException ordfe)
                    {
                        Settings.log
                                .fatal(
                                        "Settings: failed to initialise the backup configuration repository. Caught OpenRDFException",
                                        ordfe);
                        throw new RuntimeException(
                                "Settings: failed to initialise the backup configuration repository. Caught OpenRDFException");
                    }
                    catch (final java.io.IOException ioe)
                    {
                        Settings.log.error("Settings: failed to initialise the backup configuration repository. Caught java.io.IOException", ioe);
                        throw new RuntimeException("Settings: failed to initialise the backup configuration repository. Caught java.io.IOException");
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
            catch (final OpenRDFException ordfe)
            {
                Settings.log
                        .fatal(
                                "Settings: failed to initialise the backup configuration repository. Caught OpenRDFException",
                                ordfe);
                throw new RuntimeException(
                        "Settings: failed to initialise the backup configuration repository. Caught OpenRDFException");
            }
        } // end if(backupNeeded)
                
        this.currentConfigurationRepository = tempConfigurationRepository;

        if(Settings._INFO)
        {
            final long end = System.currentTimeMillis();
            Settings.log.info(String.format("%s: timing=%10d",
                    "Settings.getServerConfigurationRdf", (end - start)));

        }
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getServerConfigurationRdf: finished parsing configuration files");
        }

        if(_INFO)
        {
            try
            {
                Settings.log.info("Settings: found "+this.currentConfigurationRepository.getConnection().size()+" statements in model configuration");
            }
            catch(RepositoryException rex)
            {
                Settings.log.error("Settings: could not determine the number of statements in configuration");
            }
        }
        
        return this.currentConfigurationRepository;
    }

	private Collection<Statement> getStatementProperties(URI subjectUri, URI propertyUri, Repository nextRepository)
    {
        if(_TRACE)
            log.trace("Settings.getStatementCollectionPropertiesFromConfig: subjectUri="+subjectUri.stringValue()+" propertyUri="+propertyUri.stringValue()+" nextRepository="+nextRepository);

        try
        {
            return RdfUtils.getStatementsFromRepositoryByPredicateUrisAndSubject(nextRepository, propertyUri, subjectUri);
        }
        catch(Exception ex)
        {
            Settings.log.error("Settings.getStatementCollectionPropertiesFromConfig: error", ex);
        }
        
        return new HashSet<Statement>();
    }

	private Collection<Value> getValueProperties(URI subjectUri, URI propertyUri)
    {
        if(_TRACE)
            log.trace("Settings.getValueCollectionPropertiesFromConfig: subjectUri="+subjectUri.stringValue()+" propertyUri="+propertyUri.stringValue());

        Collection<Value> cachedResults = getConfigKeyCached(subjectUri, propertyUri);
        Collection<Value> results = new HashSet<Value>();
        
        if(cachedResults != null)
        {
            if(_TRACE)
                log.trace("Settings.getValueCollectionPropertiesFromConfig: returning cached values subjectUri="+subjectUri.stringValue()+" propertyUri="+propertyUri.stringValue());

            return cachedResults;
        }
        else
        {
            try
            {
                results = getValueProperties(subjectUri, propertyUri, getWebAppConfigurationRdf());
            
                if(results != null)
                {
                    doConfigKeyCache(subjectUri, propertyUri, results);
                }
            }
            catch(InterruptedException ex)
            {
                throw new RuntimeException(ex);
            }
        }
        
        return results;
    }

	private Collection<Value> getValueProperties(URI subjectUri, URI propertyUri, Repository nextRepository)
    {
        Collection<Value> results = new HashSet<Value>();
        
        try
        {
            results = RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject(nextRepository, propertyUri, subjectUri);
        }
        catch(Exception ex)
        {
            Settings.log.error("this.getValueCollectionPropertiesFromConfig: error", ex);
        }
        
        return results;
    }

	private synchronized Repository getWebAppConfigurationRdf() throws java.lang.InterruptedException
    {
        if(_TRACE)
            Settings.log.trace("Settings.getWebAppConfigurationRdf: entering");

        if(this.currentWebAppConfigurationRepository != null)
        {
            return this.currentWebAppConfigurationRepository;
        }
        
        if(_DEBUG)
            Settings.log.debug("Settings.getWebAppConfigurationRdf: constructing a new repository");
        
        final long start = System.currentTimeMillis();
        Repository nextBaseConfigurationRepository = getBaseConfigurationRdf();
        final String configMIMEFormat = this.getBaseConfigMimeFormat();
        final String baseURI = this.getBaseConfigUri();
        Repository tempConfigurationRepository = null;
        Repository finalConfigurationRepository = null;
        boolean backupNeeded = false;
        boolean backupFailed = false;

        RepositoryConnection finalRepositoryConnection = null;
        
        try
        {
            finalConfigurationRepository = new SailRepository(new MemoryStore());
            finalConfigurationRepository.initialize();
            
            finalRepositoryConnection = finalConfigurationRepository.getConnection();
            
            if(_DEBUG)
                Settings.log.debug("Settings.getWebAppConfigurationRdf: temp repository initialised");

            // Settings.log.error("Settings.getWebAppConfigurationRdf: Settings.WEBAPP_CONFIG_LOCATION_LIST.size()="+Settings.WEBAPP_CONFIG_LOCATION_LIST);

            ValueFactory f = finalConfigurationRepository.getValueFactory();
            
            URI subjectConfigUri = f.createURI(baseURI);

            URI webappConfigLocationsUri = f.createURI("http://purl.org/queryall/webapp_configuration:webappConfigLocations");
            
            URI activeWebappConfigsUri = f.createURI("http://purl.org/queryall/webapp_configuration:activeWebappConfigs");
            
            Collection<Value> webappConfigFiles = getValueProperties(
            		subjectConfigUri, webappConfigLocationsUri, nextBaseConfigurationRepository);

            Collection<Value> activeWebappConfigs = getValueProperties(
            		subjectConfigUri, activeWebappConfigsUri, nextBaseConfigurationRepository);
            
            Collection<String> tempCollection = new HashSet<String>();
			
			for(Value nextValue1 : activeWebappConfigs)
			{
			    tempCollection.add(nextValue1.stringValue());
			}
			
			this.setWebappConfigUriList(tempCollection);
            
            
            if(_DEBUG)
            {
                log.debug("webappConfigFiles.size()="+webappConfigFiles.size());
                log.debug("activeWebappConfigs.size()="+activeWebappConfigs.size());
            }
            
            // for(final String nextLocation : BASE_CONFIG_FILES.split(","))
            for(Value nextConfigFile : webappConfigFiles)
            {
                tempConfigurationRepository = new SailRepository(new MemoryStore());
                tempConfigurationRepository.initialize();

                String nextLocation = nextConfigFile.stringValue();
                
                // TODO: negotiate between local and non-local addresses better than this
            
                
                final RepositoryConnection myRepositoryConnection = tempConfigurationRepository.getConnection();
                

                try
                {
                    if(nextLocation.startsWith("http://") || nextLocation.startsWith("https://"))
                    {
                        //final URL url = new URL("http://quebec.bio2rdf.org/n3/provider:mirroredgeneid");
                        final URL url = new URL(nextLocation);
                        
                        if(Settings._INFO)
                        {
                            Settings.log.info("Settings.getWebAppConfigurationRdf: getting configuration from URL: nextLocation="+ nextLocation+" url="+url.toString()+" myRepositoryConnection.size()="+myRepositoryConnection.size());
                        }
                        
                        myRepositoryConnection.add(url, baseURI, RDFFormat.forMIMEType(configMIMEFormat));

                        if(Settings._INFO)
                        {
                            Settings.log.info("Settings.getWebAppConfigurationRdf: finished getting configuration from URL: url="+ url.toString()+" myRepositoryConnection.size()="+myRepositoryConnection.size());
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
                        InputStream nextInputStream = getClass().getResourceAsStream(nextLocation);

                        myRepositoryConnection.add(nextInputStream, baseURI, RDFFormat.forMIMEType(configMIMEFormat));
                        if(Settings._INFO)
                        {
                            Settings.log
                                    .info("Settings.getWebAppConfigurationRdf: finished getting configuration from file: nextLocation="
                                            + nextLocation);
                        }
                        
                    }

                    for(Value nextValue : activeWebappConfigs)
                    {
                        log.debug("Settings.getWebAppConfigurationRdf: started adding statements to finalrepository for nextValue="+nextValue.stringValue()+" finalRepositoryConnection.size()="+finalRepositoryConnection.size());
                        getWebappConfigUriList().add(nextValue.stringValue());
                        finalRepositoryConnection.add(myRepositoryConnection.getStatements((URI)nextValue, (URI)null, (Resource)null, true));
                        log.debug("Settings.getWebAppConfigurationRdf: finished adding statements to finalrepository for nextValue="+nextValue.stringValue()+" finalRepositoryConnection.size()="+finalRepositoryConnection.size());
                    }
                }
                catch (final RDFParseException rdfpe)
                {
                    Settings.log
                            .fatal(
                                    "Settings.getWebAppConfigurationRdf: failed to get the webapp configuration repository. Caught RDFParseException. nextLocation="+nextLocation,
                                    rdfpe);
                    throw new RuntimeException(
                            "Settings.getWebAppConfigurationRdf: failed to initialise the webapp configuration repository. Caught RDFParseException. nextLocation="+nextLocation);
                }
                catch (final OpenRDFException ordfe)
                {
                    Settings.log
                            .fatal(
                                    "Settings.getWebAppConfigurationRdf: failed to initialise the webapp configuration repository. Caught OpenRDFException. nextLocation="+nextLocation,
                                    ordfe);
                    throw new RuntimeException(
                            "Settings.getWebAppConfigurationRdf: failed to initialise the webapp configuration repository. Caught OpenRDFException. nextLocation="+nextLocation);
                }
                catch (final java.io.IOException ioe)
                {
                    Settings.log.error("Settings.getWebAppConfigurationRdf: failed to initialise the webapp configuration repository. Caught java.io.IOException. nextLocation="+nextLocation, ioe);
                    // throw new RuntimeException("Settings: failed to initialise the configuration repository. Caught java.io.IOException");
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
        catch (final OpenRDFException ordfe)
        {
            Settings.log
                    .fatal(
                            "Settings.getWebAppConfigurationRdf: failed to initialise the webapp configuration repository. Caught OpenRDFException",
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
                catch(Exception ex)
                {
                    log.error(ex);
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
            Settings.log.info(String.format("%s: timing=%10d",
                    "Settings.getWebAppConfigurationRdf", (end - start)));

        }
        
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getWebAppConfigurationRdf: finished parsing configuration files");
        }

        if(_INFO)
        {
            try
            {
                Settings.log.info("Settings.getWebAppConfigurationRdf: found "+this.currentWebAppConfigurationRepository.getConnection().size()+" statements in webapp configuration");
            }
            catch(RepositoryException rex)
            {
                Settings.log.error("Settings.getWebAppConfigurationRdf: could not determine the number of statements in webapp configuration");
            }
        }
        
        return this.currentWebAppConfigurationRepository;
    }

	public static Repository getSchemas()
	{
		return getSchemas(null);
	}
	
	public static Repository getSchemas(URI contextUri)
	{
		//Repository myRepository = new SailRepository(new ForwardChainingRDFSInferencer(new MemoryStore()));
		Repository myRepository = new SailRepository(new MemoryStore());
		
		try
		{
			myRepository.initialize();
		}
		catch(RepositoryException e)
		{
			log.fatal("Could not initialise repository for schemas");
			throw new RuntimeException(e);
		}

		try
		{
			if(!ProviderImpl.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
			{
				log.error("QueryAllSchemaServlet: Provider schema was not placed correctly in the rdf store");
			}
		}
		catch(Exception ex)
		{
			log.error(
					"QueryAllSchemaServlet: Problem generating Provider schema RDF with type="
							+ ex.getClass().getName(), ex);
		}

		try
		{
			if(!HttpProviderImpl.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
			{
				log.error("QueryAllSchemaServlet: HttpProviderImpl schema was not placed correctly in the rdf store");
			}
		}
		catch(Exception ex)
		{
			log.error(
					"QueryAllSchemaServlet: Problem generating HttpProviderImpl schema RDF with type="
							+ ex.getClass().getName(), ex);
		}

		try
		{
			if(!ProjectImpl.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
			{
				log.error("QueryAllSchemaServlet: Project schema was not placed correctly in the rdf store");
			}
		}
		catch(Exception ex)
		{
			log.error(
					"QueryAllSchemaServlet: Problem generating Project schema RDF with type="
							+ ex.getClass().getName(), ex);
		}

		try
		{
			if(!QueryTypeImpl.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
			{
				log.error("QueryAllSchemaServlet: QueryType schema was not placed correctly in the rdf store");
			}
		}
		catch(Exception ex)
		{
			log.error(
					"QueryAllSchemaServlet: Problem generating QueryType schema RDF with type="
							+ ex.getClass().getName(), ex);
		}

		try
		{
			if(!RegexNormalisationRuleImpl.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
			{
				log.error("QueryAllSchemaServlet: RegexNormalisationRuleImpl schema was not placed correctly in the rdf store");
			}
		}
		catch(Exception ex)
		{
			log.error(
					"QueryAllSchemaServlet: Problem generating RegexNormalisationRuleImpl schema RDF with type="
							+ ex.getClass().getName(), ex);
		}

		try
		{
			if(!SparqlNormalisationRuleImpl.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
			{
				log.error("QueryAllSchemaServlet: SparqlNormalisationRuleImpl schema was not placed correctly in the rdf store");
			}
		}
		catch(Exception ex)
		{
			log.error(
					"QueryAllSchemaServlet: Problem generating SparqlNormalisationRuleImpl schema RDF with type="
							+ ex.getClass().getName(), ex);
		}

		try
		{
			if(!XsltNormalisationRuleImpl.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
			{
				log.error("QueryAllSchemaServlet: XsltNormalisationRuleImpl schema was not placed correctly in the rdf store");
			}
		}
		catch(Exception ex)
		{
			log.error(
					"QueryAllSchemaServlet: Problem generating SparqlNormalisationRuleImpl schema RDF with type="
							+ ex.getClass().getName(), ex);
		}

		try
		{
			if(!RuleTestImpl.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
			{
				log.error("QueryAllSchemaServlet: RuleTest schema was not placed correctly in the rdf store");
			}
		}
		catch(Exception ex)
		{
			log.error(
					"QueryAllSchemaServlet: Problem generating RuleTest schema RDF with type="
							+ ex.getClass().getName(), ex);
		}

		try
		{
			if(!NamespaceEntryImpl.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
			{
				log.error("QueryAllSchemaServlet: NamespaceEntry schema was not placed correctly in the rdf store");
			}
		}
		catch(Exception ex)
		{
			log.error(
					"QueryAllSchemaServlet: Problem generating NamespaceEntry schema RDF with type="
							+ ex.getClass().getName(), ex);
		}

		try
		{
			if(!ProfileImpl.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
			{
				log.error("QueryAllSchemaServlet: Profile schema was not placed correctly in the rdf store");
			}
		}
		catch(Exception ex)
		{
			log.error(
					"QueryAllSchemaServlet: Problem generating Profile schema RDF with type="
							+ ex.getClass().getName(), ex);
		}

		try
		{
			if(!StatisticsEntry.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
			{
				log.error("QueryAllSchemaServlet: Statistics schema was not placed correctly in the rdf store");
			}
		}
		catch(Exception ex)
		{
			log.error(
					"QueryAllSchemaServlet: Problem generating Statistics schema RDF with type="
							+ ex.getClass().getName(), ex);
		}

		try
		{
			if(!ProvenanceRecord.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
			{
				log.error("QueryAllSchemaServlet: Provenance schema was not placed correctly in the rdf store");
			}
		}
		catch(Exception ex)
		{
			log.error(
					"QueryAllSchemaServlet: Problem generating Provenance schema RDF with type="
							+ ex.getClass().getName(), ex);
		}

		try
		{
			if(!QueryBundle.schemaToRdf(myRepository, contextUri, Settings.CONFIG_API_VERSION))
			{
				log.error("QueryAllSchemaServlet: QueryBundle schema was not placed correctly in the rdf store");
			}
		}
		catch(Exception ex)
		{
			log.error(
					"QueryAllSchemaServlet: Problem generating QueryBundle schema RDF with type="
							+ ex.getClass().getName(), ex);
		}
		
		return myRepository;
	}

	@Override
	public void addNamespaceEntry(NamespaceEntry nextNamespaceEntry)
	{
		// TODO Auto-generated method stub
		throw new RuntimeException("TODO: Implement me!");
	}

	@Override
	public void addNormalisationRule(NormalisationRule nextNormalisationRule)
	{
		// TODO Auto-generated method stub
		throw new RuntimeException("TODO: Implement me!");
	}

	@Override
	public void addProfile(Profile nextProfile)
	{
		// TODO Auto-generated method stub
		throw new RuntimeException("TODO: Implement me!");
	}

	@Override
	public void addProvider(Provider nextProvider)
	{
		// TODO Auto-generated method stub
		throw new RuntimeException("TODO: Implement me!");
	}

	@Override
	public void addQueryType(QueryType nextQueryType)
	{
		// TODO Auto-generated method stub
		throw new RuntimeException("TODO: Implement me!");
	}

	@Override
	public void addRuleTest(RuleTest nextRuleTest)
	{
		// TODO Auto-generated method stub
		throw new RuntimeException("TODO: Implement me!");
	}

	/**
	 * @return the cachedNamespacePrefixToUriEntries
	 */
	public Map<String, Collection<URI>> getNamespacePrefixesToUris()
	{
		if(this.cachedNamespacePrefixToUriEntries == null)
		{
			this.getAllNamespaceEntries();
		}
		
		return this.cachedNamespacePrefixToUriEntries;
	}

	/**
	 * @param cachedNamespacePrefixToUriEntries the cachedNamespacePrefixToUriEntries to set
	 */
	public void setNamespacePrefixesToUris(
			Map<String, Collection<URI>> cachedNamespacePrefixToUriEntries)
	{
		this.cachedNamespacePrefixToUriEntries = cachedNamespacePrefixToUriEntries;
	}
}
