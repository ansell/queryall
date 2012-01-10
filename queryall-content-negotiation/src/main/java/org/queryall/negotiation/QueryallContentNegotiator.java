package org.queryall.negotiation;

import de.fuberlin.wiwiss.pubby.negotiation.ContentTypeNegotiator;
import de.fuberlin.wiwiss.pubby.negotiation.MediaRangeSpec;

import java.util.regex.Pattern;

import org.queryall.api.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class QueryallContentNegotiator
{
    private static final Logger LOG = LoggerFactory.getLogger(QueryallContentNegotiator.class);
    private static final boolean TRACE = QueryallContentNegotiator.LOG.isTraceEnabled();
    private static final boolean DEBUG = QueryallContentNegotiator.LOG.isDebugEnabled();
    private static final boolean INFO = QueryallContentNegotiator.LOG.isInfoEnabled();
    
    public static ContentTypeNegotiator getContentNegotiator(final String preferredDisplayContentType)
    {
        final ContentTypeNegotiator newContentNegotiator = new ContentTypeNegotiator();
        
        if(preferredDisplayContentType.equals(Constants.APPLICATION_RDF_XML))
        {
            newContentNegotiator.addVariant("application/rdf+xml;q=0.99");
            // NOTE: We don't add application/xml as it is sent by browsers who also require XSLT to
            // process it
            // .addAliasMediaType("application/xml;q=0.95");
        }
        else
        {
            newContentNegotiator.addVariant("application/rdf+xml;q=0.9");
            // .addAliasMediaType("application/xml;q=0.5");
        }
        
        if(preferredDisplayContentType.equals(Constants.TEXT_RDF_N3))
        {
            newContentNegotiator.addVariant("text/rdf+n3;q=0.99").addAliasMediaType("text/n3;q=0.99")
                    .addAliasMediaType("application/rdf+n3;q=0.8").addAliasMediaType("application/n3;q=0.8");
        }
        else
        {
            newContentNegotiator.addVariant("text/rdf+n3;q=0.85").addAliasMediaType("text/n3;q=0.85")
                    .addAliasMediaType("application/rdf+n3;q=0.5").addAliasMediaType("application/n3;q=0.5");
        }
        
        if(preferredDisplayContentType.equals(Constants.TEXT_TURTLE))
        {
            // See http://www.w3.org/TeamSubmission/turtle/ for reasoning here
            newContentNegotiator.addVariant("text/turtle;q=0.99").addAliasMediaType("application/turtle;q=0.95")
                    .addAliasMediaType("application/x-turtle;q=0.95");
        }
        else
        {
            // See http://www.w3.org/TeamSubmission/turtle/ for reasoning here
            newContentNegotiator.addVariant("text/turtle;q=0.8").addAliasMediaType("application/turtle;q=0.8")
                    .addAliasMediaType("application/x-turtle;q=0.5");
        }
        
        if(preferredDisplayContentType.equals(Constants.TEXT_HTML)
                || preferredDisplayContentType.equals(Constants.APPLICATION_XHTML_XML))
        {
            newContentNegotiator.addVariant("text/html;q=0.99").addAliasMediaType("application/html;q=0.95")
                    .addAliasMediaType("application/xhtml+xml;q=0.95");
        }
        else
        {
            // Replace the default Opera header with one that is easier to handle re the */*
            // patterns q value, changed q=0.1 to q=0.5
            newContentNegotiator
                    .addUserAgentOverride(
                            Pattern.compile("Opera/*"),
                            "text/html, application/xml;q=0.9, application/xhtml+xml, image/png, image/jpeg, image/gif, image/x-xbitmap, */*;q=0.1",
                            "text/html, application/xml;q=0.9, application/xhtml+xml, image/png, image/jpeg, image/gif, image/x-xbitmap, */*;q=0.5");
            
            newContentNegotiator.addVariant("text/html;q=0.4").addAliasMediaType("application/html;q=0.4")
                    .addAliasMediaType("application/xhtml+xml;q=0.4");
        }
        
        // NOTE: Currently we prefer the Talis RDF/JSON specification for application/json requests
        if(preferredDisplayContentType.equals(Constants.APPLICATION_JSON)
                || preferredDisplayContentType.equals(Constants.APPLICATION_RDF_JSON))
        {
            newContentNegotiator.addVariant("application/json;q=0.99").addAliasMediaType("application/rdf+json;q=0.95");
        }
        else
        {
            newContentNegotiator.addVariant("application/json;q=0.4").addAliasMediaType("application/rdf+json;q=0.4");
        }
        
        // NOTE: Currently we prefer the Talis RDF/JSON specification for application/json requests
        // except when JSON-LD is specifically preferred
        // If it is preferred, we return application/ld+json still to match the other cases
        if(preferredDisplayContentType.equals(Constants.APPLICATION_LD_JSON))
        {
            newContentNegotiator.addVariant("application/ld+json;q=0.99").addAliasMediaType("application/json;q=0.95");
        }
        else
        {
            newContentNegotiator.addVariant("application/ld+json;q=0.4");
        }
        
        if(preferredDisplayContentType.equals(Constants.TEXT_X_NQUADS))
        {
            newContentNegotiator.addVariant("text/x-nquads;q=0.99").addAliasMediaType("text/nquads;q=0.95");
        }
        else
        {
            newContentNegotiator.addVariant("text/x-nquads;q=0.4").addAliasMediaType("text/nquads;q=0.4");
        }
        
        if(preferredDisplayContentType.equals(Constants.APPLICATION_X_TRIG))
        {
            newContentNegotiator.addVariant("application/x-trig;q=0.99").addAliasMediaType("application/trig;q=0.95");
        }
        else
        {
            newContentNegotiator.addVariant("application/x-trig;q=0.4").addAliasMediaType("application/trig;q=0.4");
        }
        
        if(preferredDisplayContentType.equals(Constants.APPLICATION_TRIX))
        {
            newContentNegotiator.addVariant("application/trix;q=0.99");
        }
        else
        {
            newContentNegotiator.addVariant("application/trix;q=0.4");
        }
        
        // NTriples content type was not intelligently defined, but we try to work with it anyway,
        // basically, if they ask for anything else at the same time as NTriples, they will get it
        // instead, unless it is defined as the preferred content type
        if(preferredDisplayContentType.equals(Constants.TEXT_PLAIN))
        {
            newContentNegotiator.addVariant("text/plain;q=0.95");
        }
        else
        {
            newContentNegotiator.addVariant("text/plain;q=0.2");
        }
        
        return newContentNegotiator;
    }
    
    public static String getResponseContentType(final String acceptHeader, final String userAgent,
            final ContentTypeNegotiator negotiator, final String fallback)
    {
        if(QueryallContentNegotiator.DEBUG)
        {
            QueryallContentNegotiator.LOG.debug("QueryallContentNegotiator: acceptHeader=" + acceptHeader
                    + " userAgent=" + userAgent);
        }
        
        final MediaRangeSpec bestMatch = negotiator.getBestMatch(acceptHeader, userAgent);
        
        if(bestMatch == null)
        {
            if(QueryallContentNegotiator.TRACE)
            {
                QueryallContentNegotiator.LOG
                        .trace("QueryallContentNegotiator: bestMatch not found, returning fallback instead");
            }
            
            return fallback;
        }
        
        return bestMatch.getMediaType();
    }
}
