/**
 * 
 */
package org.queryall.impl.provider.test;

import org.queryall.api.profile.Profile;
import org.queryall.api.provider.HttpProvider;
import org.queryall.api.test.AbstractHttpProviderTest;
import org.queryall.api.test.DummyProfile;
import org.queryall.impl.provider.HttpSparqlProviderImpl;

/**
 * Provides the implementation of the Provider class for the Abstract test class provided with
 * queryall-api-tests.jar.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class HttpSparqlProviderImplHttpTest extends AbstractHttpProviderTest
{
    @Override
    public final HttpProvider getNewTestHttpProvider()
    {
        return new HttpSparqlProviderImpl();
    }
    
    @Override
    public final Profile getNewTestProfile()
    {
        return new DummyProfile();
    }
}
