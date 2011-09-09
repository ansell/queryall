/**
 * 
 */
package org.queryall.impl.test;

import org.queryall.api.querytype.InputQueryType;
import org.queryall.api.test.AbstractInputQueryTypeTest;
import org.queryall.impl.querytype.RegexInputQueryTypeImpl;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RegexInputQueryTypeImplTest2 extends AbstractInputQueryTypeTest
{
    @Override
    public InputQueryType getNewTestInputQueryType()
    {
        return new RegexInputQueryTypeImpl();
    }
    
}
