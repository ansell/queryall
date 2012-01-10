/**
 * 
 */
package org.queryall.servlets.statistics;

/**
 * @author uqpanse1
 * 
 */
public class StatisticsHelper
{
    
    /**
	 * 
	 */
    public StatisticsHelper()
    {
        // TODO Auto-generated constructor stub
    }
    
    // if(localSettings.SUBMIT_USAGE_STATISTICS &&
    // !localSettings.getBooleanPropertyFromConfig("statisticsSubmitStatistics"))
    // {
    // Collection<StatisticsEntry> statisticsEntryList = new HashSet<StatisticsEntry>();
    //
    // Collection<String> profileUris = new HashSet<String>();
    //
    // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_PROFILES))
    // {
    // profileUris = localSettings.getStringCollectionPropertiesFromConfig("activeProfiles");
    // }
    //
    // Collection<String> configLocations = new HashSet<String>();
    //
    // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_CONFIGLOCATIONS))
    // {
    // configLocations = localSettings.CONFIG_LOCATION_LIST;
    // }
    //
    // Collection<String> querytypeUris = new HashSet<String>();
    //
    // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_QUERYTYPES))
    // {
    // // TODO: decide whether we only want the final matching query types or all of the initial
    // match query types
    // // for(QueryType nextRelevantQuery : relevantCustomQueries)
    // // querytypeUris.add(nextRelevantQuery.getKey());
    //
    // for(QueryBundle nextInitialQueryBundle : multiProviderQueryBundles)
    // querytypeUris.add(nextInitialQueryBundle.getQueryType().getKey());
    // }
    //
    // // TODO: organise how to get this information out effectively, hopefully without doing the
    // regular expression matching again
    // Collection<String> namespaceUris = new HashSet<String>();
    //
    // String configVersion = "";
    //
    // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_CONFIGVERSION))
    // {
    // configVersion = localSettings.getSettings().CONFIG_API_VERSION+"";
    // }
    //
    // int readtimeout = 0;
    //
    // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_READTIMEOUT))
    // {
    // readtimeout = localSettings.getIntPropertyFromConfig("readTimeout");
    // }
    //
    // int connecttimeout = 0;
    //
    // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_READTIMEOUT))
    // {
    // connecttimeout = localSettings.getIntPropertyFromConfig("connectTimeout");
    // }
    //
    // String userHostAddress = "";
    //
    // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_USERHOSTADDRESS))
    // {
    // userHostAddress = requesterIpAddress;
    // }
    //
    // String userAgent = "";
    //
    // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_USERAGENT))
    // {
    // userAgent = userAgentHeader;
    // }
    //
    // String statisticsRealHostName = "";
    //
    // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_REALHOSTNAME))
    // {
    // statisticsRealHostName = realHostName;
    // }
    //
    // String statisticsQueryString = "";
    //
    // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_QUERYSTRING))
    // {
    // statisticsQueryString = queryString;
    // }
    //
    // long responseTime = -1;
    //
    // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_RESPONSETIME))
    // {
    // responseTime = nextTotalTime;
    // }
    //
    // // double[] testArray = new double[5];
    // //
    // // testArray[0] = 13.0;
    // // testArray[1] = 23.0;
    // // testArray[2] = 12.0;
    // // testArray[3] = 44.0;
    // // testArray[4] = 55.0;
    // //
    // // log.info("test standard deviation = "+ localSettings.getStandardDeviation(testArray));
    //
    // Collection<Long> nonErrorLatencyList = new HashSet<Long>();
    // Collection<Long> errorLatencyList = new HashSet<Long>();
    //
    // Collection<String> successfulProviderUris = new HashSet<String>();
    // Collection<String> errorProviderUris = new HashSet<String>();
    //
    // long sumLatency = 0;
    // long sumErrorLatency = 0;
    // long nextLatency = 0;
    //
    // for(RdfFetcherQueryRunnable nextResult : fetchController.getResults())
    // {
    // nextLatency = nextResult.queryEndTime.getTime()-nextResult.queryStartTime.getTime();
    //
    // if(nextResult.wasSuccessful)
    // {
    // sumLatency += nextLatency;
    // nonErrorLatencyList.add(nextLatency);
    //
    // if(nextResult.endpointUrl != null && !nextResult.endpointUrl.trim().equals(""))
    // {
    // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_SUCCESSFULPROVIDERS))
    // {
    // successfulProviderUris.add(nextResult.endpointUrl);
    // }
    // }
    // }
    // else
    // {
    // sumErrorLatency += nextLatency;
    // errorLatencyList.add(nextLatency);
    //
    // if(nextResult.endpointUrl != null && !nextResult.endpointUrl.trim().equals(""))
    // {
    // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_ERRORPROVIDERS))
    // {
    // errorProviderUris.add(nextResult.endpointUrl);
    // }
    // }
    // }
    // }
    //
    // int sumQueries = nonErrorLatencyList.size();
    // int sumErrorQueries = errorLatencyList.size();
    //
    // long statisticsSumLatency = 0;
    //
    // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_SUMLATENCY))
    // {
    // statisticsSumLatency = sumLatency;
    // }
    //
    // long statisticsSumErrorLatency = 0;
    //
    // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_SUMERRORLATENCY))
    // {
    // statisticsSumErrorLatency = sumErrorLatency;
    // }
    //
    // int statisticsSumQueries = 0;
    //
    // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_SUMQUERIES))
    // {
    // statisticsSumQueries = sumQueries;
    // }
    //
    // int statisticsSumErrorQueries = 0;
    //
    // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_SUMERRORS))
    // {
    // statisticsSumErrorQueries = sumErrorQueries;
    // }
    //
    // double stdevlatency = RdfUtils.getStandardDeviationFromLongs(nonErrorLatencyList);
    //
    // double statisticsStdevLatency = 0.0;
    //
    // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_STDEVLATENCY))
    // {
    // statisticsStdevLatency = stdevlatency;
    // }
    //
    // double stdeverrorlatency = RdfUtils.getStandardDeviationFromLongs(errorLatencyList);
    //
    // double statisticsStdevErrorLatency = 0.0;
    //
    // if(localSettings.STATISTICS_TO_SUBMIT_LIST.contains(localSettings.STATISTICS_ITEM_STDEVERRORLATENCY))
    // {
    // statisticsStdevErrorLatency = stdeverrorlatency;
    // }
    //
    // String statisticsLastServerRestart =
    // RdfUtils.ISO8601UTC().format(BlacklistController.lastServerStartupDate);
    //
    // String statisticsServerSoftwareVersion =
    // localSettings.getStringPropertyFromConfig("userAgent");
    //
    // String statisticsacceptHeader = originalAcceptHeader;
    //
    // String statisticsrequestedContentType = requestedContentType;
    //
    // String keyToUse = (queryString.hashCode()*requesterIpAddress.hashCode())
    // + "-"
    // + (
    // (queryStartTime.getTime()*(stdevlatency+1))
    // /
    // ((nextTotalTime+1)*localSettings.getIntPropertyFromConfig("connectTimeout"))
    // );
    //
    // String key = localSettings.getDefaultHostAddress()
    // +localqueryall.namespaceStatistics
    // +localSettings.getStringPropertyFromConfig("separator")
    // +RdfUtils.percentEncode(keyToUse);
    //
    // if(INFO)
    // {
    // log.info("GeneralServlet: statistics key="+key);
    // }
    //
    // statisticsEntryList.add(new StatisticsEntry(
    // key,
    // profileUris,
    // successfulProviderUris,
    // errorProviderUris,
    // configLocations,
    // querytypeUris,
    // namespaceUris,
    // configVersion,
    // readtimeout,
    // connecttimeout,
    // userHostAddress,
    // userAgent,
    // statisticsRealHostName,
    // statisticsQueryString,
    // responseTime,
    // statisticsSumLatency,
    // statisticsSumQueries,
    // statisticsStdevLatency,
    // statisticsSumErrorQueries,
    // statisticsSumErrorLatency,
    // statisticsStdevErrorLatency,
    // statisticsLastServerRestart,
    // statisticsServerSoftwareVersion,
    // statisticsacceptHeader,
    // statisticsrequestedContentType
    // ));
    //
    // BlacklistController.persistStatistics(statisticsEntryList,
    // localSettings.getSettings().CONFIG_API_VERSION);
    // }
}
