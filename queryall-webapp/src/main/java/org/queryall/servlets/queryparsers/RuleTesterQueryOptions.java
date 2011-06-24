package org.queryall.servlets.queryparsers;

import org.apache.log4j.Logger;

/** 
 * Parses query options out of a query string
 */

public class RuleTesterQueryOptions
{
    public static final Logger log = Logger.getLogger(RuleTesterQueryOptions.class.getName());
    public static final boolean _TRACE = log.isTraceEnabled();
    public static final boolean _DEBUG = log.isDebugEnabled();
    public static final boolean _INFO = log.isInfoEnabled();
    
    private boolean _hasTestUri = false;
    private String _testUri = "";
    private String _requestString = "";

    public RuleTesterQueryOptions(String testUri)
    {
        _requestString = testUri;
        
        if(_requestString != null && _requestString.startsWith("/"))
        {
            log.error("requestString="+_requestString);
            _requestString = _requestString.substring(1);
            log.error("requestString="+_requestString);
        }
        
        parseForTestUri(_requestString);
    }
    
    private void parseForTestUri(String testUri)
    {
        if(testUri != null && testUri.trim().length() > 0)
        {
            _testUri = testUri;
            _hasTestUri = true;
        }
    }
    
    public boolean hasTestUri()
    {
        return _hasTestUri;
    }

    public String getTestUri()
    {
        return _testUri;
    }

}
