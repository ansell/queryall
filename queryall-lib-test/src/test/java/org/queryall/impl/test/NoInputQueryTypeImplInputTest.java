/**
 * 
 */
package org.queryall.impl.test;

import org.queryall.api.profile.Profile;
import org.queryall.api.querytype.InputQueryType;
import org.queryall.api.test.AbstractInputQueryTypeTest;
import org.queryall.impl.profile.ProfileImpl;
import org.queryall.impl.querytype.NoInputQueryTypeImpl;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class NoInputQueryTypeImplInputTest extends AbstractInputQueryTypeTest
{
    @Override
    public Profile getNewTestProfile()
    {
        return new ProfileImpl();
    }
    
    @Override
    public InputQueryType getNewTestInputQueryType()
    {
        return new NoInputQueryTypeImpl();
    }
}
