/**
 * 
 */
package org.queryall.impl.test;

import org.queryall.api.querytype.QueryType;
import org.queryall.api.test.AbstractQueryTypeTest;
import org.queryall.impl.querytype.RegexInputQueryTypeImpl;

/**
 * Provides the implementation of the Provider class for the Abstract test class provided with
 * queryall-api-tests.jar
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RegexInputQueryTypeImplTest extends AbstractQueryTypeTest
{
    @Override
    public QueryType getNewTestQueryType()
    {
        return new RegexInputQueryTypeImpl();
    }
}
