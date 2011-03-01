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
			
			queryStartTime = new Date();
			
			this.rawResult = fetcher.getDocumentFromUrl(this.endpointUrl, "", this.acceptHeader);
			
			queryEndTime = new Date();
			
			this.returnedContentType = fetcher.lastReturnedContentType;
			
			if(this.returnedContentType != null)
			{
				// HACK TODO: should this be any cleaner than this.... Could hypothetically pipe it through the conn neg code
				this.returnedMIMEType = this.returnedContentType.split(";")[0];
			}
			
			this.returnedContentEncoding = fetcher.lastReturnedContentEncoding;
			
			wasSuccessful = true;
		}
		catch(java.net.SocketTimeoutException ste)
		{
			queryEndTime = new Date();
			wasSuccessful = false;
			lastException = ste;
		}
		catch(Exception ex)
		{
			queryEndTime = new Date();
			wasSuccessful = false;
			lastException = ex;
		}
		finally
		{
			this.completed = true;
		}
	}
}

