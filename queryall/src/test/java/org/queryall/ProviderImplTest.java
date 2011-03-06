/**
 * 
 */
package org.queryall;

import org.queryall.impl.ProviderImpl;

/**
 * Provides the implementation of the Provider class 
 * for the Abstract test class provided with queryall-api-tests.jar
 * 
 * @author peter
 *
 */
public class ProviderImplTest extends AbstractProviderTest
{
    public Provider getNewTestProvider()
    {
        return new ProviderImpl();
    }
}
