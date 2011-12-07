package org.queryall.blacklist;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.querytype.QueryType;
import org.queryall.query.HttpUrlQueryRunnable;
import org.queryall.query.QueryDebug;
import org.queryall.query.RdfFetcherQueryRunnable;
import org.queryall.query.Settings;
import org.queryall.statistics.StatisticsEntry;
import org.queryall.utils.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The blacklist controller class aggregates failures and client queries.
 * 
 * It uses this aggregated information to identify clients that are performing too many requests in
 * a short period of time and to temporarily isolate misbehaving endpoints so that queries are
 * efficient, and servers are not overloaded while they are struggling.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class BlacklistController
{
    // Wrap up the singleton in its own inner static class
    private static class BlacklistControllerHolder
    {
        public static final BlacklistController helper = new BlacklistController(Settings.getSettings());
    }
    
    private static final Logger log = LoggerFactory.getLogger(BlacklistController.class);
    private static final boolean _TRACE = BlacklistController.log.isTraceEnabled();
    private static final boolean _DEBUG = BlacklistController.log.isDebugEnabled();
    private static final boolean _INFO = BlacklistController.log.isInfoEnabled();
    
    /**
     * @return the defaultController
     */
    public static BlacklistController getDefaultController()
    {
        return BlacklistControllerHolder.helper;
    }
    
    public BlacklistController(final QueryAllConfiguration queryAllConfiguration)
    {
        this.localSettings = queryAllConfiguration;
        this.currentIPBlacklist = Collections
                .synchronizedCollection(queryAllConfiguration.getStringProperties("blacklistBaseClientIPAddresses"));
        this.currentIPWhitelist = Collections
                .synchronizedCollection(queryAllConfiguration.getStringProperties("whitelistBaseClientIPAddresses"));        
    }
    
    private volatile ConcurrentHashMap<String, BlacklistEntry> accumulatedBlacklistStatistics =
            new ConcurrentHashMap<String, BlacklistEntry>(200);
    
    private volatile ConcurrentHashMap<String, Map<Integer, Integer>> allHttpErrorResponseCodesByServer =
            new ConcurrentHashMap<String, Map<Integer, Integer>>(200);
    
    private volatile ConcurrentHashMap<String, Integer> allServerQueryTotals = new ConcurrentHashMap<String, Integer>(200);
    
    private volatile Collection<RdfFetcherQueryRunnable> allCurrentBadQueries = Collections
            .synchronizedSet(new HashSet<RdfFetcherQueryRunnable>(200));
    
    private volatile ConcurrentHashMap<String, Collection<QueryDebug>> currentQueryDebugInformation =
            new ConcurrentHashMap<String, Collection<QueryDebug>>(200);
    
    private volatile Collection<String> currentIPBlacklist = null;
    
    private volatile Collection<String> permanentServletLifetimeIPBlacklist = Collections
            .synchronizedSet(new HashSet<String>(200));
    
    private volatile ConcurrentHashMap<String, Collection<QueryDebug>> permanentServletLifetimeIPBlacklistEvidence =
            new ConcurrentHashMap<String, Collection<QueryDebug>>(200);
    
    private volatile Collection<String> currentIPWhitelist = null;
    
    private volatile Collection<HttpUrlQueryRunnable> internalStatisticsUploadList = Collections
            .synchronizedSet(new HashSet<HttpUrlQueryRunnable>());
    
    private volatile Date lastServerStartupDate = new Date();
    
    private volatile Date lastExpiryDate = new Date();
    
    private final QueryAllConfiguration localSettings;
    
    public void accumulateBlacklist(final Collection<RdfFetcherQueryRunnable> temporaryEndpointBlacklist)
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
            
            // FIXME: Need to know when this is null and not setup properly
            if(nextQueryObject.getEndpointUrl() != null)
            {
                if(this.accumulatedBlacklistStatistics.containsKey(nextQueryObject.getEndpointUrl()))
                {
                    final BlacklistEntry previousCount =
                            this.getAccumulatedBlacklistStatistics().get(nextQueryObject.getEndpointUrl());
                    
                    if(BlacklistController._DEBUG)
                    {
                        BlacklistController.log.debug("BlacklistController.accumulateBlacklist: There were "
                                + previousCount + " previous instances on blacklist for endpointUrl="
                                + nextQueryObject.getEndpointUrl());
                    }
                    
                    previousCount.addErrorMessageForRunnable(nextQueryObject);
                    
                    this.accumulatedBlacklistStatistics.put(nextQueryObject.getEndpointUrl(), previousCount);
                }
                else
                {
                    final BlacklistEntry newFailureCount = new BlacklistEntry();
                    newFailureCount.endpointUrl = nextQueryObject.getEndpointUrl();
                    newFailureCount.addErrorMessageForRunnable(nextQueryObject);
                    
                    this.accumulatedBlacklistStatistics.put(nextQueryObject.getEndpointUrl(), newFailureCount);
                }
                
                this.allCurrentBadQueries.add(nextQueryObject);
            }
        }
    }
    
    public void accumulateHttpResponseError(final String endpointUrl, final int errorResponseCode)
    {
        if(this.allHttpErrorResponseCodesByServer == null)
        {
            synchronized(this)
            {
                if(this.allHttpErrorResponseCodesByServer == null)
                {
                    this.allHttpErrorResponseCodesByServer = new ConcurrentHashMap<String, Map<Integer, Integer>>(200);
                }
            }
        }
        
        synchronized(this.allHttpErrorResponseCodesByServer)
        {
            Map<Integer, Integer> nextErrorList = null;
            
            if(this.allHttpErrorResponseCodesByServer.containsKey(endpointUrl))
            {
                nextErrorList = this.allHttpErrorResponseCodesByServer.get(endpointUrl);
                
                if(nextErrorList.containsKey(errorResponseCode))
                {
                    final int newCount = nextErrorList.get(errorResponseCode) + 1;
                    
                    nextErrorList.put(errorResponseCode, newCount);
                }
                else
                {
                    nextErrorList.put(errorResponseCode, 1);
                }
            }
            else
            {
                nextErrorList = new ConcurrentHashMap<Integer, Integer>();
                nextErrorList.put(errorResponseCode, 1);
            }
            
            this.allHttpErrorResponseCodesByServer.put(endpointUrl, nextErrorList);
        }
    }
    
    /**
     * Accumulates the statistics using properties from the settings object, or default values if
     * the properties are not set
     * 
     * @param nextQueryObject
     */
    public void accumulateQueryDebug(final QueryDebug nextQueryObject)
    {
        this.accumulateQueryDebug(nextQueryObject,
                this.localSettings.getLongProperty("blacklistResetPeriodMilliseconds", 120000L),
                this.localSettings.getBooleanProperty("blacklistResetClientBlacklistWithEndpoints", true),
                this.localSettings.getBooleanProperty("automaticallyBlacklistClients", false),
                this.localSettings.getIntProperty("blacklistMinimumQueriesBeforeBlacklistRules", 200),
                this.localSettings.getIntProperty("blacklistClientMaxQueriesPerPeriod", 400));
    }
    
    /**
     * Accumulates the debug statistics using the given parameters
     * 
     * @param nextQueryObject
     * @param blacklistResetPeriodMilliseconds
     * @param blacklistResetClientBlacklistWithEndpoints
     * @param automaticallyBlacklistClients
     * @param blacklistMinimumQueriesBeforeBlacklistRules
     * @param blacklistClientMaxQueriesPerPeriod
     */
    public void accumulateQueryDebug(final QueryDebug nextQueryObject, final long blacklistResetPeriodMilliseconds,
            final boolean blacklistResetClientBlacklistWithEndpoints, final boolean automaticallyBlacklistClients,
            final int blacklistMinimumQueriesBeforeBlacklistRules, final int blacklistClientMaxQueriesPerPeriod)
    {
        if(automaticallyBlacklistClients)
        {
            if(BlacklistController._DEBUG)
            {
                BlacklistController.log
                        .debug("BlacklistController.accumulateQueryDebug: going to accumulate entry for clientIPAddress="
                                + nextQueryObject.getClientIPAddress());
            }
            
            this.doBlacklistExpiry(blacklistResetPeriodMilliseconds, blacklistResetClientBlacklistWithEndpoints);
            
            // TODO: refactor the following code to use ConcurrentHashMap interface
            synchronized(this.currentQueryDebugInformation)
            {
                if(this.currentQueryDebugInformation.containsKey(nextQueryObject.getClientIPAddress()))
                {
                    final Collection<QueryDebug> previousQueries =
                            this.currentQueryDebugInformation.get(nextQueryObject.getClientIPAddress());
                    
                    previousQueries.add(nextQueryObject);
                    
                    this.currentQueryDebugInformation.put(nextQueryObject.getClientIPAddress(), previousQueries);
                }
                else
                {
                    final Collection<QueryDebug> newQueries = Collections.synchronizedSet(new HashSet<QueryDebug>());
                    newQueries.add(nextQueryObject);
                    
                    this.currentQueryDebugInformation.put(nextQueryObject.getClientIPAddress(), newQueries);
                }
            }
            
            this.evaluateClientBlacklist(automaticallyBlacklistClients, blacklistMinimumQueriesBeforeBlacklistRules,
                    blacklistResetPeriodMilliseconds, blacklistClientMaxQueriesPerPeriod);
        }
    }
    
    public void accumulateQueryTotal(final String endpointUrl)
    {
        if(this.allServerQueryTotals == null)
        {
            synchronized(this)
            {
                if(this.allServerQueryTotals == null)
                {
                    this.allServerQueryTotals = new ConcurrentHashMap<String, Integer>(200);
                }
            }
        }
        
        int newCount = 1;
        
        if(this.allServerQueryTotals.containsKey(endpointUrl))
        {
            newCount = this.allServerQueryTotals.get(endpointUrl) + 1;
        }
        
        this.allServerQueryTotals.put(endpointUrl, newCount);
    }
    
    public int clearStatisticsUploadList()
    {
        final long start = System.currentTimeMillis();
        
        int numberRemoved = 0;
        
        synchronized(this)
        {
            if(BlacklistController._DEBUG)
            {
                BlacklistController.log
                        .debug("BlacklistController.clearStatisticsUploadList: start of synchronized section start="
                                + start + " internalStatisticsUploadList.size()="
                                + this.internalStatisticsUploadList.size());
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
                {
                    continue;
                }
                
                if(!nextThread.getWasSuccessful())
                {
                    BlacklistController.log
                            .error("BlacklistController: found error while clearing completed statistics threads");
                    if(BlacklistController._DEBUG)
                    {
                        BlacklistController.log.debug(nextThread.getLastException().getMessage());
                    }
                }
                
                this.internalStatisticsUploadList.remove(nextThread);
                numberRemoved++;
            }
            
            if(BlacklistController._DEBUG)
            {
                BlacklistController.log
                        .debug("BlacklistController.clearStatisticsUploadList: end of synchronized section start="
                                + start + " numberRemoved=" + numberRemoved + " internalStatisticsUploadList.size()="
                                + this.internalStatisticsUploadList.size());
            }
        }
        
        if(BlacklistController._DEBUG)
        {
            final long end = System.currentTimeMillis();
            
            BlacklistController.log.debug(String.format("%s: timing=%10d",
                    "BlacklistController.clearStatisticsUploadList", (end - start)));
        }
        
        if(BlacklistController._TRACE)
        {
            BlacklistController.log.trace("BlacklistController.clearStatisticsUploadList: returning...");
        }
        
        return numberRemoved;
    }
    
    /**
     * This method checks for the expiry of the blacklist and clears the blacklist if it has expired, and returns true if it has expired since last time, even if there were no errors since then
     */
    public boolean doBlacklistExpiry()
    {
        final long blacklistResetPeriodMilliseconds =
                this.localSettings.getLongProperty("blacklistResetPeriodMilliseconds", 60000L);
        
        final boolean blacklistResetClientBlacklistWithEndpoints =
                this.localSettings.getBooleanProperty("blacklistResetClientBlacklistWithEndpoints", true);
        
        return doBlacklistExpiry(blacklistResetPeriodMilliseconds, blacklistResetClientBlacklistWithEndpoints);
    }
    
    /**
     * 
     * @param blacklistResetPeriodMilliseconds The period of time between resets to the blacklist. If it is less than or equal to 0 (ie, <= 0), then no resets will ever occur
     * @param blacklistResetClientBlacklistWithEndpoints True if the client blacklist should be reset along with endpoints
     * @return True if the blacklist had expired and was reset, and false otherwise
     */
    public boolean doBlacklistExpiry(long blacklistResetPeriodMilliseconds, boolean blacklistResetClientBlacklistWithEndpoints)
    {
        // magic values for no expiry are <= 0
        
        if(blacklistResetPeriodMilliseconds <= 0)
        {
            return false;
        }
        
        boolean neededToExpire = false;
        
        final Date currentDate = new Date();
        
        final long differenceMilliseconds = currentDate.getTime() - this.getLastExpiryDate().getTime();
        
        if((differenceMilliseconds - blacklistResetPeriodMilliseconds) >= 0)
        {
            if(BlacklistController._INFO)
            {
                BlacklistController.log.info("BlacklistController: needToExpire");

                if(BlacklistController._TRACE)
                {
                    // Do not need to synchronize for debug messages, and in high load situations the
                    // trace level will not be used anyway
                    for(final String nextEndpointUrl : this.getAccumulatedBlacklistStatistics().keySet())
                    {
                        BlacklistController.log
                                .trace("BlacklistController: going to expire blacklist entry for endpointUrl="
                                        + nextEndpointUrl);
                    }
                }
            }
            
            synchronized(this)
            {
                this.accumulatedBlacklistStatistics.clear();
                
                this.allCurrentBadQueries.clear();
                
                if(blacklistResetClientBlacklistWithEndpoints)
                {
                    this.currentQueryDebugInformation.clear();
                }
                
                this.setLastExpiryDate(new Date());
                
                neededToExpire = true;
            }
        }
        
        return neededToExpire;
    }
    
    /**
     * Evaluates the client blacklist using the properties from the settings object, or defaults if
     * the properties are not set.
     */
    public void evaluateClientBlacklist()
    {
        this.evaluateClientBlacklist(this.localSettings.getBooleanProperty("automaticallyBlacklistClients", false),
                this.localSettings.getIntProperty("blacklistMinimumQueriesBeforeBlacklistRules", 200),
                this.localSettings.getFloatProperty("blacklistPercentageOfRobotTxtQueriesBeforeAutomatic", 0.75f),
                this.localSettings.getIntProperty("blacklistClientMaxQueriesPerPeriod", 400));
    }
    
    /**
     * Evaluates the client blacklist using the given parameters
     * 
     * @param automaticallyBlacklistClients
     * @param blacklistMinimumQueriesBeforeBlacklistRules
     * @param blacklistPercentageOfRobotTxtQueriesBeforeAutomatic
     * @param blacklistClientMaxQueriesPerPeriod
     */
    public void evaluateClientBlacklist(final boolean automaticallyBlacklistClients,
            final int blacklistMinimumQueriesBeforeBlacklistRules,
            final float blacklistPercentageOfRobotTxtQueriesBeforeAutomatic,
            final int blacklistClientMaxQueriesPerPeriod)
    {
        if(automaticallyBlacklistClients)
        {
            for(final String nextKey : this.getCurrentQueryDebugInformation().keySet())
            {
                final Collection<QueryDebug> nextClientQueryList =
                        this.getCurrentQueryDebugInformation().get(nextKey);
                
                final int overallCount = nextClientQueryList.size();
                
                if(overallCount >= blacklistMinimumQueriesBeforeBlacklistRules)
                {
                    int robotsTxtCount = 0;
                    
                    for(final QueryDebug nextQueryDebug : nextClientQueryList)
                    {
                        for(final URI nextQueryDebugTitle : nextQueryDebug.getMatchingQueryTitles())
                        {
                            // TODO: add this property to the QueryDebug interface to avoid
                            // lookups here
                            final QueryType nextQueryDebugType =
                                    this.localSettings.getQueryType(nextQueryDebugTitle);
                            
                            if(nextQueryDebugType == null)
                            {
                                BlacklistController.log.warn("Could not find query, assuming it is in robots.txt nextQueryDebugType="+nextQueryDebugType);
                                robotsTxtCount++;
                                break;
                            }
                            else if(nextQueryDebugType.getInRobotsTxt())
                            {
                                if(BlacklistController._TRACE)
                                {
                                    BlacklistController.log
                                            .trace("BlacklistController: found query in robots.txt client="
                                                    + nextKey + " nextQueryDebugTitle=" + nextQueryDebugTitle);
                                }
                                
                                robotsTxtCount++;
                                break;
                            }
                        }
                    }
                    
                    final double robotsPercentage = robotsTxtCount * 1.0 / overallCount;
                    
                    if(BlacklistController._TRACE)
                    {
                        BlacklistController.log.trace("BlacklistController: results client=" + nextKey
                                + " robotsTxtCount=" + robotsTxtCount + " overallCount=" + overallCount
                                + " robotsPercentage=" + robotsPercentage);
                    }
                    
                    if(robotsPercentage >= blacklistPercentageOfRobotTxtQueriesBeforeAutomatic)
                    {
                        BlacklistController.log
                                .warn("BlacklistController: Found client performing too many robots.txt banned queries nextKey="
                                        + nextKey
                                        + " robotsTxtCount="
                                        + robotsTxtCount
                                        + " overallCount="
                                        + overallCount + " robotsPercentage=" + robotsPercentage);
                        
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
                            
                            this.permanentServletLifetimeIPBlacklist.add(nextKey);
                            this.permanentServletLifetimeIPBlacklistEvidence.put(nextKey, nextClientQueryList);
                            
                            
//                            BlacklistController.log
//                                    .warn("Did not properly add ip to the permanent blacklist: nextKey=" + nextKey);
                        }
                    }
                }
            }
        }
    }
    
    public Map<String, BlacklistEntry> getAccumulatedBlacklistStatistics()
    {
        return this.accumulatedBlacklistStatistics;
    }
    
    public Map<String, Map<Integer, Integer>> getAllHttpErrorResponseCodesByServer()
    {
        return this.allHttpErrorResponseCodesByServer;
    }
    
    public Map<String, Integer> getAllServerQueryTotals()
    {
        return this.allServerQueryTotals;
    }
    
    /**
     * 
     * @param blacklistedUrl
     * @param urlList
     * @return
     */
    public String getAlternativeUrl(final String blacklistedUrl, List<String> urlList)
    {
        if(blacklistedUrl == null || blacklistedUrl == "" || urlList == null)
        {
            BlacklistController.log.error("BlacklistController.getAlternativeUrl: something was wrong blacklistedUrl="
                    + blacklistedUrl + " urlList=" + urlList);
            
            return null;
        }
        
        // try to avoid always returning the same alternative by randomising the list structure prior to checking it here
        urlList = ListUtils.randomiseListLayout(urlList);
        
        for(final String nextEndpoint : urlList)
        {
            if(!nextEndpoint.equals(blacklistedUrl) && !this.isUrlBlacklisted(nextEndpoint))
            {
                return nextEndpoint;
            }
        }
        
        return null;
    }
    
    public Collection<QueryDebug> getCurrentDebugInformationFor(final String nextIpAddress)
    {
        if(this.getCurrentQueryDebugInformation().containsKey(nextIpAddress))
        {
            return this.getCurrentQueryDebugInformation().get(nextIpAddress);
        }
        else
        {
            return Collections.emptyList();
        }
    }
    
    public Collection<String> getCurrentIPBlacklist()
    {
        if(this.currentIPBlacklist == null)
        {
            synchronized(this)
            {
                if(this.currentIPBlacklist == null)
                {
                    this.currentIPBlacklist = this.localSettings.getStringProperties("blacklistBaseClientIPAddresses");
                }
            }
        }
        
        return this.currentIPBlacklist;
    }
    
    public Collection<String> getCurrentIPWhitelist()
    {
        if(this.currentIPWhitelist == null)
        {
            synchronized(this)
            {
                if(this.currentIPWhitelist == null)
                {
                    this.currentIPWhitelist = this.localSettings.getStringProperties("whitelistBaseClientIPAddresses");
                }
            }
        }
        
        return this.currentIPWhitelist;
    }
    
    public Map<String, Collection<QueryDebug>> getCurrentQueryDebugInformation()
    {
        return this.currentQueryDebugInformation;
    }
    
    public Collection<String> getEndpointUrlsInBlacklist()
    {
        final long blacklistResetPeriodMilliseconds =
                this.localSettings.getLongProperty("blacklistResetPeriodMilliseconds", 60000L);
        
        final boolean blacklistResetClientBlacklistWithEndpoints =
                this.localSettings.getBooleanProperty("blacklistResetClientBlacklistWithEndpoints", true);
        
        return getEndpointUrlsInBlacklist(blacklistResetPeriodMilliseconds, blacklistResetClientBlacklistWithEndpoints);
    }
    
    public Collection<String> getEndpointUrlsInBlacklist(final long blacklistResetPeriodMilliseconds,
            final boolean blacklistResetClientBlacklistWithEndpoints)
    {
        this.doBlacklistExpiry(blacklistResetPeriodMilliseconds, blacklistResetClientBlacklistWithEndpoints);
        
        final Collection<String> results = new HashSet<String>(this.getAccumulatedBlacklistStatistics().keySet());
        
        return results;
    }
    
    public Date getLastExpiryDate()
    {
        return this.lastExpiryDate;
    }
    
    public Date getLastServerStartupDate()
    {
        return this.lastServerStartupDate;
    }
    
    public Collection<String> getPermanentIPBlacklist()
    {
        if(this.permanentServletLifetimeIPBlacklist == null)
        {
            synchronized(this)
            {
                if(this.permanentServletLifetimeIPBlacklist == null)
                {
                    this.permanentServletLifetimeIPBlacklist = Collections.synchronizedSet(new HashSet<String>());
                }
            }
        }
        
        return this.permanentServletLifetimeIPBlacklist;
    }
    
    public boolean isClientBlacklisted(final String nextClientIPAddress)
    {
        if(this.isClientWhitelisted(nextClientIPAddress))
        {
            return false;
        }
        else
        {
            // TODO: enable range blacklisting and change the following code
            return (this.getPermanentIPBlacklist().contains(nextClientIPAddress) || this.getCurrentIPBlacklist()
                    .contains(nextClientIPAddress));
        }
    }
    
    public boolean isClientPermanentlyBlacklisted(final String nextClientIPAddress)
    {
        if(this.isClientWhitelisted(nextClientIPAddress))
        {
            return false;
        }
        else
        {
            // TODO: enable range blacklisting and change the following code
            return this.getPermanentIPBlacklist().contains(nextClientIPAddress);
        }
    }
    
    public boolean isClientWhitelisted(final String nextClientIPAddress)
    {
        return this.getCurrentIPWhitelist().contains(nextClientIPAddress);
    }
    
    /**
     * 
     * @param nextEndpointUrl The endpoint URL to check for failures
     * @return True if the endpoint has more than blacklistMaxAccumulatedFailures (5 by default)
     */
    public boolean isEndpointBlacklisted(final String nextEndpointUrl)
    {
        final int blacklistMaxAccumulatedFailures = this.localSettings.getIntProperty(
                "blacklistMaxAccumulatedFailures", 5);
        
        final long blacklistResetPeriodMilliseconds =
                this.localSettings.getLongProperty("blacklistResetPeriodMilliseconds", 60000L);
        
        final boolean blacklistResetClientBlacklistWithEndpoints =
                this.localSettings.getBooleanProperty("blacklistResetClientBlacklistWithEndpoints", true);
        
        return this.isEndpointBlacklisted(nextEndpointUrl, blacklistMaxAccumulatedFailures, blacklistResetPeriodMilliseconds, blacklistResetClientBlacklistWithEndpoints);
    }        
    
    /**
     * 
     * @param nextEndpointUrl The endpoint URL to check for failures
     * @param blacklistMaxAccumulatedFailures The number of failures to accept before blacklisting an endpoint
     * @param blacklistResetPeriodMilliseconds The minimum period between blacklist resets
     * @param blacklistResetClientBlacklistWithEndpoints Whether to reset the client blacklist when resetting the endpoint blacklist
     * @return True if the endpoint has more than blacklistMaxAccumulatedFailures
     */
    public boolean isEndpointBlacklisted(final String nextEndpointUrl, int blacklistMaxAccumulatedFailures, long blacklistResetPeriodMilliseconds, boolean blacklistResetClientBlacklistWithEndpoints)
    {
        this.doBlacklistExpiry(blacklistResetPeriodMilliseconds, blacklistResetClientBlacklistWithEndpoints);
        
        if(this.accumulatedBlacklistStatistics.containsKey(nextEndpointUrl))
        {
            final BlacklistEntry currentCount = this.accumulatedBlacklistStatistics.get(nextEndpointUrl);
            
            return (currentCount.numberOfFailures >= blacklistMaxAccumulatedFailures);
        }
        else
        {
            return false;
        }
    }
    
    /**
     * 
     * @param inputUrl The full URL to check
     * 
     * @return True if the complete inputUrl or the protocol://host subsection are blacklisted
     */
    public boolean isUrlBlacklisted(final String inputUrl)
    {
        final int blacklistMaxAccumulatedFailures = this.localSettings.getIntProperty(
                "blacklistMaxAccumulatedFailures", 5);
        
        final long blacklistResetPeriodMilliseconds =
                this.localSettings.getLongProperty("blacklistResetPeriodMilliseconds", 60000L);
        
        final boolean blacklistResetClientBlacklistWithEndpoints =
                this.localSettings.getBooleanProperty("blacklistResetClientBlacklistWithEndpoints", true);
        
        return isUrlBlacklisted(inputUrl, blacklistMaxAccumulatedFailures, blacklistResetPeriodMilliseconds, blacklistResetClientBlacklistWithEndpoints);
    }
    /**
     * 
     * @param inputUrl The full URL to check
     * 
     * @return True if the complete inputUrl or the protocol://host subsection are blacklisted
     */
    public boolean isUrlBlacklisted(final String inputUrl, int blacklistMaxAccumulatedFailures, long blacklistResetPeriodMilliseconds, boolean blacklistResetClientBlacklistWithEndpoints)
    {
        URL url = null;
        
        try
        {
            url = new URL(inputUrl);
        }
        catch(final Exception ex)
        {
            // ignore it, the endpoint doesn't always have to be a URL, we just won't consult the
            // blacklist controller in this case
            if(BlacklistController._DEBUG)
            {
                log.debug("isUrlBlacklisted: Endpoint was not a URL, returning false");
            }
            
            return false;
        }
        
        // we test both the full URL and the protocol://host subsection
        if(this.isEndpointBlacklisted(inputUrl, blacklistMaxAccumulatedFailures, blacklistResetPeriodMilliseconds, blacklistResetClientBlacklistWithEndpoints)
                || this.isEndpointBlacklisted(url.getProtocol() + "://" + url.getAuthority(), blacklistMaxAccumulatedFailures, blacklistResetPeriodMilliseconds, blacklistResetClientBlacklistWithEndpoints))
        {
            if(BlacklistController._DEBUG)
            {
                BlacklistController.log.debug("BlacklistController.isUrlBlacklisted: found blacklisted URL inputUrl="
                        + url + " simple form=" + url.getProtocol() + "://" + url.getAuthority());
            }
            
            return true;
        }
        
        return false;
    }
    
    public void persistStatistics(final Collection<StatisticsEntry> nextStatisticsEntries, final int modelVersion)
    {
        final long start = System.currentTimeMillis();
        
        this.clearStatisticsUploadList();
        
        final Collection<HttpUrlQueryRunnable> runnableThreads =
                Collections.synchronizedSet(new HashSet<HttpUrlQueryRunnable>());
        
        for(final StatisticsEntry nextStatisticsEntry : nextStatisticsEntries)
        {
            try
            {
                final HttpUrlQueryRunnable nextThread =
                        nextStatisticsEntry.generateThread(this.localSettings, this, modelVersion);
                nextThread.start();
                runnableThreads.add(nextThread);
            }
            catch(final OpenRDFException ordfe)
            {
                BlacklistController.log.error("BlacklistController.persistStatistics: exception found:", ordfe);
            }
        }
        
        if(BlacklistController._DEBUG)
        {
            BlacklistController.log
                    .debug("BlacklistController.persistStatistics: start of synchronized section start=" + start
                            + " internalStatisticsUploadList.size()=" + this.internalStatisticsUploadList.size());
        }
        
        for(final HttpUrlQueryRunnable nextRunnableThread : runnableThreads)
        {
            this.internalStatisticsUploadList.add(nextRunnableThread);
        }
        
        if(BlacklistController._DEBUG)
        {
            BlacklistController.log
                    .debug("BlacklistController.persistStatistics: end of synchronized section start=" + start
                            + " internalStatisticsUploadList.size()=" + this.internalStatisticsUploadList.size());
        }
        
        if(BlacklistController._DEBUG)
        {
            final long end = System.currentTimeMillis();
            
            BlacklistController.log.debug(String.format("%s: timing=%10d", "BlacklistController.persistStatistics",
                    (end - start)));
        }
        
        if(BlacklistController._TRACE)
        {
            BlacklistController.log.trace("BlacklistController.persistStatistics: returning...");
        }
    }
    
    public void removeEndpointsFromBlacklist(final Collection<RdfFetcherQueryRunnable> successfulQueries)
    {
        final long blacklistResetPeriodMilliseconds =
                this.localSettings.getLongProperty("blacklistResetPeriodMilliseconds", 60000L);
        
        final boolean blacklistResetClientBlacklistWithEndpoints =
                this.localSettings.getBooleanProperty("blacklistResetClientBlacklistWithEndpoints", true);
        
        removeEndpointsFromBlacklist(successfulQueries, blacklistResetPeriodMilliseconds, blacklistResetClientBlacklistWithEndpoints);        
    }
    
    /**
     * FIXME: fix this method so that it only removes successful endpoint URLs, as the runnable may have tried alternatives that failed
     * 
     * 
     * @param successfulQueries
     * @param blacklistResetPeriodMilliseconds
     * @param blacklistResetClientBlacklistWithEndpoints
     */
    public void removeEndpointsFromBlacklist(final Collection<RdfFetcherQueryRunnable> successfulQueries,
            final long blacklistResetPeriodMilliseconds, final boolean blacklistResetClientBlacklistWithEndpoints)
    {
        this.doBlacklistExpiry(blacklistResetPeriodMilliseconds, blacklistResetClientBlacklistWithEndpoints);
        
        for(final RdfFetcherQueryRunnable nextQueryObject : successfulQueries)
        {
            // only deal with it if it was blacklisted
            this.accumulatedBlacklistStatistics.remove(nextQueryObject.getEndpointUrl());
        }
    }
    
    public void setLastExpiryDate(final Date lastExpiryDate)
    {
        this.lastExpiryDate = lastExpiryDate;
    }
    
    public void setLastServerStartupDate(final Date lastServerStartupDate)
    {
        this.lastServerStartupDate = lastServerStartupDate;
    }
    
}
