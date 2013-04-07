/**
 * 
 */
package org.queryall.impl.provider;

import org.kohsuke.MetaInfServices;
import org.queryall.api.provider.ProviderEnum;
import org.queryall.api.provider.ProviderFactory;
import org.queryall.api.provider.ProviderParser;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
@MetaInfServices
public class NoCommunicationProviderImplFactory implements ProviderFactory
{
    /**
     * Returns the enumeration from the enumeration that matches this factory
     */
    @Override
    public ProviderEnum getEnum()
    {
        return NoCommunicationProviderImplEnum.NO_COMMUNICATION_PROVIDER_IMPL_ENUM;
        // return NormalisationRuleEnum.valueOf(RegexNormalisationRuleImpl.class.getName());
    }
    
    /**
     * Returns the parser from the enumeration that matches this factory
     */
    @Override
    public ProviderParser getParser()
    {
        return new NoCommunicationProviderImplParser();
    }
    
}
