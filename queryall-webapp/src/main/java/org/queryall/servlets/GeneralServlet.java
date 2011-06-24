package org.queryall.servlets;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.openrdf.*;
import org.openrdf.model.*;
import org.openrdf.rio.*;
import org.openrdf.repository.*;
import org.openrdf.repository.sail.*;
import org.openrdf.sail.memory.*;

import org.apache.log4j.Logger;

import org.queryall.impl.*;
import org.bio2rdf.servlets.queryparsers.*;
import org.bio2rdf.servlets.html.*;
import org.queryall.queryutils.*;
import org.queryall.helpers.*;
import org.queryall.api.HttpProvider;
import org.queryall.api.Profile;
import org.queryall.api.QueryType;
import org.queryall.blacklist.*;

/** 
 * 
 */

public class GeneralServlet extends HttpServlet 
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 997653377781136004L;
	
	public static final Logger log = Logger.getLogger(GeneralServlet.class.getName());
    public static final boolean _TRACE = log.isTraceEnabled();
    public static final boolean _DEBUG = log.isDebugEnabled();
    public static final boolean _INFO = log.isInfoEnabled();

    
    @Override
    public void doGet(HttpServletRequest request,
                    HttpServletResponse response)
        throws ServletException, IOException 
    {
        Date queryStartTime = new Date();
        
    	Settings localSettings = Settings.getSettings();
    	BlacklistController localBlacklistController = BlacklistController.getDefaultController();

    	DefaultQueryOptions requestQueryOptions = new DefaultQueryOptions(request.getRequestURI(), request.getContextPath(), localSettings);
        
        
        String realHostName = request.getScheme() + "://" + request.getServerName() + (request.getServerPort() == 80 && request.getScheme().equals("http") ? "" : ":"+ request.getServerPort())+"/";
        
        String serverName = request.getServerName();
        
        String queryString = requestQueryOptions.getParsedRequest();
        
        String requesterIpAddress = request.getRemoteAddr();
        
        String locale = request.getLocale().toString();
        String userAgentHeader = request.getHeader("User-Agent");

        // TODO FIXME: The content type negotiator does not work with locales yet        
        // String preferredLocale = QueryallLanguageNegotiator.getResponseLanguage(locale, userAgentHeader);
        
        String characterEncoding = request.getCharacterEncoding();
        
        if(_INFO)
        {
            log.info("GeneralServlet: locale="+locale+" characterEncoding="+characterEncoding);
        }
        
        // default to 200 for response...
        int responseCode = HttpServletResponse.SC_OK;
        
        boolean isPretendQuery = requestQueryOptions.isQueryPlanRequest();
        int pageOffset = requestQueryOptions.getPageOffset();
        
        String originalAcceptHeader = request.getHeader("Accept");
        String acceptHeader = "";
        
        if(originalAcceptHeader == null || originalAcceptHeader.equals(""))
        {
            acceptHeader = localSettings.getStringPropertyFromConfig(Constants.PREFERRED_DISPLAY_CONTENT_TYPE, Constants.APPLICATION_RDF_XML);
        }
        else
        {
            acceptHeader = originalAcceptHeader;
        }
        
        if(userAgentHeader == null)
        {
            userAgentHeader = "";
        }
        
        // if(!localSettings.USER_AGENT_BLACKLIST_REGEX.trim().equals(""))
        // {
            // Matcher userAgentBlacklistMatcher = localSettings.USER_AGENT_BLACKLIST_PATTERN.matcher(userAgentHeader);
            // 
            // if(userAgentBlacklistMatcher.find())
            // {
                // log.error("GeneralServlet: found blocked user-agent userAgentHeader="+userAgentHeader + " queryString="+queryString+" requesterIpAddress="+requesterIpAddress);
                // 
                // response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                // response.sendRedirect(localSettings.getStringPropertyFromConfig("blacklistRedirectPage"));
                // return;
            // }
        // }
        
        String originalRequestedContentType = QueryallContentNegotiator.getResponseContentType(acceptHeader, userAgentHeader);
        
        String requestedContentType = originalRequestedContentType;
        
        if(requestQueryOptions.containsExplicitFormat())
        {
            String explicitUrlContentType = requestQueryOptions.getExplicitFormat();
            
            if(_DEBUG)
            {
                log.debug("GeneralServlet: found explicitUrlContentType="+explicitUrlContentType);
            }
            
            // override whatever was requested with the urlrewrite variable
            requestedContentType = explicitUrlContentType;
        }

        // Make sure that their requestedContentType is valid as an RDFFormat, or is text/html using this method
        requestedContentType = RdfUtils.findWriterFormat(requestedContentType, localSettings.getStringPropertyFromConfig(Constants.PREFERRED_DISPLAY_CONTENT_TYPE, Constants.APPLICATION_RDF_XML), Constants.APPLICATION_RDF_XML);
        
        // this will be null if they chose text/html, but it will be a valid format in other cases due to the above method
        RDFFormat writerFormat = RdfUtils.getWriterFormat(requestedContentType);
        
        // allow for users to perform redirections if the query did not contain an explicit format
        // some Linked Data provenance systems cannot handle the fact that a document would be delivered from an address that also had "real world"(TM) RDF statements including the URI
        // TODO: cleanup this code
        if(!requestQueryOptions.containsExplicitFormat())
        {
        	if(localSettings.getBooleanPropertyFromConfig("alwaysRedirectToExplicitFormatUrl", false))
        	{
        		int redirectCode = localSettings.getIntPropertyFromConfig("redirectToExplicitFormatHttpCode", 303);
        		
        		StringBuilder redirectString = new StringBuilder();
        		boolean ignoreContextPath = false;
        		
        		if(localSettings.getBooleanPropertyFromConfig("useHardcodedRequestHostname", false))
        		{
        			redirectString.append(localSettings.getStringPropertyFromConfig("hardcodedRequestHostname", ""));
        		}
        		
        		if(localSettings.getBooleanPropertyFromConfig("useHardcodedRequestContext", false))
        		{
        			redirectString.append(localSettings.getStringPropertyFromConfig("hardcodedRequestContext", ""));
        			ignoreContextPath = true;
        		}
        		
        		if(!ignoreContextPath)
    			{
        			if(request.getContextPath().equals(""))
        			{
        				redirectString.append("/");
        			}
        			else
        			{
        				redirectString.append(request.getContextPath());
        				redirectString.append("/");
        			}
        			
        		}
        		
				if(requestedContentType.equals(Constants.TEXT_HTML))
        		{
        			redirectString.append(localSettings.getStringPropertyFromConfig("htmlUrlPrefix", "page/"));

        			if(requestQueryOptions.isQueryPlanRequest())
        			{
        				redirectString.append(localSettings.getStringPropertyFromConfig("queryplanUrlPrefix","queryplan/"));
        			}
        			
        			if(requestQueryOptions.containsExplicitPageOffsetValue())
        			{
        				redirectString.append(localSettings.getStringPropertyFromConfig("pageoffsetUrlOpeningPrefix","pageoffset"));
        				redirectString.append(requestQueryOptions.getPageOffset());
        				redirectString.append(localSettings.getStringPropertyFromConfig("pageoffsetUrlClosingPrefix","/"));
        			}
        			
    				redirectString.append(requestQueryOptions.getParsedRequest());

        			if(requestQueryOptions.containsExplicitPageOffsetValue())
        			{
        				redirectString.append(localSettings.getStringPropertyFromConfig("pageoffsetUrlSuffix",""));
        			}
        			
        			if(requestQueryOptions.isQueryPlanRequest())
        			{
        				redirectString.append(localSettings.getStringPropertyFromConfig("queryplanUrlSuffix",""));
        			}
        			
    				redirectString.append(localSettings.getStringPropertyFromConfig("htmlUrlSuffix", ""));
        		}
        		else if(requestedContentType.equals(Constants.TEXT_RDF_N3))
        		{
        			redirectString.append(localSettings.getStringPropertyFromConfig("n3UrlPrefix", "n3/"));

        			if(requestQueryOptions.isQueryPlanRequest())
        			{
        				redirectString.append(localSettings.getStringPropertyFromConfig("queryplanUrlPrefix","queryplan/"));
        			}
        			
        			if(requestQueryOptions.containsExplicitPageOffsetValue())
        			{
        				redirectString.append(localSettings.getStringPropertyFromConfig("pageoffsetUrlOpeningPrefix","pageoffset"));
        				redirectString.append(requestQueryOptions.getPageOffset());
        				redirectString.append(localSettings.getStringPropertyFromConfig("pageoffsetUrlClosingPrefix","/"));
        			}
        			
    				redirectString.append(requestQueryOptions.getParsedRequest());

        			if(requestQueryOptions.containsExplicitPageOffsetValue())
        			{
        				redirectString.append(localSettings.getStringPropertyFromConfig("pageoffsetUrlSuffix",""));
        			}
        			
        			if(requestQueryOptions.isQueryPlanRequest())
        			{
        				redirectString.append(localSettings.getStringPropertyFromConfig("queryplanUrlSuffix",""));
        			}
        			
        			redirectString.append(localSettings.getStringPropertyFromConfig("n3UrlSuffix", ""));
        		}
        		else if(requestedContentType.equals(Constants.APPLICATION_RDF_XML))
        		{
        			redirectString.append(localSettings.getStringPropertyFromConfig("rdfXmlUrlPrefix", "rdfxml/"));

        			if(requestQueryOptions.isQueryPlanRequest())
        			{
        				redirectString.append(localSettings.getStringPropertyFromConfig("queryplanUrlPrefix","queryplan/"));
        			}
        			
        			if(requestQueryOptions.containsExplicitPageOffsetValue())
        			{
        				redirectString.append(localSettings.getStringPropertyFromConfig("pageoffsetUrlOpeningPrefix","pageoffset"));
        				redirectString.append(requestQueryOptions.getPageOffset());
        				redirectString.append(localSettings.getStringPropertyFromConfig("pageoffsetUrlClosingPrefix","/"));
        			}
        			
    				redirectString.append(requestQueryOptions.getParsedRequest());

        			if(requestQueryOptions.containsExplicitPageOffsetValue())
        			{
        				redirectString.append(localSettings.getStringPropertyFromConfig("pageoffsetUrlSuffix",""));
        			}
        			
        			if(requestQueryOptions.isQueryPlanRequest())
        			{
        				redirectString.append(localSettings.getStringPropertyFromConfig("queryplanUrlSuffix",""));
        			}
        			
        			redirectString.append(localSettings.getStringPropertyFromConfig("rdfXmlUrlSuffix", ""));
        		}
        		
        		else if(requestedContentType.equals(Constants.APPLICATION_JSON))
        		{
        			redirectString.append(localSettings.getStringPropertyFromConfig("jsonUrlPrefix", "json/"));

        			if(requestQueryOptions.isQueryPlanRequest())
        			{
        				redirectString.append(localSettings.getStringPropertyFromConfig("queryplanUrlPrefix","queryplan/"));
        			}
        			
        			if(requestQueryOptions.containsExplicitPageOffsetValue())
        			{
        				redirectString.append(localSettings.getStringPropertyFromConfig("pageoffsetUrlOpeningPrefix","pageoffset"));
        				redirectString.append(requestQueryOptions.getPageOffset());
        				redirectString.append(localSettings.getStringPropertyFromConfig("pageoffsetUrlClosingPrefix","/"));
        			}
        			
    				redirectString.append(requestQueryOptions.getParsedRequest());

        			if(requestQueryOptions.containsExplicitPageOffsetValue())
        			{
        				redirectString.append(localSettings.getStringPropertyFromConfig("pageoffsetUrlSuffix",""));
        			}
        			
        			if(requestQueryOptions.isQueryPlanRequest())
        			{
        				redirectString.append(localSettings.getStringPropertyFromConfig("queryplanUrlSuffix",""));
        			}
        			
        			redirectString.append(localSettings.getStringPropertyFromConfig("jsonUrlSuffix", ""));
        		}
        		
        		log.warn("Sending redirect using redirectCode="+redirectCode+" to redirectString="+redirectString.toString());
        		log.warn("contextPath="+request.getContextPath());
        		response.setStatus(redirectCode);
    			response.setHeader("Location",redirectString.toString());
    			return;
        	}
        }
        
        
        localSettings.configRefreshCheck(false);
        
        localBlacklistController.doBlacklistExpiry();
        
        boolean useDefaultProviders = true;
        
        if(_INFO)
        {
            log.info("GeneralServlet: query started on "+serverName+" requesterIpAddress="+requesterIpAddress+" queryString="+queryString+" explicitPageOffset="+requestQueryOptions.containsExplicitPageOffsetValue()+" pageOffset="+pageOffset+" isPretendQuery="+isPretendQuery+" useDefaultProviders="+useDefaultProviders);
            log.info("GeneralServlet: requestedContentType="+requestedContentType+ " acceptHeader="+request.getHeader("Accept")+" userAgent="+request.getHeader("User-Agent"));
            
            if(!originalRequestedContentType.equals(requestedContentType))
            {
                log.info("GeneralServlet: originalRequestedContentType was overwritten originalRequestedContentType="+originalRequestedContentType+" requestedContentType="+requestedContentType);
            }
        }
        
        if(localBlacklistController.isClientBlacklisted(requesterIpAddress))
        {
            log.warn("GeneralServlet: sending requesterIpAddress="+requesterIpAddress+" to blacklist redirect page");
            
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setHeader("Location", localSettings.getStringPropertyFromConfig("blacklistRedirectPage", "/error/blacklist"));
            return;
        }
        
        response.setHeader("X-Application", localSettings.getStringPropertyFromConfig("userAgent", "queryall") + "/"+Settings.VERSION);
        
        List<Profile> includedProfiles = localSettings.getAndSortProfileList(localSettings.getURICollectionPropertiesFromConfig("activeProfiles"), Constants.LOWEST_ORDER_FIRST);
        
        RdfFetchController fetchController = new RdfFetchController(localSettings, localBlacklistController, queryString, includedProfiles, useDefaultProviders, realHostName, pageOffset, requestedContentType);
        
        Collection<QueryBundle> multiProviderQueryBundles = fetchController.getQueryBundles();
        
        Collection<String> debugStrings = new ArrayList<String>(multiProviderQueryBundles.size()+5);
        
        Writer out = new OutputStreamWriter(response.getOutputStream(), Charset.forName("UTF-8"));

        try
        {
            Repository myRepository = new SailRepository(new MemoryStore());
            myRepository.initialize();
            RepositoryConnection myRepositoryConnection = myRepository.getConnection();
            
            if(isPretendQuery)
            {
                if(_DEBUG)
                {
                    log.debug("GeneralServlet: Found pretend query");
                }
                
                response.setCharacterEncoding("UTF-8");
                response.setContentType(requestedContentType);
                response.setCharacterEncoding("UTF-8");
                response.setStatus(responseCode);
                response.flushBuffer();
                
                // Start sending output before we fetch the rdf so the client doesn't decide to timeout or re-request
                // version = RdfUtils.xmlEncodeString(version).replace("--","- -");
                
                if(requestedContentType.equals(Constants.APPLICATION_RDF_XML) || requestedContentType.equals(Constants.TEXT_HTML))
                {
                    // always print the version number out for debugging
                    // debugStrings.add("<!-- bio2rdf sourceforge package version ("+ version +") -->");
                    // debugStrings.add("<!-- active profiles="+RdfUtils.xmlEncodeString(localSettings.USER_PROFILE_LIST_STRING)+" -->\n");
                    
                    // if(_INFO)
                    // {
                        // subversionId = RdfUtils.xmlEncodeString(subversionId).replace("--","- -");
                        // debugStrings.add("<!-- bio2rdf sourceforge subversion copy Id ("+ subversionId +") -->");
                        // propertiesSubversionId = RdfUtils.xmlEncodeString(propertiesSubversionId).replace("--","- -");
                        // debugStrings.add("<!-- bio2rdf sourceforge properties file subversion copy Id ("+ propertiesSubversionId +") -->");
                    // }
                }
                else if(requestedContentType.equals(Constants.TEXT_RDF_N3))
                {
                    // always print the version number out for debugging
                    // debugStrings.add("# bio2rdf sourceforge package version ("+ version.replace("\n","").replace("\r","") +")");
                    // debugStrings.add("# active profiles="+RdfUtils.xmlEncodeString(localSettings.USER_PROFILE_LIST_STRING)+"");
                    // 
                    // if(_INFO)
                    // {
                        // // debugStrings.add("# bio2rdf sourceforge subversion copy Id ("+ subversionId.replace("\n","").replace("\r","") +")");
                        // 
                        // // debugStrings.add("# bio2rdf sourceforge properties file subversion copy Id ("+ propertiesSubversionId.replace("\n","").replace("\r","") +")");
                    // }
                }
                
                for(QueryBundle nextScheduledQueryBundle : multiProviderQueryBundles)
                {
                    // log.trace("GeneralServlet: about to generate rdf for query bundle with key="+queryString+localSettings.getStringPropertyFromConfig("separator")+nextScheduledQueryBundle.originalProvider.getKey().toLowerCase()+localSettings.getStringPropertyFromConfig("separator")+nextScheduledQueryBundle.getQueryType().getKey().toLowerCase()+localSettings.getStringPropertyFromConfig("separator")+nextScheduledQueryBundle.queryEndpoint);
                    
                    nextScheduledQueryBundle.toRdf(
                        myRepository, 
                        StringUtils.createURI(StringUtils.percentEncode(queryString)
                        +localSettings.getStringPropertyFromConfig("separator", "")+"pageoffset"+pageOffset
                        +localSettings.getStringPropertyFromConfig("separator", "")+StringUtils.percentEncode(nextScheduledQueryBundle.getOriginalProvider().getKey().stringValue().toLowerCase())
                        +localSettings.getStringPropertyFromConfig("separator", "")+StringUtils.percentEncode(nextScheduledQueryBundle.getQueryType().getKey().stringValue().toLowerCase())
                        +localSettings.getStringPropertyFromConfig("separator", "")+StringUtils.percentEncode(nextScheduledQueryBundle.getQueryEndpoint()))
                        , Settings.CONFIG_API_VERSION);
                }
                
                if(_TRACE)
                {
                    log.trace("GeneralServlet: Finished with pretend query bundle rdf generation");
                }
            } // end isPretendQuery
            else if(!fetchController.queryKnown())
            {
                if(_DEBUG)
                {
                    log.debug("GeneralServlet: starting !fetchController.queryKnown() section");
                }
                
                
                // change response code to indicate that the query was in some way incorrect according to our current knowledge
                if(fetchController.anyNamespaceNotRecognised())
                {
                	// 404 for document not found, as a query type matched somewhere without having the namespace recognised
                	// There are still no results, but this is a more specific exception
                    responseCode = localSettings.getIntPropertyFromConfig("unknownNamespaceHttpResponseCode", 404);
                }
                else
                {
                	// 400 for query completely unrecognised, even when not including namespace in each query type calculation
                	responseCode = localSettings.getIntPropertyFromConfig("unknownQueryHttpResponseCode", 400);
                }
                
                response.setContentType(requestedContentType+"; charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(responseCode);
                response.flushBuffer();
                                
                Collection<String> currentStaticStrings = new HashSet<String>();
                
                Collection<String> backupStaticRdfXmlStrings = new HashSet<String>();
                
                Collection<URI> staticQueryTypesForUnknown = null;
                
                if(fetchController.anyNamespaceNotRecognised())
                    staticQueryTypesForUnknown = localSettings.getURICollectionPropertiesFromConfig("unknownNamespaceStaticAdditions");
                else
                	staticQueryTypesForUnknown = localSettings.getURICollectionPropertiesFromConfig("unknownQueryStaticAdditions");

                for(URI nextStaticQueryTypeForUnknown : staticQueryTypesForUnknown)
                {
                    if(_DEBUG)
                    {
                        log.debug("GeneralServlet: nextStaticQueryTypeForUnknown="+nextStaticQueryTypeForUnknown);
                    }
                    
                    Collection<QueryType> allCustomRdfXmlIncludeTypes = localSettings.getQueryTypesByUri(nextStaticQueryTypeForUnknown);
                    
                    // use the closest matches, even though they didn't eventuate into actual planned query bundles they matched the query string somehow
                    for(QueryType nextQueryType : allCustomRdfXmlIncludeTypes)
                    {
                        Map<String, String> attributeList = QueryCreator.getAttributeListFor(nextQueryType, new ProviderImpl(), queryString, localSettings.getStringPropertyFromConfig("hostName", ""), realHostName, pageOffset, localSettings);
                        
                        String nextBackupString = QueryCreator.createStaticRdfXmlString(nextQueryType, nextQueryType, new ProviderImpl(), attributeList, includedProfiles, localSettings.getBooleanPropertyFromConfig("recogniseImplicitRdfRuleInclusions", true) , localSettings.getBooleanPropertyFromConfig("includeNonProfileMatchedRdfRules", true), localSettings) + "\n";
                        
                        nextBackupString = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">" + nextBackupString + "</rdf:RDF>";
                        
                        currentStaticStrings.add(nextBackupString);
                    }
                }
                
                if(currentStaticStrings.size() == 0)
                {
                    log.error("Could not find anything at all to match at query level queryString="+queryString);
                    
                    if(requestedContentType.equals("application/rdf+xml") || requestedContentType.equals("text/html"))
                    {
                        debugStrings.add("<!-- Could not find anything at all to match at query level queryString="+StringUtils.xmlEncodeString(queryString).replace("--","- -")+" -->");
                    }
                    else if(requestedContentType.equals("text/rdf+n3"))
                    {
                        debugStrings.add("# Could not find anything at all to match at query level queryString="+queryString.replace("\n","").replace("\r",""));
                    }
                }
                
                // make one pass through the static strings adding them to the repository
                for(String nextUniqueStaticString : currentStaticStrings)
                {
                    try
                    {
                        myRepositoryConnection.add(new java.io.StringReader(nextUniqueStaticString), localSettings.getDefaultHostAddress()+queryString, RDFFormat.RDFXML);
                    }
                    catch(org.openrdf.rio.RDFParseException rdfpe)
                    {
                        log.error("GeneralServlet: RDFParseException: static RDF "+rdfpe.getMessage());
                        log.error("GeneralServlet: nextUniqueStaticString="+nextUniqueStaticString);
                    }
                }
                
                if(_TRACE)
                {
                    log.trace("GeneralServlet: ending !fetchController.queryKnown() section");
                }
            }
            else // fetchController.queryKnown
            {
                // for now we redirect if we find any in the set that have redirect enabled as HTTP GET URL's, otherwise fall through to the POST SPARQL RDF/XML and GET URL fetching
                for(QueryBundle nextScheduledQueryBundle : multiProviderQueryBundles)
                {
                	if(nextScheduledQueryBundle.getProvider() != null && nextScheduledQueryBundle.getProvider() instanceof HttpProvider)
                	{
                		HttpProvider nextScheduledHttpProvider = (HttpProvider)nextScheduledQueryBundle.getProvider();

	                    if(nextScheduledHttpProvider.hasEndpointUrl() 
	                        //&& nextScheduledHttpProvider.isHttpGetUrl()
	                        && nextScheduledQueryBundle.getProvider().needsRedirect()
	                    )
	                    {
	                        response.sendRedirect(nextScheduledQueryBundle.getQueryEndpoint());
	                        
	                        return;
	                    }
                	}
                }
                
                response.setContentType(requestedContentType+"; charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                
                // 3. Attempt to fetch information as needed
                fetchController.fetchRdfForQueries();
                
                // keep track of the strings so that we don't print multiples of exactly the same information more than once
                Collection<String> currentStaticStrings = new HashSet<String>();
                
                response.setStatus(responseCode);
                response.flushBuffer();
                
                // version = RdfUtils.xmlEncodeString(version).replace("--","- -");
                
                if(requestedContentType.equals(Constants.APPLICATION_RDF_XML) || requestedContentType.equals(Constants.TEXT_HTML))
                {
                    // always print the version number out for debugging
                    // debugStrings.add("<!-- bio2rdf sourceforge package version ("+ version +") -->\n");
                    // debugStrings.add("<!-- active profiles="+RdfUtils.xmlEncodeString(localSettings.USER_PROFILE_LIST_STRING)+" -->\n");
                    
                    if(_INFO)
                    {
                        // subversionId = RdfUtils.xmlEncodeString(subversionId).replace("--","- -");
                        // debugStrings.add("<!-- bio2rdf sourceforge subversion copy Id ("+ subversionId +") -->\n");
                        // propertiesSubversionId = RdfUtils.xmlEncodeString(propertiesSubversionId).replace("--","- -");
                        // debugStrings.add("<!-- bio2rdf sourceforge properties file subversion copy Id ("+ propertiesSubversionId +") -->\n");
                        debugStrings.add("<!-- result units="+fetchController.getResults().size()+" -->\n");
                    }
                }
                else if(requestedContentType.equals(Constants.TEXT_RDF_N3))
                {
                    // always print the version number out for debugging
                    // debugStrings.add("# bio2rdf sourceforge package version ("+ version.replace("\n","").replace("\r","") +")\n");
                    // debugStrings.add("# active profiles="+RdfUtils.xmlEncodeString(localSettings.USER_PROFILE_LIST_STRING)+"");
                    
                    if(_INFO)
                    {
                        // debugStrings.add("# bio2rdf sourceforge subversion copy Id ("+ subversionId.replace("\n","").replace("\r","") +")\n");
                        // 
                        // debugStrings.add("# bio2rdf sourceforge properties file subversion copy Id ("+ propertiesSubversionId.replace("\n","").replace("\r","") +")\n");
                        debugStrings.add("# result units="+fetchController.getResults().size()+" \n");
                    }
                }
                
                for(RdfFetcherQueryRunnable nextResult : fetchController.getResults())
                {
                    if(requestedContentType.equals(Constants.APPLICATION_RDF_XML) || requestedContentType.equals(Constants.TEXT_HTML))
                    {
                        // only write out the debug strings to the document if we are at least at the info or debug levels
                        if(_INFO)
                        {
                            debugStrings.add("<!-- "+StringUtils.xmlEncodeString(nextResult.getResultDebugString()).replace("--","- -") + "-->");
                        }
                    }
                    else if(requestedContentType.equals(Constants.TEXT_RDF_N3))
                    {
                        if(_INFO)
                        {
                            debugStrings.add("# "+ nextResult.getResultDebugString().replace("\n","").replace("\r","") +")");
                        }
                    }
                    
                    if(_TRACE)
                    {
                        log.trace("GeneralServlet: normalised result string : " + nextResult.getNormalisedResult());
                    }
                    Repository tempRepository = new SailRepository(new MemoryStore());
                    tempRepository.initialize();
                    
                    RdfUtils.insertResultIntoRepository(nextResult, tempRepository, localSettings);
                    
                    tempRepository = (Repository)QueryCreator.normaliseByStage(
                        NormalisationRuleImpl.getRdfruleStageAfterResultsImport(),
                        tempRepository, 
                        localSettings.getNormalisationRulesForUris(nextResult.getOriginalQueryBundle().getProvider().getNormalisationUris(), 
                            Constants.HIGHEST_ORDER_FIRST ), 
                        includedProfiles, localSettings.getBooleanPropertyFromConfig("recogniseImplicitRdfRuleInclusions", true), localSettings.getBooleanPropertyFromConfig("includeNonProfileMatchedRdfRules", true) );
                    
                    RepositoryConnection tempRepositoryConnection = tempRepository.getConnection();
                    
                    if(_DEBUG)
                    {
                        log.debug("GeneralServlet: getAllStatementsFromRepository(tempRepository).size()="+RdfUtils.getAllStatementsFromRepository(tempRepository).size());
                        log.debug("GeneralServlet: tempRepositoryConnection.size()=" + tempRepositoryConnection.size());
                    }
                    
                    RdfUtils.copyAllStatementsToRepository(myRepository, tempRepository);
                }
                    
                for(QueryBundle nextPotentialQueryBundle : multiProviderQueryBundles)
                {
                    String nextStaticString = nextPotentialQueryBundle.getStaticRdfXmlString();
                    
                    if(_TRACE)
                    {
                        log.trace("GeneralServlet: Adding static RDF/XML string nextPotentialQueryBundle.getQueryType().getKey()="+nextPotentialQueryBundle.getQueryType().getKey()+" nextStaticString="+nextStaticString);
                    }
                    
                    nextStaticString = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">" + nextStaticString + "</rdf:RDF>";
                    
                    currentStaticStrings.add(nextStaticString);
                }
                
                // make one pass through the static strings adding them to the repository
                for(String nextUniqueStaticString : currentStaticStrings)
                {
                    try
                    {
                        myRepositoryConnection.add(new java.io.StringReader(nextUniqueStaticString), localSettings.getDefaultHostAddress()+queryString, RDFFormat.RDFXML);
                    }
                    catch(org.openrdf.rio.RDFParseException rdfpe)
                    {
                        log.error("GeneralServlet: RDFParseException: static RDF "+rdfpe.getMessage());
                        log.error("GeneralServlet: nextUniqueStaticString="+nextUniqueStaticString);
                    }
                }
            } // end else !isPretendQuery
            
            if(myRepositoryConnection != null)
            {
                myRepositoryConnection.close();
            }
            
            // Normalisation Stage : after results to pool
            
            // For each of the providers, get the rules, and universally sort them and perform a single normalisation for this stage
            
            Repository convertedPool = (Repository)QueryCreator.normaliseByStage(
                NormalisationRuleImpl.getRdfruleStageAfterResultsToPool(),
                myRepository, 
                localSettings.getSortedRulesForProviders(fetchController.getAllUsedProviders(), 
                    Constants.HIGHEST_ORDER_FIRST ), 
                includedProfiles, 
                localSettings.getBooleanPropertyFromConfig("recogniseImplicitRdfRuleInclusions", true), 
                localSettings.getBooleanPropertyFromConfig("includeNonProfileMatchedRdfRules", true) );
            
            
            java.io.StringWriter cleanOutput = new java.io.StringWriter();
            
            //java.io.StringWriter cleanOutput = new java.io.StringWriter(new BufferedWriter(new CharArrayWriter()));
            
            if(requestedContentType.equals(Constants.TEXT_HTML))
            {
                if(_DEBUG)
                {
                    log.debug("GeneralServlet: about to call html rendering method");
                    log.debug("GeneralServlet: fetchController.queryKnown()="+fetchController.queryKnown());
                }
                
                try
                {
                    HtmlPageRenderer.renderHtml(getServletContext(), convertedPool, cleanOutput, fetchController, debugStrings, queryString, localSettings.getDefaultHostAddress() + queryString, realHostName, request.getContextPath(), pageOffset, localSettings);
                }
                catch(OpenRDFException ordfe)
                {
                    log.error("GeneralServlet: couldn't render HTML because of an RDF exception", ordfe);
                }
                catch(Exception ex)
                {
                    log.error("GeneralServlet: couldn't render HTML because of an unknown exception", ex);
                }
            }
            else
            {
                RdfUtils.toWriter(convertedPool, cleanOutput, writerFormat);
            }
            
            //String actualRdfString = cleanOutput.toString();
            
//            if(_TRACE)
//            {
//                log.trace("GeneralServlet: actualRdfString="+actualRdfString);
//            }
            
            if(requestedContentType.equals(Constants.APPLICATION_RDF_XML))
            {
                out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
                
                for(String nextDebugString : debugStrings)
                {
                    out.write(nextDebugString+"\n");
                }
                StringBuffer buffer = cleanOutput.getBuffer();

                // HACK: aduna sesame developers refuse to believe that anyone would want to get an RDF/XML string without the xml PI, so this is to get around their stubborness
                if(buffer.length()>38)
                {
	                for(int i = 38; i < cleanOutput.getBuffer().length(); i++)
	                	out.write(buffer.charAt(i));
	                // HACK: can't find a way to get sesame to print out the rdf without the xml PI
	                //out.write(actualRdfString.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", ""));
                }
            }
            else if(requestedContentType.equals(Constants.TEXT_RDF_N3))
            {
                for(String nextDebugString : debugStrings)
                {
                    out.write(nextDebugString+"\n");
                }

                StringBuffer buffer = cleanOutput.getBuffer();
                for(int i = 0; i < cleanOutput.getBuffer().length(); i++)
                	out.write(buffer.charAt(i));
                //out.write(actualRdfString);
            }
            else
            {
                // log.error("entering UTF-8 conversion section");
                
                // try{
                    // byte[] bytes = new byte[actualRdfString.length()];
                    // for (int i = 0; i < actualRdfString.length(); i++) 
                    // {
                        // bytes[i] = (byte) actualRdfString.charAt(i);
                    // }
                    // 
                    // actualRdfString = new String(bytes, "UTF-8");
                // }
                // catch(java.io.UnsupportedEncodingException 
                // {
                    // log.error("GeneralServlet: unsupported encoding exception for UTF-8");
                // }
                
                StringBuffer buffer = cleanOutput.getBuffer();
                for(int i = 0; i < cleanOutput.getBuffer().length(); i++)
                	out.write(buffer.charAt(i));
                //out.write(actualRdfString);
            }
            out.flush();
            
            Date queryEndTime = new Date();
            
            long nextTotalTime = queryEndTime.getTime()-queryStartTime.getTime();
            
            // Housekeeping
            
            // update a static record of the blacklist
            localBlacklistController.accumulateBlacklist(fetchController.getErrorResults(), localSettings.getLongPropertyFromConfig("blacklistResetPeriodMilliseconds", 3600000), localSettings.getBooleanPropertyFromConfig("blacklistResetClientBlacklistWithEndpoints", false));
             
            if(localSettings.getBooleanPropertyFromConfig("blacklistResetEndpointFailuresOnSuccess", true))
            {
            	localBlacklistController.removeEndpointsFromBlacklist(fetchController.getSuccessfulResults(), nextTotalTime, useDefaultProviders);
            }
            
            
            // QueryDebug nextQueryDebug = null;
            
            // Don't keep local error statistics if GeneralServlet debug level is higher than or equal to info and we aren't interested in using the client IP blacklist functionalities
            // if(INFO || localSettings.getBooleanPropertyFromConfig("automaticallyBlacklistClients"))
            // {
                // nextQueryDebug = new QueryDebug();
                // nextQueryDebug.clientIPAddress = requesterIpAddress;
                // 
                // nextQueryDebug.totalTimeMilliseconds = nextTotalTime;
                // nextQueryDebug.queryString = queryString;
                // 
                // Collection<String> queryTitles = new HashSet<String>();
                // 
                // for(QueryBundle nextInitialQueryBundle : multiProviderQueryBundles)
                // {
                    // queryTitles.add(nextInitialQueryBundle.getQueryType().getKey());
                // }
                // 
                // nextQueryDebug.matchingQueryTitles = queryTitles;
                // 
                // BlacklistController.accumulateQueryDebug(nextQueryDebug);
                // 
                // if(_INFO)
                // {
                    // log.info("GeneralServlet: query complete requesterIpAddress="+requesterIpAddress+" queryString="+queryString + " pageOffset="+pageOffset+" totalTime="+nextTotalTime);
                // }
            // }
            // 
            // if(localSettings.SUBMIT_USAGE_STATISTICS && !localSettings.getBooleanPropertyFromConfig("statisticsSubmitStatistics"))
            // {
                // Collection<StatisticsEntry> statisticsEntryList = new HashSet<StatisticsEntry>();
                // 
                // Collection<String> profileUris = new HashSet<String>();
                // 
                // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_PROFILES))
                // {
                    // profileUris = localSettings.getStringCollectionPropertiesFromConfig("activeProfiles");
                // }
                // 
                // Collection<String> configLocations = new HashSet<String>();
                // 
                // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_CONFIGLOCATIONS))
                // {
                    // configLocations = localSettings.CONFIG_LOCATION_LIST;
                // }
                // 
                // Collection<String> querytypeUris = new HashSet<String>();
                // 
                // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_QUERYTYPES))
                // {
                    // // TODO: decide whether we only want the final matching query types or all of the initial match query types
                    // // for(QueryType nextRelevantQuery : relevantCustomQueries)
                        // // querytypeUris.add(nextRelevantQuery.getKey());
                    // 
                    // for(QueryBundle nextInitialQueryBundle : multiProviderQueryBundles)
                        // querytypeUris.add(nextInitialQueryBundle.getQueryType().getKey());
                // }
                // 
                // // TODO: organise how to get this information out effectively, hopefully without doing the regular expression matching again
                // Collection<String> namespaceUris = new HashSet<String>();
                // 
                // String configVersion = "";
                // 
                // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_CONFIGVERSION))
                // {
                    // configVersion = localSettings.getSettings().CONFIG_API_VERSION+"";
                // }
                // 
                // int readtimeout = 0;
                // 
                // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_READTIMEOUT))
                // {
                    // readtimeout = localSettings.getIntPropertyFromConfig("readTimeout");
                // }
                // 
                // int connecttimeout = 0;
                // 
                // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_READTIMEOUT))
                // {
                    // connecttimeout = localSettings.getIntPropertyFromConfig("connectTimeout");
                // }
                // 
                // String userHostAddress = "";
                // 
                // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_USERHOSTADDRESS))
                // {
                    // userHostAddress = requesterIpAddress;
                // }
                // 
                // String userAgent = "";
                // 
                // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_USERAGENT))
                // {
                    // userAgent = userAgentHeader;
                // }
                // 
                // String statisticsRealHostName = "";
                // 
                // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_REALHOSTNAME))
                // {
                    // statisticsRealHostName = realHostName;
                // }
                // 
                // String statisticsQueryString = "";
                // 
                // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_QUERYSTRING))
                // {
                    // statisticsQueryString = queryString;
                // }
                // 
                // long responseTime = -1;
                // 
                // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_RESPONSETIME))
                // {
                    // responseTime = nextTotalTime;
                // }
                // 
                // // double[] testArray = new double[5];
                // // 
                // // testArray[0] = 13.0;
                // // testArray[1] = 23.0;
                // // testArray[2] = 12.0;
                // // testArray[3] = 44.0;
                // // testArray[4] = 55.0;
                // // 
                // // log.info("test standard deviation = "+ localSettings.getStandardDeviation(testArray));
                // 
                // Collection<Long> nonErrorLatencyList = new HashSet<Long>();
                // Collection<Long> errorLatencyList = new HashSet<Long>();
                // 
                // Collection<String> successfulProviderUris = new HashSet<String>();
                // Collection<String> errorProviderUris = new HashSet<String>();
                // 
                // long sumLatency = 0;
                // long sumErrorLatency = 0;
                // long nextLatency = 0;
                // 
                // for(RdfFetcherQueryRunnable nextResult : fetchController.getResults())
                // {
                    // nextLatency = nextResult.queryEndTime.getTime()-nextResult.queryStartTime.getTime();
                    // 
                    // if(nextResult.wasSuccessful)
                    // {
                        // sumLatency += nextLatency;
                        // nonErrorLatencyList.add(nextLatency);
                        // 
                        // if(nextResult.endpointUrl != null && !nextResult.endpointUrl.trim().equals(""))
                        // {
                            // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_SUCCESSFULPROVIDERS))
                            // {
                                // successfulProviderUris.add(nextResult.endpointUrl);
                            // }
                        // }
                    // }
                    // else
                    // {
                        // sumErrorLatency += nextLatency;
                        // errorLatencyList.add(nextLatency);
                        // 
                        // if(nextResult.endpointUrl != null && !nextResult.endpointUrl.trim().equals(""))
                        // {
                            // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_ERRORPROVIDERS))
                            // {
                                // errorProviderUris.add(nextResult.endpointUrl);
                            // }
                        // }
                    // }
                // }
                // 
                // int sumQueries = nonErrorLatencyList.size();
                // int sumErrorQueries = errorLatencyList.size();
                // 
                // long statisticsSumLatency = 0;
                // 
                // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_SUMLATENCY))
                // {
                    // statisticsSumLatency = sumLatency;
                // }
                // 
                // long statisticsSumErrorLatency = 0;
                // 
                // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_SUMERRORLATENCY))
                // {
                    // statisticsSumErrorLatency = sumErrorLatency;
                // }
                // 
                // int statisticsSumQueries = 0;
                // 
                // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_SUMQUERIES))
                // {
                    // statisticsSumQueries = sumQueries;
                // }
                // 
                // int statisticsSumErrorQueries = 0;
                // 
                // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_SUMERRORS))
                // {
                    // statisticsSumErrorQueries = sumErrorQueries;
                // }
                // 
                // double stdevlatency = RdfUtils.getStandardDeviationFromLongs(nonErrorLatencyList);
                // 
                // double statisticsStdevLatency = 0.0;
                // 
                // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_STDEVLATENCY))
                // {
                    // statisticsStdevLatency = stdevlatency;
                // }
                // 
                // double stdeverrorlatency = RdfUtils.getStandardDeviationFromLongs(errorLatencyList);
                // 
                // double statisticsStdevErrorLatency = 0.0;
                // 
                // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_STDEVERRORLATENCY))
                // {
                    // statisticsStdevErrorLatency = stdeverrorlatency;
                // }
                // 
                // String statisticsLastServerRestart = RdfUtils.ISO8601UTC().format(BlacklistController.lastServerStartupDate);
                // 
                // String statisticsServerSoftwareVersion = localSettings.getStringPropertyFromConfig("userAgent");
                // 
                // String statisticsacceptHeader = originalAcceptHeader;
                // 
                // String statisticsrequestedContentType = requestedContentType;
                // 
                // String keyToUse = (queryString.hashCode()*requesterIpAddress.hashCode()) 
                    // + "-" 
                    // + (
                        // (queryStartTime.getTime()*(stdevlatency+1))
                        // /
                        // ((nextTotalTime+1)*localSettings.getIntPropertyFromConfig("connectTimeout"))
                    // );
                // 
                // String key = localSettings.getDefaultHostAddress()
                        // +localSettings.DEFAULT_RDF_STATISTICS_NAMESPACE
                        // +localSettings.getStringPropertyFromConfig("separator")
                        // +RdfUtils.percentEncode(keyToUse);
                // 
                // if(_INFO)
                // {
                    // log.info("GeneralServlet: statistics key="+key);
                // }
                // 
                // statisticsEntryList.add(new StatisticsEntry(
                    // key,
                    // profileUris,
                    // successfulProviderUris,
                    // errorProviderUris,
                    // configLocations,
                    // querytypeUris,
                    // namespaceUris,
                    // configVersion,
                    // readtimeout,
                    // connecttimeout,
                    // userHostAddress,
                    // userAgent,
                    // statisticsRealHostName,
                    // statisticsQueryString,
                    // responseTime,
                    // statisticsSumLatency,
                    // statisticsSumQueries,
                    // statisticsStdevLatency,
                    // statisticsSumErrorQueries,
                    // statisticsSumErrorLatency,
                    // statisticsStdevErrorLatency,
                    // statisticsLastServerRestart,
                    // statisticsServerSoftwareVersion,
                    // statisticsacceptHeader,
                    // statisticsrequestedContentType
                // ));
                // 
                // BlacklistController.persistStatistics(statisticsEntryList, localSettings.getSettings().CONFIG_API_VERSION);
            // }
            
            if(_INFO)
            {
                log.info("GeneralServlet: finished returning information to client requesterIpAddress="+requesterIpAddress+" queryString="+queryString + " pageOffset="+pageOffset+" totalTime="+nextTotalTime);
            }    
        }
        catch(OpenRDFException ordfe)
        {
            log.fatal("GeneralServlet.doGet: caught RDF exception", ordfe);
            throw new RuntimeException("GeneralServlet.doGet failed due to an RDF exception. See log for details");
        }
        catch(InterruptedException iex)
        {
            log.error("GeneralServlet.doGet: caught interrupted exception", iex);
            throw new RuntimeException("GeneralServlet.doGet failed due to an exception. See log for details");
        }
        catch(RuntimeException rex)
        {
            log.error("GeneralServlet.doGet: caught runtime exception", rex);
            
        }
        
    }
  
}

