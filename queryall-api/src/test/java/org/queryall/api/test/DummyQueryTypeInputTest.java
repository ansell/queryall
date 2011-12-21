/**
 * 
 */
package org.queryall.api.test;

import org.queryall.api.profile.Profile;
import org.queryall.api.querytype.InputQueryType;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class DummyQueryTypeInputTest extends AbstractInputQueryTypeTest
{
    
    @Override
    public Profile getNewTestProfile()
    {
        return new DummyProfile();
    }
    
    @Override
    public InputQueryType getNewTestInputQueryType()
    {
        return new DummyQueryType();
    }
    
}
