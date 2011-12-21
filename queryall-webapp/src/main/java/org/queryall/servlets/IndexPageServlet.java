package org.queryall.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.app.VelocityEngine;
import org.openrdf.OpenRDFException;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.servlets.helpers.SettingsContextListener;
import org.queryall.servlets.html.HtmlPageRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class IndexPageServlet extends HttpServlet
{
    private static final long serialVersionUID = -6472769738354082954L;
    public static final Logger log = LoggerFactory.getLogger(IndexPageServlet.class);
    public static final boolean _TRACE = IndexPageServlet.log.isTraceEnabled();
    public static final boolean _DEBUG = IndexPageServlet.log.isDebugEnabled();
    public static final boolean _INFO = IndexPageServlet.log.isInfoEnabled();
    
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException
    {
        final QueryAllConfiguration localSettings =
                (QueryAllConfiguration)this.getServletContext().getAttribute(SettingsContextListener.QUERYALL_CONFIG);
        final VelocityEngine localVelocity =
                (VelocityEngine)this.getServletContext().getAttribute(SettingsContextListener.QUERYALL_VELOCITY);
        
        final PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        
        final String realHostName =
                request.getScheme() + "://" + request.getServerName()
                        + (request.getServerPort() == 80 ? "" : ":" + request.getServerPort()) + "/";
        
        try
        {
            HtmlPageRenderer.renderIndexPage(localSettings, localVelocity, out, new ArrayList<String>(0), realHostName,
                    request.getContextPath());
        }
        catch(final OpenRDFException ordfe)
        {
            IndexPageServlet.log.error("OpenRDFException:", ordfe);
        }
    }
    
}
