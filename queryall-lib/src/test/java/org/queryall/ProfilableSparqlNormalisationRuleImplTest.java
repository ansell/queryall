/**
 * 
 */
package org.queryall;

import org.queryall.api.ProfilableInterface;
import org.queryall.impl.SparqlNormalisationRuleImpl;

/**
 * Provides the implementation of the Provider class 
 * for the Abstract test class provided with queryall-api-tests.jar
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProfilableSparqlNormalisationRuleImplTest extends BaseProfilableNormalisationRuleTest
{
    @Override
    public ProfilableInterface getNewTestProfilable()
    {
        return new SparqlNormalisationRuleImpl();
    }
}
