/**
 * 
 */
package org.queryall.impl.test;

import org.openrdf.model.URI;
import org.queryall.api.ProfilableInterface;
import org.queryall.api.Profile;
import org.queryall.impl.querytype.QueryTypeImpl;

/**
 * Provides the implementation of the Provider class for the Abstract test class provided with
 * queryall-api-tests.jar
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class ProfilableQueryTypeImplTest extends BaseProfilableTest
{
    @Override
    public ProfilableInterface getNewTestProfilable()
    {
        return new QueryTypeImpl();
    }
    
    @Override
    public void includeFalseUri(final Profile profilable, final URI uriToExclude)
    {
        profilable.addExcludeQueryType(uriToExclude);
    }
    
    @Override
    public void includeTrueUri(final Profile profilable, final URI uriToInclude)
    {
        profilable.addIncludeQueryType(uriToInclude);
    }
}
