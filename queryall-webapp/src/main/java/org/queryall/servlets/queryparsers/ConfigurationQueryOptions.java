package org.queryall.servlets.queryparsers;

import org.queryall.helpers.Constants;
import org.queryall.helpers.Settings;
import org.queryall.helpers.StringUtils;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

/** 
 * Parses query options out of a query string
 */

public class ConfigurationQueryOptions
{
    public static final Logger log = Logger.getLogger(ConfigurationQueryOptions.class.getName());
    public static final boolean _TRACE = log.isTraceEnabled();
    public static final boolean _DEBUG = log.isDebugEnabled();
    public static final boolean _INFO = log.isInfoEnabled();
    
//    private Settings localSettings = localSettings;

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
    private Settings localSettings;
	private boolean isRootContext = false;

    
    public ConfigurationQueryOptions(String requestUri, String contextPath, Settings localSettings)
    {
    	_apiVersion = Settings.CONFIG_API_VERSION;
    	
        String requestString = requestUri;
        this.localSettings = localSettings;
        
        if(requestString == null)
        {
            requestString = "";
            log.error("ConfigurationQueryOptions: requestString was null");
        }
        
        if(contextPath.equals(""))
        {
        	isRootContext  = true;
        }
        else
        {
        	isRootContext = false;
        }
        
        if(!isRootContext)
        {
        	if(requestString.startsWith(contextPath))
        	{
        		if(_DEBUG)
        			log.debug("requestUri before removing contextPath requestString="+requestString);
        		requestString = requestString.substring(contextPath.length());
        		if(_DEBUG)
        			log.debug("removed contextPath from requestUri contextPath="+contextPath+" requestString="+requestString);
        	}
        }

        if(requestString.startsWith("/"))
        {
            if(_DEBUG)
            	log.debug("requestString="+requestString);
            requestString = requestString.substring(1);
            if(_DEBUG)
            	log.debug("requestString="+requestString);
        }
        
        requestString = parseForAdminPrefix(requestString);

        if(_adminPrefixMatch)
        {
            requestString = parseForRefresh(requestString);
    
            if(!_isRefresh)
            {
                requestString = parseForAdminConfiguration(requestString);
                
                if(_adminConfigurationMatch || _adminBasicWebappConfigurationMatch)
                {
                    requestString = parseForApiVersion(requestString);
                    
                    requestString = parseForAdminFormat(requestString);
                }
            }
        }
        else
        {
            requestString = parseForNsIdFormat(requestString);
            
            if(StringUtils.isPlainNamespaceAndIdentifier(requestString, localSettings))
            {
                _isPlainNamespaceAndIdentifier = true;
            }
        }

        parsedRequestString = requestString;
    }
    
    private String parseForAdminPrefix(String requestString)
    {
        String adminUrlPrefix = localSettings.getStringProperty("adminUrlPrefix", "admin/");
        
        if(matchesPrefixAndSuffix(requestString, adminUrlPrefix, ""))
        {
        	if(_DEBUG)
        	{
        		log.debug("found admin prefix");
        	}
        	
            _adminPrefixMatch = true;
            
            requestString = takeOffPrefixAndSuffix(requestString, adminUrlPrefix, "");
        }
        else if(_DEBUG)
    	{
    		log.debug("did not find admin prefix");
    	}

        
        return requestString;
    }
    
    private String parseForAdminConfiguration(String requestString)
    {
        String adminConfigurationPrefix = localSettings.getStringProperty("adminConfigurationPrefix", "configuration/");

        String adminWebappConfigurationPrefix = localSettings.getStringProperty("adminWebappConfigurationPrefix", "webappconfiguration/");
        
        if(matchesPrefixAndSuffix(requestString, adminConfigurationPrefix, ""))
        {
            requestString = takeOffPrefixAndSuffix(requestString, adminConfigurationPrefix, "");
            
            _adminConfigurationMatch = true;
        }
        else if(matchesPrefixAndSuffix(requestString, adminWebappConfigurationPrefix, ""))
        {
            requestString = takeOffPrefixAndSuffix(requestString, adminWebappConfigurationPrefix, "");
            
            _adminBasicWebappConfigurationMatch = true;
        }
        
        return requestString;
    }

