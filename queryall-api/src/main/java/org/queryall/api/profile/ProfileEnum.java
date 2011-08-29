/**
 * 
 */
package org.queryall.api.profile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.openrdf.model.URI;
import org.queryall.api.querytype.QueryTypeEnum;
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
    protected static final Collection<ProfileEnum> ALL_PROFILES = new ArrayList<ProfileEnum>(5);
    
    public static Collection<ProfileEnum> byTypeUris(final List<URI> nextProfileUris)
    {
        if(nextProfileUris.size() == 0)
        {
            ProfileEnum.log.info("found an empty URI set for nextProfileUris=" + nextProfileUris);
            return Collections.emptyList();
        }
        
        final List<ProfileEnum> results = new ArrayList<ProfileEnum>(ProfileEnum.ALL_PROFILES.size());
        
        for(final ProfileEnum nextProfileEnum : ProfileEnum.ALL_PROFILES)
        {
            boolean matching = (nextProfileEnum.getTypeURIs().size() == nextProfileUris.size());
            
            for(final URI nextURI : nextProfileEnum.getTypeURIs())
            {
                if(!nextProfileUris.contains(nextURI))
                {
                    ProfileEnum.log.info("found an empty URI set for nextURI=" + nextURI.stringValue());
                    
                    matching = false;
                }
            }
            
            if(matching)
            {
                ProfileEnum.log.info("found an matching URI set for nextProfileUris=" + nextProfileUris);
                results.add(nextProfileEnum);
            }
        }
        
        ProfileEnum.log.info("returning results.size()=" + results.size() + " for nextProfileUris="
                + nextProfileUris);
        
        return results;
    }
    
    /**
     * Registers the specified profile.
     */
    public static void register(final ProfileEnum nextProfile)
    {
        if(ProfileEnum.valueOf(nextProfile.getName()) != null)
        {
            QueryAllEnum.log.error("Cannot register this profile again name=" + nextProfile.getName());
        }
        else
        {
            ProfileEnum.ALL_PROFILES.add(nextProfile);
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
        for(final ProfileEnum nextProfileEnum : ProfileEnum.ALL_PROFILES)
        {
            if(nextProfileEnum.getName().equals(string))
            {
                return nextProfileEnum;
            }
        }
        
        return null;
    }
    
    /**
     * Returns all known/registered profiles.
     */
    public static Collection<ProfileEnum> values()
    {
        return Collections.unmodifiableCollection(ProfileEnum.ALL_PROFILES);
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
        ProfileEnum.ALL_PROFILES.add(this);
    }
}
