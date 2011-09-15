/**
 * 
 */
package org.queryall.impl.test;

import org.queryall.api.profile.Profile;
import org.queryall.api.test.AbstractProfilableTest;
import org.queryall.impl.profile.ProfileImpl;

/**
 * Provides the implementation of the Provider class for the Abstract test class provided with
 * queryall-api-tests.jar
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class BaseProfilableTest extends AbstractProfilableTest
{
    @Override
    public Profile getNewTestProfile()
    {
        return new ProfileImpl();
    }
}
