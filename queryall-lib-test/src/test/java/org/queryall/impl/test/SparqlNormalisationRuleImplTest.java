/**
 * 
 */
package org.queryall.impl.test;

import org.openrdf.model.URI;
import org.queryall.api.profile.Profile;
import org.queryall.api.profile.ProfileSchema;
import org.queryall.api.rdfrule.NormalisationRuleSchema;
import org.queryall.api.rdfrule.SparqlNormalisationRule;
import org.queryall.api.rdfrule.SparqlNormalisationRuleSchema;
import org.queryall.api.test.AbstractSparqlNormalisationRuleTest;
import org.queryall.impl.profile.ProfileImpl;
import org.queryall.impl.rdfrule.SparqlNormalisationRuleImpl;

/**
 * Provides the implementation of the Provider class for the Abstract test class provided with
 * queryall-api-tests.jar
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class SparqlNormalisationRuleImplTest extends AbstractSparqlNormalisationRuleTest
{
    @Override
    public Profile getNewTestProfile()
    {
        return new ProfileImpl();
    }
    
    @Override
    public SparqlNormalisationRule getNewTestSparqlRule()
    {
        return new SparqlNormalisationRuleImpl();
    }
    
    @Override
    public URI getProfileExcludeThenIncludeURI()
    {
        return ProfileSchema.getProfileExcludeThenIncludeUri();
    }
    
    @Override
    public URI getProfileIncludeExcludeOrderUndefinedUri()
    {
        return ProfileSchema.getProfileIncludeExcludeOrderUndefinedUri();
    }
    
    @Override
    public URI getProfileIncludeThenExcludeURI()
    {
        return ProfileSchema.getProfileIncludeThenExcludeUri();
    }
    
    @Override
    public URI getRdfruleStageAfterQueryCreationURI()
    {
        return NormalisationRuleSchema.getRdfruleStageAfterQueryCreation();
    }
    
    @Override
    public URI getRdfruleStageAfterQueryParsingURI()
    {
        return NormalisationRuleSchema.getRdfruleStageAfterQueryParsing();
    }
    
    @Override
    public URI getRdfruleStageAfterResultsImportURI()
    {
        return NormalisationRuleSchema.getRdfruleStageAfterResultsImport();
    }
    
    @Override
    public URI getRdfruleStageAfterResultsToDocumentURI()
    {
        return NormalisationRuleSchema.getRdfruleStageAfterResultsToDocument();
    }
    
    @Override
    public URI getRdfruleStageAfterResultsToPoolURI()
    {
        return NormalisationRuleSchema.getRdfruleStageAfterResultsToPool();
    }
    
    @Override
    public URI getRdfruleStageBeforeResultsImportURI()
    {
        return NormalisationRuleSchema.getRdfruleStageBeforeResultsImport();
    }
    
    @Override
    public URI getRdfruleStageQueryVariablesURI()
    {
        return NormalisationRuleSchema.getRdfruleStageQueryVariables();
    }
    
    @Override
    public URI getSparqlRuleModeAddAllMatchingTriplesURI()
    {
        return SparqlNormalisationRuleSchema.getSparqlRuleModeAddAllMatchingTriples();
    }
    
    @Override
    public URI getSparqlRuleModeOnlyDeleteMatchesURI()
    {
        return SparqlNormalisationRuleSchema.getSparqlRuleModeOnlyDeleteMatches();
    }
    
    @Override
    public URI getSparqlRuleModeOnlyIncludeMatchesURI()
    {
        return SparqlNormalisationRuleSchema.getSparqlRuleModeOnlyIncludeMatches();
    }
    
}
