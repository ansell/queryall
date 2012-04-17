package org.queryall.query;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.blacklist.BlacklistController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class RdfFetcherQueryRunnableImpl extends Thread implements RdfFetcherQueryRunnable // ,
// Callable<String>
{
    private static final Logger log = LoggerFactory.getLogger(RdfFetcherQueryRunnableImpl.class);
    private static final boolean TRACE = RdfFetcherQueryRunnableImpl.log.isTraceEnabled();
    private static final boolean DEBUG = RdfFetcherQueryRunnableImpl.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = RdfFetcherQueryRunnableImpl.log.isInfoEnabled();
    
    private String originalEndpointUrl = "";
    private String originalQuery = "";
    private String actualEndpointUrl;
    private String actualQuery;
    private String debug = "";
    private String acceptHeader = "";
    private String returnedContentType = null;
    private String returnedMIMEType = null;
    private String returnedContentEncoding = null;
    private QueryBundle originalQueryBundle = null;
    
    private volatile boolean wasSuccessful = false;
    private volatile boolean completed = false;
    private Exception lastException = null;
    private String resultDebugString = "";
    
    private String rawResult = "";
    private String normalisedResult = "";
    
    private Date queryStartTime = null;
    private Date queryEndTime = null;
    private QueryAllConfiguration localSettings;
    private BlacklistController localBlacklistController;
    private CountDownLatch countDownLatch;
    
    public RdfFetcherQueryRunnableImpl(final String nextEndpointUrl, final String nextQuery, final String nextDebug,
            final String nextAcceptHeader, final QueryAllConfiguration localSettings,
            final BlacklistController localBlacklistController)
    {
        this.setOriginalEndpointUrl(nextEndpointUrl);
        this.setOriginalQuery(nextQuery);
        this.setDebug(nextDebug);
        this.setAcceptHeader(nextAcceptHeader);
        this.setSettings(localSettings);
        this.setBlacklistController(localBlacklistController);
    }
    
    public RdfFetcherQueryRunnableImpl(final String nextEndpointUrl, final String nextQuery, final String nextDebug,
            final String nextAcceptHeader, final QueryAllConfiguration localSettings,
            final BlacklistController localBlacklistController, final QueryBundle nextOriginalQueryBundle)
    {
        this(nextEndpointUrl, nextQuery, nextDebug, nextAcceptHeader, localSettings, localBlacklistController);
        this.setOriginalQueryBundle(nextOriginalQueryBundle);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#getAcceptHeader()
     */
    @Override
    public String getAcceptHeader()
    {
        return this.acceptHeader;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#getActualEndpointUrl()
     */
    @Override
    public String getActualEndpointUrl()
    {
        return this.actualEndpointUrl;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#getActualQuery()
     */
    @Override
    public String getActualQuery()
    {
        return this.actualQuery;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#getBlacklistController()
     */
    @Override
    public BlacklistController getBlacklistController()
    {
        return this.localBlacklistController;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#getCompleted()
     */
    @Override
    public boolean getCompleted()
    {
        return this.completed;
    }
    
    @Override
    public CountDownLatch getCountDownLatch()
    {
        return this.countDownLatch;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#getDebug()
     */
    @Override
    public String getDebug()
    {
        return this.debug;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#getLastException()
     */
    @Override
    public Exception getLastException()
    {
        return this.lastException;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#getLocalSettings()
     */
    @Override
    public QueryAllConfiguration getLocalSettings()
    {
        return this.localSettings;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#getNormalisedResult()
     */
    @Override
    public String getNormalisedResult()
    {
        if(this.normalisedResult == null || this.normalisedResult.isEmpty())
        {
            if(RdfFetcherQueryRunnableImpl.DEBUG)
            {
                
                RdfFetcherQueryRunnableImpl.log
                        .debug("getNormalisedResult: no normalisation occurred, returning raw result instead");
            }
            
            return this.rawResult;
        }
        else
        {
            if(RdfFetcherQueryRunnableImpl.TRACE)
            {
                RdfFetcherQueryRunnableImpl.log.trace("getNormalisedResult: returning normalised result");
            }
            
            return this.normalisedResult;
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#getOriginalEndpointUrl()
     */
    @Override
    public String getOriginalEndpointUrl()
    {
        return this.originalEndpointUrl;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#getOriginalQuery()
     */
    @Override
    public String getOriginalQuery()
    {
        return this.originalQuery;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#getOriginalQueryBundle()
     */
    @Override
    public QueryBundle getOriginalQueryBundle()
    {
        return this.originalQueryBundle;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#getQueryEndTime()
     */
    @Override
    public Date getQueryEndTime()
    {
        return this.queryEndTime;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#getQueryStartTime()
     */
    @Override
    public Date getQueryStartTime()
    {
        return this.queryStartTime;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#getRawResult()
     */
    @Override
    public String getRawResult()
    {
        return this.rawResult;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#getResultDebugString()
     */
    @Override
    public String getResultDebugString()
    {
        return this.resultDebugString;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#getReturnedContentEncoding()
     */
    @Override
    public String getReturnedContentEncoding()
    {
        return this.returnedContentEncoding;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#getReturnedContentType()
     */
    @Override
    public String getReturnedContentType()
    {
        return this.returnedContentType;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#getReturnedMIMEType()
     */
    @Override
    public String getReturnedMIMEType()
    {
        return this.returnedMIMEType;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#getWasSuccessful()
     */
    @Override
    public boolean getWasSuccessful()
    {
        return this.wasSuccessful;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#notExecuted()
     */
    @Override
    public boolean notExecuted()
    {
        return !this.getCompleted();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#setAcceptHeader(java.lang.String)
     */
    @Override
    public void setAcceptHeader(final String acceptHeader)
    {
        this.acceptHeader = acceptHeader;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#setActualEndpointUrl(java.lang.String)
     */
    @Override
    public void setActualEndpointUrl(final String actualEndpointUrl)
    {
        this.actualEndpointUrl = actualEndpointUrl;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#setActualQuery(java.lang.String)
     */
    @Override
    public void setActualQuery(final String actualQuery)
    {
        this.actualQuery = actualQuery;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.queryall.query.RdfFetcherQueryRunnable#setBlacklistController(org.queryall.blacklist.
     * BlacklistController)
     */
    @Override
    public void setBlacklistController(final BlacklistController localBlacklistController)
    {
        this.localBlacklistController = localBlacklistController;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#setCompleted(boolean)
     */
    @Override
    public void setCompleted(final boolean completed)
    {
        this.completed = completed;
    }
    
    @Override
    public void setCountDownLatch(final CountDownLatch isDone)
    {
        this.countDownLatch = isDone;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#setDebug(java.lang.String)
     */
    @Override
    public void setDebug(final String debug)
    {
        this.debug = debug;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#setLastException(java.lang.Exception)
     */
    @Override
    public void setLastException(final Exception lastException)
    {
        this.lastException = lastException;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#setNormalisedResult(java.lang.String)
     */
    @Override
    public void setNormalisedResult(final String normalisedResult)
    {
        this.normalisedResult = normalisedResult;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#setOriginalEndpointUrl(java.lang.String)
     */
    @Override
    public void setOriginalEndpointUrl(final String endpointUrl)
    {
        this.originalEndpointUrl = endpointUrl;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#setOriginalQuery(java.lang.String)
     */
    @Override
    public void setOriginalQuery(final String query)
    {
        this.originalQuery = query;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.queryall.query.RdfFetcherQueryRunnable#setOriginalQueryBundle(org.queryall.query.QueryBundle
     * )
     */
    @Override
    public void setOriginalQueryBundle(final QueryBundle originalQueryBundle)
    {
        this.originalQueryBundle = originalQueryBundle;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#setQueryEndTime(java.util.Date)
     */
    @Override
    public void setQueryEndTime(final Date queryEndTime)
    {
        this.queryEndTime = queryEndTime;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#setQueryStartTime(java.util.Date)
     */
    @Override
    public void setQueryStartTime(final Date queryStartTime)
    {
        this.queryStartTime = queryStartTime;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#setRawResult(java.lang.String)
     */
    @Override
    public void setRawResult(final String rawResult)
    {
        this.rawResult = rawResult;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#setResultDebugString(java.lang.String)
     */
    @Override
    public void setResultDebugString(final String resultDebugString)
    {
        this.resultDebugString = resultDebugString;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#setReturnedContentEncoding(java.lang.String)
     */
    @Override
    public void setReturnedContentEncoding(final String returnedContentEncoding)
    {
        this.returnedContentEncoding = returnedContentEncoding;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#setReturnedContentType(java.lang.String)
     */
    @Override
    public void setReturnedContentType(final String returnedContentType)
    {
        this.returnedContentType = returnedContentType;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#setReturnedMIMEType(java.lang.String)
     */
    @Override
    public void setReturnedMIMEType(final String returnedMIMEType)
    {
        this.returnedMIMEType = returnedMIMEType;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#setSettings(org.queryall.api.base.
     * QueryAllConfiguration)
     */
    @Override
    public void setSettings(final QueryAllConfiguration localSettings)
    {
        this.localSettings = localSettings;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#setWasSuccessful(boolean)
     */
    @Override
    public void setWasSuccessful(final boolean wasSuccessful)
    {
        this.wasSuccessful = wasSuccessful;
    }
    
    @Override
    public String toString()
    {
        return "originalendpointUrl=" + this.getOriginalEndpointUrl() + "actualendpointurl="
                + this.getActualEndpointUrl() + " query=" + this.getOriginalQuery();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#wasCompletedSuccessfulQuery()
     */
    @Override
    public boolean wasCompletedSuccessfulQuery()
    {
        return this.getCompleted() && this.getWasSuccessful();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.query.RdfFetcherQueryRunnable#wasError()
     */
    @Override
    public boolean wasError()
    {
        return this.getCompleted() && this.getLastException() != null;
    }
    
}
