package org.queryall.servlets.queryparsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.utils.Constants;
import org.queryall.utils.Settings;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses query options out of a query string
 */

public class ConfigurationQueryOptions
{
    public static final Logger log = LoggerFactory.getLogger(ConfigurationQueryOptions.class);
    public static final boolean _TRACE = ConfigurationQueryOptions.log.isTraceEnabled();
    public static final boolean _DEBUG = ConfigurationQueryOptions.log.isDebugEnabled();
    public static final boolean _INFO = ConfigurationQueryOptions.log.isInfoEnabled();
    
    // private Settings localSettings = localSettings;
    
    private boolean _adminPrefixMatch = false;
    private boolean _adminBasicWebappConfigurationMatch = false;
    private boolean _adminConfigurationMatch = false;
    private boolean _isRefresh = false;
    
    private boolean _hasExplicitFormat = false;
    private String _chosenFormat = "";
    private boolean _hasExplicitApiVersionValue = false;
    private int _apiVersion = -1;
    private boolean _isPlainNamespaceAndIdentifier = false;
    
    private String parsedRequestString = "";
    private QueryAllConfiguration localSettings;
    private boolean isRootContext = false;
    
    public ConfigurationQueryOptions(final String requestUri, final String contextPath,
            final QueryAllConfiguration localSettings)
    {
        this._apiVersion = Settings.CONFIG_API_VERSION;
        
        String requestString = requestUri;
        this.localSettings = localSettings;
        
        if(requestString == null)
        {
            requestString = "";
            ConfigurationQueryOptions.log.error("ConfigurationQueryOptions: requestString was null");
        }
        
        if(contextPath.equals(""))
        {
            this.isRootContext = true;
        }
        else
        {
            this.isRootContext = false;
        }
        
        if(!this.isRootContext)
        {
            if(requestString.startsWith(contextPath))
            {
                if(ConfigurationQueryOptions._DEBUG)
                {
                    ConfigurationQueryOptions.log.debug("requestUri before removing contextPath requestString="
                            + requestString);
                }
                requestString = requestString.substring(contextPath.length());
                if(ConfigurationQueryOptions._DEBUG)
                {
                    ConfigurationQueryOptions.log.debug("removed contextPath from requestUri contextPath="
                            + contextPath + " requestString=" + requestString);
                }
            }
        }
        
        if(requestString.startsWith("/"))
        {
            if(ConfigurationQueryOptions._DEBUG)
            {
                ConfigurationQueryOptions.log.debug("requestString=" + requestString);
            }
            requestString = requestString.substring(1);
            if(ConfigurationQueryOptions._DEBUG)
            {
                ConfigurationQueryOptions.log.debug("requestString=" + requestString);
            }
        }
        
        requestString = this.parseForAdminPrefix(requestString);
        
        if(this._adminPrefixMatch)
        {
            requestString = this.parseForRefresh(requestString);
            
            if(!this._isRefresh)
            {
                requestString = this.parseForAdminConfiguration(requestString);
                
                if(this._adminConfigurationMatch || this._adminBasicWebappConfigurationMatch)
                {
                    requestString = this.parseForApiVersion(requestString);
                    
                    requestString = this.parseForAdminFormat(requestString);
                }
            }
        }
        else
        {
            requestString = this.parseForNsIdFormat(requestString);
            
            if(StringUtils.isPlainNamespaceAndIdentifier(requestString, localSettings))
            {
                this._isPlainNamespaceAndIdentifier = true;
            }
        }
        
        this.parsedRequestString = requestString;
    }
    
    public boolean containsAdminBasicWebappConfiguration()
    {
        return this._adminBasicWebappConfigurationMatch;
    }
    
    public boolean containsAdminConfiguration()
    {
        return this._adminConfigurationMatch;
    }
    
    public boolean containsExplicitApiVersion()
    {
        return this._hasExplicitApiVersionValue;
    }
    
    public boolean containsExplicitFormat()
    {
        return this._hasExplicitFormat;
    }
    
    public int getApiVersion()
    {
        return this._apiVersion;
    }
    
    public String getExplicitFormat()
    {
        return this._chosenFormat;
    }
    
    public String getParsedRequest()
    {
        return this.parsedRequestString;
    }
    
    public boolean isPlainNamespaceAndIdentifier()
    {
        return this._isPlainNamespaceAndIdentifier;
    }
    