    private String parseForRefresh(String requestString)
    {
        String adminConfigurationRefreshPrefix = localSettings.getStringProperty("adminConfigurationRefreshPrefix", "refresh");
        
        if(matchesPrefixAndSuffix(requestString, adminConfigurationRefreshPrefix, ""))
        {
            _isRefresh = true;
            requestString = takeOffPrefixAndSuffix(requestString, adminConfigurationRefreshPrefix, "");
        }
        
        return requestString;
    }
    
    private String parseForAdminFormat(String requestString)
    {
        String adminConfigurationHtmlPrefix = localSettings.getStringProperty("adminConfigurationHtmlPrefix", "");
        String adminConfigurationHtmlSuffix = localSettings.getStringProperty("adminConfigurationHtmlSuffix", "/html");
        String adminConfigurationRdfxmlPrefix = localSettings.getStringProperty("adminConfigurationRdfxmlPrefix", "");
        String adminConfigurationRdfxmlSuffix = localSettings.getStringProperty("adminConfigurationRdfxmlSuffix", "/rdfxml");
        String adminConfigurationN3Prefix = localSettings.getStringProperty("adminConfigurationN3Prefix", "");
        String adminConfigurationN3Suffix = localSettings.getStringProperty("adminConfigurationN3Suffix", "/n3");
        String adminConfigurationJsonPrefix = localSettings.getStringProperty("adminConfigurationJsonPrefix", "");
        String adminConfigurationJsonSuffix = localSettings.getStringProperty("adminConfigurationJsonSuffix", "/json");
        String adminConfigurationNTriplesPrefix = localSettings.getStringProperty("adminConfigurationNTriplesPrefix", "");
        String adminConfigurationNTriplesSuffix = localSettings.getStringProperty("adminConfigurationNTriplesSuffix", "/ntriples");
        String adminConfigurationNQuadsPrefix = localSettings.getStringProperty("adminConfigurationNQuadsPrefix", "");
        String adminConfigurationNQuadsSuffix = localSettings.getStringProperty("adminConfigurationNQuadsSuffix", "/nquads");
        
        if(matchesPrefixAndSuffix(requestString, adminConfigurationHtmlPrefix, adminConfigurationHtmlSuffix))
        {
            _hasExplicitFormat = true;
            _chosenFormat = Constants.TEXT_HTML;
            if(_DEBUG)
            	log.debug("html: requestString="+requestString);
            requestString = takeOffPrefixAndSuffix(requestString, adminConfigurationHtmlPrefix, adminConfigurationHtmlSuffix);
            if(_DEBUG)
            	log.debug("html: requestString="+requestString);
        }
        else if(matchesPrefixAndSuffix(requestString, adminConfigurationRdfxmlPrefix, adminConfigurationRdfxmlSuffix))
        {
            _hasExplicitFormat = true;
            _chosenFormat = Constants.APPLICATION_RDF_XML;
            if(_DEBUG)
            	log.debug("rdfxml: requestString="+requestString);
            requestString = takeOffPrefixAndSuffix(requestString, adminConfigurationRdfxmlPrefix, adminConfigurationRdfxmlSuffix);
            if(_DEBUG)
            	log.debug("rdfxml: requestString="+requestString);
        }
        else if(matchesPrefixAndSuffix(requestString, adminConfigurationN3Prefix, adminConfigurationN3Suffix))
        {
            _hasExplicitFormat = true;
            _chosenFormat = Constants.TEXT_RDF_N3;
            if(_DEBUG)
            	log.debug("n3: requestString="+requestString);
            requestString = takeOffPrefixAndSuffix(requestString, adminConfigurationN3Prefix, adminConfigurationN3Suffix);
            if(_DEBUG)
            	log.debug("n3: requestString="+requestString);
        }
        else if(matchesPrefixAndSuffix(requestString, adminConfigurationJsonPrefix, adminConfigurationJsonSuffix))
        {
            _hasExplicitFormat = true;
            _chosenFormat = Constants.APPLICATION_JSON;
            if(_DEBUG)
            	log.debug("json: requestString="+requestString);
            requestString = takeOffPrefixAndSuffix(requestString, adminConfigurationJsonPrefix, adminConfigurationJsonSuffix);
            if(_DEBUG)
            	log.debug("json: requestString="+requestString);
        }
        else if(matchesPrefixAndSuffix(requestString, adminConfigurationNTriplesPrefix, adminConfigurationNTriplesSuffix))
        {
            _hasExplicitFormat = true;
            _chosenFormat = Constants.TEXT_PLAIN;
            if(_DEBUG)
            	log.debug("ntriples: requestString="+requestString);
            requestString = takeOffPrefixAndSuffix(requestString, adminConfigurationNTriplesPrefix, adminConfigurationNTriplesSuffix);
            if(_DEBUG)
            	log.debug("ntriples: requestString="+requestString);
        }
        else if(matchesPrefixAndSuffix(requestString, adminConfigurationNQuadsPrefix, adminConfigurationNQuadsSuffix))
        {
            _hasExplicitFormat = true;
            _chosenFormat = Constants.TEXT_X_NQUADS;
            if(_DEBUG)
            	log.debug("nquads: requestString="+requestString);
            requestString = takeOffPrefixAndSuffix(requestString, adminConfigurationNQuadsPrefix, adminConfigurationNQuadsSuffix);
            if(_DEBUG)
            	log.debug("nquads: requestString="+requestString);
        }
        
        return requestString;
    }

