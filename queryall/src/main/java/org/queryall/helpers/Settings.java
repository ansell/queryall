package org.queryall.helpers;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
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
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;

import org.queryall.api.NamespaceEntry;
import org.queryall.api.NormalisationRule;
import org.queryall.api.Profile;
import org.queryall.api.Provider;
import org.queryall.api.QueryAllConfiguration;
import org.queryall.api.QueryType;
import org.queryall.api.RuleTest;
import org.queryall.api.Template;
import org.queryall.impl.HttpProviderImpl;
import org.queryall.impl.NamespaceEntryImpl;
import org.queryall.impl.NormalisationRuleImpl;
import org.queryall.impl.ProfileImpl;
import org.queryall.impl.ProjectImpl;
import org.queryall.impl.ProviderImpl;
import org.queryall.impl.QueryTypeImpl;
import org.queryall.impl.RegexNormalisationRuleImpl;
import org.queryall.impl.RuleTestImpl;
import org.queryall.impl.SparqlNormalisationRuleImpl;
import org.queryall.impl.TemplateImpl;
import org.queryall.impl.XsltNormalisationRuleImpl;
import org.queryall.queryutils.ProvenanceRecord;
import org.queryall.queryutils.QueryBundle;
import org.queryall.statistics.StatisticsEntry;

/**
 * A class used to get access to settings
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 * @version $Id: $
 */
public class Settings extends QueryAllConfiguration
{
    public static final Logger log = Logger
            .getLogger(Settings.class.getName());
    public static final boolean _TRACE = Settings.log.isTraceEnabled();
    public static final boolean _DEBUG = Settings.log.isDebugEnabled();
    public static final boolean _INFO = Settings.log.isInfoEnabled();

    // This matches the queryall.properties file where
    // the generally static API specific section of the configuration settings are stored
    public static final String DEFAULT_PROPERTIES_BUNDLE_NAME = "queryall";
    public static final int CONFIG_API_VERSION = 4;
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
    
    private static Collection<Provider> getProvidersForQueryTypeFromList(
            URI customService, Collection<Provider> knownProviders)
    {
        final Collection<Provider> results = new HashSet<Provider>();
        for(final Provider nextProvider : knownProviders)
        {
            if(nextProvider.containsQueryTypeUri(customService))
            {
                results.add(nextProvider);
            }
        }
        return results;
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
    private String defaultOntologyTermUriPrefix = Settings.getSystemOrPropertyString("queryall.ontologyTermUriPrefix", "http://purl.org/queryall/");
    private String defaultOntologyTermUriSuffix = Settings.getSystemOrPropertyString("queryall.ontologyTermUriSuffix", ":");
    private String currentOntologyTermUriPrefix = defaultOntologyTermUriPrefix;
    private String currentOntologyTermUriSuffix = defaultOntologyTermUriSuffix;
    private String defaultRdfWebappConfigurationNamespace = Settings.getSystemOrPropertyString("queryall.WebappConfigurationNamespace", "webapp_configuration");
    private String defaultRdfProjectNamespace = Settings.getSystemOrPropertyString("queryall.ProjectNamespace", "project");
    private String defaultRdfProviderNamespace = Settings.getSystemOrPropertyString("queryall.ProviderNamespace", "provider");
    private String defaultRdfTemplateNamespace = Settings.getSystemOrPropertyString("queryall.TemplateNamespace", "template");
    private String defaultRdfQueryNamespace = Settings.getSystemOrPropertyString("queryall.QueryNamespace", "query");
    private String defaultRdfQuerybundleNamespace = Settings.getSystemOrPropertyString("queryall.QueryBundleNamespace", "querybundle");
    private String defaultRdfRuleNamespace = Settings.getSystemOrPropertyString("queryall.RuleNamespace", "rdfrule");
    private String defaultRdfRuleTestNamespace = Settings.getSystemOrPropertyString("queryall.RuleTestNamespace", "ruletest");
            
    private String defaultRdfNamespaceEntryNamespace = Settings.getSystemOrPropertyString("queryall.NamespaceEntryNamespace", "ns");
    private String defaultRdfProfileNamespace = Settings.getSystemOrPropertyString("queryall.ProfileNamespace", "profile");
    private String defaultRdfProvenanceNamespace = Settings.getSystemOrPropertyString("queryall.ProvenanceNamespace", "provenance");
    private String defaultRdfStatisticsNamespace = Settings.getSystemOrPropertyString("queryall.StatisticsNamespace", "statistics");
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

    private String defaultAutogeneratedQueryPrefix = Settings.getSystemOrPropertyString("queryall.AutogeneratedQueryPrefix", "autogen-");
    private String defaultAutogeneratedQuerySuffix = Settings.getSystemOrPropertyString("queryall.AutogeneratedQuerySuffix", "");
    private String defaultAutogeneratedProviderPrefix = Settings.getSystemOrPropertyString("queryall.AutogeneratedProviderPrefix", "autogen-");
    private String defaultAutogeneratedProviderSuffix = Settings.getSystemOrPropertyString("queryall.AutogeneratedProviderSuffix", "");
    
    private String currentAutogeneratedQueryPrefix = defaultAutogeneratedQueryPrefix;

    private String currentAutogeneratedQuerySuffix = defaultAutogeneratedQuerySuffix;
    private String currentAutogeneratedProviderPrefix = defaultAutogeneratedProviderPrefix;
    private String currentAutogeneratedProviderSuffix = defaultAutogeneratedProviderSuffix;
    
    // these values are initialised dynamically for the lifetime of the class
    public String SUBVERSION_INFO = null;
    private String baseConfigLocation = null;
    private String baseConfigUri = null;
    
    private String baseConfigMimeFormat = null;
    private Repository currentBaseConfigurationRepository = null;
    private Repository currentWebAppConfigurationRepository = null;
    private Repository currentConfigurationRepository = null;
    private Map<URI, Provider> cachedProviders = null;
    private Map<URI, NormalisationRule> cachedNormalisationRules = null;
    private Map<URI, RuleTest> cachedRuleTests = null;
    private Map<URI, QueryType> cachedCustomQueries = null;
    private Map<URI, Template> cachedTemplates = null;
    private Map<URI, Profile> cachedProfiles = null;
    
    private Map<URI, NamespaceEntry> cachedNamespaceEntries = null;
    
    private Map<String, Collection<URI>> cachedNamespacePrefixToUriEntries = null;
    
    private Map<URI, Map<URI, Collection<Value>>> cachedWebAppConfigSearches = new Hashtable<URI, Map<URI, Collection<Value>>>(200);
    
    private Collection<String> webappConfigUriList = new HashSet<String>();
    
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
    	
    }
    
