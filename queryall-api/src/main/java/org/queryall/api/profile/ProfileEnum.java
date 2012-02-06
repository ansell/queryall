/**
 * 
 */
package org.queryall.api.profile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
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
    private static final Logger LOG = LoggerFactory.getLogger(ProfileEnum.class);
    @SuppressWarnings("unused")
    private static final boolean TRACE = ProfileEnum.LOG.isTraceEnabled();
    private static final boolean DEBUG = ProfileEnum.LOG.isDebugEnabled();
    @SuppressWarnings("unused")
    private static final boolean INFO = ProfileEnum.LOG.isInfoEnabled();
    
    protected static final Set<ProfileEnum> ALL_PROFILES = new HashSet<ProfileEnum>();
    
    public static Collection<ProfileEnum> byTypeUris(final Set<URI> nextTypeUris)
    {
        if(nextTypeUris.size() == 0)
        {
            if(ProfileEnum.DEBUG)
            {
                ProfileEnum.LOG.debug("found an empty URI set for nextProfileUris=" + nextTypeUris);
            }
            
            return Collections.emptyList();
        }
        
        final List<ProfileEnum> results = new ArrayList<ProfileEnum>(ProfileEnum.ALL_PROFILES.size());
        
        for(final ProfileEnum nextEnum : ProfileEnum.ALL_PROFILES)
        {
            if(nextEnum.matchForTypeUris(nextTypeUris))
            {
                if(ProfileEnum.DEBUG)
                {
                    ProfileEnum.LOG.debug("found a matching URI set for nextProfileUris=" + nextTypeUris);
                }
                
                results.add(nextEnum);
            }
        }
        
        if(ProfileEnum.DEBUG)
        {
            ProfileEnum.LOG
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
            if(ProfileEnum.DEBUG)
            {
                ProfileEnum.LOG.debug("Cannot register this profile again name=" + nextProfile.getName());
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
     * Create a new Profile enum using the given name, which must be unique.
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
