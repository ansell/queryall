package org.queryall.servlets.html;

import org.bio2rdf.servlets.GeneralServlet;
import org.bio2rdf.servlets.html.VelocityHelper;
import org.queryall.queryutils.*;
import org.queryall.helpers.*;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.HashSet;

import org.openrdf.repository.Repository;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import javax.servlet.ServletContext;

import org.apache.velocity.context.Context;

import org.apache.log4j.Logger;

/**
 * A servlet for serving the HTML page describing a resource.
 * Invokes a Velocity template.
 * 
 * Originally created for Pubby using Jena by...
 * @author Richard Cyganiak (richard@cyganiak.de)
 * Ported to Sesame for Bio2RDF by...
 * @author Peter Ansell (p_ansell@yahoo.com)
 * @version $Id: HtmlPageRenderer.java 944 2011-02-08 10:23:08Z p_ansell $
 */

public class HtmlPageRenderer
{
    private static final Logger log = Logger.getLogger(HtmlPageRenderer.class.getName());
    private static final boolean _TRACE = log.isTraceEnabled();
	private static final boolean _DEBUG = log.isDebugEnabled();
    @SuppressWarnings("unused")
	private static final boolean _INFO = log.isInfoEnabled();
    
    public static void renderHtml(ServletContext servletContext, Repository nextRepository, java.io.Writer nextWriter, Collection<String> debugStrings, String queryString, String resolvedUri, String realHostName, String contextPath, int pageoffset, Settings localSettings) throws OpenRDFException
    {
        renderHtml(servletContext, nextRepository, nextWriter, null, debugStrings, queryString, resolvedUri, realHostName, contextPath, pageoffset, localSettings);
    }
    
