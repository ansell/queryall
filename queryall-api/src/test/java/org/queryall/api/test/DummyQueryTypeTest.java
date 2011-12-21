/**
 * 
 */
package org.queryall.api.test;

import org.queryall.api.profile.Profile;
import org.queryall.api.querytype.QueryType;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class DummyQueryTypeTest extends AbstractQueryTypeTest
{
    
    @Override
    public Profile getNewTestProfile()
    {
        return new DummyProfile();
    }
    
    @Override
    public QueryType getNewTestQueryType()
    {
        return new DummyQueryType();
    }
    
}
