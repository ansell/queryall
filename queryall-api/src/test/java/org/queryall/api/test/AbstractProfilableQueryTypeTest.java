/**
 * 
 */
package org.queryall.api.test;

import org.openrdf.model.URI;
import org.queryall.api.profile.Profile;
import org.queryall.api.querytype.QueryType;

/**
 * 
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractProfilableQueryTypeTest extends AbstractProfilableTest
{
    @Override
    public abstract QueryType getNewTestProfilable();
    
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