    public Settings(String baseConfigLocation, String baseConfigMimeFormat, String baseConfigUri)
    {
        // Do a quick test on the base config file existence
        
        InputStream baseConfig = this.getClass().getResourceAsStream(baseConfigLocation);
        
        if(baseConfig == null)
        {
        	log.debug("Settings.init: TEST: baseConfig was null baseConfigLocation="+baseConfigLocation);
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
                            + this.getBooleanPropertyFromConfig("enablePeriodicConfigurationRefresh", true)
                            + " Settings.PERIODIC_REFRESH_MILLISECONDS="
                            + this.getLongPropertyFromConfig("periodicConfigurationMilliseconds", 60000L)
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
        
        boolean enablePeriodicConfigurationRefresh = this.getBooleanPropertyFromConfig("enablePeriodicConfigurationRefresh", true);
        long periodicConfigurationMilliseconds = this.getLongPropertyFromConfig("periodicConfigurationMilliseconds", 60000L);
        
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
                if(this.cachedNamespacePrefixToUriEntries != null)
                {
                    synchronized(this.cachedNamespacePrefixToUriEntries)
                    {
                        this.cachedNamespacePrefixToUriEntries = null;
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
                
                if(this.cachedTemplates != null)
                {
                    synchronized(this.cachedTemplates)
                    {
                        this.cachedTemplates = null;
                    }
                }
                this.getAllTemplates();
                
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
            Map<URI, NamespaceEntry> results = getNamespaceEntries(getServerConfigurationRdf());
            
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
            
            this.cachedNamespacePrefixToUriEntries = tempNamespacePrefixToUriEntries;
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
            
            Map<URI, NormalisationRule> results = getNormalisationRules(myRepository);
            
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
            
            Map<URI, Profile> results = getProfiles(myRepository);
            
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
            
            results = getProviders(myRepository);
            
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
            
            Map<URI, QueryType> results = getQueryTypes(myRepository);
            
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
            
            Map<URI, RuleTest> results = getRuleTests(myRepository);
            
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
    
    @Override
	public synchronized Map<URI, Template> getAllTemplates()
    {
        if(this.cachedTemplates != null)
        {
            return this.cachedTemplates;
        }

        try
        {
            final Repository myRepository = this.getServerConfigurationRdf();
            
            Map<URI, Template> results = getTemplates(myRepository);
            
            if(_INFO)
            {
                log.info("Settings.getAllTemplates: found "+results.size()+" templates");
            }
            
	        this.cachedTemplates = results;
	        
	        return results;
        }
        catch(InterruptedException ie)
        {
        	Settings.log.fatal("Settings.getAllTemplates: caught java.lang.InterruptedException: not throwing it.", ie);

        	return null;
        }
    }
    
    public List<Profile> getAndSortProfileList(Collection<URI> nextProfileUriList, int nextSortOrder)
    {
        final Map<URI, Profile> allProfiles = this.getAllProfiles();
        final List<Profile> results = new LinkedList<Profile>();
        
        if(nextProfileUriList == null)
        {
        	Settings.log
                    .error("Settings.getAndSortProfileList: nextProfileUriList was null!");

        	throw new RuntimeException("Settings.getAndSortProfileList: nextProfileUriList was null!");
        }
        else
        {
            for(final URI nextProfileUri : nextProfileUriList)
            {
                // log.error("Settings.getAndSortProfileList: nextProfileUri="+nextProfileUri);
                if(allProfiles.containsKey(nextProfileUri))
                {
                    final Profile nextProfileObject = allProfiles
                            .get(nextProfileUri);
                    results.add(nextProfileObject);
                }
                else if(Settings._INFO)
                {
                    Settings.log
                            .info("Settings.getAndSortProfileList: Could not get profile by URI nextProfileUri="
                                    + nextProfileUri);
                }
            }
        }
        
        if(nextSortOrder == Constants.LOWEST_ORDER_FIRST)
        {
            Collections.sort(results);
        }
        else if(nextSortOrder == Constants.HIGHEST_ORDER_FIRST)
        {
            Collections.sort(results, Collections.reverseOrder());
        }
        else
        {
            throw new RuntimeException(
                    "Settings.getAndSortProfileList: sortOrder unrecognised nextSortOrder="
                            + nextSortOrder);
        }
        return results;
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
    
    public boolean getBooleanPropertyFromConfig(String key, boolean defaultValue)
    {
        if(_TRACE)
            log.trace("Settings.getBooleanPropertyFromConfig: key="+key+" defaultValue="+defaultValue);

        boolean result = defaultValue;

        Collection<Value> values = getValueCollectionPropertiesFromConfig(key);
        
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
        return this.getStringPropertyFromConfig("uriPrefix", "http://")+this.getStringPropertyFromConfig("hostName", "bio2rdf.org")+this.getStringPropertyFromConfig("uriSuffix", "/");
    }
    
    public Collection<Provider> getDefaultProviders(QueryType queryType)
    {
        final Collection<Provider> results = new HashSet<Provider>();

        // Return an empty collection if this query type does not include defaults
    	if(queryType.getIncludeDefaults())
    	{
	        for(final Provider nextProvider : this.getAllProviders().values())
	        {
	            if(nextProvider.getIsDefaultSource()
	                    && nextProvider.containsQueryTypeUri(queryType.getKey()))
	            {
	                results.add(nextProvider);
	            }
	        }
    	}
        
        return Collections.unmodifiableCollection(results);
    }
    
    public float getFloatPropertyFromConfig(String key, float defaultValue)
    {
        float result = defaultValue;
        
        if(_TRACE)
            log.trace("Settings.getFloatPropertyFromConfig: key="+key+" defaultValue="+defaultValue);

        Collection<Value> values = getValueCollectionPropertiesFromConfig(key);
        
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
    
    public int getIntPropertyFromConfig(String key, int defaultValue)
    {
        int result = defaultValue;
        
        if(_TRACE)
            log.trace("Settings.getIntPropertyFromConfig: key="+key+" defaultValue="+defaultValue);

        Collection<Value> values = getValueCollectionPropertiesFromConfig(key);
        
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
    
    public URI getURIPropertyFromConfig(String key, URI defaultValue)
    {
    	URI result = defaultValue;
    	
        if(_TRACE)
            log.trace("Settings.getUriPropertyFromConfig: key="+key+" defaultValue="+defaultValue);

        Collection<URI> values = getURICollectionPropertiesFromConfig(key);
        
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
    
	public long getLongPropertyFromConfig(String key, long defaultValue)
    {
        long result = defaultValue;
        
        if(_TRACE)
            log.trace("Settings.getLongPropertyFromConfig: key="+key+" defaultValue="+defaultValue);

        Collection<Value> values = getValueCollectionPropertiesFromConfig(key);
        
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
    
    @Override
	public Map<URI, NamespaceEntry> getNamespaceEntries(Repository myRepository)
    {
        final Map<URI, NamespaceEntry> results = new Hashtable<URI, NamespaceEntry>();
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getNamespaceEntries: started parsing namespace entry configurations");
        }
        final long start = System.currentTimeMillis();

        final URI namespaceEntryTypeUri = NamespaceEntryImpl.getNamespaceTypeUri();
        try
        {
            final RepositoryConnection con = myRepository.getConnection();

            for(Statement nextNamespaceEntry : con.getStatements(null, RDF.TYPE, namespaceEntryTypeUri, true).asList())
            {
            	URI nextSubjectUri = (URI)nextNamespaceEntry.getSubject();
            	results.put(nextSubjectUri, new NamespaceEntryImpl(
            			con.getStatements(nextSubjectUri, (URI) null, (Value) null, true).asList(), 
            			nextSubjectUri, 
            			Settings.CONFIG_API_VERSION));
            }                
        }
        catch (final OpenRDFException e)
        {
            // handle exception
            Settings.log.fatal("Settings.getNamespaceEntries:", e);
        }

        
        if(Settings._INFO)
        {
            final long end = System.currentTimeMillis();
            Settings.log.info(String.format("%s: timing=%10d",
                    "Settings.getNamespaceEntries", (end - start)));
        }
        
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getNamespaceEntries: finished getting namespace entry information");
        }
        
        return results;
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
    
    public Collection<URI> getNamespaceUrisForTitle(String namespacePrefix)
    {
        Collection<URI> results = new HashSet<URI>();
        
        if(this.cachedNamespacePrefixToUriEntries == null)
        {
            // this function initialises the namespace prefix to URI cache
            this.getAllNamespaceEntries();
        }
        
        results = this.cachedNamespacePrefixToUriEntries.get(namespacePrefix);
        
        if(results == null)
        	return null;
        else
        	return Collections.unmodifiableCollection(results);
    }
    
    @Override
	public Map<URI, NormalisationRule> getNormalisationRules(Repository myRepository)
    {
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getNormalisationRules: started parsing rdf normalisation rules");
        }

        final long start = System.currentTimeMillis();

        final Map<URI, NormalisationRule> results = new Hashtable<URI, NormalisationRule>();
        try
        {
            final Repository configRepository = this.getServerConfigurationRdf();
            
            // TODO: use reflection and dynamic loading of rules classes to make this process generic and static to future additions
            try
            {
                final RepositoryConnection con = configRepository.getConnection();

                // Import Regular Expression Normalisation Rules first
                final URI regexRuleTypeUri = RegexNormalisationRuleImpl.getRegexRuleTypeUri();
                for(Statement nextRegexRule : con.getStatements(null, RDF.TYPE, regexRuleTypeUri, true).asList())
                {
                	URI nextSubjectUri = (URI)nextRegexRule.getSubject();
                	results.put(nextSubjectUri, new RegexNormalisationRuleImpl(
                			con.getStatements(nextSubjectUri, (URI) null, (Value) null, true).asList(), 
                			nextSubjectUri, 
                			Settings.CONFIG_API_VERSION));
                }                

        		// Then do the same thing for SPARQL Normalisation Rules
                final URI sparqlRuleTypeUri = SparqlNormalisationRuleImpl.getSparqlRuleTypeUri();
                for(Statement nextSparqlRule : con.getStatements(null, RDF.TYPE, sparqlRuleTypeUri, true).asList())
                {
                	URI nextSubjectUri = (URI)nextSparqlRule.getSubject();
                	results.put(nextSubjectUri, new SparqlNormalisationRuleImpl(
                			con.getStatements(nextSubjectUri, (URI) null, (Value) null, true).asList(), 
                			nextSubjectUri, 
                			Settings.CONFIG_API_VERSION));
                }                

                // Then do the same thing for XSLT Normalisation Rules
                final URI xsltRuleTypeUri = XsltNormalisationRuleImpl.getXsltRuleTypeUri();
                for(Statement nextXsltRule : con.getStatements(null, RDF.TYPE, xsltRuleTypeUri, true).asList())
                {
                	URI nextSubjectUri = (URI)nextXsltRule.getSubject();
                	results.put(nextSubjectUri, new XsltNormalisationRuleImpl(
                			con.getStatements(nextSubjectUri, (URI) null, (Value) null, true).asList(), 
                			nextSubjectUri, 
                			Settings.CONFIG_API_VERSION));
                }
            }
            catch (final OpenRDFException e)
            {
                // handle exception
                Settings.log.fatal("Settings.getNormalisationRules:", e);
            }
        }
        catch(java.lang.InterruptedException ie)
        {
            Settings.log.fatal("Settings.getNormalisationRules: caught java.lang.InterruptedException: not throwing it.", ie);
        }
        if(Settings._INFO)
        {
            final long end = System.currentTimeMillis();
            Settings.log.info(String.format("%s: timing=%10d",
                    "Settings.getNormalisationRules", (end - start)));
        }
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getNormalisationRules: finished parsing normalisation rules");
        }
        
        return results;
    }
    
    public List<NormalisationRule> getNormalisationRulesForUris(
            Collection<URI> rdfNormalisationsNeeded, int sortOrder)
    {
        final List<NormalisationRule> results = new ArrayList<NormalisationRule>();
        // final List<NormalisationRule> intermediateResults = new ArrayList<NormalisationRule>();
        final Map<URI, NormalisationRule> allNormalisationRules = this.getAllNormalisationRules();
        
        for(final URI nextProviderNormalisationRule : rdfNormalisationsNeeded)
        {
            if(allNormalisationRules.containsKey(nextProviderNormalisationRule))
            {
                results.add(allNormalisationRules.get(nextProviderNormalisationRule));
            }
        }
        
        if(Settings._TRACE)
        {
            Settings.log.trace("Settings: rule sorting started");
        }
        if(sortOrder == Constants.LOWEST_ORDER_FIRST)
        {
            Collections.sort(results);
            
            if(Settings._DEBUG)
            {
                int testOrder = -1;
                for(final NormalisationRule nextRule : results)
                {
                    if(testOrder == -1)
                    {
                        if(Settings._TRACE)
                        {
                            Settings.log
                                    .trace("Settings: rule sorting verification starting at nextRule.getOrder()="
                                            + nextRule.getOrder());
                        }
                        testOrder = nextRule.getOrder();
                    }
                    else if(testOrder > nextRule.getOrder())
                    {
                        Settings.log
                                .error("Settings: rules were not sorted properly testOrder="
                                        + testOrder
                                        + " nextRule.getOrder()="
                                        + nextRule.getOrder());
                    }
                    else if(testOrder < nextRule.getOrder())
                    {
                        if(Settings._TRACE)
                        {
                            Settings.log
                                    .trace("Settings: rule verification stepping from testOrder="
                                            + testOrder
                                            + " to nextRule.getOrder()="
                                            + nextRule.getOrder());
                        }
                        testOrder = nextRule.getOrder();
                    }
                }
            } // end if(Settings._TRACE)
        }
        else if(sortOrder == Constants.HIGHEST_ORDER_FIRST)
        {
            Collections.sort(results, Collections.reverseOrder());
            
            if(Settings._DEBUG)
            {
                int testOrder = -1;
                
                for(final NormalisationRule nextRule : results)
                {
                    if(testOrder == -1)
                    {
                        if(Settings._TRACE)
                        {
                            Settings.log
                                    .trace("Settings: rule sorting verification starting at nextRule.getOrder()="
                                            + nextRule.getOrder());
                        }
                        testOrder = nextRule.getOrder();
                    }
                    else if(testOrder < nextRule.getOrder())
                    {
                        Settings.log
                                .error("Settings: rules were not sorted properly testOrder="
                                        + testOrder
                                        + " nextRule.getOrder()="
                                        + nextRule.getOrder());
                    }
                    else if(testOrder > nextRule.getOrder())
                    {
                        if(Settings._TRACE)
                        {
                            Settings.log
                                    .trace("Settings: rule verification stepping from testOrder="
                                            + testOrder
                                            + " to nextRule.getOrder()="
                                            + nextRule.getOrder());
                        }
                        testOrder = nextRule.getOrder();
                    }
                }
            } // end if(Settings._TRACE)
        }
        else
        {
            Settings.log
                    .error("Settings: sortOrder was not recognised sortOrder="
                            + sortOrder);
        }
        
        if(Settings._TRACE)
        {
            Settings.log.trace("Settings: rule sorting finished");
        }
        
        return results;
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
        return Pattern.compile(this.getStringPropertyFromConfig("plainNamespaceAndIdentifierRegex", ""));
    }
    
    public Pattern getPlainNamespacePattern()
    {
        return Pattern.compile(this.getStringPropertyFromConfig("plainNamespaceRegex", ""));
    }
    
    @Override
	public Map<URI, Profile> getProfiles(Repository myRepository)
    {
    	final Map<URI, Profile> results = new Hashtable<URI, Profile>();
        
    	if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getProfiles: started parsing profile configurations");
        }
        final long start = System.currentTimeMillis();

        final URI profileTypeUri = ProfileImpl.getProfileTypeUri();

        try
        {
            final RepositoryConnection con = myRepository.getConnection();

            for(Statement nextProvider : con.getStatements(null, RDF.TYPE, profileTypeUri, true).asList())
            {
            	URI nextSubjectUri = (URI)nextProvider.getSubject();
            	results.put(nextSubjectUri, new ProfileImpl(
            			con.getStatements(nextSubjectUri, (URI) null, (Value) null, true).asList(), 
            			nextSubjectUri, 
            			Settings.CONFIG_API_VERSION));
            }                
        }
        catch (final OpenRDFException e)
        {
            // handle exception
            Settings.log.fatal("Settings.getProviders:", e);
        }

        if(Settings._INFO)
        {
            final long end = System.currentTimeMillis();
            Settings.log.info(String.format("%s: timing=%10d",
                    "Settings.getProfiles", (end - start)));
        }
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getProfiles: finished parsing profiles");
        }
        
        return results;
    }

    @Override
	public Map<URI, Provider> getProviders(Repository myRepository)
    {
        final Map<URI, Provider> results = new Hashtable<URI, Provider>();

        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getProviders: started parsing provider configurations");
        }
        final long start = System.currentTimeMillis();

        // TODO: HACK: treat all providers as HttpProviderImpl for now
        final URI providerTypeUri = ProviderImpl.getProviderTypeUri();
        
        try
        {
            final RepositoryConnection con = myRepository.getConnection();

            for(Statement nextProvider : con.getStatements(null, RDF.TYPE, providerTypeUri, true).asList())
            {
            	URI nextSubjectUri = (URI)nextProvider.getSubject();
            	results.put(nextSubjectUri, new HttpProviderImpl(
            			con.getStatements(nextSubjectUri, (URI) null, (Value) null, true).asList(), 
            			nextSubjectUri, 
            			Settings.CONFIG_API_VERSION));
            }
        }
        catch (final OpenRDFException e)
        {
            // handle exception
            Settings.log.fatal("Settings.getProviders:", e);
        }

        if(Settings._INFO)
        {
            final long end = System.currentTimeMillis();
            Settings.log.info(String.format("%s: timing=%10d",
                    "Settings.getProviders", (end - start)));
        }
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getProviders: finished parsing provider configurations");
        }
        
        return results;
    }

    public Collection<Provider> getProvidersForNamespaceUris(
            Collection<Collection<URI>> namespaceUris, URI namespaceMatchMethod)
    {
        if((namespaceUris == null) || (namespaceUris.size() == 0))
        {
            if(Settings._DEBUG)
            {
                Settings.log
                        .debug("Settings.getProvidersForNamespaceUris: namespaceUris was either null or empty");
            }
            return new HashSet<Provider>();
        }
        if(Settings._TRACE)
        {
            Settings.log
                    .trace("Settings.getProvidersForNamespaceUris: namespaceUris="
                            + namespaceUris);
        }
        final Collection<Provider> results = new HashSet<Provider>();

        for(final Provider nextProvider : this.getAllProviders().values())
        {
            boolean anyFound = false;
            boolean allFound = true;
            if(Settings._TRACE)
            {
                Settings.log
                        .trace("Settings.getProvidersForNamespaceUris: nextProvider.getKey()="
                                + nextProvider.getKey().stringValue());
            }
            
            
            for(final Collection<URI> nextNamespaceUriList : namespaceUris)
            {
                if(nextNamespaceUriList == null)
                {
                    if(Settings._DEBUG)
                    {
                        Settings.log
                                .debug("Settings.getProvidersForNamespaceUris: nextNamespaceUriList was null");
                    }
                    continue;
                }
                if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.getProvidersForNamespaceUris: nextNamespaceUriList="
                                    + nextNamespaceUriList);
                }
                boolean somethingFound = false;
                for(final URI nextNamespaceUri : nextNamespaceUriList)
                {
                    if(Settings._TRACE)
                    {
                        Settings.log
                                .trace("Settings.getProvidersForNamespaceUris: nextNamespaceUri="
                                        + nextNamespaceUri);
                    }
                    if(nextProvider.containsNamespaceUri(nextNamespaceUri))
                    {
                        somethingFound = true;
                        break;
                    }
                }
                if(somethingFound)
                {
                    anyFound = true;
                }
                else
                {
                    allFound = false;
                }
            }
            if(anyFound
                    && namespaceMatchMethod.equals(QueryTypeImpl.getNamespaceMatchAnyUri()))
            {
                results.add(nextProvider);
            }
            else if(allFound
                    && namespaceMatchMethod.equals(QueryTypeImpl.getNamespaceMatchAllUri()))
            {
                results.add(nextProvider);
            }
        }
        return results;
    }


    
    public Collection<Provider> getProvidersForQueryType(URI queryType)
    {
        final Collection<Provider> results = Settings.getProvidersForQueryTypeFromList(queryType, this.getAllProviders().values());
        
        if(Settings._DEBUG)
        {
            Settings.log.debug("Settings.getProvidersForQueryType: Found "
                    + results.size() + " providers for customService="
                    + queryType);
        }
        if(Settings._TRACE)
        {
            for(final Provider nextResult : results)
            {
                Settings.log
                        .trace("Settings.getProvidersForQueryType: nextResult="
                                + nextResult.toString());
            }
        }
        return results;
    }

