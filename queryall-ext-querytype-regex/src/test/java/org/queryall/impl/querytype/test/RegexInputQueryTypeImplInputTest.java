/**
 * 
 */
package org.queryall.impl.querytype.test;

import org.queryall.api.profile.Profile;
import org.queryall.api.querytype.RegexInputQueryType;
import org.queryall.api.test.AbstractRegexInputQueryTypeTest;
import org.queryall.api.test.DummyProfile;
import org.queryall.impl.querytype.RegexInputQueryTypeImpl;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RegexInputQueryTypeImplInputTest extends AbstractRegexInputQueryTypeTest
{
    @Override
    public Profile getNewTestProfile()
    {
        return new DummyProfile();
    }
    
    @Override
    public RegexInputQueryType getNewTestRegexInputQueryType()
    {
        return new RegexInputQueryTypeImpl();
    }
}
