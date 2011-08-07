package org.queryall.blacklist;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.queryall.queryutils.RdfFetcherQueryRunnable;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class BlacklistEntry
{
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(BlacklistEntry.class.getName());
    
    public int numberOfFailures = 0;
    public String endpointUrl = "";
    public long totalTime = 0L;
    private Collection<RdfFetcherQueryRunnable> errorRunnables = null;
    public Collection<String> errorMessages = new ArrayList<String>();
    
    @Override
    public String toString()
    {
        return "Endpoint URL = " + endpointUrl + ", Number Of Failures = " + numberOfFailures;
    }
    
    public void addErrorMessageForRunnable(RdfFetcherQueryRunnable errorRunnable)
    {
        StringBuilder resultBuffer = new StringBuilder();
        
        resultBuffer.append("Failed query key : "
                + errorRunnable.getOriginalQueryBundle().getQueryType().getKey().stringValue() + "<br />\n");
        resultBuffer.append("Failure message : " + errorRunnable.getLastException().toString() + "<br />\n");
        
        resultBuffer.append("Time to fail (milliseconds) : "
                + (errorRunnable.getQueryEndTime().getTime() - errorRunnable.getQueryStartTime().getTime())
                + " <br />\n");
        
        errorMessages.add(resultBuffer.toString());
        
        totalTime += errorRunnable.getQueryEndTime().getTime() - errorRunnable.getQueryStartTime().getTime();
        
        numberOfFailures++;
    }
    
    public String errorMessageSummaryToString()
    {
        return errorMessageSummaryToString(true);
    }
    
    public String errorMessageSummaryToString(boolean includeSpecificQueries)
    {
        StringBuilder resultBuffer = new StringBuilder();
        
        if(includeSpecificQueries)
        {
            for(String nextErrorMessage : errorMessages)
            {
                resultBuffer.append(nextErrorMessage);
            }
        }
        
        resultBuffer.append("Total time taken for failed queries: ").append(Long.toString(totalTime))
                .append("<br />\n");
        resultBuffer.append("Total number of failed queries: ").append(Integer.toString(numberOfFailures))
                .append("<br />\n");
        resultBuffer.append("Average time: ").append(Double.toString((1.0 * totalTime) / numberOfFailures))
                .append("<br />\n");
        
        return resultBuffer.toString();
    }
}
