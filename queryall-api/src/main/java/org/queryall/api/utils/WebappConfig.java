package org.queryall.api.utils;

import java.util.Collections;

import org.openrdf.model.URI;

/**
 * TODO: Create test for WebappConfig
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 *
 */
public enum WebappConfig
{
    APPLICATION_HELP_URL("applicationHelpUrl", "http://sourceforge.net/apps/mediawiki/bio2rdf/"),
    
    BLACKLIST_AUTOMATICALLY_BLACKLIST_CLIENTS("automaticallyBlacklistClients", false),
    
    BLACKLIST_CLIENT_MAX_QUERIES_PER_PERIOD("blacklistClientMaxQueriesPerPeriod", 400),
    
    BLACKLIST_MAX_ACCUMULATED_FAILURES("blacklistMaxAccumulatedFailures", 5),
    
    BLACKLIST_MINIMUM_QUERIES_BEFORE_BLACKLIST_RULES("blacklistMinimumQueriesBeforeBlacklistRules", 200),
    
    BLACKLIST_RESET_CLIENT_BLACKLIST_WITH_ENDPOINTS("blacklistResetClientBlacklistWithEndpoints", true),
    
    BLACKLIST_RESET_ENDPOINT_FAILURES_ON_SUCCESS("blacklistResetEndpointFailuresOnSuccess", true),
    
    BLACKLIST_RESET_PERIOD_MILLISECONDS("blacklistResetPeriodMilliseconds", 60000L),
    
    BLACKLIST_ROBOTS_TXT_PERCENTAGE("blacklistPercentageOfRobotTxtQueriesBeforeAutomatic", 0.75f), 
    
    DEFAULT_SEPARATOR("defaultSeparator", ":"), 
    
    HARDCODED_REQUEST_CONTEXT("useHardcodedRequestContext", "/"), 
    
    HARDCODED_REQUEST_HOSTNAME("useHardcodedRequestHostname", "/"), 
    
    /**
     * The hostname to use for queries
     * 
     * <br/>
     * Defaults to "bio2rdf.org"
     * 
     * <br/>
     * NOTE: The hostname should always be overriden in configuration files
     */
    HOST_NAME("hostName", "bio2rdf.org"),
    
    HTML_URL_PREFIX("htmlUrlPrefix", "page/"), 
    
    HTML_URL_SUFFIX("htmlUrlSuffix", ""), 
    
    JSON_URL_PREFIX("jsonUrlPrefix", "json/"), 
    
    JSON_URL_SUFFIX("jsonUrlSuffix", ""), 
    
    RDFXML_URL_PREFIX("rdfXmlUrlPrefix", "rdfxml/"), 
    
    RDFXML_URL_SUFFIX("rdfXmlUrlSuffix", ""), 
    
    N3_URL_PREFIX("n3UrlPrefix", "n3/"), 
    
    N3_URL_SUFFIX("n3UrlSuffix", ""), 
    
    INDEX_BANNER_IMAGE_PATH("indexBannerImagePath", "static/includes-images/merged-bio2rdf-banner.jpg"), 
    
    INDEX_PAGE_SCRIPTS("indexPageScripts", ""),
    
    INDEX_PAGE_SCRIPTS_LOCAL("indexPageScriptsLocal", ""),

    INDEX_PAGE_STYLESHEETS("indexPageStylesheets", ""), 
    
    INDEX_PAGE_STYLESHEETS_LOCAL("indexPageStylesheetsLocal", ""), 
    
    INDEX_PROJECT_IMAGE_PATH("indexProjectImagePath", "static/includes-images/Bio2RDF.jpg"), 
    
    PLAIN_NAMESPACE_AND_IDENTIFIER_REGEX("plainNamespaceAndIdentifierRegex", "^([\\w-]+):(.+)$"), 
    
    PLAIN_NAMESPACE_REGEX("plainNamespaceRegex", "^([\\w-]+)$"), 
    
    PROJECT_HOME_URI("projectHomeUri", "http://bio2rdf.org/"), 
    
