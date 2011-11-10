/**
 * 
 */
package org.queryall.impl.querytype.test;

import org.queryall.api.querytype.InputQueryType;
import org.queryall.api.test.AbstractInputQueryTypeTest;
import org.queryall.impl.querytype.RegexInputQueryTypeImpl;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RegexInputQueryTypeImpl2Test extends AbstractInputQueryTypeTest
{
    @Override
    public InputQueryType getNewTestInputQueryType()
    {
        return new RegexInputQueryTypeImpl();
    }
    
}
