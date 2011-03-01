package org.queryall.helpers;

import info.aduna.iteration.Iterations;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javax.servlet.*;
import javax.servlet.http.*;

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
import org.openrdf.sail.memory.model.MemValueFactory;

import org.queryall.*;
import org.queryall.impl.*;
import org.queryall.queryutils.*;

/**
 * A class used to get access to settings
 * 
 * @author Peter Ansell (p_ansell@yahoo.com)
 * @version $Id: Settings.java 975 2011-02-23 00:59:00Z p_ansell $
 */
public class Settings extends HttpServlet
{
    private static final Logger log = Logger
            .getLogger(Settings.class.getName());
    private static final boolean _TRACE = Settings.log.isTraceEnabled();
    private static final boolean _DEBUG = Settings.log.isDebugEnabled();
    private static final boolean _INFO = Settings.log.isInfoEnabled();

    // This matches the org/queryall/queryall.properties file where
    // the generally static API specific section of the configuration settings are stored
    private static final String BASE_CONFIG_BUNDLE_NAME = "org.queryall.queryall";

    public static final String CURRENT = "current";
    public static final String URL_ENCODED = "urlEncoded";
    public static final String PLUS_URL_ENCODED = "plusUrlEncoded";
    public static final String INPUT_URL_ENCODED = "inputUrlEncoded";
    public static final String INPUT_PLUS_URL_ENCODED = "inputPlusUrlEncoded";
    public static final String XML_ENCODED = "xmlEncoded";
    public static final String INPUT_XML_ENCODED = "inputXmlEncoded";
    public static final String NTRIPLES_ENCODED = "ntriplesEncoded";
    public static final String INPUT_NTRIPLES_ENCODED = "inputNTriplesEncoded";
    public static final String LOWERCASE = "lowercase";
    public static final String UPPERCASE = "uppercase";
    public static final String PRIVATE_LOWERCASE = "privatelowercase";
    public static final String PRIVATE_UPPERCASE = "privateuppercase";
    
    // These are used for sorting
    public static final int LOWEST_ORDER_FIRST = 1;
    public static final int HIGHEST_ORDER_FIRST = 2;
    
    public static final int SUBJECT = 1;
    public static final int PREDICATE = 2;
    public static final int OBJECT = 3;

    public static final String STATISTICS_ITEM_PROFILES = "profiles";
    public static final String STATISTICS_ITEM_SUCCESSFULPROVIDERS = "successfulproviders";
    public static final String STATISTICS_ITEM_ERRORPROVIDERS = "errorproviders";
    public static final String STATISTICS_ITEM_CONFIGLOCATIONS = "configlocations";
    public static final String STATISTICS_ITEM_QUERYTYPES = "querytypes";
    public static final String STATISTICS_ITEM_NAMESPACES = "namespaces";
    public static final String STATISTICS_ITEM_CONFIGVERSION = "configversion";
    public static final String STATISTICS_ITEM_READTIMEOUT = "readtimeout";
    public static final String STATISTICS_ITEM_CONNECTTIMEOUT = "connecttimeout";
    public static final String STATISTICS_ITEM_USERHOSTADDRESS = "userhostaddress";
    public static final String STATISTICS_ITEM_USERAGENT = "useragent";
    public static final String STATISTICS_ITEM_REALHOSTNAME = "realhostname";
    public static final String STATISTICS_ITEM_QUERYSTRING = "querystring";
    public static final String STATISTICS_ITEM_RESPONSETIME = "responsetime";
    public static final String STATISTICS_ITEM_SUMLATENCY = "sumlatency";
    public static final String STATISTICS_ITEM_SUMQUERIES = "sumqueries";
    public static final String STATISTICS_ITEM_STDEVLATENCY = "stdevlatency";
    public static final String STATISTICS_ITEM_SUMERRORS = "sumerrors";
    public static final String STATISTICS_ITEM_SUMERRORLATENCY = "sumerrorlatency";
    public static final String STATISTICS_ITEM_STDEVERRORLATENCY = "stdeverrorlatency";

    public static final String DC_NAMESPACE = "http://purl.org/dc/elements/1.1/";
    public static URI DC_TITLE = null;
    
    // These properties are pulled out of the queryall.properties file
    public static final String DEFAULT_ONTOLOGYTERMURI_PREFIX = Settings.getString("Settings.DEFAULT_ONTOLOGYTERMURI_PREFIX");
    public static final String DEFAULT_ONTOLOGYTERMURI_SUFFIX = Settings.getString("Settings.DEFAULT_ONTOLOGYTERMURI_SUFFIX");

    public static final String DEFAULT_RDF_WEBAPP_CONFIGURATION_NAMESPACE = Settings.getString("Settings.DEFAULT_RDF_WEBAPP_CONFIGURATION_NAMESPACE");
    public static final String DEFAULT_RDF_PROJECT_NAMESPACE = Settings.getString("Settings.DEFAULT_RDF_PROJECT_NAMESPACE");
    public static final String DEFAULT_RDF_PROVIDER_NAMESPACE = Settings.getString("Settings.DEFAULT_RDF_PROVIDER_NAMESPACE");
    public static final String DEFAULT_RDF_TEMPLATE_NAMESPACE = Settings.getString("Settings.DEFAULT_RDF_TEMPLATE_NAMESPACE");
    public static final String DEFAULT_RDF_QUERY_NAMESPACE = Settings.getString("Settings.DEFAULT_RDF_QUERY_NAMESPACE");
    public static final String DEFAULT_RDF_QUERYBUNDLE_NAMESPACE = Settings.getString("Settings.DEFAULT_RDF_QUERYBUNDLE_NAMESPACE");
    public static final String DEFAULT_RDF_RDFRULE_NAMESPACE = Settings.getString("Settings.DEFAULT_RDF_RDFRULE_NAMESPACE");
    public static final String DEFAULT_RDF_RULETEST_NAMESPACE = Settings.getString("Settings.DEFAULT_RDF_RULETEST_NAMESPACE");
    public static final String DEFAULT_RDF_NAMESPACEENTRY_NAMESPACE = Settings.getString("Settings.DEFAULT_RDF_NAMESPACEENTRY_NAMESPACE");
    public static final String DEFAULT_RDF_PROFILE_NAMESPACE = Settings.getString("Settings.DEFAULT_RDF_PROFILE_NAMESPACE");
    public static final String DEFAULT_RDF_PROVENANCE_NAMESPACE = Settings.getString("Settings.DEFAULT_RDF_PROVENANCE_NAMESPACE");
    public static final String DEFAULT_RDF_STATISTICS_NAMESPACE = Settings.getString("Settings.DEFAULT_RDF_STATISTICS_NAMESPACE");
            
    public static final String AUTOGENERATED_QUERY_PREFIX = Settings.getString("Settings.AUTOGENERATED_QUERY_PREFIX");
    public static final String AUTOGENERATED_QUERY_SUFFIX = Settings.getString("Settings.AUTOGENERATED_QUERY_SUFFIX");
    public static final String AUTOGENERATED_PROVIDER_PREFIX = Settings.getString("Settings.AUTOGENERATED_PROVIDER_PREFIX");
    public static final String AUTOGENERATED_PROVIDER_SUFFIX = Settings.getString("Settings.AUTOGENERATED_PROVIDER_SUFFIX");
    
    // these values are initialised dynamically for the lifetime of the servlet
    public static String VERSION = null;
    public static String SUBVERSION_INFO = null;
    public static int CONFIG_API_VERSION = -1;

    public static Collection<String> BASE_CONFIG_FILES = null;
    public static String BASE_CONFIG_URI = null;
    public static String BASE_CONFIG_MIME_FORMAT = null;
    
    private static Repository currentConfigurationRepository = null;
    private static Repository currentBaseConfigurationRepository = null;
    private static Repository currentWebAppConfigurationRepository = null;
    private static Map<URI, Provider> cachedProviders = null;
    private static Map<URI, NormalisationRule> cachedNormalisationRules = null;
    private static Map<URI, RuleTest> cachedRuleTests = null;
    private static Map<URI, QueryType> cachedCustomQueries = null;
    private static Map<URI, Template> cachedTemplates = null;
    private static Map<URI, Profile> cachedProfiles = null;
    private static Map<URI, NamespaceEntry> cachedNamespaceEntries = null;
    private static Map<String, Collection<URI>> cachedNamespacePrefixToUriEntries = null;
    private static Map<URI, Map<URI, Collection<Value>>> cachedWebAppConfigSearches = new Hashtable<URI, Map<URI, Collection<Value>>>();
    public static Collection<String> WEBAPP_CONFIG_URI_LIST = new HashSet<String>();
    public static Collection<String> CONFIG_LOCATION_LIST = new HashSet<String>();
    // public static Collection<String> BACKUP_CONFIG_LOCATION_LIST = new HashSet<String>();

