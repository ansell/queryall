package org.queryall.queryutils;

import java.util.Date;

public class RdfFetcherQueryRunnable extends Thread
{
    public String endpointUrl = "";
    public String format = "";
    public String query = "";
    public String debug = "";
    public String acceptHeader = "";
    public String returnedContentType = null;
    public String returnedMIMEType = null;
    public String returnedContentEncoding = null;
    public QueryBundle originalQueryBundle = null;
    
    public boolean wasSuccessful = false;
    public boolean completed = false;
    public Exception lastException = null;
    public String resultDebugString = "";
    
    public String rawResult = "";
    public String normalisedResult = "";
    
    public Date queryStartTime = null;
    public Date queryEndTime = null;
    
    
    public RdfFetcherQueryRunnable( String nextEndpointUrl, String nextFormat, String nextQuery, String nextDebug, String nextAcceptHeader)
    {
        this.endpointUrl = nextEndpointUrl;
        this.format = nextFormat;
        this.query = nextQuery;
        this.debug = nextDebug;
        this.acceptHeader = nextAcceptHeader;
    }

    public RdfFetcherQueryRunnable( String nextEndpointUrl, String nextFormat, String nextQuery, String nextDebug, String nextAcceptHeader, QueryBundle nextOriginalQueryBundle )
    {
        this.endpointUrl = nextEndpointUrl;
        this.format = nextFormat;
        this.query = nextQuery;
        this.debug = nextDebug;
        this.acceptHeader = nextAcceptHeader;
        this.originalQueryBundle = nextOriginalQueryBundle;
    }
    
    
    public boolean wasError()
    {
        return completed && lastException != null;
    }
    
    public boolean notExecuted()
    {
        return ! completed;
    }
    
    public boolean wasSuccessfulQuery()
    {
        return wasSuccessful;
    }
    
    public boolean wasEmptySuccessQuery()
    {
        return wasSuccessful && ! rawResult.trim().equals("") && normalisedResult.trim().equals("");
    }
    
    @Override
    public String toString()
    {
        return "endpointUrl="+endpointUrl+" query="+query;
    }
    
    public String getEndpointUrl()
    {
        return endpointUrl;
    }
    
    public String getQuery()
    {
        return query;
    }
}
