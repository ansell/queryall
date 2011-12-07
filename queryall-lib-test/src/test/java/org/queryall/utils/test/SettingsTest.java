/**
 * 
 */
package org.queryall.utils.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.test.AbstractQueryAllConfigurationTest;
import org.queryall.query.Settings;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 *
 */
public class SettingsTest extends AbstractQueryAllConfigurationTest
{
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        super.tearDown();
    }
    
    @Override
    protected QueryAllConfiguration getNewQueryAllConfiguration()
    {
        return new Settings();
    }
    
}
