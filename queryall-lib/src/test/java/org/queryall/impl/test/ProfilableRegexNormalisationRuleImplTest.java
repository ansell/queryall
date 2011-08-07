/**
 * 
 */
package org.queryall.impl.test;

import org.queryall.api.ProfilableInterface;
import org.queryall.impl.RegexNormalisationRuleImpl;

/**
 * Provides the implementation of the Provider class for the Abstract test class provided with
 * queryall-api-tests.jar
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProfilableRegexNormalisationRuleImplTest extends BaseProfilableNormalisationRuleTest
{
    @Override
    public ProfilableInterface getNewTestProfilable()
    {
        return new RegexNormalisationRuleImpl();
    }
}
