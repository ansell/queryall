package org.queryall.servlets;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.app.VelocityEngine;
import org.openrdf.OpenRDFException;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.servlets.helpers.SettingsContextListener;
import org.queryall.servlets.html.HtmlPageRenderer;
import org.queryall.servlets.queryparsers.DefaultQueryOptions;
import org.queryall.servlets.utils.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class IndexPageServlet extends HttpServlet
{
    private static final long serialVersionUID = -6472769738354082954L;
    public static final Logger log = LoggerFactory.getLogger(IndexPageServlet.class);
    public static final boolean TRACE = IndexPageServlet.log.isTraceEnabled();
    public static final boolean DEBUG = IndexPageServlet.log.isDebugEnabled();
    public static final boolean INFO = IndexPageServlet.log.isInfoEnabled();
    
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException
    {
        final QueryAllConfiguration localSettings =
                (QueryAllConfiguration)this.getServletContext().getAttribute(SettingsContextListener.QUERYALL_CONFIG);
        final VelocityEngine localVelocityEngine =
                (VelocityEngine)this.getServletContext().getAttribute(SettingsContextListener.QUERYALL_VELOCITY);
        
        final DefaultQueryOptions requestQueryOptions =
                new DefaultQueryOptions(request.getRequestURI(), request.getContextPath(), localSettings);
        
        final String serverName = request.getServerName();
        final String realHostName =
                request.getScheme()
                        + "://"
                        + serverName
                        + (request.getServerPort() == 80 && request.getScheme().equals("http") ? "" : ":"
                                + request.getServerPort()) + "/";
        final String queryString = requestQueryOptions.getParsedRequest();
        final String contextPath = request.getContextPath();
        // default to 200 for response...
        final int responseCode = HttpServletResponse.SC_OK;
        final int pageOffset = requestQueryOptions.getPageOffset();
        
        try
        {
            ServletUtils.sendBasicHeaders(response, responseCode, "text/html");
            
            // We do not use the default catalina writer as it may not be UTF-8 compliant
            // depending on unchangeable environment variables, instead we wrap up the catalina
            // binary output stream as a guaranteed UTF-8 Writer
            final Writer out = new OutputStreamWriter(response.getOutputStream(), Charset.forName("UTF-8"));
            
            HtmlPageRenderer.renderAjaxHtml(localVelocityEngine, localSettings, out, queryString,
                    localSettings.getDefaultHostAddress() + queryString, realHostName, contextPath, pageOffset,
                    Arrays.asList(""));
            
        }
        catch(final OpenRDFException ordfe)
        {
            IndexPageServlet.log.error("OpenRDFException:", ordfe);
        }
    }
    
}
