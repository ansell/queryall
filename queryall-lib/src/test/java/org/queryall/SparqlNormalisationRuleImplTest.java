/**
 * 
 */
package org.queryall;

import org.openrdf.model.URI;
import org.queryall.api.Profile;
import org.queryall.api.Provider;
import org.queryall.api.SparqlNormalisationRule;
import org.queryall.impl.HttpProviderImpl;
import org.queryall.impl.ProfileImpl;
import org.queryall.impl.ProviderImpl;
import org.queryall.impl.SparqlNormalisationRuleImpl;

/**
 * Provides the implementation of the Provider class 
 * for the Abstract test class provided with queryall-api-tests.jar
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class SparqlNormalisationRuleImplTest extends AbstractSparqlNormalisationRuleTest
{
    @Override
    public SparqlNormalisationRule getNewTestSparqlRule()
    {
        return new SparqlNormalisationRuleImpl();
    }

    @Override
    public Profile getNewTestProfile()
    {
        return new ProfileImpl();
    }

    @Override
    public URI getProfileExcludeThenIncludeURI()
    {
        return ProfileImpl.getExcludeThenIncludeUri();
    }

    @Override
    public URI getProfileIncludeThenExcludeURI()
    {
        return ProfileImpl.getIncludeThenExcludeUri();
    }

    @Override
    public URI getProfileIncludeExcludeOrderUndefinedUri()
    {
        return ProfileImpl.getProfileIncludeExcludeOrderUndefinedUri();
    }

	@Override
	public URI getSparqlRuleModeAddAllMatchingTriplesURI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI getRdfruleStageAfterResultsImportURI() {
		// TODO Auto-generated method stub
		return null;
	}
}
