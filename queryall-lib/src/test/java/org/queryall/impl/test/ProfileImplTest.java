/**
 * 
 */
package org.queryall.impl.test;

import org.openrdf.model.URI;
import org.queryall.api.profile.Profile;
import org.queryall.api.test.AbstractProfileTest;
import org.queryall.impl.profile.ProfileImpl;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProfileImplTest extends AbstractProfileTest
{
    @Override
    public Profile getNewTestProfile()
    {
        return new ProfileImpl();
    }
    
    @Override
    public URI getProfileExcludeThenIncludeURI()
    {
        return ProfileImpl.getProfileExcludeThenIncludeUri();
    }
    
    @Override
    public URI getProfileIncludeExcludeOrderUndefinedUri()
    {
        return ProfileImpl.getProfileIncludeExcludeOrderUndefinedUri();
    }
    
    @Override
    public URI getProfileIncludeThenExcludeURI()
    {
        return ProfileImpl.getProfileIncludeThenExcludeUri();
    }
}
