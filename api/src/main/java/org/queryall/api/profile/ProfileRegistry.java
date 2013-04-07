/**
 * 
 */
package org.queryall.api.profile;

import org.queryall.api.services.AbstractServiceLoader;

/**
 * Dynamically loads and keeps a track of the different Profile's that are available.
 * 
 * Uses ProfileEnum objects as keys, as defined in ProfileFactory
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProfileRegistry extends AbstractServiceLoader<ProfileEnum, ProfileFactory>
{
    private static final Object LOCK = new Object();
    private static volatile ProfileRegistry defaultRegistry;
    
    public static ProfileRegistry getInstance()
    {
        if(ProfileRegistry.defaultRegistry == null)
        {
            synchronized(ProfileRegistry.LOCK)
            {
                if(ProfileRegistry.defaultRegistry == null)
                {
                    ProfileRegistry.defaultRegistry = new ProfileRegistry();
                }
            }
        }
        
        return ProfileRegistry.defaultRegistry;
        
    }
    
    public ProfileRegistry()
    {
        super(ProfileFactory.class);
    }
    
    @Override
    protected ProfileEnum getKey(final ProfileFactory factory)
    {
        return factory.getEnum();
    }
    
}
