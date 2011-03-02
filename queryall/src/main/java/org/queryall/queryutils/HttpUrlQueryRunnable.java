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
            queryStartTime = new Date();
            
            RdfFetcher fetcher = new RdfFetcher();
            
            if(_TRACE)
            {
                log.trace("HttpUrlQueryRunnable.run: about to fetch");
            }
            
            if(this.httpOperation.equals(ProviderImpl.getProviderHttpPostSparqlUri().stringValue()))
            {
                this.rawResult = fetcher.submitSparqlQuery(this.endpointUrl, this.format, "", this.query, "", maxRowsParameter, this.acceptHeader);
            }
            else if(this.httpOperation.equals(ProviderImpl.getProviderHttpPostUrlUri().stringValue()) || this.httpOperation.equals(ProviderImpl.getProviderHttpGetUrlUri().stringValue()))
            {
                this.rawResult = fetcher.getDocumentFromUrl(this.endpointUrl, this.query, this.acceptHeader);
            }
            
            // make the normalised Result the same as the raw result unless people actually want to normalise it
            this.normalisedResult = rawResult;
            
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
            wasSuccessful = false;
            lastException = ste;
        }
        catch(Exception ex)
        {
            wasSuccessful = false;
            lastException = ex;
        }
        finally
        {
            queryEndTime = new Date();
            this.completed = true;
        }
    }
}

