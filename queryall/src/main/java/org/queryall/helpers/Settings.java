package org.queryall.helpers;

import info.aduna.iteration.Iterations;

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
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryResult;
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
import org.queryall.api.Template;
import org.queryall.impl.*;

/**
 * A class used to get access to settings
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 * @version $Id: Settings.java 975 2011-02-23 00:59:00Z p_ansell $
 */
public class Settings extends QueryAllConfiguration
{
    public static final String INCLUDE_NON_PROFILE_MATCHED_QUERIES = "includeNonProfileMatchedQueries";
	public static final String RECOGNISE_IMPLICIT_QUERY_INCLUSIONS = "recogniseImplicitQueryInclusions";
	public static final Logger log = Logger
            .getLogger(Settings.class.getName());
    public static final boolean _TRACE = Settings.log.isTraceEnabled();
    public static final boolean _DEBUG = Settings.log.isDebugEnabled();
    public static final boolean _INFO = Settings.log.isInfoEnabled();

    // This matches the queryall.properties file where
    // the generally static API specific section of the configuration settings are stored
    public static final String DEFAULT_PROPERTIES_BUNDLE_NAME = "queryall";
    public static final int CONFIG_API_VERSION = 3;
    public static final String VERSION = getVersion();
    
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
    
    public static Settings getSettings()
    {
        if(defaultSettings == null)
        {
            defaultSettings = new Settings();
        }
        
        return defaultSettings;
    }
    
    /**
     * Checks for the configured version first in the system vm properties, 
     * then in the localisation properties file, by default, "queryall.properties",
     * Uses the key "queryall.Version"
     * @return The version, defaults to "0.0"
     */
    private static String getVersion()
    {
    	return getSystemOrPropertyString("queryall.Version", "0.0");
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
    
    private static String getDefaultBaseConfigMimeFormatProperty()
    {
        return getSystemOrPropertyString("queryall.BaseConfigMimeFormat", "text/rdf+n3");
    }
    
    private String getBaseConfigUri()
    {
        if(this.baseConfigUri == null)
        {
            this.baseConfigUri = Settings.getDefaultBaseConfigUriProperty();
        }
        
        return this.baseConfigUri;
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

    /**
     * @return the webappConfigUriList
     */
    public Collection<String> getWebappConfigUriList()
    {
        return this.webappConfigUriList;
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
        if(Settings._TRACE)
        {
            Settings.log.trace("Settings.configRefreshCheck: returning");
        }
        return false;
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
	public Map<URI, QueryType> getQueryTypes(Repository myRepository)
    {
        final Hashtable<URI, QueryType> results = new Hashtable<URI, QueryType>();
        final long start = System.currentTimeMillis();
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getQueryTypes: started parsing custom queries");
        }
        // TODO: make the Query suffix configurable
        final String queryOntologyTypeUri = this.getOntologyTermUriPrefix()
                + this.getNamespaceForQueryType()
                + this.getOntologyTermUriSuffix() + "Query";
        try
        {
            final RepositoryConnection con = myRepository.getConnection();
            try
            {
                final String queryString = "SELECT ?queryUri WHERE { ?queryUri a <"
                        + queryOntologyTypeUri + "> . }";
                if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.getQueryTypes: found queryString="
                                    + queryString);
                }
                
                final TupleQuery tupleQuery = con.prepareTupleQuery(
                        QueryLanguage.SPARQL, queryString);
                final TupleQueryResult queryResult = tupleQuery.evaluate();
                try
                {
                    while(queryResult.hasNext())
                    {
                        final BindingSet bindingSet = queryResult.next();
                        final Value valueOfQueryUri = bindingSet
                                .getValue("queryUri");
                        if(Settings._DEBUG)
                        {
                            Settings.log
                                    .debug("Settings.getQueryTypes: found query: valueOfQueryUri="
                                            + valueOfQueryUri);
                        }
                        final RepositoryResult<Statement> statements = con
                                .getStatements((URI) valueOfQueryUri,
                                        (URI) null, (Value) null, true);
                        final Collection<Statement> nextStatementList = Iterations.addAll(statements, new HashSet<Statement>());
                        final QueryType nextQueryConfiguration = QueryTypeImpl.fromRdf(nextStatementList, (URI)valueOfQueryUri, Settings.CONFIG_API_VERSION);
                        if(nextQueryConfiguration != null)
                        {
                            results.put((URI)valueOfQueryUri,
                                    nextQueryConfiguration);
                        }
                        else
                        {
                            Settings.log
                                    .error("Settings.getQueryTypes: was not able to create a query configuration with URI valueOfQueryUri="
                                            + valueOfQueryUri.toString());
                        }
                    }
                }
                finally
                {
                    queryResult.close();
                }
            }
            finally
            {
                con.close();
            }
        }
        catch (final OpenRDFException e)
        {
            // handle exception
            Settings.log.error("Settings.getQueryTypes:", e);
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
                    .debug("Settings.getQueryTypes: finished parsing custom queries");
        }
        
        return results;
    }
    
