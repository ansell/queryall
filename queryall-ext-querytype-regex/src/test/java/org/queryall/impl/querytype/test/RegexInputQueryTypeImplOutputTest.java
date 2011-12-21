/**
 * 
 */
package org.queryall.impl.querytype.test;

import org.queryall.api.profile.Profile;
import org.queryall.api.querytype.OutputQueryType;
import org.queryall.api.test.AbstractOutputQueryTypeTest;
import org.queryall.api.test.DummyProfile;
import org.queryall.impl.querytype.RegexInputQueryTypeImpl;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RegexInputQueryTypeImplOutputTest extends AbstractOutputQueryTypeTest
{
    @Override
    public Profile getNewTestProfile()
    {
        return new DummyProfile();
    }
    
    @Override
    public OutputQueryType getNewTestOutputQueryType()
    {
        return new RegexInputQueryTypeImpl();
    }
}
