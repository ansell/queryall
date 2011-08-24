package org.queryall.query;

import java.util.Date;

import org.queryall.api.QueryAllConfiguration;
import org.queryall.blacklist.BlacklistController;
import org.queryall.impl.provider.HttpProviderImpl;
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
    
    public String httpOperation = "GET";
    public int maxRowsParameter = this.getLocalSettings().getIntProperty("pageoffsetIndividualQueryLimit", 0);
    
    public HttpUrlQueryRunnable(final String nextHttpOperation, final String nextUrl, final String nextPostInformation,
            final String nextAcceptHeader, final QueryAllConfiguration localSettings,
            final BlacklistController localBlacklistController)
    {
        super(nextUrl, nextPostInformation, "", nextAcceptHeader, localSettings, localBlacklistController);
        this.httpOperation = nextHttpOperation;
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
                this.setRawResult(fetcher.submitSparqlQuery(this.getEndpointUrl(), "", this.getQuery(), "",
                        this.maxRowsParameter, this.getAcceptHeader()));
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
