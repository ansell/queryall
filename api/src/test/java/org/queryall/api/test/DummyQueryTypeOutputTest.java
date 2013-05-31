/**
 * 
 */
package org.queryall.api.test;

import org.queryall.api.profile.Profile;
import org.queryall.api.querytype.OutputQueryType;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class DummyQueryTypeOutputTest extends AbstractOutputQueryTypeTest
{
    
    @Override
    public OutputQueryType getNewTestOutputQueryType()
    {
        return new DummyQueryType();
    }
    
    @Override
    public Profile getNewTestProfile()
    {
        return new DummyProfile();
    }
    
}
