/**
 * 
 */
package org.queryall;

import org.queryall.api.Provider;
import org.queryall.impl.HttpProviderImpl;

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
}
