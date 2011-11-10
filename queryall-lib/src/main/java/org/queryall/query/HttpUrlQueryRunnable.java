package org.queryall.query;

import java.util.Date;
import java.util.Map;

import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.provider.HttpProviderSchema;
import org.queryall.api.provider.SparqlProviderSchema;
import org.queryall.blacklist.BlacklistController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class HttpUrlQueryRunnable extends RdfFetcherQueryRunnable // extends Thread
{
    private static final Logger log = LoggerFactory.getLogger(HttpUrlQueryRunnable.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = HttpUrlQueryRunnable.log.isTraceEnabled();
    private static final boolean _DEBUG = HttpUrlQueryRunnable.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = HttpUrlQueryRunnable.log.isInfoEnabled();
    
    public String httpOperation = "GET";
    public int maxRowsParameter = this.getLocalSettings().getIntProperty("pageoffsetIndividualQueryLimit", 500);
    
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
            
            if(HttpUrlQueryRunnable._DEBUG)
            {
                HttpUrlQueryRunnable.log.debug("HttpUrlQueryRunnable.run: about to fetch endpoint="
                        + this.getEndpointUrl());
            }
            
            String tempRawResult = "";
            // TODO: make this section extensible
            if(this.httpOperation.equals(SparqlProviderSchema.getProviderHttpPostSparql().stringValue()))
            {
                tempRawResult =
                        fetcher.submitSparqlQuery(this.getEndpointUrl(), "", this.getQuery(), "",
                                this.maxRowsParameter, this.getAcceptHeader());
                
                if(fetcher.getLastWasError())
                {
                    HttpUrlQueryRunnable.log.error("Failed to fetch from endpoint=" + this.getEndpointUrl());
                    final Map<String, String> alternateEndpointsAndQueries =
                            this.getOriginalQueryBundle().getAlternativeEndpointsAndQueries();
                    
                    for(final String alternateEndpoint : alternateEndpointsAndQueries.keySet())
                    {
                        HttpUrlQueryRunnable.log.error("Trying to fetch from alternate endpoint=" + alternateEndpoint
                                + " originalEndpoint=" + this.getEndpointUrl());
                        
                        final String alternateQuery = alternateEndpointsAndQueries.get(alternateEndpoint);
                        
                        tempRawResult =
                                fetcher.submitSparqlQuery(alternateEndpoint, "", alternateQuery, "",
                                        this.maxRowsParameter, this.getAcceptHeader());
                        
                        if(!fetcher.getLastWasError())
                        {
                            // break on the first alternate that wasn't an error
                            break;
                        }
                    }
                }
            }
            else if(this.httpOperation.equals(HttpProviderSchema.getProviderHttpPostUrlUri().stringValue())
                    || this.httpOperation.equals(HttpProviderSchema.getProviderHttpGetUrlUri().stringValue()))
            {
                tempRawResult =
                        fetcher.getDocumentFromUrl(this.getEndpointUrl(), this.getQuery(), this.getAcceptHeader());
                
                if(fetcher.getLastWasError())
                {
                    HttpUrlQueryRunnable.log.error("Failed to fetch from endpoint=" + this.getEndpointUrl());
                    
                    final Map<String, String> alternateEndpointsAndQueries =
                            this.getOriginalQueryBundle().getAlternativeEndpointsAndQueries();
                    
                    HttpUrlQueryRunnable.log.error("There are " + alternateEndpointsAndQueries.size()
                            + " alternative endpoints to choose from");
                    
                    for(final String alternateEndpoint : alternateEndpointsAndQueries.keySet())
                    {
                        HttpUrlQueryRunnable.log.error("Trying to fetch from alternate endpoint=" + alternateEndpoint
                                + " originalEndpoint=" + this.getEndpointUrl());
                        
                        final String alternateQuery = alternateEndpointsAndQueries.get(alternateEndpoint);
                        
                        tempRawResult =
                                fetcher.getDocumentFromUrl(alternateEndpoint, alternateQuery, this.getAcceptHeader());
                        
                        if(!fetcher.getLastWasError())
                        {
                            // break on the first alternate that wasn't an error
                            break;
                        }
                    }
                }
            }
            
            if(!fetcher.getLastWasError())
            {
                this.setRawResult(tempRawResult);
                
                // make the normalised Result the same as the raw result unless people actually want
                // to
                // normalise it
                this.setNormalisedResult(this.getRawResult());
                
                this.setReturnedContentType(fetcher.getLastReturnedContentType());
                
                if(this.getReturnedContentType() != null)
                {
                    // HACK TODO: should this be any cleaner than this.... Could hypothetically pipe
                    // it
                    // through the conn neg code
                    this.setReturnedMIMEType(this.getReturnedContentType().split(";")[0]);
                }
                
                this.setReturnedContentEncoding(fetcher.getLastReturnedContentEncoding());
                
                this.setWasSuccessful(true);
            }
            else
            {
                this.setWasSuccessful(false);
                this.setLastException(fetcher.getLastException());
            }
        }
        catch(final Exception ex)
        {
            HttpUrlQueryRunnable.log.error("Found unknown exception", ex);
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
