package org.queryall.blacklist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

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
    
    public AtomicInteger numberOfFailures = new AtomicInteger(0);
    public String endpointUrl = "";
    public long totalTime = 0L;
    // private Collection<RdfFetcherQueryRunnableImpl> errorRunnables = null;
    public Collection<String> errorMessages = new ArrayList<String>();
    
    public void addErrorMessageForRunnable(final RdfFetcherQueryRunnable errorRunnable)
    {
        final StringBuilder resultBuffer = new StringBuilder();
        
        if(errorRunnable.getOriginalQueryBundle() != null
                && errorRunnable.getOriginalQueryBundle().getQueryType() != null)
        {
            resultBuffer.append("Failed query key : "
                    + errorRunnable.getOriginalQueryBundle().getQueryType().getKey().stringValue() + "<br />\n");
        }
        else
        {
            resultBuffer.append("Failed query, no details available for original query type <br />\n");
        }
        
        if(errorRunnable.getLastException() != null)
        {
            resultBuffer.append("Failure message : " + errorRunnable.getLastException().toString() + "<br />\n");
        }
        else
        {
            resultBuffer.append("Failure message not known <br />\n");
        }
        
        if(errorRunnable.getQueryEndTime() != null && errorRunnable.getQueryStartTime() != null)
        {
            resultBuffer.append("Time to fail (milliseconds) : "
                    + (errorRunnable.getQueryEndTime().getTime() - errorRunnable.getQueryStartTime().getTime())
                    + " <br />\n");
            
            this.totalTime += errorRunnable.getQueryEndTime().getTime() - errorRunnable.getQueryStartTime().getTime();
        }
        else
        {
            resultBuffer.append("Time to fail unknown <br />\n");
        }
        
        this.errorMessages.add(resultBuffer.toString());
        
        this.numberOfFailures.incrementAndGet();
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
        resultBuffer.append("Total number of failed queries: ").append(this.numberOfFailures.toString())
                .append("<br />\n");
        resultBuffer.append("Average time: ")
                .append(Double.toString((1.0 * this.totalTime) / this.numberOfFailures.get())).append("<br />\n");
        
        return resultBuffer.toString();
    }
    
    @Override
    public String toString()
    {
        return "Endpoint URL = " + this.endpointUrl + ", Number Of Failures = " + this.numberOfFailures.toString();
    }
}
