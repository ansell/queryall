/**
 * 
 */
package org.queryall.api.utils;

/**
 *
 */
public class QueryTypeRegistry extends AbstractServiceLoader<QueryTypeEnum, QueryTypeFactory>
{

	private static QueryTypeRegistry defaultRegistry;

	public static synchronized QueryTypeRegistry getInstance() 
	{
		if (defaultRegistry == null) 
		{
			defaultRegistry = new QueryTypeRegistry();
		}

		return defaultRegistry;
	}
	
	public QueryTypeRegistry() {
		super(QueryTypeFactory.class);
	}

	@Override
	protected QueryTypeEnum getKey(QueryTypeFactory factory) 
	{
		return factory.getQueryType();
	}

}
