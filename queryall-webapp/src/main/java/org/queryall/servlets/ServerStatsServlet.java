package org.queryall.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.utils.Constants;
import org.queryall.blacklist.BlacklistController;
import org.queryall.query.QueryDebug;
import org.queryall.servlets.helpers.SettingsContextListener;
import org.queryall.utils.MathsUtils;
import org.queryall.utils.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * 
 */

public class ServerStatsServlet extends HttpServlet
{
    /**
	 * 
	 */
    private static final long serialVersionUID = -4816515030121762308L;
    public static final Logger log = LoggerFactory.getLogger(ServerStatsServlet.class);
    public static final boolean _TRACE = ServerStatsServlet.log.isTraceEnabled();
    public static final boolean _DEBUG = ServerStatsServlet.log.isDebugEnabled();
    public static final boolean _INFO = ServerStatsServlet.log.isInfoEnabled();
    
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException
    {
        final QueryAllConfiguration localSettings =
                (QueryAllConfiguration)this.getServletContext().getAttribute(SettingsContextListener.QUERYALL_CONFIG);
        final BlacklistController localBlacklistController =
                (BlacklistController)this.getServletContext().getAttribute(SettingsContextListener.QUERYALL_BLACKLIST);
        
        final PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        
        @SuppressWarnings("unused")
        final String subversionId = "$Id: errorstats.jsp 910 2010-12-03 22:07:48Z p_ansell $";
        
        localBlacklistController.doBlacklistExpiry();
        localBlacklistController.clearStatisticsUploadList();
        
        final Date currentDate = new Date();
        
        // SimpleDateFormat ISO8601UTC =
        // new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss,SSS'Z'");
        // SimpleDateFormat ISO8601UTC =
        // new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        // ISO8601UTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String now = Constants.ISO8601UTC().format(currentDate);
        
        final long differenceMilliseconds =
                currentDate.getTime() - localBlacklistController.getLastExpiryDate().getTime();
        
        out.write("Current date : " + currentDate.toString() + "<br />\n");
        out.write("Server Version : " + Settings.VERSION + "<br />\n");
        out.write("Now : " + now + "<br />\n");
        out.write("Last error reset date: " + localBlacklistController.getLastExpiryDate().toString() + "<br />\n");
        out.write("Server startup date: " + localBlacklistController.getLastServerStartupDate().toString() + "<br />\n");
        out.write("Reset period " + localSettings.getLongProperty("blacklistResetPeriodMilliseconds", 0L) + "<br />\n");
        out.write("Client blacklist will reset in "
                + ((localSettings.getLongProperty("blacklistResetPeriodMilliseconds", 0L) - differenceMilliseconds) / 1000)
                + " seconds.<br /><br />\n");
        
        if(localBlacklistController.getAllHttpErrorResponseCodesByServer() != null)
        {
            if(localBlacklistController.getAllHttpErrorResponseCodesByServer().size() > 0)
            {
                out.write("All HTTP response error codes by endpoint since last server restart:<br /><br />\n");
                
                for(final String nextKey : localBlacklistController.getAllHttpErrorResponseCodesByServer().keySet())
                {
                    out.write("Endpoint=" + nextKey + "<br />\n");
                    
                    final Map<Integer, Integer> errorCodeList =
                            localBlacklistController.getAllHttpErrorResponseCodesByServer().get(nextKey);
                    
                    out.write("<ul>\n");
                    
                    for(final int nextErrorCode : errorCodeList.keySet())
                    {
                        out.write("<li>" + nextErrorCode + " : " + errorCodeList.get(nextErrorCode) + "</li>\n");
                    }
                    
                    out.write("</ul>\n");
                }
            }
        }
        
        if(localBlacklistController.getAllServerQueryTotals() != null)
        {
            if(localBlacklistController.getAllServerQueryTotals().size() > 0)
            {
                out.write("Total queries by endpoint since last server restart:<br />\n");
                
                out.write("<ul>\n");
                
                for(final String nextKey : localBlacklistController.getAllServerQueryTotals().keySet())
                {
                    out.write("<li>Endpoint=" + nextKey + " : "
                            + localBlacklistController.getAllServerQueryTotals().get(nextKey) + "</li>\n");
                }
                
                out.write("</ul>\n");
            }
        }
        
        for(final String nextKey : localBlacklistController.getAccumulatedBlacklistStatistics().keySet())
        {
            out.write(localBlacklistController.getAccumulatedBlacklistStatistics().get(nextKey).toString() + "<br />\n");
            out.write(localBlacklistController.getAccumulatedBlacklistStatistics().get(nextKey)
                    .errorMessageSummaryToString()
                    + "<br />\n");
        }
        
        final Collection<Long> overallQueryTimes = new HashSet<Long>();
        final Collection<Long> userSpecificQueryTimes = new HashSet<Long>();
        long overallQueryTime = 0;
        int overallQueryNumbers = 0;
        
        for(final String nextKey : localBlacklistController.getCurrentQueryDebugInformation().keySet())
        {
            out.write("<br />Queries by : " + nextKey + "<br />\n");
            
            final Collection<QueryDebug> nextSetOfQueries =
                    localBlacklistController.getCurrentQueryDebugInformation().get(nextKey);
            
            final Collection<Long> nextQueryTimes = new HashSet<Long>();
            long nextTotalQueryTime = 0;
            final int nextTotalQueryNumbers = nextSetOfQueries.size();
            
            for(final QueryDebug nextQueryDebug : nextSetOfQueries)
            {
                nextTotalQueryTime += nextQueryDebug.getTotalTimeMilliseconds();
                
                overallQueryTimes.add(nextQueryDebug.getTotalTimeMilliseconds());
                nextQueryTimes.add(nextQueryDebug.getTotalTimeMilliseconds());
                
                if(ServerStatsServlet._DEBUG)
                {
                    out.write(nextQueryDebug.toString() + "<br />\n");
                }
            }
            
            out.write("Total number of queries = " + nextTotalQueryNumbers + "<br />\n");
            out.write("Total query time = " + nextTotalQueryTime + "<br />\n");
            out.write("Average query length = " + nextTotalQueryTime / nextTotalQueryNumbers + "<br />\n");
            out.write("Standard deviation = " + MathsUtils.getStandardDeviationFromLongs(nextQueryTimes) + " <br />\n");
            
            userSpecificQueryTimes.add(nextTotalQueryTime);
            overallQueryTime += nextTotalQueryTime;
            overallQueryNumbers += nextTotalQueryNumbers;
        }
        
        if(overallQueryNumbers > 0)
        {
            out.write("<br />Overall Total number of queries = " + overallQueryNumbers + "<br />\n");
            out.write("Overall Total query time = " + overallQueryTime + "<br />\n");
            out.write("Overall Average query length = " + overallQueryTime / overallQueryNumbers + "<br />\n");
            out.write("Overall Standard deviation = " + MathsUtils.getStandardDeviationFromLongs(overallQueryTimes)
                    + " <br />\n");
            out.write("Overall requestor level Standard deviation = "
                    + MathsUtils.getStandardDeviationFromLongs(userSpecificQueryTimes) + " <br />\n");
        }
        
    }
    
}
