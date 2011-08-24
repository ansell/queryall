/**
 * 
 */
package org.queryall.api.services;

import org.queryall.api.querytype.QueryTypeEnum;
import org.queryall.api.querytype.QueryTypeFactory;
import org.queryall.api.querytype.QueryTypeParser;
import org.queryall.api.querytype.QueryTypeRegistry;
import org.queryall.exception.UnsupportedQueryTypeException;

/**
 * Provides helper methods to interact with the various dynamic services in QueryAll
 * 
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ServiceUtils 
{

	/**
	 * 
	 */
	public ServiceUtils() 
	{


	}

	/**
	 * Creates a query type parser for the given query type enum
	 * 
	 * @param queryType
	 * @return
	 * @throws UnsupportedQueryTypeException
	 */
	public static QueryTypeParser createQueryTypeParser(QueryTypeEnum queryType)
			throws UnsupportedQueryTypeException
	{
			QueryTypeFactory factory = QueryTypeRegistry.getInstance().get(queryType);

			if (factory != null) 
			{
				return factory.getParser();
			}
			
			throw new UnsupportedQueryTypeException("No factory available for query type " + queryType);
	}
}
