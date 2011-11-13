package org.queryall.servlets;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.app.VelocityEngine;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.HttpProvider;
import org.queryall.api.querytype.OutputQueryType;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.SortOrder;
import org.queryall.blacklist.BlacklistController;
import org.queryall.exception.QueryAllException;
import org.queryall.exception.UnnormalisableRuleException;
import org.queryall.negotiation.QueryallContentNegotiator;
import org.queryall.query.QueryBundle;
import org.queryall.query.QueryCreator;
import org.queryall.query.QueryDebug;
import org.queryall.query.RdfFetchController;
import org.queryall.query.RdfFetcherQueryRunnable;
import org.queryall.query.Settings;
import org.queryall.servlets.helpers.SettingsContextListener;
import org.queryall.servlets.html.HtmlPageRenderer;
import org.queryall.servlets.queryparsers.DefaultQueryOptions;
import org.queryall.utils.ListUtils;
import org.queryall.utils.ProfileUtils;
import org.queryall.utils.RdfUtils;
import org.queryall.utils.RuleUtils;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fuberlin.wiwiss.pubby.negotiation.ContentTypeNegotiator;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class GeneralServlet extends HttpServlet
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 997653377781136004L;
    
    public static final Logger log = LoggerFactory.getLogger(GeneralServlet.class);
    public static final boolean _TRACE = GeneralServlet.log.isTraceEnabled();
    public static final boolean _DEBUG = GeneralServlet.log.isDebugEnabled();
    public static final boolean _INFO = GeneralServlet.log.isInfoEnabled();
    
    /**
     * @param redirectString
     *            The StringBuilder that will have the redirect String appended to it
     * @param localSettings
     *            The Settings object
     * @param requestQueryOptions
     *            The query options object
     * @param requestedContentType
     *            The requested content type
     * @param ignoreContextPath
     *            Whether we should ignore the context path or not
     * @param contextPath
     *            The context path from the request
     */
    public static void getRedirectString(final StringBuilder redirectString, final QueryAllConfiguration localSettings,
            final DefaultQueryOptions requestQueryOptions, final String requestedContentType,
            boolean ignoreContextPath, final String contextPath)
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
                redirectString.append(localSettings.getStringProperty("queryplanUrlPrefix", "queryplan/"));
            }
            
            if(requestQueryOptions.containsExplicitPageOffsetValue())
            {
                redirectString.append(localSettings.getStringProperty("pageoffsetUrlOpeningPrefix", "pageoffset"));
                redirectString.append(requestQueryOptions.getPageOffset());
                redirectString.append(localSettings.getStringProperty("pageoffsetUrlClosingPrefix", "/"));
            }
            
            redirectString.append(requestQueryOptions.getParsedRequest());
            
            if(requestQueryOptions.containsExplicitPageOffsetValue())
            {
                redirectString.append(localSettings.getStringProperty("pageoffsetUrlSuffix", ""));
            }
            
            if(requestQueryOptions.isQueryPlanRequest())
            {
                redirectString.append(localSettings.getStringProperty("queryplanUrlSuffix", ""));
            }
            
            redirectString.append(localSettings.getStringProperty("htmlUrlSuffix", ""));
        }
        else if(requestedContentType.equals(Constants.TEXT_RDF_N3))
        {
            redirectString.append(localSettings.getStringProperty("n3UrlPrefix", "n3/"));
            
            if(requestQueryOptions.isQueryPlanRequest())
            {
                redirectString.append(localSettings.getStringProperty("queryplanUrlPrefix", "queryplan/"));
            }
            
            if(requestQueryOptions.containsExplicitPageOffsetValue())
            {
                redirectString.append(localSettings.getStringProperty("pageoffsetUrlOpeningPrefix", "pageoffset"));
                redirectString.append(requestQueryOptions.getPageOffset());
                redirectString.append(localSettings.getStringProperty("pageoffsetUrlClosingPrefix", "/"));
            }
            
            redirectString.append(requestQueryOptions.getParsedRequest());
            
            if(requestQueryOptions.containsExplicitPageOffsetValue())
            {
                redirectString.append(localSettings.getStringProperty("pageoffsetUrlSuffix", ""));
            }
            
            if(requestQueryOptions.isQueryPlanRequest())
            {
                redirectString.append(localSettings.getStringProperty("queryplanUrlSuffix", ""));
            }
            
            redirectString.append(localSettings.getStringProperty("n3UrlSuffix", ""));
        }
        else if(requestedContentType.equals(Constants.APPLICATION_RDF_XML))
        {
            redirectString.append(localSettings.getStringProperty("rdfXmlUrlPrefix", "rdfxml/"));
            
            if(requestQueryOptions.isQueryPlanRequest())
            {
                redirectString.append(localSettings.getStringProperty("queryplanUrlPrefix", "queryplan/"));
            }
            
            if(requestQueryOptions.containsExplicitPageOffsetValue())
            {
                redirectString.append(localSettings.getStringProperty("pageoffsetUrlOpeningPrefix", "pageoffset"));
                redirectString.append(requestQueryOptions.getPageOffset());
                redirectString.append(localSettings.getStringProperty("pageoffsetUrlClosingPrefix", "/"));
            }
            
            redirectString.append(requestQueryOptions.getParsedRequest());
            
            if(requestQueryOptions.containsExplicitPageOffsetValue())
            {
                redirectString.append(localSettings.getStringProperty("pageoffsetUrlSuffix", ""));
            }
            
            if(requestQueryOptions.isQueryPlanRequest())
            {
                redirectString.append(localSettings.getStringProperty("queryplanUrlSuffix", ""));
            }
            
            redirectString.append(localSettings.getStringProperty("rdfXmlUrlSuffix", ""));
        }
        
        else if(requestedContentType.equals(Constants.APPLICATION_JSON))
        {
            redirectString.append(localSettings.getStringProperty("jsonUrlPrefix", "json/"));
            
            if(requestQueryOptions.isQueryPlanRequest())
            {
                redirectString.append(localSettings.getStringProperty("queryplanUrlPrefix", "queryplan/"));
            }
            
            if(requestQueryOptions.containsExplicitPageOffsetValue())
            {
                redirectString.append(localSettings.getStringProperty("pageoffsetUrlOpeningPrefix", "pageoffset"));
                redirectString.append(requestQueryOptions.getPageOffset());
                redirectString.append(localSettings.getStringProperty("pageoffsetUrlClosingPrefix", "/"));
            }
            
            redirectString.append(requestQueryOptions.getParsedRequest());
            
            if(requestQueryOptions.containsExplicitPageOffsetValue())
            {
                redirectString.append(localSettings.getStringProperty("pageoffsetUrlSuffix", ""));
            }
            
            if(requestQueryOptions.isQueryPlanRequest())
            {
                redirectString.append(localSettings.getStringProperty("queryplanUrlSuffix", ""));
            }
            
            redirectString.append(localSettings.getStringProperty("jsonUrlSuffix", ""));
        }
        else
        {
            throw new IllegalArgumentException(
                    "GeneralServlet.getRedirectString: did not recognise requestedContentType=" + requestedContentType);
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
    private boolean checkExplicitRedirect(final HttpServletResponse response,
            final QueryAllConfiguration localSettings, final DefaultQueryOptions requestQueryOptions,
            final String contextPath, final String requestedContentType)
    {
        if(!requestQueryOptions.containsExplicitFormat())
        {
            if(localSettings.getBooleanProperty("alwaysRedirectToExplicitFormatUrl", false))
            {
                final int redirectCode = localSettings.getIntProperty("redirectToExplicitFormatHttpCode", 303);
                
                final StringBuilder redirectString = new StringBuilder();
                final boolean ignoreContextPath = false;
                
                GeneralServlet.getRedirectString(redirectString, localSettings, requestQueryOptions,
                        requestedContentType, ignoreContextPath, contextPath);
                
                if(GeneralServlet._INFO)
                {
                    GeneralServlet.log.info("Sending redirect using redirectCode=" + redirectCode
                            + " to redirectString=" + redirectString.toString());
                }
                if(GeneralServlet._DEBUG)
                {
                    GeneralServlet.log.debug("contextPath=" + contextPath);
                }
                response.setStatus(redirectCode);
                // Cannot use response.sendRedirect as it will change the status to 302, which may
                // not be desired
                response.setHeader("Location", redirectString.toString());
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException
    {
        final long queryStartTime = System.currentTimeMillis();
        
        final QueryAllConfiguration localSettings =
                (QueryAllConfiguration)this.getServletContext().getAttribute(SettingsContextListener.QUERYALL_CONFIG);
        final BlacklistController localBlacklistController =
                (BlacklistController)this.getServletContext().getAttribute(SettingsContextListener.QUERYALL_BLACKLIST);
        final ContentTypeNegotiator localContentTypeNegotiator =
                (ContentTypeNegotiator)this.getServletContext().getAttribute(
                        SettingsContextListener.QUERYALL_CONTENTNEGOTIATOR);
        final VelocityEngine localVelocityEngine =
                (VelocityEngine)this.getServletContext().getAttribute(SettingsContextListener.QUERYALL_VELOCITY);
        
        final DefaultQueryOptions requestQueryOptions =
                new DefaultQueryOptions(request.getRequestURI(), request.getContextPath(), localSettings);
        
        // TODO: should this be configurable or should it be removed?
        final boolean useDefaultProviders = true;
        // TODO FIXME: The content type negotiator does not work with locales yet
        // String preferredLocale = QueryallLanguageNegotiator.getResponseLanguage(locale,
        // userAgentHeader);
        
        final String realHostName =
                request.getScheme()
                        + "://"
                        + request.getServerName()
                        + (request.getServerPort() == 80 && request.getScheme().equals("http") ? "" : ":"
                                + request.getServerPort()) + "/";
        final String serverName = request.getServerName();
        final String queryString = requestQueryOptions.getParsedRequest();
        final String requesterIpAddress = request.getRemoteAddr();
        final String locale = request.getLocale().toString();
        final String characterEncoding = request.getCharacterEncoding();
        final String originalAcceptHeader = request.getHeader("Accept");
        String userAgentHeader = request.getHeader("User-Agent");
        final String contextPath = request.getContextPath();
        // default to 200 for response...
        int responseCode = HttpServletResponse.SC_OK;
        final boolean isPretendQuery = requestQueryOptions.isQueryPlanRequest();
        final int pageOffset = requestQueryOptions.getPageOffset();
        String acceptHeader = "";
        RDFFormat writerFormat = null;
        final Map<String, String> queryParameters = new HashMap<String, String>();
        queryParameters.put(Constants.QUERY, queryString);
        
        if(userAgentHeader == null)
        {
            userAgentHeader = "";
        }
        
        if(originalAcceptHeader == null || originalAcceptHeader.equals(""))
        {
            acceptHeader =
                    localSettings.getStringProperty(Constants.PREFERRED_DISPLAY_CONTENT_TYPE,
                            Constants.APPLICATION_RDF_XML);
        }
        else
        {
            acceptHeader = originalAcceptHeader;
        }
        
        final String originalRequestedContentType =
                QueryallContentNegotiator.getResponseContentType(acceptHeader, userAgentHeader,
                        localContentTypeNegotiator,
                        localSettings.getStringProperty("preferredDisplayContentType", Constants.APPLICATION_RDF_XML));
        
        String requestedContentType = originalRequestedContentType;
        
        // If they defined their desired format in the URL, get it here
        if(requestQueryOptions.containsExplicitFormat())
        {
            requestedContentType = requestQueryOptions.getExplicitFormat();
        }
        
        // Make sure that their requestedContentType is valid as an RDFFormat, or is text/html using
        // this method
        requestedContentType =
                RdfUtils.findBestContentType(requestedContentType, localSettings.getStringProperty(
                        Constants.PREFERRED_DISPLAY_CONTENT_TYPE, Constants.APPLICATION_RDF_XML),
                        Constants.APPLICATION_RDF_XML);
        
        // this will be null if they chose text/html, but it will be a valid format in other cases
        // due to the above method
        writerFormat = RdfUtils.getWriterFormat(requestedContentType);
        
        if(GeneralServlet._INFO)
        {
            this.logRequestDetails(request, requestQueryOptions, useDefaultProviders, serverName, queryString,
                    requesterIpAddress, locale, characterEncoding, isPretendQuery, pageOffset,
                    originalRequestedContentType, requestedContentType);
        }
        
        // allow for users to perform redirections if the query did not contain an explicit format
        if(this.checkExplicitRedirect(response, localSettings, requestQueryOptions, contextPath, requestedContentType))
        {
            // no more code necessary here
            return;
        }
        
        // TODO: avoid cast here
        ((Settings)localSettings).configRefreshCheck(false);
        localBlacklistController.doBlacklistExpiry();
        
        if(localBlacklistController.isClientBlacklisted(requesterIpAddress))
        {
            GeneralServlet.log.warn("GeneralServlet: sending requesterIpAddress=" + requesterIpAddress
                    + " to blacklist redirect page");
            
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setHeader("Location", localSettings.getStringProperty("blacklistRedirectPage", "/error/blacklist"));
            return;
        }
        
        // TODO: arrange to move this into the header include function
        response.setHeader("X-Application", localSettings.getStringProperty("userAgent", "queryall") + "/"
                + Settings.VERSION);
        
        final List<Profile> includedProfiles =
                ProfileUtils.getAndSortProfileList(localSettings.getURIProperties("activeProfiles"),
                        SortOrder.LOWEST_ORDER_FIRST, localSettings.getAllProfiles());
        
        try
        {
            final RdfFetchController fetchController =
                    new RdfFetchController(localSettings, localBlacklistController, queryParameters, includedProfiles,
                            useDefaultProviders, realHostName, pageOffset);
            
            final Collection<QueryBundle> multiProviderQueryBundles = fetchController.getQueryBundles();
            
            final Collection<String> debugStrings = new ArrayList<String>(multiProviderQueryBundles.size() + 5);
            
            // We do not use the default catalina writer as it may not be UTF-8 compliant depending
            // on
            // unchangeable environment variables
            final Writer out = new OutputStreamWriter(response.getOutputStream(), Charset.forName("UTF-8"));
            
            // Create a new in memory repository for each request
            final Repository myRepository = new SailRepository(new MemoryStore());
            myRepository.initialize();
            
            if(isPretendQuery)
            {
                if(GeneralServlet._DEBUG)
                {
                    GeneralServlet.log.debug("GeneralServlet: Found pretend query");
                }
                
                this.sendBasicHeaders(response, responseCode, requestedContentType);
                
                this.doQueryPretend(localSettings, queryString, responseCode, pageOffset, requestedContentType,
                        multiProviderQueryBundles, myRepository);
            }
            else if(!fetchController.queryKnown())
            {
                if(GeneralServlet._DEBUG)
                {
                    GeneralServlet.log.debug("GeneralServlet: starting !fetchController.queryKnown() section");
                }
                
                // change response code to indicate that the query was in some way incorrect
                // according to our current knowledge
                if(fetchController.anyNamespaceNotRecognised())
                {
                    // 404 for document not found, as a query type matched somewhere without having
                    // the namespace recognised
                    // There are still no results, but this is a more specific exception
                    responseCode = localSettings.getIntProperty("unknownNamespaceHttpResponseCode", 404);
                }
                else
                {
                    // 400 for query completely unrecognised, even when not including namespace in
                    // each query type calculation
                    responseCode = localSettings.getIntProperty("unknownQueryHttpResponseCode", 400);
                }
                
                this.sendBasicHeaders(response, responseCode, requestedContentType);
                
                this.doQueryUnknown(localSettings, realHostName, queryParameters, pageOffset, requestedContentType,
                        includedProfiles, fetchController, debugStrings, myRepository);
            }
            else
            {
                if(GeneralServlet._DEBUG)
                {
                    GeneralServlet.log
                            .debug("GeneralServlet: starting fetchController.queryKnown() and not pretend query section");
                }
                
                // for now we redirect if we find any in the set that have redirect enabled as HTTP
                // GET URL's, otherwise fall through to the POST SPARQL RDF/XML and GET URL fetching
                for(final QueryBundle nextScheduledQueryBundle : multiProviderQueryBundles)
                {
                    if(nextScheduledQueryBundle.getProvider() != null
                            && nextScheduledQueryBundle.getProvider() instanceof HttpProvider)
                    {
                        final HttpProvider nextScheduledHttpProvider =
                                (HttpProvider)nextScheduledQueryBundle.getProvider();
                        
                        if(nextScheduledHttpProvider.hasEndpointUrl()
                        // && nextScheduledHttpProvider.isHttpGetUrl()
                                && nextScheduledQueryBundle.getProvider().needsRedirect())
                        {
                            final String randomlyChosenRedirect =
                                    ListUtils.chooseRandomItemFromCollection(nextScheduledQueryBundle
                                            .getAlternativeEndpointsAndQueries().keySet());
                            
                            GeneralServlet.log.info("Sending redirect to url=" + randomlyChosenRedirect);
                            
                            response.sendRedirect(randomlyChosenRedirect);
                            
                            return;
                        }
                    }
                }
                
                this.sendBasicHeaders(response, responseCode, requestedContentType);
                
                this.doQueryNotPretend(localSettings, queryString, requestedContentType, includedProfiles,
                        fetchController, multiProviderQueryBundles, debugStrings, myRepository);
            }
            
            // Normalisation Stage : after results to pool
            final Repository convertedPool =
                    this.doPoolNormalisation(localSettings, includedProfiles, fetchController, myRepository);
            
            this.resultsToWriter(localVelocityEngine, out, localSettings, writerFormat, realHostName, queryString,
                    pageOffset, requestedContentType, fetchController, debugStrings, convertedPool, contextPath);
            
            out.flush();
            
            final long nextTotalTime = System.currentTimeMillis() - queryStartTime;
            
            if(GeneralServlet._INFO)
            {
                GeneralServlet.log.info("GeneralServlet: query complete requesterIpAddress=" + requesterIpAddress
                        + " queryString=" + queryString + " pageOffset=" + pageOffset + " totalTime=" + nextTotalTime);
                // GeneralServlet.log.info("GeneralServlet: finished returning information to client requesterIpAddress="
                // + requesterIpAddress + " queryString=" + queryString + " pageOffset=" +
                // pageOffset
                // + " totalTime=" + nextTotalTime);
            }
            
            // Housekeeping
            
            // update a the blacklist
            localBlacklistController.accumulateBlacklist(fetchController.getErrorResults(),
                    localSettings.getLongProperty("blacklistResetPeriodMilliseconds", 3600000),
                    localSettings.getBooleanProperty("blacklistResetClientBlacklistWithEndpoints", false));
            
            if(localSettings.getBooleanProperty("blacklistResetEndpointFailuresOnSuccess", true))
            {
                localBlacklistController.removeEndpointsFromBlacklist(fetchController.getSuccessfulResults(),
                        nextTotalTime, useDefaultProviders);
            }
            
            // Don't keep local error statistics if GeneralServlet debug level is higher than or
            // equal to info and we aren't interested in using the client IP blacklist
            // functionalities
            if(GeneralServlet._INFO || localSettings.getBooleanProperty("automaticallyBlacklistClients", false))
            {
                this.doQueryDebug(localSettings, localBlacklistController, queryString, requesterIpAddress,
                        multiProviderQueryBundles, nextTotalTime);
            }
        }
        catch(final QueryAllException qex)
        {
            GeneralServlet.log.error("GeneralServlet.doGet: caught queryall exception", qex);
        }
        catch(final OpenRDFException ordfe)
        {
            GeneralServlet.log.error("GeneralServlet.doGet: caught RDF exception", ordfe);
        }
        catch(final InterruptedException iex)
        {
            GeneralServlet.log.error("GeneralServlet.doGet: caught interrupted exception", iex);
        }
        catch(final RuntimeException rex)
        {
            GeneralServlet.log.error("GeneralServlet.doGet: caught runtime exception", rex);
        }
    }
    
    /**
     * Encapsulates the call to the pool normalisation method
     * 
     * @param localSettings
     * @param includedProfiles
     * @param fetchController
     * @param myRepository
     *            The repository containing the unnormalised statements
     * @return The repository containing the normalised statements
     * @throws QueryAllException
     */
    private Repository doPoolNormalisation(final QueryAllConfiguration localSettings,
            final List<Profile> includedProfiles, final RdfFetchController fetchController,
            final Repository myRepository) throws QueryAllException
    {
        try
        {
            return (Repository)RuleUtils.normaliseByStage(
                    NormalisationRuleSchema.getRdfruleStageAfterResultsToPool(),
                    myRepository,
                    RuleUtils.getSortedRulesForProviders(fetchController.getAllUsedProviders(),
                            localSettings.getAllNormalisationRules(), SortOrder.HIGHEST_ORDER_FIRST), includedProfiles,
                    localSettings.getBooleanProperty("recogniseImplicitRdfRuleInclusions", true),
                    localSettings.getBooleanProperty("includeNonProfileMatchedRdfRules", true));
        }
        catch(final UnnormalisableRuleException e)
        {
            GeneralServlet.log.error("Found unnormalisable rule exception while normalising the pool", e);
            throw new QueryAllException("Found unnormalisable rule exception while normalising the pool", e);
        }
        catch(final QueryAllException e)
        {
            GeneralServlet.log.error("Found queryall checked exception while normalising the pool", e);
            throw e;
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
    private void doQueryDebug(final QueryAllConfiguration localSettings,
            final BlacklistController localBlacklistController, final String queryString,
            final String requesterIpAddress, final Collection<QueryBundle> multiProviderQueryBundles,
            final long nextTotalTime)
    {
        QueryDebug nextQueryDebug;
        nextQueryDebug = new QueryDebug();
        nextQueryDebug.setClientIPAddress(requesterIpAddress);
        
        nextQueryDebug.setTotalTimeMilliseconds(nextTotalTime);
        nextQueryDebug.setQueryString(queryString);
        
        final Collection<URI> queryTitles = new HashSet<URI>();
        
        for(final QueryBundle nextInitialQueryBundle : multiProviderQueryBundles)
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
     * @throws QueryAllException
     * @throws UnnormalisableRuleException
     */
    private void doQueryNotPretend(final QueryAllConfiguration localSettings, final String queryString,
            final String requestedContentType, final List<Profile> includedProfiles,
            final RdfFetchController fetchController, final Collection<QueryBundle> multiProviderQueryBundles,
            final Collection<String> debugStrings, final Repository myRepository) throws InterruptedException,
        IOException, RepositoryException, OpenRDFException, UnnormalisableRuleException, QueryAllException
    {
        
        RepositoryConnection myRepositoryConnection = null;
        
        try
        {
            myRepositoryConnection = myRepository.getConnection();
            
            // Attempt to fetch information as needed
            fetchController.fetchRdfForQueries();
            
            if(GeneralServlet._INFO)
            {
                if(requestedContentType.equals(Constants.APPLICATION_RDF_XML)
                        || requestedContentType.equals(Constants.TEXT_HTML))
                {
                    debugStrings.add("<!-- result units=" + fetchController.getResults().size() + " -->\n");
                }
                else if(requestedContentType.equals(Constants.TEXT_RDF_N3))
                {
                    debugStrings.add("# result units=" + fetchController.getResults().size() + " \n");
                }
            }
            
            for(final RdfFetcherQueryRunnable nextResult : fetchController.getResults())
            {
                if(GeneralServlet._INFO)
                {
                    if(requestedContentType.equals(Constants.APPLICATION_RDF_XML)
                            || requestedContentType.equals(Constants.TEXT_HTML))
                    {
                        debugStrings.add("<!-- "
                                + StringUtils.xmlEncodeString(nextResult.getResultDebugString()).replace("--", "- -")
                                + "-->");
                    }
                    else if(requestedContentType.equals(Constants.TEXT_RDF_N3))
                    {
                        debugStrings.add("# " + nextResult.getResultDebugString().replace("\n", "").replace("\r", "")
                                + ")");
                    }
                }
                
                if(GeneralServlet._TRACE)
                {
                    GeneralServlet.log.trace("GeneralServlet: normalised result string : "
                            + nextResult.getNormalisedResult());
                }
                
                Repository tempRepository = new SailRepository(new MemoryStore());
                tempRepository.initialize();
                
                RdfUtils.insertResultIntoRepository(nextResult, tempRepository, localSettings);
                
                tempRepository =
                        (Repository)RuleUtils.normaliseByStage(NormalisationRuleSchema
                                .getRdfruleStageAfterResultsImport(), tempRepository, RuleUtils.getSortedRulesByUris(
                                localSettings.getAllNormalisationRules(), nextResult.getOriginalQueryBundle()
                                        .getProvider().getNormalisationUris(), SortOrder.HIGHEST_ORDER_FIRST),
                                includedProfiles, localSettings.getBooleanProperty(
                                        "recogniseImplicitRdfRuleInclusions", true), localSettings.getBooleanProperty(
                                        "includeNonProfileMatchedRdfRules", true));
                
                if(GeneralServlet._DEBUG)
                {
                    final RepositoryConnection tempRepositoryConnection = tempRepository.getConnection();
                    
                    GeneralServlet.log.debug("GeneralServlet: getAllStatementsFromRepository(tempRepository).size()="
                            + RdfUtils.getAllStatementsFromRepository(tempRepository).size());
                    GeneralServlet.log.debug("GeneralServlet: tempRepositoryConnection.size()="
                            + tempRepositoryConnection.size());
                }
                
                RdfUtils.copyAllStatementsToRepository(myRepository, tempRepository);
            }
            
            for(final QueryBundle nextPotentialQueryBundle : multiProviderQueryBundles)
            {
                String nextStaticString = nextPotentialQueryBundle.getStaticRdfXmlString();
                
                if(GeneralServlet._TRACE)
                {
                    GeneralServlet.log
                            .trace("GeneralServlet: Adding static RDF/XML string nextPotentialQueryBundle.getQueryType().getKey()="
                                    + nextPotentialQueryBundle.getQueryType().getKey()
                                    + " nextStaticString="
                                    + nextStaticString);
                }
                
                nextStaticString =
                        "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">"
                                + nextStaticString + "</rdf:RDF>";
                
                try
                {
                    myRepositoryConnection.add(new java.io.StringReader(nextStaticString),
                            localSettings.getDefaultHostAddress() + queryString, RDFFormat.RDFXML,
                            nextPotentialQueryBundle.getOriginalProvider().getKey());
                }
                catch(final org.openrdf.rio.RDFParseException rdfpe)
                {
                    GeneralServlet.log.error("GeneralServlet: RDFParseException: static RDF " + rdfpe.getMessage());
                    GeneralServlet.log.error("GeneralServlet: nextStaticString=" + nextStaticString);
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
    private void doQueryPretend(final QueryAllConfiguration localSettings, final String queryString,
            final int responseCode, final int pageOffset, final String requestedContentType,
            final Collection<QueryBundle> multiProviderQueryBundles, final Repository myRepository) throws IOException,
        OpenRDFException
    {
        for(final QueryBundle nextScheduledQueryBundle : multiProviderQueryBundles)
        {
            nextScheduledQueryBundle.toRdf(
                    myRepository,
                    StringUtils.createURI(StringUtils.percentEncode(queryString)
                            + localSettings.getSeparator()
                            + "pageoffset"
                            + pageOffset
                            + localSettings.getSeparator()
                            + StringUtils.percentEncode(nextScheduledQueryBundle.getOriginalProvider().getKey()
                                    .stringValue().toLowerCase())
                            + localSettings.getSeparator()
                            + StringUtils.percentEncode(nextScheduledQueryBundle.getQueryType().getKey().stringValue()
                                    .toLowerCase()) + localSettings.getSeparator()),
                    // + StringUtils.percentEncode(nextScheduledQueryBundle.getQueryEndpoint())),
                    Settings.CONFIG_API_VERSION);
        }
        
        if(GeneralServlet._TRACE)
        {
            GeneralServlet.log.trace("GeneralServlet: Finished with pretend query bundle rdf generation");
        }
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
     * @throws QueryAllException
     */
    private void doQueryUnknown(final QueryAllConfiguration localSettings, final String realHostName,
            final Map<String, String> queryParameters, final int pageOffset, final String requestedContentType,
            final List<Profile> includedProfiles, final RdfFetchController fetchController,
            final Collection<String> debugStrings, final Repository myRepository) throws IOException,
        RepositoryException, QueryAllException
    {
        RepositoryConnection myRepositoryConnection = null;
        
        final boolean convertAlternateToPreferredPrefix =
                localSettings.getBooleanProperty("convertAlternateNamespacePrefixesToPreferred", false);
        
        try
        {
            myRepositoryConnection = myRepository.getConnection();
            
            final Collection<String> currentStaticStrings = new HashSet<String>();
            
            Collection<URI> staticQueryTypesForUnknown = new ArrayList<URI>(1);
            
            // TODO: attempt to generate a non-empty namespaceEntryMap in this case??
            if(fetchController.anyNamespaceNotRecognised())
            {
                staticQueryTypesForUnknown = localSettings.getURIProperties("unknownNamespaceStaticAdditions");
            }
            else
            {
                staticQueryTypesForUnknown = localSettings.getURIProperties("unknownQueryStaticAdditions");
            }
            
            for(final URI nextStaticQueryTypeForUnknown : staticQueryTypesForUnknown)
            {
                if(GeneralServlet._DEBUG)
                {
                    GeneralServlet.log.debug("GeneralServlet: nextStaticQueryTypeForUnknown="
                            + nextStaticQueryTypeForUnknown);
                }
                
                final QueryType nextIncludeType = localSettings.getAllQueryTypes().get(nextStaticQueryTypeForUnknown);
                
                // If we didn't understand the query
                final Map<String, Collection<NamespaceEntry>> emptyNamespaceEntryMap = Collections.emptyMap();
                
                if(nextIncludeType instanceof OutputQueryType)
                {
                    final Map<String, String> attributeList =
                            QueryCreator.getAttributeListFor(nextIncludeType, null, queryParameters,
                                    localSettings.getStringProperty("hostName", "bio2rdf.org"), realHostName,
                                    pageOffset, localSettings);
                    
                    // This is a last ditch solution to giving some meaningful feedback, as we
                    // assume that the unknown query type will handle the input, so we pass it in as
                    // both parameters
                    String nextBackupString =
                            QueryCreator.createStaticRdfXmlString(nextIncludeType, (OutputQueryType)nextIncludeType,
                                    null, attributeList, emptyNamespaceEntryMap, includedProfiles,
                                    localSettings.getBooleanProperty("recogniseImplicitRdfRuleInclusions", true),
                                    localSettings.getBooleanProperty("includeNonProfileMatchedRdfRules", true),
                                    convertAlternateToPreferredPrefix, localSettings)
                                    + "\n";
                    
                    nextBackupString =
                            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">"
                                    + nextBackupString + "</rdf:RDF>";
                    
                    try
                    {
                        myRepositoryConnection.add(new java.io.StringReader(nextBackupString),
                                localSettings.getDefaultHostAddress() + queryParameters.get(Constants.QUERY),
                                RDFFormat.RDFXML, nextIncludeType.getKey());
                    }
                    catch(final org.openrdf.rio.RDFParseException rdfpe)
                    {
                        GeneralServlet.log.error("GeneralServlet: RDFParseException: static RDF " + rdfpe.getMessage());
                        GeneralServlet.log.error("GeneralServlet: nextBackupString=" + nextBackupString);
                    }
                }
                else
                {
                    GeneralServlet.log
                            .warn("Attempted to include a query type that was not parsed as an output query type key="
                                    + nextIncludeType.getKey() + " types=" + nextIncludeType.getElementTypes());
                }
            }
            
            if(currentStaticStrings.size() == 0)
            {
                GeneralServlet.log.error("Could not find anything at all to match at query level queryString="
                        + queryParameters.get(Constants.QUERY));
                
                if(requestedContentType.equals("application/rdf+xml") || requestedContentType.equals("text/html"))
                {
                    debugStrings.add("<!-- Could not find anything at all to match at query level -->");
                }
                else if(requestedContentType.equals("text/rdf+n3"))
                {
                    debugStrings.add("# Could not find anything at all to match at query level");
                }
            }
            
            if(GeneralServlet._TRACE)
            {
                GeneralServlet.log.trace("GeneralServlet: ending !fetchController.queryKnown() section");
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
    private void logRequestDetails(final HttpServletRequest request, final DefaultQueryOptions requestQueryOptions,
            final boolean useDefaultProviders, final String serverName, final String queryString,
            final String requesterIpAddress, final String locale, final String characterEncoding,
            final boolean isPretendQuery, final int pageOffset, final String originalRequestedContentType,
            final String requestedContentType)
    {
        if(GeneralServlet._INFO)
        {
            GeneralServlet.log.info("GeneralServlet: query started on " + serverName + " requesterIpAddress="
                    + requesterIpAddress + " queryString=" + queryString + " explicitPageOffset="
                    + requestQueryOptions.containsExplicitPageOffsetValue() + " pageOffset=" + pageOffset
                    + " isPretendQuery=" + isPretendQuery + " useDefaultProviders=" + useDefaultProviders);
            GeneralServlet.log.info("GeneralServlet: requestedContentType=" + requestedContentType + " acceptHeader="
                    + request.getHeader("Accept") + " userAgent=" + request.getHeader("User-Agent"));
            GeneralServlet.log.info("GeneralServlet: locale=" + locale + " characterEncoding=" + characterEncoding);
            
            if(!originalRequestedContentType.equals(requestedContentType))
            {
                GeneralServlet.log
                        .info("GeneralServlet: originalRequestedContentType was overwritten originalRequestedContentType="
                                + originalRequestedContentType + " requestedContentType=" + requestedContentType);
            }
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
     * @param contextPath
     * @throws IOException
     */
    private void resultsToWriter(final VelocityEngine nextEngine, final Writer out,
            final QueryAllConfiguration localSettings, final RDFFormat writerFormat, final String realHostName,
            final String queryString, final int pageOffset, final String requestedContentType,
            final RdfFetchController fetchController, final Collection<String> debugStrings,
            final Repository convertedPool, final String contextPath) throws IOException
    {
        // Assume an average document may easily contain 2000 characters, to save on copies inside
        // the stringwriter
        // By default it starts with only 16 characters if we don't set a number here
        final java.io.StringWriter cleanOutput = new java.io.StringWriter(2000);
        
        if(requestedContentType.equals(Constants.TEXT_HTML))
        {
            if(GeneralServlet._DEBUG)
            {
                GeneralServlet.log.debug("GeneralServlet: about to call html rendering method");
                GeneralServlet.log
                        .debug("GeneralServlet: fetchController.queryKnown()=" + fetchController.queryKnown());
            }
            
            try
            {
                HtmlPageRenderer.renderHtml(nextEngine, localSettings, fetchController, convertedPool, cleanOutput,
                        queryString, localSettings.getDefaultHostAddress() + queryString, realHostName, contextPath,
                        pageOffset, debugStrings);
            }
            catch(final OpenRDFException ordfe)
            {
                GeneralServlet.log.error("GeneralServlet: couldn't render HTML because of an RDF exception", ordfe);
            }
            catch(final Exception ex)
            {
                GeneralServlet.log.error("GeneralServlet: couldn't render HTML because of an unknown exception", ex);
            }
        }
        else
        {
            if(GeneralServlet._DEBUG)
            {
                GeneralServlet.log.debug("GeneralServlet: about to call rdf rendering method");
                GeneralServlet.log
                        .debug("GeneralServlet: fetchController.queryKnown()=" + fetchController.queryKnown());
            }
            
            RdfUtils.toWriter(convertedPool, cleanOutput, writerFormat);
        }
        
        if(requestedContentType.equals(Constants.APPLICATION_RDF_XML))
        {
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
            
            for(final String nextDebugString : debugStrings)
            {
                out.write(nextDebugString + "\n");
            }
            final StringBuffer buffer = cleanOutput.getBuffer();
            
            // HACK to get around lack of interest in sesame for getting RDF/XML documents without
            // the XML PI
            // 38 is the length of the sesame RDF/XML PI, if it changes we will start to fail with
            // all RDF/XML results and we need to change the magic number here
            if(buffer.length() > 38)
            {
                for(int i = 38; i < cleanOutput.getBuffer().length(); i++)
                {
                    out.write(buffer.charAt(i));
                }
            }
        }
        else if(requestedContentType.equals(Constants.TEXT_RDF_N3))
        {
            for(final String nextDebugString : debugStrings)
            {
                out.write(nextDebugString + "\n");
            }
            
            final StringBuffer buffer = cleanOutput.getBuffer();
            for(int i = 0; i < cleanOutput.getBuffer().length(); i++)
            {
                out.write(buffer.charAt(i));
            }
        }
        else
        {
            final StringBuffer buffer = cleanOutput.getBuffer();
            for(int i = 0; i < cleanOutput.getBuffer().length(); i++)
            {
                out.write(buffer.charAt(i));
            }
        }
    }
    
    /**
     * Sends the basic headers for each request to the client, including the final response code and
     * the requested content type
     * 
     * @param response
     * @param responseCode
     * @param requestedContentType
     * @throws IOException
     */
    private void sendBasicHeaders(final HttpServletResponse response, final int responseCode,
            final String requestedContentType) throws IOException
    {
        response.setContentType(requestedContentType + "; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(responseCode);
        response.setHeader("Vary", "Accept");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.flushBuffer();
    }
    
}
