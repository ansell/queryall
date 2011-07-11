/**
 * 
 */
package org.queryall;

import org.openrdf.model.URI;
import org.queryall.api.ProfilableInterface;
import org.queryall.api.Profile;
import org.queryall.impl.QueryTypeImpl;

/**
 * Provides the implementation of the Provider class 
 * for the Abstract test class provided with queryall-api-tests.jar
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
	public void includeTrueUri(Profile profilable, URI uriToInclude) 
	{
		profilable.addIncludeQueryType(uriToInclude);
	}

	@Override
	public void includeFalseUri(Profile profilable, URI uriToExclude) 
	{
		profilable.addExcludeQueryType(uriToExclude);
	}
}
