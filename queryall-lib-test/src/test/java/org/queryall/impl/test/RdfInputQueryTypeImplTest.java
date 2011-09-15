/**
 * 
 */
package org.queryall.impl.test;

import org.queryall.api.querytype.QueryType;
import org.queryall.api.test.AbstractQueryTypeTest;
import org.queryall.impl.querytype.RdfInputQueryTypeImpl;

/**
 * Provides the implementation of the Provider class for the Abstract test class provided with
 * queryall-api-tests.jar
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RdfInputQueryTypeImplTest extends AbstractQueryTypeTest
{
    @Override
    public QueryType getNewTestQueryType()
    {
        return new RdfInputQueryTypeImpl();
    }
}
