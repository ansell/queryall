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
    public DummyAllBlacklistedController(final QueryAllConfiguration queryAllConfiguration)
    {
        super(queryAllConfiguration);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.blacklist.BlacklistController#isClientBlacklisted(java.lang.String)
     */
    @Override
    public boolean isClientBlacklisted(final String nextClientIPAddress)
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
    public boolean isClientPermanentlyBlacklisted(final String nextClientIPAddress)
    {
        return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.blacklist.BlacklistController#isEndpointBlacklisted(java.lang.String)
     */
    @Override
    public boolean isEndpointBlacklisted(final String nextEndpointUrl)
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
    public boolean isEndpointBlacklisted(final String nextEndpointUrl, final int blacklistMaxAccumulatedFailures,
            final long blacklistResetPeriodMilliseconds, final boolean blacklistResetClientBlacklistWithEndpoints)
    {
        return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.queryall.blacklist.BlacklistController#isUrlBlacklisted(java.lang.String)
     */
    @Override
    public boolean isUrlBlacklisted(final String inputUrl)
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
    public boolean isUrlBlacklisted(final String inputUrl, final int blacklistMaxAccumulatedFailures,
            final long blacklistResetPeriodMilliseconds, final boolean blacklistResetClientBlacklistWithEndpoints)
    {
        return true;
    }
    
}
