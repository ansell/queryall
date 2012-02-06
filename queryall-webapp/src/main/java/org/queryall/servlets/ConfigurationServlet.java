package org.queryall.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
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
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.Provider;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.rdfrule.NormalisationRule;
import org.queryall.api.ruletest.RuleTest;
import org.queryall.api.utils.WebappConfig;
import org.queryall.blacklist.BlacklistController;
import org.queryall.negotiation.QueryallContentNegotiator;
import org.queryall.servlets.helpers.SettingsContextListener;
import org.queryall.servlets.html.HtmlPageRenderer;
import org.queryall.servlets.queryparsers.ConfigurationQueryOptions;
import org.queryall.utils.RdfUtils;
import org.queryall.utils.Settings;
import org.queryall.utils.SettingsFactory;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fuberlin.wiwiss.pubby.negotiation.ContentTypeNegotiator;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ConfigurationServlet extends HttpServlet
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 3372992659745059491L;
    
    public static final Logger log = LoggerFactory.getLogger(ConfigurationServlet.class);
    public static final boolean TRACE = ConfigurationServlet.log.isTraceEnabled();
    public static final boolean DEBUG = ConfigurationServlet.log.isDebugEnabled();
    public static final boolean INFO = ConfigurationServlet.log.isInfoEnabled();
    
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException
    {
        final QueryAllConfiguration localSettings =
                (Settings)this.getServletContext().getAttribute(SettingsContextListener.QUERYALL_CONFIG);
        final BlacklistController localBlacklistController =
                (BlacklistController)this.getServletContext().getAttribute(SettingsContextListener.QUERYALL_BLACKLIST);
        final ContentTypeNegotiator localContentTypeNegotiator =
                (ContentTypeNegotiator)this.getServletContext().getAttribute(
                        SettingsContextListener.QUERYALL_CONTENTNEGOTIATOR);
        final VelocityEngine localVelocityEngine =
                (VelocityEngine)this.getServletContext().getAttribute(SettingsContextListener.QUERYALL_VELOCITY);
        
        if(ConfigurationServlet.INFO)
        {
            ConfigurationServlet.log.info("request.getRequestURI()=" + request.getRequestURI());
            ConfigurationServlet.log.info("ConfigurationServlet: acceptHeader=" + request.getHeader("Accept")
                    + " userAgent=" + request.getHeader("User-Agent"));
        }
        
        final ConfigurationQueryOptions requestConfigurationQueryOptions =
                new ConfigurationQueryOptions(request.getRequestURI(), request.getContextPath(), localSettings);
        
        final PrintWriter out = response.getWriter();
        
        final java.io.StringWriter stBuff = new java.io.StringWriter();
        
        final String originalRequestedContentType =
                QueryallContentNegotiator.getResponseContentType(request.getHeader("Accept"),
                        request.getHeader("User-Agent"), localContentTypeNegotiator,
                        localSettings.getStringProperty(WebappConfig.PREFERRED_DISPLAY_CONTENT_TYPE));
        
        String requestedContentType = originalRequestedContentType;
        
        String explicitUrlContentType = "";
        
        if(requestConfigurationQueryOptions.isRefresh())
        {
            out.write("Refresh currently disabled");
            
            return;
            
            // if(localSettings.isManualRefreshAllowed())
            // {
            // if(localSettings.configRefreshCheck(true))
            // {
            // localBlacklistController.doBlacklistExpiry();
            //
            // response.setStatus(HttpServletResponse.SC_OK);
            // ConfigurationServlet.log.info("manualrefresh.jsp: manual refresh succeeded requesterIpAddress="
            // + request.getRemoteAddr());
            // out.write("Refresh succeeded.");
            // }
            // else
            // {
            // response.setStatus(500);
            // ConfigurationServlet.log
            // .error("manualrefresh.jsp: refresh failed for an unknown reason, as it was supposedly allowed in a previous check requesterIpAddress="
            // + request.getRemoteAddr());
            // out.write("Refresh failed for an unknown reason");
            // }
            // }
            // else
            // {
            // response.setStatus(401);
            // ConfigurationServlet.log.error("manualrefresh.jsp: refresh not allowed right now requesterIpAddress="
            // + request.getRemoteAddr() + " localSettings.MANUAL_CONFIGURATION_REFRESH_ALLOWED="
            // + localSettings.getStringProperty("enableManualConfigurationRefresh", ""));
            // out.write("Refresh not allowed right now.");
            // }
            //
            // out.flush();
            // return;
        }
        
        if(requestConfigurationQueryOptions.containsExplicitFormat())
        {
            explicitUrlContentType = requestConfigurationQueryOptions.getExplicitFormat();
            // override whatever was requested with the urlrewrite variable
            requestedContentType = explicitUrlContentType;
        }
        
        if(ConfigurationServlet.INFO)
        {
            ConfigurationServlet.log.info("requestedContentType=" + requestedContentType);
        }
        
        final String realHostName =
                request.getScheme()
                        + "://"
                        + request.getServerName()
                        + (request.getServerPort() == 80 && request.getScheme().equals("http") ? "" : ":"
                                + request.getServerPort()) + "/";
        
        String queryString = requestConfigurationQueryOptions.getParsedRequest();
        
        if(queryString == null)
        {
            queryString = "";
        }
        
        final int apiVersion = requestConfigurationQueryOptions.getApiVersion();
        
        if(apiVersion > SettingsFactory.CONFIG_API_VERSION)
        {
            ConfigurationServlet.log
                    .error("ConfigurationServlet: requested API version not supported by this server. apiVersion="
                            + apiVersion + " Settings.CONFIG_API_VERSION=" + SettingsFactory.CONFIG_API_VERSION);
            
            response.setContentType("text/plain");
            response.setStatus(400);
            out.write("Requested API version not supported by this server. Current supported version="
                    + SettingsFactory.CONFIG_API_VERSION);
            out.flush();
            return;
        }
        
        final Collection<String> debugStrings = new HashSet<String>();
        
        final String writerFormatString =
                RdfUtils.findBestContentType(requestedContentType,
                        localSettings.getStringProperty(WebappConfig.PREFERRED_DISPLAY_CONTENT_TYPE),
                        "application/rdf+xml");
        
        RDFFormat writerFormat = null;
        
        if(!writerFormatString.equals("text/html"))
        {
            writerFormat = RdfUtils.getWriterFormat(writerFormatString);
        }
        
        // localSettings.configRefreshCheck(false);
        
        response.setContentType(requestedContentType);
        response.setCharacterEncoding("UTF-8");
        
        boolean targetOnlyQueryString = false;
        
        final String queryStringURI = localSettings.getDefaultHostAddress() + queryString;
        
        if(ConfigurationServlet.INFO)
        {
            ConfigurationServlet.log.info("queryStringUri=" + queryStringURI);
        }
        
        if(StringUtils.isPlainNamespaceAndIdentifier(queryString, localSettings))
        {
            targetOnlyQueryString = true;
            
            if(ConfigurationServlet.INFO)
            {
                ConfigurationServlet.log.info("requested plain namespace and identifier from configuration");
            }
        }
        
        final Repository myRepository = new SailRepository(new MemoryStore());
        
        RepositoryConnection myRepositoryConnection = null;
        
        try
        {
            myRepository.initialize();
            
            if(requestConfigurationQueryOptions.containsAdminConfiguration() || targetOnlyQueryString)
            {
                final Map<URI, Provider> allProviders = localSettings.getAllProviders();
                
                for(final URI nextProviderKey : allProviders.keySet())
                {
                    if(!targetOnlyQueryString || queryStringURI.equals(nextProviderKey.stringValue()))
                    {
                        try
                        {
                            if(!allProviders.get(nextProviderKey).toRdf(myRepository, apiVersion, nextProviderKey))
                            {
                                ConfigurationServlet.log
                                        .error("ConfigurationServlet: Provider was not placed correctly in the rdf store key="
                                                + nextProviderKey);
                                // out.write("<!-- "+RdfUtils.xmlEncodeString(allProviders.get(nextProviderKey).toString()).replace("--","- -")+" -->");
                            }
                        }
                        catch(final Exception ex)
                        {
                            ConfigurationServlet.log
                                    .error("ConfigurationServlet: Problem generating Provider RDF with key: "
                                            + nextProviderKey + " type=" + ex.getClass().getName());
                            ConfigurationServlet.log.error(ex.getMessage());
                            // out.write("Problem generating Provider RDF with key: "+nextProviderKey+"<br />\n");
                            // out.write(RdfUtils.xmlEncodeString(allProviders.get(nextProviderKey).toString()));
                        }
                    }
                }
                
                final Map<URI, QueryType> allQueries = localSettings.getAllQueryTypes();
                
                for(final URI nextQueryKey : allQueries.keySet())
                {
                    if(!targetOnlyQueryString || queryStringURI.equals(nextQueryKey.stringValue()))
                    {
                        try
                        {
                            if(!allQueries.get(nextQueryKey).toRdf(myRepository, apiVersion, nextQueryKey))
                            {
                                ConfigurationServlet.log
                                        .error("ConfigurationServlet: Custom Query was not placed correctly in the rdf store key="
                                                + nextQueryKey);
                                // out.write(RdfUtils.xmlEncodeString(allQueries.get(nextQueryKey).toString()));
                            }
                        }
                        catch(final Exception ex)
                        {
                            ConfigurationServlet.log
                                    .error("ConfigurationServlet: Problem generating Query RDF with key: "
                                            + nextQueryKey + " type=" + ex.getClass().getName());
                            ConfigurationServlet.log.error(ex.getMessage());
                            // out.write("Problem generating Query RDF with key: "+nextQueryKey+"<br />\n");
                            // out.write(RdfUtils.xmlEncodeString(allQueries.get(nextQueryKey).toString()));
                        }
                    }
                }
                
                final Map<URI, NormalisationRule> allNormalisationRules = localSettings.getAllNormalisationRules();
                
                for(final URI nextNormalisationRuleKey : allNormalisationRules.keySet())
                {
                    if(!targetOnlyQueryString || queryStringURI.equals(nextNormalisationRuleKey.stringValue()))
                    {
                        try
                        {
                            if(!allNormalisationRules.get(nextNormalisationRuleKey).toRdf(myRepository, apiVersion,
                                    nextNormalisationRuleKey))
                            {
                                ConfigurationServlet.log
                                        .error("ConfigurationServlet: Rdf Normalisation Rule was not placed correctly in the rdf store key="
                                                + nextNormalisationRuleKey);
                            }
                        }
                        catch(final Exception ex)
                        {
                            ConfigurationServlet.log
                                    .error("ConfigurationServlet: Problem generating Rdf Rule RDF with key: "
                                            + nextNormalisationRuleKey + " type=" + ex.getClass().getName());
                            ConfigurationServlet.log.error(ex.getMessage());
                            // out.write("Problem generating Rdf Rule RDF with key: "+nextNormalisationRuleKey+"<br />\n");
                            // out.write(RdfUtils.xmlEncodeString(allNormalisationRules.get(nextNormalisationRuleKey).toString()));
                        }
                    }
                }
                
                final Map<URI, RuleTest> allRuleTests = localSettings.getAllRuleTests();
                
                for(final URI nextRuleTestKey : allRuleTests.keySet())
                {
                    if(!targetOnlyQueryString || queryStringURI.equals(nextRuleTestKey.stringValue()))
                    {
                        try
                        {
                            if(!allRuleTests.get(nextRuleTestKey).toRdf(myRepository, apiVersion, nextRuleTestKey))
                            {
                                ConfigurationServlet.log
                                        .error("ConfigurationServlet: Rule Test was not placed correctly in the rdf store key="
                                                + nextRuleTestKey);
                            }
                        }
                        catch(final Exception ex)
                        {
                            ConfigurationServlet.log
                                    .error("ConfigurationServlet: Problem generating Rule Test RDF with key: "
                                            + nextRuleTestKey + " type=" + ex.getClass().getName());
                            ConfigurationServlet.log.error(ex.getMessage());
                        }
                    }
                }
                
                final Map<URI, NamespaceEntry> allNamespaceEntries = localSettings.getAllNamespaceEntries();
                
                for(final URI nextNamespaceEntryKey : allNamespaceEntries.keySet())
                {
                    if(!targetOnlyQueryString || queryStringURI.equals(nextNamespaceEntryKey.stringValue()))
                    {
                        try
                        {
                            if(!allNamespaceEntries.get(nextNamespaceEntryKey).toRdf(myRepository, apiVersion,
                                    nextNamespaceEntryKey))
                            {
                                ConfigurationServlet.log
                                        .error("ConfigurationServlet: Namespace Entry was not placed correctly in the rdf store key="
                                                + nextNamespaceEntryKey);
                            }
                        }
                        catch(final Exception ex)
                        {
                            ConfigurationServlet.log
                                    .error("ConfigurationServlet: Problem generating RDF with namespace: "
                                            + nextNamespaceEntryKey);
                            ConfigurationServlet.log.error(ex.getMessage());
                        }
                    }
                }
                
                final Map<URI, Profile> allProfiles = localSettings.getAllProfiles();
                
                for(final URI nextProfileKey : allProfiles.keySet())
                {
                    if(!targetOnlyQueryString || queryStringURI.equals(nextProfileKey.stringValue()))
                    {
                        try
                        {
                            // log.info("Debug-configuration: nextProfileKey="+nextProfileKey);
                            
                            if(!allProfiles.get(nextProfileKey).toRdf(myRepository, apiVersion,
                                    allProfiles.get(nextProfileKey).getKey()))
                            {
                                ConfigurationServlet.log
                                        .error("ConfigurationServlet: Profile was not placed correctly in the rdf store key="
                                                + nextProfileKey);
                                // out.write(RdfUtils.xmlEncodeString(allNormalisationRules.get(nextNormalisationRuleKey).toString()));
                            }
                        }
                        catch(final Exception ex)
                        {
                            ConfigurationServlet.log
                                    .error("ConfigurationServlet: Problem generating Profile RDF with key: "
                                            + nextProfileKey + " type=" + ex.getClass().getName());
                            ConfigurationServlet.log.error(ex.getMessage());
                            // out.write("Problem generating Rdf Rule RDF with key: "+nextNormalisationRuleKey+"<br />\n");
                            // out.write(RdfUtils.xmlEncodeString(allNormalisationRules.get(nextNormalisationRuleKey).toString()));
                        }
                    }
                }
            }
            else if(requestConfigurationQueryOptions.containsAdminBasicWebappConfiguration())
            {
                myRepositoryConnection = myRepository.getConnection();
                
                /**
                 * // TODO: put this list into a properties or RDf file
                 * myRepositoryConnection.add(localSettings.getStatementProperties("userAgent"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties("robotHelpUrl"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties("projectHomeUri")
                 * );
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties("uriPrefix"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties("hostName"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties("uriSuffix"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties("separator"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties("rdfXmlUrlPrefix"
                 * ));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties("rdfXmlUrlSuffix"
                 * ));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties("n3UrlPrefix"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties("n3UrlSuffix"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties("htmlUrlPrefix"))
                 * ;
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties("htmlUrlSuffix"))
                 * ;
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties("jsonUrlPrefix"))
                 * ;
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties("jsonUrlSuffix"))
                 * ;
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "ntriplesUrlPrefix"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "ntriplesUrlSuffix"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties("nquadsUrlPrefix"
                 * ));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties("nquadsUrlSuffix"
                 * ));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "alwaysRedirectToExplicitFormatUrl"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "redirectToExplicitFormatHttpCode"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "queryplanUrlPrefix"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "queryplanUrlSuffix"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "pageoffsetUrlOpeningPrefix"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "pageoffsetUrlClosingPrefix"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "pageoffsetUrlSuffix"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties("adminUrlPrefix")
                 * );
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "adminWebappConfigurationPrefix"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "adminConfigurationPrefix"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "adminConfigurationRefreshPrefix"));
                 * 
                 * myRepositoryConnection.add(localSettings
                 * .getStatementProperties("adminConfigurationApiVersionOpeningPrefix"));
                 * 
                 * myRepositoryConnection.add(localSettings
                 * .getStatementProperties("adminConfigurationApiVersionClosingPrefix"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "adminConfigurationApiVersionSuffix"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "adminConfigurationHtmlPrefix"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "adminConfigurationHtmlSuffix"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "adminConfigurationRdfxmlPrefix"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "adminConfigurationRdfxmlSuffix"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "adminConfigurationN3Prefix"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "adminConfigurationN3Suffix"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "adminConfigurationJsonPrefix"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "adminConfigurationJsonSuffix"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "adminConfigurationNTriplesPrefix"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "adminConfigurationNTriplesSuffix"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "adminConfigurationNQuadsPrefix"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "adminConfigurationNQuadsSuffix"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "pageoffsetOnlyShowForNsId"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "plainNamespaceAndIdentifierRegex"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "plainNamespaceRegex"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties("tagPatternRegex"
                 * ));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties("blankTitle"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties("projectHomeUrl")
                 * );
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties("projectName"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "applicationHelpUrl"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "blacklistContactEmailAddress"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "blacklistRedirectPage"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "autogenerateIncludeStubList"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties("titleProperties"
                 * ));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties("imageProperties"
                 * ));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "commentProperties"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties("urlProperties"))
                 * ;
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties("indexTemplate"))
                 * ;
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties("resultsTemplate"
                 * ));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties("errorTemplate"))
                 * ;
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "indexPageScripts"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "indexPageScriptsLocal"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "indexPageStylesheets"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "indexPageStylesheetsLocal"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "resultsPageScripts"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "resultsPageScriptsLocal"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "resultsPageStylesheets"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "resultsPageStylesheetsLocal"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "shortcutIconPath"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "indexBannerImagePath"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "indexProjectImagePath"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "unknownQueryStaticAdditions"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "unknownQueryHttpResponseCode"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "unknownNamespaceStaticAdditions"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "unknownNamespaceHttpResponseCode"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "useAllEndpointsForEachProvider"));
                 * 
                 * myRepositoryConnection.add(localSettings.getStatementProperties(
                 * "pageoffsetMaxValue"));
                 * myRepositoryConnection.add(localSettings.getStatementProperties
                 * ("pageoffsetIndividualQueryLimit"));
                 * myRepositoryConnection.add(localSettings.getStatementProperties
                 * ("preferredDisplayContentType"));
                 * myRepositoryConnection.add(localSettings.getStatementProperties
                 * ("preferredDisplayLanguage"));
                 * myRepositoryConnection.add(localSettings.getStatementProperties
                 * ("assumedResponseContentType"));
                 * myRepositoryConnection.add(localSettings.getStatementProperties
                 * ("defaultAcceptHeader"));
                 * myRepositoryConnection.add(localSettings.getStatementProperties
                 * ("useRequestCache")); myRepositoryConnection.add(localSettings
                 * .getStatementProperties("convertAlternateNamespacePrefixesToPreferred"));
                 * myRepositoryConnection
                 * .add(localSettings.getStatementProperties("useVirtuosoMaxRowsParameter"));
                 **/
            }
            
            if(myRepositoryConnection != null)
            {
                myRepositoryConnection.close();
            }
            
            if(requestedContentType.equals("text/html"))
            {
                if(ConfigurationServlet.INFO)
                {
                    ConfigurationServlet.log.info("about to call html rendering method");
                }
                
                try
                {
                    HtmlPageRenderer.renderHtml(localVelocityEngine, localSettings, myRepository, stBuff, queryString,
                            localSettings.getDefaultHostAddress() + queryString, realHostName,
                            request.getContextPath(), -1, debugStrings);
                }
                catch(final OpenRDFException ordfe)
                {
                    ConfigurationServlet.log.error("couldn't render HTML because of an RDF exception", ordfe);
                }
                catch(final Exception ex)
                {
                    ConfigurationServlet.log.error("couldn't render HTML because of an unknown exception", ex);
                }
            }
            else
            {
                if(ConfigurationServlet.INFO)
                {
                    ConfigurationServlet.log.info("about to call rdf rendering method");
                }
                RdfUtils.toWriter(myRepository, stBuff, writerFormat);
            }
            
        }
        catch(final OpenRDFException ordfe)
        {
            ConfigurationServlet.log.error("ConfigurationServlet: error", ordfe);
        }
        
        if(ConfigurationServlet.INFO)
        {
            ConfigurationServlet.log.info("about to call out.write");
        }
        
        for(int i = 0; i < stBuff.getBuffer().length(); i++)
        {
            out.write(stBuff.getBuffer().charAt(i));
            // out.write(stBuff.toString());
        }
        
        if(ConfigurationServlet.INFO)
        {
            ConfigurationServlet.log.info("about to call out.flush");
        }
        out.flush();
        
        if(ConfigurationServlet.INFO)
        {
            ConfigurationServlet.log.info("finished");
        }
    }
}
