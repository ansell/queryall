/**
 * 
 */
package org.queryall.api.profile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.openrdf.model.URI;
import org.queryall.api.services.QueryAllEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Profile implementations register themselves with this enumeration when they are loaded.
 * 
 * NOTE: This is not an enumeration as java does not support extensible enumerations, but it should
 * act like one for all purposes other than java's underpowered switch case statement.
 * 
 */
public class ProfileEnum extends QueryAllEnum
{
    private static final Logger log = LoggerFactory.getLogger(ProfileEnum.class);
    @SuppressWarnings("unused")
    private static final boolean _TRACE = ProfileEnum.log.isTraceEnabled();
    private static final boolean _DEBUG = ProfileEnum.log.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean _INFO = ProfileEnum.log.isInfoEnabled();
    
    protected static final Collection<ProfileEnum> ALL_PROFILES = new ArrayList<ProfileEnum>(5);
    
    public static Collection<ProfileEnum> byTypeUris(final Set<URI> nextTypeUris)
    {
        if(nextTypeUris.size() == 0)
        {
            if(ProfileEnum._DEBUG)
            {
                ProfileEnum.log.debug("found an empty URI set for nextProfileUris=" + nextTypeUris);
            }
            
            return Collections.emptyList();
        }
        
        final List<ProfileEnum> results = new ArrayList<ProfileEnum>(ProfileEnum.ALL_PROFILES.size());
        
        for(final ProfileEnum nextEnum : ProfileEnum.ALL_PROFILES)
        {
            // NOTE: This restriction would force developers to include implementations for every
            // possible combination of functionalities
            // This is not likely to be practical or useful, so it is not implemented
            // The minimum restriction is that there is at least one URI, ie, the standard default
            // URI for this type of object
            // boolean matching = (nextProfileEnum.getTypeURIs().size() == nextProfileUris.size());
            boolean matching = true;
            
            for(final URI nextURI : nextTypeUris)
            {
                if(!nextEnum.getTypeURIs().contains(nextURI))
                {
                    if(ProfileEnum._DEBUG)
                    {
                        ProfileEnum.log.debug("found an empty URI set for nextURI=" + nextURI.stringValue());
                    }
                    
                    matching = false;
                }
            }
            
            if(matching)
            {
                if(ProfileEnum._DEBUG)
                {
                    ProfileEnum.log.debug("found an matching URI set for nextProfileUris=" + nextTypeUris);
                }
                
                results.add(nextEnum);
            }
        }
        
        if(ProfileEnum._DEBUG)
        {
            ProfileEnum.log
                    .debug("returning results.size()=" + results.size() + " for nextProfileUris=" + nextTypeUris);
        }
        
        return results;
    }
    
    /**
     * Registers the specified profile.
     */
    public static void register(final ProfileEnum nextProfile)
    {
        if(ProfileEnum.valueOf(nextProfile.getName()) != null)
        {
            if(ProfileEnum._DEBUG)
            {
                ProfileEnum.log.debug("Cannot register this profile again name=" + nextProfile.getName());
            }
        }
        else
        {
            ProfileEnum.ALL_PROFILES.add(nextProfile);
        }
    }
    
    public static ProfileEnum register(final String name, final Set<URI> typeURIs)
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
    public ProfileEnum(final String nextName, final Set<URI> nextTypeURIs)
    {
        super(nextName, nextTypeURIs);
        ProfileEnum.ALL_PROFILES.add(this);
    }
}
