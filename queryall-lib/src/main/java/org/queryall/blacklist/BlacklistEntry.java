package org.queryall.blacklist;

import java.util.ArrayList;
import java.util.Collection;

import org.queryall.query.RdfFetcherQueryRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class BlacklistEntry
{
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(BlacklistEntry.class);
    
    public int numberOfFailures = 0;
    public String endpointUrl = "";
    public long totalTime = 0L;
    private Collection<RdfFetcherQueryRunnable> errorRunnables = null;
    public Collection<String> errorMessages = new ArrayList<String>();
    
    public void addErrorMessageForRunnable(final RdfFetcherQueryRunnable errorRunnable)
    {
        final StringBuilder resultBuffer = new StringBuilder();
        
        resultBuffer.append("Failed query key : "
                + errorRunnable.getOriginalQueryBundle().getQueryType().getKey().stringValue() + "<br />\n");
        if(errorRunnable.getLastException() != null)
        {
            resultBuffer.append("Failure message : " + errorRunnable.getLastException().toString() + "<br />\n");
        }
        else
        {
            resultBuffer.append("Failure message not known <br />\n");
        }
        
        resultBuffer.append("Time to fail (milliseconds) : "
                + (errorRunnable.getQueryEndTime().getTime() - errorRunnable.getQueryStartTime().getTime())
                + " <br />\n");
        
        this.errorMessages.add(resultBuffer.toString());
        
        this.totalTime += errorRunnable.getQueryEndTime().getTime() - errorRunnable.getQueryStartTime().getTime();
        
        this.numberOfFailures++;
    }
    
    public String errorMessageSummaryToString()
    {
        return this.errorMessageSummaryToString(true);
    }
    
    public String errorMessageSummaryToString(final boolean includeSpecificQueries)
    {
        final StringBuilder resultBuffer = new StringBuilder();
        
        if(includeSpecificQueries)
        {
            for(final String nextErrorMessage : this.errorMessages)
            {
                resultBuffer.append(nextErrorMessage);
            }
        }
        
        resultBuffer.append("Total time taken for failed queries: ").append(Long.toString(this.totalTime))
                .append("<br />\n");
        resultBuffer.append("Total number of failed queries: ").append(Integer.toString(this.numberOfFailures))
                .append("<br />\n");
        resultBuffer.append("Average time: ").append(Double.toString((1.0 * this.totalTime) / this.numberOfFailures))
                .append("<br />\n");
        
        return resultBuffer.toString();
    }
    
    @Override
    public String toString()
    {
        return "Endpoint URL = " + this.endpointUrl + ", Number Of Failures = " + this.numberOfFailures;
    }
}
