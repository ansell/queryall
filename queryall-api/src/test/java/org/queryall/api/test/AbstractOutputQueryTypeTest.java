package org.queryall.api.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.queryall.api.querytype.OutputQueryType;
import org.queryall.api.querytype.QueryType;

public abstract class AbstractOutputQueryTypeTest extends AbstractQueryTypeTest
{
    
    /**
     * This method needs to be overriden to return a new instance of the implemented OutputQueryType
     * for each call
     * 
     * @return A new instance of the OutputQueryType implemented class
     */
    public abstract OutputQueryType getNewTestOutputQueryType();
    
    @Override
    public final QueryType getNewTestQueryType()
    {
        return this.getNewTestOutputQueryType();
    }
    
    @Override
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
    }
    
    @Override
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
    
}