    private static ServletConfig servletConfig = null;
    
    private static long initialisedTimestamp = System.currentTimeMillis();
    
    // No instantiation/encapsulation allowed or possible for this class    
    // private Settings()
    // {
    // }
    
    static
    {
        ValueFactory f = new MemValueFactory();
        
        DC_TITLE = f.createURI(DC_NAMESPACE+"title");
        
    }
    
    public void init() throws ServletException
    {
        Settings.setServletConfig(getServletConfig());
    }    
    
    public static synchronized void setServletConfig(ServletConfig newServletConfig)
    {
        log.info("Settings: newServletConfig="+newServletConfig);
        
        servletConfig = newServletConfig;

        VERSION = servletConfig.getServletContext().getInitParameter("VERSION");

        SUBVERSION_INFO = servletConfig.getServletContext().getInitParameter("SUBVERSION_INFO");

        log.info("Settings: VERSION="+VERSION);

        CONFIG_API_VERSION = Integer.parseInt(servletConfig.getServletContext().getInitParameter("CONFIG_API_VERSION"));

        String newBaseConfigFilesString = servletConfig.getServletContext().getInitParameter("BASE_CONFIG_FILES");
        String newBaseConfigUri = servletConfig.getServletContext().getInitParameter("BASE_CONFIG_URI");
        String newBaseConfigMimeFormat = servletConfig.getServletContext().getInitParameter("BASE_CONFIG_MIME_FORMAT");

        Collection<String> newBaseConfigFiles = new LinkedList<String>();
        
        if(newBaseConfigFilesString == null)
        {
            log.fatal("Settings: null init parameter named BASE_CONFIG_FILES newBaseConfigFilesString="+newBaseConfigFilesString);
            return;
        }
        
        for(String nextSplit : newBaseConfigFilesString.split(","))
        {
            if(nextSplit != null && !nextSplit.equals(""))
            {
                newBaseConfigFiles.add(nextSplit);
            }
        }
        
        if(BASE_CONFIG_FILES == null && BASE_CONFIG_URI == null && BASE_CONFIG_MIME_FORMAT == null)
        {
            BASE_CONFIG_FILES = newBaseConfigFiles;
            BASE_CONFIG_URI = newBaseConfigUri;
            BASE_CONFIG_MIME_FORMAT = newBaseConfigMimeFormat;
        }
    }
    
