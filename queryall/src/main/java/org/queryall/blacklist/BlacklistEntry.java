package org.queryall.blacklist;

import java.util.Collection;

import org.apache.log4j.Logger;

import org.queryall.queryutils.RdfFetcherQueryRunnable;

public class BlacklistEntry
{
    @SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(BlacklistEntry.class.getName());
    
    public int numberOfFailures = 0;
    public String endpointUrl = "";
    public Collection<RdfFetcherQueryRunnable> errorRunnables = null;
    
    public String toString()
    {
        return "Endpoint URL = "+endpointUrl+", Number Of Failures = "+numberOfFailures;
    }
    
    public String errorMessageSummaryToString()
    {
        StringBuilder resultBuffer = new StringBuilder();
        
        long totalTime = 0;
        int numberOfEntries = 0;
        
        for(RdfFetcherQueryRunnable nextErrorQuery : errorRunnables)
        {
            resultBuffer.append("Failed query key : "+nextErrorQuery.originalQueryBundle.getQueryType().getKey().stringValue()+"<br />\n");
            resultBuffer.append("Failure message : "+nextErrorQuery.lastException.toString()+"<br />\n");
            
            resultBuffer.append("Time to fail (milliseconds) : "+(nextErrorQuery.queryEndTime.getTime()-nextErrorQuery.queryStartTime.getTime())+" <br />\n");
            
            totalTime += nextErrorQuery.queryEndTime.getTime()-nextErrorQuery.queryStartTime.getTime();
            
            numberOfEntries++;
        }
        
        resultBuffer.append("Total time taken for failed queries: " + totalTime + "<br />\n");
        resultBuffer.append("Total number of failed queries: " + numberOfEntries + "<br />\n");
        resultBuffer.append("Average time: " + totalTime/numberOfEntries + "<br />\n");
        
        return resultBuffer.toString();
    }
}