    public boolean isRefresh()
    {
        return this._isRefresh;
    }
    
    private boolean matchesPrefixAndSuffix(final String nextString, final String nextPrefix, final String nextSuffix)
    {
        return nextString.startsWith(nextPrefix) && nextString.endsWith(nextSuffix)
                && nextString.length() >= (nextPrefix.length() + nextSuffix.length());
    }
    
    private String parseForAdminConfiguration(String requestString)
    {
        final String adminConfigurationPrefix =
                this.localSettings.getStringProperty("adminConfigurationPrefix", "configuration/");
        
        final String adminWebappConfigurationPrefix =
                this.localSettings.getStringProperty("adminWebappConfigurationPrefix", "webappconfiguration/");
        
        if(this.matchesPrefixAndSuffix(requestString, adminConfigurationPrefix, ""))
        {
            requestString = this.takeOffPrefixAndSuffix(requestString, adminConfigurationPrefix, "");
            
            this._adminConfigurationMatch = true;
        }
        else if(this.matchesPrefixAndSuffix(requestString, adminWebappConfigurationPrefix, ""))
        {
            requestString = this.takeOffPrefixAndSuffix(requestString, adminWebappConfigurationPrefix, "");
            
            this._adminBasicWebappConfigurationMatch = true;
        }
        
        return requestString;
    }
    