    PROJECT_HOME_URL("projectHomeUrL", "http://bio2rdf.org/"), 
    
    PROJECT_NAME("projectName", "Bio2RDF"), 

    SHORTCUT_ICON_PATH("shortcutIconPath" , "static/includes-images/favicon.ico"), 
    
    TAG_PATTERN_REGEX("tagPatternRegex", ".*(\\$\\{[\\w_-]+\\}).*"), 

    TITLE_PROPERTIES("titleProperties", ""), 
    
    URI_PREFIX("uriPrefix", "http://"), 
    
    /**
     * The URI suffix for constructing URIs for this webapp
     * 
     * <br/>
     * Defaults to "/"
     * 
     * <br/>
     * NOTE: This should be overriden in cases where the webapp is not hosted at the root path
     */
    URI_SUFFIX("uriSuffix", "/"), 

    USE_HARDCODED_REQUEST_CONTEXT("useHardcodedRequestContext", false), 

    USE_HARDCODED_REQUEST_HOSTNAME("useHardcodedRequestHostname", false), 

    USER_AGENT("userAgent", "queryall"), 
    
    INDEX_TEMPLATE("indexTemplate", "default-index.vm"), 
    
    
    ERROR_TEMPLATE("errorTemplate", "error.vm"), 
    
    RESULTS_TEMPLATE("resultsTemplate", "page.vm"), 
    
    PAGEOFFSET_URL_OPENING_PREFIX("pageoffsetUrlOpeningPrefix", "pageoffset"), 
    
    PAGEOFFSET_URL_CLOSING_PREFIX("pageoffsetUrlClosingPrefix", "/"), 
    
    PAGEOFFSET_URL_SUFFIX("pageoffsetUrlSuffix", ""), 
    
    PAGEOFFSET_ONLY_SHOW_FOR_NSID("pageoffsetOnlyShowForNsId", true), 
    
    PAGEOFFSET_MAX_VALUE("pageoffsetMaxValue", 20), 
    
    PAGEOFFSET_INDIVIDUAL_QUERY_LIMIT("pageoffsetIndividualQueryLimit", 500), 
    
    BLANK_TITLE("blankTitle", ""), 
    
    COMMENT_PROPERTIES("titleProperties", ""), 
    
    IMAGE_PROPERTIES("imageProperties", ""), 
    
    UNKNOWN_NAMESPACE_HTTP_RESPONSE_CODE("unknownNamespaceHttpResponseCode", 404), 
    
    UNKNOWN_QUERY_HTTP_RESPONSE_CODE("unknownQueryHttpResponseCode", 400), 
    
    ACTIVE_PROFILES("activeProfiles", ""), 
    
    BLACKLIST_REDIRECT_PAGE("blacklistRedirectPage", "/error/blacklist"), 
    
    PREFERRED_DISPLAY_CONTENT_TYPE("preferredDisplayContentType", "application/rdf+xml"), 
    
    RECOGNISE_IMPLICIT_PROVIDER_INCLUSIONS("recogniseImplicitProviderInclusions", true), 
    
    INCLUDE_NON_PROFILE_MATCHED_PROVIDERS("includeNonProfileMatchedProviders", true),
    
    RECOGNISE_IMPLICIT_QUERY_INCLUSIONS("recogniseImplicitQueryInclusions", true), 
    
    INCLUDE_NON_PROFILE_MATCHED_QUERIES("includeNonProfileMatchedQueries", true),
    
    RECOGNISE_IMPLICIT_RDFRULE_INCLUSIONS("recogniseImplicitRdfRuleInclusions", true), 
    
    INCLUDE_NON_PROFILE_MATCHED_RDFRULES("includeNonProfileMatchedRdfRules", true), 
    
    ENABLE_PERIODIC_CONFIGURATION_REFRESH("enablePeriodicConfigurationRefresh", true), 
    
    PERIODIC_CONFIGURATION_REFRESH_MILLISECONDS("periodicConfigurationMilliseconds", 60000L),
    
