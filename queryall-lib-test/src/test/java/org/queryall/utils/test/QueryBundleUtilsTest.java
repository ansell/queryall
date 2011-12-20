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
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        testQueryTypeEmpty = new DummyQueryType();
        testChosenProvidersEmpty = new ArrayList<Provider>();
        testQueryParametersEmpty = new HashMap<String, String>();
        testNamespaceInputVariablesEmpty = new HashMap<String, Collection<NamespaceEntry>>();
        testSortedIncludedProfilesEmpty = new ArrayList<Profile>();
        testSettingsEmpty = new Settings();
        testBlacklistControllerEmpty = new BlacklistController(testSettingsEmpty);
        testHostName = "http://test.example.org/";
        testUseAllEndpointsTrue = true;
        testPageOffset1 = 1;
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        testQueryTypeEmpty = null;
        testChosenProvidersEmpty = null;
        testQueryParametersEmpty = null;
        testNamespaceInputVariablesEmpty = null;
        testSortedIncludedProfilesEmpty = null;
        testSettingsEmpty = null;
        testBlacklistControllerEmpty = null;
        testHostName = null;
        testUseAllEndpointsTrue = false;
        testPageOffset1 = -1;
    }
    
    /**
     * @throws QueryAllException 
     *
     */
    @Test
    public final void testGenerateQueryBundlesForQueryTypeAndProviders() throws QueryAllException
    {
        Collection<QueryBundle> results = QueryBundleUtils.generateQueryBundlesForQueryTypeAndProviders(this.testQueryTypeEmpty, this.testChosenProvidersEmpty,
                this.testQueryParametersEmpty, this.testNamespaceInputVariablesEmpty, testSortedIncludedProfilesEmpty, testSettingsEmpty,
                testBlacklistControllerEmpty, testHostName, testUseAllEndpointsTrue, testPageOffset1);
        
        Assert.assertNotNull(results);
        Assert.assertEquals(0, results.size());
    }
    
}
