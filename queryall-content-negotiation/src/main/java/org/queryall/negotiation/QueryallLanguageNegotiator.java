package org.queryall.negotiation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fuberlin.wiwiss.pubby.negotiation.ContentTypeNegotiator;
import de.fuberlin.wiwiss.pubby.negotiation.MediaRangeSpec;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class QueryallLanguageNegotiator
{
    private static final Logger LOG = LoggerFactory.getLogger(QueryallLanguageNegotiator.class);
    private static final boolean TRACE = QueryallLanguageNegotiator.LOG.isTraceEnabled();
    private static final boolean DEBUG = QueryallLanguageNegotiator.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = QueryallLanguageNegotiator.LOG.isInfoEnabled();
    
    // private static ContentTypeNegotiator contentNegotiator;
    
    public static ContentTypeNegotiator getLanguageNegotiator(final String preferredDisplayLanguage)
    {
        final ContentTypeNegotiator contentNegotiator = new ContentTypeNegotiator();
        
        if(preferredDisplayLanguage != null && !preferredDisplayLanguage.trim().equals(""))
        {
            contentNegotiator.addVariant(preferredDisplayLanguage);
        }
        
        contentNegotiator.addVariant("en;q=0.9").addAliasMediaType("en_GB;q=0.9").addAliasMediaType("en_AU;q=0.9")
                .addAliasMediaType("en_CA;q=0.9").addAliasMediaType("en_US;q=0.9");
        
        contentNegotiator.addVariant("de;q=0.85");
        
        contentNegotiator.addVariant("nl;q=0.85");
        
        return contentNegotiator;
    }
    
    public static String getResponseLanguage(final String acceptHeader, final String userAgent,
            final String preferredDisplayLanguage)
    {
        if(QueryallLanguageNegotiator.DEBUG)
        {
            QueryallLanguageNegotiator.LOG.debug("QueryallLanguageNegotiator: acceptHeader=" + acceptHeader
                    + " userAgent=" + userAgent);
        }
        
        final ContentTypeNegotiator negotiator =
                QueryallLanguageNegotiator.getLanguageNegotiator(preferredDisplayLanguage);
        final MediaRangeSpec bestMatch = negotiator.getBestMatch(acceptHeader, userAgent);
        
        if(bestMatch == null)
        {
            if(QueryallLanguageNegotiator.TRACE)
            {
                QueryallLanguageNegotiator.LOG
                        .trace("QueryallLanguageNegotiator: bestMatch not found, returning en instead");
            }
            
            return preferredDisplayLanguage;
        }
        
        if(QueryallLanguageNegotiator.TRACE)
        {
            QueryallLanguageNegotiator.LOG.trace("QueryallLanguageNegotiator: bestMatch found, returning "
                    + bestMatch.getMediaType());
        }
        
        return bestMatch.getMediaType();
    }
}