    /**
     * DO NOT USE: ONLY FOR TESTING
     * 
     * <br/>
     * 
     * Defaults to 2.5f
     */
    _TEST_FLOAT_PROPERTY("http://test.example.org/", "_testFloatProperty", 2.5f),
    
    /**
     * DO NOT USE: ONLY FOR TESTING
     * 
     * <br/>
     * 
     * Defaults to 2
     */
    _TEST_INT_PROPERTY("http://test.example.org/", "_testIntProperty", 2),
    
    /**
     * DO NOT USE: ONLY FOR TESTING
     * 
     * <br/>
     * 
     * Defaults to 2L
     */
    _TEST_LONG_PROPERTY("http://test.example.org/", "_testLongProperty", 2L), 
    
    
    /**
     * DO NOT USE: ONLY FOR TESTING
     * 
     * <br/>
     * 
     * Defaults to false
     */
    _TEST_BOOLEAN_PROPERTY("http://test.example.org/", "_testBooleanProperty", false), 
    
    
    /**
     * DO NOT USE: ONLY FOR TESTING
     * 
     * <br/>
     * 
     * Defaults to Collections.emptyList()
     */
    _TEST_URI_COLLECTION_PROPERTY("http://test.example.org/", "_testUriCollectionProperty", Collections.emptyList()), 
    
    /**
     * DO NOT USE: ONLY FOR TESTING
     * 
     * <br/>
     * 
     * Defaults to Collections.emptyList()
     */
    _TEST_STRING_COLLECTION_PROPERTY("http://test.example.org/", "_testStringCollectionProperty", Collections.emptyList()), 
    
    /**
     * DO NOT USE: ONLY FOR TESTING
     * 
     * <br/>
     * 
     * Defaults to Constants.valueFactory.createURI("http://other.example.org/_testValueForUriProperty")
     */
    _TEST_URI_PROPERTY("http://test.example.org/", "_testUriProperty", Constants.valueFactory.createURI("http://other.example.org/_testValueForUriProperty")), 
    
    
    /**
     * DO NOT USE: ONLY FOR TESTING
     * 
     * <br/>
     * 
     * Defaults to "mySampleOnlyTestString"
     */
    _TEST_STRING_PROPERTY("http://test.example.org/", "_testStringProperty", "mySampleOnlyTestString"), 
    
    
    
    
    ;
    
    public static WebappConfig valueOf(URI keyUri)
    {
        if(keyUri == null)
        {
            throw new IllegalArgumentException("key cannot be null");
        }
        
        for(WebappConfig nextConfig : WebappConfig.values())
        {
            if(nextConfig.getUri().equals(keyUri))
            {
                return nextConfig;
            }
        }
        
        return null;
    }
    
    private Object defaultValue;
    private String key;
    private String namespace;
    private URI uriValue;
    
    /**
     * Sets up a config key using the default QueryAllNamespaces.WEBAPPCONFIG.getBaseURI() namespace with the given key appended to it
     * 
     * @param key
     * @param defaultValue
     */
    WebappConfig(String key, Object defaultValue)
    {
        this.namespace = QueryAllNamespaces.WEBAPPCONFIG.getBaseURI();
        this.defaultValue = defaultValue;
        this.key = key;
        this.uriValue = Constants.valueFactory.createURI(namespace, key);
    }
    
    WebappConfig(String namespace, String key, Object defaultValue)
    {
        this.defaultValue = defaultValue;
        this.key = key;
        this.namespace = namespace;
        this.uriValue = Constants.valueFactory.createURI(namespace, key);
    }

    WebappConfig(URI uri, Object defaultValue)
    {
        this.defaultValue = defaultValue;
        this.key = uri.getLocalName();
        this.namespace = uri.getNamespace();
        this.uriValue = uri;
    }
    
    public Object getDefaultValue()
    {
        return this.defaultValue;
    }
    
    public String getKey()
    {
        return this.key;
    }
    
    public String getNamespace()
    {
        return this.namespace;
    }
    
    public URI getUri()
    {
        return this.uriValue;
    }
}
