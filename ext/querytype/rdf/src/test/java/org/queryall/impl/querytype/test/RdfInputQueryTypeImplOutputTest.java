/**
 * 
 */
package org.queryall.impl.querytype.test;

import org.queryall.api.profile.Profile;
import org.queryall.api.querytype.OutputQueryType;
import org.queryall.api.test.AbstractOutputQueryTypeTest;
import org.queryall.api.test.DummyProfile;
import org.queryall.impl.querytype.RdfInputQueryTypeImpl;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RdfInputQueryTypeImplOutputTest extends AbstractOutputQueryTypeTest
{
    @Override
    public OutputQueryType getNewTestOutputQueryType()
    {
        return new RdfInputQueryTypeImpl();
    }
    
    @Override
    public Profile getNewTestProfile()
    {
        return new DummyProfile();
    }
}
