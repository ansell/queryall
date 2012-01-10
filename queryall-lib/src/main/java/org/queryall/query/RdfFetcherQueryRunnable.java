package org.queryall.query;

import java.util.Date;
import java.util.concurrent.Callable;

import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.blacklist.BlacklistController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class RdfFetcherQueryRunnable extends Thread implements Callable<String>
{
    private static final Logger log = LoggerFactory.getLogger(RdfFetcherQueryRunnable.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = RdfFetcherQueryRunnable.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean DEBUG = RdfFetcherQueryRunnable.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = RdfFetcherQueryRunnable.log.isInfoEnabled();
    
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
    
    private boolean wasSuccessful = false;
    private boolean completed = false;
    private Exception lastException = null;
    private String resultDebugString = "";
    
    private String rawResult = "";
    private String normalisedResult = "";
    
    private Date queryStartTime = null;
    private Date queryEndTime = null;
    private QueryAllConfiguration localSettings;
    private BlacklistController localBlacklistController;
    
    public RdfFetcherQueryRunnable(final String nextEndpointUrl, final String nextQuery, final String nextDebug,
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
    
    public RdfFetcherQueryRunnable(final String nextEndpointUrl, final String nextQuery, final String nextDebug,
            final String nextAcceptHeader, final QueryAllConfiguration localSettings,
            final BlacklistController localBlacklistController, final QueryBundle nextOriginalQueryBundle)
    {
        this(nextEndpointUrl, nextQuery, nextDebug, nextAcceptHeader, localSettings, localBlacklistController);
        this.setOriginalQueryBundle(nextOriginalQueryBundle);
    }
    
    /**
     * @return the acceptHeader
     */
    public String getAcceptHeader()
    {
        return this.acceptHeader;
    }
    
    /**
     * @return the actualEndpointUrl
     */
    public String getActualEndpointUrl()
    {
        return this.actualEndpointUrl;
    }
    
    /**
     * @return the actualQuery
     */
    public String getActualQuery()
    {
        return this.actualQuery;
    }
    
    /**
     * @return the localBlacklistController
     */
    public BlacklistController getBlacklistController()
    {
        return this.localBlacklistController;
    }
    
    /**
     * @return the completed
     */
    public boolean getCompleted()
    {
        return this.completed;
    }
    
    /**
     * @return the debug
     */
    public String getDebug()
    {
        return this.debug;
    }
    
    /**
     * @return the lastException
     */
    public Exception getLastException()
    {
        return this.lastException;
    }
    
    /**
     * @return the localSettings
     */
    public QueryAllConfiguration getLocalSettings()
    {
        return this.localSettings;
    }
    
    /**
     * @return the normalisedResult
     */
    public String getNormalisedResult()
    {
        if(this.normalisedResult == null || this.normalisedResult.length() == 0)
        {
            if(RdfFetcherQueryRunnable.DEBUG)
            {
                RdfFetcherQueryRunnable.log
                        .debug("RdfFetcherQueryRunnable.getNormalisedResult: no normalisation occurred, returning raw result instead");
            }
            
            return this.rawResult;
        }
        else
        {
            return this.normalisedResult;
        }
    }
    
    public String getOriginalEndpointUrl()
    {
        return this.originalEndpointUrl;
    }
    
    public String getOriginalQuery()
    {
        return this.originalQuery;
    }
    
    /**
     * @return the originalQueryBundle
     */
    public QueryBundle getOriginalQueryBundle()
    {
        return this.originalQueryBundle;
    }
    
    /**
     * @return the queryEndTime
     */
    public Date getQueryEndTime()
    {
        return this.queryEndTime;
    }
    
    /**
     * @return the queryStartTime
     */
    public Date getQueryStartTime()
    {
        return this.queryStartTime;
    }
    
    /**
     * @return the rawResult
     */
    public String getRawResult()
    {
        return this.rawResult;
    }
    
    /**
     * @return the resultDebugString
     */
    public String getResultDebugString()
    {
        return this.resultDebugString;
    }
    
    /**
     * @return the returnedContentEncoding
     */
    public String getReturnedContentEncoding()
    {
        return this.returnedContentEncoding;
    }
    
    /**
     * @return the returnedContentType
     */
    public String getReturnedContentType()
    {
        return this.returnedContentType;
    }
    
    /**
     * @return the returnedMIMEType
     */
    public String getReturnedMIMEType()
    {
        return this.returnedMIMEType;
    }
    
    /**
     * @return the wasSuccessful
     */
    public boolean getWasSuccessful()
    {
        return this.wasSuccessful;
    }
    
    public boolean notExecuted()
    {
        return !this.getCompleted();
    }
    
    /**
     * @param acceptHeader
     *            the acceptHeader to set
     */
    public void setAcceptHeader(final String acceptHeader)
    {
        this.acceptHeader = acceptHeader;
    }
    
    /**
     * @param actualEndpointUrl
     *            the actualEndpointUrl to set
     */
    public void setActualEndpointUrl(final String actualEndpointUrl)
    {
        this.actualEndpointUrl = actualEndpointUrl;
    }
    
    /**
     * @param actualQuery
     *            the actualQuery to set
     */
    public void setActualQuery(final String actualQuery)
    {
        this.actualQuery = actualQuery;
    }
    
    /**
     * @param localBlacklistController
     *            the localBlacklistController to set
     */
    public void setBlacklistController(final BlacklistController localBlacklistController)
    {
        this.localBlacklistController = localBlacklistController;
    }
    
    /**
     * @param completed
     *            the completed to set
     */
    public void setCompleted(final boolean completed)
    {
        this.completed = completed;
    }
    
    /**
     * @param debug
     *            the debug to set
     */
    public void setDebug(final String debug)
    {
        this.debug = debug;
    }
    
    /**
     * @param lastException
     *            the lastException to set
     */
    public void setLastException(final Exception lastException)
    {
        this.lastException = lastException;
    }
    
    /**
     * @param normalisedResult
     *            the normalisedResult to set
     */
    public void setNormalisedResult(final String normalisedResult)
    {
        this.normalisedResult = normalisedResult;
    }
    
    /**
     * @param endpointUrl
     *            the endpointUrl to set
     */
    public void setOriginalEndpointUrl(final String endpointUrl)
    {
        this.originalEndpointUrl = endpointUrl;
    }
    
    /**
     * @param query
     *            the query to set
     */
    public void setOriginalQuery(final String query)
    {
        this.originalQuery = query;
    }
    
    /**
     * @param originalQueryBundle
     *            the originalQueryBundle to set
     */
    public void setOriginalQueryBundle(final QueryBundle originalQueryBundle)
    {
        this.originalQueryBundle = originalQueryBundle;
    }
    
    /**
     * @param queryEndTime
     *            the queryEndTime to set
     */
    public void setQueryEndTime(final Date queryEndTime)
    {
        this.queryEndTime = queryEndTime;
    }
    
    /**
     * @param queryStartTime
     *            the queryStartTime to set
     */
    public void setQueryStartTime(final Date queryStartTime)
    {
        this.queryStartTime = queryStartTime;
    }
    
    /**
     * @param rawResult
     *            the rawResult to set
     */
    public void setRawResult(final String rawResult)
    {
        this.rawResult = rawResult;
    }
    
    /**
     * @param resultDebugString
     *            the resultDebugString to set
     */
    public void setResultDebugString(final String resultDebugString)
    {
        this.resultDebugString = resultDebugString;
    }
    
    /**
     * @param returnedContentEncoding
     *            the returnedContentEncoding to set
     */
    public void setReturnedContentEncoding(final String returnedContentEncoding)
    {
        this.returnedContentEncoding = returnedContentEncoding;
    }
    
    /**
     * @param returnedContentType
     *            the returnedContentType to set
     */
    public void setReturnedContentType(final String returnedContentType)
    {
        this.returnedContentType = returnedContentType;
    }
    
    /**
     * @param returnedMIMEType
     *            the returnedMIMEType to set
     */
    public void setReturnedMIMEType(final String returnedMIMEType)
    {
        this.returnedMIMEType = returnedMIMEType;
    }
    
    /**
     * @param localSettings
     *            the localSettings to set
     */
    public void setSettings(final QueryAllConfiguration localSettings)
    {
        this.localSettings = localSettings;
    }
    
    /**
     * @param wasSuccessful
     *            the wasSuccessful to set
     */
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
    
    public boolean wasCompletedSuccessfulQuery()
    {
        return this.getCompleted() && this.getWasSuccessful();
    }
    
    public boolean wasError()
    {
        return this.getCompleted() && this.getLastException() != null;
    }
}