    private String parseForAdminFormat(String requestString)
    {
        final String adminConfigurationHtmlPrefix =
                this.localSettings.getStringProperty("adminConfigurationHtmlPrefix", "");
        final String adminConfigurationHtmlSuffix =
                this.localSettings.getStringProperty("adminConfigurationHtmlSuffix", "/html");
        final String adminConfigurationRdfxmlPrefix =
                this.localSettings.getStringProperty("adminConfigurationRdfxmlPrefix", "");
        final String adminConfigurationRdfxmlSuffix =
                this.localSettings.getStringProperty("adminConfigurationRdfxmlSuffix", "/rdfxml");
        final String adminConfigurationN3Prefix =
                this.localSettings.getStringProperty("adminConfigurationN3Prefix", "");
        final String adminConfigurationN3Suffix =
                this.localSettings.getStringProperty("adminConfigurationN3Suffix", "/n3");
        final String adminConfigurationJsonPrefix =
                this.localSettings.getStringProperty("adminConfigurationJsonPrefix", "");
        final String adminConfigurationJsonSuffix =
                this.localSettings.getStringProperty("adminConfigurationJsonSuffix", "/json");
        final String adminConfigurationNTriplesPrefix =
                this.localSettings.getStringProperty("adminConfigurationNTriplesPrefix", "");
        final String adminConfigurationNTriplesSuffix =
                this.localSettings.getStringProperty("adminConfigurationNTriplesSuffix", "/ntriples");
        final String adminConfigurationNQuadsPrefix =
                this.localSettings.getStringProperty("adminConfigurationNQuadsPrefix", "");
        final String adminConfigurationNQuadsSuffix =
                this.localSettings.getStringProperty("adminConfigurationNQuadsSuffix", "/nquads");
        
        if(this.matchesPrefixAndSuffix(requestString, adminConfigurationHtmlPrefix, adminConfigurationHtmlSuffix))
        {
            this._hasExplicitFormat = true;
            this._chosenFormat = Constants.TEXT_HTML;
            if(ConfigurationQueryOptions._DEBUG)
            {
                ConfigurationQueryOptions.log.debug("html: requestString=" + requestString);
            }
            requestString =
                    this.takeOffPrefixAndSuffix(requestString, adminConfigurationHtmlPrefix,
                            adminConfigurationHtmlSuffix);
            if(ConfigurationQueryOptions._DEBUG)
            {
                ConfigurationQueryOptions.log.debug("html: requestString=" + requestString);
            }
        }
        else if(this.matchesPrefixAndSuffix(requestString, adminConfigurationRdfxmlPrefix,
                adminConfigurationRdfxmlSuffix))
        {
            this._hasExplicitFormat = true;
            this._chosenFormat = Constants.APPLICATION_RDF_XML;
            if(ConfigurationQueryOptions._DEBUG)
            {
                ConfigurationQueryOptions.log.debug("rdfxml: requestString=" + requestString);
            }
            requestString =
                    this.takeOffPrefixAndSuffix(requestString, adminConfigurationRdfxmlPrefix,
                            adminConfigurationRdfxmlSuffix);
            if(ConfigurationQueryOptions._DEBUG)
            {
                ConfigurationQueryOptions.log.debug("rdfxml: requestString=" + requestString);
            }
        }
        else if(this.matchesPrefixAndSuffix(requestString, adminConfigurationN3Prefix, adminConfigurationN3Suffix))
        {
            this._hasExplicitFormat = true;
            this._chosenFormat = Constants.TEXT_RDF_N3;
            if(ConfigurationQueryOptions._DEBUG)
            {
                ConfigurationQueryOptions.log.debug("n3: requestString=" + requestString);
            }
            requestString =
                    this.takeOffPrefixAndSuffix(requestString, adminConfigurationN3Prefix, adminConfigurationN3Suffix);
            if(ConfigurationQueryOptions._DEBUG)
            {
                ConfigurationQueryOptions.log.debug("n3: requestString=" + requestString);
            }
        }
        else if(this.matchesPrefixAndSuffix(requestString, adminConfigurationJsonPrefix, adminConfigurationJsonSuffix))
        {
            this._hasExplicitFormat = true;
            this._chosenFormat = Constants.APPLICATION_JSON;
            if(ConfigurationQueryOptions._DEBUG)
            {
                ConfigurationQueryOptions.log.debug("json: requestString=" + requestString);
            }
            requestString =
                    this.takeOffPrefixAndSuffix(requestString, adminConfigurationJsonPrefix,
                            adminConfigurationJsonSuffix);
            if(ConfigurationQueryOptions._DEBUG)
            {
                ConfigurationQueryOptions.log.debug("json: requestString=" + requestString);
            }
        }
        else if(this.matchesPrefixAndSuffix(requestString, adminConfigurationNTriplesPrefix,
                adminConfigurationNTriplesSuffix))
        {
            this._hasExplicitFormat = true;
            this._chosenFormat = Constants.TEXT_PLAIN;
            if(ConfigurationQueryOptions._DEBUG)
            {
                ConfigurationQueryOptions.log.debug("ntriples: requestString=" + requestString);
            }
            requestString =
                    this.takeOffPrefixAndSuffix(requestString, adminConfigurationNTriplesPrefix,
                            adminConfigurationNTriplesSuffix);
            if(ConfigurationQueryOptions._DEBUG)
            {
                ConfigurationQueryOptions.log.debug("ntriples: requestString=" + requestString);
            }
        }
        else if(this.matchesPrefixAndSuffix(requestString, adminConfigurationNQuadsPrefix,
                adminConfigurationNQuadsSuffix))
        {
            this._hasExplicitFormat = true;
            this._chosenFormat = Constants.TEXT_X_NQUADS;
            if(ConfigurationQueryOptions._DEBUG)
            {
                ConfigurationQueryOptions.log.debug("nquads: requestString=" + requestString);
            }
            requestString =
                    this.takeOffPrefixAndSuffix(requestString, adminConfigurationNQuadsPrefix,
                            adminConfigurationNQuadsSuffix);
            if(ConfigurationQueryOptions._DEBUG)
            {
                ConfigurationQueryOptions.log.debug("nquads: requestString=" + requestString);
            }
        }
        
        return requestString;
    }
    
    private String parseForAdminPrefix(String requestString)
    {
        final String adminUrlPrefix = this.localSettings.getStringProperty("adminUrlPrefix", "admin/");
        
        if(this.matchesPrefixAndSuffix(requestString, adminUrlPrefix, ""))
        {
            if(ConfigurationQueryOptions._DEBUG)
            {
                ConfigurationQueryOptions.log.debug("found admin prefix");
            }
            
            this._adminPrefixMatch = true;
            
            requestString = this.takeOffPrefixAndSuffix(requestString, adminUrlPrefix, "");
        }
        else if(ConfigurationQueryOptions._DEBUG)
        {
            ConfigurationQueryOptions.log.debug("did not find admin prefix");
        }
        
        return requestString;
    }
    
