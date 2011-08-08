package de.fuberlin.wiwiss.pubby.negotiation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class ContentTypeNegotiator
{
    
    private class AcceptHeaderOverride
    {
        private Pattern userAgentPattern;
        private String original;
        private String replacement;
        
        AcceptHeaderOverride(final Pattern userAgentPattern, final String original, final String replacement)
        {
            this.userAgentPattern = userAgentPattern;
            this.original = original;
            this.replacement = replacement;
        }
        
        String getReplacement()
        {
            return this.replacement;
        }
        
        @SuppressWarnings("unused")
        boolean matches(final String acceptHeader)
        {
            return this.matches(acceptHeader, null);
        }
        
        boolean matches(final String acceptHeader, final String userAgentHeader)
        {
            return (this.userAgentPattern == null || (userAgentHeader != null && !userAgentHeader.equals("") && this.userAgentPattern
                    .matcher(userAgentHeader).find())) && (this.original == null || this.original.equals(acceptHeader));
        }
    }
    
    private class Negotiation
    {
        private final List<MediaRangeSpec> ranges;
        private MediaRangeSpec bestMatchingVariant = null;
        private MediaRangeSpec bestDefaultVariant = null;
        private double bestMatchingQuality = 0;
        private double bestDefaultQuality = 0;
        
        Negotiation(final List<MediaRangeSpec> ranges)
        {
            this.ranges = ranges;
        }
        
        private void evaluateDefaultVariant(final MediaRangeSpec variant)
        {
            if(variant.getQuality() > this.bestDefaultQuality)
            {
                
                this.bestDefaultVariant = variant;
                this.bestDefaultQuality = 0.00001 * variant.getQuality();
            }
        }
        
        private void evaluateVariant(final MediaRangeSpec variant)
        {
            this.evaluateVariantAlias(variant, variant);
        }
        
        private void evaluateVariantAlias(final MediaRangeSpec variant, final MediaRangeSpec isAliasFor)
        {
            if(variant.getBestMatch(this.ranges) == null)
            {
                return;
            }
            final double q = variant.getBestMatch(this.ranges).getQuality();
            if(q * variant.getQuality() > this.bestMatchingQuality)
            {
                this.bestMatchingVariant = isAliasFor;
                this.bestMatchingQuality = q * variant.getQuality();
            }
        }
        
        MediaRangeSpec negotiate()
        {
            // Iterator it = variantSpecs.iterator();
            // while (it.hasNext()) {
            // VariantSpec variant = (VariantSpec) it.next();
            for(final VariantSpec variant : ContentTypeNegotiator.this.variantSpecs)
            {
                if(variant.isDefault)
                {
                    this.evaluateDefaultVariant(variant.getMediaType());
                }
                this.evaluateVariant(variant.getMediaType());
                final Iterator<MediaRangeSpec> aliasIt = variant.getAliases().iterator();
                while(aliasIt.hasNext())
                {
                    final MediaRangeSpec alias = aliasIt.next();
                    this.evaluateVariantAlias(alias, variant.getMediaType());
                }
            }
            return (this.bestMatchingVariant == null) ? this.bestDefaultVariant : this.bestMatchingVariant;
        }
    }
    
    public class VariantSpec
    {
        private MediaRangeSpec type;
        private List<MediaRangeSpec> aliases = new ArrayList<MediaRangeSpec>();
        private boolean isDefault = false;
        
        public VariantSpec(final String mediaType)
        {
            this.type = MediaRangeSpec.parseType(mediaType);
        }
        
        public VariantSpec addAliasMediaType(final String mediaType)
        {
            this.aliases.add(MediaRangeSpec.parseType(mediaType));
            return this;
        }
        
        public List<MediaRangeSpec> getAliases()
        {
            return this.aliases;
        }
        
        public MediaRangeSpec getMediaType()
        {
            return this.type;
        }
        
        public boolean isDefault()
        {
            return this.isDefault;
        }
        
        public void makeDefault()
        {
            this.isDefault = true;
        }
    }
    
    private static final Logger log = Logger.getLogger(ContentTypeNegotiator.class.getName());
    
    @SuppressWarnings("unused")
    private static final boolean _TRACE = ContentTypeNegotiator.log.isTraceEnabled();
    private static final boolean _DEBUG = ContentTypeNegotiator.log.isDebugEnabled();
    private static final boolean _INFO = ContentTypeNegotiator.log.isInfoEnabled();
    
    private Collection<VariantSpec> variantSpecs = new HashSet<VariantSpec>();
    
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
    
    public MediaRangeSpec getBestMatch(final String accept, String userAgent)
    {
        if(userAgent == null)
        {
            userAgent = "";
        }
        
        if(ContentTypeNegotiator._DEBUG)
        {
            ContentTypeNegotiator.log.debug("ContentTypeNegotiator: getBestMatch accept=" + accept + " userAgent="
                    + userAgent);
        }
        
        // Iterator it = userAgentOverrides.iterator();
        String overriddenAccept = accept;
        // while (it.hasNext()) {
        // AcceptHeaderOverride override = (AcceptHeaderOverride) it.next();
        for(final AcceptHeaderOverride override : this.userAgentOverrides)
        {
            if(override.matches(accept, userAgent))
            {
                overriddenAccept = override.getReplacement();
            }
        }
        
        if(ContentTypeNegotiator._DEBUG)
        {
            ContentTypeNegotiator.log.debug("ContentTypeNegotiator: getBestMatch overriddenAccept=" + overriddenAccept);
        }
        
        final MediaRangeSpec result = new Negotiation(this.toAcceptRanges(overriddenAccept)).negotiate();
        
        if(ContentTypeNegotiator._INFO)
        {
            ContentTypeNegotiator.log.info("ContentTypeNegotiator: getBestMatch result=" + result);
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