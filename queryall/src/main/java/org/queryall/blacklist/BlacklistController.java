
package org.queryall.blacklist;

import java.util.HashSet;
import java.util.Date;
import java.util.Map;
import java.util.Hashtable;
import java.util.Collection;
import java.util.List;
import java.net.URL;

import org.apache.log4j.Logger;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;

import org.queryall.queryutils.RdfFetcherQueryRunnable;
import org.queryall.queryutils.HttpUrlQueryRunnable;
import org.queryall.queryutils.QueryDebug;
import org.queryall.statistics.StatisticsEntry;
import org.queryall.helpers.Settings;
import org.queryall.helpers.Utilities;
import org.queryall.QueryType;

public class BlacklistController
{
    private static final Logger log = Logger
            .getLogger(BlacklistController.class.getName());
    private static final boolean _TRACE = BlacklistController.log
            .isTraceEnabled();
    private static final boolean _DEBUG = BlacklistController.log
            .isDebugEnabled();
    private static final boolean _INFO = BlacklistController.log
            .isInfoEnabled();
    
    public static Map<String, BlacklistEntry> accumulatedBlacklistStatistics = new Hashtable<String, BlacklistEntry>();
    
    public static Map<String, Hashtable<Integer, Integer>> allHttpErrorResponseCodesByServer = new Hashtable<String, Hashtable<Integer, Integer>>();
    
    public static Map<String, Integer> allServerQueryTotals = new Hashtable<String, Integer>();
    
    public static Collection<RdfFetcherQueryRunnable> allCurrentBadQueries = new HashSet<RdfFetcherQueryRunnable>();
    
    public static Map<String, Collection<QueryDebug>> currentQueryDebugInformation = new Hashtable<String, Collection<QueryDebug>>();
    
    public static Collection<String> currentIPBlacklist = null;
    
    public static Collection<String> permanentServletLifetimeIPBlacklist = new HashSet<String>();
    
    public static Map<String, Collection<QueryDebug>> permanentServletLifetimeIPBlacklistEvidence = new Hashtable<String, Collection<QueryDebug>>();
    
    public static Collection<String> currentIPWhitelist = null;
    
    public static Collection<HttpUrlQueryRunnable> internalStatisticsUploadList = new HashSet<HttpUrlQueryRunnable>();
    
    public static Date lastServerStartupDate = new Date();
    
    public static Date lastExpiryDate = new Date();
    
    public static void accumulateBlacklist(
            Collection<RdfFetcherQueryRunnable> temporaryEndpointBlacklist)
    {
        synchronized(BlacklistController.accumulatedBlacklistStatistics)
        {
            BlacklistController.doBlacklistExpiry();
            
            for(final RdfFetcherQueryRunnable nextQueryObject : temporaryEndpointBlacklist)
            {
                if(BlacklistController._DEBUG)
                {
                    BlacklistController.log
                            .debug("BlacklistController.accumulateBlacklist: going to accumulate entry for endpointUrl="
                                    + nextQueryObject.endpointUrl);
                }
                
                if(BlacklistController.accumulatedBlacklistStatistics
                        .containsKey(nextQueryObject.endpointUrl))
                {
                    final BlacklistEntry previousCount = BlacklistController.accumulatedBlacklistStatistics
                            .get(nextQueryObject.endpointUrl);
                    
                    if(BlacklistController._DEBUG)
                    {
                        BlacklistController.log
                                .debug("BlacklistController.accumulateBlacklist: There were "
                                        + previousCount
                                        + " previous instances on blacklist for endpointUrl="
                                        + nextQueryObject.endpointUrl);
                    }
                    
                    previousCount.numberOfFailures++;
                    previousCount.errorRunnables.add(nextQueryObject);
                    
                    BlacklistController.accumulatedBlacklistStatistics.put(
                            nextQueryObject.endpointUrl, previousCount);
                }
                else
                {
                    final BlacklistEntry newFailureCount = new BlacklistEntry();
                    newFailureCount.endpointUrl = nextQueryObject.endpointUrl;
                    newFailureCount.numberOfFailures = 1;
                    newFailureCount.errorRunnables = new HashSet<RdfFetcherQueryRunnable>();
                    newFailureCount.errorRunnables.add(nextQueryObject);
                    
                    BlacklistController.accumulatedBlacklistStatistics.put(
                            nextQueryObject.endpointUrl, newFailureCount);
                }
                
                BlacklistController.allCurrentBadQueries.add(nextQueryObject);
            }
        }
    }
    
