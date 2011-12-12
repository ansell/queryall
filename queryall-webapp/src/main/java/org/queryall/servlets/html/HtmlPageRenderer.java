package org.queryall.servlets.html;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.VelocityException;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.repository.Repository;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.utils.PropertyUtils;
import org.queryall.api.utils.WebappConfig;
import org.queryall.query.QueryBundle;
import org.queryall.query.RdfFetchController;
import org.queryall.query.RdfFetcherQueryRunnable;
import org.queryall.utils.ListUtils;
import org.queryall.utils.RdfUtils;
import org.queryall.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A servlet for serving the HTML page describing a resource. Invokes a Velocity template.
 * 
 * Originally created for Pubby using Jena by...
 * 
 * @author Richard Cyganiak (richard@cyganiak.de) Ported to Sesame for Bio2RDF by...
 * @author Peter Ansell (p_ansell@yahoo.com)
 * @version $Id: HtmlPageRenderer.java 944 2011-02-08 10:23:08Z p_ansell $
 */

public class HtmlPageRenderer
{
    private static final Logger log = LoggerFactory.getLogger(HtmlPageRenderer.class);
    private static final boolean _TRACE = HtmlPageRenderer.log.isTraceEnabled();
    private static final boolean _DEBUG = HtmlPageRenderer.log.isDebugEnabled();
    private static final boolean _INFO = HtmlPageRenderer.log.isInfoEnabled();
    
