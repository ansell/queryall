/**
 * 
 */
package org.queryall;

import org.openrdf.model.URI;
import org.queryall.impl.QueryTypeImpl;

/**
 * Provides the implementation of the Provider class 
 * for the Abstract test class provided with queryall-api-tests.jar
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class QueryTypeImplTest extends AbstractQueryTypeTest
{
    @Override
    public QueryType getNewTestQueryType()
    {
        return new QueryTypeImpl();
    }

    public URI getAllNamespaceMatchMethodUri()
    {
        return QueryTypeImpl.getQueryNamespaceMatchAll();
    }

    public URI getAnyNamespaceMatchMethodUri()
    {
        return QueryTypeImpl.getQueryNamespaceMatchAny();
    }
}
