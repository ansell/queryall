package de.fuberlin.wiwiss.pubby.negotiation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentTypeNegotiator
{
    
    private static final Logger LOG = LoggerFactory.getLogger(ContentTypeNegotiator.class);
    
    @SuppressWarnings("unused")
    private static final boolean TRACE = ContentTypeNegotiator.LOG.isTraceEnabled();
    private static final boolean DEBUG = ContentTypeNegotiator.LOG.isDebugEnabled();
    private static final boolean INFO = ContentTypeNegotiator.LOG.isInfoEnabled();
    
    Collection<VariantSpec> variantSpecs = new HashSet<VariantSpec>();
    
    private List<MediaRangeSpec> defaultAcceptRanges = Collections.singletonList(MediaRangeSpec.parseRange("*/*"));
    
    private Collection<AcceptHeaderOverride> userAgentOverrides = new HashSet<AcceptHeaderOverride>();
    
    /**
     * Overrides the Accept header for certain user agents. This can be used to implement
     * special-case handling for user agents that send faulty Accept headers.
     * 
     * @param userAgentString
     *            A pattern to be matched against the User-Agent header; <tt>null</tt> means
     *            regardless of User-Agent
     * @param originalAcceptHeader
     *            Only override the Accept header if the user agent sends this header; <tt>null</tt>
     *            means always override
     * @param newAcceptHeader
     *            The Accept header to be used instead
     */
    public void addUserAgentOverride(final Pattern userAgentString, final String originalAcceptHeader,
            final String newAcceptHeader)
    {
        this.userAgentOverrides.add(new AcceptHeaderOverride(userAgentString, originalAcceptHeader, newAcceptHeader));
    }
    
    public VariantSpec addVariant(final String mediaType)
    {
        final VariantSpec result = new VariantSpec(mediaType);
        this.variantSpecs.add(result);
        return result;
    }
    
    public MediaRangeSpec getBestMatch(final String accept)
    {
        final MediaRangeSpec result = this.getBestMatch(accept, null);
        
        // log.info("ContentTypeNegotiator: getBestMatch result="+result.toString());
        
        return result;
    }
    
    public MediaRangeSpec getBestMatch(final String accept, final String userAgent)
    {
        String realUserAgent = userAgent;
        
        if(realUserAgent == null)
        {
            realUserAgent = "";
        }
        
        if(ContentTypeNegotiator.DEBUG)
        {
            ContentTypeNegotiator.LOG.debug("ContentTypeNegotiator: getBestMatch accept=" + accept + " realUserAgent="
                    + realUserAgent);
        }
        
        // Iterator it = userAgentOverrides.iterator();
        String overriddenAccept = accept;
        // while (it.hasNext()) {
        // AcceptHeaderOverride override = (AcceptHeaderOverride) it.next();
        for(final AcceptHeaderOverride override : this.userAgentOverrides)
        {
            if(override.matches(accept, realUserAgent))
            {
                overriddenAccept = override.getReplacement();
            }
        }
        
        if(ContentTypeNegotiator.DEBUG)
        {
            ContentTypeNegotiator.LOG.debug("ContentTypeNegotiator: getBestMatch overriddenAccept=" + overriddenAccept);
        }
        
        final MediaRangeSpec result = new Negotiation(this, this.toAcceptRanges(overriddenAccept)).negotiate();
        
        if(ContentTypeNegotiator.INFO)
        {
            ContentTypeNegotiator.LOG.info("ContentTypeNegotiator: getBestMatch result=" + result);
        }
        
        return result;
    }
    
    /**
     * Sets an Accept header to be used as the default if a client does not send an Accept header,
     * or if the Accept header cannot be parsed. Defaults to "* / *".
     */
    public void setDefaultAccept(final String accept)
    {
        this.defaultAcceptRanges = MediaRangeSpec.parseAccept(accept);
    }
    
    private List<MediaRangeSpec> toAcceptRanges(final String accept)
    {
        if(accept == null)
        {
            return this.defaultAcceptRanges;
        }
        final List<MediaRangeSpec> result = MediaRangeSpec.parseAccept(accept);
        if(result.isEmpty())
        {
            return this.defaultAcceptRanges;
        }
        return result;
    }
}
