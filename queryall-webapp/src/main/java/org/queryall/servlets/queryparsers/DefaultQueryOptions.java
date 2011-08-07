package org.queryall.servlets.queryparsers;

import org.queryall.api.QueryAllConfiguration;
import org.queryall.enumerations.Constants;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

/** 
 * Parses query options out of a query string
 */

public class DefaultQueryOptions
{
    public static final Logger log = Logger.getLogger(DefaultQueryOptions.class.getName());
    public static final boolean _TRACE = log.isTraceEnabled();
    public static final boolean _DEBUG = log.isDebugEnabled();
    public static final boolean _INFO = log.isInfoEnabled();
    
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
    
    public DefaultQueryOptions(String requestUri, String contextPath, QueryAllConfiguration nextSettings)
    {
        this.localSettings = nextSettings;

        pageoffsetUrlOpeningPrefix = localSettings.getStringProperty("pageoffsetUrlOpeningPrefix", "pageoffset");
        pageoffsetUrlClosingPrefix = localSettings.getStringProperty("pageoffsetUrlClosingPrefix", "/");
        pageoffsetUrlSuffix = localSettings.getStringProperty("pageoffsetUrlSuffix", "");
        htmlUrlPrefix = localSettings.getStringProperty("htmlUrlPrefix", "page/");
        htmlUrlSuffix = localSettings.getStringProperty("htmlUrlSuffix", "");
        rdfXmlUrlPrefix = localSettings.getStringProperty("rdfXmlUrlPrefix", "rdfxml/");
        rdfXmlUrlSuffix = localSettings.getStringProperty("rdfXmlUrlSuffix", "");
        n3UrlPrefix = localSettings.getStringProperty("n3UrlPrefix", "n3/");
        n3UrlSuffix = localSettings.getStringProperty("n3UrlSuffix", "");
        jsonUrlPrefix = localSettings.getStringProperty("jsonUrlPrefix", "json/");
        jsonUrlSuffix = localSettings.getStringProperty("jsonUrlSuffix", "");
        ntriplesUrlPrefix = localSettings.getStringProperty("ntriplesUrlPrefix", "ntriples/");
        ntriplesUrlSuffix = localSettings.getStringProperty("ntriplesUrlSuffix", "");
        nquadsUrlPrefix = localSettings.getStringProperty("nquadsUrlPrefix", "nquads/");
        nquadsUrlSuffix = localSettings.getStringProperty("nquadsUrlSuffix", "");
        queryplanUrlPrefix = localSettings.getStringProperty("queryplanUrlPrefix", "queryplan/");
        queryplanUrlSuffix = localSettings.getStringProperty("queryplanUrlSuffix", "");
        
        String pageOffsetPatternString = "^"+pageoffsetUrlOpeningPrefix+"(\\d+)"+pageoffsetUrlClosingPrefix+"(.+)"+pageoffsetUrlSuffix+"$";

        if(_TRACE)
            log.trace("pageOffsetPatternString="+pageOffsetPatternString);
        
        queryPlanPattern = Pattern.compile(pageOffsetPatternString);

        if(contextPath.equals(""))
        {
        	isRootContext = true;
        }
        else
        {
        	isRootContext = false;
        }
        
        if(!isRootContext)
        {
        	if(requestUri.startsWith(contextPath))
        	{
        		if(_DEBUG)
        			log.debug("requestUri before removing contextPath requestUri="+requestUri);
        		requestUri = requestUri.substring(contextPath.length());
        		if(_DEBUG)
        			log.debug("removed contextPath from requestUri contextPath="+contextPath+" requestUri="+requestUri);
        	}
        }
        
        String requestString = requestUri;
        
        if(requestString.startsWith("/"))
        {
            if(_DEBUG)
            	log.debug("requestString="+requestString);
            requestString = requestString.substring(1);
            if(_DEBUG)
            	log.debug("requestString="+requestString);
        }
        
        requestString = parseForFormat(requestString);
        
        requestString = parseForQueryPlan(requestString);
        
        requestString = parseForPageOffset(requestString);
        
        parsedRequestString = requestString;
    }
    