	public static void renderHtml(ServletContext servletContext, Repository nextRepository, java.io.Writer nextWriter, RdfFetchController fetchController, Collection<String> debugStrings, String queryString, String resolvedUri, String realHostName, String contextPath, int pageoffset, Settings localSettings) throws OpenRDFException
    {
        boolean nextpagelinkuseful = false;
        boolean previouspagelinkuseful = false;
        int previouspageoffset = pageoffset - 1;
        // String discoLink = "http://www4.wiwiss.fu-berlin.de/rdf_browser/?browse_uri=" + RdfUtils.percentEncode(resolvedUri);
        // String tabulatorLink = "http://dig.csail.mit.edu/2005/ajar/ajaw/tab.html?uri=" + RdfUtils.percentEncode(resolvedUri);
        // String openLinkLink = "http://demo.openlinksw.com/rdfbrowser/?uri=" + RdfUtils.percentEncode(resolvedUri);
        
        if(fetchController != null)
        {
            for(RdfFetcherQueryRunnable nextResult : fetchController.getResults())
            {
                debugStrings.add("<!-- "+StringUtils.xmlEncodeString(nextResult.getResultDebugString()).replace("--","- -") + "-->");
            }
        }
        
        if(contextPath == null || contextPath.equals("/"))
        {
            contextPath = "";
        }
        else if(contextPath.startsWith("/") && contextPath.length() > 1 )
        {
            // take off the first slash and add one to the end for our purposes
            contextPath = contextPath.substring(1)+"/";
        }
        
        if(localSettings.getBooleanPropertyFromConfig("useHardcodedRequestContext", true))
        {
            contextPath = localSettings.getStringPropertyFromConfig("hardcodedRequestContext", "");
        }
        
        if(localSettings.getBooleanPropertyFromConfig("useHardcodedRequestHostname", true))
        {
            realHostName = localSettings.getStringPropertyFromConfig("hardcodedRequestHostname", "");
        }
        
        if(_TRACE)
        {
            log.trace("renderHtml: about to create VelocityHelper class");
        }
        
        
        
        VelocityHelper template = new VelocityHelper(servletContext);
        
        
        if(_TRACE)
        {
            log.trace("renderHtml: finished creating VelocityHelper class");
        }
        
        Context context = template.getVelocityContext();
        
        context.put("debug_level_info", GeneralServlet._INFO);
        context.put("debug_level_debug", GeneralServlet._DEBUG);
        context.put("debug_level_trace", GeneralServlet._TRACE);
        
        
        context.put("project_name", localSettings.getStringPropertyFromConfig("projectName", ""));
        context.put("project_base_url", localSettings.getStringPropertyFromConfig("projectHomeUrl", ""));
        context.put("project_html_url_prefix", localSettings.getStringPropertyFromConfig("htmlUrlPrefix", ""));
        context.put("project_html_url_suffix", localSettings.getStringPropertyFromConfig("htmlUrlSuffix", ""));
        context.put("project_link", localSettings.getStringPropertyFromConfig("projectHomeUrl", ""));
        context.put("application_name", localSettings.getStringPropertyFromConfig("userAgent", "")+ "/"+Settings.VERSION);
        context.put("application_help", localSettings.getStringPropertyFromConfig("applicationHelpUrl", ""));
        context.put("uri", resolvedUri);
        
        boolean is_plainnsid = false;
        
        if(queryString != null)
        {
            context.put("query_string", queryString);
            
            if(StringUtils.isPlainNamespaceAndIdentifier(queryString, localSettings))
            {
                is_plainnsid = true;
                
                List<String> namespaceAndIdentifier = StringUtils.getNamespaceAndIdentifier(queryString, localSettings);
                
                if(namespaceAndIdentifier.size() == 2)
                {
                    context.put("namespace", namespaceAndIdentifier.get(0));
                    context.put("identifier", namespaceAndIdentifier.get(1));
                }
                else
                {
                    log.warn("Namespace and identifier did not have exactly two components: namesapceAndIdentifier.size()="+namespaceAndIdentifier.size());
                }
            }
        }
        
        context.put("is_plainnsid", is_plainnsid);
        context.put("real_hostname", realHostName);
        context.put("context_path", contextPath);
        context.put("server_base", realHostName+contextPath);
        context.put("rdfxml_link", realHostName+contextPath+localSettings.getStringPropertyFromConfig("rdfXmlUrlPrefix", "")+queryString+localSettings.getStringPropertyFromConfig("rdfXmlUrlSuffix", ""));
        context.put("rdfn3_link", realHostName+contextPath+localSettings.getStringPropertyFromConfig("n3UrlPrefix", "")+queryString+localSettings.getStringPropertyFromConfig("n3UrlSuffix", ""));
        context.put("html_link", realHostName+contextPath+localSettings.getStringPropertyFromConfig("htmlUrlPrefix", "")+queryString+localSettings.getStringPropertyFromConfig("htmlUrlSuffix", ""));
        context.put("json_link", realHostName+contextPath+localSettings.getStringPropertyFromConfig("jsonUrlPrefix", "")+queryString+localSettings.getStringPropertyFromConfig("jsonUrlSuffix", ""));
        // context.put("disco_link", discoLink);
        // context.put("tabulator_link", tabulatorLink);
        // context.put("openlink_link", openLinkLink);
        Collection<String> endpointsList = new HashSet<String>();
        
        if(fetchController != null)
        {
            for(QueryBundle nextQueryBundle : fetchController.getQueryBundles())
            {
                if(!endpointsList.contains(nextQueryBundle.getQueryEndpoint()))
                {
                    endpointsList.add(nextQueryBundle.getQueryEndpoint());
                }
            }
        }
        
        context.put("provider_endpoints", endpointsList);
        
        if(fetchController != null)
            context.put("query_bundles", fetchController.getQueryBundles());
        
        //Collection<Value> titles = new HashSet<Value>();
        //Collection<Value> comments = new HashSet<Value>();
        //Collection<Value> images = new HashSet<Value>();
        Collection<Value> titles = RdfUtils.getValuesFromRepositoryByPredicateUris(nextRepository, localSettings.getURICollectionPropertiesFromConfig("titleProperties"));
        Collection<Value> comments = RdfUtils.getValuesFromRepositoryByPredicateUris(nextRepository, localSettings.getURICollectionPropertiesFromConfig("commentProperties"));
        Collection<Value> images = RdfUtils.getValuesFromRepositoryByPredicateUris(nextRepository, localSettings.getURICollectionPropertiesFromConfig("imageProperties"));
        
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
            context.put("title", localSettings.getStringPropertyFromConfig("blankTitle", ""));
        }
        else
        {
            context.put("title", chosenTitle);
        }
        
