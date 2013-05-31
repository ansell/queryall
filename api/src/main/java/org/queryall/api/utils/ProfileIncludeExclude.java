/**
 * 
 */
package org.queryall.api.utils;

import org.openrdf.model.URI;
import org.queryall.api.profile.ProfileSchema;

/**
 * An enum with the possible include/exclude strategies to be used with ProfilableInterface objects
 * in ProfileMatch.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public enum ProfileIncludeExclude
{
    /**
     * Process include instructions first, and then exclude both explicitly and by default.
     */
    INCLUDE_THEN_EXCLUDE(ProfileSchema.getProfileIncludeThenExcludeUri()),
    
    /**
     * Process exclude instructions first, and then include explicitly, and if there are no matches,
     * include implicitly based on the users preference.
     */
    EXCLUDE_THEN_INCLUDE(ProfileSchema.getProfileExcludeThenIncludeUri()),
    
    /**
     * Undefined mode that will inherit the default mode from the current profile. It is not valid
     * for this mode to be used by profiles for their default order.
     */
    UNDEFINED(ProfileSchema.getProfileIncludeExcludeOrderUndefinedUri());
    
    /**
     * 
     * @param nextUri
     *            The URI to search for in the list of known include exclude instructions.
     * @return The include exclude instruction whose URI matches the given URI.
     * @throws IllegalArgumentException
     *             If the URI did not match any of the known instructions.
     */
    public static ProfileIncludeExclude valueOf(final URI nextUri) throws IllegalArgumentException
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
    
    /**
     * 
     * @return The URI matching this include/exclude strategy.
     */
    public URI getUri()
    {
        return this.uri;
    }
}
