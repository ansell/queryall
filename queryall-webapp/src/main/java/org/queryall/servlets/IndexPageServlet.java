package org.queryall.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openrdf.OpenRDFException;
import org.queryall.api.QueryAllConfiguration;
import org.queryall.query.Settings;
import org.queryall.servlets.html.HtmlPageRenderer;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class IndexPageServlet extends HttpServlet
{
    private static final long serialVersionUID = -6472769738354082954L;
    public static final Logger log = LoggerFactory.getLogger(IndexPageServlet.class.getName());
    public static final boolean _TRACE = IndexPageServlet.log.isTraceEnabled();
    public static final boolean _DEBUG = IndexPageServlet.log.isDebugEnabled();
    public static final boolean _INFO = IndexPageServlet.log.isInfoEnabled();
    
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException
    {
        final QueryAllConfiguration localSettings = Settings.getSettings();
        
        final PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        
        final String realHostName =
                request.getScheme() + "://" + request.getServerName()
                        + (request.getServerPort() == 80 ? "" : ":" + request.getServerPort()) + "/";
        
        try
        {
            HtmlPageRenderer.renderIndexPage(localSettings, this.getServletContext(), out, new LinkedList<String>(),
                    realHostName, request.getContextPath());
        }
        catch(final OpenRDFException ordfe)
        {
            IndexPageServlet.log.error("OpenRDFException:", ordfe);
        }
    }
    
}
