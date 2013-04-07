/**
 * 
 */
package org.queryall.impl.querytype.test;

import org.queryall.api.profile.Profile;
import org.queryall.api.querytype.SparqlProcessorQueryType;
import org.queryall.api.test.AbstractSparqlProcessorQueryTypeTest;
import org.queryall.api.test.DummyProfile;
import org.queryall.impl.querytype.RegexInputQueryTypeImpl;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RegexInputQueryTypeImplProcessorTest extends AbstractSparqlProcessorQueryTypeTest
{
    @Override
    public Profile getNewTestProfile()
    {
        return new DummyProfile();
    }
    
    @Override
    public SparqlProcessorQueryType getNewTestSparqlProcessorQueryType()
    {
        return new RegexInputQueryTypeImpl();
    }
}