    private String parseForFormat(String requestString)
    {
        if(matchesPrefixAndSuffix(requestString, htmlUrlPrefix, htmlUrlSuffix))
        {
            _hasExplicitFormat = true;
            _chosenFormat = Constants.TEXT_HTML;
            if(_DEBUG)
            	log.debug("requestString="+requestString);
            requestString = takeOffPrefixAndSuffix(requestString, htmlUrlPrefix, htmlUrlSuffix);
            if(_DEBUG)
            	log.debug("requestString="+requestString);
        }
        else if(matchesPrefixAndSuffix(requestString, rdfXmlUrlPrefix, rdfXmlUrlSuffix))
        {
            _hasExplicitFormat = true;
            _chosenFormat = Constants.APPLICATION_RDF_XML;
            if(_DEBUG)
            	log.debug("requestString="+requestString);
            requestString = takeOffPrefixAndSuffix(requestString, rdfXmlUrlPrefix, rdfXmlUrlSuffix);
            if(_DEBUG)
            	log.debug("requestString="+requestString);
        }
        else if(matchesPrefixAndSuffix(requestString, n3UrlPrefix, n3UrlSuffix))
        {
            _hasExplicitFormat = true;
            _chosenFormat = Constants.TEXT_RDF_N3;
            if(_DEBUG)
            	log.debug("requestString="+requestString);
            requestString = takeOffPrefixAndSuffix(requestString, n3UrlPrefix, n3UrlSuffix);
            if(_DEBUG)
            	log.debug("requestString="+requestString);
        }
        else if(matchesPrefixAndSuffix(requestString, jsonUrlPrefix, jsonUrlSuffix))
        {
            _hasExplicitFormat = true;
            _chosenFormat = Constants.APPLICATION_JSON;
            if(_DEBUG)
            	log.debug("requestString="+requestString);
            requestString = takeOffPrefixAndSuffix(requestString, jsonUrlPrefix, jsonUrlSuffix);
            if(_DEBUG)
            	log.debug("requestString="+requestString);
        }
        else if(matchesPrefixAndSuffix(requestString, ntriplesUrlPrefix, ntriplesUrlSuffix))
        {
            _hasExplicitFormat = true;
            _chosenFormat = Constants.TEXT_PLAIN;
            if(_DEBUG)
            	log.debug("requestString="+requestString);
            requestString = takeOffPrefixAndSuffix(requestString, ntriplesUrlPrefix, ntriplesUrlSuffix);
            if(_DEBUG)
            	log.debug("requestString="+requestString);
        }
        else if(matchesPrefixAndSuffix(requestString, nquadsUrlPrefix, nquadsUrlSuffix))
        {
            _hasExplicitFormat = true;
            _chosenFormat = Constants.TEXT_X_NQUADS;
            if(_DEBUG)
            	log.debug("requestString="+requestString);
            requestString = takeOffPrefixAndSuffix(requestString, nquadsUrlPrefix, nquadsUrlSuffix);
            if(_DEBUG)
            	log.debug("requestString="+requestString);
        }
        
        return requestString;
    }
    
    private boolean matchesPrefixAndSuffix(String nextString, String nextPrefix, String nextSuffix)
    {
        return nextString.startsWith(nextPrefix) 
            && nextString.endsWith(nextSuffix) 
            && nextString.length() >= (nextPrefix.length() + nextSuffix.length());
    }
    
    private String takeOffPrefixAndSuffix(String nextString, String nextPrefix, String nextSuffix)
    {
        if(matchesPrefixAndSuffix(nextString, nextPrefix, nextSuffix))
        {
            return nextString.substring(nextPrefix.length(),
                nextString.length()-nextSuffix.length());
        }
        else
        {
            log.error("Could not takeOffPrefixAndSuffix because the string was not long enough");
        }
        
        return nextString;
    }

    private String parseForQueryPlan(String requestString)
    {
        if(matchesPrefixAndSuffix(requestString, queryplanUrlPrefix, queryplanUrlSuffix))
        {
            _hasQueryPlanRequest = true;
            if(_DEBUG)
            	log.debug("requestString="+requestString);
            requestString = takeOffPrefixAndSuffix(requestString, queryplanUrlPrefix, queryplanUrlSuffix);
            if(_DEBUG)
            	log.debug("requestString="+requestString);
        }
        
        return requestString;
    }
    
    private String parseForPageOffset(String requestString)
    {
        Matcher matcher = queryPlanPattern.matcher(requestString);
        
        if(!matcher.matches())
        {
            return requestString;
        }
        
        try
        {
            // This will always be a non-negative integer due to the way the pattern matches, but it may be 0 so we correct that case
            pageoffset = Integer.parseInt(matcher.group(1));
        }
        catch(NumberFormatException nfe)
        {
            pageoffset = 1;
        }
        
        if(pageoffset == 0)
            pageoffset = 1;
        
        _hasExplicitPageOffsetValue = true;
        
        requestString = matcher.group(2);
        
        if(_DEBUG)
        {
        	log.debug("pageoffset="+pageoffset);
        	log.debug("requestString="+requestString);
        }

        return requestString;
    }
    
    public boolean containsExplicitFormat()
    {
        return _hasExplicitFormat;
    }
    
    public String getExplicitFormat()
    {
        return _chosenFormat;
    }

    public boolean containsExplicitPageOffsetValue()
    {
        return _hasExplicitPageOffsetValue;
    }

    public int getPageOffset()
    {
        return pageoffset;
    }

    public boolean isQueryPlanRequest()
    {
        return _hasQueryPlanRequest;
    }
    
    public String getParsedRequest()
    {
        return parsedRequestString;
    }
}
