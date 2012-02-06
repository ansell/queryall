/**
 * 
 */
package org.queryall.api.querytype;

import org.queryall.api.services.QueryAllFactory;

/**
 * A factory for creating parsers for QueryType objects.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public interface QueryTypeFactory extends QueryAllFactory<QueryTypeEnum, QueryTypeParser, QueryType>
{
    
}
