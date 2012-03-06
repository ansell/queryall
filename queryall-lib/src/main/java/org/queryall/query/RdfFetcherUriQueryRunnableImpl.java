package org.queryall.query;

import java.util.Date;
import java.util.Map;

import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.blacklist.BlacklistController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RdfFetcherUriQueryRunnableImpl extends RdfFetcherQueryRunnableImpl
{
    private static final Logger log = LoggerFactory.getLogger(RdfFetcherUriQueryRunnableImpl.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = RdfFetcherUriQueryRunnableImpl.log.isTraceEnabled();
    private static final boolean DEBUG = RdfFetcherUriQueryRunnableImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = RdfFetcherUriQueryRunnableImpl.log.isInfoEnabled();
    
    public RdfFetcherUriQueryRunnableImpl(final String nextEndpointUrl, final String nextQuery, final String nextDebug,
            final String nextAcceptHeader, final QueryAllConfiguration localSettings,
            final BlacklistController localBlacklistController, final QueryBundle nextOriginalQueryBundle)
    {
        super(nextEndpointUrl, nextQuery, nextDebug, nextAcceptHeader, localSettings, localBlacklistController,
                nextOriginalQueryBundle);
    }
    
    @Override
    public String call() throws Exception
    {
        this.doWork();
        
        return this.getNormalisedResult();
    }
    
    private void doWork()
    {
        try
        {
            final RdfFetcher fetcher = new RdfFetcher(this.getLocalSettings(), this.getBlacklistController());
            
            this.setQueryStartTime(new Date());
            
            String tempRawResult =
                    fetcher.getDocumentFromUrl(this.getOriginalEndpointUrl(), "", this.getAcceptHeader());
            
            if(fetcher.getLastWasError())
            {
                RdfFetcherUriQueryRunnableImpl.log.error("Failed to fetch from endpoint="
                        + this.getOriginalEndpointUrl());
                
                final Map<String, String> alternateEndpointsAndQueries =
                        this.getOriginalQueryBundle().getAlternativeEndpointsAndQueries();
                
                RdfFetcherUriQueryRunnableImpl.log.error("There are " + (alternateEndpointsAndQueries.size() - 1)
                        + " alternative endpoints to choose from");
                
                for(final String alternateEndpoint : alternateEndpointsAndQueries.keySet())
                {
                    if(!alternateEndpoint.equals(this.getOriginalEndpointUrl()))
                    {
                        RdfFetcherUriQueryRunnableImpl.log.error("Trying to fetch from alternate endpoint="
                                + alternateEndpoint + " originalEndpoint=" + this.getOriginalEndpointUrl());
                        
                        final String alternateQuery = alternateEndpointsAndQueries.get(alternateEndpoint);
                        
                        if(RdfFetcherUriQueryRunnableImpl.DEBUG)
                        {
                            RdfFetcherUriQueryRunnableImpl.log.debug("alternateQuery=" + alternateQuery);
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
            }
            else
            {
                this.setActualEndpointUrl(this.getOriginalEndpointUrl());
                this.setActualQuery(this.getOriginalQuery());
            }
            
            if(!fetcher.getLastWasError())
            {
                this.setRawResult(tempRawResult);
                
                this.setQueryEndTime(new Date());
                
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
            RdfFetcherUriQueryRunnableImpl.log.error("Found unknown exception", ex);
            this.setWasSuccessful(false);
            this.setLastException(ex);
        }
        finally
        {
            this.setQueryEndTime(new Date());
            this.setCompleted(true);
        }
    }
    
    @Override
    public void run()
    {
        this.doWork();
    }
}
