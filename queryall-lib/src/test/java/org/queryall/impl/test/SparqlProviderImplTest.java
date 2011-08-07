/**
 * 
 */
package org.queryall.impl.test;

import org.queryall.api.SparqlProvider;
import org.queryall.api.test.AbstractSparqlProviderTest;
import org.queryall.impl.HttpProviderImpl;

/**
 * Provides the implementation of the Provider class for the Abstract test class provided with
 * queryall-api-tests.jar
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class SparqlProviderImplTest extends AbstractSparqlProviderTest
{
    @Override
    public SparqlProvider getNewTestSparqlProvider()
    {
        return new HttpProviderImpl();
    }
}
