package org.queryall.queryutils;

import java.util.Date;

import org.queryall.api.QueryAllConfiguration;
import org.queryall.blacklist.BlacklistController;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RdfFetcherUriQueryRunnable extends RdfFetcherQueryRunnable
{
    public RdfFetcherUriQueryRunnable(String nextEndpointUrl, String nextFormat, String nextQuery, String nextDebug,
            String nextAcceptHeader, QueryAllConfiguration localSettings, BlacklistController localBlacklistController,
            QueryBundle nextOriginalQueryBundle)
    {
        super(nextEndpointUrl, nextFormat, nextQuery, nextDebug, nextAcceptHeader, localSettings,
                localBlacklistController, nextOriginalQueryBundle);
    }
    
    @Override
    public void run()
    {
        try
        {
            RdfFetcher fetcher = new RdfFetcher(this.getLocalSettings(), getBlacklistController());
            
            setQueryStartTime(new Date());
            
            this.setRawResult(fetcher.getDocumentFromUrl(this.getEndpointUrl(), "", this.getAcceptHeader()));
            
            setQueryEndTime(new Date());
            
            this.setReturnedContentType(fetcher.lastReturnedContentType);
            
            if(this.getReturnedContentType() != null)
            {
                // HACK TODO: should this be any cleaner than this.... Could hypothetically pipe it
                // through the conn neg code
                this.setReturnedMIMEType(this.getReturnedContentType().split(";")[0]);
            }
            
            this.setReturnedContentEncoding(fetcher.lastReturnedContentEncoding);
            
            setWasSuccessful(true);
        }
        catch(java.net.SocketTimeoutException ste)
        {
            setQueryEndTime(new Date());
            setWasSuccessful(false);
            setLastException(ste);
        }
        catch(Exception ex)
        {
            setQueryEndTime(new Date());
            setWasSuccessful(false);
            setLastException(ex);
        }
        finally
        {
            this.setCompleted(true);
        }
    }
}