    private String parseForApiVersion(String requestString)
    {
        final String adminConfigurationApiOpeningPrefix =
                this.localSettings.getStringProperty("adminConfigurationApiVersionOpeningPrefix", "");
        final String adminConfigurationApiClosingPrefix =
                this.localSettings.getStringProperty("adminConfigurationApiVersionClosingPrefix", "/");
        final String adminConfigurationApiSuffix =
                this.localSettings.getStringProperty("adminConfigurationApiVersionSuffix", "");
        
        // FIXME: this does not work if there is no file format suffix, it will always call the
        // current API version in these cases
        final String apiVersionPatternString =
                "^" + adminConfigurationApiOpeningPrefix + "(\\d+)" + adminConfigurationApiClosingPrefix + "(.*)"
                        + adminConfigurationApiSuffix + "$";
        
        if(ConfigurationQueryOptions._DEBUG)
        {
            ConfigurationQueryOptions.log.debug("apiVersionPatternString=" + apiVersionPatternString);
            ConfigurationQueryOptions.log.debug("requestString=" + requestString);
        }
        
        final Pattern apiVersionPattern = Pattern.compile(apiVersionPatternString);
        
        final Matcher matcher = apiVersionPattern.matcher(requestString);
        
        if(!matcher.matches())
        {
            return requestString;
        }
        
        try
        {
            // This will always be a non-negative integer due to the way the pattern matches, but it
            // may be 0 so we correct that case
            this._apiVersion = Integer.parseInt(matcher.group(1));
            
            if(this._apiVersion == 0)
            {
                this._apiVersion = Settings.CONFIG_API_VERSION;
            }
            else
            {
                this._hasExplicitApiVersionValue = true;
            }
        }
        catch(final NumberFormatException nfe)
        {
            this._apiVersion = Settings.CONFIG_API_VERSION;
            ConfigurationQueryOptions.log
                    .error("ConfigurationQueryOptions: nfe: check the adminConfigurationApiOpeningPrefix for the likely mistake",
                            nfe);
        }
        
        requestString = matcher.group(2);
        
        if(ConfigurationQueryOptions._DEBUG)
        {
            ConfigurationQueryOptions.log.debug("apiVersion=" + this._apiVersion);
            ConfigurationQueryOptions.log.debug("requestString=" + requestString);
        }
        
        return requestString;
    }
    
