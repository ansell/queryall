package org.queryall.negotiation;

import org.apache.log4j.Logger;
import org.queryall.query.Settings;

import de.fuberlin.wiwiss.pubby.negotiation.ContentTypeNegotiator;
import de.fuberlin.wiwiss.pubby.negotiation.MediaRangeSpec;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class QueryallLanguageNegotiator
{
    private static final Logger log = Logger.getLogger(QueryallLanguageNegotiator.class.getName());
    private static final boolean _TRACE = QueryallLanguageNegotiator.log.isTraceEnabled();
    private static final boolean _DEBUG = QueryallLanguageNegotiator.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = QueryallLanguageNegotiator.log.isInfoEnabled();
    
    private final static ContentTypeNegotiator contentNegotiator;
    
    static
    {
        contentNegotiator = new ContentTypeNegotiator();
        
        if(Settings.getSettings().getStringProperty("preferredDisplayLanguage", "") != null
                && !Settings.getSettings().getStringProperty("preferredDisplayLanguage", "").trim().equals(""))
        {
            QueryallLanguageNegotiator.contentNegotiator.addVariant(Settings.getSettings().getStringProperty(
                    "preferredDisplayLanguage", ""));
        }
        
        QueryallLanguageNegotiator.contentNegotiator.addVariant("en;q=0.9").addAliasMediaType("en_GB;q=0.9")
                .addAliasMediaType("en_AU;q=0.9").addAliasMediaType("en_CA;q=0.9").addAliasMediaType("en_US;q=0.9");
        
        QueryallLanguageNegotiator.contentNegotiator.addVariant("de;q=0.85");
        
        QueryallLanguageNegotiator.contentNegotiator.addVariant("nl;q=0.85");
    }
    
    public static ContentTypeNegotiator getLanguageNegotiator()
    {
        return QueryallLanguageNegotiator.contentNegotiator;
    }
    
    public static String getResponseLanguage(final String acceptHeader, final String userAgent)
    {
        if(QueryallLanguageNegotiator._DEBUG)
        {
            QueryallLanguageNegotiator.log.debug("QueryallLanguageNegotiator: acceptHeader=" + acceptHeader
                    + " userAgent=" + userAgent);
        }
        
        final ContentTypeNegotiator negotiator = QueryallLanguageNegotiator.getLanguageNegotiator();
        final MediaRangeSpec bestMatch = negotiator.getBestMatch(acceptHeader, userAgent);
        
        if(bestMatch == null)
        {
            if(QueryallLanguageNegotiator._TRACE)
            {
                QueryallLanguageNegotiator.log
                        .trace("QueryallLanguageNegotiator: bestMatch not found, returning en instead");
            }
            
            return Settings.getSettings().getStringProperty("preferredDisplayLanguage", "");
        }
        
        if(QueryallLanguageNegotiator._TRACE)
        {
            QueryallLanguageNegotiator.log.trace("QueryallLanguageNegotiator: bestMatch found, returning "
                    + bestMatch.getMediaType());
        }
        
        return bestMatch.getMediaType();
    }
}
