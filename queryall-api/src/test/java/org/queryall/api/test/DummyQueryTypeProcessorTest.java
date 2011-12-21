/**
 * 
 */
package org.queryall.api.test;

import org.queryall.api.profile.Profile;
import org.queryall.api.querytype.ProcessorQueryType;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class DummyQueryTypeProcessorTest extends AbstractProcessorQueryTypeTest
{
    
    @Override
    public Profile getNewTestProfile()
    {
        return new DummyProfile();
    }
    
    @Override
    public ProcessorQueryType getNewTestProcessorQueryType()
    {
        return new DummyQueryType();
    }

    @Override
    public String getNewTestProcessingTemplateString()
    {
        return "";
    }
    
}
