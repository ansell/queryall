package org.queryall.servlets.queryparsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.WebappConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses query options out of a query string
 */

public class DefaultQueryOptions
{
    public static final Logger log = LoggerFactory.getLogger(DefaultQueryOptions.class);
    public static final boolean _TRACE = DefaultQueryOptions.log.isTraceEnabled();
    public static final boolean _DEBUG = DefaultQueryOptions.log.isDebugEnabled();
    public static final boolean _INFO = DefaultQueryOptions.log.isInfoEnabled();
    
    private boolean _hasExplicitFormat = false;
    private String _chosenFormat = "";
    private boolean _hasExplicitPageOffsetValue = false;
    private int pageoffset = 1;
    private boolean _hasQueryPlanRequest = false;
    private boolean isRootContext = false;
    
    private String parsedRequestString = "";
    
    private QueryAllConfiguration localSettings;
    private Pattern queryPlanPattern;
    private String queryplanUrlPrefix;
    private String queryplanUrlSuffix;
    private String pageoffsetUrlOpeningPrefix;
    private String pageoffsetUrlClosingPrefix;
    private String pageoffsetUrlSuffix;
    private String htmlUrlPrefix;
    private String htmlUrlSuffix;
    private String rdfXmlUrlPrefix;
    private String rdfXmlUrlSuffix;
    private String n3UrlPrefix;
    private String n3UrlSuffix;
    private String jsonUrlPrefix;
    private String jsonUrlSuffix;
    private String ntriplesUrlPrefix;
    private String ntriplesUrlSuffix;
    private String nquadsUrlPrefix;
    private String nquadsUrlSuffix;
    
    public DefaultQueryOptions(String requestUri, final String contextPath, final QueryAllConfiguration nextSettings)
    {
        this.localSettings = nextSettings;
        
        this.pageoffsetUrlOpeningPrefix =
                this.localSettings.getStringProperty(WebappConfig.PAGEOFFSET_URL_OPENING_PREFIX);
        this.pageoffsetUrlClosingPrefix = this.localSettings.getStringProperty(WebappConfig.PAGEOFFSET_URL_CLOSING_PREFIX);
        this.pageoffsetUrlSuffix = this.localSettings.getStringProperty(WebappConfig.PAGEOFFSET_URL_SUFFIX);
        this.htmlUrlPrefix = this.localSettings.getStringProperty(WebappConfig.HTML_URL_PREFIX);
        this.htmlUrlSuffix = this.localSettings.getStringProperty(WebappConfig.HTML_URL_SUFFIX);
        this.rdfXmlUrlPrefix = this.localSettings.getStringProperty(WebappConfig.RDFXML_URL_PREFIX);
        this.rdfXmlUrlSuffix = this.localSettings.getStringProperty(WebappConfig.RDFXML_URL_SUFFIX);
        this.n3UrlPrefix = this.localSettings.getStringProperty(WebappConfig.N3_URL_PREFIX);
        this.n3UrlSuffix = this.localSettings.getStringProperty(WebappConfig.N3_URL_SUFFIX);
        this.jsonUrlPrefix = this.localSettings.getStringProperty(WebappConfig.JSON_URL_PREFIX);
        this.jsonUrlSuffix = this.localSettings.getStringProperty(WebappConfig.JSON_URL_SUFFIX);
        this.ntriplesUrlPrefix = this.localSettings.getStringProperty(WebappConfig.NTRIPLES_URL_PREFIX);
        this.ntriplesUrlSuffix = this.localSettings.getStringProperty(WebappConfig.NTRIPLES_URL_SUFFIX);
        this.nquadsUrlPrefix = this.localSettings.getStringProperty(WebappConfig.NQUADS_URL_PREFIX);
        this.nquadsUrlSuffix = this.localSettings.getStringProperty(WebappConfig.NQUADS_URL_SUFFIX);
        this.queryplanUrlPrefix = this.localSettings.getStringProperty(WebappConfig.QUERYPLAN_URL_PREFIX);
        this.queryplanUrlSuffix = this.localSettings.getStringProperty(WebappConfig.QUERYPLAN_URL_SUFFIX);
        
        final String pageOffsetPatternString =
                "^" + this.pageoffsetUrlOpeningPrefix + "(\\d+)" + this.pageoffsetUrlClosingPrefix + "(.+)"
                        + this.pageoffsetUrlSuffix + "$";
        
        if(DefaultQueryOptions._TRACE)
        {
            DefaultQueryOptions.log.trace("pageOffsetPatternString=" + pageOffsetPatternString);
        }
        
        this.queryPlanPattern = Pattern.compile(pageOffsetPatternString);
        
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
            if(requestUri.startsWith(contextPath))
            {
                if(DefaultQueryOptions._DEBUG)
                {
                    DefaultQueryOptions.log.debug("requestUri before removing contextPath requestUri=" + requestUri);
                }
                requestUri = requestUri.substring(contextPath.length());
                if(DefaultQueryOptions._DEBUG)
                {
                    DefaultQueryOptions.log.debug("removed contextPath from requestUri contextPath=" + contextPath
                            + " requestUri=" + requestUri);
                }
            }
        }
        
