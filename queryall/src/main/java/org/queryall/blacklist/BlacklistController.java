
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
import org.queryall.api.QueryType;
import org.queryall.helpers.ListUtils;
import org.queryall.helpers.Settings;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
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
    
    public Map<String, BlacklistEntry> accumulatedBlacklistStatistics = new Hashtable<String, BlacklistEntry>();
    
    public Map<String, Hashtable<Integer, Integer>> allHttpErrorResponseCodesByServer = new Hashtable<String, Hashtable<Integer, Integer>>();
    
    public Map<String, Integer> allServerQueryTotals = new Hashtable<String, Integer>();
    
    public Collection<RdfFetcherQueryRunnable> allCurrentBadQueries = new HashSet<RdfFetcherQueryRunnable>();
    
    public Map<String, Collection<QueryDebug>> currentQueryDebugInformation = new Hashtable<String, Collection<QueryDebug>>();
    
    public Collection<String> currentIPBlacklist = null;
    
    public Collection<String> permanentServletLifetimeIPBlacklist = new HashSet<String>();
    
    public Map<String, Collection<QueryDebug>> permanentServletLifetimeIPBlacklistEvidence = new Hashtable<String, Collection<QueryDebug>>();
    
    public Collection<String> currentIPWhitelist = null;
    
    public Collection<HttpUrlQueryRunnable> internalStatisticsUploadList = new HashSet<HttpUrlQueryRunnable>();
    
    public Date lastServerStartupDate = new Date();
    
    public Date lastExpiryDate = new Date();
    
    private Settings localSettings = Settings.getSettings();
    
    private static BlacklistController defaultController = new BlacklistController(Settings.getSettings());
    
    
    public BlacklistController(Settings nextLocalSettings)
    {
        localSettings = nextLocalSettings;
    }

    /**
     * @return the defaultController
     */
    public static BlacklistController getDefaultController()
    {
        return defaultController;
    }

    public void accumulateBlacklist(
            Collection<RdfFetcherQueryRunnable> temporaryEndpointBlacklist, long blacklistResetPeriodMilliseconds, boolean blacklistResetClientBlacklistWithEndpoints)
    {
        synchronized(this.accumulatedBlacklistStatistics)
        {
            this.doBlacklistExpiry();
            
            for(final RdfFetcherQueryRunnable nextQueryObject : temporaryEndpointBlacklist)
            {
                if(BlacklistController._DEBUG)
                {
                    BlacklistController.log
                            .debug("BlacklistController.accumulateBlacklist: going to accumulate entry for endpointUrl="
                                    + nextQueryObject.getEndpointUrl());
                }
                
                if(this.accumulatedBlacklistStatistics
                        .containsKey(nextQueryObject.getEndpointUrl()))
                {
                    final BlacklistEntry previousCount = this.accumulatedBlacklistStatistics
                            .get(nextQueryObject.getEndpointUrl());
                    
                    if(BlacklistController._DEBUG)
                    {
                        BlacklistController.log
                                .debug("BlacklistController.accumulateBlacklist: There were "
                                        + previousCount
                                        + " previous instances on blacklist for endpointUrl="
                                        + nextQueryObject.getEndpointUrl());
                    }
                    
                    previousCount.numberOfFailures++;
                    previousCount.errorRunnables.add(nextQueryObject);
                    
                    this.accumulatedBlacklistStatistics.put(
                            nextQueryObject.getEndpointUrl(), previousCount);
                }
                else
                {
                    final BlacklistEntry newFailureCount = new BlacklistEntry();
                    newFailureCount.endpointUrl = nextQueryObject.getEndpointUrl();
                    newFailureCount.numberOfFailures = 1;
                    newFailureCount.errorRunnables = new HashSet<RdfFetcherQueryRunnable>();
                    newFailureCount.errorRunnables.add(nextQueryObject);
                    
                    this.accumulatedBlacklistStatistics.put(
                            nextQueryObject.getEndpointUrl(), newFailureCount);
                }
                
                this.allCurrentBadQueries.add(nextQueryObject);
            }
        }
    }
    
    public synchronized void accumulateHttpResponseError(
            String endpointUrl, int errorResponseCode)
    {
        if(this.allHttpErrorResponseCodesByServer == null)
        {
            if(BlacklistController._TRACE)
            {
                BlacklistController.log
                        .trace("BlacklistController.accumulateHttpResponseError: allHttpErrorResponseCodesByServer was null");
            }
            
            this.allHttpErrorResponseCodesByServer = new Hashtable<String, Hashtable<Integer, Integer>>();
        }
        
        Hashtable<Integer, Integer> nextErrorList = null;
        
        if(BlacklistController._TRACE)
        {
            
            for(final String nextCurrentEndpoint : this.allHttpErrorResponseCodesByServer
                    .keySet())
            {
                BlacklistController.log.trace("nextCurrentEndpoint="
                        + nextCurrentEndpoint);
            }
        }
        
        if(this.allHttpErrorResponseCodesByServer
                .containsKey(endpointUrl))
        {
            if(BlacklistController._TRACE)
            {
                BlacklistController.log
                        .trace("BlacklistController.accumulateHttpResponseError: allHttpErrorResponseCodesByServer already contains endpointUrl="
                                + endpointUrl);
            }
            
            nextErrorList = this.allHttpErrorResponseCodesByServer
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
        
        this.allHttpErrorResponseCodesByServer.put(endpointUrl,
                nextErrorList);
    }
    
    public synchronized void accumulateQueryDebug(
            QueryDebug nextQueryObject, Settings localSettings, long blacklistResetPeriodMilliseconds, boolean blacklistResetClientBlacklistWithEndpoints, boolean automaticallyBlacklistClients, int blacklistMinimumQueriesBeforeBlacklistRules, int blacklistClientMaxQueriesPerPeriod)
    {
        if(automaticallyBlacklistClients)
        {
            if(BlacklistController._DEBUG)
            {
                BlacklistController.log
                        .debug("BlacklistController.accumulateQueryDebug: going to accumulate entry for clientIPAddress="
                                + nextQueryObject.clientIPAddress);
            }
            
            this.doBlacklistExpiry();
            
            if(this.currentQueryDebugInformation
                    .containsKey(nextQueryObject.clientIPAddress))
            {
                final Collection<QueryDebug> previousQueries = this.currentQueryDebugInformation
                        .get(nextQueryObject.clientIPAddress);
                
                previousQueries.add(nextQueryObject);
                
                this.currentQueryDebugInformation.put(
                        nextQueryObject.clientIPAddress, previousQueries);
            }
            else
            {
                final Collection<QueryDebug> newQueries = new HashSet<QueryDebug>();
                newQueries.add(nextQueryObject);
                
                this.currentQueryDebugInformation.put(
                        nextQueryObject.clientIPAddress, newQueries);
            }
            
            this.evaluateClientBlacklist(localSettings, automaticallyBlacklistClients, blacklistMinimumQueriesBeforeBlacklistRules, blacklistResetPeriodMilliseconds, blacklistClientMaxQueriesPerPeriod);
        }
    }
    
    public synchronized void accumulateQueryTotal(String endpointUrl)
    {
        if(this.allServerQueryTotals == null)
        {
            this.allServerQueryTotals = new Hashtable<String, Integer>();
        }
        
        int newCount = 1;
        
        if(this.allServerQueryTotals.containsKey(endpointUrl))
        {
            newCount = this.allServerQueryTotals
                    .get(endpointUrl) + 1;
        }
        
        this.allServerQueryTotals.put(endpointUrl, newCount);
    }
    
    public int clearStatisticsUploadList()
    {
        final long start = System.currentTimeMillis();
        
        int numberRemoved = 0;
        
        synchronized(this.internalStatisticsUploadList)
        {
            if(BlacklistController._DEBUG)
            {
                BlacklistController.log
                        .debug("BlacklistController.clearStatisticsUploadList: start of synchronized section start="
                                + start
                                + " internalStatisticsUploadList.size()="
                                + this.internalStatisticsUploadList
                                        .size());
            }
            
            final Collection<HttpUrlQueryRunnable> completedThreads = new HashSet<HttpUrlQueryRunnable>();
            
            for(final HttpUrlQueryRunnable nextThread : this.internalStatisticsUploadList)
            {
                if(nextThread.getCompleted())
                {
                    completedThreads.add(nextThread);
                }
            }
            
            for(final HttpUrlQueryRunnable nextThread : completedThreads)
            {
                if(nextThread == null)
                    continue;
                
                if(!nextThread.getWasSuccessful())
                {
                    BlacklistController.log.error(
                                    "BlacklistController: found error while clearing completed statistics threads");
                    if(BlacklistController._DEBUG)
                    {
                        BlacklistController.log.debug(nextThread.getLastException());
                    }
                }
                
                this.internalStatisticsUploadList
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
                                + this.internalStatisticsUploadList
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
    public boolean doBlacklistExpiry()
    {
        long blacklistResetPeriodMilliseconds = localSettings.getLongPropertyFromConfig("blacklistResetPeriodMilliseconds", 60000L);
        
        boolean blacklistResetClientBlacklistWithEndpoints = localSettings.getBooleanPropertyFromConfig("blacklistResetClientBlacklistWithEndpoints", true);

        // magic values for no expiry are <= 0
        
        if(blacklistResetPeriodMilliseconds <= 0)
        {
            return false;
        }
        
        boolean neededToExpire = false;
        
        final Date currentDate = new Date();
        
        final long differenceMilliseconds = currentDate.getTime()
                - this.lastExpiryDate.getTime();
        
        if((differenceMilliseconds - blacklistResetPeriodMilliseconds) >= 0)
        {
            if(BlacklistController._INFO)
            {
                BlacklistController.log
                        .info("BlacklistController: needToExpire");
            }
            
            if(BlacklistController._TRACE)
            {
                
                for(final String nextEndpointUrl : this.accumulatedBlacklistStatistics
                        .keySet())
                {
                    BlacklistController.log
                            .trace("BlacklistController: going to expire blacklist entry for endpointUrl="
                                    + nextEndpointUrl);
                }
            }
            
            // wipe the relevant objects out so the memory and references they
            // this have will be garbage collected
            this.accumulatedBlacklistStatistics = new Hashtable<String, BlacklistEntry>();
            
            this.allCurrentBadQueries = new HashSet<RdfFetcherQueryRunnable>();
            
            if(blacklistResetClientBlacklistWithEndpoints)
            {
                this.currentQueryDebugInformation = new Hashtable<String, Collection<QueryDebug>>();
                this.initialiseBlacklist();
            }
            
            // keep a track of this time
            this.lastExpiryDate = new Date();
            
            neededToExpire = true;
        }
        
        return neededToExpire;
    }
    
    public void evaluateClientBlacklist(Settings localSettings, boolean automaticallyBlacklistClients, int blacklistMinimumQueriesBeforeBlacklistRules, float blacklistPercentageOfRobotTxtQueriesBeforeAutomatic, int blacklistClientMaxQueriesPerPeriod)
    {
        if(automaticallyBlacklistClients)
        {
            
            for(final String nextKey : this.currentQueryDebugInformation.keySet())
            {
                final Collection<QueryDebug> nextClientQueryList = this.currentQueryDebugInformation
                        .get(nextKey);
                
                final int overallCount = nextClientQueryList.size();
                
                if(overallCount >= blacklistMinimumQueriesBeforeBlacklistRules)
                {
                    int robotsTxtCount = 0;
                    
                    for(final QueryDebug nextQueryDebug : nextClientQueryList)
                    {
                        boolean isQueryRobotsTxt = false;
                        
                        for(final URI nextQueryDebugTitle : nextQueryDebug.matchingQueryTitles)
                        {
                            for(final QueryType nextQueryDebugType : localSettings.getQueryTypesByUri(nextQueryDebugTitle))
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
                    
                    if(robotsPercentage > blacklistPercentageOfRobotTxtQueriesBeforeAutomatic)
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
                        
                        if(!this.isClientBlacklisted(nextKey))
                        {
                            this.currentIPBlacklist.add(nextKey);
                        }
                    }
                    
                    if(overallCount > blacklistClientMaxQueriesPerPeriod)
                    {
                        if(!this.isClientWhitelisted(nextKey))
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
                                            + " blacklistClientMaxQueriesPerPeriod="
                                            + blacklistClientMaxQueriesPerPeriod);
                            
                            this.permanentServletLifetimeIPBlacklist
                                    .add(nextKey);
                            this.permanentServletLifetimeIPBlacklistEvidence
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
    
    public Collection<QueryDebug> getCurrentDebugInformationFor(
            String nextIpAddress)
    {
        if(this.currentQueryDebugInformation
                .containsKey(nextIpAddress))
        {
            return this.currentQueryDebugInformation
                    .get(nextIpAddress);
        }
        else
        {
            return new HashSet<QueryDebug>();
        }
    }
    
    public Collection<String> getCurrentIPBlacklist()
    {
        if(this.currentIPBlacklist != null)
        {
            return this.currentIPBlacklist;
        }
        
        this.initialiseBlacklist();
        
        return this.currentIPBlacklist;
    }
    
    public Collection<String> getCurrentIPWhitelist()
    {
        if(this.currentIPWhitelist != null)
        {
            return this.currentIPWhitelist;
        }
        
        this.initialiseWhitelist();
        
        return this.currentIPWhitelist;
    }
    
    public Collection<String> getEndpointUrlsInBlacklist(long blacklistResetPeriodMilliseconds, boolean blacklistResetClientBlacklistWithEndpoints)
    {
        this.doBlacklistExpiry();
        
        final Collection<String> results = new HashSet<String>();
        
        for(final String nextKey : this.accumulatedBlacklistStatistics
                .keySet())
        {
            results.add(nextKey);
        }
        
        return results;
    }
    
    public Collection<String> getPermanentIPBlacklist()
    {
        if(this.permanentServletLifetimeIPBlacklist != null)
        {
            return this.permanentServletLifetimeIPBlacklist;
        }
        
        this.initialiseBlacklist();
        
        return this.permanentServletLifetimeIPBlacklist;
    }
    
    public void initialiseBlacklist()
    {
        if(this.permanentServletLifetimeIPBlacklist == null)
        {
            this.permanentServletLifetimeIPBlacklist = new HashSet<String>();
        }
        
        this.currentIPBlacklist = localSettings.getStringCollectionPropertiesFromConfig("blacklistBaseClientIPAddresses");
    }
    
    public void initialiseWhitelist()
    {
        this.currentIPWhitelist = localSettings.getStringCollectionPropertiesFromConfig("whitelistBaseClientIPAddresses");
    }
    
    public boolean isClientBlacklisted(String nextClientIPAddress)
    {
        if(this.getCurrentIPWhitelist().contains(
                nextClientIPAddress))
        {
            return false;
        }
        else
        {
            // TODO: enable range blacklisting and change the following code
            return (this.getPermanentIPBlacklist().contains(
                    nextClientIPAddress) || this
                    .getCurrentIPBlacklist().contains(nextClientIPAddress));
        }
    }
    
    public boolean isClientPermanentlyBlacklisted(
            String nextClientIPAddress)
    {
        if(this.getCurrentIPWhitelist().contains(
                nextClientIPAddress))
        {
            return false;
        }
        else
        {
            // TODO: enable range blacklisting and change the following code
            return this.getPermanentIPBlacklist().contains(
                    nextClientIPAddress);
        }
    }
    
    public boolean isClientWhitelisted(String nextClientIPAddress)
    {
        return this.getCurrentIPWhitelist().contains(
                nextClientIPAddress);
    }
    
    public boolean isEndpointBlacklisted(String nextEndpointUrl)
    {
        this.doBlacklistExpiry();
        
        if(this.accumulatedBlacklistStatistics
                .containsKey(nextEndpointUrl))
        {
            final BlacklistEntry currentCount = this.accumulatedBlacklistStatistics
                    .get(nextEndpointUrl);
            
            return (currentCount.numberOfFailures >= localSettings.getIntPropertyFromConfig("blacklistMaxAccumulatedFailures", 0));
        }
        else
        {
            return false;
        }
    }
    
    public void persistStatistics(
            Collection<StatisticsEntry> nextStatisticsEntries, int modelVersion)
    {
        final long start = System.currentTimeMillis();
        
        this.clearStatisticsUploadList();
        
        final Collection<HttpUrlQueryRunnable> runnableThreads = new HashSet<HttpUrlQueryRunnable>();
        
        for(final StatisticsEntry nextStatisticsEntry : nextStatisticsEntries)
        {
            try
            {
                final HttpUrlQueryRunnable nextThread = nextStatisticsEntry.generateThread(localSettings, this, modelVersion);
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
        
        synchronized(this.internalStatisticsUploadList)
        {
            if(BlacklistController._DEBUG)
            {
                BlacklistController.log
                        .debug("BlacklistController.persistStatistics: start of synchronized section start="
                                + start
                                + " internalStatisticsUploadList.size()="
                                + this.internalStatisticsUploadList
                                        .size());
            }
            
            for(final HttpUrlQueryRunnable nextRunnableThread : runnableThreads)
            {
                this.internalStatisticsUploadList
                        .add(nextRunnableThread);
            }
            
            if(BlacklistController._DEBUG)
            {
                BlacklistController.log
                        .debug("BlacklistController.persistStatistics: end of synchronized section start="
                                + start
                                + " internalStatisticsUploadList.size()="
                                + this.internalStatisticsUploadList
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
    
    public void removeEndpointsFromBlacklist(
            Collection<RdfFetcherQueryRunnable> successfulQueries, long blacklistResetPeriodMilliseconds, boolean blacklistResetClientBlacklistWithEndpoints)
    {
        this.doBlacklistExpiry();

        synchronized(this.accumulatedBlacklistStatistics)
        {
            for(final RdfFetcherQueryRunnable nextQueryObject : successfulQueries)
            {
                // only deal with it if it was blacklisted
                this.accumulatedBlacklistStatistics
                        .remove(nextQueryObject.getEndpointUrl());
            }
        }
    }

    public boolean isUrlBlacklisted(String inputUrl)
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
        
        // we test both the full URL and the protocol://host subsection
        if( this.isEndpointBlacklisted( inputUrl ) || this.isEndpointBlacklisted(url.getProtocol()+"://"+url.getAuthority()) )
        {
            if( _DEBUG )
            {
                log.debug( "BlacklistController.isUrlBlacklisted: found blacklisted URL inputUrl="+url+" simple form="+url.getProtocol()+"://"+url.getAuthority());
            }
            
            return true;
        }
        
        return false;
    }
    
    public String getAlternativeUrl(String blacklistedUrl, List<String> urlList)
    {
        if(blacklistedUrl == null || blacklistedUrl == "" || urlList == null)
        {
            log.error("BlacklistController.getAlternativeUrl: something was wrong blacklistedUrl="+blacklistedUrl+" urlList="+urlList);
            
            return null;
        }
        
        // try to avoid always returning the same alternative by randomising here
        urlList = ListUtils.randomiseListLayout(urlList);
        
        for(String nextEndpoint : urlList)
        {
            if(nextEndpoint.equals(blacklistedUrl))
                continue;
            
            if(!this.isUrlBlacklisted(nextEndpoint))
            {
                return nextEndpoint;
            }
        }
        
        return null;
    }

}