    public static synchronized void accumulateHttpResponseError(
            String endpointUrl, int errorResponseCode)
    {
        if(BlacklistController.allHttpErrorResponseCodesByServer == null)
        {
            if(BlacklistController._TRACE)
            {
                BlacklistController.log
                        .trace("BlacklistController.accumulateHttpResponseError: allHttpErrorResponseCodesByServer was null");
            }
            
            BlacklistController.allHttpErrorResponseCodesByServer = new Hashtable<String, Hashtable<Integer, Integer>>();
        }
        
        Hashtable<Integer, Integer> nextErrorList = null;
        
        if(BlacklistController._TRACE)
        {
            
            for(final String nextCurrentEndpoint : BlacklistController.allHttpErrorResponseCodesByServer
                    .keySet())
            {
                BlacklistController.log.trace("nextCurrentEndpoint="
                        + nextCurrentEndpoint);
            }
        }
        
        if(BlacklistController.allHttpErrorResponseCodesByServer
                .containsKey(endpointUrl))
        {
            if(BlacklistController._TRACE)
            {
                BlacklistController.log
                        .trace("BlacklistController.accumulateHttpResponseError: allHttpErrorResponseCodesByServer already contains endpointUrl="
                                + endpointUrl);
            }
            
            nextErrorList = BlacklistController.allHttpErrorResponseCodesByServer
                    .get(endpointUrl);
            
            if(nextErrorList.containsKey(errorResponseCode))
            {
                if(BlacklistController._TRACE)
                {
                    BlacklistController.log
                            .trace("BlacklistController.accumulateHttpResponseError: allHttpErrorResponseCodesByServer nextErrorList already contains errorResponseCode="
                                    + errorResponseCode
                                    + " endpointUrl="
                                    + endpointUrl);
                }
                
                final int newCount = nextErrorList.get(errorResponseCode) + 1;
                
                if(BlacklistController._TRACE)
                {
                    BlacklistController.log
                            .trace("BlacklistController.accumulateHttpResponseError: allHttpErrorResponseCodesByServer nextErrorList already contains newCount="
                                    + newCount
                                    + " errorResponseCode="
                                    + errorResponseCode
                                    + " endpointUrl="
                                    + endpointUrl);
                }
                
                nextErrorList.put(errorResponseCode, newCount);
            }
            else
            {
                nextErrorList.put(errorResponseCode, 1);
            }
        }
        else
        {
            if(BlacklistController._TRACE)
            {
                BlacklistController.log
                        .trace("BlacklistController.accumulateHttpResponseError: new endpoint for list errorResponseCode="
                                + errorResponseCode
                                + " endpointUrl="
                                + endpointUrl);
            }
            
            nextErrorList = new Hashtable<Integer, Integer>();
            nextErrorList.put(errorResponseCode, 1);
        }
        
        BlacklistController.allHttpErrorResponseCodesByServer.put(endpointUrl,
                nextErrorList);
    }
    
    public static synchronized void accumulateQueryDebug(
            QueryDebug nextQueryObject)
    {
        if(Settings.getSettings().getBooleanPropertyFromConfig("automaticallyBlacklistClients"))
        {
            if(BlacklistController._DEBUG)
            {
                BlacklistController.log
                        .debug("BlacklistController.accumulateQueryDebug: going to accumulate entry for clientIPAddress="
                                + nextQueryObject.clientIPAddress);
            }
            
            BlacklistController.doBlacklistExpiry();
            
            if(BlacklistController.currentQueryDebugInformation
                    .containsKey(nextQueryObject.clientIPAddress))
            {
                final Collection<QueryDebug> previousQueries = BlacklistController.currentQueryDebugInformation
                        .get(nextQueryObject.clientIPAddress);
                
                previousQueries.add(nextQueryObject);
                
                BlacklistController.currentQueryDebugInformation.put(
                        nextQueryObject.clientIPAddress, previousQueries);
            }
            else
            {
                final Collection<QueryDebug> newQueries = new HashSet<QueryDebug>();
                newQueries.add(nextQueryObject);
                
                BlacklistController.currentQueryDebugInformation.put(
                        nextQueryObject.clientIPAddress, newQueries);
            }
            
            BlacklistController.evaluateClientBlacklist();
        }
    }
    
