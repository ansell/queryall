/**
 * 
 */
package org.queryall.impl.test;

import org.openrdf.model.URI;
import org.queryall.api.querytype.QueryType;
import org.queryall.api.querytype.QueryTypeSchema;
import org.queryall.api.test.AbstractQueryTypeTest;
import org.queryall.impl.querytype.RegexInputQueryTypeImpl;

/**
 * Provides the implementation of the Provider class for the Abstract test class provided with
 * queryall-api-tests.jar
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class QueryTypeImplTest extends AbstractQueryTypeTest
{
    @Override
    public URI getAllNamespaceMatchMethodUri()
    {
        return QueryTypeSchema.getQueryNamespaceMatchAll();
    }
    
    @Override
    public URI getAnyNamespaceMatchMethodUri()
    {
        return QueryTypeSchema.getQueryNamespaceMatchAny();
    }
    
    @Override
    public QueryType getNewTestQueryType()
    {
        return new RegexInputQueryTypeImpl();
    }
}
