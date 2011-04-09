package org.queryall;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.sail.memory.model.MemValueFactory;
import org.queryall.api.Profile;
import org.queryall.api.Provider;

/**
 * Abstract unit test for Provider API
 * 
 * Implementations must implement the abstract setUp method
 */

@SuppressWarnings("unused")
public abstract class AbstractProviderTest 
    extends TestCase
{
    protected URI testTrueQueryTypeUri;
    protected URI testFalseQueryTypeUri;
    protected URI testTrueRuleUri;
    protected URI testFalseRuleUri;
    protected URI testTrueNamespaceUri;
    protected URI testFalseNamespaceUri;
    protected URI testTrueProviderUri;
    protected URI testFalseProviderUri;
    
    private Provider providerNonDefault;
    private Provider providerSpecificDefault;
    private Provider providerNoNamespacesDefault;
    private Provider providerIncludeImplicitly;
    private Provider providerExcludeImplicitly;
    private Provider providerIncludeExcludeOrderUndefined;
    private Provider providerTrueUri;
    private Provider providerFalseUri;

    private Profile profileIncludeAllImplicitly;
    private Profile profileExcludeImplicitly;
    private Profile profileExcludeImplicitlyAndByDefault;
    private Profile profileIncludeAllImplicitlyExcludeByDefault;
    private Profile profileIncludeTrueOnly;
    private Profile profileExcludeFalseOnly;

    private List<Profile> profileListEmpty;
    private List<Profile> profileListSingleIncludeAllImplicitly;
    private List<Profile> profileListSingleExcludeImplicitly;
    private List<Profile> profileListSingleExcludeExplicitlyAndByDefault;
    private List<Profile> profileListSingleIncludeAllImplicitlyExcludeByDefault;
    private List<Profile> profileListSingleIncludeTrue;
    private List<Profile> profileListSingleExcludeFalse;
    private List<Profile> profileListMultipleIncludeTrueThenExcludeFalse;
    private List<Profile> profileListMultipleExcludeFalseThenIncludeTrue;
    private List<Profile> profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly;
    private List<Profile> profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly;

    /**
     * This method performs the following actions:
     * - Creates new Providers for the Provider type fields using multiple calls to getNewTestProvider
     * - Create org.openrdf.model.URI instances for the test URIs
     * - Add testTrue*'s using the relevant methods from the API
     */
    @Before
    public void setUp() throws Exception
    {
        ValueFactory f = new MemValueFactory();

        testTrueQueryTypeUri = f.createURI("http://example.org/test/includedQueryType");
        testFalseQueryTypeUri = f.createURI("http://example.org/test/excludedQueryType");
        testTrueRuleUri = f.createURI("http://example.org/test/includedRule");
        testFalseRuleUri = f.createURI("http://example.org/test/excludedRule");
        testTrueNamespaceUri = f.createURI("http://example.org/test/includedNamespace");
        testFalseNamespaceUri = f.createURI("http://example.org/test/excludedNamespace");
        testTrueProviderUri = f.createURI("http://example.org/test/includedProvider");
        testFalseProviderUri = f.createURI("http://example.org/test/excludedProvider");

        
        
        providerNonDefault = getNewTestProvider();
        providerNonDefault.setIsDefaultSource(false);
        providerNonDefault.addIncludedInQueryType(testTrueQueryTypeUri);
        providerNonDefault.addNormalisationUri(testTrueRuleUri);
        providerNonDefault.addNamespace(testTrueNamespaceUri);
        providerNonDefault.setProfileIncludeExcludeOrder(getProfileIncludeExcludeOrderUndefinedUri());
        
        providerSpecificDefault = getNewTestProvider();
        providerSpecificDefault.setIsDefaultSource(true);
        providerSpecificDefault.addIncludedInQueryType(testTrueQueryTypeUri);
        providerSpecificDefault.addNormalisationUri(testTrueRuleUri);
        providerSpecificDefault.addNamespace(testTrueNamespaceUri);
        providerSpecificDefault.setProfileIncludeExcludeOrder(getProfileIncludeExcludeOrderUndefinedUri());
        
        providerNoNamespacesDefault = getNewTestProvider();
        providerNoNamespacesDefault.setIsDefaultSource(true);
        providerNoNamespacesDefault.addIncludedInQueryType(testTrueQueryTypeUri);
        providerNoNamespacesDefault.addNormalisationUri(testTrueRuleUri);
        providerNoNamespacesDefault.setProfileIncludeExcludeOrder(getProfileIncludeExcludeOrderUndefinedUri());
        
        providerTrueUri = getNewTestProvider();
        providerTrueUri.setProfileIncludeExcludeOrder(getProfileIncludeExcludeOrderUndefinedUri());
        providerTrueUri.setKey(testTrueProviderUri);
        
        providerFalseUri = getNewTestProvider();
        providerFalseUri.setProfileIncludeExcludeOrder(getProfileIncludeExcludeOrderUndefinedUri());
        providerFalseUri.setKey(testFalseProviderUri);

        providerIncludeImplicitly = getNewTestProvider();
        providerIncludeImplicitly.setProfileIncludeExcludeOrder(getProfileExcludeThenIncludeURI());
        
        providerExcludeImplicitly = getNewTestProvider();
        providerExcludeImplicitly.setProfileIncludeExcludeOrder(getProfileIncludeThenExcludeURI());
        
        providerIncludeExcludeOrderUndefined = getNewTestProvider();
        providerIncludeExcludeOrderUndefined.setProfileIncludeExcludeOrder(getProfileIncludeExcludeOrderUndefinedUri());
        
        
        
        profileIncludeAllImplicitly = getNewTestProfile();
        profileIncludeAllImplicitly.setAllowImplicitProviderInclusions(true);
        profileIncludeAllImplicitly.setAllowImplicitQueryTypeInclusions(true);
        profileIncludeAllImplicitly.setAllowImplicitRdfRuleInclusions(true);
        profileIncludeAllImplicitly.setDefaultProfileIncludeExcludeOrder(getProfileExcludeThenIncludeURI());
        
        profileIncludeAllImplicitlyExcludeByDefault = getNewTestProfile();
        profileIncludeAllImplicitlyExcludeByDefault.setAllowImplicitProviderInclusions(true);
        profileIncludeAllImplicitlyExcludeByDefault.setAllowImplicitQueryTypeInclusions(true);
        profileIncludeAllImplicitlyExcludeByDefault.setAllowImplicitRdfRuleInclusions(true);
        profileIncludeAllImplicitlyExcludeByDefault.setDefaultProfileIncludeExcludeOrder(getProfileIncludeThenExcludeURI());
        
        profileExcludeImplicitly = getNewTestProfile();
        profileExcludeImplicitly.setAllowImplicitProviderInclusions(false);
        profileExcludeImplicitly.setAllowImplicitQueryTypeInclusions(false);
        profileExcludeImplicitly.setAllowImplicitRdfRuleInclusions(false);
        profileExcludeImplicitly.setDefaultProfileIncludeExcludeOrder(getProfileExcludeThenIncludeURI());
        
        profileExcludeImplicitlyAndByDefault = getNewTestProfile();
        profileExcludeImplicitlyAndByDefault.setAllowImplicitProviderInclusions(false);
        profileExcludeImplicitlyAndByDefault.setAllowImplicitQueryTypeInclusions(false);
        profileExcludeImplicitlyAndByDefault.setAllowImplicitRdfRuleInclusions(false);
        profileExcludeImplicitlyAndByDefault.setDefaultProfileIncludeExcludeOrder(getProfileIncludeThenExcludeURI());
        
        profileIncludeTrueOnly = getNewTestProfile();
        profileIncludeTrueOnly.addIncludeProvider(testTrueProviderUri);
        profileIncludeTrueOnly.setAllowImplicitProviderInclusions(false);
        profileIncludeTrueOnly.setAllowImplicitQueryTypeInclusions(false);
        profileIncludeTrueOnly.setAllowImplicitRdfRuleInclusions(false);
        profileIncludeTrueOnly.setDefaultProfileIncludeExcludeOrder(getProfileIncludeThenExcludeURI());
        
        profileExcludeFalseOnly = getNewTestProfile();
        profileExcludeFalseOnly.addExcludeProvider(testFalseProviderUri);
        profileExcludeFalseOnly.setAllowImplicitProviderInclusions(false);
        profileExcludeFalseOnly.setAllowImplicitQueryTypeInclusions(false);
        profileExcludeFalseOnly.setAllowImplicitRdfRuleInclusions(false);
        profileExcludeFalseOnly.setDefaultProfileIncludeExcludeOrder(getProfileIncludeThenExcludeURI());

        
        
        profileListEmpty = new LinkedList<Profile>();

        profileListSingleIncludeAllImplicitly = new LinkedList<Profile>();
        profileListSingleIncludeAllImplicitly.add(profileIncludeAllImplicitly);
        
        profileListSingleIncludeAllImplicitlyExcludeByDefault = new LinkedList<Profile>();
        profileListSingleIncludeAllImplicitlyExcludeByDefault.add(profileIncludeAllImplicitlyExcludeByDefault);
        
        profileListSingleExcludeImplicitly = new LinkedList<Profile>();
        profileListSingleExcludeImplicitly.add(profileExcludeImplicitly);
        
        profileListSingleExcludeExplicitlyAndByDefault = new LinkedList<Profile>();
        profileListSingleExcludeExplicitlyAndByDefault.add(profileExcludeImplicitlyAndByDefault);
        
        profileListSingleIncludeTrue = new LinkedList<Profile>();
        profileListSingleIncludeTrue.add(profileIncludeTrueOnly);
        
        profileListSingleExcludeFalse = new LinkedList<Profile>();
        profileListSingleExcludeFalse.add(profileExcludeFalseOnly);

        profileListMultipleIncludeTrueThenExcludeFalse = new LinkedList<Profile>();
        profileListMultipleIncludeTrueThenExcludeFalse.add(profileIncludeTrueOnly);
        profileListMultipleIncludeTrueThenExcludeFalse.add(profileExcludeFalseOnly);
        
        profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly = new LinkedList<Profile>();
        profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly.add(profileIncludeTrueOnly);
        profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly.add(profileExcludeFalseOnly);
        profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly.add(profileIncludeAllImplicitly);
        
        profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly = new LinkedList<Profile>();
        profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly.add(profileIncludeTrueOnly);
        profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly.add(profileExcludeFalseOnly);
        profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly.add(profileExcludeImplicitlyAndByDefault);
        
        profileListMultipleExcludeFalseThenIncludeTrue = new LinkedList<Profile>();
        profileListMultipleExcludeFalseThenIncludeTrue.add(profileExcludeFalseOnly);
        profileListMultipleExcludeFalseThenIncludeTrue.add(profileIncludeTrueOnly);
    }
    
    /**
     * This method must be overridden to return a new instance of 
     * the implemented Provider class for each successive invocation
     */
    public abstract Provider getNewTestProvider();
    
    /**
     * This method must be overridden to return a new instance of 
     * the implemented Profile class for each successive invocation
     */
    public abstract Profile getNewTestProfile();
    

    public abstract URI getProfileIncludeExcludeOrderUndefinedUri();

    public abstract URI getProfileExcludeThenIncludeURI();
    
    public abstract URI getProfileIncludeThenExcludeURI();

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        testTrueQueryTypeUri = null;
        testFalseQueryTypeUri = null;
        testTrueRuleUri = null;
        testFalseRuleUri = null;
        testTrueNamespaceUri = null;
        testFalseNamespaceUri = null;
        testTrueProviderUri = null;
        testFalseProviderUri = null;
        
        providerNonDefault = null;
        providerSpecificDefault = null;
        providerNoNamespacesDefault = null;
        providerIncludeImplicitly = null;
        providerExcludeImplicitly = null;
        providerIncludeExcludeOrderUndefined = null;
        providerTrueUri = null;
        providerFalseUri = null;

        profileIncludeAllImplicitly = null;
        profileExcludeImplicitly = null;
        profileExcludeImplicitlyAndByDefault = null;
        profileIncludeAllImplicitlyExcludeByDefault = null;
        profileIncludeTrueOnly = null;
        profileExcludeFalseOnly = null;

        profileListEmpty = null;
        profileListSingleIncludeAllImplicitly = null;
        profileListSingleExcludeImplicitly = null;
        profileListSingleExcludeExplicitlyAndByDefault = null;
        profileListSingleIncludeAllImplicitlyExcludeByDefault = null;
        profileListSingleIncludeTrue = null;
        profileListSingleExcludeFalse = null;
        profileListMultipleIncludeTrueThenExcludeFalse = null;
        profileListMultipleExcludeFalseThenIncludeTrue = null;
        profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly = null;
        profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly = null;

    }

    /**
     * Test method for {@link org.queryall.api.Provider#containsQueryTypeUri(org.openrdf.model.URI)}.
     */
    @Test
    public void testHandlesQueryTypes()
    {
        assertTrue(providerSpecificDefault.containsQueryTypeUri(testTrueQueryTypeUri));
        assertFalse(providerSpecificDefault.containsQueryTypeUri(testFalseQueryTypeUri));
        assertTrue(providerNonDefault.containsQueryTypeUri(testTrueQueryTypeUri));
        assertFalse(providerNonDefault.containsQueryTypeUri(testFalseQueryTypeUri));
        assertTrue(providerNoNamespacesDefault.containsQueryTypeUri(testTrueQueryTypeUri));
        assertFalse(providerNoNamespacesDefault.containsQueryTypeUri(testFalseQueryTypeUri));
    }

    /**
     * Test method for {@link org.queryall.api.Provider#containsNamespaceUri(org.openrdf.model.URI)}.
     */
    @Test
    public void testContainsNamespaceUri()
    {
        assertTrue(providerSpecificDefault.containsNamespaceUri(testTrueNamespaceUri));
        assertFalse(providerSpecificDefault.containsNamespaceUri(testFalseNamespaceUri));
        assertTrue(providerNonDefault.containsNamespaceUri(testTrueNamespaceUri));
        assertFalse(providerNonDefault.containsNamespaceUri(testFalseNamespaceUri));
        assertFalse(providerNoNamespacesDefault.containsNamespaceUri(testTrueNamespaceUri));
        assertFalse(providerNoNamespacesDefault.containsNamespaceUri(testFalseNamespaceUri));
    }
    
    /**
     * Test method for {@link org.queryall.api.Provider#containsNamespaceOrDefault(org.openrdf.model.URI)}.
     */
    @Test
    public void testContainsNamespaceOrDefault()
    {
        assertTrue(providerSpecificDefault.containsNamespaceOrDefault(testTrueNamespaceUri));
        assertTrue(providerSpecificDefault.containsNamespaceOrDefault(testFalseNamespaceUri));
        assertTrue(providerNonDefault.containsNamespaceOrDefault(testTrueNamespaceUri));
        assertFalse(providerNonDefault.containsNamespaceOrDefault(testFalseNamespaceUri));
        assertTrue(providerNoNamespacesDefault.containsNamespaceOrDefault(testTrueNamespaceUri));
        assertTrue(providerNoNamespacesDefault.containsNamespaceOrDefault(testFalseNamespaceUri));

    }    

    /**
     * Test method for {@link org.queryall.api.Provider#isUsedWithProfileList(org.openrdf.model.URI, boolean, boolean)}.
     * 
     * This method contains a matrix of single and multiple profile configurations that are designed to test the isUsedWithProfileList method for differing provider configurations
     * 
     * In cases where the providers do not match explicitly, the final two parameters of the isUsedWithProfileList method are used to determine whether the provider is useful
     * 
     * The second parameter determines whether implicit inclusions are recognised. 
     * These are relevant if the providers include exclude order is computed to be excludeThenInclude. 
     * This may occur in cases where either
     * the providers default include exclude order was undefined and
     * any profile had a default include exclude order of excludeThenInclude; 
     * or when the providers default include exclude order was excludeThenInclude.
     */
    @Test
    public void testIsUsedWithProfileList()
    {
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeImplicitly, profileListEmpty, 
                true, true, false, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerExcludeImplicitly, profileListEmpty, 
                false, false, false, false);

        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeExcludeOrderUndefined, profileListEmpty, 
                true, true, false, false);


        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeImplicitly, profileListSingleIncludeAllImplicitly, 
                true, true, true, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerExcludeImplicitly, profileListSingleIncludeAllImplicitly, 
                false, false, false, false);

        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeExcludeOrderUndefined, profileListSingleIncludeAllImplicitly, 
                true, true, true, false);
        
        
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeImplicitly, profileListSingleIncludeAllImplicitlyExcludeByDefault, 
                true, true, true, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerExcludeImplicitly, profileListSingleIncludeAllImplicitlyExcludeByDefault, 
                false, false, false, false);

        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeExcludeOrderUndefined, profileListSingleIncludeAllImplicitlyExcludeByDefault, 
                true, true, false, false);
        

        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeImplicitly, profileListSingleExcludeImplicitly, 
                true, true, false, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerExcludeImplicitly, profileListSingleExcludeImplicitly, 
                false, false, false, false);

        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeExcludeOrderUndefined, profileListSingleExcludeImplicitly, 
                true, true, false, false);
        

        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeImplicitly, profileListSingleExcludeExplicitlyAndByDefault, 
                true, true, false, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerExcludeImplicitly, profileListSingleExcludeExplicitlyAndByDefault, 
                false, false, false, false);

        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeExcludeOrderUndefined, profileListSingleExcludeExplicitlyAndByDefault, 
                true, true, false, false);

        

        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeImplicitly, profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, 
                true, true, true, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerExcludeImplicitly, profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, 
                false, false, false, false);

        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeExcludeOrderUndefined, profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, 
                true, true, true, false);

        

        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeImplicitly, profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, 
                true, true, false, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerExcludeImplicitly, profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, 
                false, false, false, false);

        ProfilableTestUtil.testIsUsedWithProfileList(
                providerIncludeExcludeOrderUndefined, profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, 
                true, true, false, false);
        
        
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerTrueUri, profileListSingleIncludeTrue, 
                true, true, true, true);
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerFalseUri, profileListSingleIncludeTrue, 
                true, true, false, false);
        

        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerTrueUri, profileListSingleExcludeFalse, 
                true, true, false, false);
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerFalseUri, profileListSingleExcludeFalse, 
                false, false, false, false);
        
        

        ProfilableTestUtil.testIsUsedWithProfileList(
                providerTrueUri, profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, 
                true, true, true, true);
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerFalseUri, profileListMultipleIncludeTrueThenExcludeFalseThenIncludeAllImplicitly, 
                false, false, false, false);
        
        
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerTrueUri, profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, 
                true, true, true, true);
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerFalseUri, profileListMultipleIncludeTrueThenExcludeFalseThenExcludeAllImplicitly, 
                false, false, false, false);
        
        
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerTrueUri, profileListMultipleIncludeTrueThenExcludeFalse, 
                true, true, true, true);
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerFalseUri, profileListMultipleIncludeTrueThenExcludeFalse, 
                false, false, false, false);

        
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerTrueUri, profileListMultipleExcludeFalseThenIncludeTrue, 
                true, true, true, true);
        
        ProfilableTestUtil.testIsUsedWithProfileList(
                providerFalseUri, profileListMultipleExcludeFalseThenIncludeTrue, 
                false, false, false, false);
    }
}
