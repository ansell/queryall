package org.queryall.servlets;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.app.VelocityEngine;
import org.openrdf.OpenRDFException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.HttpProvider;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.PropertyUtils;
import org.queryall.api.utils.SortOrder;
import org.queryall.api.utils.WebappConfig;
import org.queryall.blacklist.BlacklistController;
import org.queryall.exception.QueryAllException;
import org.queryall.negotiation.QueryallContentNegotiator;
import org.queryall.query.QueryBundle;
import org.queryall.query.RdfFetchController;
import org.queryall.servlets.helpers.SettingsContextListener;
import org.queryall.servlets.html.HtmlPageRenderer;
import org.queryall.servlets.queryparsers.DefaultQueryOptions;
import org.queryall.servlets.utils.ServletUtils;
import org.queryall.utils.ListUtils;
import org.queryall.utils.ProfileUtils;
import org.queryall.utils.RdfUtils;
import org.queryall.utils.SettingsFactory;
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
    
    private static final Logger log = LoggerFactory.getLogger(GeneralServlet.class);
    private static final boolean TRACE = GeneralServlet.log.isTraceEnabled();
    private static final boolean DEBUG = GeneralServlet.log.isDebugEnabled();
    private static final boolean INFO = GeneralServlet.log.isInfoEnabled();
    
    public static void doGetRequest(final HttpServletRequest request, final HttpServletResponse response,
            final QueryAllConfiguration localSettings, final BlacklistController localBlacklistController,
            final ContentTypeNegotiator localContentTypeNegotiator, final VelocityEngine localVelocityEngine)
        throws ServletException, IOException
    {
        final long queryStartTime = System.currentTimeMillis();
        
        final DefaultQueryOptions requestQueryOptions =
                new DefaultQueryOptions(request.getRequestURI(), request.getContextPath(), localSettings);
        
        // TODO: should this be configurable or should it be removed?
        // final boolean useDefaultProviders = true;
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
            acceptHeader = localSettings.getString(WebappConfig.PREFERRED_DISPLAY_CONTENT_TYPE);
        }
        else
        {
            acceptHeader = originalAcceptHeader;
        }
        
        final String originalRequestedContentType =
                QueryallContentNegotiator.getResponseContentType(acceptHeader, userAgentHeader,
                        localContentTypeNegotiator,
                        localSettings.getString(WebappConfig.PREFERRED_DISPLAY_CONTENT_TYPE));
        
        String requestedContentType = originalRequestedContentType;
        
        // If they defined their desired format in the URL, get it here
        if(requestQueryOptions.containsExplicitFormat())
        {
            requestedContentType = requestQueryOptions.getExplicitFormat();
        }
        
        // Make sure that their requestedContentType is valid as an RDFFormat, or is text/html using
        // this method
        requestedContentType =
                RdfUtils.findBestContentType(requestedContentType,
                        localSettings.getString(WebappConfig.PREFERRED_DISPLAY_CONTENT_TYPE),
                        Constants.APPLICATION_RDF_XML);
        
        // this will be null if they chose text/html, but it will be a valid format in other cases
        // due to the above method
        writerFormat = RdfUtils.getWriterFormat(requestedContentType);
        
        if(GeneralServlet.INFO)
        {
            ServletUtils.logRequestDetails(serverName, queryString, requesterIpAddress, locale, characterEncoding,
                    isPretendQuery, pageOffset, originalRequestedContentType, requestedContentType,
                    requestQueryOptions.containsExplicitPageOffsetValue(), acceptHeader, userAgentHeader);
        }
        
        // allow for users to perform redirections if the query did not contain an explicit format
        if(ServletUtils.checkExplicitRedirect(response, localSettings, requestQueryOptions, contextPath,
                requestedContentType))
        {
            // no more code necessary here if the redirect was performed
            return;
        }
        
        SettingsFactory.configRefreshCheck(localSettings, false);
        localBlacklistController.doBlacklistExpiry();
        
        if(localBlacklistController.isClientBlacklisted(requesterIpAddress))
        {
            GeneralServlet.log.warn("GeneralServlet: sending requesterIpAddress={} to blacklist redirect page",
                    requesterIpAddress);
            
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setHeader("Location", localSettings.getString(WebappConfig.BLACKLIST_REDIRECT_PAGE));
            return;
        }
        
        // TODO: arrange to move this into the header include function
        response.setHeader("X-Application", localSettings.getString(WebappConfig.USER_AGENT) + "/"
                + PropertyUtils.VERSION);
        
        final Collection<String> debugStrings = new ArrayList<String>();
        
        // Create a new in memory repository for each request
        final Repository myRepository = new SailRepository(new MemoryStore());
        
        try
        {
            myRepository.initialize();
            
            // If the writerFormat was not found then we default to HTML, and if we are using Ajax,
            // we don't need to fetch and can simply return here
            
            if(writerFormat == null && localSettings.getBoolean(WebappConfig.USE_AJAX_HTML_INTERFACE))
            {
                ServletUtils.sendBasicHeaders(response, responseCode, requestedContentType);
                
                // We do not use the default catalina writer as it may not be UTF-8 compliant
                // depending on unchangeable environment variables, instead we wrap up the catalina
                // binary output stream as a guaranteed UTF-8 Writer
                final Writer out = new OutputStreamWriter(response.getOutputStream(), Charset.forName("UTF-8"));
                
                HtmlPageRenderer.renderAjaxHtml(localVelocityEngine, localSettings, out, queryString,
                        localSettings.getDefaultHostAddress() + queryString, realHostName, contextPath, pageOffset,
                        debugStrings);
                
                return;
            }
            
            final List<Profile> includedProfiles =
                    ProfileUtils.getAndSortProfileList(localSettings.getURIs(WebappConfig.ACTIVE_PROFILES),
                            SortOrder.LOWEST_ORDER_FIRST, localSettings.getAllProfiles());
            
            final RdfFetchController fetchController =
                    new RdfFetchController(localSettings, localBlacklistController, queryParameters, includedProfiles,
                            realHostName, pageOffset);
            
            final Collection<QueryBundle> multiProviderQueryBundles = fetchController.getQueryBundles();
            
            // if all of the query types were specified as dummy query types, then ignore them for
            // the purposes of the query type blacklisted check
            boolean nonDummyQueryTypeFound = false;
            
            for(final QueryBundle nextQueryBundle : multiProviderQueryBundles)
            {
                if(!nextQueryBundle.getQueryType().getIsDummyQueryType())
                {
                    nonDummyQueryTypeFound = true;
                }
            }
            
            if(isPretendQuery)
            {
                if(GeneralServlet.DEBUG)
                {
                    GeneralServlet.log.debug("GeneralServlet: Found pretend query");
                }
                
                ServletUtils.sendBasicHeaders(response, responseCode, requestedContentType);
                
                ServletUtils.doQueryPretend(queryString, responseCode, pageOffset, requestedContentType,
                        multiProviderQueryBundles, myRepository, localSettings.getSeparator());
            }
            else if((multiProviderQueryBundles.isEmpty() || !nonDummyQueryTypeFound)
                    && fetchController.getQueryTypeWasBlacklisted())
            {
                // Likely case for this is that all providers are temporarily blacklisted, so in
                // order to avoid sending a 4XX code, we send a 5XX code here before we get to the
                // 4XX code section
                // NOTE: In cases where there were non-blacklisted providers available for at least
                // one query type, we return results instead of 5XX code. 5XX is reserved for cases
                // where there would be absolutely no results due to our blacklisting strategy
                
                if(GeneralServlet.DEBUG)
                {
                    GeneralServlet.log
                            .debug("GeneralServlet: starting no providers and fetchController.getQueryTypeWasBlacklisted() section");
                }
                
                // Defaults to 503 when there was a blacklisted provider and we have no planned
                // query bundles
                responseCode = localSettings.getInt(WebappConfig.NO_ACCESSIBLE_PROVIDERS_HTTP_RESPONSE_CODE);
                
                ServletUtils.sendBasicHeaders(response, responseCode, requestedContentType);
                
                // estimate the Retry-After header based on the known blacklist reset period
                final int blacklistResetPeriod = localSettings.getInt(WebappConfig.BLACKLIST_RESET_PERIOD_MILLISECONDS);
                
                if(blacklistResetPeriod > 0)
                {
                    final int blacklistResetSeconds = (int)Math.ceil(blacklistResetPeriod / 1000);
                    
                    if(blacklistResetSeconds > 0)
                    {
                        response.addHeader("Retry-After", String.valueOf(blacklistResetSeconds));
                    }
                }
                
                ServletUtils.doQueryUnknown(localSettings, realHostName, queryParameters, pageOffset,
                        requestedContentType, includedProfiles, false, false, true, debugStrings, myRepository);
                
                if(GeneralServlet.TRACE)
                {
                    GeneralServlet.log
                            .trace("GeneralServlet: ending no providers and fetchController.getQueryTypeWasBlacklisted() section");
                }
            }
            else if(!fetchController.queryKnown())
            {
                if(GeneralServlet.DEBUG)
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
                    responseCode = localSettings.getInt(WebappConfig.UNKNOWN_NAMESPACE_HTTP_RESPONSE_CODE);
                }
                else
                {
                    // 400 for query completely unrecognised, even when not including namespace in
                    // each query type calculation
                    responseCode = localSettings.getInt(WebappConfig.UNKNOWN_QUERY_HTTP_RESPONSE_CODE);
                }
                
                ServletUtils.sendBasicHeaders(response, responseCode, requestedContentType);
                
                ServletUtils.doQueryUnknown(localSettings, realHostName, queryParameters, pageOffset,
                        requestedContentType, includedProfiles, fetchController.anyNamespaceNotRecognised(),
                        !fetchController.anyNamespaceNotRecognised(), false, debugStrings, myRepository);
                
                if(GeneralServlet.TRACE)
                {
                    GeneralServlet.log.trace("GeneralServlet: ending !fetchController.queryKnown() section");
                }
            }
            else
            {
                if(GeneralServlet.DEBUG)
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
                            
                            if(GeneralServlet.INFO)
                            {
                                GeneralServlet.log.info("Sending redirect to url={}", randomlyChosenRedirect);
                            }
                            
                            response.sendRedirect(randomlyChosenRedirect);
                            
                            return;
                        }
                    }
                }
                
                ServletUtils.sendBasicHeaders(response, responseCode, requestedContentType);
                
                ServletUtils.doQueryNotPretend(localSettings, queryString, requestedContentType, includedProfiles,
                        fetchController, multiProviderQueryBundles, debugStrings, myRepository);
                
                if(GeneralServlet.TRACE)
                {
                    GeneralServlet.log
                            .trace("GeneralServlet: ending fetchController.queryKnown() and not pretend query section");
                }
            }
            
            if(GeneralServlet.DEBUG)
            {
                GeneralServlet.log.debug("GeneralServlet: about to normalise pool data");
            }
            
            // Normalisation Stage : after results to pool
            Repository convertedPool = myRepository;
            
            try
            {
                convertedPool =
                        ServletUtils
                                .doPoolNormalisation(localSettings, includedProfiles, fetchController, myRepository);
            }
            catch(final Exception ex)
            {
                GeneralServlet.log.error("Found exception while performing pool normalisation", ex);
            }
            catch(final Throwable th)
            {
                GeneralServlet.log.error("Found throwable while performing pool normalisation", th);
            }
            
            if(GeneralServlet.TRACE)
            {
                GeneralServlet.log.trace("GeneralServlet: finished normalising pool data");
            }
            
            // We do not use the default catalina writer as it may not be UTF-8 compliant depending
            // on unchangeable environment variables, instead we wrap up the catalina binary output
            // stream as a guaranteed UTF-8 Writer
            Writer out;
            
            if(writerFormat == null)
            {
                out = new OutputStreamWriter(response.getOutputStream(), Charset.forName("UTF-8"));
            }
            else if(writerFormat.hasCharset())
            {
                out = new OutputStreamWriter(response.getOutputStream(), writerFormat.getCharset());
            }
            else
            {
                // TODO: Support binary results formats by not using a Writer
                out = new OutputStreamWriter(response.getOutputStream(), Charset.forName("UTF-8"));
            }
            
            ServletUtils.resultsToWriter(localVelocityEngine, out, localSettings, writerFormat, realHostName,
                    queryString, pageOffset, requestedContentType, fetchController, debugStrings, convertedPool,
                    contextPath);
            
            if(GeneralServlet.TRACE)
            {
                GeneralServlet.log.trace("GeneralServlet: about to flush output");
            }
            
            out.flush();
            
            final long nextTotalTime = System.currentTimeMillis() - queryStartTime;
            
            if(GeneralServlet.INFO)
            {
                GeneralServlet.log
                        .info("GeneralServlet: query complete requesterIpAddress={} queryString={} pageOffset={} totalTime={}",
                                requesterIpAddress, queryString, pageOffset, nextTotalTime);
            }
            
            // Housekeeping
            
            // update the blacklist
            localBlacklistController.accumulateBlacklist(fetchController.getErrorResults());
            
            final boolean resetClientBlacklistFailuresOnSuccess =
                    localSettings.getBoolean(WebappConfig.BLACKLIST_RESET_ENDPOINT_FAILURES_ON_SUCCESS);
            final boolean resetClientBlacklistWithEndpoints =
                    localSettings.getBoolean(WebappConfig.BLACKLIST_RESET_CLIENT_BLACKLIST_WITH_ENDPOINTS);
            
            if(resetClientBlacklistFailuresOnSuccess)
            {
                localBlacklistController.removeEndpointsFromBlacklist(fetchController.getSuccessfulResults(),
                        nextTotalTime, resetClientBlacklistWithEndpoints);
            }
            
            // Don't keep local error statistics if GeneralServlet debug level is higher than or
            // equal to info and we aren't interested in using the client IP blacklist
            // functionalities
            if(GeneralServlet.INFO
                    || localSettings.getBoolean(WebappConfig.BLACKLIST_AUTOMATICALLY_BLACKLIST_CLIENTS,
                            (Boolean)WebappConfig.BLACKLIST_AUTOMATICALLY_BLACKLIST_CLIENTS.getDefaultValue()))
            {
                ServletUtils.doQueryDebug(localBlacklistController, queryString, requesterIpAddress,
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
        finally
        {
            try
            {
                myRepository.shutDown();
            }
            catch(final RepositoryException ordfe)
            {
                GeneralServlet.log.error("GeneralServlet.doGet: caught RDF exception", ordfe);
            }
        }
    }
    
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException
    {
        final QueryAllConfiguration localSettings =
                (QueryAllConfiguration)this.getServletContext().getAttribute(SettingsContextListener.QUERYALL_CONFIG);
        final BlacklistController localBlacklistController =
                (BlacklistController)this.getServletContext().getAttribute(SettingsContextListener.QUERYALL_BLACKLIST);
        final ContentTypeNegotiator localContentTypeNegotiator =
                (ContentTypeNegotiator)this.getServletContext().getAttribute(
                        SettingsContextListener.QUERYALL_CONTENTNEGOTIATOR);
        final VelocityEngine localVelocityEngine =
                (VelocityEngine)this.getServletContext().getAttribute(SettingsContextListener.QUERYALL_VELOCITY);
        
        GeneralServlet.doGetRequest(request, response, localSettings, localBlacklistController,
                localContentTypeNegotiator, localVelocityEngine);
    }
    
}
