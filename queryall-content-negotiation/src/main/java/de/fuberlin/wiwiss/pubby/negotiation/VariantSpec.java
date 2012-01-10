package de.fuberlin.wiwiss.pubby.negotiation;

import java.util.ArrayList;
import java.util.List;

public class VariantSpec
{
    private MediaRangeSpec type;
    private List<MediaRangeSpec> aliases = new ArrayList<MediaRangeSpec>();
    boolean isDefault = false;
    
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