    private String parseForNsIdFormat(String requestString)
    {
        String nsIdHtmlPrefix = localSettings.getStringProperty("htmlUrlPrefix", "page/");
        String nsIdHtmlSuffix = localSettings.getStringProperty("htmlUrlSuffix", "");
        String nsIdRdfxmlPrefix = localSettings.getStringProperty("rdfXmlUrlPrefix", "rdfxml/");
        String nsIdRdfxmlSuffix = localSettings.getStringProperty("rdfXmlUrlSuffix", "");
        String nsIdN3Prefix = localSettings.getStringProperty("n3UrlPrefix", "n3/");
        String nsIdN3Suffix = localSettings.getStringProperty("n3UrlSuffix", "");
        String nsIdJsonPrefix = localSettings.getStringProperty("jsonUrlPrefix", "json/");
        String nsIdJsonSuffix = localSettings.getStringProperty("jsonUrlSuffix", "");
        String nsIdNTriplesPrefix = localSettings.getStringProperty("ntriplesUrlPrefix", "ntriples/");
        String nsIdNTriplesSuffix = localSettings.getStringProperty("ntriplesUrlSuffix", "");
        String nsIdNQuadsPrefix = localSettings.getStringProperty("nquadsUrlPrefix", "nquads/");
        String nsIdNQuadsSuffix = localSettings.getStringProperty("nquadsUrlSuffix", "");
        
        
        if(matchesPrefixAndSuffix(requestString, nsIdHtmlPrefix, nsIdHtmlSuffix))
        {
            _hasExplicitFormat = true;
            _chosenFormat = Constants.TEXT_HTML;
            if(_DEBUG)
            	log.debug("html: requestString="+requestString);
            requestString = takeOffPrefixAndSuffix(requestString, nsIdHtmlPrefix, nsIdHtmlSuffix);
            if(_DEBUG)
            	log.debug("html: requestString="+requestString);
        }
        else if(matchesPrefixAndSuffix(requestString, nsIdRdfxmlPrefix, nsIdRdfxmlSuffix))
        {
            _hasExplicitFormat = true;
            _chosenFormat = Constants.APPLICATION_RDF_XML;
            if(_DEBUG)
            	log.debug("rdfxml: requestString="+requestString);
            requestString = takeOffPrefixAndSuffix(requestString, nsIdRdfxmlPrefix, nsIdRdfxmlSuffix);
            if(_DEBUG)
            	log.debug("rdfxml: requestString="+requestString);
        }
        else if(matchesPrefixAndSuffix(requestString, nsIdN3Prefix, nsIdN3Suffix))
        {
            _hasExplicitFormat = true;
            _chosenFormat = Constants.TEXT_RDF_N3;
            if(_DEBUG)
            	log.debug("n3: requestString="+requestString);
            requestString = takeOffPrefixAndSuffix(requestString, nsIdN3Prefix, nsIdN3Suffix);
            if(_DEBUG)
            	log.debug("n3: requestString="+requestString);
        }
        else if(matchesPrefixAndSuffix(requestString, nsIdJsonPrefix, nsIdJsonSuffix))
        {
            _hasExplicitFormat = true;
            _chosenFormat = Constants.APPLICATION_JSON;
            if(_DEBUG)
            	log.debug("json: requestString="+requestString);
            requestString = takeOffPrefixAndSuffix(requestString, nsIdJsonPrefix, nsIdJsonSuffix);
            if(_DEBUG)
            	log.debug("json: requestString="+requestString);
        }
        else if(matchesPrefixAndSuffix(requestString, nsIdNTriplesPrefix, nsIdNTriplesSuffix))
        {
            _hasExplicitFormat = true;
            _chosenFormat = Constants.TEXT_PLAIN;
            if(_DEBUG)
            	log.debug("ntriples: requestString="+requestString);
            requestString = takeOffPrefixAndSuffix(requestString, nsIdNTriplesPrefix, nsIdNTriplesSuffix);
            if(_DEBUG)
            	log.debug("ntriples: requestString="+requestString);
        }
        else if(matchesPrefixAndSuffix(requestString, nsIdNQuadsPrefix, nsIdNQuadsSuffix))
        {
            _hasExplicitFormat = true;
            _chosenFormat = Constants.TEXT_X_NQUADS;
            if(_DEBUG)
            	log.debug("nquads: requestString="+requestString);
            requestString = takeOffPrefixAndSuffix(requestString, nsIdNQuadsPrefix, nsIdNQuadsSuffix);
            if(_DEBUG)
            	log.debug("nquads: requestString="+requestString);
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

    private String parseForApiVersion(String requestString)
    {
        String adminConfigurationApiOpeningPrefix = localSettings.getStringProperty("adminConfigurationApiVersionOpeningPrefix", "");
        String adminConfigurationApiClosingPrefix = localSettings.getStringProperty("adminConfigurationApiVersionClosingPrefix", "/");
        String adminConfigurationApiSuffix = localSettings.getStringProperty("adminConfigurationApiVersionSuffix", "");

        // FIXME: this does not work if there is no file format suffix, it will always call the current API version in these cases
        String apiVersionPatternString = "^"+adminConfigurationApiOpeningPrefix+"(\\d+)"+adminConfigurationApiClosingPrefix+"(.*)"+adminConfigurationApiSuffix+"$";
        
        if(_DEBUG)
        {
        	log.debug("apiVersionPatternString="+apiVersionPatternString);
        	log.debug("requestString="+requestString);
        }
        
        Pattern apiVersionPattern = Pattern.compile(apiVersionPatternString);
        
        Matcher matcher = apiVersionPattern.matcher(requestString);
        
        if(!matcher.matches())
        {
            return requestString;
        }
        
        try
        {
            // This will always be a non-negative integer due to the way the pattern matches, but it may be 0 so we correct that case
            _apiVersion = Integer.parseInt(matcher.group(1));

            if(_apiVersion == 0)
            {
                _apiVersion = Settings.CONFIG_API_VERSION;
            }
            else
            {
                _hasExplicitApiVersionValue = true;
            }
        }
        catch(NumberFormatException nfe)
        {
            _apiVersion = Settings.CONFIG_API_VERSION;
            log.error("ConfigurationQueryOptions: nfe: check the adminConfigurationApiOpeningPrefix for the likely mistake", nfe);
        }
        
        requestString = matcher.group(2);
        
        if(_DEBUG)
        {
        	log.debug("apiVersion="+_apiVersion);
        	log.debug("requestString="+requestString);
        }
        
        return requestString;
    }
    
    public boolean isRefresh()
    {
        return _isRefresh;
    }
    
    public boolean containsExplicitFormat()
    {
        return _hasExplicitFormat;
    }
    
    public String getExplicitFormat()
    {
        return _chosenFormat;
    }

    public boolean containsExplicitApiVersion()
    {
        return _hasExplicitApiVersionValue;
    }
    
    public boolean containsAdminConfiguration()
    {
        return _adminConfigurationMatch;
    }
    
    public boolean containsAdminBasicWebappConfiguration()
    {
        return _adminBasicWebappConfigurationMatch;
    }
    
    public int getApiVersion()
    {
        return _apiVersion;
    }

    public boolean isPlainNamespaceAndIdentifier()
    {
        return _isPlainNamespaceAndIdentifier;
    }
    
    public String getParsedRequest()
    {
        return parsedRequestString;
    }
}
