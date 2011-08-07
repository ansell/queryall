/**
 * 
 */
package org.queryall.impl.test;

import org.openrdf.model.URI;
import org.queryall.api.Profile;
import org.queryall.api.test.AbstractProfileTest;
import org.queryall.impl.ProfileImpl;

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
        return ProfileImpl.getExcludeThenIncludeUri();
    }
    
    @Override
    public URI getProfileIncludeExcludeOrderUndefinedUri()
    {
        return ProfileImpl.getProfileIncludeExcludeOrderUndefinedUri();
    }
    
    @Override
    public URI getProfileIncludeThenExcludeURI()
    {
        return ProfileImpl.getIncludeThenExcludeUri();
    }
}
