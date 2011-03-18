package org.queryall.queryutils;

import java.util.Date;

import org.apache.log4j.Logger;

public class RdfFetcherQueryRunnable extends Thread
{
    static final Logger log = Logger.getLogger(RdfFetcherQueryRunnable.class
            .getName());
    static final boolean _TRACE = log.isTraceEnabled();
    static final boolean _DEBUG = log.isDebugEnabled();
    static final boolean _INFO = log.isInfoEnabled();
    
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
    
    
    public RdfFetcherQueryRunnable( String nextEndpointUrl, String nextFormat, String nextQuery, String nextDebug, String nextAcceptHeader)
    {
        this.setEndpointUrl(nextEndpointUrl);
        this.setFormat(nextFormat);
        this.setQuery(nextQuery);
        this.setDebug(nextDebug);
        this.setAcceptHeader(nextAcceptHeader);
    }

    public RdfFetcherQueryRunnable( String nextEndpointUrl, String nextFormat, String nextQuery, String nextDebug, String nextAcceptHeader, QueryBundle nextOriginalQueryBundle )
    {
        this.setEndpointUrl(nextEndpointUrl);
        this.setFormat(nextFormat);
        this.setQuery(nextQuery);
        this.setDebug(nextDebug);
        this.setAcceptHeader(nextAcceptHeader);
        this.setOriginalQueryBundle(nextOriginalQueryBundle);
    }
    
    
    public boolean wasError()
    {
        return getCompleted() && getLastException() != null;
    }
    
    public boolean notExecuted()
    {
        return ! getCompleted();
    }
    
    public boolean wasSuccessfulQuery()
    {
        return getWasSuccessful();
    }
    
    public boolean wasEmptySuccessQuery()
    {
        return getWasSuccessful() && ! getRawResult().trim().equals("") && getNormalisedResult().trim().equals("");
    }
    
    @Override
    public String toString()
    {
        return "endpointUrl="+getEndpointUrl()+" query="+getQuery();
    }
    
    public String getEndpointUrl()
    {
        return endpointUrl;
    }
    
    public String getQuery()
    {
        return query;
    }

    /**
     * @param endpointUrl the endpointUrl to set
     */
    public void setEndpointUrl(String endpointUrl)
    {
        this.endpointUrl = endpointUrl;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(String format)
    {
        this.format = format;
    }

    /**
     * @return the format
     */
    public String getFormat()
    {
        return format;
    }

    /**
     * @param query the query to set
     */
    public void setQuery(String query)
    {
        this.query = query;
    }

    /**
     * @param debug the debug to set
     */
    public void setDebug(String debug)
    {
        this.debug = debug;
    }

    /**
     * @return the debug
     */
    public String getDebug()
    {
        return debug;
    }

    /**
     * @param acceptHeader the acceptHeader to set
     */
    public void setAcceptHeader(String acceptHeader)
    {
        this.acceptHeader = acceptHeader;
    }

    /**
     * @return the acceptHeader
     */
    public String getAcceptHeader()
    {
        return acceptHeader;
    }

    /**
     * @param returnedContentType the returnedContentType to set
     */
    public void setReturnedContentType(String returnedContentType)
    {
        this.returnedContentType = returnedContentType;
    }

    /**
     * @return the returnedContentType
     */
    public String getReturnedContentType()
    {
        return returnedContentType;
    }

    /**
     * @param returnedMIMEType the returnedMIMEType to set
     */
    public void setReturnedMIMEType(String returnedMIMEType)
    {
        this.returnedMIMEType = returnedMIMEType;
    }

    /**
     * @return the returnedMIMEType
     */
    public String getReturnedMIMEType()
    {
        return returnedMIMEType;
    }

    /**
     * @param returnedContentEncoding the returnedContentEncoding to set
     */
    public void setReturnedContentEncoding(String returnedContentEncoding)
    {
        this.returnedContentEncoding = returnedContentEncoding;
    }

    /**
     * @return the returnedContentEncoding
     */
    public String getReturnedContentEncoding()
    {
        return returnedContentEncoding;
    }

    /**
     * @param originalQueryBundle the originalQueryBundle to set
     */
    public void setOriginalQueryBundle(QueryBundle originalQueryBundle)
    {
        this.originalQueryBundle = originalQueryBundle;
    }

    /**
     * @return the originalQueryBundle
     */
    public QueryBundle getOriginalQueryBundle()
    {
        return originalQueryBundle;
    }

    /**
     * @param wasSuccessful the wasSuccessful to set
     */
    public void setWasSuccessful(boolean wasSuccessful)
    {
        this.wasSuccessful = wasSuccessful;
    }

    /**
     * @return the wasSuccessful
     */
    public boolean getWasSuccessful()
    {
        return wasSuccessful;
    }

    /**
     * @param completed the completed to set
     */
    public void setCompleted(boolean completed)
    {
        this.completed = completed;
    }

    /**
     * @return the completed
     */
    public boolean getCompleted()
    {
        return completed;
    }

    /**
     * @param lastException the lastException to set
     */
    public void setLastException(Exception lastException)
    {
        this.lastException = lastException;
    }

    /**
     * @return the lastException
     */
    public Exception getLastException()
    {
        return lastException;
    }

    /**
     * @param resultDebugString the resultDebugString to set
     */
    public void setResultDebugString(String resultDebugString)
    {
        this.resultDebugString = resultDebugString;
    }

    /**
     * @return the resultDebugString
     */
    public String getResultDebugString()
    {
        return resultDebugString;
    }

    /**
     * @param rawResult the rawResult to set
     */
    public void setRawResult(String rawResult)
    {
        this.rawResult = rawResult;
    }

    /**
     * @return the rawResult
     */
    public String getRawResult()
    {
        return rawResult;
    }

    /**
     * @param normalisedResult the normalisedResult to set
     */
    public void setNormalisedResult(String normalisedResult)
    {
        this.normalisedResult = normalisedResult;
    }

    /**
     * @return the normalisedResult
     */
    public String getNormalisedResult()
    {
        if(normalisedResult == null || normalisedResult.trim().length() == 0)
        {
            log.info("RdfFetcherQueryRunnable.getNormalisedResult: no normalisation occurred, returning raw result instead");
            return rawResult;
        }
        else
        {
            return normalisedResult;
        }
    }

    /**
     * @param queryStartTime the queryStartTime to set
     */
    public void setQueryStartTime(Date queryStartTime)
    {
        this.queryStartTime = queryStartTime;
    }

    /**
     * @return the queryStartTime
     */
    public Date getQueryStartTime()
    {
        return queryStartTime;
    }

    /**
     * @param queryEndTime the queryEndTime to set
     */
    public void setQueryEndTime(Date queryEndTime)
    {
        this.queryEndTime = queryEndTime;
    }

    /**
     * @return the queryEndTime
     */
    public Date getQueryEndTime()
    {
        return queryEndTime;
    }
}
