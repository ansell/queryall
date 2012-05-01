/**
 * 
 */
package org.queryall.blacklist.test;

import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.blacklist.BlacklistController;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class DummyAllBlacklistedController extends BlacklistController
{
    
    /**
     * @param queryAllConfiguration
     */
    public DummyAllBlacklistedController(QueryAllConfiguration queryAllConfiguration)
    {
        super(queryAllConfiguration);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.blacklist.BlacklistController#isClientBlacklisted(java.lang.String)
     */
    @Override
    public boolean isClientBlacklisted(String nextClientIPAddress)
    {
        return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.queryall.blacklist.BlacklistController#isClientPermanentlyBlacklisted(java.lang.String)
     */
    @Override
    public boolean isClientPermanentlyBlacklisted(String nextClientIPAddress)
    {
        return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.blacklist.BlacklistController#isEndpointBlacklisted(java.lang.String)
     */
    @Override
    public boolean isEndpointBlacklisted(String nextEndpointUrl)
    {
        return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.blacklist.BlacklistController#isEndpointBlacklisted(java.lang.String, int,
     * long, boolean)
     */
    @Override
    public boolean isEndpointBlacklisted(String nextEndpointUrl, int blacklistMaxAccumulatedFailures,
            long blacklistResetPeriodMilliseconds, boolean blacklistResetClientBlacklistWithEndpoints)
    {
        return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.blacklist.BlacklistController#isUrlBlacklisted(java.lang.String)
     */
    @Override
    public boolean isUrlBlacklisted(String inputUrl)
    {
        return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.blacklist.BlacklistController#isUrlBlacklisted(java.lang.String, int, long,
     * boolean)
     */
    @Override
    public boolean isUrlBlacklisted(String inputUrl, int blacklistMaxAccumulatedFailures,
            long blacklistResetPeriodMilliseconds, boolean blacklistResetClientBlacklistWithEndpoints)
    {
        return true;
    }
    
}