        String requestString = requestUri;
        
        if(requestString.startsWith("/"))
        {
            if(DefaultQueryOptions._DEBUG)
            {
                DefaultQueryOptions.log.debug("requestString=" + requestString);
            }
            requestString = requestString.substring(1);
            if(DefaultQueryOptions._DEBUG)
            {
                DefaultQueryOptions.log.debug("requestString=" + requestString);
            }
        }
        
        requestString = this.parseForFormat(requestString);
        
        requestString = this.parseForQueryPlan(requestString);
        
        requestString = this.parseForPageOffset(requestString);
        
        this.parsedRequestString = requestString;
    }
    
    public boolean containsExplicitFormat()
    {
        return this._hasExplicitFormat;
    }
    
    public boolean containsExplicitPageOffsetValue()
    {
        return this._hasExplicitPageOffsetValue;
    }
    
    public String getExplicitFormat()
    {
        return this._chosenFormat;
    }
    
    public int getPageOffset()
    {
        return this.pageoffset;
    }
    
    public String getParsedRequest()
    {
        return this.parsedRequestString;
    }
    
    public boolean isQueryPlanRequest()
    {
        return this._hasQueryPlanRequest;
    }
    
    private boolean matchesPrefixAndSuffix(final String nextString, final String nextPrefix, final String nextSuffix)
    {
        return nextString.startsWith(nextPrefix) && nextString.endsWith(nextSuffix)
                && nextString.length() >= (nextPrefix.length() + nextSuffix.length());
    }
    
    private String parseForFormat(String requestString)
    {
        if(this.matchesPrefixAndSuffix(requestString, this.htmlUrlPrefix, this.htmlUrlSuffix))
        {
            this._hasExplicitFormat = true;
            this._chosenFormat = Constants.TEXT_HTML;
            if(DefaultQueryOptions._DEBUG)
            {
                DefaultQueryOptions.log.debug("requestString=" + requestString);
            }
            requestString = this.takeOffPrefixAndSuffix(requestString, this.htmlUrlPrefix, this.htmlUrlSuffix);
            if(DefaultQueryOptions._DEBUG)
            {
                DefaultQueryOptions.log.debug("requestString=" + requestString);
            }
        }
        else if(this.matchesPrefixAndSuffix(requestString, this.rdfXmlUrlPrefix, this.rdfXmlUrlSuffix))
        {
            this._hasExplicitFormat = true;
            this._chosenFormat = Constants.APPLICATION_RDF_XML;
            if(DefaultQueryOptions._DEBUG)
            {
                DefaultQueryOptions.log.debug("requestString=" + requestString);
            }
            requestString = this.takeOffPrefixAndSuffix(requestString, this.rdfXmlUrlPrefix, this.rdfXmlUrlSuffix);
            if(DefaultQueryOptions._DEBUG)
            {
                DefaultQueryOptions.log.debug("requestString=" + requestString);
            }
        }
        else if(this.matchesPrefixAndSuffix(requestString, this.n3UrlPrefix, this.n3UrlSuffix))
        {
            this._hasExplicitFormat = true;
            this._chosenFormat = Constants.TEXT_RDF_N3;
            if(DefaultQueryOptions._DEBUG)
            {
                DefaultQueryOptions.log.debug("requestString=" + requestString);
            }
            requestString = this.takeOffPrefixAndSuffix(requestString, this.n3UrlPrefix, this.n3UrlSuffix);
            if(DefaultQueryOptions._DEBUG)
            {
                DefaultQueryOptions.log.debug("requestString=" + requestString);
            }
        }
        else if(this.matchesPrefixAndSuffix(requestString, this.jsonUrlPrefix, this.jsonUrlSuffix))
        {
            this._hasExplicitFormat = true;
            this._chosenFormat = Constants.APPLICATION_JSON;
            if(DefaultQueryOptions._DEBUG)
            {
                DefaultQueryOptions.log.debug("requestString=" + requestString);
            }
            requestString = this.takeOffPrefixAndSuffix(requestString, this.jsonUrlPrefix, this.jsonUrlSuffix);
            if(DefaultQueryOptions._DEBUG)
            {
                DefaultQueryOptions.log.debug("requestString=" + requestString);
            }
        }
        else if(this.matchesPrefixAndSuffix(requestString, this.ntriplesUrlPrefix, this.ntriplesUrlSuffix))
        {
            this._hasExplicitFormat = true;
            this._chosenFormat = Constants.TEXT_PLAIN;
            if(DefaultQueryOptions._DEBUG)
            {
                DefaultQueryOptions.log.debug("requestString=" + requestString);
            }
            requestString = this.takeOffPrefixAndSuffix(requestString, this.ntriplesUrlPrefix, this.ntriplesUrlSuffix);
            if(DefaultQueryOptions._DEBUG)
            {
                DefaultQueryOptions.log.debug("requestString=" + requestString);
            }
        }
        else if(this.matchesPrefixAndSuffix(requestString, this.nquadsUrlPrefix, this.nquadsUrlSuffix))
        {
            this._hasExplicitFormat = true;
            this._chosenFormat = Constants.TEXT_X_NQUADS;
            if(DefaultQueryOptions._DEBUG)
            {
                DefaultQueryOptions.log.debug("requestString=" + requestString);
            }
            requestString = this.takeOffPrefixAndSuffix(requestString, this.nquadsUrlPrefix, this.nquadsUrlSuffix);
            if(DefaultQueryOptions._DEBUG)
            {
                DefaultQueryOptions.log.debug("requestString=" + requestString);
            }
        }
        
        return requestString;
    }
    
    private String parseForPageOffset(String requestString)
    {
        final Matcher matcher = this.queryPlanPattern.matcher(requestString);
        
        if(!matcher.matches())
        {
            return requestString;
        }
        
        try
        {
            // This will always be a non-negative integer due to the way the pattern matches, but it
            // may be 0 so we correct that case
            this.pageoffset = Integer.parseInt(matcher.group(1));
        }
        catch(final NumberFormatException nfe)
        {
            this.pageoffset = 1;
        }
        
        if(this.pageoffset == 0)
        {
            this.pageoffset = 1;
        }
        
        this._hasExplicitPageOffsetValue = true;
        
        requestString = matcher.group(2);
        
        if(DefaultQueryOptions._DEBUG)
        {
            DefaultQueryOptions.log.debug("pageoffset=" + this.pageoffset);
            DefaultQueryOptions.log.debug("requestString=" + requestString);
        }
        
        return requestString;
    }
    
    private String parseForQueryPlan(String requestString)
    {
        if(this.matchesPrefixAndSuffix(requestString, this.queryplanUrlPrefix, this.queryplanUrlSuffix))
        {
            this._hasQueryPlanRequest = true;
            if(DefaultQueryOptions._DEBUG)
            {
                DefaultQueryOptions.log.debug("requestString=" + requestString);
            }
            requestString =
                    this.takeOffPrefixAndSuffix(requestString, this.queryplanUrlPrefix, this.queryplanUrlSuffix);
            if(DefaultQueryOptions._DEBUG)
            {
                DefaultQueryOptions.log.debug("requestString=" + requestString);
            }
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
            DefaultQueryOptions.log.error("Could not takeOffPrefixAndSuffix because the string was not long enough");
        }
        
        return nextString;
    }
}
