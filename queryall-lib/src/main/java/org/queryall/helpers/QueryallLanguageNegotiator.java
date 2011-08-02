package org.queryall.helpers;

import de.fuberlin.wiwiss.pubby.negotiation.ContentTypeNegotiator;
import de.fuberlin.wiwiss.pubby.negotiation.MediaRangeSpec;

import org.apache.log4j.Logger;

public class QueryallLanguageNegotiator
{
    private static final Logger log = Logger.getLogger(QueryallLanguageNegotiator.class.getName());
    private static final boolean _TRACE = log.isTraceEnabled();
    private static final boolean _DEBUG = log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = log.isInfoEnabled();
    
    private final static ContentTypeNegotiator contentNegotiator;
    
    static
    {
        contentNegotiator = new ContentTypeNegotiator();
        
        if(Settings.getSettings().getStringProperty("preferredDisplayLanguage", "") != null 
            && !Settings.getSettings().getStringProperty("preferredDisplayLanguage", "").trim().equals(""))
        {
            contentNegotiator.addVariant(Settings.getSettings().getStringProperty("preferredDisplayLanguage", ""));
        }
        
        contentNegotiator.addVariant("en;q=0.9")
            .addAliasMediaType("en_GB;q=0.9")
            .addAliasMediaType("en_AU;q=0.9")
            .addAliasMediaType("en_CA;q=0.9")
            .addAliasMediaType("en_US;q=0.9");
        
        contentNegotiator.addVariant("de;q=0.85");
        
        contentNegotiator.addVariant("nl;q=0.85");
    }
    
    public static ContentTypeNegotiator getLanguageNegotiator()
    {
        return QueryallLanguageNegotiator.contentNegotiator;
    }
    
    public static String getResponseLanguage(String acceptHeader, String userAgent)
    {
        if(_DEBUG)
        {
            log.debug("QueryallLanguageNegotiator: acceptHeader="+acceptHeader+" userAgent="+userAgent);
        }
        
        ContentTypeNegotiator negotiator = getLanguageNegotiator();
        MediaRangeSpec bestMatch = negotiator.getBestMatch(acceptHeader, userAgent);
        
        if (bestMatch == null)
        {
            if(_TRACE)
            {
                log.trace("QueryallLanguageNegotiator: bestMatch not found, returning en instead");
            }
            
            return Settings.getSettings().getStringProperty("preferredDisplayLanguage", "");
        }
        
        if(_TRACE)
        {
            log.trace("QueryallLanguageNegotiator: bestMatch found, returning "+bestMatch.getMediaType());
        }

        return bestMatch.getMediaType();
    }
}
