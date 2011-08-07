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
import org.queryall.utils.ProfileUtils;
import org.queryall.utils.QueryTypeUtils;
import org.queryall.utils.RdfUtils;
import org.queryall.utils.RuleUtils;
import org.queryall.utils.StringUtils;
import org.queryall.negotiation.QueryallContentNegotiator;
import org.queryall.query.*;
import org.queryall.enumerations.*;
import org.queryall.api.HttpProvider;
import org.queryall.api.Profile;
import org.queryall.api.QueryAllConfiguration;
import org.queryall.api.QueryType;
import org.queryall.blacklist.*;

/** 
 * @author Peter Ansell p_ansell@yahoo.com
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
        long queryStartTime = System.currentTimeMillis();
        
        QueryAllConfiguration localSettings = Settings.getSettings();
    	BlacklistController localBlacklistController = BlacklistController.getDefaultController();
    	DefaultQueryOptions requestQueryOptions = new DefaultQueryOptions(request.getRequestURI(), request.getContextPath(), localSettings);
        
        // TODO: should this be configurable or should it be removed?
        boolean useDefaultProviders = true;
        // TODO FIXME: The content type negotiator does not work with locales yet        
        // String preferredLocale = QueryallLanguageNegotiator.getResponseLanguage(locale, userAgentHeader);
        
        String realHostName = request.getScheme() + "://" + request.getServerName() + (request.getServerPort() == 80 && request.getScheme().equals("http") ? "" : ":"+ request.getServerPort())+"/";
        String serverName = request.getServerName();
        String queryString = requestQueryOptions.getParsedRequest();
        String requesterIpAddress = request.getRemoteAddr();
        String locale = request.getLocale().toString();
        String characterEncoding = request.getCharacterEncoding();
        String originalAcceptHeader = request.getHeader("Accept");
        String userAgentHeader = request.getHeader("User-Agent");
        String contextPath = request.getContextPath();
        // default to 200 for response...
        int responseCode = HttpServletResponse.SC_OK;
        boolean isPretendQuery = requestQueryOptions.isQueryPlanRequest();
        int pageOffset = requestQueryOptions.getPageOffset();
        String acceptHeader = "";
        RDFFormat writerFormat = null;
        
        if(userAgentHeader == null)
        {
            userAgentHeader = "";
        }

        if(originalAcceptHeader == null || originalAcceptHeader.equals(""))
        {
            acceptHeader = localSettings.getStringProperty(Constants.PREFERRED_DISPLAY_CONTENT_TYPE, Constants.APPLICATION_RDF_XML);
        }
        else
        {
            acceptHeader = originalAcceptHeader;
        }
        
        String originalRequestedContentType = QueryallContentNegotiator.getResponseContentType(acceptHeader, userAgentHeader, localSettings.getStringProperty("preferredDisplayContentType", Constants.APPLICATION_RDF_XML));
        
        String requestedContentType = originalRequestedContentType;
        
        // If they defined their desired format in the URL, get it here
        if(requestQueryOptions.containsExplicitFormat())
        {
            requestedContentType = requestQueryOptions.getExplicitFormat();
        }

        // Make sure that their requestedContentType is valid as an RDFFormat, or is text/html using this method
        requestedContentType = RdfUtils.findBestContentType(requestedContentType, localSettings.getStringProperty(Constants.PREFERRED_DISPLAY_CONTENT_TYPE, Constants.APPLICATION_RDF_XML), Constants.APPLICATION_RDF_XML);
        
        // this will be null if they chose text/html, but it will be a valid format in other cases due to the above method
		writerFormat = RdfUtils.getWriterFormat(requestedContentType);
        
        if(_INFO)
        {
            logRequestDetails(request, requestQueryOptions, useDefaultProviders, serverName, queryString, requesterIpAddress, locale,
					characterEncoding, isPretendQuery, pageOffset, originalRequestedContentType, requestedContentType);
        }
        
        // allow for users to perform redirections if the query did not contain an explicit format
        if(checkExplicitRedirect(response, localSettings, requestQueryOptions, contextPath, requestedContentType))
        {
        	// no more code necessary here
        	return;
        }
        
        // TODO: avoid cast here
        ((Settings) localSettings).configRefreshCheck(false);
        localBlacklistController.doBlacklistExpiry();
        
        if(localBlacklistController.isClientBlacklisted(requesterIpAddress))
        {
            log.warn("GeneralServlet: sending requesterIpAddress="+requesterIpAddress+" to blacklist redirect page");
            
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setHeader("Location", localSettings.getStringProperty("blacklistRedirectPage", "/error/blacklist"));
            return;
        }
        
        response.setHeader("X-Application", localSettings.getStringProperty("userAgent", "queryall") + "/"+Settings.VERSION);
        
        List<Profile> includedProfiles = ProfileUtils.getAndSortProfileList(localSettings.getURIProperties("activeProfiles"), SortOrder.LOWEST_ORDER_FIRST, localSettings.getAllProfiles());
        
        RdfFetchController fetchController = new RdfFetchController(localSettings, localBlacklistController, queryString, includedProfiles, useDefaultProviders, realHostName, pageOffset, requestedContentType);
        
        Collection<QueryBundle> multiProviderQueryBundles = fetchController.getQueryBundles();
        
        Collection<String> debugStrings = new ArrayList<String>(multiProviderQueryBundles.size()+5);
        
        // We do not use the default catalina writer as it may not be UTF-8 compliant depending on unchangeable environment variables
        Writer out = new OutputStreamWriter(response.getOutputStream(), Charset.forName("UTF-8"));

        try
        {
        	// Create a new in memory repository for each request
            Repository myRepository = new SailRepository(new MemoryStore());
            myRepository.initialize();
            
            if(isPretendQuery)
            {
                if(_DEBUG)
                {
                    log.debug("GeneralServlet: Found pretend query");
                }
                
        		sendBasicHeaders(response, responseCode, requestedContentType);
        		
                doQueryPretend(localSettings, queryString, responseCode, pageOffset, requestedContentType, multiProviderQueryBundles,
						myRepository);
            }
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
        		    responseCode = localSettings.getIntProperty("unknownNamespaceHttpResponseCode", 404);
        		}
        		else
        		{
        			// 400 for query completely unrecognised, even when not including namespace in each query type calculation
        			responseCode = localSettings.getIntProperty("unknownQueryHttpResponseCode", 400);
        		}
        		
        		sendBasicHeaders(response, responseCode, requestedContentType);
                
                doQueryUnknown(localSettings, realHostName, queryString, pageOffset, requestedContentType, includedProfiles, fetchController,
						debugStrings, myRepository);
            }
            else
            {
                if(_DEBUG)
                {
                    log.debug("GeneralServlet: starting fetchController.queryKnown() and not pretend query section");
                }
                
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
                
        		sendBasicHeaders(response, responseCode, requestedContentType);
        		
                doQueryNotPretend(localSettings, queryString, requestedContentType, includedProfiles, fetchController, multiProviderQueryBundles, debugStrings,
						myRepository);
            }
            
            
            // Normalisation Stage : after results to pool
            Repository convertedPool = doPoolNormalisation(localSettings, includedProfiles, fetchController, myRepository);
            
            resultsToWriter(out, localSettings, writerFormat, realHostName, queryString, pageOffset, requestedContentType, fetchController,
					debugStrings, convertedPool, contextPath);
            
            out.flush();
            
            long nextTotalTime = System.currentTimeMillis()-queryStartTime;
            
            if(_INFO)
            {
                log.info("GeneralServlet: query complete requesterIpAddress="+requesterIpAddress+" queryString="+queryString + " pageOffset="+pageOffset+" totalTime="+nextTotalTime);
                log.info("GeneralServlet: finished returning information to client requesterIpAddress="+requesterIpAddress+" queryString="+queryString + " pageOffset="+pageOffset+" totalTime="+nextTotalTime);
           }    

            // Housekeeping
            
            // update a the blacklist
            localBlacklistController.accumulateBlacklist(fetchController.getErrorResults(), localSettings.getLongProperty("blacklistResetPeriodMilliseconds", 3600000), localSettings.getBooleanProperty("blacklistResetClientBlacklistWithEndpoints", false));
             
            if(localSettings.getBooleanProperty("blacklistResetEndpointFailuresOnSuccess", true))
            {
            	localBlacklistController.removeEndpointsFromBlacklist(fetchController.getSuccessfulResults(), nextTotalTime, useDefaultProviders);
            }
            
            // Don't keep local error statistics if GeneralServlet debug level is higher than or equal to info and we aren't interested in using the client IP blacklist functionalities
             if(_INFO || localSettings.getBooleanProperty("automaticallyBlacklistClients", false))
             {
                 doQueryDebug(localSettings, localBlacklistController, queryString, requesterIpAddress, multiProviderQueryBundles, nextTotalTime);
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
            throw new RuntimeException("GeneralServlet.doGet failed due to an Interrupted exception. See log for details");
        }
        catch(RuntimeException rex)
        {
            log.error("GeneralServlet.doGet: caught runtime exception", rex);
        }
    }


	/**
	 * @param response
	 * @param localSettings
	 * @param requestQueryOptions
	 * @param contextPath
	 * @param requestedContentType
	 * @return 
	 */
	private boolean checkExplicitRedirect(HttpServletResponse response, QueryAllConfiguration localSettings, DefaultQueryOptions requestQueryOptions,
			String contextPath, String requestedContentType)
	{
		if(!requestQueryOptions.containsExplicitFormat())
        {
        	if(localSettings.getBooleanProperty("alwaysRedirectToExplicitFormatUrl", false))
        	{
        		int redirectCode = localSettings.getIntProperty("redirectToExplicitFormatHttpCode", 303);
        		
        		StringBuilder redirectString = new StringBuilder();
        		boolean ignoreContextPath = false;
        		
        		getRedirectString(redirectString, localSettings, requestQueryOptions, requestedContentType, ignoreContextPath, contextPath);
        		
        		if(_INFO)
        			log.info("Sending redirect using redirectCode="+redirectCode+" to redirectString="+redirectString.toString());
        		if(_DEBUG)
        			log.debug("contextPath="+contextPath);
        		response.setStatus(redirectCode);
        		// Cannot use response.sendRedirect as it will change the status to 302, which may not be desired
    			response.setHeader("Location",redirectString.toString());
    			return true;
        	}
        }
		return false;
	}


	/**
	 * Sends the basic headers for each request to the client, including the final response code and the requested content type
	 * 
	 * @param response
	 * @param responseCode
	 * @param requestedContentType
	 * @throws IOException
	 */
	private void sendBasicHeaders(HttpServletResponse response, int responseCode, String requestedContentType) throws IOException
	{
		response.setContentType(requestedContentType+"; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(responseCode);
		response.setHeader("Vary", "Accept");
		response.flushBuffer();
	}


	/**
	 * Encapsulates the basic logging details for a single request
	 * 
	 * @param request
	 * @param requestQueryOptions
	 * @param useDefaultProviders
	 * @param serverName
	 * @param queryString
	 * @param requesterIpAddress
	 * @param locale
	 * @param characterEncoding
	 * @param isPretendQuery
	 * @param pageOffset
	 * @param originalRequestedContentType
	 * @param requestedContentType
	 */
	private void logRequestDetails(HttpServletRequest request, DefaultQueryOptions requestQueryOptions, boolean useDefaultProviders,
			String serverName, String queryString, String requesterIpAddress, String locale, String characterEncoding, boolean isPretendQuery,
			int pageOffset, String originalRequestedContentType, String requestedContentType)
	{
		log.info("GeneralServlet: query started on "+serverName+" requesterIpAddress="+requesterIpAddress+" queryString="+queryString+" explicitPageOffset="+requestQueryOptions.containsExplicitPageOffsetValue()+" pageOffset="+pageOffset+" isPretendQuery="+isPretendQuery+" useDefaultProviders="+useDefaultProviders);
		log.info("GeneralServlet: requestedContentType="+requestedContentType+ " acceptHeader="+request.getHeader("Accept")+" userAgent="+request.getHeader("User-Agent"));
		log.info("GeneralServlet: locale="+locale+" characterEncoding="+characterEncoding);
		
		if(!originalRequestedContentType.equals(requestedContentType))
		{
		    log.info("GeneralServlet: originalRequestedContentType was overwritten originalRequestedContentType="+originalRequestedContentType+" requestedContentType="+requestedContentType);
		}
	}


	/**
	 * @param localSettings
	 * @param localBlacklistController
	 * @param queryString
	 * @param requesterIpAddress
	 * @param multiProviderQueryBundles
	 * @param nextTotalTime
	 */
	private void doQueryDebug(QueryAllConfiguration localSettings, BlacklistController localBlacklistController, String queryString, String requesterIpAddress,
			Collection<QueryBundle> multiProviderQueryBundles, long nextTotalTime)
	{
		QueryDebug nextQueryDebug;
		nextQueryDebug = new QueryDebug();
		 nextQueryDebug.setClientIPAddress(requesterIpAddress);
		 
		 nextQueryDebug.setTotalTimeMilliseconds(nextTotalTime);
		 nextQueryDebug.setQueryString(queryString);
		 
		 Collection<URI> queryTitles = new HashSet<URI>();
		 
		 for(QueryBundle nextInitialQueryBundle : multiProviderQueryBundles)
		 {
		     queryTitles.add(nextInitialQueryBundle.getQueryType().getKey());
		 }
		 
		 nextQueryDebug.setMatchingQueryTitles(queryTitles);
		 
		 localBlacklistController.accumulateQueryDebug(nextQueryDebug, localSettings, 
				 localSettings.getLongProperty("blacklistResetPeriodMilliseconds", 120000L), 
				 localSettings.getBooleanProperty("blacklistResetClientBlacklistWithEndpoints", true),
				 localSettings.getBooleanProperty("automaticallyBlacklistClients", false),
				 localSettings.getIntProperty("blacklistMinimumQueriesBeforeBlacklistRules", 200),
				 localSettings.getIntProperty("blacklistClientMaxQueriesPerPeriod", 400));
	}


	/**
	 * @param localSettings
	 * @param realHostName
	 * @param queryString
	 * @param pageOffset
	 * @param requestedContentType
	 * @param includedProfiles
	 * @param fetchController
	 * @param debugStrings
	 * @param myRepository
	 * @throws IOException
	 * @throws RepositoryException
	 */
	private void doQueryUnknown(QueryAllConfiguration localSettings, String realHostName, String queryString, int pageOffset, String requestedContentType,
			List<Profile> includedProfiles, RdfFetchController fetchController, Collection<String> debugStrings, Repository myRepository) throws IOException, RepositoryException
	{
        RepositoryConnection myRepositoryConnection = null;
        try
        {
			myRepositoryConnection = myRepository.getConnection();
			                
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
			    
			    Collection<QueryType> allCustomRdfXmlIncludeTypes = QueryTypeUtils.getQueryTypesByUri(localSettings.getAllQueryTypes(), nextStaticQueryTypeForUnknown);
			    
			    // use the closest matches, even though they didn't eventuate into actual planned query bundles they matched the query string somehow
			    for(QueryType nextQueryType : allCustomRdfXmlIncludeTypes)
			    {
			        Map<String, String> attributeList = QueryCreator.getAttributeListFor(nextQueryType, new ProviderImpl(), queryString, localSettings.getStringProperty("hostName", ""), realHostName, pageOffset, localSettings);
			        
			        String nextBackupString = QueryCreator.createStaticRdfXmlString(nextQueryType, nextQueryType, new ProviderImpl(), attributeList, includedProfiles, localSettings.getBooleanProperty("recogniseImplicitRdfRuleInclusions", true) , localSettings.getBooleanProperty("includeNonProfileMatchedRdfRules", true), localSettings) + "\n";
			        
			        nextBackupString = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">" + nextBackupString + "</rdf:RDF>";
			        
			        try
			        {
			        	// TODO: avoid cast here
			            myRepositoryConnection.add(new java.io.StringReader(nextBackupString), ((Settings) localSettings).getDefaultHostAddress()+queryString, RDFFormat.RDFXML, nextQueryType.getKey());
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
        finally
        {
        	if(myRepositoryConnection != null)
        	{
        		myRepositoryConnection.close();
        	}
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
	private void doQueryPretend(QueryAllConfiguration localSettings, String queryString, int responseCode, int pageOffset,
			String requestedContentType, Collection<QueryBundle> multiProviderQueryBundles, Repository myRepository) throws IOException,
			OpenRDFException
	{
		for(QueryBundle nextScheduledQueryBundle : multiProviderQueryBundles)
		{
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
	 * @param contextPath TODO
	 * @throws IOException
	 */
	private void resultsToWriter(Writer out, QueryAllConfiguration localSettings, RDFFormat writerFormat, String realHostName,
			String queryString, int pageOffset, String requestedContentType, RdfFetchController fetchController, Collection<String> debugStrings,
			Repository convertedPool, String contextPath) throws IOException
	{
		java.io.StringWriter cleanOutput = new java.io.StringWriter();
		
		if(requestedContentType.equals(Constants.TEXT_HTML))
		{
		    if(_DEBUG)
		    {
		        log.debug("GeneralServlet: about to call html rendering method");
		        log.debug("GeneralServlet: fetchController.queryKnown()="+fetchController.queryKnown());
		    }
		    
		    try
		    {
		    	// TODO: avoid cast here
		        HtmlPageRenderer.renderHtml(getServletContext(), convertedPool, cleanOutput, fetchController, debugStrings, queryString, ((Settings) localSettings).getDefaultHostAddress() + queryString, realHostName, contextPath, pageOffset, localSettings);
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
		}
		else
		{
		    StringBuffer buffer = cleanOutput.getBuffer();
		    for(int i = 0; i < cleanOutput.getBuffer().length(); i++)
		    	out.write(buffer.charAt(i));
		}
	}

	/**
	 * Encapsulates the call to the pool normalisation method
	 * 
	 * @param localSettings
	 * @param includedProfiles
	 * @param fetchController
	 * @param myRepository The repository containing the unnormalised statements
	 * @return The repository containing the normalised statements
	 */
	private Repository doPoolNormalisation(QueryAllConfiguration localSettings, List<Profile> includedProfiles, RdfFetchController fetchController,
			Repository myRepository)
	{
		return (Repository)QueryCreator.normaliseByStage(
		    NormalisationRuleImpl.getRdfruleStageAfterResultsToPool(),
		    myRepository, 
		    RuleUtils.getSortedRulesForProviders(fetchController.getAllUsedProviders(), 
		        localSettings.getAllNormalisationRules(), SortOrder.HIGHEST_ORDER_FIRST ), 
		    includedProfiles, 
		    localSettings.getBooleanProperty("recogniseImplicitRdfRuleInclusions", true), 
		    localSettings.getBooleanProperty("includeNonProfileMatchedRdfRules", true) );
	}

	/**
	 * @param localSettings
	 * @param queryString
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
	private void doQueryNotPretend(QueryAllConfiguration localSettings, String queryString, String requestedContentType, List<Profile> includedProfiles,
			RdfFetchController fetchController, Collection<QueryBundle> multiProviderQueryBundles, Collection<String> debugStrings,
			Repository myRepository) throws InterruptedException, IOException, RepositoryException, OpenRDFException
	{
		
        RepositoryConnection myRepositoryConnection = null;

        try
        {
	        myRepositoryConnection = myRepository.getConnection();
	        
			// Attempt to fetch information as needed
			fetchController.fetchRdfForQueries();
			
		    if(_INFO)
		    {
				if(requestedContentType.equals(Constants.APPLICATION_RDF_XML) || requestedContentType.equals(Constants.TEXT_HTML))
				{
			        debugStrings.add("<!-- result units="+fetchController.getResults().size()+" -->\n");
			    }
				else if(requestedContentType.equals(Constants.TEXT_RDF_N3))
				{
			        debugStrings.add("# result units="+fetchController.getResults().size()+" \n");
			    }
			}
			
			for(RdfFetcherQueryRunnable nextResult : fetchController.getResults())
			{
		        if(_INFO)
		        {
				    if(requestedContentType.equals(Constants.APPLICATION_RDF_XML) || requestedContentType.equals(Constants.TEXT_HTML))
				    {
			            debugStrings.add("<!-- "+StringUtils.xmlEncodeString(nextResult.getResultDebugString()).replace("--","- -") + "-->");
			        }
				    else if(requestedContentType.equals(Constants.TEXT_RDF_N3))
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
			        RuleUtils.getSortedRulesByUris(localSettings.getAllNormalisationRules(), nextResult.getOriginalQueryBundle().getProvider().getNormalisationUris(), 
			            SortOrder.HIGHEST_ORDER_FIRST ), 
			        includedProfiles, localSettings.getBooleanProperty("recogniseImplicitRdfRuleInclusions", true), localSettings.getBooleanProperty("includeNonProfileMatchedRdfRules", true) );
			    
			    if(_DEBUG)
			    {
				    RepositoryConnection tempRepositoryConnection = tempRepository.getConnection();
				    
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
			    	// TODO: avoid cast here
			        myRepositoryConnection.add(new java.io.StringReader(nextStaticString), ((Settings) localSettings).getDefaultHostAddress()+queryString, RDFFormat.RDFXML, nextPotentialQueryBundle.getOriginalProvider().getKey());
			    }
			    catch(org.openrdf.rio.RDFParseException rdfpe)
			    {
			        log.error("GeneralServlet: RDFParseException: static RDF "+rdfpe.getMessage());
			        log.error("GeneralServlet: nextStaticString="+nextStaticString);
			    }
			}
        }
        finally
        {
        	if(myRepositoryConnection != null)
        	{
        		myRepositoryConnection.close();
        	}
        }
	}


	/**
	 * @param redirectString The StringBuilder that will have the redirect String appended to it
	 * @param localSettings The Settings object
	 * @param requestQueryOptions The query options object
	 * @param requestedContentType The requested content type
	 * @param ignoreContextPath Whether we should ignore the context path or not
	 * @param contextPath The context path from the request
	 */
	public static void getRedirectString(StringBuilder redirectString, QueryAllConfiguration localSettings, DefaultQueryOptions requestQueryOptions,
			String requestedContentType, boolean ignoreContextPath, String contextPath)
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
			if(contextPath.equals(""))
			{
				redirectString.append("/");
			}
			else
			{
				redirectString.append(contextPath);
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
		else
		{
			throw new IllegalArgumentException("GeneralServlet.getRedirectString: did not recognise requestedContentType="+requestedContentType);
		}
	}
  
}