        context.put("titles", titles);
        context.put("comments", comments);
        context.put("images", images);

        context.put("shortcut_icon", localSettings.getStringPropertyFromConfig("shortcutIconPath","static/includes-images/favicon.ico"));
        context.put("scripts", localSettings.getStringCollectionPropertiesFromConfig("resultsPageScripts"));
        context.put("local_scripts", localSettings.getStringCollectionPropertiesFromConfig("resultsPageScriptsLocal"));
        context.put("stylesheets", localSettings.getStringCollectionPropertiesFromConfig("resultsPageStylesheets"));
        context.put("local_stylesheets", localSettings.getStringCollectionPropertiesFromConfig("resultsPageStylesheetsLocal"));
        
        // For each URI in localSettings.IMAGE_QUERY_TYPES
        // Make sure the URI is a valid QueryType
        // If there are any matches, replace the input_NN's with the namespace and identifier known here and then show a link to the image in HTML
        //Collection<Provider> providersForThisNamespace = localSettings.getProvidersForQueryTypeForNamespaceUris(String customService, Collection<Collection<String>> namespaceUris, NamespaceEntry.)
        
        List<Statement> allStatements = RdfUtils.getAllStatementsFromRepository(nextRepository);
        
        // TODO: go through the statements and check an internal label cache to see if there is an existing label available 
        // and if not schedule a thread to retrieve a label for the item so that for other uses it can be shown
        
        log.info("HtmlPageRenderer: allStatements.size()="+allStatements.size());
        
        // TODO: what should be shown if there are no RDF statements? A 404 error doesn't seem appropriate because the query might be quite legitimate and no triples is the valid response
        
        context.put("statements", allStatements);
        
        context.put("xmlutil", new info.aduna.xml.XMLUtil());
        context.put("bio2rdfutil", new org.queryall.helpers.RdfUtils());
        
        // our only way of guessing if other pages are available without doing an explicit count
        if(allStatements.size() >= localSettings.getIntPropertyFromConfig("pageoffsetIndividualQueryLimit", 0))
        {
            nextpagelinkuseful = true;
        }
        
        if(pageoffset > 1)
        {
            previouspagelinkuseful = true;
        }
        else if(pageoffset < 1)
        {
            // pageoffset less than one does not need a nextpagelinkuseful as it is useful as a marker to avoid this function
            pageoffset = 1;
            previouspageoffset = 1;
            
            previouspagelinkuseful = false;
            nextpagelinkuseful = false;
        }
        
        // To prevent infinite or extended requests, we have a maximum value that we can go up to
        if(pageoffset > localSettings.getIntPropertyFromConfig("pageoffsetMaxValue", 20))
        {
            // setup the pageoffset value so it artificially points to the limit so that non-conforming robots that don't follow robots.txt don't accidentally run into issues when people play around with links to very high page offsets
            previouspageoffset = localSettings.getIntPropertyFromConfig("pageoffsetMaxValue", 20);
            nextpagelinkuseful = false;
        }
        
        // If configured to only show pageoffset for plain nsid's as opposed to the other queries then decide here whether to show it
        if(localSettings.getBooleanPropertyFromConfig("pageoffsetOnlyShowForNsId", true) && !is_plainnsid)
        {
            nextpagelinkuseful = false;
        }
        
