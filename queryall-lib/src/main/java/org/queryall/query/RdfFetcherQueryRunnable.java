package org.queryall.query;

import java.util.Date;

import org.queryall.api.QueryAllConfiguration;
import org.queryall.blacklist.BlacklistController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RdfFetcherQueryRunnable extends Thread
{
    private static final Logger log = LoggerFactory.getLogger(RdfFetcherQueryRunnable.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = RdfFetcherQueryRunnable.log.isTraceEnabled();
    @SuppressWarnings("unused")
    private static final boolean _DEBUG = RdfFetcherQueryRunnable.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = RdfFetcherQueryRunnable.log.isInfoEnabled();
    
    private String endpointUrl = "";
    private String format = "";
    private String query = "";
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
    
    public RdfFetcherQueryRunnable(final String nextEndpointUrl, final String nextFormat, final String nextQuery,
            final String nextDebug, final String nextAcceptHeader, final QueryAllConfiguration localSettings,
            final BlacklistController localBlacklistController)
    {
        this.setEndpointUrl(nextEndpointUrl);
        this.setFormat(nextFormat);
        this.setQuery(nextQuery);
        this.setDebug(nextDebug);
        this.setAcceptHeader(nextAcceptHeader);
        this.setSettings(localSettings);
        this.setBlacklistController(localBlacklistController);
    }
    
    public RdfFetcherQueryRunnable(final String nextEndpointUrl, final String nextFormat, final String nextQuery,
            final String nextDebug, final String nextAcceptHeader, final QueryAllConfiguration localSettings,
            final BlacklistController localBlacklistController, final QueryBundle nextOriginalQueryBundle)
    {
        this(nextEndpointUrl, nextFormat, nextQuery, nextDebug, nextAcceptHeader, localSettings,
                localBlacklistController);
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
    
    public String getEndpointUrl()
    {
        return this.endpointUrl;
    }
    
    /**
     * @return the format
     */
    public String getFormat()
    {
        return this.format;
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
        if(this.normalisedResult == null || this.normalisedResult.trim().length() == 0)
        {
            RdfFetcherQueryRunnable.log
                    .info("RdfFetcherQueryRunnable.getNormalisedResult: no normalisation occurred, returning raw result instead");
            return this.rawResult;
        }
        else
        {
            return this.normalisedResult;
        }
    }
    
    /**
     * @return the originalQueryBundle
     */
    public QueryBundle getOriginalQueryBundle()
    {
        return this.originalQueryBundle;
    }
    
    public String getQuery()
    {
        return this.query;
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
     * @param endpointUrl
     *            the endpointUrl to set
     */
    public void setEndpointUrl(final String endpointUrl)
    {
        this.endpointUrl = endpointUrl;
    }
    
    /**
     * @param format
     *            the format to set
     */
    public void setFormat(final String format)
    {
        this.format = format;
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
     * @param originalQueryBundle
     *            the originalQueryBundle to set
     */
    public void setOriginalQueryBundle(final QueryBundle originalQueryBundle)
    {
        this.originalQueryBundle = originalQueryBundle;
    }
    
    /**
     * @param query
     *            the query to set
     */
    public void setQuery(final String query)
    {
        this.query = query;
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
        return "endpointUrl=" + this.getEndpointUrl() + " query=" + this.getQuery();
    }
    
    public boolean wasEmptySuccessQuery()
    {
        return this.getWasSuccessful() && !this.getRawResult().trim().equals("")
                && this.getNormalisedResult().trim().equals("");
    }
    
    public boolean wasError()
    {
        return this.getCompleted() && this.getLastException() != null;
    }
    
    public boolean wasSuccessfulQuery()
    {
        return this.getWasSuccessful();
    }
}
