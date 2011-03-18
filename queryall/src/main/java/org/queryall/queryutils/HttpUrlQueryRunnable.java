package org.queryall.queryutils;

import org.apache.log4j.Logger;
import java.util.Date;

import org.queryall.helpers.Settings;
import org.queryall.impl.ProviderImpl;

public class HttpUrlQueryRunnable  extends RdfFetcherQueryRunnable //extends Thread
{
    private static final Logger log = Logger.getLogger(HttpUrlQueryRunnable.class.getName());
    private static final boolean _TRACE = log.isTraceEnabled();
    @SuppressWarnings("unused")
	private static final boolean _DEBUG = log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = log.isInfoEnabled();
    
    private Settings localSettings = Settings.getSettings();
    
    public String httpOperation = "GET";
    // public String url = "";
    // public String postInformation = "";
    // public String acceptHeader = "";
    public int maxRowsParameter = localSettings.getIntPropertyFromConfig("pageoffsetIndividualQueryLimit");
    // public String format = "";
    
    // public String rawResult = "";
    // public String returnedContentType = null;
    // public String returnedMIMEType = null;
    // public String returnedContentEncoding = null;
    
    // public Exception lastException = null;
    
    // public Date queryStartTime = null;
    // public Date queryEndTime = null;
    
    // public boolean completed = false;
    // public boolean wasSuccessful = false;
    
    public HttpUrlQueryRunnable
    (
        String nextHttpOperation,
        String nextUrl,
        String nextPostInformation,
        String nextAcceptHeader,
        String nextFormat
    )
    {
        super(nextUrl, nextFormat, nextPostInformation, "", nextAcceptHeader);
        this.httpOperation = nextHttpOperation;
        // this.url = nextUrl;
        // this.postInformation = nextPostInformation;
        // this.acceptHeader = nextAcceptHeader;
        // this.format = nextFormat;
    }
    
    public void run()
    {
        try
        {
            setQueryStartTime(new Date());
            
            RdfFetcher fetcher = new RdfFetcher();
            
            if(_TRACE)
            {
                log.trace("HttpUrlQueryRunnable.run: about to fetch");
            }
            
            if(this.httpOperation.equals(ProviderImpl.getProviderHttpPostSparqlUri().stringValue()))
            {
                this.setRawResult(fetcher.submitSparqlQuery(this.getEndpointUrl(), this.getFormat(), "", this.getQuery(), "", maxRowsParameter, this.getAcceptHeader()));
            }
            else if(this.httpOperation.equals(ProviderImpl.getProviderHttpPostUrlUri().stringValue()) || this.httpOperation.equals(ProviderImpl.getProviderHttpGetUrlUri().stringValue()))
            {
                this.setRawResult(fetcher.getDocumentFromUrl(this.getEndpointUrl(), this.getQuery(), this.getAcceptHeader()));
            }
            
            // make the normalised Result the same as the raw result unless people actually want to normalise it
            this.setNormalisedResult(getRawResult());
            
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
            setWasSuccessful(false);
            setLastException(ste);
        }
        catch(Exception ex)
        {
            setWasSuccessful(false);
            setLastException(ex);
        }
        finally
        {
            setQueryEndTime(new Date());
            this.setCompleted(true);
        }
    }
}

