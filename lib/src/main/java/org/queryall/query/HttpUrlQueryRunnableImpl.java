package org.queryall.query;

import java.util.Date;
import java.util.Map;

import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.provider.HttpProviderSchema;
import org.queryall.api.provider.SparqlProviderSchema;
import org.queryall.api.utils.WebappConfig;
import org.queryall.blacklist.BlacklistController;
import org.queryall.exception.QueryAllException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class HttpUrlQueryRunnableImpl extends RdfFetcherQueryRunnableImpl // extends Thread
{
    private static final Logger log = LoggerFactory.getLogger(HttpUrlQueryRunnableImpl.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = HttpUrlQueryRunnableImpl.log.isTraceEnabled();
    private static final boolean DEBUG = HttpUrlQueryRunnableImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = HttpUrlQueryRunnableImpl.log.isInfoEnabled();
    
    public String httpOperation = "GET";
    public int maxRowsParameter = this.getLocalSettings().getInt(WebappConfig.PAGEOFFSET_INDIVIDUAL_QUERY_LIMIT);
    
    public HttpUrlQueryRunnableImpl(final String nextHttpOperation, final String nextUrl,
            final String nextPostInformation, final String nextAcceptHeader, final QueryAllConfiguration localSettings,
            final BlacklistController localBlacklistController)
    {
        super(nextUrl, nextPostInformation, "", nextAcceptHeader, localSettings, localBlacklistController);
        this.httpOperation = nextHttpOperation;
    }
    
    // @Override
    // public String call() throws Exception
    // {
    // this.doWork();
    //
    // return this.getNormalisedResult();
    // }
    
    private void doWork()
    {
        try
        {
            this.setQueryStartTime(new Date());
            
            final RdfFetcher fetcher = new RdfFetcher(this.getLocalSettings(), this.getBlacklistController());
            
            if(HttpUrlQueryRunnableImpl.DEBUG)
            {
                HttpUrlQueryRunnableImpl.log.debug("HttpUrlQueryRunnableImpl.run: about to fetch endpoint="
                        + this.getOriginalEndpointUrl());
            }
            
            String tempRawResult = "";
            // TODO: make this section extensible
            if(this.httpOperation.equals(SparqlProviderSchema.getProviderHttpPostSparql().stringValue()))
            {
                tempRawResult =
                        fetcher.submitSparqlQuery(this.getOriginalEndpointUrl(), "", this.getOriginalQuery(), "",
                                this.maxRowsParameter, this.getAcceptHeader());
                
                if(fetcher.getLastWasError())
                {
                    HttpUrlQueryRunnableImpl.log
                            .error("Failed to fetch from endpoint=" + this.getOriginalEndpointUrl());
                    final Map<String, String> alternateEndpointsAndQueries =
                            this.getOriginalQueryBundle().getAlternativeEndpointsAndQueries();
                    
                    for(final String alternateEndpoint : alternateEndpointsAndQueries.keySet())
                    {
                        
                        final String alternateQuery = alternateEndpointsAndQueries.get(alternateEndpoint);
                        
                        HttpUrlQueryRunnableImpl.log.error("Trying to fetch from alternate endpoint="
                                + alternateEndpoint + " originalEndpoint=" + this.getOriginalEndpointUrl());
                        
                        if(HttpUrlQueryRunnableImpl.DEBUG)
                        {
                            HttpUrlQueryRunnableImpl.log.debug("alternateQuery=" + alternateQuery);
                        }
                        
                        tempRawResult =
                                fetcher.submitSparqlQuery(alternateEndpoint, "", alternateQuery, "",
                                        this.maxRowsParameter, this.getAcceptHeader());
                        
                        if(!fetcher.getLastWasError())
                        {
                            // break on the first alternate that wasn't an error
                            this.setActualEndpointUrl(alternateEndpoint);
                            this.setActualQuery(alternateQuery);
                            break;
                        }
                    }
                }
                else
                {
                    this.setActualEndpointUrl(this.getOriginalEndpointUrl());
                    this.setActualQuery(this.getOriginalQuery());
                }
            }
            else if(this.httpOperation.equals(HttpProviderSchema.getProviderHttpPostUrl().stringValue())
                    || this.httpOperation.equals(HttpProviderSchema.getProviderHttpGetUrl().stringValue()))
            {
                tempRawResult =
                        fetcher.getDocumentFromUrl(this.getOriginalEndpointUrl(), this.getOriginalQuery(),
                                this.getAcceptHeader());
                
                if(fetcher.getLastWasError())
                {
                    HttpUrlQueryRunnableImpl.log
                            .error("Failed to fetch from endpoint=" + this.getOriginalEndpointUrl());
                    
                    final Map<String, String> alternateEndpointsAndQueries =
                            this.getOriginalQueryBundle().getAlternativeEndpointsAndQueries();
                    
                    HttpUrlQueryRunnableImpl.log.error("There are " + alternateEndpointsAndQueries.size()
                            + " alternative endpoints to choose from");
                    
                    for(final String alternateEndpoint : alternateEndpointsAndQueries.keySet())
                    {
                        HttpUrlQueryRunnableImpl.log.error("Trying to fetch from alternate endpoint="
                                + alternateEndpoint + " originalEndpoint=" + this.getOriginalEndpointUrl());
                        
                        final String alternateQuery = alternateEndpointsAndQueries.get(alternateEndpoint);
                        
                        if(HttpUrlQueryRunnableImpl.DEBUG)
                        {
                            HttpUrlQueryRunnableImpl.log.debug("alternateQuery=" + alternateQuery);
                        }
                        
                        tempRawResult =
                                fetcher.getDocumentFromUrl(alternateEndpoint, alternateQuery, this.getAcceptHeader());
                        
                        if(!fetcher.getLastWasError())
                        {
                            // break on the first alternate that wasn't an error
                            this.setActualEndpointUrl(alternateEndpoint);
                            this.setActualQuery(alternateQuery);
                            break;
                        }
                    }
                }
                else
                {
                    this.setActualEndpointUrl(this.getOriginalEndpointUrl());
                    this.setActualQuery(this.getOriginalQuery());
                }
            }
            
            if(!fetcher.getLastWasError())
            {
                this.setRawResult(tempRawResult);
                
                // make the normalised Result the same as the raw result unless people actually want
                // to normalise it
                // this.setNormalisedResult(this.getRawResult());
                
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
        catch(final QueryAllException qae)
        {
            HttpUrlQueryRunnableImpl.log.error("Found QueryAllException", qae);
            this.setWasSuccessful(false);
            this.setLastException(qae);
        }
        catch(final Exception ex)
        {
            HttpUrlQueryRunnableImpl.log.error("Found unknown exception", ex);
            this.setWasSuccessful(false);
            this.setLastException(ex);
        }
        finally
        {
            this.setQueryEndTime(new Date());
            this.setCompleted(true);
            if(this.getCountDownLatch() != null)
            {
                this.getCountDownLatch().countDown();
            }
        }
    }
    
    @Override
    public void run()
    {
        this.doWork();
    }
}
