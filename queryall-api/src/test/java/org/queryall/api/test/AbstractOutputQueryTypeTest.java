package org.queryall.api.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.queryall.api.profile.Profile;
import org.queryall.api.querytype.OutputQueryType;
import org.queryall.api.querytype.QueryType;

public abstract class AbstractOutputQueryTypeTest extends AbstractQueryTypeTest
{
    
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
    }
    
    @After
    public void tearDown() throws Exception
    {
        super.tearDown();
    }
    
    @Test
    public final void testGetOutputString()
    {
        
    }
    
    @Test
    public final void testSetOutputString()
    {
        
    }

    @Override
    public final QueryType getNewTestQueryType()
    {
        return getNewTestOutputQueryType();
    }

    /**
     * This method needs to be overriden to return a new instance of the implemented OutputQueryType for each call
     * 
     * @return A new instance of the OutputQueryType implemented class
     */
    public abstract OutputQueryType getNewTestOutputQueryType();
    
}
