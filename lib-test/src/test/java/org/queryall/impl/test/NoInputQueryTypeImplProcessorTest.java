/**
 * 
 */
package org.queryall.impl.test;

import org.queryall.api.profile.Profile;
import org.queryall.api.querytype.ProcessorQueryType;
import org.queryall.api.test.AbstractProcessorQueryTypeTest;
import org.queryall.api.test.DummyProfile;
import org.queryall.impl.querytype.NoInputQueryTypeImpl;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class NoInputQueryTypeImplProcessorTest extends AbstractProcessorQueryTypeTest
{
    @Override
    public String getNewTestProcessingTemplateString()
    {
        return "";
    }
    
    @Override
    public ProcessorQueryType getNewTestProcessorQueryType()
    {
        return new NoInputQueryTypeImpl();
    }
    
    @Override
    public Profile getNewTestProfile()
    {
        return new DummyProfile();
    }
}
