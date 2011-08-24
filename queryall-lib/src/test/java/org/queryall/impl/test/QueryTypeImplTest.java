/**
 * 
 */
package org.queryall.impl.test;

import org.openrdf.model.URI;
import org.queryall.api.QueryType;
import org.queryall.api.test.AbstractQueryTypeTest;
import org.queryall.impl.querytype.QueryTypeImpl;

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
        return QueryTypeImpl.getQueryNamespaceMatchAll();
    }
    
    @Override
    public URI getAnyNamespaceMatchMethodUri()
    {
        return QueryTypeImpl.getQueryNamespaceMatchAny();
    }
    
    @Override
    public QueryType getNewTestQueryType()
    {
        return new QueryTypeImpl();
    }
}
