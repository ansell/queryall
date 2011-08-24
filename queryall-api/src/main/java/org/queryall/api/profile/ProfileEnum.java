/**
 * 
 */
package org.queryall.api.profile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.openrdf.model.URI;
import org.queryall.api.services.QueryAllEnum;

/**
 * Profile implementations register themselves with this enumeration when they are loaded.
 * 
 * NOTE: This is not an enumeration as java does not support extensible enumerations, but it should
 * act like one for all purposes other than java's underpowered switch case statement.
 * 
 */
public class ProfileEnum extends QueryAllEnum
{
    protected static final Collection<ProfileEnum> ALL_PROJECTS = new ArrayList<ProfileEnum>(5);
    
    public static Collection<ProfileEnum> byTypeUris(final List<URI> nextProfileUris)
    {
        final List<ProfileEnum> results = new ArrayList<ProfileEnum>(ProfileEnum.ALL_PROJECTS.size());
        
        for(final ProfileEnum nextProfileEnum : ProfileEnum.ALL_PROJECTS)
        {
            if(nextProfileEnum.getTypeURIs().equals(nextProfileUris))
            {
                results.add(nextProfileEnum);
            }
        }
        
        return results;
    }
    
    /**
     * Registers the specified query type.
     */
    public static void register(final ProfileEnum nextProfile)
    {
        if(ProfileEnum.valueOf(nextProfile.getName()) != null)
        {
            QueryAllEnum.log.error("Cannot register this project again name=" + nextProfile.getName());
        }
        else
        {
            ProfileEnum.ALL_PROJECTS.add(nextProfile);
        }
    }
    
    public static ProfileEnum register(final String name, final List<URI> typeURIs)
    {
        final ProfileEnum newProfileEnum = new ProfileEnum(name, typeURIs);
        ProfileEnum.register(newProfileEnum);
        return newProfileEnum;
    }
    
    public static ProfileEnum valueOf(final String string)
    {
        for(final ProfileEnum nextProfileEnum : ProfileEnum.ALL_PROJECTS)
        {
            if(nextProfileEnum.getName().equals(string))
            {
                return nextProfileEnum;
            }
        }
        
        return null;
    }
    
    /**
     * Returns all known/registered query types.
     */
    public static Collection<ProfileEnum> values()
    {
        return Collections.unmodifiableCollection(ProfileEnum.ALL_PROJECTS);
    }
    
    /**
     * Create a new Profile enum using the given name, which must be unique
     * 
     * @param nextName
     * @param nextTypeURIs
     */
    public ProfileEnum(final String nextName, final List<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
    }
}
