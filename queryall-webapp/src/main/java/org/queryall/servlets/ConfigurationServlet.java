package org.queryall.servlets;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.queryall.servlets.queryparsers.*;
import org.queryall.servlets.html.*;
import org.queryall.helpers.*;
import org.queryall.api.NamespaceEntry;
import org.queryall.api.NormalisationRule;
import org.queryall.api.Profile;
import org.queryall.api.Provider;
import org.queryall.api.QueryType;
import org.queryall.api.RuleTest;
import org.queryall.blacklist.*;

import org.openrdf.*;
import org.openrdf.model.*;
import org.openrdf.rio.*;
import org.openrdf.repository.*;
import org.openrdf.repository.sail.*;
import org.openrdf.sail.memory.*;


import org.apache.log4j.Logger;

/** 
 * 
 */

public class ConfigurationServlet extends HttpServlet 
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 3372992659745059491L;
	
	public static final Logger log = Logger.getLogger(ConfigurationServlet.class.getName());
    public static final boolean _TRACE = log.isTraceEnabled();
    public static final boolean _DEBUG = log.isDebugEnabled();
    public static final boolean _INFO = log.isInfoEnabled();

    
    @Override
    public void doGet(HttpServletRequest request,
                        HttpServletResponse response)
        throws ServletException, IOException 
    {
        Settings localSettings = Settings.getSettings();
        BlacklistController localBlacklistController = BlacklistController.getDefaultController();
        
        if(_INFO)
        {
            log.info("request.getRequestURI()="+request.getRequestURI());
            log.info("ConfigurationServlet: acceptHeader="+request.getHeader("Accept")+" userAgent="+request.getHeader("User-Agent"));
        }
        
       ConfigurationQueryOptions requestConfigurationQueryOptions = new ConfigurationQueryOptions(request.getRequestURI(), request.getContextPath(), localSettings);
        
        PrintWriter out = response.getWriter();
        
        java.io.StringWriter stBuff = new java.io.StringWriter();
        
        String originalRequestedContentType = QueryallContentNegotiator.getResponseContentType(request.getHeader("Accept"), request.getHeader("User-Agent"));
        
        String requestedContentType = originalRequestedContentType;
        
        String explicitUrlContentType = "";

        if(requestConfigurationQueryOptions.isRefresh())
        {
            if(localSettings.isManualRefreshAllowed())
            {
                if(localSettings.configRefreshCheck(true))
                {
                    localBlacklistController.doBlacklistExpiry();
                    
                    response.setStatus(HttpServletResponse.SC_OK);
                    log.info("manualrefresh.jsp: manual refresh succeeded requesterIpAddress="+request.getRemoteAddr());
                    out.write("Refresh succeeded.");
                }
                else
                {
                    response.setStatus(500);
                    log.error("manualrefresh.jsp: refresh failed for an unknown reason, as it was supposedly allowed in a previous check requesterIpAddress="+request.getRemoteAddr());
                    out.write("Refresh failed for an unknown reason");
                }
            }
            else
            {
                response.setStatus(401);
                log.error("manualrefresh.jsp: refresh not allowed right now requesterIpAddress="+request.getRemoteAddr()+ " localSettings.MANUAL_CONFIGURATION_REFRESH_ALLOWED="+localSettings.getStringPropertyFromConfig("enableManualConfigurationRefresh", ""));
                out.write("Refresh not allowed right now.");
            }
            
            out.flush();
            return;
        }
        
        if(requestConfigurationQueryOptions.containsExplicitFormat())
        {
            explicitUrlContentType = requestConfigurationQueryOptions.getExplicitFormat();
            // override whatever was requested with the urlrewrite variable
            requestedContentType = explicitUrlContentType;
        }
        
        if(_INFO)
        {
        	log.info("requestedContentType="+requestedContentType);
        }
        
        String realHostName = request.getScheme() + "://" + request.getServerName() + (request.getServerPort() == 80 && request.getScheme().equals("http") ? "" : ":"+ request.getServerPort())+"/";
                
        String queryString = requestConfigurationQueryOptions.getParsedRequest();
        
        if(queryString == null)
        {
            queryString = "";
        }
        
        int apiVersion = requestConfigurationQueryOptions.getApiVersion();
        
        if(apiVersion > Settings.CONFIG_API_VERSION)
        {
            log.error("ConfigurationServlet: requested API version not supported by this server. apiVersion="+apiVersion+" Settings.CONFIG_API_VERSION="+Settings.CONFIG_API_VERSION);
            
            response.setContentType("text/plain");
            response.setStatus(400);
            out.write("Requested API version not supported by this server. Current supported version="+Settings.CONFIG_API_VERSION);
            out.flush();
            return;
        }
        
        Collection<String> debugStrings = new HashSet<String>();
        
        String writerFormatString = RdfUtils.findWriterFormat(requestedContentType, localSettings.getStringPropertyFromConfig("preferredDisplayContentType", ""), "application/rdf+xml");
        
        RDFFormat writerFormat = null;
        
        if(!writerFormatString.equals("text/html"))
        {
            writerFormat = Rio.getWriterFormatForMIMEType(writerFormatString);
        }

        localSettings.configRefreshCheck(false);
        
        response.setContentType(requestedContentType);
        response.setCharacterEncoding("UTF-8");
        
        boolean targetOnlyQueryString = false;
        
        final String queryStringURI = localSettings.getDefaultHostAddress()+queryString;
        
        if(_INFO)
        	log.info("queryStringUri="+queryStringURI);
        
        if(StringUtils.isPlainNamespaceAndIdentifier(queryString, localSettings))
        {
            targetOnlyQueryString = true;

            if(_INFO)
            	log.info("requested plain namespace and identifier from configuration");
        }
        
        
        Repository myRepository = new SailRepository(new MemoryStore());

        RepositoryConnection myRepositoryConnection = null;
        
        try
        {
            myRepository.initialize();
            
            if(requestConfigurationQueryOptions.containsAdminConfiguration() || targetOnlyQueryString)
            {
                Map<URI, Provider> allProviders = localSettings.getAllProviders();
                
                for(URI nextProviderKey : allProviders.keySet())
                {
                    if(!targetOnlyQueryString || queryStringURI.equals(nextProviderKey.stringValue()))
                    {
                        try
                        {
                            if(!allProviders.get(nextProviderKey).toRdf(myRepository, nextProviderKey, apiVersion))
                            {
                                log.error("ConfigurationServlet: Provider was not placed correctly in the rdf store key="+nextProviderKey);
                                // out.write("<!-- "+RdfUtils.xmlEncodeString(allProviders.get(nextProviderKey).toString()).replace("--","- -")+" -->");
                            }
                        }
                        catch(Exception ex)
                        {
                            log.error("ConfigurationServlet: Problem generating Provider RDF with key: "+nextProviderKey+" type="+ex.getClass().getName());
                            log.error(ex.getMessage());
                            // out.write("Problem generating Provider RDF with key: "+nextProviderKey+"<br />\n");
                            // out.write(RdfUtils.xmlEncodeString(allProviders.get(nextProviderKey).toString()));
                        }
                    }
                }
                
                Map<URI, QueryType> allQueries = localSettings.getAllQueryTypes();
                
                for(URI nextQueryKey : allQueries.keySet())
                {
                    if(!targetOnlyQueryString || queryStringURI.equals(nextQueryKey.stringValue()))
                    {
                        try
                        {
                            if(!allQueries.get(nextQueryKey).toRdf(myRepository, nextQueryKey, apiVersion))
                            {
                                log.error("ConfigurationServlet: Custom Query was not placed correctly in the rdf store key="+nextQueryKey);
                                // out.write(RdfUtils.xmlEncodeString(allQueries.get(nextQueryKey).toString()));
                            }
                        }
                        catch(Exception ex)
                        {
                            log.error("ConfigurationServlet: Problem generating Query RDF with key: "+nextQueryKey+" type="+ex.getClass().getName());
                            log.error(ex.getMessage());
                            // out.write("Problem generating Query RDF with key: "+nextQueryKey+"<br />\n");
                            // out.write(RdfUtils.xmlEncodeString(allQueries.get(nextQueryKey).toString()));
                        }
                    }
                }
                
                Map<URI, NormalisationRule> allNormalisationRules = localSettings.getAllNormalisationRules();
                
                for(URI nextNormalisationRuleKey : allNormalisationRules.keySet())
                {
                    if(!targetOnlyQueryString || queryStringURI.equals(nextNormalisationRuleKey.stringValue()))
                    {
                        try
                        {
                            if(!allNormalisationRules.get(nextNormalisationRuleKey).toRdf(myRepository, nextNormalisationRuleKey, apiVersion))
                            {
                                log.error("ConfigurationServlet: Rdf Normalisation Rule was not placed correctly in the rdf store key="+nextNormalisationRuleKey);
                            }
                        }
                        catch(Exception ex)
                        {
                            log.error("ConfigurationServlet: Problem generating Rdf Rule RDF with key: "+nextNormalisationRuleKey+" type="+ex.getClass().getName());
                            log.error(ex.getMessage());
                            // out.write("Problem generating Rdf Rule RDF with key: "+nextNormalisationRuleKey+"<br />\n");
                            // out.write(RdfUtils.xmlEncodeString(allNormalisationRules.get(nextNormalisationRuleKey).toString()));
                        }
                    }
                }
                
                Map<URI, RuleTest> allRuleTests = localSettings.getAllRuleTests();
                
                for(URI nextRuleTestKey : allRuleTests.keySet())
                {
                    if(!targetOnlyQueryString || queryStringURI.equals(nextRuleTestKey.stringValue()))
                    {
                        try
                        {
                            if(!allRuleTests.get(nextRuleTestKey).toRdf(myRepository, nextRuleTestKey, apiVersion))
                            {
                                log.error("ConfigurationServlet: Rule Test was not placed correctly in the rdf store key="+nextRuleTestKey);
                            }
                        }
                        catch(Exception ex)
                        {
                            log.error("ConfigurationServlet: Problem generating Rule Test RDF with key: "+nextRuleTestKey+" type="+ex.getClass().getName());
                            log.error(ex.getMessage());
                        }
                    }
                }
                
                Map<URI, NamespaceEntry> allNamespaceEntries = localSettings.getAllNamespaceEntries();
                
                for(URI nextNamespaceEntryKey : allNamespaceEntries.keySet())
                {
                    if(!targetOnlyQueryString || queryStringURI.equals(nextNamespaceEntryKey.stringValue()))
                    {
                        try
                        {
                            if(!allNamespaceEntries.get(nextNamespaceEntryKey).toRdf(myRepository, nextNamespaceEntryKey, apiVersion))
                            {
                                log.error("ConfigurationServlet: Namespace Entry was not placed correctly in the rdf store key="+nextNamespaceEntryKey);
                            }
                        }
                        catch(Exception ex)
                        {
                            log.error("ConfigurationServlet: Problem generating RDF with namespace: "+nextNamespaceEntryKey);
                            log.error(ex.getMessage());
                        }
                    }
                }
                
                Map<URI, Profile> allProfiles = localSettings.getAllProfiles();
                
                for(URI nextProfileKey : allProfiles.keySet())
                {
                    if(!targetOnlyQueryString || queryStringURI.equals(nextProfileKey.stringValue()))
                    {
                        try
                        {
                            // log.info("Debug-configuration: nextProfileKey="+nextProfileKey);
                            
                            if(!allProfiles.get(nextProfileKey).toRdf(myRepository, allProfiles.get(nextProfileKey).getKey(), apiVersion))
                            {
                                log.error("ConfigurationServlet: Profile was not placed correctly in the rdf store key="+nextProfileKey);
                                // out.write(RdfUtils.xmlEncodeString(allNormalisationRules.get(nextNormalisationRuleKey).toString()));
                            }
                        }
                        catch(Exception ex)
                        {
                            log.error("ConfigurationServlet: Problem generating Profile RDF with key: "+nextProfileKey+" type="+ex.getClass().getName());
                            log.error(ex.getMessage());
                            // out.write("Problem generating Rdf Rule RDF with key: "+nextNormalisationRuleKey+"<br />\n");
                            // out.write(RdfUtils.xmlEncodeString(allNormalisationRules.get(nextNormalisationRuleKey).toString()));
                        }
                    }
                }
            }
            else if(requestConfigurationQueryOptions.containsAdminBasicWebappConfiguration())
            {
                myRepositoryConnection = myRepository.getConnection();

                // TODO: put this list into a properties or RDf file
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("userAgent"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("robotHelpUrl"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("projectHomeUri"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("uriPrefix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("hostName"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("uriSuffix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("separator"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("rdfXmlUrlPrefix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("rdfXmlUrlSuffix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("n3UrlPrefix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("n3UrlSuffix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("htmlUrlPrefix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("htmlUrlSuffix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("jsonUrlPrefix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("jsonUrlSuffix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("ntriplesUrlPrefix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("ntriplesUrlSuffix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("nquadsUrlPrefix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("nquadsUrlSuffix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("alwaysRedirectToExplicitFormatUrl"));

                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("redirectToExplicitFormatHttpCode"));

                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("queryplanUrlPrefix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("queryplanUrlSuffix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("pageoffsetUrlOpeningPrefix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("pageoffsetUrlClosingPrefix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("pageoffsetUrlSuffix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("adminUrlPrefix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("adminWebappConfigurationPrefix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("adminConfigurationPrefix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("adminConfigurationRefreshPrefix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("adminConfigurationApiVersionOpeningPrefix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("adminConfigurationApiVersionClosingPrefix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("adminConfigurationApiVersionSuffix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("adminConfigurationHtmlPrefix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("adminConfigurationHtmlSuffix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("adminConfigurationRdfxmlPrefix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("adminConfigurationRdfxmlSuffix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("adminConfigurationN3Prefix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("adminConfigurationN3Suffix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("adminConfigurationJsonPrefix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("adminConfigurationJsonSuffix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("adminConfigurationNTriplesPrefix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("adminConfigurationNTriplesSuffix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("adminConfigurationNQuadsPrefix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("adminConfigurationNQuadsSuffix"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("pageoffsetOnlyShowForNsId"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("plainNamespaceAndIdentifierRegex"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("plainNamespaceRegex"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("tagPatternRegex"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("blankTitle"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("projectHomeUrl"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("projectName"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("applicationHelpUrl"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("blacklistContactEmailAddress"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("blacklistRedirectPage"));

                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("autogenerateIncludeStubList"));

                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("titleProperties"));

                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("imageProperties"));

                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("commentProperties"));

                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("urlProperties"));

                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("indexTemplate"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("resultsTemplate"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("errorTemplate"));

                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("indexPageScripts"));

                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("indexPageScriptsLocal"));

                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("indexPageStylesheets"));

                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("indexPageStylesheetsLocal"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("resultsPageScripts"));

                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("resultsPageScriptsLocal"));

                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("resultsPageStylesheets"));

                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("resultsPageStylesheetsLocal"));
                
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("shortcutIconPath"));

                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("indexBannerImagePath"));

                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("indexProjectImagePath"));

                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("unknownQueryStaticAdditions"));

                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("unknownQueryHttpResponseCode"));

                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("unknownNamespaceStaticAdditions"));

                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("unknownNamespaceHttpResponseCode"));

                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("useAllEndpointsForEachProvider"));

                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("pageoffsetMaxValue"));
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("pageoffsetIndividualQueryLimit"));
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("preferredDisplayContentType"));
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("preferredDisplayLanguage"));
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("assumedResponseContentType"));
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("defaultAcceptHeader"));
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("useRequestCache"));
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("convertAlternateNamespacePrefixesToPreferred"));
                myRepositoryConnection.add(localSettings.getStatementPropertiesFromConfig("useVirtuosoMaxRowsParameter"));
            }
            
            if(myRepositoryConnection != null)
            {
                myRepositoryConnection.close();
            }
            
            if(requestedContentType.equals("text/html"))
            {
                if(_INFO)
                {
                    log.info("about to call html rendering method");
                }
                
                try
                {
                    HtmlPageRenderer.renderHtml(getServletContext(), myRepository, stBuff, debugStrings, queryString, localSettings.getDefaultHostAddress() + queryString, realHostName, request.getContextPath(), -1, localSettings);
                }
                catch(OpenRDFException ordfe)
                {
                    log.error("couldn't render HTML because of an RDF exception", ordfe);
                }
                catch(Exception ex)
                {
                    log.error("couldn't render HTML because of an unknown exception", ex);
                }
            }
            else
            {
                if(_INFO)
                {
                    log.info("about to call rdf rendering method");
                }
                RdfUtils.toWriter(myRepository, stBuff, writerFormat);
            }
        
        }
        catch(OpenRDFException ordfe)
        {
            log.error("ConfigurationServlet: error", ordfe);
        }
        
        if(_INFO)
        {
            log.info("about to call out.write");
        }
        
        for(int i = 0; i < stBuff.getBuffer().length(); i++)
        	out.write(stBuff.getBuffer().charAt(i));
//        out.write(stBuff.toString());
        
        if(_INFO)
        {
            log.info("about to call out.flush");
        }
        out.flush();

        if(_INFO)
        {
            log.info("finished");
        }
    }
}

