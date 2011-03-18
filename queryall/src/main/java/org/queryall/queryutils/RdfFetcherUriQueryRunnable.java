package org.queryall.queryutils;

import java.util.Date;

public class RdfFetcherUriQueryRunnable extends RdfFetcherQueryRunnable
{
	public RdfFetcherUriQueryRunnable
	(
		String nextEndpointUrl,
		String nextFormat,
		String nextQuery,
		String nextDebug,
		String nextAcceptHeader, 
		QueryBundle nextOriginalQueryBundle
	)
	{
		super(nextEndpointUrl, nextFormat, nextQuery, nextDebug, nextAcceptHeader, nextOriginalQueryBundle);
	}
	
	public void run()
	{
		try
		{
			RdfFetcher fetcher = new RdfFetcher();
			
			setQueryStartTime(new Date());
			
			this.setRawResult(fetcher.getDocumentFromUrl(this.getEndpointUrl(), "", this.getAcceptHeader()));
			
			setQueryEndTime(new Date());
			
			this.setReturnedContentType(fetcher.lastReturnedContentType);
			
			if(this.getReturnedContentType() != null)
			{
				// HACK TODO: should this be any cleaner than this.... Could hypothetically pipe it through the conn neg code
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

