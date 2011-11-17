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
public class RdfFetcherSparqlQueryRunnable extends RdfFetcherQueryRunnable
{
    private static final Logger log = LoggerFactory.getLogger(RdfFetcherSparqlQueryRunnable.class);
    private static final boolean _TRACE = RdfFetcherSparqlQueryRunnable.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = RdfFetcherSparqlQueryRunnable.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = RdfFetcherSparqlQueryRunnable.log.isInfoEnabled();
    
    public String graphUri = "";
    public int maxRowsParameter = this.getLocalSettings().getIntProperty("pageoffsetIndividualQueryLimit", 500);
    
    public RdfFetcherSparqlQueryRunnable(final String nextEndpointUrl, final String nextGraphUri,
            final String nextQuery, final String nextDebug, final String nextAcceptHeader,
            final int nextMaxRowsParameter, final QueryAllConfiguration localSettings,
            final BlacklistController localBlacklistController, final QueryBundle nextOriginalQueryBundle)
    {
        super(nextEndpointUrl, nextQuery, nextDebug, nextAcceptHeader, localSettings, localBlacklistController,
                nextOriginalQueryBundle);
        
        this.graphUri = nextGraphUri;
        this.maxRowsParameter = nextMaxRowsParameter;
    }
    
    @Override
    public void run()
    {
        try
        {
            final RdfFetcher fetcher = new RdfFetcher(this.getLocalSettings(), this.getBlacklistController());
            
            this.setQueryStartTime(new Date());
            
            String tempRawResult =
                    fetcher.submitSparqlQuery(this.getEndpointUrl(), "", this.getQuery(), "", this.maxRowsParameter,
                            this.getAcceptHeader());
            
            if(fetcher.getLastWasError())
            {
                RdfFetcherSparqlQueryRunnable.log.error("Failed to fetch from endpoint=" + this.getEndpointUrl());
                
                final Map<String, String> alternateEndpointsAndQueries =
                        this.getOriginalQueryBundle().getAlternativeEndpointsAndQueries();
                
                RdfFetcherSparqlQueryRunnable.log.error("There are " + (alternateEndpointsAndQueries.size() - 1)
                        + " alternative endpoints to choose from");
                
                for(final String alternateEndpoint : alternateEndpointsAndQueries.keySet())
                {
                    if(!alternateEndpoint.equals(this.getEndpointUrl()))
                    {
                        RdfFetcherSparqlQueryRunnable.log.error("Trying to fetch from alternate endpoint="
                                + alternateEndpoint + " originalEndpoint=" + this.getEndpointUrl());
                        
                        final String alternateQuery = alternateEndpointsAndQueries.get(alternateEndpoint);
                        
                        if(_DEBUG)
                        {
                            log.debug("alternateQuery="+alternateQuery);
                        }
                        
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
                
            }
            else
            {
                this.setWasSuccessful(false);
                this.setLastException(fetcher.getLastException());
            }
        }
        catch(final Exception ex)
        {
            RdfFetcherSparqlQueryRunnable.log.error("Found unknown exception", ex);
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