        if(nextpagelinkuseful)
        {
            context.put("nextpagelink", realHostName
                +contextPath
                +localSettings.getStringPropertyFromConfig("htmlUrlPrefix", "page/")
                +localSettings.getStringPropertyFromConfig("pageoffsetUrlOpeningPrefix", "pageoffset")
                +(pageoffset+1)
                +localSettings.getStringPropertyFromConfig("pageoffsetUrlClosingPrefix", "/")
                +queryString
                +localSettings.getStringPropertyFromConfig("pageoffsetUrlSuffix", "")
                +localSettings.getStringPropertyFromConfig("htmlUrlSuffix", ""));
            context.put("nextpagelabel", (pageoffset+1));
        }
        
        if(previouspagelinkuseful)
        {
            context.put("previouspagelink", realHostName
                +contextPath
                +localSettings.getStringPropertyFromConfig("htmlUrlPrefix", "page/")
                +localSettings.getStringPropertyFromConfig("pageoffsetUrlOpeningPrefix", "pageoffset")
                +(previouspageoffset)
                +localSettings.getStringPropertyFromConfig("pageoffsetUrlClosingPrefix", "/")
                +queryString
                +localSettings.getStringPropertyFromConfig("pageoffsetUrlSuffix", "")
                +localSettings.getStringPropertyFromConfig("htmlUrlSuffix", ""));
            context.put("previouspagelabel", previouspageoffset);
        }
        
        context.put("debugStrings", debugStrings);
        
        context.put("xmlEncoded_testString", "<test&amp;\"\'&>");
        
        //http://velocity.apache.org/engine/devel/webapps.html
        // Any user-entered text that contains special HTML or XML entities (such as <, >, or &) needs to be escaped before included in the web page. This is required, both to ensure the text is visible, and also to prevent dangerous cross-site scripting . Unlike, for example, JSTL (the Java Standard Tag Language found in Java Server Pages), Velocity does not escape references by default.
        //
        // However, Velocity provides the ability to specify a ReferenceInsertionEventHandler which will alter the value of a reference before it is inserted into the page. Specifically, you can configure the EscapeHtmlReference handler into your velocity.properties file to escape all references (optionally) matching a regular expression. The following example will escape HTML entities in any reference that starts with "msg" (e.g. $msgText).
        //
        // eventhandler.referenceinsertion.class = org.apache.velocity.app.event.implement.EscapeHtmlReference
        // eventhandler.escape.html.match = /msg.*/
        //
        // Note that other kinds of escaping are sometimes required. For example, in style sheets the @ character needs to be escaped, and in Javascript strings the single apostrophe ' needs to be escaped.
        
        if(_TRACE)
        {
            log.trace("renderHtml: about to render XHTML to nextWriter="+nextWriter);
        }
        
        try
        {
            if(fetchController == null || fetchController.queryKnown())
            {
                if(_DEBUG)
                {
                    log.debug("renderHtml: fetchController.queryKnown(), using page.vm template");
                }
                
                template.renderXHTML("page.vm", nextWriter);
            }
            else
            {
                if(_DEBUG)
                {
                    log.debug("renderHtml: !fetchController.queryKnown(), using error.vm template");
                }

                context.put("namespaceRecognised", !fetchController.anyNamespaceNotRecognised());
                context.put("queryKnown", fetchController.queryKnown());
                
                template.renderXHTML("error.vm", nextWriter);
            }
        }
        catch(Exception ex)
        {
            log.fatal("renderHtml: caught exception while rendering XHTML",ex);

            try
            {
            	nextWriter.write("Fatal error. See logs for details");
            }
            catch(IOException ioe)
            {
            	log.fatal("renderHtml: Could not write out error message to nextWriter");
            }
        }
        