    public static void renderHtml(final VelocityEngine nextEngine, final QueryAllConfiguration localSettings,
            final RdfFetchController fetchController, final Repository nextRepository, final java.io.Writer nextWriter,
            final String queryString, final String resolvedUri, String realHostName, String contextPath,
            int pageoffset, final Collection<String> debugStrings) throws OpenRDFException
    {
        boolean nextpagelinkuseful = false;
        boolean previouspagelinkuseful = false;
        int previouspageoffset = pageoffset - 1;
        
        if(fetchController != null)
        {
            for(final RdfFetcherQueryRunnable nextResult : fetchController.getResults())
            {
                debugStrings.add("<!-- "
                        + StringUtils.xmlEncodeString(nextResult.getResultDebugString()).replace("--", "- -") + "-->");
            }
        }
        
        if(contextPath == null || contextPath.equals("/"))
        {
            contextPath = "";
        }
        else if(contextPath.startsWith("/") && contextPath.length() > 1)
        {
            // take off the first slash and add one to the end for our purposes
            contextPath = contextPath.substring(1) + "/";
        }
        
        if(localSettings.getBooleanProperty(WebappConfig.USE_HARDCODED_REQUEST_CONTEXT))
        {
            contextPath = localSettings.getStringProperty(WebappConfig.HARDCODED_REQUEST_CONTEXT);
        }
        
        if(localSettings.getBooleanProperty(WebappConfig.USE_HARDCODED_REQUEST_HOSTNAME))
        {
            realHostName = localSettings.getStringProperty(WebappConfig.HARDCODED_REQUEST_HOSTNAME);
        }
        
        if(HtmlPageRenderer._TRACE)
        {
            HtmlPageRenderer.log.trace("renderHtml: about to create VelocityHelper class");
        }
        
        if(HtmlPageRenderer._TRACE)
        {
            HtmlPageRenderer.log.trace("renderHtml: finished creating VelocityHelper class");
        }
        
        final Context velocityContext = new VelocityContext();
        
        velocityContext.put("debug_level_info", HtmlPageRenderer._INFO);
        velocityContext.put("debug_level_debug", HtmlPageRenderer._DEBUG);
        velocityContext.put("debug_level_trace", HtmlPageRenderer._TRACE);
        
        velocityContext.put("project_name", localSettings.getStringProperty(WebappConfig.PROJECT_NAME));
        velocityContext.put("project_base_url", localSettings.getStringProperty(WebappConfig.PROJECT_HOME_URI));
        velocityContext.put("project_html_url_prefix", localSettings.getStringProperty(WebappConfig.HTML_URL_PREFIX));
        velocityContext.put("project_html_url_suffix", localSettings.getStringProperty(WebappConfig.HTML_URL_SUFFIX));
        velocityContext.put("project_link", localSettings.getStringProperty(WebappConfig.PROJECT_HOME_URL));
        velocityContext.put("application_name", localSettings.getStringProperty(WebappConfig.USER_AGENT) + "/"
                + PropertyUtils.VERSION);
        velocityContext.put("application_help", localSettings.getStringProperty(WebappConfig.APPLICATION_HELP_URL));
        velocityContext.put("uri", resolvedUri);
        
        boolean is_plainnsid = false;
        
        if(queryString != null)
        {
            velocityContext.put("query_string", queryString);
            
            if(StringUtils.isPlainNamespaceAndIdentifier(queryString, localSettings))
            {
                is_plainnsid = true;
                
                final Map<String, List<String>> namespaceAndIdentifier =
                        StringUtils.getNamespaceAndIdentifier(queryString, localSettings);
                
                if(namespaceAndIdentifier.size() == 2)
                {
                    // HACK FIXME: these may not always be returned as input_1 and input_2
                    velocityContext.put("namespace", namespaceAndIdentifier.get("input_1").get(0));
                    velocityContext.put("identifier", namespaceAndIdentifier.get("input_2").get(0));
                }
                else
                {
                    HtmlPageRenderer.log
                            .warn("Namespace and identifier did not have exactly two components: namesapceAndIdentifier.size()="
                                    + namespaceAndIdentifier.size());
                }
            }
        }
        
        velocityContext.put("is_plainnsid", is_plainnsid);
        velocityContext.put("real_hostname", realHostName);
        velocityContext.put("context_path", contextPath);
        velocityContext.put("server_base", realHostName + contextPath);
        velocityContext.put("rdfxml_link",
                realHostName + contextPath + localSettings.getStringProperty(WebappConfig.RDFXML_URL_PREFIX)
                        + queryString + localSettings.getStringProperty(WebappConfig.RDFXML_URL_SUFFIX));
        velocityContext.put("rdfn3_link",
                realHostName + contextPath + localSettings.getStringProperty(WebappConfig.N3_URL_PREFIX) + queryString
                        + localSettings.getStringProperty(WebappConfig.N3_URL_SUFFIX));
        velocityContext.put("html_link",
                realHostName + contextPath + localSettings.getStringProperty(WebappConfig.HTML_URL_PREFIX)
                        + queryString + localSettings.getStringProperty(WebappConfig.HTML_URL_SUFFIX));
        velocityContext.put("json_link",
                realHostName + contextPath + localSettings.getStringProperty(WebappConfig.JSON_URL_PREFIX)
                        + queryString + localSettings.getStringProperty(WebappConfig.JSON_URL_SUFFIX));
        // context.put("disco_link", discoLink);
        // context.put("tabulator_link", tabulatorLink);
        // context.put("openlink_link", openLinkLink);
        final Set<String> endpointsList = new HashSet<String>();
        
        if(fetchController != null)
        {
            for(final QueryBundle nextQueryBundle : fetchController.getQueryBundles())
            {
                for(final String nextAlternativeEndpoint : nextQueryBundle.getAlternativeEndpointsAndQueries().keySet())
                {
                    endpointsList.add(nextAlternativeEndpoint);
                }
            }
        }
        
        velocityContext.put("provider_endpoints", endpointsList);
        
        if(fetchController != null)
        {
            velocityContext.put("query_bundles", fetchController.getQueryBundles());
        }
        
        // Collection<Value> titles = new HashSet<Value>();
        // Collection<Value> comments = new HashSet<Value>();
        // Collection<Value> images = new HashSet<Value>();
        final Collection<Value> titles =
                RdfUtils.getValuesFromRepositoryByPredicateUris(nextRepository,
                        localSettings.getURIProperties(WebappConfig.TITLE_PROPERTIES));
        final Collection<Value> comments =
                RdfUtils.getValuesFromRepositoryByPredicateUris(nextRepository,
                        localSettings.getURIProperties(WebappConfig.COMMENT_PROPERTIES));
        final Collection<Value> images =
                RdfUtils.getValuesFromRepositoryByPredicateUris(nextRepository,
                        localSettings.getURIProperties(WebappConfig.IMAGE_PROPERTIES));
        
        String chosenTitle = "";
        
        while(chosenTitle.trim().equals("") && titles.size() > 0)
        {
            chosenTitle = RdfUtils.getUTF8StringValueFromSesameValue(ListUtils.chooseRandomItemFromCollection(titles));
            
            if(chosenTitle.trim().equals(""))
            {
                titles.remove(chosenTitle);
            }
        }
        
        if(chosenTitle.trim().equals(""))
        {
            velocityContext.put("title", localSettings.getStringProperty(WebappConfig.BLANK_TITLE));
        }
        else
        {
            velocityContext.put("title", chosenTitle);
        }
        
        velocityContext.put("titles", titles);
        velocityContext.put("comments", comments);
        velocityContext.put("images", images);
        
        velocityContext.put("shortcut_icon", localSettings.getStringProperty(WebappConfig.SHORTCUT_ICON_PATH));
        velocityContext.put("scripts", localSettings.getStringProperties(WebappConfig.INDEX_PAGE_SCRIPTS));
        velocityContext.put("local_scripts", localSettings.getStringProperties(WebappConfig.INDEX_PAGE_SCRIPTS_LOCAL));
        velocityContext.put("stylesheets", localSettings.getStringProperties(WebappConfig.INDEX_PAGE_STYLESHEETS));
        velocityContext.put("local_stylesheets",
                localSettings.getStringProperties(WebappConfig.INDEX_PAGE_STYLESHEETS_LOCAL));
        
        // For each URI in localSettings.IMAGE_QUERY_TYPES
        // Make sure the URI is a valid QueryType
        // If there are any matches, replace the input_NN's with the namespace and identifier known
        // here and then show a link to the image in HTML
        // Collection<Provider> providersForThisNamespace =
        // localSettings.getProvidersForQueryTypeForNamespaceUris(String customService,
        // Collection<Collection<String>> namespaceUris, NamespaceEntry.)
        
        final List<Statement> allStatements = RdfUtils.getAllStatementsFromRepository(nextRepository);
        
        // TODO: go through the statements and check an internal label cache to see if there is an
        // existing label available
        // and if not schedule a thread to retrieve a label for the item so that for other uses it
        // can be shown
        
        HtmlPageRenderer.log.info("HtmlPageRenderer: allStatements.size()=" + allStatements.size());
        
        // TODO: what should be shown if there are no RDF statements? A 404 error doesn't seem
        // appropriate because the query might be quite legitimate and no triples is the valid
        // response
        
        velocityContext.put("statements", allStatements);
        
        velocityContext.put("xmlutil", new info.aduna.xml.XMLUtil());
        velocityContext.put("bio2rdfutil", new org.queryall.utils.RdfUtils());
        
        // our only way of guessing if other pages are available without doing an explicit count
        if(allStatements.size() >= localSettings.getIntProperty(WebappConfig.PAGEOFFSET_INDIVIDUAL_QUERY_LIMIT))
        {
            nextpagelinkuseful = true;
        }
        
        if(pageoffset > 1)
        {
            previouspagelinkuseful = true;
        }
        else if(pageoffset < 1)
        {
            // pageoffset less than one does not need a nextpagelinkuseful as it is useful as a
            // marker to avoid this function
            pageoffset = 1;
            previouspageoffset = 1;
            
            previouspagelinkuseful = false;
            nextpagelinkuseful = false;
        }
        
        final int pageoffsetMaxValue = localSettings.getIntProperty(WebappConfig.PAGEOFFSET_MAX_VALUE);
        
        // To prevent infinite or extended requests, we have a maximum value that we can go up to
        if(pageoffset > pageoffsetMaxValue)
        {
            // setup the pageoffset value so it artificially points to the limit so that
            // non-conforming robots that don't follow robots.txt don't accidentally run into issues
            // when people play around with links to very high page offsets
            previouspageoffset = pageoffsetMaxValue;
            nextpagelinkuseful = false;
        }
        
        // If configured to only show pageoffset for plain nsid's as opposed to the other queries
        // then decide here whether to show it
        if(localSettings.getBooleanProperty(WebappConfig.PAGEOFFSET_ONLY_SHOW_FOR_NSID) && !is_plainnsid)
        {
            nextpagelinkuseful = false;
        }
        
        if(nextpagelinkuseful)
        {
            velocityContext.put(
                    "nextpagelink",
                    realHostName + contextPath + localSettings.getStringProperty(WebappConfig.HTML_URL_PREFIX)
                            + localSettings.getStringProperty(WebappConfig.PAGEOFFSET_URL_OPENING_PREFIX)
                            + (pageoffset + 1)
                            + localSettings.getStringProperty(WebappConfig.PAGEOFFSET_URL_CLOSING_PREFIX) + queryString
                            + localSettings.getStringProperty(WebappConfig.PAGEOFFSET_URL_SUFFIX)
                            + localSettings.getStringProperty(WebappConfig.HTML_URL_SUFFIX));
            velocityContext.put("nextpagelabel", (pageoffset + 1));
        }
        
        if(previouspagelinkuseful)
        {
            velocityContext.put(
                    "previouspagelink",
                    realHostName + contextPath + localSettings.getStringProperty(WebappConfig.HTML_URL_PREFIX)
                            + localSettings.getStringProperty(WebappConfig.PAGEOFFSET_URL_OPENING_PREFIX)
                            + (previouspageoffset)
                            + localSettings.getStringProperty(WebappConfig.PAGEOFFSET_URL_CLOSING_PREFIX) + queryString
                            + localSettings.getStringProperty(WebappConfig.PAGEOFFSET_URL_SUFFIX)
                            + localSettings.getStringProperty(WebappConfig.HTML_URL_SUFFIX));
            velocityContext.put("previouspagelabel", previouspageoffset);
        }
        
        velocityContext.put("debugStrings", debugStrings);
        
        velocityContext.put("xmlEncoded_testString", "<test&amp;\"\'&>");
        
        // http://velocity.apache.org/engine/devel/webapps.html
        // Any user-entered text that contains special HTML or XML entities (such as <, >, or &)
        // needs to be escaped before included in the web page. This is required, both to ensure the
        // text is visible, and also to prevent dangerous cross-site scripting . Unlike, for
        // example, JSTL (the Java Standard Tag Language found in Java Server Pages), Velocity does
        // not escape references by default.
        //
        // However, Velocity provides the ability to specify a ReferenceInsertionEventHandler which
        // will alter the value of a reference before it is inserted into the page. Specifically,
        // you can configure the EscapeHtmlReference handler into your velocity.properties file to
        // escape all references (optionally) matching a regular expression. The following example
        // will escape HTML entities in any reference that starts with "msg" (e.g. $msgText).
        //
        // eventhandler.referenceinsertion.class =
        // org.apache.velocity.app.event.implement.EscapeHtmlReference
        // eventhandler.escape.html.match = /msg.*/
        //
        // Note that other kinds of escaping are sometimes required. For example, in style sheets
        // the @ character needs to be escaped, and in Javascript strings the single apostrophe '
        // needs to be escaped.
        
        if(HtmlPageRenderer._TRACE)
        {
            HtmlPageRenderer.log.trace("renderHtml: about to render XHTML to nextWriter=" + nextWriter);
        }
        
        try
        {
            if(fetchController == null || fetchController.queryKnown())
            {
                if(HtmlPageRenderer._DEBUG)
                {
                    HtmlPageRenderer.log.debug("renderHtml: fetchController.queryKnown(), using results template");
                }
                final String templateLocation = localSettings.getStringProperty(WebappConfig.RESULTS_TEMPLATE);
                // final VelocityEngine nextEngine =
                // (VelocityEngine)servletContext.getAttribute(SettingsContextListener.QUERYALL_VELOCITY);
                
                VelocityHelper.renderXHTML(nextEngine, velocityContext, templateLocation, nextWriter);
            }
            else
            {
                if(HtmlPageRenderer._DEBUG)
                {
                    HtmlPageRenderer.log.debug("renderHtml: !fetchController.queryKnown(), using error template");
                }
                
                velocityContext.put("namespaceRecognised", !fetchController.anyNamespaceNotRecognised());
                velocityContext.put("queryKnown", fetchController.queryKnown());
                
                final String templateLocation = localSettings.getStringProperty(WebappConfig.ERROR_TEMPLATE);
                
                // final VelocityEngine nextEngine =
                // (VelocityEngine)servletContext.getAttribute(SettingsContextListener.QUERYALL_VELOCITY);
                
                VelocityHelper.renderXHTML(nextEngine, velocityContext, templateLocation, nextWriter);
            }
        }
        catch(final VelocityException ex)
        {
            HtmlPageRenderer.log.error("renderHtml: caught exception while rendering XHTML", ex);
            
            try
            {
                nextWriter.write("Fatal error. See logs for details");
            }
            catch(final IOException ioe)
            {
                HtmlPageRenderer.log.error("renderHtml: Could not write out error message to nextWriter");
            }
        }
        
        if(HtmlPageRenderer._TRACE)
        {
            HtmlPageRenderer.log.trace("renderHtml: finished rendering XHTML");
        }
    }
    
