/**
 * 
 */
package org.queryall.impl.profile;

import org.kohsuke.MetaInfServices;
import org.queryall.api.profile.ProfileEnum;
import org.queryall.api.profile.ProfileFactory;
import org.queryall.api.profile.ProfileParser;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices
public class ProfileImplFactory implements ProfileFactory
{
    /**
     * Returns the enumeration from the enumeration that matches this factory
     */
    @Override
    public ProfileEnum getEnum()
    {
        return ProfileImplEnum.PROFILE_IMPL_ENUM;
        // return NormalisationRuleEnum.valueOf(RegexNormalisationRuleImpl.class.getName());
    }
    
    /**
     * Returns the parser from the enumeration that matches this factory
     */
    @Override
    public ProfileParser getParser()
    {
        return new ProfileImplParser();
    }
    
}
