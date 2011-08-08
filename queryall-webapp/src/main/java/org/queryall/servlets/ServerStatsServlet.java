package org.queryall.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.queryall.api.QueryAllConfiguration;
import org.queryall.blacklist.BlacklistController;
import org.queryall.enumerations.Constants;
import org.queryall.query.QueryDebug;
import org.queryall.query.Settings;
import org.queryall.utils.MathsUtils;

/** 
 * 
 */

public class ServerStatsServlet extends HttpServlet
{
    /**
	 * 
	 */
    private static final long serialVersionUID = -4816515030121762308L;
    public static final Logger log = Logger.getLogger(ServerStatsServlet.class.getName());
    public static final boolean _TRACE = ServerStatsServlet.log.isTraceEnabled();
    public static final boolean _DEBUG = ServerStatsServlet.log.isDebugEnabled();
    public static final boolean _INFO = ServerStatsServlet.log.isInfoEnabled();
    
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException
    {
        final QueryAllConfiguration localSettings = Settings.getSettings();
        final BlacklistController localBlacklistController = BlacklistController.getDefaultController();
        
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
        
        final long differenceMilliseconds = currentDate.getTime() - localBlacklistController.lastExpiryDate.getTime();
        
        out.write("Current date : " + currentDate.toString() + "<br />\n");
        out.write("Server Version : " + Settings.VERSION + "<br />\n");
        out.write("Now : " + now + "<br />\n");
        out.write("Last error reset date: " + localBlacklistController.lastExpiryDate.toString() + "<br />\n");
        out.write("Server startup date: " + localBlacklistController.lastServerStartupDate.toString() + "<br />\n");
        out.write("Reset period " + localSettings.getLongProperty("blacklistResetPeriodMilliseconds", 0L) + "<br />\n");
        out.write("Client blacklist will reset in "
                + ((localSettings.getLongProperty("blacklistResetPeriodMilliseconds", 0L) - differenceMilliseconds) / 1000)
                + " seconds.<br /><br />\n");
        
        if(localBlacklistController.allHttpErrorResponseCodesByServer != null)
        {
            if(localBlacklistController.allHttpErrorResponseCodesByServer.size() > 0)
            {
                out.write("All HTTP response error codes by endpoint since last server restart:<br /><br />\n");
                
                for(final String nextKey : localBlacklistController.allHttpErrorResponseCodesByServer.keySet())
                {
                    out.write("Endpoint=" + nextKey + "<br />\n");
                    
                    final Hashtable<Integer, Integer> errorCodeList =
                            localBlacklistController.allHttpErrorResponseCodesByServer.get(nextKey);
                    
                    out.write("<ul>\n");
                    
                    for(final int nextErrorCode : errorCodeList.keySet())
                    {
                        out.write("<li>" + nextErrorCode + " : " + errorCodeList.get(nextErrorCode) + "</li>\n");
                    }
                    
                    out.write("</ul>\n");
                }
            }
        }
        
        if(localBlacklistController.allServerQueryTotals != null)
        {
            if(localBlacklistController.allServerQueryTotals.size() > 0)
            {
                out.write("Total queries by endpoint since last server restart:<br />\n");
                
                out.write("<ul>\n");
                
                for(final String nextKey : localBlacklistController.allServerQueryTotals.keySet())
                {
                    out.write("<li>Endpoint=" + nextKey + " : "
                            + localBlacklistController.allServerQueryTotals.get(nextKey) + "</li>\n");
                }
                
                out.write("</ul>\n");
            }
        }
        
        for(final String nextKey : localBlacklistController.accumulatedBlacklistStatistics.keySet())
        {
            out.write(localBlacklistController.accumulatedBlacklistStatistics.get(nextKey).toString() + "<br />\n");
            out.write(localBlacklistController.accumulatedBlacklistStatistics.get(nextKey)
                    .errorMessageSummaryToString() + "<br />\n");
        }
        
        final Collection<Long> overallQueryTimes = new HashSet<Long>();
        final Collection<Long> userSpecificQueryTimes = new HashSet<Long>();
        long overallQueryTime = 0;
        int overallQueryNumbers = 0;
        
        for(final String nextKey : localBlacklistController.currentQueryDebugInformation.keySet())
        {
            out.write("<br />Queries by : " + nextKey + "<br />\n");
            
            final Collection<QueryDebug> nextSetOfQueries =
                    localBlacklistController.currentQueryDebugInformation.get(nextKey);
            
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
