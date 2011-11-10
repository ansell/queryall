/**
 * 
 */
package org.queryall.impl.querytype.test;

import org.queryall.api.querytype.InputQueryType;
import org.queryall.api.test.AbstractInputQueryTypeTest;
import org.queryall.impl.querytype.RdfInputQueryTypeImpl;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RdfInputQueryTypeImpl2Test extends AbstractInputQueryTypeTest
{
    @Override
    public InputQueryType getNewTestInputQueryType()
    {
        return new RdfInputQueryTypeImpl();
    }
    
}
