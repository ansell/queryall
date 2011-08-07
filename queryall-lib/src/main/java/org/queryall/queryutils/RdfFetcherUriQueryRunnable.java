package org.queryall.queryutils;

import java.util.Date;

import org.queryall.api.QueryAllConfiguration;
import org.queryall.blacklist.BlacklistController;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RdfFetcherUriQueryRunnable extends RdfFetcherQueryRunnable
{
    public RdfFetcherUriQueryRunnable(final String nextEndpointUrl, final String nextFormat, final String nextQuery,
            final String nextDebug, final String nextAcceptHeader, final QueryAllConfiguration localSettings,
            final BlacklistController localBlacklistController, final QueryBundle nextOriginalQueryBundle)
    {
        super(nextEndpointUrl, nextFormat, nextQuery, nextDebug, nextAcceptHeader, localSettings,
                localBlacklistController, nextOriginalQueryBundle);
    }
    
    @Override
    public void run()
    {
        try
        {
            final RdfFetcher fetcher = new RdfFetcher(this.getLocalSettings(), this.getBlacklistController());
            
            this.setQueryStartTime(new Date());
            
            this.setRawResult(fetcher.getDocumentFromUrl(this.getEndpointUrl(), "", this.getAcceptHeader()));
            
            this.setQueryEndTime(new Date());
            
            this.setReturnedContentType(fetcher.lastReturnedContentType);
            
            if(this.getReturnedContentType() != null)
            {
                // HACK TODO: should this be any cleaner than this.... Could hypothetically pipe it
                // through the conn neg code
                this.setReturnedMIMEType(this.getReturnedContentType().split(";")[0]);
            }
            
            this.setReturnedContentEncoding(fetcher.lastReturnedContentEncoding);
            
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
