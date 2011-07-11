/**
 * 
 */
package org.queryall;

import org.openrdf.model.URI;
import org.queryall.api.Profile;
import org.queryall.api.Provider;
import org.queryall.impl.HttpProviderImpl;
import org.queryall.impl.ProfileImpl;

/**
 * Provides the implementation of the Provider class 
 * for the Abstract test class provided with queryall-api-tests.jar
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProviderImplTest extends AbstractProviderTest
{
    @Override
    public Provider getNewTestProvider()
    {
        return new HttpProviderImpl();
    }

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
    public URI getProfileIncludeThenExcludeURI()
    {
        return ProfileImpl.getIncludeThenExcludeUri();
    }

    @Override
    public URI getProfileIncludeExcludeOrderUndefinedUri()
    {
        return ProfileImpl.getProfileIncludeExcludeOrderUndefinedUri();
    }
}
