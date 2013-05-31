/**
 * 
 */
package org.queryall.utils;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
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
import org.queryall.api.utils.WebappConfig;
import org.queryall.exception.QueryAllRuntimeException;
import org.queryall.exception.SettingAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates a new Settings object using the given base config location/mime type/URI
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class SettingsFactory
{
    private static final Logger log = LoggerFactory.getLogger(SettingsFactory.class);
    private static final boolean TRACE = SettingsFactory.log.isTraceEnabled();
    private static final boolean DEBUG = SettingsFactory.log.isDebugEnabled();
    private static final boolean INFO = SettingsFactory.log.isInfoEnabled();
    
    /**
     * The current version of the RDF configuration API that we support for pulling in and
     * serialising settings.
     */
    public static final int CONFIG_API_VERSION = 5;
    
    /**
     * Generates all of the NamespaceEntry objects from the RDF found in serverConfigurationRdf and
     * inserts them into nextSettings
     * 
     * @param serverConfigurationRdf
     * @param nextSettings
     * @throws SettingAlreadyExistsException
     */
    public static void addNamespaceEntries(final Repository serverConfigurationRdf,
            final QueryAllConfiguration nextSettings) throws SettingAlreadyExistsException
    {
        final Map<URI, NamespaceEntry> results = RdfUtils.getNamespaceEntries(serverConfigurationRdf);
        
        if(SettingsFactory.INFO)
        {
            SettingsFactory.log.info("addNamespaceEntries: found " + results.size() + " namespaces");
        }
        
        for(final URI nextNamespaceEntryUri : results.keySet())
        {
            final NamespaceEntry nextNamespaceEntryConfiguration = results.get(nextNamespaceEntryUri);
            
            nextSettings.addNamespaceEntry(nextNamespaceEntryConfiguration);
        }
    }
    
    /**
     * Generates all of the NormalisationRule objects from the RDF found in serverConfigurationRdf
     * and inserts them into nextSettings
     * 
     * @param serverConfigurationRdf
     * @param nextSettings
     * @throws SettingAlreadyExistsException
     */
    public static void addNormalisationRules(final Repository serverConfigurationRdf,
            final QueryAllConfiguration nextSettings) throws SettingAlreadyExistsException
    {
        final Map<URI, NormalisationRule> results = RdfUtils.getNormalisationRules(serverConfigurationRdf);
        
        if(SettingsFactory.INFO)
        {
            SettingsFactory.log.info("addNormalisationRules: found " + results.size() + " normalisation rules");
        }
        
        for(final URI nextNormalisationRuleUri : results.keySet())
        {
            final NormalisationRule nextNormalisationRuleConfiguration = results.get(nextNormalisationRuleUri);
            
            nextSettings.addNormalisationRule(nextNormalisationRuleConfiguration);
        }
    }
    
    /**
     * Generates all of the Profile objects from the RDF found in serverConfigurationRdf and inserts
     * them into nextSettings
     * 
     * @param serverConfigurationRdf
     * @param nextSettings
     * @throws SettingAlreadyExistsException
     */
    public static void addProfiles(final Repository serverConfigurationRdf, final QueryAllConfiguration nextSettings)
        throws SettingAlreadyExistsException
    {
        final Map<URI, Profile> results = RdfUtils.getProfiles(serverConfigurationRdf);
        
        if(SettingsFactory.INFO)
        {
            SettingsFactory.log.info("addProfiles: found " + results.size() + " profiles");
        }
        
        for(final URI nextProfileUri : results.keySet())
        {
            final Profile nextProfileConfiguration = results.get(nextProfileUri);
            
            nextSettings.addProfile(nextProfileConfiguration);
        }
    }
    
    /**
     * Generates all of the Provider objects from the RDF found in serverConfigurationRdf and
     * inserts them into nextSettings
     * 
     * @param serverConfigurationRdf
     * @param nextSettings
     * @throws SettingAlreadyExistsException
     */
    public static void addProviders(final Repository serverConfigurationRdf, final QueryAllConfiguration nextSettings)
        throws SettingAlreadyExistsException
    {
        final Map<URI, Provider> results = RdfUtils.getProviders(serverConfigurationRdf);
        
        if(SettingsFactory.INFO)
        {
            SettingsFactory.log.info("addProviders: found " + results.size() + " providers");
        }
        
        for(final URI nextProviderUri : results.keySet())
        {
            final Provider nextProviderConfiguration = results.get(nextProviderUri);
            
            nextSettings.addProvider(nextProviderConfiguration);
        }
    }
    
    /**
     * Generates all of the QueryType objects from the RDF found in serverConfigurationRdf and
     * inserts them into nextSettings
     * 
     * @param serverConfigurationRdf
     * @param nextSettings
     * @throws SettingAlreadyExistsException
     */
    public static void addQueryTypes(final Repository serverConfigurationRdf, final QueryAllConfiguration nextSettings)
        throws SettingAlreadyExistsException
    {
        final Map<URI, QueryType> results = RdfUtils.getQueryTypes(serverConfigurationRdf);
        
        if(SettingsFactory.INFO)
        {
            SettingsFactory.log.info("addQueryTypes: found " + results.size() + " query types");
        }
        
        for(final URI nextQueryTypeUri : results.keySet())
        {
            final QueryType nextQueryTypeConfiguration = results.get(nextQueryTypeUri);
            
            nextSettings.addQueryType(nextQueryTypeConfiguration);
        }
    }
    
    /**
     * Generates all of the RuleTest objects from the RDF found in serverConfigurationRdf and
     * inserts them into nextSettings
     * 
     * @param serverConfigurationRdf
     * @param nextSettings
     * @throws SettingAlreadyExistsException
     */
    public static void addRuleTests(final Repository serverConfigurationRdf, final QueryAllConfiguration nextSettings)
        throws SettingAlreadyExistsException
    {
        final Map<URI, RuleTest> results = RdfUtils.getRuleTests(serverConfigurationRdf);
        
        if(SettingsFactory.INFO)
        {
            SettingsFactory.log.info("addRuleTests: found " + results.size() + " rule tests");
        }
        
        for(final URI nextRuleTestUri : results.keySet())
        {
            final RuleTest nextRuleTestConfiguration = results.get(nextRuleTestUri);
            
            nextSettings.addRuleTest(nextRuleTestConfiguration);
        }
    }
    
    public static boolean configRefreshCheck(final QueryAllConfiguration nextSettings, final boolean tryToForceRefresh)
    {
        final long currentTimestamp = System.currentTimeMillis();
        
        final boolean enablePeriodicConfigurationRefresh =
                nextSettings.getBooleanProperty(WebappConfig.ENABLE_PERIODIC_CONFIGURATION_REFRESH);
        final long periodicConfigurationMilliseconds =
                nextSettings.getLongProperty(WebappConfig.PERIODIC_CONFIGURATION_REFRESH_MILLISECONDS);
        
        if(SettingsFactory.DEBUG)
        {
            SettingsFactory.log.debug("configRefreshCheck: enablePeriodicConfigurationRefresh="
                    + enablePeriodicConfigurationRefresh);
            SettingsFactory.log.debug("configRefreshCheck: periodicConfigurationMilliseconds="
                    + periodicConfigurationMilliseconds);
        }
        
        if(tryToForceRefresh
                || (enablePeriodicConfigurationRefresh && ((currentTimestamp - nextSettings.getLastInitialised()) > periodicConfigurationMilliseconds)))
        {
            synchronized(nextSettings)
            {
                // TODO: HACK Should be retrieving the same base config location as the one which
                // created this settings object originally
                SettingsFactory.initialise(nextSettings, SettingsFactory.getDefaultBaseConfigLocationProperty(),
                        SettingsFactory.getDefaultBaseConfigMimeFormatProperty(),
                        SettingsFactory.getDefaultBaseConfigUriProperty());
                
                nextSettings.setLastInitialised(System.currentTimeMillis());
            }
            
            return true;
        }
        if(SettingsFactory.DEBUG)
        {
            SettingsFactory.log.debug("configRefreshCheck: returning false");
        }
        return false;
    }
    
    public static void extractProperties(final QueryAllConfiguration nextSettings,
            final Repository webAppConfigurationRdf, final Collection<URI> webappConfigUris)
    {
        RepositoryConnection conn = null;
        try
        {
            conn = webAppConfigurationRdf.getConnection();
            
            // final Collection<String> propertyBaseUriQueries = new ArrayList<String>();
            // HACK TODO: Generalise properties to URIs instead of substrings so that arbitrary
            // properties can be set
            // http://purl.org/queryall/webapp_configuration:
            // propertyBaseUriQueries
            // .add("SELECT ?key ?uri ?value ?predicate WHERE { ?uri ?predicate ?value . FILTER(strstarts(str(?predicate), \"http://purl.org/queryall/webapp_configuration:\")) . BIND(substr(str(?predicate), 47) AS ?key) . } ");
            
            // propertyBaseUriQueries
            // .add("SELECT ?key ?uri ?value ?predicate WHERE { ?uri ?predicate ?value . FILTER(strstarts(str(?predicate), \"http://purl.org/queryall/webapp_configuration:\")) . BIND(substr(str(?predicate), 47) AS ?key) . } ");
            
            final StringBuilder configInList = new StringBuilder(100);
            
            for(final URI nextWebappConfigUri : webappConfigUris)
            {
                if(configInList.length() > 0)
                {
                    configInList.append(", ");
                }
                
                configInList.append("<").append(nextWebappConfigUri.stringValue()).append(">");
            }
            
            final String configList = configInList.toString();
            
            for(final WebappConfig nextConfig : WebappConfig.values())
            {
                final String nextQuery =
                        "SELECT ?value WHERE { ?uri <" + nextConfig.getUri().stringValue()
                                + "> ?value . FILTER(?uri IN(" + configList + ")) . }";
                
                try
                {
                    final TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, nextQuery);
                    
                    final TupleQueryResult tupleQueryResult = tupleQuery.evaluate();
                    
                    if(SettingsFactory.DEBUG)
                    {
                        if(!tupleQueryResult.hasNext())
                        {
                            SettingsFactory.log.debug("Could not find any properties for nextQuery=" + nextQuery);
                        }
                    }
                    
                    // for each result, insert the property into the nextSettings object using the
                    // nextConfig object that this query was derived from
                    while(tupleQueryResult.hasNext())
                    {
                        final BindingSet bindingSet = tupleQueryResult.next();
                        
                        final Binding value = bindingSet.getBinding("value");
                        
                        // TODO: verify or compile a collection of the values?
                        nextSettings.setProperty(nextConfig, value.getValue());
                    }
                    
                }
                catch(final MalformedQueryException e)
                {
                    SettingsFactory.log.error("Found MalformedQueryException nextQuery=" + nextQuery, e);
                }
                catch(final QueryEvaluationException e)
                {
                    SettingsFactory.log.error("Found QueryEvaluationException nextQuery=" + nextQuery, e);
                }
            }
        }
        catch(final RepositoryException e)
        {
            SettingsFactory.log.error("Found exception while extracting properties", e);
        }
        finally
        {
            if(conn != null)
            {
                try
                {
                    conn.close();
                }
                catch(final RepositoryException e)
                {
                    SettingsFactory.log.error("Found exception while closing connection", e);
                }
            }
        }
    }
    
    /**
     * Creates a new QueryAllConfiguration instance using the default properties, derived from
     * System properties and the /queryall.properties file if it exists
     * 
     * @return A new Settings object
     */
    public static QueryAllConfiguration generateSettings()
    {
        return SettingsFactory.generateSettings(SettingsFactory.getDefaultBaseConfigLocationProperty(),
                SettingsFactory.getDefaultBaseConfigMimeFormatProperty(),
                SettingsFactory.getDefaultBaseConfigUriProperty());
    };
    
    /**
     * Wrapper for the initialise method that uses the Settings class as the QueryAllConfiguration
     * implementation
     * 
     * @param baseConfigLocation
     * @param baseConfigMimeType
     * @param baseConfigUri
     * @return
     */
    public static QueryAllConfiguration generateSettings(final String baseConfigLocation,
            final String baseConfigMimeType, final String baseConfigUri)
    {
        final QueryAllConfiguration result = new Settings();
        
        SettingsFactory.initialise(result, baseConfigLocation, baseConfigMimeType, baseConfigUri);
        
        return result;
    }
    
    public static Collection<String> getBackupConfigLocations(final Repository webAppConfigurationRdf,
            final Collection<URI> webappConfigUris) throws OpenRDFException
    {
        final Collection<String> results = new ArrayList<String>();
        
        for(final URI nextWebappConfigUri : webappConfigUris)
        {
            final URI queryConfigLocationsUri =
                    webAppConfigurationRdf.getValueFactory().createURI(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(),
                            "backupQueryConfigLocations");
            
            final Collection<Value> backupQueryConfigLocationValues =
                    RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject(webAppConfigurationRdf,
                            queryConfigLocationsUri, nextWebappConfigUri);
            
            for(final Value nextValue : backupQueryConfigLocationValues)
            {
                // BNode string values are useless, ignore them silently here
                if(nextValue instanceof URI || nextValue instanceof Literal)
                {
                    results.add(nextValue.stringValue());
                }
            }
        }
        
        return results;
    }
    
    private static Repository getBaseConfigurationRdf(final String baseConfigLocation, final String baseConfigMimeType,
            final String baseConfigUri) throws java.lang.InterruptedException
    {
        if(SettingsFactory.TRACE)
        {
            SettingsFactory.log.trace("getBaseConfigurationRdf: entering method");
        }
        
        if(SettingsFactory.DEBUG)
        {
            SettingsFactory.log.debug("getBaseConfigurationRdf: constructing a new repository");
        }
        
        final long start = System.currentTimeMillis();
        final String configMIMEFormat = baseConfigMimeType;
        final String baseURI = baseConfigUri;
        Repository currentBaseConfigurationRepository = null;
        
        try
        {
            currentBaseConfigurationRepository = new SailRepository(new MemoryStore());
            currentBaseConfigurationRepository.initialize();
            
            if(SettingsFactory.DEBUG)
            {
                SettingsFactory.log.debug("getBaseConfigurationRdf: temp repository initialised");
            }
            
            // log.error("getBaseConfigurationRdf: WEBAPP_CONFIG_LOCATION_LIST.size()="+WEBAPP_CONFIG_LOCATION_LIST);
            
            final RepositoryConnection myRepositoryConnection = currentBaseConfigurationRepository.getConnection();
            
            final String nextLocation = baseConfigLocation;
            final InputStream nextInputStream = SettingsFactory.class.getResourceAsStream(nextLocation);
            
            if(nextInputStream == null)
            {
                throw new QueryAllRuntimeException("Was not able to find base config location nextLocation="
                        + nextLocation);
            }
            
            try
            {
                if(SettingsFactory.INFO)
                {
                    SettingsFactory.log.info("getBaseConfigurationRdf: getting configuration from file: nextLocation="
                            + nextLocation + " nextInputStream=" + nextInputStream);
                }
                
                myRepositoryConnection.add(nextInputStream, baseURI, RDFFormat.forMIMEType(configMIMEFormat));
                if(SettingsFactory.INFO)
                {
                    SettingsFactory.log
                            .info("getBaseConfigurationRdf: finished getting configuration from file: nextLocation="
                                    + nextLocation);
                }
            }
            catch(final RDFParseException rdfpe)
            {
                SettingsFactory.log.error(
                        "getBaseConfigurationRdf: failed to get the configuration repository. Caught RDFParseException. nextLocation="
                                + nextLocation, rdfpe);
                throw new RuntimeException(
                        "getBaseConfigurationRdf: failed to initialise the configuration repository. Caught RDFParseException. nextLocation="
                                + nextLocation);
            }
            catch(final OpenRDFException ordfe)
            {
                SettingsFactory.log
                        .error("getBaseConfigurationRdf: failed to initialise the base configuration repository. Caught OpenRDFException. nextLocation="
                                + nextLocation, ordfe);
                throw new RuntimeException(
                        "getBaseConfigurationRdf: failed to initialise the base configuration repository. Caught OpenRDFException. nextLocation="
                                + nextLocation);
            }
            catch(final java.io.IOException ioe)
            {
                SettingsFactory.log
                        .error("getBaseConfigurationRdf: failed to initialise the base configuration repository. Caught java.io.IOException. nextLocation="
                                + nextLocation, ioe);
                throw new RuntimeException(
                        "getBaseConfigurationRdf: failed to initialise the base configuration repository. Caught java.io.IOException. nextLocation="
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
            SettingsFactory.log
                    .error("getBaseConfigurationRdf: failed to initialise the webapp configuration repository. Caught OpenRDFException",
                            ordfe);
            throw new RuntimeException(
                    "getBaseConfigurationRdf: failed to initialise the webapp configuration repository. Caught OpenRDFException");
        }
        
        if(SettingsFactory.INFO)
        {
            final long end = System.currentTimeMillis();
            SettingsFactory.log.info(String.format("%s: timing=%10d", "getBaseConfigurationRdf", (end - start)));
            
        }
        
        if(SettingsFactory.DEBUG)
        {
            SettingsFactory.log.debug("getBaseConfigurationRdf: finished parsing configuration files");
        }
        
        if(SettingsFactory.INFO)
        {
            try
            {
                SettingsFactory.log.info("getBaseConfigurationRdf: found "
                        + currentBaseConfigurationRepository.getConnection().size()
                        + " statements in base configuration");
            }
            catch(final RepositoryException rex)
            {
                SettingsFactory.log
                        .error("getBaseConfigurationRdf: could not determine the number of statements in webapp configuration");
            }
        }
        
        if(SettingsFactory.TRACE)
        {
            try
            {
                for(final Statement nextStatement : RdfUtils
                        .getAllStatementsFromRepository(currentBaseConfigurationRepository))
                {
                    SettingsFactory.log.trace(nextStatement.toString());
                }
            }
            catch(final Exception ex)
            {
                SettingsFactory.log.error("Could not dump statements", ex);
            }
        }
        
        return currentBaseConfigurationRepository;
    }
    
    public static Collection<String> getConfigLocations(final Repository webAppConfigurationRdf,
            final Collection<URI> webappConfigUris) throws OpenRDFException
    {
        final Collection<String> results = new ArrayList<String>();
        
        final URI queryConfigLocationsUri =
                webAppConfigurationRdf.getValueFactory().createURI(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(),
                        "queryConfigLocations");
        
        for(final URI nextWebappConfigUri : webappConfigUris)
        {
            final Collection<Value> queryConfigLocationValues =
                    RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject(webAppConfigurationRdf,
                            queryConfigLocationsUri, nextWebappConfigUri);
            
            for(final Value nextValue : queryConfigLocationValues)
            {
                // BNode string values are useless, ignore them silently here
                if(nextValue instanceof URI || nextValue instanceof Literal)
                {
                    results.add(nextValue.stringValue());
                }
            }
        }
        
        return results;
    }
    
    /**
     * Checks for the base config location first in the system vm properties, then in the
     * localisation properties file, by default, "queryall.properties", Uses the key
     * "queryall.BaseConfigLocation"
     * 
     * @return The location of the base configuration file, defaults to "/queryallBaseConfig.n3"
     */
    public static String getDefaultBaseConfigLocationProperty()
    {
        return PropertyUtils.getSystemOrPropertyString("queryall.BaseConfigLocation", "/queryallBaseConfig.n3");
    }
    
    /**
     * Uses the key "queryall.BaseConfigMimeFormat" in the properties file or system properties
     * 
     * @return The MIME format of the base configuration file, defaults to "text/rdf+n3"
     */
    public static String getDefaultBaseConfigMimeFormatProperty()
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
    public static String getDefaultBaseConfigUriProperty()
    {
        return PropertyUtils.getSystemOrPropertyString("queryall.BaseConfigUri",
                "http://purl.org/queryall/webapp_configuration:theBaseConfig");
    }
    
    private static Repository getServerConfigurationRdf(final Collection<String> queryConfigLocations,
            final Collection<String> backupQueryConfigLocations) throws java.lang.InterruptedException
    {
        final long start = System.currentTimeMillis();
        // TODO: replace me with systematic method
        final String configMIMEFormat = "text/rdf+n3";
        // TODO: replace me with modifiable property
        final String baseURI = "http://example.org/queryall/base/uri/";
        Repository currentConfigurationRepository = null;
        boolean backupNeeded = false;
        final boolean backupFailed = false;
        
        try
        {
            // start off with the schemas in the repository
            currentConfigurationRepository = new SailRepository(new MemoryStore());
            currentConfigurationRepository.initialize();
            
            // Initialise the repository with the schemas so that we can perform inferencing if
            // necessary
            // TODO: reenable inferencing on this information to infer all types of objects based on
            // their properties and/or rdf:type definitions and rdfs:subclass etc.
            currentConfigurationRepository =
                    Schema.getSchemas(currentConfigurationRepository, SettingsFactory.CONFIG_API_VERSION);
            
            final Collection<String> queryConfigLocationsList = queryConfigLocations;
            
            if(queryConfigLocationsList == null)
            {
                SettingsFactory.log.error("queryConfigLocationsList was null");
                throw new RuntimeException("Configuration locations were not discovered, failing fast.");
            }
            
            for(final String nextLocation : queryConfigLocationsList)
            {
                // TODO: negotiate between local and non-local addresses better
                // than this
                final RepositoryConnection myRepositoryConnection = currentConfigurationRepository.getConnection();
                try
                {
                    if(nextLocation.startsWith("http://") || nextLocation.startsWith("https://"))
                    {
                        // final URL url = new
                        // URL("http://quebec.bio2rdf.org/n3/provider:mirroredgeneid");
                        final URL url = new URL(nextLocation);
                        
                        if(SettingsFactory.INFO)
                        {
                            SettingsFactory.log.info("Getting configuration from URL: nextLocation=" + nextLocation
                                    + " url=" + url.toString());
                        }
                        
                        myRepositoryConnection.add(url, baseURI, RDFFormat.forMIMEType(configMIMEFormat));
                        
                        if(SettingsFactory.INFO)
                        {
                            SettingsFactory.log.info("Finished getting configuration from URL: url=" + url.toString());
                        }
                    }
                    else
                    {
                        if(SettingsFactory.INFO)
                        {
                            SettingsFactory.log.info("Getting configuration from file: nextLocation=" + nextLocation);
                        }
                        final InputStream nextInputStream = SettingsFactory.class.getResourceAsStream(nextLocation);
                        
                        if(nextInputStream == null)
                        {
                            SettingsFactory.log
                                    .error("Could not get resource as inputstream. The file may not be in the classpath. nextLocation={}",
                                            nextLocation);
                        }
                        else
                        {
                            myRepositoryConnection.add(nextInputStream, baseURI,
                                    RDFFormat.forMIMEType(configMIMEFormat));
                            if(SettingsFactory.INFO)
                            {
                                SettingsFactory.log.info("Finished getting configuration from file: nextLocation="
                                        + nextLocation);
                            }
                        }
                    }
                }
                catch(final RDFParseException rdfpe)
                {
                    SettingsFactory.log.error(
                            "failed to get the configuration repository. Caught RDFParseException. nextLocation="
                                    + nextLocation, rdfpe);
                    throw new RuntimeException(
                            "failed to initialise the configuration repository. Caught RDFParseException. nextLocation="
                                    + nextLocation);
                }
                catch(final OpenRDFException ordfe)
                {
                    SettingsFactory.log.error(
                            "failed to initialise the configuration repository. Caught OpenRDFException. nextLocation="
                                    + nextLocation, ordfe);
                    throw new RuntimeException(
                            "failed to initialise the configuration repository. Caught OpenRDFException. nextLocation="
                                    + nextLocation);
                }
                catch(final java.io.IOException ioe)
                {
                    SettingsFactory.log.error(
                            "failed to initialise the configuration repository. Caught java.io.IOException. nextLocation="
                                    + nextLocation, ioe);
                    // throw new
                    // RuntimeException("failed to initialise the configuration repository. Caught java.io.IOException");
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
            SettingsFactory.log.error("failed to initialise the configuration repository. Caught OpenRDFException",
                    ordfe);
            throw new RuntimeException("failed to initialise the configuration repository. Caught OpenRDFException");
        }
        
        if(backupNeeded)
        {
            // Try again with the backup configuration list...
            try
            {
                currentConfigurationRepository = new SailRepository(new MemoryStore());
                currentConfigurationRepository.initialize();
                
                currentConfigurationRepository =
                        Schema.getSchemas(currentConfigurationRepository, SettingsFactory.CONFIG_API_VERSION);
                
                for(final String nextLocation : backupQueryConfigLocations)
                {
                    // TODO: negotiate between local and non-local addresses better than
                    // this
                    final RepositoryConnection myRepositoryConnection = currentConfigurationRepository.getConnection();
                    try
                    {
                        if(nextLocation.startsWith("http://") || nextLocation.startsWith("https://"))
                        {
                            // final URL url = new
                            // URL("http://quebec.bio2rdf.org/n3/provider:mirroredgeneid");
                            final URL url = new URL(nextLocation);
                            
                            if(SettingsFactory.INFO)
                            {
                                SettingsFactory.log.info("getting backup configuration from URL: nextLocation="
                                        + nextLocation + " url=" + url.toString());
                            }
                            
                            myRepositoryConnection.add(url, baseURI, RDFFormat.forMIMEType(configMIMEFormat));
                            
                            if(SettingsFactory.INFO)
                            {
                                SettingsFactory.log.info("finished getting backup configuration from URL: url="
                                        + url.toString());
                            }
                        }
                        else
                        {
                            if(SettingsFactory.INFO)
                            {
                                SettingsFactory.log.info("getting backup configuration from file: nextLocation="
                                        + nextLocation);
                            }
                            final InputStream nextInputStream = SettingsFactory.class.getResourceAsStream(nextLocation);
                            
                            if(nextInputStream == null)
                            {
                                SettingsFactory.log
                                        .error("Could not get resource as inputstream. The file may not be in the classpath. nextLocation={}",
                                                nextLocation);
                            }
                            else
                            {
                                
                                myRepositoryConnection.add(nextInputStream, baseURI,
                                        RDFFormat.forMIMEType(configMIMEFormat));
                                if(SettingsFactory.INFO)
                                {
                                    SettingsFactory.log
                                            .info("finished getting backup configuration from file: nextLocation="
                                                    + nextLocation);
                                }
                            }
                        }
                    }
                    catch(final RDFParseException rdfpe)
                    {
                        SettingsFactory.log.error(
                                "failed to get the backup configuration repository. Caught RDFParseException", rdfpe);
                        throw new RuntimeException(
                                "failed to initialise the backup configuration repository. Caught RDFParseException");
                    }
                    catch(final OpenRDFException ordfe)
                    {
                        SettingsFactory.log.error(
                                "failed to initialise the backup configuration repository. Caught OpenRDFException",
                                ordfe);
                        throw new RuntimeException(
                                "failed to initialise the backup configuration repository. Caught OpenRDFException");
                    }
                    catch(final java.io.IOException ioe)
                    {
                        SettingsFactory.log.error(
                                "failed to initialise the backup configuration repository. Caught java.io.IOException",
                                ioe);
                        throw new RuntimeException(
                                "failed to initialise the backup configuration repository. Caught java.io.IOException");
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
                SettingsFactory.log.error(
                        "failed to initialise the backup configuration repository. Caught OpenRDFException", ordfe);
                throw new RuntimeException(
                        "failed to initialise the backup configuration repository. Caught OpenRDFException");
            }
        } // end if(backupNeeded)
        
        if(SettingsFactory.INFO)
        {
            final long end = System.currentTimeMillis();
            SettingsFactory.log.info(String.format("%s: timing=%10d", "getServerConfigurationRdf", (end - start)));
            
        }
        if(SettingsFactory.DEBUG)
        {
            SettingsFactory.log.debug("getServerConfigurationRdf: finished parsing configuration files");
        }
        
        if(SettingsFactory.INFO)
        {
            try
            {
                SettingsFactory.log.info("found " + currentConfigurationRepository.getConnection().size()
                        + " statements in model configuration");
            }
            catch(final RepositoryException rex)
            {
                SettingsFactory.log.error("could not determine the number of statements in configuration");
            }
        }
        
        return currentConfigurationRepository;
    }
    
    public static Collection<Value> getWebappConfigLocations(final Repository baseConfigurationRdf,
            final URI baseConfigUri) throws OpenRDFException
    {
        final Collection<Value> results = new ArrayList<Value>();
        
        final URI queryConfigLocationsUri =
                baseConfigurationRdf.getValueFactory().createURI(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(),
                        "webappConfigLocations");
        
        final Collection<Value> backupQueryConfigLocationValues =
                RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject(baseConfigurationRdf,
                        queryConfigLocationsUri, baseConfigUri);
        
        for(final Value nextValue : backupQueryConfigLocationValues)
        {
            // BNode string values are useless, ignore them silently here
            if(nextValue instanceof URI || nextValue instanceof Literal)
            {
                results.add(nextValue);
            }
        }
        
        return results;
    }
    
    private static Repository getWebAppConfigurationRdf(final Repository baseConfigurationRepository,
            final String baseConfigLocation, final String baseConfigMimeType, final String baseConfigUri,
            final Collection<Value> webappConfigFiles, final Collection<URI> activeWebappConfigs)
        throws java.lang.InterruptedException
    {
        if(SettingsFactory.DEBUG)
        {
            if(SettingsFactory.TRACE)
            {
                SettingsFactory.log.trace("getWebAppConfigurationRdf: entering");
            }
            
            SettingsFactory.log.debug("getWebAppConfigurationRdf: constructing a new repository");
        }
        
        final long start = System.currentTimeMillis();
        // final Repository nextBaseConfigurationRepository = baseConfigurationRepository;
        // SettingsFactory.getBaseConfigurationRdf(baseConfigLocation, baseConfigMimeType,
        // baseConfigUri);
        final String configMIMEFormat = baseConfigMimeType;
        // final String baseURI = baseConfigUri;
        Repository tempConfigurationRepository = null;
        Repository currentWebAppConfigurationRepository = null;
        boolean backupNeeded = false;
        final boolean backupFailed = false;
        
        RepositoryConnection finalRepositoryConnection = null;
        
        try
        {
            currentWebAppConfigurationRepository = new SailRepository(new MemoryStore());
            currentWebAppConfigurationRepository.initialize();
            
            finalRepositoryConnection = currentWebAppConfigurationRepository.getConnection();
            
            if(SettingsFactory.DEBUG)
            {
                SettingsFactory.log.debug("getWebAppConfigurationRdf: temp repository initialised");
            }
            
            // log.error("getWebAppConfigurationRdf: Settings.WEBAPP_CONFIG_LOCATION_LIST.size()="+Settings.WEBAPP_CONFIG_LOCATION_LIST);
            
            // final ValueFactory f = currentWebAppConfigurationRepository.getValueFactory();
            
            // final URI subjectConfigUri = f.createURI(baseURI);
            
            if(SettingsFactory.DEBUG)
            {
                SettingsFactory.log.debug("webappConfigFiles.size()=" + webappConfigFiles.size());
                SettingsFactory.log.debug("activeWebappConfigs.size()=" + activeWebappConfigs.size());
            }
            
            // for(final String nextLocation : BASE_CONFIG_FILES.split(","))
            for(final Value nextConfigFile : webappConfigFiles)
            {
                tempConfigurationRepository = new SailRepository(new MemoryStore());
                tempConfigurationRepository.initialize();
                
                final String nextLocation = nextConfigFile.stringValue();
                
                // TODO: negotiate between local and non-local addresses better
                // than this
                
                final RepositoryConnection myRepositoryConnection = tempConfigurationRepository.getConnection();
                
                try
                {
                    if(nextConfigFile instanceof URI)
                    {
                        // final URL url = new
                        // URL("http://quebec.bio2rdf.org/n3/provider:mirroredgeneid");
                        final URL url = new URL(nextLocation);
                        
                        if(SettingsFactory.INFO)
                        {
                            SettingsFactory.log
                                    .info("getWebAppConfigurationRdf: getting configuration from URL: nextLocation="
                                            + nextLocation + " url=" + url.toString()
                                            + " myRepositoryConnection.size()=" + myRepositoryConnection.size());
                        }
                        
                        myRepositoryConnection.add(url, nextConfigFile.stringValue(),
                                RDFFormat.forMIMEType(configMIMEFormat));
                        
                        if(SettingsFactory.INFO)
                        {
                            SettingsFactory.log
                                    .info("getWebAppConfigurationRdf: finished getting configuration from URL: url="
                                            + url.toString() + " myRepositoryConnection.size()="
                                            + myRepositoryConnection.size());
                        }
                    }
                    else
                    {
                        if(SettingsFactory.INFO)
                        {
                            SettingsFactory.log
                                    .info("getWebAppConfigurationRdf: getting configuration from file: nextLocation="
                                            + nextLocation);
                        }
                        final InputStream nextInputStream = SettingsFactory.class.getResourceAsStream(nextLocation);
                        
                        if(nextInputStream != null)
                        {
                            myRepositoryConnection.add(nextInputStream, nextConfigFile.stringValue(),
                                    RDFFormat.forMIMEType(configMIMEFormat));
                            if(SettingsFactory.INFO)
                            {
                                SettingsFactory.log
                                        .info("getWebAppConfigurationRdf: finished getting configuration from file: nextLocation="
                                                + nextLocation);
                            }
                        }
                        else
                        {
                            SettingsFactory.log
                                    .error("Could not resolve config location to an input stream nextLocation="
                                            + nextLocation);
                        }
                    }
                    
                    for(final URI nextValue : activeWebappConfigs)
                    {
                        SettingsFactory.log
                                .debug("getWebAppConfigurationRdf: started adding statements to finalrepository for nextValue="
                                        + nextValue.stringValue()
                                        + " finalRepositoryConnection.size()="
                                        + finalRepositoryConnection.size());
                        // webappConfigUriList.add(nextValue.stringValue());
                        finalRepositoryConnection.add(myRepositoryConnection.getStatements(nextValue, (URI)null,
                                (Resource)null, true));
                        SettingsFactory.log
                                .debug("getWebAppConfigurationRdf: finished adding statements to finalrepository for nextValue="
                                        + nextValue.stringValue()
                                        + " finalRepositoryConnection.size()="
                                        + finalRepositoryConnection.size());
                    }
                }
                catch(final RDFParseException rdfpe)
                {
                    SettingsFactory.log.error(
                            "getWebAppConfigurationRdf: failed to get the webapp configuration repository. Caught RDFParseException. nextLocation="
                                    + nextLocation, rdfpe);
                    throw new RuntimeException(
                            "getWebAppConfigurationRdf: failed to initialise the webapp configuration repository. Caught RDFParseException. nextLocation="
                                    + nextLocation);
                }
                catch(final OpenRDFException ordfe)
                {
                    SettingsFactory.log
                            .error("getWebAppConfigurationRdf: failed to initialise the webapp configuration repository. Caught OpenRDFException. nextLocation="
                                    + nextLocation, ordfe);
                    throw new RuntimeException(
                            "getWebAppConfigurationRdf: failed to initialise the webapp configuration repository. Caught OpenRDFException. nextLocation="
                                    + nextLocation);
                }
                catch(final java.io.IOException ioe)
                {
                    SettingsFactory.log
                            .error("getWebAppConfigurationRdf: failed to initialise the webapp configuration repository. Caught java.io.IOException. nextLocation="
                                    + nextLocation, ioe);
                    // throw new
                    // RuntimeException(": failed to initialise the configuration repository. Caught java.io.IOException");
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
            SettingsFactory.log
                    .error("getWebAppConfigurationRdf: failed to initialise the webapp configuration repository. Caught OpenRDFException",
                            ordfe);
            throw new RuntimeException(
                    "getWebAppConfigurationRdf: failed to initialise the webapp configuration repository. Caught OpenRDFException");
        }
        finally
        {
            if(currentWebAppConfigurationRepository != null)
            {
                try
                {
                    finalRepositoryConnection.close();
                }
                catch(final Exception ex)
                {
                    SettingsFactory.log.error(ex.getMessage());
                }
            }
        }
        
        if(SettingsFactory.INFO)
        {
            final long end = System.currentTimeMillis();
            SettingsFactory.log.info(String.format("%s: timing=%10d", "getWebAppConfigurationRdf", (end - start)));
            
        }
        
        if(SettingsFactory.DEBUG)
        {
            SettingsFactory.log.debug("getWebAppConfigurationRdf: finished parsing configuration files");
        }
        
        if(SettingsFactory.INFO)
        {
            try
            {
                SettingsFactory.log.info("getWebAppConfigurationRdf: found "
                        + currentWebAppConfigurationRepository.getConnection().size()
                        + " statements in webapp configuration");
            }
            catch(final RepositoryException rex)
            {
                SettingsFactory.log
                        .error("getWebAppConfigurationRdf: could not determine the number of statements in webapp configuration");
            }
        }
        
        return currentWebAppConfigurationRepository;
    }
    
    public static Collection<URI> getWebappConfigUris(final Repository baseConfigurationRdf, final URI baseConfigUri)
        throws OpenRDFException
    {
        final Collection<URI> results = new ArrayList<URI>();
        
        final URI queryConfigLocationsUri =
                baseConfigurationRdf.getValueFactory().createURI(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(),
                        "activeWebappConfigs");
        
        final Collection<Value> queryConfigLocationValues =
                RdfUtils.getValuesFromRepositoryByPredicateUrisAndSubject(baseConfigurationRdf,
                        queryConfigLocationsUri, baseConfigUri);
        
        for(final Value nextValue : queryConfigLocationValues)
        {
            // BNode string values are useless, ignore them silently here
            if(nextValue instanceof URI)
            {
                results.add((URI)nextValue);
            }
        }
        
        return results;
    }
    
    public static void initialise(final QueryAllConfiguration nextSettings, final String baseConfigLocation,
            final String baseConfigMimeType, final String baseConfigUri)
    {
        Repository baseConfigurationRdf;
        Repository webAppConfigurationRdf;
        Repository serverConfigurationRdf;
        try
        {
            baseConfigurationRdf =
                    SettingsFactory.getBaseConfigurationRdf(baseConfigLocation, baseConfigMimeType, baseConfigUri);
            
            final URI realBaseConfigUri = baseConfigurationRdf.getValueFactory().createURI(baseConfigUri);
            final Collection<Value> webappConfigLocations =
                    SettingsFactory.getWebappConfigLocations(baseConfigurationRdf, realBaseConfigUri);
            
            final Collection<URI> webappConfigUris =
                    SettingsFactory.getWebappConfigUris(baseConfigurationRdf, realBaseConfigUri);
            
            webAppConfigurationRdf =
                    SettingsFactory.getWebAppConfigurationRdf(baseConfigurationRdf, baseConfigLocation,
                            baseConfigMimeType, baseConfigUri, webappConfigLocations, webappConfigUris);
            
            final Collection<String> configLocations =
                    SettingsFactory.getConfigLocations(webAppConfigurationRdf, webappConfigUris);
            final Collection<String> backupConfigLocations =
                    SettingsFactory.getBackupConfigLocations(webAppConfigurationRdf, webappConfigUris);
            
            serverConfigurationRdf = SettingsFactory.getServerConfigurationRdf(configLocations, backupConfigLocations);
            
            if(SettingsFactory.INFO)
            {
                SettingsFactory.log.info("About to reset properties on nextSettings");
            }
            
            nextSettings.resetProperties();
            SettingsFactory.extractProperties(nextSettings, webAppConfigurationRdf, webappConfigUris);
            
            if(SettingsFactory.INFO)
            {
                SettingsFactory.log.info("About to reset namespace entries on nextSettings");
            }
            
            nextSettings.resetNamespaceEntries();
            try
            {
                SettingsFactory.addNamespaceEntries(serverConfigurationRdf, nextSettings);
            }
            catch(final SettingAlreadyExistsException saee)
            {
                SettingsFactory.log.error("Duplicate namespace entry", saee);
            }
            
            if(SettingsFactory.INFO)
            {
                SettingsFactory.log.info("About to reset query types on nextSettings");
            }
            
            nextSettings.resetQueryTypes();
            try
            {
                SettingsFactory.addQueryTypes(serverConfigurationRdf, nextSettings);
            }
            catch(final SettingAlreadyExistsException saee)
            {
                SettingsFactory.log.error("Duplicate query type", saee);
            }
            
            if(SettingsFactory.INFO)
            {
                SettingsFactory.log.info("About to reset providers on nextSettings");
            }
            
            nextSettings.resetProviders();
            try
            {
                SettingsFactory.addProviders(serverConfigurationRdf, nextSettings);
            }
            catch(final SettingAlreadyExistsException saee)
            {
                SettingsFactory.log.error("Duplicate provider", saee);
            }
            
            if(SettingsFactory.INFO)
            {
                SettingsFactory.log.info("About to reset normalisation rules on nextSettings");
            }
            
            nextSettings.resetNormalisationRules();
            try
            {
                SettingsFactory.addNormalisationRules(serverConfigurationRdf, nextSettings);
            }
            catch(final SettingAlreadyExistsException saee)
            {
                SettingsFactory.log.error("Duplicate normalisation rule", saee);
            }
            
            if(SettingsFactory.INFO)
            {
                SettingsFactory.log.info("About to reset profiles on nextSettings");
            }
            
            nextSettings.resetProfiles();
            try
            {
                SettingsFactory.addProfiles(serverConfigurationRdf, nextSettings);
            }
            catch(final SettingAlreadyExistsException saee)
            {
                SettingsFactory.log.error("Duplicate profile", saee);
            }
            
            if(SettingsFactory.INFO)
            {
                SettingsFactory.log.info("About to reset rule tests on nextSettings");
            }
            
            nextSettings.resetRuleTests();
            try
            {
                SettingsFactory.addRuleTests(serverConfigurationRdf, nextSettings);
            }
            catch(final SettingAlreadyExistsException saee)
            {
                SettingsFactory.log.error("Duplicate rule test", saee);
            }
        }
        catch(final InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        catch(final OpenRDFException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 
     */
    private SettingsFactory()
    {
        
    }
    
    public Collection<Statement> getStatementProperties(final String key, final Collection<URI> webappConfigUris,
            final Repository webappConfig)
    {
        if(SettingsFactory.TRACE)
        {
            SettingsFactory.log.trace("getStatementPropertiesFromConfig: key=" + key);
        }
        
        final Collection<Statement> results = new HashSet<Statement>();
        
        try
        {
            final ValueFactory f = webappConfig.getValueFactory();
            
            // TODO: in future should reform this to accept a full URI as the
            // key so properties outside of the queryall vocabulary can be used
            // for properties
            final URI propertyUri = f.createURI(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), key);
            
            for(final URI nextConfigUri : webappConfigUris)
            {
                results.addAll(this.getStatementProperties(nextConfigUri, propertyUri, webappConfig));
            }
        }
        catch(final Exception ex)
        {
            SettingsFactory.log.error("getStatementPropertiesFromConfig: error", ex);
        }
        
        return results;
    }
    
    private Collection<Statement> getStatementProperties(final URI subjectUri, final URI propertyUri,
            final Repository nextRepository)
    {
        if(SettingsFactory.TRACE)
        {
            SettingsFactory.log.trace("getStatementCollectionPropertiesFromConfig: subjectUri="
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
            SettingsFactory.log.error("getStatementCollectionPropertiesFromConfig: error", ex);
        }
        
        return Collections.emptyList();
    }
}