    public static synchronized void accumulateQueryTotal(String endpointUrl)
    {
        if(BlacklistController.allServerQueryTotals == null)
        {
            BlacklistController.allServerQueryTotals = new Hashtable<String, Integer>();
        }
        
        int newCount = 1;
        
        if(BlacklistController.allServerQueryTotals.containsKey(endpointUrl))
        {
            newCount = BlacklistController.allServerQueryTotals
                    .get(endpointUrl) + 1;
        }
        
        BlacklistController.allServerQueryTotals.put(endpointUrl, newCount);
    }
    
    public static int clearStatisticsUploadList()
    {
        final long start = System.currentTimeMillis();
        
        int numberRemoved = 0;
        
        synchronized(BlacklistController.internalStatisticsUploadList)
        {
            if(BlacklistController._DEBUG)
            {
                BlacklistController.log
                        .debug("BlacklistController.clearStatisticsUploadList: start of synchronized section start="
                                + start
                                + " internalStatisticsUploadList.size()="
                                + BlacklistController.internalStatisticsUploadList
                                        .size());
            }
            
            final Collection<HttpUrlQueryRunnable> completedThreads = new HashSet<HttpUrlQueryRunnable>();
            
            for(final HttpUrlQueryRunnable nextThread : BlacklistController.internalStatisticsUploadList)
            {
                if(nextThread.completed)
                {
                    completedThreads.add(nextThread);
                }
            }
            
            for(final HttpUrlQueryRunnable nextThread : completedThreads)
            {
                if(nextThread == null)
                    continue;
                
                if(!nextThread.wasSuccessful)
                {
                    BlacklistController.log.error(
                                    "BlacklistController: found error while clearing completed statistics threads");
                    if(BlacklistController._DEBUG)
                    {
                        BlacklistController.log.debug(nextThread.lastException);
                    }
                }
                
                BlacklistController.internalStatisticsUploadList
                        .remove(nextThread);
                numberRemoved++;
            }
            
            if(BlacklistController._DEBUG)
            {
                BlacklistController.log
                        .debug("BlacklistController.clearStatisticsUploadList: end of synchronized section start="
                                + start
                                + " numberRemoved="
                                + numberRemoved
                                + " internalStatisticsUploadList.size()="
                                + BlacklistController.internalStatisticsUploadList
                                        .size());
            }
        }
        
        if(BlacklistController._DEBUG)
        {
            final long end = System.currentTimeMillis();
            
            BlacklistController.log.debug(String.format("%s: timing=%10d",
                    "BlacklistController.clearStatisticsUploadList",
                    (end - start)));
        }
        
        if(BlacklistController._TRACE)
        {
            BlacklistController.log
                    .trace("BlacklistController.clearStatisticsUploadList: returning...");
        }
        
        return numberRemoved;
    }
    
    // this method checks for the expiry of the blacklist and clears the
    // blacklist if it has expired, and returns true if it has expired since
    // last time, even if there were no errors since then
    public static boolean doBlacklistExpiry()
    {
        // magic values for no expiry are <= 0
        
        if(Settings.getSettings().getLongPropertyFromConfig("blacklistResetPeriodMilliseconds") <= 0)
        {
            return false;
        }
        
        boolean neededToExpire = false;
        
        final Date currentDate = new Date();
        
        final long differenceMilliseconds = currentDate.getTime()
                - BlacklistController.lastExpiryDate.getTime();
        
        if((differenceMilliseconds - Settings.getSettings().getLongPropertyFromConfig("blacklistResetPeriodMilliseconds")) >= 0)
        {
            if(BlacklistController._INFO)
            {
                BlacklistController.log
                        .info("BlacklistController: needToExpire");
            }
            
            if(BlacklistController._TRACE)
            {
                
                for(final String nextEndpointUrl : BlacklistController.accumulatedBlacklistStatistics
                        .keySet())
                {
                    BlacklistController.log
                            .trace("BlacklistController: going to expire blacklist entry for endpointUrl="
                                    + nextEndpointUrl);
                }
            }
            
            // wipe the relevant objects out so the memory and references they
            // currently have will be garbage collected
            BlacklistController.accumulatedBlacklistStatistics = new Hashtable<String, BlacklistEntry>();
            
            BlacklistController.allCurrentBadQueries = new HashSet<RdfFetcherQueryRunnable>();
            
            if(Settings.getSettings().getBooleanPropertyFromConfig("blacklistResetClientBlacklistWithEndpoints"))
            {
                BlacklistController.currentQueryDebugInformation = new Hashtable<String, Collection<QueryDebug>>();
                BlacklistController.initialiseBlacklist();
            }
            
            // keep a track of this time
            BlacklistController.lastExpiryDate = new Date();
            
            neededToExpire = true;
        }
        
        return neededToExpire;
    }
    