    public static synchronized boolean configRefreshCheck(boolean tryToForceRefresh)
    {
        final long currentTimestamp = System.currentTimeMillis();
        
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.configRefreshCheck: before check Settings.PERIODIC_CONFIGURATION_REFRESH="
                            + Settings.getBooleanPropertyFromConfig("enablePeriodicConfigurationRefresh")
                            + " Settings.PERIODIC_REFRESH_MILLISECONDS="
                            + Settings.getLongPropertyFromConfig("periodicConfigurationMilliseconds")
                            + " currentTimestamp - initialisedTimestamp="
                            + (currentTimestamp - Settings.initialisedTimestamp)
                            + " ");
        }
        if(tryToForceRefresh && !Settings.isManualRefreshAllowed())
        {
            Settings.log
                    .error("Settings.configRefreshCheck: attempted to force refresh outside of manual refresh time and ability guidelines");
            return false;
        }
        
        boolean enablePeriodicConfigurationRefresh = Settings.getBooleanPropertyFromConfig("enablePeriodicConfigurationRefresh");
        long periodicConfigurationMilliseconds = Settings.getLongPropertyFromConfig("periodicConfigurationMilliseconds");
        
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.configRefreshCheck: enablePeriodicConfigurationRefresh="+enablePeriodicConfigurationRefresh);
            Settings.log
                    .debug("Settings.configRefreshCheck: periodicConfigurationMilliseconds="+periodicConfigurationMilliseconds);
        }
    
        if(tryToForceRefresh
                || (enablePeriodicConfigurationRefresh && ((currentTimestamp - Settings.initialisedTimestamp) > periodicConfigurationMilliseconds)))
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
                if(Settings.currentWebAppConfigurationRepository != null)
                {
                    synchronized(Settings.currentWebAppConfigurationRepository)
                    {
                        previousWebappConfiguration = Settings.currentWebAppConfigurationRepository;
                        if(Settings._DEBUG)
                        {
                            Settings.log
                                    .debug("Settings.configRefreshCheck: refresh required... currentWebappConfigurationRepository about to be set to null");
                        }
                        Settings.currentWebAppConfigurationRepository = null;
                    }
                }
                
                Settings.getWebAppConfigurationRdf();
                
                if(Settings.currentWebAppConfigurationRepository == null)
                {
                    currentWebAppConfigurationRepository = previousWebappConfiguration;
                    
                    Settings.log
                            .error("Settings.configRefreshCheck: WebappConfiguration was not valid after the refresh, resetting to the previousWebappConfiguration");

                    // reset the timestamp so that we don't try too often
                    // TODO: improve functionality for specifying retry time if failures occur
                    // Settings.initialisedTimestamp = System.currentTimeMillis();
                    
                    // return false;
                }

                
                if(Settings.currentConfigurationRepository != null)
                {
                    synchronized(Settings.currentConfigurationRepository)
                    {
                        previousConfiguration = Settings.currentConfigurationRepository;
                        if(Settings._DEBUG)
                        {
                            Settings.log
                                    .debug("Settings.configRefreshCheck: refresh required... currentConfigurationRepository about to be set to null");
                        }
                        Settings.currentConfigurationRepository = null;
                    }
                }
                if(Settings._INFO)
                {
                    Settings.log
                            .info("Settings.configRefreshCheck: refresh required... getServerConfigurationRdf about to be called");
                }
                Settings.getServerConfigurationRdf();
                
                if(Settings.currentConfigurationRepository == null)
                {
                    currentConfigurationRepository = previousConfiguration;
                    
                    Settings.log
                            .error("Settings.configRefreshCheck: configuration was not valid after the refresh, resetting to the previousConfiguration");

                    // reset the timestamp so that we don't try too often
                    // TODO: improve functionality for specifying retry time if failures occur
                    Settings.initialisedTimestamp = System.currentTimeMillis();
                    
                    return false;
                }

                if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.configRefreshCheck: refresh required... currentConfigurationRepository refreshed");
                }
                if(Settings.cachedProviders != null)
                {
                    synchronized(Settings.cachedProviders)
                    {
                        Settings.cachedProviders = null;
                    }
                }
                Settings.getAllProviders();
                if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.configRefreshCheck: refresh required... cachedProviders refreshed");
                }
                if(Settings.cachedCustomQueries != null)
                {
                    synchronized(Settings.cachedCustomQueries)
                    {
                        Settings.cachedCustomQueries = null;
                    }
                }
                Settings.getAllCustomQueries();
                if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.configRefreshCheck: refresh required... cachedCustomQueries refreshed");
                }
                if(Settings.cachedNamespaceEntries != null)
                {
                    synchronized(Settings.cachedNamespaceEntries)
                    {
                        Settings.cachedNamespaceEntries = null;
                    }
                }
                if(Settings.cachedNamespacePrefixToUriEntries != null)
                {
                    synchronized(Settings.cachedNamespacePrefixToUriEntries)
                    {
                        Settings.cachedNamespacePrefixToUriEntries = null;
                    }
                }
                Settings.getAllNamespaceEntries();
                if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.configRefreshCheck: refresh required... cachedNamespaceEntries and cachedNamespacePrefixToUriEntries refreshed");
                }
                
                if(Settings.cachedProfiles != null)
                {
                    synchronized(Settings.cachedProfiles)
                    {
                        Settings.cachedProfiles = null;
                    }
                }
                Settings.getAllProfiles();
                
                if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.configRefreshCheck: refresh required... cachedProfiles refreshed");
                }
                
                if(Settings.cachedNormalisationRules != null)
                {
                    synchronized(Settings.cachedNormalisationRules)
                    {
                        Settings.cachedNormalisationRules = null;
                    }
                }
                Settings.getAllNormalisationRules();
                if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.configRefreshCheck: refresh required... cachedNormalisationRules refreshed");
                }
                if(Settings.cachedRuleTests != null)
                {
                    synchronized(Settings.cachedRuleTests)
                    {
                        Settings.cachedRuleTests = null;
                    }
                }
                
                Settings.getAllRuleTests();
                
                if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.configRefreshCheck: refresh required... cachedRuleTests refreshed");
                }
                
                if(Settings.cachedTemplates != null)
                {
                    synchronized(Settings.cachedTemplates)
                    {
                        Settings.cachedTemplates = null;
                    }
                }
                Settings.getAllTemplates();
                
                if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.configRefreshCheck: refresh required... cachedTemplates refreshed");
                }
                
                Settings.initialisedTimestamp = System.currentTimeMillis();

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
                Settings.currentConfigurationRepository = previousConfiguration;
                return false;
            }
        }
        if(Settings._TRACE)
        {
            Settings.log.trace("Settings.configRefreshCheck: returning");
        }
        return false;
    }
    
    public static synchronized Map<URI, QueryType> getAllCustomQueries()
    {
        if(Settings.cachedCustomQueries != null)
        {
            return Settings.cachedCustomQueries;
        }
        
        try
        {
            final Repository myRepository = Settings.getServerConfigurationRdf();
            
            Map<URI, QueryType> results = getCustomQueries(myRepository);
            
            if(_INFO)
            {
                log.info("Settings.getAllCustomQueries: found "+results.size()+" queries");
            }
            
            Settings.cachedCustomQueries = results;
            
            return results;
        }
        catch(java.lang.InterruptedException ie)
        {
            Settings.log.fatal("Settings: caught java.lang.InterruptedException: not throwing it.", ie);
            
            return null;
        }
    }
    
    public static Map<URI, QueryType> getCustomQueries(Repository myRepository)
    {
        final Hashtable<URI, QueryType> results = new Hashtable<URI, QueryType>();
        final long start = System.currentTimeMillis();
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getCustomQueries: started parsing custom queries");
        }
        // TODO: make the Query suffix configurable
        final String queryOntologyTypeUri = Settings.DEFAULT_ONTOLOGYTERMURI_PREFIX
                + Settings.DEFAULT_RDF_QUERY_NAMESPACE
                + Settings.DEFAULT_ONTOLOGYTERMURI_SUFFIX + "Query";
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
                            .trace("Settings.getCustomQueries: found queryString="
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
                                    .debug("Settings: found query: valueOfQueryUri="
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
                                    .error("Settings: was not able to create a query configuration with URI valueOfQueryUri="
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
            Settings.log.error("Settings.getCustomQueries:", e);
        }
        if(Settings._INFO)
        {
            final long end = System.currentTimeMillis();
            Settings.log.info(String.format("%s: timing=%10d",
                    "Settings.getCustomQueries", (end - start)));
        }
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getCustomQueries: finished parsing custom queries");
        }
        
        return results;
    }
    
    public static synchronized boolean addTemplate(Template nextTemplate, boolean overwritePrevious)
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
    
    public static Template getTemplate(URI nextKey)
    {
        Template result = null;
        
        Map<URI, Template> allTemplates = Settings.getAllTemplates();
        
        if(allTemplates.containsKey(nextKey))
        {
            result = allTemplates.get(nextKey);
        }
        
        return result;
    }
    
    public static synchronized Template createNewTemplateByString(String templateKey, String templateString, String contentType)
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
    public static void matchTemplatesToPatternsForQuery(
        List<Template> allTemplates, 
        List<Template> constantParameters, 
        StringBuilder queryString)
    {
        //StringBuilder result = new StringBuilder(queryString);
        StringBuilder result = queryString;
        
        for(Template nextTemplate : constantParameters)
        {
            Pattern nextPattern = Pattern.compile(nextTemplate.getMatchRegex());
            
            Utilities.replaceMatchesForRegexOnString(nextPattern, nextTemplate.getMatchRegex(), result, new StringBuilder(nextTemplate.getTemplateString()));
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
            
            Utilities.replaceMatchesForRegexOnString(nextPattern, nextTemplate.getMatchRegex(), result, new StringBuilder(nextTemplate.getTemplateString()));
            
            // 
            // if(nextTemplate.isNativeFunction)
            // {
                // Utilities.applyNativeFunctionTemplate(nextTemplate, result);
            // }
            // else
            // {
                // Utilities.replaceMatchesForRegexOnString(nextPattern, nextTemplate.matchRegex, result, new StringBuilder(nextTemplate.templateString));
            // }
            if(_DEBUG)
            {
                // log.debug("nextTemplate.getKey()="+nextTemplate.getKey().stringValue()+ " matches.size()="+matches.size());
                log.debug("afterthistemplate: nextTemplate.getKey()="+nextTemplate.getKey().stringValue()+" original="+original+" result="+result.toString());
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
            Settings.matchTemplatesToPatternsForQuery(nextCalledTemplates, constantParameters, result);
            
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
            Utilities.replaceMatchesForRegexOnString(nextPattern, nextTemplate.getMatchRegex(), result, new StringBuilder(nextTemplate.getTemplateString()));
        }
        
        // return result;
    }
    
    public static synchronized Template createNewTemplateByKey(String templateKey, String contentType)
    {
        Template result = new TemplateImpl();
        
        result.setKey(templateKey);
        result.setContentType(contentType);
        
        return result;
    }
    
    public static synchronized Map<URI, Template> getAllTemplates()
    {
        if(Settings.cachedTemplates != null)
        {
            return Settings.cachedTemplates;
        }

        final Hashtable<URI, Template> results = new Hashtable<URI, Template>();
        
        final long start = System.currentTimeMillis();

        if(Settings._DEBUG)
        {
            Settings.log.debug("Settings.getAllTemplates: started parsing templates");
        }
        try
        {
            final Repository myRepository = Settings.getServerConfigurationRdf();
            
            final String templateOntologyTypeUri = Settings.DEFAULT_ONTOLOGYTERMURI_PREFIX
                    + Settings.DEFAULT_RDF_TEMPLATE_NAMESPACE
                    + Settings.DEFAULT_ONTOLOGYTERMURI_SUFFIX + "Template";
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
        Settings.cachedTemplates = results;
        return Settings.cachedTemplates;
    }
    
    public static synchronized Map<URI, NamespaceEntry> getAllNamespaceEntries()
    {
        if(Settings.cachedNamespaceEntries != null)
        {
            return Settings.cachedNamespaceEntries;
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
            
            Settings.cachedNamespacePrefixToUriEntries = tempNamespacePrefixToUriEntries;
            Settings.cachedNamespaceEntries = results;
            
            return results;
        }
        catch(java.lang.InterruptedException ie)
        {
            Settings.log.fatal("Settings.getAllNamespaceEntries: caught java.lang.InterruptedException: not throwing it.", ie);
            
            return null;
        }
    }
    
    public static synchronized Map<URI, NamespaceEntry> getNamespaceEntries(Repository myRepository)
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
        final String namespaceEntryOntologyTypeUri = new NamespaceEntryImpl().getElementType();
        try
        {
            final RepositoryConnection con = myRepository.getConnection();
            try
            {
                final String queryString = "SELECT ?namespaceEntryUri WHERE { ?namespaceEntryUri a <"
                        + namespaceEntryOntologyTypeUri + "> . }";
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
                                    .debug("Settings: found namespace: valueOfNamespaceEntryUri="
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
    
    public static synchronized Map<URI, Profile> getAllProfiles()
    {
        if(Settings.cachedProfiles != null)
        {
            return Settings.cachedProfiles;
        }
        final Map<URI, Profile> results = new Hashtable<URI, Profile>();
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getAllProfiles: started parsing profile configurations");
        }
        final long start = System.currentTimeMillis();

        try
        {
            final Repository myRepository = Settings
                    .getServerConfigurationRdf();
            final String profileOntologyTypeUri = new ProfileImpl().getElementType();

            try
            {
                final RepositoryConnection con = myRepository.getConnection();
                try
                {
                    final String queryString = "SELECT ?profileUri WHERE { ?profileUri a <"
                            + profileOntologyTypeUri + "> . }";
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
                                        .debug("Settings: found profile: valueOfProfileUri="
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
                                        .error("Settings: was not able to create a profile with URI valueOfProfileUri="
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
                    "Settings.getAllProfiles", (end - start)));
        }
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getAllProfiles: finished parsing profiles");
        }
        
        Settings.cachedProfiles = results;
        
        if(_INFO)
        {
            log.info("Settings.getAllProfiles: found "+results.size()+" profiles");
        }
        
        return Settings.cachedProfiles;
    }
    
    public static synchronized Map<URI, Provider> getAllProviders()
    {
        if(Settings.cachedProviders != null)
        {
            return Settings.cachedProviders;
        }
        
        Map<URI, Provider> results = null;
        
        try
        {
            final Repository myRepository = Settings.getServerConfigurationRdf();
            
            results = getProviders(myRepository);
            
            if(results != null)
            {
                Settings.cachedProviders = results;
            }
        }
        catch(java.lang.InterruptedException ie)
        {
            Settings.log.fatal("Settings: caught java.lang.InterruptedException: not throwing it.", ie);
            
            return null;
        }
        
        if(_INFO)
        {
            log.info("Settings.getAllProviders: found "+results.size()+" providers");
        }
        
        return results;
    }
    
    public static Map<URI, Provider> getProviders(Repository myRepository)
    {
        final Hashtable<URI, Provider> results = new Hashtable<URI, Provider>();

        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getProviders: started parsing provider configurations");
        }
        final long start = System.currentTimeMillis();

        final String providerOntologyTypeUri = Settings.DEFAULT_ONTOLOGYTERMURI_PREFIX
                + Settings.DEFAULT_RDF_PROVIDER_NAMESPACE
                + Settings.DEFAULT_ONTOLOGYTERMURI_SUFFIX + "Provider";
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
                                    .debug("Settings: found provider: valueOfProviderUri="
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
                                    .error("Settings: was not able to create a provider configuration with URI valueOfProviderUri="
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
            Settings.log.error("Settings:", e);
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
    
    public static synchronized Map<URI, NormalisationRule> getAllNormalisationRules()
    {
        if(Settings.cachedNormalisationRules != null)
        {
            return Settings.cachedNormalisationRules;
        }

        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getAllNormalisationRules: started parsing rdf normalisation rules");
        }

        final long start = System.currentTimeMillis();

        final Hashtable<URI, NormalisationRule> results = new Hashtable<URI, NormalisationRule>();
        try
        {
            final Repository configRepository = Settings.getServerConfigurationRdf();
            
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
                                        .debug("Settings: found regex rule: valueOfRdfRuleUri="
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
                Settings.log.error("Settings:", e);
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
                                        .debug("Settings: found sparql rule: valueOfRdfRuleUri="
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
            Settings.log.fatal("Settings: caught java.lang.InterruptedException: not throwing it.", ie);
        }
        if(Settings._INFO)
        {
            final long end = System.currentTimeMillis();
            Settings.log.info(String.format("%s: timing=%10d",
                    "Settings.getAllNormalisationRules", (end - start)));
        }
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getAllNormalisationRules: finished parsing normalisation rules");
        }
        Settings.cachedNormalisationRules = results;

        if(_INFO)
        {
            log.info("Settings.getAllNormalisationRules: found "+results.size()+" normalisation rules");
        }
        

        return Settings.cachedNormalisationRules;
    }
    
    public static synchronized Map<URI, RuleTest> getAllRuleTests()
    {
        if(Settings.cachedRuleTests != null)
        {
            return Settings.cachedRuleTests;
        }
        final Hashtable<URI, RuleTest> results = new Hashtable<URI, RuleTest>();
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getAllRuleTests: started parsing rule test configurations");
        }
        final long start = System.currentTimeMillis();
        try
        {
            final Repository configRepository = Settings
                    .getServerConfigurationRdf();
            final String ruleTestOntologyTypeUri = new RuleTestImpl().getElementType();
            try
            {
                final RepositoryConnection con = configRepository.getConnection();
                try
                {
                    final String queryString = "SELECT ?ruleTestUri WHERE { ?ruleTestUri a <"
                            + ruleTestOntologyTypeUri + "> . }";
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
                                        .debug("Settings: found ruletest: valueOfRuleTestUri="
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
                                        .error("Settings: was not able to create a rule test configuration with URI valueOfRuleTestUri="
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
                    "Settings.getAllRuleTests", (end - start)));
        }
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.getAllRuleTests: finished getting rdf rule tests");
        }
        Settings.cachedRuleTests = results;

        if(_INFO)
        {
            log.info("Settings.getAllRuleTests: found "+results.size()+" rule tests");
        }
        
        return Settings.cachedRuleTests;
    }
    
    public static List<Profile> getAndSortProfileList(Collection<URI> nextProfileUriList, int nextSortOrder)
    {
        final Map<URI, Profile> allProfiles = Settings.getAllProfiles();
        final List<Profile> results = new ArrayList<Profile>();
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
        
        if(nextSortOrder == Settings.LOWEST_ORDER_FIRST)
        {
            Collections.sort(results);
        }
        else if(nextSortOrder == Settings.HIGHEST_ORDER_FIRST)
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
    
    public static Collection<QueryType> getCustomQueriesByUri(URI queryTypeUri)
    {
        final Collection<QueryType> results = new HashSet<QueryType>();
        for(final QueryType nextQueryType : Settings
                .getAllCustomQueries().values())
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
    public static Collection<QueryType> getCustomQueriesMatchingQueryString(String queryString, List<Profile> profileList)
    {
        log.debug("Settings.getCustomQueriesMatchingQueryString: profileList.size()="+profileList.size());
        
        for(Profile nextProfile : profileList)
        {
            log.trace("Settings.getCustomQueriesMatchingQueryString: nextProfile.getKey()="+nextProfile.getKey().stringValue());
        }
        
        final Collection<QueryType> results = new HashSet<QueryType>();
        
        for(QueryType nextQuery : Settings.getAllCustomQueries().values())
        {
            // FIXME: allow for queries with no matching groups
            // Currently queries with no matching groups will fail this step and not be considered valid
            if(nextQuery.matchesForQueryString(queryString).size() > 0)
            {
                if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.getCustomQueriesMatchingQueryString: tentative, pre-profile-check match for"
                                    + " nextQuery.getKey()="
                                    + nextQuery.getKey().stringValue()
                                    + " queryString="
                                    + queryString);
                }
                if(Settings.isQueryUsedWithProfileList(nextQuery.getKey(),
                        nextQuery.getProfileIncludeExcludeOrder(), profileList))
                {
                    if(Settings._DEBUG)
                    {
                        Settings.log
                                .debug("Settings.getCustomQueriesMatchingQueryString: profileList suitable for"
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
                            .trace("Settings.getCustomQueriesMatchingQueryString: profileList not suitable for"
                                    + " nextQuery.getKey()="
                                    + nextQuery.getKey().stringValue()
                                    + " queryString="
                                    + queryString);
                }
            }
        }
        return results;
    }
    
    public static Collection<Provider> getDefaultProviders(URI queryKey)
    {
        final Collection<Provider> results = new HashSet<Provider>();
        
        for(final Provider nextProvider : Settings.getAllProviders().values())
        {
            if(nextProvider.getIsDefaultSource()
                    && nextProvider.handlesQueryExplicitly(queryKey))
            {
                results.add(nextProvider);
            }
        }
        
        return results;
    }
    
    public static NamespaceEntry getNamespaceEntryByUri(URI namespaceEntryUri)
    {
        final Map<URI, NamespaceEntry> allNamespaces = Settings.getAllNamespaceEntries();
        
        if(allNamespaces.containsKey(namespaceEntryUri))
        {
            return allNamespaces.get(namespaceEntryUri);
        }
        else
        {
            return null;
        }
    }
    
    public static Collection<URI> getNamespaceUrisForTitle(String namespacePrefix)
    {
        Collection<URI> results = new HashSet<URI>();
        
        if(Settings.cachedNamespacePrefixToUriEntries == null)
        {
            // HACK
            // this function initialised the namespace prefix to URI cache
            Settings.getAllNamespaceEntries();
        }
        
        results = Settings.cachedNamespacePrefixToUriEntries.get(namespacePrefix);
        return results;
    }
    
    public static List<NormalisationRule> getSortedRulesForProviders(Collection<Provider> Providers, int sortOrder)
    {
        List<NormalisationRule> results = new ArrayList<NormalisationRule>();
        
        for(Provider nextProvider : Providers)
        {
            results.addAll(getSortedRulesForProvider(nextProvider, sortOrder));
        }
        
        if(sortOrder == Settings.HIGHEST_ORDER_FIRST)
        {
            Collections.sort(results, Collections.reverseOrder());
        }
        else if(sortOrder == Settings.LOWEST_ORDER_FIRST)
        {
            Collections.sort(results);
        }
        
        return results;
    }
    
    public static List<NormalisationRule> getSortedRulesForProvider(
            Provider nextProvider, int sortOrder)
    {
        if(nextProvider.getNormalisationsNeeded().size() == 0)
        {
            return new ArrayList<NormalisationRule>(1);
        }
        else
        {
            return Settings.getNormalisationRulesForUris(
                    nextProvider.getNormalisationsNeeded(),
                    sortOrder);
        }
    }
    
    public static List<NormalisationRule> getNormalisationRulesForUris(
            Collection<URI> rdfNormalisationsNeeded, int sortOrder)
    {
        final List<NormalisationRule> results = new ArrayList<NormalisationRule>();
        // final List<NormalisationRule> intermediateResults = new ArrayList<NormalisationRule>();
        final Map<URI, NormalisationRule> allNormalisationRules = Settings.getAllNormalisationRules();
        
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
        if(sortOrder == Settings.LOWEST_ORDER_FIRST)
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
        else if(sortOrder == Settings.HIGHEST_ORDER_FIRST)
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
    
    public static Collection<Provider> getProvidersForQueryType(
            URI customService)
    {
        final Collection<Provider> results = Settings.getProvidersForQueryTypeFromList(customService, Settings.getAllProviders().values());
        
        if(Settings._DEBUG)
        {
            Settings.log.debug("Settings.getProvidersForQueryType: Found "
                    + results.size() + " providers for customService="
                    + customService);
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
    
    public static Collection<Provider> getProvidersForQueryTypeForNamespaceUris(
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
        
        final Collection<Provider> namespaceProviders = Settings.getProvidersForNamespaceUris(namespaceUris, namespaceMatchMethod);
        
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
    
    public static Collection<Provider> getProvidersForQueryTypeFromList(
            URI customService, Collection<Provider> knownProviders)
    {
        final Collection<Provider> results = new HashSet<Provider>();
        for(final Provider nextProvider : knownProviders)
        {
            if(nextProvider.handlesQueryExplicitly(customService))
            {
                results.add(nextProvider);
            }
        }
        return results;
    }
    
    public static Collection<Provider> getProvidersFornamespacePreferredPrefixs(
            Collection<String> namespacePreferredPrefixs, URI namespaceMatchMethod)
    {
        if((namespacePreferredPrefixs == null)
                || (namespacePreferredPrefixs.size() == 0))
        {
            if(Settings._DEBUG)
            {
                Settings.log
                        .debug("Settings.getProvidersFornamespacePreferredPrefixs: namespacePreferredPrefixs was either null or empty");
            }
            return new HashSet<Provider>(1);
        }
        
        final Collection<Collection<URI>> allNamespaceUris = new HashSet<Collection<URI>>();
        
        for(final String nextnamespacePreferredPrefix : namespacePreferredPrefixs)
        {
            allNamespaceUris.add(Settings.getNamespaceUrisForTitle(nextnamespacePreferredPrefix));
        }
        
        return Settings.getProvidersForNamespaceUris(allNamespaceUris, namespaceMatchMethod);
    }
    
    public static Collection<Provider> getProvidersForNamespaceUris(
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
        // final Hashtable<String, Provider> nextAllProviders = Settings.getAllProviders();
        // final Collection<Provider> providerCollection = nextAllProviders
                // .values();
        for(final Provider nextProvider : Settings.getAllProviders().values())
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
    
    public static Collection<RuleTest> getRuleTestsForNormalisationRuleUri(
            URI normalisationRuleUri)
    {
        final Collection<RuleTest> results = new HashSet<RuleTest>();
        //final Hashtable<String, RuleTest> allRdfRules = Settings.getAllRuleTests();
        
        for(final RuleTest nextRuleTest : Settings.getAllRuleTests().values())
        {
            if(nextRuleTest.getRuleUris().contains(normalisationRuleUri))
            {
                results.add(nextRuleTest);
            }
        }
        return results;
    }
    
    private static synchronized Repository getWebAppConfigurationRdf() throws java.lang.InterruptedException
    {
        if(Settings.currentWebAppConfigurationRepository != null)
        {
            return Settings.currentWebAppConfigurationRepository;
        }
        
        if(_DEBUG)
            Settings.log.debug("Settings.getWebAppConfigurationRdf: constructing a new repository");
        
        final long start = System.currentTimeMillis();
        final String configMIMEFormat = Settings.BASE_CONFIG_MIME_FORMAT;
        final String baseURI = Settings.BASE_CONFIG_URI;
        Repository tempConfigurationRepository = null;
        Repository finalConfigurationRepository = null;
        Repository nextBaseConfigurationRepository = getBaseConfigurationRdf();
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
            
            URI subjectConfigUri = f.createURI(BASE_CONFIG_URI);

            URI webappConfigLocationsUri = f.createURI("http://purl.org/queryall/webapp_configuration:webappConfigLocations");
            
            URI activeWebappConfigsUri = f.createURI("http://purl.org/queryall/webapp_configuration:activeWebappConfigs");
            
            Collection<Value> webappConfigFiles = getValueCollectionPropertiesFromConfig(subjectConfigUri, webappConfigLocationsUri, nextBaseConfigurationRepository);

            Collection<Value> activeWebappConfigs = getValueCollectionPropertiesFromConfig(
                subjectConfigUri, activeWebappConfigsUri, nextBaseConfigurationRepository);

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
                
                // Settings.log.error("Settings.getWebAppConfigurationRdf: nextLocation="+nextLocation);
                // TODO: negotiate between local and non-local addresses better
                // than this
            
                
                final RepositoryConnection myRepositoryConnection = tempConfigurationRepository.getConnection();
                

                try
                {
                    File nextUrlFile = new File(nextLocation);
                    if(!nextUrlFile.isAbsolute())
                    {
                        // only let people utilise the WEB-INF directory if they
                        // give a relative URL
                        // configFile = new
                        // File(getServletContext().getRealPath("/") +
                        // "/WEB-INF/" + configUrl);
                        nextUrlFile = new File(getWebInfPath()+nextLocation);
                    }
                    
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
                                            + nextLocation
                                            + " nextUrlFile="
                                            + nextUrlFile);
                        }
                        
                        myRepositoryConnection.add(nextUrlFile, baseURI, RDFFormat.forMIMEType(configMIMEFormat));
                        if(Settings._INFO)
                        {
                            Settings.log
                                    .info("Settings.getWebAppConfigurationRdf: finished getting configuration from file: nextLocation="
                                            + nextLocation
                                            + " nextUrlFile="
                                            + nextUrlFile);
                        }
                        
                    }

                    for(Value nextValue : activeWebappConfigs)
                    {
                        log.debug("Settings.getWebAppConfigurationRdf: started adding statements to finalrepository for nextValue="+nextValue.stringValue()+" finalRepositoryConnection.size()="+finalRepositoryConnection.size());
                        WEBAPP_CONFIG_URI_LIST.add(nextValue.stringValue());
                        finalRepositoryConnection.add(myRepositoryConnection.getStatements((URI)nextValue, (URI)null, (Resource)null, true));
                        log.debug("Settings.getWebAppConfigurationRdf: finished adding statements to finalrepository for nextValue="+nextValue.stringValue()+" finalRepositoryConnection.size()="+finalRepositoryConnection.size());
                    }
                }
                catch (final RDFParseException rdfpe)
                {
                    Settings.log
                            .fatal(
                                    "Settings.getWebAppConfigurationRdf: failed to get the configuration repository. Caught RDFParseException. nextLocation="+nextLocation,
                                    rdfpe);
                    throw new RuntimeException(
                            "Settings.getWebAppConfigurationRdf: failed to initialise the configuration repository. Caught RDFParseException. nextLocation="+nextLocation);
                }
                catch (final OpenRDFException ordfe)
                {
                    Settings.log
                            .fatal(
                                    "Settings.getWebAppConfigurationRdf: failed to initialise the base configuration repository. Caught OpenRDFException. nextLocation="+nextLocation,
                                    ordfe);
                    throw new RuntimeException(
                            "Settings.getWebAppConfigurationRdf: failed to initialise the base configuration repository. Caught OpenRDFException. nextLocation="+nextLocation);
                }
                catch (final java.io.IOException ioe)
                {
                    Settings.log.error("Settings.getWebAppConfigurationRdf: failed to initialise the base configuration repository. Caught java.io.IOException. nextLocation="+nextLocation, ioe);
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
            Settings.currentWebAppConfigurationRepository = finalConfigurationRepository;
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
                Settings.log.info("Settings.getWebAppConfigurationRdf: found "+Settings.currentWebAppConfigurationRepository.getConnection().size()+" statements in webapp configuration");
            }
            catch(RepositoryException rex)
            {
                Settings.log.error("Settings.getWebAppConfigurationRdf: could not determine the number of statements in webapp configuration");
            }
        }
        
        return Settings.currentWebAppConfigurationRepository;
    }    
    
    private static synchronized Repository getBaseConfigurationRdf() throws java.lang.InterruptedException
    {
        if(Settings.currentBaseConfigurationRepository != null)
        {
            return Settings.currentBaseConfigurationRepository;
        }
        
        if(_DEBUG)
        {
            Settings.log.debug("Settings.getBaseConfigurationRdf: constructing a new repository");
        }
        
        final long start = System.currentTimeMillis();
        final String configMIMEFormat = Settings.BASE_CONFIG_MIME_FORMAT;
        final String baseURI = Settings.BASE_CONFIG_URI;
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

            for(final String nextLocation : BASE_CONFIG_FILES)
            {
                if(nextLocation.equals(""))
                    continue;
                
                if(_INFO)
                {
                    Settings.log.info("Settings.getBaseConfigurationRdf: nextLocation="+nextLocation);
                }
                // TODO: negotiate between local and non-local addresses better
                // than this
                final RepositoryConnection myRepositoryConnection = tempConfigurationRepository
                        .getConnection();
                try
                {
                    File nextUrlFile = new File(nextLocation);
                    if(!nextUrlFile.isAbsolute())
                    {
                        // only let people utilise the WEB-INF directory if they
                        // give a relative URL
                        // configFile = new
                        // File(getServletContext().getRealPath("/") +
                        // "/WEB-INF/" + configUrl);
                        nextUrlFile = new File(getWebInfPath()+nextLocation);
                    }
                    
                    if(nextLocation.startsWith("http://") || nextLocation.startsWith("https://"))
                    {
                        final URL url = new URL(nextLocation);
                        
                        if(Settings._INFO)
                        {
                            Settings.log.info("Settings.getBaseConfigurationRdf: getting configuration from URL: nextLocation="+ nextLocation+" url="+url.toString());
                        }
                        
                        myRepositoryConnection.add(url, baseURI, RDFFormat.forMIMEType(configMIMEFormat));

                        if(Settings._INFO)
                        {
                            Settings.log.info("Settings.getBaseConfigurationRdf: finished getting configuration from URL: url="+ url.toString());
                        }
                    }
                    else
                    {
                        if(Settings._INFO)
                        {
                            Settings.log
                                    .info("Settings.getBaseConfigurationRdf: getting configuration from file: nextLocation="
                                            + nextLocation
                                            + " nextUrlFile="
                                            + nextUrlFile);
                        }
                        
                        myRepositoryConnection.add(nextUrlFile, baseURI, RDFFormat.forMIMEType(configMIMEFormat));
                        if(Settings._INFO)
                        {
                            Settings.log
                                    .info("Settings.getBaseConfigurationRdf: finished getting configuration from file: nextLocation="
                                            + nextLocation
                                            + " nextUrlFile="
                                            + nextUrlFile);
                        }
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
        
        if(tempConfigurationRepository == null)
        {
            throw new RuntimeException(
            "Settings.getBaseConfigurationRdf: failed to initialise the webapp configuration repository");
        }
        else
        {
            Settings.currentBaseConfigurationRepository = tempConfigurationRepository;
        }

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
                Settings.log.info("Settings.getBaseConfigurationRdf: found "+Settings.currentBaseConfigurationRepository.getConnection().size()+" statements in base configuration");
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
                for(Statement nextStatement : Utilities.getAllStatementsFromRepository(Settings.currentBaseConfigurationRepository))
                {
                    Settings.log.trace(nextStatement.toString());
                }
            }
            catch(Exception ex)
            {
                log.error("Could not dump statements",ex);
            }
        }
        
        return Settings.currentBaseConfigurationRepository;
    }

    private static synchronized Repository getServerConfigurationRdf() throws java.lang.InterruptedException
    {
        if(Settings.currentConfigurationRepository != null)
        {
            return Settings.currentConfigurationRepository;
        }
        final long start = System.currentTimeMillis();
        final String configMIMEFormat = Settings.BASE_CONFIG_MIME_FORMAT;
        final String baseURI = Settings.getDefaultHostAddress();
        Repository tempConfigurationRepository = null;
        boolean backupNeeded = false;
        boolean backupFailed = false;
        
        try
        {
            tempConfigurationRepository = new SailRepository(new MemoryStore());
            tempConfigurationRepository.initialize();
            
            for(final String nextLocation : Settings.getStringCollectionPropertiesFromConfig("queryConfigLocations"))
            {
                // TODO: negotiate between local and non-local addresses better
                // than this
                final RepositoryConnection myRepositoryConnection = tempConfigurationRepository
                        .getConnection();
                try
                {
                    File nextUrlFile = new File(nextLocation);
                    if(!nextUrlFile.isAbsolute())
                    {
                        // only let people utilise the WEB-INF directory if they
                        // give a relative URL
                        // configFile = new
                        // File(getServletContext().getRealPath("/") +
                        // "/WEB-INF/" + configUrl);
                        nextUrlFile = new File(Settings.getWebInfPath()+nextLocation);
                    }
                    
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
                                            + nextLocation
                                            + " nextUrlFile="
                                            + nextUrlFile);
                        }
                        myRepositoryConnection.add(nextUrlFile, baseURI, RDFFormat.forMIMEType(configMIMEFormat));
                        if(Settings._INFO)
                        {
                            Settings.log
                                    .info("Settings: finished getting configuration from file: nextLocation="
                                            + nextLocation
                                            + " nextUrlFile="
                                            + nextUrlFile);
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
                
                for(final String nextLocation : Settings.getStringCollectionPropertiesFromConfig("backupQueryConfigLocations"))
                {
                    // TODO: negotiate between local and non-local addresses better
                    // than this
                    final RepositoryConnection myRepositoryConnection = tempConfigurationRepository
                            .getConnection();
                    try
                    {
                        File nextUrlFile = new File(nextLocation);
                        if(!nextUrlFile.isAbsolute())
                        {
                            // only let people utilise the WEB-INF directory if they
                            // give a relative URL
                            // configFile = new
                            // File(getServletContext().getRealPath("/") +
                            // "/WEB-INF/" + configUrl);
                            nextUrlFile = new File(getWebInfPath() +nextLocation);
                        }
                        
                        if(nextLocation.startsWith("http://") || nextLocation.startsWith("https://"))
                        {
                            if(Settings._INFO)
                            {
                                Settings.log.info("Settings: getting backup configuration from URL: nextLocation="+ nextLocation);
                            }
                            final URL url = new URL(nextLocation);
                            
                            RdfFetcherQueryRunnable nextThread = new RdfFetcherUriQueryRunnable( nextLocation,
                                         configMIMEFormat,
                                         "",
                                         "",
                                         configMIMEFormat,
                                         new QueryBundle() );
                            
                            nextThread.start();
                            
                            try
                            {
                                // effectively attempt to join each of the threads, this loop will complete when they are all completed
                                nextThread.join();
                            }
                            catch( InterruptedException ie )
                            {
                                log.error( "RdfFetchController.fetchRdfForQuery: caught interrupted exception message="+ie.getMessage() );
                                throw ie;
                            }
                            
                            if(nextThread.wasSuccessful)
                            {
                                myRepositoryConnection.add(new java.io.StringReader(nextThread.rawResult), url.toString(),
                                        RDFFormat.forMIMEType(configMIMEFormat));
                                if(Settings._INFO)
                                {
                                    Settings.log.info("Settings: finished getting backup configuration from URL: nextLocation="+ nextLocation);
                                }
                            }
                            else
                            {
                                Settings.log.error("Settings: error getting backup configuration from URL: nextLocation="+ nextLocation+" nextThread.lastException="+nextThread.lastException);
                                
                                // backupNeeded = true;
                                backupFailed = true;
                            }
                        }
                        else
                        {
                            if(Settings._INFO)
                            {
                                Settings.log
                                        .info("Settings: getting backup configuration from file: nextLocation="
                                                + nextLocation
                                                + " nextUrlFile="
                                                + nextUrlFile);
                            }
                            myRepositoryConnection.add(nextUrlFile, baseURI, RDFFormat.forMIMEType(configMIMEFormat));
                            if(Settings._INFO)
                            {
                                Settings.log
                                        .info("Settings: finished getting backup configuration from file: nextLocation="
                                                + nextLocation
                                                + " nextUrlFile="
                                                + nextUrlFile);
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
                
        if(tempConfigurationRepository == null)
        {
            throw new RuntimeException(
            "Settings: failed to initialise the configuration repository");
        }
        else
        {
            Settings.currentConfigurationRepository = tempConfigurationRepository;
        }

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
                Settings.log.info("Settings: found "+Settings.currentConfigurationRepository.getConnection().size()+" statements in model configuration");
            }
            catch(RepositoryException rex)
            {
                Settings.log.error("Settings: could not determine the number of statements in configuration");
            }
        }
        
        return Settings.currentConfigurationRepository;
    }
    
    public static String getString(String key)
    {
        String result = null;
        try
        {
            result = ResourceBundle.getBundle(Settings.BASE_CONFIG_BUNDLE_NAME)
                    .getString(key);
        }
        catch (final Exception ex)
        {
            Settings.log.error(ex, ex);
        }
        return result;
    }
    
    public static boolean isManualRefreshAllowed()
    {
        boolean manualRefresh = Settings.getBooleanPropertyFromConfig("enableManualConfigurationRefresh");
        long timestampDiff = (System.currentTimeMillis() - Settings.initialisedTimestamp);
        long manualRefreshMinimum = Settings.getLongPropertyFromConfig("manualConfigurationMinimumMilliseconds");
        
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
    
    public static boolean isProviderUsedWithProfileList(URI providerUri,
            URI profileIncludeExcludeOrder,
            List<Profile> nextSortedProfileList)
    {
        for(final Profile nextProfile : nextSortedProfileList)
        {
            final int trueResult = nextProfile.usedWithProvider(providerUri,
                    profileIncludeExcludeOrder);
            if(trueResult == ProfileImpl.IMPLICIT_INCLUDE)
            {
                if(Settings._DEBUG)
                {
                    Settings.log
                            .debug("Settings.isProviderUsedWithProfileList: found implicit include for providerUri="
                                    + providerUri
                                    + " profile="
                                    + nextProfile.getKey().stringValue());
                    // log.debug("Settings: Settings.getBooleanPropertyFromConfig("recogniseImplicitProviderInclusions")="+Settings.getBooleanPropertyFromConfig("recogniseImplicitProviderInclusions"));
                }
                
                if(Settings.getBooleanPropertyFromConfig("recogniseImplicitProviderInclusions"))
                {
                    if(Settings._DEBUG)
                    {
                        Settings.log
                                .debug("Settings.isProviderUsedWithProfileList: returning implicit include true for providerUri="
                                        + providerUri
                                        + " profile="
                                        + nextProfile.getKey().stringValue());
                    }
                    return true;
                }
                else if(Settings._DEBUG)
                {
                    Settings.log
                            .debug("Settings.isProviderUsedWithProfileList: implicit include not recognised for providerUri="
                                    + providerUri
                                    + " profile="
                                    + nextProfile.getKey().stringValue());
                }
            }
            else if(trueResult == ProfileImpl.SPECIFIC_INCLUDE)
            {
                if(Settings._DEBUG)
                {
                    Settings.log
                            .debug("Settings.isProviderUsedWithProfileList: returning specific true for providerUri="
                                    + providerUri
                                    + " profile="
                                    + nextProfile.getKey().stringValue());
                }
                return true;
            }
            else if(trueResult == ProfileImpl.SPECIFIC_EXCLUDE)
            {
                if(Settings._DEBUG)
                {
                    Settings.log
                            .debug("Settings.isProviderUsedWithProfileList: returning specific false for providerUri="
                                    + providerUri
                                    + " profile="
                                    + nextProfile.getKey().stringValue());
                }
                return false;
            }
        }
        
        boolean returnValue = (profileIncludeExcludeOrder.equals(ProfileImpl.getExcludeThenIncludeUri()) && Settings.getBooleanPropertyFromConfig("includeNonProfileMatchedProviders"));
        
        if(Settings._DEBUG)
        {
            Settings.log
                    .debug("Settings.isProviderUsedWithProfileList: returning no matches found returnValue="
                            + returnValue
                            + " for providerUri=" + providerUri);
        }
        
        return returnValue;
    }
    
    public static boolean isQueryUsedWithProfileList(URI queryUri,
            URI profileIncludeExcludeOrder,
            List<Profile> nextSortedProfileList)
    {
        for(final Profile nextProfile : nextSortedProfileList)
        {
            final int trueResult = nextProfile.usedWithQuery(queryUri,
                    profileIncludeExcludeOrder);
            if(trueResult == ProfileImpl.IMPLICIT_INCLUDE)
            {
                if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.isQueryUsedWithProfileList: found implicit include for queryUri="
                                    + queryUri + " profile=" + nextProfile.getKey().stringValue());
                    // log.debug("Settings: Settings.getBooleanPropertyFromConfig("recogniseImplicitQueryInclusions")="+Settings.getBooleanPropertyFromConfig("recogniseImplicitQueryInclusions"));
                }
                if(Settings.getBooleanPropertyFromConfig("recogniseImplicitQueryInclusions"))
                {
                    if(Settings._TRACE)
                    {
                        Settings.log
                                .trace("Settings.isQueryUsedWithProfileList: returning implicit include true for queryUri="
                                        + queryUri
                                        + " profile="
                                        + nextProfile.getKey().stringValue());
                    }
                    return true;
                }
                else if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.isQueryUsedWithProfileList: implicit include not recognised for queryUri="
                                    + queryUri + " profile=" + nextProfile.getKey().stringValue());
                }
            }
            else if(trueResult == ProfileImpl.SPECIFIC_INCLUDE)
            {
                if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.isQueryUsedWithProfileList: returning specific true for queryUri="
                                    + queryUri + " profile=" + nextProfile.getKey().stringValue());
                }
                return true;
            }
            else if(trueResult == ProfileImpl.SPECIFIC_EXCLUDE)
            {
                if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.isQueryUsedWithProfileList: returning specific false for queryUri="
                                    + queryUri + " profile=" + nextProfile.getKey().stringValue());
                }
                return false;
            }
        }
        
        boolean returnValue = (profileIncludeExcludeOrder.equals(ProfileImpl.getExcludeThenIncludeUri()) && Settings.getBooleanPropertyFromConfig("includeNonProfileMatchedQueries"));
        
        if(Settings._DEBUG)
        {
            // log.debug("Settings: Settings.getBooleanPropertyFromConfig("includeNonProfileMatchedQueries")="+Settings.getBooleanPropertyFromConfig("includeNonProfileMatchedQueries"));
            Settings.log
                    .debug("Settings.isQueryUsedWithProfileList: returning with no specific or implicit matches found returnValue="
                            + returnValue
                            + " for queryUri=" + queryUri);
        }
        
        return returnValue;
    }
    
    public static boolean isRdfRuleUsedWithProfileList(URI rdfRuleUri,
            URI profileIncludeExcludeOrder,
            Collection<Profile> nextSortedProfileList)
    {
        for(final Profile nextProfile : nextSortedProfileList)
        {
            final int trueResult = nextProfile.usedWithRdfRule(rdfRuleUri,
                    profileIncludeExcludeOrder);
            if(trueResult == ProfileImpl.IMPLICIT_INCLUDE)
            {
                if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.isRdfRuleUsedWithProfileList: found implicit include for rdfRuleUri="
                                    + rdfRuleUri
                                    + " profile="
                                    + nextProfile.getKey().stringValue());
                    // log.debug("Settings: Settings.getBooleanPropertyFromConfig("recogniseImplicitRdfRuleInclusions")="+Settings.getBooleanPropertyFromConfig("recogniseImplicitRdfRuleInclusions"));
                }
                if(Settings.getBooleanPropertyFromConfig("recogniseImplicitRdfRuleInclusions"))
                {
                    if(Settings._TRACE)
                    {
                        Settings.log
                                .trace("Settings.isRdfRuleUsedWithProfileList: returning implicit include true for rdfRuleUri="
                                        + rdfRuleUri
                                        + " profile="
                                        + nextProfile.getKey().stringValue());
                    }
                    return true;
                }
                else if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.isRdfRuleUsedWithProfileList: implicit include not recognised for rdfRuleUri="
                                    + rdfRuleUri
                                    + " profile="
                                    + nextProfile.getKey().stringValue());
                }
            }
            else if(trueResult == ProfileImpl.SPECIFIC_INCLUDE)
            {
                if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.isRdfRuleUsedWithProfileList: returning specific true for rdfRuleUri="
                                    + rdfRuleUri
                                    + " profile="
                                    + nextProfile.getKey().stringValue());
                }
                return true;
            }
            else if(trueResult == ProfileImpl.SPECIFIC_EXCLUDE)
            {
                if(Settings._TRACE)
                {
                    Settings.log
                            .trace("Settings.isRdfRuleUsedWithProfileList: returning specific false for rdfRuleUri="
                                    + rdfRuleUri
                                    + " profile="
                                    + nextProfile.getKey().stringValue());
                }
                return false;
            }
        }
        
        boolean returnValue = (profileIncludeExcludeOrder.equals(ProfileImpl.getExcludeThenIncludeUri()) && Settings.getBooleanPropertyFromConfig("includeNonProfileMatchedRdfRules"));
        
        if(Settings._DEBUG)
        {
            // log.debug("Settings: Settings.getBooleanPropertyFromConfig("includeNonProfileMatchedRdfRules")="+Settings.getBooleanPropertyFromConfig("includeNonProfileMatchedRdfRules"));
            Settings.log
                    .debug("Settings.isRdfRuleUsedWithProfileList: returning no specific or implicit matches found returnValue="
                            + returnValue
                            + " for rdfRuleUri=" + rdfRuleUri);
        }
        
        return returnValue;
    }
    
    public static Collection<String> getStringCollectionPropertiesFromConfig(String key)
    {
        Collection<String> results = new HashSet<String>();
        
        Collection<Value> values = getValueCollectionPropertiesFromConfig(key);
        
        for(Value nextValue : values)
        {
            results.add(Utilities.getUTF8StringValueFromSesameValue(nextValue));
        }
        
        return results;
    }
    
    public static Collection<URI> getURICollectionPropertiesFromConfig(String key)
    {
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

    public static Collection<Statement> getStatementPropertiesFromConfig(String key)
    {
        Collection<Statement> results = new HashSet<Statement>();
        
        try
        {
            Repository webappConfig = getWebAppConfigurationRdf();
            
            final ValueFactory f = webappConfig.getValueFactory();

            // TODO: in future should reform this to accept a full URI as the key so properties outside of the queryall vocabulary can be used for properties
            URI propertyUri = f.createURI(Settings.DEFAULT_ONTOLOGYTERMURI_PREFIX + Settings.DEFAULT_RDF_WEBAPP_CONFIGURATION_NAMESPACE + Settings.DEFAULT_ONTOLOGYTERMURI_SUFFIX + key);
            
            for(String nextConfigUri : WEBAPP_CONFIG_URI_LIST)
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

    private static Collection<Statement> getStatementCollectionPropertiesFromConfig(URI subjectUri, URI propertyUri, Repository nextRepository)
    {
        try
        {
            return Utilities.getStatementsFromRepositoryByPredicateUrisAndSubject(nextRepository, propertyUri, subjectUri);
        }
        catch(Exception ex)
        {
            Settings.log.error("Settings.getStatementCollectionPropertiesFromConfig: error", ex);
        }
        
        return new HashSet<Statement>();
    }


    
    public static Collection<Value> getValueCollectionPropertiesFromConfig(String key)
    {
        Collection<Value> results = new HashSet<Value>();
        
        try
        {
            Repository webappConfig = getWebAppConfigurationRdf();
            
            final ValueFactory f = webappConfig.getValueFactory();

            // TODO: in future should reform this to accept a full URI as the key so properties outside of the queryall vocabulary can be used for properties
            URI propertyUri = f.createURI(Settings.DEFAULT_ONTOLOGYTERMURI_PREFIX + Settings.DEFAULT_RDF_WEBAPP_CONFIGURATION_NAMESPACE + Settings.DEFAULT_ONTOLOGYTERMURI_SUFFIX + key);
            
            for(String nextConfigUri : WEBAPP_CONFIG_URI_LIST)
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

    public static String getStringPropertyFromConfig(String key)
    {
        Collection<String> values = getStringCollectionPropertiesFromConfig(key);
        
        if(values.size() != 1)
        {
            Settings.log.error("Settings.getStringPropertyFromConfig: Did not find a unique result for key="+key+ " values.size()="+values.size());
            throw new RuntimeException("Settings.getStringPropertyFromConfig: Did not find a unique result for key="+key);
        }

        for(String nextValue : values)
        {
            return nextValue;
        }
        
        return "";
    }

    public static boolean getBooleanPropertyFromConfig(String key)
    {
        Collection<Value> values = getValueCollectionPropertiesFromConfig(key);
        
        if(values.size() != 1)
        {
            Settings.log.error("Settings.getBooleanPropertyFromConfig: Did not find a unique result for key="+key+ " values.size()="+values.size());
            throw new RuntimeException("Settings.getBooleanPropertyFromConfig: Did not find a unique result for key="+key);
        }

        for(Value nextValue : values)
        {
            return Utilities.getBooleanFromValue(nextValue);
        }
        
        return false;
    }

    public static long getLongPropertyFromConfig(String key)
    {
        Collection<Value> values = getValueCollectionPropertiesFromConfig(key);
        
        if(values.size() != 1)
        {
            Settings.log.error("Settings.getLongPropertyFromConfig: Did not find a unique result for key="+key+ " values.size()="+values.size());
            throw new RuntimeException("Settings.getLongPropertyFromConfig: Did not find a unique result for key="+key);
        }

        for(Value nextValue : values)
        {
            return Utilities.getLongFromValue(nextValue);
        }
        
        return 0L;
    }
        
    public static int getIntPropertyFromConfig(String key)
    {
        Collection<Value> values = getValueCollectionPropertiesFromConfig(key);
        
        if(values.size() != 1)
        {
            Settings.log.error("Settings.getIntPropertyFromConfig: Did not find a unique result for key="+key+ " values.size()="+values.size());
            throw new RuntimeException("Settings.getIntPropertyFromConfig: Did not find a unique result for key="+key);
        }

        for(Value nextValue : values)
        {
            return Utilities.getIntegerFromValue(nextValue);
        }
        
        return 0;
    }
        
    public static float getFloatPropertyFromConfig(String key)
    {
        Collection<Value> values = getValueCollectionPropertiesFromConfig(key);
        
        if(values.size() != 1)
        {
            Settings.log.error("Settings.getFloatPropertyFromConfig: Did not find a unique result for key="+key+ " values.size()="+values.size());
            throw new RuntimeException("Settings.getFloatPropertyFromConfig: Did not find a unique result for key="+key);
        }

        for(Value nextValue : values)
        {
            return Utilities.getFloatFromValue(nextValue);
        }
        
        return 0.0f;
    }
        
    private static Collection<Value> getValueCollectionPropertiesFromConfig(URI subjectUri, URI propertyUri)
    {
        Collection<Value> cachedResults = getConfigKeyCached(subjectUri, propertyUri);
        Collection<Value> results = new HashSet<Value>();
        
        if(cachedResults != null)
        {
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
    
    private static Collection<Value> getValueCollectionPropertiesFromConfig(URI subjectUri, URI propertyUri, Repository nextRepository)
    {
        Collection<Value> results = new HashSet<Value>();
        
        try
        {
            //Repository webappConfig = getWebAppConfigurationRdf();
            results = Utilities.getValuesFromRepositoryByPredicateUrisAndSubject(nextRepository, propertyUri, subjectUri);
        }
        catch(Exception ex)
        {
            Settings.log.error("Settings.getValueCollectionPropertiesFromConfig: error", ex);
        }
        
        return results;
    }

    private static Collection<Value> getConfigKeyCached(URI subjectKey, URI propertyKey)
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
    
    private static void doConfigKeyCache(URI subjectKey, URI propertyKey, Collection<Value> newObject)
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

    public static boolean runRuleTests(Collection<RuleTest> myRuleTests)
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
                for(final NormalisationRule nextRule : Settings.getNormalisationRulesForUris(nextRuleTest.getRuleUris(), Settings.LOWEST_ORDER_FIRST))
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
                
                for(final NormalisationRule nextRule : Settings.getNormalisationRulesForUris(nextRuleTest.getRuleUris(), Settings.HIGHEST_ORDER_FIRST))
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

    
    public static String getDefaultHostAddress()
    {
        return Settings.getStringPropertyFromConfig("uriPrefix")+Settings.getStringPropertyFromConfig("hostName")+Settings.getStringPropertyFromConfig("uriSuffix");
    }

    public static Pattern getPlainNamespaceAndIdentifierPattern()
    {
        return Pattern.compile(Settings.getStringPropertyFromConfig("plainNamespaceAndIdentifierRegex"));
    }

    public static Pattern getPlainNamespacePattern()
    {
        return Pattern.compile(Settings.getStringPropertyFromConfig("plainNamespaceRegex"));
    }
    
    public static Pattern getTagPattern()
    {
        return Pattern.compile(Settings.getStringPropertyFromConfig("tagPatternRegex"));
    }
    
    public static String getWebInfPath()
    {
        return servletConfig.getServletContext().getRealPath("/")+"WEB-INF/";
    }
}