    public boolean addTemplate(Template nextTemplate, boolean overwritePrevious)
    {
        Template existingTemplate = getTemplate(nextTemplate.getKey());
        
        if(existingTemplate == null || overwritePrevious)
        {
            if(cachedTemplates.containsKey(nextTemplate.getKey()))
                cachedTemplates.remove(nextTemplate.getKey());
            
            cachedTemplates.put(nextTemplate.getKey(), nextTemplate);
            
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public Template getTemplate(URI nextKey)
    {
        Template result = null;
        
        Map<URI, Template> allTemplates = this.getAllTemplates();
        
        if(allTemplates.containsKey(nextKey))
        {
            result = allTemplates.get(nextKey);
        }
        
        return result;
    }
    
    public Template createNewTemplateByString(String templateKey, String templateString, String contentType)
    {
        Template result = new TemplateImpl();
        
        result.setKey(templateKey);
        result.setTemplateString(templateString);
        result.setContentType(contentType);
        
        return result;
    }
    
    // There is a one to one relationship between templates and patterns
    // Patterns must be ordered before giving them to this method
    // The pattern order does not need to be universally applicable, 
    // as templates can be explicitly chained using the referencedTemplates method for one to one correspondence if needed
    // TODO: make a non-recursive implementation to keep memory footprint low
    public void matchTemplatesToPatternsForQuery(
        List<Template> allTemplates, 
        List<Template> constantParameters, 
        StringBuilder queryString)
    {
        //StringBuilder result = new StringBuilder(queryString);
        StringBuilder result = queryString;
        
        for(Template nextTemplate : constantParameters)
        {
            Pattern nextPattern = Pattern.compile(nextTemplate.getMatchRegex());
            
            StringUtils.replaceMatchesForRegexOnString(nextPattern, nextTemplate.getMatchRegex(), result, new StringBuilder(nextTemplate.getTemplateString()));
        }
        
        for(Template nextTemplate : allTemplates)
        {
            Pattern nextPattern = Pattern.compile(nextTemplate.getMatchRegex());
            
            // avoid actually assigning a real value to this unless we need to
            String original = null;
            
            if(_DEBUG)
            {
                original = result.toString();
            }
            
            StringUtils.replaceMatchesForRegexOnString(nextPattern, nextTemplate.getMatchRegex(), result, new StringBuilder(nextTemplate.getTemplateString()));
            
            // 
            // if(nextTemplate.isNativeFunction)
            // {
                // RdfUtils.applyNativeFunctionTemplate(nextTemplate, result);
            // }
            // else
            // {
                // RdfUtils.replaceMatchesForRegexOnString(nextPattern, nextTemplate.matchRegex, result, new StringBuilder(nextTemplate.templateString));
            // }
            if(_DEBUG)
            {
                // log.debug("nextTemplate.getKey()="+nextTemplate.getKey().stringValue()+ " matches.size()="+matches.size());
                log.debug("Settings.matchTemplatesToPatternsForQuery: afterthistemplate: nextTemplate.getKey()="+nextTemplate.getKey().stringValue()+" original="+original+" result="+result.toString());
            }
            
            List<Template> nextCalledTemplates = new ArrayList<Template>(nextTemplate.getReferencedTemplates().size());
            
            for(URI nextReferencedTemplate : nextTemplate.getReferencedTemplates())
            {
                // self-referencing is not allowed to prevent common recursive errors
                if(!nextReferencedTemplate.equals(nextTemplate.getKey()))
                {
                    for(Template nextOtherTemplate : allTemplates)
                    {
                        if(nextOtherTemplate.getKey().equals(nextReferencedTemplate))
                        {
                            nextCalledTemplates.add(nextOtherTemplate);
                        }
                    }
                }
                else
                {
                    log.warn("Settings.matchTemplatesToPatternsForQuery: found self-referencedTemplate key="+nextTemplate.getKey());
                }
            }
            
            if(_DEBUG)
            {
                original = result.toString();
            }
            
            // result = matchTemplatesToPatternsForQuery(nextCalledTemplates, constantParameters, result);
            this.matchTemplatesToPatternsForQuery(nextCalledTemplates, constantParameters, result);
            
            if(_DEBUG)
            {
                log.debug("afterreferencedtemplates: nextTemplate.getKey()="+nextTemplate.getKey()+" original="+original+" result="+result.toString());
            }
        } // end for(Template nextTemplate: allTemplates)
        
        // do one replace of any parameter templates that remain
        // TODO: should we keep going until there are no more replacements made? Limits on how many times this is allowed to occur?
        for(Template nextTemplate : constantParameters)
        {
            Pattern nextPattern = Pattern.compile(nextTemplate.getMatchRegex());
            StringUtils.replaceMatchesForRegexOnString(nextPattern, nextTemplate.getMatchRegex(), result, new StringBuilder(nextTemplate.getTemplateString()));
        }
        
        // return result;
    }
    
    public Template createNewTemplateByKey(String templateKey, String contentType)
    {
        Template result = new TemplateImpl();
        
        result.setKey(templateKey);
        result.setContentType(contentType);
        
        return result;
    }
    
    @Override
	public synchronized Map<URI, Template> getAllTemplates()
    {
        if(this.cachedTemplates != null)
        {
            return this.cachedTemplates;
        }

        final Hashtable<URI, Template> results = new Hashtable<URI, Template>();
        
        final long start = System.currentTimeMillis();

        if(Settings._DEBUG)
        {
            Settings.log.debug("Settings.getAllTemplates: started parsing templates");
        }
        try
        {
            final Repository myRepository = this.getServerConfigurationRdf();
            
            final String templateOntologyTypeUri = this.getOntologyTermUriPrefix()
                    + this.getNamespaceForTemplate()
                    + this.getOntologyTermUriSuffix() + "Template";
            try
            {
                final RepositoryConnection con = myRepository.getConnection();
                try
                {
                    final String queryString = "SELECT ?templateUri WHERE { ?templateUri a <"
                            + templateOntologyTypeUri + "> . }";
                    final TupleQuery tupleQuery = con.prepareTupleQuery(
                            QueryLanguage.SPARQL, queryString);
                    final TupleQueryResult queryResult = tupleQuery.evaluate();
                    try
                    {
                        while(queryResult.hasNext())
                        {
                            final BindingSet bindingSet = queryResult.next();
                            final Value valueOfTemplateUri = bindingSet
                                    .getValue("templateUri");
                            if(Settings._DEBUG)
                            {
                                Settings.log
                                        .debug("Settings: found template: valueOfTemplateUri="
                                                + valueOfTemplateUri);
                            }
                            final RepositoryResult<Statement> statements = con
                                    .getStatements((URI) valueOfTemplateUri,
                                            (URI) null, (Value) null, true);
                            final Collection<Statement> nextStatementList = Iterations
                                    .addAll(statements, new HashSet<Statement>());
                            final Template nextTemplate = TemplateImpl
                                    .fromRdf(nextStatementList, (URI)valueOfTemplateUri, Settings.CONFIG_API_VERSION);
                            if(nextTemplate != null)
                            {
                                results.put((URI)valueOfTemplateUri,
                                        nextTemplate);
                            }
                            else
                            {
                                Settings.log.error("Settings.getAllTemplates: was not able to create a template configuration with URI valueOfTemplateUri="
                                                + valueOfTemplateUri.toString());
                            }
                        }
                    }
                    finally
                    {
                        queryResult.close();
                    }
                }
                finally
                {
                    con.close();
                }
            }
            catch (OpenRDFException e)
            {
                // handle exception
                Settings.log.error("Settings:", e);
            }
        }
        catch(java.lang.InterruptedException ie)
        {
            Settings.log.fatal("Settings: caught java.lang.InterruptedException: not throwing it.", ie);
        }
        
        if(Settings._INFO)
        {
            final long end = System.currentTimeMillis();
            Settings.log.info(String.format("%s: timing=%10d",
                    "Settings.getAllTemplates", (end - start)));
        }
        if(Settings._DEBUG)
        {
            Settings.log.debug("Settings.getAllTemplates: finished parsing templates");
        }
        
        this.cachedTemplates = results;
        
        return this.cachedTemplates;
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
	public Map<URI, NamespaceEntry> getNamespaceEntries(Repository myRepository)
    {
        final Map<URI, NamespaceEntry> results = new Hashtable<URI, NamespaceEntry>();
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getNamespaceEntries: started parsing namespace entry configurations");
        }
        final long start = System.currentTimeMillis();
        // final Repository configRepository = Settings
                // .getServerConfigurationRdf();
        final URI namespaceEntryOntologyTypeUri = new NamespaceEntryImpl().getElementType();
        try
        {
            final RepositoryConnection con = myRepository.getConnection();
            try
            {
                final String queryString = "SELECT ?namespaceEntryUri WHERE { ?namespaceEntryUri a <"
                        + namespaceEntryOntologyTypeUri.stringValue() + "> . }";
                final TupleQuery tupleQuery = con.prepareTupleQuery(
                        QueryLanguage.SPARQL, queryString);
                final TupleQueryResult queryResult = tupleQuery.evaluate();
                try
                {
                    while(queryResult.hasNext())
                    {
                        final BindingSet bindingSet = queryResult.next();
                        final Value valueOfNamespaceEntryUri = bindingSet
                                .getValue("namespaceEntryUri");
                        if(Settings._DEBUG)
                        {
                            Settings.log
                                    .debug("Settings.getNamespaceEntries: found namespace: valueOfNamespaceEntryUri="
                                            + valueOfNamespaceEntryUri);
                        }
                        final RepositoryResult<Statement> statements = con
                                .getStatements((URI) valueOfNamespaceEntryUri,
                                        (URI) null, (Value) null, true);
                        final Collection<Statement> nextStatementList = Iterations
                                .addAll(statements, new HashSet<Statement>());
                        final NamespaceEntry nextNamespaceEntryConfiguration = NamespaceEntryImpl
                                .fromRdf(nextStatementList,
                                        (URI)valueOfNamespaceEntryUri, Settings.CONFIG_API_VERSION);
                        if(nextNamespaceEntryConfiguration != null)
                        {
                            results.put((URI)valueOfNamespaceEntryUri,
                                    nextNamespaceEntryConfiguration);
                        }
                        else
                        {
                            Settings.log
                                    .error("Settings.getNamespaceEntries: was not able to create a namespace entry configuration with URI valueOfNamespaceEntryUri="
                                            + valueOfNamespaceEntryUri
                                                    .toString());
                        }
                    }
                }
                finally
                {
                    queryResult.close();
                }
            }
            finally
            {
                con.close();
            }
        }
        catch (final OpenRDFException e)
        {
            // handle exception
            Settings.log.error("Settings.getNamespaceEntries:", e);
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
	public Map<URI, Profile> getProfiles(Repository myRepository)
    {
    	final Map<URI, Profile> results = new Hashtable<URI, Profile>();
        
    	if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getProfiles: started parsing profile configurations");
        }
        final long start = System.currentTimeMillis();

        final URI profileOntologyTypeUri = new ProfileImpl().getElementType();

        try
        {
            final RepositoryConnection con = myRepository.getConnection();
            try
            {
                final String queryString = "SELECT ?profileUri WHERE { ?profileUri a <"
                        + profileOntologyTypeUri.stringValue() + "> . }";
                final TupleQuery tupleQuery = con.prepareTupleQuery(
                        QueryLanguage.SPARQL, queryString);
                final TupleQueryResult queryResult = tupleQuery.evaluate();
                try
                {
                    while(queryResult.hasNext())
                    {
                        final BindingSet bindingSet = queryResult.next();
                        final Value valueOfProfileUri = bindingSet
                                .getValue("profileUri");
                        if(Settings._DEBUG)
                        {
                            Settings.log
                                    .debug("Settings.getProfiles: found profile: valueOfProfileUri="
                                            + valueOfProfileUri);
                        }
                        final RepositoryResult<Statement> statements = con
                                .getStatements((URI) valueOfProfileUri,
                                        (URI) null, (Value) null, true);
                        final Collection<Statement> nextStatementList = Iterations
                                .addAll(statements, new HashSet<Statement>());
                        final Profile nextProfile = ProfileImpl
                                .fromRdf(nextStatementList, (URI)valueOfProfileUri, Settings.CONFIG_API_VERSION);
                        if(nextProfile != null)
                        {
                            results.put((URI)valueOfProfileUri,
                                    nextProfile);
                        }
                        else
                        {
                            Settings.log
                                    .error("Settings.getProfiles: was not able to create a profile with URI valueOfProfileUri="
                                            + valueOfProfileUri.stringValue());
                        }
                    }
                }
                finally
                {
                    queryResult.close();
                }
            }
            finally
            {
                con.close();
            }
        }
        catch (final OpenRDFException e)
        {
            // handle exception
            Settings.log.error("Settings.getProfiles:", e);
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
	public Map<URI, Provider> getProviders(Repository myRepository)
    {
        final Map<URI, Provider> results = new Hashtable<URI, Provider>();

        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getProviders: started parsing provider configurations");
        }
        final long start = System.currentTimeMillis();

        final String providerOntologyTypeUri = this.getOntologyTermUriPrefix()
                + this.getNamespaceForProvider()
                + this.getOntologyTermUriSuffix() + "Provider";
        try
        {
            final RepositoryConnection con = myRepository.getConnection();
            try
            {
                final String queryString = "SELECT ?providerUri WHERE { ?providerUri a <"
                        + providerOntologyTypeUri + "> . }";
                final TupleQuery tupleQuery = con.prepareTupleQuery(
                        QueryLanguage.SPARQL, queryString);
                final TupleQueryResult queryResult = tupleQuery.evaluate();
                try
                {
                    while(queryResult.hasNext())
                    {
                        final BindingSet bindingSet = queryResult.next();
                        final Value valueOfProviderUri = bindingSet
                                .getValue("providerUri");
                        if(Settings._DEBUG)
                        {
                            Settings.log
                                    .debug("Settings.getProviders: found provider: valueOfProviderUri="
                                            + valueOfProviderUri);
                        }
                        final RepositoryResult<Statement> statements = con
                                .getStatements((URI) valueOfProviderUri,
                                        (URI) null, (Value) null, true);
                        final Collection<Statement> nextStatementList = Iterations
                                .addAll(statements, new HashSet<Statement>());
                        final Provider nextProvider = ProviderImpl
                                .fromRdf(nextStatementList, (URI)valueOfProviderUri, Settings.CONFIG_API_VERSION);
                        if(nextProvider != null)
                        {
                            results.put((URI)valueOfProviderUri,
                                    nextProvider);
                        }
                        else
                        {
                            Settings.log
                                    .error("Settings.getProviders: was not able to create a provider configuration with URI valueOfProviderUri="
                                            + valueOfProviderUri.stringValue());
                        }
                    }
                }
                finally
                {
                    queryResult.close();
                }
            }
            finally
            {
                con.close();
            }
        }
        catch (final OpenRDFException e)
        {
            // handle exception
            Settings.log.error("Settings.getProviders:", e);
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
	public Map<URI, NormalisationRule> getNormalisationRules(Repository myRepository)
    {
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getNormalisationRules: started parsing rdf normalisation rules");
        }

        final long start = System.currentTimeMillis();

        final Hashtable<URI, NormalisationRule> results = new Hashtable<URI, NormalisationRule>();
        try
        {
            final Repository configRepository = this.getServerConfigurationRdf();
            
            // TODO: use reflection and dynamic loading of rules classes to make this process generic and static to future additions
            
            // Import Regular Expression Normalisation Rules first

            final String regexRuleTypeUri = RegexNormalisationRule.getRegexRuleTypeUri().stringValue();

            try
            {
                final RepositoryConnection con = configRepository.getConnection();

                try
                {
                    final String queryString = "SELECT ?rdfRuleUri WHERE { ?rdfRuleUri a <"
                            + regexRuleTypeUri + "> . }";
                    final TupleQuery tupleQuery = con.prepareTupleQuery(
                            QueryLanguage.SPARQL, queryString);
                    final TupleQueryResult queryResult = tupleQuery.evaluate();
                    try
                    {
                        while(queryResult.hasNext())
                        {
                            final BindingSet bindingSet = queryResult.next();
                            final Value valueOfRdfRuleUri = bindingSet
                                    .getValue("rdfRuleUri");
                            if(Settings._DEBUG)
                            {
                                Settings.log
                                        .debug("Settings.getNormalisationRules: found regex rule: valueOfRdfRuleUri="
                                                + valueOfRdfRuleUri);
                            }
                            final RepositoryResult<Statement> statements = con
                                    .getStatements((URI) valueOfRdfRuleUri,
                                            (URI) null, (Value) null, true);
                            final Collection<Statement> nextStatementList = Iterations
                                    .addAll(statements, new HashSet<Statement>());
                            final RegexNormalisationRule nextRdfRuleConfiguration = 
                                        new RegexNormalisationRule(nextStatementList, (URI)valueOfRdfRuleUri, Settings.CONFIG_API_VERSION);
                            results.put((URI)valueOfRdfRuleUri, nextRdfRuleConfiguration);
                        }
                    }
                    finally
                    {
                        queryResult.close();
                    }
                }
                finally
                {
                    con.close();
                }
            }
            catch (final OpenRDFException e)
            {
                // handle exception
                Settings.log.error("Settings.getNormalisationRules:", e);
            }

            // Then do the same thing for SPARQL Normalisation Rules
            final String sparqlruleTypeUri = SparqlNormalisationRule.getSparqlRuleTypeUri().stringValue();

            try
            {
                final RepositoryConnection con = configRepository.getConnection();

                try
                {
                    final String queryString = "SELECT ?rdfRuleUri WHERE { ?rdfRuleUri a <"
                            + sparqlruleTypeUri + "> . }";
                    final TupleQuery tupleQuery = con.prepareTupleQuery(
                            QueryLanguage.SPARQL, queryString);
                    final TupleQueryResult queryResult = tupleQuery.evaluate();
                    
                    try
                    {
                        while(queryResult.hasNext())
                        {
                            final BindingSet bindingSet = queryResult.next();
                            final Value valueOfRdfRuleUri = bindingSet
                                    .getValue("rdfRuleUri");
                            if(Settings._DEBUG)
                            {
                                Settings.log
                                        .debug("Settings.getNormalisationRules: found sparql rule: valueOfRdfRuleUri="
                                                + valueOfRdfRuleUri);
                            }
                            final RepositoryResult<Statement> statements = con
                                    .getStatements((URI) valueOfRdfRuleUri,
                                            (URI) null, (Value) null, true);
                            final Collection<Statement> nextStatementList = Iterations
                                    .addAll(statements, new HashSet<Statement>());
                            final SparqlNormalisationRule nextRdfRuleConfiguration = 
                                        new SparqlNormalisationRule(nextStatementList, (URI)valueOfRdfRuleUri, Settings.CONFIG_API_VERSION);
                            results.put((URI)valueOfRdfRuleUri,
                                    nextRdfRuleConfiguration);
                        }
                    }
                    finally
                    {
                        queryResult.close();
                    }
                }
                finally
                {
                    con.close();
                }
            }
            catch (final OpenRDFException e)
            {
                // handle exception
                Settings.log.error("Settings:", e);
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
    
    public Map<URI, RuleTest> getRuleTests(Repository myRepository)
    {
        final Map<URI, RuleTest> results = new Hashtable<URI, RuleTest>();
        
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getRuleTests: started parsing rule test configurations");
        }
        final long start = System.currentTimeMillis();
        try
        {
            final Repository configRepository = this.getServerConfigurationRdf();
            final URI ruleTestOntologyTypeUri = new RuleTestImpl().getElementType();
            try
            {
                final RepositoryConnection con = configRepository.getConnection();
                try
                {
                    final String queryString = "SELECT ?ruleTestUri WHERE { ?ruleTestUri a <"
                            + ruleTestOntologyTypeUri.stringValue() + "> . }";
                    final TupleQuery tupleQuery = con.prepareTupleQuery(
                            QueryLanguage.SPARQL, queryString);
                    final TupleQueryResult queryResult = tupleQuery.evaluate();
                    try
                    {
                        while(queryResult.hasNext())
                        {
                            final BindingSet bindingSet = queryResult.next();
                            final Value valueOfRuleTestUri = bindingSet
                                    .getValue("ruleTestUri");
                            if(Settings._DEBUG)
                            {
                                Settings.log
                                        .debug("Settings.getRuleTests: found ruletest: valueOfRuleTestUri="
                                                + valueOfRuleTestUri);
                            }
                            final RepositoryResult<Statement> statements = con
                                    .getStatements((URI) valueOfRuleTestUri,
                                            (URI) null, (Value) null, true);
                            final Collection<Statement> nextStatementList = Iterations
                                    .addAll(statements, new HashSet<Statement>());
                            final RuleTest nextRuleTestConfiguration = RuleTestImpl
                                    .fromRdf(nextStatementList, (URI)valueOfRuleTestUri, Settings.CONFIG_API_VERSION);
                            if(nextRuleTestConfiguration != null)
                            {
                                results.put((URI)valueOfRuleTestUri,
                                        nextRuleTestConfiguration);
                            }
                            else
                            {
                                Settings.log
                                        .error("Settings.getRuleTests: was not able to create a rule test configuration with URI valueOfRuleTestUri="
                                                + valueOfRuleTestUri.toString());
                            }
                        }
                    }
                    finally
                    {
                        queryResult.close();
                    }
                }
                finally
                {
                    con.close();
                }
            }
            catch (final OpenRDFException e)
            {
                // handle exception
                Settings.log.error("Settings.getRuleTests:", e);
            }
        }
        catch(java.lang.InterruptedException ie)
        {
            Settings.log.fatal("Settings.getRuleTests: caught java.lang.InterruptedException: not throwing it.", ie);
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
    
    // FIXME: This will only return anything if at least one group appears in the
    // relevant regular expression!!
    public Collection<QueryType> getQueryTypesMatchingQueryString(String queryString, List<Profile> profileList)
    {
        log.debug("this.getCustomQueriesMatchingQueryString: profileList.size()="+profileList.size());
        
        for(Profile nextProfile : profileList)
        {
            log.trace("this.getCustomQueriesMatchingQueryString: nextProfile.getKey()="+nextProfile.getKey().stringValue());
        }
        
        final Collection<QueryType> results = new HashSet<QueryType>();
        
        for(QueryType nextQuery : this.getAllQueryTypes().values())
        {
            // FIXME: allow for queries with no matching groups
            // Currently queries with no matching groups will fail this step and not be considered valid
            if(nextQuery.matchesForQueryString(queryString).size() > 0)
            {
                if(Settings._TRACE)
                {
                    Settings.log
                            .trace("this.getCustomQueriesMatchingQueryString: tentative, pre-profile-check match for"
                                    + " nextQuery.getKey()="
                                    + nextQuery.getKey().stringValue()
                                    + " queryString="
                                    + queryString);
                }
                if(nextQuery.isUsedWithProfileList(profileList, this.getBooleanPropertyFromConfig(RECOGNISE_IMPLICIT_QUERY_INCLUSIONS, true), this.getBooleanPropertyFromConfig(INCLUDE_NON_PROFILE_MATCHED_QUERIES, true)))
                {
                    if(Settings._DEBUG)
                    {
                        Settings.log
                                .debug("this.getCustomQueriesMatchingQueryString: profileList suitable for"
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
                            .trace("this.getCustomQueriesMatchingQueryString: profileList not suitable for"
                                    + " nextQuery.getKey()="
                                    + nextQuery.getKey().stringValue()
                                    + " queryString="
                                    + queryString);
                }
            }
        }
        return results;
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
    
    public NamespaceEntry getNamespaceEntryByUri(URI namespaceEntryUri)
    {
        final Map<URI, NamespaceEntry> allNamespaces = this.getAllNamespaceEntries();
        
        if(allNamespaces.containsKey(namespaceEntryUri))
        {
            return allNamespaces.get(namespaceEntryUri);
        }
        else
        {
            return null;
        }
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
        
        return Collections.unmodifiableCollection(results);
    }
    
    public List<NormalisationRule> getSortedRulesForProviders(Collection<Provider> Providers, int sortOrder)
    {
        List<NormalisationRule> results = new LinkedList<NormalisationRule>();
        
        for(Provider nextProvider : Providers)
        {
            results.addAll(getSortedRulesForProvider(nextProvider, sortOrder));
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
    
    public List<NormalisationRule> getSortedRulesForProvider(
            Provider nextProvider, int sortOrder)
    {
        if(nextProvider.getNormalisationUris().size() == 0)
        {
            return new LinkedList<NormalisationRule>();
        }
        else
        {
            return this.getNormalisationRulesForUris(
                    nextProvider.getNormalisationUris(),
                    sortOrder);
        }
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
            URI customService, Collection<Collection<URI>> namespaceUris,
            URI namespaceMatchMethod)
    {
        if(Settings._TRACE)
        {
            Settings.log
                    .trace("Settings.getProvidersForQueryTypeForNamespaceUris: customService="
                            + customService
                            + " namespaceMatchMethod="
                            + namespaceMatchMethod
                            + " namespaceUris="
                            + namespaceUris);
        }
        
        final Collection<Provider> namespaceProviders = this.getProvidersForNamespaceUris(namespaceUris, namespaceMatchMethod);
        
        if(Settings._TRACE)
        {
            Settings.log
                    .trace("Settings.getProvidersForQueryTypeForNamespaceUris: customService="
                            + customService
                            + " namespaceProviders="
                            + namespaceProviders);
        }
        
        final Collection<Provider> results = Settings.getProvidersForQueryTypeFromList(customService, namespaceProviders);
        
        if(Settings._TRACE)
        {
            Settings.log
                    .trace("Settings.getProvidersForQueryTypeForNamespaceUris: customService="
                            + customService + " results=" + results);
        }
        return results;
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
    
    public Collection<RuleTest> getRuleTestsForNormalisationRuleUri(
            URI normalisationRuleUri)
    {
        final Collection<RuleTest> results = new HashSet<RuleTest>();
        
        for(final RuleTest nextRuleTest : this.getAllRuleTests().values())
        {
            if(nextRuleTest.getRuleUris().contains(normalisationRuleUri))
            {
                results.add(nextRuleTest);
            }
        }
        return results;
    }
    
    private synchronized Repository getWebAppConfigurationRdf() throws java.lang.InterruptedException
    {
        if(_DEBUG)
            Settings.log.debug("Settings.getWebAppConfigurationRdf: constructing a new repository");

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
            tempConfigurationRepository = new SailRepository(new MemoryStore());
            tempConfigurationRepository.initialize();
            
            for(final String nextLocation : this.getStringCollectionPropertiesFromConfig("queryConfigLocations"))
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
                tempConfigurationRepository = new SailRepository(new MemoryStore());
                tempConfigurationRepository.initialize();
                
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
            result = MathsUtils.getFloatFromValue(nextValue);
        }
        
        if(_TRACE)
            log.trace("Settings.getFloatPropertyFromConfig: key="+key+" result="+result);

        return result;
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
                                .debug("RegexNormalisationRule: TEST-PASS input test pass: nextTestInputString="
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
                                .info("RegexNormalisationRule: TEST-FAIL: input test did not result in the output string: nextTestInputString="
                                        + nextTestInputString
                                        + " actual output :: nextInputTestResult="
                                        + nextInputTestResult
                                        + " expected output :: nextTestOutputString="
                                        + nextTestOutputString);
                        log
                                .info("RegexNormalisationRule: TEST-FAIL: nextRuleTest.toString()="
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
                                    .debug("RegexNormalisationRule: TEST-PASS output test pass: nextTestInputString="
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
                                    .info("RegexNormalisationRule: TEST-FAIL: output test did not result in the input string: nextTestInputString="
                                            + nextTestInputString
                                            + " actual output :: nextOutputTestResult="
                                            + nextOutputTestResult
                                            + " expected output :: nextTestOutputString="
                                            + nextTestOutputString);
                            log
                                    .info("RegexNormalisationRule: TEST-FAIL: nextRuleTest.toString()="
                                            + nextRuleTest.toString());
                        }
                    }
                }
            } // end if(this.stages.contains(rdfruleStageBeforeResultsImport)
        } // end for(nextRuleTest
        
        return allPassed;
    }

    
    public String getDefaultHostAddress()
    {
        return this.getStringPropertyFromConfig("uriPrefix", "")+this.getStringPropertyFromConfig("hostName", "")+this.getStringPropertyFromConfig("uriSuffix", "");
    }

    public Pattern getPlainNamespaceAndIdentifierPattern()
    {
        return Pattern.compile(this.getStringPropertyFromConfig("plainNamespaceAndIdentifierRegex", ""));
    }

    public Pattern getPlainNamespacePattern()
    {
        return Pattern.compile(this.getStringPropertyFromConfig("plainNamespaceRegex", ""));
    }
    
    public Pattern getTagPattern()
    {
        return Pattern.compile(this.getStringPropertyFromConfig("tagPatternRegex", ""));
    }

	/**
	 * @param ontologyTermUriPrefix the dEFAULT_ONTOLOGYTERMURI_PREFIX to set
	 */
	@Override
	public void setOntologyTermUriPrefix(String ontologyTermUriPrefix) {
		currentOntologyTermUriPrefix = ontologyTermUriPrefix;
	}

	/**
	 * @return the dEFAULT_ONTOLOGYTERMURI_PREFIX
	 */
	@Override
	public String getOntologyTermUriPrefix() {
		return currentOntologyTermUriPrefix;
	}

	/**
	 * @param current_ONTOLOGYTERMURI_SUFFIX the current_ONTOLOGYTERMURI_SUFFIX to set
	 */
	@Override
	public void setOntologyTermUriSuffix(String current_ONTOLOGYTERMURI_SUFFIX) {
		currentOntologyTermUriSuffix = current_ONTOLOGYTERMURI_SUFFIX;
	}

	/**
	 * @return the current_ONTOLOGYTERMURI_SUFFIX
	 */
	@Override
	public String getOntologyTermUriSuffix() {
		return currentOntologyTermUriSuffix;
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
	 * @return the current_RDF_WEBAPP_CONFIGURATION_NAMESPACE
	 */
	@Override
	public String getNamespaceForWebappConfiguration() {
		return currentRdfWebappConfigurationNamespace;
	}

	/**
	 * @param rdfProjectNamespace the current_RDF_PROJECT_NAMESPACE to set
	 */
	@Override
	public void setNamespaceForProject(String rdfProjectNamespace) {
		currentRdfProjectNamespace = rdfProjectNamespace;
	}

	/**
	 * @return the current_RDF_PROJECT_NAMESPACE
	 */
	@Override
	public String getNamespaceForProject() {
		return currentRdfProjectNamespace;
	}

	/**
	 * @param current_RDF_PROVIDER_NAMESPACE the current_RDF_PROVIDER_NAMESPACE to set
	 */
	@Override
	public void setNamespaceForProvider(String current_RDF_PROVIDER_NAMESPACE) {
		currentRdfProviderNamespace = current_RDF_PROVIDER_NAMESPACE;
	}

	/**
	 * @return the current_RDF_PROVIDER_NAMESPACE
	 */
	@Override
	public String getNamespaceForProvider() {
		return currentRdfProviderNamespace;
	}

	/**
	 * @param current_RDF_TEMPLATE_NAMESPACE the current_RDF_TEMPLATE_NAMESPACE to set
	 */
	@Override
	public void setNamespaceForTemplate(String current_RDF_TEMPLATE_NAMESPACE) {
		currentRdfTemplateNamespace = current_RDF_TEMPLATE_NAMESPACE;
	}

	/**
	 * @return the current_RDF_TEMPLATE_NAMESPACE
	 */
	@Override
	public String getNamespaceForTemplate() {
		return currentRdfTemplateNamespace;
	}

	/**
	 * @param current_RDF_QUERY_NAMESPACE the current_RDF_QUERY_NAMESPACE to set
	 */
	@Override
	public void setNamespaceForQueryType(String current_RDF_QUERY_NAMESPACE) {
		currentRdfQueryNamespace = current_RDF_QUERY_NAMESPACE;
	}

	/**
	 * @return the current_RDF_QUERY_NAMESPACE
	 */
	@Override
	public String getNamespaceForQueryType() {
		return currentRdfQueryNamespace;
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
	 * @return the current_RDF_QUERYBUNDLE_NAMESPACE
	 */
	@Override
	public String getNamespaceForQueryBundle() {
		return currentRdfQuerybundleNamespace;
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
	 * @return the current_RDF_RDFRULE_NAMESPACE
	 */
	@Override
	public String getNamespaceForNormalisationRule() {
		return currentRdfRuleNamespace;
	}

	/**
	 * @param current_RDF_RULETEST_NAMESPACE the current_RDF_RULETEST_NAMESPACE to set
	 */
	@Override
	public void setNamespaceForRuleTest(String current_RDF_RULETEST_NAMESPACE) {
		currentRdfRuleTestNamespace = current_RDF_RULETEST_NAMESPACE;
	}

	/**
	 * @return the current_RDF_RULETEST_NAMESPACE
	 */
	@Override
	public String getNamespaceForRuleTest() {
		return currentRdfRuleTestNamespace;
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
	 * @return the current_RDF_NAMESPACEENTRY_NAMESPACE
	 */
	@Override
	public String getNamespaceForNamespaceEntry() {
		return currentRdfNamespaceEntryNamespace;
	}

	/**
	 * @param current_RDF_PROFILE_NAMESPACE the current_RDF_PROFILE_NAMESPACE to set
	 */
	@Override
	public void setNamespaceForProfile(String current_RDF_PROFILE_NAMESPACE) {
		currentRdfProfileNamespace = current_RDF_PROFILE_NAMESPACE;
	}

	/**
	 * @return the current_RDF_PROFILE_NAMESPACE
	 */
	@Override
	public String getNamespaceForProfile() {
		return currentRdfProfileNamespace;
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
	 * @return the current_RDF_PROVENANCE_NAMESPACE
	 */
	@Override
	public String getNamespaceForProvenance() {
		return currentRdfProvenanceNamespace;
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
	 * @return the current_RDF_STATISTICS_NAMESPACE
	 */
	@Override
	public String getNamespaceForStatistics() {
		return currentRdfStatisticsNamespace;
	}

	/**
	 * @param autogeneratedQueryPrefix the aUTOGENERATED_QUERY_PREFIX to set
	 */
	public void setAutogeneratedQueryPrefix(String autogeneratedQueryPrefix) {
		currentAutogeneratedQueryPrefix = autogeneratedQueryPrefix;
	}

	/**
	 * @return the aUTOGENERATED_QUERY_PREFIX
	 */
	public String getAutogeneratedQueryPrefix() {
		return currentAutogeneratedQueryPrefix;
	}

	/**
	 * @param aUTOGENERATED_QUERY_SUFFIX the aUTOGENERATED_QUERY_SUFFIX to set
	 */
	public void setAutogeneratedQuerySuffix(String aUTOGENERATED_QUERY_SUFFIX) {
		currentAutogeneratedQuerySuffix = aUTOGENERATED_QUERY_SUFFIX;
	}

	/**
	 * @return the aUTOGENERATED_QUERY_SUFFIX
	 */
	public String getAutogeneratedQuerySuffix() {
		return currentAutogeneratedQuerySuffix;
	}

	/**
	 * @param aUTOGENERATED_PROVIDER_PREFIX the aUTOGENERATED_PROVIDER_PREFIX to set
	 */
	public void setAutogeneratedProviderPrefix(
			String aUTOGENERATED_PROVIDER_PREFIX) {
		currentAutogeneratedProviderPrefix = aUTOGENERATED_PROVIDER_PREFIX;
	}

	/**
	 * @return the aUTOGENERATED_PROVIDER_PREFIX
	 */
	public String getAutogeneratedProviderPrefix() {
		return currentAutogeneratedProviderPrefix;
	}

	/**
	 * @param aUTOGENERATED_PROVIDER_SUFFIX the aUTOGENERATED_PROVIDER_SUFFIX to set
	 */
	public void setAutogeneratedProviderSuffix(
			String aUTOGENERATED_PROVIDER_SUFFIX) {
		currentAutogeneratedProviderSuffix = aUTOGENERATED_PROVIDER_SUFFIX;
	}

	/**
	 * @return the aUTOGENERATED_PROVIDER_SUFFIX
	 */
	public String getAutogeneratedProviderSuffix() {
		return currentAutogeneratedProviderSuffix;
	}
}