    public static void renderHtml(final VelocityEngine nextEngine, final QueryAllConfiguration localSettings,
            final Repository nextRepository, final java.io.Writer nextWriter, final String queryString,
            final String resolvedUri, final String realHostName, final String contextPath, final int pageoffset,
            final Collection<String> debugStrings) throws OpenRDFException
    {
        HtmlPageRenderer.renderHtml(nextEngine, localSettings, null, nextRepository, nextWriter, queryString,
                resolvedUri, realHostName, contextPath, pageoffset, debugStrings);
    }
    
    public static void renderIndexPage(final QueryAllConfiguration localSettings, final VelocityEngine nextEngine,
            final java.io.Writer nextWriter, final Collection<String> debugStrings, String realHostName,
            String contextPath) throws OpenRDFException
    {
        if(contextPath == null || contextPath.equals("/"))
        {
            contextPath = "";
        }
        else if(contextPath.startsWith("/") && contextPath.length() > 1)
        {
            // take off the first slash and add one to the end for our purposes
            contextPath = contextPath.substring(1) + "/";
        }
        
        if(localSettings.getBooleanProperty(WebappConfig.USE_HARDCODED_REQUEST_CONTEXT))
        {
            contextPath = localSettings.getStringProperty(WebappConfig.HARDCODED_REQUEST_CONTEXT);
        }
        
        if(localSettings.getBooleanProperty(WebappConfig.USE_HARDCODED_REQUEST_HOSTNAME))
        {
            realHostName = localSettings.getStringProperty(WebappConfig.HARDCODED_REQUEST_HOSTNAME);
        }
        
        if(HtmlPageRenderer._TRACE)
        {
            HtmlPageRenderer.log.trace("renderIndexPage: about to create VelocityHelper class");
        }
        
        final Context velocityContext = new VelocityContext();
        velocityContext.put("statistics_providers", Integer.toString(localSettings.getAllProviders().size()));
        velocityContext.put("statistics_namespaceentries",
                Integer.toString(localSettings.getAllNamespaceEntries().size()));
        velocityContext.put("statistics_normalisationrules",
                Integer.toString(localSettings.getAllNormalisationRules().size()));
        velocityContext.put("statistics_normalisationruletests",
                Integer.toString(localSettings.getAllRuleTests().size()));
        velocityContext.put("statistics_querytypes", Integer.toString(localSettings.getAllQueryTypes().size()));
        
        velocityContext.put("debug_level_info", HtmlPageRenderer._INFO);
        velocityContext.put("debug_level_debug", HtmlPageRenderer._DEBUG);
        velocityContext.put("debug_level_trace", HtmlPageRenderer._TRACE);
        
        velocityContext.put("title", localSettings.getStringProperty(WebappConfig.PROJECT_NAME));
        
        velocityContext.put("project_name", localSettings.getStringProperty(WebappConfig.PROJECT_NAME));
        velocityContext.put("project_base_url", localSettings.getStringProperty(WebappConfig.PROJECT_HOME_URI));
        velocityContext.put("project_html_url_prefix", localSettings.getStringProperty(WebappConfig.HTML_URL_PREFIX));
        velocityContext.put("project_html_url_suffix", localSettings.getStringProperty(WebappConfig.HTML_URL_SUFFIX));
        velocityContext.put("project_link", localSettings.getStringProperty(WebappConfig.PROJECT_HOME_URL));
        velocityContext.put("application_name", localSettings.getStringProperty(WebappConfig.USER_AGENT) + "/"
                + PropertyUtils.VERSION);
        velocityContext.put("application_help", localSettings.getStringProperty(WebappConfig.APPLICATION_HELP_URL));
        
        velocityContext
                .put("index_banner_image", localSettings.getStringProperty(WebappConfig.INDEX_BANNER_IMAGE_PATH));
        velocityContext.put("index_project_image",
                localSettings.getStringProperty(WebappConfig.INDEX_PROJECT_IMAGE_PATH));
        
        velocityContext.put("shortcut_icon", localSettings.getStringProperty(WebappConfig.SHORTCUT_ICON_PATH));
        velocityContext.put("scripts", localSettings.getStringProperties(WebappConfig.INDEX_PAGE_SCRIPTS));
        velocityContext.put("local_scripts", localSettings.getStringProperties(WebappConfig.INDEX_PAGE_SCRIPTS_LOCAL));
        velocityContext.put("stylesheets", localSettings.getStringProperties(WebappConfig.INDEX_PAGE_STYLESHEETS));
        velocityContext.put("local_stylesheets",
                localSettings.getStringProperties(WebappConfig.INDEX_PAGE_STYLESHEETS_LOCAL));
        
        velocityContext.put("real_hostname", realHostName);
        velocityContext.put("context_path", contextPath);
        velocityContext.put("server_base", realHostName + contextPath);
        
        final String templateLocation = localSettings.getStringProperty(WebappConfig.INDEX_TEMPLATE);
        
        try
        {
            VelocityHelper.renderXHTML(nextEngine, velocityContext, templateLocation, nextWriter);
        }
        catch(final Exception ex)
        {
            HtmlPageRenderer.log.error("renderIndexPage: caught exception while rendering XHTML");
            HtmlPageRenderer.log.error(ex.getMessage());
            
            try
            {
                nextWriter.write("Fatal error. See logs for details");
            }
            catch(final IOException ioe)
            {
                HtmlPageRenderer.log.error("renderIndexPage: Could not write out error message to nextWriter");
            }
        }
        
        if(HtmlPageRenderer._TRACE)
        {
            HtmlPageRenderer.log.trace("renderIndexPage: finished rendering XHTML");
        }
    }
}
