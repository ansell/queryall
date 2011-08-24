/**
 * 
 */
package org.queryall.api.utils;

/**
 * Dynamically loads and keeps a track of the different QueryType's that are available.
 * 
 * Uses QueryTypeEnum objects as keys, as defined in QueryTypeFactory
 *
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class QueryTypeRegistry extends AbstractServiceLoader<QueryTypeEnum, QueryTypeFactory>
{
	private static QueryTypeRegistry defaultRegistry;

    //  RDFParserRegistry.getInstance();
    //  
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