    private String parseForNsIdFormat(String requestString)
    {
        final String nsIdHtmlPrefix = this.localSettings.getStringProperty("htmlUrlPrefix", "page/");
        final String nsIdHtmlSuffix = this.localSettings.getStringProperty("htmlUrlSuffix", "");
        final String nsIdRdfxmlPrefix = this.localSettings.getStringProperty("rdfXmlUrlPrefix", "rdfxml/");
        final String nsIdRdfxmlSuffix = this.localSettings.getStringProperty("rdfXmlUrlSuffix", "");
        final String nsIdN3Prefix = this.localSettings.getStringProperty("n3UrlPrefix", "n3/");
        final String nsIdN3Suffix = this.localSettings.getStringProperty("n3UrlSuffix", "");
        final String nsIdJsonPrefix = this.localSettings.getStringProperty("jsonUrlPrefix", "json/");
        final String nsIdJsonSuffix = this.localSettings.getStringProperty("jsonUrlSuffix", "");
        final String nsIdNTriplesPrefix = this.localSettings.getStringProperty("ntriplesUrlPrefix", "ntriples/");
        final String nsIdNTriplesSuffix = this.localSettings.getStringProperty("ntriplesUrlSuffix", "");
        final String nsIdNQuadsPrefix = this.localSettings.getStringProperty("nquadsUrlPrefix", "nquads/");
        final String nsIdNQuadsSuffix = this.localSettings.getStringProperty("nquadsUrlSuffix", "");
        
        if(this.matchesPrefixAndSuffix(requestString, nsIdHtmlPrefix, nsIdHtmlSuffix))
        {
            this._hasExplicitFormat = true;
            this._chosenFormat = Constants.TEXT_HTML;
            if(ConfigurationQueryOptions._DEBUG)
            {
                ConfigurationQueryOptions.log.debug("html: requestString=" + requestString);
            }
            requestString = this.takeOffPrefixAndSuffix(requestString, nsIdHtmlPrefix, nsIdHtmlSuffix);
            if(ConfigurationQueryOptions._DEBUG)
            {
                ConfigurationQueryOptions.log.debug("html: requestString=" + requestString);
            }
        }
        else if(this.matchesPrefixAndSuffix(requestString, nsIdRdfxmlPrefix, nsIdRdfxmlSuffix))
        {
            this._hasExplicitFormat = true;
            this._chosenFormat = Constants.APPLICATION_RDF_XML;
            if(ConfigurationQueryOptions._DEBUG)
            {
                ConfigurationQueryOptions.log.debug("rdfxml: requestString=" + requestString);
            }
            requestString = this.takeOffPrefixAndSuffix(requestString, nsIdRdfxmlPrefix, nsIdRdfxmlSuffix);
            if(ConfigurationQueryOptions._DEBUG)
            {
                ConfigurationQueryOptions.log.debug("rdfxml: requestString=" + requestString);
            }
        }
        else if(this.matchesPrefixAndSuffix(requestString, nsIdN3Prefix, nsIdN3Suffix))
        {
            this._hasExplicitFormat = true;
            this._chosenFormat = Constants.TEXT_RDF_N3;
            if(ConfigurationQueryOptions._DEBUG)
            {
                ConfigurationQueryOptions.log.debug("n3: requestString=" + requestString);
            }
            requestString = this.takeOffPrefixAndSuffix(requestString, nsIdN3Prefix, nsIdN3Suffix);
            if(ConfigurationQueryOptions._DEBUG)
            {
                ConfigurationQueryOptions.log.debug("n3: requestString=" + requestString);
            }
        }
        else if(this.matchesPrefixAndSuffix(requestString, nsIdJsonPrefix, nsIdJsonSuffix))
        {
            this._hasExplicitFormat = true;
            this._chosenFormat = Constants.APPLICATION_JSON;
            if(ConfigurationQueryOptions._DEBUG)
            {
                ConfigurationQueryOptions.log.debug("json: requestString=" + requestString);
            }
            requestString = this.takeOffPrefixAndSuffix(requestString, nsIdJsonPrefix, nsIdJsonSuffix);
            if(ConfigurationQueryOptions._DEBUG)
            {
                ConfigurationQueryOptions.log.debug("json: requestString=" + requestString);
            }
        }
        else if(this.matchesPrefixAndSuffix(requestString, nsIdNTriplesPrefix, nsIdNTriplesSuffix))
        {
            this._hasExplicitFormat = true;
            this._chosenFormat = Constants.TEXT_PLAIN;
            if(ConfigurationQueryOptions._DEBUG)
            {
                ConfigurationQueryOptions.log.debug("ntriples: requestString=" + requestString);
            }
            requestString = this.takeOffPrefixAndSuffix(requestString, nsIdNTriplesPrefix, nsIdNTriplesSuffix);
            if(ConfigurationQueryOptions._DEBUG)
            {
                ConfigurationQueryOptions.log.debug("ntriples: requestString=" + requestString);
            }
        }
        else if(this.matchesPrefixAndSuffix(requestString, nsIdNQuadsPrefix, nsIdNQuadsSuffix))
        {
            this._hasExplicitFormat = true;
            this._chosenFormat = Constants.TEXT_X_NQUADS;
            if(ConfigurationQueryOptions._DEBUG)
            {
                ConfigurationQueryOptions.log.debug("nquads: requestString=" + requestString);
            }
            requestString = this.takeOffPrefixAndSuffix(requestString, nsIdNQuadsPrefix, nsIdNQuadsSuffix);
            if(ConfigurationQueryOptions._DEBUG)
            {
                ConfigurationQueryOptions.log.debug("nquads: requestString=" + requestString);
            }
        }
        
        return requestString;
    }
    
    private String parseForRefresh(String requestString)
    {
        final String adminConfigurationRefreshPrefix =
                this.localSettings.getStringProperty("adminConfigurationRefreshPrefix", "refresh");
        
        if(this.matchesPrefixAndSuffix(requestString, adminConfigurationRefreshPrefix, ""))
        {
            this._isRefresh = true;
            requestString = this.takeOffPrefixAndSuffix(requestString, adminConfigurationRefreshPrefix, "");
        }
        
        return requestString;
    }
    
    private String takeOffPrefixAndSuffix(final String nextString, final String nextPrefix, final String nextSuffix)
    {
        if(this.matchesPrefixAndSuffix(nextString, nextPrefix, nextSuffix))
        {
            return nextString.substring(nextPrefix.length(), nextString.length() - nextSuffix.length());
        }
        else
        {
            ConfigurationQueryOptions.log
                    .error("Could not takeOffPrefixAndSuffix because the string was not long enough");
        }
        
        return nextString;
    }
}