    public static void evaluateClientBlacklist()
    {
        if(Settings.getSettings().getBooleanPropertyFromConfig("automaticallyBlacklistClients"))
        {
            
            for(final String nextKey : BlacklistController.currentQueryDebugInformation.keySet())
            {
                final Collection<QueryDebug> nextClientQueryList = BlacklistController.currentQueryDebugInformation
                        .get(nextKey);
                
                final int overallCount = nextClientQueryList.size();
                
                if(overallCount >= Settings.getSettings().getIntPropertyFromConfig("blacklistMinimumQueriesBeforeBlacklistRules"))
                {
                    int robotsTxtCount = 0;
                    
                    for(final QueryDebug nextQueryDebug : nextClientQueryList)
                    {
                        boolean isQueryRobotsTxt = false;
                        
                        for(final URI nextQueryDebugTitle : nextQueryDebug.matchingQueryTitles)
                        {
                            for(final QueryType nextQueryDebugType : Settings.getSettings().getQueryTypesByUri(nextQueryDebugTitle))
                            {
                                if(nextQueryDebugType.getInRobotsTxt())
                                {
                                    if(BlacklistController._TRACE)
                                    {
                                        BlacklistController.log
                                                .trace("BlacklistController: found query in robots.txt client="
                                                        + nextKey
                                                        + " nextQueryDebugTitle="
                                                        + nextQueryDebugTitle);
                                    }
                                    
                                    isQueryRobotsTxt = true;
                                    break;
                                }
                            }
                            
                            if(isQueryRobotsTxt)
                            {
                                break;
                            }
                        }
                        
                        if(isQueryRobotsTxt)
                        {
                            robotsTxtCount++;
                        }
                    }
                    
                    final double robotsPercentage = robotsTxtCount * 1.0
                            / overallCount;
                    
                    if(BlacklistController._TRACE)
                    {
                        BlacklistController.log
                                .trace("BlacklistController: results client="
                                        + nextKey + " robotsTxtCount="
                                        + robotsTxtCount + " overallCount="
                                        + overallCount + " robotsPercentage="
                                        + robotsPercentage);
                    }
                    
                    if(robotsPercentage > Settings.getSettings().getFloatPropertyFromConfig("blacklistPercentageOfRobotTxtQueriesBeforeAutomatic"))
                    {
                        BlacklistController.log
                                .warn("BlacklistController: Found client performing too many robots.txt banned queries nextKey="
                                        + nextKey
                                        + " robotsTxtCount="
                                        + robotsTxtCount
                                        + " overallCount="
                                        + overallCount
                                        + " robotsPercentage="
                                        + robotsPercentage);
                        
                        if(!BlacklistController.isClientBlacklisted(nextKey))
                        {
                            BlacklistController.currentIPBlacklist.add(nextKey);
                        }
                    }
                    
                    if(overallCount > Settings.getSettings().getIntPropertyFromConfig("blacklistClientMaxQueriesPerPeriod"))
                    {
                        if(!BlacklistController.isClientWhitelisted(nextKey))
                        {
                            BlacklistController.log
                                    .warn("BlacklistController: Found client performing too many queries and banned them permanently for the lifetime of the servlet nextKey="
                                            + nextKey
                                            + " robotsTxtCount="
                                            + robotsTxtCount
                                            + " overallCount="
                                            + overallCount
                                            + " robotsPercentage="
                                            + robotsPercentage
                                            + " Settings.getSettings().getIntPropertyFromConfig(\"blacklistClientMaxQueriesPerPeriod\")="
                                            + Settings.getSettings().getIntPropertyFromConfig("blacklistClientMaxQueriesPerPeriod"));
                            
                            BlacklistController.permanentServletLifetimeIPBlacklist
                                    .add(nextKey);
                            BlacklistController.permanentServletLifetimeIPBlacklistEvidence
                                    .put(nextKey, nextClientQueryList);
                            
                            BlacklistController.log
                                    .warn("Did not properly add ip to the permanent blacklist: nextKey="
                                            + nextKey);
                        }
                    }
                }
            }
        }
    }
    
