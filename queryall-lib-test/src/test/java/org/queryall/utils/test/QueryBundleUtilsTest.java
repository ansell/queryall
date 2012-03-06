/**
 * 
 */
package org.queryall.utils.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.queryall.api.base.QueryAllConfiguration;
import org.queryall.api.namespace.NamespaceEntry;
import org.queryall.api.profile.Profile;
import org.queryall.api.provider.Provider;
import org.queryall.api.querytype.InputQueryType;
import org.queryall.api.test.DummyProfile;
import org.queryall.api.test.DummyProvider;
import org.queryall.api.test.DummyQueryType;
import org.queryall.api.utils.ProfileIncludeExclude;
import org.queryall.blacklist.BlacklistController;
import org.queryall.exception.QueryAllException;
import org.queryall.query.QueryBundle;
import org.queryall.utils.QueryBundleUtils;
import org.queryall.utils.Settings;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class QueryBundleUtilsTest
{
    private QueryAllConfiguration testSettingsEmpty;
    private BlacklistController testBlacklistControllerEmpty;
    
    private InputQueryType testQueryTypeEmpty;
    private Collection<Provider> testChosenProvidersEmpty;
    private Collection<Provider> testChosenProvidersSingleTrivial;
    private Provider testProviderTrivial1;
    
    private List<Profile> testSortedIncludedProfilesEmpty;
    private List<Profile> testSortedIncludedProfilesSingleAllInclude;
    private Profile testProfileSingleAllInclude;
    
    private Map<String, String> testQueryParametersEmpty;
    private Map<String, Collection<NamespaceEntry>> testNamespaceInputVariablesEmpty;
    private String testHostName;
    private boolean testUseAllEndpointsTrue;
    private int testPageOffset1;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        this.testSettingsEmpty = new Settings();
        this.testBlacklistControllerEmpty = new BlacklistController(this.testSettingsEmpty);
        
        this.testQueryTypeEmpty = new DummyQueryType();
        this.testQueryTypeEmpty.setKey("http://test.example.org/querybundleutilstest/querytype/empty");
        this.testChosenProvidersEmpty = new ArrayList<Provider>(0);
        this.testChosenProvidersSingleTrivial = new ArrayList<Provider>(1);
        // add an instance of DummyProvider which only implements the Provider interface, so should
        // not be recognised as HttpProvider or NoCommunicationProvider, among any others
        this.testProviderTrivial1 = new DummyProvider();
        this.testProviderTrivial1.setKey("http://test.example.org/querybundleutilstest/provider/trivial/1");
        this.testChosenProvidersSingleTrivial.add(this.testProviderTrivial1);
        
        this.testSortedIncludedProfilesEmpty = new ArrayList<Profile>(0);
        this.testSortedIncludedProfilesSingleAllInclude = new ArrayList<Profile>(1);
        this.testProfileSingleAllInclude = new DummyProfile();
        this.testProfileSingleAllInclude.setKey("http://other.example.org/test/profile/singleallinclude");
        // By default this property is undefined to make it easier to layer profiles, but we want it
        // defined here to make sure that all settings are set to include
        this.testProfileSingleAllInclude
                .setDefaultProfileIncludeExcludeOrder(ProfileIncludeExclude.EXCLUDE_THEN_INCLUDE);
        this.testSortedIncludedProfilesSingleAllInclude.add(this.testProfileSingleAllInclude);
        
        this.testQueryParametersEmpty = new HashMap<String, String>();
        this.testNamespaceInputVariablesEmpty = new HashMap<String, Collection<NamespaceEntry>>();
        this.testHostName = "http://test.example.org/";
        this.testUseAllEndpointsTrue = true;
        this.testPageOffset1 = 1;
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        this.testSettingsEmpty = null;
        this.testBlacklistControllerEmpty = null;
        
        this.testQueryTypeEmpty = null;
        this.testChosenProvidersEmpty = null;
        this.testChosenProvidersSingleTrivial = null;
        this.testProviderTrivial1 = null;
        
        this.testSortedIncludedProfilesEmpty = null;
        this.testSortedIncludedProfilesSingleAllInclude = null;
        this.testProfileSingleAllInclude = null;
        
        this.testQueryParametersEmpty = null;
        this.testNamespaceInputVariablesEmpty = null;
        this.testHostName = null;
        this.testUseAllEndpointsTrue = false;
        this.testPageOffset1 = -1;
    }
    
    /**
     * Tests the generateQueryBundlesForQueryTypeAndProviders method with all items in their empty
     * or freshly initialised state
     * 
     * @throws QueryAllException
     * 
     */
    @Test
    public final void testGenerateQueryBundlesAllEmpty() throws QueryAllException
    {
        final Collection<QueryBundle> results =
                QueryBundleUtils.generateQueryBundlesForQueryTypeAndProviders(this.testQueryTypeEmpty,
                        this.testChosenProvidersEmpty, this.testQueryParametersEmpty,
                        this.testNamespaceInputVariablesEmpty, this.testSortedIncludedProfilesEmpty,
                        this.testSettingsEmpty, this.testBlacklistControllerEmpty, this.testHostName,
                        this.testUseAllEndpointsTrue, this.testPageOffset1);
        
        Assert.assertNotNull(results);
        Assert.assertEquals(0, results.size());
    }
    
    /**
     * Tests the generateQueryBundlesForQueryTypeAndProviders method with a single dummy provider
     * and a single query type, with all other items in their empty state
     * 
     * The dummy provider and dummy query type are setup to return true to challenges related to
     * their applicability to any query, so they will generate a single query bundle.
     * 
     * @throws QueryAllException
     * 
     */
    @Test
    public final void testGenerateQueryBundlesAllEmptyExceptProviders() throws QueryAllException
    {
        final Collection<QueryBundle> results =
                QueryBundleUtils.generateQueryBundlesForQueryTypeAndProviders(this.testQueryTypeEmpty,
                        this.testChosenProvidersSingleTrivial, this.testQueryParametersEmpty,
                        this.testNamespaceInputVariablesEmpty, this.testSortedIncludedProfilesEmpty,
                        this.testSettingsEmpty, this.testBlacklistControllerEmpty, this.testHostName,
                        this.testUseAllEndpointsTrue, this.testPageOffset1);
        
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        
        final QueryBundle result = results.iterator().next();
        
        Assert.assertNotNull(result);
        
        Assert.assertEquals(this.testSettingsEmpty, result.getQueryallSettings());
        
        Assert.assertEquals(this.testQueryTypeEmpty, result.getQueryType());
        Assert.assertEquals(this.testProviderTrivial1, result.getProvider());
        Assert.assertEquals(this.testSortedIncludedProfilesEmpty, result.getRelevantProfiles());
        
        Assert.assertFalse(result.getRedirectRequired());
        Assert.assertEquals("", result.getStaticRdfXmlString());
        
        Assert.assertNotNull(result.getAlternativeEndpointsAndQueries());
        Assert.assertEquals(0, result.getAlternativeEndpointsAndQueries().size());
    }
    
    /**
     * Tests the generateQueryBundlesForQueryTypeAndProviders method with a single dummy provider
     * and a single query type, with all other items in their empty state
     * 
     * The dummy provider and dummy query type are setup to return true to challenges related to
     * their applicability to any query, so they will generate a single query bundle.
     * 
     * The single profile is set to all inclusive for every setting, including the default profile
     * include exclude order setting
     * 
     * @throws QueryAllException
     * 
     */
    @Test
    public final void testGenerateQueryBundlesSingleProviderSingleAllIncludeProfile() throws QueryAllException
    {
        final Collection<QueryBundle> results =
                QueryBundleUtils.generateQueryBundlesForQueryTypeAndProviders(this.testQueryTypeEmpty,
                        this.testChosenProvidersSingleTrivial, this.testQueryParametersEmpty,
                        this.testNamespaceInputVariablesEmpty, this.testSortedIncludedProfilesSingleAllInclude,
                        this.testSettingsEmpty, this.testBlacklistControllerEmpty, this.testHostName,
                        this.testUseAllEndpointsTrue, this.testPageOffset1);
        
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        
        final QueryBundle result = results.iterator().next();
        
        Assert.assertNotNull(result);
        
        Assert.assertEquals(this.testSettingsEmpty, result.getQueryallSettings());
        
        Assert.assertEquals(this.testQueryTypeEmpty, result.getQueryType());
        Assert.assertEquals(this.testProviderTrivial1, result.getProvider());
        Assert.assertEquals(this.testSortedIncludedProfilesSingleAllInclude, result.getRelevantProfiles());
        
        Assert.assertEquals(1, result.getRelevantProfiles().size());
        
        final Profile nextProfile = result.getRelevantProfiles().get(0);
        Assert.assertEquals(true, nextProfile.getAllowImplicitProviderInclusions());
        Assert.assertEquals(true, nextProfile.getAllowImplicitQueryTypeInclusions());
        Assert.assertEquals(true, nextProfile.getAllowImplicitRdfRuleInclusions());
        Assert.assertEquals(ProfileIncludeExclude.EXCLUDE_THEN_INCLUDE,
                nextProfile.getDefaultProfileIncludeExcludeOrder());
        Assert.assertEquals(0, nextProfile.getExcludeProviders().size());
        Assert.assertEquals(0, nextProfile.getExcludeQueryTypes().size());
        Assert.assertEquals(0, nextProfile.getExcludeRdfRules().size());
        Assert.assertEquals(0, nextProfile.getIncludeProviders().size());
        Assert.assertEquals(0, nextProfile.getIncludeQueryTypes().size());
        Assert.assertEquals(0, nextProfile.getIncludeRdfRules().size());
        Assert.assertEquals(100, nextProfile.getOrder());
        Assert.assertEquals(nextProfile, this.testProfileSingleAllInclude);
        
        Assert.assertFalse(result.getRedirectRequired());
        Assert.assertEquals("", result.getStaticRdfXmlString());
        
        Assert.assertNotNull(result.getAlternativeEndpointsAndQueries());
        Assert.assertEquals(0, result.getAlternativeEndpointsAndQueries().size());
    }
    
}
