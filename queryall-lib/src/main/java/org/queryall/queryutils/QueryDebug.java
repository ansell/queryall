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
    
    /**
     * @return the clientIPAddress
     */
    public String getClientIPAddress()
    {
        return this.clientIPAddress;
    }
    
    /**
     * @return the matchingQueryTitles
     */
    public Collection<URI> getMatchingQueryTitles()
    {
        return this.matchingQueryTitles;
    }
    
    /**
     * @return the queryString
     */
    public String getQueryString()
    {
        return this.queryString;
    }
    
    /**
     * @return the totalTimeMilliseconds
     */
    public long getTotalTimeMilliseconds()
    {
        return this.totalTimeMilliseconds;
    }
    
    /**
     * @param clientIPAddress
     *            the clientIPAddress to set
     */
    public void setClientIPAddress(final String clientIPAddress)
    {
        this.clientIPAddress = clientIPAddress;
    }
    
    /**
     * @param matchingQueryTitles
     *            the matchingQueryTitles to set
     */
    public void setMatchingQueryTitles(final Collection<URI> matchingQueryTitles)
    {
        this.matchingQueryTitles = matchingQueryTitles;
    }
    
    /**
     * @param queryString
     *            the queryString to set
     */
    public void setQueryString(final String queryString)
    {
        this.queryString = queryString;
    }
    
    /**
     * @param totalTimeMilliseconds
     *            the totalTimeMilliseconds to set
     */
    public void setTotalTimeMilliseconds(final long totalTimeMilliseconds)
    {
        this.totalTimeMilliseconds = totalTimeMilliseconds;
    }
    
    @Override
    public String toString()
    {
        String result =
                "Client IP Address = " + this.getClientIPAddress() + ", Total query time = "
                        + this.getTotalTimeMilliseconds() + ", queryString = " + this.getQueryString()
                        + ", relevantQueryTitles=";
        
        final Collection<URI> uniqueTitles = new HashSet<URI>();
        
        for(final URI nextQueryTitle : this.getMatchingQueryTitles())
        {
            if(!uniqueTitles.contains(nextQueryTitle))
            {
                uniqueTitles.add(nextQueryTitle);
            }
        }
        
        for(final URI nextUniqueQueryTitle : uniqueTitles)
        {
            result += nextUniqueQueryTitle.stringValue() + ",";
        }
        
        return result;
    }
    
}
