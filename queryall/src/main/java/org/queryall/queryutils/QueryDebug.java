package org.queryall.queryutils;

import org.apache.log4j.Logger;
import java.util.HashSet;
import java.util.Collection;
import org.openrdf.model.URI;

public class QueryDebug
{
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(QueryDebug.class.getName());
	
	public long totalTimeMilliseconds = 0;
	public String clientIPAddress = "";
	public String queryString = "";
	public Collection<URI> matchingQueryTitles = new HashSet<URI>();
	
	public String toString()
	{
		String result = "Client IP Address = "+clientIPAddress+", Total query time = " + totalTimeMilliseconds + ", queryString = "+queryString + ", relevantQueryTitles=";
		
		Collection<URI> uniqueTitles = new HashSet<URI>();
		
		for(URI nextQueryTitle : matchingQueryTitles)
		{
			if(!uniqueTitles.contains(nextQueryTitle))
			{
				uniqueTitles.add(nextQueryTitle);
			}
		}
		
		for(URI nextUniqueQueryTitle : uniqueTitles)
		{
			result += nextUniqueQueryTitle.stringValue() + ",";
		}
		
		return result;
	}
	
}
