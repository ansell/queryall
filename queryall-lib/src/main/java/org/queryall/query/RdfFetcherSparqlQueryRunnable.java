package org.queryall.query;

import java.util.Date;

import org.queryall.api.QueryAllConfiguration;
import org.queryall.blacklist.BlacklistController;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RdfFetcherSparqlQueryRunnable extends RdfFetcherQueryRunnable
{
    public String graphUri = "";
    public int maxRowsParameter = this.getLocalSettings().getIntProperty("pageoffsetIndividualQueryLimit", 0);
    
    public RdfFetcherSparqlQueryRunnable(final String nextEndpointUrl, final String nextGraphUri,
            final String nextQuery, final String nextDebug, final String nextAcceptHeader, final int nextMaxRowsParameter,
            final QueryAllConfiguration localSettings, final BlacklistController localBlacklistController,
            final QueryBundle nextOriginalQueryBundle)
    {
        super(nextEndpointUrl, nextQuery, nextDebug, nextAcceptHeader, localSettings, localBlacklistController,
                nextOriginalQueryBundle);
        
        this.graphUri = nextGraphUri;
        this.maxRowsParameter = nextMaxRowsParameter;
    }
    
    @Override
    public void run()
    {
        try
        {
            final RdfFetcher fetcher = new RdfFetcher(this.getLocalSettings(), this.getBlacklistController());
            
            this.setQueryStartTime(new Date());
            
            this.setRawResult(fetcher.submitSparqlQuery(this.getEndpointUrl(), // this.originalQueryBundle.originalProvider.getSparqlGraphUri(),
            this.graphUri,
            this.getQuery(), this.getDebug(), this.maxRowsParameter, this.getAcceptHeader()));
            
            this.setQueryEndTime(new Date());
            
            this.setReturnedContentType(fetcher.getLastReturnedContentType());
            
            if(this.getReturnedContentType() != null)
            {
                // HACK TODO: should this be any cleaner than this.... Could hypothetically pipe it
                // through the conn neg code
                this.setReturnedMIMEType(this.getReturnedContentType().split(";")[0]);
            }
            
            this.setReturnedContentEncoding(fetcher.getLastReturnedContentEncoding());
            
            this.setWasSuccessful(true);
        }
        catch(final java.net.SocketTimeoutException ste)
        {
            this.setQueryEndTime(new Date());
            this.setWasSuccessful(false);
            this.setLastException(ste);
        }
        catch(final Exception ex)
        {
            this.setQueryEndTime(new Date());
            this.setWasSuccessful(false);
            this.setLastException(ex);
        }
        finally
        {
            this.setCompleted(true);
        }
    }
}