    public static Collection<QueryDebug> getCurrentDebugInformationFor(
            String nextIpAddress)
    {
        if(BlacklistController.currentQueryDebugInformation
                .containsKey(nextIpAddress))
        {
            return BlacklistController.currentQueryDebugInformation
                    .get(nextIpAddress);
        }
        else
        {
            return new HashSet<QueryDebug>();
        }
    }
    
    public static Collection<String> getCurrentIPBlacklist()
    {
        if(BlacklistController.currentIPBlacklist != null)
        {
            return BlacklistController.currentIPBlacklist;
        }
        
        BlacklistController.initialiseBlacklist();
        
        return BlacklistController.currentIPBlacklist;
    }
    
    public static Collection<String> getCurrentIPWhitelist()
    {
        if(BlacklistController.currentIPWhitelist != null)
        {
            return BlacklistController.currentIPWhitelist;
        }
        
        BlacklistController.initialiseWhitelist();
        
        return BlacklistController.currentIPWhitelist;
    }
    
    public static Collection<String> getEndpointUrlsInBlacklist()
    {
        BlacklistController.doBlacklistExpiry();
        
        final Collection<String> results = new HashSet<String>();
        
        for(final String nextKey : BlacklistController.accumulatedBlacklistStatistics
                .keySet())
        {
            results.add(nextKey);
        }
        
        return results;
    }
    
    public static Collection<String> getPermanentIPBlacklist()
    {
        if(BlacklistController.permanentServletLifetimeIPBlacklist != null)
        {
            return BlacklistController.permanentServletLifetimeIPBlacklist;
        }
        
        BlacklistController.initialiseBlacklist();
        
        return BlacklistController.permanentServletLifetimeIPBlacklist;
    }
    
    public static void initialiseBlacklist()
    {
        if(BlacklistController.permanentServletLifetimeIPBlacklist == null)
        {
            BlacklistController.permanentServletLifetimeIPBlacklist = new HashSet<String>();
        }
        
        BlacklistController.currentIPBlacklist = Settings.getSettings().getStringCollectionPropertiesFromConfig("blacklistBaseClientIPAddresses");
    }
    
    public static void initialiseWhitelist()
    {
        BlacklistController.currentIPWhitelist = Settings.getSettings().getStringCollectionPropertiesFromConfig("whitelistBaseClientIPAddresses");
    }
    
    public static boolean isClientBlacklisted(String nextClientIPAddress)
    {
        if(BlacklistController.getCurrentIPWhitelist().contains(
                nextClientIPAddress))
        {
            return false;
        }
        else
        {
            // TODO: enable range blacklisting and change the following code
            return (BlacklistController.getPermanentIPBlacklist().contains(
                    nextClientIPAddress) || BlacklistController
                    .getCurrentIPBlacklist().contains(nextClientIPAddress));
        }
    }
    
    public static boolean isClientPermanentlyBlacklisted(
            String nextClientIPAddress)
    {
        if(BlacklistController.getCurrentIPWhitelist().contains(
                nextClientIPAddress))
        {
            return false;
        }
        else
        {
            // TODO: enable range blacklisting and change the following code
            return BlacklistController.getPermanentIPBlacklist().contains(
                    nextClientIPAddress);
        }
    }
    
    public static boolean isClientWhitelisted(String nextClientIPAddress)
    {
        return BlacklistController.getCurrentIPWhitelist().contains(
                nextClientIPAddress);
    }
    
    public static boolean isEndpointBlacklisted(String nextEndpointUrl)
    {
        BlacklistController.doBlacklistExpiry();
        
        if(BlacklistController.accumulatedBlacklistStatistics
                .containsKey(nextEndpointUrl))
        {
            final BlacklistEntry currentCount = BlacklistController.accumulatedBlacklistStatistics
                    .get(nextEndpointUrl);
            
            return (currentCount.numberOfFailures >= Settings.getSettings().getIntPropertyFromConfig("blacklistMaxAccumulatedFailures"));
        }
        else
        {
            return false;
        }
    }
    
