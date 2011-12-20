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
import org.queryall.api.test.DummyProvider;
import org.queryall.api.test.DummyQueryType;
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
    
    private InputQueryType testQueryTypeEmpty;
    private Collection<Provider> testChosenProvidersEmpty;
    private Map<String, String> testQueryParametersEmpty;
    private Map<String, Collection<NamespaceEntry>> testNamespaceInputVariablesEmpty;
    private List<Profile> testSortedIncludedProfilesEmpty;
    private QueryAllConfiguration testSettingsEmpty;
    private BlacklistController testBlacklistControllerEmpty;
    private String testHostName;
    private boolean testUseAllEndpointsTrue;
    private int testPageOffset1;
    private Collection<Provider> testChosenProvidersSingleTrivial;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        this.testQueryTypeEmpty = new DummyQueryType();
        
        this.testChosenProvidersEmpty = new ArrayList<Provider>(0);
        this.testChosenProvidersSingleTrivial = new ArrayList<Provider>(1);
        // add an instance of DummyProvider which only implements the Provider interface, so should
        // not be recognised as HttpProvider or NoCommunicationProvider, among any others
        this.testChosenProvidersSingleTrivial.add(new DummyProvider());
        
        this.testQueryParametersEmpty = new HashMap<String, String>();
        
        this.testNamespaceInputVariablesEmpty = new HashMap<String, Collection<NamespaceEntry>>();
        
        this.testSortedIncludedProfilesEmpty = new ArrayList<Profile>(0);
        
        this.testSettingsEmpty = new Settings();
        
        this.testBlacklistControllerEmpty = new BlacklistController(this.testSettingsEmpty);
        
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
        this.testQueryTypeEmpty = null;
        this.testChosenProvidersEmpty = null;
        this.testQueryParametersEmpty = null;
        this.testNamespaceInputVariablesEmpty = null;
        this.testSortedIncludedProfilesEmpty = null;
        this.testSettingsEmpty = null;
        this.testBlacklistControllerEmpty = null;
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
    }
}