        if(_TRACE)
        {
            log.trace("renderHtml: finished rendering XHTML");
        }
    }
	
	public static void renderIndexPage(Settings localSettings, ServletContext servletContext, java.io.Writer nextWriter, Collection<String> debugStrings, String realHostName, String contextPath) throws OpenRDFException
	{
        if(contextPath == null || contextPath.equals("/"))
        {
            contextPath = "";
        }
        else if(contextPath.startsWith("/") && contextPath.length() > 1 )
        {
            // take off the first slash and add one to the end for our purposes
            contextPath = contextPath.substring(1)+"/";
        }
        
        if(localSettings.getBooleanPropertyFromConfig("useHardcodedRequestContext", true))
        {
            contextPath = localSettings.getStringPropertyFromConfig("hardcodedRequestContext", "");
        }
        
        if(localSettings.getBooleanPropertyFromConfig("useHardcodedRequestHostname", true))
        {
            realHostName = localSettings.getStringPropertyFromConfig("hardcodedRequestHostname", "");
        }
        
        if(_TRACE)
        {
            log.trace("renderIndexPage: about to create VelocityHelper class");
        }

        VelocityHelper template = new VelocityHelper(servletContext);
		
        Context context = template.getVelocityContext();
        context.put("statistics_providers", Integer.toString(localSettings.getAllProviders().size()));
        context.put("statistics_namespaceentries", Integer.toString(localSettings.getAllNamespaceEntries().size()));
        context.put("statistics_normalisationrules", Integer.toString(localSettings.getAllNormalisationRules().size()));
        context.put("statistics_normalisationruletests", Integer.toString(localSettings.getAllRuleTests().size()));
        context.put("statistics_querytypes", Integer.toString(localSettings.getAllQueryTypes().size()));
        
        context.put("debug_level_info", GeneralServlet._INFO);
        context.put("debug_level_debug", GeneralServlet._DEBUG);
        context.put("debug_level_trace", GeneralServlet._TRACE);
        
        context.put("title", localSettings.getStringPropertyFromConfig("projectName", "Bio2RDF"));

        context.put("project_name", localSettings.getStringPropertyFromConfig("projectName", "Bio2RDF"));
        context.put("project_base_url", localSettings.getStringPropertyFromConfig("projectHomeUrl", "http://bio2rdf.org/"));
        context.put("project_html_url_prefix", localSettings.getStringPropertyFromConfig("htmlUrlPrefix", "html/"));
        context.put("project_html_url_suffix", localSettings.getStringPropertyFromConfig("htmlUrlSuffix", ""));
        context.put("project_link", localSettings.getStringPropertyFromConfig("projectHomeUrl", "http://bio2rdf.org/"));
        context.put("application_name", localSettings.getStringPropertyFromConfig("userAgent", "queryall")+ "/"+Settings.VERSION);
        context.put("application_help", localSettings.getStringPropertyFromConfig("applicationHelpUrl", "http://sourceforge.net/apps/mediawiki/bio2rdf/"));

        context.put("index_banner_image", localSettings.getStringPropertyFromConfig("indexBannerImagePath","static/includes-images/Bio2RDF.jpg"));
        
        context.put("shortcut_icon", localSettings.getStringPropertyFromConfig("shortcutIconPath","static/includes-images/favicon.ico"));
        context.put("scripts", localSettings.getStringCollectionPropertiesFromConfig("indexPageScripts"));
        context.put("local_scripts", localSettings.getStringCollectionPropertiesFromConfig("indexPageScriptsLocal"));
        context.put("stylesheets", localSettings.getStringCollectionPropertiesFromConfig("indexPageStylesheets"));
        context.put("local_stylesheets", localSettings.getStringCollectionPropertiesFromConfig("indexPageStylesheetsLocal"));
        
        context.put("real_hostname", realHostName);
        context.put("context_path", contextPath);
        context.put("server_base", realHostName+contextPath);

        String templateLocation = localSettings.getStringPropertyFromConfig("indexTemplate", "default-index.vm");

		try
		{
			template.renderXHTML(templateLocation, nextWriter);
		}
		catch(Exception ex)
		{
            log.fatal("renderIndexPage: caught exception while rendering XHTML",ex);

            try
            {
            	nextWriter.write("Fatal error. See logs for details");
            }
            catch(IOException ioe)
            {
            	log.fatal("renderIndexPage: Could not write out error message to nextWriter");
            }
        }
        
        if(_TRACE)
        {
            log.trace("renderIndexPage: finished rendering XHTML");
        }
	}
}
