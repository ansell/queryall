package org.queryall.api.utils;

import org.openrdf.model.URI;

public enum WebappConfig
{
    APPLICATION_HELP_URL(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "applicationHelpUrl", "http://sourceforge.net/apps/mediawiki/bio2rdf/"),
    
    BLACKLIST_AUTOMATICALLY_BLACKLIST_CLIENTS(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "automaticallyBlacklistClients", false),
    
    BLACKLIST_CLIENT_MAX_QUERIES_PER_PERIOD(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "blacklistClientMaxQueriesPerPeriod", 400),
    
    BLACKLIST_MAX_ACCUMULATED_FAILURES(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "blacklistMaxAccumulatedFailures", 5),
    
    BLACKLIST_MINIMUM_QUERIES_BEFORE_BLACKLIST_RULES(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "blacklistMinimumQueriesBeforeBlacklistRules", 200),
    
    BLACKLIST_RESET_CLIENT_BLACKLIST_WITH_ENDPOINTS(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "blacklistResetClientBlacklistWithEndpoints", true),
    
    BLACKLIST_RESET_ENDPOINT_FAILURES_ON_SUCCESS(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "blacklistResetEndpointFailuresOnSuccess", true),
    
    BLACKLIST_RESET_PERIOD_MILLISECONDS(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "blacklistResetPeriodMilliseconds", 60000L),
    
    BLACKLIST_ROBOTS_TXT_PERCENTAGE(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "blacklistPercentageOfRobotTxtQueriesBeforeAutomatic", 0.75f), 
    
    DEFAULT_SEPARATOR(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "defaultSeparator", ":"), 
    
    HARDCODED_REQUEST_CONTEXT(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "useHardcodedRequestContext", "/"), 
    
    HARDCODED_REQUEST_HOSTNAME(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "useHardcodedRequestHostname", "/"), 
    
    /**
     * The hostname to use for queries
     * 
     * <br/>
     * Defaults to "bio2rdf.org"
     * 
     * <br/>
     * NOTE: The hostname should always be overriden in configuration files
     */
    HOST_NAME(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "hostName", "bio2rdf.org"),
    
    HTML_URL_PREFIX(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "htmlUrlPrefix", "page/"), 
    
    HTML_URL_SUFFIX(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "htmlUrlSuffix", ""), 
    
    JSON_URL_PREFIX(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "jsonUrlPrefix", "json/"), 
    
    JSON_URL_SUFFIX(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "jsonUrlSuffix", ""), 
    
    RDFXML_URL_PREFIX(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "rdfXmlUrlPrefix", "rdfxml/"), 
    
    RDFXML_URL_SUFFIX(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "rdfXmlUrlSuffix", ""), 
    
    N3_URL_PREFIX(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "n3UrlPrefix", "n3/"), 
    
    N3_URL_SUFFIX(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "n3UrlSuffix", ""), 
    
    INDEX_BANNER_IMAGE_PATH(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "indexBannerImagePath", "static/includes-images/merged-bio2rdf-banner.jpg"), 
    
    INDEX_PAGE_SCRIPTS(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "indexPageScripts", ""),
    
    INDEX_PAGE_SCRIPTS_LOCAL(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "indexPageScriptsLocal", ""),

    INDEX_PAGE_STYLESHEETS(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "indexPageStylesheets", ""), 
    
    INDEX_PAGE_STYLESHEETS_LOCAL(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "indexPageStylesheetsLocal", ""), 
    
    INDEX_PROJECT_IMAGE_PATH(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "indexProjectImagePath", "static/includes-images/Bio2RDF.jpg"), 
    
    PLAIN_NAMESPACE_AND_IDENTIFIER_REGEX(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "plainNamespaceAndIdentifierRegex", "^([\\w-]+):(.+)$"), 
    
    PLAIN_NAMESPACE_REGEX(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "plainNamespaceRegex", "^([\\w-]+)$"), 
    
    PROJECT_HOME_URI(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "projectHomeUri", "http://bio2rdf.org/"), 
    
    PROJECT_HOME_URL(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "projectHomeUrL", "http://bio2rdf.org/"), 
    
    PROJECT_NAME(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "projectName", "Bio2RDF"), 

    SHORTCUT_ICON_PATH(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "shortcutIconPath" , "static/includes-images/favicon.ico"), 
    
    TAG_PATTERN_REGEX(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "tagPatternRegex", ".*(\\$\\{[\\w_-]+\\}).*"), 

    TITLE_PROPERTIES(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "titleProperties", ""), 
    
    URI_PREFIX(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "uriPrefix", "http://"), 
    
    /**
     * The URI suffix for constructing URIs for this webapp
     * 
     * <br/>
     * Defaults to "/"
     * 
     * <br/>
     * NOTE: This should be overriden in cases where the webapp is not hosted at the root path
     */
    URI_SUFFIX(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "uriSuffix", "/"), 

    USE_HARDCODED_REQUEST_CONTEXT(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "useHardcodedRequestContext", false), 

    USE_HARDCODED_REQUEST_HOSTNAME(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "useHardcodedRequestHostname", false), 

    USER_AGENT(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "userAgent", "queryall"), 
    
    INDEX_TEMPLATE(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "indexTemplate", "default-index.vm"), 
    
    
    ERROR_TEMPLATE(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "errorTemplate", "error.vm"), 
    
    RESULTS_TEMPLATE(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "resultsTemplate", "page.vm"), 
    
    PAGEOFFSET_URL_OPENING_PREFIX(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "pageoffsetUrlOpeningPrefix", "pageoffset"), 
    
    PAGEOFFSET_URL_CLOSING_PREFIX(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "pageoffsetUrlClosingPrefix", "/"), 
    
    PAGEOFFSET_URL_SUFFIX(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "pageoffsetUrlSuffix", ""), 
    
    PAGEOFFSET_ONLY_SHOW_FOR_NSID(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "pageoffsetOnlyShowForNsId", true), 
    
    PAGEOFFSET_MAX_VALUE(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "pageoffsetMaxValue", 20), 
    
    PAGEOFFSET_INDIVIDUAL_QUERY_LIMIT(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "pageoffsetIndividualQueryLimit", 500), 
    
    BLANK_TITLE(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "blankTitle", ""), 
    
    COMMENT_PROPERTIES(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "titleProperties", ""), 
    
    IMAGE_PROPERTIES(QueryAllNamespaces.WEBAPPCONFIG.getBaseURI(), "imageProperties", ""),
    
    
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