    public Collection<Provider> getProvidersForQueryTypeForNamespaceUris(
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
        
        final Collection<Provider> namespaceProviders = this.getProvidersForNamespaceUris(namespaceUris, namespaceMatchMethod);
        
        if(Settings._TRACE)
        {
            Settings.log
                    .trace("Settings.getProvidersForQueryTypeForNamespaceUris: queryType="
                            + queryType
                            + " namespaceProviders="
                            + namespaceProviders);
        }
        
        final Collection<Provider> results = Settings.getProvidersForQueryTypeFromList(queryType, namespaceProviders);
        
        if(Settings._TRACE)
        {
            Settings.log
                    .trace("Settings.getProvidersForQueryTypeForNamespaceUris: queryType="
                            + queryType + " results=" + results);
        }
        return results;
    }

    @Override
	public Map<URI, QueryType> getQueryTypes(Repository myRepository)
    {
        final Map<URI, QueryType> results = new Hashtable<URI, QueryType>();
        final long start = System.currentTimeMillis();
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getQueryTypes: started parsing query types");
        }

        final URI queryTypeUri = QueryTypeImpl.getQueryTypeUri();

        try
        {
            final RepositoryConnection con = myRepository.getConnection();

            for(Statement nextQueryType : con.getStatements(null, RDF.TYPE, queryTypeUri, true).asList())
            {
            	URI nextSubjectUri = (URI)nextQueryType.getSubject();
            	results.put(nextSubjectUri, new QueryTypeImpl(
            			con.getStatements(nextSubjectUri, (URI) null, (Value) null, true).asList(), 
            			nextSubjectUri, 
            			Settings.CONFIG_API_VERSION));
            }                
        }
        catch (final OpenRDFException e)
        {
            // handle exception
            Settings.log.fatal("Settings.getQueryTypes:", e);
        }

