/**
 * 
 */
package org.queryall.impl.test;

import org.queryall.api.querytype.RdfInputQueryType;
import org.queryall.api.querytype.RegexInputQueryType;
import org.queryall.api.test.AbstractRegexInputQueryTypeTest;
import org.queryall.impl.querytype.RdfInputQueryTypeImpl;
import org.queryall.impl.querytype.RegexInputQueryTypeImpl;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RdfInputQueryTypeImpl3Test extends AbstractRdfInputQueryTypeTest
{
    @Override
    public RdfInputQueryType getNewTestRdfInputQueryType()
    {
        return new RdfInputQueryTypeImpl();
    }
    
}
