package de.fuberlin.wiwiss.pubby.negotiation;

import java.util.List;

public class Negotiation
{
    /**
     * 
     */
    private final ContentTypeNegotiator contentTypeNegotiator;
    private final List<MediaRangeSpec> ranges;
    private MediaRangeSpec bestMatchingVariant = null;
    private MediaRangeSpec bestDefaultVariant = null;
    private double bestMatchingQuality = 0;
    private double bestDefaultQuality = 0;
    
    Negotiation(final ContentTypeNegotiator nextContentTypeNegotiator, final List<MediaRangeSpec> nextRanges)
    {
        this.contentTypeNegotiator = nextContentTypeNegotiator;
        this.ranges = nextRanges;
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
        for(final VariantSpec variant : this.contentTypeNegotiator.variantSpecs)
        {
            if(variant.isDefault)
            {
                this.evaluateDefaultVariant(variant.getMediaType());
            }
            this.evaluateVariant(variant.getMediaType());
            for(final MediaRangeSpec alias : variant.getAliases())
            {
                this.evaluateVariantAlias(alias, variant.getMediaType());
            }
        }
        return (this.bestMatchingVariant == null) ? this.bestDefaultVariant : this.bestMatchingVariant;
    }
}