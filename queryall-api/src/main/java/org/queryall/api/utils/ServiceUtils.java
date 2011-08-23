/**
 * 
 */
package org.queryall.api.utils;

import org.openrdf.query.QueryLanguage;
import org.openrdf.query.UnsupportedQueryLanguageException;
import org.openrdf.query.parser.QueryParser;
import org.openrdf.query.parser.QueryParserFactory;
import org.openrdf.query.parser.QueryParserRegistry;

/**
 * 
 * 
 * http://solitarygeek.com/java/a-simple-pluggable-java-application
 */
public class ServiceUtils 
{

	/**
	 * 
	 */
	public ServiceUtils() 
	{


	}

	public static QueryTypeParser createParser(QueryTypeEnum queryType)
			throws UnsupportedQueryLanguageException
		{
			QueryTypeFactory factory = QueryTypeRegistry.getInstance().get(queryType);

			if (factory != null) 
			{
				return factory.getParser();
			}

			throw new UnsupportedQueryLanguageException("No factory available for query type " + queryType);
		}
}
