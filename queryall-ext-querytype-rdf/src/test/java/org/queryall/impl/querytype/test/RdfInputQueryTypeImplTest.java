/**
 * 
 */
package org.queryall.impl.querytype.test;

import org.queryall.api.profile.Profile;
import org.queryall.api.querytype.RdfInputQueryType;
import org.queryall.api.test.AbstractRdfInputQueryTypeTest;
import org.queryall.impl.profile.ProfileImpl;
import org.queryall.impl.querytype.RdfInputQueryTypeImpl;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RdfInputQueryTypeImplTest extends AbstractRdfInputQueryTypeTest
{
    @Override
    public Profile getNewTestProfile()
    {
        return new ProfileImpl();
    }
    
    @Override
    public RdfInputQueryType getNewTestRdfInputQueryType()
    {
        return new RdfInputQueryTypeImpl();
    }
}
