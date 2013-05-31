/**
 * 
 */
package org.queryall.utils.test;

import org.junit.After;
import org.junit.Before;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.utils.Settings;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class SettingsTest extends BaseQueryAllConfigurationTest
{
    
    @Override
    protected QueryAllConfiguration getNewQueryAllConfiguration()
    {
        return new Settings();
    }
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
    }
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    @After
    public void tearDown() throws Exception
    {
        super.tearDown();
    }
    
}