        if(Settings._INFO)
        {
            final long end = System.currentTimeMillis();
            Settings.log.info(String.format("%s: timing=%10d",
                    "Settings.getQueryTypes", (end - start)));
        }
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getQueryTypes: finished parsing query types");
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
        log.debug("getQueryTypesMatchingQueryString: profileList.size()="+profileList.size());
        
        for(Profile nextProfile : profileList)
        {
            log.trace("getQueryTypesMatchingQueryString: nextProfile.getKey()="+nextProfile.getKey().stringValue());
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
                if(nextQuery.isUsedWithProfileList(profileList, this.getBooleanPropertyFromConfig(Constants.RECOGNISE_IMPLICIT_QUERY_INCLUSIONS, true), this.getBooleanPropertyFromConfig(Constants.INCLUDE_NON_PROFILE_MATCHED_QUERIES, true)))
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
        
    @Override
    public Map<URI, RuleTest> getRuleTests(Repository myRepository)
    {
        final Map<URI, RuleTest> results = new Hashtable<URI, RuleTest>();
        
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getRuleTests: started parsing rule test configurations");
        }
        final long start = System.currentTimeMillis();
        
        final URI ruleTestTypeUri = RuleTestImpl.getRuletestTypeUri();
        try
        {
            final RepositoryConnection con = myRepository.getConnection();

            for(Statement nextProvider : con.getStatements(null, RDF.TYPE, ruleTestTypeUri, true).asList())
            {
            	URI nextSubjectUri = (URI)nextProvider.getSubject();
            	results.put(nextSubjectUri, new RuleTestImpl(
            			con.getStatements(nextSubjectUri, (URI) null, (Value) null, true).asList(), 
            			nextSubjectUri, 
            			Settings.CONFIG_API_VERSION));
            }                
        }
        catch (final OpenRDFException e)
        {
            // handle exception
            Settings.log.fatal("Settings.getRuleTests:", e);
        }


        if(Settings._INFO)
        {
            final long end = System.currentTimeMillis();
            Settings.log.info(String.format("%s: timing=%10d",
                    "Settings.getRuleTests", (end - start)));
        }
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getRuleTests: finished getting rdf rule tests");
        }
        
