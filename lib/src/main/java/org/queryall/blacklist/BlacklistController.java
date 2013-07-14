package org.queryall.blacklist;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.utils.WebappConfig;
import org.queryall.query.HttpUrlQueryRunnableImpl;
import org.queryall.query.QueryDebug;
import org.queryall.query.RdfFetcherQueryRunnable;
import org.queryall.statistics.StatisticsEntry;
import org.queryall.utils.ListUtils;
import org.queryall.utils.SettingsFactory;
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
        public static final BlacklistController helper = new BlacklistController(SettingsFactory.generateSettings());
    }
    
    private static final Logger log = LoggerFactory.getLogger(BlacklistController.class);
    private static final boolean TRACE = BlacklistController.log.isTraceEnabled();
    private static final boolean DEBUG = BlacklistController.log.isDebugEnabled();
    private static final boolean INFO = BlacklistController.log.isInfoEnabled();
    
    /**
     * @return the defaultController
     */
    public static BlacklistController getDefaultController()
    {
        return BlacklistControllerHolder.helper;
    }
    
    private final ConcurrentMap<String, BlacklistEntry> accumulatedBlacklistStatistics =
            new ConcurrentHashMap<String, BlacklistEntry>(200);
    
    private final ConcurrentMap<String, ConcurrentHashMap<Integer, AtomicInteger>> allHttpErrorResponseCodesByServer =
            new ConcurrentHashMap<String, ConcurrentHashMap<Integer, AtomicInteger>>(200);
    
    private final ConcurrentMap<String, AtomicInteger> allServerQueryTotals =
            new ConcurrentHashMap<String, AtomicInteger>(200);
    
    // private final Collection<RdfFetcherQueryRunnable> allCurrentBadQueries =
    // Collections
    // .synchronizedList(new ArrayList<RdfFetcherQueryRunnable>(200));
    
    private final ConcurrentMap<String, Collection<QueryDebug>> currentQueryDebugInformation =
            new ConcurrentHashMap<String, Collection<QueryDebug>>(200);
    
    private volatile Collection<String> currentIPBlacklist = null;
    
    private final Collection<String> permanentServletLifetimeIPBlacklist = Collections
            .synchronizedList(new ArrayList<String>(200));
    
    private final ConcurrentMap<String, Collection<QueryDebug>> permanentServletLifetimeIPBlacklistEvidence =
            new ConcurrentHashMap<String, Collection<QueryDebug>>(200);
    
    private volatile Collection<String> currentIPWhitelist = null;
    
    private final Collection<HttpUrlQueryRunnableImpl> internalStatisticsUploadList = Collections
            .synchronizedList(new ArrayList<HttpUrlQueryRunnableImpl>());
    
    private volatile Date lastServerStartupDate = new Date();
    
    private volatile Date lastExpiryDate = new Date();
    
    private final QueryAllConfiguration localSettings;
    
    public BlacklistController(final QueryAllConfiguration queryAllConfiguration)
    {
        this.localSettings = queryAllConfiguration;
        this.currentIPBlacklist =
                Collections.synchronizedList(new ArrayList<String>(queryAllConfiguration
                        .getStrings(WebappConfig.BLACKLIST_BASE_CLIENT_IP_ADDRESSES)));
        this.currentIPWhitelist =
                Collections.synchronizedList(new ArrayList<String>(queryAllConfiguration
                        .getStrings(WebappConfig.WHITELIST_BASE_CLIENT_IP_ADDRESSES)));
    }
    
    public void accumulateBlacklist(final Collection<RdfFetcherQueryRunnable> temporaryEndpointBlacklist)
    {
        this.doBlacklistExpiry();
        
        for(final RdfFetcherQueryRunnable nextQueryObject : temporaryEndpointBlacklist)
        {
            if(BlacklistController.DEBUG)
            {
                BlacklistController.log
                        .debug("BlacklistController.accumulateBlacklist: going to accumulate entry for endpointUrl="
                                + nextQueryObject.getActualEndpointUrl());
            }
            
            if(nextQueryObject.getActualEndpointUrl() != null)
            {
                if(this.accumulatedBlacklistStatistics.containsKey(nextQueryObject.getActualEndpointUrl()))
                {
                    final BlacklistEntry previousCount =
                            this.getAccumulatedBlacklistStatistics().get(nextQueryObject.getActualEndpointUrl());
                    
                    if(BlacklistController.DEBUG)
                    {
                        BlacklistController.log.debug("BlacklistController.accumulateBlacklist: There were "
                                + previousCount + " previous instances on blacklist for endpointUrl="
                                + nextQueryObject.getActualEndpointUrl());
                    }
                    
                    previousCount.addErrorMessageForRunnable(nextQueryObject);
                    
                    this.accumulatedBlacklistStatistics.put(nextQueryObject.getActualEndpointUrl(), previousCount);
                }
                else
                {
                    final BlacklistEntry newFailureCount = new BlacklistEntry();
                    newFailureCount.endpointUrl = nextQueryObject.getActualEndpointUrl();
                    newFailureCount.addErrorMessageForRunnable(nextQueryObject);
                    
                    this.accumulatedBlacklistStatistics.put(nextQueryObject.getActualEndpointUrl(), newFailureCount);
                }
                
                // we should not be holding onto these items
                // this.allCurrentBadQueries.add(nextQueryObject);
            }
        }
    }
    
    /**
     * NOTE: This method checks for the keys in the error maps to avoid creating new potentially
     * temporary ConcurrentHashMap instances unless it is clear they may be needed.
     * 
     * @param endpointUrl
     * @param errorResponseCode
     */
    public void accumulateHttpResponseError(final String endpointUrl, final int errorResponseCode)
    {
        // Do not blacklist endpoint for a 400 query as it may just be a
        // misconfigured query type that is not a symptom of the endpoint being
        // down
        // https://github.com/ansell/queryall/issues/24
        if(errorResponseCode != 400)
        {
            // if it contains the endpoint URL already then we rely on the
            // ConcurrentHashMap
            // capabilities without synchronization
            // If the key is already in the map we avoid creating at least one
            // map
            if(this.allHttpErrorResponseCodesByServer.containsKey(endpointUrl))
            {
                ConcurrentHashMap<Integer, AtomicInteger> nextErrorList =
                        this.allHttpErrorResponseCodesByServer.get(endpointUrl);
                
                // if the error code is already present we avoid creating a
                // second map
                if(nextErrorList.containsKey(errorResponseCode))
                {
                    nextErrorList.get(errorResponseCode).incrementAndGet();
                }
                else
                {
                    nextErrorList = new ConcurrentHashMap<Integer, AtomicInteger>();
                    nextErrorList.put(errorResponseCode, new AtomicInteger(1));
                    final ConcurrentHashMap<Integer, AtomicInteger> putIfAbsent =
                            this.allHttpErrorResponseCodesByServer.putIfAbsent(endpointUrl, nextErrorList);
                    
                    // if someone else put the key in while we were creating our
                    // map, then we simply
                    // increment the AtomicInteger on their map
                    if(putIfAbsent != null)
                    {
                        final AtomicInteger putIfAbsent2 =
                                putIfAbsent.putIfAbsent(errorResponseCode, new AtomicInteger(1));
                        
                        if(putIfAbsent2 != null)
                        {
                            putIfAbsent2.incrementAndGet();
                        }
                    }
                }
            }
            else
            {
                final ConcurrentHashMap<Integer, AtomicInteger> nextErrorList =
                        new ConcurrentHashMap<Integer, AtomicInteger>();
                nextErrorList.put(errorResponseCode, new AtomicInteger(1));
                final ConcurrentHashMap<Integer, AtomicInteger> putIfAbsent =
                        this.allHttpErrorResponseCodesByServer.putIfAbsent(endpointUrl, nextErrorList);
                
                if(putIfAbsent != null)
                {
                    // if the error code is already present we avoid creating a
                    // second map
                    if(putIfAbsent.containsKey(errorResponseCode))
                    {
                        putIfAbsent.get(errorResponseCode).incrementAndGet();
                    }
                    else
                    {
                        final AtomicInteger putIfAbsent2 =
                                putIfAbsent.putIfAbsent(errorResponseCode, new AtomicInteger(1));
                        
                        if(putIfAbsent2 != null)
                        {
                            putIfAbsent2.incrementAndGet();
                        }
                    }
                }
            }
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
        this.accumulateQueryDebug(nextQueryObject, this.localSettings.getLong(
                WebappConfig.BLACKLIST_RESET_PERIOD_MILLISECONDS,
                (Long)WebappConfig.BLACKLIST_RESET_PERIOD_MILLISECONDS.getDefaultValue()), this.localSettings
                .getBoolean(WebappConfig.BLACKLIST_RESET_CLIENT_BLACKLIST_WITH_ENDPOINTS,
                        (Boolean)WebappConfig.BLACKLIST_RESET_CLIENT_BLACKLIST_WITH_ENDPOINTS.getDefaultValue()),
                this.localSettings.getBoolean(WebappConfig.BLACKLIST_AUTOMATICALLY_BLACKLIST_CLIENTS,
                        (Boolean)WebappConfig.BLACKLIST_AUTOMATICALLY_BLACKLIST_CLIENTS.getDefaultValue()),
                this.localSettings.getInt(WebappConfig.BLACKLIST_MINIMUM_QUERIES_BEFORE_BLACKLIST_RULES,
                        (Integer)WebappConfig.BLACKLIST_MINIMUM_QUERIES_BEFORE_BLACKLIST_RULES.getDefaultValue()),
                this.localSettings.getInt(WebappConfig.BLACKLIST_CLIENT_MAX_QUERIES_PER_PERIOD,
                        (Integer)WebappConfig.BLACKLIST_CLIENT_MAX_QUERIES_PER_PERIOD.getDefaultValue()));
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
            if(BlacklistController.DEBUG)
            {
                BlacklistController.log
                        .debug("BlacklistController.accumulateQueryDebug: going to accumulate entry for clientIPAddress="
                                + nextQueryObject.getClientIPAddress());
            }
            
            this.doBlacklistExpiry(blacklistResetPeriodMilliseconds, blacklistResetClientBlacklistWithEndpoints);
            
            final Collection<QueryDebug> putIfAbsent =
                    this.currentQueryDebugInformation.putIfAbsent(nextQueryObject.getClientIPAddress(),
                            new ArrayList<QueryDebug>(Collections.singletonList(nextQueryObject)));
            
            // if there was already an entry, then it will be returned and we
            // need to update that
            // list instead
            if(putIfAbsent != null)
            {
                putIfAbsent.add(nextQueryObject);
            }
            
            this.evaluateClientBlacklist(automaticallyBlacklistClients, blacklistMinimumQueriesBeforeBlacklistRules,
                    blacklistResetPeriodMilliseconds, blacklistClientMaxQueriesPerPeriod);
        }
    }
    
    public void accumulateQueryTotal(final String endpointUrl)
    {
        final AtomicInteger putIfAbsent = this.allServerQueryTotals.putIfAbsent(endpointUrl, new AtomicInteger(1));
        
        if(putIfAbsent != null)
        {
            putIfAbsent.incrementAndGet();
        }
    }
    
    public int clearStatisticsUploadList()
    {
        final long start = System.currentTimeMillis();
        
        int numberRemoved = 0;
        
        synchronized(this)
        {
            if(BlacklistController.DEBUG)
            {
                BlacklistController.log
                        .debug("BlacklistController.clearStatisticsUploadList: start of synchronized section start="
                                + start + " internalStatisticsUploadList.size()="
                                + this.internalStatisticsUploadList.size());
            }
            
            final Collection<HttpUrlQueryRunnableImpl> completedThreads = new ArrayList<HttpUrlQueryRunnableImpl>();
            
            for(final HttpUrlQueryRunnableImpl nextThread : this.internalStatisticsUploadList)
            {
                if(nextThread.getCompleted())
                {
                    completedThreads.add(nextThread);
                }
            }
            
            for(final HttpUrlQueryRunnableImpl nextThread : completedThreads)
            {
                if(nextThread == null)
                {
                    continue;
                }
                
                if(!nextThread.getWasSuccessful())
                {
                    BlacklistController.log
                            .error("BlacklistController: found error while clearing completed statistics threads");
                    if(BlacklistController.DEBUG)
                    {
                        BlacklistController.log.debug(nextThread.getLastException().getMessage());
                    }
                }
                
                this.internalStatisticsUploadList.remove(nextThread);
                numberRemoved++;
            }
            
            if(BlacklistController.DEBUG)
            {
                BlacklistController.log
                        .debug("BlacklistController.clearStatisticsUploadList: end of synchronized section start="
                                + start + " numberRemoved=" + numberRemoved + " internalStatisticsUploadList.size()="
                                + this.internalStatisticsUploadList.size());
            }
        }
        
        if(BlacklistController.DEBUG)
        {
            final long end = System.currentTimeMillis();
            
            BlacklistController.log.debug(String.format("%s: timing=%10d",
                    "BlacklistController.clearStatisticsUploadList", (end - start)));
        }
        
        if(BlacklistController.TRACE)
        {
            BlacklistController.log.trace("BlacklistController.clearStatisticsUploadList: returning...");
        }
        
        return numberRemoved;
    }
    
    /**
     * This method checks for the expiry of the blacklist and clears the blacklist if it has
     * expired, and returns true if it has expired since last time, even if there were no errors
     * since then
     */
    public boolean doBlacklistExpiry()
    {
        final long blacklistResetPeriodMilliseconds =
                this.localSettings.getLong(WebappConfig.BLACKLIST_RESET_PERIOD_MILLISECONDS,
                        (Long)WebappConfig.BLACKLIST_RESET_PERIOD_MILLISECONDS.getDefaultValue());
        
        final boolean blacklistResetClientBlacklistWithEndpoints =
                this.localSettings.getBoolean(WebappConfig.BLACKLIST_RESET_CLIENT_BLACKLIST_WITH_ENDPOINTS,
                        (Boolean)WebappConfig.BLACKLIST_RESET_CLIENT_BLACKLIST_WITH_ENDPOINTS.getDefaultValue());
        
        return this.doBlacklistExpiry(blacklistResetPeriodMilliseconds, blacklistResetClientBlacklistWithEndpoints);
    }
    
    /**
     * 
     * @param blacklistResetPeriodMilliseconds
     *            The period of time between resets to the blacklist. If it is less than or equal to
     *            0 (ie, <= 0), then no resets will ever occur
     * @param blacklistResetClientBlacklistWithEndpoints
     *            True if the client blacklist should be reset along with endpoints
     * @return True if the blacklist had expired and was reset, and false otherwise
     */
    public boolean doBlacklistExpiry(final long blacklistResetPeriodMilliseconds,
            final boolean blacklistResetClientBlacklistWithEndpoints)
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
            if(BlacklistController.INFO)
            {
                BlacklistController.log.info("BlacklistController: needToExpire");
                
                if(BlacklistController.TRACE)
                {
                    // Do not need to synchronize for debug messages, and in
                    // high load situations
                    // the trace level will not be used anyway
                    for(final String nextEndpointUrl : this.getAccumulatedBlacklistStatistics().keySet())
                    {
                        BlacklistController.log
                                .trace("BlacklistController: going to expire blacklist entry for endpointUrl="
                                        + nextEndpointUrl);
                    }
                }
            }
            
            this.accumulatedBlacklistStatistics.clear();
            
            // this.allCurrentBadQueries.clear();
            
            if(blacklistResetClientBlacklistWithEndpoints)
            {
                this.currentQueryDebugInformation.clear();
                // Note: We keep the ban, but remove the evidence here to save
                // memory
                this.permanentServletLifetimeIPBlacklistEvidence.clear();
            }
            
            this.setLastExpiryDate(new Date());
            
            neededToExpire = true;
        }
        
        return neededToExpire;
    }
    
    /**
     * Evaluates the client blacklist using the properties from the settings object, or defaults if
     * the properties are not set.
     */
    public void evaluateClientBlacklist()
    {
        this.evaluateClientBlacklist(this.localSettings.getBoolean(
                WebappConfig.BLACKLIST_AUTOMATICALLY_BLACKLIST_CLIENTS,
                (Boolean)WebappConfig.BLACKLIST_AUTOMATICALLY_BLACKLIST_CLIENTS.getDefaultValue()), this.localSettings
                .getInt(WebappConfig.BLACKLIST_MINIMUM_QUERIES_BEFORE_BLACKLIST_RULES,
                        (Integer)WebappConfig.BLACKLIST_MINIMUM_QUERIES_BEFORE_BLACKLIST_RULES.getDefaultValue()),
                this.localSettings.getFloat(WebappConfig.BLACKLIST_ROBOTS_TXT_PERCENTAGE,
                        (Float)WebappConfig.BLACKLIST_ROBOTS_TXT_PERCENTAGE.getDefaultValue()), this.localSettings
                        .getInt(WebappConfig.BLACKLIST_CLIENT_MAX_QUERIES_PER_PERIOD,
                                (Integer)WebappConfig.BLACKLIST_CLIENT_MAX_QUERIES_PER_PERIOD.getDefaultValue()));
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
                final Collection<QueryDebug> nextClientQueryList = this.getCurrentQueryDebugInformation().get(nextKey);
                
                final int overallCount = nextClientQueryList.size();
                
                if(overallCount >= blacklistMinimumQueriesBeforeBlacklistRules)
                {
                    int robotsTxtCount = 0;
                    
                    for(final QueryDebug nextQueryDebug : nextClientQueryList)
                    {
                        for(final URI nextQueryDebugTitle : nextQueryDebug.getMatchingQueryTitles())
                        {
                            // TODO: add this property to the QueryDebug
                            // interface to avoid
                            // lookups here
                            final QueryType nextQueryDebugType = this.localSettings.getQueryType(nextQueryDebugTitle);
                            
                            if(nextQueryDebugType == null)
                            {
                                BlacklistController.log
                                        .warn("Could not find query, assuming it is in robots.txt nextQueryDebugType="
                                                + nextQueryDebugType);
                                robotsTxtCount++;
                                break;
                            }
                            else if(nextQueryDebugType.getInRobotsTxt())
                            {
                                if(BlacklistController.TRACE)
                                {
                                    BlacklistController.log
                                            .trace("BlacklistController: found query in robots.txt client=" + nextKey
                                                    + " nextQueryDebugTitle=" + nextQueryDebugTitle);
                                }
                                
                                robotsTxtCount++;
                                break;
                            }
                        }
                    }
                    
                    final double robotsPercentage = robotsTxtCount * 1.0 / overallCount;
                    
                    if(BlacklistController.TRACE)
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
                            
                            // BlacklistController.log
                            // .warn("Did not properly add ip to the permanent blacklist: nextKey="
                            // + nextKey);
                        }
                    }
                }
            }
        }
    }
    
    public ConcurrentMap<String, BlacklistEntry> getAccumulatedBlacklistStatistics()
    {
        return this.accumulatedBlacklistStatistics;
    }
    
    public ConcurrentMap<String, ConcurrentHashMap<Integer, AtomicInteger>> getAllHttpErrorResponseCodesByServer()
    {
        return this.allHttpErrorResponseCodesByServer;
    }
    
    public ConcurrentMap<String, AtomicInteger> getAllServerQueryTotals()
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
        
        // try to avoid always returning the same alternative by randomising the
        // list structure
        // prior to checking it here
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
        final Collection<QueryDebug> results = this.getCurrentQueryDebugInformation().get(nextIpAddress);
        
        if(results == null)
        {
            return Collections.emptyList();
        }
        else
        {
            return results;
        }
    }
    
    public Collection<String> getCurrentIPBlacklist()
    {
        // lazy initialisation for this to give a chance for settings to be
        // populated
        if(this.currentIPBlacklist == null)
        {
            synchronized(this)
            {
                if(this.currentIPBlacklist == null)
                {
                    this.currentIPBlacklist =
                            this.localSettings.getStrings(WebappConfig.BLACKLIST_BASE_CLIENT_IP_ADDRESSES);
                }
            }
        }
        
        return this.currentIPBlacklist;
    }
    
    public Collection<String> getCurrentIPWhitelist()
    {
        // lazy initialisation for this to give a chance for settings to be
        // populated
        if(this.currentIPWhitelist == null)
        {
            synchronized(this)
            {
                if(this.currentIPWhitelist == null)
                {
                    this.currentIPWhitelist =
                            this.localSettings.getStrings(WebappConfig.WHITELIST_BASE_CLIENT_IP_ADDRESSES);
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
                this.localSettings.getLong(WebappConfig.BLACKLIST_RESET_PERIOD_MILLISECONDS,
                        (Long)WebappConfig.BLACKLIST_RESET_PERIOD_MILLISECONDS.getDefaultValue());
        
        final boolean blacklistResetClientBlacklistWithEndpoints =
                this.localSettings.getBoolean(WebappConfig.BLACKLIST_RESET_CLIENT_BLACKLIST_WITH_ENDPOINTS,
                        (Boolean)WebappConfig.BLACKLIST_RESET_CLIENT_BLACKLIST_WITH_ENDPOINTS.getDefaultValue());
        
        return this.getEndpointUrlsInBlacklist(blacklistResetPeriodMilliseconds,
                blacklistResetClientBlacklistWithEndpoints);
    }
    
    public Collection<String> getEndpointUrlsInBlacklist(final long blacklistResetPeriodMilliseconds,
            final boolean blacklistResetClientBlacklistWithEndpoints)
    {
        this.doBlacklistExpiry(blacklistResetPeriodMilliseconds, blacklistResetClientBlacklistWithEndpoints);
        
        return Collections.unmodifiableSet(this.getAccumulatedBlacklistStatistics().keySet());
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
        if(nextClientIPAddress == null)
        {
            return false;
        }
        
        return this.getCurrentIPWhitelist().contains(nextClientIPAddress);
    }
    
    /**
     * 
     * @param nextEndpointUrl
     *            The endpoint URL to check for failures
     * @return True if the endpoint has more than blacklistMaxAccumulatedFailures (5 by default)
     */
    public boolean isEndpointBlacklisted(final String nextEndpointUrl)
    {
        if(nextEndpointUrl == null)
        {
            return false;
        }
        
        final int blacklistMaxAccumulatedFailures =
                this.localSettings.getInt(WebappConfig.BLACKLIST_MAX_ACCUMULATED_FAILURES,
                        (Integer)WebappConfig.BLACKLIST_MAX_ACCUMULATED_FAILURES.getDefaultValue());
        
        final long blacklistResetPeriodMilliseconds =
                this.localSettings.getLong(WebappConfig.BLACKLIST_RESET_PERIOD_MILLISECONDS,
                        (Long)WebappConfig.BLACKLIST_RESET_PERIOD_MILLISECONDS.getDefaultValue());
        
        final boolean blacklistResetClientBlacklistWithEndpoints =
                this.localSettings.getBoolean(WebappConfig.BLACKLIST_RESET_CLIENT_BLACKLIST_WITH_ENDPOINTS,
                        (Boolean)WebappConfig.BLACKLIST_RESET_CLIENT_BLACKLIST_WITH_ENDPOINTS.getDefaultValue());
        
        return this.isEndpointBlacklisted(nextEndpointUrl, blacklistMaxAccumulatedFailures,
                blacklistResetPeriodMilliseconds, blacklistResetClientBlacklistWithEndpoints);
    }
    
    /**
     * 
     * @param nextEndpointUrl
     *            The endpoint URL to check for failures
     * @param blacklistMaxAccumulatedFailures
     *            The number of failures to accept before blacklisting an endpoint
     * @param blacklistResetPeriodMilliseconds
     *            The minimum period between blacklist resets
     * @param blacklistResetClientBlacklistWithEndpoints
     *            Whether to reset the client blacklist when resetting the endpoint blacklist
     * @return True if the endpoint has more than blacklistMaxAccumulatedFailures
     */
    public boolean isEndpointBlacklisted(final String nextEndpointUrl, final int blacklistMaxAccumulatedFailures,
            final long blacklistResetPeriodMilliseconds, final boolean blacklistResetClientBlacklistWithEndpoints)
    {
        if(nextEndpointUrl == null)
        {
            return false;
        }
        
        this.doBlacklistExpiry(blacklistResetPeriodMilliseconds, blacklistResetClientBlacklistWithEndpoints);
        
        if(this.accumulatedBlacklistStatistics.containsKey(nextEndpointUrl))
        {
            final BlacklistEntry currentCount = this.accumulatedBlacklistStatistics.get(nextEndpointUrl);
            
            return (currentCount.numberOfFailures.intValue() >= blacklistMaxAccumulatedFailures);
        }
        else
        {
            return false;
        }
    }
    
    /**
     * 
     * NOTE: If inputUrl is null, this method will always return false.
     * 
     * @param inputUrl
     *            The full URL to check
     * 
     * @return True if the complete inputUrl or the protocol://host subsection are blacklisted
     */
    public boolean isUrlBlacklisted(final String inputUrl)
    {
        if(inputUrl == null)
        {
            return false;
        }
        
        final int blacklistMaxAccumulatedFailures =
                this.localSettings.getInt(WebappConfig.BLACKLIST_MAX_ACCUMULATED_FAILURES,
                        (Integer)WebappConfig.BLACKLIST_MAX_ACCUMULATED_FAILURES.getDefaultValue());
        
        final long blacklistResetPeriodMilliseconds =
                this.localSettings.getLong(WebappConfig.BLACKLIST_RESET_PERIOD_MILLISECONDS,
                        (Long)WebappConfig.BLACKLIST_RESET_PERIOD_MILLISECONDS.getDefaultValue());
        
        final boolean blacklistResetClientBlacklistWithEndpoints =
                this.localSettings.getBoolean(WebappConfig.BLACKLIST_RESET_CLIENT_BLACKLIST_WITH_ENDPOINTS,
                        (Boolean)WebappConfig.BLACKLIST_RESET_CLIENT_BLACKLIST_WITH_ENDPOINTS.getDefaultValue());
        
        return this.isUrlBlacklisted(inputUrl, blacklistMaxAccumulatedFailures, blacklistResetPeriodMilliseconds,
                blacklistResetClientBlacklistWithEndpoints);
    }
    
    /**
     * 
     * @param inputUrl
     *            The full URL to check
     * 
     * @return True if the complete inputUrl or the protocol://host subsection are blacklisted
     */
    public boolean isUrlBlacklisted(final String inputUrl, final int blacklistMaxAccumulatedFailures,
            final long blacklistResetPeriodMilliseconds, final boolean blacklistResetClientBlacklistWithEndpoints)
    {
        if(inputUrl == null)
        {
            return false;
        }
        
        URL url = null;
        
        try
        {
            url = new URL(inputUrl);
        }
        catch(final Exception ex)
        {
            // ignore it, the endpoint doesn't always have to be a URL, we just
            // won't consult the
            // blacklist controller in this case
            if(BlacklistController.DEBUG)
            {
                BlacklistController.log.debug("isUrlBlacklisted: Endpoint was not a URL, returning false");
            }
            
            return false;
        }
        
        // we test both the full URL and the protocol://host subsection
        if(this.isEndpointBlacklisted(inputUrl, blacklistMaxAccumulatedFailures, blacklistResetPeriodMilliseconds,
                blacklistResetClientBlacklistWithEndpoints)
                || this.isEndpointBlacklisted(url.getProtocol() + "://" + url.getAuthority(),
                        blacklistMaxAccumulatedFailures, blacklistResetPeriodMilliseconds,
                        blacklistResetClientBlacklistWithEndpoints))
        {
            if(BlacklistController.DEBUG)
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
        
        final Collection<HttpUrlQueryRunnableImpl> runnableThreads =
                new ArrayList<HttpUrlQueryRunnableImpl>(nextStatisticsEntries.size());
        
        for(final StatisticsEntry nextStatisticsEntry : nextStatisticsEntries)
        {
            try
            {
                final HttpUrlQueryRunnableImpl nextThread =
                        nextStatisticsEntry.generateThread(this.localSettings, this, modelVersion);
                nextThread.start();
                runnableThreads.add(nextThread);
            }
            catch(final OpenRDFException ordfe)
            {
                BlacklistController.log.error("BlacklistController.persistStatistics: exception found:", ordfe);
            }
        }
        
        if(BlacklistController.DEBUG)
        {
            BlacklistController.log.debug("BlacklistController.persistStatistics: start of synchronized section start="
                    + start + " internalStatisticsUploadList.size()=" + this.internalStatisticsUploadList.size());
        }
        
        this.internalStatisticsUploadList.addAll(runnableThreads);
        
        if(BlacklistController.DEBUG)
        {
            BlacklistController.log.debug("BlacklistController.persistStatistics: end of synchronized section start="
                    + start + " internalStatisticsUploadList.size()=" + this.internalStatisticsUploadList.size());
        }
        
        if(BlacklistController.DEBUG)
        {
            final long end = System.currentTimeMillis();
            
            BlacklistController.log.debug(String.format("%s: timing=%10d", "BlacklistController.persistStatistics",
                    (end - start)));
        }
        
        if(BlacklistController.TRACE)
        {
            BlacklistController.log.trace("BlacklistController.persistStatistics: returning...");
        }
    }
    
    public void removeEndpointsFromBlacklist(final Collection<RdfFetcherQueryRunnable> successfulQueries)
    {
        final long blacklistResetPeriodMilliseconds =
                this.localSettings.getLong(WebappConfig.BLACKLIST_RESET_PERIOD_MILLISECONDS,
                        (Long)WebappConfig.BLACKLIST_RESET_PERIOD_MILLISECONDS.getDefaultValue());
        
        final boolean blacklistResetClientBlacklistWithEndpoints =
                this.localSettings.getBoolean(WebappConfig.BLACKLIST_RESET_CLIENT_BLACKLIST_WITH_ENDPOINTS,
                        (Boolean)WebappConfig.BLACKLIST_RESET_CLIENT_BLACKLIST_WITH_ENDPOINTS.getDefaultValue());
        
        this.removeEndpointsFromBlacklist(successfulQueries, blacklistResetPeriodMilliseconds,
                blacklistResetClientBlacklistWithEndpoints);
    }
    
    /**
     * FIXME: fix this method so that it only removes successful endpoint URLs, as the runnable may
     * have tried alternatives that failed
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
            this.accumulatedBlacklistStatistics.remove(nextQueryObject.getActualEndpointUrl());
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
