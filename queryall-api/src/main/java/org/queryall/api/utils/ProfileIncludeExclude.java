/**
 * 
 */
package org.queryall.api.utils;

import org.openrdf.model.URI;
import org.queryall.api.profile.ProfileSchema;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public enum ProfileIncludeExclude
{
    INCLUDE_THEN_EXCLUDE(ProfileSchema.getProfileIncludeThenExcludeUri()),
    
    EXCLUDE_THEN_INCLUDE(ProfileSchema.getProfileExcludeThenIncludeUri()),
    
    UNDEFINED(ProfileSchema.getProfileIncludeExcludeOrderUndefinedUri());
    
    public static ProfileIncludeExclude valueOf(final URI nextUri)
    {
        for(final ProfileIncludeExclude nextEnum : ProfileIncludeExclude.values())
        {
            if(nextEnum.getUri().equals(nextUri))
            {
                return nextEnum;
            }
        }
        
        throw new IllegalArgumentException("Profile Include Exclude order was not found for URI nextUri=" + nextUri);
    }
    
    private URI uri;
    
    ProfileIncludeExclude(final URI nextUri)
    {
        this.uri = nextUri;
    }
    
    public URI getUri()
    {
        return this.uri;
    }
}
