package org.queryall.queryutils;

import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.openrdf.model.URI;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class QueryDebug
{
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(QueryDebug.class.getName());
    
    private long totalTimeMilliseconds = 0;
    private String clientIPAddress = "";
    private String queryString = "";
    private Collection<URI> matchingQueryTitles = new HashSet<URI>();
    
    @Override
    public String toString()
    {
        String result =
                "Client IP Address = " + getClientIPAddress() + ", Total query time = " + getTotalTimeMilliseconds()
                        + ", queryString = " + getQueryString() + ", relevantQueryTitles=";
        
        Collection<URI> uniqueTitles = new HashSet<URI>();
        
        for(URI nextQueryTitle : getMatchingQueryTitles())
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
    
    /**
     * @return the matchingQueryTitles
     */
    public Collection<URI> getMatchingQueryTitles()
    {
        return matchingQueryTitles;
    }
    
    /**
     * @param matchingQueryTitles
     *            the matchingQueryTitles to set
     */
    public void setMatchingQueryTitles(Collection<URI> matchingQueryTitles)
    {
        this.matchingQueryTitles = matchingQueryTitles;
    }
    
    /**
     * @return the queryString
     */
    public String getQueryString()
    {
        return queryString;
    }
    
    /**
     * @param queryString
     *            the queryString to set
     */
    public void setQueryString(String queryString)
    {
        this.queryString = queryString;
    }
    
    /**
     * @return the clientIPAddress
     */
    public String getClientIPAddress()
    {
        return clientIPAddress;
    }
    
    /**
     * @param clientIPAddress
     *            the clientIPAddress to set
     */
    public void setClientIPAddress(String clientIPAddress)
    {
        this.clientIPAddress = clientIPAddress;
    }
    
    /**
     * @return the totalTimeMilliseconds
     */
    public long getTotalTimeMilliseconds()
    {
        return totalTimeMilliseconds;
    }
    
    /**
     * @param totalTimeMilliseconds
     *            the totalTimeMilliseconds to set
     */
    public void setTotalTimeMilliseconds(long totalTimeMilliseconds)
    {
        this.totalTimeMilliseconds = totalTimeMilliseconds;
    }
    
}
