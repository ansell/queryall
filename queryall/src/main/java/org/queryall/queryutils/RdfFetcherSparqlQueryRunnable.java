package org.queryall.queryutils;

import java.util.Date;

import org.queryall.blacklist.BlacklistController;
import org.queryall.helpers.*;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RdfFetcherSparqlQueryRunnable extends RdfFetcherQueryRunnable
{
	public String graphUri = "";
	public int maxRowsParameter = Settings.getSettings().getIntPropertyFromConfig("pageoffsetIndividualQueryLimit", 0);
    
	public RdfFetcherSparqlQueryRunnable
	(
		String nextEndpointUrl,
		String nextGraphUri,
		String nextFormat,
		String nextQuery,
		String nextDebug,
		String nextAcceptHeader,
        int nextMaxRowsParameter,
        Settings localSettings,
        BlacklistController localBlacklistController,
		QueryBundle nextOriginalQueryBundle
	)
	{
		super(nextEndpointUrl, nextFormat, nextQuery, nextDebug, nextAcceptHeader, localSettings, localBlacklistController, nextOriginalQueryBundle);
		
		this.graphUri = nextGraphUri;
        this.maxRowsParameter = nextMaxRowsParameter;
	}
	
	public void run()
	{
		try
		{
			RdfFetcher fetcher = new RdfFetcher(getLocalSettings(), getBlacklistController());
			
			setQueryStartTime(new Date());
			
			this.setRawResult(fetcher.submitSparqlQuery(
				this.getEndpointUrl(),
				this.getFormat(),
				//this.originalQueryBundle.originalProvider.getSparqlGraphUri(),
				this.graphUri,
				this.getQuery(),
				this.getDebug(),
                this.maxRowsParameter,
				this.getAcceptHeader()));
			
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

