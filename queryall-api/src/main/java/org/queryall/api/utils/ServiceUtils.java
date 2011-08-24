/**
 * 
 */
package org.queryall.api.utils;

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
