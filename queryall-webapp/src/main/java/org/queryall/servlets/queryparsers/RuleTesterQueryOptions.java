package org.queryall.servlets.queryparsers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses query options out of a query string
 */

public class RuleTesterQueryOptions
{
    public static final Logger log = LoggerFactory.getLogger(RuleTesterQueryOptions.class);
    public static final boolean TRACE = RuleTesterQueryOptions.log.isTraceEnabled();
    public static final boolean DEBUG = RuleTesterQueryOptions.log.isDebugEnabled();
    public static final boolean INFO = RuleTesterQueryOptions.log.isInfoEnabled();
    
    private boolean _hasTestUri = false;
    private String _testUri = "";
    private String _requestString = "";
    
    public RuleTesterQueryOptions(final String testUri)
    {
        this._requestString = testUri;
        
        if(this._requestString != null && this._requestString.startsWith("/"))
        {
            RuleTesterQueryOptions.log.error("requestString=" + this._requestString);
            this._requestString = this._requestString.substring(1);
            RuleTesterQueryOptions.log.error("requestString=" + this._requestString);
        }
        
        this.parseForTestUri(this._requestString);
    }
    
    public String getTestUri()
    {
        return this._testUri;
    }
    
    public boolean hasTestUri()
    {
        return this._hasTestUri;
    }
    
    private void parseForTestUri(final String testUri)
    {
        if(testUri != null && testUri.trim().length() > 0)
        {
            this._testUri = testUri;
            this._hasTestUri = true;
        }
    }
    
}
