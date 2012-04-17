package org.queryall.api.utils;

import java.util.Collections;

import org.openrdf.model.URI;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public enum WebappConfig
{
    /**
     * DO NOT USE: ONLY FOR TESTING.
     * 
     * <br/>
     * 
     * Defaults to false.
     */
    _TEST_BOOLEAN_PROPERTY("http://test.example.org/", "_testBooleanProperty", false),
    
    /**
     * DO NOT USE: ONLY FOR TESTING.
     * 
     * <br/>
     * 
     * Defaults to 2.5f.
     */
    _TEST_FLOAT_PROPERTY("http://test.example.org/", "_testFloatProperty", 2.5f),
    
    /**
     * DO NOT USE: ONLY FOR TESTING.
     * 
     * <br/>
     * 
     * Defaults to 2.
     */
    _TEST_INT_PROPERTY("http://test.example.org/", "_testIntProperty", 2),
    
    /**
     * DO NOT USE: ONLY FOR TESTING.
     * 
     * <br/>
     * 
     * Defaults to 2L.
     */
    _TEST_LONG_PROPERTY("http://test.example.org/", "_testLongProperty", 2L),
    
    /**
     * DO NOT USE: ONLY FOR TESTING.
     * 
     * <br/>
     * 
     * Defaults to Collections.emptyList().
     */
    _TEST_STRING_COLLECTION_PROPERTY("http://test.example.org/", "_testStringCollectionProperty", Collections
            .emptyList()),
    
    /**
     * DO NOT USE: ONLY FOR TESTING.
     * 
     * <br/>
     * 
     * Defaults to "mySampleOnlyTestString".
     */
    _TEST_STRING_PROPERTY("http://test.example.org/", "_testStringProperty", "mySampleOnlyTestString"),
    
    /**
     * DO NOT USE: ONLY FOR TESTING.
     * 
     * <br/>
     * 
     * Defaults to Collections.emptyList().
     */
    _TEST_URI_COLLECTION_PROPERTY("http://test.example.org/", "_testUriCollectionProperty", Collections.emptyList()),
    
    /**
     * DO NOT USE: ONLY FOR TESTING.
     * 
     * <br/>
     * 
     * Defaults to
     * Constants.valueFactory.createURI("http://other.example.org/_testValueForUriProperty").
     */
    _TEST_URI_PROPERTY("http://test.example.org/", "_testUriProperty", Constants.VALUE_FACTORY
            .createURI("http://other.example.org/_testValueForUriProperty")),
    
    ACTIVE_PROFILES("activeProfiles", Collections.emptyList()),
    
    ADMIN_CONFIGURATION_API_VERSION_CLOSING_PREFIX("adminConfigurationApiVersionClosingPrefix", "/"),
    
    ADMIN_CONFIGURATION_API_VERSION_OPENING_PREFIX("adminConfigurationApiVersionOpeningPrefix", ""),
    
    ADMIN_CONFIGURATION_API_VERSION_SUFFIX("adminConfigurationApiVersionSuffix", ""),
    
    ADMIN_CONFIGURATION_HTML_PREFIX("adminConfigurationHtmlPrefix", ""),
    
    ADMIN_CONFIGURATION_HTML_SUFFIX("adminConfigurationHtmlSuffix", "/html"),
    
    ADMIN_CONFIGURATION_JSON_PREFIX("adminConfigurationJsonPrefix", ""),
    
    ADMIN_CONFIGURATION_JSON_SUFFIX("adminConfigurationJsonSuffix", "/json"),
    
    ADMIN_CONFIGURATION_N3_PREFIX("adminConfigurationN3Prefix", ""),
    
    ADMIN_CONFIGURATION_N3_SUFFIX("adminConfigurationN3Suffix", "/n3"),
    
    ADMIN_CONFIGURATION_NQUADS_PREFIX("adminConfigurationNQuadsPrefix", ""),
    
    ADMIN_CONFIGURATION_NQUADS_SUFFIX("adminConfigurationNQuadsSuffix", "/nquads"),
    
    ADMIN_CONFIGURATION_NTRIPLES_PREFIX("adminConfigurationNTriplesPrefix", ""),
    
    ADMIN_CONFIGURATION_NTRIPLES_SUFFIX("adminConfigurationNTriplesSuffix", "/ntriples"),
    
    ADMIN_CONFIGURATION_PREFIX("adminConfigurationPrefix", "configuration/"),
    
    ADMIN_CONFIGURATION_RDFXML_PREFIX("adminConfigurationRdfxmlPrefix", ""),
    
    ADMIN_CONFIGURATION_RDFXML_SUFFIX("adminConfigurationRdfxmlSuffix", "/rdfxml"),
    
    ADMIN_CONFIGURATION_REFRESH_PREFIX("adminConfigurationRefreshPrefix", "refresh"),
    
    ADMIN_URL_PREFIX("adminUrlPrefix", "admin/"),
    
    ADMIN_WEBAPP_CONFIGURATION_PREFIX("adminWebappConfigurationPrefix", "webappconfiguration/"),
    
    ALWAYS_REDIRECT_TO_EXPLICIT_FORMAT_URL("alwaysRedirectToExplicitFormatUrl", false),
    
    APPLICATION_HELP_URL("applicationHelpUrl", "http://sourceforge.net/apps/mediawiki/bio2rdf/"),
    
    ASSUMED_RESPONSE_CONTENT_TYPE("assumedResponseContentType", Constants.APPLICATION_RDF_XML),
    
    BLACKLIST_AUTOMATICALLY_BLACKLIST_CLIENTS("automaticallyBlacklistClients", false),
    
    BLACKLIST_BASE_CLIENT_IP_ADDRESSES("blacklistBaseClientIPAddresses", Collections.emptyList()),
    
    BLACKLIST_BASE_USER_AGENTS("blacklistBaseUserAgents", Collections.emptyList()),
    
    BLACKLIST_CLIENT_MAX_QUERIES_PER_PERIOD("blacklistClientMaxQueriesPerPeriod", 400),
    
    BLACKLIST_MAX_ACCUMULATED_FAILURES("blacklistMaxAccumulatedFailures", 5),
    
    BLACKLIST_MINIMUM_QUERIES_BEFORE_BLACKLIST_RULES("blacklistMinimumQueriesBeforeBlacklistRules", 200),
    
    BLACKLIST_REDIRECT_PAGE("blacklistRedirectPage", "/error/blacklist"),
    
    BLACKLIST_RESET_CLIENT_BLACKLIST_WITH_ENDPOINTS("blacklistResetClientBlacklistWithEndpoints", true),
    
    BLACKLIST_RESET_ENDPOINT_FAILURES_ON_SUCCESS("blacklistResetEndpointFailuresOnSuccess", true),
    
    BLACKLIST_RESET_PERIOD_MILLISECONDS("blacklistResetPeriodMilliseconds", 60000L),
    
    BLACKLIST_ROBOTS_TXT_PERCENTAGE("blacklistPercentageOfRobotTxtQueriesBeforeAutomatic", 0.75f),
    
    BLANK_TITLE("blankTitle", "(No title)"),
    
    COMMENT_PROPERTIES("titleProperties", Collections.emptyList()),
    
    CONNECT_TIMEOUT("connectTimeout", 3000),
    
    CONVERT_ALTERNATE_NAMESPACE_PREFIXES_TO_PREFERRED("convertAlternateNamespacePrefixesToPreferred", true),
    
    DEFAULT_ACCEPT_HEADER("defaultAcceptHeader", "application/rdf+xml, text/rdf+n3"),
    
    DEFAULT_SEPARATOR("defaultSeparator", ":"),
    
    ENABLE_PERIODIC_CONFIGURATION_REFRESH("enablePeriodicConfigurationRefresh", true),
    
    ERROR_TEMPLATE("errorTemplate", "error.vm"),
    
    HARDCODED_REQUEST_CONTEXT("hardcodedRequestContext", "/"),
    
    HARDCODED_REQUEST_HOSTNAME("hardcodedRequestHostname", "/"),
    
    /**
     * The hostname to use for queries.
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
    
    IMAGE_PROPERTIES("imageProperties", Collections.emptyList()),
    
    INCLUDE_NON_PROFILE_MATCHED_PROVIDERS("includeNonProfileMatchedProviders", true),
    
    INCLUDE_NON_PROFILE_MATCHED_QUERIES("includeNonProfileMatchedQueries", true),
    
    INCLUDE_NON_PROFILE_MATCHED_RDFRULES("includeNonProfileMatchedRdfRules", true),
    
    INDEX_BANNER_IMAGE_PATH("indexBannerImagePath", "static/includes-images/merged-bio2rdf-banner.jpg"),
    
    INDEX_PAGE_SCRIPTS("indexPageScripts", Collections.emptyList()),
    
    INDEX_PAGE_SCRIPTS_LOCAL("indexPageScriptsLocal", Collections.emptyList()),
    
    INDEX_PAGE_STYLESHEETS("indexPageStylesheets", Collections.emptyList()),
    
    INDEX_PAGE_STYLESHEETS_LOCAL("indexPageStylesheetsLocal", Collections.emptyList()),
    
    INDEX_PROJECT_IMAGE_PATH("indexProjectImagePath", "static/includes-images/Bio2RDF.jpg"),
    
    INDEX_TEMPLATE("indexTemplate", "default-index.vm"),
    
    JSON_URL_PREFIX("jsonUrlPrefix", "json/"),
    
    JSON_URL_SUFFIX("jsonUrlSuffix", ""),
    
    N3_URL_PREFIX("n3UrlPrefix", "n3/"),
    
    N3_URL_SUFFIX("n3UrlSuffix", ""),
    
    NQUADS_URL_PREFIX("nquadsUrlPrefix", "nquads/"),
    
    NQUADS_URL_SUFFIX("nquadsUrlSuffix", ""),
    
    NTRIPLES_URL_PREFIX("ntriplesUrlPrefix", "ntriples/"),
    
    NTRIPLES_URL_SUFFIX("ntriplesUrlSuffix", ""),
    
    PAGEOFFSET_INDIVIDUAL_QUERY_LIMIT("pageoffsetIndividualQueryLimit", 500),
    
    PAGEOFFSET_MAX_VALUE("pageoffsetMaxValue", 20),
    
    PAGEOFFSET_ONLY_SHOW_FOR_NSID("pageoffsetOnlyShowForNsId", true),
    
    PAGEOFFSET_QUICK_QUERY_LIMIT("pageoffsetQuickQueryLimit", 20),
    
    PAGEOFFSET_URL_CLOSING_PREFIX("pageoffsetUrlClosingPrefix", "/"),
    
    PAGEOFFSET_URL_OPENING_PREFIX("pageoffsetUrlOpeningPrefix", "pageoffset"),
    
    PAGEOFFSET_URL_SUFFIX("pageoffsetUrlSuffix", ""),
    
    PERIODIC_CONFIGURATION_REFRESH_MILLISECONDS("periodicConfigurationMilliseconds", 60000L),
    
    PLAIN_NAMESPACE_AND_IDENTIFIER_REGEX("plainNamespaceAndIdentifierRegex", "^([\\w-]+):(.+)$"),
    
    PLAIN_NAMESPACE_REGEX("plainNamespaceRegex", "^([\\w-]+)$"),
    
    PREFERRED_DISPLAY_CONTENT_TYPE("preferredDisplayContentType", "application/rdf+xml"),
    
    PROJECT_HOME_URI("projectHomeUri", "http://bio2rdf.org/"),
    
    PROJECT_HOME_URL("projectHomeUrL", "http://bio2rdf.org/"),
    
    PROJECT_NAME("projectName", "Bio2RDF"),
    
    QUERYPLAN_URL_PREFIX("queryplanUrlPrefix", "queryplan/"),
    
    QUERYPLAN_URL_SUFFIX("queryplanUrlSuffix", ""),
    
    RDFXML_URL_PREFIX("rdfXmlUrlPrefix", "rdfxml/"),
    
    RDFXML_URL_SUFFIX("rdfXmlUrlSuffix", ""),
    
    READ_TIMEOUT("readTimeout", 30000),
    
    RECOGNISE_IMPLICIT_PROVIDER_INCLUSIONS("recogniseImplicitProviderInclusions", true),
    
    RECOGNISE_IMPLICIT_QUERY_INCLUSIONS("recogniseImplicitQueryInclusions", true),
    
    RECOGNISE_IMPLICIT_RDFRULE_INCLUSIONS("recogniseImplicitRdfRuleInclusions", true),
    
    /**
     * The HTTP Status code to use when redirecting from a format-agnostic, content negotiated URL
     * to a URL that explicitly contains the format, as required by some Linked Data applications.
     * 
     * <br/>
     * Defaults to "303".
     * 
     * <br/>
     * NOTE: This may also be set to 301, 302, or 307 for similar redirect behaviour.
     */
    REDIRECT_TO_EXPLICIT_FORMAT_HTTP_CODE("redirectToExplicitFormatHttpCode", 303),
    
    RESULTS_TEMPLATE("resultsTemplate", "page.vm"),
    
    ROBOT_HELP_URL("robotHelpUrl", "https://sourceforge.net/apps/mediawiki/bio2rdf/index.php?title=RobotHelp"),
    
    SHORTCUT_ICON_PATH("shortcutIconPath", "static/includes-images/favicon.ico"),
    
    TAG_PATTERN_REGEX("tagPatternRegex", ".*(\\$\\{[\\w_-]+\\}).*"),
    
    TITLE_PROPERTIES("titleProperties", Collections.emptyList()),
    
    TRY_ALL_ENDPOINTS_FOR_EACH_PROVIDER("tryAllEndpointsForEachProvider", true),
    
    UNKNOWN_NAMESPACE_HTTP_RESPONSE_CODE("unknownNamespaceHttpResponseCode", 404),
    
    UNKNOWN_NAMESPACE_STATIC_ADDITIONS("unknownNamespaceStaticAdditions", Collections.emptyList()),
    
    UNKNOWN_QUERY_HTTP_RESPONSE_CODE("unknownQueryHttpResponseCode", 400),
    
    UNKNOWN_QUERY_STATIC_ADDITIONS("unknownQueryStaticAdditions", Collections.emptyList()),
    
    URI_PREFIX("uriPrefix", "http://"),
    
    /**
     * The URI suffix for constructing URIs for this webapp.
     * 
     * <br/>
     * Defaults to "/".
     * 
     * <br/>
     * NOTE: This should be overriden in cases where the webapp is not hosted at the root path.
     */
    URI_SUFFIX("uriSuffix", "/"),
    
    USE_HARDCODED_REQUEST_CONTEXT("useHardcodedRequestContext", false),
    
    USE_HARDCODED_REQUEST_HOSTNAME("useHardcodedRequestHostname", false),
    
    USE_REQUEST_CACHE("useRequestCache", true),
    
    /**
     * The user agent to use in the HTTP User-Agent header.
     * 
     * <br/>
     * Defaults to "queryall".
     * 
     * <br/>
     * NOTE: This may be overriden to indicate the application using this library.
     */
    USER_AGENT("userAgent", "queryall"),
    
    WHITELIST_BASE_CLIENT_IP_ADDRESSES("whitelistBaseClientIPAddresses", Collections.emptyList());
    
    public static WebappConfig valueOf(final URI keyUri)
    {
        if(keyUri == null)
        {
            throw new IllegalArgumentException("key cannot be null");
        }
        
        for(final WebappConfig nextConfig : WebappConfig.values())
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
     * Sets up a config key using the default QueryAllNamespaces.WEBAPPCONFIG.getBaseURI() namespace
     * with the given key appended to it
     * 
     * @param nextKey
     * @param nextDefaultValue
     */
    WebappConfig(final String nextKey, final Object nextDefaultValue)
    {
        this.namespace = QueryAllNamespaces.WEBAPPCONFIG.getBaseURI();
        this.defaultValue = nextDefaultValue;
        this.key = nextKey;
        this.uriValue = Constants.VALUE_FACTORY.createURI(this.namespace, nextKey);
    }
    
    WebappConfig(final String nextNamespace, final String nextKey, final Object nextDefaultValue)
    {
        this.defaultValue = nextDefaultValue;
        this.key = nextKey;
        this.namespace = nextNamespace;
        this.uriValue = Constants.VALUE_FACTORY.createURI(nextNamespace, nextKey);
    }
    
    WebappConfig(final URI uri, final Object nextDefaultValue)
    {
        this.defaultValue = nextDefaultValue;
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
    
    /**
     * 
     * @return False if the default value is a collection, or more generally, any Iterable,
     *         indicating that the property can contain multiple values simultaneously, and return
     *         true to overwrite all subsequent properties for non-collection properties
     */
    public boolean overwrite()
    {
        return !(this.defaultValue instanceof Iterable<?>);
    }
}