    public static void persistStatistics(
            Collection<StatisticsEntry> nextStatisticsEntries, int modelVersion)
    {
        final long start = System.currentTimeMillis();
        
        BlacklistController.clearStatisticsUploadList();
        
        final Collection<HttpUrlQueryRunnable> runnableThreads = new HashSet<HttpUrlQueryRunnable>();
        
        for(final StatisticsEntry nextStatisticsEntry : nextStatisticsEntries)
        {
            try
            {
                final HttpUrlQueryRunnable nextThread = nextStatisticsEntry.generateThread(modelVersion);
                nextThread.start();
                runnableThreads.add(nextThread);
            }
            catch (final OpenRDFException ordfe)
            {
                BlacklistController.log
                        .error(
                                "BlacklistController.persistStatistics: exception found:",
                                ordfe);
            }
        }
        
        synchronized(BlacklistController.internalStatisticsUploadList)
        {
            if(BlacklistController._DEBUG)
            {
                BlacklistController.log
                        .debug("BlacklistController.persistStatistics: start of synchronized section start="
                                + start
                                + " internalStatisticsUploadList.size()="
                                + BlacklistController.internalStatisticsUploadList
                                        .size());
            }
            
            for(final HttpUrlQueryRunnable nextRunnableThread : runnableThreads)
            {
                BlacklistController.internalStatisticsUploadList
                        .add(nextRunnableThread);
            }
            
            if(BlacklistController._DEBUG)
            {
                BlacklistController.log
                        .debug("BlacklistController.persistStatistics: end of synchronized section start="
                                + start
                                + " internalStatisticsUploadList.size()="
                                + BlacklistController.internalStatisticsUploadList
                                        .size());
            }
        }
        
        if(BlacklistController._DEBUG)
        {
            final long end = System.currentTimeMillis();
            
            BlacklistController.log.debug(String.format("%s: timing=%10d",
                    "BlacklistController.persistStatistics", (end - start)));
        }
        
        if(BlacklistController._TRACE)
        {
            BlacklistController.log
                    .trace("BlacklistController.persistStatistics: returning...");
        }
    }
    
    public static void removeEndpointsFromBlacklist(
            Collection<RdfFetcherQueryRunnable> successfulQueries)
    {
        BlacklistController.doBlacklistExpiry();

        synchronized(BlacklistController.accumulatedBlacklistStatistics)
        {
            for(final RdfFetcherQueryRunnable nextQueryObject : successfulQueries)
            {
                // only deal with it if it was blacklisted
                BlacklistController.accumulatedBlacklistStatistics
                        .remove(nextQueryObject.endpointUrl);
            }
        }
    }

    public static boolean isUrlBlacklisted(String inputUrl)
    {
        URL url = null;
        
        try
        {
            url = new URL(inputUrl);
        }
        catch(Exception ex)
        {
            // ignore it, the endpoint doesn't always have to be a URL, we just won't consult the blacklist controller in this case
            return false;
        }
        
        if( BlacklistController.isEndpointBlacklisted( inputUrl ) || BlacklistController.isEndpointBlacklisted(url.getProtocol()+"://"+url.getAuthority()) )
        {
            if( _DEBUG )
            {
                log.debug( "BlacklistController.isUrlBlacklisted: found blacklisted URL inputUrl="+url+" simple form="+url.getProtocol()+"://"+url.getAuthority());
            }
            
            return true;
        }
        
        return false;
    }
    
    public static String getAlternativeUrl(String blacklistedUrl, List<String> urlList)
    {
        if(blacklistedUrl == null || blacklistedUrl == "" || urlList == null)
        {
            log.error("BlacklistController.getAlternativeUrl: something was wrong blacklistedUrl="+blacklistedUrl+" urlList="+urlList);
            
            return null;
        }
        
        // try to avoid always returning the same alternative by randomising here
        urlList = Utilities.randomiseListLayout(urlList);
        
        for(String nextEndpoint : urlList)
        {
            if(nextEndpoint.equals(blacklistedUrl))
                continue;
            
            if(!BlacklistController.isUrlBlacklisted(nextEndpoint))
            {
                return nextEndpoint;
            }
        }
        
        return null;
    }

}