        return results;
    }
        
    public List<NormalisationRule> getSortedRulesForProviders(Collection<Provider> Providers, int sortOrder)
    {
        List<NormalisationRule> results = new LinkedList<NormalisationRule>();
        
        for(Provider nextProvider : Providers)
        {
            results.addAll(getNormalisationRulesForUris(nextProvider.getNormalisationUris(), sortOrder));
        }
        
        if(sortOrder == Constants.HIGHEST_ORDER_FIRST)
        {
            Collections.sort(results, Collections.reverseOrder());
        }
        else if(sortOrder == Constants.LOWEST_ORDER_FIRST)
        {
            Collections.sort(results);
        }
        else
        {
            throw new RuntimeException(
                    "Settings.getSortedRulesForProviders: sortOrder unrecognised sortOrder="
                            + sortOrder);
        }
        
        return results;
    }
    
    public Collection<Statement> getStatementPropertiesFromConfig(String key)
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

                results.addAll(getStatementCollectionPropertiesFromConfig(configUri, propertyUri, webappConfig));
            }
        }
        catch(Exception ex)
        {
            Settings.log.error("Settings.getStatementPropertiesFromConfig: error", ex);
        }
        
        return results;
    }

    public Collection<String> getStringCollectionPropertiesFromConfig(String key)
    {
        if(_TRACE)
            log.trace("Settings.getStringCollectionPropertiesFromConfig: key="+key);

        Collection<String> results = new LinkedList<String>();
        
        Collection<Value> values = getValueCollectionPropertiesFromConfig(key);
        
        for(Value nextValue : values)
        {
            results.add(nextValue.stringValue());
            //results.add(RdfUtils.getUTF8StringValueFromSesameValue(nextValue));
        }
        
        return results;
    }

	public String getStringPropertyFromConfig(String key, String defaultValue)
    {
        String result = defaultValue;
        
        if(_TRACE)
            log.trace("Settings.getStringPropertyFromConfig: key="+key+" defaultValue="+defaultValue);

        Collection<String> values = getStringCollectionPropertiesFromConfig(key);
        
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
        return Pattern.compile(this.getStringPropertyFromConfig("tagPatternRegex", ""));
    }

    @Override
    public Map<URI, Template> getTemplates(Repository myRepository)
    {
        final Map<URI, Template> results = new Hashtable<URI, Template>();
        
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getTemplates: started parsing template configurations");
        }
        
        final long start = System.currentTimeMillis();
        
        final URI ruleTestTypeUri = TemplateImpl.getTemplateTypeUri();
        try
        {
            final RepositoryConnection con = myRepository.getConnection();

            for(Statement nextProvider : con.getStatements(null, RDF.TYPE, ruleTestTypeUri, true).asList())
            {
            	URI nextSubjectUri = (URI)nextProvider.getSubject();
            	results.put(nextSubjectUri, new TemplateImpl(
            			con.getStatements(nextSubjectUri, (URI) null, (Value) null, true).asList(), 
            			nextSubjectUri, 
            			Settings.CONFIG_API_VERSION));
            }                
        }
        catch (final OpenRDFException e)
        {
            // handle exception
            Settings.log.fatal("Settings.getTemplates:", e);
        }

		if(Settings._INFO)
		{
			final long end = System.currentTimeMillis();
			Settings.log.info(String.format("%s: timing=%10d",
		        "Settings.getTemplates", (end - start)));
		}
		
		if(Settings._DEBUG)
		{
			Settings.log.debug("Settings.getTemplates: finished parsing templates");
		}


        return results;
    }
    
    public Collection<URI> getURICollectionPropertiesFromConfig(String key)
    {
        if(_TRACE)
            log.trace("Settings.getURICollectionPropertiesFromConfig: key="+key);

        Collection<URI> results = new HashSet<URI>();
        
        for(Value nextValue : getValueCollectionPropertiesFromConfig(key))
        {
            if(nextValue instanceof URI)
            {
                results.add((URI)nextValue);
            }
            else
            {
                log.fatal("Settings.getURICollectionPropertiesFromConfig: nextValue was not an instance of URI nextValue="+nextValue);
            }
        }
        
        return results;
    }

	public Collection<Value> getValueCollectionPropertiesFromConfig(String key)
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

                results.addAll(getValueCollectionPropertiesFromConfig(configUri, propertyUri));
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
        boolean manualRefresh = this.getBooleanPropertyFromConfig("enableManualConfigurationRefresh", true);
        long timestampDiff = (System.currentTimeMillis() - this.initialisedTimestamp);
        long manualRefreshMinimum = this.getLongPropertyFromConfig("manualConfigurationMinimumMilliseconds", 60000L);
        
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
     * Runs rule tests over the stages "QueryVariables" and "BeforeResultsImport"
     * 
     * @param myRuleTests
     * @return true if all of the tests passed, otherwise it returns false
     */
    public boolean runRuleTests(Collection<RuleTest> myRuleTests)
    {
        boolean allPassed = true;
        
        for(final RuleTest nextRuleTest : myRuleTests)
        {
            final String nextTestInputString = nextRuleTest
                    .getTestInputString();
            final String nextTestOutputString = nextRuleTest
                    .getTestOutputString();
            
            String nextInputTestResult = nextTestInputString;
            
            if(nextRuleTest.getStages().contains(NormalisationRuleImpl.getRdfruleStageQueryVariables()))
            {
                for(final NormalisationRule nextRule : this.getNormalisationRulesForUris(nextRuleTest.getRuleUris(), Constants.LOWEST_ORDER_FIRST))
                {
                    nextInputTestResult = (String)nextRule.normaliseByStage(NormalisationRuleImpl.getRdfruleStageQueryVariables(), nextTestInputString);
                }
                
                if(nextInputTestResult.equals(nextTestOutputString))
                {
                    if(_DEBUG)
                    {
                        log
                                .debug("Settings.runRuleTests: TEST-PASS input test pass: nextTestInputString="
                                        + nextTestInputString
                                        + " nextInputTestResult="
                                        + nextInputTestResult);
                    }
                }
                else
                {
                    allPassed = false;
                    
                    if(_INFO)
                    {
                        log
                                .info("Settings.runRuleTests: TEST-FAIL: input test did not result in the output string: nextTestInputString="
                                        + nextTestInputString
                                        + " actual output :: nextInputTestResult="
                                        + nextInputTestResult
                                        + " expected output :: nextTestOutputString="
                                        + nextTestOutputString);
                        log
                                .info("Settings.runRuleTests: TEST-FAIL: nextRuleTest.toString()="
                                        + nextRuleTest.toString());
                    }
                }
            }
            
            if(nextRuleTest.getStages().contains(NormalisationRuleImpl.getRdfruleStageBeforeResultsImport()))
            {
                String nextOutputTestResult = nextTestInputString;
                
                for(final NormalisationRule nextRule : this.getNormalisationRulesForUris(nextRuleTest.getRuleUris(), Constants.HIGHEST_ORDER_FIRST))
                {
                    nextOutputTestResult = (String)nextRule.normaliseByStage(NormalisationRuleImpl.getRdfruleStageBeforeResultsImport(), nextTestInputString);
                    
                    if(nextOutputTestResult.equals(nextTestInputString))
                    {
                        if(_DEBUG)
                        {
                            log
                                    .debug("Settings.runRuleTests: TEST-PASS output test pass: nextTestInputString="
                                            + nextTestInputString
                                            + " actual output :: nextOutputTestResult="
                                            + nextOutputTestResult
                                            + " expected output :: nextTestOutputString="
                                            + nextTestOutputString);
                        }
                    }
                    else
                    {
                        allPassed = false;
                        
                        if(_INFO)
                        {
                            log
                                    .info("Settings.runRuleTests: TEST-FAIL: output test did not result in the input string: nextTestInputString="
                                            + nextTestInputString
                                            + " actual output :: nextOutputTestResult="
                                            + nextOutputTestResult
                                            + " expected output :: nextTestOutputString="
                                            + nextTestOutputString);
                            log
                                    .info("Settings.runRuleTests: TEST-FAIL: nextRuleTest.toString()="
                                            + nextRuleTest.toString());
                        }
                    }
                }
            } // end if(this.stages.contains(rdfruleStageBeforeResultsImport)
        } // end for(nextRuleTest
        
        return allPassed;
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

	/**
     * @param webappConfigUriList the webappConfigUriList to set
     */
    public void setWebappConfigUriListByValues(Collection<Value> webappConfigUriList)
    {
        Collection<String> tempCollection = new HashSet<String>();

        for(Value nextValue : webappConfigUriList)
        {
            tempCollection.add(nextValue.stringValue());
        }
        
        this.webappConfigUriList = tempCollection;
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

	private String getBaseConfigLocation()
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
    private String getBaseConfigMimeFormat()
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
        boolean backupNeeded = false;
        boolean backupFailed = false;
        
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
                Settings.log.error("Settings.getBaseConfigurationRdf: failed to initialise the base configuration repository. Caught java.io.IOException. nextLocation="+nextLocation, ioe);
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
            
            Collection<String> queryConfigLocationsList = this.getStringCollectionPropertiesFromConfig("queryConfigLocations");
            
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
                
                for(final String nextLocation : this.getStringCollectionPropertiesFromConfig("backupQueryConfigLocations"))
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
                        // backupFailed = true;
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

	private Collection<Statement> getStatementCollectionPropertiesFromConfig(URI subjectUri, URI propertyUri, Repository nextRepository)
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

	private Collection<Value> getValueCollectionPropertiesFromConfig(URI subjectUri, URI propertyUri)
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
                results = getValueCollectionPropertiesFromConfig(subjectUri, propertyUri, getWebAppConfigurationRdf());
            
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

	private Collection<Value> getValueCollectionPropertiesFromConfig(URI subjectUri, URI propertyUri, Repository nextRepository)
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
            
            Collection<Value> webappConfigFiles = getValueCollectionPropertiesFromConfig(
            		subjectConfigUri, webappConfigLocationsUri, nextBaseConfigurationRepository);

            Collection<Value> activeWebappConfigs = getValueCollectionPropertiesFromConfig(
            		subjectConfigUri, activeWebappConfigsUri, nextBaseConfigurationRepository);
            
            setWebappConfigUriListByValues(activeWebappConfigs);
            
            
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
		Repository myRepository = new SailRepository(new ForwardChainingRDFSInferencer(new MemoryStore()));
		
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
			if(!ProviderImpl.schemaToRdf(myRepository,
					contextUri, Settings.CONFIG_API_VERSION))
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
			if(!HttpProviderImpl.schemaToRdf(myRepository,
					contextUri, Settings.CONFIG_API_VERSION))
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
			if(!ProjectImpl.schemaToRdf(myRepository,
					contextUri, Settings.CONFIG_API_VERSION))
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
			if(!QueryTypeImpl.schemaToRdf(myRepository,
					contextUri, Settings.CONFIG_API_VERSION))
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
			if(!RegexNormalisationRuleImpl.schemaToRdf(myRepository,
					contextUri, Settings.CONFIG_API_VERSION))
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
			if(!SparqlNormalisationRuleImpl.schemaToRdf(myRepository,
					contextUri, Settings.CONFIG_API_VERSION))
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
			if(!XsltNormalisationRuleImpl.schemaToRdf(myRepository,
					contextUri, Settings.CONFIG_API_VERSION))
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
			if(!RuleTestImpl.schemaToRdf(myRepository,
					contextUri, Settings.CONFIG_API_VERSION))
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
			if(!NamespaceEntryImpl.schemaToRdf(myRepository,
					contextUri, Settings.CONFIG_API_VERSION))
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
			if(!ProfileImpl.schemaToRdf(myRepository,
					contextUri, Settings.CONFIG_API_VERSION))
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
			if(!StatisticsEntry.schemaToRdf(myRepository,
					contextUri, Settings.CONFIG_API_VERSION))
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
			if(!ProvenanceRecord.schemaToRdf(myRepository,
					contextUri, Settings.CONFIG_API_VERSION))
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
			if(!QueryBundle.schemaToRdf(myRepository,
					contextUri, Settings.CONFIG_API_VERSION))
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
}
