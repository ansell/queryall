/**
 * 
 */
package org.queryall.impl.project;

import org.kohsuke.MetaInfServices;
import org.queryall.api.project.ProjectEnum;
import org.queryall.api.project.ProjectFactory;
import org.queryall.api.project.ProjectParser;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices
public class ProjectImplFactory implements ProjectFactory
{
    /**
     * Returns the enumeration from the enumeration that matches this factory
     */
    @Override
    public ProjectEnum getEnum()
    {
        return ProjectImplEnum.PROJECT_IMPL_ENUM;
        // return NormalisationRuleEnum.valueOf(RegexNormalisationRuleImpl.class.getName());
    }
    
    /**
     * Returns the parser from the enumeration that matches this factory
     */
    @Override
    public ProjectParser getParser()
    {
        return new ProjectImplParser();
    }
    
}
