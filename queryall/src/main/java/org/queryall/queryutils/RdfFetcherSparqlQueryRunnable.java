package org.queryall.queryutils;

import java.util.Date;

import org.queryall.helpers.*;

public class RdfFetcherSparqlQueryRunnable extends RdfFetcherQueryRunnable
{
	public String graphUri = "";
	public int maxRowsParameter = Settings.getSettings().getIntPropertyFromConfig("pageoffsetIndividualQueryLimit");
    
	public RdfFetcherSparqlQueryRunnable
	(
		String nextEndpointUrl,
		String nextGraphUri,
		String nextFormat,
		String nextQuery,
		String nextDebug,
		String nextAcceptHeader,
        int nextMaxRowsParameter,
		QueryBundle nextOriginalQueryBundle
	)
	{
		super(nextEndpointUrl, nextFormat, nextQuery, nextDebug, nextAcceptHeader, nextOriginalQueryBundle);
		
		this.graphUri = nextGraphUri;
        this.maxRowsParameter = nextMaxRowsParameter;
	}
	
	public void run()
	{
		try
		{
			RdfFetcher fetcher = new RdfFetcher();
			
			queryStartTime = new Date();
			
			this.rawResult = fetcher.submitSparqlQuery(
				this.endpointUrl,
				this.format,
				//this.originalQueryBundle.originalProvider.getSparqlGraphUri(),
				this.graphUri,
				this.query,
				this.debug,
                this.maxRowsParameter,
				this.acceptHeader);
			
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

