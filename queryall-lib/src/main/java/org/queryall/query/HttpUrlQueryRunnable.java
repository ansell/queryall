package org.queryall.query;

import java.util.Date;

import org.queryall.api.QueryAllConfiguration;
import org.queryall.blacklist.BlacklistController;
import org.queryall.impl.HttpProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class HttpUrlQueryRunnable extends RdfFetcherQueryRunnable // extends Thread
{
    private static final Logger log = LoggerFactory.getLogger(HttpUrlQueryRunnable.class);
    private static final boolean _TRACE = HttpUrlQueryRunnable.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = HttpUrlQueryRunnable.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = HttpUrlQueryRunnable.log.isInfoEnabled();
    
    // private Settings localSettings = Settings.getSettings();
    
    public String httpOperation = "GET";
    // public String url = "";
    // public String postInformation = "";
    // public String acceptHeader = "";
    public int maxRowsParameter = this.getLocalSettings().getIntProperty("pageoffsetIndividualQueryLimit", 0);
    
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
    
    public HttpUrlQueryRunnable(final String nextHttpOperation, final String nextUrl, final String nextPostInformation,
            final String nextAcceptHeader, final String nextFormat, final QueryAllConfiguration localSettings,
            final BlacklistController localBlacklistController)
    {
        super(nextUrl, nextPostInformation, "", nextAcceptHeader, localSettings, localBlacklistController);
        this.httpOperation = nextHttpOperation;
        // this.url = nextUrl;
        // this.postInformation = nextPostInformation;
        // this.acceptHeader = nextAcceptHeader;
        // this.format = nextFormat;
        
    }
    
    @Override
    public void run()
    {
        try
        {
            this.setQueryStartTime(new Date());
            
            final RdfFetcher fetcher = new RdfFetcher(this.getLocalSettings(), this.getBlacklistController());
            
            if(HttpUrlQueryRunnable._TRACE)
            {
                HttpUrlQueryRunnable.log.trace("HttpUrlQueryRunnable.run: about to fetch");
            }
            
            if(this.httpOperation.equals(HttpProviderImpl.getProviderHttpPostSparqlUri().stringValue()))
            {
                this.setRawResult(fetcher.submitSparqlQuery(this.getEndpointUrl(), "", this.getQuery(),
                        "", this.maxRowsParameter, this.getAcceptHeader()));
            }
            else if(this.httpOperation.equals(HttpProviderImpl.getProviderHttpPostUrlUri().stringValue())
                    || this.httpOperation.equals(HttpProviderImpl.getProviderHttpGetUrlUri().stringValue()))
            {
                this.setRawResult(fetcher.getDocumentFromUrl(this.getEndpointUrl(), this.getQuery(),
                        this.getAcceptHeader()));
            }
            
            // make the normalised Result the same as the raw result unless people actually want to
            // normalise it
            this.setNormalisedResult(this.getRawResult());
            
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
            this.setWasSuccessful(false);
            this.setLastException(ste);
        }
        catch(final Exception ex)
        {
            this.setWasSuccessful(false);
            this.setLastException(ex);
        }
        finally
        {
            this.setQueryEndTime(new Date());
            this.setCompleted(true);
        }
    }
}
