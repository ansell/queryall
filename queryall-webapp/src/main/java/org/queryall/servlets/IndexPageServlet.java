package org.queryall.servlets;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.Logger;

import org.queryall.query.Settings;
import org.queryall.servlets.html.HtmlPageRenderer;

import org.openrdf.OpenRDFException;

import org.queryall.api.QueryAllConfiguration;

/** 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class IndexPageServlet extends HttpServlet 
{
	private static final long serialVersionUID = -6472769738354082954L;
	public static final Logger log = Logger.getLogger(IndexPageServlet.class.getName());
    public static final boolean _TRACE = log.isTraceEnabled();
    public static final boolean _DEBUG = log.isDebugEnabled();
    public static final boolean _INFO = log.isInfoEnabled();

    
    @Override
    public void doGet(HttpServletRequest request,
                        HttpServletResponse response)
        throws ServletException, IOException 
    {
    	QueryAllConfiguration localSettings = Settings.getSettings();
        
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        
        String realHostName = request.getScheme() + "://" + request.getServerName() + (request.getServerPort() == 80 ? "" : ":"+ request.getServerPort())+"/";
        
        try
        {
        	HtmlPageRenderer.renderIndexPage(localSettings, getServletContext(), out, new LinkedList<String>(), realHostName, request.getContextPath());
        }
        catch(OpenRDFException ordfe)
        {
        	log.fatal("OpenRDFException:", ordfe);
        }
    }
    
}

