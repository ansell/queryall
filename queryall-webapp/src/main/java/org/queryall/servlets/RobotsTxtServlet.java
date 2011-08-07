package org.queryall.servlets;

import java.util.Collection;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.queryall.api.QueryAllConfiguration;
import org.queryall.helpers.Settings;

import org.apache.log4j.Logger;

/** 
 * @author Peter Ansell p_ansell@yahoo.com
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
        
    	if(_INFO)
    		log.info("Robots.txt requested: request.getRequestURI()="+request.getRequestURI());
        
        response.setContentType("text/plain");
        
        PrintWriter out = response.getWriter();
    
        Collection<String> robotsList = ((QueryAllConfiguration)Settings.getSettings()).getStringProperties("blacklistBaseUserAgents");
        
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

        // TODO: automate this using configuration
        
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

        out.write("Disallow: /json/label/\n");
        out.write("Disallow: /json/links/\n");
        out.write("Disallow: /json/linkstonamespace/\n");
        out.write("Disallow: /json/proteinlinks/\n");
        out.write("Disallow: /json/search/\n");
        out.write("Disallow: /json/searchns/\n");
        out.write("Disallow: /json/related/\n");
        out.write("Disallow: /json/suppliers/\n");
        out.write("Disallow: /json/index/\n");
        out.write("Disallow: /json/html/\n");
        out.write("Disallow: /json/image/\n");
        out.write("Disallow: /json/license/\n");
        out.write("Disallow: /json/ihop/\n");
        out.write("Disallow: /json/xml/\n");
        out.write("Disallow: /json/data/\n");
        out.write("Disallow: /json/countlinks/\n");
        out.write("Disallow: /json/countlinksns/\n\n");

        out.write("Disallow: /ntriples/label/\n");
        out.write("Disallow: /ntriples/links/\n");
        out.write("Disallow: /ntriples/linkstonamespace/\n");
        out.write("Disallow: /ntriples/proteinlinks/\n");
        out.write("Disallow: /ntriples/search/\n");
        out.write("Disallow: /ntriples/searchns/\n");
        out.write("Disallow: /ntriples/related/\n");
        out.write("Disallow: /ntriples/suppliers/\n");
        out.write("Disallow: /ntriples/index/\n");
        out.write("Disallow: /ntriples/html/\n");
        out.write("Disallow: /ntriples/image/\n");
        out.write("Disallow: /ntriples/license/\n");
        out.write("Disallow: /ntriples/ihop/\n");
        out.write("Disallow: /ntriples/xml/\n");
        out.write("Disallow: /ntriples/data/\n");
        out.write("Disallow: /ntriples/countlinks/\n");
        out.write("Disallow: /ntriples/countlinksns/\n\n");

        out.write("Disallow: /nquads/label/\n");
        out.write("Disallow: /nquads/links/\n");
        out.write("Disallow: /nquads/linkstonamespace/\n");
        out.write("Disallow: /nquads/proteinlinks/\n");
        out.write("Disallow: /nquads/search/\n");
        out.write("Disallow: /nquads/searchns/\n");
        out.write("Disallow: /nquads/related/\n");
        out.write("Disallow: /nquads/suppliers/\n");
        out.write("Disallow: /nquads/index/\n");
        out.write("Disallow: /nquads/html/\n");
        out.write("Disallow: /nquads/image/\n");
        out.write("Disallow: /nquads/license/\n");
        out.write("Disallow: /nquads/ihop/\n");
        out.write("Disallow: /nquads/xml/\n");
        out.write("Disallow: /nquads/data/\n");
        out.write("Disallow: /nquads/countlinks/\n");
        out.write("Disallow: /nquads/countlinksns/\n\n");

        out.flush();
    }
  
}

