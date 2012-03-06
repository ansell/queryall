package org.queryall.query;

import java.util.Date;
import java.util.concurrent.Callable;

import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.blacklist.BlacklistController;

public interface RdfFetcherQueryRunnable extends Runnable, Callable<String>
{
    
    /**
     * @return the acceptHeader
     */
    String getAcceptHeader();
    
    /**
     * @return the actualEndpointUrl
     */
    String getActualEndpointUrl();
    
    /**
     * @return the actualQuery
     */
    String getActualQuery();
    
    /**
     * @return the localBlacklistController
     */
    BlacklistController getBlacklistController();
    
    /**
     * @return the completed
     */
    boolean getCompleted();
    
    /**
     * @return the debug
     */
    String getDebug();
    
    /**
     * @return the lastException
     */
    Exception getLastException();
    
    /**
     * @return the localSettings
     */
    QueryAllConfiguration getLocalSettings();
    
    /**
     * @return the normalisedResult
     */
    String getNormalisedResult();
    
    String getOriginalEndpointUrl();
    
    String getOriginalQuery();
    
    /**
     * @return the originalQueryBundle
     */
    QueryBundle getOriginalQueryBundle();
    
    /**
     * @return the queryEndTime
     */
    Date getQueryEndTime();
    
    /**
     * @return the queryStartTime
     */
    Date getQueryStartTime();
    
    /**
     * @return the rawResult
     */
    String getRawResult();
    
    /**
     * @return the resultDebugString
     */
    String getResultDebugString();
    
    /**
     * @return the returnedContentEncoding
     */
    String getReturnedContentEncoding();
    
    /**
     * @return the returnedContentType
     */
    String getReturnedContentType();
    
    /**
     * @return the returnedMIMEType
     */
    String getReturnedMIMEType();
    
    /**
     * @return the wasSuccessful
     */
    boolean getWasSuccessful();
    
    boolean notExecuted();
    
    /**
     * @param acceptHeader
     *            the acceptHeader to set
     */
    void setAcceptHeader(final String acceptHeader);
    
    /**
     * @param actualEndpointUrl
     *            the actualEndpointUrl to set
     */
    void setActualEndpointUrl(final String actualEndpointUrl);
    
    /**
     * @param actualQuery
     *            the actualQuery to set
     */
    void setActualQuery(final String actualQuery);
    
    /**
     * @param localBlacklistController
     *            the localBlacklistController to set
     */
    void setBlacklistController(final BlacklistController localBlacklistController);
    
    /**
     * @param completed
     *            the completed to set
     */
    void setCompleted(final boolean completed);
    
    /**
     * @param debug
     *            the debug to set
     */
    void setDebug(final String debug);
    
    /**
     * @param lastException
     *            the lastException to set
     */
    void setLastException(final Exception lastException);
    
    /**
     * @param normalisedResult
     *            the normalisedResult to set
     */
    void setNormalisedResult(final String normalisedResult);
    
    /**
     * @param endpointUrl
     *            the endpointUrl to set
     */
    void setOriginalEndpointUrl(final String endpointUrl);
    
    /**
     * @param query
     *            the query to set
     */
    void setOriginalQuery(final String query);
    
    /**
     * @param originalQueryBundle
     *            the originalQueryBundle to set
     */
    void setOriginalQueryBundle(final QueryBundle originalQueryBundle);
    
    /**
     * @param queryEndTime
     *            the queryEndTime to set
     */
    void setQueryEndTime(final Date queryEndTime);
    
    /**
     * @param queryStartTime
     *            the queryStartTime to set
     */
    void setQueryStartTime(final Date queryStartTime);
    
    /**
     * @param rawResult
     *            the rawResult to set
     */
    void setRawResult(final String rawResult);
    
    /**
     * @param resultDebugString
     *            the resultDebugString to set
     */
    void setResultDebugString(final String resultDebugString);
    
    /**
     * @param returnedContentEncoding
     *            the returnedContentEncoding to set
     */
    void setReturnedContentEncoding(final String returnedContentEncoding);
    
    /**
     * @param returnedContentType
     *            the returnedContentType to set
     */
    void setReturnedContentType(final String returnedContentType);
    
    /**
     * @param returnedMIMEType
     *            the returnedMIMEType to set
     */
    void setReturnedMIMEType(final String returnedMIMEType);
    
    /**
     * @param localSettings
     *            the localSettings to set
     */
    void setSettings(final QueryAllConfiguration localSettings);
    
    /**
     * @param wasSuccessful
     *            the wasSuccessful to set
     */
    void setWasSuccessful(final boolean wasSuccessful);
    
    boolean wasCompletedSuccessfulQuery();
    
    boolean wasError();
    
}