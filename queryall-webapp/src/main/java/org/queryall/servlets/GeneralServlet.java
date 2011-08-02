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
import org.queryall.servlets.queryparsers.*;
import org.queryall.servlets.html.*;
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
        
        // TODO: should this be configurable or should it be removed?
        boolean useDefaultProviders = true;
        
        RDFFormat writerFormat = null;
        
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
            acceptHeader = localSettings.getStringProperty(Constants.PREFERRED_DISPLAY_CONTENT_TYPE, Constants.APPLICATION_RDF_XML);
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
        
        String originalRequestedContentType = QueryallContentNegotiator.getResponseContentType(acceptHeader, userAgentHeader, localSettings.getStringProperty("preferredDisplayContentType", Constants.APPLICATION_RDF_XML));
        
        String requestedContentType = originalRequestedContentType;
        
        if(requestQueryOptions.containsExplicitFormat())
        {
            String explicitUrlContentType = requestQueryOptions.getExplicitFormat();
            
            if(_DEBUG)
            {
                log.debug("GeneralServlet: found explicitUrlContentType="+explicitUrlContentType);
            }
            
            // override whatever was requested with the variable from the query options
            requestedContentType = explicitUrlContentType;
        }

        // Make sure that their requestedContentType is valid as an RDFFormat, or is text/html using this method
        requestedContentType = RdfUtils.findBestContentType(requestedContentType, localSettings.getStringProperty(Constants.PREFERRED_DISPLAY_CONTENT_TYPE, Constants.APPLICATION_RDF_XML), Constants.APPLICATION_RDF_XML);
        
        // this will be null if they chose text/html, but it will be a valid format in other cases due to the above method
		writerFormat = RdfUtils.getWriterFormat(requestedContentType);
        
        // allow for users to perform redirections if the query did not contain an explicit format
        if(!requestQueryOptions.containsExplicitFormat())
        {
        	if(localSettings.getBooleanProperty("alwaysRedirectToExplicitFormatUrl", false))
        	{
        		int redirectCode = localSettings.getIntProperty("redirectToExplicitFormatHttpCode", 303);
        		
        		StringBuilder redirectString = new StringBuilder();
        		boolean ignoreContextPath = false;
        		
        		getRedirectString(redirectString, request, localSettings, requestQueryOptions, requestedContentType, ignoreContextPath);
        		
        		log.warn("Sending redirect using redirectCode="+redirectCode+" to redirectString="+redirectString.toString());
        		log.warn("contextPath="+request.getContextPath());
        		response.setStatus(redirectCode);
    			response.setHeader("Location",redirectString.toString());
    			return;
        	}
        }
        
        
        localSettings.configRefreshCheck(false);
        
        localBlacklistController.doBlacklistExpiry();
        
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
            response.setHeader("Location", localSettings.getStringProperty("blacklistRedirectPage", "/error/blacklist"));
            return;
        }
        
        response.setHeader("X-Application", localSettings.getStringProperty("userAgent", "queryall") + "/"+Settings.VERSION);
        
        List<Profile> includedProfiles = localSettings.getAndSortProfileList(localSettings.getURIProperties("activeProfiles"), Constants.LOWEST_ORDER_FIRST);
        
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
                
                doQueryPretend(response, localSettings, queryString, responseCode, pageOffset, requestedContentType, multiProviderQueryBundles,
						myRepository);
            } // end isPretendQuery
            else if(!fetchController.queryKnown())
            {
                if(_DEBUG)
                {
                    log.debug("GeneralServlet: starting !fetchController.queryKnown() section");
                }
                
                
                doQueryUnknown(response, localSettings, realHostName, queryString, pageOffset, requestedContentType, includedProfiles,
						fetchController, debugStrings, myRepositoryConnection);
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
                
                doQueryNotPretend(response, localSettings, queryString, responseCode, requestedContentType, includedProfiles, fetchController,
						multiProviderQueryBundles, debugStrings, myRepository, myRepositoryConnection);
            } // end else !isPretendQuery
            
            if(myRepositoryConnection != null)
            {
                myRepositoryConnection.close();
            }
            
            // Normalisation Stage : after results to pool
            
            // For each of the providers, get the rules, and universally sort them and perform a single normalisation for this stage
            
            Repository convertedPool = doPoolNormalisation(localSettings, includedProfiles, fetchController, myRepository);
            
            resultsToWriter(out, request, localSettings, writerFormat, realHostName, queryString, pageOffset, requestedContentType, fetchController,
					debugStrings, convertedPool);
            
            out.flush();
            
            Date queryEndTime = new Date();
            
            long nextTotalTime = queryEndTime.getTime()-queryStartTime.getTime();
            
            // Housekeeping
            
            // update a static record of the blacklist
            localBlacklistController.accumulateBlacklist(fetchController.getErrorResults(), localSettings.getLongProperty("blacklistResetPeriodMilliseconds", 3600000), localSettings.getBooleanProperty("blacklistResetClientBlacklistWithEndpoints", false));
             
            if(localSettings.getBooleanProperty("blacklistResetEndpointFailuresOnSuccess", true))
            {
            	localBlacklistController.removeEndpointsFromBlacklist(fetchController.getSuccessfulResults(), nextTotalTime, useDefaultProviders);
            }
            
            
            QueryDebug nextQueryDebug = null;
            
            // Don't keep local error statistics if GeneralServlet debug level is higher than or equal to info and we aren't interested in using the client IP blacklist functionalities
             if(_INFO || localSettings.getBooleanProperty("automaticallyBlacklistClients", false))
             {
                 nextQueryDebug = new QueryDebug();
                 nextQueryDebug.clientIPAddress = requesterIpAddress;
                 
                 nextQueryDebug.totalTimeMilliseconds = nextTotalTime;
                 nextQueryDebug.queryString = queryString;
                 
                 Collection<URI> queryTitles = new HashSet<URI>();
                 
                 for(QueryBundle nextInitialQueryBundle : multiProviderQueryBundles)
                 {
                     queryTitles.add(nextInitialQueryBundle.getQueryType().getKey());
                 }
                 
                 nextQueryDebug.matchingQueryTitles = queryTitles;
                 
                 localBlacklistController.accumulateQueryDebug(nextQueryDebug, localSettings, 
                		 localSettings.getLongProperty("blacklistResetPeriodMilliseconds", 120000L), 
                		 localSettings.getBooleanProperty("blacklistResetClientBlacklistWithEndpoints", true),
                		 localSettings.getBooleanProperty("automaticallyBlacklistClients", false),
                		 localSettings.getIntProperty("blacklistMinimumQueriesBeforeBlacklistRules", 200),
                		 localSettings.getIntProperty("blacklistClientMaxQueriesPerPeriod", 400));
                 
                 if(_INFO)
                 {
                     log.info("GeneralServlet: query complete requesterIpAddress="+requesterIpAddress+" queryString="+queryString + " pageOffset="+pageOffset+" totalTime="+nextTotalTime);
                 }
             }
             

            
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


	/**
	 * @param response
	 * @param localSettings
	 * @param realHostName
	 * @param queryString
	 * @param pageOffset
	 * @param requestedContentType
	 * @param includedProfiles
	 * @param fetchController
	 * @param debugStrings
	 * @param myRepositoryConnection
	 * @throws IOException
	 * @throws RepositoryException
	 */
	private void doQueryUnknown(HttpServletResponse response, Settings localSettings, String realHostName, String queryString, int pageOffset,
			String requestedContentType, List<Profile> includedProfiles, RdfFetchController fetchController, Collection<String> debugStrings,
			RepositoryConnection myRepositoryConnection) throws IOException, RepositoryException
	{
		int responseCode;
		// change response code to indicate that the query was in some way incorrect according to our current knowledge
		if(fetchController.anyNamespaceNotRecognised())
		{
			// 404 for document not found, as a query type matched somewhere without having the namespace recognised
			// There are still no results, but this is a more specific exception
		    responseCode = localSettings.getIntProperty("unknownNamespaceHttpResponseCode", 404);
		}
		else
		{
			// 400 for query completely unrecognised, even when not including namespace in each query type calculation
			responseCode = localSettings.getIntProperty("unknownQueryHttpResponseCode", 400);
		}
		
		response.setContentType(requestedContentType+"; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(responseCode);
		response.setHeader("Vary", "Accept");
		response.flushBuffer();
		                
		Collection<String> currentStaticStrings = new HashSet<String>();
		
		Collection<URI> staticQueryTypesForUnknown = new ArrayList<URI>(1);
		
		if(fetchController.anyNamespaceNotRecognised())
		    staticQueryTypesForUnknown = localSettings.getURIProperties("unknownNamespaceStaticAdditions");
		else
			staticQueryTypesForUnknown = localSettings.getURIProperties("unknownQueryStaticAdditions");

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
		        Map<String, String> attributeList = QueryCreator.getAttributeListFor(nextQueryType, new ProviderImpl(), queryString, localSettings.getStringProperty("hostName", ""), realHostName, pageOffset, localSettings);
		        
		        String nextBackupString = QueryCreator.createStaticRdfXmlString(nextQueryType, nextQueryType, new ProviderImpl(), attributeList, includedProfiles, localSettings.getBooleanProperty("recogniseImplicitRdfRuleInclusions", true) , localSettings.getBooleanProperty("includeNonProfileMatchedRdfRules", true), localSettings) + "\n";
		        
		        nextBackupString = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">" + nextBackupString + "</rdf:RDF>";
		        
		        try
		        {
		            myRepositoryConnection.add(new java.io.StringReader(nextBackupString), localSettings.getDefaultHostAddress()+queryString, RDFFormat.RDFXML, nextQueryType.getKey());
		        }
		        catch(org.openrdf.rio.RDFParseException rdfpe)
		        {
		            log.error("GeneralServlet: RDFParseException: static RDF "+rdfpe.getMessage());
		            log.error("GeneralServlet: nextBackupString="+nextBackupString);
		        }
		    }
		}
		
		if(currentStaticStrings.size() == 0)
		{
		    log.error("Could not find anything at all to match at query level queryString="+queryString);
		    
		    if(requestedContentType.equals("application/rdf+xml") || requestedContentType.equals("text/html"))
		    {
		        debugStrings.add("<!-- Could not find anything at all to match at query level -->");
		    }
		    else if(requestedContentType.equals("text/rdf+n3"))
		    {
		        debugStrings.add("# Could not find anything at all to match at query level");
		    }
		}
		
		
		if(_TRACE)
		{
		    log.trace("GeneralServlet: ending !fetchController.queryKnown() section");
		}
	}


	/**
	 * @param response
	 * @param localSettings
	 * @param queryString
	 * @param responseCode
	 * @param pageOffset
	 * @param requestedContentType
	 * @param multiProviderQueryBundles
	 * @param myRepository
	 * @throws IOException
	 * @throws OpenRDFException
	 */
	private void doQueryPretend(HttpServletResponse response, Settings localSettings, String queryString, int responseCode, int pageOffset,
			String requestedContentType, Collection<QueryBundle> multiProviderQueryBundles, Repository myRepository) throws IOException,
			OpenRDFException
	{
		response.setCharacterEncoding("UTF-8");
		response.setContentType(requestedContentType);
		response.setCharacterEncoding("UTF-8");
		response.setStatus(responseCode);
		response.setHeader("Vary", "Accept");
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
		        +localSettings.getStringProperty("separator", ":")+"pageoffset"+pageOffset
		        +localSettings.getStringProperty("separator", ":")+StringUtils.percentEncode(nextScheduledQueryBundle.getOriginalProvider().getKey().stringValue().toLowerCase())
		        +localSettings.getStringProperty("separator", ":")+StringUtils.percentEncode(nextScheduledQueryBundle.getQueryType().getKey().stringValue().toLowerCase())
		        +localSettings.getStringProperty("separator", ":")+StringUtils.percentEncode(nextScheduledQueryBundle.getQueryEndpoint()))
		        , Settings.CONFIG_API_VERSION);
		}
		
		if(_TRACE)
		{
		    log.trace("GeneralServlet: Finished with pretend query bundle rdf generation");
		}
	}


	/**
	 * @param out
	 * @param request
	 * @param localSettings
	 * @param writerFormat
	 * @param realHostName
	 * @param queryString
	 * @param pageOffset
	 * @param requestedContentType
	 * @param fetchController
	 * @param debugStrings
	 * @param convertedPool
	 * @throws IOException
	 */
	private void resultsToWriter(Writer out, HttpServletRequest request, Settings localSettings, RDFFormat writerFormat, String realHostName,
			String queryString, int pageOffset, String requestedContentType, RdfFetchController fetchController, Collection<String> debugStrings,
			Repository convertedPool) throws IOException
	{
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
		    if(_DEBUG)
		    {
		        log.debug("GeneralServlet: about to call rdf rendering method");
		        log.debug("GeneralServlet: fetchController.queryKnown()="+fetchController.queryKnown());
		    }

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
	}


	/**
	 * @param localSettings
	 * @param includedProfiles
	 * @param fetchController
	 * @param myRepository
	 * @return
	 */
	private Repository doPoolNormalisation(Settings localSettings, List<Profile> includedProfiles, RdfFetchController fetchController,
			Repository myRepository)
	{
		Repository convertedPool;
		convertedPool = (Repository)QueryCreator.normaliseByStage(
		    NormalisationRuleImpl.getRdfruleStageAfterResultsToPool(),
		    myRepository, 
		    localSettings.getSortedRulesForProviders(fetchController.getAllUsedProviders(), 
		        Constants.HIGHEST_ORDER_FIRST ), 
		    includedProfiles, 
		    localSettings.getBooleanProperty("recogniseImplicitRdfRuleInclusions", true), 
		    localSettings.getBooleanProperty("includeNonProfileMatchedRdfRules", true) );
		return convertedPool;
	}


	/**
	 * @param response
	 * @param localSettings
	 * @param queryString
	 * @param responseCode
	 * @param requestedContentType
	 * @param includedProfiles
	 * @param fetchController
	 * @param multiProviderQueryBundles
	 * @param debugStrings
	 * @param myRepository
	 * @param myRepositoryConnection
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws RepositoryException
	 * @throws OpenRDFException
	 */
	private void doQueryNotPretend(HttpServletResponse response, Settings localSettings, String queryString, int responseCode,
			String requestedContentType, List<Profile> includedProfiles, RdfFetchController fetchController,
			Collection<QueryBundle> multiProviderQueryBundles, Collection<String> debugStrings, Repository myRepository,
			RepositoryConnection myRepositoryConnection) throws InterruptedException, IOException, RepositoryException, OpenRDFException
	{
		response.setContentType(requestedContentType+"; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		// 3. Attempt to fetch information as needed
		fetchController.fetchRdfForQueries();
		
		// keep track of the strings so that we don't print multiples of exactly the same information more than once
		Collection<String> currentStaticStrings = new HashSet<String>();
		
		response.setStatus(responseCode);
		response.setHeader("Vary", "Accept");
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
		        includedProfiles, localSettings.getBooleanProperty("recogniseImplicitRdfRuleInclusions", true), localSettings.getBooleanProperty("includeNonProfileMatchedRdfRules", true) );
		    
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
		    
		    try
		    {
		        myRepositoryConnection.add(new java.io.StringReader(nextStaticString), localSettings.getDefaultHostAddress()+queryString, RDFFormat.RDFXML, nextPotentialQueryBundle.getOriginalProvider().getKey());
		    }
		    catch(org.openrdf.rio.RDFParseException rdfpe)
		    {
		        log.error("GeneralServlet: RDFParseException: static RDF "+rdfpe.getMessage());
		        log.error("GeneralServlet: nextStaticString="+nextStaticString);
		    }
		}
	}


	/**
	 * @param redirectString The StringBuilder that will have the redirect String appended to it
	 * @param request The request that was given by the user
	 * @param localSettings The Settings object
	 * @param requestQueryOptions The query options object
	 * @param requestedContentType The requested content type
	 * @param ignoreContextPath Whether we should ignore the context path or not
	 */
	public static void getRedirectString(StringBuilder redirectString, HttpServletRequest request, Settings localSettings,
			DefaultQueryOptions requestQueryOptions, String requestedContentType, boolean ignoreContextPath)
	{
		if(localSettings.getBooleanProperty("useHardcodedRequestHostname", false))
		{
			redirectString.append(localSettings.getStringProperty("hardcodedRequestHostname", ""));
		}
		
		if(localSettings.getBooleanProperty("useHardcodedRequestContext", false))
		{
			redirectString.append(localSettings.getStringProperty("hardcodedRequestContext", ""));
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
			redirectString.append(localSettings.getStringProperty("htmlUrlPrefix", "page/"));

			if(requestQueryOptions.isQueryPlanRequest())
			{
				redirectString.append(localSettings.getStringProperty("queryplanUrlPrefix","queryplan/"));
			}
			
			if(requestQueryOptions.containsExplicitPageOffsetValue())
			{
				redirectString.append(localSettings.getStringProperty("pageoffsetUrlOpeningPrefix","pageoffset"));
				redirectString.append(requestQueryOptions.getPageOffset());
				redirectString.append(localSettings.getStringProperty("pageoffsetUrlClosingPrefix","/"));
			}
			
			redirectString.append(requestQueryOptions.getParsedRequest());

			if(requestQueryOptions.containsExplicitPageOffsetValue())
			{
				redirectString.append(localSettings.getStringProperty("pageoffsetUrlSuffix",""));
			}
			
			if(requestQueryOptions.isQueryPlanRequest())
			{
				redirectString.append(localSettings.getStringProperty("queryplanUrlSuffix",""));
			}
			
			redirectString.append(localSettings.getStringProperty("htmlUrlSuffix", ""));
		}
		else if(requestedContentType.equals(Constants.TEXT_RDF_N3))
		{
			redirectString.append(localSettings.getStringProperty("n3UrlPrefix", "n3/"));

			if(requestQueryOptions.isQueryPlanRequest())
			{
				redirectString.append(localSettings.getStringProperty("queryplanUrlPrefix","queryplan/"));
			}
			
			if(requestQueryOptions.containsExplicitPageOffsetValue())
			{
				redirectString.append(localSettings.getStringProperty("pageoffsetUrlOpeningPrefix","pageoffset"));
				redirectString.append(requestQueryOptions.getPageOffset());
				redirectString.append(localSettings.getStringProperty("pageoffsetUrlClosingPrefix","/"));
			}
			
			redirectString.append(requestQueryOptions.getParsedRequest());

			if(requestQueryOptions.containsExplicitPageOffsetValue())
			{
				redirectString.append(localSettings.getStringProperty("pageoffsetUrlSuffix",""));
			}
			
			if(requestQueryOptions.isQueryPlanRequest())
			{
				redirectString.append(localSettings.getStringProperty("queryplanUrlSuffix",""));
			}
			
			redirectString.append(localSettings.getStringProperty("n3UrlSuffix", ""));
		}
		else if(requestedContentType.equals(Constants.APPLICATION_RDF_XML))
		{
			redirectString.append(localSettings.getStringProperty("rdfXmlUrlPrefix", "rdfxml/"));

			if(requestQueryOptions.isQueryPlanRequest())
			{
				redirectString.append(localSettings.getStringProperty("queryplanUrlPrefix","queryplan/"));
			}
			
			if(requestQueryOptions.containsExplicitPageOffsetValue())
			{
				redirectString.append(localSettings.getStringProperty("pageoffsetUrlOpeningPrefix","pageoffset"));
				redirectString.append(requestQueryOptions.getPageOffset());
				redirectString.append(localSettings.getStringProperty("pageoffsetUrlClosingPrefix","/"));
			}
			
			redirectString.append(requestQueryOptions.getParsedRequest());

			if(requestQueryOptions.containsExplicitPageOffsetValue())
			{
				redirectString.append(localSettings.getStringProperty("pageoffsetUrlSuffix",""));
			}
			
			if(requestQueryOptions.isQueryPlanRequest())
			{
				redirectString.append(localSettings.getStringProperty("queryplanUrlSuffix",""));
			}
			
			redirectString.append(localSettings.getStringProperty("rdfXmlUrlSuffix", ""));
		}
		
		else if(requestedContentType.equals(Constants.APPLICATION_JSON))
		{
			redirectString.append(localSettings.getStringProperty("jsonUrlPrefix", "json/"));

			if(requestQueryOptions.isQueryPlanRequest())
			{
				redirectString.append(localSettings.getStringProperty("queryplanUrlPrefix","queryplan/"));
			}
			
			if(requestQueryOptions.containsExplicitPageOffsetValue())
			{
				redirectString.append(localSettings.getStringProperty("pageoffsetUrlOpeningPrefix","pageoffset"));
				redirectString.append(requestQueryOptions.getPageOffset());
				redirectString.append(localSettings.getStringProperty("pageoffsetUrlClosingPrefix","/"));
			}
			
			redirectString.append(requestQueryOptions.getParsedRequest());

			if(requestQueryOptions.containsExplicitPageOffsetValue())
			{
				redirectString.append(localSettings.getStringProperty("pageoffsetUrlSuffix",""));
			}
			
			if(requestQueryOptions.isQueryPlanRequest())
			{
				redirectString.append(localSettings.getStringProperty("queryplanUrlSuffix",""));
			}
			
			redirectString.append(localSettings.getStringProperty("jsonUrlSuffix", ""));
		}
	}
  
}

