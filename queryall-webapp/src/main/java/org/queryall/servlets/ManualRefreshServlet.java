package org.queryall.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ManualRefreshServlet extends HttpServlet
{
    /**
	 * 
	 */
    private static final long serialVersionUID = -8130327002164154880L;
    public static final Logger log = LoggerFactory.getLogger(ManualRefreshServlet.class);
    public static final boolean TRACE = ManualRefreshServlet.log.isTraceEnabled();
    public static final boolean DEBUG = ManualRefreshServlet.log.isDebugEnabled();
    public static final boolean INFO = ManualRefreshServlet.log.isInfoEnabled();
    
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException
    {
        // final QueryAllConfiguration localSettings =
        // (QueryAllConfiguration)this.getServletContext().getAttribute(SettingsContextListener.QUERYALL_CONFIG);
        // final BlacklistController localBlacklistController =
        // (BlacklistController)this.getServletContext().getAttribute(SettingsContextListener.QUERYALL_BLACKLIST);
        
        final PrintWriter out = response.getWriter();
        
        // final boolean refreshAllowed = ((Settings)localSettings).isManualRefreshAllowed();
        final boolean refreshAllowed = false;
        
        if(refreshAllowed)
        {
            // if(((Settings)localSettings).configRefreshCheck(true))
            // {
            // localBlacklistController.doBlacklistExpiry();
            //
            // response.setStatus(HttpServletResponse.SC_OK);
            // ManualRefreshServlet.log.info("manualrefresh.jsp: manual refresh succeeded requesterIpAddress="
            // + request.getRemoteAddr());
            // out.write("Refresh succeeded.");
            // }
            // else
            // {
            // response.setStatus(500);
            // ManualRefreshServlet.log
            // .error("manualrefresh.jsp: refresh failed for an unknown reason, as it was supposedly allowed in a previous check requesterIpAddress="
            // + request.getRemoteAddr());
            // out.write("Refresh failed for an unknown reason");
            // }
        }
        else
        {
            response.setStatus(401);
            ManualRefreshServlet.log.error("manualrefresh.jsp: refresh not allowed right now requesterIpAddress="
                    + request.getRemoteAddr());
            out.write("Refresh not allowed right now.");
        }
    }
}
