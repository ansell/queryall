/**
 * 
 */
package org.queryall.servlets.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
import org.queryall.api.querytype.InputQueryType;
import org.queryall.api.querytype.OutputQueryType;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.querytype.RdfOutputQueryType;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.utils.Constants;
import org.queryall.api.utils.SortOrder;
import org.queryall.api.utils.WebappConfig;
import org.queryall.blacklist.BlacklistController;
import org.queryall.exception.QueryAllException;
import org.queryall.exception.UnnormalisableRuleException;
import org.queryall.query.QueryBundle;
import org.queryall.query.QueryCreator;
import org.queryall.query.QueryDebug;
import org.queryall.query.RdfFetchController;
import org.queryall.query.RdfFetcherQueryRunnable;
import org.queryall.servlets.GeneralServlet;
import org.queryall.servlets.html.HtmlPageRenderer;
import org.queryall.servlets.queryparsers.DefaultQueryOptions;
import org.queryall.utils.RdfUtils;
import org.queryall.utils.RuleUtils;
import org.queryall.utils.SettingsFactory;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class ServletUtils
{
    private static final Logger log = LoggerFactory.getLogger(ServletUtils.class);
    
    /**
     * @param response
     * @param localSettings
     * @param requestQueryOptions
     * @param contextPath
     * @param requestedContentType
     * @return
     */
    public static boolean checkExplicitRedirect(final HttpServletResponse response,
            final QueryAllConfiguration localSettings, final DefaultQueryOptions requestQueryOptions,
            final String contextPath, final String requestedContentType)
    {
        if(!requestQueryOptions.containsExplicitFormat())
        {
            if(localSettings.getBooleanProperty(WebappConfig.ALWAYS_REDIRECT_TO_EXPLICIT_FORMAT_URL))
            {
                final int redirectCode =
                        localSettings.getIntProperty(WebappConfig.REDIRECT_TO_EXPLICIT_FORMAT_HTTP_CODE);
                
                final StringBuilder redirectString = new StringBuilder();
                final boolean ignoreContextPath = false;
                
                ServletUtils.getRedirectString(redirectString, localSettings, requestQueryOptions,
                        requestedContentType, ignoreContextPath, contextPath);
                
                if(GeneralServlet.INFO)
                {
                    ServletUtils.log.info("Sending redirect using redirectCode=" + redirectCode + " to redirectString="
                            + redirectString.toString());
                }
                if(GeneralServlet.DEBUG)
                {
                    ServletUtils.log.debug("contextPath=" + contextPath);
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
    public static Repository doPoolNormalisation(final QueryAllConfiguration localSettings,
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
                    localSettings.getBooleanProperty(WebappConfig.RECOGNISE_IMPLICIT_RDFRULE_INCLUSIONS),
                    localSettings.getBooleanProperty(WebappConfig.INCLUDE_NON_PROFILE_MATCHED_RDFRULES));
        }
        catch(final UnnormalisableRuleException e)
        {
            ServletUtils.log.error("Found unnormalisable rule exception while normalising the pool", e);
            throw new QueryAllException("Found unnormalisable rule exception while normalising the pool", e);
        }
        catch(final QueryAllException e)
        {
            ServletUtils.log.error("Found queryall checked exception while normalising the pool", e);
            throw e;
        }
    }
    
    /**
     * @param localBlacklistController
     * @param queryString
     * @param requesterIpAddress
     * @param multiProviderQueryBundles
     * @param nextTotalTime
     */
    public static void doQueryDebug(final BlacklistController localBlacklistController, final String queryString,
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
        
        localBlacklistController.accumulateQueryDebug(nextQueryDebug);
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
    public static void doQueryNotPretend(final QueryAllConfiguration localSettings, final String queryString,
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
            
            if(GeneralServlet.INFO)
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
                if(GeneralServlet.INFO)
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
                
                if(GeneralServlet.TRACE)
                {
                    ServletUtils.log.trace("GeneralServlet: normalised result string : "
                            + nextResult.getNormalisedResult());
                }
                
                Repository tempRepository = new SailRepository(new MemoryStore());
                tempRepository.initialize();
                
                RdfUtils.insertResultIntoRepository(nextResult, tempRepository,
                        localSettings.getStringProperty(WebappConfig.ASSUMED_RESPONSE_CONTENT_TYPE),
                        localSettings.getDefaultHostAddress());
                
                // Perform normalisation for the AfterResultsImport stage
                tempRepository =
                        (Repository)RuleUtils.normaliseByStage(NormalisationRuleSchema
                                .getRdfruleStageAfterResultsImport(), tempRepository, RuleUtils.getSortedRulesByUris(
                                localSettings.getAllNormalisationRules(), nextResult.getOriginalQueryBundle()
                                        .getProvider().getNormalisationUris(), SortOrder.HIGHEST_ORDER_FIRST),
                                includedProfiles, localSettings
                                        .getBooleanProperty(WebappConfig.RECOGNISE_IMPLICIT_RDFRULE_INCLUSIONS),
                                localSettings.getBooleanProperty(WebappConfig.INCLUDE_NON_PROFILE_MATCHED_RDFRULES));
                
                if(GeneralServlet.DEBUG)
                {
                    final RepositoryConnection tempRepositoryConnection = tempRepository.getConnection();
                    
                    ServletUtils.log.debug("GeneralServlet: getAllStatementsFromRepository(tempRepository).size()="
                            + RdfUtils.getAllStatementsFromRepository(tempRepository).size());
                    ServletUtils.log.debug("GeneralServlet: tempRepositoryConnection.size()="
                            + tempRepositoryConnection.size());
                }
                
                RdfUtils.copyAllStatementsToRepository(myRepository, tempRepository);
            }
            
            for(final QueryBundle nextPotentialQueryBundle : multiProviderQueryBundles)
            {
                String nextStaticString = nextPotentialQueryBundle.getStaticRdfXmlString();
                
                if(GeneralServlet.TRACE)
                {
                    ServletUtils.log
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
                    // TODO: make this section customisable, so it doesn't always need to accept RDF
                    // directly
                    if(nextPotentialQueryBundle.getQueryType() != null
                            && nextPotentialQueryBundle.getQueryType() instanceof RdfOutputQueryType)
                    {
                        myRepositoryConnection.add(new java.io.StringReader(nextStaticString),
                                localSettings.getDefaultHostAddress() + queryString, RdfUtils.getWriterFormat(RdfUtils
                                        .findBestContentType(((RdfOutputQueryType)nextPotentialQueryBundle
                                                .getQueryType()).getOutputRdfFormat(), Constants.APPLICATION_RDF_XML,
                                                Constants.APPLICATION_RDF_XML)), nextPotentialQueryBundle.getProvider()
                                        .getKey());
                    }
                    else if(nextPotentialQueryBundle.getQueryType() != null
                            && nextPotentialQueryBundle.getQueryType() instanceof OutputQueryType)
                    {
                        myRepositoryConnection.add(new java.io.StringReader(nextStaticString),
                                localSettings.getDefaultHostAddress() + queryString, RdfUtils
                                        .getWriterFormat(Constants.APPLICATION_RDF_XML), nextPotentialQueryBundle
                                        .getProvider().getKey());
                    }
                    else if(nextPotentialQueryBundle.getQueryType() != null)
                    {
                        ServletUtils.log.warn("Found a query type that was not an instance of OutputQueryType");
                    }
                }
                catch(final org.openrdf.rio.RDFParseException rdfpe)
                {
                    ServletUtils.log.error("GeneralServlet: RDFParseException: static RDF " + rdfpe.getMessage());
                    ServletUtils.log.error("GeneralServlet: nextStaticString=" + nextStaticString);
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
     * @param queryString
     * @param responseCode
     * @param pageOffset
     * @param requestedContentType
     * @param multiProviderQueryBundles
     * @param myRepository
     * @param separator
     *            TODO
     * @param response
     * @throws IOException
     * @throws OpenRDFException
     */
    public static void doQueryPretend(final String queryString, final int responseCode, final int pageOffset,
            final String requestedContentType, final Collection<QueryBundle> multiProviderQueryBundles,
            final Repository myRepository, final String separator) throws IOException, OpenRDFException
    {
        for(final QueryBundle nextScheduledQueryBundle : multiProviderQueryBundles)
        {
            nextScheduledQueryBundle.toRdf(
                    myRepository,
                    StringUtils.createURI(StringUtils.percentEncode(queryString)
                            + separator
                            + "pageoffset"
                            + pageOffset
                            + separator
                            + StringUtils.percentEncode(nextScheduledQueryBundle.getProvider().getKey().stringValue()
                                    .toLowerCase())
                            + separator
                            + StringUtils.percentEncode(nextScheduledQueryBundle.getQueryType().getKey().stringValue()
                                    .toLowerCase()) + separator),
                    // + StringUtils.percentEncode(nextScheduledQueryBundle.getQueryEndpoint())),
                    SettingsFactory.CONFIG_API_VERSION);
        }
        
        if(GeneralServlet.TRACE)
        {
            ServletUtils.log.trace("GeneralServlet: Finished with pretend query bundle rdf generation");
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
    public static void doQueryUnknown(final QueryAllConfiguration localSettings, final String realHostName,
            final Map<String, String> queryParameters, final int pageOffset, final String requestedContentType,
            final List<Profile> includedProfiles, final RdfFetchController fetchController,
            final Collection<String> debugStrings, final Repository myRepository) throws IOException,
        RepositoryException, QueryAllException
    {
        RepositoryConnection myRepositoryConnection = null;
        
        final boolean convertAlternateToPreferredPrefix =
                localSettings.getBooleanProperty(WebappConfig.CONVERT_ALTERNATE_NAMESPACE_PREFIXES_TO_PREFERRED);
        
        try
        {
            myRepositoryConnection = myRepository.getConnection();
            
            final Collection<String> currentStaticStrings = new HashSet<String>();
            
            Collection<URI> staticQueryTypesForUnknown = new ArrayList<URI>(1);
            
            // TODO: attempt to generate a non-empty namespaceEntryMap in this case??
            if(fetchController.anyNamespaceNotRecognised())
            {
                staticQueryTypesForUnknown =
                        localSettings.getURIProperties(WebappConfig.UNKNOWN_NAMESPACE_STATIC_ADDITIONS);
            }
            else
            {
                staticQueryTypesForUnknown =
                        localSettings.getURIProperties(WebappConfig.UNKNOWN_QUERY_STATIC_ADDITIONS);
            }
            
            for(final URI nextStaticQueryTypeForUnknown : staticQueryTypesForUnknown)
            {
                if(GeneralServlet.DEBUG)
                {
                    ServletUtils.log.debug("GeneralServlet: nextStaticQueryTypeForUnknown="
                            + nextStaticQueryTypeForUnknown);
                }
                
                final QueryType nextIncludeType = localSettings.getAllQueryTypes().get(nextStaticQueryTypeForUnknown);
                
                if(nextIncludeType == null)
                {
                    throw new QueryAllException(
                            "Could not find query type for static unknown query type nextStaticQueryTypeForUnknown="
                                    + nextStaticQueryTypeForUnknown.stringValue()
                                    + " fetchController.anyNamespaceNotRecognised()="
                                    + fetchController.anyNamespaceNotRecognised());
                }
                
                // If we didn't understand the query
                final Map<String, Collection<NamespaceEntry>> emptyNamespaceEntryMap = Collections.emptyMap();
                
                if(nextIncludeType instanceof InputQueryType && nextIncludeType instanceof OutputQueryType)
                {
                    final Map<String, String> attributeList =
                            QueryCreator.getAttributeListFor(nextIncludeType, null, queryParameters,
                                    localSettings.getStringProperty(WebappConfig.HOST_NAME), realHostName, pageOffset,
                                    localSettings.getStringProperty(WebappConfig.HOST_NAME),
                                    localSettings.getDefaultHostAddress(), localSettings.getSeparator());
                    
                    // This is a last ditch solution to giving some meaningful feedback, as we
                    // assume that the unknown query type will handle the input, so we pass it in as
                    // both parameters
                    String nextBackupString =
                            QueryCreator
                                    .createStaticRdfXmlString(
                                            (InputQueryType)nextIncludeType,
                                            (OutputQueryType)nextIncludeType,
                                            null,
                                            attributeList,
                                            emptyNamespaceEntryMap,
                                            includedProfiles,
                                            localSettings
                                                    .getBooleanProperty(WebappConfig.RECOGNISE_IMPLICIT_RDFRULE_INCLUSIONS),
                                            localSettings
                                                    .getBooleanProperty(WebappConfig.INCLUDE_NON_PROFILE_MATCHED_RDFRULES),
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
                        ServletUtils.log.error("GeneralServlet: RDFParseException: static RDF " + rdfpe.getMessage());
                        ServletUtils.log.error("GeneralServlet: nextBackupString=" + nextBackupString);
                    }
                }
                else
                {
                    ServletUtils.log
                            .warn("Attempted to include a static, unknown-query/namespace, type that was not parsed as both an input and output query type key="
                                    + nextIncludeType.getKey() + " types=" + nextIncludeType.getElementTypes());
                }
            }
            
            if(currentStaticStrings.size() == 0)
            {
                ServletUtils.log.error("Could not find anything at all to match at query level queryString="
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
            
            if(GeneralServlet.TRACE)
            {
                ServletUtils.log.trace("GeneralServlet: ending !fetchController.queryKnown() section");
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
        if(localSettings.getBooleanProperty(WebappConfig.USE_HARDCODED_REQUEST_HOSTNAME))
        {
            redirectString.append(localSettings.getStringProperty(WebappConfig.HARDCODED_REQUEST_HOSTNAME));
        }
        
        if(localSettings.getBooleanProperty(WebappConfig.USE_HARDCODED_REQUEST_CONTEXT))
        {
            redirectString.append(localSettings.getStringProperty(WebappConfig.HARDCODED_REQUEST_CONTEXT));
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
        
        // add the prefix for this type
        if(requestedContentType.equals(Constants.TEXT_HTML))
        {
            redirectString.append(localSettings.getStringProperty(WebappConfig.HTML_URL_PREFIX));
        }
        else if(requestedContentType.equals(Constants.TEXT_RDF_N3))
        {
            redirectString.append(localSettings.getStringProperty(WebappConfig.N3_URL_PREFIX));
        }
        else if(requestedContentType.equals(Constants.APPLICATION_RDF_XML))
        {
            redirectString.append(localSettings.getStringProperty(WebappConfig.RDFXML_URL_PREFIX));
        }
        else if(requestedContentType.equals(Constants.APPLICATION_JSON))
        {
            redirectString.append(localSettings.getStringProperty(WebappConfig.JSON_URL_PREFIX));
        }
        else
        {
            ServletUtils.log
                    .warn("Did not recognise requested content type, so not adding a redirect URL prefix for it. requestedContentType="
                            + requestedContentType);
        }
        
        if(requestQueryOptions.isQueryPlanRequest())
        {
            redirectString.append(localSettings.getStringProperty(WebappConfig.QUERYPLAN_URL_PREFIX));
        }
        
        if(requestQueryOptions.containsExplicitPageOffsetValue())
        {
            redirectString.append(localSettings.getStringProperty(WebappConfig.PAGEOFFSET_URL_OPENING_PREFIX));
            redirectString.append(requestQueryOptions.getPageOffset());
            redirectString.append(localSettings.getStringProperty(WebappConfig.PAGEOFFSET_URL_CLOSING_PREFIX));
        }
        
        redirectString.append(requestQueryOptions.getParsedRequest());
        
        if(requestQueryOptions.containsExplicitPageOffsetValue())
        {
            redirectString.append(localSettings.getStringProperty(WebappConfig.PAGEOFFSET_URL_SUFFIX));
        }
        
        if(requestQueryOptions.isQueryPlanRequest())
        {
            redirectString.append(localSettings.getStringProperty(WebappConfig.QUERYPLAN_URL_SUFFIX));
        }
        
        // Then add on the suffix for this type
        if(requestedContentType.equals(Constants.TEXT_HTML))
        {
            redirectString.append(localSettings.getStringProperty(WebappConfig.HTML_URL_SUFFIX));
        }
        else if(requestedContentType.equals(Constants.TEXT_RDF_N3))
        {
            redirectString.append(localSettings.getStringProperty(WebappConfig.N3_URL_SUFFIX));
        }
        else if(requestedContentType.equals(Constants.APPLICATION_RDF_XML))
        {
            redirectString.append(localSettings.getStringProperty(WebappConfig.RDFXML_URL_SUFFIX));
        }
        else if(requestedContentType.equals(Constants.APPLICATION_JSON))
        {
            redirectString.append(localSettings.getStringProperty(WebappConfig.JSON_URL_SUFFIX));
        }
        else
        {
            ServletUtils.log
                    .warn("Did not recognise requested content type, so not adding a redirect URL suffix for it. requestedContentType="
                            + requestedContentType);
        }
    }
    
    /**
     * Encapsulates the basic logging details for a single request
     * 
     * @param serverName
     * @param queryString
     * @param requesterIpAddress
     * @param locale
     * @param characterEncoding
     * @param isPretendQuery
     * @param pageOffset
     * @param originalRequestedContentType
     * @param requestedContentType
     * @param containsExplicitPageOffset
     *            TODO
     * @param acceptHeader
     *            TODO
     * @param userAgentHeader
     *            TODO
     */
    public static void logRequestDetails(final String serverName, final String queryString,
            final String requesterIpAddress, final String locale, final String characterEncoding,
            final boolean isPretendQuery, final int pageOffset, final String originalRequestedContentType,
            final String requestedContentType, final boolean containsExplicitPageOffset, final String acceptHeader,
            final String userAgentHeader)
    {
        if(GeneralServlet.INFO)
        {
            ServletUtils.log.info("GeneralServlet: query started on " + serverName + " requesterIpAddress="
                    + requesterIpAddress + " queryString=" + queryString + " explicitPageOffset="
                    + containsExplicitPageOffset + " pageOffset=" + pageOffset + " isPretendQuery=" + isPretendQuery);
            ServletUtils.log.info("GeneralServlet: requestedContentType=" + requestedContentType + " acceptHeader="
                    + acceptHeader + " userAgent=" + userAgentHeader);
            ServletUtils.log.info("GeneralServlet: locale=" + locale + " characterEncoding=" + characterEncoding);
            
            if(!originalRequestedContentType.equals(requestedContentType))
            {
                ServletUtils.log
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
    public static void resultsToWriter(final VelocityEngine nextEngine, final Writer out,
            final QueryAllConfiguration localSettings, final RDFFormat writerFormat, final String realHostName,
            final String queryString, final int pageOffset, final String requestedContentType,
            final RdfFetchController fetchController, final Collection<String> debugStrings,
            final Repository convertedPool, final String contextPath) throws IOException
    {
        // Assume an average document may easily contain 2000 characters, to save on copies inside
        // the stringwriter
        // By default it starts with only 16 characters if we don't set a number here
        final java.io.StringWriter cleanOutput = new java.io.StringWriter(2000);
        
        // TODO: Make this process generic to allow output to arbitrary formats instead of just
        if(requestedContentType.equals(Constants.TEXT_HTML))
        {
            if(GeneralServlet.DEBUG)
            {
                ServletUtils.log.debug("GeneralServlet: about to call html rendering method");
                ServletUtils.log.debug("GeneralServlet: fetchController.queryKnown()=" + fetchController.queryKnown());
            }
            
            try
            {
                HtmlPageRenderer.renderHtml(nextEngine, localSettings, fetchController, convertedPool, cleanOutput,
                        queryString, localSettings.getDefaultHostAddress() + queryString, realHostName, contextPath,
                        pageOffset, debugStrings);
            }
            catch(final OpenRDFException ordfe)
            {
                ServletUtils.log.error("GeneralServlet: couldn't render HTML because of an RDF exception", ordfe);
            }
            catch(final Exception ex)
            {
                ServletUtils.log.error("GeneralServlet: couldn't render HTML because of an unknown exception", ex);
            }
        }
        else
        {
            if(GeneralServlet.DEBUG)
            {
                ServletUtils.log.debug("GeneralServlet: about to call rdf rendering method");
                ServletUtils.log.debug("GeneralServlet: fetchController.queryKnown()=" + fetchController.queryKnown());
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
            // TODO: Make a sesametools version of the RDF/XML output writer that allows the choice
            // of having a PI or not
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
    public static void sendBasicHeaders(final HttpServletResponse response, final int responseCode,
            final String requestedContentType) throws IOException
    {
        response.setContentType(requestedContentType + "; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(responseCode);
        response.setHeader("Vary", "Accept");
        // TODO: Make the Accept-Control-Allow-Origin header configurable
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.flushBuffer();
    }
    
}
