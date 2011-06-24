package org.bio2rdf.servlets;

import java.util.Collection;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.queryall.helpers.Settings;

import org.apache.log4j.Logger;

/** 
 * 
 */

public class RobotsTxtServlet extends HttpServlet 
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 3224404845116318101L;
	public static final Logger log = Logger.getLogger(RobotsTxtServlet.class.getName());
    public static final boolean _TRACE = log.isTraceEnabled();
    public static final boolean _DEBUG = log.isDebugEnabled();
    public static final boolean _INFO = log.isInfoEnabled();

    
    @Override
    public void doGet(HttpServletRequest request,
                        HttpServletResponse response)
        throws ServletException, IOException 
    {
        // Settings.setServletContext(getServletConfig().getServletContext());
        
        log.debug("request.getRequestURI()="+request.getRequestURI());
        
        response.setContentType("text/plain");
        
        PrintWriter out = response.getWriter();
    
        Collection<String> robotsList = Settings.getSettings().getStringCollectionPropertiesFromConfig("blacklistBaseUserAgents");
        
        if(robotsList != null)
        {
            for(String nextRobot : robotsList)
            {
                out.write("User-agent: "+nextRobot+"\n");
            }
            
            out.write("Disallow: /\n");
        }
                
        out.write("\nUser-agent: *\n");
        
        out.write("Disallow: /queryplan/\n");
        out.write("Disallow: /admin/\n");
        out.write("Disallow: /error/\n\n");

        out.write("Disallow: /label/\n");
        out.write("Disallow: /links/\n");
        out.write("Disallow: /linkstonamespace/\n");
        out.write("Disallow: /proteinlinks/\n");
        out.write("Disallow: /search/\n");
        out.write("Disallow: /searchns/\n");
        out.write("Disallow: /related/\n");
        out.write("Disallow: /suppliers/\n");
        out.write("Disallow: /index/\n");
        out.write("Disallow: /html/\n");
        out.write("Disallow: /image/\n");
        out.write("Disallow: /license/\n");
        out.write("Disallow: /ihop/\n");
        out.write("Disallow: /xml/\n");
        out.write("Disallow: /data/\n");
        out.write("Disallow: /countlinks/\n");
        out.write("Disallow: /countlinksns/\n\n");

        out.write("Disallow: /page/label/\n");
        out.write("Disallow: /page/links/\n");
        out.write("Disallow: /page/linkstonamespace/\n");
        out.write("Disallow: /page/proteinlinks/\n");
        out.write("Disallow: /page/search/\n");
        out.write("Disallow: /page/searchns/\n");
        out.write("Disallow: /page/related/\n");
        out.write("Disallow: /page/suppliers/\n");
        out.write("Disallow: /page/index/\n");
        out.write("Disallow: /page/html/\n");
        out.write("Disallow: /page/image/\n");
        out.write("Disallow: /page/license/\n");
        out.write("Disallow: /page/ihop/\n");
        out.write("Disallow: /page/xml/\n");
        out.write("Disallow: /page/data/\n");
        out.write("Disallow: /page/countlinks/\n");
        out.write("Disallow: /page/countlinksns/\n\n");

        out.write("Disallow: /rdfxml/label/\n");
        out.write("Disallow: /rdfxml/links/\n");
        out.write("Disallow: /rdfxml/linkstonamespace/\n");
        out.write("Disallow: /rdfxml/proteinlinks/\n");
        out.write("Disallow: /rdfxml/search/\n");
        out.write("Disallow: /rdfxml/searchns/\n");
        out.write("Disallow: /rdfxml/related/\n");
        out.write("Disallow: /rdfxml/suppliers/\n");
        out.write("Disallow: /rdfxml/index/\n");
        out.write("Disallow: /rdfxml/html/\n");
        out.write("Disallow: /rdfxml/image/\n");
        out.write("Disallow: /rdfxml/license/\n");
        out.write("Disallow: /rdfxml/ihop/\n");
        out.write("Disallow: /rdfxml/xml/\n");
        out.write("Disallow: /rdfxml/data/\n");
        out.write("Disallow: /rdfxml/countlinks/\n");
        out.write("Disallow: /rdfxml/countlinksns/\n\n");

        out.write("Disallow: /n3/label/\n");
        out.write("Disallow: /n3/links/\n");
        out.write("Disallow: /n3/linkstonamespace/\n");
        out.write("Disallow: /n3/proteinlinks/\n");
        out.write("Disallow: /n3/search/\n");
        out.write("Disallow: /n3/searchns/\n");
        out.write("Disallow: /n3/related/\n");
        out.write("Disallow: /n3/suppliers/\n");
        out.write("Disallow: /n3/index/\n");
        out.write("Disallow: /n3/html/\n");
        out.write("Disallow: /n3/image/\n");
        out.write("Disallow: /n3/license/\n");
        out.write("Disallow: /n3/ihop/\n");
        out.write("Disallow: /n3/xml/\n");
        out.write("Disallow: /n3/data/\n");
        out.write("Disallow: /n3/countlinks/\n");
        out.write("Disallow: /n3/countlinksns/\n\n");

        out.flush();
    }
  
}

